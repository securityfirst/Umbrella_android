package org.secfirst.umbrella.whitelabel.feature.reader.view

import org.secfirst.umbrella.whitelabel.data.database.reader.FeedLocation
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.database.reader.LocationInfo
import org.secfirst.umbrella.whitelabel.data.database.reader.RSS
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView

interface ReaderView : BaseView {

    fun showAllRss(rss: List<RSS>) {}

    fun showNewestRss(rss: RSS) {}

    fun prepareFeedSource(feedSources: List<FeedSource>) {}

    fun prepareFeedLocation(feedLocation: FeedLocation) {}

    fun prepareRefreshInterval(position: Int) {}

    fun newAddressAvailable(locationInfo: LocationInfo) {}
}