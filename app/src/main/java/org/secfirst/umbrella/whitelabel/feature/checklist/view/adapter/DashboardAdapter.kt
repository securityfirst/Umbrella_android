package org.secfirst.umbrella.whitelabel.feature.checklist.view.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.checklist_dashboard_header.view.*
import kotlinx.android.synthetic.main.checklist_dashboard_item.view.*
import kotlinx.android.synthetic.main.editchecklistdialog.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.Dashboard
import org.secfirst.umbrella.whitelabel.misc.ITEM_VIEW_TYPE_HEADER
import org.secfirst.umbrella.whitelabel.misc.ITEM_VIEW_TYPE_ITEM

@SuppressLint("SetTextI18n")
class DashboardAdapter(private val dashboardItems: MutableList<Dashboard.Item>,
                       private val onDashboardItemClicked: (Checklist?) -> Unit,
                       private val onDashboardItemUpdated: (Checklist) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private fun isHeader(position: Int) = dashboardItems[position].title.isNotBlank()

    override fun getItemCount() = dashboardItems.size

    fun removeAt(position: Int) {
        dashboardItems.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    fun getChecklist(position: Int) = dashboardItems[position].checklist

    override fun getItemViewType(position: Int) = if (isHeader(position)) ITEM_VIEW_TYPE_HEADER else ITEM_VIEW_TYPE_ITEM

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.checklist_dashboard_item, parent, false)

        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            val headerView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.checklist_dashboard_header, parent, false)
            return DashboardHeaderViewHolder(headerView)
        }
        return DashboardHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isHeader(position)) {
            holder as DashboardHeaderViewHolder
            holder.bind(dashboardItems[position].title)
        } else {
            holder as DashboardHolder
            holder.bind(dashboardItems[position], clickListener = { onDashboardItemClicked(dashboardItems[position].checklist) },
                    longClickListener = { onDashboardItemUpdated(dashboardItems[position].checklist!!) })
        }
    }

    class DashboardHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(title: String) {
            itemView.headChecklistDashboard.text = title
        }
    }

    class DashboardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(dashboardItem: Dashboard.Item, clickListener: (DashboardHolder) -> Unit,
                 longClickListener: (DashboardHolder) -> Unit) {

            with(dashboardItem) {
                itemView.itemLabel.text = if (difficulty?.title != null) {
                    label + " - " + difficulty?.title
                } else label
                itemView.itemPercentage.text = "$progress%"
                itemView.setOnClickListener { clickListener(this@DashboardHolder) }
                editItem(dashboardItem, longClickListener)
            }
        }

        private fun editItem(dashboardItem: Dashboard.Item, longClickListener: (DashboardHolder) -> Unit) {
            with(dashboardItem) {
                checklist?.custom?.let {
                    itemView.setOnLongClickListener {
                        val promptsView = LayoutInflater.from(itemView.context).inflate(R.layout.editchecklistdialog, null)
                        val alertDialogBuilder = AlertDialog.Builder(itemView.context)
                        val userInput = promptsView.editChecklistItem
                        alertDialogBuilder
                                .setCancelable(false)
                                .setPositiveButton(R.string.export_dialog_ok
                                ) { _, _ ->
                                    itemView.itemLabel.text = userInput.text.toString()
                                    checklist?.title = userInput.text.toString()
                                    longClickListener(this@DashboardHolder)

                                }
                                .setNegativeButton(R.string.export_dialog_cancel
                                ) { dialog, _ -> dialog.cancel() }

                        alertDialogBuilder
                                .create()
                                .setView(promptsView)

                        alertDialogBuilder.show()
                        true
                    }
                }
            }
        }
    }
}
