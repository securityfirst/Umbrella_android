package org.secfirst.umbrella.feature.base.presenter

import org.secfirst.umbrella.feature.base.interactor.BaseInteractor
import org.secfirst.umbrella.feature.base.view.BaseView

interface BasePresenter<V : BaseView, I : BaseInteractor> {

    fun onAttach(view: V?)

    fun onDetach()

    fun getView(): V?
}
