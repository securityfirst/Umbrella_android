package org.secfirst.umbrella;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.secfirst.umbrella.adapters.SettingsAdapter;
import org.secfirst.umbrella.data.CheckListDataSource;
import org.secfirst.umbrella.data.SegmentsDataSource;
import org.secfirst.umbrella.models.CheckItem;
import org.secfirst.umbrella.models.Segment;
import org.secfirst.umbrella.util.UmbrellaRestClient;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class SettingsActivity extends ActionBarActivity {
    private ProgressDialog mProgress;
    private static int syncDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.settings_list);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.Adapter mAdapter = new SettingsAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

    }

    public void checkDone() {
        syncDone++;
        if (syncDone==2) mProgress.dismiss();
    }

    public void syncApi() {
        syncDone = 0;
        mProgress = UmbrellaUtil.launchRingDialogWithText(SettingsActivity.this, "Checking for updates");

        UmbrellaRestClient.get("segments", null, null, SettingsActivity.this, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Gson gson = new GsonBuilder().create();
                Type listType = new TypeToken<ArrayList<Segment>>() {
                }.getType();
                ArrayList<Segment> receivedSegments = gson.fromJson(response.toString(), listType);
                if (receivedSegments.size() > 0) {
                    SegmentsDataSource segmentDAO = new SegmentsDataSource(SettingsActivity.this);
                    segmentDAO.open();
                    UmbrellaUtil.syncSegments(segmentDAO, receivedSegments);
                    Log.i("segments", "synced");
                }
                checkDone();
            }
        });

        UmbrellaRestClient.get("check_items", null, null, SettingsActivity.this, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Gson gson = new GsonBuilder().create();
                Type listType = new TypeToken<ArrayList<CheckItem>>() {
                }.getType();
                ArrayList<CheckItem> receivedItems = gson.fromJson(response.toString(), listType);
                if (receivedItems.size() > 0) {
                    CheckListDataSource checkListDataSource = new CheckListDataSource(SettingsActivity.this);
                    checkListDataSource.open();
                    UmbrellaUtil.syncCheckLists(checkListDataSource, receivedItems);
                    Log.i("check items", "synced");
                }
                checkDone();
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
