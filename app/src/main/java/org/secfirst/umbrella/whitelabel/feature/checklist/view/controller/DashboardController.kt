package org.secfirst.umbrella.whitelabel.feature.checklist.view.controller

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.checklist_dashboard.*
import kotlinx.android.synthetic.main.checklist_dashboard.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.component.SwipeToDeleteCallback
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.Dashboard
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.checklist.DaggerChecklistComponent
import org.secfirst.umbrella.whitelabel.feature.checklist.interactor.ChecklistBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.checklist.presenter.ChecklistBasePresenter
import org.secfirst.umbrella.whitelabel.feature.checklist.view.ChecklistView
import org.secfirst.umbrella.whitelabel.feature.checklist.view.adapter.DashboardAdapter
import org.secfirst.umbrella.whitelabel.misc.initRecyclerView
import javax.inject.Inject

class DashboardController(bundle: Bundle) : BaseController(bundle), ChecklistView {
    @Inject
    internal lateinit var presenter: ChecklistBasePresenter<ChecklistView, ChecklistBaseInteractor>
    private val dashboardItemClick: (Checklist) -> Unit = this::onDashboardItemClicked
    private val isCustomBoard by lazy { args.getBoolean(EXTRA_IS_CUSTOM_BOARD) }
    private lateinit var adapter: DashboardAdapter
    private val dashboardItemOnLongClick: (Checklist, Int) -> Unit = this::onDashboardItemLongClicked

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
        view.addNewChecklist.setOnClickListener { addNewChecklist() }
        presenter.onAttach(this)
        return view
    }

    override fun onAttach(view: View) {
        checkWorkflow()
    }

    private fun onDeleteChecklist(checklist: Checklist) {
        presenter.submitDeleteChecklist(checklist)
    }

    private fun initOnDeleteChecklist() {
        val swipeHandler = object : SwipeToDeleteCallback(context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val checklist = adapter.getChecklist(position)
                if (checklist != null) {
                    onDeleteChecklist(checklist)
                }
                adapter.removeAt(position)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeHandler)
        itemTouchHelper.attachToRecyclerView(checklistDashboardRecyclerView)
    }

    private fun checkWorkflow() {
        if (isCustomBoard) {
            addNewChecklist?.visibility = View.VISIBLE
            presenter.submitLoadCustomDashboard()
            initOnDeleteChecklist()
        } else
            presenter.submitLoadDashboard()
    }

    private fun onDashboardItemClicked(checklist: Checklist) {
        parentController?.router?.pushController(RouterTransaction.with(ChecklistController(checklist.id)))
    }

    private fun onDashboardItemLongClicked(checklist: Checklist, position: Int) {
        AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.checklist_delete_item))
                .setNegativeButton(context.getString(R.string.cancel)) { dialog, _ -> dialog.dismiss() }
                .setPositiveButton(context.getString(R.string.ok)) { _, _ ->
                    presenter.submitDeleteChecklist(checklist)
                    adapter.removeAt(position)
                }
                .create()
                .show()
    }

    private fun addNewChecklist() {
        parentController?.router?.pushController(RouterTransaction
                .with(ChecklistCustomController(System.currentTimeMillis().toString())))
    }

    override fun showDashboard(dashboards: MutableList<Dashboard.Item>) {
        adapter = DashboardAdapter(dashboards, dashboardItemClick, dashboardItemOnLongClick)
        checklistDashboardRecyclerView?.initRecyclerView(adapter)
    }

    companion object {
        const val EXTRA_IS_CUSTOM_BOARD = "custom_board"
    }
}