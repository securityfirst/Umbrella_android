package org.secfirst.umbrella.feature.reader.interactor

import kotlinx.coroutines.Deferred
import okhttp3.ResponseBody
import org.secfirst.umbrella.data.database.content.ContentRepo
import org.secfirst.umbrella.data.database.reader.FeedLocation
import org.secfirst.umbrella.data.database.reader.FeedSource
import org.secfirst.umbrella.data.database.reader.RSS
import org.secfirst.umbrella.data.database.reader.ReaderRepo
import org.secfirst.umbrella.data.network.ApiHelper
import org.secfirst.umbrella.data.preferences.AppPreferenceHelper
import org.secfirst.umbrella.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class ReaderInteractorImp @Inject constructor(apiHelper: ApiHelper,
                                              preferenceHelper: AppPreferenceHelper,
                                              contentRepo: ContentRepo,
                                              private val readerRepo: ReaderRepo)

    : BaseInteractorImp(apiHelper, preferenceHelper, contentRepo), ReaderBaseInteractor {

    override suspend fun applyChangeDatabaseAccess(userToken: String): Boolean {
        val res = readerRepo.changeToken(userToken)
        if (res) {
            preferenceHelper.setLoggedIn(true)
            preferenceHelper.setSkipPassword(true)
        }
        return res
    }

    override suspend fun deleteLocation() = readerRepo.deleteLocation()

    override suspend fun fetchRefreshInterval() = preferenceHelper.getRefreshInterval()

    override suspend fun putRefreshInterval(position: Int) = preferenceHelper.setRefreshInterval(position)

    override suspend fun insertFeedLocation(feedLocation: FeedLocation) = readerRepo.saveFeedLocation(feedLocation)

    override suspend fun insertAllFeedSources(feedSources: List<FeedSource>) = readerRepo.saveAllFeedSources(feedSources)

    override suspend fun fetchFeedSources() = readerRepo.getAllFeedSources()

    override suspend fun fetchFeedLocation() = readerRepo.getFeedLocation()

    override suspend fun insertAllRss(rssList: List<RSS>) = readerRepo.saveAllRss(rssList)

    override suspend fun doRSsCallAsync(url: String): Deferred<ResponseBody> = apiHelper.getRss(url)

    override suspend fun doFeedCallAsync(countryCode: String,
                                         source: String,
                                         since: String) = apiHelper.getFeedList(countryCode, source, since)

    override suspend fun deleteRss(rss: RSS) = readerRepo.delete(rss)

    override suspend fun insertRss(rss: RSS) = readerRepo.saveRss(rss)

    override suspend fun fetchRss(): List<RSS> = readerRepo.getAllRss()
}