package org.secfirst.umbrella.whitelabel.feature.account

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import org.secfirst.umbrella.whitelabel.R

class AccountController : Controller() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.account_view, container, false)
    }

}