package org.secfirst.umbrella.whitelabel.feature.checklist.view.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.checklist_dashboard.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
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

class DashboardController : BaseController(), ChecklistView {
    @Inject
    internal lateinit var presenter: ChecklistBasePresenter<ChecklistView, ChecklistBaseInteractor>
    private val dashboardItemClick: (Checklist?) -> Unit = this::onDashboardItemClicked

    override fun onInject() {
        DaggerChecklistComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onAttach(view: View) {
        presenter.onAttach(this)
        presenter.submitLoadDashboard()
    }


    private fun onDashboardItemClicked(checklist: Checklist?) {
        if (checklist != null)
            parentController?.router?.pushController(RouterTransaction.with(ChecklistDetailController(checklist)))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.checklist_dashboard, container, false)
    }

    override fun showDashboard(dashboards: List<Dashboard.Item>) {
        val dashboardAdapter = DashboardAdapter(dashboards, dashboardItemClick)
        checklistDashboardRecyclerView?.initRecyclerView(dashboardAdapter)
    }
}