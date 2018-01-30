package org.secfirst.umbrella;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.secfirst.umbrella.models.FeedItem;
import org.secfirst.umbrella.util.NotificationUtil;

import java.util.List;

public class BackgroundReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String json = intent.getStringExtra(BaseActivity.EXTRA_FEEDS);
        List<FeedItem> feeds = new Gson().fromJson(json, new TypeToken<List<FeedItem>>(){}.getType());
        NotificationUtil.showNotificationBackground(context, feeds);
  }
}
