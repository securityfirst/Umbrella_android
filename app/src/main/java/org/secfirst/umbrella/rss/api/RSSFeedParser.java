package org.secfirst.umbrella.rss.api;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;

/**
 * Created by dougl on 15/01/2018.
 */

public class RSSFeedParser extends Observable {

    private ArrayList<Article> mArticles;
    private Article mCurrentArticle;
    private Channel mChannel;

    public RSSFeedParser() {
        mArticles = new ArrayList<>();
        mCurrentArticle = new Article();
    }


    public void channelParse(String xml) throws XmlPullParserException, IOException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

        factory.setNamespaceAware(false);
        XmlPullParser xmlPullParser = factory.newPullParser();

        xmlPullParser.setInput(new StringReader(xml));
        boolean insideItem = false;
        boolean lastItem = false;
        int eventType = xmlPullParser.getEventType();
        Channel channel = new Channel();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {

                XMLNode xmlNode = XMLNode.getEnum(xmlPullParser.getName());

                if (xmlNode != null && (!lastItem)) {
                    switch (xmlNode) {
                        case IMAGE:
                            insideItem = true;
                            break;
                        case TITLE:
                            String title = safeNextText(xmlPullParser);
                            channel.setTitle(title);
                            Log.e(RSSFeedParser.class.getName(), "Channel title - " + title);
                            break;
                        case DESCRIPTION:
                            String description = safeNextText(xmlPullParser);
                            channel.setDescription(description);
                            Log.e(RSSFeedParser.class.getName(), "Channel description- " + description);
                        case URL:
                            if (insideItem) {
                                String image = safeNextText(xmlPullParser);
                                channel.setImage(image);
                                lastItem = true;
                                Log.e(RSSFeedParser.class.getName(), "Channel Image - " + image);
                            }
                            break;
                        default:
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG && xmlPullParser.getName().equalsIgnoreCase("item")) {
                insideItem = false;
                mChannel = channel;
            }
            eventType = xmlPullParser.next();
        }

        articleParse(xml);
    }

    private void articleParse(String xml) throws XmlPullParserException, IOException {

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

        factory.setNamespaceAware(false);
        XmlPullParser xmlPullParser = factory.newPullParser();

        xmlPullParser.setInput(new StringReader(xml));
        boolean insideItem = false;
        int eventType = xmlPullParser.getEventType();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {

                XMLNode xmlNode = XMLNode.getEnum(xmlPullParser.getName());

                if (xmlNode != null) {
                    switch (xmlNode) {
                        case ITEM:
                            insideItem = true;
                            break;
                        case TITLE:
                            if (insideItem) {
                                String title = xmlPullParser.nextText();
                                mCurrentArticle.setTitle(title);
                                Log.e(RSSFeedParser.class.getName(), "TITLE - " + title);
                            }
                            break;
                        case CREATOR:
                            if (insideItem) {
                                String author = xmlPullParser.nextText();
                                mCurrentArticle.setAuthor(author);
                                Log.e(RSSFeedParser.class.getName(), "CREATOR - " + author);
                            }
                            break;
                        case CATEGORY:
                            if (insideItem) {
                                String category = xmlPullParser.nextText();
                                mCurrentArticle.addCategory(category);
                                Log.e(RSSFeedParser.class.getName(), "CATEGORY - " + category);
                            }
                            break;
                        case ENCODED:
                            if (insideItem) {
                                String htmlData = xmlPullParser.nextText();
                                Document doc = Jsoup.parse(htmlData);
                                try {
                                    //choose the first image found in the article
                                    String pic = doc.select("img").first().attr("abs:src");
                                    mCurrentArticle.setImage(pic);
                                    Log.e(RSSFeedParser.class.getName(), "" + pic);
                                } catch (NullPointerException e) {
                                    mCurrentArticle.setImage(null);
                                }
                                mCurrentArticle.setContent(htmlData);
                                Log.e(RSSFeedParser.class.getName(), "ENCODED - " + htmlData);
                            }
                            break;
                        case DESCRIPTION:
                            if (insideItem) {
                                String description = xmlPullParser.nextText();
                                mCurrentArticle.setDescription(description);
                                Log.e(RSSFeedParser.class.getName(), "DESCRIPTION - " + description);
                            }
                            break;
                        case DATE:
                            @SuppressWarnings("deprecation")
                            Date pubDate = new Date(xmlPullParser.nextText());
                            mCurrentArticle.setPubDate(pubDate);
                            Log.e(RSSFeedParser.class.getName(), "DATE -" + pubDate);
                            break;
                        case END_TAG:
                            if (xmlPullParser.getName().equalsIgnoreCase("item")) {
                                insideItem = false;
                                mArticles.add(mCurrentArticle);
                                mCurrentArticle = new Article();
                            }
                            break;
                        default:
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG && xmlPullParser.getName().equalsIgnoreCase("item")) {
                insideItem = false;
                Log.e("test", "----------------------------------------------------------------");
                mArticles.add(mCurrentArticle);
                mCurrentArticle = new Article();
            }

            eventType = xmlPullParser.next();
        }
        mChannel.setArticles(mArticles);
        triggerObserver();
    }

    private String safeNextText(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        String result = parser.nextText();
        if (parser.getEventType() != XmlPullParser.END_TAG) {
            parser.nextTag();
        }
        return result;
    }

    private void triggerObserver() {
        setChanged();
        notifyObservers(mChannel);
    }
}
