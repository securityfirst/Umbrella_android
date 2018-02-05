package org.secfirst.umbrella.rss.feed;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.rss.RSSFeedService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dougl on 18/01/2018.
 */

public class CustomFeedAdapter extends RecyclerView.Adapter<CustomFeedAdapter.RSSChannel> {

    private final Context mContext;
    private List<CustomFeed> mCustomFeeds;
    private List<CustomFeed> mSelectedFeeds = new ArrayList<>();
    private boolean isMultiSelect = false;


    public CustomFeedAdapter(@NonNull List<CustomFeed> customFeeds, Context context) {
        this.mCustomFeeds = customFeeds;
        this.mContext = context;
    }


    public void add(CustomFeed channel) {
        mCustomFeeds.add(channel);
        notifyItemInserted(mCustomFeeds.size() - 1);
    }

    public void reload() {
        List<CustomFeed> customFeedList = mCustomFeeds;
        clear();
        add(customFeedList);
    }

    public void add(List<CustomFeed> channels) {
        for (CustomFeed customFeed : channels) {
            mCustomFeeds.add(customFeed);
            notifyItemInserted(mCustomFeeds.size() - 1);
        }
    }

    public boolean isEmpty() {
        return false;
    }

    public void remove(CustomFeed feed) {
        int position = mCustomFeeds.indexOf(feed);
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

    public void restoreItem(CustomFeed customFeed, int position) {
        mCustomFeeds.add(position, customFeed);
        // notify item added by position
        notifyItemInserted(position);
    }


    public void cleanSelectedFeeds() {
        mSelectedFeeds = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void addSelectedFeeds(CustomFeed customFeed) {
        mSelectedFeeds.add(customFeed);
        notifyDataSetChanged();
    }

    public List<CustomFeed> getSelectedFeeds() {
        return mSelectedFeeds;
    }

    public List<CustomFeed> getCustomFeeds() {
        return mCustomFeeds;
    }

    public boolean isMultiSelect() {
        return isMultiSelect;
    }

    public void setMultiSelect(boolean multiSelect) {
        isMultiSelect = multiSelect;
    }

    @Override
    public CustomFeedAdapter.RSSChannel onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rss_list_row, parent, false);
        return new RSSChannel(itemView);
    }

    @Override
    public void onBindViewHolder(RSSChannel holder, int position) {
        CustomFeed feed = mCustomFeeds.get(position);
        holder.channelTitle.setText(feed.getTitle());
        holder.channelDescription.setText(feed.getDetail());
        if (mSelectedFeeds.contains(feed)) {
            //if item is selected then,set foreground color of FrameLayout.
            holder.viewForeground.setBackgroundColor(ContextCompat.getColor(mContext, R.color.grey));
        } else {
            //else remove selected item color.
            holder.viewForeground.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        }
    }

    @Override
    public int getItemCount() {
        return mCustomFeeds.size();
    }

    public class RSSChannel extends RecyclerView.ViewHolder {
        public TextView channelTitle;
        public TextView channelDescription;
        public LinearLayout viewForeground;
        public RelativeLayout viewBackground;

        RSSChannel(View view) {
            super(view);

            channelTitle = view.findViewById(R.id.rss_channel_title);
            channelDescription = view.findViewById(R.id.rss_channel_description);
            viewForeground = view.findViewById(R.id.channel_item_view_foreground);
            viewBackground = view.findViewById(R.id.channel_item_view_background);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if (!isMultiSelect) {
                        CustomFeed currentFeed = mCustomFeeds.get(getLayoutPosition());
                        RSSFeedService rssFeedService = new RSSFeedService();
                        rssFeedService.onFinish(new RSSFeedService.RSSEvent() {
                            @Override
                            public void onTaskInProgress() {

                            }

                            @Override
                            public void onTaskCompleted(List<CustomFeed> customFeeds) {
                                if (!customFeeds.isEmpty()) {
                                    openArticleFragment(view, customFeeds);
                                }
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
                }
            });
        }


        private void openArticleFragment(View view, List<CustomFeed> customFeeds) {
            ArticleActivity.mCustomFeed = customFeeds.get(0);
            Intent intent = new Intent(view.getContext(), ArticleActivity.class);
            view.getContext().startActivity(intent);
        }
    }
}
