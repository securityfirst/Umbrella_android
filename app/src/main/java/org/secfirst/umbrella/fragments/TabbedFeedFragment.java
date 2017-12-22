package org.secfirst.umbrella.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
import org.json.JSONObject;
import org.secfirst.umbrella.BaseActivity;
import org.secfirst.umbrella.LocationDialog;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.models.FeedItem;
import org.secfirst.umbrella.models.FeedSource;
import org.secfirst.umbrella.models.Registry;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.OnLocationEventListener;
import org.secfirst.umbrella.util.UmbrellaRestClient;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class TabbedFeedFragment extends Fragment implements OnLocationEventListener {

    private TextView mNoFeedSettings;
    private TextView mRefreshIntervalValue;
    private TextView mFeedSourcesValue;
    private Global mGlobal;
    private TextView mLocationLabel;
    private String mLocation;
    private boolean mChangeLocation;
    private boolean mOpenList;
    private LinearLayout mUndefinedButton;

    public static TabbedFeedFragment newInstance(boolean isChangeLocation) {
        Bundle args = new Bundle();
        TabbedFeedFragment fragment = new TabbedFeedFragment();
        fragment.setArguments(args);
        fragment.mChangeLocation = isChangeLocation;
        return fragment;
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
        mGlobal = ((BaseActivity) getActivity()).getGlobal();
        mLocationLabel = (TextView) rootView.findViewById(R.id.location_label);
        mNoFeedSettings = (TextView) rootView.findViewById(R.id.no_feed_settings);
        mRefreshIntervalValue = (TextView) rootView.findViewById(R.id.refresh_interval_value);
        mFeedSourcesValue = (TextView) rootView.findViewById(R.id.feed_sources_value);
        mUndefinedButton = (LinearLayout) rootView.findViewById(R.id.feed_undefined_button);

        LinearLayout footer = new LinearLayout(getActivity());
        footer.setOrientation(LinearLayout.HORIZONTAL);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 50);
        footer.setLayoutParams(lp);

        LinearLayout locationLayout = (LinearLayout) rootView.findViewById(R.id.location_layout);
        locationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                LocationDialog custom = LocationDialog.newInstance(TabbedFeedFragment.this);
                custom.show(fragmentManager, "");
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

        refreshInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGlobal.hasPasswordSet(false)) {
                    showRefresh();
                } else {
                    mGlobal.setPassword(getActivity(), null);
                }
            }
        });

        mRefreshIntervalValue.setText(mGlobal.getRefreshLabel(null));
        setFeedSourceLabel();
        getFeeds(getActivity(), false);
        setUndefinedListenerButton();
        return rootView;
    }

    private void setUndefinedListenerButton() {
        mUndefinedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    List<Registry> selections = mGlobal.getDaoRegistry().queryForEq(Registry.FIELD_NAME, "feed_sources");
                    if (selections.isEmpty()) {
                        showFeedSources();
                    } else if (mLocationLabel.getText().equals(getString(R.string.feed_location_label))) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        LocationDialog custom = LocationDialog.newInstance(TabbedFeedFragment.this);
                        custom.show(fragmentManager, "");
                    } else {
                        getFeeds(getContext(), false);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setFeedSourceLabel() {
        if (!mGlobal.getSelectedFeedSourcesLabel(false).equals(""))
            mFeedSourcesValue.setText(mGlobal.getSelectedFeedSourcesLabel(false));
    }

    private void verifyDefaultColor() {

        if (mFeedSourcesValue.getText().equals(getString(R.string.set_sources))) {
            mFeedSourcesValue.setTextColor(mNoFeedSettings.getCurrentTextColor());
        } else
            mFeedSourcesValue.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_green_dark));


        if (mLocationLabel.getText().equals(getString(R.string.feed_location_label))) {
            mLocationLabel.setTextColor(mNoFeedSettings.getCurrentTextColor());
        } else
            mLocationLabel.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_green_dark));

    }

    @Override
    public void onResume() {
        super.onResume();
        Registry selLoc = mGlobal.getRegistry("mLocation");
        if (selLoc != null) {
            mLocationLabel.setText(selLoc.getValue());
            mLocation = selLoc.getValue();

        } else {
            mGlobal.getString(R.string.set_location);
        }
        verifyDefaultColor();

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
                                        List<FeedSource> sourceExists = mGlobal.getDaoFeedSource().queryForEq(FeedSource.FIELD_URL, input);
                                        if (sourceExists != null && sourceExists.size() > 0) {
                                            Toast.makeText(getContext(), R.string.source_already_added, Toast.LENGTH_SHORT).show();
                                        } else {
                                            mGlobal.getDaoFeedSource().create(new FeedSource(input.toString(), 0, input.toString()));
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
                    List<FeedSource> externalSources = mGlobal.getDaoFeedSource().queryForAll();
                    if (externalSources != null && externalSources.size() > 0) {
                        ArrayList<String> sourceUrls = new ArrayList<String>();
                        for (FeedSource externalSource : externalSources) {
                            if (externalSource.getUrl() != null && !externalSource.getUrl().equals("")) {
                                sourceUrls.add(externalSource.getUrl());
                            }
                        }
                        if (sourceUrls.size() > 0) {
                            new MaterialDialog.Builder(getContext())
                                    .title(R.string.delete_external_sources)
                                    .items(sourceUrls)
                                    .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                                        @Override
                                        public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                            try {
                                                DeleteBuilder<FeedSource, String> toDelete = mGlobal.getDaoFeedSource().deleteBuilder();
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
                    for (FeedItem feedItem : mGlobal.getDaoFeedItem().queryForAll()) {
                        Timber.d("fi %s", feedItem);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                break;
            case R.id.remove_items:
                try {
                    DeleteBuilder<FeedItem, String> db = mGlobal.getDaoFeedItem().deleteBuilder();
                    db.delete();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void refreshFeed(boolean feedSourceClicked) {
        if (isFeedSet()) {
            getFeeds(getActivity(), feedSourceClicked);
        } else {
            Toast.makeText(getActivity(), R.string.set_location_source_in_settings, Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteOldFeedItems() {
        Registry selLoc = mGlobal.getRegistry("mLocation");
        try {
            if (selLoc != null && mLocation != null && (!mLocation.equals(selLoc.getValue())))
                mGlobal.getDaoFeedItem().delete(mGlobal.getFeedItems());
        } catch (SQLException e) {
            Toast.makeText(getActivity(), R.string.no_results_label, Toast.LENGTH_SHORT).show();
        }


    }

    public boolean getFeeds(final Context context, final boolean feedSourceClicked) {
        Registry selISO2 = mGlobal.getRegistry("iso2");
        if (!mChangeLocation) {
            if (selISO2 != null) {
                List<Registry> selections;
                try {
                    selections = mGlobal.getDaoRegistry().queryForEq(Registry.FIELD_NAME, "feed_sources");
                    if (selections.size() > 0) {
                        String separator = ",";
                        int total = selections.size() * separator.length();
                        for (Registry item : selections) {
                            total += item.getValue().length();
                        }
                        StringBuilder sb = new StringBuilder(total);
                        for (Registry item : selections) {
                            sb.append(separator).append(item.getValue());
                        }

                        //TODO remove since "since=0" before commit this code.
                        // *mGlobal.getFeedItemsRefreshed()

                        String sources = sb.substring(separator.length());
                        final String mUrl = "feed?country=" + selISO2.getValue() + "&sources=" + sources
                                + "&since=" + 0;

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
                                            mGlobal.getDaoFeedItem().create(receivedItem);
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                                            .beginTransaction();
                                    transaction.replace(R.id.root_frame, FeedListFragment.
                                            newInstance(receivedItems));
                                    transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
                                    transaction.addToBackStack(null);
                                    transaction.commit();
                                } else {
                                    if (feedSourceClicked) {
                                        FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                                                .beginTransaction();
                                        transaction.replace(R.id.root_frame, FeedEmptyFragment.
                                                newInstance(mGlobal.getRegistry("mLocation").getValue()));
                                        transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
                                        transaction.addToBackStack(null);
                                        transaction.commit();
                                    }

                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                                super.onFailure(statusCode, headers, throwable, errorResponse);
                                if (throwable instanceof javax.net.ssl.SSLPeerUnverifiedException) {
                                    Toast.makeText(getContext(), "The SSL certificate pin is not valid." +
                                            " Most likely the certificate has expired and was renewed. Update " +
                                            "the app to refresh the accepted pins", Toast.LENGTH_LONG).show();
                                }
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
            }
        }
        mChangeLocation = false;
        return false;
    }

    public void showRefresh() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                getActivity());
        builderSingle.setTitle(mGlobal.getString(R.string.choose_refresh_inteval));
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.select_dialog_singlechoice);
        int currentRefresh = mGlobal.getRefreshValue();
        int selectedIndex = 0;
        int i = 0;
        final Map<String, Integer> refreshValues = UmbrellaUtil.getRefreshValues(mGlobal.getApplicationContext());
        for (Map.Entry<String, Integer> entry : refreshValues.entrySet()) {
            if (entry.getValue().equals(currentRefresh)) {
                selectedIndex = i;
            }
            arrayAdapter.add(entry.getKey());
            i++;
        }
        builderSingle.setNegativeButton(mGlobal.getString(R.string.cancel),
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
                        for (Map.Entry<String, Integer> entry : refreshValues.entrySet()) {
                            Integer value = entry.getValue();
                            if (entry.getKey().equals(chosen)) {
                                BaseActivity baseAct = ((BaseActivity) getActivity());
                                if (baseAct.mBounded) baseAct.mService.setRefresh(value);
                                mGlobal.setRefreshValue(value);
                                mRefreshIntervalValue.setText(mGlobal.getRefreshLabel(null));
                                refreshFeed(false);
                                dialog.dismiss();
                            }
                        }
                    }
                });
        builderSingle.show();
    }

    public void showFeedSources() {
        final CharSequence[] items = mGlobal.getFeedSourcesArray();
        final ArrayList<Integer> selectedItems = new ArrayList<>();
        boolean[] currentSelections = new boolean[items.length];
        List<Registry> selections;
        try {
            selections = mGlobal.getDaoRegistry().queryForEq(Registry.FIELD_NAME, "feed_sources");
            for (int i = 0; i < items.length; i++) {
                currentSelections[i] = false;
                for (Registry reg : selections) {
                    if (reg.getValue().equals(String.valueOf(mGlobal.getFeedSourceCodeByIndex(i)))) {
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
        builder.setTitle(mGlobal.getString(R.string.select_feed_sources));
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
                .setPositiveButton(mGlobal.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        List<Registry> selections;
                        try {
                            selections = mGlobal.getDaoRegistry().queryForEq(Registry.FIELD_NAME, "feed_sources");
                            for (Registry selection : selections) {
                                mGlobal.getDaoRegistry().delete(selection);
                            }
                        } catch (SQLException e) {
                            Timber.e(e);
                        }
                        for (Integer item : selectedItems) {
                            try {
                                mGlobal.getDaoRegistry().create(new Registry("feed_sources",
                                        String.valueOf(mGlobal.getFeedSourceCodeByIndex(item))));
                                mOpenList = true;
                            } catch (SQLException e) {
                                Timber.e(e);
                            }
                        }
                        deleteOldFeedItems();
                        mFeedSourcesValue.setText(mGlobal.getSelectedFeedSourcesLabel(false));
                        refreshFeed(true);
                        verifyDefaultColor();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(mGlobal.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public boolean isFeedSet() {
        return !mGlobal.getRefreshLabel(null).equals("")
                && !mGlobal.getChosenCountry().equals("");
    }

    @Override
    public void locationEvent(String currentLocation) {
        mLocationLabel.setText(currentLocation);
        mLocationLabel.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_green_dark));
        mGlobal.setRegistry("mLocation", currentLocation);
        if (mOpenList)
            getFeeds(getContext(), false);
    }
}