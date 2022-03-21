package org.secfirst.umbrella.feature.segment.view.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.tiagohm.markdownview.css.styles.Github
import kotlinx.android.synthetic.main.segment_detail.view.*
import org.secfirst.umbrella.R
import org.secfirst.umbrella.data.database.segment.Markdown
import org.secfirst.umbrella.feature.base.view.BaseController
import org.secfirst.umbrella.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.misc.launchSilent


class SegmentDetailController(bundle: Bundle) : BaseController(bundle) {

    private val markdown by lazy { args.getParcelable(EXTRA_SELECTED_SEGMENT_DETAIL) as Markdown? }

    override fun onInject() {}

    constructor(markdown: Markdown) : this(Bundle().apply {
        putParcelable(EXTRA_SELECTED_SEGMENT_DETAIL, markdown)
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        val view = inflater.inflate(R.layout.segment_detail, container, false)
        loadMarkdown(view)
        return view
    }

    private fun loadMarkdown(view: View) {
        val css = Github()
        css.addRule("body", "line-height: 1.6", "padding: 0px")
        view.markdownView.addStyleSheet(css)
        launchSilent(uiContext) { view.markdownView.loadMarkdown(markdown?.text) }
    }

    fun getTitle() = markdown?.title

    companion object {
        const val EXTRA_SELECTED_SEGMENT_DETAIL = "selected_segment_detail"
    }


}
