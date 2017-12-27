package org.secfirst.umbrella.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.secfirst.umbrella.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabbedFeedRootFragment extends Fragment {

    private ViewPager mViewPage;

    public static TabbedFeedRootFragment newInstance(ViewPager viewPager) {

        Bundle args = new Bundle();
        TabbedFeedRootFragment fragment = new TabbedFeedRootFragment();
        fragment.mViewPage = viewPager;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dasboard_root, container, false);
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.root_frame, TabbedFeedFragment.newInstance(null, mViewPage));
        transaction.commit();
        return view;
    }

}
