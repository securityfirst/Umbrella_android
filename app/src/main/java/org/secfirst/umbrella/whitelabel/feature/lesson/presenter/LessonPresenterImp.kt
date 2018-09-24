package org.secfirst.umbrella.whitelabel.feature.lesson.presenter

import org.secfirst.umbrella.whitelabel.data.database.lesson.toLesson
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.lesson.interactor.LessonBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.lesson.view.LessonView
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import javax.inject.Inject

class LessonPresenterImp<V : LessonView, I : LessonBaseInteractor> @Inject constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), LessonBasePresenter<V, I> {


    override fun submitSelectLesson(idReference: Long) {
        launchSilent(uiContext) {
            interactor?.let {

                val topicPreferred = it.fetchTopicPreferredBy(idReference)
                if (topicPreferred != null)
                    topicPreferred.subcategorySelected?.let { subcategory ->
                        getView()?.startDeferredSegment(subcategory.id)
                    }
                else getView()?.startDifficultyController(idReference)
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