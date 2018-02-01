package org.secfirst.umbrella.models;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;

import java.util.List;

/**
 * Created by dougl on 31/01/2018.
 */
public class RSS {

    private Feed feed;
    public static String DEFAULT_RSS = "rss";
    public static String FILE_NAME = "default_rss_feed.json";

    public static int DEFAULT_RSS_AVAILABLE = 1;

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public class Feed {

        private List<Item> items;

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }
    }

    public class Item {

        private String link;

        public void setLink(String link) {
            this.link = link;
        }

        public String getLink() {
            return link;
        }
    }

    public static boolean isLoadedDefault(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences("org.secfirst.umbrella", Application.MODE_PRIVATE);
        boolean value = sharedPref.getBoolean(DEFAULT_RSS, false);
        if (!value) {
            storeDefaultStatus(activity);
            return false;
        }

        return true;
    }

    private static void storeDefaultStatus(Activity activity) {
        SharedPreferences sharedPref = activity.getSharedPreferences("org.secfirst.umbrella", Application.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(DEFAULT_RSS, true);
        editor.apply();
    }
}
