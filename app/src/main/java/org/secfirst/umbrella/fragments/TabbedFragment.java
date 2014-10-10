package org.secfirst.umbrella.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import org.secfirst.umbrella.MainActivity;
import org.secfirst.umbrella.R;

import java.util.Locale;

public class TabbedFragment extends Fragment {

    public static final String TAG = TabbedFragment.class.getSimpleName();
    private static final String ARG_SECTION_NUMBER = "section_number";
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    int mDrawerPager;

    public static TabbedFragment newInstance(int sectionNumber) {
        TabbedFragment tabbedFragment = new TabbedFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        tabbedFragment.setArguments(args);
        return tabbedFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tabbed, container, false);

        mSectionsPagerAdapter = new SectionsPagerAdapter(
                getChildFragmentManager());
        mViewPager = (ViewPager) v.findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = new TabbedContentFragment();
            Bundle args = new Bundle();
            args.putInt(TabbedContentFragment.ARG_SECTION_NUMBER, position + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.section1_tab_title1).toUpperCase(l);
                case 1:
                    return getString(R.string.section1_tab_title2).toUpperCase(l);
                case 2:
                    return getString(R.string.section1_tab_title3).toUpperCase(l);
            }
            return null;
        }
    }

    public static class TabbedContentFragment extends Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";

        public TabbedContentFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tabbed_content,
                    container, false);
            int drawerItem = ((MainActivity) getActivity()).drawerItem;

            WebView wv = (WebView) rootView.findViewById(R.id.web_view);
            wv.getSettings().setJavaScriptEnabled(true);
            switch (drawerItem) {
                case 0:
                    if (getArguments().getInt(ARG_SECTION_NUMBER)==1) {
                        wv.loadDataWithBaseURL(null, getString(R.string.section1_tab_text1), "text/html", "utf-8", null);
                    }
                    break;
                case 1:
                    if (getArguments().getInt(ARG_SECTION_NUMBER)==1) {
                        wv.loadDataWithBaseURL(null, getString(R.string.section2_tab_text1), "text/html", "utf-8", null);
                    }
                    break;
                case 2:
                    if (getArguments().getInt(ARG_SECTION_NUMBER)==1) {
                        wv.loadDataWithBaseURL(null, getString(R.string.section3_tab_text1), "text/html", "utf-8", null);
                    }
                    break;
                case 3:
                    if (getArguments().getInt(ARG_SECTION_NUMBER)==1) {
                        wv.loadDataWithBaseURL(null, getString(R.string.section4_tab_text1), "text/html", "utf-8", null);
                    }
                    break;
                case 4:
                    if (getArguments().getInt(ARG_SECTION_NUMBER)==1) {
                        wv.loadDataWithBaseURL(null, getString(R.string.section5_tab_text1), "text/html", "utf-8", null);
                    }
                    break;
            }
            return rootView;
        }
    }

}