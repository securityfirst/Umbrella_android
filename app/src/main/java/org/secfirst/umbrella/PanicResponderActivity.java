package org.secfirst.umbrella;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.UmbrellaUtil;

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
            UmbrellaUtil.setMaskMode(PanicResponderActivity.this, true);
            if (global.hasPasswordSet(false)) global.logout(PanicResponderActivity.this, false);
            Intent i = new Intent(PanicResponderActivity.this, CalcActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

        if (Build.VERSION.SDK_INT >= 21) {
            finishAndRemoveTask();
        } else {
            finish();
        }
    }
}
