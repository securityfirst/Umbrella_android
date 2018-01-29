package org.secfirst.umbrella.rss.feed;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.rss.RSSFeedService;

import java.util.List;

/**
 * Created by dougl on 18/01/2018.
 */

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.RSSChannel> {

    private List<CustomFeed> mCustomFeeds;


    public ChannelAdapter(@NonNull List<CustomFeed> customFeeds) {
        this.mCustomFeeds = customFeeds;
    }


    public void add(CustomFeed channel) {
        mCustomFeeds.add(channel);
        notifyItemInserted(mCustomFeeds.size() - 1);
    }

    public boolean isEmpty() {
        return false;
    }

    public void remove(CustomFeed channel) {
        int position = mCustomFeeds.indexOf(channel);
        if (position > -1) {
            mCustomFeeds.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(mCustomFeeds.get(0));
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
        CustomFeed channel = mCustomFeeds.get(position);
        holder.channelTitle.setText(channel.getTitle());
        holder.channelDescription.setText(channel.getDetail());
    }

    @Override
    public int getItemCount() {
        return mCustomFeeds.size();
    }

    public class RSSChannel extends RecyclerView.ViewHolder {
        public TextView channelTitle;
        public TextView channelDescription;

        RSSChannel(View view) {
            super(view);

            channelTitle = view.findViewById(R.id.rss_channel_title);
            channelDescription = view.findViewById(R.id.rss_channel_description);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    CustomFeed currentFeed = mCustomFeeds.get(getLayoutPosition());
                    RSSFeedService rssFeedService = new RSSFeedService();
                    rssFeedService.onFinish(new RSSFeedService.RSSEvent() {
                        @Override
                        public void onTaskInProgress() {

                        }

                        @Override
                        public void onTaskCompleted(CustomFeed feed) {
                            mCustomFeeds.get(getLayoutPosition()).setFeed(feed.getFeed());
                            openArticleFragment(view);

                        }

                        @Override
                        public void onError() {
                            Toast.makeText(view.getContext(),
                                    view.getContext().getString(R.string.error_show_message_custom_feed)
                                    , Toast.LENGTH_SHORT).show();
                        }
                    });
                    rssFeedService.execute(currentFeed.getFeedUrl());

                }
            });
        }


        private void openArticleFragment(View view) {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.rss_container, ArticleFragment.newInstance
                            (mCustomFeeds.get(getLayoutPosition()).getFeed()))
                    .addToBackStack(null)
                    .commit();
        }
    }
}
