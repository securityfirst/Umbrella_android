package org.secfirst.umbrella.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.secfirst.umbrella.R;

/**
 * Created by HAL-9000 on 13/12/2017.
 */

public class FeedEmptyFragment extends Fragment {

    private View mView;
    private CardView mEmptyCard;
    private String mLocation;

    public static FeedEmptyFragment newInstance(String location) {
        Bundle args = new Bundle();
        FeedEmptyFragment fragment = new FeedEmptyFragment();
        fragment.setArguments(args);
        fragment.mLocation = location;
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.dasboard_feed_empty_view, container, false);
        mEmptyCard = (CardView) mView.findViewById(R.id.empty_dashboard);
        TextView title = (TextView) mView.findViewById(R.id.empty_dashboard_title);
        title.setText(mLocation);
        mEmptyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.root_frame, new TabbedFeedFragment());
                transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        return mView;
    }
}
