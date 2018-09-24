package org.secfirst.umbrella.whitelabel.feature.difficulty.presenter

import org.secfirst.umbrella.whitelabel.data.database.difficulty.toDifficult
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.difficulty.interactor.DifficultyBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.difficulty.view.DifficultyView
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import javax.inject.Inject

class DifficultyPresenterImp<V : DifficultyView, I : DifficultyBaseInteractor> @Inject constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), DifficultyBasePresenter<V, I> {

    override fun submitSelectDifficult(idReference: Long) {
        launchSilent(uiContext) {
            interactor?.let { it ->
                val subCategory = it.fetchSubcategoryBy(idReference)
                getView()?.showDifficulties(subCategory.toDifficult())
            }
        }
    }
}