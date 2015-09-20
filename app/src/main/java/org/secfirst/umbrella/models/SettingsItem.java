package org.secfirst.umbrella.models;

public class SettingsItem {

    private String title;
    private Object value;

    public SettingsItem() {

    }

    public SettingsItem(String title) {
        this.title = title;
        this.value = 1;
    }

    public String getTitle() {
        return title;
    }

    public Object getValue() {
        return value;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
