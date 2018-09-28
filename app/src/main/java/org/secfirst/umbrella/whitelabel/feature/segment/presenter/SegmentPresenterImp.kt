package org.secfirst.umbrella.whitelabel.feature.segment.presenter

import org.secfirst.umbrella.whitelabel.data.database.segment.Segment
import org.secfirst.umbrella.whitelabel.data.database.segment.toSegment
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.segment.interactor.SegmentBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentView
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import javax.inject.Inject


class SegmentPresenterImp<V : SegmentView, I : SegmentBaseInteractor> @Inject constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), SegmentBasePresenter<V, I> {

    override fun submitLoadSegments(idReference: Long) {
        launchSilent(uiContext) {
            interactor?.let {
                val subcategory = it.fetchSubcategoryBy(idReference)
                val segments = arrayListOf<Segment>()
                subcategory?.difficulties?.forEach { child ->
                    val difficultTitle = "${subcategory.title} ${child.title}"
                    val segment = child.markdowns.toSegment(subcategory.id, difficultTitle)
                    segments.add(segment)
                }
                //getView()?.showSegments(segments)
            }
        }
    }
}