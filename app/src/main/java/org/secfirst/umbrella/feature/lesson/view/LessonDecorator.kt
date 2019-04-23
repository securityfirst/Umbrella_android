package org.secfirst.umbrella.feature.lesson.view

import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import org.secfirst.umbrella.R

class LessonDecorator : Item() {

    override fun bind(viewHolder: ViewHolder, position: Int) {}
    override fun getLayout() = R.layout.lesson_decorator_line
}