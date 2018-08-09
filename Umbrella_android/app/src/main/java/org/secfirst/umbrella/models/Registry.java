package org.secfirst.umbrella.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class Registry implements Serializable {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_VALUE = "value";
    @DatabaseField(columnName = "_id", generatedId = true, allowGeneratedIdInsert = true)
    private int id;
    @DatabaseField(columnName = FIELD_NAME)
    private String name;
    @DatabaseField(columnName = FIELD_VALUE)
    private String value;

    public Registry() {
    }

    public Registry(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Registry{");
        sb.append("id='").append(id).append('\'');
        sb.append(",name='").append(name).append('\'');
        sb.append(",value='").append(value).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
