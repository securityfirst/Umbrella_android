package org.secfirst.umbrella.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.table.TableUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.commons.collections4.ListUtils;
import org.apache.http.Header;
import org.json.JSONArray;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.SettingsActivity;
import org.secfirst.umbrella.models.CategoryItem;
import org.secfirst.umbrella.models.ChecksItem;
import org.secfirst.umbrella.models.Difficulty;
import org.secfirst.umbrella.models.ItemsItem;
import org.secfirst.umbrella.models.Registry;
import org.secfirst.umbrella.util.DelayAutoCompleteTextView;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.UmbrellaRestClient;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class SettingsFragment extends PreferenceFragmentCompat {
    private static final int REQUEST_RINGTONE = 93;

    private int syncDone;
    private Global global;
    private ArrayList<Address> mAddressList;

    ListPreference refreshInterval, selectLanguage;
    Preference serverRefresh, setLocation, feedSources, notificationRingtone;
    SwitchPreferenceCompat skipPassword, showNotifications, notificationVibration;
    private Address mAddress;

    private ProgressDialog progressDialog;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        if (global==null) global = (Global) getContext().getApplicationContext();
        addPreferencesFromResource(R.xml.pref_general);

        skipPassword = (SwitchPreferenceCompat) findPreference("skip_password");
        skipPassword.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Timber.d("skip password %s", o);
                global.setSkipPassword((Boolean) o);
                return true;
            }
        });

        selectLanguage = (ListPreference) findPreference("select_language");
        selectLanguage.setEntries(UmbrellaUtil.getLanguageEntries());
        selectLanguage.setEntryValues(UmbrellaUtil.getLanguageEntryValues());
        selectLanguage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String languageToLoad = (String) newValue;
                global.setRegistry("language", languageToLoad);
                if (getActivity()!=null) ((SettingsActivity) getActivity()).setLocale(languageToLoad);

                return true;
            }
        });

        serverRefresh = findPreference("refresh_data");
        serverRefresh.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (global.hasPasswordSet(false)) {
                    syncApi();
                } else {
                    global.setPassword(getContext(), SettingsFragment.this);
                }
                return true;
            }
        });

        Preference showData = findPreference("show_data");
        showData.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                try {
                    List<CategoryItem> categories = global.getDaoCategoryItem().queryForAll();
                    for (CategoryItem category : categories) {
                        Timber.d("cat %s, child of %s", category.getName(), category.getParent());
                        PreparedQuery<ItemsItem> queryBuilder =
                                global.getDaoItemsItem().queryBuilder().where().eq(ItemsItem.FIELD_CATEGORY, category.getName()).prepare();
                        List<ItemsItem> items = global.getDaoItemsItem().query(queryBuilder);
                        Timber.d("category items for %s, %d", category.getName(), items.size());
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                return true;
            }
        });

        refreshInterval = (ListPreference) findPreference("refresh_interval");
        refreshInterval.setEntries(UmbrellaUtil.getRefreshEntries(getContext()));
        refreshInterval.setEntryValues(UmbrellaUtil.getRefreshEntryValues());
        refreshInterval.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (!global.hasPasswordSet(false)) {
                    global.setPassword(getContext(), SettingsFragment.this);
                    return true;
                }
                return false;
            }
        });
        refreshInterval.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Timber.d("ref change %s", o);
                global.setRefreshValue(Integer.parseInt(o.toString()));
                refreshInterval.setSummary(refreshInterval.getEntries()[refreshInterval.findIndexOfValue(o.toString())]);
                return true;
            }
        });

        setLocation = findPreference("set_location");
        setLocation.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (!global.hasPasswordSet(false)) {
                    global.setPassword(getContext(), SettingsFragment.this);
                } else {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View forDialog = inflater.inflate(R.layout.item_set_location, null);
                    final DelayAutoCompleteTextView cityPrediction = (DelayAutoCompleteTextView) forDialog.findViewById(R.id.set_location);
                    Registry selLoc = global.getRegistry("location");
                    if (selLoc!=null) cityPrediction.setText(selLoc.getValue());
                    cityPrediction.setThreshold(1);
                    cityPrediction.setAdapter(new GeoCodingAutoCompleteAdapter(getContext(), R.layout.autocomplete_list_item));
                    cityPrediction.setLoadingIndicator((android.widget.ProgressBar) forDialog.findViewById(R.id.progress_bar));
                    cityPrediction.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
                        {
                            if (global.hasPasswordSet(false)) {
                                if (position != 0 && mAddressList != null && mAddressList.size() >= position) {
                                    mAddress = mAddressList.get(position - 1);
                                    String chosenAddress = (String) adapterView.getItemAtPosition(position);
                                    cityPrediction.setText(chosenAddress);
                                    cityPrediction.setSelection(cityPrediction.getText().length());
                                    setLocation.setSummary(chosenAddress);

                                    Registry selLoc = global.getRegistry("location");
                                    if (selLoc!=null) {
                                        selLoc.setValue(chosenAddress);
                                        try {
                                            global.getDaoRegistry().update(selLoc);
                                        } catch (SQLException e) {
                                            Timber.e(e);
                                        }
                                    } else {
                                        try {
                                            global.getDaoRegistry().create(new Registry("location", chosenAddress));
                                        } catch (SQLException e) {
                                            Timber.e(e);
                                        }
                                    }
                                    Registry iso2 = global.getRegistry("iso2");
                                    if (iso2!=null) {
                                        iso2.setValue(mAddress.getCountryCode().toLowerCase());
                                        try {
                                            global.getDaoRegistry().update(iso2);
                                        } catch (SQLException e) {
                                            Timber.e(e);
                                        }
                                    } else {
                                        try {
                                            global.getDaoRegistry().create(new Registry("iso2", mAddress.getCountryCode().toLowerCase()));
                                        } catch (SQLException e) {
                                            Timber.e(e);
                                        }
                                    }
                                    Registry selCountry  = global.getRegistry("country");
                                    if (selCountry!=null) {
                                        selCountry.setValue(mAddress.getCountryName());
                                        try {
                                            global.getDaoRegistry().update(selCountry);
                                        } catch (SQLException e) {
                                            Timber.e(e);
                                        }
                                    } else {
                                        try {
                                            global.getDaoRegistry().create(new Registry("country", mAddress.getCountryName()));
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getContext(), R.string.review_sources_for_feed, Toast.LENGTH_SHORT).show();
                                                    showFeedSources();
                                                }
                                            }, 500);
                                        } catch (SQLException e) {
                                            Timber.e(e);
                                        }
                                    }
                                } else {
                                    mAddress = null;
                                }
                            } else {
                                global.setPassword(getContext(), SettingsFragment.this);
                            }
                        }
                    });
                    new MaterialDialog.Builder(getContext())
                            .title(R.string.set_location)
                            .customView(forDialog, true)
                            .negativeText(R.string.cancel)
                            .positiveText(R.string.ok)
                            .show();
                }
                return true;
            }
        });

        feedSources = findPreference("feed_sources");
        feedSources.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (!global.hasPasswordSet(false)) {
                    global.setPassword(getContext(), SettingsFragment.this);
                } else {
                    showFeedSources();
                }
                return true;
            }
        });

        showNotifications = (SwitchPreferenceCompat) findPreference("show_notifications");
        showNotifications.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Boolean visible = (Boolean) newValue;
                global.setNotificationsEnabled(visible);
                toggleNotificationPref(visible);
                return true;
            }
        });

        notificationVibration = (SwitchPreferenceCompat) findPreference("notification_vibration");
        notificationVibration.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean visible = (Boolean) newValue;
                if (visible) {
                    global.setNotificationsEnabled(true);
                }
                global.setNotificationVibrationEnabled(visible);
                toggleVibrationPref(visible);
                return true;
            }
        });

        notificationRingtone = findPreference("notification_ringtone");
        notificationRingtone.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_NOTIFICATION_URI);
                Uri existingRIngtone = global.getNotificationRingtone();
                if (existingRIngtone!=null) {
                    if (existingRIngtone.toString().length() == 0) {
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                    } else {
                        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, existingRIngtone);
                    }
                } else {
                    // No ringtone has been selected, set to the default
                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Settings.System.DEFAULT_NOTIFICATION_URI);
                }
                startActivityForResult(intent, REQUEST_RINGTONE);
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateSummaries();
    }

    public void syncApi() {
        syncDone = 0;
        progressDialog = UmbrellaUtil.launchRingDialogWithText((Activity) getContext(), getString(R.string.checking_for_updates));

        UmbrellaRestClient.get("api/tree?content=html", null, null, getContext(), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                Gson gson = new GsonBuilder().create();
                Type listType = new TypeToken<ArrayList<CategoryItem>>() {}.getType();
                ArrayList<CategoryItem> receivedTree = gson.fromJson(response.toString(), listType);
                try {
                    TableUtils.dropTable(global.getOrmHelper().getConnectionSource(), CategoryItem.class, true);
                    TableUtils.createTableIfNotExists(global.getOrmHelper().getConnectionSource(), CategoryItem.class);
                    TableUtils.dropTable(global.getOrmHelper().getConnectionSource(), ItemsItem.class, true);
                    TableUtils.createTableIfNotExists(global.getOrmHelper().getConnectionSource(), ItemsItem.class);
                    TableUtils.dropTable(global.getOrmHelper().getConnectionSource(), ChecksItem.class, true);
                    TableUtils.createTableIfNotExists(global.getOrmHelper().getConnectionSource(), ChecksItem.class);
                    TableUtils.dropTable(global.getOrmHelper().getConnectionSource(), Difficulty.class, true);
                    TableUtils.createTableIfNotExists(global.getOrmHelper().getConnectionSource(), Difficulty.class);
                    CategoryItem myItem = new CategoryItem();
                    myItem.setName("My Security");
                    global.getDaoCategoryItem().create(myItem);
                    for (CategoryItem categoryItem : receivedTree) {
                        try {
                            if (global.getDaoCategoryItem().create(categoryItem) > 0) {
                                for (CategoryItem subCategoryItem : categoryItem.getSubcategories()) {
                                    if (subCategoryItem.getName().equals("_")) {
                                        subCategoryItem = categoryItem;
                                    } else {
                                        subCategoryItem.setParent(categoryItem.getName());
                                        global.getDaoCategoryItem().create(subCategoryItem);
                                    };
                                    for (ItemsItem itemsItem : ListUtils.emptyIfNull(subCategoryItem.getItems())) {
                                        itemsItem.setCategory(subCategoryItem.getName());
                                        global.getDaoItemsItem().create(itemsItem);
                                    }
                                    for (ChecksItem checksItem : ListUtils.emptyIfNull(subCategoryItem.getChecks())) {
                                        checksItem.setCategory(subCategoryItem.getName());
                                        global.getDaoChecksItem().create(checksItem);
                                    }
                                }
                                for (ItemsItem itemsItem : ListUtils.emptyIfNull(categoryItem.getItems())) {
                                    itemsItem.setCategory(categoryItem.getName());
                                    global.getDaoItemsItem().create(itemsItem);
                                }
                                for (ChecksItem checksItem : ListUtils.emptyIfNull(categoryItem.getChecks())) {
                                    checksItem.setCategory(categoryItem.getName());
                                    global.getDaoChecksItem().create(checksItem);
                                }
                            }
                        } catch (SQLException e) {
                            Timber.e(e);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        });
    }

    public void checkDone() {
        syncDone++;
        if (syncDone==2 && progressDialog!=null) progressDialog.dismiss();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                        global.deleteRegistriesByName("feed_sources");
                        for (Integer item : selectedItems) {
                            Timber.d("sel %s", String.valueOf(global.getFeedSourceCodeByIndex(item)));
                            try {
                                global.getDaoRegistry().create(new Registry("feed_sources", String.valueOf(global.getFeedSourceCodeByIndex(item))));
                            } catch (SQLException e) {
                                Timber.e(e);
                            }
                        }
                        String selectedFeedSources = global.getSelectedFeedSourcesLabel(true);
                        feedSources.setSummary(!selectedFeedSources.equals("") ? selectedFeedSources : global.getString(R.string.feed_sources));
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

    public void updateSummaries() {
        skipPassword.setChecked(global.getSkipPassword());
//        serverRefresh.setVisible(false);
        int refValue = global.getRefreshValue();
        String refLabel = global.getRefreshLabel(refValue);
        refreshInterval.setValue(String.valueOf(refValue));
        refreshInterval.setSummary(!refLabel.equals("") ? refLabel : global.getString(R.string.choose_refresh_inteval));
        Registry selLoc = global.getRegistry("location");
        setLocation.setSummary(selLoc!=null ? selLoc.getValue() : global.getString(R.string.set_location));
        String selectedFeedSources = global.getSelectedFeedSourcesLabel(true);
        feedSources.setSummary(!selectedFeedSources.equals("") ? selectedFeedSources : global.getString(R.string.feed_sources));
        toggleNotificationPref(null);
        toggleVibrationPref(null);
        showNotifications.setChecked(global.getNotificationsEnabled());
        notificationVibration.setChecked(global.getNotificationVibrationEnabled());
        showRingtoneName();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_RINGTONE && resultCode == RESULT_OK) {
            if(data != null) {
                Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                if(uri != null) {

                    global.setNotificationRingtoneEnabled(true);
                    global.setNotificationRingtone(uri);
                } else {
                    global.setNotificationRingtoneEnabled(false);
                }
            }
            showRingtoneName();
        }
    }

    public void toggleNotificationPref(Boolean visible) {
        if (visible==null) {
            visible = global.hasPasswordSet(false) && global.getNotificationsEnabled();
        }
        notificationVibration.setVisible(visible);
        if (!visible || (notificationVibration.isVisible() && notificationVibration.isChecked() )) notificationRingtone.setVisible(visible);
    }

    public void toggleVibrationPref(Boolean visible) {
        if (visible==null) {
            visible = global.hasPasswordSet(false) && global.getNotificationsEnabled() && global.getNotificationVibrationEnabled();
        }
        notificationRingtone.setVisible(visible);
    }

    public void showRingtoneName() {
        notificationRingtone.setSummary(String.format(getString(R.string.notification_ringtone),
                global.getNotificationRingtoneEnabled()
                        ? RingtoneManager.getRingtone(getContext(), global.getNotificationRingtone()).getTitle(getContext())
                        : getString(R.string.none)));
    }

    private class GeoCodingAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
        private ArrayList<String> resultList;

        GeoCodingAutoCompleteAdapter(Context context, int textViewResourceId) {
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
            try {
                foundGeocode = new Geocoder(getContext()).getFromLocationName(input, 7);
                mAddressList = new ArrayList<>(foundGeocode);
            } catch (IOException e) {
                Timber.e(e);
            }

            return foundGeocode;
        }

        @NonNull
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
                            Timber.d("address %s", current);
                            if (!current.getAddressLine(0).equals("")) {
                                String toAdd = current.getAddressLine(0);
                                if (current.getAddressLine(1) != null)
                                    toAdd += ", " + current.getAddressLine(1);
                                if (current.getAddressLine(2) != null)
                                    toAdd += ", " + current.getAddressLine(2);
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
