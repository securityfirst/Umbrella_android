package org.secfirst.umbrella;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.secfirst.umbrella.models.Category;
import org.secfirst.umbrella.models.CheckItem;
import org.secfirst.umbrella.models.Registry;
import org.secfirst.umbrella.models.Segment;
import org.secfirst.umbrella.util.UmbrellaRestClient;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SettingsActivity extends BaseActivity {
    private ProgressDialog mProgress;
    private static int syncDone;
    private AutoCompleteTextView mAutocompleteLocation;
    private ArrayList<Address> mAddressList;
    private Address mAddress;
    private Registry mLocation, mCountry;
    private CheckBox skipPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView refreshData = (TextView) findViewById(R.id.refresh_data);
        TextView refreshInterval = (TextView) findViewById(R.id.refresh_interval);
        TextView feedSources = (TextView) findViewById(R.id.feed_sources);
        mAutocompleteLocation = (AutoCompleteTextView) findViewById(R.id.settings_autocomplete);
        LinearLayout skipPWLayout = (LinearLayout) findViewById(R.id.skip_password_layout);
        skipPw = (CheckBox) findViewById(R.id.skip_password);
        refreshData.setVisibility(View.GONE); // enable when backend ready
        refreshData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (global.hasPasswordSet(false)) {
                    syncApi();
                } else {
                    global.setPassword(SettingsActivity.this);
                }
            }
        });

        refreshInterval.setVisibility(!global.isLoggedIn() ? View.GONE : View.VISIBLE);
        refreshInterval.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.hasPasswordSet(false)) {
                    showRefresh();
                } else {
                    global.setPassword(SettingsActivity.this);
                }
            }
        });
        feedSources.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFeedSources();
            }
        });

        mAutocompleteLocation.setAdapter(new GeoCodingAutoCompleteAdapter(SettingsActivity.this, R.layout.autocomplete_list_item));
        mAutocompleteLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !global.hasPasswordSet(false)) {
                    global.setPassword(SettingsActivity.this);
                }
            }
        });

        mAutocompleteLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (global.hasPasswordSet(false)) {
                    UmbrellaUtil.hideSoftKeyboard(SettingsActivity.this);
                    if (position != 0 && mAddressList != null && mAddressList.size() >= position) {
                        mAddress = mAddressList.get(position - 1);
                        String chosenAddress = mAutocompleteLocation.getText().toString();
                        mAutocompleteLocation.setText(chosenAddress);
                        List<Registry> selLoc = null;
                        try {
                            selLoc = global.getDaoRegistry().queryForEq(Registry.FIELD_NAME, "location");
                        } catch (SQLException e) {
                            UmbrellaUtil.logIt(SettingsActivity.this, Log.getStackTraceString(e.getCause()));
                        }
                        if (selLoc!=null && selLoc.size() > 0) {
                            mLocation = selLoc.get(0);
                            mLocation.setValue(chosenAddress);
                            try {
                                global.getDaoRegistry().update(mLocation);
                            } catch (SQLException e) {
                                UmbrellaUtil.logIt(SettingsActivity.this, Log.getStackTraceString(e.getCause()));
                            }
                        } else {
                            mLocation = new Registry("location", chosenAddress);
                            try {
                                global.getDaoRegistry().create(mLocation);
                            } catch (SQLException e) {
                                UmbrellaUtil.logIt(SettingsActivity.this, Log.getStackTraceString(e.getCause()));
                            }
                        }
                        List<Registry> selISO2 = null;
                        Registry iso2;
                        try {
                            selISO2 = global.getDaoRegistry().queryForEq(Registry.FIELD_NAME, "iso2");
                            if (selISO2.size() > 0) {
                                iso2 = selISO2.get(0);
                                iso2.setValue(mAddress.getCountryCode().toLowerCase());
                                try {
                                    global.getDaoRegistry().update(iso2);
                                } catch (SQLException e) {
                                    UmbrellaUtil.logIt(SettingsActivity.this, Log.getStackTraceString(e.getCause()));
                                }
                            } else {
                                iso2 = new Registry("iso2", mAddress.getCountryCode().toLowerCase());
                                try {
                                    global.getDaoRegistry().create(iso2);
                                } catch (SQLException e) {
                                    UmbrellaUtil.logIt(SettingsActivity.this, Log.getStackTraceString(e.getCause()));
                                }
                            }
                        } catch (SQLException e) {
                            UmbrellaUtil.logIt(SettingsActivity.this, Log.getStackTraceString(e.getCause()));
                        }
                        List<Registry> selCountry = null;
                        try {
                            selCountry = global.getDaoRegistry().queryForEq(Registry.FIELD_NAME, "country");
                            if (selCountry.size() > 0) {
                                mCountry = selCountry.get(0);
                                mCountry.setValue(mAddress.getCountryName());
                                try {
                                    global.getDaoRegistry().update(mCountry);
                                } catch (SQLException e) {
                                    UmbrellaUtil.logIt(SettingsActivity.this, Log.getStackTraceString(e.getCause()));
                                }
                            } else {
                                mCountry = new Registry("country", mAddress.getCountryName());
                                try {
                                    global.getDaoRegistry().create(mCountry);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SettingsActivity.this, R.string.review_sources_for_feed, Toast.LENGTH_SHORT).show();
                                            showFeedSources();
                                        }
                                    }, 500);
                                } catch (SQLException e) {
                                    UmbrellaUtil.logIt(SettingsActivity.this, Log.getStackTraceString(e.getCause()));
                                }
                            }
                        } catch (SQLException e) {
                            UmbrellaUtil.logIt(SettingsActivity.this, Log.getStackTraceString(e.getCause()));
                        }
                    } else {
                        mAddress = null;
                    }
                } else {
                    global.setPassword(SettingsActivity.this);
                }
            }
        });

        skipPw.setChecked(global.getSkipPassword());
        skipPWLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean toChange = !global.getSkipPassword();
                skipPw.setChecked(toChange);
                global.setSkipPassword(toChange);
            }
        });
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_settings;
    }

    public void checkDone() {
        syncDone++;
        if (syncDone==2) mProgress.dismiss();
    }

    public void showRefresh() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(
                SettingsActivity.this);
        builderSingle.setTitle(R.string.choose_refresh_inteval);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                SettingsActivity.this,
                android.R.layout.select_dialog_singlechoice);
        int currentRefresh = global.getRefreshValue();
        int selectedIndex = 0;
        int i = 0;
        final HashMap<String, Integer> refreshValues = UmbrellaUtil.getRefreshValues(SettingsActivity.this);
        for (Map.Entry<String, Integer> entry : refreshValues.entrySet()) {
            if (entry.getValue().equals(currentRefresh)) {
                selectedIndex = i;

            }
            arrayAdapter.add((String) entry.getKey());
            i++;
        }
        builderSingle.setNegativeButton(R.string.cancel,
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
                                if (mBounded) mService.setRefresh(value);
                                global.setRefreshValue(value);
                                dialog.dismiss();
                            }
                        }
                    }
                });
        builderSingle.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Registry> selLoc = null;
        try {
            selLoc = global.getDaoRegistry().queryForEq(Registry.FIELD_NAME, "location");
        } catch (SQLException e) {
            UmbrellaUtil.logIt(SettingsActivity.this, Log.getStackTraceString(e.getCause()));
        }
        if (selLoc!=null && selLoc.size() > 0) {
            mAutocompleteLocation.setHint(selLoc.get(0).getValue());
        } else {
            mAutocompleteLocation.setHint(global.getString(R.string.set_location));
        }
    }

    public void showFeedSources() {
        final CharSequence[] items = global.getFeedSourcesArray();
        final ArrayList<Integer> selectedItems = new ArrayList<Integer>();
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
            UmbrellaUtil.logIt(SettingsActivity.this, Log.getStackTraceString(e.getCause()));
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.select_feed_sources);
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
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        List<Registry> selections = null;
                        try {
                            selections = global.getDaoRegistry().queryForEq(Registry.FIELD_NAME, "feed_sources");
                            for (Registry selection : selections) {
                                global.getDaoRegistry().delete(selection);
                            }
                        } catch (SQLException e) {
                            UmbrellaUtil.logIt(SettingsActivity.this, Log.getStackTraceString(e.getCause()));
                        }
                        for (Integer item : selectedItems) {
                            try {
                                global.getDaoRegistry().create(new Registry("feed_sources", String.valueOf(global.getFeedSourceCodeByIndex(item))));
                            } catch (SQLException e) {
                                UmbrellaUtil.logIt(SettingsActivity.this, Log.getStackTraceString(e.getCause()));
                            }
                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void syncApi() {
        syncDone = 0;
        mProgress = UmbrellaUtil.launchRingDialogWithText(SettingsActivity.this, getString(R.string.checking_for_updates));

        UmbrellaRestClient.get("segments", null, null, SettingsActivity.this, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Gson gson = new GsonBuilder().create();
                Type listType = new TypeToken<ArrayList<Segment>>() {
                }.getType();
                ArrayList<Segment> receivedSegments = gson.fromJson(response.toString(), listType);
                if (receivedSegments!=null && receivedSegments.size() > 0) {
                    global.syncSegments(receivedSegments);
                }
                checkDone();
            }
        });

        UmbrellaRestClient.get("check_items", null, null, SettingsActivity.this, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Gson gson = new GsonBuilder().create();
                Type listType = new TypeToken<ArrayList<CheckItem>>() {
                }.getType();
                ArrayList<CheckItem> receivedItems = gson.fromJson(response.toString(), listType);
                if (receivedItems!=null && receivedItems.size() > 0) {
                    global.syncCheckLists(receivedItems);
                }
                checkDone();
            }
        });

        UmbrellaRestClient.get("categories", null, null, SettingsActivity.this, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Gson gson = new GsonBuilder().create();
                Type listType = new TypeToken<ArrayList<Category>>() {
                }.getType();
                ArrayList<Category> receivedItems = gson.fromJson(response.toString(), listType);
                if (receivedItems!=null && receivedItems.size() > 0) {
                    global.syncCategories(receivedItems);
                }
                checkDone();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
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
            Context context = SettingsActivity.this;
            try {
                foundGeocode = new Geocoder(context).getFromLocationName(input, 7);
                mAddressList = new ArrayList<>(foundGeocode);
            } catch (IOException e) {
                UmbrellaUtil.logIt(SettingsActivity.this, Log.getStackTraceString(e.getCause()));
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
                        ArrayList<String> toStrings = new ArrayList<String>();
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
