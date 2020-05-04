package org.secfirst.umbrella.feature.reader.view.feed

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import org.jetbrains.anko.*
import org.secfirst.umbrella.R
import org.secfirst.umbrella.data.database.reader.FeedSource
import org.secfirst.umbrella.misc.medium

class SourcesAdapter(val feedSources: List<FeedSource>) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return with(parent!!.context) {

            linearLayout {
                orientation = LinearLayout.VERTICAL
                checkBox(feedSources[position].name) {
                    typeface = medium
                    textColor = ContextCompat.getColor(this@with, R.color.feedSources_color)
                    textSize = 16f
                    isChecked = feedSources[position].lastChecked
                    setOnClickListener { updateChecked(feedSources[position].name, isChecked) }
                }.lparams(width = wrapContent, height = wrapContent) {
                    topMargin = dip(10)
                }

            }
        }
    }

    override fun getItem(position: Int) = feedSources[position]

    override fun getItemId(position: Int): Long = 0L

    override fun getCount(): Int = feedSources.size

    private fun updateChecked(feedSourceName: String, lastChecked: Boolean) {
        feedSources.forEach {
            if (it.name == feedSourceName)
                it.lastChecked = lastChecked
        }
    }

    fun selectAllSources(selectAll: Boolean) {
        feedSources.forEach { it.lastChecked = selectAll }
        notifyDataSetChanged()
    }

}