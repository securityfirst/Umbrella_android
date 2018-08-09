package org.secfirst.umbrella.models;

public class DrawerChildItem {

    private String title;
    private long position;

    public DrawerChildItem(String name, long position) {
        this.title = name;
        this.position = position;

    }

    public long getPosition() {
        return position;
    }

    public String getTitle() {
        return title;
    }
}
