package org.secfirst.umbrella.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.j256.ormlite.stmt.PreparedQuery;

import org.secfirst.umbrella.BaseActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.models.CategoryItem;
import org.secfirst.umbrella.models.Difficulty;
import org.secfirst.umbrella.util.Global;

import java.sql.SQLException;
import java.util.List;

import timber.log.Timber;


public class DifficultyFragment extends Fragment {

    private static final String SECTION_VALUE = "section_value";
    public static final String BEGINNER = "beginner";
    public static final String INTERMEDIATE = "advanced";
    public static final String EXPERT = "expert";

    private String mSection;

    private OnDifficultySelected mListener;

    public static DifficultyFragment newInstance(String sectionValue) {
        DifficultyFragment fragment = new DifficultyFragment();
        Bundle args = new Bundle();
        args.putString(SECTION_VALUE, sectionValue);
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
            mSection = getArguments().getString(SECTION_VALUE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_select, container, false);
        Global global = ((BaseActivity) getActivity()).getGlobal();
        try {
            PreparedQuery<CategoryItem> queryBuilder =
                    global.getDaoCategoryItem().queryBuilder().where().eq(CategoryItem.FIELD_NAME, mSection).prepare();
            Timber.d("msec %s", mSection);
            final CategoryItem childCategory = global.getDaoCategoryItem().queryForFirst(queryBuilder);
            View btnBeginner = v.findViewById(R.id.card_beginner);
            btnBeginner.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDifficultySelected(BEGINNER);
                }
            });
            View btnIntermediate = v.findViewById(R.id.card_intermediate);
            btnIntermediate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDifficultySelected(INTERMEDIATE);
                }
            });
            View btnExpert = v.findViewById(R.id.card_expert);
            btnExpert.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
onDifficultySelected(EXPERT);
                }
            });
            ((TextView) v.findViewById(R.id.beginner_description)).setText("Beginner");
            ((TextView) v.findViewById(R.id.advanced_description)).setText("Advanced");
            ((TextView) v.findViewById(R.id.expert_description)).setText("Expert");
            btnBeginner.setVisibility(childCategory.hasDifficulty(global, BEGINNER) ? View.VISIBLE : View.GONE);
            btnIntermediate.setVisibility(childCategory.hasDifficulty(global, INTERMEDIATE) ? View.VISIBLE : View.GONE);
            btnExpert.setVisibility(childCategory.hasDifficulty(global, EXPERT) ? View.VISIBLE : View.GONE);
        } catch (SQLException e) {
            Timber.e(e);
        }
        return v;
    }

    public void onDifficultySelected(String difficulty) {
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
        void onDifficultySelected(String difficulty);
    }

}
