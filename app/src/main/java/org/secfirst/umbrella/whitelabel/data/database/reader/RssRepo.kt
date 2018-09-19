package org.secfirst.umbrella.whitelabel.data.database.reader

interface RssRepo {

    suspend fun saveRss(rss: RSS)

    suspend fun saveAllRss(rssList: List<RSS>)

    suspend fun delete(rss: RSS): Boolean

    suspend fun getAllRss(): List<RSS>
}