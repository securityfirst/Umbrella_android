package org.secfirst.umbrella.whitelabel.feature.lesson.presenter

import org.secfirst.umbrella.whitelabel.data.database.lesson.Lesson
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


    override fun submitSelectLesson(subject: String, idReference: Long) {
        launchSilent(uiContext) {
            interactor?.let {
                val segments = mutableListOf<Segment>()
                if (subject.toLowerCase() == Lesson.GLOSSARY.toLowerCase()) {
                    val category = it.fetchCategoryBy(idReference)
                    category?.let { catIt ->
                        val segment = catIt.markdowns.toSegment(category.id, category.title)
                        segments.add(segment)
                    }
                    getView()?.startDeferredSegment(segments)
                }
            }
        }

    }

    override fun submitSelectLesson(idReference: Long) {
        launchSilent(uiContext) {
            interactor?.let {
                val topicPreferred = it.fetchTopicPreferredBy(idReference)
                if (topicPreferred?.subcategoryId != null) {
                    val segments = mutableListOf<Segment>()
                    val subcategoryPreferred = it.fetchSubcategoryBy(idReference)
                    subcategoryPreferred?.children?.forEach { child ->
                        val difficultTitle = "${subcategoryPreferred.title} ${child.title}"
                        val segment = child.markdowns.toSegment(subcategoryPreferred.id, difficultTitle)
                        segments.add(segment)
                        getView()?.startDeferredSegment(segments)
                    }
                } else getView()?.startDifficultyController(idReference)
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