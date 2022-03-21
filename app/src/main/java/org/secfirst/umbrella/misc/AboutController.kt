package org.secfirst.umbrella.misc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.tiagohm.markdownview.css.styles.Github
import kotlinx.android.synthetic.main.about.*
import org.secfirst.umbrella.R
import org.secfirst.umbrella.data.database.segment.Markdown
import org.secfirst.umbrella.feature.base.view.BaseController

class AboutController(bundle: Bundle) : BaseController(bundle) {

    private val markdown by lazy { args.getParcelable(EXTRA_ABOUT) as Markdown? }

    constructor(markdown: Markdown) : this(Bundle().apply {
        putParcelable(EXTRA_ABOUT, markdown)
    })

    companion object {
        private const val EXTRA_ABOUT = "extra about"
    }

    override fun onInject() {

    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        aboutMarkdownView.addStyleSheet(Github())
        aboutMarkdownView.loadMarkdown(markdown?.text)
        setUpToolbar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        return inflater.inflate(R.layout.about, container, false)
    }

    private fun setUpToolbar() {
        aboutToolbar?.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            mainActivity.supportActionBar?.title = markdown?.title
        }
    }
}