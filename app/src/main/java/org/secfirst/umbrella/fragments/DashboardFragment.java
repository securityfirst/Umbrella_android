package org.secfirst.umbrella.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.orm.query.Select;

import org.apache.http.Header;
import org.json.JSONObject;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.adapters.DashCheckListAdapter;
import org.secfirst.umbrella.adapters.FeedAdapter;
import org.secfirst.umbrella.models.Category;
import org.secfirst.umbrella.models.CheckItem;
import org.secfirst.umbrella.models.DashCheckFinished;
import org.secfirst.umbrella.models.Difficulty;
import org.secfirst.umbrella.models.FeedItem;
import org.secfirst.umbrella.models.Relief.Data;
import org.secfirst.umbrella.models.Relief.Response;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.UmbrellaRestClient;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashboardFragment extends Fragment {

    private Global global;
    private ListView checkListView, feedListView;
    private ArrayList<DashCheckFinished> checkLists;
    private DashCheckListAdapter mAdapter;

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

        checkLists = getChecklistProgress();

        checkCategory.setText("Total done");
        percentDone.setText(String.valueOf(getTotalCheckListPercentage(checkLists)) + "%");
        mAdapter = new DashCheckListAdapter(getActivity(), checkLists);
        checkListView = (ListView) view.findViewById(R.id.check_list);
        if (checkLists.size()==0) {
            checkListView.setVisibility(View.GONE);
            CardView noView = (CardView) view.findViewById(R.id.check_list_no_view);
            noView.setVisibility(View.VISIBLE);
        }
        checkListView.setDividerHeight(10);
        checkListView.setAdapter(mAdapter);
        setListViewHeightBasedOnChildren(checkListView);

        feedListView = (ListView) view.findViewById(R.id.feed_list);
        getFeeds();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkLists = getChecklistProgress();
        if (checkLists.size()>0 && mAdapter!=null) {
            mAdapter.updateData(checkLists);
            setListViewHeightBasedOnChildren(checkListView);
        }
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
            if (category!=null) {
                DashCheckFinished dashCheckFinished = new DashCheckFinished(category.getCategory(), difficulty.getSelected());
                for (CheckItem checkItem : mCheckList) {
                    if (!checkItem.getNoCheck() && !checkItem.isDisabled()) {
                        if (checkItem.getValue()) {
                            int val = dashCheckFinished.getChecked() + 1;
                            dashCheckFinished.setChecked(val);
                        }
                        dashCheckFinished.setTotal(dashCheckFinished.getTotal() + 1);
                    }
                }
                if (dashCheckFinished.getChecked()>0) returned.add(dashCheckFinished);
            }
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

    private void getFeeds() {
//        ProgressDialog mProgress = UmbrellaUtil.launchRingDialogWithText(getActivity(), "Checking for updates");

        UmbrellaRestClient.getFeed("http://api.rwlabs.org/v1/countries/255", null, getActivity(), new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Gson gson = new GsonBuilder().create();
                Type listType = new TypeToken<Response>() {
                }.getType();
                Response receivedResponse = gson.fromJson(response.toString(), listType);
                if (receivedResponse!=null) {
                    List<Data> dataList = Arrays.asList(receivedResponse.getData());
                    ArrayList<FeedItem> items = UmbrellaUtil.parseReliefWeb(dataList);

                    FeedAdapter feedAdapter = new FeedAdapter(getActivity(), items);
                    feedListView.setVisibility(View.VISIBLE);
                    feedListView.setDividerHeight(10);
                    feedListView.setAdapter(feedAdapter);
//                    setListViewHeightBasedOnChildren(feedListView);
                }
            }
        });
    }

}
