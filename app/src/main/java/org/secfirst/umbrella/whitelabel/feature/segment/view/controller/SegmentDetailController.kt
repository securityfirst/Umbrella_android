package org.secfirst.umbrella.whitelabel.feature.segment.view.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.tiagohm.markdownview.css.styles.Github
import kotlinx.android.synthetic.main.segment_detail.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController


class SegmentDetailController(bundle: Bundle) : BaseController(bundle) {

    private val markdown by lazy { args.getParcelable(EXTRA_SELECTED_SEGMENT_DETAIL) as Markdown }

    override fun onInject() {}

    constructor(markdown: Markdown) : this(Bundle().apply {
        putParcelable(EXTRA_SELECTED_SEGMENT_DETAIL, markdown)
    })

    companion object {
        const val EXTRA_SELECTED_SEGMENT_DETAIL = "selected_segment_detail"
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        markdownView.addStyleSheet(Github())
        markdownView.loadMarkdown(markdown.text)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.segment_detail, container, false)
    }

    fun getTitle() = markdown.title
}