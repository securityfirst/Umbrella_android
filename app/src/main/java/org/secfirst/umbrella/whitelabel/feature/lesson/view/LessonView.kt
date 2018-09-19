package org.secfirst.umbrella.whitelabel.feature.lesson.view

import org.secfirst.umbrella.whitelabel.data.database.lesson.Difficult
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView
import org.secfirst.umbrella.whitelabel.data.database.lesson.Lesson
import org.secfirst.umbrella.whitelabel.data.database.lesson.Segment

interface LessonView : BaseView {

    fun showAllLesson(lessons: List<Lesson>){}

    fun showSelectDifficult(difficulties: List<Difficult>){}

    fun showSegments(segments : List<Segment>){}
}