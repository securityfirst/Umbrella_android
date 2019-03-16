package org.secfirst.umbrella.whitelabel.misc

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.head_section.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController

val TextView.regular: Typeface get() = Typeface.createFromAsset(context.assets, "fonts/Roboto-Regular.ttf")
val TextView.medium: Typeface get() = Typeface.createFromAsset(context.assets, "fonts/Roboto-Medium.ttf")

fun Context.shareLink(link: String) {
    val sendIntent = Intent()
    sendIntent.action = Intent.ACTION_SEND
    sendIntent.putExtra(Intent.EXTRA_TEXT, link)
    sendIntent.type = "text/html"
    this.startActivity(Intent.createChooser(sendIntent, this.resources.getText(R.string.send_to)))
}

fun RecyclerView.initRecyclerView(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>,
                                  layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(context),
                                  hasFixedSize: Boolean = true) {
    this.layoutManager = layoutManager
    this.adapter = adapter
    setHasFixedSize(hasFixedSize)
}

fun BaseController.hideKeyboard() {
    val inputMethodManager = this.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.view?.windowToken, 0)
}

fun Activity.hideKeyboard() {
    val inputMethodManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
}

class HeaderViewHolder(headerView: View) : RecyclerView.ViewHolder(headerView) {
    val sectionText = headerView.sectionText
}

const val ITEM_VIEW_TYPE_HEADER = 0
const val ITEM_VIEW_TYPE_ITEM = 1

fun Spinner.init(array: Int) {
    ArrayAdapter.createFromResource(
            this.context, array,
            android.R.layout.simple_spinner_item
    ).also { adapter ->
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.adapter = adapter
    }
}
