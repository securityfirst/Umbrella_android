package org.secfirst.umbrella.whitelabel.data.database.account

import org.secfirst.umbrella.whitelabel.data.database.reader.FeedLocation
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource

interface AccountRepo {

    suspend fun loginDatabase(userToken: String)

    suspend fun changeToken(userToken: String): Boolean

    suspend fun getFeedLocation(): FeedLocation?

    suspend fun getAllFeedSources(): List<FeedSource>

    suspend fun saveAllFeedSources(feedSources: List<FeedSource>)

    suspend fun saveFeedLocation(feedLocation: FeedLocation)
}