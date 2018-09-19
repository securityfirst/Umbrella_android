package org.secfirst.umbrella.whitelabel.feature.reader.view

import org.secfirst.umbrella.whitelabel.data.database.reader.RSS
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView

interface ReaderView : BaseView {

    fun showAllRss(rss: List<RSS>)

    fun showNewestRss(rss: RSS)
}