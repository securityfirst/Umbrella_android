package org.secfirst.umbrella.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.models.Segment;
import org.secfirst.umbrella.util.Global;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    private ArrayList<Segment> mSegment = new ArrayList<Segment>();
    private ArrayList<String> mTitles = new ArrayList<String>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public TextView mSubtitle;
        public TextView mBody;
        public ViewHolder(View v) {
            super(v);
            mTitle = (TextView) v.findViewById(R.id.search_result_title);
            mBody = (TextView) v.findViewById(R.id.search_result_body);
        }
    }

    public SearchAdapter(Context context, ArrayList<Segment> segmentList) {
        mSegment = segmentList;
        Global global = (Global) context.getApplicationContext();
        mTitles = global.getDrawerItems();
    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_result_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Segment current = mSegment.get(position);
        if (mTitles.size()>current.getCategory()) {
            holder.mTitle.setText(mTitles.get(current.getCategory()));
        }
        String body = current.getBody().substring(0, 80);
        holder.mBody.setText(body);
    }

    @Override
    public int getItemCount() {
        return mSegment.size();
    }
}