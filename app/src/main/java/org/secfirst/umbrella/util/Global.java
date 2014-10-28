package org.secfirst.umbrella.util;

import android.app.Application;
import android.content.SharedPreferences;

import org.secfirst.umbrella.models.DrawerChildItem;

import java.util.ArrayList;

public class Global extends Application {

    private SharedPreferences prefs;
    private SharedPreferences.Editor sped;
    private boolean _shownTour;

    public void savePassword() {

    }

    public void set_shownTour(boolean hasShown) {
        _shownTour = hasShown;
    }

    public boolean hasShownTour() {
        return _shownTour;
    }

    public ArrayList<String> getDrawerItems() {
        ArrayList<String> groupItem = new ArrayList<String>();
        groupItem.add("My Security");
        groupItem.add("Communications");
        groupItem.add("Personal");
        groupItem.add("Travel");
        groupItem.add("Operations");
        groupItem.add("Home / Office");
        groupItem.add("Computer Network");
        groupItem.add("Glossary");
        groupItem.add("Index");
        return groupItem;
    }

    public ArrayList<ArrayList<DrawerChildItem>> getDrawerChildItems() {
        ArrayList<ArrayList<DrawerChildItem>> childItem = new ArrayList<ArrayList<DrawerChildItem>>();
        ArrayList<DrawerChildItem> child = new ArrayList<DrawerChildItem>();
        childItem.add(child);
        child = new ArrayList<DrawerChildItem>();
        child.add(new DrawerChildItem("Passwords", 1));
        child.add(new DrawerChildItem("Mobile Phones", 2));
        child.add(new DrawerChildItem("Stay Anonymous Online", 3));
        child.add(new DrawerChildItem("Safe Deleting", 4));
        childItem.add(child);


        child = new ArrayList<DrawerChildItem>();
        childItem.add(child);

        child = new ArrayList<DrawerChildItem>();
        childItem.add(child);
        return childItem;
    }

}
