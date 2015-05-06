package org.secfirst.umbrella.models;

import com.orm.SugarRecord;

import java.util.Date;

public class Difficulty extends SugarRecord<Difficulty> {

    private long category;
    private int selected;
    private long createdAt;

    public Difficulty() { }

    public Difficulty(long category, int selected) {
        this.category = category;
        this.selected = selected;
        this.createdAt = new Date().getTime();
    }

    public long getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
