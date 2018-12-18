package org.secfirst.umbrella.whitelabel.feature.maskapp.presenter

import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.whitelabel.feature.maskapp.interactor.MaskAppBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.maskapp.view.MaskAppView


interface MaskAppBasePresenter<V : MaskAppView, I : MaskAppBaseInteractor> : BasePresenter<V, I> {

    fun setMaskApp()
}