package org.secfirst.umbrella.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.secfirst.umbrella.MainActivity;
import org.secfirst.umbrella.R;

import java.util.Locale;

public class DashboardFragment extends Fragment {

    SectionsPagerAdapter mSectionsPagerAdapter;
    public ViewPager mViewPager;

    public static DashboardFragment newInstance(int toDash) {
        DashboardFragment fragment = new DashboardFragment();
        Bundle args = new Bundle();
        args.putInt("dashboard", toDash);
        fragment.setArguments(args);
        return fragment;
    }

    public DashboardFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tabbed, container, false);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mViewPager = (ViewPager) v.findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(-1 * getArguments().getInt("dashboard", 0));

        return v;
    }

    @Override
    public void onAttach(Context context) {
        if (!context.getResources().getConfiguration().locale.toString().equals(Locale.getDefault().toString())) {
            Configuration config = new Configuration();
            config.locale = Locale.getDefault();
            context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
        }
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            ((MainActivity) getActivity()).titleSpinner.setVisibility(View.GONE);
            getActivity().setTitle(getString(R.string.my_security));
        }
    }

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            switch (position) {
                case 1:
                    fragment = TabbedFeedRootFragment.newInstance(mViewPager);
                    break;
                case 2:
                    fragment = new TabbedFormsFragment();
                    break;
                default:
                    fragment = new TabbedChecklistFragment();
            }
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
                case 1:
                    return getString(R.string.feeds).toUpperCase(l);
                case 2:
                    return getString(R.string.forms).toUpperCase(l);
                default:
                    return getString(R.string.my_checklists).toUpperCase(l);
            }
        }
    }

}
