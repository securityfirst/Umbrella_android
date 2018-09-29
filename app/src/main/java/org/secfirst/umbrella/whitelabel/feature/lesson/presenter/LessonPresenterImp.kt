package org.secfirst.umbrella.whitelabel.feature.lesson.presenter

import org.secfirst.umbrella.whitelabel.data.database.content.Subject
import org.secfirst.umbrella.whitelabel.data.database.lesson.Lesson.Companion.ABOUT
import org.secfirst.umbrella.whitelabel.data.database.lesson.Lesson.Companion.GLOSSARY
import org.secfirst.umbrella.whitelabel.data.database.lesson.toLesson
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.data.database.segment.Segment
import org.secfirst.umbrella.whitelabel.data.database.segment.toSegment
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.lesson.interactor.LessonBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.lesson.view.LessonView
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import javax.inject.Inject

class LessonPresenterImp<V : LessonView, I : LessonBaseInteractor> @Inject constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), LessonBasePresenter<V, I> {


    override fun submitSelectHead(subject: String, moduleId: Long) {
        launchSilent(uiContext) {
            interactor?.let {
                val segments = mutableListOf<Segment>()
                when (subject.toLowerCase()) {
                    GLOSSARY -> {
                        val category = it.fetchCategoryBy(moduleId)
                        category?.let { catIt ->
                            val segment = catIt.markdowns.toSegment(category.id, category.title)
                            segments.add(segment)
                        }
                        getView()?.startDeferredSegment(segments)
                    }
                    ABOUT -> {
                        val markdown = it.fetchMarkdownByModule(moduleId)
                        markdown?.let { mark ->
                            getView()?.startSegmentDetail(mark)
                        }
                    }
                    else -> ""
                }
            }
        }
    }

    override fun submitSelectLesson(subject: Subject) {
        launchSilent(uiContext) {
            interactor?.let {

                val moduleId = if (subject.module != null) subject.module!!.id else 0
                val topicPreferred = it.fetchTopicPreferredBy(subject.id)
                val markdown = it.fetchMarkdownBySubject(subject.id)

                if (topicPreferred != null)
                    subjectInSegment(topicPreferred.subject)
                else if (subject.difficulties.isEmpty() && markdown != null) {
                    subjectInSegmentDetail(markdown)
                } else {
                    subjectInDifficulty(moduleId)
                }
            }
        }
    }

    private fun subjectInDifficulty(moduleId: Long) {
        getView()?.startDifficultyController(moduleId)
    }

    private fun subjectInSegment(subjectPreferred: Subject?) {

        val segments = mutableListOf<Segment>()
        subjectPreferred?.difficulties?.forEach { child ->
            val difficultTitle = "${subjectPreferred.title} ${child.title}"
            val segment = child.markdowns.toSegment(subjectPreferred.id, difficultTitle)
            segments.add(segment)
            getView()?.startDeferredSegment(segments)
        }
    }

    private fun subjectInSegmentDetail(markdown: Markdown) {
        getView()?.startSegmentDetail(markdown)
    }

    override fun submitLoadAllLesson() {
        launchSilent(uiContext) {
            interactor?.let {
                val categories = it.fetchCategories()
                        .asSequence()
                        .filter { category -> category.title != "" }
                        .toList()
                getView()?.showAllLesson(categories.toLesson())
            }
        }
    }
}