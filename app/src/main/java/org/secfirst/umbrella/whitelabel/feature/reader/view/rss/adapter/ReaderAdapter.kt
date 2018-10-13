package org.secfirst.umbrella.whitelabel.feature.reader.view.rss.adapter

import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.support.RouterPagerAdapter
import org.secfirst.umbrella.whitelabel.feature.reader.view.feed.FeedController
import org.secfirst.umbrella.whitelabel.feature.reader.view.rss.RssController

class ReaderAdapter(host: Controller) : RouterPagerAdapter(host) {
    override fun configureRouter(router: Router, position: Int) {
        if (!router.hasRootController()) {
            when (position) {
                0 -> router.setRoot(RouterTransaction.with(FeedController()))
                1 -> router.setRoot(RouterTransaction.with(RssController()))
            }
        }
    }

    override fun getPageTitle(position: Int) = if (position == 0) "FEED" else "RSS"

    override fun getCount() = 2

}