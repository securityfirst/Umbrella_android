package org.secfirst.umbrella.models;

import com.orm.SugarRecord;

public class Favourite extends SugarRecord<Favourite> {

    private long category;
    private int difficulty;

    public Favourite() {}

    public Favourite(long category, int difficulty) {
        this.category = category;
        this.difficulty = difficulty;
    }

    public long getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }
}
