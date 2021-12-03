package org.secfirst.umbrella.feature.login.view

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.Toast
import com.bluelinelabs.conductor.RouterTransaction
import kotlinx.android.synthetic.main.account_reset_password_alert.view.*
import kotlinx.android.synthetic.main.login_view.*
import kotlinx.android.synthetic.main.login_view.view.*
import org.jetbrains.anko.toast
import org.secfirst.umbrella.R
import org.secfirst.umbrella.UmbrellaApplication
import org.secfirst.umbrella.feature.base.view.BaseController
import org.secfirst.umbrella.feature.checklist.view.controller.HostChecklistController
import org.secfirst.umbrella.feature.login.DaggerLoginComponent
import org.secfirst.umbrella.feature.login.interactor.LoginBaseInteractor
import org.secfirst.umbrella.feature.login.presenter.LoginBasePresenter
import org.secfirst.umbrella.misc.doRestartApplication
import org.secfirst.umbrella.misc.hideKeyboard
import javax.inject.Inject

class LoginController : BaseController(), LoginView {

    @Inject
    internal lateinit var presenter: LoginBasePresenter<LoginView, LoginBaseInteractor>
    private lateinit var resetPasswordDialog: AlertDialog
    private lateinit var resetPasswordView: View

    override fun onInject() {
        DaggerLoginComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        enableNavigation(false)
        setUpToolbar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View {
        presenter.onAttach(this)
        val view = inflater.inflate(R.layout.login_view, container, false)
        resetPasswordView = inflater.inflate(R.layout.account_reset_password_alert, container, false)

        resetPasswordDialog = AlertDialog
                .Builder(activity)
                .setView(resetPasswordView)
                .create()
        resetPasswordView.resetCancel.setOnClickListener { resetAlertCancel() }
        resetPasswordView.resetOk.setOnClickListener { resetAlertOk() }
        view.loginButton.setOnClickListener { doLogin() }

        setHasOptionsMenu(true)

        return view
    }

    private fun resetAlertCancel() = resetPasswordDialog.dismiss()

    private fun resetAlertOk() {
        resetPasswordDialog.dismiss()
        presenter.submitResetPassword()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.findItem(R.id.menu_search).apply {
            this.isVisible = false
        }
        return inflater.inflate(R.menu.login_menu, menu)
    }

    override fun isLoginOk(isLogged: Boolean) {
        if (isLogged) {
            hideKeyboard()
            enableNavigation(true)
            router.pushController(RouterTransaction.with(HostChecklistController()))
            router.popController(this)
            mainActivity.resetAppbar()
        } else
            Toast.makeText(context, "Incorrect login.", Toast.LENGTH_LONG).show()
    }

    private fun doLogin() {
        presenter.submitChangeDatabaseAccess(loginPwText?.text.toString())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.login_reset_password -> resetPasswordDialog.show()
        }
        return true
    }

    private fun setUpToolbar() {
        loginToolbar?.let {
            /*mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)*/
        }
    }

    override fun onResetContent(res: Boolean) {
        if (res) doRestartApplication(context)
        else context.toast(context.getString(R.string.login_error_reset_password))
    }
}
