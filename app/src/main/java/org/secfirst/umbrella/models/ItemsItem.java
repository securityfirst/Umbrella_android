package org.secfirst.umbrella.models;

import com.j256.ormlite.field.DatabaseField;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ItemsItem {
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_DIFFICULTY = "difficulty";
    @DatabaseField(columnName = FIELD_DIFFICULTY)
    public String difficulty;
    @DatabaseField
    public String title;
    @DatabaseField
    public String body;
    @DatabaseField(columnName = FIELD_CATEGORY)
    public String category;

    public ItemsItem() {}

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}