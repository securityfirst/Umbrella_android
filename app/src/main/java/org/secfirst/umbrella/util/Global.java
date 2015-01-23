package org.secfirst.umbrella.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class Global extends Application {

    private SharedPreferences prefs;
    private SharedPreferences.Editor sped;
    private boolean _termsAccepted;
    private String _password = "";

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onCreate() {
        super.onCreate();
        Context mContext = getApplicationContext();
        prefs = mContext.getSharedPreferences(
                "org.secfirst.umbrella", Application.MODE_PRIVATE);
        sped = prefs.edit();
    }

    public void savePassword(String password) {
        this._password = password;
        sped.putString("password", password);
        sped.commit();
    }

    public void set_termsAccepted(boolean terms) {
        _termsAccepted = terms;
    }

    public boolean getTermsAccepted() {
        return _termsAccepted;
    }

    public boolean checkPassword(String password) {
        this._password = prefs.getString("password", "");
        if (!this._password.equals("") && password.equals(this._password)) {
            return true;
        }
        return false;
    }

    public boolean hasPasswordSet() {
        if (this._password.equals("")) {
            String password = prefs.getString("password", "");
            if (!password.equals("")) {
                return true;
            }
            return false;
        }
        return true;
    }

}
