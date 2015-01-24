package org.secfirst.umbrella.models;

public class DashCheckFinished {

    private int percent;
    private String category;

    public DashCheckFinished(String category, int percent) {
        this.category = category;
        this.percent = percent;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
