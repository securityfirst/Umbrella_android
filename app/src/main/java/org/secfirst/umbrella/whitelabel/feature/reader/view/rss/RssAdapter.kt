package org.secfirst.umbrella.whitelabel.feature.reader.view.rss

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.rss_item_view.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.reader.RSS

class RssAdapter(private val onClickPress: (RSS) -> Unit) : RecyclerView.Adapter<RssAdapter.RssHolder>() {

    private val rssList: MutableList<RSS> = mutableListOf()

    fun removeAt(position: Int) {
        rssList.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    fun getAt(position: Int) = rssList[position]

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RssHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rss_item_view, parent, false)
        return RssHolder(view)
    }

    override fun getItemCount() = rssList.size

    override fun onBindViewHolder(holder: RssHolder, position: Int) {
        holder.bind(rssList[position], clickListener = { onClickPress(rssList[position]) })
    }

    fun addAll(feedList: List<RSS>) {
        feedList.forEach { rssList.add(it) }
        notifyDataSetChanged()
    }

    fun add(rss: RSS) {
        rssList.add(rss)
        notifyDataSetChanged()
    }

    fun getRss(): MutableList<RSS> = rssList

    fun remove(rss: RSS) {
        rssList.remove(rss)
        notifyDataSetChanged()
    }

    class RssHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(rss: RSS, clickListener: (RssHolder) -> Unit) {
            with(rss) {
                itemView.rssTitle.text = title
                itemView.rssDescription.text = description
                itemView.setOnClickListener { clickListener(this@RssHolder) }
            }
        }
    }
}

