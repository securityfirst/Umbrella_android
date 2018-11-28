package org.secfirst.umbrella.whitelabel.feature.content.presenter

import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.whitelabel.feature.content.ContentView
import org.secfirst.umbrella.whitelabel.feature.content.interactor.ContentBaseInteractor
import java.io.File


interface ContentBasePresenter<V : ContentView, I : ContentBaseInteractor> : BasePresenter<V, I> {

    fun manageContent()

    fun updateContent(pairFiles: List<Pair<String, File>>)

    fun cleanContent()
}