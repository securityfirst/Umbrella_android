package org.secfirst.umbrella.whitelabel.feature.checklist.view

import org.secfirst.umbrella.whitelabel.data.database.checklist.Dashboard
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView

interface ChecklistView : BaseView {
    fun showDashboard(dashboards: List<Dashboard.Item>){}
}