package org.secfirst.umbrella.whitelabel.feature.reader.view.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.feed_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.network.FeedItemResponse
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentController
import org.secfirst.umbrella.whitelabel.misc.initRecyclerView

class FeedController(bundle: Bundle) : BaseController(bundle) {

    private val feeds by lazy { args.getParcelableArray(SegmentController.EXTRA_SEGMENT) as Array<FeedItemResponse> }
    private val onClickOpenArticle: (String?) -> Unit = this::onClickFeedItem

    constructor(feedItemResponse: Array<FeedItemResponse>) : this(Bundle().apply {
        putParcelableArray(SegmentController.EXTRA_SEGMENT, feedItemResponse)
    })

    override fun onInject() {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.feed_view, container, false)
    }

    private fun onClickFeedItem(url: String?) {

    }

    override fun onAttach(view: View) {
        val adapter = FeedAdapter(onClickOpenArticle)
        feedItemRecyclerView?.initRecyclerView(adapter)
        adapter.addAll(feeds.toList())
    }

}