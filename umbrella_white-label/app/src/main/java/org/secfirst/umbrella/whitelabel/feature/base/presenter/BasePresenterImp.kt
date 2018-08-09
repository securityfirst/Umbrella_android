package org.secfirst.umbrella.whitelabel.feature.base.presenter

import io.reactivex.disposables.CompositeDisposable
import org.secfirst.umbrella.whitelabel.feature.base.interactor.BaseInteractor
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView
import org.secfirst.umbrella.whitelabel.misc.SchedulerProvider

abstract class BasePresenterImp<V : BaseView, I : BaseInteractor>
internal constructor(protected var interactor: I?,
                     protected val schedulerProvider: SchedulerProvider,
                     protected val compositeDisposable: CompositeDisposable) : BasePresenter<V, I> {

    private var view: V? = null

    private val isViewAttached: Boolean get() = view != null

    override fun onAttach(view: V?) {
        this.view = view
    }

    override fun getView(): V? = view

    override fun onDetach() {
        compositeDisposable.dispose()
        view = null
        interactor = null
    }
}
