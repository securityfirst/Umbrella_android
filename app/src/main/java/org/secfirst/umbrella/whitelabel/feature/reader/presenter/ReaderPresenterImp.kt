package org.secfirst.umbrella.whitelabel.feature.reader.presenter

import android.util.Log
import com.einmalfel.earl.EarlParser
import com.google.gson.Gson
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedLocation
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.data.database.reader.RSS
import org.secfirst.umbrella.whitelabel.data.database.reader.updateRSS
import org.secfirst.umbrella.whitelabel.data.network.FeedItemResponse
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

    override fun setSkipPassword(value: Boolean) {
        interactor?.setSkipPassword(value)
    }

    override fun submitChangeDatabaseAccess(userToken: String) {
        launchSilent(uiContext) {
            val res = interactor?.applyChangeDatabaseAccess(userToken) ?: false
            getView()?.isChangedToken(res)
        }
    }

    override fun isSkipPassword() {
        val res = interactor?.isSkippPassword() ?: false
        getView()?.isSkipPassword(res)
    }

    private val tag: String = ReaderPresenterImp::class.java.name

    override fun prepareView() {
        launchSilent(uiContext) {
            interactor?.let {
                val feedSources = it.fetchFeedSources()
                val refreshIntervalPosition = it.fetchRefreshInterval()
                val feedLocation = it.fetchFeedLocation() ?: FeedLocation()
                getView()?.prepareView(feedSources, refreshIntervalPosition, feedLocation)
            }
        }
    }

    override fun submitFeedRequest(feedLocation: FeedLocation,
                                   feedSources: List<FeedSource>,
                                   isFirstRequest: Boolean) {
        interactor?.let {
            launchSilent(uiContext) {
                try {
                    val feedResponseBody = it.doFeedCallAsync(feedLocation.iso2,
                            getSelectedFeedSources(feedSources), "0").await()
                    val feedItemResponse = Gson()
                            .fromJson(feedResponseBody.string(), Array<FeedItemResponse>::class.java)
                    getView()?.startFeedController(feedItemResponse, isFirstRequest)
                } catch (exception: Exception) {
                    getView()?.feedError()
                    Log.e("test", "Error when try to fetch feed.")
                }
            }
        }
    }

    override fun submitFeedLocation(feedLocation: FeedLocation) {
        interactor?.let {
            launchSilent(uiContext) { it.insertFeedLocation(feedLocation) }
        }
    }

    override fun submitDeleteRss(rss: RSS) {
        launchSilent(uiContext) {
            interactor?.deleteRss(rss)
        }
    }

    override fun submitDeleteFeedLocation() {
        launchSilent(uiContext) {
            interactor?.deleteLocation()
        }
    }

    override fun submitFetchRss() {
        launchSilent(uiContext) {
            var rssList = listOf<RSS>()
            interactor?.let { rssList = it.fetchRss() }
            getView()?.showAllRss(processRss(rssList))
        }
    }

    override fun submitInsertRss(rss: RSS) {
        launchSilent(uiContext) {
            interactor?.let {
                it.insertRss(rss)
                processRss(rss)?.let { rss -> getView()?.showNewestRss(rss) }
            }
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

    override fun submitPutRefreshInterval(position: Int) {
        launchSilent(uiContext) {
            interactor?.putRefreshInterval(position)
        }
    }

    private suspend fun processRss(rssList: List<RSS>): List<RSS> {
        val rssResult = mutableListOf<RSS>()
        interactor?.let {
            rssList.forEach { rssIt ->
                try {
                    val responseBody = it.doRSsCallAsync(rssIt.url_).await()
                    val feed = EarlParser.parseOrThrow(responseBody.byteStream(), 0)
                    rssResult.add(feed.updateRSS(rssIt))
                } catch (exception: Exception) {
                    Log.e(tag, "could't load the RSS:, ${rssIt.link}")
                }
            }
        }
        return rssResult
    }

    private suspend fun processRss(rss: RSS): RSS? {
        interactor?.let {
            try {
                val responseBody = it.doRSsCallAsync(rss.url_).await()
                val feed = EarlParser.parseOrThrow(responseBody.byteStream(), 0)
                return feed.updateRSS(rss)
            } catch (exception: Exception) {
                Log.e(tag, "could't load the RSS")
            }
        }
        return null
    }

    private fun getSelectedFeedSources(feedSources: List<FeedSource>): String {
        val selectedSources = feedSources.filter { it.lastChecked }
        val codeSources = mutableListOf<Int>()
        selectedSources.forEach { codeSources.add(it.code) }
        return codeSources.joinToString(",")
    }
}