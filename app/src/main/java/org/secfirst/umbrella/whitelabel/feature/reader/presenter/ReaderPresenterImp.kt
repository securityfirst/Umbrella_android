package org.secfirst.umbrella.whitelabel.feature.reader.presenter

import android.util.Log
import com.einmalfel.earl.EarlParser
import com.google.gson.Gson
import getAssetFileBy
import org.secfirst.umbrella.whitelabel.data.database.reader.RSS
import org.secfirst.umbrella.whitelabel.data.database.reader.RSS_FILE_NAME
import org.secfirst.umbrella.whitelabel.data.database.reader.RefRSS
import org.secfirst.umbrella.whitelabel.data.database.reader.convertToRSS
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.reader.interactor.ReaderBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.reader.view.ReaderView
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import javax.inject.Inject


class ReaderPresenterImp<V : ReaderView, I : ReaderBaseInteractor>
@Inject internal constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), ReaderBasePresenter<V, I> {


    private val tag: String = ReaderPresenterImp::class.java.name

    override fun submitDeleteRss(rss: RSS) {
        launchSilent(uiContext) {
            interactor?.deleteRss(rss)
        }
    }


    override fun submitFetchRss() {
        launchSilent(uiContext) {
            var refRss: RefRSS
            interactor?.let {
                val databaseRss = it.fetchRss()
                if (databaseRss.isEmpty()) {
                    val assetsRss = mutableListOf<RSS>()
                    refRss = getRssFromAssert()
                    refRss.items.forEach { item -> assetsRss.add(RSS(item.link)) }
                    submitRss(assetsRss)
                    getView()?.showAllRss(processRss(assetsRss))
                }
                getView()?.showAllRss(processRss(databaseRss))
            }
        }
    }

    private fun submitRss(rssList: MutableList<RSS>) {
        launchSilent(uiContext) {
            interactor?.insertAllRss(rssList)
        }
    }

    private suspend fun processRss(rssList: List<RSS>): List<RSS> {
        val rssResult = mutableListOf<RSS>()
        interactor?.let {
            rssList.forEach { rssIt ->
                try {
                    val responseBody = it.doRSsCall(rssIt.url_).await()
                    val feed = EarlParser.parseOrThrow(responseBody.byteStream(), 0)
                    rssResult.add(feed.convertToRSS)
                } catch (exception: Exception) {
                    Log.e(tag, "could't load the RSS:, ${rssIt.link}")
                }
            }
        }
        return rssResult
    }

    private suspend fun processRss(url: String): RSS? {
        interactor?.let {
            try {
                val responseBody = it.doRSsCall(url).await()
                val feed = EarlParser.parseOrThrow(responseBody.byteStream(), 0)
                return feed.convertToRSS
            } catch (exception: Exception) {
                Log.e(tag, "could't load the RSS:, $url")
            }
        }
        return null
    }

    override fun submitInsertRss(rss: RSS) {
        launchSilent(uiContext) {
            interactor?.let {
                it.insertRss(rss)
                processRss(rss.link)?.let { rss -> getView()?.showNewestRss(rss) }
            }
        }
    }

    private fun getRssFromAssert(): RefRSS {
        val input = getAssetFileBy(RSS_FILE_NAME)
        val jsonInString = input.bufferedReader().use { it.readText() }
        return Gson().fromJson(jsonInString, RefRSS::class.java)
    }
}