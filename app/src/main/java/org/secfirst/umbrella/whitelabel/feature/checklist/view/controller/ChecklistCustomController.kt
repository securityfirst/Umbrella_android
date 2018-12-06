package org.secfirst.umbrella.whitelabel.feature.checklist.view.controller

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.checklist_custom_view.*
import kotlinx.android.synthetic.main.checklist_custom_view.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.checklist.DaggerChecklistComponent
import org.secfirst.umbrella.whitelabel.feature.checklist.interactor.ChecklistBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.checklist.presenter.ChecklistBasePresenter
import org.secfirst.umbrella.whitelabel.feature.checklist.view.ChecklistView
import org.secfirst.umbrella.whitelabel.feature.checklist.view.adapter.ChecklistCustomAdapter
import org.secfirst.umbrella.whitelabel.misc.initRecyclerView
import javax.inject.Inject

class ChecklistCustomController(bundle: Bundle) : BaseController(bundle), ChecklistView {


    @Inject
    internal lateinit var presenter: ChecklistBasePresenter<ChecklistView, ChecklistBaseInteractor>
    private val idChecklist by lazy { args.getString(EXTRA_ID_CUSTOM_CHECKLIST) }
    private lateinit var adapter: ChecklistCustomAdapter
    private lateinit var checklistTitle: String

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
        presenter.onAttach(this)
        adapter = ChecklistCustomAdapter()
        view.checklistCustomRecyclerView.initRecyclerView(adapter)
        view.addChecklistItem.setOnClickListener { addChecklistItem() }
        enterAction(view)
        return view
    }

    private fun enterAction(view: View) {
        view.checklistContent?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                addChecklistItem()
                return@setOnKeyListener true
            }
            false
        }
    }

    private fun addChecklistItem() {
        val checklistItem = checklistContent.text.toString()
        checklistContent?.text?.clear()
        if (adapter.size() == 0) checklistTitle = checklistItem
        else {
            checklistContent.hint = context.getText(R.string.custom_checklist_hint_add_checklistItem)
            presenter.submitInsertCustomChecklist(checklistTitle, checklistItem, idChecklist)
        }
        adapter.add(checklistItem)
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