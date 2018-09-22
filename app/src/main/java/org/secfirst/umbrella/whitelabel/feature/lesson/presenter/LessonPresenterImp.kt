package org.secfirst.umbrella.whitelabel.feature.lesson.presenter

import org.secfirst.umbrella.whitelabel.data.database.lesson.TopicPreferred
import org.secfirst.umbrella.whitelabel.data.database.lesson.toDifficult
import org.secfirst.umbrella.whitelabel.data.database.lesson.toLesson
import org.secfirst.umbrella.whitelabel.data.database.lesson.toSegment
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.lesson.interactor.LessonBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.lesson.view.LessonView
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import javax.inject.Inject

class LessonPresenterImp<V : LessonView, I : LessonBaseInteractor> @Inject constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), LessonBasePresenter<V, I> {

    override fun submitSelectDifficult(idReference: Long) {
        launchSilent(uiContext) {
            interactor?.let {
                it.fetchChildBy(idReference)?.let { child ->
                    val subcategorySelected = it.fetchSubcategoryBy(idReference)
                    it.insertTopicPreferred(TopicPreferred(subcategorySelected, child))
                    getView()?.showDeferredSegment(child.markdowns.toSegment(idReference, subcategorySelected.title, child.index))
                }
            }
        }
    }


    override fun submitSelecteLesson(idReference: Long) {
        launchSilent(uiContext) {
            interactor?.let {

                val topicPreferred = it.fetchTopicPreferredBy(idReference)
                if (topicPreferred != null)
                    topicPreferred.subcategorySelected?.let { subcategory ->
                        topicPreferred.childSelected?.let { child ->
                            val toolbarTitle = "${subcategory.title} ${child.title}"
                            getView()?.showDeferredSegment(child.markdowns.toSegment(subcategory.id, toolbarTitle, child.index))
                        }
                    }
                else {
                    val subCategory = it.fetchSubcategoryBy(idReference)
                    getView()?.showDifficulties(subCategory.toDifficult())
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