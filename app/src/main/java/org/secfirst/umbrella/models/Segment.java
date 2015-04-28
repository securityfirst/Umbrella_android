package org.secfirst.umbrella.models;

import com.orm.SugarRecord;

public class Segment extends SugarRecord<Segment> {
    private String title;
    private String body;
    private int category;
    private int difficulty;

    public Segment(){}

    public Segment(int category, int difficulty, String body) {
        this.body = body;
        this.difficulty = difficulty;
        this.category = category;
    }

    public Segment(int category, int difficulty, String title, String body) {
        this.title = title;
        this.body = body;
        this.difficulty = difficulty;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getCategory() {
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
