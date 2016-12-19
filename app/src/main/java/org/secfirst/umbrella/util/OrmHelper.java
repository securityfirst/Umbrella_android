package org.secfirst.umbrella.util;

import android.content.Context;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import net.sqlcipher.database.SQLiteDatabase;

import org.secfirst.umbrella.R;
import org.secfirst.umbrella.models.Category;
import org.secfirst.umbrella.models.CheckItem;
import org.secfirst.umbrella.models.Difficulty;
import org.secfirst.umbrella.models.Favourite;
import org.secfirst.umbrella.models.FeedItem;
import org.secfirst.umbrella.models.FeedSource;
import org.secfirst.umbrella.models.Language;
import org.secfirst.umbrella.models.Registry;
import org.secfirst.umbrella.models.Segment;

import java.sql.SQLException;
import java.util.List;

import timber.log.Timber;

public class OrmHelper extends OrmLiteSqliteOpenHelper {
    public static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 5;
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
            while (++oldVersion <= newVersion && oldVersion>1) {
                UpgradeHelper.addUpgrade(oldVersion);
            }
            final List<String> availableUpdates = UpgradeHelper.availableUpdates(this.context.getResources());

            for (final String statement : availableUpdates) {
                database.beginTransaction();
                try {
                    database.execSQL(statement);
                    database.setTransactionSuccessful();
                } finally {
                    database.endTransaction();
                }
            }
        }
        catch (final Exception e) {
            Timber.e(e);
            onCreate(database, connectionSource);
        }
    }

    @Override
    public void close() {
        super.close();
    }

    public void createTables(ConnectionSource source) {
        try {
            TableUtils.createTableIfNotExists(source, Segment.class);
        } catch (SQLException e) {
            Timber.e(e);
        }
        try {
            TableUtils.createTableIfNotExists(source, CheckItem.class);
        } catch (SQLException e) {
            Timber.e(e);
        }
        try {
            TableUtils.createTableIfNotExists(source, Category.class);
        } catch (SQLException e) {
            Timber.e(e);
        }
        try {
            TableUtils.createTableIfNotExists(source, Registry.class);
        } catch (SQLException e) {
            Timber.e(e);
        }
        try {
            TableUtils.createTableIfNotExists(source, Favourite.class);
        } catch (SQLException e) {
            Timber.e(e);
        }
        try {
            TableUtils.createTableIfNotExists(source, Difficulty.class);
        } catch (SQLException e) {
            Timber.e(e);
        }
        try {
            TableUtils.createTableIfNotExists(source, Language.class);
        } catch (SQLException e) {
            Timber.e(e);
        }
        try {
            TableUtils.createTableIfNotExists(source, FeedItem.class);
        } catch (SQLException e) {
            Timber.e(e);
        }
        try {
            TableUtils.createTableIfNotExists(source, FeedSource.class);
        } catch (SQLException e) {
            Timber.e(e);
        }
    }
}
