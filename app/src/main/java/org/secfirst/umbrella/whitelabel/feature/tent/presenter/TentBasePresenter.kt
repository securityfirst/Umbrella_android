package org.secfirst.umbrella.whitelabel.feature.tent.presenter

import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.whitelabel.feature.tent.TentView
import org.secfirst.umbrella.whitelabel.feature.tent.interactor.TentBaseInteractor

interface TentBasePresenter<V : TentView, I : TentBaseInteractor> : BasePresenter<V, I> {

    fun submitUpdateRepository()

    fun submitFetchRepository(url : String)

    fun submitLoadElementsFile()

    fun submitLoadFile()
}