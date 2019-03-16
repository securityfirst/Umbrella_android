package org.secfirst.umbrella.whitelabel.feature.segment.view

import androidx.core.content.ContextCompat
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.segment_foot.*
import org.jetbrains.anko.backgroundColor
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.misc.appContext


class SegmentFoot(private val onFootClicked: (Int) -> Unit,
                  private val onChecklistShareClick: () -> Unit,
                  private val onChecklistFavoriteClick: (Boolean) -> Unit,
                  private val checklist: Checklist) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val colours = intArrayOf(R.color.umbrella_purple,
                R.color.umbrella_green,
                R.color.umbrella_yellow)

        viewHolder.checklistShare.setOnClickListener { onChecklistShareClick() }
        viewHolder.segmentFootLayout.setOnClickListener { onFootClicked(position) }
        viewHolder.checklistFavorite.isChecked = checklist.favorite
        viewHolder.footLayout.backgroundColor = ContextCompat.getColor(appContext(), colours[position % 3])
        viewHolder.checklistLayoutFav.setOnClickListener {
            onChecklistFavoriteClick(viewHolder.checklistFavorite.isChecked)
        }
    }

    override fun getLayout() = R.layout.segment_foot
}