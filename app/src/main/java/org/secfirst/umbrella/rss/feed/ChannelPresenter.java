package org.secfirst.umbrella.rss.feed;

import org.secfirst.umbrella.rss.api.Channel;
import org.secfirst.umbrella.rss.api.RSSFeedService;

/**
 * Created by dougl on 18/01/2018.
 */

public class ChannelPresenter implements ChannelContract.Presenter {

    private ChannelContract.View mViewRss;

    public ChannelPresenter(ChannelContract.View view) {
        mViewRss = view;
        mViewRss.setPresenter(this);
    }

    @Override
    public void start() {

    }

    @Override
    public void addFeed(Channel channel) {

    }

    @Override
    public void removeFeed(Channel channel) {

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
            public void onTaskCompleted(Channel channel) {
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
