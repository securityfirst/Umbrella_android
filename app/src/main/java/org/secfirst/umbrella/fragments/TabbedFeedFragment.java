package org.secfirst.umbrella.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.text.InputType;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.secfirst.umbrella.BaseActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.SettingsActivity;
import org.secfirst.umbrella.adapters.FeedAdapter;
import org.secfirst.umbrella.models.FeedItem;
import org.secfirst.umbrella.models.FeedSource;
import org.secfirst.umbrella.models.Registry;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.UmbrellaRestClient;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class TabbedFeedFragment extends Fragment {
    SwipeRefreshLayout mSwipeRefreshLayout;
    FeedAdapter feedAdapter;
    TextView noFeedSettings;
    ScrollView noFeedCard;
    ListView feedListView;
    TextView header;
    TextView refreshIntervalValue;
    TextView feedSourcesValue;
    CardView noFeedItems;
    Global global;
    private AutoCompleteTextView mAutocompleteLocation;
    private ArrayList<Address> mAddressList;
    private Address mAddress;
    private Registry mLocation, mCountry;
    private ArrayList<FeedItem> items = new ArrayList<>();

    public TabbedFeedFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard_feed,
                container, false);
        global = ((BaseActivity) getActivity()).getGlobal();
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFeed();
            }
        });
        mSwipeRefreshLayout.setNestedScrollingEnabled(true);
        feedListView = (ListView) rootView.findViewById(R.id.feed_list);
        noFeedSettings = (TextView) rootView.findViewById(R.id.no_feed_settings);
        noFeedItems = (CardView) rootView.findViewById(R.id.no_feed_items);
        noFeedItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });
        refreshIntervalValue = (TextView) rootView.findViewById(R.id.refresh_interval_value);
        feedSourcesValue = (TextView) rootView.findViewById(R.id.feed_sources_value);
        noFeedCard = (ScrollView) rootView.findViewById(R.id.no_feed_list);
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

        mAutocompleteLocation.setAdapter(new GeoCodingAutoCompleteAdapter(getActivity(), R.layout.autocomplete_list_item));
        mAutocompleteLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !global.hasPasswordSet(false)) {
                    global.setPassword(getActivity(), null);
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
                if (global.hasPasswordSet(false)) {
                    showRefresh();
                } else {
                    global.setPassword(getActivity(), null);
                }
            }
        });

        mAutocompleteLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (global.hasPasswordSet(false)) {
                    UmbrellaUtil.hideSoftKeyboard(getActivity());
                    if (position != 0 && mAddressList != null && mAddressList.size() >= position) {
                        mAddress = mAddressList.get(position - 1);
                        String chosenAddress = mAutocompleteLocation.getText().toString();
                        mAutocompleteLocation.setText(chosenAddress);
                        global.setRegistry("location", chosenAddress);
                        global.setRegistry("iso2", mAddress.getCountryCode().toLowerCase());
                        global.setRegistry("country", mAddress.getCountryName());
                    } else {
                        mAddress = null;
                    }
                } else {
                    global.setPassword(getActivity(), null);
                }
            }
        });
        mAutocompleteLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        refreshIntervalValue.setText(global.getRefreshLabel(null));
        feedSourcesValue.setText(global.getSelectedFeedSourcesLabel(false));
        getFeeds(getActivity());
        refreshView();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Registry selLoc = global.getRegistry("location");
        mAutocompleteLocation.setHint(selLoc!=null ? selLoc.getValue() : global.getString(R.string.set_location));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.menu_sources, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.add_source:
                new MaterialDialog.Builder(getContext())
                        .title(R.string.add_feed_source_title)
                        .content(R.string.add_feed_source_body)
                        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_URI)
                        .input(getString(R.string.add_feed_source_hint), "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                if (Patterns.WEB_URL.matcher(input).matches()) {
                                    try {
                                        List<FeedSource> sourceExists = global.getDaoFeedSource().queryForEq(FeedSource.FIELD_URL, input);
                                        if (sourceExists!=null && sourceExists.size()>0) {
                                            Toast.makeText(getContext(), R.string.source_already_added, Toast.LENGTH_SHORT).show();
                                        } else {
                                            global.getDaoFeedSource().create(new FeedSource(input.toString(), 0, input.toString()));
                                            Toast.makeText(getContext(), String.format("Source %s was successfully added", input.toString()), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Toast.makeText(getContext(), R.string.string_not_valid_url, Toast.LENGTH_SHORT).show();
                                    dialog.show();
                                }
                            }
                        }).show();
                break;
            case R.id.manage_sources:
                try {
                    List<FeedSource> externalSources = global.getDaoFeedSource().queryForAll();
                    if (externalSources!=null && externalSources.size()>0) {
                        ArrayList<String> sourceUrls = new ArrayList<String>();
                        for (FeedSource externalSource : externalSources) {
                            if (externalSource.getUrl()!=null && !externalSource.getUrl().equals("")) {
                                sourceUrls.add(externalSource.getUrl());
                            }
                        }
                        if (sourceUrls.size()>0) {
                            new MaterialDialog.Builder(getContext())
                                    .title(R.string.delete_external_sources)
                                    .items(sourceUrls)
                                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                        @Override
                                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                            Timber.d("text %s", text);
                                            try {
                                                DeleteBuilder<FeedSource, String> toDelete = global.getDaoFeedSource().deleteBuilder();
                                                toDelete.where().eq(FeedSource.FIELD_URL, text);
                                                toDelete.delete();
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                            return true;
                                        }
                                    })
                                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .negativeText(R.string.cancel)
                                    .show();
                        } else {
                            Toast.makeText(getContext(), R.string.no_external_sources_found, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), R.string.no_external_sources_found, Toast.LENGTH_SHORT).show();
                    }
                } catch (SQLException e) {
                    Timber.e(e);
                    Toast.makeText(getContext(), R.string.no_external_sources_found, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.list_items:
                try {
                    for (FeedItem feedItem : global.getDaoFeedItem().queryForAll()) {
                        Timber.d("fi %s", feedItem);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.remove_items:
                try {
                    DeleteBuilder<FeedItem, String> db = global.getDaoFeedItem().deleteBuilder();
                    db.delete();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                refreshView();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refreshView() {
        ArrayList<FeedItem> items = new ArrayList<>(global.getFeedItems());
        feedAdapter.updateData(items);
        Registry selCountry = global.getRegistry("country");
        String headerText = "";
        if (selCountry!=null)
            headerText = global.getString(R.string.country_selected)+": " + selCountry.getValue() + "\n";
        mSwipeRefreshLayout.setVisibility(isFeedSet() ? View.VISIBLE : View.GONE);
        noFeedItems.setVisibility((isFeedSet() && (items.size() == 0)) ? View.VISIBLE : View.GONE);
        headerText += global.getString(R.string.lat_updated)+": " + DateFormat.getDateTimeInstance().format(new Date(global.getFeedItemsRefreshed()));
        feedListView.setVisibility(isFeedSet() && items.size()>0 ? View.VISIBLE : View.GONE);
        noFeedCard.setVisibility(isFeedSet() ? View.GONE : View.VISIBLE);
        noFeedCard.setVisibility( (items.size()>0 && isFeedSet()) ? View.GONE : View.VISIBLE);
        header.setText(headerText);
    }

    public void refreshFeed() {
        refreshView();
        if (isFeedSet()) {
            getFeeds(getActivity());
        } else {
            Toast.makeText(getActivity(), R.string.set_location_source_in_settings, Toast.LENGTH_SHORT).show();
        }
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public boolean getFeeds(final Context context) {
        Registry selISO2 = global.getRegistry("iso2");
        if (selISO2!=null) {
            List<Registry> selections;
            try {
                selections = global.getDaoRegistry().queryForEq(Registry.FIELD_NAME, "feed_sources");
                if (selections.size()>0) {
                    String separator = ",";
                    int total = selections.size() * separator.length();
                    for (Registry item : selections) {
                        total += item.getValue().length();
                    }
                    StringBuilder sb = new StringBuilder(total);
                    for (Registry item : selections) {
                        sb.append(separator).append(item.getValue());
                    }
                    String sources = sb.substring(separator.length());
                    String mUrl = "feed?country=" + selISO2.getValue() + "&sources=" + sources + "&since="+global.getFeedItemsRefreshed();
                    Timber.d("url %s", mUrl);
                    UmbrellaRestClient.get(mUrl, null, "", context, new JsonHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            super.onSuccess(statusCode, headers, response);
                            Gson gson = new GsonBuilder().create();
                            Type listType = new TypeToken<ArrayList<FeedItem>>() {
                            }.getType();
                            ArrayList<FeedItem> receivedItems = gson.fromJson(response.toString(), listType);
                            if (receivedItems != null && receivedItems.size() > 0) {
                                for (FeedItem receivedItem : receivedItems) {
                                    try {
                                        global.getDaoFeedItem().create(receivedItem);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                                refreshView();
                                feedListView.smoothScrollToPosition(0);
                                mSwipeRefreshLayout.setRefreshing(false);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                            super.onFailure(statusCode, headers, responseString, throwable);
                            refreshView();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), R.string.no_sources_selected, Toast.LENGTH_SHORT).show();
                }
                return true;
            } catch (SQLException e) {
                Timber.e(e);
            }
            return false;
        } else {
            noFeedCard.setVisibility(View.VISIBLE);
            feedListView.setVisibility(View.GONE);
            return false;
        }
    }

    public void showRefresh() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                getActivity());
        builderSingle.setTitle(global.getString(R.string.choose_refresh_inteval));
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.select_dialog_singlechoice);
        int currentRefresh = global.getRefreshValue();
        int selectedIndex = 0;
        int i = 0;
        final HashMap<String, Integer> refreshValues = UmbrellaUtil.getRefreshValues(global.getApplicationContext());
        for (Object key : refreshValues.keySet()) {
            if (refreshValues.get(key).equals(currentRefresh)) {
                selectedIndex = i;

            }
            arrayAdapter.add((String) key);
            i++;
        }
        builderSingle.setNegativeButton(global.getString(R.string.cancel),
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
                                refreshIntervalValue.setText(global.getRefreshLabel(null));
                                refreshFeed();
                                dialog.dismiss();
                            }
                        }
                    }
                });
        builderSingle.show();
    }

    public void showFeedSources() {
        final CharSequence[] items = global.getFeedSourcesArray();
        final ArrayList<Integer> selectedItems = new ArrayList<>();
        boolean[] currentSelections = new boolean[items.length];
        List<Registry> selections;
        try {
            selections = global.getDaoRegistry().queryForEq(Registry.FIELD_NAME, "feed_sources");
            for (int i = 0; i < items.length; i++) {
                currentSelections[i] = false;
                for (Registry reg : selections) {
                    if (reg.getValue().equals(String.valueOf(global.getFeedSourceCodeByIndex(i)))) {
                        currentSelections[i] = true;
                        selectedItems.add(i);
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            Timber.e(e);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(global.getString(R.string.select_feed_sources));
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
                .setPositiveButton(global.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        List<Registry> selections;
                        try {
                            selections = global.getDaoRegistry().queryForEq(Registry.FIELD_NAME, "feed_sources");
                            for (Registry selection : selections) {
                                global.getDaoRegistry().delete(selection);
                            }
                        } catch (SQLException e) {
                            Timber.e(e);
                        }
                        for (Integer item : selectedItems) {
                            try {
                                global.getDaoRegistry().create(new Registry("feed_sources", String.valueOf(global.getFeedSourceCodeByIndex(item))));
                            } catch (SQLException e) {
                                Timber.e(e);
                            }
                        }
                        feedSourcesValue.setText(global.getSelectedFeedSourcesLabel(false));
                        refreshFeed();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(global.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean isFeedSet() {
        return !global.getSelectedFeedSourcesLabel(false).equals("") && !global.getRefreshLabel(null).equals("") && !global.getChosenCountry().equals("");
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
                Timber.e(e);
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
                        resultList.add(0, global.getString(R.string.current_location));

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