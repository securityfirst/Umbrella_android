package org.secfirst.umbrella.fragments;

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
import android.support.v4.app.FragmentManager;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.secfirst.umbrella.CalcActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.SettingsActivity;
import org.secfirst.umbrella.TourActivity;
import org.secfirst.umbrella.models.Registry;
import org.secfirst.umbrella.util.DelayAutoCompleteTextView;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.OrmHelper;
import org.secfirst.umbrella.util.SyncProgressListener;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.media.RingtoneManager.getRingtone;

public class SettingsFragment extends PreferenceFragmentCompat implements SyncProgressListener {
    private static final int REQUEST_RINGTONE = 93;
    private ArrayList<Address> mAddressList;
    private static final int STORAGE_PERMISSION_RC = 69;
    private ListPreference refreshInterval, selectLanguage;
    private Preference serverRefresh, setLocation, feedSources, notificationRingtone, databasePreference;
    private SwitchPreferenceCompat skipPassword, showNotifications, notificationVibration;
    private Address mAddress;
    private MaterialDialog materialDialog;
    private static boolean isDeleteDatabase;



    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        addPreferencesFromResource(R.xml.pref_general);

        skipPassword = (SwitchPreferenceCompat) findPreference("skip_password");
        skipPassword.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Global.INSTANCE.setSkipPassword((Boolean) o);
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
                Global.INSTANCE.setRegistry("language", languageToLoad);
                if (getActivity() != null)
                    ((SettingsActivity) getActivity()).setLocale(languageToLoad);

