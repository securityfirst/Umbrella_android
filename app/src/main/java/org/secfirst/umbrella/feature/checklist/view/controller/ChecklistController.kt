package org.secfirst.umbrella.feature.checklist.view.controller

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.alert_control.view.*
import kotlinx.android.synthetic.main.checklist_add_item_dialog.view.*
import kotlinx.android.synthetic.main.checklist_view.*
import kotlinx.android.synthetic.main.checklist_view.view.*
import kotlinx.android.synthetic.main.form_progress.*
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.secfirst.umbrella.R
import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.component.SwipeToDeleteCallback
import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.checklist.Content
import org.secfirst.umbrella.feature.base.view.BaseController
import org.secfirst.umbrella.feature.checklist.DaggerChecklistComponent
import org.secfirst.umbrella.feature.checklist.interactor.ChecklistBaseInteractor
import org.secfirst.umbrella.feature.checklist.presenter.ChecklistBasePresenter
import org.secfirst.umbrella.feature.checklist.view.ChecklistView
import org.secfirst.umbrella.feature.checklist.view.adapter.ChecklistAdapter
import org.secfirst.umbrella.misc.initRecyclerView
import javax.inject.Inject

@SuppressLint("SetTextI18n")
class ChecklistController(bundle: Bundle) : BaseController(bundle), ChecklistView {

    @Inject
    internal lateinit var presenter: ChecklistBasePresenter<ChecklistView, ChecklistBaseInteractor>
    private lateinit var checklistView: View
    private lateinit var checklistViewDialog: View
    private lateinit var checklistDialog: Dialog
    private lateinit var adapter: ChecklistAdapter
    private val checklistItemClick: (Content) -> Unit = this::onChecklistItemClicked
    private val checklistProgress: (Int) -> Unit = this::onUpdateChecklistProgress
    private val checklistItemLongClick: (Int, String) -> Unit = this::onLongClick
    private lateinit var checklist: Checklist
    private val checklistId by lazy { args.getString(EXTRA_ID_CHECKLIST) }
    private val enableNavigation by lazy { args.getBoolean(ENABLE_NAVIGATION) }

    constructor(checklistId: String, enableNavigation: Boolean = false) : this(Bundle().apply {
        putString(EXTRA_ID_CHECKLIST, checklistId)
        putBoolean(ENABLE_NAVIGATION, enableNavigation)
    })

    override fun onInject() {
        DaggerChecklistComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        checklistView = inflater.inflate(R.layout.checklist_view, container, false)
        checklistViewDialog = inflater.inflate(R.layout.checklist_add_item_dialog, container, false)
        checklistDialog = AlertDialog.Builder(context)
                .setView(checklistViewDialog)
                .create()
        presenter.onAttach(this)
        checklistId?.let { presenter.submitChecklist(it) }
        checklistView.addNewItemChecklist.setOnClickListener { onAddItemClicked() }
        swipeToDeleteCallback()
        checkIsNavigation()
        return checklistView
    }

    private fun swipeToDeleteCallback() {
        val swipeHandler = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                onDeleteChecklist(adapter.getChecklistItem(position))
                adapter.removeAt(position)
                onUpdateChecklistProgress(Math.ceil(checklist.content.filter { it.value }.size * 100.0 / checklist.content.size).toInt())
                currentProgress()
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(checklistView.checklistRecyclerView)
    }

    private fun onDeleteChecklist(checklistItem: Content) = presenter.submitDeleteChecklistContent(checklistItem)

    private fun onChecklistItemClicked(checklistItem: Content) = presenter.submitInsertChecklistContent(checklistItem)

    private fun currentProgress() {
        progressAnswer.progress = checklist.progress
        titleProgressAnswer.text = "${checklist.progress}%"
    }

    private fun onUpdateChecklistProgress(percentage: Int) {
        if (percentage <= 0) {
            titleProgressAnswer.text = "0%"
            progressAnswer.progress = 0
        } else {
            titleProgressAnswer.text = "$percentage%"
            progressAnswer.progress = percentage
        }
        checklist.progress = progressAnswer.progress
        presenter.submitUpdateChecklist(checklist)
    }

    private fun onLongClick(position: Int, oldChecklistItem: String) {
        checklistViewDialog.editChecklistItem.setText(oldChecklistItem)
        checklistViewDialog.checklistDialogTitle.text = context.getText(R.string.checklist_edit_item_title)
        checklistViewDialog.alertControlOk.setOnClickListener { editChecklistItem(position) }
        checklistViewDialog.alertControlCancel.setOnClickListener { checklistDialog.dismiss() }
        checklistDialog.show()
    }

    private fun editChecklistItem(position: Int) {
        val newChecklistItem = checklistViewDialog.editChecklistItem.text.toString()
        adapter.updateItem(newChecklistItem, position)
        checklistDialog.dismiss()
    }

    private fun onAddItemClicked() {
        checklistViewDialog.checklistDialogTitle.text = context.getString(R.string.add_custom_checklist_item_title)
        checklistViewDialog.editChecklistItem.text?.clear()
        checklistViewDialog.alertControlOk.onClick {
            checklist.content.add(Content(check = checklistViewDialog.editChecklistItem.text.toString(), checklist = checklist))
            onChecklistItemAdded(checklist.content.last())
            onUpdateChecklistProgress(Math.ceil(checklist.content.filter { it.value }.size * 100.0 / checklist.content.size).toInt())
            currentProgress()
            checklistDialog.dismiss()
        }
        checklistViewDialog.alertControlCancel.onClick { checklistDialog.dismiss() }
        checklistDialog.show()
    }

    private fun onChecklistItemAdded(checklistItem: Content) = presenter.submitInsertChecklistContent(checklistItem)

    override fun getChecklist(checklist: Checklist) {
        this.checklist = checklist
        adapter = ChecklistAdapter(checklist.content, checklistItemClick,
                checklistProgress, checklistItemLongClick)
        checklistRecyclerView?.initRecyclerView(adapter)
        if (this.checklist.custom) {
            checklistView.addNewItemChecklist.show()
        }
        currentProgress()
    }

    fun getTitle() = "Checklist"

    private fun checkIsNavigation() {
        enableNavigation(enableNavigation)
        if (!enableNavigation) {
            checklistView.checklistBarLayout.visibility = View.VISIBLE
            setUpToolbar()
        }
    }

    override fun handleBack(): Boolean {
        router.popCurrentController()
        return true
    }

    override fun onDestroyView(view: View) {
        enableNavigation(true)
    }

    companion object {
        private const val EXTRA_ID_CHECKLIST = "id_custom_check_list"
        private const val ENABLE_NAVIGATION = "enable_navigation"
    }

    private fun setUpToolbar() {
        checklistView.checklistToolbar.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            mainActivity.supportActionBar?.title = getTitle()
        }
    }
}
