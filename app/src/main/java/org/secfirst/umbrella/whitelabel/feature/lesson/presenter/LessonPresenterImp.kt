package org.secfirst.umbrella.whitelabel.feature.lesson.presenter

import org.secfirst.umbrella.whitelabel.data.database.lesson.*
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.lesson.interactor.LessonBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.lesson.view.LessonView
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import javax.inject.Inject

class LessonPresenterImp<V : LessonView, I : LessonBaseInteractor> @Inject constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), LessonBasePresenter<V, I> {

    override fun submitSegments(difficultSelected: Difficult) {
        launchSilent(uiContext) {
            interactor?.let {
                val child = it.fetchChildBy(difficultSelected.idReference, difficultSelected.title)
                getView()?.showSegments(child.markdowns.toSegment())
            }
        }
    }

    override fun submitSelectTopic(topicSelected: Lesson.Topic) {
        launchSilent(uiContext) {
            interactor?.let {
                val subcategory = it.fetchSubcategoryBy(topicSelected.idReference)
                getView()?.showSelectDifficult(subcategory.toDifficult())
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