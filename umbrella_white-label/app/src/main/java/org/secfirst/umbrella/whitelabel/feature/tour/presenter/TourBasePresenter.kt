package org.secfirst.umbrella.whitelabel.feature.tour.presenter

import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.whitelabel.feature.tour.interactor.TourBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.tour.view.TourView

interface TourBasePresenter<V : TourView, I : TourBaseInteractor> : BasePresenter<V, I> {

    fun manageContent()

}