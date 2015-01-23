package org.secfirst.umbrella;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

import org.secfirst.umbrella.fragments.TourSlideFragment;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.TourViewPager;

public class TourActivity extends ActionBarActivity implements TourViewPager.OnSwipeOutListener {

    private static final int NUM_PAGES = 3;
    private TourViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private CirclePageIndicator mIndicator;
    private Global global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getSupportActionBar().hide();

        setContentView(R.layout.activity_tour);
        global = (Global) getApplicationContext();

        mPager = (TourViewPager) findViewById(R.id.myCustomViewPager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnSwipeOutListener(this);

        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);

        TextView btnSkip = (TextView) findViewById(R.id.btn_skip);
        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toMain();
            }
        });

        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mIndicator.setCurrentItem(position);
                if (position + 1 == NUM_PAGES) {
                    mPager.setChildId(1);
                } else {
                    mPager.setChildId(0);
                }
                invalidateOptionsMenu();
            }
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.tour, menu);
        MenuItem item;
        if (mPager.getCurrentItem() == mPagerAdapter.getCount() - 1) {
            item = menu.add(Menu.NONE, R.id.action_finish, Menu.NONE,
                    R.string.action_finish);
        } else  {
            item = menu.add(Menu.NONE, R.id.action_next, Menu.NONE,
                    R.string.action_next);
        }
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM | MenuItemCompat.SHOW_AS_ACTION_WITH_TEXT);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
                return true;
            case R.id.action_next:
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                return true;
            case R.id.action_finish:
                Intent goGrid = new Intent(TourActivity.this, MainActivity.class);
                startActivity(goGrid);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSwipeOutAtEnd() {
        toMain();
    }

    public void toMain() {
        global.set_termsAccepted(true);
        Intent toMain = new Intent(TourActivity.this, MainActivity.class);
        toMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(toMain);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return TourSlideFragment.create(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}