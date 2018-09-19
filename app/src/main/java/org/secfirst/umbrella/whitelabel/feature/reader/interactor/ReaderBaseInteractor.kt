package org.secfirst.umbrella.whitelabel.feature.reader.interactor


import kotlinx.coroutines.experimental.Deferred
import okhttp3.ResponseBody
import org.secfirst.umbrella.whitelabel.data.database.reader.RSS
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor

interface ReaderBaseInteractor : BaseInteractor {

    suspend fun insertRss(rss: RSS)

    suspend fun insertAllRss(rssList: List<RSS>)

    suspend fun fetchRss(): List<RSS>

    suspend fun deleteRss(rss: RSS): Boolean

    suspend fun doRSsCall(url: String): Deferred<ResponseBody>

}