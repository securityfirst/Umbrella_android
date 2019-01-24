package org.secfirst.umbrella.whitelabel.feature.checklist.view.controller

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.checklist_view.*
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
class ChecklistController(bundle: Bundle) : BaseController(bundle), ChecklistView {

    @Inject
    internal lateinit var presenter: ChecklistBasePresenter<ChecklistView, ChecklistBaseInteractor>
    private lateinit var checklistView: View
    private lateinit var adapter: ChecklistAdapter
    private val checklistItemClick: (Content) -> Unit = this::onChecklistItemClicked
    private val checklistProgress: (Int) -> Unit = this::onUpdateChecklistProgress
    private lateinit var checklist: Checklist
    private val checklistId by lazy { args.getString(ChecklistCustomController.EXTRA_ID_CUSTOM_CHECKLIST) }

    constructor(checklistId: String) : this(Bundle().apply {
        putString(ChecklistCustomController.EXTRA_ID_CUSTOM_CHECKLIST, checklistId)
    })

    override fun onInject() {
        DaggerChecklistComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        checklistView = inflater.inflate(R.layout.checklist_view, container, false)
        presenter.onAttach(this)
        presenter.submitChecklist(checklistId)
        initSwipeDelete()
        return checklistView
    }

    private fun initSwipeDelete() {
        val swipeHandler = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                onDeleteChecklist(adapter.getChecklistItem(position))
                adapter.removeAt(position)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(checklistRecyclerView)
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

    override fun getChecklist(checklist: Checklist) {
        this.checklist = checklist
        checklistRecyclerView?.initRecyclerView(adapter)
        adapter = ChecklistAdapter(checklist.content, checklistItemClick, checklistProgress)
        currentProgress()
    }

    fun getTitle() = "Checklist"
}