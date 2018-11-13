package org.secfirst.umbrella.whitelabel.feature.checklist.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController

class CustomChecklistController : BaseController() {

    override fun onInject() {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.account_view, container, false)
        return view
    }
}