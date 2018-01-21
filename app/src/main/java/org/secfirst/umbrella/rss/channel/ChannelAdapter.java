package org.secfirst.umbrella.rss.channel;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.einmalfel.earl.Feed;

import org.secfirst.umbrella.R;

import java.util.List;

/**
 * Created by dougl on 18/01/2018.
 */

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.RSSChannel> {

    private List<Feed> feeds;


    public ChannelAdapter(@NonNull List<Feed> feeds) {
        this.feeds = feeds;
    }


    public void add(Feed channel) {
        feeds.add(channel);
        notifyItemInserted(feeds.size() - 1);
    }

    public boolean isEmpty() {
        return false;
    }

    public void remove(Feed channel) {
        int position = feeds.indexOf(channel);
        if (position > -1) {
            feeds.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(feeds.get(0));
        }
    }

    @Override
    public ChannelAdapter.RSSChannel onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rss_list_row, parent, false);
        return new RSSChannel(itemView);
    }

    @Override
    public void onBindViewHolder(RSSChannel holder, int position) {
        Feed channel = feeds.get(position);
        holder.channelTitle.setText(channel.getTitle());
        holder.channelDescription.setText(channel.getDescription());
    }

    @Override
    public int getItemCount() {
        return feeds.size();
    }

    public class RSSChannel extends RecyclerView.ViewHolder {
        public TextView channelTitle;
        public TextView channelDescription;

        RSSChannel(View view) {
            super(view);
            channelTitle = view.findViewById(R.id.rss_channel_title);
            channelDescription = view.findViewById(R.id.rss_channel_description);
        }
    }
}
