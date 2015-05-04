package org.secfirst.umbrella.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.adapters.FeedAdapter;
import org.secfirst.umbrella.models.FeedItem;
import org.secfirst.umbrella.models.Relief.Data;
import org.secfirst.umbrella.models.Relief.Response;
import org.secfirst.umbrella.util.UmbrellaRestClient;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TabbedFeedFragment extends Fragment {

    private ListView feedListView;

    public TabbedFeedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard_feed,
                container, false);
        feedListView = (ListView) rootView.findViewById(R.id.feed_list);
        getFeeds();
        return rootView;
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
                if (receivedResponse != null) {
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