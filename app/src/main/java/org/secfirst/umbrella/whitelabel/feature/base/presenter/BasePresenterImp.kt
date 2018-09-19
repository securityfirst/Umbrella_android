package org.secfirst.umbrella.whitelabel.feature.base.presenter

import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView

abstract class BasePresenterImp<V : BaseView, I : BaseInteractor>
internal constructor(protected var interactor: I?) : BasePresenter<V, I> {

    private var view: V? = null

    private val isViewAttached: Boolean get() = view != null

    override fun onAttach(view: V?) {
        this.view = view
    }

    override fun getView(): V? = view

    override fun onDetach() {
        view = null
        interactor = null
    }
}
