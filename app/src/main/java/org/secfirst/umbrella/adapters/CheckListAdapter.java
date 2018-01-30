package org.secfirst.umbrella.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.fragments.TabbedFragment;
import org.secfirst.umbrella.models.CheckItem;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class CheckListAdapter extends BaseAdapter {

    private List<CheckItem> checkList = new ArrayList<>();
    private Context mContext;
    private TabbedFragment.CheckItemFragment mFragment;

    public CheckListAdapter(Context context, List<CheckItem> mCheckList, TabbedFragment.CheckItemFragment fragment) {
        mFragment = fragment;
        mContext = context;
        checkList = mCheckList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return checkList.size();
    }

    @Override
    public Object getItem(int position) {
        Log.e("test", "size list _" + checkList.size());
        return checkList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return (checkList.size() > position) ? checkList.get(position).getId() : 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, final ViewGroup viewGroup) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.check_list_item, null);
            holder = new ViewHolder();
            holder.checkItemLayout = convertView.findViewById(R.id.check_item_layout);
            holder.checkItemTitle = convertView.findViewById(R.id.check_item_title);
            holder.checkItemSubtitle = convertView.findViewById(R.id.check_item_subtitle);
            holder.checkBox = convertView.findViewById(R.id.check_value);
            holder.checkView = convertView.findViewById(R.id.card_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.checkItemTitle.setVisibility(checkList.get(position).getNoCheck() ? View.GONE : View.VISIBLE);
        holder.checkItemSubtitle.setVisibility(checkList.get(position).getNoCheck() ? View.VISIBLE : View.GONE);
        holder.checkBox.setVisibility(checkList.get(position).getNoCheck() ? View.GONE : View.VISIBLE);
        holder.checkItemSubtitle.setText(checkList.get(position).getTitle());
        holder.checkItemTitle.setText(checkList.get(position).getTitle());
        if (checkList.get(position).getNoCheck()) {
            holder.checkItemLayout.setPadding(0, 0, 0, 0);
        } else {
            holder.checkItemLayout.setPadding(UmbrellaUtil.dpToPix(20, mContext), 0, 0, 0);
        }
        holder.checkBox.setChecked(checkList.get(position).getValue());
        holder.checkBox.setEnabled(!checkList.get(position).isDisabled());

        holder.checkView.setCardElevation(checkList.get(position).getValue() ? 0 : 4);
        if (checkList.get(position).isDisabled()) {
            holder.checkView.setCardBackgroundColor(viewGroup.getResources().getColor(R.color.grey));
        } else {
            holder.checkView.setCardBackgroundColor(viewGroup.getResources().getColor((checkList.get(position).getNoCheck() || checkList.get(position).getValue()) ? R.color.white : R.color.umbrella_yellow));
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = ((CheckBox) v).isChecked();
                setChecked(isChecked, position);
                holder.checkView.setCardElevation(isChecked ? 0 : 4);
                Log.e("test","size - "+ checkList.size());
                if (!checkList.get(position).isCustom()) {
                    holder.checkView.setCardBackgroundColor(viewGroup.getResources().getColor(isChecked ? R.color.white : R.color.umbrella_yellow));
                }
            }
        });

        if (checkList.get(position).isCustom()) {
            holder.checkView.setCardBackgroundColor(viewGroup.getResources().getColor(R.color.umbrella_green));
        }

        holder.checkItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (checkList.get(position).isCustom()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(mContext.getString(R.string.select_action));
                    builder.setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setPositiveButton(mContext.getString(R.string.delete), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Global.INSTANCE.getDaoCheckItem().delete(checkList.get(position));
                            } catch (SQLException e) {
                                Timber.e(e);
                            }
                            checkList.remove(position);
                            notifyDataSetChanged();
                        }
                    });
                    builder.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle(mContext.getString(R.string.select_action));
                    builder.setNegativeButton(mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setPositiveButton(checkList.get(position).isDisabled() ? mContext.getString(R.string.enable) : mContext.getString(R.string.disable), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (checkList.get(position).isDisabled()) {
                                checkList.get(position).enable();
                            } else {
                                checkList.get(position).disable();
                            }
                            try {
                                Global.INSTANCE.getDaoCheckItem().update(checkList.get(position));
                            } catch (SQLException e) {
                                Timber.e(e);
                            }
                            checkList.set(position, checkList.get(position));
                            CheckItem current = (CheckItem) getItem(position);
                            mFragment.refreshCheckList(current.getCategory(), current.getDifficulty());
                        }
                    });
                    builder.setNeutralButton(mContext.getString(R.string.delete), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Global.INSTANCE.getDaoCheckItem().delete(checkList.get(position));
                            } catch (SQLException e) {
                                Timber.e(e);
                            }
                            checkList.remove(position);
                            notifyDataSetChanged();
                        }
                    });
                    builder.show();
                }
                return false;
            }
        });

        return convertView;
    }

    private void setChecked(boolean b, int i) {

        CheckItem current = (CheckItem) getItem(i);
        current.setValue(b ? 1 : 0);
        try {
            Global.INSTANCE.getDaoCheckItem().update(current);
        } catch (SQLException e) {
            Timber.e(e);
        }
        checkList.get(i).setValue(b ? 1 : 0);
        mFragment.refreshCheckList(current.getCategory(), current.getDifficulty());
    }

    private static class ViewHolder {
        LinearLayout checkItemLayout;
        TextView checkItemTitle;
        TextView checkItemSubtitle;
        CheckBox checkBox;
        CardView checkView;
    }

    public void updateData(List<CheckItem> items) {
        checkList = items;
        notifyDataSetChanged();
    }
}
