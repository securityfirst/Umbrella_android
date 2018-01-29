package org.secfirst.umbrella.rss;

import android.os.AsyncTask;

import com.einmalfel.earl.EarlParser;
import com.einmalfel.earl.Feed;

import org.secfirst.umbrella.rss.feed.CustomFeed;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.DataFormatException;

/**
 * Created by dougl on 21/01/2018.
 */

public class RSSFeedService extends AsyncTask<String, Void, CustomFeed> {

    private RSSEvent mRssEvent;

    @Override
    protected CustomFeed doInBackground(String... urls) {
        InputStream inputStream;
        CustomFeed customFeed = new CustomFeed();
        Feed feed;

        try {

            inputStream = new URL(urls[0]).openConnection().getInputStream();
            feed = EarlParser.parseOrThrow(inputStream, 0);

            customFeed.setFeedUrl(urls[0]);
            customFeed.setDetail(feed.getDescription());
            customFeed.setTitle(feed.getTitle());
            customFeed.setFeed(feed);

        } catch (IOException e) {
            mRssEvent.onError();
            cancel(true);
        } catch (XmlPullParserException e) {
            mRssEvent.onError();
            cancel(true);
        } catch (DataFormatException e) {
            mRssEvent.onError();
            cancel(true);
        }
        return customFeed;
    }


    @Override
    protected void onPostExecute(CustomFeed customFeed) {
        super.onPostExecute(customFeed);
        mRssEvent.onTaskCompleted(customFeed);
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        mRssEvent.onTaskInProgress();
    }

    public interface RSSEvent {

        void onTaskInProgress();

        void onTaskCompleted(CustomFeed customFeed);

        void onError();
    }

    public void onFinish(RSSEvent rssEvent) {
        this.mRssEvent = rssEvent;
    }
}
