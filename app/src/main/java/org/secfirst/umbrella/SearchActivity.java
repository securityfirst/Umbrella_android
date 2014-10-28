package org.secfirst.umbrella;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import org.secfirst.umbrella.adapters.SearchAdapter;
import org.secfirst.umbrella.data.SegmentsDataSource;
import org.secfirst.umbrella.models.Segment;

import java.util.ArrayList;


public class SearchActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.search_results_list);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        LinearLayout noResults = (LinearLayout) findViewById(R.id.no_results);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (query!=null) {
                SegmentsDataSource dataSource = new SegmentsDataSource(this);
                dataSource.open();
                ArrayList<Segment> mSegments = new ArrayList<Segment>();
                mSegments = dataSource.searchSegments(query);
                dataSource.close();
                if (mSegments.size()>0) {
                    Log.i("result count", String.valueOf(mSegments.size()));
                    RecyclerView.Adapter mAdapter = new SearchAdapter(this, mSegments);
                    mRecyclerView.setAdapter(mAdapter);
                    noResults.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        }
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
