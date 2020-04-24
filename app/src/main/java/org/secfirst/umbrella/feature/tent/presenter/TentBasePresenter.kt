package org.secfirst.umbrella.feature.tent.presenter

import org.secfirst.umbrella.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.feature.tent.TentView
import org.secfirst.umbrella.feature.tent.interactor.TentBaseInteractor

interface TentBasePresenter<V : TentView, I : TentBaseInteractor> : BasePresenter<V, I> {

    fun submitUpdateRepository()

    fun submitFetchRepository(url : String)

    fun submitLoadElementsFile(path : String)
}