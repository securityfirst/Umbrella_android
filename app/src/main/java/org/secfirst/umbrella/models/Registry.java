package org.secfirst.umbrella.models;

import com.orm.SugarRecord;

public class Registry extends SugarRecord<Registry> {

    private String name;
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
}
