package org.secfirst.umbrella.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.secfirst.umbrella.BaseActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.models.Category;
import org.secfirst.umbrella.models.Difficulty;
import org.secfirst.umbrella.util.Global;

import java.sql.SQLException;
import java.util.List;

import timber.log.Timber;


public class DifficultyFragment extends Fragment {

    private static final String SECTION_NUMBER = "section_number";
    private static final int BEGINNER = 0;
    private static final int INTERMEDIATE = 1;
    private static final int EXPERT = 2;

    private long mSection;

    private OnDifficultySelected mListener;

    public static DifficultyFragment newInstance(long sectionNumber) {
        DifficultyFragment fragment = new DifficultyFragment();
        Bundle args = new Bundle();
        args.putLong(SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public DifficultyFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSection = getArguments().getLong(SECTION_NUMBER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_select, container, false);
        Global global = ((BaseActivity) getActivity()).getGlobal();
        try {
            final Category childCategory = global.getDaoCategory().queryForId(String.valueOf(mSection));
            View btnBeginner = v.findViewById(R.id.card_beginner);
            btnBeginner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDifficultySelected(getString(R.string.beginner));
                }
            });
            View btnIntermediate = v.findViewById(R.id.card_intermediate);
            btnIntermediate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDifficultySelected(getString(R.string.advanced));
                }
            });
            View btnExpert = v.findViewById(R.id.card_expert);
            btnExpert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

onDifficultySelected(getString(R.string.expert));
                }
            });
            ((TextView) v.findViewById(R.id.beginner_description)).setText("Beginner");
            ((TextView) v.findViewById(R.id.advanced_description)).setText("Advanced");
            ((TextView) v.findViewById(R.id.expert_description)).setText("Expert");
            btnBeginner.setVisibility(childCategory.hasDifficulty(global, getString(R.string.beginner)) ? View.VISIBLE : View.GONE);
            btnIntermediate.setVisibility(childCategory.hasDifficulty(global, getString(R.string.advanced)) ? View.VISIBLE : View.GONE);
            btnExpert.setVisibility(childCategory.hasDifficulty(global, getString(R.string.expert)) ? View.VISIBLE : View.GONE);
        } catch (SQLException e) {
            Timber.e(e);
        }
        return v;
    }

    public void onDifficultySelected(int difficulty) {
        Global global = ((BaseActivity) getActivity()).getGlobal();
        List<Difficulty> df = null;
        try {
            df = global.getDaoDifficulty().queryForEq(Difficulty.FIELD_CATEGORY, String.valueOf(mSection));
        } catch (SQLException e) {
            Timber.e(e);
        }
        Difficulty d;
        if (df!=null && !df.isEmpty()) {
            d = df.get(0);
            d.setSelected(difficulty);
            try {
                global.getDaoDifficulty().update(d);
            } catch (SQLException e) {
                Timber.e(e);
            }
        } else {
            try {
                global.getDaoDifficulty().create(new Difficulty(mSection, difficulty));
            } catch (SQLException e) {
                Timber.e(e);
            }
        }
        if (mListener != null) {
            mListener.onDifficultySelected(difficulty);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnDifficultySelected) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnDifficultySelected {
        public void onDifficultySelected(int difficulty);
    }

}
