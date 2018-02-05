package org.secfirst.umbrella.rss;

import android.os.AsyncTask;
import android.util.Log;

import com.einmalfel.earl.EarlParser;
import com.einmalfel.earl.Feed;

import org.secfirst.umbrella.rss.feed.CustomFeed;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dougl on 21/01/2018.
 */

public class RSSFeedService extends AsyncTask<String, Void, List<CustomFeed>> {

    private RSSEvent mRssEvent;

    @Override
    protected List<CustomFeed> doInBackground(String... urls) {
        InputStream inputStream;
        List<CustomFeed> customFeeds = new ArrayList<>();
        Feed feed;
        try {
            for (String url : urls) {
                CustomFeed customFeed = new CustomFeed();
                inputStream = new URL(url).openConnection().getInputStream();
                feed = EarlParser.parseOrThrow(inputStream, 0);
                customFeed.setFeedUrl(url);
                customFeed.setDetail(feed.getDescription());
                customFeed.setTitle(feed.getTitle());
                customFeed.setFeed(feed);
                customFeeds.add(customFeed);
            }
        } catch (Exception e) {
            mRssEvent.onError();
            cancel(true);
        }
        return customFeeds;
    }


    @Override
    protected void onPostExecute(List<CustomFeed> customFeeds) {
        super.onPostExecute(customFeeds);
        mRssEvent.onTaskCompleted(customFeeds);
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        mRssEvent.onTaskInProgress();
        Log.e("test", "progresss");
    }

    public interface RSSEvent {

        void onTaskInProgress();

        void onTaskCompleted(List<CustomFeed> customFeeds);

        void onError();
    }

    public void onFinish(RSSEvent rssEvent) {
        this.mRssEvent = rssEvent;
    }
}
