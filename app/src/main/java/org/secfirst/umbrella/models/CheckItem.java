package org.secfirst.umbrella.models;

import com.j256.ormlite.field.DatabaseField;
import com.orm.SugarRecord;

import java.io.Serializable;

public class CheckItem extends SugarRecord<CheckItem> implements Serializable {
    @DatabaseField(columnName = "_id", generatedId = true, allowGeneratedIdInsert = true)
    private int id;
    @DatabaseField
    private int mid;
    @DatabaseField
    private String title;
    @DatabaseField
    private String text;
    @DatabaseField
    private int value;
    @DatabaseField
    private long parent;
    @DatabaseField
    private int category;
    @DatabaseField
    private int difficulty;
    @DatabaseField
    private int custom;
    @DatabaseField
    private int disabled;
    @DatabaseField
    private int noCheck;

    public CheckItem(){}

    public CheckItem(String title, String text, boolean value, long parent, int category, int difficulty) {
        this.title = title;
        this.text = text;
        this.value = (value) ? 1 : 0;
        this.parent = parent;
        this.category = category;
        this.difficulty = difficulty;
    }

    public CheckItem(String title, String text, boolean value, long parent, int category, int difficulty, boolean noCheck) {
        this.title = title;
        this.text = text;
        this.value = (value) ? 1 : 0;
        this.parent = parent;
        this.category = category;
        this.difficulty = difficulty;
        this.noCheck = noCheck ? 1 : 0;
    }

    public CheckItem(String title, int category) {
        this.title = title;
        this.text = "";
        this.value = 0;
        this.parent = 0;
        this.category = category;
    }

    public int getMId() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
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
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public boolean isCustom() {
        return custom==1;
    }

    public boolean isDisabled() {
        return disabled==1;
    }

    public void disable() {
        disabled = 1;
    }

    public void enable() {
        disabled = 0;
    }

    public boolean getNoCheck() {
        return noCheck != 0;
    }

    public void setNoCheck(boolean noCheck) {
        this.noCheck = noCheck ? 1 : 0;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Segment{");
        sb.append("id='").append(id).append('\'');
        sb.append(",title='").append(title).append('\'');
        sb.append(", text='").append(text).append('\'');
        sb.append(", value='").append(value).append('\'');
        sb.append(", parent='").append(parent).append('\'');
        sb.append(", difficulty='").append(difficulty).append('\'');
        sb.append(", category='").append(category).append('\'');
        sb.append(", nocheck='").append(noCheck).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
