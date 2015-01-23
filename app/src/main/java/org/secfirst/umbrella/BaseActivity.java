package org.secfirst.umbrella;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import org.secfirst.umbrella.util.Global;

public abstract class BaseActivity extends ActionBarActivity {

    protected Global global;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        global = (Global) getApplicationContext();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (global == null) global = (Global) getApplicationContext();
    }

    public Global getGlobal() {
        return this.global;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}