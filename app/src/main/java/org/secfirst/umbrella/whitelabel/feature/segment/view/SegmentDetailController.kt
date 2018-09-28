package org.secfirst.umbrella.whitelabel.feature.segment.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.segment_detail.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.content.Markdown
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController

class SegmentDetailController(bundle: Bundle) : BaseController(bundle) {

    private val markdown by lazy { args.getParcelable(EXTRA_SELECTED_SEGMENT_DETAIL) as Markdown }

    constructor(markdown: Markdown) : this(Bundle().apply {
        putParcelable(EXTRA_SELECTED_SEGMENT_DETAIL, markdown)
    })

    companion object {
        const val EXTRA_SELECTED_SEGMENT_DETAIL = "selected_segment_detail"
    }

    override fun onInject() {

    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        markdownView?.loadMarkdown(markdown.text)
        setUpToolbar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.segment_detail, container, false)
    }

    private fun setUpToolbar() {
        segmentDetailToolbar?.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            mainActivity.supportActionBar?.title = markdown.title
        }
    }
}