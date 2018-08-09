package org.secfirst.umbrella.whitelabel.misc

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.inputmethod.InputMethodManager
import org.secfirst.umbrella.whitelabel.feature.MainActivity
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController


fun RecyclerView.initRecyclerView(layoutManager: RecyclerView.LayoutManager,
                                  adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>,
                                  hasFixedSize: Boolean = true) {
    this.layoutManager = layoutManager
    this.adapter = adapter
    setHasFixedSize(hasFixedSize)
}

fun BaseController.hideKeyboard() {
    val inputMethodManager = this.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.view?.windowToken, 0)
}

fun MainActivity.hideKeyboard() {
    val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
}
