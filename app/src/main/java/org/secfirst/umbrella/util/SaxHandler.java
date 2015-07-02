package org.secfirst.umbrella.util;

import android.util.Log;

import org.secfirst.umbrella.BuildConfig;
import org.secfirst.umbrella.models.FeedItem;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SaxHandler extends DefaultHandler {

    String tempVal = "";
    private List<FeedItem> feedItems;
    private FeedItem tempFeedItem;

    public SaxHandler() {
        feedItems = new ArrayList<>();
    }

    public List<FeedItem> getFeeditems() {
        return feedItems;
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        tempVal = "";
        if (qName.equalsIgnoreCase("title")) {
            tempFeedItem = new FeedItem();
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (qName.equalsIgnoreCase("item")) {
            feedItems.add(tempFeedItem);
        } else if (qName.equalsIgnoreCase("title")) {
            tempFeedItem.setTitle(tempVal);
        } else if (qName.equalsIgnoreCase("description")) {
            tempFeedItem.setBody(tempVal);
        } else if (qName.equalsIgnoreCase("link")) {
            tempFeedItem.setUrl(tempVal);
        } else if (qName.equalsIgnoreCase("pubdate")) {
            try {
                DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
                Date date = formatter.parse(tempVal);
                tempFeedItem.setDate(date.getTime()/1000);
            } catch (ParseException e) {
                if (BuildConfig.BUILD_TYPE.equals("debug"))
                    Log.getStackTraceString(e.getCause().getCause());
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        tempVal = new String(ch, start, length);
    }
}