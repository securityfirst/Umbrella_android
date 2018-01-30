package org.secfirst.umbrella.rss.feed;

import com.einmalfel.earl.Item;

import org.secfirst.umbrella.rss.RSSFeedService;
import org.secfirst.umbrella.util.Global;

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
    public void loadFeed(final String url) {
        RSSFeedService rssFeedService = new RSSFeedService();
        rssFeedService.onFinish(new RSSFeedService.RSSEvent() {
            @Override
            public void onTaskInProgress() {
                mViewRss.setLoadingIndicator();
            }

            @Override
            public void onTaskCompleted(CustomFeed customFeed) {
                mViewRss.finishLoadFeed(cleanMalformedArticles(customFeed));
                saveFeed(customFeed);
            }

            @Override
            public void onError() {
                mViewRss.errorLoadFeed();
            }
        });
        rssFeedService.execute(url);
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

    private CustomFeed cleanMalformedArticles(CustomFeed customFeed) {
        for (Item item : customFeed.getFeed().getItems()) {
            if (item.getTitle() == null || item.getDescription() == null) {
                customFeed.getFeed().getItems().remove(item);
            } else if (item.getTitle().equalsIgnoreCase("")
                    || item.getDescription().equalsIgnoreCase("")) {
                customFeed.getFeed().getItems().remove(item);
            }
        }
        return customFeed;
    }
}
