package org.secfirst.umbrella.util;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import net.sqlcipher.database.SQLiteDatabase;

import org.secfirst.umbrella.BuildConfig;
import org.secfirst.umbrella.R;
import org.secfirst.umbrella.models.Category;
import org.secfirst.umbrella.models.CheckItem;
import org.secfirst.umbrella.models.Difficulty;
import org.secfirst.umbrella.models.Favourite;
import org.secfirst.umbrella.models.Registry;
import org.secfirst.umbrella.models.Segment;

import java.sql.SQLException;
import java.util.List;

public class OrmHelper extends OrmLiteSqliteOpenHelper {
    public static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 2;
    private Context context;

    public OrmHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, context.getString(R.string.default_db_password));
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource source) {
        createTables(source);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource source, int oldVersion, int newVersion) {
        try {
            while (++oldVersion <= newVersion) {
                switch (oldVersion) {
                    case 2: {
                        UpgradeHelper.addUpgrade(2);
                        break;
                    }
                }
            }
            final List<String> availableUpdates = UpgradeHelper.availableUpdates(this.context.getResources());

            for (final String statement : availableUpdates) {
                database.beginTransaction();
                try {
                    database.execSQL(statement);
                    database.setTransactionSuccessful();
                }
                finally {
                    database.endTransaction();
                }
            }
        }
        catch (final Exception e) {
            if (BuildConfig.BUILD_TYPE.equals("debug"))
                Log.getStackTraceString(e.getCause());
            onCreate(database, connectionSource);
        }
    }

    @Override
    public void close() {
        super.close();
    }

    public void createTables(ConnectionSource source) {
        try {
            TableUtils.createTable(source, Segment.class);
        } catch (SQLException e) {
            if (BuildConfig.BUILD_TYPE.equals("debug"))
                Log.getStackTraceString(e.getCause());
        }
        try {
            TableUtils.createTable(source, CheckItem.class);
        } catch (SQLException e) {
            if (BuildConfig.BUILD_TYPE.equals("debug"))
                Log.getStackTraceString(e.getCause());
        }
        try {
            TableUtils.createTable(source, Category.class);
        } catch (SQLException e) {
            if (BuildConfig.BUILD_TYPE.equals("debug"))
                Log.getStackTraceString(e.getCause());
        }
        try {
            TableUtils.createTable(source, Registry.class);
        } catch (SQLException e) {
            if (BuildConfig.BUILD_TYPE.equals("debug"))
                Log.getStackTraceString(e.getCause());
        }
        try {
            TableUtils.createTable(source, Favourite.class);
        } catch (SQLException e) {
            if (BuildConfig.BUILD_TYPE.equals("debug"))
                Log.getStackTraceString(e.getCause());
        }
        try {
            TableUtils.createTable(source, Difficulty.class);
        } catch (SQLException e) {
            if (BuildConfig.BUILD_TYPE.equals("debug"))
                Log.getStackTraceString(e.getCause());
        }
    }
}
