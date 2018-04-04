package org.secfirst.umbrella;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.seismic.ShakeDetector;

import org.secfirst.umbrella.fragments.HandsShakeDialog;
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
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends AppCompatActivity implements ShakeDetector.Listener {

    public static final String NOTIFICATION_EVENT = "org.secfirst.umbrella.notification";
    public static final String EXTRA_FEEDS = "feeds";

    public static final Intent getNotificationIntent(List<FeedItem> extraFeeds) {
        Intent intent = new Intent(NOTIFICATION_EVENT);
        intent.putExtra(EXTRA_FEEDS, new Gson().toJson(extraFeeds));
        return intent;
    }

    protected Toolbar toolbar;
    public boolean mBounded;
    public RefreshService mService;
    private static TimerTask logoutTask;
    private Timer logoutTimer;
    final Handler handler = new Handler();
    ShakeDetector sd;

    private BroadcastReceiver mForegroundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String json = intent.getStringExtra(EXTRA_FEEDS);
            List<FeedItem> feeds = new Gson().fromJson(json, new TypeToken<List<FeedItem>>() {
            }.getType());
            NotificationUtil.showNotificationForeground(context, feeds);
            abortBroadcast();
        }
    };
    private IntentFilter mFilter = new IntentFilter(NOTIFICATION_EVENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableScreenShot();
        Registry language = Global.INSTANCE.getRegistry("language");
        if (language != null && !language.getValue().equals("")) {
            setLocale(language.getValue());
        } else {
            CharSequence[] languageList = UmbrellaUtil.getLanguageEntryValues();
            if (languageList.length > 0) {
                setLocale((String) languageList[0]);
            }
        }
        setScreen();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mFilter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
    }

    private void enableScreenShot() {
        if (!BuildConfig.DEBUG) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
    }


    @Override
    protected void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sd == null) sd = new ShakeDetector(BaseActivity.this);
                sd.start((SensorManager) getSystemService(SENSOR_SERVICE));
            }
        }, 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sd != null) sd.stop();
    }

    private static final int STORAGE_PERMISSION_RC = 69;
    private int chooserDialog;

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_RC) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                handler.postDelayed(() -> findViewById(chooserDialog).performClick(), 1000);
            } else {
                Toast.makeText(
                        this,
                        "The folder or file chooser will not work without "
                                + "permission to read external storage.",
                        Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        resetLogoutTimer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            unregisterReceiver(mForegroundReceiver);
        } catch (IllegalArgumentException e) {
            Timber.e(e);
        }
        if (mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
    }

    ;

    private void setLogoutTimerTask() {
        logoutTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        Global.INSTANCE.logout(BaseActivity.this, false);
                    }
                });
            }
        };
    }

    private void resetLogoutTimer() {
        if (logoutTask != null) {
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
            RefreshService.LocalBinder mLocalBinder = (RefreshService.LocalBinder) service;
            mService = mLocalBinder.getServerInstance();
        }
    };

    @Override
    public void hearShake() {
        if (!UmbrellaUtil.isAppMasked(this)) {
            try {
                if (Global.INSTANCE.hasPasswordSet(false)) Global.INSTANCE.logout(this, false);
                FragmentManager fragmentManager = getSupportFragmentManager();
                HandsShakeDialog handsShake = HandsShakeDialog.newInstance();
                handsShake.show(fragmentManager, "");
            } catch (IllegalStateException e) {
                Log.e(BaseActivity.class.getName(), "hearShake - BaseActivity");
            }
        }
    }
}