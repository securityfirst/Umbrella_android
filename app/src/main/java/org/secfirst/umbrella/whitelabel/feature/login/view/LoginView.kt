package org.secfirst.umbrella.whitelabel.feature.login.view

import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView

interface LoginView : BaseView {

    fun isLoginOk(isLogged: Boolean)
    fun onResetContent(res: Boolean)
}