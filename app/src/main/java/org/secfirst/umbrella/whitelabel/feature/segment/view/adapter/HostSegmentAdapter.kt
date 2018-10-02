package org.secfirst.umbrella.whitelabel.feature.segment.view.adapter

import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.support.RouterPagerAdapter
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.segment.HostSegmentTabControl
import org.secfirst.umbrella.whitelabel.feature.checklist.ChecklistController
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentController
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentDetailController

class HostSegmentAdapter(private val host: Controller,
                         private val controllers: List<Controller>) : RouterPagerAdapter(host) {


    override fun configureRouter(router: Router, position: Int) {
        if (!router.hasRootController()) {

            if (position == 0) {
                val segmentController = controllers[position] as SegmentController
                segmentController.setIndexTab(position)
                segmentController.hostSegmentTabControl = host as HostSegmentTabControl
                router.setRoot(RouterTransaction.with(segmentController))
            } else if (position >= 1 && position < controllers.size) {
                val detailController = controllers[position] as SegmentDetailController
                router.setRoot(RouterTransaction.with(detailController))
            } else {
                val checklistController = controllers[position] as ChecklistController
                router.setRoot(RouterTransaction.with(checklistController))
            }
        }
    }

    override fun getPageTitle(position: Int): String {
        return if (position == 0) {
            val currentController = controllers[position] as SegmentController
            currentController.context.getString(R.string.lesson_tab)

        } else if (position >= 1 && position < controllers.size) {
            val currentController = controllers[position] as SegmentDetailController
            currentController.getTitle()

        } else {
            val currentController = controllers[position] as ChecklistController
            currentController.titleTab
        }
    }

    override fun getCount() = controllers.size

}