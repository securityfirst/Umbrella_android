package org.secfirst.umbrella.rss.feed;

import org.secfirst.umbrella.BasePresenter;
import org.secfirst.umbrella.BaseView;
import org.secfirst.umbrella.rss.api.Channel;

/**
 * Created by dougl on 18/01/2018.
 */

public interface ChannelContract {

    interface View extends BaseView<Presenter> {

        void loadInProgressFeed();

        void finishLoadFeed();
    }

    interface Presenter extends BasePresenter {

        void addFeed(Channel channel);

        void removeFeed(Channel channel);

        void loadFeed();
    }
}
