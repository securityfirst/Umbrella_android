package org.secfirst.umbrella.whitelabel.feature.segment.view

import android.content.Context
import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import org.secfirst.umbrella.whitelabel.data.database.segment.Segment


class DifficultSpinnerAdapter(context: Context,
                              @LayoutRes private val layoutResource: Int,
                              private val displayNames: List<Segment>) :
        ArrayAdapter<Segment>(context, layoutResource, displayNames) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createViewFromResource(position, convertView, parent)
    }

    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: TextView = convertView as TextView?
                ?: LayoutInflater.from(context).inflate(layoutResource, parent, false) as TextView
        view.text = displayNames[position].toolbarTitle
        return view
    }
}