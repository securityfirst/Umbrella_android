package org.secfirst.umbrella.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.SettingsActivity;
import org.secfirst.umbrella.models.SettingsItem;

import java.util.ArrayList;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {
    private ArrayList<SettingsItem> mSettings = new ArrayList<SettingsItem>();
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public ViewHolder(View v) {
            super(v);
            mTitle = (TextView) v.findViewById(R.id.settings_text);
        }
    }

    public SettingsAdapter(Context context) {
        mContext = context;
        mSettings.add(new SettingsItem("Refresh from the server"));
        mSettings.add(new SettingsItem("Refresh from the server"));
        mSettings.add(new SettingsItem("Refresh from the server"));
        notifyDataSetChanged();
    }

    @Override
    public SettingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.settings_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTitle.setText(mSettings.get(position).getTitle());
        holder.mTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("syncing", "now");
                ((SettingsActivity) mContext).syncApi();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSettings.size();
    }
}