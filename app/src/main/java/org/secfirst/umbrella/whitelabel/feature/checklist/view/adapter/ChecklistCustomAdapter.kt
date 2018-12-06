package org.secfirst.umbrella.whitelabel.feature.checklist.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.checklist_custom_item.view.*
import org.secfirst.umbrella.whitelabel.R


class ChecklistCustomAdapter : RecyclerView.Adapter<ChecklistCustomAdapter.ChecklistHolder>() {

    private var checklistItems = mutableListOf<String>()

    fun add(value: String) {
        val position = if (checklistItems.size > 0) checklistItems.size else 0
        checklistItems.add(position, value)
        notifyItemInserted(position)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.checklist_custom_item, parent, false)
        return ChecklistHolder(view)
    }

    override fun getItemCount() = checklistItems.size

    override fun onBindViewHolder(holder: ChecklistHolder, position: Int) {
        if (checklistItems.size < 0)
            holder.bind("")
        else
            holder.bind(checklistItems[position])
    }

    class ChecklistHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: String) {
            itemView.checkCustomItem.text = value
        }
    }
}