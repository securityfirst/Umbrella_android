package org.secfirst.umbrella.whitelabel.feature.checklist

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.scrollView
import org.secfirst.umbrella.whitelabel.R

class ChecklistUI : AnkoComponent<ChecklistController> {

    override fun createView(ui: AnkoContext<ChecklistController>) = ui.apply {

        scrollView {
            background = ColorDrawable(Color.parseColor(context.getString(R.string.chilist_view_background)))

        }

    }.view

}