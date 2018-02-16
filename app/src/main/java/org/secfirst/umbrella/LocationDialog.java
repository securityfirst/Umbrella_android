package org.secfirst.umbrella;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mobsandgeeks.saripaar.QuickRule;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import org.secfirst.umbrella.fragments.FeedEmptyFragment;
import org.secfirst.umbrella.fragments.FeedListFragment;
import org.secfirst.umbrella.fragments.TabbedFeedFragment;
import org.secfirst.umbrella.models.FeedItem;
import org.secfirst.umbrella.models.Registry;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.OnLocationEventListener;
import org.secfirst.umbrella.util.UmbrellaRestClient;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class LocationDialog extends DialogFragment implements Validator.ValidationListener {


    private OnLocationEventListener mOnLocationEventListener;
    private List<Address> mAddressList;
    private Address mAddress;
    @NotEmpty
    private AppCompatAutoCompleteTextView mAutocompleteLocation;
    private Validator mValidator;
    private TextView mButtonOk;
    private TextView mButtonCancel;
    private FragmentActivity mActivity;
    private boolean mSourceFeedEnable;
    private String mChosenAddress;

    public static LocationDialog newInstance(TabbedFeedFragment tabbedFeedFragment, boolean openSource) {
        Bundle args = new Bundle();
        LocationDialog locationDialog = new LocationDialog();
        locationDialog.setArguments(args);
        locationDialog.mOnLocationEventListener = tabbedFeedFragment;
        locationDialog.mActivity = tabbedFeedFragment.getActivity();
        locationDialog.mSourceFeedEnable = openSource;
        return locationDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.location_view, container, false);
        mButtonCancel = view.findViewById(R.id.place_search_dialog_cancel_TV);
        mButtonOk = view.findViewById(R.id.place_search_dialog_ok_TV);
        mAutocompleteLocation = view.findViewById(R.id.place_search_dialog_location_ET);
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
        mValidator.put(mAutocompleteLocation, new AutocompleteLocationRule());
        initOkButtons();
        initAutoCompleteOnItemClick();
        initAutoCompleteOnFocusChange();
        return view;
    }

    private void initOkButtons() {
        mButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UmbrellaUtil.isOnline(getContext())) {
                    mValidator.validate();
                } else {
                    Toast.makeText(getContext(), R.string.feed_no_connection, Toast.LENGTH_SHORT).show();
                }
            }
        });
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void initAutoCompleteOnFocusChange() {

        mAutocompleteLocation.setAdapter(new GeoCodingAutoCompleteAdapter(getActivity(), R.layout.autocomplete_list_item));
        mAutocompleteLocation.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && !Global.INSTANCE.hasPasswordSet(false)) {
                    Global.INSTANCE.setPassword(getActivity(), null);
                }

            }
        });
    }

    private void initAutoCompleteOnItemClick() {
        mAutocompleteLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (Global.INSTANCE.hasPasswordSet(false)) {
                    UmbrellaUtil.hideSoftKeyboard(getActivity());
                    if (mAddressList != null) {
                        mAddress = mAddressList.get(position);
                        if (mAddress != null) {
                            mChosenAddress = mAutocompleteLocation.getText().toString();
                            locationRegistry(mChosenAddress);
                        }
                    } else {
                        mAddress = null;
                    }
                } else {
                    Global.INSTANCE.setPassword(getActivity(), null);
                }
            }
        });
    }

    @Override
    public void onValidationSucceeded() {
        mAutocompleteLocation.setText(mChosenAddress);
        locationRegistry(mAutocompleteLocation.getText().toString());
        mOnLocationEventListener.locationEvent(mAutocompleteLocation.getText().toString(), mSourceFeedEnable);
        deleteOldFeedItems();
        verifyIfSourceWasSelected();
        dismiss();
    }

    private void locationRegistry(String input) {
        try {
            Address address = autoComplete(input).get(0);
            Global.INSTANCE.setRegistry("location", input);
            Global.INSTANCE.setRegistry("iso2", address.getCountryCode().toLowerCase());
            Global.INSTANCE.setRegistry("country", address.getCountryName());
        } catch (Exception e) {
            Timber.e("Invalid address.", e);
        }
    }


    private void verifyIfSourceWasSelected() {
        if (!Global.INSTANCE.getSelectedFeedSourcesLabel(false).equals("")
                && !Global.INSTANCE.getRefreshLabel(null).equals("")) {
            getFeeds(getContext());
        }
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getContext());
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
        }
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


    public boolean getFeeds(final Context context) {
        Registry selISO2 = Global.INSTANCE.getRegistry("iso2");
        if (selISO2 != null) {
            List<Registry> selections;
            try {
                selections = Global.INSTANCE.getDaoRegistry().queryForEq(Registry.FIELD_NAME, "feed_sources");
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

                    String sources = sb.substring(separator.length());
                    final String mUrl = "feed?country=" + selISO2.getValue() + "&sources=" + sources
                            + "&since=" + Global.INSTANCE.getFeedItemsRefreshed();

                    mOnLocationEventListener.locationStartFetchData();
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
                                        Global.INSTANCE.getDaoFeedItem().create(receivedItem);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                                FragmentTransaction transaction = mActivity.getSupportFragmentManager()
                                        .beginTransaction();
                                transaction.replace(R.id.root_frame, FeedListFragment.
                                        newInstance(receivedItems));
                                transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            } else {
                                FragmentTransaction transaction = mActivity.getSupportFragmentManager()
                                        .beginTransaction();
                                transaction.replace(R.id.root_frame, FeedEmptyFragment.
                                        newInstance(Global.INSTANCE.getRegistry("mLocation").getValue()));
                                transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
                                transaction.addToBackStack(null);
                                transaction.commit();
                            }
                            mOnLocationEventListener.locationEndFetchData();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                            if (throwable instanceof javax.net.ssl.SSLPeerUnverifiedException) {
                                Toast.makeText(getContext(), "The SSL certificate pin is not valid." +
                                        " Most likely the certificate has expired and was renewed. Update " +
                                        "the app to refresh the accepted pins", Toast.LENGTH_LONG).show();
                            }
                            mOnLocationEventListener.locationEndFetchData();
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
        return false;
    }


    private void deleteOldFeedItems() {
        Registry selLoc = Global.INSTANCE.getRegistry("mLocation");
        try {
            if (selLoc != null)
                Global.INSTANCE.getDaoFeedItem().delete(Global.INSTANCE.getFeedItems());
        } catch (SQLException e) {
            Toast.makeText(getActivity(), R.string.no_results_label, Toast.LENGTH_SHORT).show();
        }
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
                                try {
                                    String toAdd = current.getAddressLine(0);
                                    if (current.getAddressLine(1) != null)
                                        toAdd += " " + current.getAddressLine(1);
                                    if (current.getAddressLine(2) != null)
                                        toAdd += " " + current.getAddressLine(2);
                                    toStrings.add(toAdd);
                                } catch (Exception e) {
                                    Timber.e("Some fields are null in Location list.", e);
                                }
                            }
                        }
                        resultList = toStrings;
                        filterResults.values = resultList;
                        filterResults.count = resultList.size();
                        notifyDataSetChanged();
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

    public class AutocompleteLocationRule extends QuickRule<AppCompatAutoCompleteTextView> {

        @Override
        public boolean isValid(AppCompatAutoCompleteTextView view) {
            boolean value;
            try {
                Address address = autoComplete(mAutocompleteLocation.getText().toString()).get(0);
                if (address.getCountryCode().toLowerCase().equals("") && address.getCountryName().equals("")) {
                    value = false;
                } else {
                    mChosenAddress = address.getCountryName();
                    value = true;
                }
            } catch (Exception e) {
                value = false;
            }

            return value;
        }

        @Override
        public String getMessage(Context context) {
            return getString(R.string.error_address_not_found);
        }
    }
}
