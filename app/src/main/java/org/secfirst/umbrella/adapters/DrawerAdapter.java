package org.secfirst.umbrella.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.secfirst.umbrella.MainActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.models.DrawerChildItem;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class DrawerAdapter extends BaseExpandableListAdapter {

    public ArrayList<String> groupItem = new ArrayList<String>();
    ArrayList<ArrayList<DrawerChildItem>> childItem = new ArrayList<ArrayList<DrawerChildItem>>();
    private Context mContext;

    public DrawerAdapter(Context context) {
        mContext = context;
        groupItem = UmbrellaUtil.getParentCategories();
        childItem = UmbrellaUtil.getChildItems();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final DrawerChildItem tempChild = childItem.get(groupPosition).get(childPosition);

        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_child_item, null);
            holder = new ViewHolder();
            holder.childTitle = (TextView) convertView.findViewById(R.id.drawer_child_text);
            holder.childIcon = (ImageView) convertView.findViewById(R.id.drawer_child_icon);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int iconDrawable = R.drawable.ic_lock_grey600_24dp;
        switch (childPosition) {
            case 1:
                iconDrawable = R.drawable.ic_screen_lock_portrait_grey600_24dp;
                break;
            case 2:
                iconDrawable = R.drawable.ic_email_grey600_24dp;
                break;
            case 3:
                iconDrawable = R.drawable.ic_delete_grey600_24dp;
                break;
        }

        holder.childIcon.setImageDrawable(mContext.getResources().getDrawable(iconDrawable));
        holder.childTitle.setText(tempChild.getTitle());
        holder.childTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) mContext).onNavigationDrawerItemSelected(tempChild);
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return (childItem.size() > groupPosition) ? (childItem.get(groupPosition)).size() : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public int getGroupCount() {
        return groupItem.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_group_item, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.drawer_group_text);
        tv.setText(groupItem.get(groupPosition));
        if (groupPosition==0) {
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity) mContext).setDashboard(groupItem.get(groupPosition));
                }
            });
        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    private static class ViewHolder {
        public TextView childTitle;
        public ImageView childIcon;
    }

}