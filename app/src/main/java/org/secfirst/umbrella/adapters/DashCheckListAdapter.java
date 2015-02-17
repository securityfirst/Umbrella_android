package org.secfirst.umbrella.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
        DashCheckFinished current = checkItems.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.dash_check_item, null);
            holder = new ViewHolder();
            holder.categoryName = (TextView) convertView.findViewById(R.id.check_category);
            holder.percent = (TextView) convertView.findViewById(R.id.check_percent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.categoryName.setText(current.getCategory());
        holder.percent.setText(String.valueOf(current.getPercent())+"%");

        return convertView;
    }

    private static class ViewHolder {
        public TextView categoryName;
        public TextView percent;
    }

    public void updateData(ArrayList<DashCheckFinished> list) {
        this.checkItems = list;
        notifyDataSetChanged();
    }
}
