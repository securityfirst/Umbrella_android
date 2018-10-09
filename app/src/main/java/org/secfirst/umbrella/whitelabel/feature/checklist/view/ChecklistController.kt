package org.secfirst.umbrella.whitelabel.feature.checklist.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.checklist_view.*
import kotlinx.android.synthetic.main.form_progress.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.Content
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.checklist.DaggerChecklistComponent
import org.secfirst.umbrella.whitelabel.feature.checklist.interactor.ChecklistBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.checklist.presenter.ChecklistBasePresenter
import org.secfirst.umbrella.whitelabel.feature.checklist.view.adapter.ChecklistAdapter
import org.secfirst.umbrella.whitelabel.misc.initRecyclerView
import javax.inject.Inject

@SuppressLint("SetTextI18n")
class ChecklistController(bundle: Bundle) : BaseController(bundle), ChecklistView {

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
        checklistRecyclerView?.initRecyclerView(adapter)
        presenter.onAttach(this)
        currentProgress()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        checklistView = inflater.inflate(R.layout.checklist_view, container, false)
        return checklistView
    }

    private fun onChecklistItemClicked(checklistItem: Content) {
        presenter.submitInsertChecklistContent(checklistItem)
    }

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

    companion object {
        const val EXTRA_CHECKLIST = "extra_checklist"
    }

    fun getTitle() = "Checklist"
}