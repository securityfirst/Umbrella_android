package org.secfirst.umbrella.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.j256.ormlite.dao.Dao;

import net.sqlcipher.database.SQLiteDatabase;

import org.secfirst.umbrella.MainActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.RefreshService;
import org.secfirst.umbrella.models.Category;
import org.secfirst.umbrella.models.CheckItem;
import org.secfirst.umbrella.models.FeedItem;
import org.secfirst.umbrella.models.InitialData;
import org.secfirst.umbrella.models.Registry;
import org.secfirst.umbrella.models.Segment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Global extends com.orm.SugarApp {

    private SharedPreferences prefs;
    private SharedPreferences.Editor sped;
    private boolean _termsAccepted, isLoggedIn;
    private String _password = "";
    private ArrayList<FeedItem> feedItems;
    private long feeditemsRefreshed;
    private OrmHelper dbHelper;
    private Dao<Segment, String> daoSegment;
    private Dao<CheckItem, String> daoCheckItem;
    private Dao<Category, String> daoCategory;
    private Dao<Registry, String> daoRegistry;

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onCreate() {
        super.onCreate();
        Context mContext = getApplicationContext();
        prefs = mContext.getSharedPreferences(
                "org.secfirst.umbrella", Application.MODE_PRIVATE);
        sped = prefs.edit();
        initializeSQLCipher();
        Intent i = new Intent(getApplicationContext(), RefreshService.class);
        i.putExtra("refresh_feed", getRefreshValue());
        startService(i);
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

    public ArrayList<FeedItem> getFeedItems() {
        return feedItems;
    }

    public void setFeedItems(ArrayList<FeedItem> feedItems) {
        this.feedItems = feedItems;
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
        View view = LayoutInflater.from(activity).inflate(R.layout.password_alert, null);
        final EditText pwInput = (EditText) view.findViewById(R.id.pwinput);
        final EditText confirmInput = (EditText) view.findViewById(R.id.pwconfirm);
        alert.setView(view);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.setCancelable(false);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        final AlertDialog dialog = alert.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pw = pwInput.getText().toString();
                String checkError = UmbrellaUtil.checkPasswordStrength(pw);
                if (!pw.equals(confirmInput.getText().toString())) {
                    Toast.makeText(activity, "Passwords do not match.", Toast.LENGTH_LONG).show();
                } else if (checkError.equals("")) {
                    savePassword(pw);
                    dialog.dismiss();
                    Toast.makeText(activity, "You have successfully set your password.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "You must choose a stronger password. " + checkError, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void resetPassword(final Activity activity) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Confirm reset password");
        alertDialogBuilder.setMessage("Are you sure you want to reset your password? This also means losing any data you might have entered so far\n");
        alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                savePassword("");
                resetDB();
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

    public void initializeSQLCipher() {
        SQLiteDatabase.loadLibs(this);
        if (dbHelper==null) {
            dbHelper = new OrmHelper(getApplicationContext());
            dbHelper.getWritableDatabase(OrmHelper.DATABASE_PASSWORD);
        }
        getDaoSegment();
        getDaoCheckItem();
        getDaoCategory();
        getDaoRegistry();
    }

    public void resetDB() {
        if (dbHelper!=null) {
            dbHelper.dropTables(dbHelper.getConnectionSource());
        }
        migrateData();
    }



    public Dao<Segment, String> getDaoSegment() {
        if (daoSegment==null) {
            try {
                daoSegment = dbHelper.getDao(Segment.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoSegment;
    }

    public Dao<CheckItem, String> getDaoCheckItem() {
        if (daoCheckItem==null) {
            try {
                daoCheckItem = dbHelper.getDao(CheckItem.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoCheckItem;
    }

    public Dao<Category, String> getDaoCategory() {
        if (daoCategory==null) {
            try {
                daoCategory = dbHelper.getDao(Category.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoCategory;
    }

    public Dao<Registry, String> getDaoRegistry() {
        if (daoRegistry==null) {
            try {
                daoRegistry = dbHelper.getDao(Registry.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoRegistry;
    }

    public void migrateData() {

        ArrayList<Segment> segments = InitialData.getSegmentList();
        try {
            List<Segment> fromDB = getDaoSegment().queryForAll();
            if (fromDB.size() == 0) {
                for (Segment segment : segments) {
                    getDaoSegment().create(segment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<CheckItem> checkList = InitialData.getCheckList();
        try {
            List<CheckItem> listsFromDB = getDaoCheckItem().queryForAll();
            if (listsFromDB.size() == 0) {
                for (CheckItem checkItem : checkList) {
                    getDaoCheckItem().create(checkItem);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayList<Category> categoryList = InitialData.getCategoryList();
        try {
            List<Category> catFromDB = getDaoCategory().queryForAll();
            if (catFromDB.size() == 0) {
                for (Category category : categoryList) {
                    getDaoCategory().create(category);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        setRefreshValue((int) TimeUnit.MINUTES.toMillis(30));
    }

    public void syncSegments(ArrayList<Segment> segments) {
        Segment.deleteAll(Segment.class);
        for (Segment segment : segments) {
            segment.save();
        }
    }

    public void syncCategories(ArrayList<Category> categories) {
        Category.deleteAll(Category.class);
        for (Category item : categories) {
            item.save();
        }
    }

    public void syncCheckLists(ArrayList<CheckItem> checkList) {
        CheckItem.deleteAll(CheckItem.class);
        CheckItem previousItem = null;
        for (CheckItem checkItem : checkList) {
            if (previousItem!=null && checkItem.getTitle().equals(previousItem.getTitle())&& checkItem.getParent()!=0) {
                checkItem.setParent(previousItem.getId());
                checkItem.save();
            } else {
                previousItem = checkItem;
            }
        }
    }

    public int getRefreshValue() {
        int retInterval = 0;
        try {
            List<Registry> selInterval = getDaoRegistry().queryForEq(Registry.FIELD_NAME, "refresh_value");
            Log.i("sel interval1", String.valueOf(selInterval));
            if (selInterval.size() > 0) {
                try {
                    retInterval = Integer.parseInt(selInterval.get(0).getValue());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            List<Registry> allReg = getDaoRegistry().queryForAll();
            for (Registry registry : allReg) {
                Log.d("reg", registry.toString());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return retInterval;
    }

    public void setRefreshValue(int refreshValue) {
        try {
            List<Registry> selInterval = getDaoRegistry().queryForEq(Registry.FIELD_NAME, "refresh_value");
            if (selInterval.size() > 0) {
                selInterval.get(0).setValue(String.valueOf(refreshValue));
                getDaoRegistry().update(selInterval.get(0));
            } else {
                getDaoRegistry().create(new Registry("refresh_value", String.valueOf(refreshValue)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
