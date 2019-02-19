package org.secfirst.umbrella.whitelabel.feature.lesson.view

import androidx.core.content.ContextCompat
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.lesson_menu_head.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.misc.appContext

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
            bindIcon(viewHolder)
        }
    }

    private fun bindIcon(viewHolder: ViewHolder) {
        viewHolder.arrow.apply {
            if (expandableGroup.isExpanded) {
                setImageResource(R.drawable.ic_keyboard_arrow_down_black)
                viewHolder.iconHeader
                        .setColorFilter(ContextCompat.getColor(appContext(), R.color.umbrella_purple))
                viewHolder.subHeaderText
                        .setTextColor(ContextCompat.getColor(appContext(), R.color.umbrella_purple))
            } else {
                setImageResource(R.drawable.ic_keyboard_arrow_up_black)
                viewHolder.iconHeader.clearColorFilter()
                viewHolder.subHeaderText
                        .setTextColor(ContextCompat.getColor(appContext(), android.R.color.black))
            }
        }
    }

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        this.expandableGroup = onToggleListener
    }
}