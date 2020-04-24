package org.secfirst.umbrella.feature.login.view

import org.secfirst.umbrella.feature.base.view.BaseView

interface LoginView : BaseView {

    fun isLoginOk(isLogged: Boolean)
    fun onResetContent(res: Boolean)
}