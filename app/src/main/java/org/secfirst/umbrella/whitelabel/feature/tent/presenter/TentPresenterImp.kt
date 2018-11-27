package org.secfirst.umbrella.whitelabel.feature.tent.presenter

import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.tent.TentView
import org.secfirst.umbrella.whitelabel.feature.tent.interactor.TentBaseInteractor
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import javax.inject.Inject

class TentPresenterImp<V : TentView, I : TentBaseInteractor>
@Inject internal constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), TentBasePresenter<V, I> {

    override  fun submitUpdateRepository() {
        launchSilent(uiContext) {
            interactor?.let {
                getView()?.isUpdateRepository(it.updateRepository())
            }
        }
    }

    override fun submitFetchRepository() {
        launchSilent(uiContext) {
            interactor?.let {
                getView()?.isFetchRepository(it.fetchRepository())
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

    override fun submitLoadCategoryImage(imgName: String) {
        launchSilent(uiContext) {
            interactor?.let {
                getView()?.getCategoryImage(it.loadCategoryImage(imgName))
            }
        }
    }

    override fun submitLoadFile() {
        launchSilent(uiContext) {
            interactor?.loadFile()
        }
    }
}