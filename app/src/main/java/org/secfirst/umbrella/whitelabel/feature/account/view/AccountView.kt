package org.secfirst.umbrella.whitelabel.feature.account.view

import org.secfirst.umbrella.whitelabel.feature.base.view.BaseView

interface AccountView : BaseView {

    fun isLogged(res: Boolean)
}