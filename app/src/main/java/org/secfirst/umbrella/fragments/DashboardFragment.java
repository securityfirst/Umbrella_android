package org.secfirst.umbrella.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.adapters.DashCheckListAdapter;
import org.secfirst.umbrella.adapters.DashFeedAdapter;
import org.secfirst.umbrella.models.CheckItem;
import org.secfirst.umbrella.models.DashCheckFinished;
import org.secfirst.umbrella.models.FeedItem;
import org.secfirst.umbrella.util.Global;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private Global global;

    public static DashboardFragment newInstance(Global global) {
        DashboardFragment fragment = new DashboardFragment();
        fragment.global = global;
        return fragment;
    }

    public DashboardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        TextView checkCategory = (TextView) view.findViewById(R.id.check_category);
        TextView percentDone = (TextView) view.findViewById(R.id.check_percent);

        checkCategory.setText("Total checklists done");
        percentDone.setText("54%");

        ArrayList<DashCheckFinished> checkLists = new ArrayList<>();
        checkLists.add(new DashCheckFinished("Passwords", 66));
        checkLists.add(new DashCheckFinished("Mobile Phones", 56));
        checkLists.add(new DashCheckFinished("Safe Deleting", 46));
        DashCheckListAdapter mAdapter = new DashCheckListAdapter(getActivity(), checkLists);
        ListView mListView = (ListView) view.findViewById(R.id.check_list);
        mListView.setDividerHeight(10);
        mListView.setAdapter(mAdapter);

        ArrayList<FeedItem> forFeed = new ArrayList<>();
        forFeed.add(new FeedItem("First title", "subtitle1", "1info about...", ""));
        forFeed.add(new FeedItem("Second title", "subtitle2", "2info about...", ""));
        forFeed.add(new FeedItem("Third title", "subtitle3", "3info about...", ""));
        DashFeedAdapter feedAdapter = new DashFeedAdapter(getActivity(), forFeed);
        ListView feedListView = (ListView) view.findViewById(R.id.feed_list);
        feedListView.setDividerHeight(10);
        feedListView.setAdapter(feedAdapter);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private List<CheckItem> refreshCheckItems(Activity activity) {
        List<CheckItem> checkItems = CheckItem.listAll(CheckItem.class);
        return checkItems;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

}
