package org.secfirst.umbrella.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.orm.query.Select;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.adapters.DashCheckListAdapter;
import org.secfirst.umbrella.adapters.DashFeedAdapter;
import org.secfirst.umbrella.models.Category;
import org.secfirst.umbrella.models.CheckItem;
import org.secfirst.umbrella.models.DashCheckFinished;
import org.secfirst.umbrella.models.Difficulty;
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


        getChecklistProgress();
        ArrayList<DashCheckFinished> checkLists = getChecklistProgress();

        checkCategory.setText("Total checklists done");
        percentDone.setText(String.valueOf(getTotalCheckListPercentage(checkLists)));
        DashCheckListAdapter mAdapter = new DashCheckListAdapter(getActivity(), checkLists);
        ListView mListView = (ListView) view.findViewById(R.id.check_list);
        if (checkLists.size()==0) {
            mListView.setVisibility(View.GONE);
            CardView noView = (CardView) view.findViewById(R.id.check_list_no_view);
            noView.setVisibility(View.VISIBLE);
        }
        mListView.setDividerHeight(10);
        mListView.setAdapter(mAdapter);
        setListViewHeightBasedOnChildren(mListView);

        ArrayList<FeedItem> forFeed = new ArrayList<>();
        forFeed.add(new FeedItem("First title", "subtitle1", "1info about...", ""));
        forFeed.add(new FeedItem("Second title", "subtitle2", "2info about...", ""));
        forFeed.add(new FeedItem("Third title", "subtitle3", "3info about...", ""));
        DashFeedAdapter feedAdapter = new DashFeedAdapter(getActivity(), forFeed);
        ListView feedListView = (ListView) view.findViewById(R.id.feed_list);
        feedListView.setDividerHeight(10);
        feedListView.setAdapter(feedAdapter);
        setListViewHeightBasedOnChildren(feedListView);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public ArrayList<DashCheckFinished> getChecklistProgress() {
        ArrayList<DashCheckFinished> returned = new ArrayList<>();
        List<Difficulty> hasDifficulty = Select.from(Difficulty.class).list();
        for (Difficulty difficulty : hasDifficulty) {
            List<CheckItem> mCheckList = CheckItem.find(CheckItem.class, "category = ? and difficulty = ?", String.valueOf(difficulty.getCategory()), String.valueOf(difficulty.getSelected() + 1));
            Category category = Category.findById(Category.class, difficulty.getCategory());
            Log.i("dif", String.valueOf(difficulty.getCategory()));
            Log.i("sel", String.valueOf(difficulty.getSelected()));
            DashCheckFinished dashCheckFinished = new DashCheckFinished(category.getCategory());
            for (CheckItem checkItem : mCheckList) {
                if (checkItem.getValue()) {
                    int val = dashCheckFinished.getChecked() + 1;
                    dashCheckFinished.setChecked(val);
                }
                dashCheckFinished.setTotal(dashCheckFinished.getTotal() + 1);
            }
            returned.add(dashCheckFinished);
        }
        return returned;
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

    private int getTotalCheckListPercentage(ArrayList<DashCheckFinished> checkLists) {
        int checked, total;
        checked = total = 0;
        for (DashCheckFinished checkList : checkLists) {
            checked = checked + checkList.getChecked();
            total = total + checkList.getTotal();
        }
        return (int)((checked * 100.0f) / total);
    }

}
