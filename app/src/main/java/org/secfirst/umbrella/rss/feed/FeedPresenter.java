package org.secfirst.umbrella.rss.feed;

import com.einmalfel.earl.Feed;

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
                mViewRss.loadInProgressFeed();
            }

            @Override
            public void onTaskCompleted(Feed channel) {
                mViewRss.finishLoadFeed(channel);
            }

            @Override
            public void onError() {
                mViewRss.errorLoadFeed();
            }
        });
        rssFeedService.execute(url);
    }
}
