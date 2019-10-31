package org.secfirst.umbrella.feature.login.presenter

import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.feature.base.presenter.BasePresenterImp
import org.secfirst.umbrella.feature.login.interactor.LoginBaseInteractor
import org.secfirst.umbrella.feature.login.view.LoginView
import org.secfirst.umbrella.misc.AppExecutors.Companion.uiContext
import org.secfirst.umbrella.misc.launchSilent
import javax.inject.Inject


class LoginPresenterImp<V : LoginView, I : LoginBaseInteractor> @Inject constructor(
        interactor: I) : BasePresenterImp<V, I>(
        interactor = interactor), LoginBasePresenter<V, I> {

    override fun submitResetPassword() {
        launchSilent(uiContext) {
            interactor?.let {
                val res = it.resetContent()
                getView()?.onResetContent(res)
            }
        }
    }

    override fun submitChangeDatabaseAccess(userToken: String) {
        interactor?.let {
            launchSilent(uiContext) {
                if (UmbrellaApplication.instance.checkPassword(userToken)) {
                    it.dispatchLoginDatabaseAccess(userToken)
                    it.setSkipPassword(true)
                    getView()?.isLoginOk(true)
                } else {
                    getView()?.isLoginOk(false)
                }
            }
        }
    }
}