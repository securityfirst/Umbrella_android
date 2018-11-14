package org.secfirst.umbrella.whitelabel.feature.login.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.login.DaggerLoginComponent
import org.secfirst.umbrella.whitelabel.feature.login.interactor.LoginBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.login.presenter.LoginBasePresenter
import javax.inject.Inject

class LoginController : BaseController() {

    @Inject
    internal lateinit var presenter: LoginBasePresenter<LoginView, LoginBaseInteractor>

    override fun onInject() {
        DaggerLoginComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.login_view, container, false)
    }
}
