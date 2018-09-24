package org.secfirst.umbrella.whitelabel.feature.difficulty.presenter

import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.whitelabel.feature.difficulty.interactor.DifficultyBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.difficulty.view.DifficultyView

interface DifficultyBasePresenter<V : DifficultyView, I : DifficultyBaseInteractor> : BasePresenter<V, I> {

    fun submitSelectDifficult(idReference: Long)

}