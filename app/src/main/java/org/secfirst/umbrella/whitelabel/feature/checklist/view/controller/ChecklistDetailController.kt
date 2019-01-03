package org.secfirst.umbrella.whitelabel.feature.checklist.view.controller

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import kotlinx.android.synthetic.main.checklist_detail_view.*
import kotlinx.android.synthetic.main.checklist_item.view.*
import kotlinx.android.synthetic.main.form_progress.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.component.SwipeToDeleteCallback
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.Content
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.checklist.DaggerChecklistComponent
import org.secfirst.umbrella.whitelabel.feature.checklist.interactor.ChecklistBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.checklist.presenter.ChecklistBasePresenter
import org.secfirst.umbrella.whitelabel.feature.checklist.view.ChecklistView
import org.secfirst.umbrella.whitelabel.feature.checklist.view.adapter.ChecklistAdapter
import org.secfirst.umbrella.whitelabel.misc.initRecyclerView
import javax.inject.Inject


@SuppressLint("SetTextI18n")
class ChecklistDetailController(bundle: Bundle) : BaseController(bundle), ChecklistView {

    @Inject
    internal lateinit var presenter: ChecklistBasePresenter<ChecklistView, ChecklistBaseInteractor>
    private lateinit var checklistView: View
    private val checklistItemClick: (Content) -> Unit = this::onChecklistItemClicked
    private val checklistProgress: (Int) -> Unit = this::onUpdateChecklistProgress
    private val checklist by lazy { args.getParcelable(EXTRA_CHECKLIST) as Checklist }

    constructor(checklist: Checklist) : this(Bundle().apply {
        putParcelable(EXTRA_CHECKLIST, checklist)
    })

    override fun onInject() {
        DaggerChecklistComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onAttach(view: View) {
        val adapter = ChecklistAdapter(checklist.content, checklistItemClick, checklistProgress)
        setUpToolbar()
        checklistDetailRecyclerView?.initRecyclerView(adapter)
        val swipeHandler = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                onDeleteChecklist(adapter.getChecklistItem(position))
                adapter.removeAt(position)
                onUpdateChecklistProgress(Math.ceil(checklist.content.filter { it.value }.size * 100.0 / checklist.content.size).toInt())
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(checklistDetailRecyclerView)
        currentProgress()

        if(checklist.custom){
            addNewItemChecklist.visibility = View.VISIBLE
        }

        //add new checklist item
        addNewItemChecklist.setOnClickListener {

            val li = LayoutInflater.from(context)
            val promptsView = li.inflate(R.layout.editchecklistdialog, null)

            val alertDialogBuilder = AlertDialog.Builder(context)

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView)

            val userInput = promptsView
                    .findViewById(R.id.editChecklistItem) as EditText

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton(R.string.export_dialog_ok,
                            DialogInterface.OnClickListener { _, _ ->
                                checklist.content.add(Content(check = userInput.text.toString(), checklist = checklist))
                                onChecklistUpdated(checklist)
                            })
                    .setNegativeButton(R.string.export_dialog_cancel,
                            DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })

            // create alert dialog
            val alertDialog = alertDialogBuilder.create()

            // show it
            alertDialog.show() }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        disableNavigation()
        presenter.onAttach(this)
        checklistView = inflater.inflate(R.layout.checklist_detail_view, container, false)
        return checklistView
    }

    private fun onDeleteChecklist(checklistItem: Content) = presenter.submitDeleteChecklistContent(checklistItem)

    private fun onChecklistItemClicked(checklistItem: Content) = presenter.submitInsertChecklistContent(checklistItem)

    private fun currentProgress() {
        progressAnswer.progress = checklist.progress
        titleProgressAnswer.text = "${checklist.progress}%"
    }

    private fun setUpToolbar() {
        checklistDetailToolbar?.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            mainActivity.supportActionBar?.title = getTitle()
        }
    }

    private fun onChecklistUpdated(checklist: Checklist)= presenter.submitUpdateChecklist(checklist)

    override fun onDestroy() {
        super.onDestroy()
        enableNavigation()
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

    companion object {
        const val EXTRA_CHECKLIST = "extra_checklist"
    }

    private fun getTitle() = context.getString(R.string.checklistDetail_title)
}