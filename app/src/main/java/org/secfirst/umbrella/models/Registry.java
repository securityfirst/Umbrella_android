package org.secfirst.umbrella.models;

import com.j256.ormlite.field.DatabaseField;
import com.orm.SugarRecord;

import java.io.Serializable;

public class Registry extends SugarRecord<Registry> implements Serializable {
    @DatabaseField(columnName = "_id", generatedId = true, allowGeneratedIdInsert = true)
    private int id;
    @DatabaseField
    private String name;
    @DatabaseField
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
        final StringBuilder sb = new StringBuilder("Segment{");
        sb.append("id='").append(id).append('\'');
        sb.append(",name='").append(name).append('\'');
        sb.append(",value='").append(value).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
