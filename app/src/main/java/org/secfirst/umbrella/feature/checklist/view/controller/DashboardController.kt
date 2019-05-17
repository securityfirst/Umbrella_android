package org.secfirst.umbrella.feature.checklist.view.controller

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.alert_control.view.*
import kotlinx.android.synthetic.main.checklist_custom_dialog.view.*
import kotlinx.android.synthetic.main.checklist_dashboard.*
import kotlinx.android.synthetic.main.checklist_dashboard.view.*
import kotlinx.android.synthetic.main.empty_view.*
import kotlinx.android.synthetic.main.main_view.*
import kotlinx.android.synthetic.main.share_dialog.view.*
import org.apache.commons.io.FilenameUtils
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.secfirst.umbrella.BuildConfig
import org.secfirst.umbrella.R
import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.component.DialogManager
import org.secfirst.umbrella.component.SwipeToDeleteCallback
import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.checklist.Dashboard
import org.secfirst.umbrella.data.database.checklist.covertToHTML
import org.secfirst.umbrella.feature.base.view.BaseController
import org.secfirst.umbrella.feature.checklist.DaggerChecklistComponent
import org.secfirst.umbrella.feature.checklist.interactor.ChecklistBaseInteractor
import org.secfirst.umbrella.feature.checklist.presenter.ChecklistBasePresenter
import org.secfirst.umbrella.feature.checklist.view.ChecklistView
import org.secfirst.umbrella.feature.checklist.view.adapter.DashboardAdapter
import org.secfirst.umbrella.feature.segment.view.controller.HostSegmentController
import org.secfirst.umbrella.misc.createDocument
import org.secfirst.umbrella.misc.initRecyclerView
import java.io.File
import javax.inject.Inject

class DashboardController(bundle: Bundle) : BaseController(bundle), ChecklistView {


    @Inject
    internal lateinit var presenter: ChecklistBasePresenter<ChecklistView, ChecklistBaseInteractor>
    private val dashboardItemClick: (Checklist) -> Unit = this::onDashboardItemClicked
    private val shareChecklistClick: (Checklist) -> Unit = this::onChecklistShareClick
    private val isCustomBoard by lazy { args.getBoolean(EXTRA_IS_CUSTOM_BOARD) }
    private lateinit var adapter: DashboardAdapter
    private lateinit var customChecklistDialog: AlertDialog
    private lateinit var customChecklistView: View
    private lateinit var shareDialog: AlertDialog
    private lateinit var shareView: View

    constructor(isCustomBoard: Boolean) : this(Bundle().apply {
        putBoolean(EXTRA_IS_CUSTOM_BOARD, isCustomBoard)
    })

    override fun onInject() {
        DaggerChecklistComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.checklist_dashboard, container, false)
        customChecklistView = inflater.inflate(R.layout.checklist_custom_dialog, container, false)
        shareView = inflater.inflate(R.layout.share_dialog, container, false)
        customChecklistDialog = android.app.AlertDialog
                .Builder(activity)
                .setView(customChecklistView)
                .create()


        shareDialog = AlertDialog
                .Builder(activity)
                .setView(shareView)
                .create()

