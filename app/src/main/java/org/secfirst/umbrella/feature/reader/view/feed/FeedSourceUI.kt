package org.secfirst.umbrella.feature.reader.view.feed

import android.view.View
import android.widget.LinearLayout
import android.widget.ListView
import androidx.core.content.ContextCompat
import org.jetbrains.anko.*
import org.secfirst.umbrella.R
import org.secfirst.umbrella.misc.medium

class FeedSourceUI(private val sourcesAdapter: SourcesAdapter) : AnkoComponent<FeedSourceDialog> {

    private var sourceList: ListView? = null

    override fun createView(ui: AnkoContext<FeedSourceDialog>) = ui.apply {
        linearLayout {
            orientation = LinearLayout.VERTICAL
            setPadding(0, 0, 0, dip(5))

            textView(ctx.getString(R.string.feed_source_name)) {
                typeface = medium
                textColor = 0xde000000.toInt()
                textSize = 20f

            }.lparams(width = wrapContent, height = wrapContent) {
                leftMargin = dip(28)
                topMargin = dip(20)
            }
            linearLayout {
                orientation = LinearLayout.VERTICAL
                verticalLayout {
                    sourceList = listView {
                        //assign adapter
                        divider = null
                        adapter = sourcesAdapter
                    }
                }.lparams {
                    margin = dip(5)
                }
                checkBox("Select All") {
                    typeface = medium
                    textColor = ContextCompat.getColor(ctx, R.color.feedSources_color)
                    textSize = 16f
                    setOnClickListener { sourcesAdapter.selectAllSources(isChecked) }
                }.lparams(width = wrapContent, height = wrapContent) {
                    topMargin = dip(10)
                }
                include<View>(R.layout.alert_control) {
                }.lparams(width = wrapContent, height = wrapContent) {
                    topMargin = dip(40)
                    leftMargin = dip(140)
                }
            }.lparams(width = wrapContent, height = wrapContent) {

            }.lparams(width = wrapContent, height = wrapContent) {
                leftMargin = dip(20)
                topMargin = dip(20)
            }
        }
    }.view

    fun getFeedSourcesUpdated() = sourcesAdapter.feedSources
}