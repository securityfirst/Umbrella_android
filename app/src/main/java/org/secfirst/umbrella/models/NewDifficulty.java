package org.secfirst.umbrella.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class NewDifficulty implements Serializable {
    String id;
    @SerializedName("description")
    String description;
    @SerializedName("checks")
    private ArrayList<CheckItem> checkItems;
    @SerializedName("items")
    private ArrayList<Segment> segments;

    public NewDifficulty() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public String toString() {
        return "Segment{" + "id='" + id + '\'' +
                ",description='" + description + '\'' +
                ", check_items='" + getCheckItems().size() + '\'' +
                ", items='" + getSegments().size() + '\'' +
                '}';
    }

}
