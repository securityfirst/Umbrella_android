package org.secfirst.umbrella.models;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.secfirst.umbrella.util.Global;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

@DatabaseTable(tableName = "form_items")
public class FormItem {
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_TYPE = "type";
    public static final String FIELD_HINT = "hint";

    @DatabaseField(columnName = "_id", generatedId = true, allowGeneratedIdInsert = true)
    private int _id;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private FormScreen formScreen;
    @DatabaseField(columnName = FIELD_TITLE)
    @SerializedName(FIELD_TITLE)
    String title;
    @DatabaseField(columnName = FIELD_TYPE)
    @SerializedName(FIELD_TYPE)
    String type;
    @DatabaseField(columnName = FIELD_HINT)
    @SerializedName(FIELD_HINT)
    String hint;
    @ForeignCollectionField
    ForeignCollection<FormOption> options;

    List<FormValue> values;

    public FormItem() {}

    public FormItem(String title, String type, FormScreen screen) {
        this.title = title;
        this.type = type;
        this.formScreen = screen;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public ForeignCollection<FormOption> getOptions() {
        return options;
    }

    public void setOptions(ForeignCollection<FormOption> options) {
        this.options = options;
    }

    public FormScreen getFormScreen(Global global) {
        if (global!=null && formScreen!=null && formScreen.get_id()!=0) {
            try {
                formScreen = global.getDaoFormScreen().queryForId(String.valueOf(formScreen.get_id()));
            } catch (SQLException e) {
                Timber.e(e);
            }
        }
        return formScreen;
    }

    public List<FormValue> getValues() {
        return values;
    }

    public void addValue(FormValue formValue) {
        if (values==null) values = new ArrayList<>();
        values.add(formValue);
    }

    public void setValues(List<FormValue> values) {
        this.values = values;
    }


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
