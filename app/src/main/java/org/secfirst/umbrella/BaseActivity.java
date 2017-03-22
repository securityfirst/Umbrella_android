package org.secfirst.umbrella;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.seismic.ShakeDetector;

import org.secfirst.umbrella.models.FeedItem;
import org.secfirst.umbrella.models.Registry;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.NotificationUtil;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import timber.log.Timber;

public abstract class BaseActivity extends AppCompatActivity implements ShakeDetector.Listener {

    public static final String NOTIFICATION_EVENT = "org.secfirst.umbrella.notification";
    public static final String EXTRA_FEEDS = "feeds";

    public static final Intent getNotificationIntent(List<FeedItem> extraFeeds) {
        Intent intent = new Intent(NOTIFICATION_EVENT);
        intent.putExtra(EXTRA_FEEDS, new Gson().toJson(extraFeeds));
        return intent;
    }

    protected Global global;
    protected Toolbar toolbar;
    public boolean mBounded;
    public RefreshService mService;
    private static TimerTask logoutTask;
    private Timer logoutTimer;
    final Handler handler = new Handler();
    ShakeDetector sd;
    private MaterialDialog materialDialog;

    private BroadcastReceiver mForegroundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String json = intent.getStringExtra(EXTRA_FEEDS);
            List<FeedItem> feeds = new Gson().fromJson(json, new TypeToken<List<FeedItem>>(){}.getType());
            NotificationUtil.showNotificationForeground(context, global, feeds);
            abortBroadcast();
        }
    };
    private IntentFilter mFilter = new IntentFilter(NOTIFICATION_EVENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        global = (Global) getApplicationContext();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        Registry language = global.getRegistry("language");
        if (language!=null && !language.getValue().equals("")) {
            setLocale(language.getValue());
        } else {
            setScreen();
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
    }

    private void setScreen() {
        setContentView(getLayoutResource());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        registerReceiver(mForegroundReceiver, mFilter);
        Intent mIntent = new Intent(this, RefreshService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (global == null) global = (Global) getApplicationContext();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sd==null) sd = new ShakeDetector(BaseActivity.this);
                sd.start((SensorManager) getSystemService(SENSOR_SERVICE));
            }
        }, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sd!=null) sd.stop();
        if (materialDialog !=null) materialDialog.dismiss();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        resetLogoutTimer();
    }

    public Global getGlobal() {
        return this.global;
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(mForegroundReceiver);
        } catch (IllegalArgumentException e) {
            Timber.e(e);
        }
        if(mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
    };

    private void setLogoutTimerTask() {
        logoutTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        global.logout(BaseActivity.this, false);
                    }
                });
            }
        };
    }

    private void resetLogoutTimer() {
        if (logoutTask!=null) {
            logoutTask.cancel();
            setLogoutTimerTask();
            logoutTimer.schedule(logoutTask, 1800000);
        }
    }

    public void setLocale(String languageToLoad) {
        Configuration config = new Configuration();
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(locale);
        } else {
            config.locale = locale;
        }
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        setScreen();
    }

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

    @Override
    public void hearShake() {
        if (materialDialog!=null) materialDialog.dismiss();
        materialDialog = new MaterialDialog.Builder(this)
                .title(R.string.masking_mode_title)
                .content(getString(R.string.masking_mode_body, getString(R.string.app_calc)))
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        UmbrellaUtil.setMaskMode(BaseActivity.this, true);
                        if (global.hasPasswordSet(false)) global.logout(BaseActivity.this, false);
                        Intent i = new Intent(BaseActivity.this, CalcActivity.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                        finish();
                    }
                })
                .show();
    }
}