package org.secfirst.umbrella.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.j256.ormlite.stmt.PreparedQuery;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.models.Category;
import org.secfirst.umbrella.models.Difficulty;
import org.secfirst.umbrella.util.Global;

import java.sql.SQLException;

import timber.log.Timber;


public class DifficultyFragment extends Fragment {

    private static final String SECTION_NUMBER = "section_number";
    public static final int BEGINNER = 0;
    public static final int INTERMEDIATE = 1;
    public static final int EXPERT = 2;

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
        Difficulty d = getSelectedDifficulty();
        if (d!=null && d.getSelected()>= 0 && d.getSelected()<3) {
            onDifficultySelected(d.getSelected());
        } else {
            try {
                final Category childCategory = Global.INSTANCE.getDaoCategory().queryForId(String.valueOf(mSection));
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
                ((TextView) v.findViewById(R.id.beginner_description)).setText(childCategory.getTextBeginner());
                ((TextView) v.findViewById(R.id.advanced_description)).setText(childCategory.getTextAdvanced());
                ((TextView) v.findViewById(R.id.expert_description)).setText(childCategory.getTextExpert());
                btnBeginner.setVisibility(childCategory.getDifficultyBeginner() ? View.VISIBLE : View.GONE);
                btnIntermediate.setVisibility(childCategory.getDifficultyAdvanced() ? View.VISIBLE : View.GONE);
                btnExpert.setVisibility(childCategory.getDifficultyExpert() ? View.VISIBLE : View.GONE);
            } catch (SQLException e) {
                Timber.e(e);
            }
        }
        return v;
    }

    public void onDifficultySelected(int difficulty) {
        Difficulty d = getSelectedDifficulty();
        if (d!=null) {
            d.setSelected(difficulty);
            try {
                Global.INSTANCE.getDaoDifficulty().update(d);
            } catch (SQLException e) {
                Timber.e(e);
            }
        } else {
            try {
                Global.INSTANCE.getDaoDifficulty().create(new Difficulty(mSection, difficulty));
            } catch (SQLException e) {
                Timber.e(e);
            }
        }
        if (mListener != null) {
            mListener.onDifficultySelected(difficulty);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnDifficultySelected) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnDifficultySelected {
        void onDifficultySelected(int difficulty);
    }

    private Difficulty getSelectedDifficulty() {
        Difficulty df = null;
        try {
            PreparedQuery<Difficulty> queryBuilder = Global.INSTANCE.getDaoDifficulty().queryBuilder().where().eq(Difficulty.FIELD_CATEGORY, mSection).prepare();
            df = Global.INSTANCE.getDaoDifficulty().queryForFirst(queryBuilder);
        } catch (SQLException e) {
            Timber.e(e);
        }
        return df;
    }

}
