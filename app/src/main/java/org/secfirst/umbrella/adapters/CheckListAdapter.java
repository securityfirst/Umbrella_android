package org.secfirst.umbrella.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
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
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.util.ArrayList;
import java.util.List;

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
    public Object getItem(int i) {
        return checkList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return (checkList.size()>i) ? checkList.get(i).getId() : 0;
    }

    @Override
    public View getView(final int i, View convertView, final ViewGroup viewGroup) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.check_list_item, null);
            holder = new ViewHolder();
            holder.checkItemLayout = (LinearLayout) convertView.findViewById(R.id.check_item_layout);
            holder.checkItemTitle = (TextView) convertView.findViewById(R.id.check_item_title);
            holder.checkItemSubtitle = (TextView) convertView.findViewById(R.id.check_item_subtitle);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.check_value);
            holder.checkView = (CardView) convertView.findViewById(R.id.card_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (checkList.get(i).getParent()==0) {
            holder.checkItemTitle.setText(checkList.get(i).getTitle());
            holder.checkItemSubtitle.setVisibility(checkList.get(i).getText().equals("") ? View.GONE : View.VISIBLE);
            holder.checkItemTitle.setVisibility(View.VISIBLE);
            holder.checkItemSubtitle.setText(checkList.get(i).getText());
            holder.checkItemLayout.setPadding(0, 0, 0, 0);
        } else {
            holder.checkItemSubtitle.setText(checkList.get(i).getText());
            holder.checkItemTitle.setVisibility(View.GONE);
            holder.checkItemSubtitle.setVisibility(View.VISIBLE);
            holder.checkItemLayout.setPadding(UmbrellaUtil.dpToPix(20, mContext), 0, 0, 0);
        }
        holder.checkBox.setChecked(checkList.get(i).getValue());
        holder.checkBox.setEnabled(!checkList.get(i).isDisabled());
        holder.checkBox.setVisibility(checkList.get(i).getNoCheck() ? View.GONE : View.VISIBLE);

        holder.checkView.setCardElevation(checkList.get(i).getValue()? 0 : 4);
        if (checkList.get(i).isDisabled()) {
            holder.checkView.setCardBackgroundColor(viewGroup.getResources().getColor(R.color.grey));
        } else {
            holder.checkView.setCardBackgroundColor(viewGroup.getResources().getColor((checkList.get(i).getNoCheck() || checkList.get(i).getValue()) ? R.color.white : R.color.umbrella_yellow));
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = ((CheckBox) v).isChecked();
                setChecked(isChecked, i);
                holder.checkView.setCardElevation(isChecked ? 0 : 4);
                if (!checkList.get(i).isCustom()) {
                    holder.checkView.setCardBackgroundColor(viewGroup.getResources().getColor(isChecked ? R.color.white : R.color.umbrella_yellow));
                }
            }
        });

        if (checkList.get(i).isCustom()) {
            holder.checkView.setCardBackgroundColor(viewGroup.getResources().getColor(R.color.umbrella_green));
        }

        holder.checkItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (checkList.get(i).isCustom()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Select an action");
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkList.get(i).delete();
                            checkList.remove(i);
                            notifyDataSetChanged();
                        }
                    });
                    builder.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Select an action");
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.setPositiveButton(checkList.get(i).isDisabled() ? "Enable" : "Disable", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (checkList.get(i).isDisabled()) {
                                checkList.get(i).enable();
                            } else {
                                checkList.get(i).disable();
                            }
                            checkList.get(i).save();
                            checkList.set(i, checkList.get(i));
                            CheckItem current = (CheckItem) getItem(i);
                            mFragment.refreshCheckList(current.getCategory(), current.getDifficulty());
                        }
                    });
                    builder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            checkList.get(i).delete();
                            checkList.remove(i);
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
        current.save();
        checkList.get(i).setValue(b ? 1 : 0);
        mFragment.refreshCheckList(current.getCategory(), current.getDifficulty());
    }

    private static class ViewHolder {
        public LinearLayout checkItemLayout;
        public TextView checkItemTitle;
        public TextView checkItemSubtitle;
        public CheckBox checkBox;
        public CardView checkView;
    }

    public void updateData(List<CheckItem> items) {
        checkList = items;
        notifyDataSetChanged();
    }
}
