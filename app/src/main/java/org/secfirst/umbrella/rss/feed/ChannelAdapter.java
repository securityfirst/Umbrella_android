package org.secfirst.umbrella.rss.feed;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.rss.api.Channel;

import java.util.List;

/**
 * Created by dougl on 18/01/2018.
 */

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.RSSChannel> {

    private List<Channel> channels;


    public ChannelAdapter(@NonNull List<Channel> channels) {
        this.channels = channels;
    }


    public void add(Channel channel) {
        channels.add(channel);
        notifyItemInserted(channels.size() - 1);
    }

    public boolean isEmpty() {
        return false;
    }

    public void remove(Channel channel) {
        int position = channels.indexOf(channel);
        if (position > -1) {
            channels.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(channels.get(0));
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
        Channel channel = channels.get(position);
        holder.channelTitle.setText(channel.getTitle());
        holder.channelDescription.setText(channel.getDescription());
    }

    @Override
    public int getItemCount() {
        return channels.size();
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
