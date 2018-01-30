package org.secfirst.umbrella.models;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

import org.secfirst.umbrella.util.UmbrellaUtil;

import java.io.Serializable;

public class CheckItem implements Serializable {
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_TEXT = "text";
    public static final String FIELD_VALUE = "value";
    public static final String FIELD_PARENT = "parent";
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_DIFFICULTY = "difficulty";
    public static final String FIELD_DIFFICULTY_STRING = "difficulty_string";
    public static final String FIELD_CUSTOM = "custom";
    public static final String FIELD_DISABLED = "disabled";
    @DatabaseField(columnName = "_id", generatedId = true, allowGeneratedIdInsert = true)
    private int id;
    @DatabaseField(columnName = FIELD_TITLE)
    @SerializedName("text")
    private String title;
    @DatabaseField(columnName = FIELD_TEXT)
    @SerializedName("title")
    private String text;
    @DatabaseField(columnName = FIELD_VALUE)
    private int value;
    @DatabaseField(columnName = FIELD_PARENT)
    private long parent;
    @DatabaseField(columnName = FIELD_CATEGORY)
    private int category;
    @DatabaseField(columnName = FIELD_DIFFICULTY)
    @SerializedName("difficulty_old")
    private int difficulty;
    @DatabaseField(columnName = FIELD_DIFFICULTY_STRING)
    @SerializedName("difficulty")
    private String difficultyString;

    public String getDifficultyString() {
        return difficultyString;
    }

    public void setDifficultyString(String difficultyString) {
        this.difficultyString = difficultyString;
    }

    @DatabaseField(columnName = FIELD_CUSTOM)
    private int custom;
    @DatabaseField(columnName = FIELD_DISABLED)
    private int disabled;
    @SerializedName("no_check")
    @DatabaseField
    private boolean noCheck;

    public CheckItem() {
    }

    public CheckItem(String title, String text, boolean value, long parent, int category, int difficulty) {
        this.title = title;
        this.text = text;
        this.value = value ? 1 : 0;
        this.parent = parent;
        this.category = category;
        this.difficulty = difficulty;
    }

    public CheckItem(String title, String text, boolean value, long parent, int category, int difficulty, boolean noCheck) {
        this.title = title;
        this.text = text;
        this.value = value ? 1 : 0;
        this.parent = parent;
        this.category = category;
        this.difficulty = difficulty;
        this.noCheck = noCheck;
    }

    public CheckItem(String title, int category) {
        this.title = title;
        this.text = "";
        this.value = 0;
        this.parent = 0;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int getCustom() {
        return custom;
    }

    public void setCustom(int custom) {
        this.custom = custom;
    }

    public int getDisabled() {
        return disabled;
    }

    public void setDisabled(int disabled) {
        this.disabled = disabled;
    }

    public int getDifficulty() {
        difficultyString = difficultyString == null ? UmbrellaUtil.a(difficulty) : difficultyString;
        difficulty = UmbrellaUtil.getDifficultyFromString(difficultyString);
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public boolean isCustom() {
        return custom == 1;
    }

    public boolean isDisabled() {
        return disabled == 1;
    }

    public void disable() {
        disabled = 1;
    }

    public void enable() {
        disabled = 0;
    }

    public boolean getNoCheck() {
        return noCheck;
    }

    public void setNoCheck(boolean noCheck) {
        this.noCheck = noCheck;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CheckItem{");
        sb.append("id='").append(id).append('\'');
        sb.append(", text='").append(text).append('\'');
        sb.append(", value='").append(value).append('\'');
        sb.append(", parent='").append(parent).append('\'');
        sb.append(", difficulty='").append(difficulty).append('\'');
        sb.append(", difficultyString='").append(difficultyString).append('\'');
        sb.append(", category='").append(category).append('\'');
        sb.append(", nocheck='").append(noCheck).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
