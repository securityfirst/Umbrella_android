package org.secfirst.umbrella.whitelabel.feature.tour.view

import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView

interface TourView : BaseView {

    fun downloadContentCompleted(res: Boolean)

    fun downloadContentInProgress()
}