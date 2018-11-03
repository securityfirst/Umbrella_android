package org.secfirst.umbrella.whitelabel.feature.reader.view.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.network.FeedItemResponse
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentController

class FeedController(bundle: Bundle) : BaseController(bundle) {

    private val feeds by lazy { args.getParcelableArray(SegmentController.EXTRA_SEGMENT) as Array<FeedItemResponse> }

    constructor(feedItemResponse: Array<FeedItemResponse>) : this(Bundle().apply {
        putParcelableArray(SegmentController.EXTRA_SEGMENT, feedItemResponse)
    })

    override fun onInject() {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.feed_view, container, false)
    }

}