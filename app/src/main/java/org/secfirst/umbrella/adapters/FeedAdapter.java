package org.secfirst.umbrella.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.models.FeedItem;
import org.secfirst.umbrella.util.Global;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FeedAdapter extends BaseAdapter {

    ArrayList<FeedItem> feedItems  = new ArrayList<>();
    Context mContext;

    public FeedAdapter(Context context, ArrayList<FeedItem> mItems) {
        if (mItems!=null) {
            this.feedItems = mItems;
        }
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
            holder.date = (TextView) convertView.findViewById(R.id.feed_date);
            holder.card = (CardView) convertView.findViewById(R.id.card_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(current.getTitle());
        holder.body.setText(current.getBody());

        if (current.getDate()>0) {
            holder.date.setText(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(new Date(current.getDate() * 1000)));
        }

        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Patterns.WEB_URL.matcher(current.getUrl()).matches()) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(current.getUrl()));
                    mContext.startActivity(i);
                }
            }
        });
        return convertView;
    }

    public void updateData(ArrayList<FeedItem> updateItems) {
        if (updateItems!=null) {
            feedItems = updateItems;
        }
        notifyDataSetChanged();
    }

    public void updateData() {
        Global global = (Global) mContext.getApplicationContext();
        feedItems = global.getFeedItems();
        notifyDataSetChanged();
    }


    private static class ViewHolder {
        public TextView title;
        public TextView body;
        public TextView date;
        public CardView card;
    }
}
