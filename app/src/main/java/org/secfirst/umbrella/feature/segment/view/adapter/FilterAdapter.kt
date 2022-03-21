package org.secfirst.umbrella.feature.segment.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import org.secfirst.umbrella.R
import org.secfirst.umbrella.data.database.difficulty.Difficulty

@SuppressLint("SetTextI18n")
class FilterAdapter(context: Context, private val difficulties: List<Difficulty>) :
        ArrayAdapter<Difficulty>(context, android.R.layout.simple_dropdown_item_1line, difficulties) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        return createViewFromDropdown(position, convertView, parent)
    }


    private fun createViewFromResource(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: TextView = convertView as TextView?
                ?: LayoutInflater.from(context).inflate(R.layout.difficulty_spinner_view, parent, false) as TextView
        view.text = "${difficulties[0].subject?.title} ${difficulties[position].title}"
        return view
    }

    private fun createViewFromDropdown(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: TextView = convertView as TextView?
                ?: LayoutInflater.from(context).inflate(android.R.layout.simple_dropdown_item_1line, parent, false) as TextView
        view.text = "${difficulties[0].subject?.title} ${difficulties[position].title}"
        return view
    }
}