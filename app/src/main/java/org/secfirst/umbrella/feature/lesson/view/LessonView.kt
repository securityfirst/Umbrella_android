package org.secfirst.umbrella.feature.lesson.view


import org.secfirst.umbrella.data.database.lesson.Lesson
import org.secfirst.umbrella.data.database.lesson.Subject
import org.secfirst.umbrella.data.database.segment.Markdown
import org.secfirst.umbrella.feature.base.view.BaseView

interface LessonView : BaseView {

    fun showAllLesson(lessons: List<Lesson>)

    fun startDifficultyController(subject: Subject)

    fun startSegment(markdownIds: ArrayList<String>, enableFilter: Boolean)

    fun startSegmentAlone(markdown: Markdown)
}