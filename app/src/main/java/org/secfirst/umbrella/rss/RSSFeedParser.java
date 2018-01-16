package org.secfirst.umbrella.rss;

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

    private ArrayList<Article> articles;
    private Article currentArticle;

    public RSSFeedParser() {
        articles = new ArrayList<>();
        currentArticle = new Article();
    }

    public void test(String xml) throws XmlPullParserException, IOException {

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
                                currentArticle.setTitle(title);
                                Log.e(RSSFeedParser.class.getName(), "TITLE - " + title);
                            }
                            break;
                        case LINK:
                            if (insideItem) {
                                String link = xmlPullParser.nextText();
                                currentArticle.setLink(link);
                                Log.e(RSSFeedParser.class.getName(), "LINK - " + link);
                            }
                            break;
                        case CREATOR:
                            if (insideItem) {
                                String author = xmlPullParser.nextText();
                                currentArticle.setAuthor(author);
                                Log.e(RSSFeedParser.class.getName(), "CREATOR - " + author);
                            }
                            break;
                        case CATEGORY:
                            if (insideItem) {
                                String category = xmlPullParser.nextText();
                                currentArticle.addCategory(category);
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
                                    currentArticle.setImage(pic);
                                    Log.e(RSSFeedParser.class.getName(), "" + pic);
                                } catch (NullPointerException e) {
                                    currentArticle.setImage(null);
                                }
                                currentArticle.setContent(htmlData);
                                Log.e(RSSFeedParser.class.getName(), "ENCODED - " + htmlData);
                            }
                            break;
                        case DESCRIPTION:
                            if (insideItem) {
                                String description = xmlPullParser.nextText();
                                currentArticle.setDescription(description);
                                Log.e(RSSFeedParser.class.getName(), "DESCRIPTION - " + description);
                            }
                            break;
                        case DATE:
                            @SuppressWarnings("deprecation")
                            Date pubDate = new Date(xmlPullParser.nextText());
                            currentArticle.setPubDate(pubDate);
                            Log.e(RSSFeedParser.class.getName(), "DATE -" + pubDate);
                            break;
                        case END_TAG:
                            if (xmlPullParser.getName().equalsIgnoreCase("item")) {
                                insideItem = false;
                                articles.add(currentArticle);
                                currentArticle = new Article();
                            }
                            break;
                        default:
                    }
                }
            } else if (eventType == XmlPullParser.END_TAG && xmlPullParser.getName().equalsIgnoreCase("item")) {
                insideItem = false;
                articles.add(currentArticle);
                currentArticle = new Article();
            }
            eventType = xmlPullParser.next();
        }
        triggerObserver();
    }

    private void triggerObserver() {
        setChanged();
        notifyObservers(articles);
    }
}
