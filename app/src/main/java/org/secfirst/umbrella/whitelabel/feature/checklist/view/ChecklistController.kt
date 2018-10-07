package org.secfirst.umbrella.whitelabel.feature.checklist.view

import android.annotation.SuppressLint
import android.content.Context
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
import org.secfirst.umbrella.whitelabel.misc.initRecyclerView
import javax.inject.Inject

class ChecklistController(bundle: Bundle) : BaseController(bundle), ChecklistView {

    @Inject
    internal lateinit var presenter: ChecklistBasePresenter<ChecklistView, ChecklistBaseInteractor>
    private lateinit var checklistView: View
    private val checklistItemClick: (Content) -> Unit = this::onChecklistItemClicked
    private val checklistProgress: (Int) -> Unit = this::onUpdateChecklistProgress
    private val checklist by lazy { args.getParcelable(EXTRA_CHECKLIST) as Checklist }
    var titleTab = ""

    constructor(checklist: Checklist) : this(Bundle().apply {
        putParcelable(EXTRA_CHECKLIST, checklist)
    })

    override fun onContextAvailable(context: Context) {
        titleTab = context.getString(R.string.checklist_title)
    }


    private fun onChecklistItemClicked(checklistItem: Content) {
        presenter.submitInsertChecklist(checklistItem)
    }

    @SuppressLint("SetTextI18n")
    private fun onUpdateChecklistProgress(percentage: Int) {
        progressAnswer?.progress = percentage
        titleProgressAnswer.text = "$percentage%"
    }

    override fun onAttach(view: View) {
        val adapter = ChecklistAdapter(checklist.content, checklistItemClick, checklistProgress)
        checklistRecyclerView?.initRecyclerView(adapter)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        checklistView = inflater.inflate(R.layout.checklist_view, container, false)
        return checklistView
    }

    override fun onInject() {
        DaggerChecklistComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    companion object {
        const val EXTRA_CHECKLIST = "extra_checklist"
    }
}