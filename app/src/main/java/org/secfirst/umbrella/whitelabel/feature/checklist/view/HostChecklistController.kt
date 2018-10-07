package org.secfirst.umbrella.whitelabel.feature.checklist.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.host_checklist.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.checklist.view.adapter.HostChecklistAdapter


class HostChecklistController : BaseController() {

    override fun onInject() {
    }

    override fun onAttach(view: View) {
        hostChecklistPager?.adapter = HostChecklistAdapter(this)
        hostChecklistTab?.setupWithViewPager(hostChecklistPager)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.host_checklist, container, false)
    }

    override fun onDestroyView(view: View) {
        hostChecklistPager?.adapter = null
        hostChecklistTab?.setupWithViewPager(null)
        super.onDestroyView(view)
    }
}