package org.secfirst.umbrella.whitelabel.feature.lesson.presenter

import org.secfirst.umbrella.whitelabel.data.database.lesson.Lesson.Companion.GLOSSARY
import org.secfirst.umbrella.whitelabel.data.database.lesson.toLesson
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


    override fun submitSelectLesson(subject: String, moduleId: Long) {
        launchSilent(uiContext) {
            interactor?.let {
                val segments = mutableListOf<Segment>()
                if (subject.toLowerCase() == GLOSSARY.toLowerCase()) {
                    val category = it.fetchCategoryBy(moduleId)
                    category?.let { catIt ->
                        val segment = catIt.markdowns.toSegment(category.id, category.title)
                        segments.add(segment)
                    }
                    getView()?.startDeferredSegment(segments)
                }
            }
        }

    }

    override fun submitSelectLesson(moduleId: Long) {
        launchSilent(uiContext) {
            interactor?.let {

                val topicPreferred = it.fetchTopicPreferredBy(moduleId)
                val subjectPreferred = it.fetchSubcategoryBy(moduleId)
                val markdown = it.fetchMarkdownBy(moduleId)

                when {
                    markdown != null -> {
                        getView()?.startSegmentDetail(markdown)
                    }

                    topicPreferred != null -> {
                        val segments = mutableListOf<Segment>()
                        subjectPreferred?.difficulties?.forEach { child ->
                            val difficultTitle = "${subjectPreferred.title} ${child.title}"
                            val segment = child.markdowns.toSegment(subjectPreferred.id, difficultTitle)
                            segments.add(segment)
                            getView()?.startDeferredSegment(segments)
                        }
                    }
                    else -> getView()?.startDifficultyController(moduleId)
                }
            }
        }
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