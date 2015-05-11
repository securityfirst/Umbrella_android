package org.secfirst.umbrella;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import org.secfirst.umbrella.util.Global;

public abstract class BaseActivity extends ActionBarActivity {

    protected Global global;
    protected boolean mBounded;
    protected RefreshService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        global = (Global) getApplicationContext();
        setContentView(getLayoutResource());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(R.string.app_name);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    protected abstract int getLayoutResource();

    @Override
    protected void onStart() {
        super.onStart();
        Intent mIntent = new Intent(this, RefreshService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (global == null) global = (Global) getApplicationContext();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if(mBounded) mService.resetLogoutTimer();
    }

    public Global getGlobal() {
        return this.global;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            mBounded = false;
            mService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            mBounded = true;
            RefreshService.LocalBinder mLocalBinder = (RefreshService.LocalBinder)service;
            mService = mLocalBinder.getServerInstance();
        }
    };
}