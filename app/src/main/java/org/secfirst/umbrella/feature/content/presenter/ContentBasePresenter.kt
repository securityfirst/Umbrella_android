package org.secfirst.umbrella.feature.content.presenter

import org.secfirst.umbrella.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.feature.content.ContentView
import org.secfirst.umbrella.feature.content.interactor.ContentBaseInteractor
import java.io.File


interface ContentBasePresenter<V : ContentView, I : ContentBaseInteractor> : BasePresenter<V, I> {

    fun manageContent(url:String)

    fun updateContent(pairFiles: List<Pair<String, File>>)
}