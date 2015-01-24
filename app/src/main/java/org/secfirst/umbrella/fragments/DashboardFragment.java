package org.secfirst.umbrella.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.adapters.DashCheckListAdapter;
import org.secfirst.umbrella.adapters.DashFeedAdapter;
import org.secfirst.umbrella.data.CheckListDataSource;
import org.secfirst.umbrella.models.CheckItem;
import org.secfirst.umbrella.models.DashCheckFinished;
import org.secfirst.umbrella.models.FeedItem;
import org.secfirst.umbrella.util.Global;

import java.util.ArrayList;

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
        checkLists.add(new DashCheckFinished("first", 66));
        checkLists.add(new DashCheckFinished("second", 56));
        checkLists.add(new DashCheckFinished("third", 46));
        DashCheckListAdapter mAdapter = new DashCheckListAdapter(getActivity(), checkLists);
        ListView mListView = (ListView) view.findViewById(R.id.check_list);
        mListView.setAdapter(mAdapter);

        ArrayList<FeedItem> forFeed = new ArrayList<>();
        forFeed.add(new FeedItem("First title", "subtitl1e", "1info about...", ""));
        forFeed.add(new FeedItem("Second title", "subtitle2", "2info about...", ""));
        forFeed.add(new FeedItem("Third title", "subtitle3", "3info about...", ""));
        DashFeedAdapter feedAdapter = new DashFeedAdapter(getActivity(), forFeed);
        ListView feedListView = (ListView) view.findViewById(R.id.feed_list);
        feedListView.setAdapter(feedAdapter);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    private ArrayList<CheckItem> refreshCheckItems(Activity activity) {
        CheckListDataSource checkListDataSource = new CheckListDataSource(activity);
        checkListDataSource.open();
        ArrayList<CheckItem> checkItems = checkListDataSource.getAllItems();
        checkListDataSource.close();
        return checkItems;
    }

}
