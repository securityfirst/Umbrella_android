package org.secfirst.umbrella;

import com.einmalfel.earl.EarlParser;
import com.einmalfel.earl.Feed;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.DataFormatException;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by dougl on 21/01/2018.
 */
@RunWith(JUnit4.class)
public class RSSFeedParserTest {

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

    @Test
    public void fileObjectShouldNotBeNull() throws Exception {
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("rss_parse/" + "rss_bbc.xml");
        assertThat(convertStreamToString(inputStream), notNullValue());
    }

    @Test
    public void getAllArticlesFromBBCFeed() throws IOException, XmlPullParserException, DataFormatException {
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("rss_parse/" + "rss_bbc.xml");
        Feed feed = EarlParser.parseOrThrow(inputStream, 0);
        assertEquals(49, feed.getItems().size());
    }

    @Test(expected = XmlPullParserException.class)
    public void tryToReadMalformedRss() throws IOException, XmlPullParserException, DataFormatException {
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("rss_parse/" + "malformated_rss.xml");
        EarlParser.parseOrThrow(inputStream, 0);
    }

    @Test
    public void getInformationAboutChannelInXmlV1() throws IOException, XmlPullParserException, DataFormatException {
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("rss_parse/" + "rss_bbc.xml");
        Feed feed = EarlParser.parseOrThrow(inputStream, 0);
        assertEquals(feed.getTitle(), "BBC News - Business");
        assertEquals(feed.getDescription(), "BBC News - Business");
        assertEquals(feed.getImageLink(), "http://news.bbcimg.co.uk/nol/shared/img/bbc_news_120x60.gif");
    }

    @Test
    public void getInformationAboutChannelInXmlV2() throws IOException, XmlPullParserException, DataFormatException {
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("rss_parse/" + "cbn_rss.xml");
        Feed feed = EarlParser.parseOrThrow(inputStream, 0);
        assertEquals(feed.getTitle(), "CBNNews.com");
        assertEquals(feed.getDescription(), "CBNNews.com Feed");
    }
}
