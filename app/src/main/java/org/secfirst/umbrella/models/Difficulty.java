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
    private String category;
    @DatabaseField(columnName = FIELD_SELECTED)
    private String selected;
    @DatabaseField(columnName = FIELD_CREATED_AT)
    private long createdAt;

    public Difficulty() { }

    public Difficulty(String name, String selected) {
        this.category = name;
        this.selected = selected;
        this.createdAt = new Date().getTime();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSelected() {
        return selected;
    }

    public void setSelected(String selected) {
        this.selected = selected;
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