        customChecklistView.alertControlOk.onClick { startCustomChecklist() }
        customChecklistView.alertControlCancel.onClick { customChecklistDialog.dismiss() }
        view.addNewChecklistBtn.setOnClickListener { showCustomChecklistDialog() }
        presenter.onAttach(this)
        return view
    }

    override fun onAttach(view: View) {
        checkWorkflow()
    }

    private fun onDeleteChecklist(checklist: Checklist) {
        presenter.submitDeleteChecklist(checklist)
    }

    private fun showCustomChecklistDialog() {
        val dialogManager = DialogManager(this)
        dialogManager.showDialog(object : DialogManager.DialogFactory {
            override fun createDialog(context: Context?): Dialog {
                return customChecklistDialog
            }
        })
        customChecklistDialog.show()
    }

    private fun initOnDeleteChecklist() {
        val swipeHandler = object : SwipeToDeleteCallback(context, true) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val checklist = adapter.getChecklist(position)
                resetChecklist(checklist!!)
                adapter.removeAt(position)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(checklistDashboardRecyclerView)
    }

    private fun initOnDeleteCustomChecklist() {
        val swipeHandler = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val checklist = adapter.getChecklist(position)
                onDeleteChecklist(checklist!!)
                adapter.removeAt(position)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(checklistDashboardRecyclerView)
    }

    private fun checkWorkflow() {
        if (isCustomBoard) {
            addNewChecklistBtn?.show()
            presenter.submitLoadCustomDashboard()
            initOnDeleteCustomChecklist()
        } else {
            presenter.submitLoadDashboard()
            initOnDeleteChecklist()
        }
    }

    private fun onDashboardItemClicked(checklist: Checklist) {
        if (checklist.custom)
            parentController?.router?.pushController(RouterTransaction.with(ChecklistController(checklist.id)))
        else {
            parentController?.router?.pushController(RouterTransaction.with(HostSegmentController(arrayListOf(checklist.difficulty!!.id), true, isFromDashboard = true)))
            mainActivity.navigation.menu.getItem(3).isChecked = true
        }
    }

    private fun resetChecklist(checklist: Checklist) {
        checklist.content.forEach {
            it.value = false
        }
        checklist.progress = 0
        checklist.favorite = false
        presenter.submitUpdateChecklist(checklist)
    }

    private fun startCustomChecklist() {
        val customName = customChecklistView.editChecklistItem.text.toString()
        if (customName.isNotBlank())
            parentController?.router?.pushController(RouterTransaction
                    .with(ChecklistCustomController(System.currentTimeMillis().toString(), customName)))
        else
            customChecklistView.editChecklistItem.error = context.getString(R.string.invalid_name_custom_checklist_messenge)
    }

    override fun showDashboard(dashboards: MutableList<Dashboard.Item>) {
        if (dashboards.isEmpty() && isCustomBoard) {
            emptyTitleView?.text = context.getText(R.string.empty_custom_checklist_message)
            addNewChecklistBtn?.show()
        } else if (dashboards.isEmpty() && !isCustomBoard) {
            emptyTitleView?.text = context.getText(R.string.empty_checklist_message)
        } else {
            customChecklistContainer?.visibility = View.VISIBLE
            emptyDashboardView?.visibility = View.GONE
            adapter = DashboardAdapter(dashboards, dashboardItemClick, shareChecklistClick)
            checklistDashboardRecyclerView?.initRecyclerView(adapter)
        }
    }

    private fun onChecklistShareClick(checklist: Checklist) {
        val checklistHtml = checklist.covertToHTML()
        val doc = Jsoup.parse(checklistHtml)
        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml)
        showShareDialog(doc, context.getString(R.string.checklistDetail_title))
    }

    private fun shareDocument(fileToShare: File) {
        val pm = context.packageManager
        val uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, fileToShare)
        val shareIntent = ShareCompat.IntentBuilder.from(activity)
                .setType(context.contentResolver.getType(uri))
                .setStream(uri)
                .intent

        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, FilenameUtils.removeExtension(fileToShare.name))
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        if (shareIntent.resolveActivity(pm) != null)
            startActivity(Intent.createChooser(shareIntent, context.getString(R.string.export_lesson)))

    }

    private fun showShareDialog(doc: Document, title: String) {
        var type = context.getString(R.string.pdf_name)
        shareView.pdfRadio.text = context.getString(R.string.pdf_name)
        shareView.htmlRadio.text = context.getString(R.string.html_name)

        shareView.shareDocumentButton.setOnClickListener {
            shareDocument(createDocument(doc, title, type, context))
            shareDialog.dismiss()
        }
        shareView.cancelShareButton.setOnClickListener { shareDialog.dismiss() }
        shareView.shareGroup.setOnCheckedChangeListener { _, checkedId ->
            type = if (shareView.pdfRadio.id == checkedId)
                context.getString(R.string.pdf_name)
            else
                context.getString(R.string.html_name)
        }
        shareDialog.show()
    }


    companion object {
        const val EXTRA_IS_CUSTOM_BOARD = "custom_board"
    }
}