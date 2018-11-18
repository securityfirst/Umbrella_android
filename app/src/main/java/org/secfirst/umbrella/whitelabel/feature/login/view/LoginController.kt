package org.secfirst.umbrella.whitelabel.feature.login.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.login_view.*
import kotlinx.android.synthetic.main.login_view.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.checklist.view.HostChecklistController
import org.secfirst.umbrella.whitelabel.feature.login.DaggerLoginComponent
import org.secfirst.umbrella.whitelabel.feature.login.interactor.LoginBaseInteractor
import org.secfirst.umbrella.whitelabel.feature.login.presenter.LoginBasePresenter
import org.secfirst.umbrella.whitelabel.misc.hideKeyboard
import javax.inject.Inject

class LoginController : BaseController(), LoginView {

    @Inject
    internal lateinit var presenter: LoginBasePresenter<LoginView, LoginBaseInteractor>

    override fun onInject() {
        DaggerLoginComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        disableNavigation()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        presenter.onAttach(this)
        val view = inflater.inflate(R.layout.login_view, container, false)
        view.loginButton.setOnClickListener { doLogin() }
        return view
    }


    override fun isLoginOk(isLogged: Boolean) {
        if (isLogged) {
            hideKeyboard()
            enableNavigation()
            router.pushController(RouterTransaction.with(HostChecklistController()))
            router.popController(this)
        } else
            Toast.makeText(context, "Incorrect login.", Toast.LENGTH_LONG).show()
    }

    private fun doLogin() {
        presenter.submitChangeDatabaseAccess(loginPwText?.text.toString())
    }
}
