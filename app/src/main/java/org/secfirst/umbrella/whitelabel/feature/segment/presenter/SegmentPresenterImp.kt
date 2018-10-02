package org.secfirst.umbrella.whitelabel.feature.segment.presenter

import org.secfirst.umbrella.whitelabel.data.database.content.Module
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.data.database.difficulty.orderByDifficultySelected
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
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

    override fun submitLoadSegments(selectModule: Module) {
        val segments = mutableListOf<Segment>()
        with(selectModule) {
            if (markdowns.size > Markdown.SINGLE_CHOICE) {
                val segment = markdowns.toSegment(title, title)
                segments.add(segment)
                getView()?.showSegments(segments)
            }
        }
    }

    override fun submitLoadSegments(selectDifficulty: Difficulty) {
        launchSilent(uiContext) {
            interactor?.let { safeInteractor ->
                val segments = mutableListOf<Segment>()
                val subject = safeInteractor.fetchSubcategoryBy(selectDifficulty.subject!!.id)
                val orderDifficulties = subject?.difficulties?.orderByDifficultySelected(selectDifficulty.id)
                orderDifficulties?.forEach { safeOrderDiff ->
                    val toolbarTitle = "${subject.title} ${safeOrderDiff.title}"
                    val segment = safeOrderDiff.markdowns.toSegment(toolbarTitle, subject.title)
                    segments.add(segment)
                }
                getView()?.showSegments(segments)
            }
        }
    }
}