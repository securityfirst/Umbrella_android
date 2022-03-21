package org.secfirst.umbrella.feature.checklist.view.adapter

import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.viewpager.RouterPagerAdapter
import org.secfirst.umbrella.R
import org.secfirst.umbrella.feature.checklist.view.controller.DashboardController

class HostChecklistAdapter(private val host: Controller) : RouterPagerAdapter(host) {

    override fun configureRouter(router: Router, position: Int) {
        if (!router.hasRootController()) {
            when (position) {
                0 -> router.setRoot(RouterTransaction.with(DashboardController(false)))
                1 -> router.setRoot(RouterTransaction.with(DashboardController(true)))
            }
        }
    }

    override fun getPageTitle(position: Int) = if (position == 0) host.activity?.getString(R.string.checklist_title_tab)
    else host.activity?.getString(R.string.custom_checklist_title_tab)

    override fun getCount() = 2
}