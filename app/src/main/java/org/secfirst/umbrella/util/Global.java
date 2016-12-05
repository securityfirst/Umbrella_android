package org.secfirst.umbrella.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.IntentCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.table.TableUtils;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;

import org.secfirst.umbrella.BuildConfig;
import org.secfirst.umbrella.LoginActivity;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.RefreshService;
import org.secfirst.umbrella.TourActivity;
import org.secfirst.umbrella.models.Category;
import org.secfirst.umbrella.models.CheckItem;
import org.secfirst.umbrella.models.Difficulty;
import org.secfirst.umbrella.models.Favourite;
import org.secfirst.umbrella.models.FeedItem;
import org.secfirst.umbrella.models.FeedSource;
import org.secfirst.umbrella.models.Registry;
import org.secfirst.umbrella.models.Segment;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Global extends Application {

    private SharedPreferences prefs;
    private SharedPreferences.Editor sped;
    private boolean _termsAccepted, isLoggedIn, password;
    private ArrayList<FeedItem> feedItems = new ArrayList<>();
    private long feeditemsRefreshed;
    private Dao<Segment, String> daoSegment;
    private Dao<CheckItem, String> daoCheckItem;
    private Dao<Category, String> daoCategory;
    private Dao<Registry, String> daoRegistry;
    private Dao<Favourite, String> daoFavourite;
    private Dao<Difficulty, String> daoDifficulty;
    private OrmHelper dbHelper;

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
        setFeeditemsRefreshed(new Date().getTime());
    }

    public void addToFeedItems(ArrayList<FeedItem> feedItems) {
        for (FeedItem feedItem : feedItems) {
            addFeedItem(feedItem);
        }
    }

    public void addFeedItem(FeedItem feedItem) {
        if (this.feedItems==null) this.feedItems = new ArrayList<>();
        this.feedItems.add(feedItem);
        setFeeditemsRefreshed(new Date().getTime());
    }

    public boolean getNotificationsEnabled() {
        Registry r = getRegistry("notificationsEnabled");
        boolean enabled = false;
        try {
            if (r!=null) enabled = Boolean.valueOf(r.getValue());
        } catch(NumberFormatException nfe) {
            UmbrellaUtil.logIt(this, Log.getStackTraceString(nfe.getCause()));
        }
        return enabled;
    }

    public Registry getRegistry(String name) {
        Registry registry = null;
        try {
            PreparedQuery<Registry> queryBuilder =
                    getDaoRegistry().queryBuilder().where().eq(Registry.FIELD_NAME, name).prepare();
            registry = getDaoRegistry().queryForFirst(queryBuilder);
        } catch (SQLException e) {
            UmbrellaUtil.logIt(this, Log.getStackTraceString(e.getCause()));
        }
        return registry;
    }

    public void setRegistry(String name, Object value) {
        Registry registry = null;
        try {
            PreparedQuery<Registry> queryBuilder =
                    getDaoRegistry().queryBuilder().where().eq(Registry.FIELD_NAME, name).prepare();
            registry = getDaoRegistry().queryForFirst(queryBuilder);
        } catch (SQLException e) {
            UmbrellaUtil.logIt(this, Log.getStackTraceString(e.getCause()));
        } finally {
            if (registry!=null) {
                try {
                    getDaoRegistry().update(registry);
                } catch (SQLException e) {
                    UmbrellaUtil.logIt(this, Log.getStackTraceString(e.getCause()));
                }
            } else {
                try {
                    getDaoRegistry().create(new Registry(name, String.valueOf(value)));
                } catch (SQLException e) {
                    UmbrellaUtil.logIt(this, Log.getStackTraceString(e.getCause()));
                }
            }
        }
    }

    public void setNotificationsEnabled(boolean enabled) {
        setRegistry("notificationsEnabled", enabled);
    }

    public boolean getNotificationRingtoneEnabled() {
        Registry r = getRegistry("notificationRingtoneEnabled");
        boolean enabled = true;
        try {
            if (r!=null) enabled = Boolean.valueOf(r.getValue());
        } catch(NumberFormatException nfe) {
            UmbrellaUtil.logIt(this, Log.getStackTraceString(nfe.getCause()));
        }
        return enabled;
    }

    public void setNotificationRingtoneEnabled(boolean enabled) {
        setRegistry("notificationRingtoneEnabled", enabled);
    }

    public Uri getNotificationRingtone() {
        Registry r = getRegistry("notificationRingtone");
        Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        try {
            if (r!=null && !r.getValue().equals("")) ringtone = Uri.parse(r.getValue());
        } catch(IllegalArgumentException e) {
            UmbrellaUtil.logIt(this, Log.getStackTraceString(e.getCause()));
        }
        return ringtone;
    }

    public void setNotificationRingtone(Uri notificationRingtoneUri) {
        if(notificationRingtoneUri== null) return;
        setRegistry("notificationRingtone", notificationRingtoneUri.toString());
    }

    public boolean getNotificationVibrationEnabled() {
        Registry r = getRegistry("notificationVibration");
        boolean enabled = true;
        try {
            if (r!=null) enabled = Boolean.valueOf(r.getValue());
        } catch(NumberFormatException nfe) {
            UmbrellaUtil.logIt(this, Log.getStackTraceString(nfe.getCause()));
        }
        return enabled;
    }

    public void setNotificationVibrationEnabled(boolean enabled) {
        setRegistry("notificationVibration", enabled);
    }

    public boolean getSkipPassword() {
        return prefs.getBoolean("skipPassword", false);
    }

    public void setSkipPassword(boolean skipPassword) {
        sped.putBoolean("skipPassword", skipPassword).commit();
    }

    public boolean hasPasswordSet(boolean withoutSkip) {
        if (withoutSkip) return password;
        else return password || prefs.getBoolean("skipPassword", false);
    }

    public void setPassword(final Activity activity) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(R.string.set_password_title);
        alert.setMessage(R.string.set_password_body);
        View view = LayoutInflater.from(activity).inflate(R.layout.password_alert, null);
        final EditText pwInput = (EditText) view.findViewById(R.id.pwinput);
        final EditText confirmInput = (EditText) view.findViewById(R.id.pwconfirm);
        alert.setView(view);
        alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });
        alert.setNeutralButton(R.string.skip, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final AlertDialog.Builder alert2 = new AlertDialog.Builder(activity);
                alert2.setTitle(R.string.skip_password_title);
                alert2.setMessage(R.string.skip_password_warning);
                alert2.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPassword(activity);
                    }
                });
                alert2.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setSkipPassword(true);
                    }
                });
                final AlertDialog dialog2 = alert2.create();
                dialog2.show();
            }
        });
        alert.setCancelable(false);
        alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
                String checkError = UmbrellaUtil.checkPasswordStrength(pw, getApplicationContext());
                if (!pw.equals(confirmInput.getText().toString())) {
                    Toast.makeText(activity, R.string.passwords_do_not_match, Toast.LENGTH_LONG).show();
                } else if (checkError.equals("")) {
                    getOrmHelper().getWritableDatabase(getOrmHelper().getPassword()).rawExecSQL("PRAGMA rekey = '" + new SelectArg(pw) + "';");
                    password = true;
                    dialog.dismiss();
                    Toast.makeText(activity, R.string.you_have_successfully_set_your_password, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(activity, getString(R.string.choose_stronger_password) + checkError, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void logout(Context context) {
        OpenHelperManager.setHelper(null);
        setLoggedIn(false);

        if (OpenHelperManager.getHelper(context, OrmHelper.class) != null) {
            OpenHelperManager.getHelper(context, OrmHelper.class).close();
            OpenHelperManager.setHelper(null);
            dbHelper = null;
        }
        if (context.getClass().getSimpleName().equals("MainActivity")) {
            Intent i = new Intent(context, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(i);
        }
    }

    public void resetPassword(final Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle(getString(R.string.reset_password_title));
        alertDialogBuilder.setMessage(getString(R.string.reset_password_text));
        alertDialogBuilder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                closeDbAndDAOs();
                deleteDatabase(getApplicationContext().getDatabasePath(OrmHelper.DATABASE_NAME));
                removeSharedPreferences();
                Intent i = new Intent(context, TourActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Toast.makeText(context, R.string.content_reset_to_default, Toast.LENGTH_SHORT).show();
                ((Activity) context).finish();
                ;
                password = isLoggedIn = false;
                startActivity(i);
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

    public OrmHelper getOrmHelper() {
        SQLiteDatabase.loadLibs(this);
        if (dbHelper==null || !dbHelper.isOpen()) {
            createDatabaseIfNotExists();
            dbHelper = new OrmHelper(getApplicationContext());
        }
        return dbHelper;
    }

    public boolean initializeSQLCipher(String password) {
        if (password.equals("")) password = getString(R.string.default_db_password);
        SQLiteDatabase.loadLibs(this);
        if (dbHelper==null || !dbHelper.isOpen()) {
            createDatabaseIfNotExists();
            dbHelper = new OrmHelper(getApplicationContext());
        }
        try {
            getOrmHelper().getWritableDatabase(password);
            getDaoSegment();
            getDaoCheckItem();
            getDaoCategory();
            getDaoRegistry();
            getDaoFavourite();
            getDaoDifficulty();
            startService();
            if (password.equals(getString(R.string.default_db_password)))setLoggedIn(true);
            return true;
        } catch (SQLiteException e) {
            UmbrellaUtil.logIt(getApplicationContext(), e.toString());
            if (BuildConfig.BUILD_TYPE.equals("debug"))
                Log.getStackTraceString(e.getCause());
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
                if (BuildConfig.BUILD_TYPE.equals("debug"))
                    Log.getStackTraceString(e.getCause());
            }
        }
        return daoSegment;
    }

    public Dao<CheckItem, String> getDaoCheckItem() {
        if (daoCheckItem==null) {
            try {
                daoCheckItem = getOrmHelper().getDao(CheckItem.class);
            } catch (SQLException e) {
                if (BuildConfig.BUILD_TYPE.equals("debug"))
                    Log.getStackTraceString(e.getCause());
            }
        }
        return daoCheckItem;
    }

    public Dao<Category, String> getDaoCategory() {
        if (daoCategory==null) {
            try {
                daoCategory = getOrmHelper().getDao(Category.class);
            } catch (SQLException e) {
                if (BuildConfig.BUILD_TYPE.equals("debug"))
                    Log.getStackTraceString(e.getCause());
            }
        }
        return daoCategory;
    }

    public Dao<Registry, String> getDaoRegistry() {
        if (daoRegistry==null) {
            try {
                daoRegistry = getOrmHelper().getDao(Registry.class);
            } catch (SQLException e) {
                if (BuildConfig.BUILD_TYPE.equals("debug"))
                    Log.getStackTraceString(e.getCause());
            }
        }
        return daoRegistry;
    }

    public Dao<Favourite, String> getDaoFavourite() {
        if (daoFavourite==null) {
            try {
                daoFavourite = getOrmHelper().getDao(Favourite.class);
            } catch (SQLException e) {
                if (BuildConfig.BUILD_TYPE.equals("debug"))
                    Log.getStackTraceString(e.getCause());
            }
        }
        return daoFavourite;
    }

    public Dao<Difficulty, String> getDaoDifficulty() {
        if (daoDifficulty==null) {
            try {
                daoDifficulty = getOrmHelper().getDao(Difficulty.class);
            } catch (SQLException e) {
                if (BuildConfig.BUILD_TYPE.equals("debug"))
                    Log.getStackTraceString(e.getCause());
            }
        }
        return daoDifficulty;
    }

    public void syncSegments(ArrayList<Segment> segments) {
        if (getOrmHelper()!=null) {
            try {
                TableUtils.clearTable(getOrmHelper().getConnectionSource(), Segment.class);
                for (Segment segment : segments) {
                    getDaoSegment().create(segment);
                }
            } catch (SQLException e) {
                if (BuildConfig.BUILD_TYPE.equals("debug"))
                    Log.getStackTraceString(e.getCause());
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
                if (BuildConfig.BUILD_TYPE.equals("debug"))
                    Log.getStackTraceString(e.getCause());
            }
        }
    }

    public void syncCheckLists(ArrayList<CheckItem> checkList) {
        if (getOrmHelper()!=null) {
            try {
                DeleteBuilder<CheckItem, String> deleteBuilder = getDaoCheckItem().deleteBuilder();
                deleteBuilder.where().not().eq(CheckItem.FIELD_CUSTOM, "1");
                deleteBuilder.delete();
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
                if (BuildConfig.BUILD_TYPE.equals("debug"))
                    Log.getStackTraceString(e.getCause());
            }
        }
    }

    public String getRefreshLabel() {
        String refreshValueLabel = "";
        int refreshValue = getRefreshValue();
        HashMap<String, Integer> refreshValues = UmbrellaUtil.getRefreshValues(getApplicationContext());
        for (Map.Entry<String, Integer> entry : refreshValues.entrySet()) {
            if (entry.getValue().equals(refreshValue)) {
                refreshValueLabel = entry.getKey();
            }
        }
        return refreshValueLabel;
    }

    public ArrayList<Integer> getSelectedFeedSources() {
        final CharSequence[] items = {" ReliefWeb "," UN "," FCO "," CDC "};
        final ArrayList<Integer> selectedItems = new ArrayList<>();
        List<Registry> selections;
        try {
            selections = getDaoRegistry().queryForEq(Registry.FIELD_NAME, "feed_sources");
            for (int i = 0; i < items.length; i++) {
                for (Registry reg : selections) {
                    if (reg.getValue().equals(String.valueOf(i))) {
                        selectedItems.add(i);
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            if (BuildConfig.BUILD_TYPE.equals("debug"))
                Log.getStackTraceString(e.getCause());
        }
        return selectedItems;
    }

    public String getSelectedFeedSourcesLabel() {
        String feedSourcesLabel = "";
        final CharSequence[] items = {" ReliefWeb ", " UN ", " FCO ", " CDC "};
        final List<Integer> selectedItems = getSelectedFeedSources();
        for (Integer selectedItem : selectedItems) {
            if (!selectedItem.equals(selectedItems.get(0))) feedSourcesLabel += "\n";
            feedSourcesLabel += " - "+items[selectedItem];
        }
        return feedSourcesLabel;
    }

    public String getChosenCountry() {
        String selectedCountry = "";
        Dao<Registry, String> regDao = getDaoRegistry();
        List<Registry> selCountry = null;
        try {
            selCountry = regDao.queryForEq(Registry.FIELD_NAME, "country");
        } catch (SQLException e) {
            if (BuildConfig.BUILD_TYPE.equals("debug"))
                Log.getStackTraceString(e.getCause());
        }
        if (selCountry != null && !selCountry.isEmpty()) {
            selectedCountry = selCountry.get(0).getValue();
        }
        return selectedCountry;
    }

    public int getRefreshValue() {
        int retInterval = 0;
        try {
            List<Registry> selInterval = getDaoRegistry().queryForEq(Registry.FIELD_NAME, "refresh_value");
            if (!selInterval.isEmpty()) {
                try {
                    retInterval = Integer.parseInt(selInterval.get(0).getValue());
                } catch (NumberFormatException nfe) {
                    if (BuildConfig.BUILD_TYPE.equals("debug"))
                        Log.getStackTraceString(nfe.getCause());
                }
            }
        } catch (SQLException e) {
            if (BuildConfig.BUILD_TYPE.equals("debug"))
                Log.getStackTraceString(e.getCause());
        }
        return retInterval;
    }

    public void setRefreshValue(int refreshValue) {
        try {
            List<Registry> selInterval = getDaoRegistry().queryForEq(Registry.FIELD_NAME, "refresh_value");
            if (!selInterval.isEmpty()) {
                selInterval.get(0).setValue(String.valueOf(refreshValue));
                getDaoRegistry().update(selInterval.get(0));
            } else {
                getDaoRegistry().create(new Registry("refresh_value", String.valueOf(refreshValue)));
            }
        } catch (SQLException e) {
            if (BuildConfig.BUILD_TYPE.equals("debug"))
                Log.getStackTraceString(e.getCause());
        }
    }


    public long getFeeditemsRefreshed() {
        return feeditemsRefreshed;
    }

    public void setFeeditemsRefreshed(long feeditemsRefreshed) {
        this.feeditemsRefreshed = feeditemsRefreshed;
    }

    public void exportDatabase() {
        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();

            if (sd.canWrite()) {
                String backupDBPath = OrmHelper.DATABASE_NAME;
                String currentDBPath = "//data//"+getPackageName()+"//databases//"+backupDBPath+"";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            Log.e("can't write", e.getLocalizedMessage());
        }
    }



    public void createDatabaseIfNotExists() {
        File destFile = getApplicationContext().getDatabasePath(OrmHelper.DATABASE_NAME);
        if (!destFile.exists()) {
            try {
                copyDataBase(destFile);
            } catch (IOException e) {
                if (BuildConfig.BUILD_TYPE.equals("debug"))
                    Log.getStackTraceString(e.getCause());
            }
        }
    }

    private void copyDataBase(File destFile) throws IOException {
        destFile.getParentFile().mkdirs();
        InputStream externalDbStream = getAssets().open(OrmHelper.DATABASE_NAME);
        String outFileName = destFile.getPath();
        OutputStream localDbStream = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = externalDbStream.read(buffer)) > 0) {
            localDbStream.write(buffer, 0, bytesRead);
        }
        localDbStream.close();
        externalDbStream.close();
    }

    public static boolean deleteDatabase(File file) {
        if (file == null) {
            throw new IllegalArgumentException("file must not be null");
        }

        boolean deleted = file.delete();
        deleted |= new File(file.getPath() + "-journal").delete();
        deleted |= new File(file.getPath() + "-shm").delete();
        deleted |= new File(file.getPath() + "-wal").delete();

        File dir = file.getParentFile();
        if (dir != null) {
            final String prefix = file.getName() + "-mj";
            final FileFilter filter = new FileFilter() {
                @Override
                public boolean accept(File candidate) {
                    return candidate.getName().startsWith(prefix);
                }
            };
            for (File masterJournal : dir.listFiles(filter)) {
                deleted |= masterJournal.delete();
            }
        }
        return deleted;
    }

    public void removeSharedPreferences() {
        File sharedPreferenceFile = new File(getFilesDir().getPath().replaceFirst("/files$", "/shared_prefs/"));
        File[] listFiles = sharedPreferenceFile.listFiles();
        for (File file : listFiles) {
            file.delete();
        }
    }

    public ArrayList<FeedSource> getFeedSourcesList() {
        ArrayList<FeedSource> sourcesList = new ArrayList<>();
        sourcesList.add(new FeedSource("UN / ReliefWeb", 0));
        sourcesList.add(new FeedSource("CDC", 3));
        return sourcesList;
    }

    public CharSequence[] getFeedSourcesArray() {
        List<FeedSource> feedSources = getFeedSourcesList();
        List<String> sourcesList = new ArrayList<>();
        for (FeedSource source : feedSources) {
            sourcesList.add(source.getName());
        }
        return sourcesList.toArray(new CharSequence[sourcesList.size()]);
    }

    public int getFeedSourceCodeByIndex(int index) {
        List<FeedSource> feedSources = getFeedSourcesList();
        if (index < feedSources.size()) {
            return feedSources.get(index).getCode();
        }
        return -1;
    }

    public void closeDbAndDAOs() {
        getOrmHelper().close();
        daoSegment = null;
        daoCheckItem = null;
        daoCategory = null;
        daoRegistry = null;
        daoFavourite = null;
        daoDifficulty = null;
    }
}