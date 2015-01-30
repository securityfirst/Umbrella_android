package org.secfirst.umbrella.fragments;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.secfirst.umbrella.BuildConfig;
import org.secfirst.umbrella.MainActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.adapters.CheckListAdapter;
import org.secfirst.umbrella.models.CheckItem;
import org.secfirst.umbrella.models.Segment;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.util.List;
import java.util.Locale;

public class TabbedFragment extends Fragment {

    public static final String ARG_DIFFICULTY_NUMBER = "spinner_number";
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    public static int difficulty;
    public long sectionNumber;

    public static TabbedFragment newInstance(long sectionNumber, int spinnerNumber) {
        TabbedFragment tabbedFragment = new TabbedFragment();
        Bundle args = new Bundle();
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
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public int difficulty;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            Bundle args = new Bundle();
            if (position==0) {
                fragment = new TabbedContentFragment();
            } else {
                fragment = new CheckItemFragment();
            }
            args.putInt(TabbedFragment.ARG_DIFFICULTY_NUMBER, difficulty + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.section1_tab_title1).toUpperCase(l);
                case 1:
                    return getString(R.string.section1_tab_title3).toUpperCase(l);
            }
            return null;
        }
    }

    public static class TabbedContentFragment extends Fragment {

        private List<Segment> mSegments;
        private TextView content;
        private static String[][] contentHtml = {{},{}, {},
                {
                        "<h1>Keep what you need</h1><p>The most basic rule for managing information securely is that any information that could put people at risk should always be compartmentalised and kept ‘need to know’. This means that only those people who really need that piece of information to operate should be given it. For example, if a team is working with a high-risk source, only the source’s direct contact within the team should have his or her real name and details – the rest of the team can operate using a code name. This reduces the risk for both the source and those other members of the team.</p><p>If there is not a good reason to keep a piece of sensitive information then you should simply delete it (see the Safely Deleting lesson).</p><img src=\"pic\"><h1>Threat Modelling</h1><p>There is no single solution for keeping your information safe. Managing your information securely isn’t about which tools you use; it’s about understanding the threats you face and how you can counter those threats. To become more secure, you should figure out what you need to protect, and whom you need to protect it from. Threats can change depending on where you’re located, what you’re doing, and whom you’re working with. The easiest way to figure out what solutions are best for you is to carry out a threat modelling assessment.</p><p>When carrying out an assessment, there are five main questions you should ask yourself:</p><h2>1. What do you want to protect?</h2><p>What information could put you, your work or others at risk if were public? This is often the kind of information kept in your emails, contact lists, messages and files. It might relate to a specific sensitive campaign you are working on.</p><h2>2. Who do you want to protect it from?</h2><p>This could be any person or entity that poses a threat against an your or your work, also known as an adversary. Think about who would have a motive in reading or deleting your information or disrupting your work. Examples could be a government, a company you are exposing, your boss, or a hacker.</p><h2>3. How likely is it that you will need to protect it?</h2><p>It is important to distinguish between threats and risks. While a threat is a bad thing that can happen, risk is the likelihood that the threat will occur. Calculating risk means figuring out the chance that a threat might actually occur – how likely is it that a threat would be carried out? You also need to think about the capability of potential attackers. For example, your mobile phone provider has access to all of your phone records and therefore has the capability to use or share that data. A hacker on an open Wi-Fi network can access your unencrypted communications. A government might have stronger capabilities.</p><h2>4. How bad are the consequences if you fail?</h2><p>The motives of adversaries differ widely, as do their attacks. A company trying to prevent the spread of a video showing their illegal activity may simply want to delete the video, whereas a government may wish to gain access to the names/details of activists it sees as a threat to the state in order to arrest or harass them.</p><h2>5. How much trouble are you willing to go through in order to try to prevent those?</h2><p>This means figuring out which threats you are going to take seriously, and which may be too rare or too harmless (or too difficult to combat) to worry about. Many people find certain threats unacceptable no matter what the risk, because the presence of the threat at any likelihood is not worth the cost. In other cases, people disregard high risks because they don't view the threat as a problem.</p><h3>RELATED LESSONS/TOOLS</h3><p>- Information Security<br>- Communications Security<br>- Network Security</p><h3>FURTHER READING</h3><p>- https://ssd.eff.org/en/module/introduction-threat-modeling<br>- Citizen Lab  - https://citizenlab.org<br>- Crypto Law Survey - http://www.cryptolaw.org<br>- OpenNet (Outlines internet restrictions) - https://opennet.net<br>- https://prism-break.org/en/<br>- DLA Piper (Data Protection Laws of the World) - http://www.dlapiperdataprotection.com</p>"
                },
                {
                        "",
                        ""
                }
        };

        public TabbedContentFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_tabbed_content,
                    container, false);

            int drawerItem = (int)((MainActivity) getActivity()).drawerItem;
            content = (TextView) rootView.findViewById(R.id.content);
            int difficulty = getArguments().getInt(ARG_DIFFICULTY_NUMBER, 1) - 1;
            if (contentHtml.length > drawerItem && contentHtml[drawerItem].length > difficulty) {
                String html = contentHtml[drawerItem][difficulty];
                html = html.replaceAll("\\<h1\\>", "<p><font color=\"#33b5e5\"><big><big>");
                html = html.replaceAll("\\</h1\\>", "</big></big></font></p>");
                html = html.replaceAll("\\<h2\\>", "<p><font color=\"#9ABE2E\"><big>");
                html = html.replaceAll("\\</h2\\>", "</big></font></p>");
                content.setText(Html.fromHtml(html, new Html.ImageGetter() {
                    @Override
                    public Drawable getDrawable(String source) {
                        Drawable d = getActivity().getResources().getDrawable(getActivity().getResources().getIdentifier(source, "drawable", BuildConfig.APPLICATION_ID));
                        d.setBounds(0, 0, getActivity().getWindowManager().getDefaultDisplay().getWidth(), d.getIntrinsicHeight() * getActivity().getWindowManager().getDefaultDisplay().getWidth() / d.getIntrinsicWidth());
                        return d;
                    }
                }, null));
            } else {
                content.setText("");
            }
            return rootView;
        }

    }

    public static class CheckItemFragment extends Fragment {

        private List<CheckItem> mCheckList;
        private ProgressBar checkBar;
        private TextView checkBarText, textDifficulty;

        public CheckItemFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_check_item,
                    container, false);


            long drawerItem = ((MainActivity) getActivity()).drawerItem;
            ListView contentBox = (ListView) rootView.findViewById(R.id.content_box);
            checkBar = (ProgressBar) rootView.findViewById(R.id.progress_bar_checked);
            checkBarText = (TextView) rootView.findViewById(R.id.check_bar_text);
            setProgressBarTo(0);
            textDifficulty = (TextView) rootView.findViewById(R.id.difficulty);
            textDifficulty.setText(UmbrellaUtil.getDifficultyString(getArguments().getInt(ARG_DIFFICULTY_NUMBER, 1)));

            switch (getArguments().getInt(TabbedFragment.ARG_DIFFICULTY_NUMBER, 1)) {
                case 1:
                    // do the beginner check list
                case 2:
                    // do the intermediate check list
                case 3:
                    // show expert check list
                default:
                    //default to beginner for now
            }
            refreshCheckList(drawerItem);
            contentBox.setAdapter(new CheckListAdapter(getActivity(), mCheckList, this));
            contentBox.setDivider(null);
            return rootView;
        }

        public void refreshCheckList(long category) {
            mCheckList = CheckItem.find(CheckItem.class, "category = ?", String.valueOf(category));
            if (mCheckList.size() > 0) {
                int selected = 0;
                for (CheckItem checkItem : mCheckList) {
                    if (checkItem.getValue()) selected++;
                }
                setProgressBarTo((int) Math.round(selected * 100.0 / mCheckList.size()));
            }
        }

        public void setProgressBarTo(int percent) {
            if (percent>=0 && percent<=100) {
                checkBar.setProgress(percent);
                checkBarText.setText(percent + "% done.");
            }
        }
    }

}