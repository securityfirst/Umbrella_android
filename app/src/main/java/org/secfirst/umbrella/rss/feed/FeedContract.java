package org.secfirst.umbrella.rss.feed;

import com.einmalfel.earl.Feed;

import org.secfirst.umbrella.BasePresenter;
import org.secfirst.umbrella.BaseView;

/**
 * Created by dougl on 18/01/2018.
 */

public interface FeedContract {

    interface View extends BaseView<Presenter> {

        void loadInProgressFeed();

        void finishLoadFeed(Feed feed);

        void errorLoadFeed();
    }

    interface Presenter extends BasePresenter {

        void addFeed(Feed feed);

        void removeFeed(Feed feed);

        void loadFeed(String url);
    }
}
