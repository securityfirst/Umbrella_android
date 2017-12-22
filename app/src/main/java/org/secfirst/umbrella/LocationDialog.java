package org.secfirst.umbrella;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
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

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import org.secfirst.umbrella.fragments.TabbedFeedFragment;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.OnLocationEventListener;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by HAL-9000 on 12/12/2017.
 */

public class LocationDialog extends DialogFragment implements Validator.ValidationListener {


    private OnLocationEventListener onLocationEventListener;
    private Global global;
    private List<Address> mAddressList;
    private Address mAddress;
    @NotEmpty
    private AppCompatAutoCompleteTextView mAutocompleteLocation;
    private Validator mValidator;
    private TextView mButtonOk;
    private TextView mButtonCancel;

    public static LocationDialog newInstance(TabbedFeedFragment tabbedFeedFragment) {
        Bundle args = new Bundle();
        LocationDialog locationDialog = new LocationDialog();
        locationDialog.setArguments(args);
        locationDialog.onLocationEventListener = tabbedFeedFragment;
        return locationDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.location_view, container, false);
        global = ((BaseActivity) getActivity()).getGlobal();
        mButtonCancel = (TextView) view.findViewById(R.id.place_search_dialog_cancel_TV);
        mButtonOk = (TextView) view.findViewById(R.id.place_search_dialog_ok_TV);
        mAutocompleteLocation = (AppCompatAutoCompleteTextView) view.findViewById(R.id.place_search_dialog_location_ET);
        mValidator = new Validator(this);
        mValidator.setValidationListener(this);
        initOkButtons();
        initAutoCompleteOnItemClick();
        initAutoCompleteOnFocusChange();
        return view;
    }

    private void initOkButtons() {
        mButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mValidator.validate();
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
                if (hasFocus && !global.hasPasswordSet(false)) {
                    global.setPassword(getActivity(), null);
                }

            }
        });
    }

    private void initAutoCompleteOnItemClick() {
        mAutocompleteLocation.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (global.hasPasswordSet(false)) {
                    UmbrellaUtil.hideSoftKeyboard(getActivity());
                    if (mAddressList != null) {
                        mAddress = mAddressList.get(position);
                        if (mAddress != null) {
                            String chosenAddress = mAutocompleteLocation.getText().toString();
                            mAutocompleteLocation.setText(chosenAddress);
                            global.setRegistry("location", chosenAddress);
                            global.setRegistry("iso2", mAddress.getCountryCode().toLowerCase());
                            global.setRegistry("country", mAddress.getCountryName());
                        }
                    } else {
                        mAddress = null;
                    }
                } else {
                    global.setPassword(getActivity(), null);
                }
            }
        });
    }

    @Override
    public void onValidationSucceeded() {
        onLocationEventListener.locationEvent(mAutocompleteLocation.getText().toString());
        dismiss();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getContext());
            // Display error messages ;)
            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
            }
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
                                if (current.getAddressLine(1) != null)
                                    toAdd += " " + current.getAddressLine(1);
                                if (current.getAddressLine(2) != null)
                                    toAdd += " " + current.getAddressLine(2);
                                toStrings.add(toAdd);
                            }
                        }
                        resultList = toStrings;
                        //resultList.add(0, global.getString(R.string.current_location));

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
