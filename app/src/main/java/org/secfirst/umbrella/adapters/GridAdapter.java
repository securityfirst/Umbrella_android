package org.secfirst.umbrella.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.secfirst.umbrella.MainActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.fragments.TabbedFragment;
import org.secfirst.umbrella.models.Segment;

import java.util.ArrayList;
import java.util.List;

public class GridAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Segment> mSegments;
    private int[] colours = {R.color.umbrella_purple, R.color.umbrella_green, R.color.umbrella_yellow};

    public GridAdapter(Context context, List<Segment> segmentList) {
        mContext = context;
        mSegments = new ArrayList<>(segmentList);
    }

    @Override
    public int getCount() {
        return mSegments.size();
    }

    @Override
    public Object getItem(int position) {
        return mSegments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final Segment current = mSegments.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) convertView.findViewById(R.id.grid_title);
            holder.box = convertView.findViewById(R.id.color_box);
            holder.cardView = (CardView) convertView.findViewById(R.id.card_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.title.setText(current.getTitle());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment frag = ((MainActivity) mContext).getSupportFragmentManager().findFragmentByTag("tabbed");
                if (frag!=null) {
                    ((TabbedFragment)frag).mViewPager.setCurrentItem(position+1);
                }
            }
        });

        holder.box.setBackgroundColor(mContext.getResources().getColor(colours[position%3]));

        return convertView;
    }

    private static class ViewHolder {
        public View box;
        public CardView cardView;
        public TextView title;
    }

}
