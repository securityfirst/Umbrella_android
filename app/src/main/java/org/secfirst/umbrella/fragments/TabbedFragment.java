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

import org.secfirst.umbrella.MainActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.adapters.CheckListAdapter;
import org.secfirst.umbrella.adapters.GridAdapter;
import org.secfirst.umbrella.models.CheckItem;
import org.secfirst.umbrella.models.Segment;
import org.secfirst.umbrella.util.Global;

import java.util.List;
import java.util.Locale;

public class TabbedFragment extends Fragment {

    public static final String ARG_DIFFICULTY_NUMBER = "spinner_number";
    public static final String ARG_SEGMENT_INDEX = "segment_index";
    SectionsPagerAdapter mSectionsPagerAdapter;
    public ViewPager mViewPager;
    public static int difficulty;
    public long sectionNumber;

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
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mSectionsPagerAdapter.difficulty = difficulty;
        mViewPager = (ViewPager) v.findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
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

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            int drawerItem = (int)((MainActivity) getActivity()).drawerItem;
            segments = Segment.find(Segment.class, "category = ? and difficulty = ?", String.valueOf(drawerItem), String.valueOf(difficulty+1));
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
            final List<Segment> segments = Segment.find(Segment.class, "category = ? and difficulty = ?", String.valueOf(drawerItem), String.valueOf(difficulty));
            if (segments.size() > 0) {
                GridView gridView = (GridView) rootView.findViewById(R.id.grid_tiles);
                GridAdapter gAdapter = new GridAdapter(getActivity(), segments);
                gridView.setAdapter(gAdapter);
            }
            TextView toChecklist = (TextView) rootView.findViewById(R.id.grid_title);
            View checkListBox = rootView.findViewById(R.id.color_box);
            toChecklist.setText("Checklist");
            int[] colours = {R.color.umbrella_purple, R.color.umbrella_green, R.color.umbrella_yellow};
            checkListBox.setBackgroundColor(getActivity().getResources().getColor(colours[(segments.size()) % 3]));
            CardView checklistCard = (CardView) rootView.findViewById(R.id.checklist_view);
            checklistCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment frag = getActivity().getSupportFragmentManager().findFragmentByTag("tabbed");
                    if (frag!=null) {
                        ((TabbedFragment)frag).mViewPager.setCurrentItem(segments.size()+2);
                    }
                }
            });
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
            List<Segment> segments = Segment.find(Segment.class, "category = ? and difficulty = ?", String.valueOf(drawerItem), String.valueOf(difficulty));
            if (segments.size() > 0 && segments.size()>=segmentInt+1) {
                final String html = segments.get(segmentInt).getBody();
                if (html != null) {
                    content.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            content.loadDataWithBaseURL("file:///android_res/drawable/", "<style>img{width:100%}h1{color:#33b5e5}h2{color:#9ABE2E}</style>" + html, "text/html", "UTF-8", "UTF-8");
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
                    Global global = ((MainActivity) getActivity()).getGlobal();
                    if (!global.hasPasswordSet()) {
                        global.setPassword(getActivity());
                    } else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                        alert.setTitle("Add a new check item");
                        alert.setMessage("Set a meaningful message for the check item\n");
                        final EditText pwInput = new EditText(getActivity());
                        alert.setView(pwInput);
                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String pw = pwInput.getText().toString();
                                if (pw.length()>4) {
                                    CheckItem nItem = new CheckItem(pw, (int) drawerItem);
                                    nItem.setCustom(1);
                                    nItem.setDifficulty(diffArg);
                                    nItem.save();
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

        public void refreshCheckList(long category, int difficulty) {
            mCheckList = CheckItem.find(CheckItem.class, "category = ? and difficulty = ?", String.valueOf(category), String.valueOf(difficulty));
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

        public void setProgressBarTo(int percent) {
            if (percent>=0 && percent<=100) {
                checkBar.setProgress(percent);
                checkBarText.setText(percent + "% filled");
            }
        }
    }

}