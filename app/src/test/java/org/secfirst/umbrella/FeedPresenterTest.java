package org.secfirst.umbrella;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.secfirst.umbrella.rss.RSSFeedService;
import org.secfirst.umbrella.rss.feed.CustomFeed;
import org.secfirst.umbrella.rss.feed.FeedContract;
import org.secfirst.umbrella.rss.feed.FeedPresenter;

import static org.mockito.Mockito.verify;

/**
 * Created by dougl on 24/01/2018.
 */

public class FeedPresenterTest {
    @Mock
    private FeedContract.View mFeedView;
    private FeedPresenter mPresenter;

    @Mock
    private RSSFeedService mRssFeedService;

    @Captor
    private ArgumentCaptor<RSSFeedService.RSSEvent> mFeedServiceCallbackCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mPresenter = new FeedPresenter(mFeedView);
        mRssFeedService = new RSSFeedService();
    }

    @Test
    public void setThePresenterToView() {
        mPresenter = new FeedPresenter(mFeedView);
        verify(mFeedView).setPresenter(mPresenter);
    }

    @Test(expected = Exception.class)
    public void tryToRemoveAnInvalidFeed() {
        mPresenter.removeFeed(new CustomFeed());
    }

    @Test(expected = Exception.class)
    public void tryToSaveAnInvalidFeed() {
        mPresenter.saveFeed(null);
    }
}
