package org.secfirst.umbrella.feature.checklist.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.checklist_item.view.*
import kotlinx.android.synthetic.main.head_section.view.*
import org.secfirst.umbrella.R
import org.secfirst.umbrella.data.database.checklist.Content
import org.secfirst.umbrella.misc.ITEM_VIEW_TYPE_HEADER
import org.secfirst.umbrella.misc.ITEM_VIEW_TYPE_ITEM


class ChecklistAdapter(private val checklistContent: MutableList<Content>,
                       private val onItemChecked: (Content) -> Unit,
                       private val onUpdateProgress: (Int) -> Unit,
                       private val onLongClick: (Int, String) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun add(newContent: Content) {
        checklistContent.add(newContent)
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        checklistContent.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    fun updateItem(item: String, position: Int) {
        checklistContent[position].check = item
        notifyItemChanged(position)
    }

    fun getChecklistItem(position: Int) = checklistContent[position]

    override fun getItemCount() = checklistContent.size

    override fun getItemViewType(position: Int) =
            if (isHeader(position)) ITEM_VIEW_TYPE_HEADER else ITEM_VIEW_TYPE_ITEM

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.checklist_item, parent, false)

        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            val headerView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.checklist_header, parent, false)
            return ChecklistHeaderViewHolder(headerView)
        }

        return ChecklistHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isHeader(position)) {
            holder as ChecklistHeaderViewHolder
            holder.bind(checklistContent[position].label)
        } else {
            holder as ChecklistHolder
            holder.bind(checklistContent[position], checklistContent,
                    onItemChecked = { onItemChecked(checklistContent[position]) },
                    onUpdateChecked = { onUpdateProgress(percentage.toInt()) },
                    onLongClick = { onLongClick(position, checklistContent[position].check) })
        }
    }

    private fun isHeader(position: Int) = checklistContent[position].label.isNotBlank()

    class ChecklistHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(title: String) {
            itemView.sectionText.text = title
        }
    }

    class ChecklistHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(currentContent: Content,
                 list: List<Content>,
                 onItemChecked: (ChecklistHolder) -> Unit,
                 onUpdateChecked: (ChecklistHolder) -> Unit,
                 onLongClick: (ChecklistHolder) -> Unit) {

            itemView.checkItem.isChecked = currentContent.value
            itemView.itemTitle.text = currentContent.check

            itemView.checkItem.setOnClickListener {
                currentContent.value = itemView.checkItem.isChecked
                updateProgress(list.filter { item -> item.label.isEmpty() })
                onItemChecked(this@ChecklistHolder)
                onUpdateChecked(this@ChecklistHolder)
            }
            itemView.itemTitle.setOnLongClickListener {
                onLongClick(this@ChecklistHolder)
                true
            }
            itemView.checklistCardItemView.setOnLongClickListener {
                onLongClick(this@ChecklistHolder)
                true
            }
        }

        private fun updateProgress(list: List<Content>) {
            val ratio = list.filter { it.value }.size
            percentage = Math.ceil(ratio * 100.0 / list.size)
        }
    }

    companion object {
        private var percentage = 0.0
    }
}



