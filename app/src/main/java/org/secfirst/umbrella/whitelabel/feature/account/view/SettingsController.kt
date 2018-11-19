package org.secfirst.umbrella.whitelabel.feature.account.view

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.account_settings_view.*
import kotlinx.android.synthetic.main.account_settings_view.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.component.DialogManager
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController

class SettingsController : BaseController() {

    private lateinit var exportAlertDialog: AlertDialog
    private lateinit var exportView: View

    override fun onInject() {}

    override fun onAttach(view: View) {
        super.onAttach(view)
        setUpToolbar()
        disableNavigation()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.account_settings_view, container, false)
        exportView = inflater.inflate(R.layout.settings_export_dialog, container, false)
        exportAlertDialog = AlertDialog
                .Builder(activity)
                .setView(exportView)
                .create()

        view.settingsExportData.setOnClickListener { exportDataClick() }
        return view
    }

    private fun exportDataClick() {
        val dialogManager = DialogManager(this)
        dialogManager.showDialog(object : DialogManager.DialogFactory {
            override fun createDialog(context: Context?): Dialog {
                return exportAlertDialog
            }
        })
    }

    private fun setUpToolbar() {
        settingsToolbar?.let {
            it.title = "Settings"
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.setDisplayShowHomeEnabled(true)
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }
}