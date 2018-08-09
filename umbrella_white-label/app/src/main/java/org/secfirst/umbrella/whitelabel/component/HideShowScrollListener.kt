package org.secfirst.umbrella.whitelabel.component

import android.content.Context
import android.support.v7.widget.RecyclerView


abstract class HideShowScrollListener(context: Context) : RecyclerView.OnScrollListener() {

    private var toolbarOffset = 0
    private val toolbarHeight: Int

    init {
        val actionBarAttr = intArrayOf(android.R.attr.actionBarSize)
        val attributeStyle = context.obtainStyledAttributes(actionBarAttr)
        toolbarHeight = attributeStyle.getDimension(0, 0f).toInt() + 10
        attributeStyle.recycle()
    }

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        clipToolbarOffset()
        onMoved(toolbarOffset)

        if (toolbarOffset < toolbarHeight && dy > 5 || toolbarOffset > 0 && dy < 0) {
            toolbarOffset += dy
        }
    }

    private fun clipToolbarOffset() {
        if (toolbarOffset > toolbarHeight) {
            toolbarOffset = toolbarHeight
        } else if (toolbarOffset < 0) {
            toolbarOffset = 0
        }
    }

    abstract fun onMoved(distance: Int)
}