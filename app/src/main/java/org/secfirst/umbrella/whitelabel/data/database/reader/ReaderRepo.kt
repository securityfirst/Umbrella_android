package org.secfirst.umbrella.whitelabel.data.database.reader

interface ReaderRepo {

    suspend fun saveRss(rss: RSS)

    suspend fun saveFeedLocation(feedLocation: FeedLocation)

    suspend fun saveAllRss(rssList: List<RSS>)

    suspend fun saveAllFeedSources(feedSources: List<FeedSource>)

    suspend fun delete(rss: RSS): Boolean

    suspend fun getAllRss(): List<RSS>

    suspend fun getFeedLocation(): FeedLocation?

    suspend fun getAllFeedSources(): List<FeedSource>

}