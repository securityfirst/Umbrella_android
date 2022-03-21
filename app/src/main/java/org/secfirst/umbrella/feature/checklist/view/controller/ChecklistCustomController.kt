package org.secfirst.umbrella.feature.checklist.view.controller

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.checklist_custom_view.*
import kotlinx.android.synthetic.main.checklist_custom_view.view.*
import org.secfirst.umbrella.R
import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.component.SwipeToDeleteCallback
import org.secfirst.umbrella.feature.base.view.BaseController
import org.secfirst.umbrella.feature.checklist.DaggerChecklistComponent
import org.secfirst.umbrella.feature.checklist.interactor.ChecklistBaseInteractor
import org.secfirst.umbrella.feature.checklist.presenter.ChecklistBasePresenter
import org.secfirst.umbrella.feature.checklist.view.ChecklistView
import org.secfirst.umbrella.feature.checklist.view.adapter.ChecklistCustomAdapter
import org.secfirst.umbrella.misc.initRecyclerView
import javax.inject.Inject


class ChecklistCustomController(bundle: Bundle) : BaseController(bundle), ChecklistView {

    @Inject
    internal lateinit var presenter: ChecklistBasePresenter<ChecklistView, ChecklistBaseInteractor>
    private val checklistId by lazy { args.getString(EXTRA_ID_CUSTOM_CHECKLIST) }
    private val checklistName by lazy { args.getString(EXTRA_CUSTOM_CHECKLIST_NAME) }
    private lateinit var adapter: ChecklistCustomAdapter

    constructor(checklistId: String, checklistName: String) : this(Bundle().apply {
        putString(EXTRA_ID_CUSTOM_CHECKLIST, checklistId)
        putString(EXTRA_CUSTOM_CHECKLIST_NAME, checklistName)
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
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
        adapter.add(checklistItem)
    }

    private fun submitChecklist() {
        if (adapter.getChecklistItems().isNotEmpty())
            checklistName?.let {
                checklistId?.let { it1 ->
                    presenter.submitInsertCustomChecklist(
                        it,
                        it1, adapter.getChecklistItems())
                }
            }
    }

    private fun initDeleteChecklistItem(view: View) {
        val swipeHandler = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                adapter.removeAt(position)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(view.checklistCustomRecyclerView)
    }

    private fun setUpToolbar() {
        customChecklistToolbar?.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            mainActivity.supportActionBar?.title = checklistName
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
    }

    override fun handleBack(): Boolean {
        submitChecklist()
        router.popCurrentController()
        return true
    }

    companion object {
        private const val EXTRA_ID_CUSTOM_CHECKLIST = "id_custom_check_list"
        private const val EXTRA_CUSTOM_CHECKLIST_NAME = "name_custom_check_list"
        private const val INITIAL_INDEX = 0
    }
}