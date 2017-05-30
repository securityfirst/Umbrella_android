package org.secfirst.umbrella.models;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "form_options")
public class FormOption {
    public static final String FIELD_OPTION = "option";
    @DatabaseField(canBeNull = true, foreign = true)
    private FormItem formItem;
    @DatabaseField(columnName = FIELD_OPTION)
    @SerializedName(FIELD_OPTION)
    String option;

    public FormOption() {}

    public FormOption(String option, FormItem formItem) {
        this.option = option;
        this.formItem = formItem;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

}
