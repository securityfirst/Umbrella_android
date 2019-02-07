package org.secfirst.umbrella.whitelabel.feature.reader.view.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.feed_header.view.*
import kotlinx.android.synthetic.main.feed_item_view.view.*
import org.jsoup.Jsoup
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.network.FeedItemResponse
import org.secfirst.umbrella.whitelabel.misc.ITEM_VIEW_TYPE_HEADER
import org.secfirst.umbrella.whitelabel.misc.ITEM_VIEW_TYPE_ITEM
import org.secfirst.umbrella.whitelabel.misc.hostURL
import org.secfirst.umbrella.whitelabel.misc.timestampToStringFormat

class FeedAdapter(private val onClickFeedItem: (String) -> Unit,
                  private val onClickChangeLocation: () -> Unit,
                  private val placeName: String) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var feedItems: List<FeedItemResponse>

    private fun isHeader(position: Int) = position == 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_item_view, parent, false)
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            val headerView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.feed_header, parent, false)
            return FeedHeadHolder(headerView)
        }
        return FeedItemHolder(view)
    }

    override fun getItemViewType(position: Int) = if (isHeader(position)) ITEM_VIEW_TYPE_HEADER else ITEM_VIEW_TYPE_ITEM

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            isHeader(position) -> {
                holder as FeedHeadHolder
                holder.bind(placeName, clickListener = { onClickChangeLocation() })
            }
            else -> {
                holder as FeedItemHolder
                holder.bind(feedItems[position], clickListener = { onClickFeedItem(feedItems[position].url) })
            }
        }
    }

    override fun getItemCount() = feedItems.size

    fun addAll(feedItems: List<FeedItemResponse>) {
        this.feedItems = feedItems
    }

    class FeedHeadHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(title: String, clickListener: (FeedHeadHolder) -> Unit) {
            itemView.feedPlace.text = title
            itemView.feedHeadCard.setOnClickListener { clickListener(this) }
        }
    }

    class FeedItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(feedItem: FeedItemResponse, clickListener: (FeedItemHolder) -> Unit) {
            itemView.feedTitle.text = feedItem.title
            itemView.feedSite.text = feedItem.url.hostURL()
            itemView.feedBody.text = Jsoup.parse(feedItem.description).text()
            itemView.feedDate.text = feedItem.updatedAt.timestampToStringFormat()
            itemView.feedItemCard.setOnClickListener { clickListener(this@FeedItemHolder) }
        }
    }
}