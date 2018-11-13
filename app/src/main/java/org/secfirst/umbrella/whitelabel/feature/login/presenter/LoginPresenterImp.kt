package org.secfirst.umbrella.whitelabel.feature.login.presenter

import org.secfirst.umbrella.whitelabel.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.whitelabel.feature.login.interactor.LoginBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.login.view.LoginView
import javax.inject.Inject


class LoginPresenterImp<V : LoginView, I : LoginBaseInteractor> @Inject constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), LoginBasePresenter<V, I> {

    override fun submitDatabaseAccess(userToken: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun submitChangeDatabaseAccess(userToken: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}