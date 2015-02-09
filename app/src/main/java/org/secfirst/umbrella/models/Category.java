package org.secfirst.umbrella.models;


import com.orm.SugarRecord;

public class Category extends SugarRecord<Category> {

    private long id;
    private int parent;
    private String category;
    private int difficulties;

    public Category() {}

    public Category(int id, int parent, String category, int difficulties) {
        this.id = id;
        this.parent = parent;
        this.category = category;
        this.difficulties = difficulties;
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

    public int getDifficulties() {
        return difficulties;
    }

    public void setDifficulties(int difficulties) {
        this.difficulties = difficulties;
    }
}
