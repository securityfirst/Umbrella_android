package org.secfirst.umbrella.whitelabel.component

import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.LinearLayout
import org.jetbrains.anko.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.reader.FeedSource
import org.secfirst.umbrella.whitelabel.misc.medium

class FeedSourceUI(private val feedSources: List<FeedSource>) : AnkoComponent<FeedSourceDialog> {

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
                topMargin = dip(24)
            }
            linearLayout {
                orientation = LinearLayout.VERTICAL
                feedSources.forEach { feedSource ->
                    checkBox(feedSource.name) {
                        typeface = medium
                        textColor = ContextCompat.getColor(ctx, R.color.feedSources_color)
                        textSize = 16f
                        isChecked = feedSource.lastChecked
                        setOnClickListener { updateChecked(feedSource.name, isChecked) }
                    }.lparams(width = wrapContent, height = wrapContent) {
                        topMargin = dip(10)
                    }
                }
                include<View>(R.layout.alert_control) {
                }.lparams(width = wrapContent, height = wrapContent) {
                    rightMargin = dip(10)
                }
            }.lparams(width = wrapContent, height = wrapContent) {

            }.lparams(width = wrapContent, height = wrapContent) {
                leftMargin = dip(20)
                topMargin = dip(20)
            }
        }
    }.view

    private fun updateChecked(feedSourceName: String, lastChecked: Boolean) {
        feedSources.forEach {
            if (it.name == feedSourceName)
                it.lastChecked = lastChecked
        }
    }

    fun getFeedSourcesUpdated() = feedSources
}