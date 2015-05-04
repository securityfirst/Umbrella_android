package org.secfirst.umbrella.adapters;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.models.DashCheckFinished;

import java.util.ArrayList;

public class DashCheckListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<DashCheckFinished> checkItems;

    public DashCheckListAdapter(Context context, ArrayList<DashCheckFinished> checkItems) {
        this.checkItems = checkItems;
        mContext = context;
    }

    @Override
    public int getCount() {
        return checkItems.size();
    }

    @Override
    public Object getItem(int position) {
        return checkItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final DashCheckFinished current = checkItems.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.dash_check_item, null);
            holder = new ViewHolder();
            holder.checkHeader = (TextView) convertView.findViewById(R.id.check_header);
            holder.icon = (ImageView) convertView.findViewById(R.id.check_icon);
            holder.categoryName = (TextView) convertView.findViewById(R.id.check_category);
            holder.percent = (TextView) convertView.findViewById(R.id.check_percent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("umbrella://checklist/" + current.getCategory().replace(' ', '-')));
                mContext.startActivity(i);
            }
        });

        if (position==0 || (position==1 && !current.isNoPercent())) {
            holder.checkHeader.setVisibility(View.VISIBLE);
            if (position==0) {
                holder.checkHeader.setTextColor(mContext.getResources().getColor(R.color.umbrella_yellow));
                holder.checkHeader.setText(mContext.getResources().getString(R.string.check_lists_total));
            } else {
                holder.checkHeader.setTextColor(mContext.getResources().getColor(R.color.umbrella_green));
                holder.checkHeader.setText(mContext.getResources().getString(R.string.my_checklists));
            }
        } else {
            holder.checkHeader.setVisibility(View.GONE);
        }
        holder.icon.setVisibility(current.isNoIcon() ? View.GONE : View.VISIBLE);
        if (current.getDifficulty() < 3) {
            holder.icon.setImageResource(new int[]{R.drawable.ic_beginner, R.drawable.ic_advance, R.drawable.ic_expert}[current.getDifficulty()]);
        }
        holder.categoryName.setText(current.getCategory());
        holder.categoryName.setGravity(current.isNoPercent() ? Gravity.CENTER : Gravity.LEFT);
        holder.percent.setVisibility(current.isNoPercent() ? View.GONE : View.VISIBLE);
        holder.percent.setText(String.valueOf(current.getPercent())+"%");

        return convertView;
    }

    private static class ViewHolder {
        public ImageView icon;
        public TextView categoryName;
        public TextView checkHeader;
        public TextView percent;
    }

    public void updateData(ArrayList<DashCheckFinished> list) {
        this.checkItems = list;
        notifyDataSetChanged();
    }
}
