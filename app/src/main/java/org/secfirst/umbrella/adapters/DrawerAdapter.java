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

import java.util.ArrayList;

@SuppressWarnings("unchecked")
public class DrawerAdapter extends BaseExpandableListAdapter {

    public ArrayList<String> groupItem;
    ArrayList<ArrayList<DrawerChildItem>> childItem = new ArrayList<ArrayList<DrawerChildItem>>();
    private Context context;

    public DrawerAdapter(Context context) {
        this.context = context;
        this.groupItem = new ArrayList<String>();
        this.groupItem.add(context.getString(R.string.title_section1));
        this.groupItem.add(context.getString(R.string.title_section2));
        this.groupItem.add(context.getString(R.string.title_section3));
        this.groupItem.add(context.getString(R.string.title_section4));
        this.groupItem.add(context.getString(R.string.title_section5));
        ArrayList<DrawerChildItem> child = new ArrayList<DrawerChildItem>();
        child.add(new DrawerChildItem("Java", 1));
        child.add(new DrawerChildItem("Drupal", 2));
        child.add(new DrawerChildItem(".Net Framework", 3));
        child.add(new DrawerChildItem("PHP", 3));
        childItem.add(child);

        child = new ArrayList<DrawerChildItem>();
        child.add(new DrawerChildItem("Android", 4));
        child.add(new DrawerChildItem("Window Mobile", 5));
        child.add(new DrawerChildItem("iPHone", 6));
        child.add(new DrawerChildItem("Blackberry", 7));
        childItem.add(child);

        child = new ArrayList<DrawerChildItem>();
        child.add(new DrawerChildItem("HTC", 8));
        child.add(new DrawerChildItem("Apple", 9));
        child.add(new DrawerChildItem("Samsung", 10));
        child.add(new DrawerChildItem("Nokia", 11));
        childItem.add(child);

        child = new ArrayList<DrawerChildItem>();
        child.add(new DrawerChildItem("Contact Us", 12));
        child.add(new DrawerChildItem("About Us", 13));
        child.add(new DrawerChildItem("Location", 14));
        child.add(new DrawerChildItem("Root Cause", 15));
        childItem.add(child);
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
                ((MainActivity) context).onNavigationDrawerItemSelected(tempChild.getPosition());
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