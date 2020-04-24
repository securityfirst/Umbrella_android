package org.secfirst.umbrella.feature.checklist.view

import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.checklist.Dashboard
import org.secfirst.umbrella.feature.base.view.BaseView

interface ChecklistView : BaseView {

    fun showDashboard(dashboards: MutableList<Dashboard.Item>) {}

    fun getChecklist(checklist: Checklist) {}

    fun showPathways(dashboards: MutableList<Dashboard.Item>) {}
}