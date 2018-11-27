package org.secfirst.umbrella.whitelabel.feature.tent.presenter

import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.whitelabel.feature.tent.TentView
import org.secfirst.umbrella.whitelabel.feature.tent.interactor.TentBaseInteractor

interface TentBasePresenter<V : TentView, I : TentBaseInteractor> : BasePresenter<V, I> {

    fun submitUpdateRepository()

    fun submitFetchRepository()

    fun submitLoadElementsFile()

    fun submitLoadCategoryImage(imgName: String)

    fun submitLoadFile()
}