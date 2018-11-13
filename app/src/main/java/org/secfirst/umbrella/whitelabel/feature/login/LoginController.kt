package org.secfirst.umbrella.whitelabel.feature.login

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController

class LoginController : BaseController() {

    override fun onInject() {

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.login_view, container, false)
    }
}