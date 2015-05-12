package org.secfirst.umbrella;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SettingsActivity extends BaseActivity {
    private ProgressDialog mProgress;
    private static int syncDone;
    private AutoCompleteTextView mAutocompleteLocation;
    private ArrayList<Address> mAddressList;
    private Address mAddress;
    private Registry mLocation, mCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextView refreshData = (TextView) findViewById(R.id.refresh_data);
        TextView refreshInterval = (TextView) findViewById(R.id.refresh_interval);
        TextView feedSources = (TextView) findViewById(R.id.feed_sources);
        mAutocompleteLocation = (AutoCompleteTextView) findViewById(R.id.settings_autocomplete);
        refreshData.setVisibility(View.GONE); // enable when backend ready
        refreshData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (global.hasPasswordSet()) {
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
                if (global.hasPasswordSet()) {
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
        mAutocompleteLocation.setHint("Set location");
        mAutocompleteLocation.setAdapter(new GeoCodingAutoCompleteAdapter(SettingsActivity.this, R.layout.autocomplete_list_item));
        mAutocompleteLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (global.hasPasswordSet()) {
                } else {
                    global.setPassword(SettingsActivity.this);

                }
            }
        });
        mAutocompleteLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (global.hasPasswordSet()) {
                    UmbrellaUtil.hideSoftKeyboard(SettingsActivity.this);
                    if (position != 0 && mAddressList != null && mAddressList.size() >= position) {
                        mAddress = mAddressList.get(position - 1);
                        String chosenAddress = mAutocompleteLocation.getText().toString();
                        mAutocompleteLocation.setText(chosenAddress);
                        List<Registry> selLoc = Registry.find(Registry.class, "name = ?", "location");
                        if (selLoc.size() > 0) {
                            mLocation = selLoc.get(0);
                            mLocation.setValue(chosenAddress);
                        } else {
                            mLocation = new Registry("location", chosenAddress);
                        }
                        List<Registry> selCountry = Registry.find(Registry.class, "name = ?", "country");
                        if (selCountry.size() > 0) {
                            mCountry = selCountry.get(0);
                            mLocation.setValue(mAddress.getCountryName());
                        } else {
                            mCountry = new Registry("country", mAddress.getCountryName());
                        }
                        mCountry.save();
                    } else {
                        mAddress = null;
                    }
                } else {
                    global.setPassword(SettingsActivity.this);
                }
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
        builderSingle.setTitle("Choose refresh interval:");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                SettingsActivity.this,
                android.R.layout.select_dialog_singlechoice);
        int currentRefresh = UmbrellaUtil.getRefreshValue();
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
                                if (mBounded) mService.setRefresh(value);
                                UmbrellaUtil.setRefreshValue(value);
                                dialog.dismiss();
                            }
                        }
                    }
                });
        builderSingle.show();
    }

    public void showFeedSources() {
        final CharSequence[] items = {" ReliefWeb "," UN "," FCO "," CDC "};
        final ArrayList<Integer> selectedItems = new ArrayList<Integer>();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select The Feed Sources");
        builder.setMultiChoiceItems(items, null,
                new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int indexSelected,
                                        boolean isChecked) {
                        if (isChecked) {
                            selectedItems.add(indexSelected);
                        } else if (selectedItems.contains(indexSelected)) {
                            selectedItems.remove(Integer.valueOf(indexSelected));
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //save selected items
                        Toast.makeText(SettingsActivity.this, "Save sources", Toast.LENGTH_SHORT).show();
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

    public void syncApi() {
        syncDone = 0;
        mProgress = UmbrellaUtil.launchRingDialogWithText(SettingsActivity.this, "Checking for updates");

        UmbrellaRestClient.get("segments", null, null, SettingsActivity.this, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Gson gson = new GsonBuilder().create();
                Type listType = new TypeToken<ArrayList<Segment>>() {
                }.getType();
                ArrayList<Segment> receivedSegments = gson.fromJson(response.toString(), listType);
                if (receivedSegments!=null && receivedSegments.size() > 0) {
                    UmbrellaUtil.syncSegments(receivedSegments);
                    Log.i("segments", "synced");
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
                    UmbrellaUtil.syncCheckLists(receivedItems);
                    Log.i("check items", "synced");
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
                    UmbrellaUtil.syncCategories(receivedItems);
                    Log.i("categories", "synced");
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
                e.printStackTrace();
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
