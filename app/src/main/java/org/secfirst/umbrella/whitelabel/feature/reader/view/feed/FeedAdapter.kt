package org.secfirst.umbrella.whitelabel.feature.reader.view.feed

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.feed_item_view.view.*
import org.jsoup.Jsoup
import org.secfirst.umbrella.whitelabel.R
import org.secfirst.umbrella.whitelabel.data.network.FeedItemResponse
import org.secfirst.umbrella.whitelabel.misc.hostURL
import org.secfirst.umbrella.whitelabel.misc.timestampToStringFormat

class FeedAdapter(private val onClickFeedItem: (String?) -> Unit) : RecyclerView.Adapter<FeedAdapter.FeedItemHolder>() {

    private lateinit var feedItems: List<FeedItemResponse>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feed_item_view, parent, false)
        return FeedItemHolder(view)
    }


    override fun onBindViewHolder(holder: FeedItemHolder, position: Int) {
        holder.bind(feedItems[position], clickListener = { onClickFeedItem(feedItems[position].url) })
    }

    override fun getItemCount() = feedItems.size

    fun addAll(feedItems: List<FeedItemResponse>) {
        this.feedItems = feedItems
    }

    class FeedItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(feedItem: FeedItemResponse, clickListener: (FeedItemHolder) -> Unit) {
            with(feedItem) {

                itemView.feedTitle.text = title
                itemView.feedSite.text = url.hostURL()
                itemView.feedBody.text = Jsoup.parse(description).text()
                itemView.feedDate.text = updatedAt.timestampToStringFormat()
                itemView.feedItemCard.setOnClickListener { clickListener(this@FeedItemHolder) }
            }
        }
    }
}