package org.secfirst.umbrella.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.secfirst.umbrella.MainActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.adapters.CheckListAdapter;
import org.secfirst.umbrella.models.CheckItem;
import org.secfirst.umbrella.models.Difficulty;
import org.secfirst.umbrella.models.Favourite;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

import static org.secfirst.umbrella.fragments.TabbedFragment.ARG_DIFFICULTY_NUMBER;

/**
 * Created by dougl on 20/02/2018.
 */

public class CheckItemFragment extends Fragment implements CheckListAdapter.OnCheckListEvent {

    private List<CheckItem> mCheckList;
    private ProgressBar checkBar;
    private TextView checkBarText;
    private CheckListAdapter cLAdapter;
    private int mDifficult;

    public CheckItemFragment() {
    }

    public static CheckItemFragment newInstance(int difficult) {
        Bundle args = new Bundle();
        CheckItemFragment fragment = new CheckItemFragment();
        fragment.setArguments(args);
        fragment.mDifficult = difficult;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        View rootView = inflater.inflate(R.layout.fragment_check_item,
                container, false);

        final long drawerItem = ((MainActivity) getActivity()).drawerItem;
        RecyclerView contentBox = rootView.findViewById(R.id.content_box);
        checkBar = rootView.findViewById(R.id.progress_bar_checked);
        checkBarText = rootView.findViewById(R.id.check_bar_text);
        setProgressBarTo(0);
        final int diffArg = getArguments().getInt(ARG_DIFFICULTY_NUMBER, 1);
        ImageButton addItem = rootView.findViewById(R.id.fab);
        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Global.INSTANCE.hasPasswordSet(false)) {
                    Global.INSTANCE.setPassword(getActivity(), null);
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                    alert.setTitle(R.string.add_new_checkitem);
                    alert.setMessage(R.string.add_own_checklist_item);
                    final EditText pwInput = new EditText(getActivity());
                    alert.setView(pwInput);
                    alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            String pw = pwInput.getText().toString();
                            if (pw.length() > 4) {
                                CheckItem nItem = new CheckItem(pw, (int) drawerItem);
                                nItem.setCustom(1);
                                nItem.setDifficulty(diffArg);
                                nItem.setDifficultyString(UmbrellaUtil.getDifficultyFromId(diffArg));
                                try {
                                    Global.INSTANCE.getDaoCheckItem().create(nItem);
                                } catch (SQLException e) {
                                    Timber.e(e);
                                }
                                refreshCheckList(drawerItem, diffArg);
                                dialog.dismiss();
                                Toast.makeText(getActivity(), R.string.you_have_added_new_item, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), R.string.text_item_has_to_be_longer, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });
                    alert.show();
                }
            }
        });

        refreshCheckList(drawerItem, diffArg);
        cLAdapter = new CheckListAdapter(getActivity(), this);
        contentBox.setAdapter(cLAdapter);
        contentBox.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        contentBox.setLayoutManager(mLayoutManager);
        populateAdapter();
        return rootView;
    }

    private void populateAdapter() {
        for (CheckItem checkItem : mCheckList) {
            cLAdapter.add(checkItem);
        }
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (menuVisible) {
            if (getActivity() != null) setFavouriteIcon(getActivity());
        } else {
            if (getActivity() != null)
                ((MainActivity) getActivity()).favouriteItem.setVisible(false);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (getActivity() != null) setFavouriteIcon(getActivity());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (getActivity() != null) {
            final long drawerItem = ((MainActivity) getActivity()).drawerItem;
            if (id == R.id.favourite) {
                List<Difficulty> hasDifficulty = null;
                try {
                    hasDifficulty = Global.INSTANCE.getDaoDifficulty().queryForEq(Difficulty.FIELD_CATEGORY, String.valueOf(drawerItem));
                } catch (SQLException e) {
                    Timber.e(e);
                }
                if (hasDifficulty != null && !hasDifficulty.isEmpty()) {
                    try {
                        QueryBuilder<Favourite, String> queryBuilder = Global.INSTANCE.getDaoFavourite().queryBuilder();
                        Where<Favourite, String> where = queryBuilder.where();
                        where.eq(Favourite.FIELD_CATEGORY, String.valueOf(drawerItem)).and().eq(Favourite.FIELD_DIFFICULTY, String.valueOf(hasDifficulty.get(0).getSelected()));
                        Favourite favourite = queryBuilder.queryForFirst();
                        if (favourite != null) {
                            try {
                                Global.INSTANCE.getDaoFavourite().delete(favourite);
                            } catch (SQLException e) {
                                Timber.e(e);
                            }
                        } else {
                            try {
                                Global.INSTANCE.getDaoFavourite().create(new Favourite(drawerItem, hasDifficulty.get(0).getSelected()));
                            } catch (SQLException e) {
                                Timber.e(e);
                            }
                        }
                    } catch (SQLException e) {
                        Timber.e(e);
                    }
                }
                setFavouriteIcon(getActivity());
                getActivity().invalidateOptionsMenu();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void setFavouriteIcon(Context context) {
        final long drawerItem = ((MainActivity) context).drawerItem;
        try {
            QueryBuilder<Favourite, String> queryBuilder = Global.INSTANCE.getDaoFavourite().queryBuilder();
            Where<Favourite, String> where = queryBuilder.where();
            where.eq(Favourite.FIELD_CATEGORY, String.valueOf(drawerItem)).and().eq(Favourite.FIELD_DIFFICULTY, mDifficult);
            Favourite favourite = queryBuilder.queryForFirst();
            ((MainActivity) context).favouriteItem.setIcon(favourite != null ? R.drawable.abc_btn_rating_star_on_mtrl_alpha : R.drawable.abc_btn_rating_star_off_mtrl_alpha);
            ((MainActivity) context).favouriteItem.setVisible(true);
        } catch (SQLException e) {
            Timber.e(e);
        }
    }

    @Override
    public void refreshCheckList(List<CheckItem> checkList) {
        if (checkList != null && !checkList.isEmpty()) {
            int selected = 0;
            int total = 0;
            for (CheckItem checkItem : checkList) {
                if (!checkItem.getNoCheck() && !checkItem.isDisabled()) {
                    total++;
                    if (checkItem.getValue()) selected++;
                }
            }
            setProgressBarTo((int) Math.round(selected * 100.0 / total));
        }
    }

    public void refreshCheckList(long category, int difficulty) {
        try {
            QueryBuilder<CheckItem, String> queryBuilder = Global.INSTANCE.getDaoCheckItem().queryBuilder();
            Where<CheckItem, String> where = queryBuilder.where();
            where.eq(CheckItem.FIELD_CATEGORY, String.valueOf(category)).and().eq(CheckItem.FIELD_DIFFICULTY, String.valueOf(difficulty));
            mCheckList = queryBuilder.query();
        } catch (SQLException e) {
            Timber.e(e);
        }

        if (mCheckList != null) {
            if (cLAdapter != null) {
                cLAdapter.updateData(mCheckList);
            }
            if (!mCheckList.isEmpty()) {
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
        if (percent >= 0 && percent <= 100) {
            checkBar.setProgress(percent);
            checkBarText.setText(percent + "% " + getActivity().getString(R.string.filled));
        }
    }
}
