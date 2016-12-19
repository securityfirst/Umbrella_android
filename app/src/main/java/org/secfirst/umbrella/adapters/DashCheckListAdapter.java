package org.secfirst.umbrella.adapters;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.models.DashCheckFinished;
import org.secfirst.umbrella.models.Favourite;
import org.secfirst.umbrella.util.Global;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class DashCheckListAdapter extends BaseAdapter {

    private Context mContext;
    private List<DashCheckFinished> checkItems;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
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
                if (!current.isNoIcon()) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse("umbrella://checklist/" + current.getCategory().replace('-', '_').replace(' ', '-').toLowerCase()));
                    mContext.startActivity(i);
                }
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!current.isFavourited()) {
                    return false;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle(mContext.getString(R.string.select_action));
                builder.setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.setPositiveButton(mContext.getString(R.string.unfauvorite), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<Favourite> favourites;
                        try {
                            QueryBuilder<Favourite, String> queryBuilder = ((Global) mContext.getApplicationContext()).getDaoFavourite().queryBuilder();
                            Where<Favourite, String> where = queryBuilder.where();
                            where.eq(Favourite.FIELD_CATEGORY, new SelectArg(current.getCategory())).and().eq(Favourite.FIELD_DIFFICULTY, String.valueOf(current.getDifficulty()));
                            favourites = queryBuilder.query();
                            favourites.clear();
                            checkItems.remove(position);
                            notifyDataSetChanged();
                        } catch (SQLException e) {
                            Timber.e(e);
                        }
                    }
                });
                builder.show();
                return true;
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
