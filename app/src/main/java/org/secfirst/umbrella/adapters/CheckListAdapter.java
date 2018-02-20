package org.secfirst.umbrella.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.models.CheckItem;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by dougl on 19/02/2018.
 */

public class CheckListAdapter extends RecyclerView.Adapter<CheckListAdapter.ViewHolder> {

    private final OnCheckListEvent mCheckListEvent;
    private List<CheckItem> checkList;
    private final Context mContext;


    public CheckListAdapter(Context context, OnCheckListEvent checkListEvent) {
        mCheckListEvent = checkListEvent;
        mContext = context;
        checkList = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.check_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
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
            holder.checkView.setCardBackgroundColor(mContext.getResources().getColor(R.color.grey));
        } else {
            holder.checkView.setCardBackgroundColor(mContext.getResources().getColor
                    ((checkList.get(position).getNoCheck() || checkList.get(position).getValue()) ? R.color.white : R.color.umbrella_yellow));
        }

        if (checkList.get(position).isCustom()) {
            holder.checkView.setCardBackgroundColor(mContext.getResources().getColor(R.color.umbrella_green));
        }


    }

    @Override
    public int getItemCount() {
        Log.e("test", "size checkList - " + checkList.size());
        if (checkList == null) {
            return 0;
        } else
            return checkList.size();
    }

    public void updateData(List<CheckItem> items) {
        checkList = new ArrayList<>();
        checkList = items;
        notifyDataSetChanged();
    }

    public void add(CheckItem checkItem) {
        checkList.add(checkItem);
        notifyItemInserted(checkList.size() - 1);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        LinearLayout checkItemLayout;
        TextView checkItemTitle;
        TextView checkItemSubtitle;
        CheckBox checkBox;
        CardView checkView;

        public ViewHolder(View itemView) {
            super(itemView);
            checkItemLayout = itemView.findViewById(R.id.check_item_layout);
            checkItemTitle = itemView.findViewById(R.id.check_item_title);
            checkItemSubtitle = itemView.findViewById(R.id.check_item_subtitle);
            checkBox = itemView.findViewById(R.id.check_value);
            checkView = itemView.findViewById(R.id.card_view);

            checkBox.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            boolean isChecked = ((CheckBox) view).isChecked();
            setChecked(isChecked, getLayoutPosition());
            checkView.setCardElevation(isChecked ? 0 : 4);
            if (!checkList.get(getLayoutPosition()).isCustom()) {
                checkView.setCardBackgroundColor(mContext.getResources().getColor(isChecked ? R.color.white : R.color.umbrella_yellow));
            }
        }

        private void setChecked(boolean b, int i) {
            CheckItem current = checkList.get(getLayoutPosition());
            current.setValue(b ? 1 : 0);
            try {
                Global.INSTANCE.getDaoCheckItem().update(current);
            } catch (SQLException e) {
                Timber.e(e);
            }
            checkList.get(i).setValue(b ? 1 : 0);
            mCheckListEvent.refreshCheckList(checkList);
        }

        @Override
        public boolean onLongClick(View view) {

            if (checkList.get(getLayoutPosition()).isCustom()) {
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
                            Global.INSTANCE.getDaoCheckItem().delete(checkList.get(getLayoutPosition()));
                        } catch (SQLException e) {
                            Timber.e(e);
                        }
                        checkList.remove(getLayoutPosition());
                        notifyDataSetChanged();
                        mCheckListEvent.refreshCheckList(checkList);
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
                builder.setPositiveButton(checkList.get(getLayoutPosition()).isDisabled() ? mContext.getString(R.string.enable)
                        : mContext.getString(R.string.disable), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (checkList.get(getLayoutPosition()).isDisabled()) {
                            checkList.get(getLayoutPosition()).enable();
                        } else {
                            checkList.get(getLayoutPosition()).disable();
                        }
                        try {
                            Global.INSTANCE.getDaoCheckItem().update(checkList.get(getLayoutPosition()));
                        } catch (SQLException e) {
                            Timber.e(e);
                        }
                        checkList.set(getLayoutPosition(), checkList.get(getLayoutPosition()));
                        notifyDataSetChanged();
                        mCheckListEvent.refreshCheckList(checkList);
                    }
                });
                builder.setNeutralButton(mContext.getString(R.string.delete), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Global.INSTANCE.getDaoCheckItem().delete(checkList.get(getLayoutPosition()));
                        } catch (SQLException e) {
                            Timber.e(e);
                        }
                        checkList.remove(getLayoutPosition());
                        notifyDataSetChanged();
                        mCheckListEvent.refreshCheckList(checkList);
                    }
                });
                builder.show();
            }
            return false;
        }
    }


    public interface OnCheckListEvent {
        void refreshCheckList(List<CheckItem> checkItems);
    }

}
