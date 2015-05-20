package org.secfirst.umbrella.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.dao.Dao;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SaxAsyncHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.secfirst.umbrella.BaseActivity;
import org.secfirst.umbrella.MainActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.SettingsActivity;
import org.secfirst.umbrella.adapters.FeedAdapter;
import org.secfirst.umbrella.models.FeedItem;
import org.secfirst.umbrella.models.Registry;
import org.secfirst.umbrella.models.Relief.Countries.RWCountries;
import org.secfirst.umbrella.models.Relief.Data;
import org.secfirst.umbrella.models.Relief.Response;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.SaxHandler;
import org.secfirst.umbrella.util.UmbrellaRestClient;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TabbedFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout mSwipeRefreshLayout;
    FeedAdapter feedAdapter;
    TextView noFeedItems;
    CardView noFeedCard;
    ListView feedListView;
    TextView header;
    private ArrayList<FeedItem> items = new ArrayList<>();

    public TabbedFeedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard_feed,
                container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        feedListView = (ListView) rootView.findViewById(R.id.feed_list);
        noFeedItems = (TextView) rootView.findViewById(R.id.no_feed_items);
        noFeedCard = (CardView) rootView.findViewById(R.id.card_view);
        feedAdapter = new FeedAdapter(getActivity(), items);
        header = new TextView(getActivity());
        header.setTextColor(getResources().getColor(R.color.white));
        header.setGravity(Gravity.CENTER_HORIZONTAL);
        feedListView.addHeaderView(header);
        LinearLayout footer = new LinearLayout(getActivity());
        footer.setOrientation(LinearLayout.HORIZONTAL);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 50);
        footer.setLayoutParams(lp);
        feedListView.addFooterView(footer);
        feedListView.setAdapter(feedAdapter);
        feedListView.setDividerHeight(10);
        getFeeds(getActivity());
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRefresh() {
        refreshFeed();
    }

    public void refreshView() {
        ArrayList<FeedItem> items = ((BaseActivity) getActivity()).getGlobal().getFeedItems();
        feedAdapter.updateData(items);
        noFeedItems.setText(R.string.no_feed_updates);
        if (items!=null) {
            if (items.size() > 0)
                header.setText("Last updated: " + DateFormat.getDateTimeInstance().format(new Date(((BaseActivity) getActivity()).getGlobal().getFeeditemsRefreshed())));
            noFeedCard.setVisibility(items.size()>0?View.GONE:View.VISIBLE);
            feedListView.setVisibility(items.size() > 0 ? View.VISIBLE : View.GONE);
        } else {
            noFeedCard.setVisibility(View.VISIBLE);
            feedListView.setVisibility(View.GONE);
        }
    }

    public void refreshFeed() {
        boolean isCountrySet = getFeeds(getActivity());
        if (isCountrySet) {
            refreshView();
        } else {
            noFeedItems.setVisibility(View.VISIBLE);
            noFeedItems.setText("Please set your location in the settings.");
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getActivity(), SettingsActivity.class));
                }
            }, 1000);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void parseReliefWeb(List<Data> dataList) {
        for (Data data : dataList) {
            if (data.getFields().getDescriptionhtml()!=null) {
                Document document = Jsoup.parse(data.getFields().getDescriptionhtml());
                Element ul = document.select("ul").get(0);
                items = new ArrayList<>();
                if (ul.childNodeSize()>0) {
                    for (Element li : ul.select("li")) {
                        FeedItem toAdd = new FeedItem(li.text(), "Loading...", li.select("a").get(0).attr("href"));
                        toAdd.setDate(UmbrellaUtil.parseReliefTitleForDate(li.text()));
                        items.add(toAdd);
                        new GetRWBody(items.size() - 1, toAdd.getUrl()).execute();
                    }
                }
            }
        }
    }

    public boolean getFeeds(final Context context) {
        Global global = ((MainActivity) getActivity()).getGlobal();
        global.setFeedItems(new ArrayList<FeedItem>());
        Dao<Registry, String> regDao = global.getDaoRegistry();
        List<Registry> selCountry = null;
        try {
            selCountry = regDao.queryForEq(Registry.FIELD_NAME, "country");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (selCountry!=null && selCountry.size()>0) {
            List<Registry> selections = null;
            try {
                selections = regDao.queryForEq(Registry.FIELD_NAME, "feed_sources");
                for (Registry selection : selections) {
                    if (selection.getValue().equals("0")) getReliefWeb(selCountry, context);
                    if (selection.getValue().equals("3")) getCDC(context);
                }
                noFeedCard.setVisibility(View.GONE);
                noFeedItems.setText(getResources().getString(R.string.no_feed_updates));
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        } else {
            noFeedCard.setVisibility(View.VISIBLE);
            return false;
        }
    }

    public void getCDC(final Context context) {
        UmbrellaRestClient.getFeed("http://www2c.cdc.gov/podcasts/createrss.asp?t=r&c=66", null, context, new SaxAsyncHttpResponseHandler<SaxHandler>(new SaxHandler()) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, SaxHandler saxHandler) {
                List<FeedItem> feedItems = saxHandler.getFeeditems();
                if (feedItems!=null && feedItems.size()>0) {
                    ((BaseActivity) context).getGlobal().addToFeedItems(new ArrayList<>(feedItems));
                    mSwipeRefreshLayout.setRefreshing(false);
                    refreshView();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, SaxHandler saxHandler) {
            }
        });
    }


    public void getReliefWeb(List<Registry> selCountry, final Context context) {
        UmbrellaRestClient.getFeed("http://api.rwlabs.org/v1/countries/?query[value]=" + selCountry.get(0).getValue(), null, context, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Gson gson = new GsonBuilder().create();
                Type listType = new TypeToken<RWCountries>() {
                }.getType();
                RWCountries receivedSegments = gson.fromJson(response.toString(), listType);
                if (receivedSegments != null) {
                    ArrayList<org.secfirst.umbrella.models.Relief.Countries.Data> results = receivedSegments.getData();
                    if (results.size() > 0) {
                        getReports(results.get(0).getId(), context);
                    } else {
                        noFeedCard.setVisibility(View.VISIBLE);
                    }
                } else {
                    noFeedCard.setVisibility(View.VISIBLE);
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void getReports(String countryId, final Context context) {
        UmbrellaRestClient.getFeed("http://api.rwlabs.org/v1/countries/" + countryId, null, context, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Gson gson = new GsonBuilder().create();
                Type listType = new TypeToken<Response>() {
                }.getType();
                Response receivedResponse = gson.fromJson(response.toString(), listType);
                if (receivedResponse != null) {
                    List<org.secfirst.umbrella.models.Relief.Data> dataList = Arrays.asList(receivedResponse.getData());
                    parseReliefWeb(dataList);
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private class GetRWBody extends AsyncTask<String, Void, String> {
        int index;
        String url;

        GetRWBody(int index, String url) {
            this.index = index;
            this.url = url;
        }

        @Override
        protected String doInBackground(String... params) {
            String body ="";
            Document doc;
            try {
                doc = Jsoup.connect(url).get();
                Elements forBody = doc.select("div.body.field");
                if (!forBody.isEmpty()) {
                    items.get(index).setBody(forBody.get(0).text());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return body;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            items.get(index).setBody("");
            feedAdapter.updateData();
        }

        @Override
        protected void onPostExecute(String result) {
            if (getActivity()!=null) ((BaseActivity) getActivity()).getGlobal().addFeedItem(items.get(index));
            feedAdapter.updateData();
        }
    }

}