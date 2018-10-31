package org.secfirst.umbrella.whitelabel.feature.reader.presenter

import android.location.Geocoder
import android.util.Log
import com.einmalfel.earl.EarlParser
import com.google.gson.Gson
import getAssetFileBy
import kotlinx.coroutines.experimental.withContext
import org.secfirst.umbrella.whitelabel.data.database.reader.*
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.reader.interactor.ReaderBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.reader.view.ReaderView
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.ioContext
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import java.util.*
import javax.inject.Inject


class ReaderPresenterImp<V : ReaderView, I : ReaderBaseInteractor>
@Inject internal constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), ReaderBasePresenter<V, I> {

    private val tag: String = ReaderPresenterImp::class.java.name

    override fun submitAutocompleteAddress(locationName: String) {
        launchSilent(uiContext) {
            interactor?.let {
                val res = it.fetchGeolocation(locationName)
                getView()?.newAddressAvailable(res)
            }
        }
    }

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

    override fun submitLoadFeedSources() {
        launchSilent(uiContext) {
            val feedSources = interactor?.fetchFeedSources()
            if (feedSources != null)
                getView()?.prepareFeedSource(feedSources)
        }
    }

    override fun submitInsertFeedSource(feedSources: List<FeedSource>) {
        launchSilent(uiContext) {
            interactor?.insertAllFeedSources(feedSources)
        }
    }

    override fun submitInsertFeedLocation(feedLocation: FeedLocation) {
        launchSilent(uiContext) {
            interactor?.insertFeedLocation(feedLocation)
        }
    }


    override fun submitLoadFeedLocation() {
        launchSilent(uiContext) {
            val feedLocation = interactor?.fetchFeedLocation()
            if (feedLocation != null)
                getView()?.prepareFeedLocation(feedLocation)
        }
    }

    override fun submitLoadRefreshInterval() {
        launchSilent(uiContext) {
            val position = interactor?.fetchRefreshInterval()
            if (position != null)
                getView()?.prepareRefreshInterval(position)
        }
    }

    override fun submitPutRefreshInterval(position: Int) {
        launchSilent(uiContext) {
            interactor?.putRefreshInterval(position)
        }
    }
}