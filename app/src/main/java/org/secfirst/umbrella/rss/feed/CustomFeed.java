package org.secfirst.umbrella.rss.feed;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by dougl on 24/01/2018.
 */
@DatabaseTable(tableName = "feed_items")
public class CustomFeed {

    public static final String FIELD_URL = "url";

    @DatabaseField(columnName = "_id", generatedId = true, allowGeneratedIdInsert = true)
    private int _id;
    @DatabaseField(columnName = FIELD_URL)
    @SerializedName("url")
    private String url;

    public CustomFeed() {
    }

    public CustomFeed(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "CustomFeed{" +
                "url='" + url + '\'' +
                '}';
    }
}
