package org.secfirst.umbrella.feature.reader.view

import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.viewpager.RouterPagerAdapter
import org.secfirst.umbrella.R
import org.secfirst.umbrella.feature.reader.view.feed.FeedSettingsController
import org.secfirst.umbrella.feature.reader.view.rss.RssController

class ReaderAdapter(private val host: Controller) : RouterPagerAdapter(host) {
    override fun configureRouter(router: Router, position: Int) {
        if (!router.hasRootController()) {
            when (position) {
                0 -> router.setRoot(RouterTransaction.with(FeedSettingsController()))
                1 -> router.setRoot(RouterTransaction.with(RssController()))
            }
        }
    }

    override fun getPageTitle(position: Int): String {
        return when (position) {
            0 -> host.applicationContext?.getString(R.string.feed_title_tab) ?: ""
            1 -> host.applicationContext?.getString(R.string.rss_title_tab) ?: ""
            else -> ""
        }
    }

    override fun getCount() = 2

}