package org.secfirst.umbrella.models;


public class Category {

    private int id;
    private int parent;
    private String category;

    public Category() {}

    public Category(int id, int parent, String category) {
        this.id = id;
        this.parent = parent;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public int getParent() {
        return parent;
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
}
