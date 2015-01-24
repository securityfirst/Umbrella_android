package org.secfirst.umbrella.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.models.FeedItem;

import java.util.ArrayList;

public class DashFeedAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<FeedItem> feedItems;

    public DashFeedAdapter(Context context, ArrayList<FeedItem> feedItems) {
        mContext = context;
        this.feedItems = feedItems;
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int position) {
        return feedItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final FeedItem current = feedItems.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.feed_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.feed_title);
            holder.heading = (TextView) convertView.findViewById(R.id.feed_subtitle);
            holder.body = (TextView) convertView.findViewById(R.id.feed_body);
            holder.card = (CardView) convertView.findViewById(R.id.card_view);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(current.getTitle());
        holder.heading.setText(current.getHeading());
        holder.body.setText(current.getBody());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(current.getUrl()));
                mContext.startActivity(i);
            }
        });

        return convertView;
    }

    private static class ViewHolder {
        public TextView title;
        public TextView heading;
        public TextView body;
        public CardView card;
    }
}
