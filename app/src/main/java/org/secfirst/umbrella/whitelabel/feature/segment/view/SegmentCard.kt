package org.secfirst.umbrella.whitelabel.feature.segment.view

import android.util.DisplayMetrics
import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.segment_item.*
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.windowManager
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.misc.SMALL_DEVICE_HEIGHT
import org.secfirst.umbrella.whitelabel.misc.SMALL_DEVICE_WIDTH
import org.secfirst.umbrella.whitelabel.misc.appContext


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
            viewHolder.segmentLayout.backgroundColor = ContextCompat.getColor(appContext(), colours[position % 3])
        }
        setPaddingForSmallDevices(viewHolder)
    }

    private fun setPaddingForSmallDevices(viewHolder: ViewHolder) {
        val displayMetrics = DisplayMetrics()
        appContext().windowManager.defaultDisplay.getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels
        if (height <= SMALL_DEVICE_HEIGHT && width <= SMALL_DEVICE_WIDTH)
            viewHolder.checklistCardItemView.layoutParams.width =
                    appContext().resources.getDimension(org.secfirst.umbrella.whitelabel.R.dimen.segment_item_small_width).toInt()

    }

    override fun getLayout() = R.layout.segment_item
}