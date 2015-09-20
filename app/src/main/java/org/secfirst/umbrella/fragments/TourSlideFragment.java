package org.secfirst.umbrella.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.TourActivity;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.UmbrellaUtil;

public class TourSlideFragment extends Fragment {

    private int mPageNumber;
    private Global global;
    OnNavigateToMainListener mCallback;

    public interface OnNavigateToMainListener {
        void onNavigationRequested();
    }

    public static TourSlideFragment create(int pageNumber, Global global) {
        TourSlideFragment fragment = new TourSlideFragment();
        fragment.global = global;
        Bundle args = new Bundle();
        args.putInt("page", pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnNavigateToMainListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnNavigateToMainListener");
        }
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
        LinearLayout titleLayout = (LinearLayout) rootView.findViewById(R.id.layout_title);

        TextView headingTitle = (TextView) rootView.findViewById(R.id.heading_title);
        TextView headingBody = (TextView) rootView.findViewById(R.id.heading_body);
        final WebView terms = (WebView) rootView.findViewById(R.id.terms_content);
        ImageView tourImage = (ImageView) rootView.findViewById(R.id.tour_image);
        final Button skipBtn = (Button) rootView.findViewById(R.id.btn_skip);
        headingBody.setPadding(UmbrellaUtil.dpToPix(25, getActivity()), UmbrellaUtil.dpToPix(40, getActivity()), UmbrellaUtil.dpToPix(25, getActivity()), 0);

        if (mPageNumber!=0) {
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (metrics.widthPixels*0.75), (int) (metrics.widthPixels*0.75));

            params.topMargin = UmbrellaUtil.dpToPix(40, getActivity());
            params.gravity = Gravity.CENTER_HORIZONTAL;
            tourImage.setLayoutParams(params);
            tourImage.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        switch (mPageNumber) {
            case 0:
                slideLayout.setBackgroundColor(getResources().getColor(R.color.umbrella_purple));
                headingBody.setText(R.string.tour_slide_1_text);
                break;
            case 1:
                tourImage.setImageResource(R.drawable.walktrough2);
                slideLayout.setBackgroundColor(getResources().getColor(R.color.umbrella_green));
                headingBody.setText(R.string.tour_slide_2_text);
                break;
            case 2:
                tourImage.setImageResource(R.drawable.walktrough3);
                slideLayout.setBackgroundColor(getResources().getColor(R.color.umbrella_yellow));
                headingBody.setText(R.string.tour_slide_3_text);
                break;
            case 3:
                tourImage.setImageResource(R.drawable.walktrough4);
                slideLayout.setBackgroundColor(getResources().getColor(R.color.umbrella_purple));
                headingBody.setText(R.string.tour_slide_4_text);
                break;
            case 4:
                terms.setVisibility(View.VISIBLE);
                umbrellaLayout.setVisibility(View.GONE);
                headingBody.setVisibility(View.GONE);
                titleLayout.setVisibility(View.VISIBLE);
                skipBtn.setTextColor(getActivity().getResources().getColor(R.color.white));
                skipBtn.setEnabled(true);
                skipBtn.setVisibility(View.VISIBLE);
                skipBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("click", "boom");
                        global.set_termsAccepted(true);
                        ((TourActivity) getActivity()).navigateToMain();
                        mCallback.onNavigationRequested();
                    }
                });
                slideLayout.setBackgroundColor(getResources().getColor(R.color.umbrella_yellow));
                headingTitle.setText(R.string.terms_conditions);
                final String termsHtml = "<p><strong>Most important Terms and Privacy points</strong></p><p>Terms of Service and Privacy Policies are important. You should always know what you’re agreeing to, so you should always read the terms and conditions carefully. You can read ours in full here.</p><p>That said, we know people don’t usually read them all that carefully, so we’ve outlined the main points of ours below.</p><p>&bull;&nbsp;&nbsp;&nbsp;<strong>We don’t take your data</strong> – We take your security and privacy very seriously. That’s why we don’t take any personal data from you and you stay anonymous. We don’t have access to anything you store or input into the app – that information is only stored on your device. (This means that we can’t recover your data if you accidently delete it or forget your password.) We can’t see anything about you, so no one else can either.</p><p>&bull;&nbsp;&nbsp;&nbsp;<strong>We don’t track you</strong> – If you want to use the dashboard to get up-to-date information on risks in your area, you just enter the city – we don’t track your location. Ever.</p><p>&bull;&nbsp;&nbsp;&nbsp;<strong>We recommend you use a password</strong> – If you want to save your checklists and keep an eye on your progress, we recommend you put a password on the app. This will encrypt the app. If someone gets their hands on your phone you don’t want them knowing which security protocols you’ve implemented and which you haven’t. We haven’t made this compulsory in case you weren’t storing much, but we think you’re better safe than sorry.</p><p>&bull;&nbsp;&nbsp;&nbsp;<strong>We’re not liable</strong> – Umbrella is an app full of advice on what to do in risky situations. This advice may not always apply and you should consider all potential risks, use a wide range of advice, and use your own judgement before deciding what to do. We hope you’ll understand that we are not liable or responsible for the actions you take – basically that whatever you do is done at your own risk.</p><p>&bull;&nbsp;&nbsp;&nbsp;<strong>It’s free</strong> – Umbrella is free to use and there are no advertisements. Its development is supported by grants and training revenue. Like us, almost all the tools we recommend are free, open-source, and where appropriate, end-to-end encrypted.</p>";
                terms.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        terms.loadDataWithBaseURL("file:///android_res/drawable/", "<style>body{color:#444444}img{width:100%}h1{color:#33b5e5; font-weight:normal;}h2{color:#9ABE2E; font-weight:normal;}a{color:#33b5e5}.button,.button:link{display:block;text-decoration:none;color:white;border:none;width:100%;text-align:center;border-radius:3px;padding-top:10px;padding-bottom:10px;}.green{background:#9ABE2E}.purple{background:#b83656}.yellow{background:#f3bc2b}</style>" + termsHtml, "text/html", "UTF-8", "UTF-8");
                    }
                }, 100);
                break;
        }

        return rootView;
    }


}