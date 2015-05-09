package org.secfirst.umbrella;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import org.secfirst.umbrella.util.UmbrellaUtil;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class ServiceReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {

    }

    public void callRefresh(Context context) {
        Toast.makeText(context, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK).toString(), Toast.LENGTH_SHORT).show();
        UmbrellaUtil.getFeeds(context);
    }

    public void callLogout(Context context) {
        Intent i = new Intent(context, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}