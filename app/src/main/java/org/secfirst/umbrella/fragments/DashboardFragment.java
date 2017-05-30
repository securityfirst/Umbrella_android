package org.secfirst.umbrella.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.secfirst.umbrella.BaseActivity;
import org.secfirst.umbrella.MainActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.util.Global;

import java.util.Locale;

public class DashboardFragment extends Fragment {

    private Global global;
    SectionsPagerAdapter mSectionsPagerAdapter;
    public ViewPager mViewPager;

    public static DashboardFragment newInstance(Global global, boolean toDash) {
        DashboardFragment fragment = new DashboardFragment();
        fragment.global = global;
        Bundle args = new Bundle();
        args.putBoolean("dashboard", toDash);
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
        mViewPager.setCurrentItem(getArguments().getBoolean("dashboard", false) ? 1 : 0);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity()!=null) {
            ((MainActivity) getActivity()).titleSpinner.setVisibility(View.GONE);
            if (global==null) global = ((BaseActivity) getActivity()).getGlobal();
            getActivity().setTitle(global.getString(R.string.my_security));
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
                    fragment = new TabbedFeedFragment();
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
