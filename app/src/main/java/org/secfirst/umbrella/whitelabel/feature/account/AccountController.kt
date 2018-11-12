package org.secfirst.umbrella.whitelabel.feature.account

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.account_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController

class AccountController : BaseController() {


    override fun onInject() {
    }

    override fun onAttach(view: View) {
        super.onAttach(view)
        setUpToolbar()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.account_view, container, false)
    }

    private fun setUpToolbar() {
        accountToolbar?.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.title = context.getString(R.string.title_account)
        }
    }

}