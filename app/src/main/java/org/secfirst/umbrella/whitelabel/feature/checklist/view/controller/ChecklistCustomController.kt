package org.secfirst.umbrella.whitelabel.feature.checklist.view.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.checklist_custom_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.checklist.DaggerChecklistComponent
import org.secfirst.umbrella.whitelabel.feature.checklist.interactor.ChecklistBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.checklist.presenter.ChecklistBasePresenter
import org.secfirst.umbrella.whitelabel.feature.checklist.view.ChecklistView
import javax.inject.Inject

class ChecklistCustomController(bundle: Bundle) : BaseController(bundle), ChecklistView {


    @Inject
    internal lateinit var presenter: ChecklistBasePresenter<ChecklistView, ChecklistBaseInteractor>

    private val idChecklist by lazy { args.getString(EXTRA_ID_CUSTOM_CHECKLIST) }

    constructor(idChecklist: String) : this(Bundle().apply {
        putString(EXTRA_ID_CUSTOM_CHECKLIST, idChecklist)
    })

    override fun onInject() {
        DaggerChecklistComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onAttach(view: View) {
        setUpToolbar()
        disableNavigation()
    }

    override fun onDestroyView(view: View) {
        enableNavigation()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.checklist_custom_view, container, false)
        addChecklistItemByKeyboard()
        presenter.onAttach(this)
        addChecklistItem?.setOnClickListener { addChecklistItem() }
        return view
    }

    private fun addChecklistItem() {
        val checklistItem = checklistContent.text.toString()
        presenter.submitInsertCustomChecklist(checklistItem, idChecklist)
    }

    private fun addChecklistItemByKeyboard() {

    }

    private fun setUpToolbar() {
        customChecklistToolbar?.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            mainActivity.supportActionBar?.title = "Your checklists"
        }
    }

    companion object {
        const val EXTRA_ID_CUSTOM_CHECKLIST = "id_custom_check_list"
    }
}