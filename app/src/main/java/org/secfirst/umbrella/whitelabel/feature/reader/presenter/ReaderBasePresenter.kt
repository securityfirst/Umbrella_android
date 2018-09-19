package org.secfirst.umbrella.whitelabel.feature.reader.presenter

import org.secfirst.umbrella.whitelabel.data.database.reader.RSS
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.whitelabel.feature.reader.interactor.ReaderBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.reader.view.ReaderView

interface ReaderBasePresenter<V : ReaderView, I : ReaderBaseInteractor> : BasePresenter<V, I> {

    fun submitFetchRss()

    fun submitInsertRss(rss: RSS)

    fun submitDeleteRss(rss: RSS)
}