package org.secfirst.umbrella.whitelabel.feature.segment.view

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.segment_item.view.*
import org.jetbrains.anko.backgroundColor
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.segment.Segment

class SegmentAdapter(private val onClickSegment: (Segment.Item) -> Unit) : RecyclerView.Adapter<SegmentAdapter.SegmentHolder>() {

    private val segments: MutableList<Segment.Item> = mutableListOf()

    fun add(segmentsItem: List<Segment.Item>) {
        this.segments.clear()
        segmentsItem.forEach { this.segments.add(it) }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SegmentHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.segment_item, parent, false)
        return SegmentHolder(view, segments.size)
    }


    override fun onBindViewHolder(holder: SegmentHolder, position: Int) {
        holder.bind(segments[position], clickListener = { onClickSegment(segments[position]) })
    }

    override fun getItemCount() = segments.size

    class SegmentHolder(itemView: View, var size: Int) : RecyclerView.ViewHolder(itemView) {
        val colours = intArrayOf(R.color.umbrella_purple, R.color.umbrella_green, R.color.umbrella_yellow)

        fun bind(segment: Segment.Item, clickListener: (SegmentHolder) -> Unit) {
            itemView.setOnClickListener { clickListener(this) }
            with(segment) {
                val index = adapterPosition + 1
                itemView.segmentIndex.text = index.toString()
                itemView.segmentDescription.text = title
                itemView.segmentLayout.backgroundColor = ContextCompat.getColor(itemView.context, colours[adapterPosition % 3])
            }
        }
    }
}