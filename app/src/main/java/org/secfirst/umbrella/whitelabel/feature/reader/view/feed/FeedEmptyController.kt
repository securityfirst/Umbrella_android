package org.secfirst.umbrella.whitelabel.feature.reader.view.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.feed_empty_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController

class FeedEmptyController(bundle: Bundle) : BaseController(bundle) {

    private val placeName by lazy { args.getString(FeedController.EXTRA_FEED_PLACE) }

    override fun onInject() {}

    constructor(placeName: String) : this(Bundle().apply {
        putString(FeedController.EXTRA_FEED_PLACE, placeName)
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.feed_empty_view, container, false)
    }

    override fun onAttach(view: View) {
        feedEmptyTitle?.let { it.text = placeName }
        feedEmptyChangeLocation?.setOnClickListener { backToFeedSettings() }
    }

    private fun backToFeedSettings() {
        router.popCurrentController()
    }
}