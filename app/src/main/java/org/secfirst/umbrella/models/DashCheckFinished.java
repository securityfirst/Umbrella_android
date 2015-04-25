package org.secfirst.umbrella.models;

public class DashCheckFinished {

    private int difficulty;
    private int percent;
    private int total;
    private int checked;
    private String category;

    public DashCheckFinished(String category, int difficulty) {
        this.category = category;
        this.difficulty = difficulty;
    }

    public int getPercent() {
        return (int)((checked * 100.0f) / total);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }

    public int getDifficulty() {
        return difficulty;
    }
}
