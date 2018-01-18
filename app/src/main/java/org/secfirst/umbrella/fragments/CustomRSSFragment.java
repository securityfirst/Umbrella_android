package org.secfirst.umbrella.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.secfirst.umbrella.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomRSSFragment extends Fragment {


    public CustomRSSFragment() {
        // Required empty public constructor
    }

    public static CustomRSSFragment newInstance() {
        Bundle args = new Bundle();
        CustomRSSFragment fragment = new CustomRSSFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_custom_rss, container, false);
    }

}
