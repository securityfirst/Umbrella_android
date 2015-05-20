package org.secfirst.umbrella;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

public class RefreshService extends Service
{
    ServiceReceiver receiver = new ServiceReceiver();
    IBinder mBinder = new LocalBinder();
    final Handler handler = new Handler();
    private static TimerTask refreshFeed, logoutTask;
    Timer feedTimer, logoutTimer;
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        feedTimer = new Timer();
//        logoutTimer = new Timer();
        refreshFeed = setFeedRefreshTask();
//        setLogoutTimerTask();
//        logoutTimer.schedule(logoutTask, TimeUnit.MINUTES.toMillis(30), TimeUnit.MINUTES.toMillis(30));
        int refreshFeedValue = 0;
        if (intent!=null) refreshFeedValue = intent.getIntExtra("refresh_feed", 0);
        if (refreshFeedValue>0) {
            feedTimer.schedule(refreshFeed, 1800000, 1800000);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public RefreshService getServerInstance() {
            return RefreshService.this;
        }
    }

    public TimerTask setFeedRefreshTask() {
        return new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        receiver.callRefresh(RefreshService.this);
                    }
                });
            }
        };
    }

    public void setLogoutTimerTask() {
        logoutTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        receiver.callLogout(RefreshService.this);
                    }
                });
            }
        };
    }

    public void resetLogoutTimer() {
        if (logoutTask!=null) {
            logoutTask.cancel();
            setLogoutTimerTask();
            logoutTimer.schedule(logoutTask, 1800000);
        }
    }

    public void setRefresh(int interval) {
        refreshFeed.cancel();
        feedTimer.cancel();
        if (interval>0) {
            feedTimer = new Timer();
            refreshFeed = setFeedRefreshTask();
            feedTimer.schedule(refreshFeed, interval, interval);
        }
    }
}