package org.secfirst.umbrella.adapters;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.SettingsActivity;
import org.secfirst.umbrella.models.Registry;
import org.secfirst.umbrella.models.SettingsItem;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.ViewHolder> {
    private ArrayList<SettingsItem> mSettings = new ArrayList<SettingsItem>();
    private Context mContext;
    private ArrayList<Address> mAddressList;
    private Address mAddress;
    private Registry mLocation;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTitle;
        public AutoCompleteTextView mAutocomplete;
        public ViewHolder(View v) {
            super(v);
            mTitle = (TextView) v.findViewById(R.id.settings_text);
            mAutocomplete = (AutoCompleteTextView) v.findViewById(R.id.settings_autocomplete);
        }
    }

    public SettingsAdapter(Context context) {
        mContext = context;
        mSettings.add(new SettingsItem("Refresh from the server"));
        mSettings.add(new SettingsItem("Set your location"));
        notifyDataSetChanged();
    }

    @Override
    public SettingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.settings_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mTitle.setText(mSettings.get(position).getTitle());
        holder.mAutocomplete.setAdapter(new GeoCodingAutoCompleteAdapter(mContext, R.layout.autocomplete_list_item));
        holder.mAutocomplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("location", "select");
                UmbrellaUtil.hideSoftKeyboard((Activity)mContext);
                if (position != 0 && mAddressList != null && mAddressList.size() >= position) {
                    mAddress = mAddressList.get(position - 1);
                    Log.i("address selected", mAddress.toString());
                    String chosenAddress = holder.mAutocomplete.getText().toString();
                    holder.mTitle.setText(chosenAddress);
                    holder.mTitle.setVisibility(View.VISIBLE);
                    holder.mAutocomplete.setVisibility(View.GONE);
                    List<Registry> selLoc = Registry.find(Registry.class, "name = ?", "location");
                    if (selLoc.size()>0) {
                        mLocation = selLoc.get(0);
                        mLocation.setValue(chosenAddress);
                    } else {
                        mLocation = new Registry("location", chosenAddress);
                    }
                    mLocation.save();
                } else {
                    mAddress = null;
                }
            }
        });
        switch (position) {
            case 1:
                List<Registry> selLoc = Registry.find(Registry.class, "name = ?", "location");
                if (selLoc.size()>0) {
                    Log.i("set location", selLoc.get(0).getValue());
                    holder.mTitle.setText(selLoc.get(0).getValue());
                    holder.mAutocomplete.setVisibility(View.GONE);
                } else {
                    holder.mTitle.setVisibility(View.GONE);
                    holder.mAutocomplete.setVisibility(View.VISIBLE);
                }
                holder.mTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.mTitle.setVisibility(View.GONE);
                        holder.mAutocomplete.setVisibility(View.VISIBLE);
                        holder.mAutocomplete.requestFocus();
                        ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    }
                });
                break;
            default:
                holder.mTitle.setVisibility(View.VISIBLE);
                holder.mAutocomplete.setVisibility(View.GONE);
                holder.mTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i("syncing", "now");
                        ((SettingsActivity) mContext).syncApi();
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mSettings.size();
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
            Context context = mContext;
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