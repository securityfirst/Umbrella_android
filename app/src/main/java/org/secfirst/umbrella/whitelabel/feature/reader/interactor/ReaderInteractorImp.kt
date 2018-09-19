package org.secfirst.umbrella.whitelabel.feature.reader.interactor

import kotlinx.coroutines.experimental.Deferred
import okhttp3.ResponseBody
import org.secfirst.umbrella.whitelabel.data.database.reader.RSS
import org.secfirst.umbrella.whitelabel.data.database.reader.RssRepo
import org.secfirst.umbrella.whitelabel.data.network.ApiHelper
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractorImp
import javax.inject.Inject

class ReaderInteractorImp @Inject constructor(apiHelper: ApiHelper, private val rssRepo: RssRepo)
    : BaseInteractorImp(apiHelper), ReaderBaseInteractor {

    override suspend fun insertAllRss(rssList: List<RSS>) = rssRepo.saveAllRss(rssList)

    override suspend fun doRSsCall(url: String): Deferred<ResponseBody> = apiHelper.getRss(url)

    override suspend fun deleteRss(rss: RSS) = rssRepo.delete(rss)

    override suspend fun insertRss(rss: RSS) = rssRepo.saveRss(rss)

    override suspend fun fetchRss(): List<RSS> = rssRepo.getAllRss()
}