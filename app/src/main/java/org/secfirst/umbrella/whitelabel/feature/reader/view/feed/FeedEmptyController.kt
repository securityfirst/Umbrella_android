package org.secfirst.umbrella.whitelabel.feature.reader.view.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.feed_empty_view.*
import kotlinx.android.synthetic.main.feed_empty_view.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedLocation
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.reader.DaggerReanderComponent
import org.secfirst.umbrella.whitelabel.feature.reader.interactor.ReaderBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.reader.presenter.ReaderBasePresenter
import org.secfirst.umbrella.whitelabel.feature.reader.view.ReaderView
import javax.inject.Inject

class FeedEmptyController : BaseController(), ReaderView {

    @Inject
    internal lateinit var presenter: ReaderBasePresenter<ReaderView, ReaderBaseInteractor>

    override fun onInject() {
        DaggerReanderComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.feed_empty_view, container, false)
        presenter.onAttach(this)
        presenter.prepareView()
        view.feedEmptyChangeLocation.setOnClickListener { backToFeedSettings() }
        return view
    }

    override fun prepareView(feedSources: List<FeedSource>, refreshIntervalPosition: Int, feedLocation: FeedLocation) {
        feedEmptyTitle?.let { it.text = feedLocation.location }
    }

    private fun backToFeedSettings() {
        presenter.submitDeleteFeedLocation()
        router.popCurrentController()
    }
}