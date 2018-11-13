package org.secfirst.umbrella.whitelabel.feature.login.presenter

import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.whitelabel.feature.login.interactor.LoginBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.login.view.LoginView


interface LoginBasePresenter<V : LoginView, I : LoginBaseInteractor> : BasePresenter<V, I> {

    fun submitDatabaseAccess(userToken: String)

    fun submitChangeDatabaseAccess(userToken: String)
}