package org.secfirst.umbrella.feature.search.presenter

import org.secfirst.umbrella.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.feature.search.interactor.SearchBaseInteractor
import org.secfirst.umbrella.feature.search.view.SearchView

interface SearchBasePresenter<V : SearchView, I : SearchBaseInteractor> : BasePresenter<V, I> {
    
        fun submitSearchQuery(query: String)
    }