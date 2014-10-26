package org.secfirst.umbrella.models;

public class DrawerChildItem {

    private String title;
    private int position;

    public DrawerChildItem(String name, int position) {
        this.title = name;
        this.position = position;

    }

    public int getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }
}
