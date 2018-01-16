package org.secfirst.umbrella.rss;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by dougl on 15/01/2018.
 */

public class RSSFeedService extends AsyncTask<String, Void, String> implements Observer {
    private RSSFeedParser RSSFeedParser;
    private RSSEvent rssEvent;

    public RSSFeedService() {
        RSSFeedParser = new RSSFeedParser();
        RSSFeedParser.addObserver(this);
    }

    @Override
    protected String doInBackground(String... urls) {
        Response response;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urls[0])
                .build();

        try {
            response = client.newCall(request).execute();
            if (response.isSuccessful())
                return response.body().string();
            else
                rssEvent.onError();

        } catch (IOException e) {
            e.printStackTrace();
            rssEvent.onError();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            try {
                RSSFeedParser.test(result);
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
        ArrayList<Article> articles = (ArrayList<Article>) data;
        rssEvent.onTaskCompleted(articles);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        rssEvent.onTaskInProgress();
    }

    public interface RSSEvent {

        void onTaskInProgress();

        void onTaskCompleted(ArrayList<Article> list);

        void onError();
    }

    public void onFinish(RSSEvent onComplete) {
        this.rssEvent = onComplete;
    }
}
