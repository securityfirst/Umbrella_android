package org.secfirst.umbrella.whitelabel.feature.checklist.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.checklist_dashboard_header.view.*
import kotlinx.android.synthetic.main.checklist_dashboard_item.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.checklist.Dashboard
import org.secfirst.umbrella.whitelabel.data.database.difficulty.Difficulty
import org.secfirst.umbrella.whitelabel.misc.ITEM_VIEW_TYPE_HEADER
import org.secfirst.umbrella.whitelabel.misc.ITEM_VIEW_TYPE_ITEM
import org.secfirst.umbrella.whitelabel.misc.appContext

@SuppressLint("SetTextI18n")
class DashboardAdapter(private val dashboardItems: MutableList<Dashboard.Item>,
                       private val onDashboardItemClicked: (Checklist) -> Unit,
                       private val onDashboardItemLongClicked: (Checklist, Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


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
            holder.bind(dashboardItems[position], clickListener = { onDashboardItemClicked(dashboardItems[position].checklist!!) },
                    longClickListener = { onDashboardItemLongClicked(dashboardItems[position].checklist!!, position) })
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
                itemView.itemLabel.text = label
                itemView.itemPercentage.text = "$progress%"
                setDifficultyColor(dashboardItem.levelLabel)
                if (adapterPosition != 1)
                    itemView.setOnClickListener { clickListener(this@DashboardHolder) }

                itemView.setOnLongClickListener {
                    longClickListener(this@DashboardHolder)
                    true
                }
            }
        }

        private fun setDifficultyColor(level: Int) {
            when (level) {
                Difficulty.BEGINNER -> itemView.levelColor
                        .setBackgroundColor(ContextCompat.getColor(appContext(), R.color.umbrella_green))
                Difficulty.ADVANCED -> itemView.levelColor
                        .setBackgroundColor(ContextCompat.getColor(appContext(), R.color.umbrella_yellow))
                Difficulty.EXPERT -> itemView.levelColor
                        .setBackgroundColor(ContextCompat.getColor(appContext(), R.color.umbrella_purple))
            }
        }
    }
}
