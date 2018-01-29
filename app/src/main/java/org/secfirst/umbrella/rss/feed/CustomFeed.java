package org.secfirst.umbrella.rss.feed;

import com.einmalfel.earl.Feed;
import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by dougl on 24/01/2018.
 */
@DatabaseTable(tableName = "custom_feed")
public class CustomFeed {

    private static final String FIELD_URL = "url";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_DESCRIPTION = "detail";

    @DatabaseField(columnName = "_id", generatedId = true, allowGeneratedIdInsert = true)
    private int _id;

    @DatabaseField(columnName = FIELD_URL)
    @SerializedName("url")
    private String feedUrl;

    @DatabaseField(columnName = FIELD_DESCRIPTION)
    @SerializedName("detail")
    private String detail;

    @DatabaseField(columnName = FIELD_TITLE)
    @SerializedName("title")
    private String title;

    private Feed feed;

    public CustomFeed() {
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDetail(String description) {
        this.detail = description;
    }

    public String getDetail() {
        return detail;
    }

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public String getTitle() {
        return title;
    }
}
