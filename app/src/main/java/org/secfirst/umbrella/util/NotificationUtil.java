package org.secfirst.umbrella.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import org.secfirst.umbrella.MainActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.models.FeedItem;

import java.util.List;

public class NotificationUtil {

    public static void showNotificationBackground(Context context, List<FeedItem> feeds) {
        showNotification(context, feeds, NotificationCompat.PRIORITY_HIGH);
    }

    public static void showNotificationForeground(Context context, List<FeedItem> feeds) {
        showNotification(context, feeds, NotificationCompat.PRIORITY_LOW);
    }

    private static void showNotification(Context context, List<FeedItem> feeds, int priority) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        String title = feeds.size() == 1 ? feeds.get(0).getTitle() : context.getString(R.string.dashboard);
        String content = feeds.size() == 1 ? feeds.get(0).getBody() : String.format(context.getString(R.string.new_feeds), feeds.size());
        NotificationCompat.Builder b = getBaseNotificationBuilder(context, title, content, pendingIntent);
        b.setPriority(priority);
        if(Global.INSTANCE.getNotificationRingtoneEnabled()) {
            b.setSound(Global.INSTANCE.getNotificationRingtone());
        }
        if(Global.INSTANCE.getNotificationVibrationEnabled()) {
            b.setVibrate(new long[]{0, 500});
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, b.build());
    }

    private static NotificationCompat.Builder getBaseNotificationBuilder(Context context, String title, String text, PendingIntent pendingIntent) {
        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_umbrella)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setLights(ContextCompat.getColor(context, R.color.umbrella_purple), 1000, 500)
                .setColor(ContextCompat.getColor(context, R.color.umbrella_purple))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentIntent(pendingIntent);
    }
}
