package org.secfirst.umbrella.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;

@DatabaseTable(tableName = "languages")
public class Language implements Serializable {
    private static final String FIELD_NAME = "name";
    private static final String FIELD_LABEL = "label";
    @DatabaseField(columnName = "_id", generatedId = true, allowGeneratedIdInsert = true)
    private int id;
    @DatabaseField(columnName = FIELD_NAME)
    private String name;
    @DatabaseField(columnName = FIELD_LABEL)
    private String label;

    public Language() { }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Language{");
        sb.append("id='").append(id).append('\'');
        sb.append("name='").append(name).append('\'');
        sb.append(",label='").append(label).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
