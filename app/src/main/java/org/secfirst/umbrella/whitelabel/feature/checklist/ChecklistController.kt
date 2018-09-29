package org.secfirst.umbrella.whitelabel.feature.checklist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.jetbrains.anko.AnkoContext
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController

class ChecklistController : BaseController(), ChecklistView {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        val view = ChecklistUI()
        return view.createView(AnkoContext.create(context, this, false))
    }

    override fun onInject() {
        DaggerChecklistComponent.builder()
                .application(UmbrellaApplication.instance)
                .build()
                .inject(this)
    }

}