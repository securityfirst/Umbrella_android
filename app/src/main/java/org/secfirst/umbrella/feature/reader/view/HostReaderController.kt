package org.secfirst.umbrella.feature.reader.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.host_reader_view.*
import kotlinx.android.synthetic.main.host_reader_view.view.*
import org.secfirst.umbrella.R
import org.secfirst.umbrella.feature.base.view.BaseController


class HostReaderController : BaseController() {

    override fun onInject() {
    }

    override fun onAttach(view: View) {
        enableNavigation(true)
        feedPager?.adapter = ReaderAdapter(this)
        feedTab?.setupWithViewPager(feedPager)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        val view = inflater.inflate(R.layout.host_reader_view, container, false)
        view.toolbar.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.title = context.getString(R.string.feed_title)
        }
        return view
    }

    override fun onDestroyView(view: View) {
        feedPager?.adapter = null
        feedTab?.setupWithViewPager(null)
        super.onDestroyView(view)
    }
}

