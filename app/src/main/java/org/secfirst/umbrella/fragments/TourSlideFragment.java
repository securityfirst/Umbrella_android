package org.secfirst.umbrella.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.secfirst.umbrella.R;

public class TourSlideFragment extends Fragment {

    private int mPageNumber;

    public static TourSlideFragment create(int pageNumber) {
        TourSlideFragment fragment = new TourSlideFragment();
        Bundle args = new Bundle();
        args.putInt("page", pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public TourSlideFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt("page");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_tour_slide, container, false);

        LinearLayout slideLayout = (LinearLayout) rootView.findViewById(R.id.slide_layout);

        TextView headingTitle = (TextView) rootView.findViewById(R.id.heading_title);
        TextView headingBody = (TextView) rootView.findViewById(R.id.heading_body);

        switch (mPageNumber) {
            case 0:
                slideLayout.setBackgroundColor(getResources().getColor(R.color.umbrella_green));
                headingTitle.setText("Welcome to Umbrella");
                headingBody.setText("We help you feel safer by making security easier! We have gathered the best advice available and can help you do everything from planning a safe journey to sending a secure email.");
                break;
            case 1:
                slideLayout.setBackgroundColor(getResources().getColor(R.color.umbrella_purple));
                headingTitle.setText("Security");
                headingBody.setText("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor liqua.");
                break;
            case 2:
                slideLayout.setBackgroundColor(getResources().getColor(R.color.umbrella_yellow));
                headingTitle.setText("Security");
                headingBody.setText("Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor liqua.");
                break;
        }

        return rootView;
    }

}