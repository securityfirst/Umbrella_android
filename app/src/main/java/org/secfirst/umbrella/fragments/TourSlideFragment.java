package org.secfirst.umbrella.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.IntentCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.secfirst.umbrella.MainActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.util.Global;

public class TourSlideFragment extends Fragment {

    private int mPageNumber, mOffset;
    private Global global;

    public static TourSlideFragment create(int pageNumber, Global global) {
        TourSlideFragment fragment = new TourSlideFragment();
        fragment.global = global;
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
        LinearLayout umbrellaLayout = (LinearLayout) rootView.findViewById(R.id.umbrella_layout);

        TextView headingTitle = (TextView) rootView.findViewById(R.id.heading_title);
        TextView headingBody = (TextView) rootView.findViewById(R.id.heading_body);
        Button skipBtn = (Button) rootView.findViewById(R.id.btn_skip);
        final ScrollView termsView = (ScrollView) rootView.findViewById(R.id.scroll_terms);

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
                termsView.setVisibility(View.VISIBLE);
                umbrellaLayout.setVisibility(View.GONE);
                headingBody.setVisibility(View.GONE);
                skipBtn.setVisibility(View.VISIBLE);
                termsView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    @Override
                    public void onScrollChanged() {
                        mOffset = termsView.getScrollY();
                    }
                });
                skipBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOffset>100) {
                            global.set_termsAccepted(true);
                            Intent toMain = new Intent(getActivity(), MainActivity.class);
                            toMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(toMain);
                        } else {
                            Toast.makeText(getActivity(), "You have to read and accept terms and conditions to continue", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                slideLayout.setBackgroundColor(getResources().getColor(R.color.umbrella_yellow));
                headingTitle.setText("Terms and conditions");
                break;
        }

        return rootView;
    }


}