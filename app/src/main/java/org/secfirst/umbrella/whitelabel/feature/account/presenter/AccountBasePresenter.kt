package org.secfirst.umbrella.whitelabel.feature.account.presenter

import org.secfirst.umbrella.whitelabel.feature.account.view.AccountView
import org.secfirst.umbrella.whitelabel.feature.account.interactor.AccountBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenter

interface AccountBasePresenter<V : AccountView, I : AccountBaseInteractor> : BasePresenter<V, I> {

    fun submitDatabaseAccess(userToken: String)

    fun submitChangeDatabaseAccess(userToken: String)
}