package org.secfirst.umbrella.whitelabel.feature.lesson.view


import org.secfirst.umbrella.whitelabel.data.database.lesson.Lesson
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.segment.Segment
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView

interface LessonView : BaseView {

    fun showAllLesson(lessons: List<Lesson>)

    fun startDeferredSegment(segments: List<Segment>)

    fun startSegmentDetail(markdown : Markdown)

    fun startDifficultyController(moduleId: Long)


}