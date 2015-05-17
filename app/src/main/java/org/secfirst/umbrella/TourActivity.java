package org.secfirst.umbrella;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.viewpagerindicator.CirclePageIndicator;

import org.secfirst.umbrella.fragments.TourSlideFragment;
import org.secfirst.umbrella.util.TourViewPager;

public class TourActivity extends BaseActivity implements TourViewPager.OnSwipeOutListener {

    private static final int NUM_PAGES = 6;
    private TourViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private CirclePageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPager = (TourViewPager) findViewById(R.id.myCustomViewPager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnSwipeOutListener(this);

        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);

        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mIndicator.setCurrentItem(position);
                if (position + 1 == NUM_PAGES) {
                    mPager.setChildId(1);
                } else {
                    mPager.setChildId(0);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        new MigrateData(this).execute();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_tour;
    }

    @Override
    public void onSwipeOutAtEnd() {
        if(!global.getTermsAccepted()) {
            Toast.makeText(this, "You have to read and accept terms and conditions to continue", Toast.LENGTH_SHORT).show();
        }
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return TourSlideFragment.create(position, global);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    private class MigrateData extends AsyncTask<Void, Void, Void> {
        Activity activity;

        MigrateData(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... params) {
            global.migrateData();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }
}