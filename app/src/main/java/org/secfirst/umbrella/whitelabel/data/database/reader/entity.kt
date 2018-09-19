package org.secfirst.umbrella.whitelabel.data.database.reader

import com.einmalfel.earl.Enclosure
import com.einmalfel.earl.Feed
import com.einmalfel.earl.Item
import com.raizlabs.android.dbflow.annotation.PrimaryKey
import com.raizlabs.android.dbflow.annotation.Table
import org.secfirst.umbrella.whitelabel.data.database.AppDatabase
import java.io.Serializable
import java.util.*


data class RefRSS(var items: MutableList<RefRSSItem> = mutableListOf())

data class RefRSSItem(val link: String)

@Table(database = AppDatabase::class)
data class RSS(@PrimaryKey
               var url_: String = "", var title_: String = "",
               var description_: String = "", var publicationDate_: Date = Date(),
               var imageLink_: String = "", var copyRight_: String = "",
               var author_: String = "", var items_: MutableList<Article> = mutableListOf()) : Feed, Serializable {

    override fun getLink(): String = url_

    override fun getImageLink(): String = imageLink_

    override fun getItems(): MutableList<out Item> = items_

    override fun getCopyright() = author

    override fun getDescription(): String = description_

    override fun getTitle(): String = title_

    override fun getAuthor(): String = author_

    override fun getPublicationDate(): Date = publicationDate_

    constructor(link: String = "") : this(link, "", "", Date(), "", "", "")

}

data class Article(var url_: String = "", var title_: String = "",
                   var description_: String = "", var publicationDate_: Date = Date(),
                   var imageLink_: String = "", var copyRight_: String = "",
                   var author_: String = "", var enclosures_: MutableList<out Enclosure> = mutableListOf()) : Item, Serializable {

    override fun getLink() = url_

    override fun getImageLink() = imageLink_

    override fun getEnclosures() = enclosures_

    override fun getDescription() = description_

    override fun getId(): String = ""

    override fun getTitle() = title_

    override fun getAuthor() = author_

    override fun getPublicationDate() = publicationDate_

}

val Feed.convertToRSS: RSS
    get() {
        val rss = RSS()
        rss.url_ = this.link ?: ""
        rss.title_ = this.title
        rss.description_ = this.description ?: ""
        rss.publicationDate_ = this.publicationDate ?: Date()
        rss.imageLink_ = this.imageLink ?: ""
        rss.copyRight_ = this.copyright ?: ""
        rss.author_ = this.author ?: ""
        val articleList = mutableListOf<Article>()
        items.forEach {
            val article = Article(it.link
                    ?: "", it.title ?: "", it.description
                    ?: "", it.publicationDate!!, it.imageLink ?: "", "", it.author
                    ?: "", it.enclosures)
            articleList.add(article)
        }
        rss.items_ = articleList
        return rss
    }
const val RSS_FILE_NAME: String = "default_rss.json"

