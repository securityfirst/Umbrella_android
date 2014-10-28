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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.secfirst.umbrella.MainActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.adapters.CheckListAdapter;
import org.secfirst.umbrella.adapters.SegmentAdapter;
import org.secfirst.umbrella.data.CheckListDataSource;
import org.secfirst.umbrella.data.SegmentsDataSource;
import org.secfirst.umbrella.models.CheckItem;
import org.secfirst.umbrella.models.Segment;

import java.util.ArrayList;
import java.util.Locale;

public class TabbedFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;

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
        private ArrayList<Segment> mSegments;
        private ArrayList<CheckItem> mCheckList;
        private ProgressBar checkBar;
        private TextView checkBarText;

        public TabbedContentFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tabbed_content,
                    container, false);


            int drawerItem = ((MainActivity) getActivity()).drawerItem;
            ListView contentBox = (ListView) rootView.findViewById(R.id.content_box);
            LinearLayout checkBarLayout = (LinearLayout) rootView.findViewById(R.id.progress_checked);
            checkBar = (ProgressBar) rootView.findViewById(R.id.progress_bar_checked);
            checkBarText = (TextView) rootView.findViewById(R.id.check_bar_text);
            setProgressBarTo(0);

            checkBarLayout.setVisibility(getArguments().getInt(ARG_SECTION_NUMBER) == 3 ? View.VISIBLE : View.GONE);

            switch (getArguments().getInt(ARG_SECTION_NUMBER)) {
                case 1:
                    SegmentsDataSource dataSource = new SegmentsDataSource(getActivity());
                    dataSource.open();
                    mSegments = dataSource.getAllSegmentsByCategory(drawerItem);
                    dataSource.close();
                    contentBox.setAdapter(new SegmentAdapter(getActivity(), mSegments));
                    contentBox.setDivider(null);
                    break;
                case 2:
                    break;
                case 3:
                    refreshCheckList(drawerItem);
                    contentBox.setAdapter(new CheckListAdapter(getActivity(), mCheckList, this));
                    contentBox.setDivider(null);
                    break;
                default:

            }
            return rootView;
        }

        public void refreshCheckList(int category) {
            CheckListDataSource checkListDataSource = new CheckListDataSource(getActivity());
            checkListDataSource.open();
            mCheckList = checkListDataSource.getAllItemsByCategory(category);
            if (mCheckList.size()>0) {
                int selected = 0;
                for (CheckItem checkItem : mCheckList) {
                    if (checkItem.getValue()) selected++;
                }
                setProgressBarTo((int) Math.round(selected * 100.0 / mCheckList.size()));
            }
            checkListDataSource.close();
        }

        public void setProgressBarTo(int percent) {
            if (percent>=0 && percent<=100) {
                checkBar.setProgress(percent);
                checkBarText.setText(percent + "% done.");
            }
        }
    }

}