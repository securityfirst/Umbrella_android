package org.secfirst.umbrella.util;

import android.app.Application;
import android.content.SharedPreferences;

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

}
