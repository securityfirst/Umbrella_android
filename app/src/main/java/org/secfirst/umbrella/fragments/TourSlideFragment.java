package org.secfirst.umbrella.fragments;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.util.Locale;

public class TourSlideFragment extends Fragment {

    private int mPageNumber;
    private Button mLanguageButton;

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

    private void setUpLanguage(String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        Global.INSTANCE.setRegistry("language", language);
        getActivity().recreate();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_tour_slide, container, false);


        mLanguageButton = rootView.findViewById(R.id.tour_language);
        mLanguageButton.setOnClickListener(v -> new MaterialDialog.Builder(getContext())
                .title(R.string.select_language)
                .items(R.array.language_array)
                .itemsCallbackSingleChoice(
                        0,
                        (dialog, view, which, text) -> {
                            if (which == 0) {
                                text = "en";
                            } else if (which == 1) {
                                text = "es";
                            } else {
                                text = "zh";
                            }
                            setUpLanguage("" + text);
                            return true; // allow selection
                        })
                .positiveText(R.string.choose)
                .show());

        LinearLayout slideLayout = rootView.findViewById(R.id.slide_layout);
        LinearLayout umbrellaLayout = rootView.findViewById(R.id.umbrella_layout);
        LinearLayout titleLayout = rootView.findViewById(R.id.layout_title);

        TextView headingTitle = rootView.findViewById(R.id.heading_title);
        TextView headingBody = rootView.findViewById(R.id.heading_body);
        final WebView terms = rootView.findViewById(R.id.terms_content);
        ImageView tourImage = rootView.findViewById(R.id.tour_image);
        headingBody.setPadding(UmbrellaUtil.dpToPix(25, getActivity()), UmbrellaUtil.dpToPix(40, getActivity()), UmbrellaUtil.dpToPix(25, getActivity()), 0);

        if (mPageNumber != 0) {
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int) (metrics.widthPixels * 0.75), (int) (metrics.widthPixels * 0.75));

            params.topMargin = UmbrellaUtil.dpToPix(40, getActivity());
            params.gravity = Gravity.CENTER_HORIZONTAL;
            tourImage.setLayoutParams(params);
            tourImage.setScaleType(ImageView.ScaleType.FIT_XY);
        }

        switch (mPageNumber) {
            case 0:
                slideLayout.setBackgroundColor(getResources().getColor(R.color.umbrella_purple));
                headingBody.setText(R.string.tour_slide_1_text);
                mLanguageButton.setVisibility(View.VISIBLE);
                break;
            case 1:
                tourImage.setImageResource(R.drawable.walktrough2);
                slideLayout.setBackgroundColor(getResources().getColor(R.color.umbrella_green));
                headingBody.setText(R.string.tour_slide_2_text);
                mLanguageButton.setVisibility(View.INVISIBLE);
                break;
            case 2:
                tourImage.setImageResource(R.drawable.walktrough3);
                slideLayout.setBackgroundColor(getResources().getColor(R.color.umbrella_yellow));
                headingBody.setText(R.string.tour_slide_3_text);
                mLanguageButton.setVisibility(View.INVISIBLE);
                break;
            case 3:
                tourImage.setImageResource(R.drawable.walktrough4);
                slideLayout.setBackgroundColor(getResources().getColor(R.color.umbrella_purple));
                headingBody.setText(R.string.tour_slide_4_text);
                mLanguageButton.setVisibility(View.INVISIBLE);
                break;
            case 4:
                terms.setVisibility(View.VISIBLE);
                umbrellaLayout.setVisibility(View.GONE);
                headingBody.setVisibility(View.GONE);
                titleLayout.setVisibility(View.VISIBLE);
                mLanguageButton.setVisibility(View.INVISIBLE);
                slideLayout.setBackgroundColor(getResources().getColor(R.color.umbrella_yellow));
                headingTitle.setText(R.string.terms_conditions);
                terms.postDelayed(() -> {
                    String html = "<style>body{color:#444444}img{width:100%}h1{color:#33b5e5; " +
                            "font-weight:normal;}h2{color:#9ABE2E; font-weight:normal;}getDifficultyFromId{color:#33b5e5}.button," +
                            ".button:link{display:block;text-decoration:none;color:white;border:none;width:100%;text-align:center;" +
                            "border-radius:3px;padding-top:10px;padding-bottom:10px;}.green{background:#9ABE2E}.purple{background:#b83656}" +
                            ".yellow{background:#f3bc2b}</style>";
                    terms.loadDataWithBaseURL("file:///android_res/drawable/", html
                                    + UmbrellaUtil.getStringFromAssetFile(getContext(), "terms.html"),
                            "text/html", "UTF-8", "UTF-8");
                }, 100);
                break;
        }

        return rootView;
    }


}