package org.secfirst.umbrella.whitelabel.feature.segment.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.segment_detail.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController

class SegmentDetailController(bundle: Bundle) : BaseController(bundle) {




    override fun onInject() {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.segment_view, container, false)
    }

    private fun setUpToolbar() {
        segmentDetailToolbar?.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            mainActivity.supportActionBar?.setDisplayShowTitleEnabled(false)
        }
    }
}