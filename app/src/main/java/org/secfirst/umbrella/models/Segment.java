package org.secfirst.umbrella.models;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

import org.secfirst.umbrella.util.UmbrellaUtil;

import java.io.Serializable;

public class Segment implements Serializable {
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_BODY = "body";
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_DIFFICULTY = "difficulty";
    @SerializedName("_id")
    @DatabaseField(columnName = "_id", generatedId = true, allowGeneratedIdInsert = true)
    private int id;
    @DatabaseField(columnName = FIELD_TITLE)
    private String title;
    @DatabaseField(columnName = FIELD_BODY)
    private String body;
    @DatabaseField(columnName = FIELD_CATEGORY)
    private int category;
    @SerializedName("difficulty_old")
    @DatabaseField(columnName = FIELD_DIFFICULTY)
    private int difficulty;
    @SerializedName("difficulty")
    private String difficultyString;
    @SerializedName("id")
    private String idString;

    public String getDifficultyString() {
        return difficultyString;
    }

    public void setDifficultyString(String difficultyString) {
        this.difficultyString = difficultyString;
    }

    public String getIdString() {
        return idString;
    }

    public void setIdString(String idString) {
        this.idString = idString;
    }

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
        if (difficulty==0 && difficultyString!=null) difficulty = UmbrellaUtil.getDifficultyFromString(difficultyString);
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        return "Segment{" + "id='" + id + '\'' +
                ",title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", difficultyString='" + difficultyString + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
