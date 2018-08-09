package org.secfirst.umbrella.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.secfirst.umbrella.BaseActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.UmbrellaUtil;

public class TourSlideFragment extends Fragment {

    private int mPageNumber;
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
        if (getActivity()!=null && global==null) global = ((BaseActivity) getActivity()).getGlobal();
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_tour_slide, container, false);

        LinearLayout slideLayout = (LinearLayout) rootView.findViewById(R.id.slide_layout);
        LinearLayout umbrellaLayout = (LinearLayout) rootView.findViewById(R.id.umbrella_layout);
        LinearLayout titleLayout = (LinearLayout) rootView.findViewById(R.id.layout_title);

        TextView headingTitle = (TextView) rootView.findViewById(R.id.heading_title);
        TextView headingBody = (TextView) rootView.findViewById(R.id.heading_body);
        final WebView terms = (WebView) rootView.findViewById(R.id.terms_content);
        ImageView tourImage = (ImageView) rootView.findViewById(R.id.tour_image);
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
                slideLayout.setBackgroundColor(getResources().getColor(R.color.umbrella_yellow));
                headingTitle.setText(R.string.terms_conditions);
                terms.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        terms.loadDataWithBaseURL("file:///android_res/drawable/", "<style>body{color:#444444}img{width:100%}h1{color:#33b5e5; font-weight:normal;}h2{color:#9ABE2E; font-weight:normal;}a{color:#33b5e5}.button,.button:link{display:block;text-decoration:none;color:white;border:none;width:100%;text-align:center;border-radius:3px;padding-top:10px;padding-bottom:10px;}.green{background:#9ABE2E}.purple{background:#b83656}.yellow{background:#f3bc2b}</style>" + UmbrellaUtil.getStringFromAssetFile(getContext(), "terms.html"), "text/html", "UTF-8", "UTF-8");
                    }
                }, 100);
                break;
        }

        return rootView;
    }


}