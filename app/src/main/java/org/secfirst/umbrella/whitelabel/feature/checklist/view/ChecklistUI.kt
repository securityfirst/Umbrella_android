package org.secfirst.umbrella.whitelabel.feature.checklist.view

import android.graphics.Color
import android.graphics.Color.WHITE
import android.graphics.drawable.ColorDrawable
import org.jetbrains.anko.*
import org.jetbrains.anko.appcompat.v7.toolbar
import org.secfirst.umbrella.whitelabel.R

class ChecklistUI : AnkoComponent<ChecklistController> {

    override fun createView(ui: AnkoContext<ChecklistController>) = ui.apply {

        scrollView {
            background = ColorDrawable(Color.parseColor(ctx.getString(R.string.chilist_view_background)))

            themedToolbar(theme = R.style.ToolbarStyle) {
                toolbar {
                    title = ctx.getString(R.string.checklist_title)
                    setTitleTextColor(WHITE)
                }.let {
                    ui.owner.mainActivity.setSupportActionBar(it)
                    ui.owner.mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
                }
            }
        }

    }.view

}