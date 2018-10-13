package org.secfirst.umbrella.whitelabel.feature.tour.presenter

import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.tour.interactor.TourBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.tour.view.TourView
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import javax.inject.Inject


class TourPresenterImp<V : TourView, I : TourBaseInteractor>
@Inject internal constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), TourBasePresenter<V, I> {

    override fun manageContent() {
        var isFetchData: Boolean
        launchSilent(uiContext) {
            interactor?.let {
                getView()?.downloadContentInProgress()
                isFetchData = it.fetchData()

                if (isFetchData) {
                    val root = it.initParser()
                    it.persist(root)
                }
                it.persistFeedSource(createFeedSources())
                getView()?.downloadContentCompleted(isFetchData)
            }
        }
    }

    private fun createFeedSources(): List<FeedSource> {
        val feedSource1 = FeedSource(0, "UN/ ReliefWeb", false)
        val feedSource2 = FeedSource(1, "CDC", false)
        val feedSource3 = FeedSource(2, "Global Disaster Alert \\nCoordination System", false)
        val feedSource4 = FeedSource(3, "US State Department Country \\nWarnings", false)
        val feedSources = mutableListOf<FeedSource>()
        feedSources.add(feedSource1)
        feedSources.add(feedSource2)
        feedSources.add(feedSource3)
        feedSources.add(feedSource4)
        return feedSources
    }
}