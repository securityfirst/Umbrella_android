package org.secfirst.umbrella.whitelabel.feature.tour.presenter

import io.reactivex.disposables.CompositeDisposable
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.tour.interactor.TourBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.tour.view.TourView
import org.secfirst.umbrella.whitelabel.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.whitelabel.misc.SchedulerProvider
import org.secfirst.umbrella.whitelabel.misc.launchSilent
import javax.inject.Inject


class TourPresenterImp<V : TourView, I : TourBaseInteractor>
@Inject internal constructor(
        interactor: I,
        schedulerProvider: SchedulerProvider,
        disposable: CompositeDisposable) : BasePresenterImp<V, I>(
        interactor = interactor,
        schedulerProvider = schedulerProvider,
        compositeDisposable = disposable), TourBasePresenter<V, I> {

    override fun manageContent() {
        var result = false
        launchSilent(uiContext) {
            interactor?.let {
                getView()?.downloadContentInProgress()
                result = it.fetchData()
                if (result) {
                    val root = it.initParser()
                    it.persist(root)
                }
            }
            if (isActive) {
                getView()?.downloadContentCompleted(result)
            }
        }
    }
}