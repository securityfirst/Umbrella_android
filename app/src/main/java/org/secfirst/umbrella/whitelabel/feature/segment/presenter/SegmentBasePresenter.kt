package org.secfirst.umbrella.whitelabel.feature.segment.presenter

import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.content.Module
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.whitelabel.feature.segment.interactor.SegmentBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.segment.view.SegmentView


interface SegmentBasePresenter<V : SegmentView, I : SegmentBaseInteractor> : BasePresenter<V, I> {

    fun submitLoadSegments(selectDifficulty: Difficulty)

    fun submitLoadSegments(selectModule: Module)

    fun submitChecklistFavorite(checklist: Checklist)
}