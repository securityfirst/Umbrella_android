package org.secfirst.umbrella.whitelabel.feature.checklist.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.checklist_item.view.*
import kotlinx.android.synthetic.main.head_section.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.checklist.Content
import org.secfirst.umbrella.whitelabel.misc.ITEM_VIEW_TYPE_HEADER
import org.secfirst.umbrella.whitelabel.misc.ITEM_VIEW_TYPE_ITEM

class ChecklistAdapter(private val checklistContent: List<Content>,
                       private val onItemChecked: (Content) -> Unit,
                       private val onUpdateProgress: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.checklist_item, parent, false)

        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            val headerView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.head_section, parent, false)
            return ChecklistHeaderViewHolder(headerView)
        }

        return ChecklistHolder(view)
    }

    override fun getItemCount() = checklistContent.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isHeader(position)) {
            holder as ChecklistHeaderViewHolder
            holder.bind(checklistContent[position].label)
        } else {
            holder as ChecklistHolder
            holder.bind(checklistContent[position], checklistContent.size,
                    onItemChecked = { onItemChecked(checklistContent[position]) },
                    onUpdateChecked = { onUpdateProgress(percentage) })
        }
    }

    private fun isHeader(position: Int) = checklistContent[position].label.isNotBlank()


    override fun getItemViewType(position: Int): Int {
        return if (isHeader(position)) ITEM_VIEW_TYPE_HEADER else ITEM_VIEW_TYPE_ITEM
    }

    class ChecklistHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(title: String) {
            itemView.sectionText.text = title
        }
    }

    class ChecklistHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(checklistContent: Content,
                 sizeList: Int,
                 onItemChecked: (ChecklistHolder) -> Unit,
                 onUpdateChecked: (ChecklistHolder) -> Unit) {

            with(checklistContent) {
                itemView.checkItem.setOnClickListener {
                    value = itemView.checkItem.isChecked
                    updateProgress(sizeList)
                    onItemChecked(this@ChecklistHolder)
                    onUpdateChecked(this@ChecklistHolder)
                }
            }
            itemView.checkItem.isChecked = checklistContent.value
            itemView.checkItem.text = checklistContent.check
        }

        private fun updateProgress(sizeList: Int) {
            if (itemView.checkItem.isChecked) {
                itemSelected += 1
            } else {
                itemSelected -= 1
            }
            percentage = if (itemSelected == sizeList) 100 else itemSelected * 100 / sizeList
        }
    }

    companion object {
        private var itemSelected = 0
        private var percentage = 0
    }
}