                materialDialog = new MaterialDialog.Builder(getContext())
                        .title(R.string.update_from_server)
                        .content(R.string.downloading)
                        .cancelable(false)
                        .autoDismiss(false)
                        .progress(false, 100, false)
                        .show();
                Global.INSTANCE.syncApi(getActivity(), SettingsFragment.this);
                return true;
            }
        });

        serverRefresh = findPreference("refresh_data");
        serverRefresh.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                materialDialog = new MaterialDialog.Builder(getContext())
                        .title(R.string.update_from_server)
                        .content(R.string.downloading)
                        .cancelable(false)
                        .autoDismiss(false)
                        .progress(false, 100, false)
                        .show();
                Global.INSTANCE.syncApi(getActivity(), SettingsFragment.this);
                return true;
            }
        });

        databasePreference = findPreference("share_db_file");
        databasePreference.setOnPreferenceClickListener(preference -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            BackupDatabaseDialog custom = BackupDatabaseDialog.newInstance();
            custom.show(fragmentManager, "");
            return true;
        });

        Preference showData = findPreference("mask_app");
        showData.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new MaterialDialog.Builder(getContext())
                        .title(R.string.masking_mode_title)
                        .content(getString(R.string.masking_mode_body, getString(R.string.app_calc)))
                        .positiveText(R.string.ok)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                UmbrellaUtil.setMaskMode(getActivity(), true);
                                if (Global.INSTANCE.hasPasswordSet(false))
                                    Global.INSTANCE.logout(getActivity(), false);
                                Intent i = new Intent(getActivity(), CalcActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                getActivity().finish();
                            }
                        })
                        .show();
                return true;
            }
        });

        refreshInterval = (ListPreference) findPreference("refresh_interval");
        refreshInterval.setEntries(UmbrellaUtil.getRefreshEntries(getContext()));
        refreshInterval.setEntryValues(UmbrellaUtil.getRefreshEntryValues(getContext()));
        refreshInterval.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (!Global.INSTANCE.hasPasswordSet(false)) {
                    Global.INSTANCE.setPassword(getContext(), SettingsFragment.this);
                    return true;
                }
                return false;
            }
        });
        refreshInterval.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                Global.INSTANCE.setRefreshValue(Integer.parseInt(o.toString()));
                refreshInterval.setSummary(refreshInterval.getEntries()[refreshInterval.findIndexOfValue(o.toString())]);
                return true;
            }
        });

        setLocation = findPreference("set_location");
        setLocation.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if (!Global.INSTANCE.hasPasswordSet(false)) {
                    Global.INSTANCE.setPassword(getContext(), SettingsFragment.this);
                } else {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View forDialog = inflater.inflate(R.layout.item_set_location, null);
                    final DelayAutoCompleteTextView cityPrediction = (DelayAutoCompleteTextView) forDialog.findViewById(R.id.set_location);
                    Registry selLoc = Global.INSTANCE.getRegistry("location");
                    if (selLoc != null) cityPrediction.setText(selLoc.getValue());
                    cityPrediction.setThreshold(1);
                    cityPrediction.setAdapter(new GeoCodingAutoCompleteAdapter(getContext(), R.layout.autocomplete_list_item));
                    cityPrediction.setLoadingIndicator((android.widget.ProgressBar) forDialog.findViewById(R.id.progress_bar));
                    cityPrediction.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            if (Global.INSTANCE.hasPasswordSet(false)) {
                                if (position != 0 && mAddressList != null && mAddressList.size() >= position) {
                                    mAddress = mAddressList.get(position - 1);
                                    String chosenAddress = (String) adapterView.getItemAtPosition(position);
                                    cityPrediction.setText(chosenAddress);
                                    cityPrediction.setSelection(cityPrediction.getText().length());
                                    setLocation.setSummary(chosenAddress);

                                    Registry selLoc = Global.INSTANCE.getRegistry("location");
                                    if (selLoc != null) {
                                        selLoc.setValue(chosenAddress);
                                        try {
                                            Global.INSTANCE.getDaoRegistry().update(selLoc);
                                        } catch (SQLException e) {
                                            Timber.e(e);
                                        }
                                    } else {
                                        try {
                                            Global.INSTANCE.getDaoRegistry().create(new Registry("location", chosenAddress));
                                        } catch (SQLException e) {
                                            Timber.e(e);
                                        }
                                    }
                                    Registry iso2 = Global.INSTANCE.getRegistry("iso2");
                                    if (iso2 != null) {
                                        iso2.setValue(mAddress.getCountryCode().toLowerCase());
                                        try {
                                            Global.INSTANCE.getDaoRegistry().update(iso2);
                                        } catch (SQLException e) {
                                            Timber.e(e);
                                        }
                                    } else {
                                        try {
                                            Global.INSTANCE.getDaoRegistry().create(new Registry("iso2", mAddress.getCountryCode().toLowerCase()));
                                        } catch (SQLException e) {
                                            Timber.e(e);
                                        }
                                    }
                                    Registry selCountry = Global.INSTANCE.getRegistry("country");
                                    if (selCountry != null) {
                                        selCountry.setValue(mAddress.getCountryName());
                                        try {
                                            Global.INSTANCE.getDaoRegistry().update(selCountry);
                                        } catch (SQLException e) {
                                            Timber.e(e);
                                        }
                                    } else {
                                        try {
                                            Global.INSTANCE.getDaoRegistry().create(new Registry("country", mAddress.getCountryName()));
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
                                Global.INSTANCE.setPassword(getContext(), SettingsFragment.this);
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
                if (!Global.INSTANCE.hasPasswordSet(false)) {
                    Global.INSTANCE.setPassword(getContext(), SettingsFragment.this);
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
                Global.INSTANCE.setNotificationsEnabled(visible);
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
                    Global.INSTANCE.setNotificationsEnabled(true);
                }
                Global.INSTANCE.setNotificationVibrationEnabled(visible);
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
                Uri existingRIngtone = Global.INSTANCE.getNotificationRingtone();
                if (existingRIngtone != null) {
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
        if (isDeleteDatabase) {
            Global.INSTANCE.closeDbAndDAOs();
            Global.deleteDatabase(getContext().getDatabasePath(OrmHelper.DATABASE_NAME));
            Global.INSTANCE.removeSharedPreferences();
            Intent i = new Intent(getContext(), TourActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            isDeleteDatabase = false;
        }
    }


    public void showFeedSources() {
        final CharSequence[] items = Global.INSTANCE.getFeedSourcesArray();
        final ArrayList<Integer> selectedItems = new ArrayList<>();
        boolean[] currentSelections = new boolean[items.length];
        List<Registry> selections;
        try {
            selections = Global.INSTANCE.getDaoRegistry().queryForEq(Registry.FIELD_NAME, "feed_sources");
            for (int i = 0; i < items.length; i++) {
                currentSelections[i] = false;
                for (Registry reg : selections) {
                    if (reg.getValue().equals(String.valueOf(Global.INSTANCE.getFeedSourceCodeByIndex(i)))) {
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
                        Global.INSTANCE.deleteRegistriesByName("feed_sources");
                        for (Integer item : selectedItems) {
                            try {
                                Global.INSTANCE.getDaoRegistry().create(new Registry("feed_sources",
                                        String.valueOf(Global.INSTANCE.getFeedSourceCodeByIndex(item))));
                            } catch (SQLException e) {
                                Timber.e(e);
                            }
                        }
                        String selectedFeedSources = Global.INSTANCE.getSelectedFeedSourcesLabel(true);
                        feedSources.setSummary(!selectedFeedSources.equals("") ? selectedFeedSources
                                : Global.INSTANCE.getString(R.string.feed_sources));

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
        Registry language = Global.INSTANCE.getRegistry("language");
        if (language == null || language.getValue().equals(""))
            language = new Registry("language", "en");
        selectLanguage.setSummary(UmbrellaUtil.getLanguageEntryByValue(language.getValue()));
        selectLanguage.setValue(language.getValue());
        skipPassword.setChecked(Global.INSTANCE.getSkipPassword());
        int refValue = Global.INSTANCE.getRefreshValue();
        String refLabel = Global.INSTANCE.getRefreshLabel(refValue);
        refreshInterval.setValue(String.valueOf(refValue));
        refreshInterval.setSummary(!refLabel.equals("") ? refLabel : Global.INSTANCE.getString(R.string.choose_refresh_inteval));
        Registry selLoc = Global.INSTANCE.getRegistry("location");
        setLocation.setSummary(selLoc != null ? selLoc.getValue() : Global.INSTANCE.getString(R.string.set_location));
        String selectedFeedSources = Global.INSTANCE.getSelectedFeedSourcesLabel(true);
        feedSources.setSummary(!selectedFeedSources.equals("") ? selectedFeedSources : Global.INSTANCE.getString(R.string.feed_sources));
        toggleNotificationPref(null);
        toggleVibrationPref(null);
        showNotifications.setChecked(Global.INSTANCE.getNotificationsEnabled());
        notificationVibration.setChecked(Global.INSTANCE.getNotificationVibrationEnabled());
        showRingtoneName();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_RINGTONE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                if (uri != null) {

                    Global.INSTANCE.setNotificationRingtoneEnabled(true);
                    Global.INSTANCE.setNotificationRingtone(uri);
                } else {
                    Global.INSTANCE.setNotificationRingtoneEnabled(false);
                }
            }
            showRingtoneName();
        }
    }

    public void toggleNotificationPref(Boolean visible) {
        if (visible == null) {
            visible = Global.INSTANCE.hasPasswordSet(false) && Global.INSTANCE.getNotificationsEnabled();
        }
        notificationVibration.setVisible(visible);
        if (!visible || (notificationVibration.isVisible() && notificationVibration.isChecked()))
            notificationRingtone.setVisible(visible);
    }

    public void toggleVibrationPref(Boolean visible) {
        if (visible == null) {
            visible = Global.INSTANCE.hasPasswordSet(false) && Global.INSTANCE.getNotificationsEnabled() && Global.INSTANCE.getNotificationVibrationEnabled();
        }
        notificationRingtone.setVisible(visible);
    }

    public void showRingtoneName() {
        notificationRingtone.setSummary(String.format(getString(R.string.notification_ringtone),
                Global.INSTANCE.getNotificationRingtoneEnabled() && RingtoneManager.getRingtone(getContext(), Global.INSTANCE.getNotificationRingtone()) != null
                        ? getRingtone(getContext(), Global.INSTANCE.getNotificationRingtone()).getTitle(getContext())
                        : getString(R.string.none)));
    }

    @Override
    public void onProgressChange(int progress) {
        if (materialDialog != null && !materialDialog.isCancelled())
            materialDialog.setProgress(progress);
    }

    @Override
    public void onStatusChange(String status) {
        if (materialDialog != null && !materialDialog.isCancelled())
            materialDialog.setContent(status);
    }

    @Override
    public void onDone() {
        if (materialDialog != null && !materialDialog.isCancelled())
            materialDialog.dismiss();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
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
                        //resultList.add(0, Global.INSTANCE.getString(R.string.current_location));

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
