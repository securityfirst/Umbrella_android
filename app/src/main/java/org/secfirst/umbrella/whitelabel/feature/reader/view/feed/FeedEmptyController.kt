package org.secfirst.umbrella.whitelabel.feature.reader.view.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.feed_empty_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.reader.DaggerReanderComponent
import org.secfirst.umbrella.whitelabel.feature.reader.interactor.ReaderBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.reader.presenter.ReaderBasePresenter
import org.secfirst.umbrella.whitelabel.feature.reader.view.ReaderView
import javax.inject.Inject

class FeedEmptyController(bundle: Bundle) : BaseController(bundle), ReaderView {

    @Inject
    internal lateinit var presenter: ReaderBasePresenter<ReaderView, ReaderBaseInteractor>
    private val placeName by lazy { args.getString(FeedController.EXTRA_FEED_PLACE) }

    override fun onInject() {
        DaggerReanderComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    constructor(placeName: String) : this(Bundle().apply {
        putString(FeedController.EXTRA_FEED_PLACE, placeName)
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        presenter.onAttach(this)
        return inflater.inflate(R.layout.feed_empty_view, container, false)
    }

    override fun onAttach(view: View) {
        feedEmptyTitle?.let { it.text = placeName }
        feedEmptyChangeLocation?.setOnClickListener { backToFeedSettings() }
    }

    private fun backToFeedSettings() {
        presenter.submitDeleteFeedLocation()
        router.popCurrentController()
    }
}