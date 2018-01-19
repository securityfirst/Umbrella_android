package org.secfirst.umbrella;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.UmbrellaUtil;

public class ServiceReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {

    }

    public void callRefresh(Context context) {
        UmbrellaUtil.getFeeds(context);
    }

    public void callLogout(Context context) {
        Global.INSTANCE.logout(context, false);
    }
}