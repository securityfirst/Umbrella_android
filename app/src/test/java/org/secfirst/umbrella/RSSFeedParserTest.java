package org.secfirst.umbrella;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.shadows.ShadowLooper;
import org.secfirst.umbrella.rss.services.RSSFeedService;
import org.secfirst.umbrella.rss.entities.CustomFeed;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.DataFormatException;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by dougl on 21/01/2018.
 */
@RunWith(RobolectricTestRunner.class)
public class RSSFeedParserTest {

    private RSSFeedService rssFeedService;

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    @Before
    public void setUp() throws Exception {
        rssFeedService = new RSSFeedService();
        ShadowApplication.runBackgroundTasks();
        Robolectric.flushBackgroundThreadScheduler();
        ShadowLooper.runUiThreadTasks();
    }

    @Test
    public void fileObjectShouldNotBeNull() throws Exception {
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("rss_parse/" + "rss_bbc.xml");
        assertThat(convertStreamToString(inputStream), notNullValue());
    }

    @Test
    public void getArticlesFromBBCFeed() throws IOException, XmlPullParserException, DataFormatException {
        String urlString = "http://feeds.bbci.co.uk/news/business/rss.xml";
        rssFeedService.onFinish(new RSSFeedService.RSSEvent() {
            @Override
            public void onTaskInProgress() {
            }

            @Override
            public void onTaskCompleted(List<CustomFeed> customFeed) {
                assertNotNull(customFeed.get(0).getFeed().getItems().get(0).getTitle());
                assertNotNull(customFeed.get(0).getFeed().getItems().get(0).getDescription());
            }

            @Override
            public void onError() {
                Assert.assertTrue(false);
            }
        });
        rssFeedService.execute(urlString);
    }

    @Test
    public void getInformationAboutChannelInXmlV1() throws IOException, XmlPullParserException, DataFormatException {
        String url = "http://deadline.com/feed/";
        rssFeedService.onFinish(new RSSFeedService.RSSEvent() {
            @Override
            public void onTaskInProgress() {
            }

            @Override
            public void onTaskCompleted(List<CustomFeed> customFeed) {
                assertNotNull(customFeed.get(0).getFeed().getTitle());
                assertNotNull(customFeed.get(0).getFeed().getDescription());
            }

            @Override
            public void onError() {
                Assert.assertTrue(false);
            }
        });
        rssFeedService.execute(url);
    }

    @Test
    public void getInformationAboutChannelInXmlV2() throws IOException, XmlPullParserException, DataFormatException {

        String url = "http://entertainmentweekly.tumblr.com/rss";
        rssFeedService.onFinish(new RSSFeedService.RSSEvent() {
            @Override
            public void onTaskInProgress() {
            }

            @Override
            public void onTaskCompleted(List<CustomFeed> customFeed) {
                assertNotNull(customFeed.get(0).getFeed().getTitle());
                assertNotNull(customFeed.get(0).getFeed().getDescription());
            }

            @Override
            public void onError() {
                Assert.assertTrue(false);
            }
        });
        rssFeedService.execute(url);
    }
}
