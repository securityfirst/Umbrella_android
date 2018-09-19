package org.secfirst.umbrella.whitelabel.misc

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import kotlinx.android.synthetic.main.head_section.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.UmbrellaApplication
import org.secfirst.umbrella.whitelabel.feature.base.view.BaseController
import org.secfirst.umbrella.whitelabel.feature.main.MainActivity

val TextView.regular: Typeface get() = Typeface.createFromAsset(context.assets, "fonts/Roboto-Regular.ttf")
val TextView.medium: Typeface get() = Typeface.createFromAsset(context.assets, "fonts/Roboto-Medium.ttf")


fun Context.shareLink(link: String) {
    val sendIntent = Intent()
    sendIntent.action = Intent.ACTION_SEND
    sendIntent.putExtra(Intent.EXTRA_TEXT, link)
    sendIntent.type = "text/html"
    this.startActivity(Intent.createChooser(sendIntent, this.resources.getText(R.string.send_to)))
}

fun RecyclerView.initRecyclerView(layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(UmbrellaApplication.instance),
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

class HeaderViewHolder(headerView: View) : RecyclerView.ViewHolder(headerView) {
    val sectionText = headerView.sectionText
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : Parcelable> Bundle.getParcelableMutableList(key: String): MutableList<T> = (getParcelableArray(key) as Array<T>).toMutableList()

inline fun <reified T : Parcelable> Bundle.putParcelableCollection(key: String, value: Collection<T>) = putParcelableArray(key, value.toTypedArray())


