package org.secfirst.umbrella.whitelabel.feature.segment.view.adapter

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.segment_foot.view.*
import kotlinx.android.synthetic.main.segment_item.view.*
import org.jetbrains.anko.backgroundColor
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.database.checklist.Checklist
import org.secfirst.umbrella.whitelabel.data.database.segment.Markdown
import org.secfirst.umbrella.whitelabel.misc.ITEM_VIEW_TYPE_HEADER
import org.secfirst.umbrella.whitelabel.misc.ITEM_VIEW_TYPE_ITEM

class SegmentAdapter(private val onClickSegment: (Int) -> Unit,
                     private val onFootClicked: (Int) -> Unit,
                     private val onChecklistShareClick: () -> Unit,
                     private val onSegmentShareClick: (Markdown) -> Unit,
                     private val onChecklistFavoriteClick: (Boolean) -> Unit,
                     private val onSegmentFavoriteClick: (Markdown) -> Unit,
                     private val checklist: Checklist?,
                     private val markdowns: MutableList<Markdown>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    fun isChecklistFoot(position: Int): Boolean {
        return position == markdowns.size
    }

    fun addAll(markdowns: MutableList<Markdown>) {
        this.markdowns.addAll(markdowns)
        notifyDataSetChanged()
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
        if (isChecklistFoot(position)) {
            holder as FooterHolder
            holder.bind(checklist,
                    footClick = { onFootClicked(position) },
                    checklistFavoriteClick = { onChecklistFavoriteClick(isChecklistFavorite) },
                    checklistShareClick = { onChecklistShareClick() })
        } else {
            holder as SegmentHolder
            holder.bind(markdowns[position], clickListener = { onClickSegment(position) },
                    segmentFavoriteClick = { onSegmentFavoriteClick(markdowns[position]) },
                    segmentShareClick = { onSegmentShareClick(markdowns[position]) })
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isChecklistFoot(position)) ITEM_VIEW_TYPE_HEADER else ITEM_VIEW_TYPE_ITEM
    }

    override fun getItemCount() = markdowns.size + 1

    class FooterHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val colours = intArrayOf(R.color.umbrella_purple,
                R.color.umbrella_green,
                R.color.umbrella_yellow)

        fun bind(checklist: Checklist?,
                 footClick: (FooterHolder) -> Unit,
                 checklistFavoriteClick: (FooterHolder) -> Unit,
                 checklistShareClick: (FooterHolder) -> Unit) {

            if (checklist == null) {
                itemView.visibility = View.GONE
            } else {
                checklist.let {
                    itemView.checklistShare.setOnClickListener { checklistShareClick(this) }
                    itemView.setOnClickListener { footClick(this) }
                    itemView.checklistFavorite.isChecked = it.favorite
                    itemView.footLayout.backgroundColor = ContextCompat.getColor(itemView.context, colours[adapterPosition % 3])
                    itemView.checklistFavorite.setOnClickListener {
                        isChecklistFavorite = itemView.checklistFavorite.isChecked
                        checklistFavoriteClick(this)
                    }
                }
            }
        }
    }

    class SegmentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val colours = intArrayOf(R.color.umbrella_purple,
                R.color.umbrella_green,
                R.color.umbrella_yellow)

        fun bind(markdown: Markdown, clickListener: (SegmentHolder) -> Unit,
                 segmentFavoriteClick: (SegmentHolder) -> Unit,
                 segmentShareClick: (SegmentHolder) -> Unit) {

            itemView.segmentFavorite.isChecked = markdown.favorite
            itemView.segmentFavorite.setOnClickListener {
                markdown.favorite = itemView.segmentFavorite.isChecked
                segmentFavoriteClick(this)
            }
            itemView.setOnClickListener { clickListener(this) }
            itemView.segmentShare.setOnClickListener { segmentShareClick(this) }

            with(markdown) {
                val index = adapterPosition + 1
                itemView.segmentIndex.text = index.toString()
                itemView.segmentDescription.text = title
                itemView.segmentLayout.backgroundColor = ContextCompat.getColor(itemView.context, colours[adapterPosition % 3])
            }
        }
    }
}

private var isChecklistFavorite: Boolean = false