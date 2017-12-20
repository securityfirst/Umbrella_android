package org.secfirst.umbrella.models;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.apache.commons.lang3.builder.ToStringBuilder;

@DatabaseTable(tableName = "forms")
public class Form {
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_ID = "_id";
    @DatabaseField(columnName = FIELD_ID, generatedId = true, allowGeneratedIdInsert = true)
    private int _id;
    @DatabaseField(columnName = FIELD_TITLE)
    @SerializedName("title")
    String title;
    @ForeignCollectionField(eager = true)
    @SerializedName("screens")
    ForeignCollection<FormScreen> screens;

    public Form() {}

    public Form(String title) {
        this.title = title;
    }

    public Form(String title, ForeignCollection<FormScreen> screens) {
        this.title = title;
        setScreens(screens);
    }

    public int get_id() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ForeignCollection<FormScreen> getScreens() {
        return screens;
    }

    public void setScreens(ForeignCollection<FormScreen> screens) {
        this.screens = screens;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
