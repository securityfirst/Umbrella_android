package org.secfirst.umbrella.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Tree {
    @SerializedName("categories")
    private ArrayList<NewCategory> categories;
    @SerializedName("assets")
    private ArrayList<String> assets;

    public Tree() {}

    public ArrayList<NewCategory> getCategories() {
        if (categories==null) categories = new ArrayList<>();
        return categories;
    }

    public void setCategories(ArrayList<NewCategory> categories) {
        this.categories = categories;
    }

    public ArrayList<String> getAssets() {
        if (assets==null) assets = new ArrayList<>();
        return assets;
    }

    public void setAssets(ArrayList<String> assets) {
        this.assets = assets;
    }
}
