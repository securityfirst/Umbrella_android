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
    public static final String FIELD_LABEL = "label";

    @DatabaseField(columnName = "_id", generatedId = true, allowGeneratedIdInsert = true)
    private int _id;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private FormScreen formScreen;
    @DatabaseField(columnName = FIELD_TITLE)
    @SerializedName("name")
    String title;
    @DatabaseField(columnName = FIELD_TYPE)
    @SerializedName(FIELD_TYPE)
    String type;
    @DatabaseField(columnName = FIELD_HINT)
    @SerializedName(FIELD_HINT)
    String hint;
    @DatabaseField(persisted = false)
    ArrayList<FormOption> optionArrayList;
    @ForeignCollectionField
    ForeignCollection<FormOption> options;

    List<FormValue> values;
    @DatabaseField(columnName = FIELD_LABEL)
    @SerializedName("label")
    String label;

    public FormItem() {}

    public ArrayList<FormOption> getOptionArrayList() {
        if (optionArrayList==null) optionArrayList = new ArrayList<>();
        return optionArrayList;
    }

    public void setOptionArrayList(ArrayList<FormOption> optionArrayList) {
        this.optionArrayList = optionArrayList;
    }

    public void setFormScreen(FormScreen formScreen) {
        this.formScreen = formScreen;
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

    public FormScreen getFormScreen() {
        if (formScreen!=null && formScreen.get_id()!=0) {
            try {
                formScreen = Global.INSTANCE.getDaoFormScreen().queryForId(String.valueOf(formScreen.get_id()));
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

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
