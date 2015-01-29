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
import android.widget.CompoundButton;
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
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        final CheckItem current = checkList.get(i);

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

        if (current.getParent()==0) {
            holder.checkItemTitle.setText(current.getTitle());
            holder.checkItemSubtitle.setVisibility(current.getText().equals("") ? View.GONE : View.VISIBLE);
            holder.checkItemTitle.setVisibility(View.VISIBLE);
            holder.checkItemSubtitle.setText(current.getText());
            holder.checkBox.setChecked(current.getValue());
            holder.checkItemLayout.setPadding(0, 0, 0, 0);
        } else {
            holder.checkItemSubtitle.setText(current.getText());
            holder.checkItemTitle.setVisibility(View.GONE);
            holder.checkItemSubtitle.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(current.getValue());
            holder.checkItemLayout.setPadding(UmbrellaUtil.dpToPix(20, mContext), 0, 0, 0);
        }

        holder.checkView.setCardElevation(current.getValue()? 0 : 4);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                setChecked(current, b);
                holder.checkView.setCardElevation(b ? 0 : 4 );
            }
        });

        holder.checkItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (current.isCustom()) {
                    CharSequence menuChoiceCustom[] = new CharSequence[]{"Delete"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Select an action");
                    builder.setItems(menuChoiceCustom, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                current.delete();
                                checkList.remove(i);
                                notifyDataSetChanged();
                            } else {
                                // edit to come
                            }
                        }
                    });
                    builder.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Select an action");
                    if (current.isDisabled()) {
                        CharSequence menuChoice[] = new CharSequence[]{"Enable"};
                        builder.setItems(menuChoice, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                current.enable();
                                current.save();
                                checkList.set(i, current);
                                notifyDataSetChanged();
                            }
                        });
                    } else {
                        CharSequence menuChoice[] = new CharSequence[]{"Disable"};
                        builder.setItems(menuChoice, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                current.disable();
                                current.save();
                                checkList.set(i, current);
                                notifyDataSetChanged();
                            }
                        });
                    }
                    builder.show();
                }
                return false;
            }
        });

        return convertView;
    }

    private void setChecked(CheckItem current, boolean b) {
        current.setValue(b ? 1 : 0);
        current.save();
        mFragment.refreshCheckList(current.getCategory());
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
