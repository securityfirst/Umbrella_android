package org.secfirst.umbrella.whitelabel.feature.checklist.view.adapter

import android.app.Dialog
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.alert_control.view.*
import kotlinx.android.synthetic.main.checklist_add_item_dialog.view.*
import kotlinx.android.synthetic.main.checklist_item.view.*
import kotlinx.android.synthetic.main.head_section.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.checklist.Content
import org.secfirst.umbrella.whitelabel.misc.ITEM_VIEW_TYPE_HEADER
import org.secfirst.umbrella.whitelabel.misc.ITEM_VIEW_TYPE_ITEM


class ChecklistAdapter(private val checklistContent: MutableList<Content>,
                       private val onItemChecked: (Content) -> Unit,
                       private val onUpdateProgress: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    fun add(newContent: Content) {
        checklistContent.add(newContent)
        notifyDataSetChanged()
    }

    fun removeAt(position: Int) {
        checklistContent.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    fun update(checklistItem: Content, position: Int) {
        checklistContent[position] = checklistItem
        notifyItemChanged(position)
        notifyDataSetChanged()
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
                    .inflate(R.layout.head_checklist, parent, false)
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
                    onUpdateChecked = { onUpdateProgress(percentage.toInt()) })
        }
    }

    private fun isHeader(position: Int) = checklistContent[position].label.isNotBlank()

    class ChecklistHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(title: String) {
            itemView.sectionText.text = title
        }
    }

    class ChecklistHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private var editView: View
        private lateinit var editDialog: Dialog

        init {
            val inflater = LayoutInflater.from(itemView.context)
            editView = inflater.inflate(R.layout.checklist_add_item_dialog, null)
            editView.title.text = itemView.context.getString(R.string.checklist_edit_item_title)
        }

        fun bind(currentContent: Content,
                 list: List<Content>,
                 onItemChecked: (ChecklistHolder) -> Unit,
                 onUpdateChecked: (ChecklistHolder) -> Unit) {

            itemView.checkItem.isChecked = currentContent.value
            itemView.itemTitle.text = currentContent.check

            itemView.checkItem.setOnClickListener {
                currentContent.value = itemView.checkItem.isChecked
                updateProgress(list.filter { item -> item.label.isEmpty() })
                onItemChecked(this@ChecklistHolder)
                onUpdateChecked(this@ChecklistHolder)
            }
            createEditItemAlert(currentContent, onItemChecked)
            itemView.itemTitle.setOnLongClickListener {
                editDialog.show()
                true
            }
            itemView.checklistCardItemView.setOnLongClickListener {
                editDialog.show()
                true
            }
        }

        private fun createEditItemAlert(currentContent: Content, onItemChecked: (ChecklistHolder) -> Unit) {
            val context = itemView.context
            editDialog = AlertDialog.Builder(context)
                    .setView(editView)
                    .create()
            editView.alertControlOk.setOnClickListener { editContent(currentContent, onItemChecked) }
            editView.alertControlCancel.setOnClickListener { editDialog.dismiss() }
        }

        private fun editContent(currentContent: Content, onItemChecked: (ChecklistHolder) -> Unit) {
            itemView.checkItem.text = editView.editChecklistItem.text.toString()
            currentContent.check = editView.editChecklistItem.text.toString()
            onItemChecked(this@ChecklistHolder)
            editDialog.dismiss()
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



