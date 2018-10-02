package org.secfirst.umbrella.whitelabel.feature.segment.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.segment.Segment


class DifficultSpinnerAdapter(context: Context,
                              private val displayNames: List<Segment>) :
        ArrayAdapter<Segment>(context, android.R.layout.simple_dropdown_item_1line, displayNames) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createViewFromDropdown(position, convertView, parent)
    }

    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: TextView = convertView as TextView?
                ?: LayoutInflater.from(context).inflate(R.layout.difficulty_spinner_view, parent, false) as TextView
        view.text = displayNames[position].toolbarTitle
        return view
    }

    private fun createViewFromDropdown(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: TextView = convertView as TextView?
                ?: LayoutInflater.from(context).inflate(android.R.layout.simple_dropdown_item_1line, parent, false) as TextView
        view.text = displayNames[position].toolbarTitle
        return view
    }


}