package org.secfirst.umbrella.whitelabel.feature.checklist.view.adapter

import android.app.AlertDialog
import android.content.DialogInterface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import kotlinx.android.synthetic.main.checklist_item.view.*
import kotlinx.android.synthetic.main.head_section.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.checklist.Content
import org.secfirst.umbrella.whitelabel.misc.ITEM_VIEW_TYPE_HEADER
import org.secfirst.umbrella.whitelabel.misc.ITEM_VIEW_TYPE_ITEM


class ChecklistAdapter(private val checklistContent: MutableList<Content>,
                       private val onItemChecked: (Content) -> Unit,
                       private val onUpdateProgress: (Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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

        private lateinit var popupView: View

        fun bind(currentContent: Content,
                 list: List<Content>,
                 onItemChecked: (ChecklistHolder) -> Unit,
                 onUpdateChecked: (ChecklistHolder) -> Unit) {

            itemView.checkItem.isChecked = currentContent.value
            itemView.checkItem.text = currentContent.check


            with(currentContent) {
                itemView.checkItem.setOnClickListener {
                    value = itemView.checkItem.isChecked
                    updateProgress(list.filter { item -> item.label.isEmpty() })
                    onItemChecked(this@ChecklistHolder)
                    onUpdateChecked(this@ChecklistHolder)
                }

                //Edit checklist item

                itemView.cardView.setOnLongClickListener {

                    val li = LayoutInflater.from(itemView.context)
                    val promptsView = li.inflate(R.layout.editchecklistdialog, null)

                    val alertDialogBuilder = AlertDialog.Builder(itemView.context)

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView)

                    val userInput = promptsView
                            .findViewById(R.id.editChecklistItem) as EditText

                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton(R.string.export_dialog_ok,
                                    DialogInterface.OnClickListener { _, _ ->
                                        itemView.checkItem.text = userInput.text.toString()
                                        currentContent.check = userInput.text.toString()
                                        onItemChecked(this@ChecklistHolder)
                                    })
                            .setNegativeButton(R.string.export_dialog_cancel,
                                    DialogInterface.OnClickListener { dialog, _ -> dialog.cancel() })

                    // create alert dialog
                    val alertDialog = alertDialogBuilder.create()

                    // show it
                    alertDialog.show()
                    true
                    
                }
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



