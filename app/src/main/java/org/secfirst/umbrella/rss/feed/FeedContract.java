package org.secfirst.umbrella.rss.feed;

import org.secfirst.umbrella.BasePresenter;
import org.secfirst.umbrella.BaseView;

import java.io.InputStream;
import java.util.List;

/**
 * Created by dougl on 18/01/2018.
 */

public interface FeedContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator();

        void finishLoadFeed(List<CustomFeed> customFeeds);

        void errorLoadFeed();

        void errorSaveFeed();

        void showFeeds(List<CustomFeed> feedLinks);

        void errorDeleteFeed();
    }

    interface Presenter extends BasePresenter {

        void removeFeed(CustomFeed customFeed);

        void loadFeed(String... urls);

        void saveFeed(List<CustomFeed> customFeeds);

        void saveFeed(CustomFeed customFeed);

        List<CustomFeed> getFeeds();

        String[] getDefaultFeedUrl(InputStream inputStream);
    }
}
