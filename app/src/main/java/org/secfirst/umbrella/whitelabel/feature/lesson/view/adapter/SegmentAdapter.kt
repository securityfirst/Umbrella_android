package org.secfirst.umbrella.whitelabel.feature.lesson.view.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.segment_item.view.*
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.lesson.Segment

class SegmentAdapter(private val onClickSegment: (Segment) -> Unit) : RecyclerView.Adapter<SegmentAdapter.SegmentHolder>() {

    private val segments = mutableListOf<Segment>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SegmentHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.segment_item, parent, false)
        return SegmentHolder(view)
    }


    override fun onBindViewHolder(holder: SegmentHolder, position: Int) {
        holder.bind(segments[position], clickListener = { onClickSegment(segments[position]) })
    }

    override fun getItemCount() = segments.size

    fun addAll(segments: List<Segment>) {
        segments.forEach { this.segments.add(it) }
        notifyDataSetChanged()
    }

    class SegmentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(segment: Segment, clickListener: (SegmentHolder) -> Unit) {
            itemView.setOnClickListener { clickListener(this) }
            with(segment) {
                itemView.segmentIndex.text = adapterPosition.toString()
                itemView.segmentDescription.text = title
                //itemView.segmentLayout.backgroundColor = Color.parseColor(layoutColor)
            }
        }
    }
}