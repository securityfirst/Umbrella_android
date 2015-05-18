package org.secfirst.umbrella.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;

import org.secfirst.umbrella.LoginActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.RefreshService;
import org.secfirst.umbrella.models.Category;
import org.secfirst.umbrella.models.CheckItem;
import org.secfirst.umbrella.models.Difficulty;
import org.secfirst.umbrella.models.Favourite;
import org.secfirst.umbrella.models.FeedItem;
import org.secfirst.umbrella.models.InitialData;
import org.secfirst.umbrella.models.Registry;
import org.secfirst.umbrella.models.Segment;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Global extends Application {

    private SharedPreferences prefs;
    private SharedPreferences.Editor sped;
    private boolean _termsAccepted, isLoggedIn;
    private boolean password;
    private ArrayList<FeedItem> feedItems = new ArrayList<>();
    private long feeditemsRefreshed;
    private Dao<Segment, String> daoSegment;
    private Dao<CheckItem, String> daoCheckItem;
    private Dao<Category, String> daoCategory;
    private Dao<Registry, String> daoRegistry;
    private Dao<Favourite, String> daoFavourite;
    private Dao<Difficulty, String> daoDifficulty;

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

    public void addToFeedItems(ArrayList<FeedItem> feedItems) {
        for (FeedItem feedItem : feedItems) {
            addFeedItem(feedItem);
        }
    }

    public void addFeedItem(FeedItem feedItem) {
        if (this.feedItems==null) this.feedItems = new ArrayList<>();
        this.feedItems.add(feedItem);
    }

    public boolean hasPasswordSet() {
        return password;
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
                    getOrmHelper().getWritableDatabase(getOrmHelper().getPassword()).rawExecSQL("PRAGMA rekey = '" + pw + "';");
                    password = true;
                    dialog.dismiss();
                    Toast.makeText(activity, "You have successfully set your password.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, "You must choose a stronger password. " + checkError, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void logout(Context context) {
        OpenHelperManager.setHelper(null);
        setLoggedIn(false);
        if (context.getClass().getSimpleName().equals("MainActivity")) {
            context.startActivity(new Intent(context, LoginActivity.class));
        }
    }

    public void resetPassword(final Activity activity) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle(getString(R.string.reset_password_title));
        alertDialogBuilder.setMessage(getString(R.string.reset_password_text));
        alertDialogBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getOrmHelper().getWritableDatabase(getOrmHelper().getPassword()).rawExecSQL("PRAGMA rekey = '" + OrmHelper.DATABASE_PASSWORD + "';");
                getOrmHelper().getWritableDatabase(OrmHelper.DATABASE_PASSWORD);
                new ResetData(activity).execute();
            }
        });
        alertDialogBuilder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private class ResetData extends AsyncTask<Void, Void, Void> {
        Activity activity;

        ResetData(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... params) {
            getOrmHelper().recreateTables(getOrmHelper().getConnectionSource());
            password = false;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(activity, getString(R.string.password_reset_notification), Toast.LENGTH_SHORT).show();
            set_termsAccepted(false);
            activity.finish();
            startActivity(activity.getIntent());
        }
    }

    public OrmHelper getOrmHelper() {
        return OpenHelperManager.getHelper(getApplicationContext(), OrmHelper.class);
    }

    public boolean initializeSQLCipher(String password) {
        SQLiteDatabase.loadLibs(this);
        if (OpenHelperManager.getHelper(getApplicationContext(), OrmHelper.class)==null) {
            OpenHelperManager.setHelper(new OrmHelper(getApplicationContext()));
        }
        try {
            if (password.equals("")) password = OrmHelper.DATABASE_PASSWORD;
            getOrmHelper().getWritableDatabase(password);
            getDaoSegment();
            getDaoCheckItem();
            getDaoCategory();
            getDaoRegistry();
            getDaoFavourite();
            getDaoDifficulty();
            startService();
            if (password.equals(OrmHelper.DATABASE_PASSWORD))setLoggedIn(true);
            return true;
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
        this.password = true;
        return false;
    }

    public void startService() {
        Intent i = new Intent(getApplicationContext(), RefreshService.class);
        i.putExtra("refresh_feed", getRefreshValue());
        startService(i);
    }

    public Dao<Segment, String> getDaoSegment() {
        if (daoSegment==null) {
            try {
                daoSegment = getOrmHelper().getDao(Segment.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoSegment;
    }

    public Dao<CheckItem, String> getDaoCheckItem() {
        if (daoCheckItem==null) {
            try {
                daoCheckItem = getOrmHelper().getDao(CheckItem.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoCheckItem;
    }

    public Dao<Category, String> getDaoCategory() {
        if (daoCategory==null) {
            try {
                daoCategory = getOrmHelper().getDao(Category.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoCategory;
    }

    public Dao<Registry, String> getDaoRegistry() {
        if (daoRegistry==null) {
            try {
                if (getOrmHelper().isOpen())
                daoRegistry = getOrmHelper().getDao(Registry.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoRegistry;
    }

    public Dao<Favourite, String> getDaoFavourite() {
        if (daoFavourite==null) {
            try {
                daoFavourite = getOrmHelper().getDao(Favourite.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoFavourite;
    }

    public Dao<Difficulty, String> getDaoDifficulty() {
        if (daoDifficulty==null) {
            try {
                daoDifficulty = getOrmHelper().getDao(Difficulty.class);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return daoDifficulty;
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
        if (getOrmHelper()!=null) {
            try {
                TableUtils.dropTable(getOrmHelper().getConnectionSource(), Segment.class, true);
                TableUtils.createTable(getOrmHelper().getConnectionSource(), Segment.class);
                for (Segment segment : segments) {
                    getDaoSegment().create(segment);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void syncCategories(ArrayList<Category> categories) {
        if (getOrmHelper()!=null) {
            try {
                TableUtils.dropTable(getOrmHelper().getConnectionSource(), Category.class, true);
                TableUtils.createTable(getOrmHelper().getConnectionSource(), Category.class);
                for (Category item : categories) {
                    getDaoCategory().create(item);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void syncCheckLists(ArrayList<CheckItem> checkList) {
        if (getOrmHelper()!=null) {
            try {
                TableUtils.dropTable(getOrmHelper().getConnectionSource(), CheckItem.class, true);
                TableUtils.createTable(getOrmHelper().getConnectionSource(), CheckItem.class);
                CheckItem previousItem = null;
                for (CheckItem checkItem : checkList) {
                    if (previousItem!=null && checkItem.getTitle().equals(previousItem.getTitle())&& checkItem.getParent()!=0) {
                        checkItem.setParent(previousItem.getId());
                        getDaoCheckItem().create(checkItem);
                    } else {
                        previousItem = checkItem;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int getRefreshValue() {
        int retInterval = 0;
        try {
            List<Registry> selInterval = getDaoRegistry().queryForEq(Registry.FIELD_NAME, "refresh_value");
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


    public long getFeeditemsRefreshed() {
        return feeditemsRefreshed;
    }

    public void setFeeditemsRefreshed(long feeditemsRefreshed) {
        this.feeditemsRefreshed = feeditemsRefreshed;
    }
}