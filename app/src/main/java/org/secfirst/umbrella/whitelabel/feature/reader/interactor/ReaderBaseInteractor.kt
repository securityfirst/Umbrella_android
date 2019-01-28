package org.secfirst.umbrella.whitelabel.feature.reader.interactor


import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedLocation
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.database.reader.RSS
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface ReaderBaseInteractor : BaseInteractor {

    suspend fun insertRss(rss: RSS)

    suspend fun insertFeedLocation(feedLocation: FeedLocation)

    suspend fun insertAllFeedSources(feedSources: List<FeedSource>)

    suspend fun insertAllRss(rssList: List<RSS>)

    suspend fun fetchRss(): List<RSS>

    suspend fun fetchFeedSources(): List<FeedSource>

    suspend fun fetchFeedLocation(): FeedLocation?

    suspend fun deleteRss(rss: RSS): Boolean

    suspend fun deleteLocation()

    suspend fun doRSsCallAsync(url: String): Deferred<ResponseBody>

    suspend fun fetchRefreshInterval(): Int

    suspend fun putRefreshInterval(position: Int): Boolean

    suspend fun doFeedCallAsync(countryCode: String, source: String, since: String): Deferred<ResponseBody>

    suspend fun applyChangeDatabaseAccess(userToken: String): Boolean

}