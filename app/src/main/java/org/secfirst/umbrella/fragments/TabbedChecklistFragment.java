package org.secfirst.umbrella.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import org.secfirst.umbrella.BaseActivity;
import org.secfirst.umbrella.MainActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.adapters.DashCheckListAdapter;
import org.secfirst.umbrella.models.Category;
import org.secfirst.umbrella.models.CheckItem;
import org.secfirst.umbrella.models.DashCheckFinished;
import org.secfirst.umbrella.models.Difficulty;
import org.secfirst.umbrella.models.Favourite;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class TabbedChecklistFragment extends Fragment {

    private ArrayList<DashCheckFinished> checkLists;
    private DashCheckListAdapter mAdapter;

    public TabbedChecklistFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard_checklist,
                container, false);
        checkLists = getChecklistProgress();

        mAdapter = new DashCheckListAdapter(getActivity(), checkLists);
        ListView checkListView = (ListView) rootView.findViewById(R.id.check_list);
        checkListView.setDividerHeight(10);
        checkListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkLists = getChecklistProgress();
        if (checkLists.size()>0 && mAdapter!=null) {
            mAdapter.updateData(checkLists);
        }
    }

    private int getTotalCheckListPercentage(ArrayList<DashCheckFinished> checkLists) {
        int checked, total;
        checked = total = 0;
        for (DashCheckFinished checkList : checkLists) {
            checked = checked + checkList.getChecked();
            total = total + checkList.getTotal();
        }
        return (int)((checked * 100.0f) / total);
    }

    public ArrayList<DashCheckFinished> getChecklistProgress() {
        final Global global = ((MainActivity) getActivity()).getGlobal();
        ArrayList<DashCheckFinished> returned = new ArrayList<>();
        List<Favourite> favourites = null;
        try {
            favourites = global.getDaoFavourite().queryForAll();
        } catch (SQLException e) {
            UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
        }
        if (favourites!=null) {
            for (Favourite favourite : favourites) {
                List<CheckItem> mCheckList = null;
                try {
                    QueryBuilder<CheckItem, String> queryBuilder = ((BaseActivity) getActivity()).getGlobal().getDaoCheckItem().queryBuilder();
                    Where<CheckItem, String> where = queryBuilder.where();
                    where.eq(CheckItem.FIELD_CATEGORY, favourite.getCategory()).and().eq(CheckItem.FIELD_DIFFICULTY, String.valueOf(favourite.getDifficulty() + 1));
                    mCheckList = queryBuilder.query();
                } catch (SQLException e) {
                    UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
                }
                Category category = null;
                try {
                    category = global.getDaoCategory().queryForId(String.valueOf(favourite.getCategory()));
                } catch (SQLException e) {
                    UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
                }
                if (category != null) {
                    DashCheckFinished dashCheckFinished = new DashCheckFinished(category.getCategory(), favourite.getDifficulty(), true);
                    if (mCheckList != null) {
                        for (CheckItem checkItem : mCheckList) {
                            if (!checkItem.getNoCheck() && !checkItem.isDisabled()) {
                                if (checkItem.getValue()) {
                                    int val = dashCheckFinished.getChecked() + 1;
                                    dashCheckFinished.setChecked(val);
                                }
                                dashCheckFinished.setTotal(dashCheckFinished.getTotal() + 1);
                            }
                        }
                        returned.add(dashCheckFinished);
                    }
                }
            }
        }
        int favReturnedSize = returned.size();
        try {
            QueryBuilder<Difficulty, String> queryBuilder = ((BaseActivity)getActivity()).getGlobal().getDaoDifficulty().queryBuilder().orderBy(Difficulty.FIELD_CREATED_AT, false);
            List<Difficulty> hasDifficulty = queryBuilder.query();
            for (Difficulty difficulty : hasDifficulty) {
                List<CheckItem> mCheckList = null;
                try {
                    QueryBuilder<CheckItem, String> queryBuilder1 = ((BaseActivity)getActivity()).getGlobal().getDaoCheckItem().queryBuilder();
                    Where<CheckItem, String> where = queryBuilder1.where();
                    where.eq(CheckItem.FIELD_CATEGORY, String.valueOf(difficulty.getCategory())).and().eq(CheckItem.FIELD_DIFFICULTY, String.valueOf(difficulty.getSelected() + 1));
                    mCheckList = queryBuilder1.query();
                } catch (SQLException e) {
                    UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
                }
                try {
                    Category category = global.getDaoCategory().queryForId(String.valueOf(difficulty.getCategory()));
                    DashCheckFinished dashCheckFinished = new DashCheckFinished(category.getCategory(), difficulty.getSelected(), false);
                    if (mCheckList!=null) {
                        for (CheckItem checkItem : mCheckList) {
                            if (!checkItem.getNoCheck() && !checkItem.isDisabled()) {
                                if (checkItem.getValue()) {
                                    int val = dashCheckFinished.getChecked() + 1;
                                    dashCheckFinished.setChecked(val);
                                }
                                dashCheckFinished.setTotal(dashCheckFinished.getTotal() + 1);
                            }
                        }
                    }
                    boolean isAlreadyPresent = false;
                    for (DashCheckFinished retItem : returned) {
                        if (retItem.getCategory().equals(dashCheckFinished.getCategory()) && retItem.getDifficulty() == dashCheckFinished.getDifficulty())
                            isAlreadyPresent = true;
                    }
                    if (dashCheckFinished.getChecked() > 0 && !isAlreadyPresent) returned.add(dashCheckFinished);
//                    if (returned.size() - favReturnedSize > 4) break;
                } catch (SQLException e) {
                    UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
                }
            }
        } catch (SQLException e) {
            UmbrellaUtil.logIt(getActivity(), Log.getStackTraceString(e.getCause()));
        }
        DashCheckFinished totalDone = new DashCheckFinished("Total done", getTotalCheckListPercentage(returned), 100, false);
        totalDone.setNoIcon(true);
        returned.add(0, totalDone);
        if (returned.size()<2) {
            DashCheckFinished noItems = new DashCheckFinished("No check items started on yet", 0, 0, false);
            noItems.setNoIcon(true);
            noItems.setNoPercent(true);
            returned.add(noItems);
        }
        return returned;
    }

}