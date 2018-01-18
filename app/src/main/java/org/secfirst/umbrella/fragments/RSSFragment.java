package org.secfirst.umbrella.fragments;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.secfirst.umbrella.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RSSFragment extends Fragment implements View.OnClickListener {


    private FloatingActionButton mAddFeedButton;

    public RSSFragment() {
        // Required empty public constructor
    }

    public static RSSFragment newInstance() {
        Bundle args = new Bundle();
        RSSFragment fragment = new RSSFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_custom_rss, container, false);
        mAddFeedButton = (FloatingActionButton) view.findViewById(R.id.add_feed_btn);
        mAddFeedButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.add_feed_btn:
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                RSSDialog rssDialog = RSSDialog.newInstance();
                rssDialog.show(fragmentManager, "");
                break;
        }
    }
}
