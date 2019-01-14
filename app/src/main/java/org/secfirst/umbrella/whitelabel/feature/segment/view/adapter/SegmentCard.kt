package org.secfirst.umbrella.whitelabel.feature.segment.view.adapter

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.segment_item.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown


open class SegmentCard(private val onClickSegment: (Int) -> Unit,
                       private val onSegmentShareClick: (Markdown) -> Unit,
                       private val onSegmentFavoriteClick: (Markdown) -> Unit,
                       private val markdown: Markdown) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val colours = intArrayOf(R.color.umbrella_purple,
                R.color.umbrella_green,
                R.color.umbrella_yellow)

        viewHolder.segmentFavorite.isChecked = markdown.favorite
        viewHolder.segmentFavorite.setOnClickListener {
            markdown.favorite = viewHolder.segmentFavorite.isChecked
            onSegmentFavoriteClick(markdown)
        }
        viewHolder.segmentLayout.setOnClickListener { onClickSegment(position) }
        viewHolder.segmentShare.setOnClickListener { onSegmentShareClick(markdown) }

        with(markdown) {
            val index = position + 1
            viewHolder.segmentIndex.text = index.toString()
            viewHolder.segmentDescription.text = title
            //viewHolder.segmentLayout.backgroundColor = ContextCompat.getColor(appContext(), colours[position % 3])
        }
    }

    override fun getLayout() = R.layout.segment_item
}