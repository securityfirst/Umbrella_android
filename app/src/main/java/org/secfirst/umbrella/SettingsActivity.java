package org.secfirst.umbrella;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import org.secfirst.umbrella.adapters.SettingsAdapter;


public class SettingsActivity extends BaseActivity {
    private ProgressDialog mProgress;
    private static int syncDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.settings_list);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.Adapter mAdapter = new SettingsAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_settings;
    }

    public void checkDone() {
        syncDone++;
        if (syncDone==2) mProgress.dismiss();
    }

    public void syncApi() {
        syncDone = 0;
//        mProgress = UmbrellaUtil.launchRingDialogWithText(SettingsActivity.this, "Checking for updates");
//
//        UmbrellaRestClient.get("segments", null, null, SettingsActivity.this, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                super.onSuccess(statusCode, headers, response);
//                Gson gson = new GsonBuilder().create();
//                Type listType = new TypeToken<ArrayList<Segment>>() {
//                }.getType();
//                ArrayList<Segment> receivedSegments = gson.fromJson(response.toString(), listType);
//                if (receivedSegments.size() > 0) {
//                    UmbrellaUtil.syncSegments(receivedSegments);
//                    Log.i("segments", "synced");
//                }
//                checkDone();
//            }
//        });
//
//        UmbrellaRestClient.get("check_items", null, null, SettingsActivity.this, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                super.onSuccess(statusCode, headers, response);
//                Gson gson = new GsonBuilder().create();
//                Type listType = new TypeToken<ArrayList<CheckItem>>() {
//                }.getType();
//                ArrayList<CheckItem> receivedItems = gson.fromJson(response.toString(), listType);
//                if (receivedItems.size() > 0) {
//                    UmbrellaUtil.syncCheckLists(receivedItems);
//                    Log.i("check items", "synced");
//                }
//                checkDone();
//            }
//        });
//
//        UmbrellaRestClient.get("categories", null, null, SettingsActivity.this, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                super.onSuccess(statusCode, headers, response);
//                Gson gson = new GsonBuilder().create();
//                Type listType = new TypeToken<ArrayList<Category>>() {
//                }.getType();
//                ArrayList<Category> receivedItems = gson.fromJson(response.toString(), listType);
//                if (receivedItems.size() > 0) {
//                    UmbrellaUtil.syncCategories(receivedItems);
//                    Log.i("categories", "synced");
//                }
//                checkDone();
//            }
//        });
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
