package org.secfirst.umbrella.whitelabel.feature.lesson.view

import org.secfirst.umbrella.whitelabel.data.database.lesson.Difficult
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView
import org.secfirst.umbrella.whitelabel.data.database.lesson.Lesson

interface LessonView : BaseView {

    fun showAllLesson(lessons: List<Lesson>){}

    fun showSelectDifficult(difficulties: List<Difficult>){}
}