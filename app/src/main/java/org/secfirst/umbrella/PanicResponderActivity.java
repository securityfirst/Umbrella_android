package org.secfirst.umbrella;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import org.secfirst.umbrella.util.Global;

public class PanicResponderActivity extends Activity {

    public static final String PANIC_TRIGGER_ACTION = "info.guardianproject.panic.action.TRIGGER";

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null && PANIC_TRIGGER_ACTION.equals(intent.getAction())) {
            Global global = (Global) getApplicationContext();
            global.logout(this, false);
            ExitActivity.exitAndRemoveFromRecentApps(this);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            finishAndRemoveTask();
        } else {
            finish();
        }
    }
}
