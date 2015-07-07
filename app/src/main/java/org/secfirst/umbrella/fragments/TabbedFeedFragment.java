package org.secfirst.umbrella.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;

public class TabbedFeedFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    SwipeRefreshLayout mSwipeRefreshLayout;
    FeedAdapter feedAdapter;
    TextView noFeedItems;
    LinearLayout noFeedCard;
    ListView feedListView;
    TextView header, refreshIntervalValue, feedSourcesValue;
    Global global;
    private AutoCompleteTextView mAutocompleteLocation;
    private ArrayList<Address> mAddressList;
    private Address mAddress;
    private Registry mLocation, mCountry;
    private ArrayList<FeedItem> items = new ArrayList<>();

    public TabbedFeedFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard_feed,
                container, false);
        global = ((BaseActivity) getActivity()).getGlobal();
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        feedListView = (ListView) rootView.findViewById(R.id.feed_list);
        noFeedItems = (TextView) rootView.findViewById(R.id.no_feed_items);
        refreshIntervalValue = (TextView) rootView.findViewById(R.id.refresh_interval_value);
        feedSourcesValue = (TextView) rootView.findViewById(R.id.feed_sources_value);
        noFeedCard = (LinearLayout) rootView.findViewById(R.id.no_feed_list);
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

        mAutocompleteLocation = (AutoCompleteTextView) rootView.findViewById(R.id.settings_autocomplete);

        mAutocompleteLocation.setHint("Set location");
        mAutocompleteLocation.setAdapter(new GeoCodingAutoCompleteAdapter(getActivity(), R.layout.autocomplete_list_item));
        mAutocompleteLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !global.hasPasswordSet()) {
                    global.setPassword(getActivity());
                }

            }
        });
        LinearLayout refreshInterval = (LinearLayout) rootView.findViewById(R.id.refresh_interval);
        LinearLayout feedSources = (LinearLayout) rootView.findViewById(R.id.feed_sources);
        feedSources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFeedSources();
            }
        });
        refreshInterval.setVisibility(!global.isLoggedIn() ? View.GONE : View.VISIBLE);
        refreshInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.hasPasswordSet()) {
                    showRefresh();
                } else {
                    global.setPassword(getActivity());
                }
            }
        });

        mAutocompleteLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (global.hasPasswordSet()) {
                    UmbrellaUtil.hideSoftKeyboard(getActivity());
                    if (position != 0 && mAddressList != null && mAddressList.size() >= position) {
                        mAddress = mAddressList.get(position - 1);
                        String chosenAddress = mAutocompleteLocation.getText().toString();
                        mAutocompleteLocation.setText(chosenAddress);
                        List<Registry> selLoc = null;
                        try {
                            selLoc = global.getDaoRegistry().queryForEq(Registry.FIELD_NAME, "location");
                        } catch (SQLException e) {
                            UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
                        }
                        if (selLoc!=null && selLoc.size() > 0) {
                            mLocation = selLoc.get(0);
                            mLocation.setValue(chosenAddress);
                        } else {
                            mLocation = new Registry("location", chosenAddress);
                        }
                        List<Registry> selCountry = null;
                        try {
                            selCountry = global.getDaoRegistry().queryForEq(Registry.FIELD_NAME, "country");
                        } catch (SQLException e) {
                            UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
                        }
                        if (selCountry!=null && selCountry.size() > 0) {
                            mCountry = selCountry.get(0);
                            mLocation.setValue(mAddress.getCountryName());
                            try {
                                global.getDaoRegistry().update(mCountry);
                            } catch (SQLException e) {
                                UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
                            }
                        } else {
                            mCountry = new Registry("country", mAddress.getCountryName());
                            try {
                                global.getDaoRegistry().create(mCountry);
                                if (isFeedSet()) getFeeds(getActivity());
                            } catch (SQLException e) {
                                UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
                            }
                        }
                    } else {
                        mAddress = null;
                    }
                } else {
                    global.setPassword(getActivity());
                }
            }
        });
        refreshIntervalValue.setText(global.getRefreshLabel());
        feedSourcesValue.setText(global.getSelectedFeedSourcesLabel());
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
            mSwipeRefreshLayout.setVisibility(items.size() > 0 ? View.VISIBLE : View.GONE);
        } else {
            noFeedCard.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setVisibility(View.GONE);
        }
    }

    public void refreshFeed() {
        refreshView();
        if (isFeedSet()) {
            getFeeds(getActivity());
        } else {
            Toast.makeText(getActivity(), "Please set sources and location in the settings", Toast.LENGTH_SHORT).show();
        }
        mSwipeRefreshLayout.setRefreshing(false);
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
            UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
        }
        if (selCountry!=null && selCountry.size()>0) {
            List<Registry> selections;
            try {
                selections = regDao.queryForEq(Registry.FIELD_NAME, "feed_sources");
                for (Registry selection : selections) {
                    if (selection.getValue().equals("0")) getReliefWeb(selCountry, context);
                    if (selection.getValue().equals("3")) getCDC(context);
                }
                noFeedCard.setVisibility(View.GONE);
                noFeedItems.setText(getResources().getString(R.string.no_feed_updates));
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                return true;
            } catch (SQLException e) {
                UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
            }
            return false;
        } else {
            noFeedCard.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setVisibility(View.GONE);
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
        UmbrellaRestClient.getFeed("https://api.rwlabs.org/v1/countries/?query[value]=" + selCountry.get(0).getValue(), null, context, new JsonHttpResponseHandler() {
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
        UmbrellaRestClient.getFeed("https://api.rwlabs.org/v1/countries/" + countryId, null, context, new JsonHttpResponseHandler() {

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
                UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
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

    public void showRefresh() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                getActivity());
        builderSingle.setTitle("Choose refresh interval:");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.select_dialog_singlechoice);
        int currentRefresh = global.getRefreshValue();
        int selectedIndex = 0;
        int i = 0;
        final HashMap<String, Integer> refreshValues = UmbrellaUtil.getRefreshValues();
        for (Object key : refreshValues.keySet()) {
            if (refreshValues.get(key).equals(currentRefresh)) {
                selectedIndex = i;

            }
            arrayAdapter.add((String) key);
            i++;
        }
        builderSingle.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setSingleChoiceItems(arrayAdapter, selectedIndex,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String chosen = arrayAdapter.getItem(which);
                        for (Object key : refreshValues.keySet()) {
                            Integer value = refreshValues.get(key);
                            if (key.equals(chosen)) {
                                BaseActivity baseAct = ((BaseActivity) getActivity());
                                if (baseAct.mBounded) baseAct.mService.setRefresh(value);
                                global.setRefreshValue(value);
                                refreshIntervalValue.setText(global.getRefreshLabel());
                                if (isFeedSet()) getFeeds(getActivity());
                                dialog.dismiss();
                            }
                        }
                    }
                });
        builderSingle.show();
    }

    public void showFeedSources() {
        final CharSequence[] items = {" ReliefWeb "," UN "," FCO "," CDC "};
        final ArrayList<Integer> selectedItems = new ArrayList<>();
        boolean[] currentSelections = new boolean[items.length];
        List<Registry> selections;
        try {
            selections = global.getDaoRegistry().queryForEq(Registry.FIELD_NAME, "feed_sources");
            for (int i = 0; i < items.length; i++) {
                currentSelections[i] = false;
                for (Registry reg : selections) {
                    if (reg.getValue().equals(String.valueOf(i))) {
                        currentSelections[i] = true;
                        selectedItems.add(i);
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select The Feed Sources");
        builder.setMultiChoiceItems(items, currentSelections,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            selectedItems.add(indexSelected);
                        } else {
                            selectedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        List<Registry> selections;
                        try {
                            selections = global.getDaoRegistry().queryForEq(Registry.FIELD_NAME, "feed_sources");
                            for (Registry selection : selections) {
                                global.getDaoRegistry().delete(selection);
                            }
                        } catch (SQLException e) {
                            UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
                        }
                        for (Integer item : selectedItems) {
                            try {
                                global.getDaoRegistry().create(new Registry("feed_sources", String.valueOf(item)));
                            } catch (SQLException e) {
                                UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
                            }
                        }
                        feedSourcesValue.setText(global.getSelectedFeedSourcesLabel());
                        if (isFeedSet()) getFeeds(getActivity());
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean isFeedSet() {
        if (!global.getSelectedFeedSourcesLabel().equals("") && !global.getRefreshLabel().equals("") && !global.getChosenCountry().equals("")) {
            return true;
        }
        return false;
    }

    private class GeoCodingAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        public GeoCodingAutoCompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return resultList.get(index);
        }

        private List<Address> autoComplete(String input) {
            List<Address> foundGeocode = null;
            Context context = getActivity();
            try {
                foundGeocode = new Geocoder(context).getFromLocationName(input, 7);
                mAddressList = new ArrayList<>(foundGeocode);
            } catch (IOException e) {
                UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
            }

            return foundGeocode;
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        List<Address> list = autoComplete(constraint.toString());
                        ArrayList<String> toStrings = new ArrayList<>();
                        for (Address current : list) {
                            if (!current.getAddressLine(0).equals("")) {
                                String toAdd = current.getAddressLine(0);
                                if (current.getAddressLine(0) != null)
                                    toAdd += " " + current.getAddressLine(1);
                                if (current.getAddressLine(2) != null)
                                    toAdd += " " + current.getAddressLine(2);
                                toStrings.add(toAdd);
                            }
                        }
                        resultList = toStrings;
                        resultList.add(0, "Current location");

                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
        }
    }

}