package org.secfirst.umbrella.whitelabel.feature.reader.view.rss

import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.support.RouterPagerAdapter
import org.secfirst.umbrella.whitelabel.feature.reader.view.feed.FeedSettingsController
import org.secfirst.umbrella.whitelabel.feature.reader.view.rss.RssController
import org.secfirst.umbrella.whitelabel.feature.reader.view.server.ServerController

class ReaderAdapter(host: Controller) : RouterPagerAdapter(host) {
    override fun configureRouter(router: Router, position: Int) {
        if (!router.hasRootController()) {
            when (position) {
                0 -> router.setRoot(RouterTransaction.with(FeedSettingsController()))
                1 -> router.setRoot(RouterTransaction.with(RssController()))
                2 -> router.setRoot(RouterTransaction.with(ServerController()))
            }
        }
    }

    override fun getPageTitle(position: Int): String {
        return when (position) {
            0 -> "FEED"
            1 -> "RSS"
            2 -> "SWITCH SERVER"
            else -> ""
        }
    }

    override fun getCount() = 3

}