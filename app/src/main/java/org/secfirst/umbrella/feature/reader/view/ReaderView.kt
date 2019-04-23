package org.secfirst.umbrella.feature.reader.view

import org.secfirst.umbrella.data.database.reader.FeedLocation
import org.secfirst.umbrella.data.database.reader.FeedSource
import org.secfirst.umbrella.data.database.reader.RSS
import org.secfirst.umbrella.data.network.FeedItemResponse
import org.secfirst.umbrella.feature.base.view.BaseView

interface ReaderView : BaseView {

    fun showRss(rss: RSS) {}

    fun showNewestRss(rss: RSS) {}

    fun showRssError() {}

    fun isSkipPassword(res: Boolean) {}

    fun isChangedToken(res: Boolean) {}

    fun startFeedController(feedItemResponse: Array<FeedItemResponse>, isFirstRequest: Boolean = false) {}

    fun feedError() {}

    fun prepareView(feedSources: List<FeedSource>,
                    refreshInterval: String,
                    feedLocation: FeedLocation) {
    }
}