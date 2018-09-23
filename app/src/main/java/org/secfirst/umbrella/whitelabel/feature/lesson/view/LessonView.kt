package org.secfirst.umbrella.whitelabel.feature.lesson.view


import org.secfirst.umbrella.whitelabel.data.database.lesson.Difficult
import org.secfirst.umbrella.whitelabel.data.database.lesson.Lesson
import org.secfirst.umbrella.whitelabel.data.database.lesson.Segment
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView

interface LessonView : BaseView {

    fun showAllLesson(lessons: List<Lesson>) {}

    fun showDifficultLevel(segmentList: ArrayList<Segment>) {}

    fun showDeferredSegment(segment: Segment) {}

    fun showDifficulties(difficulties: List<Difficult>) {}

}