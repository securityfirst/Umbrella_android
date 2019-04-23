package org.secfirst.umbrella.feature.login.presenter

import org.secfirst.umbrella.feature.base.presenter.BasePresenter
import org.secfirst.umbrella.feature.login.interactor.LoginBaseInteractor
import org.secfirst.umbrella.feature.login.view.LoginView


interface LoginBasePresenter<V : LoginView, I : LoginBaseInteractor> : BasePresenter<V, I> {

    fun submitChangeDatabaseAccess(userToken: String)

    fun submitResetPassword()
}