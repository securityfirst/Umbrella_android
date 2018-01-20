package org.secfirst.umbrella.rss.api;

import org.xmlpull.v1.XmlPullParser;

/**
 * Created by dougl on 16/01/2018.
 */

public enum XMLNode {

    ITEM("item"), TITLE("title"), LINK("link"),
    CREATOR("dc:creator"), CATEGORY("category"),
    ENCODED("content:encoded"), SRC("abs:src"),
    DESCRIPTION("description"), DATE("pubDate"),
    END_TAG(String.valueOf(XmlPullParser.END_TAG));

    private String name;

    XMLNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name();
    }

    public static XMLNode getEnum(String value) {
        for (XMLNode xmlNodeName : values()) {
            if (xmlNodeName.name.equalsIgnoreCase(value))
                return xmlNodeName;
        }
        return null;
    }
}
