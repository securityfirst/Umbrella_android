package org.secfirst.umbrella.whitelabel.feature.account

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.account_password_alert.view.*
import kotlinx.android.synthetic.main.account_view.*
import kotlinx.android.synthetic.main.account_view.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.component.DialogManager
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController

class AccountController : BaseController() {

    private lateinit var passwordAlertDialog: AlertDialog
    private lateinit var passwordView: View

    override fun onInject() {
        //database<AppDatabase>().reopen()
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        setUpToolbar()


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val accountView = inflater.inflate(R.layout.account_view, container, false)
        passwordView = inflater.inflate(R.layout.account_password_alert, container, false)
        passwordAlertDialog = AlertDialog
                .Builder(activity)
                .setView(passwordView)
                .create()
        accountView.accountPassword.setOnClickListener { onPasswordClick() }
        passwordView.passwordSkip.setOnClickListener { onSkip() }
        passwordView.passwordOk.setOnClickListener { onOk() }
        passwordView.passwordCancel.setOnClickListener { onCancel() }

        return accountView
    }

    private fun onOk() {

    }

    private fun onCancel() = passwordAlertDialog.dismiss()

    private fun onSkip() = passwordAlertDialog.dismiss()

    private fun onPasswordClick() {
        val dialogManager = DialogManager(this)
        dialogManager.showDialog(object : DialogManager.DialogFactory {
            override fun createDialog(context: Context?): Dialog {
                return passwordAlertDialog
            }
        })
    }

    private fun setUpToolbar() {
        accountToolbar?.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.title = context.getString(R.string.title_account)
        }
    }

}