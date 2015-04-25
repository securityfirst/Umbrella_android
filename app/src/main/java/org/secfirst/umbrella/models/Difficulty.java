package org.secfirst.umbrella.models;

import com.orm.SugarRecord;

public class Difficulty extends SugarRecord<Difficulty> {

    private long category;
    private int selected;

    public Difficulty() { }

    public Difficulty(long category, int selected) {
        this.category = category;
        this.selected = selected;
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
}
