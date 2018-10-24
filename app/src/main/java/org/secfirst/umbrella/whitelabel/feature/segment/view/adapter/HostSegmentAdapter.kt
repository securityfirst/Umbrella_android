package org.secfirst.umbrella.whitelabel.feature.segment.view.adapter

import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.support.RouterPagerAdapter
import org.secfirst.umbrella.whitelabel.feature.checklist.view.ChecklistController
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentController
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentDetailController

class HostSegmentAdapter(host: Controller,
                         private val controllers: List<Controller>,
                         private val segmentPageLimit: Int) : RouterPagerAdapter(host) {


    override fun configureRouter(router: Router, position: Int) {
        if (!router.hasRootController()) {

            when (position) {
                0 -> {
                    val segmentController = controllers[position] as SegmentController
                    segmentController.setIndexTab(position)
                    router.setRoot(RouterTransaction.with(segmentController))
                }
                in 1..segmentPageLimit -> {
                    val detailController = controllers[position] as SegmentDetailController
                    router.setRoot(RouterTransaction.with(detailController))
                }
                else -> {
                    val checklistController = controllers[position] as ChecklistController
                    router.setRoot(RouterTransaction.with(checklistController))
                }
            }
        }
    }

    override fun getPageTitle(position: Int): String {
        return when (position) {
            0 -> {
                val currentController = controllers[position] as SegmentController
                currentController.getTitle()

            }
            in 1..segmentPageLimit -> {
                val currentController = controllers[position] as SegmentDetailController
                currentController.getTitle()

            }
            else -> {
                val currentController = controllers[position] as ChecklistController
                currentController.getTitle()
            }
        }
    }

    override fun getCount() = controllers.size

}