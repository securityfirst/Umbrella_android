package org.secfirst.umbrella;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;

import org.secfirst.umbrella.adapters.SearchAdapter;
import org.secfirst.umbrella.models.Segment;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.sql.SQLException;
import java.util.List;


public class SearchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.search_results_list);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        LinearLayout noResults = (LinearLayout) findViewById(R.id.no_results);
        LinearLayout results = (LinearLayout) findViewById(R.id.results);
        TextView searchCount = (TextView) findViewById(R.id.search_count_text);

        Intent intent = getIntent();
        if (intent!=null && Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query!=null) {
                List<Segment> mSegments;
                try {
                    QueryBuilder<Segment, String> queryBuilder = global.getDaoSegment().queryBuilder();
                    Where<Segment, String> where = queryBuilder.where();
                    where.like(Segment.FIELD_BODY, new SelectArg("%" + query + "%"));
                    mSegments = queryBuilder.query();
                    RecyclerView.Adapter mAdapter = new SearchAdapter(this, mSegments, query);
                    mRecyclerView.setAdapter(mAdapter);
                    noResults.setVisibility(View.GONE);
                    results.setVisibility(View.VISIBLE);
                    String searchCountText = mSegments.size()+" "+((mSegments.size()==1)?getString(R.string.search_result):getString(R.string.results))+" "+getString(R.string.found_for_this_query);
                    searchCount.setText(searchCountText);
                } catch (SQLException e) {
                    UmbrellaUtil.logIt(this, Log.getStackTraceString(e.getCause()));
                }
            }
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_search;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
