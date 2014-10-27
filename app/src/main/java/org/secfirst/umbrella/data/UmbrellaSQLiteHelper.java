package org.secfirst.umbrella.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UmbrellaSQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_SEGMENTS = "segments";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_SUBTITLE = "subtitle";
    public static final String COLUMN_BODY = "body";
    public static final String COLUMN_CATEGORY = "category";

    public static final String TABLE_CHECK_ITEMS = "check_items";
    public static final String COLUMN_CHECK_LIST_ID = "_id";
    public static final String COLUMN_CHECK_LIST_TITLE = "title";
    public static final String COLUMN_CHECK_LIST_TEXT = "text";
    public static final String COLUMN_CHECK_LIST_CHECKED = "checked";
    public static final String COLUMN_CHECK_LIST_PARENT = "parent";
    public static final String COLUMN_CHECK_LIST_CATEGORY = "category";


    private static final String DATABASE_NAME = "content.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table "
            + TABLE_SEGMENTS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TITLE
            + " text not null, " + COLUMN_SUBTITLE
            + " text not null, " + COLUMN_BODY
            + " text not null, " + COLUMN_CATEGORY+ " integer not null);";

    private static final String DATABASE_CREATE2 = "create table "
            + TABLE_CHECK_ITEMS + "(" + COLUMN_CHECK_LIST_ID
            + " integer primary key autoincrement, " + COLUMN_CHECK_LIST_TITLE
            + " text not null, " + COLUMN_CHECK_LIST_TEXT
            + " text not null, " + COLUMN_CHECK_LIST_CHECKED
            + " integer not null, " + COLUMN_CHECK_LIST_PARENT
            + " integer not null, " + COLUMN_CHECK_LIST_CATEGORY+ " integer not null);";

    public UmbrellaSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
        database.execSQL(DATABASE_CREATE2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(UmbrellaSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEGMENTS);
        onCreate(db);
    }

}