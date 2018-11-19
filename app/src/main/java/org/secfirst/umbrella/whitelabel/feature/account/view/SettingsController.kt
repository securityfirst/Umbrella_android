package org.secfirst.umbrella.whitelabel.feature.account.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.account_settings_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController

class SettingsController : BaseController() {

    override fun onInject() {

    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        setUpToolbar()
        disableNavigation()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.account_settings_view, container, false)
        return view
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