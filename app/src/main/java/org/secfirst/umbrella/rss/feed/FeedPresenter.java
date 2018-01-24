package org.secfirst.umbrella.rss.feed;

import com.einmalfel.earl.Feed;
import com.einmalfel.earl.Item;

import org.secfirst.umbrella.rss.RSSFeedService;


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

    }

    @Override
    public void addFeed(Feed feed) {

    }

    @Override
    public void removeFeed(Feed feed) {

    }

    @Override
    public void loadFeed(String url) {
        RSSFeedService rssFeedService = new RSSFeedService();
        rssFeedService.onFinish(new RSSFeedService.RSSEvent() {
            @Override
            public void onTaskInProgress() {
                mViewRss.setLoadingIndicator();
            }

            @Override
            public void onTaskCompleted(Feed feed) {
                mViewRss.finishLoadFeed(cleanMalformedArticles(feed));

            }

            @Override
            public void onError() {
                mViewRss.errorLoadFeed();
            }
        });
        rssFeedService.execute(url);
    }

    private Feed cleanMalformedArticles(Feed feed) {
        for (Item item : feed.getItems()) {
            if (item.getTitle() == null || item.getDescription() == null) {
                feed.getItems().remove(item);
            } else if (item.getTitle().equalsIgnoreCase("")
                    || item.getDescription().equalsIgnoreCase("")) {
                feed.getItems().remove(item);
            }
        }
        return feed;
    }
}
