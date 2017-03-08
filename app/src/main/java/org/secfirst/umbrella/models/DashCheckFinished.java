package org.secfirst.umbrella.models;

public class DashCheckFinished {

    private String difficulty;
    private int percent;
    private int total;
    private int checked;
    private String category;
    private boolean noIcon, noPercent;
    private boolean favourited;

    public DashCheckFinished(String category, String difficulty, boolean favourited) {
        this.category = category;
        this.difficulty = difficulty;
        this.favourited = favourited;
    }

    public DashCheckFinished(String category, int checked, int total, boolean favourited) {
        this.category = category;
        this.checked = checked;
        this.total = total;
        this.favourited = favourited;
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

    public String getDifficulty() {
        return difficulty;
    }

    public boolean isNoIcon() {
        return noIcon;
    }

    public void setNoIcon(boolean noIcon) {
        this.noIcon = noIcon;
    }

    public boolean isNoPercent() {
        return noPercent;
    }

    public void setNoPercent(boolean noPercent) {
        this.noPercent = noPercent;
    }

    public boolean isFavourited() {
        return favourited;
    }

    public void setFavourited(boolean favourited) {
        this.favourited = favourited;
    }
}
