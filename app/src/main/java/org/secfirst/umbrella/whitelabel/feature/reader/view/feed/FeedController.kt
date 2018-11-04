package org.secfirst.umbrella.whitelabel.feature.reader.view.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.feed_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.component.WebViewController
import org.secfirst.umbrella.whitelabel.data.network.FeedItemResponse
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.misc.initRecyclerView

class FeedController(bundle: Bundle) : BaseController(bundle) {

    private val feeds by lazy { args.getParcelableArray(EXTRA_FEED_LIST) as Array<FeedItemResponse> }
    private val onClickOpenArticle: (String) -> Unit = this::onClickFeedItem
    private val onClickLocation: () -> Unit = this::onClickChangeLocation
    private val placeName by lazy { args.getString(EXTRA_FEED_PLACE) }

    constructor(feedItemResponse: Array<FeedItemResponse>, placeName: String) : this(Bundle().apply {
        putParcelableArray(EXTRA_FEED_LIST, feedItemResponse)
        putString(EXTRA_FEED_PLACE, placeName)
    })

    override fun onInject() {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.feed_view, container, false)
    }

    private fun onClickFeedItem(url: String) {
        parentController?.router?.pushController(RouterTransaction.with(WebViewController(url)))
    }

    private fun onClickChangeLocation() {
        router.popCurrentController()
    }

    override fun onAttach(view: View) {
        if (feeds.isNotEmpty()) {
            val adapter = FeedAdapter(onClickOpenArticle, onClickLocation, placeName)
            feedItemRecyclerView?.initRecyclerView(adapter)
            adapter.addAll(feeds.toList())
        } else {
            router.popCurrentController()
            router.pushController(RouterTransaction.with(FeedEmptyController(placeName)))
        }
    }

    companion object {
        const val EXTRA_FEED_LIST = "extra_feed_list"
        const val EXTRA_FEED_PLACE = "extra_feed_place"
    }
}