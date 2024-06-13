package org.secfirst.umbrella.feature.lesson.view

import androidx.core.content.ContextCompat
import com.xwray.groupie.ExpandableGroup
import com.xwray.groupie.ExpandableItem
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.lesson_menu_head.*
import org.secfirst.umbrella.R
import org.secfirst.umbrella.data.database.lesson.Lesson
import org.secfirst.umbrella.misc.appContext

class LessonGroup(private val lesson: Lesson,
                  private val hasChild: Boolean,
                  private val onclickGroup: (Lesson) -> Unit) : LessonHeader(lesson.moduleId, lesson.pathIcon, lesson.moduleTitle), ExpandableItem {

    private lateinit var expandableGroup: ExpandableGroup

    override fun bind(viewHolder: ViewHolder, position: Int) {
        super.bind(viewHolder, position)
        viewHolder.lessonHeaderLayout.setOnClickListener {
            onclickGroup(lesson)
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
                if (hasChild) {
                    viewHolder.iconHeader
                            .setColorFilter(ContextCompat.getColor(appContext(), R.color.umbrella_purple))
                    viewHolder.subHeaderText
                            .setTextColor(ContextCompat.getColor(appContext(), R.color.umbrella_purple))
                }
            } else {
                setImageResource(R.drawable.ic_keyboard_arrow_up_black)
                viewHolder.iconHeader.clearColorFilter()

            }
        }
    }

    override fun setExpandableGroup(onToggleListener: ExpandableGroup) {
        this.expandableGroup = onToggleListener
    }
}