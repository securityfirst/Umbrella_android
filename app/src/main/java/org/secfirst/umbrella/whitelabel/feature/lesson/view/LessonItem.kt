package org.secfirst.umbrella.whitelabel.feature.lesson.view

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.lesson_menu_item.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject

class LessonItem(private val subject: Subject, private val onclickLesson: (Subject) -> Unit) : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.categoryName.text = subject.title
        viewHolder.lessonItemLayout.setOnClickListener { onclickLesson(subject) }
    }

    override fun getLayout() = R.layout.lesson_menu_item
}