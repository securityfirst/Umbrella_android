package org.secfirst.umbrella.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.j256.ormlite.stmt.PreparedQuery;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.adapters.FilledOutFormListAdapter;
import org.secfirst.umbrella.adapters.FormListAdapter;
import org.secfirst.umbrella.models.DashCheckFinished;
import org.secfirst.umbrella.models.Form;
import org.secfirst.umbrella.models.FormValue;
import org.secfirst.umbrella.util.Global;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TabbedFormsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private ArrayList<DashCheckFinished> checkLists;
    private FormListAdapter formListAdapter;
    private FilledOutFormListAdapter filledOutAdapter;
    Global global;
    List<Form> allForms = new ArrayList<>();
    SwipeRefreshLayout mSwipeRefresh;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        global = (Global) context.getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dashboard_forms,
                container, false);

        RecyclerView formListView = (RecyclerView) rootView.findViewById(R.id.form_list);
        RecyclerView filledOutList = (RecyclerView) rootView.findViewById(R.id.filled_out_list);

        mSwipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefresh.setOnRefreshListener(this);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        formListView.setLayoutManager(llm);

        formListAdapter = new FormListAdapter(getContext());

        formListView.setAdapter(formListAdapter);

        LinearLayoutManager llm1 = new LinearLayoutManager(getContext());
        llm1.setOrientation(LinearLayoutManager.VERTICAL);
        formListView.setLayoutManager(llm1);

        filledOutAdapter = new FilledOutFormListAdapter(getContext());
        filledOutList.setAdapter(filledOutAdapter);
        LinearLayoutManager llm2 = new LinearLayoutManager(getContext());
        llm2.setOrientation(LinearLayoutManager.VERTICAL);
        filledOutList.setLayoutManager(llm2);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    public void onRefresh() {
        try {
            allForms = global.getDaoForm().queryForAll();
            PreparedQuery<FormValue> queryBuilder = global.getDaoFormValue().queryBuilder().groupBy(FormValue.FIELD_SESSION).prepare();
            List<FormValue> fValues = global.getDaoFormValue().query(queryBuilder);
            formListAdapter.updateData(allForms);
            filledOutAdapter.updateData(fValues);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mSwipeRefresh.setRefreshing(false);
    }

}
