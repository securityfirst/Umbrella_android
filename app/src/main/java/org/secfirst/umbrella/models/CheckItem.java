package org.secfirst.umbrella.models;

public class CheckItem {

    private long id;
    private String title;
    private String text;
    private int value;
    private long parent;
    private int category;

    public CheckItem(){}

    public CheckItem(String title, String text, boolean value, long parent, int category) {
        this.title = title;
        this.text = text;
        this.value = (value) ? 1 : 0;
        this.parent = parent;
        this.category = category;
    }

    public CheckItem(String title, int category) {
        this.title = title;
        this.text = "";
        this.value = 0;
        this.parent = 0;
        this.category = category;
    }

    public int getCategory() {
        return category;
    }

    public boolean getValue() {
        return value == 1;
    }

    public int getIntValue() {
        return value;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public long getParent() {
        return parent;
    }

    public void setParent(long parent) {
        this.parent = parent;
    }
}
