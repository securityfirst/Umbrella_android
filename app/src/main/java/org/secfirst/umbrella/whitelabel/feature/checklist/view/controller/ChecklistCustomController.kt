package org.secfirst.umbrella.whitelabel.feature.checklist.view.controller

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import kotlinx.android.synthetic.main.checklist_custom_view.*
import kotlinx.android.synthetic.main.checklist_custom_view.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.component.SwipeToDeleteCallback
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
    private val checklistId by lazy { args.getString(EXTRA_ID_CUSTOM_CHECKLIST) }
    private lateinit var adapter: ChecklistCustomAdapter

    constructor(checklistId: String) : this(Bundle().apply {
        putString(EXTRA_ID_CUSTOM_CHECKLIST, checklistId)
    })

    override fun onInject() {
        DaggerChecklistComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onAttach(view: View) {
        setUpToolbar()
        enableNavigation(false)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.checklist_custom_view, container, false)
        presenter.onAttach(this)
        adapter = ChecklistCustomAdapter()
        view.checklistCustomRecyclerView.initRecyclerView(adapter)
        view.addChecklistItem.setOnClickListener { validateChecklistItemIsEmpty() }
        enterAction(view)
        initDeleteChecklistItem(view)
        return view
    }

    private fun enterAction(view: View) {
        view.checklistContent?.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                validateChecklistItemIsEmpty()
                return@setOnKeyListener true
            }
            false
        }
    }

    private fun addChecklistItem() {
        val checklistItem = checklistContent.text.toString()
        checklistContent?.text?.clear()
        if (cardViewTitle?.visibility == INVISIBLE) {
            editChecklistTitle?.setText(checklistItem)
            cardViewTitle?.visibility = VISIBLE
            checklistContent.hint = context.getText(R.string.custom_checklist_hint_add_checklistItem)
        } else {
            adapter.add(checklistItem)
        }
    }

    private fun submitChecklist() {
        if (adapter.getChecklistItems().isNotEmpty())
            presenter.submitInsertCustomChecklist(editChecklistTitle.text.toString(),
                    checklistId, adapter.getChecklistItems())
    }

    private fun initDeleteChecklistItem(view: View) {
        val swipeHandler = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                adapter.removeAt(position)
                if (position == INITIAL_INDEX)
                    checklistContent.hint = context.getText(R.string.custom_checklist_hint_add_checklistTitle)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(view.checklistCustomRecyclerView)
    }

    private fun setUpToolbar() {
        customChecklistToolbar?.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            mainActivity.supportActionBar?.title = context.getString(R.string.custom_checklist_title)
        }
    }

    private fun validateChecklistItemIsEmpty(): Boolean {
        return if (checklistContent?.text.toString().isNotBlank()) {
            addChecklistItem()
            true
        } else {
            checklistContent?.error = context.getString(R.string.checklist_custom_empty_item_error_message)
            false
        }
    }

    override fun onDestroyView(view: View) {
        enableNavigation(true)
        submitChecklist()
    }

    override fun handleBack(): Boolean {
        submitChecklist()
        return super.handleBack()
    }

    companion object {
        private const val EXTRA_ID_CUSTOM_CHECKLIST = "id_custom_check_list"
        private const val INITIAL_INDEX = 0
    }
}