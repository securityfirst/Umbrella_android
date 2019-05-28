package org.secfirst.umbrella.feature.checklist.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.pathways_item.view.*
import org.secfirst.umbrella.R
import org.secfirst.umbrella.data.database.checklist.Checklist
import org.secfirst.umbrella.data.database.checklist.Dashboard
import org.secfirst.umbrella.misc.ITEM_VIEW_TYPE_ITEM

class PathwaysAdapter(private val dashboardItems: MutableList<Dashboard.Item>,
                      private val onDashboardItemClicked: (Checklist) -> Unit,
                      private val onChecklistStarClick: (Checklist, Int) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun getItemCount() = dashboardItems.size

    fun removeAt(position: Int) {
        dashboardItems.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    fun getPathways(position: Int) = dashboardItems[position].checklist

    override fun getItemViewType(position: Int) = ITEM_VIEW_TYPE_ITEM

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.pathways_item, parent, false)
        return PathwaysHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as PathwaysHolder
        holder.bind(dashboardItems[position], clickListener = { onDashboardItemClicked(dashboardItems[position].checklist!!) },
                starListener = { onChecklistStarClick(dashboardItems[position].checklist!!, position) })

    }

    class PathwaysHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(dashboardItem: Dashboard.Item, clickListener: (PathwaysHolder) -> Unit, starListener: (PathwaysHolder) -> Unit) {
            with(dashboardItem) {
                if (dashboardItem.checklist!!.favorite)
                    itemView.star.setImageResource(R.drawable.ic_share)
                else
                    itemView.star.setImageResource(R.drawable.ic_alarm)
                itemView.pathwaysLabel.text = label
                itemView.setOnClickListener { clickListener(this@PathwaysHolder) }
                itemView.pathwaysStar.setOnClickListener { starListener(this@PathwaysHolder) }
            }
        }

    }
}