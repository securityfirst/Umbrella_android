package org.secfirst.umbrella.models;

import com.google.gson.annotations.SerializedName;

import org.secfirst.umbrella.util.Global;

import java.io.Serializable;
import java.util.ArrayList;

public class NewCategory implements Serializable {
    @SerializedName("id")
    private String id;
    @SerializedName("subcategories")
    private ArrayList<NewCategory> subcategories;
    @SerializedName("name")
    private String name;
    @SerializedName("difficulties")
    private ArrayList<NewDifficulty> difficulties;
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

    private boolean hasBeginner() {
        if (hasBeginner==null) {
            hasBeginner = false;
            for (NewDifficulty difficulty : getDifficulties()) {
                if (difficulty.getId().equals("beginner")) {
                    hasBeginner = true;
                    break;
                }
            }
        }
        return hasBeginner;
    }

    private boolean hasAdvanced() {
        if (hasAdvanced==null) {
            hasAdvanced = false;
            for (NewDifficulty difficulty : getDifficulties()) {
                if (difficulty.getId().equals("advanced")) {
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
            for (NewDifficulty difficulty : getDifficulties()) {
                if (difficulty.getId().equals("expert")) {
                    hasExpert = true;
                    break;
                }
            }
        }
        return hasExpert;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Category getCategory(Global global) {
        boolean hasDifficulty = hasDifficulty();
        if (getName().equals("_") && hasBeginner() && getDifficulties().get(0).getSegments().size()==1) {
            hasDifficulty = false;
        }
        String textBeginner = "", textAdvanced = "", textExpert  = "";
        for (NewDifficulty difficulty : getDifficulties()) {
            if (difficulty.getId().equals("advanced")) {
                textAdvanced = difficulty.getDescription();
            } else if (difficulty.getId().equals("expert")) {
                textExpert = difficulty.getDescription();
            } else if (difficulty.getId().equals("beginner")) {
                textBeginner = difficulty.getDescription();
            }
        }
        return new Category(0, 0, getName(), hasDifficulty, hasBeginner(), hasAdvanced(), hasExpert(), textBeginner, textAdvanced, textExpert);
    }

    public boolean hasDifficulty() {
        return hasBeginner() || hasAdvanced() || hasExpert();
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<NewDifficulty> getDifficulties() {
        if (difficulties == null) difficulties = new ArrayList<>();
        return difficulties;
    }

    public void setDifficulties(ArrayList<NewDifficulty> newDifficulties) {
        this.difficulties = newDifficulties;
    }

}
