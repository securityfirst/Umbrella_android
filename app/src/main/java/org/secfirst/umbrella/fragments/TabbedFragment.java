package org.secfirst.umbrella.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.GridView;
import android.widget.TextView;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.secfirst.umbrella.AboutActivity;
import org.secfirst.umbrella.MainActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.adapters.GridAdapter;
import org.secfirst.umbrella.models.Segment;
import org.secfirst.umbrella.util.Global;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;
import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

public class TabbedFragment extends Fragment {

    public static final String ARG_DIFFICULTY_NUMBER = "spinner_number";
    public static final String ARG_SEGMENT_INDEX = "segment_index";
    SectionsPagerAdapter mSectionsPagerAdapter;
    public ViewPager mViewPager;
    public static int difficulty;
    public long sectionNumber;
    public static boolean hasChecklist;

    public static TabbedFragment newInstance(long sectionNumber, int spinnerNumber, boolean checklist, int page) {
        TabbedFragment tabbedFragment = new TabbedFragment();
        Bundle args = new Bundle();
        args.putBoolean("checklist", checklist);
        args.putInt("page", page);
        tabbedFragment.sectionNumber = sectionNumber;
        difficulty = spinnerNumber;
        tabbedFragment.setArguments(args);
        return tabbedFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tabbed, container, false);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), difficulty);
        mViewPager = v.findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == mSectionsPagerAdapter.getCount() - 1 && positionOffset == 0) {
                    if (!Global.INSTANCE.hasShownCoachMark("mark_off_tasks")) {
                        new MaterialTapTargetPrompt.Builder(getActivity())
                                .setTarget(R.id.check_value)
                                .setSecondaryText(getActivity().getString(R.string.mark_off_tasks))
                                .setTextGravity(Gravity.CENTER)
                                .setFocalColour(getResources().getColor(R.color.coachmark_focal_background))
                                .setSecondaryText(getString(R.string.coach_marks_message))
                                .setBackgroundColour(getResources().getColor(R.color.coachmark_background_dark))
                                .setSecondaryTextColour(getResources().getColor(R.color.umbrella_green))
                                .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                                    @Override
                                    public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                                        if (state == MaterialTapTargetPrompt.STATE_REVEALED)
                                            Global.INSTANCE.setCoachMarkShown("mark_off_tasks", true);
                                        if (state == MaterialTapTargetPrompt.STATE_DISMISSED && !Global.INSTANCE.hasShownCoachMark("add_new_tasks")) {
                                            new MaterialTapTargetPrompt.Builder(getActivity())
                                                    .setTarget(R.id.fab)
                                                    .setSecondaryText(getActivity().getString(R.string.click_to_add_new_tasks))
                                                    .setTextGravity(Gravity.CENTER)
                                                    .setFocalColour(getResources().getColor(R.color.coachmark_focal_background))
                                                    .setSecondaryText(getString(R.string.coach_marks_add_item_message))
                                                    .setBackgroundColour(getResources().getColor(R.color.coachmark_background_dark))
                                                    .setSecondaryTextColour(getResources().getColor(R.color.umbrella_green))
                                                    .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                                                        @Override
                                                        public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                                                            if (state == MaterialTapTargetPrompt.STATE_REVEALED)
                                                                Global.INSTANCE.setCoachMarkShown("add_new_tasks", true);
                                                            if (state == MaterialTapTargetPrompt.STATE_DISMISSED && !Global.INSTANCE.hasShownCoachMark("star_check_list") && isAdded()) {
                                                                new MaterialTapTargetPrompt.Builder(getActivity())
                                                                        .setTarget(getActivity().findViewById(R.id.favourite))
                                                                        .setSecondaryText(getActivity().getString(R.string.star_this_checklist))
                                                                        .setTextGravity(Gravity.CENTER)
                                                                        .setFocalColour(getResources().getColor(R.color.coachmark_focal_background))
                                                                        .setSecondaryText(getString(R.string.coach_marks_star_message))
                                                                        .setBackgroundColour(getResources().getColor(R.color.coachmark_background_dark))
                                                                        .setSecondaryTextColour(getResources().getColor(R.color.umbrella_green))
                                                                        .setPromptStateChangeListener(new MaterialTapTargetPrompt.PromptStateChangeListener() {
                                                                            @Override
                                                                            public void onPromptStateChanged(MaterialTapTargetPrompt prompt, int state) {
                                                                                if (state == MaterialTapTargetPrompt.STATE_REVEALED) {
                                                                                    Global.INSTANCE.setCoachMarkShown("star_check_list", true);
                                                                                }
                                                                            }
                                                                        })
                                                                        .show();
                                                            }
                                                        }
                                                    })
                                                    .show();
                                        }
                                    }
                                })
                                .show();
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        hasChecklist = getArguments().getBoolean("checklist", false);
        int page = getArguments().getInt("page", 0);
        mViewPager.setCurrentItem(hasChecklist ? mSectionsPagerAdapter.getCount() - 1 : page);
        return v;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public int difficulty;
        private List<Segment> segments;

        SectionsPagerAdapter(FragmentManager fm, int difficulty) {
            super(fm);
            this.difficulty = difficulty;
            int drawerItem = (int) ((MainActivity) getActivity()).drawerItem;
            try {
                QueryBuilder<Segment, String> queryBuilder = Global.INSTANCE.getDaoSegment().queryBuilder();
                Where<Segment, String> where = queryBuilder.where();
                where.eq(Segment.FIELD_CATEGORY, String.valueOf(drawerItem)).and().eq(Segment.FIELD_DIFFICULTY, String.valueOf(difficulty + 1));
                segments = queryBuilder.query();
            } catch (SQLException e) {
                Timber.e(e);
            }
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            Bundle args = new Bundle();
            if (position == 0) {
                fragment = new TabbedContentFragment();
            } else if (position == segments.size() + 1) {
                fragment = CheckItemFragment.newInstance(difficulty);
            } else {
                fragment = new TabbedSegmentFragment();
                args.putInt(TabbedFragment.ARG_SEGMENT_INDEX, position - 1);
            }
            args.putInt(TabbedFragment.ARG_DIFFICULTY_NUMBER, difficulty + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return segments.size() + 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            if (position == 0) {
                return getContext().getString(R.string.section1_tab_title1).toUpperCase(l);
            } else if (position == segments.size() + 1) {
                return getContext().getString(R.string.section1_tab_title3).toUpperCase(l);
            } else {
                if (segments.get(position - 1).getTitle() != null) {
                    return segments.get(position - 1).getTitle().toUpperCase(l);
                } else {
                    return (getContext().getString(R.string.slide) + position).toUpperCase(l);
                }
            }
        }
    }

    public static class TabbedContentFragment extends Fragment {

        public TabbedContentFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tabbed_content,
                    container, false);

            int drawerItem = (int) ((MainActivity) getActivity()).drawerItem;
            int difficulty = getArguments() != null ? getArguments().getInt(ARG_DIFFICULTY_NUMBER, 1) : 1;
            try {
                QueryBuilder<Segment, String> queryBuilder = Global.INSTANCE.getDaoSegment().queryBuilder();
                Where<Segment, String> where = queryBuilder.where();
                where.eq(Segment.FIELD_CATEGORY, drawerItem)
                        .and().like(Segment.FIELD_DIFFICULTY, difficulty);
                final List<Segment> segments = queryBuilder.query();
                if (!segments.isEmpty()) {
                    GridView gridView = rootView.findViewById(R.id.grid_tiles);
                    GridAdapter gAdapter = new GridAdapter(getActivity(), segments);
                    gridView.setAdapter(gAdapter);
                }
                TextView toChecklist = rootView.findViewById(R.id.grid_title);
                toChecklist.setText(getString(R.string.checklist));
                int[] colours = {R.color.umbrella_purple, R.color.umbrella_green, R.color.umbrella_yellow};
                toChecklist.setBackgroundColor(getActivity().getResources().getColor(colours[(segments.size()) % 3]));
                CardView checklistCard = rootView.findViewById(R.id.checklist_view);
                if (drawerItem == 56) checklistCard.setVisibility(View.GONE);
                checklistCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment frag = getActivity().getSupportFragmentManager().findFragmentByTag("tabbed");
                        if (frag != null) {
                            ((TabbedFragment) frag).mViewPager.setCurrentItem(segments.size() + 2);
                        }
                        if (getActivity() != null)
                            ((MainActivity) getActivity()).favouriteItem.setVisible(true);
                    }
                });
            } catch (SQLException e) {
                Timber.e(e);
            }

            return rootView;
        }

    }

    public static class TabbedSegmentFragment extends Fragment {

        private WebView content;

        public TabbedSegmentFragment() {
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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_segment,
                    container, false);
            WebViewClient interceptUrlLicences = new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    Intent i = new Intent(getActivity(), AboutActivity.class);
                    if (url.equals("umbrella://licences/")) {
                        i.putExtra("topic", "licences");
                        rootView.getContext().startActivity(i);
                    } else if (url.equals("umbrella://thankyou/")) {
                        i.putExtra("topic", "thankyou");
                        rootView.getContext().startActivity(i);
                    } else {
                        rootView.getContext().startActivity(
                                new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                    }
                    return true;
                }

                @Override
                public void onLoadResource(WebView view, String url) {
                }
            };
            content = rootView.findViewById(R.id.segment_content);
            content.setWebViewClient(interceptUrlLicences);

            int drawerItem = (int) ((MainActivity) getActivity()).drawerItem;
            int difficulty = getArguments() != null ? getArguments().getInt(ARG_DIFFICULTY_NUMBER, 1) : 1;
            int segmentInt = getArguments() != null ? getArguments().getInt(ARG_SEGMENT_INDEX, 0) : 0;
            List<Segment> segments = null;
            try {
                QueryBuilder<Segment, String> queryBuilder = Global.INSTANCE.getDaoSegment().queryBuilder();
                Where<Segment, String> where = queryBuilder.where();
                where.eq(Segment.FIELD_CATEGORY, String.valueOf(drawerItem)).and().eq(Segment.FIELD_DIFFICULTY, String.valueOf(difficulty));
                segments = queryBuilder.query();
            } catch (SQLException e) {
                Timber.e(e);
            }
            if (segments != null && !segments.isEmpty() && segments.size() >= segmentInt + 1) {
                final String html = segments.get(segmentInt).getBody();
                if (html != null) {
                    content.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            content.loadDataWithBaseURL("file:///android_res/drawable/", "<style>body{color:#444444}img{width:100%}h1{color:#33b5e5; font-weight:normal;}h2{color:#9ABE2E; font-weight:normal;}getDifficultyFromId{color:#33b5e5}.button,.button:link{display:block;text-decoration:none;color:white;border:none;width:100%;text-align:center;border-radius:3px;padding-top:10px;padding-bottom:10px;}.green{background:#9ABE2E}.purple{background:#b83656}.yellow{background:#f3bc2b}</style>" + html, "text/html", "UTF-8", "UTF-8");
                        }
                    }, 100);
                }
            }
            return rootView;
        }

    }
}