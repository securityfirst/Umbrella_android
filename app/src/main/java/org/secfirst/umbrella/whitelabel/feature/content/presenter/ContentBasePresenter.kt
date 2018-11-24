package org.secfirst.umbrella.whitelabel.feature.content.presenter

import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.whitelabel.feature.content.ContentView
import org.secfirst.umbrella.whitelabel.feature.content.interactor.ContentBaseInteractor


interface ContentBasePresenter<V : ContentView, I : ContentBaseInteractor> : BasePresenter<V, I> {

    fun manageContent()

    fun cleanContent()
}