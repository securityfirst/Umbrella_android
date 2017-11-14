package org.secfirst.umbrella.models;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.secfirst.umbrella.util.Global;

import java.sql.SQLException;
import java.util.Date;

import timber.log.Timber;

@DatabaseTable(tableName = "form_values")
public class FormValue {
    public static final String FIELD_ID = "_id";
    public static final String FIELD_FORM_ITEM_ID = "formItem_id";
    public static final String FIELD_VALUE = "value";
    public static final String FIELD_SESSION = "session_id";
    @DatabaseField(columnName = FIELD_ID, generatedId = true, allowGeneratedIdInsert = true)
    private int _id;
    @DatabaseField(canBeNull = true, foreign = true, columnName = FIELD_FORM_ITEM_ID)
    private FormItem formItem;
    @DatabaseField(canBeNull = true, persisted = false)
    private int formId;
    @DatabaseField(columnName = FIELD_SESSION)
    Long sessionID;
    @DatabaseField(version = true, dataType = DataType.DATE_LONG)
    private Date lastModified;
    @DatabaseField(columnName = FIELD_VALUE)
    @SerializedName(FIELD_VALUE)
    String value;

    public FormValue() {    }

    public FormValue(String value, FormItem formItem, Long sessionID) {
        this.value = value;
        this.formItem = formItem;
        if (formItem!=null) setFormId(formItem.get_id());
        this.sessionID = sessionID;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public Long getSessionID() {
        return sessionID;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public void setSessionID(Long sessionID) {
        sessionID = sessionID;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public FormItem getFormItem(Global global) {
        if (global!=null && formItem!=null && formItem.get_id()!=0) {
            try {
                formItem = global.getDaoFormItem().queryForId(String.valueOf(formItem.get_id()));
            } catch (SQLException e) {
                Timber.e(e);
            }
        }
        return formItem;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
