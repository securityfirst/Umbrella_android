package org.secfirst.umbrella.whitelabel.data.database.content


import android.content.Context
import android.content.Intent
import android.net.Uri
import org.apache.commons.text.WordUtils
import org.secfirst.advancedsearch.models.SearchResult
import org.secfirst.umbrella.whitelabel.data.database.checklist.Content
import org.secfirst.umbrella.whitelabel.data.database.lesson.Module
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.database.reader.RSS

class ContentData(val modules: MutableList<Module> = mutableListOf())

fun Content.toSearchResult(): SearchResult {
    val segments = this.checklist?.id.orEmpty().split("/")
    return SearchResult("${WordUtils.capitalizeFully(segments[1])} - ${WordUtils.capitalizeFully(segments[3])}", ""
    ) { c: Context ->
        val withoutLanguage = this.checklist?.id?.split("/")?.drop(1)?.joinToString("/")
        c.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("umbrella://$withoutLanguage")))
    }
}

fun createFeedSources(): List<FeedSource> {

    val feedSources = mutableListOf<FeedSource>()
    val feedSource1 = FeedSource("ReliefWeb / United Nations (UN)", false, 0)
    val feedSource3 = FeedSource("Foreign and Commonwealth Office", false, 2)
    val feedSource4 = FeedSource("Centres for Disease Control", false, 3)
    val feedSource5 = FeedSource("Global Disaster Alert Coordination System", false, 4)
    val feedSource6 = FeedSource("US State Department Country Warnings", false, 5)

    feedSources.add(feedSource1)
    feedSources.add(feedSource3)
    feedSources.add(feedSource4)
    feedSources.add(feedSource5)
    feedSources.add(feedSource6)
    return feedSources
}

fun createDefaultRSS(): List<RSS> {
    val rssList = mutableListOf<RSS>()
    val rss1 = RSS("https://threatpost.com/feed/")
    val rss2 = RSS("https://krebsonsecurity.com/feed/")
    val rss3 = RSS("http://feeds.bbci.co.uk/news/world/rss.xml?edition=uk")
    val rss4 = RSS("http://rss.cnn.com/rss/edition.rss")
    val rss5 = RSS("https://www.aljazeera.com/xml/rss/all.xml")
    val rss6 = RSS("https://www.theguardian.com/world/rss")
    rssList.add(rss1)
    rssList.add(rss2)
    rssList.add(rss3)
    rssList.add(rss4)
    rssList.add(rss5)
    rssList.add(rss6)
    return rssList
}