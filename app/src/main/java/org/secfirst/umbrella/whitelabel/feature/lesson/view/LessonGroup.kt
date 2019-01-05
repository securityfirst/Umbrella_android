package org.secfirst.umbrella.whitelabel.feature.lesson.view

import android.graphics.drawable.Animatable
import android.view.View
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.lesson_menu_head.*
import org.secfirst.umbrella.whitelabel.R

class LessonGroup(private val moduleId: String,
                  iconPath: String,
                  titleHeader: String,
                  private val onclickGroup: (String) -> Unit) : LessonHeader(moduleId, iconPath, titleHeader), ExpandableItem {

    private lateinit var expandableGroup: ExpandableGroup

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.lessonHeaderLayout.setOnClickListener {
            onclickGroup(moduleId)
            expandableGroup.onToggleExpanded()
            bindIcon(viewHolder)
        }
        viewHolder.arrow.apply {
            visibility = View.VISIBLE
            setImageResource(if (expandableGroup.isExpanded) R.drawable.collapse else R.drawable.expand)
        }
    }

    private fun bindIcon(viewHolder: ViewHolder) {
        viewHolder.arrow.apply {
            visibility = View.VISIBLE
            setImageResource(if (expandableGroup.isExpanded) R.drawable.collapse_animated else R.drawable.expand_animated)
            (drawable as Animatable).start()
        }
    }

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        this.expandableGroup = onToggleListener
    }
}