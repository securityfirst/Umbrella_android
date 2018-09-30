package org.secfirst.umbrella.whitelabel.feature.segment.view

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.segment_item.view.*
import org.jetbrains.anko.backgroundColor
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown

class SegmentAdapter(private val onClickSegment: (Markdown) -> Unit,
                     private val onFootSegment: () -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val markdowns: MutableList<Markdown> = mutableListOf()

    fun add(segmentsItem: List<Markdown>) {
        this.markdowns.clear()
        segmentsItem.forEach { this.markdowns.add(it) }
        notifyDataSetChanged()
    }

    fun isHeader(position: Int): Boolean {
        return position == markdowns.size
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.segment_item, parent, false)
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            val headerView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.segment_foot, parent, false)
            return FooterHolder(headerView)
        }
        return SegmentHolder(view)
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (!isHeader(position)) {
            holder as SegmentHolder
            holder.bind(markdowns[position], clickListener = { onClickSegment(markdowns[position]) })
        } else {
            holder as FooterHolder
            holder.bind(clickListener = { onFootSegment() })
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isHeader(position)) ITEM_VIEW_TYPE_HEADER else ITEM_VIEW_TYPE_ITEM
    }

    override fun getItemCount() = markdowns.size + 1


    class FooterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(clickListener: (FooterHolder) -> Unit) {
            itemView.setOnClickListener { clickListener(this) }
        }
    }

    class SegmentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val colours = intArrayOf(R.color.umbrella_purple, R.color.umbrella_green, R.color.umbrella_yellow)

        fun bind(markdown: Markdown, clickListener: (SegmentHolder) -> Unit) {
            itemView.setOnClickListener { clickListener(this) }
            with(markdown) {
                val index = adapterPosition + 1
                itemView.segmentIndex.text = index.toString()
                itemView.segmentDescription.text = title
                itemView.segmentLayout.backgroundColor = ContextCompat.getColor(itemView.context, colours[adapterPosition % 3])
            }
        }
    }

    companion object {
        private const val ITEM_VIEW_TYPE_HEADER = 0
        private const val ITEM_VIEW_TYPE_ITEM = 1
    }
}
