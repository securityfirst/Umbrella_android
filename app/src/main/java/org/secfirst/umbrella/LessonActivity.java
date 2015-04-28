package org.secfirst.umbrella;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.viewpagerindicator.CirclePageIndicator;

import org.secfirst.umbrella.fragments.SegmentFragment;
import org.secfirst.umbrella.models.Category;
import org.secfirst.umbrella.models.Segment;

import java.util.List;


public class LessonActivity extends BaseActivity {

    private List<Segment> segments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        int toSlide = i.getIntExtra("to_slide", 0);
        int difficulty = i.getIntExtra("difficulty", 0);
        int category = i.getIntExtra("category", 0);
        segments = Segment.find(Segment.class, "category = ? and difficulty = ?", String.valueOf(category), String.valueOf(difficulty));
        if (segments.size()>0) {
            SegmentPagerAdapter sAdapter = new SegmentPagerAdapter(getSupportFragmentManager(), segments);
            final ViewPager lessonPager = (ViewPager) findViewById(R.id.segment_pager);
            lessonPager.setAdapter(sAdapter);
            CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
            mIndicator.setViewPager(lessonPager);
            lessonPager.setCurrentItem(toSlide);
            lessonPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    if (position <= segments.size()) {
                        setTitle(segments.get(position).getTitle());
                    }
                }
            });
        } else {
            finish();
        }
        Category category1 = Category.findById(Category.class, (long) category);
        if (category1!=null) setTitle(category1.getCategory());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_lesson;
    }

    public class SegmentPagerAdapter extends FragmentPagerAdapter {

        private List<Segment> segments;

        public SegmentPagerAdapter(FragmentManager fm, List<Segment> sList) {
            super(fm);
            segments = sList;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            Bundle args = new Bundle();
            args.putString("segment", segments.get(position).getBody());
            fragment = new SegmentFragment();
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return segments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }
}
