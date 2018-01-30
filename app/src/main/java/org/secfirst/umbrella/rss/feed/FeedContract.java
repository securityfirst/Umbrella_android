package org.secfirst.umbrella.rss.feed;

import org.secfirst.umbrella.BasePresenter;
import org.secfirst.umbrella.BaseView;

import java.util.List;

/**
 * Created by dougl on 18/01/2018.
 */

public interface FeedContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator();

        void finishLoadFeed(CustomFeed customFeed);

        void errorLoadFeed();

        void errorSaveFeed();

        void showFeeds(List<CustomFeed> feedLinks);

        void errorDeleteFeed();
    }

    interface Presenter extends BasePresenter {

        void removeFeed(CustomFeed customFeed);

        void loadFeed(String url);

        void saveFeed(CustomFeed customFeed);
    }
}
