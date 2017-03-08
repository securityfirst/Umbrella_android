package org.secfirst.umbrella.models;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

public class Favourite implements Serializable {
    public static final String FIELD_CATEGORY = "category";
    public static final String FIELD_DIFFICULTY = "difficulty";
    @DatabaseField(columnName = "_id", generatedId = true, allowGeneratedIdInsert = true)
    private int id;
    @DatabaseField(columnName = FIELD_CATEGORY)
    private String category;
    @DatabaseField(columnName = FIELD_DIFFICULTY)
    private String difficulty;

    public Favourite() {}

    public Favourite(String category, String difficulty) {
        this.category = category;
        this.difficulty = difficulty;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Favourite{");
        sb.append("id='").append(id).append('\'');
        sb.append("category='").append(category).append('\'');
        sb.append(",difficulty='").append(difficulty).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
