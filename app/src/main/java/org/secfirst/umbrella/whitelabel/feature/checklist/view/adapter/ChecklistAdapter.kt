package org.secfirst.umbrella.whitelabel.feature.checklist.view.adapter

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.checklist_action_dialog.view.*
import kotlinx.android.synthetic.main.checklist_item.view.*
import kotlinx.android.synthetic.main.head_section.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.checklist.Content
import org.secfirst.umbrella.whitelabel.misc.ITEM_VIEW_TYPE_HEADER
import org.secfirst.umbrella.whitelabel.misc.ITEM_VIEW_TYPE_ITEM

class ChecklistAdapter(private val checklistContent: MutableList<Content>,
                       private val viewDialog: View,
                       private val onItemChecked: (Content) -> Unit,
                       private val onUpdateProgress: (Int) -> Unit,
                       private val onLongPress: () -> Unit,
                       private val onDeleteAction: (Content) -> Unit,
                       private val onDisableAction: (Content) -> Unit,
                       private val onCancelAction: () -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    fun removeAt(position: Int) {
        checklistContent.removeAt(position)
        notifyItemRemoved(position)
    }

    fun remove(checklistItem: Content) {
        checklistContent.remove(checklistItem)
        notifyDataSetChanged()
    }

    fun update(checklistItem: Content, position: Int) {
        checklistContent[position] = checklistItem
        notifyItemChanged(position)
        notifyDataSetChanged()
    }

    override fun getItemCount() = checklistContent.size

    override fun getItemViewType(position: Int) = if (isHeader(position)) ITEM_VIEW_TYPE_HEADER else ITEM_VIEW_TYPE_ITEM

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
            holder.bind(checklistContent[position], checklistContent, viewDialog,
                    onLongPress = { onLongPress() },
                    onCancelAction = { onCancelAction() },
                    onDeleteAction = { onDeleteAction(checklistContent[position]) },
                    onDisableAction = { onDisableAction(checklistContent[position]) },
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

        private lateinit var popupView: View

        fun bind(currentContent: Content,
                 list: List<Content>,
                 viewDialog: View,
                 onLongPress: (ChecklistHolder) -> Unit,
                 onCancelAction: (ChecklistHolder) -> Unit,
                 onDeleteAction: (ChecklistHolder) -> Unit,
                 onDisableAction: (ChecklistHolder) -> Unit,
                 onItemChecked: (ChecklistHolder) -> Unit,
                 onUpdateChecked: (ChecklistHolder) -> Unit) {

            itemView.checkItem.isChecked = currentContent.value
            itemView.checkItem.text = currentContent.check
            popupView = viewDialog

            popupView.checklistDialogCancel.setOnClickListener { onCancel(onCancelAction) }
            popupView.checklistDialogDelete.setOnClickListener { onDelete(onDeleteAction) }
            popupView.checklistDialogDisable.setOnClickListener { onDisable(list, onDisableAction) }

            with(currentContent) {
                disableColorChecklistItemByDefault(list)
                itemView.checkItem.setOnClickListener {
                    value = itemView.checkItem.isChecked
                    updateProgress(list.filter { item -> item.label.isEmpty() })
                    onItemChecked(this@ChecklistHolder)
                    onUpdateChecked(this@ChecklistHolder)
                }
                itemView.checkItem.setOnLongClickListener {
                    onLongPress(this@ChecklistHolder)
                    return@setOnLongClickListener true
                }
            }
        }

        private fun onDisable(list: List<Content>, onDisableAction: (ChecklistHolder) -> Unit) {
            onDisableAction(this)
            isDisableColorChecklistItem(list)
        }

        private fun onDelete(onDeleteAction: (ChecklistHolder) -> Unit) {
            onDeleteAction(this)
        }

        private fun onCancel(onCancelAction: (ChecklistHolder) -> Unit) {
            onCancelAction(this)
        }

        private fun disableColorChecklistItemByDefault(content: List<Content>) {
            if (content[adapterPosition].disable) {
                itemView.checkItem.isEnabled = false
                itemView.cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.ms_white_54_opacity))
            }
        }

        private fun isDisableColorChecklistItem(list: List<Content>): Content {
            val content = list[adapterPosition]
            if (content.disable) {
                content.disable = false
                itemView.checkItem.isEnabled = true
                itemView.cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.green_button))
            } else {
                itemView.checkItem.isEnabled = false
                content.disable = true
                itemView.cardView.setCardBackgroundColor(ContextCompat.getColor(itemView.context, R.color.umbrella_purple))
            }
            return content
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



