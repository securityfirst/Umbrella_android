package org.secfirst.umbrella.whitelabel.feature.account.interactor

import org.secfirst.umbrella.whitelabel.data.database.reader.FeedLocation
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.database.reader.LocationInfo
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface AccountBaseInteractor : BaseInteractor {

    suspend fun accessDatabase(userToken: String)

    suspend fun changeDatabaseAccess(userToken: String): Boolean

    suspend fun insertFeedLocation(feedLocation: FeedLocation)

    suspend fun insertAllFeedSources(feedSources: List<FeedSource>)

    suspend fun fetchFeedSources(): List<FeedSource>

    suspend fun fetchFeedLocation(): FeedLocation?

    suspend fun fetchGeolocation(nameLocation: String): LocationInfo

    fun setLoggedIn(): Boolean
}