package org.secfirst.umbrella.data.database.reader

import android.os.Parcelable
import com.einmalfel.earl.Enclosure
import com.einmalfel.earl.Feed
import com.einmalfel.earl.Item
import com.einmalfel.earl.RSSFeed
import com.raizlabs.android.dbflow.annotation.Column
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import kotlinx.android.parcel.Parcelize
import org.secfirst.umbrella.data.database.AppDatabase
import java.io.Serializable
import java.util.*


data class RefRSS(var items: MutableList<RefRSSItem> = mutableListOf())

data class RefRSSItem(val link: String)

@Table(database = AppDatabase::class)
data class RSS(
    @PrimaryKey(autoincrement = true)
    var id: Long = 0,
    @Column
    var url_: String = "",
    var title_: String = "",
    var description_: String = "",
    var publicationDate_: Date = Date(),
    var imageLink_: String = "",
    var copyRight_: String = "",
    var author_: String = "",
    var items_: MutableList<Article> = mutableListOf()
) : Feed, Serializable {

    override fun getLink(): String = url_

    override fun getImageLink(): String = imageLink_

    override fun getItems(): MutableList<out Item> = items_

    override fun getCopyright() = author

    override fun getDescription(): String = description_

    override fun getTitle(): String = title_

    override fun getAuthor(): String = author_

    override fun getPublicationDate(): Date = publicationDate_

    constructor(link: String) : this(0, link, "", "", Date(), "", "", "")

}


data class Article(
    var url_: String = "", var title_: String = "",
    var description_: String = "", var publicationDate_: Date = Date(),
    var imageLink_: String = "", var copyRight_: String = "",
    var author_: String = "", var enclosures_: MutableList<out Enclosure> = mutableListOf()
) : Item, Serializable {

    override fun getLink() = url_

    override fun getImageLink() = imageLink_

    override fun getEnclosures() = enclosures_

    override fun getDescription() = description_

    override fun getId(): String = ""

    override fun getTitle() = title_

    override fun getAuthor() = author_

    override fun getPublicationDate() = publicationDate_

}


fun Feed.updateRSS(rss: RSS, rssFeed: RSSFeed?): RSS {
    rss.url_ = this.link ?: rss.url_
    rss.title_ = this.title
    rss.description_ = this.description ?: ""
    rss.publicationDate_ = this.publicationDate ?: Date()
    rss.imageLink_ = this.imageLink ?: ""
    rss.copyRight_ = this.copyright ?: ""
    rss.author_ = this.author ?: ""
    val articleList = mutableListOf<Article>()
    items.forEach {
        var imageUrl = ""
        if (it.imageLink != null) imageUrl = it.imageLink.toString()
        else if (rssFeed!!.items[items.indexOf(it)].media != null) imageUrl =
            rssFeed.items[items.indexOf(it)].media!!.contents[0].url.toString()
        val article = Article(
            it.link
                ?: "", it.title ?: "", it.description
                ?: "", it.publicationDate!!, imageUrl, "", it.author
                ?: "", it.enclosures
        )
        articleList.add(article)
    }
    rss.items_ = articleList
    return rss
}

const val RSS_FILE_NAME: String = "default_rss.json"

@Table(database = AppDatabase::class, allFields = true)
@Parcelize
data class FeedLocation(
    @PrimaryKey
    var id: Long = 1,
    var location: String = "",
    var iso2: String = ""
) : Parcelable

@Table(database = AppDatabase::class, allFields = true, useBooleanGetterSetters = false)
@Parcelize
data class FeedSource(
    @PrimaryKey(autoincrement = true)
    var id: Long = 0,
    var name: String = "",
    var lastChecked: Boolean = false,
    var code: Int = 0
) : Parcelable {

    constructor(name: String, lastChecked: Boolean, code: Int) : this(0, name, lastChecked, code)
}

data class LocationInfo(
    val locationNames: List<String> = mutableListOf(),
    val countryCode: String = ""
) {
    constructor() : this(mutableListOf(), "")
}