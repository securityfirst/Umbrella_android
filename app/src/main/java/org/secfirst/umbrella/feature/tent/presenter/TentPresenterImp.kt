package org.secfirst.umbrella.feature.tent.presenter

import org.secfirst.umbrella.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.feature.tent.TentView
import org.secfirst.umbrella.feature.tent.interactor.TentBaseInteractor
import org.secfirst.umbrella.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.misc.launchSilent
import javax.inject.Inject

class TentPresenterImp<V : TentView, I : TentBaseInteractor>
@Inject internal constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), TentBasePresenter<V, I> {

    override fun submitUpdateRepository() {
        launchSilent(uiContext) {
            interactor?.let {
                getView()?.isUpdateRepository(it.updateRepository())
            }
        }
    }

    override fun submitFetchRepository(url : String) {
        launchSilent(uiContext) {
            interactor?.let {
                getView()?.isFetchRepository(it.fetchRepository(url))
            }
        }
    }

    override fun submitLoadElementsFile() {
        launchSilent(uiContext) {
            interactor?.let {
                getView()?.onLoadElementSuccess(it.loadElementsFile())
            }
        }
    }

    override fun submitLoadFile() {
        launchSilent(uiContext) {
            interactor?.loadFile()
        }
    }
}