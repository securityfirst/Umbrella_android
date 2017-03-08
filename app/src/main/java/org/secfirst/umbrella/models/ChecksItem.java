package org.secfirst.umbrella.models;

import com.j256.ormlite.field.DatabaseField;

public class ChecksItem {
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_DIFFICULTY = "difficulty";
    @DatabaseField(columnName = "_id", generatedId = true, allowGeneratedIdInsert = true)
    private int id;
    @DatabaseField(columnName = FIELD_DIFFICULTY)
    private String difficulty;
    @DatabaseField
    private String text;
    @DatabaseField(columnName = FIELD_CATEGORY)
    private String category;
    @DatabaseField
    private boolean noCheck;
    @DatabaseField
    private int value;
    @DatabaseField
    private int disabled;
    @DatabaseField
    private int custom;

    public ChecksItem() {}

    public ChecksItem(String text, String category) {
        this.text = text;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isNoCheck() {
        return noCheck;
    }

    public void setNoCheck(boolean noCheck) {
        this.noCheck = noCheck;
    }

    public void setCustom(int custom) {
        this.custom = custom;
    }

    public boolean isCustom() {
        return custom > 1;
    }

    public boolean isChecked() {
        return value > 1;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isDisabled() {
        return disabled > 0;
    }

    public void setDisabled(int disabled) {
        this.disabled = disabled;
    }

    public void enable() {
        setDisabled(1);
    }

    public void disable() {
        setDisabled(0);
    }

    public boolean hasParent() {
        return false;
    }
}