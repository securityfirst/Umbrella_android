package org.secfirst.umbrella.whitelabel.feature.reader.view

import org.secfirst.umbrella.whitelabel.data.database.reader.FeedLocation
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.database.reader.LocationInfo
import org.secfirst.umbrella.whitelabel.data.database.reader.RSS
import org.secfirst.umbrella.whitelabel.data.network.FeedItemResponse
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView

interface ReaderView : BaseView {

    fun showAllRss(rss: List<RSS>) {}

    fun showNewestRss(rss: RSS) {}

    fun newAddressAvailable(locationInfo: LocationInfo) {}

    fun startFeedController(feedItemResponse: Array<FeedItemResponse>, isFirstRequest: Boolean = false) {}

    fun prepareView(feedSources: List<FeedSource>,
                    refreshIntervalPosition: Int,
                    feedLocation: FeedLocation) {
    }
}