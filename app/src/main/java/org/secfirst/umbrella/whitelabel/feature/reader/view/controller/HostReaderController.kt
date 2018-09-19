package org.secfirst.umbrella.whitelabel.feature.reader.view.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.host_reader_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.reader.view.adapter.ReaderAdapter


class HostReaderController : BaseController() {

    override fun onInject() {
    }

    override fun onAttach(view: View) {
        enableNavigation()
        feedPager?.adapter = ReaderAdapter(this)
        feedTab?.setupWithViewPager(feedPager)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.host_reader_view, container, false)
    }

    override fun onDestroyView(view: View) {
        feedPager?.adapter = null
        feedTab?.setupWithViewPager(null)

        super.onDestroyView(view)
    }

    override fun getEnableBackAction() = false

    override fun getToolbarTitle() = ""
}

