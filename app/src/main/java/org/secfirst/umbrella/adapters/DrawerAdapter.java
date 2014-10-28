package org.secfirst.umbrella.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import org.secfirst.umbrella.MainActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.models.DrawerChildItem;
import org.secfirst.umbrella.util.Global;

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class DrawerAdapter extends BaseExpandableListAdapter {

    public ArrayList<String> groupItem;
    ArrayList<ArrayList<DrawerChildItem>> childItem = new ArrayList<ArrayList<DrawerChildItem>>();
    private Context context;

    public DrawerAdapter(Context context) {
        this.context = context;
        Global global = (Global) context.getApplicationContext();
        this.groupItem = new ArrayList<String>();
        this.groupItem = global.getDrawerItems();
        this.childItem = global.getDrawerChildItems();
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
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_child_item, null);
            holder = new ViewHolder();
            holder.childTitle = (TextView) convertView.findViewById(R.id.drawer_child_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.childTitle.setText(tempChild.getTitle());
        holder.childTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("clicked", String.valueOf(tempChild.getPosition()));
                ((MainActivity) context).onNavigationDrawerItemSelected(tempChild);
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return (childItem.size()>groupPosition) ? (childItem.get(groupPosition)).size() : 0;
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
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.drawer_group_item, null);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.drawer_group_text);
        tv.setText(groupItem.get(groupPosition));

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
    }

}