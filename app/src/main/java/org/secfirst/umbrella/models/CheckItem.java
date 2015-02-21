package org.secfirst.umbrella.models;

import com.orm.SugarRecord;

public class CheckItem extends SugarRecord<CheckItem> {

    private int mid;
    private String title;
    private String text;
    private int value;
    private long parent;
    private int category;
    private int difficulty;
    private int custom;
    private int disabled;
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
}
