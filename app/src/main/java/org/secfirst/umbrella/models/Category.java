package org.secfirst.umbrella.models;


import com.orm.SugarRecord;

public class Category extends SugarRecord<Category> {

    private long id;
    private int parent;
    private String category;
    private int difficultyBeginner;
    private int difficultyAdvanced;
    private int difficultyExpert;

    public Category() {}

    public Category(int id, int parent, String category, boolean difficultyBeginner, boolean difficultyAdvanced, boolean difficultyExpert) {
        this.id = id;
        this.parent = parent;
        this.category = category;
        this.difficultyBeginner = difficultyBeginner ? 1 : 0;
        this.difficultyAdvanced = difficultyAdvanced ? 1 : 0;
        this.difficultyExpert = difficultyExpert ? 1 : 0;
    }

    public int getParent() {
        return parent;
    }

    public long getMId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public boolean getDifficultyBeginner() {
        return difficultyBeginner != 0;
    }

    public boolean getDifficultyAdvanced() {
        return difficultyAdvanced != 0;
    }

    public boolean getDifficultyExpert() {
        return difficultyExpert != 0;
    }
}
