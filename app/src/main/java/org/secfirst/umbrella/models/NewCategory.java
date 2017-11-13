package org.secfirst.umbrella.models;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.fragments.DifficultyFragment;
import org.secfirst.umbrella.util.Global;

import java.io.Serializable;
import java.util.ArrayList;

public class NewCategory implements Serializable {
    @DatabaseField(columnName = "_id", generatedId = true, allowGeneratedIdInsert = true)
    private int id;
    @SerializedName("subcategories")
    private ArrayList<NewCategory> subcategories;
    @SerializedName("name")
    private String name;
    @SerializedName("checks")
    private ArrayList<CheckItem> checkItems;
    @SerializedName("items")
    private ArrayList<Segment> segments;
    private Boolean hasBeginner, hasAdvanced, hasExpert;

    public NewCategory() {}

    public ArrayList<NewCategory> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(ArrayList<NewCategory> subcategories) {
        if (subcategories == null) subcategories = new ArrayList<>();
        this.subcategories = subcategories;
    }

    public String getName() {
        return name;
    }

    public boolean hasBeginner() {
        if (hasBeginner==null) {
            hasBeginner = false;
            for (Segment segment : getSegments()) {
                if (segment.getDifficulty() == DifficultyFragment.BEGINNER) {
                    hasBeginner = true;
                    break;
                }
            }
        }
        return hasBeginner;
    }

    public boolean hasAdvanced() {
        if (hasAdvanced==null) {
            hasAdvanced = false;
            for (Segment segment : getSegments()) {
                if (segment.getDifficulty() == DifficultyFragment.INTERMEDIATE) {
                    hasAdvanced = true;
                    break;
                }
            }
        }
        return hasAdvanced;
    }

    public boolean hasExpert() {
        if (hasExpert==null) {
            hasExpert = false;
            for (Segment segment : getSegments()) {
                if (segment.getDifficulty() == DifficultyFragment.EXPERT) {
                    hasExpert = true;
                    break;
                }
            }
        }
        return hasExpert;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Category getCategory(Global global) {
        return new Category(id, 0, getName(), hasDifficulty(), hasBeginner(), hasAdvanced(), hasExpert(), getName()+global.getString(R.string.beginner), getName()+global.getString(R.string.advanced), getName()+global.getString(R.string.expert));
    }

    public boolean hasDifficulty() {
        return hasBeginner() || hasAdvanced() || hasExpert();
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<CheckItem> getCheckItems() {
        if (checkItems == null) checkItems = new ArrayList<>();
        return checkItems;
    }

    public void setCheckItems(ArrayList<CheckItem> checkItems) {
        this.checkItems = checkItems;
    }

    public ArrayList<Segment> getSegments() {
        if (segments == null) segments = new ArrayList<>();
        return segments;
    }

    public void setSegments(ArrayList<Segment> segments) {
        this.segments = segments;
    }
}
