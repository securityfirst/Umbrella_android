package org.secfirst.umbrella.models;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.secfirst.umbrella.util.Global;

import java.sql.SQLException;

import timber.log.Timber;

@DatabaseTable(tableName = "form_screens")
public class FormScreen {
    public static final String FIELD_TITLE = "title";
    @DatabaseField(columnName = "_id", generatedId = true, allowGeneratedIdInsert = true)
    private int _id;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Form form;
    @DatabaseField(columnName = FIELD_TITLE)
    @SerializedName("title")
    String title;
    @ForeignCollectionField
    @SerializedName("items")
    ForeignCollection<FormItem> items;

    public FormScreen() {}

    public FormScreen(String title) {
        this.title = title;
    }

    public FormScreen(String title, Form form) {
        this.title = title;
        this.form = form;
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

    public ForeignCollection<FormItem> getItems() {
        return items;
    }

    public void setItems(ForeignCollection<FormItem> items) {
        this.items = items;
    }

    public Form getForm(Global global) {
        if (global!=null && form!=null && form.get_id()!=0) {
            try {
                form = global.getDaoForm().queryForId(String.valueOf(form.get_id()));
            } catch (SQLException e) {
                Timber.e(e);
            }
        }
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
