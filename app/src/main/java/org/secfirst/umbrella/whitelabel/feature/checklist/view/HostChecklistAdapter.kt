package org.secfirst.umbrella.whitelabel.feature.checklist.view

import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.support.RouterPagerAdapter

class HostChecklistAdapter(host: Controller) : RouterPagerAdapter(host) {

    override fun configureRouter(router: Router, position: Int) {
        if (!router.hasRootController()) {
            when (position) {
                0 -> router.setRoot(RouterTransaction.with(DashboardController()))
                1 -> router.setRoot(RouterTransaction.with(CustomChecklistController()))
            }
        }
    }

    override fun getPageTitle(position: Int) = if (position == 0) "OVERVIEW" else "CUSTOM"

    override fun getCount() = 2
}