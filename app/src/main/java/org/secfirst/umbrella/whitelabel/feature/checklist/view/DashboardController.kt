package org.secfirst.umbrella.whitelabel.feature.checklist.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.checklist_dashboard.*
import kotlinx.android.synthetic.main.host_reader_view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.reader.view.adapter.ReaderAdapter

class DashboardController : BaseController() {

    override fun onInject() {
    }

    override fun onAttach(view: View) {
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.checklist_dashboard, container, false)
    }
}