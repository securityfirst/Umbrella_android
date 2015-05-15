package org.secfirst.umbrella.util;

import android.content.Context;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import net.sqlcipher.database.SQLiteDatabase;

import org.secfirst.umbrella.models.Category;
import org.secfirst.umbrella.models.CheckItem;
import org.secfirst.umbrella.models.Difficulty;
import org.secfirst.umbrella.models.Favourite;
import org.secfirst.umbrella.models.Registry;
import org.secfirst.umbrella.models.Segment;

import java.sql.SQLException;

public class OrmHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "database.db";
    public static final String DATABASE_PASSWORD = "umbrella";
    private static final int DATABASE_VERSION = 1;

    public OrmHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, DATABASE_PASSWORD);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource source) {
        createTables(source);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource source, int oldVersion, int newVersion) {
        dropTables(source);
        createTables(source);
    }

    @Override
    public void close() {
        super.close();
    }

    public void recreateTables(ConnectionSource source) {
        try {
            TableUtils.dropTable(source, Segment.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils.dropTable(source, CheckItem.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils.dropTable(source, Registry.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils.dropTable(source, Favourite.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils.dropTable(source, Difficulty.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils.createTable(source, Segment.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils.createTable(source, CheckItem.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils.createTable(source, Registry.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils.createTable(source, Favourite.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils.createTable(source, Difficulty.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTables(ConnectionSource source) {
        try {
            TableUtils.createTable(source, Segment.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils.createTable(source, CheckItem.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils.createTable(source, Category.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils.createTable(source, Registry.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils.createTable(source, Favourite.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils.createTable(source, Difficulty.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dropTables(ConnectionSource source) {
        try {
            TableUtils.dropTable(source, Segment.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils.dropTable(source, CheckItem.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils.dropTable(source, Category.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils.dropTable(source, Registry.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils.dropTable(source, Favourite.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            TableUtils.dropTable(source, Difficulty.class, true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
