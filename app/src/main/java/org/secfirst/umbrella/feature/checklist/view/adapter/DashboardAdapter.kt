package org.secfirst.umbrella.feature.checklist.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.checklist_dashboard_footer.view.*
import kotlinx.android.synthetic.main.checklist_dashboard_header.view.*
import kotlinx.android.synthetic.main.checklist_dashboard_item.view.*
import org.jetbrains.anko.backgroundDrawable
import org.secfirst.umbrella.R
import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.checklist.Dashboard
import org.secfirst.umbrella.data.database.difficulty.Difficulty
import org.secfirst.umbrella.misc.ITEM_VIEW_TYPE_FOOTER
import org.secfirst.umbrella.misc.ITEM_VIEW_TYPE_HEADER
import org.secfirst.umbrella.misc.ITEM_VIEW_TYPE_ITEM
import org.secfirst.umbrella.misc.appContext

@SuppressLint("SetTextI18n")
class DashboardAdapter(private val dashboardItems: MutableList<Dashboard.Item>,
                       private val onDashboardItemClicked: (Checklist) -> Unit,
                       private val onChecklistShareClick: (Checklist) -> Unit,
                       private val onStarClick: (Checklist, Int) -> Unit,
                       private val onFooterClick: () -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private fun isHeader(position: Int) = dashboardItems[position].title.isNotBlank() && !dashboardItems[position].footer

    private fun isFooter(position: Int) = dashboardItems[position].title.isNotBlank() && dashboardItems[position].footer

    override fun getItemCount() = dashboardItems.size

    fun removeAt(position: Int) {
        dashboardItems.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    fun getChecklist(position: Int) = dashboardItems[position].checklist

    override fun getItemViewType(position: Int): Int {
        when (true) {
            isHeader(position) -> return ITEM_VIEW_TYPE_HEADER
            isFooter(position) -> return ITEM_VIEW_TYPE_FOOTER
            else -> return ITEM_VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.checklist_dashboard_item, parent, false)

        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            val headerView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.checklist_dashboard_header, parent, false)
            return DashboardHeaderViewHolder(headerView)
        }
        if (viewType == ITEM_VIEW_TYPE_FOOTER) {
            val footerView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.checklist_dashboard_footer, parent, false)
            return DashboardFooterViewHolder(footerView)
        }
        return DashboardHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (true) {
            isHeader(position) -> {
                holder as DashboardHeaderViewHolder
                holder.bind(dashboardItems[position].title)
            }
            isFooter(position) -> {
                holder as DashboardFooterViewHolder
                holder.bind(dashboardItems[position].title, footerListener = { onFooterClick() })
            }
            else -> {
                holder as DashboardHolder
                holder.bind(dashboardItems[position], clickListener = { onDashboardItemClicked(dashboardItems[position].checklist!!) },
                        shareListener = { onChecklistShareClick(dashboardItems[position].checklist!!) },
                        starListener = { onStarClick(dashboardItems[position].checklist!!, position) })
            }
        }
    }

    class DashboardHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(title: String) {
            itemView.headChecklistDashboard.text = title
        }
    }

    class DashboardFooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(title: String, footerListener: (DashboardFooterViewHolder) -> Unit) {
            itemView.footerChecklistDashboard.text = title
            itemView.setOnClickListener { footerListener(this@DashboardFooterViewHolder) }
        }
    }

    class DashboardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(dashboardItem: Dashboard.Item, clickListener: (DashboardHolder) -> Unit, shareListener: (DashboardHolder) -> Unit, starListener: (DashboardHolder) -> Unit) {

            with(dashboardItem) {
                itemView.itemLabel.text = label
                val pathways = dashboardItem.checklist?.pathways ?: false
                if (pathways) {
                    itemView.itemPercentage.visibility = View.INVISIBLE
                    itemView.starPathways.visibility = View.VISIBLE
                    itemView.starPathways.setOnClickListener { starListener(this@DashboardHolder) }
                } else
                    itemView.itemPercentage.text = "$progress%"
                val isCustomChecklist = dashboardItem.checklist?.custom ?: false
                setDifficultyColor(dashboardItem.levelLabel, isCustomChecklist)
                if (this.label.contains("Total Done"))
                    itemView.levelColor
                            .backgroundDrawable = ContextCompat.getDrawable(appContext(), R.drawable.ic_total_done)
                else {
                    itemView.checklistShare.visibility = View.VISIBLE
                    itemView.setOnClickListener { clickListener(this@DashboardHolder) }
                    itemView.checklistShare.setOnClickListener { shareListener(this@DashboardHolder) }

                }
            }
        }

        private fun setDifficultyColor(level: Int, isCustomChecklist: Boolean) {
            when (level) {
                Difficulty.BEGINNER -> itemView.levelColor.backgroundDrawable =
                        ContextCompat.getDrawable(appContext(), R.drawable.ic_beginner)

                Difficulty.ADVANCED -> itemView.levelColor
                        .backgroundDrawable = ContextCompat.getDrawable(appContext(), R.drawable.ic_intermediate)

                Difficulty.EXPERT -> itemView.levelColor
                        .backgroundDrawable = ContextCompat.getDrawable(appContext(), R.drawable.ic_expert)

                else -> if (isCustomChecklist) itemView.levelColor.visibility = View.GONE
            }
        }
    }
}
