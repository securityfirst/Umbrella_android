package org.secfirst.umbrella.feature.reader.view.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.feed_view.*
import kotlinx.android.synthetic.main.feed_view.view.*
import org.secfirst.umbrella.R
import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.component.WebViewController
import org.secfirst.umbrella.data.database.reader.FeedLocation
import org.secfirst.umbrella.data.database.reader.FeedSource
import org.secfirst.umbrella.data.network.FeedItemResponse
import org.secfirst.umbrella.feature.base.view.BaseController
import org.secfirst.umbrella.feature.reader.DaggerReanderComponent
import org.secfirst.umbrella.feature.reader.interactor.ReaderBaseInteractor
import org.secfirst.umbrella.feature.reader.presenter.ReaderBasePresenter
import org.secfirst.umbrella.feature.reader.view.ReaderView
import javax.inject.Inject

class FeedController(bundle: Bundle) : BaseController(bundle), ReaderView {

    @Inject
    internal lateinit var presenter: ReaderBasePresenter<ReaderView, ReaderBaseInteractor>
    private val feeds by lazy { args.getParcelableArray(EXTRA_FEED_LIST) as Array<FeedItemResponse> }
    private val onClickOpenArticle: (String) -> Unit = this::onClickFeedItem
    private val onClickLocation: () -> Unit = this::onClickChangeLocation

    constructor(feedItemResponse: Array<FeedItemResponse>) : this(Bundle().apply {
        putParcelableArray(EXTRA_FEED_LIST, feedItemResponse)
    })

    override fun onInject() {
        DaggerReanderComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.feed_view, container, false)
        view.feedItemRecyclerView.layoutManager = LinearLayoutManager(context)
        presenter.onAttach(this)
        if (feeds.isEmpty()) {
            router.popCurrentController()
            router.pushController(RouterTransaction.with(FeedEmptyController()))
        } else
            presenter.prepareView()
        return view
    }

    override fun prepareView(feedSources: List<FeedSource>, refreshInterval: String, feedLocation: FeedLocation) {
        val adapter = FeedAdapter(onClickOpenArticle, onClickLocation, feedLocation.location)
        feedItemRecyclerView?.adapter = adapter
        adapter.addAll(feeds.toList())
    }

    private fun onClickFeedItem(url: String) {
        parentController?.router?.pushController(RouterTransaction.with(WebViewController(url)))
    }

    private fun onClickChangeLocation() {
        presenter.submitDeleteFeedLocation()
        router.popCurrentController()
    }

    companion object {
        const val EXTRA_FEED_LIST = "extra_feed_list"
    }
}