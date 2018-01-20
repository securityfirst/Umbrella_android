package org.secfirst.umbrella.rss.feed;

import org.secfirst.umbrella.rss.api.Channel;

/**
 * Created by dougl on 18/01/2018.
 */

public class ChannelPresenter implements ChannelContract.Presenter {

    private ChannelContract.View mViewRss;

    public ChannelPresenter() {
        mViewRss.setPresenter(this);
    }

    @Override
    public void start() {
        loadFeed();
    }

    @Override
    public void addFeed(Channel channel) {

    }

    @Override
    public void removeFeed(Channel channel) {

    }

    @Override
    public void loadFeed() {

    }


}
