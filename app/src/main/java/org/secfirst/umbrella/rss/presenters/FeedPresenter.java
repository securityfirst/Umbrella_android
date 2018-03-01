package org.secfirst.umbrella.rss.presenters;

import com.google.gson.Gson;

import org.secfirst.umbrella.models.RSS;
import org.secfirst.umbrella.rss.entities.CustomFeed;
import org.secfirst.umbrella.rss.services.RSSFeedService;
import org.secfirst.umbrella.util.Global;
import org.secfirst.umbrella.util.UmbrellaUtil;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;


/**
 * Created by dougl on 18/01/2018.
 */

public class FeedPresenter implements FeedContract.Presenter {

    private FeedContract.View mViewRss;

    public FeedPresenter(FeedContract.View view) {
        mViewRss = view;
        mViewRss.setPresenter(this);
    }

    @Override
    public void start() {
        getFeeds();
    }

    @Override
    public void removeFeed(CustomFeed customFeed) {
        try {
            Global.INSTANCE.getDaoRSS().delete(customFeed);
        } catch (SQLException e) {
            mViewRss.errorDeleteFeed();
        }
    }

    @Override
    public void loadFeed(final String... urls) {
        RSSFeedService rssFeedService = new RSSFeedService();
        rssFeedService.onFinish(new RSSFeedService.RSSEvent() {
            @Override
            public void onTaskInProgress() {
                mViewRss.setLoadingIndicator();
            }

            @Override
            public void onTaskCompleted(List<CustomFeed> customFeeds) {
                mViewRss.finishLoadFeed(customFeeds);
            }

            @Override
            public void onError() {
                mViewRss.errorLoadFeed();
            }
        });

        rssFeedService.execute(urls);
    }

    @Override
    public void saveFeed(List<CustomFeed> customFeeds) {
        try {
            for (CustomFeed customFeed : customFeeds) {
                Global.INSTANCE.getDaoRSS().
                        createIfNotExists(customFeed);
            }
        } catch (SQLException e) {
            mViewRss.errorSaveFeed();
        }
    }

    @Override
    public void saveFeed(CustomFeed customFeed) {
        try {
            Global.INSTANCE.getDaoRSS().
                    createIfNotExists(customFeed);
        } catch (SQLException e) {
            mViewRss.errorSaveFeed();
        }
    }

    @Override
    public List<CustomFeed> getFeeds() {
        List<CustomFeed> customFeeds = Global.INSTANCE.getCustomFeed();
        if (!customFeeds.isEmpty())
            mViewRss.showFeeds(customFeeds);
        return customFeeds;
    }

    @Override
    public String[] getDefaultFeedUrl(InputStream inputStream) {
        String test = UmbrellaUtil.inputStreamToString(inputStream);
        RSS rss = new Gson().fromJson(test, RSS.class);
        String[] urls = new String[rss.getFeed().getItems().size()];
        for (int i = 0; i < rss.getFeed().getItems().size(); i++) {
            urls[i] = rss.getFeed().getItems().get(i).getLink();
        }
        return urls;
    }

    @Override
    public String splitFeedLinkToShare(List<CustomFeed> selectedFeeds) {
        StringBuilder string = new StringBuilder();
        for (CustomFeed feed : selectedFeeds) {
            string.append(feed.getFeedUrl());
            string.append(System.getProperty("line.separator"));
        }
        return string.toString();
    }
}
