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


    override fun submitLoadLessonInSegment(idReference: Long) {
        launchSilent(uiContext) {
            interactor?.let {
                val subcategory = it.fetchSubcategoryBy(idReference)
                val segments = arrayListOf<Segment>()
                subcategory.children.forEach { child ->
                    val spinnerTitle = "${subcategory.title} ${child.title}"
                    val segment = child.markdowns.toSegment(subcategory.id,spinnerTitle)
                    segments.add(segment)
                }
                getView()?.showDifficultLevel(segments)
            }
        }
    }

    override fun submitSelectDifficult(idReference: Long) {
        launchSilent(uiContext) {
            interactor?.let {
                it.fetchChildBy(idReference)?.let { child ->
                    val subcategorySelected = it.fetchSubcategoryBy(idReference)
                    it.insertTopicPreferred(TopicPreferred(subcategorySelected, child))
                    getView()?.showDeferredSegment(child.markdowns.toSegment(idReference, subcategorySelected.title))
                }
            }
        }
    }


    override fun submitSelectLesson(idReference: Long) {
        launchSilent(uiContext) {
            interactor?.let {

                val topicPreferred = it.fetchTopicPreferredBy(idReference)
                if (topicPreferred != null)
                    topicPreferred.subcategorySelected?.let { subcategory ->
                        topicPreferred.childSelected?.let { child ->
                            val toolbarTitle = "${subcategory.title} ${child.title}"
                            getView()?.showDeferredSegment(child.markdowns.toSegment(subcategory.id, toolbarTitle))
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