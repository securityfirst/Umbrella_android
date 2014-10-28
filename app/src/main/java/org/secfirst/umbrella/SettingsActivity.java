package org.secfirst.umbrella;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.secfirst.umbrella.adapters.SettingsAdapter;
import org.secfirst.umbrella.data.SegmentsDataSource;
import org.secfirst.umbrella.models.Segment;
import org.secfirst.umbrella.util.UmbrellaRestClient;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class SettingsActivity extends ActionBarActivity {
    private RecyclerView.Adapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.settings_list);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new SettingsAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

    }

    public void syncApi() {
        UmbrellaRestClient.get("segments", null, null, SettingsActivity.this, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Gson gson = new GsonBuilder().create();
                Type listType = new TypeToken<ArrayList<Segment>>() {
                }.getType();
                ArrayList<Segment> receivedSegments = gson.fromJson(response.toString(), listType);
                if (receivedSegments.size()>0) {
                    SegmentsDataSource segmentDAO = new SegmentsDataSource(SettingsActivity.this);
                    segmentDAO.open();
                    UmbrellaUtil.syncSegments(segmentDAO, receivedSegments);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id==android.R.id.home) {
            startActivity(new Intent(this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
