package org.secfirst.umbrella.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.models.Category;
import org.secfirst.umbrella.models.Difficulty;

import java.util.List;


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
        final Category childCategory = Category.findById(Category.class, mSection);
        View v = inflater.inflate(R.layout.fragment_select, container, false);
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
        btnBeginner.setVisibility(childCategory.getDifficultyBeginner() ? View.VISIBLE : View.GONE);
        btnIntermediate.setVisibility(childCategory.getDifficultyAdvanced() ? View.VISIBLE : View.GONE);
        btnExpert.setVisibility(childCategory.getDifficultyExpert() ? View.VISIBLE : View.GONE);
        return v;
    }

    public void onDifficultySelected(int difficulty) {
        List<Difficulty> df = Difficulty.find(Difficulty.class, "category = ?", String.valueOf(mSection));
        Difficulty d;
        if (df.size()>0) {
            d = df.get(0);
            d.setSelected(difficulty);
        } else {
            d = new Difficulty(mSection, difficulty);
        }
        Log.i("set diff1", String.valueOf(difficulty));
        d.save();
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
