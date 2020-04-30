package org.secfirst.umbrella.feature.maskapp.presenter

import org.secfirst.umbrella.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.feature.maskapp.interactor.MaskAppBaseInteractor
import org.secfirst.umbrella.feature.maskapp.view.MaskAppView


interface MaskAppBasePresenter<V : MaskAppView, I : MaskAppBaseInteractor> : BasePresenter<V, I> {

    fun setMaskApp()
}