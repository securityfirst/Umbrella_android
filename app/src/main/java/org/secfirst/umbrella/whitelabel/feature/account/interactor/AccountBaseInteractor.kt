package org.secfirst.umbrella.whitelabel.feature.account.interactor

import org.secfirst.umbrella.whitelabel.data.database.reader.FeedLocation
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface AccountBaseInteractor : BaseInteractor {

    suspend fun accessDatabase(userToken: String)

    suspend fun changeDatabaseAccess(userToken: String): Boolean

    suspend fun insertFeedLocation(feedLocation: FeedLocation)

    suspend fun insertAllFeedSources(feedSources: List<FeedSource>)

    suspend fun fetchFeedSources(): List<FeedSource>

    suspend fun fetchFeedLocation(): FeedLocation?

    fun setSkipPassword(value: Boolean): Boolean

    suspend fun fetchRefreshInterval(): Int

    suspend fun putRefreshInterval(position: Int): Boolean

    fun setLoggedIn(): Boolean

    fun isSkippPassword(): Boolean

    fun setMaskApp(value: Boolean): Boolean

    fun isMaskApp(): Boolean
}