package org.secfirst.umbrella.whitelabel.feature.checklist.view.adapter

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.checklist_dashboard_header.view.*
import kotlinx.android.synthetic.main.checklist_dashboard_item.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.Dashboard
import org.secfirst.umbrella.whitelabel.misc.ITEM_VIEW_TYPE_HEADER
import org.secfirst.umbrella.whitelabel.misc.ITEM_VIEW_TYPE_ITEM

@SuppressLint("SetTextI18n")
class DashboardAdapter(private val dashboardItems: List<Dashboard.Item>,
                       private val onDashboardItemClicked: (Checklist?) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private fun isHeader(position: Int) = dashboardItems[position].title.isNotBlank()

    override fun getItemCount() = dashboardItems.size

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
            holder.bind(dashboardItems[position], clickListener = { onDashboardItemClicked(dashboardItems[position].checklist) })
        }
    }

    class DashboardHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(title: String) {
            itemView.headChecklistDashboard.text = title
        }
    }

    class DashboardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(dashboardItem: Dashboard.Item, clickListener: (DashboardHolder) -> Unit) {
            with(dashboardItem) {
                itemView.itemLabel.text = label
                itemView.itemPercentage.text = "$progress%"
                itemView.setOnClickListener { clickListener(this@DashboardHolder) }
            }
        }
    }
}