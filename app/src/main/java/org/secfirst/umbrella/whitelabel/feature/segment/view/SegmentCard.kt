package org.secfirst.umbrella.whitelabel.feature.segment.view

import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.flexbox.FlexboxLayoutManager
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.segment_item.*
import org.jetbrains.anko.backgroundColor
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.misc.appContext


open class SegmentCard(private val onClickSegment: (Int) -> Unit,
                       private val onSegmentShareClick: (Markdown) -> Unit,
                       private val onSegmentFavoriteClick: (Markdown) -> Unit,
                       private val markdown: Markdown?) : Item() {

    private lateinit var viewHolder: ViewHolder

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val colours = intArrayOf(R.color.umbrella_purple,
                R.color.umbrella_green,
                R.color.umbrella_yellow)
        this.viewHolder = viewHolder
        markdown?.let { safeMarkdown ->

            viewHolder.segmentFavorite.isChecked = safeMarkdown.favorite
            viewHolder.segmentFavorite.setOnClickListener {
                markdown.favorite = viewHolder.segmentFavorite.isChecked
                onSegmentFavoriteClick(safeMarkdown)
            }
            viewHolder.segmentLayout.setOnClickListener { onClickSegment(position) }
            viewHolder.segmentShare.setOnClickListener { onSegmentShareClick(safeMarkdown) }

            with(markdown) {
                val index = position + 1
                viewHolder.segmentIndex.text = index.toString()
                viewHolder.segmentDescription.text = title
                viewHolder.segmentLayout.backgroundColor = ContextCompat.getColor(appContext(), colours[position % 3])

            }

        }
        val lp = viewHolder.segmentCardItemView.layoutParams
        if (lp is FlexboxLayoutManager.LayoutParams)
            lp.flexGrow = 1f

        if (markdown == null)
            viewHolder.segmentCardItemView.visibility = View.INVISIBLE
    }

    override fun getLayout() = R.layout.segment_item

}