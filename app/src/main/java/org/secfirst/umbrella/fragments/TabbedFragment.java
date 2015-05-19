package org.secfirst.umbrella.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.PointTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.secfirst.umbrella.BaseActivity;
import org.secfirst.umbrella.MainActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.adapters.CheckListAdapter;
import org.secfirst.umbrella.adapters.GridAdapter;
import org.secfirst.umbrella.models.CheckItem;
import org.secfirst.umbrella.models.Segment;
import org.secfirst.umbrella.util.Global;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

public class TabbedFragment extends Fragment {

    public static final String ARG_DIFFICULTY_NUMBER = "spinner_number";
    public static final String ARG_SEGMENT_INDEX = "segment_index";
    SectionsPagerAdapter mSectionsPagerAdapter;
    public ViewPager mViewPager;
    public static int difficulty;
    public long sectionNumber;
    public static boolean hasChecklist;

    public static TabbedFragment newInstance(long sectionNumber, int spinnerNumber, boolean checklist) {
        TabbedFragment tabbedFragment = new TabbedFragment();
        Bundle args = new Bundle();
        args.putBoolean("checklist", checklist);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tabbed, container, false);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(), difficulty);
        mViewPager = (ViewPager) v.findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == mSectionsPagerAdapter.getCount() - 1 && positionOffset == 0) {
                    if (android.os.Build.VERSION.SDK_INT >= 11) {
                        new ShowcaseView.Builder(getActivity())
                                .setTarget(new ViewTarget(R.id.check_value, getActivity()))
                                .setContentText("Mark off tasks as you complete them\n\nHold down on a task to delete or disable it")
                                .setStyle(R.style.CustomShowcaseTheme4)
                                .hideOnTouchOutside()
                                .singleShot(4)
                                .setShowcaseEventListener(new OnShowcaseEventListener() {
                                    @Override
                                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
                                        new ShowcaseView.Builder(getActivity())
                                                .setTarget(new ViewTarget(R.id.fab, getActivity()))
                                                .setContentText("Click here to add new tasks – you’ll need to create a password for this!")
                                                .setStyle(R.style.CustomShowcaseTheme4)
                                                .hideOnTouchOutside()
                                                .singleShot(5)
                                                .setShowcaseEventListener(new OnShowcaseEventListener() {
                                                    @Override
                                                    public void onShowcaseViewHide(ShowcaseView showcaseView) {
                                                        new ShowcaseView.Builder(getActivity())
                                                                .setTarget(new PointTarget(getView().getWidth(), 0))
                                                                .setContentText("Star this checklist to make it one of your favourites\n\nClick here to share checklist")
                                                                .setStyle(R.style.CustomShowcaseTheme4)
                                                                .hideOnTouchOutside()
                                                                .singleShot(6)
                                                                .build();
                                                    }

                                                    @Override
                                                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                                                    }

                                                    @Override
                                                    public void onShowcaseViewShow(ShowcaseView showcaseView) {

                                                    }
                                                })
                                                .build();
                                    }

                                    @Override
                                    public void onShowcaseViewDidHide(ShowcaseView showcaseView) {

                                    }

                                    @Override
                                    public void onShowcaseViewShow(ShowcaseView showcaseView) {

                                    }
                                })
                                .build();
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
        mViewPager.setCurrentItem(getArguments().getBoolean("checklist", false) ? mSectionsPagerAdapter.getCount() - 1 : 0);
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public int difficulty;
        private List<Segment> segments;

        public SectionsPagerAdapter(FragmentManager fm, int difficulty) {
            super(fm);
            this.difficulty = difficulty;
            int drawerItem = (int)((MainActivity) getActivity()).drawerItem;
            try {
                QueryBuilder<Segment, String> queryBuilder = ((BaseActivity)getActivity()).getGlobal().getDaoSegment().queryBuilder();
                Where<Segment, String> where = queryBuilder.where();
                where.eq(Segment.FIELD_CATEGORY, String.valueOf(drawerItem)).and().eq(Segment.FIELD_DIFFICULTY, String.valueOf(difficulty + 1));
                segments = queryBuilder.query();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            Bundle args = new Bundle();
            if (position==0) {
                fragment = new TabbedContentFragment();
            } else if (position==segments.size()+1) {
                fragment = new CheckItemFragment();
            } else {
                fragment = new TabbedSegmentFragment();
                args.putInt(TabbedFragment.ARG_SEGMENT_INDEX, position-1);
            }
            args.putInt(TabbedFragment.ARG_DIFFICULTY_NUMBER, difficulty + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return segments.size()+2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            if (position==0) {
                return getString(R.string.section1_tab_title1).toUpperCase(l);
            } else if (position==segments.size()+1) {
                return getString(R.string.section1_tab_title3).toUpperCase(l);
            } else {
                if (segments.get(position-1).getTitle()!=null) {
                    return segments.get(position-1).getTitle().toUpperCase(l);
                } else {
                    return ("Slide " + position).toUpperCase(l);
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

            int drawerItem = (int)((MainActivity) getActivity()).drawerItem;
            int difficulty = getArguments() != null ? getArguments().getInt(ARG_DIFFICULTY_NUMBER, 1) : 1;
            try {
                QueryBuilder<Segment, String> queryBuilder = ((BaseActivity)getActivity()).getGlobal().getDaoSegment().queryBuilder();
                Where<Segment, String> where = queryBuilder.where();
                where.eq(Segment.FIELD_CATEGORY, String.valueOf(drawerItem)).and().eq(Segment.FIELD_DIFFICULTY, String.valueOf(difficulty));
                final List<Segment> segments = queryBuilder.query();
                if (segments.size() > 0) {
                    GridView gridView = (GridView) rootView.findViewById(R.id.grid_tiles);
                    GridAdapter gAdapter = new GridAdapter(getActivity(), segments);
                    gridView.setAdapter(gAdapter);
                }
                TextView toChecklist = (TextView) rootView.findViewById(R.id.grid_title);
                toChecklist.setText("Checklist");
                int[] colours = {R.color.umbrella_purple, R.color.umbrella_green, R.color.umbrella_yellow};
                toChecklist.setBackgroundColor(getActivity().getResources().getColor(colours[(segments.size()) % 3]));
                CardView checklistCard = (CardView) rootView.findViewById(R.id.checklist_view);
                if (drawerItem == 56) checklistCard.setVisibility(View.GONE);
                checklistCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Fragment frag = getActivity().getSupportFragmentManager().findFragmentByTag("tabbed");
                        if (frag!=null) {
                            ((TabbedFragment)frag).mViewPager.setCurrentItem(segments.size()+2);
                        }
                        if (getActivity()!=null) ((MainActivity) getActivity()).favouriteItem.setVisible(true);
                    }
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return rootView;
        }

    }

    public static class TabbedSegmentFragment extends Fragment {

        private WebView content;

        public TabbedSegmentFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_segment,
                    container, false);
            content = (WebView) rootView.findViewById(R.id.segment_content);

            int drawerItem = (int)((MainActivity) getActivity()).drawerItem;
            int difficulty = getArguments() != null ? getArguments().getInt(ARG_DIFFICULTY_NUMBER, 1) : 1;
            int segmentInt = getArguments() != null ? getArguments().getInt(ARG_SEGMENT_INDEX, 0) : 0;
            List<Segment> segments = null;
            try {
                QueryBuilder<Segment, String> queryBuilder = ((BaseActivity)getActivity()).getGlobal().getDaoSegment().queryBuilder();
                Where<Segment, String> where = queryBuilder.where();
                where.eq(Segment.FIELD_CATEGORY, String.valueOf(drawerItem)).and().eq(Segment.FIELD_DIFFICULTY, String.valueOf(difficulty));
                segments = queryBuilder.query();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (segments!=null && segments.size() > 0 && segments.size()>=segmentInt+1) {
                final String html = segments.get(segmentInt).getBody();
                if (html != null) {
                    content.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            content.loadDataWithBaseURL("file:///android_res/drawable/", "<style>body{color:#444444}img{width:100%}h1{color:#33b5e5; font-weight:normal;}h2{color:#9ABE2E; font-weight:normal;}a{color:#33b5e5}.button,.button:link{display:block;text-decoration:none;color:white;border:none;width:100%;text-align:center;border-radius:3px;padding-top:10px;padding-bottom:10px;}.green{background:#9ABE2E}.purple{background:#b83656}.yellow{background:#f3bc2b}</style>" + html, "text/html", "UTF-8", "UTF-8");
                        }
                    }, 100);
                }
            }
            return rootView;
        }

    }

    public static class CheckItemFragment extends Fragment {

        private List<CheckItem> mCheckList;
        private ProgressBar checkBar;
        private TextView checkBarText;
        private CheckListAdapter cLAdapter;

        public CheckItemFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_check_item,
                    container, false);

            final long drawerItem = ((MainActivity) getActivity()).drawerItem;
            ListView contentBox = (ListView) rootView.findViewById(R.id.content_box);
            checkBar = (ProgressBar) rootView.findViewById(R.id.progress_bar_checked);
            checkBarText = (TextView) rootView.findViewById(R.id.check_bar_text);
            setProgressBarTo(0);
            final int diffArg = getArguments().getInt(ARG_DIFFICULTY_NUMBER, 1);
            ImageButton addItem = (ImageButton) rootView.findViewById(R.id.fab);
            addItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Global global = ((MainActivity) getActivity()).getGlobal();
                    if (!global.hasPasswordSet()) {
                        global.setPassword(getActivity());
                    } else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setTitle("Add a new check item");
                        alert.setMessage("Add your own checklist item\n");
                        final EditText pwInput = new EditText(getActivity());
                        alert.setView(pwInput);
                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String pw = pwInput.getText().toString();
                                if (pw.length()>4) {
                                    CheckItem nItem = new CheckItem(pw, (int) drawerItem);
                                    nItem.setCustom(1);
                                    nItem.setDifficulty(diffArg);
                                    try {
                                        global.getDaoCheckItem().create(nItem);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                    refreshCheckList(drawerItem, diffArg);
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(), "You have added a new item.", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "The item text has to be longer than that", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
                        alert.show();
                    }
                }
            });

            refreshCheckList(drawerItem, diffArg);
            cLAdapter = new CheckListAdapter(getActivity(), mCheckList, this);
            contentBox.setAdapter(cLAdapter);
            contentBox.setDivider(null);
            return rootView;
        }

        @Override
        public void setMenuVisibility(boolean menuVisible) {
            super.setMenuVisibility(menuVisible);
            if (menuVisible) {
               if (getActivity()!=null) ((MainActivity) getActivity()).favouriteItem.setVisible(true);
            } else {
               if (getActivity()!=null) ((MainActivity) getActivity()).favouriteItem.setVisible(false);
            }
        }

        public void refreshCheckList(long category, int difficulty) {
            try {
                QueryBuilder<CheckItem, String> queryBuilder = ((BaseActivity)getActivity()).getGlobal().getDaoCheckItem().queryBuilder();
                Where<CheckItem, String> where = queryBuilder.where();
                where.eq(CheckItem.FIELD_CATEGORY, String.valueOf(category)).and().eq(CheckItem.FIELD_DIFFICULTY, String.valueOf(difficulty));
                mCheckList = queryBuilder.query();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (mCheckList!=null) {
                if (cLAdapter != null) {
                    cLAdapter.updateData(mCheckList);
                }
                if (mCheckList.size() > 0) {
                    int selected = 0;
                    int total = 0;
                    for (CheckItem checkItem : mCheckList) {
                        if (!checkItem.getNoCheck() && !checkItem.isDisabled()) {
                            total++;
                            if (checkItem.getValue()) selected++;
                        }
                    }
                    setProgressBarTo((int) Math.round(selected * 100.0 / total));
                }
            }
        }

        public void setProgressBarTo(int percent) {
            if (percent>=0 && percent<=100) {
                checkBar.setProgress(percent);
                checkBarText.setText(percent + "% filled");
            }
        }
    }

}