package org.secfirst.umbrella.rss.api;

import android.os.AsyncTask;

import java.util.Observable;
import java.util.Observer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dougl on 15/01/2018.
 */

public class RSSFeedService extends AsyncTask<String, Void, String> implements Observer {
    private RSSFeedParser rssFeedParser;
    private RSSEvent rssEvent;

    public RSSFeedService() {
        rssFeedParser = new RSSFeedParser();
        rssFeedParser.addObserver(this);
    }

    @Override
    protected String doInBackground(String... urls) {
        Response response;
        OkHttpClient client = new OkHttpClient();
        try {
            Request request = new Request.Builder()
                    .url(urls[0])
                    .build();
            response = client.newCall(request).execute();
            if (response.isSuccessful())
                return response.body().string();
            else
                rssEvent.onError();

        } catch (Exception e) {
            e.printStackTrace();
            rssEvent.onError();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            try {
                rssFeedParser.channelParse(result);
            } catch (Exception e) {
                e.printStackTrace();
                rssEvent.onError();
            }
        } else
            rssEvent.onError();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void update(Observable observable, Object data) {
        Channel channel = (Channel) data;
        rssEvent.onTaskCompleted(channel);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        rssEvent.onTaskInProgress();
    }

    public interface RSSEvent {

        void onTaskInProgress();

        void onTaskCompleted(Channel channel);

        void onError();
    }

    public void onFinish(RSSEvent onComplete) {
        this.rssEvent = onComplete;
    }
}
