package org.secfirst.umbrella.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "feed_sources")
public class FeedSource implements Serializable {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_CODE = "code";
    public static final String FIELD_EXTERNAL = "is_external";
    public static final String FIELD_URL = "url";
    public static final String FIELD_LAST_CHECKED = "last_checked";

    @DatabaseField(columnName = "_id", generatedId = true, allowGeneratedIdInsert = true)
    private int _id;
    @DatabaseField(columnName = FIELD_NAME)
    private String name;
    @DatabaseField(columnName = FIELD_CODE)
    private int code;
    @DatabaseField(columnName = FIELD_EXTERNAL)
    private boolean isExternal;
    @DatabaseField(columnName = FIELD_URL)
    private String url;
    @DatabaseField(columnName = FIELD_LAST_CHECKED)
    private int lastChecked;

    public FeedSource() {}

    public FeedSource(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public FeedSource(String url, int code, String name) {
        this.url = url;
        this.code = code;
        this.name = name;
        this.isExternal = url!=null && !url.isEmpty();
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isExternal() {
        return isExternal;
    }

    public void setExternal(boolean external) {
        isExternal = external;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getLastChecked() {
        return lastChecked;
    }

    public void setLastChecked(int lastChecked) {
        this.lastChecked = lastChecked;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Segment{");
        sb.append("id='").append(_id).append('\'');
        sb.append(",name='").append(name).append('\'');
        sb.append(", code='").append(code).append('\'');
        sb.append(", is_external='").append(isExternal).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", last_checked='").append(lastChecked).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
