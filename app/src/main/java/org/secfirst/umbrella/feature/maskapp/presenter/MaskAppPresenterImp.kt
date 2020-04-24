package org.secfirst.umbrella.feature.maskapp.presenter

import org.secfirst.umbrella.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.feature.maskapp.interactor.MaskAppBaseInteractor
import org.secfirst.umbrella.feature.maskapp.view.MaskAppView
import javax.inject.Inject


class MaskAppPresenterImp<V : MaskAppView, I : MaskAppBaseInteractor> @Inject constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), MaskAppBasePresenter<V, I> {

    override fun setMaskApp() {
        val res = interactor?.setMaskApp(false) ?: false
        getView()?.isMaskApp(res)
    }
}