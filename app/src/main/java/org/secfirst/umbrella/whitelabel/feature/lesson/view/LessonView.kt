package org.secfirst.umbrella.whitelabel.feature.lesson.view


import org.secfirst.umbrella.whitelabel.data.database.lesson.Lesson
import org.secfirst.umbrella.whitelabel.data.database.lesson.Subject
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView

interface LessonView : BaseView {

    fun showAllLesson(lessons: List<Lesson>)

    // fun startSegmentController(subject: Subject)

    fun startDifficultyController(subject: Subject)

    fun startSegmentWithFilter(difficultyIds: ArrayList<String>)

    fun startSegment(markdownIds: ArrayList<String>)
}