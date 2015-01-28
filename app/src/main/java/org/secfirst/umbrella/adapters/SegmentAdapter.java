package org.secfirst.umbrella.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.models.Segment;

import java.util.ArrayList;
import java.util.List;

public class SegmentAdapter extends BaseAdapter {

    private Context context;
    private List<Segment> mSegments = new ArrayList<>();

    public SegmentAdapter(Context context, List<Segment> segments) {
        this.context = context;
        mSegments = segments;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mSegments.size();
    }

    @Override
    public Object getItem(int i) {
        return mSegments.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mSegments.get(i).getId();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        Segment current = mSegments.get(i);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.segment_item, null);
            holder = new ViewHolder();
            holder.segmentTitle = (TextView) convertView.findViewById(R.id.segment_title);
            holder.segmentSubtitle = (TextView) convertView.findViewById(R.id.segment_subtitle);
            holder.segmentBody = (TextView) convertView.findViewById(R.id.segment_body);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (!current.getTitle().equals("")) {
            holder.segmentTitle.setVisibility(View.VISIBLE);
            holder.segmentTitle.setText(current.getTitle());
        } else {
            holder.segmentTitle.setVisibility(View.GONE);

        }

        if (!current.getSubtitle().equals("")) {
            holder.segmentSubtitle.setVisibility(View.VISIBLE);
            holder.segmentSubtitle.setText(current.getSubtitle());
        } else {
            holder.segmentSubtitle.setVisibility(View.GONE);
        }

        if (!current.getBody().equals("")) {
            holder.segmentBody.setVisibility(View.VISIBLE);
            holder.segmentBody.setText(Html.fromHtml(current.getBody()));
        } else {
            holder.segmentBody.setVisibility(View.GONE);
        }

        return convertView;
    }

    private static class ViewHolder {
        public TextView segmentTitle;
        public TextView segmentSubtitle;
        public TextView segmentBody;
    }
}
