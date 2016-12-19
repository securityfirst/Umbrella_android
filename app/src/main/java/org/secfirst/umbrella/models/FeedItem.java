package org.secfirst.umbrella.models;


import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "feed_items")
public class FeedItem implements Serializable {

    public static final String FIELD_TITLE = "title";
    public static final String FIELD_DESCRIPTION = "description";
    public static final String FIELD_URL = "url";
    public static final String FIELD_UPDATED_AT = "updated_at";

    @DatabaseField(columnName = "_id", generatedId = true, allowGeneratedIdInsert = true)
    private int _id;
    @DatabaseField(columnName = FIELD_TITLE)
    @SerializedName("title")
    private String title;
    @DatabaseField(columnName = FIELD_DESCRIPTION)
    @SerializedName("description")
    private String body;
    @DatabaseField(columnName = FIELD_URL)
    @SerializedName("url")
    private String url;
    @DatabaseField(columnName = FIELD_UPDATED_AT)
    @SerializedName("updated_at")
    private long date;

    public FeedItem() {}

    public FeedItem(String title, String body, String url) {
        this.title = title;
        this.body = body;
        this.url = url;
    }

    public FeedItem(String title, String heading, String body, String url) {
        this.title = title;
        this.body = body;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Segment{");
        sb.append("id='").append(_id).append('\'');
        sb.append(",title='").append(title).append('\'');
        sb.append(", description='").append(body).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", updated_at='").append(date).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
