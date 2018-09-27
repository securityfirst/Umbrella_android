package org.secfirst.umbrella.whitelabel.feature.difficulty.presenter

import org.secfirst.umbrella.whitelabel.data.database.difficulty.toDifficult
import org.secfirst.umbrella.whitelabel.data.database.lesson.TopicPreferred
import org.secfirst.umbrella.whitelabel.data.database.segment.Segment
import org.secfirst.umbrella.whitelabel.data.database.segment.toSegment
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.difficulty.interactor.DifficultyBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.difficulty.view.DifficultyView
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import javax.inject.Inject

class DifficultyPresenterImp<V : DifficultyView, I : DifficultyBaseInteractor> @Inject constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), DifficultyBasePresenter<V, I> {

    override fun submitLoadSegments(idReference: Long) {
        launchSilent(uiContext) {
            interactor?.let {
                val segments = mutableListOf<Segment>()
                val subcategory = it.fetchSubcategoryBy(idReference)
                subcategory?.children?.forEach { child ->
                    val difficultTitle = "${subcategory.title} ${child.title}"
                    val segment = child.markdowns.toSegment(subcategory.id, difficultTitle)
                    segments.add(segment)
                }
                getView()?.startSegment(segments)
            }
        }
    }

    override fun saveDifficultySelected(idReference: Long) {
        launchSilent(uiContext) {
            interactor?.let {
                val subCategory = it.fetchSubcategoryBy(idReference)
                val childSelect = it.fetchChildBy(idReference)
                it.insertTopicPreferred(TopicPreferred(subCategory?.id, childSelect?.id))
            }
        }
    }

    override fun submitSelectDifficult(idReference: Long) {
        launchSilent(uiContext) {
            interactor?.let { it ->
                val subCategory = it.fetchSubcategoryBy(idReference)
                subCategory?.let { subIt ->
                    getView()?.showDifficulties(subIt.toDifficult())
                }
            }
        }
    }
}