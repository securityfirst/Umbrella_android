package org.secfirst.umbrella.rss;

import android.os.AsyncTask;

import com.einmalfel.earl.EarlParser;
import com.einmalfel.earl.Feed;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.DataFormatException;

/**
 * Created by dougl on 21/01/2018.
 */

public class RSSFeedService extends AsyncTask<String, Void, Feed> {

    private RSSEvent mRssEvent;

    @Override
    protected Feed doInBackground(String... urls) {
        InputStream inputStream;
        Feed feed = null;
        try {
            inputStream = new URL(urls[0]).openConnection().getInputStream();
            feed = EarlParser.parseOrThrow(inputStream, 0);
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
        return feed;
    }


    @Override
    protected void onPostExecute(Feed feed) {
        super.onPostExecute(feed);
        mRssEvent.onTaskCompleted(feed);
    }


    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        mRssEvent.onTaskInProgress();
    }

    public interface RSSEvent {

        void onTaskInProgress();

        void onTaskCompleted(Feed feed);

        void onError();
    }

    public void onFinish(RSSEvent rssEvent) {
        this.mRssEvent = rssEvent;
    }
}
