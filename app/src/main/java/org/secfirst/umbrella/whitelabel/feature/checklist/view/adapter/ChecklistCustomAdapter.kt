package org.secfirst.umbrella.whitelabel.feature.checklist.view.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.checklist_custom_item.view.*
import org.secfirst.umbrella.whitelabel.R


class ChecklistCustomAdapter : RecyclerView.Adapter<ChecklistCustomAdapter.ChecklistHolder>() {

    private var checklistItems = mutableListOf<String>()


    fun removeAt(position: Int) {
        checklistItems.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    fun add(value: String) {
        val position = if (checklistItems.size > 0) checklistItems.size else 0
        checklistItems.add(position, value)
        notifyItemInserted(position)
        notifyDataSetChanged()
    }

    fun getChecklistItems() = checklistItems

    fun size() = checklistItems.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChecklistHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.checklist_custom_item, parent, false)
        return ChecklistHolder(view)
    }

    override fun getItemCount() = checklistItems.size

    override fun onBindViewHolder(holder: ChecklistHolder, position: Int) {
        if (checklistItems.size < 0)
            holder.bind("", checklistItems)
        else
            holder.bind(checklistItems[position], checklistItems)
    }

    class ChecklistHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(value: String, checklistItems: MutableList<String>) {
            itemView.checkCustomItem.setText(value)
            updateChecklistItem(checklistItems)
        }

        private fun updateChecklistItem(checklistItems: MutableList<String>) {
            itemView.checkCustomItem.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {}

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(newText: CharSequence?, start: Int, before: Int, count: Int) {
                    checklistItems[adapterPosition] = newText.toString()
                }
            })
        }
    }
}