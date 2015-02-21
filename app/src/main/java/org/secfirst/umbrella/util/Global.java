package org.secfirst.umbrella.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import org.secfirst.umbrella.MainActivity;

public class Global extends com.orm.SugarApp {

    private SharedPreferences prefs;
    private SharedPreferences.Editor sped;
    private boolean _termsAccepted, isLoggedIn;
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

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public void savePassword(String password) {
        this._password = password;
        sped.putString("password", password).commit();
    }

    public void set_termsAccepted(boolean terms) {
        _termsAccepted = terms;
        sped.putBoolean("termsAccepted", _termsAccepted).commit();
    }

    public boolean getTermsAccepted() {
        _termsAccepted = prefs.getBoolean("termsAccepted", false);
        return _termsAccepted;
    }

    public boolean checkPassword(String password) {
        this._password = prefs.getString("password", "");
        if (!this._password.equals("") && password.equals(this._password)) {
            setLoggedIn(true);
            return true;
        }
        return false;
    }

    public boolean hasPasswordSet() {
        if (this._password.equals("")) {
            String password = prefs.getString("password", "");
            return !password.equals("");
        }
        return true;
    }

    public void setPassword(final Activity activity) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle("Set your password");
        alert.setMessage("Your password must be at least 8 characters long and must contain at least one digit and one capital letter\n");
        final EditText pwInput = new EditText(activity);
        pwInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        alert.setView(pwInput);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String pw = pwInput.getText().toString();
                String checkError = UmbrellaUtil.checkPasswordStrength(pw);
                if (checkError.equals("")) {
                    savePassword(pw);
                    dialog.dismiss();
                    Toast.makeText(activity, "You have successfully set your password.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "You must choose a stronger password. " + checkError, Toast.LENGTH_SHORT).show();
                }
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    public void resetPassword(final Activity activity) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Confirm reset password");
        alertDialogBuilder.setMessage("Are you sure you want to reset your password? This also means losing any data you might have entered so far\n");
        alertDialogBuilder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                savePassword("");
                UmbrellaUtil.resetDataToInitial();
                Toast.makeText(activity, "Password reset and all data removed.", Toast.LENGTH_SHORT).show();
                activity.startActivity(new Intent(activity, MainActivity.class));
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
