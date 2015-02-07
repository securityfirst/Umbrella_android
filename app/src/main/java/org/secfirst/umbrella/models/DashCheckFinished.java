package org.secfirst.umbrella.models;

public class DashCheckFinished {

    private int percent;
    private int total;
    private int checked;
    private String category;

    public DashCheckFinished(String category) {
        this.category = category;
    }

    public DashCheckFinished(String category, int percent) {
        this.category = category;
        this.percent = percent;
    }

    public int getPercent() {
        return (int)((checked * 100.0f) / total);
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
}
