package org.secfirst.umbrella.whitelabel.feature.checklist.view.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.checklist_custom_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController

class ChecklistCustomController : BaseController() {

    override fun onInject() {}

    override fun onAttach(view: View) {
        setUpToolbar()
        disableNavigation()
    }

    override fun onDestroyView(view: View) {
        enableNavigation()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = inflater.inflate(R.layout.checklist_custom_view, container, false)
        return view
    }

    private fun setUpToolbar() {
        customChecklistToolbar?.let {
            mainActivity.setSupportActionBar(it)
            mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
            mainActivity.supportActionBar?.title = "Your checklists"
        }
    }
}