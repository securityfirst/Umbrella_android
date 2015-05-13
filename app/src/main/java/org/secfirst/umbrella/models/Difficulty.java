package org.secfirst.umbrella.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;

public class Difficulty implements Serializable {
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_SELECTED = "selected";
    public static final String FIELD_CREATED_AT = "created_at";
    @DatabaseField(columnName = "_id", generatedId = true, allowGeneratedIdInsert = true)
    private int id;
    @DatabaseField(columnName = FIELD_CATEGORY)
    private long category;
    @DatabaseField(columnName = FIELD_SELECTED)
    private int selected;
    @DatabaseField(columnName = FIELD_CREATED_AT)
    private long createdAt;

    public Difficulty() { }

    public Difficulty(long category, int selected) {
        this.category = category;
        this.selected = selected;
        this.createdAt = new Date().getTime();
    }

    public long getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Category{");
        sb.append("id='").append(id).append('\'');
        sb.append(", category='").append(category).append('\'');
        sb.append(",selected='").append(selected).append('\'');
        sb.append(", created_at='").append(createdAt).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
