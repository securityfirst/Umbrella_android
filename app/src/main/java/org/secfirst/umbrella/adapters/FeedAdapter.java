package org.secfirst.umbrella.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.models.FeedItem;

import java.util.ArrayList;

public class FeedAdapter extends BaseAdapter {

    ArrayList<FeedItem> feedItems;
    Context mContext;

    public FeedAdapter(Context context, ArrayList<FeedItem> mItems) {
        this.feedItems = mItems;
        this.mContext = context;
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
            holder.body = (TextView) convertView.findViewById(R.id.feed_body);
            holder.card = (CardView) convertView.findViewById(R.id.card_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(current.getTitle());
        holder.body.setText(current.getUrl());
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Link somewhere", Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        public TextView title;
        public TextView body;
        public CardView card;
    }
}
