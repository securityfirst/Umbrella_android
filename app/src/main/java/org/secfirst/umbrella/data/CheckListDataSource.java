package org.secfirst.umbrella.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.secfirst.umbrella.models.CheckItem;

import java.util.ArrayList;

public class CheckListDataSource {

    private SQLiteDatabase database;
    private UmbrellaSQLiteHelper dbHelper;
    private String[] allColumns = { UmbrellaSQLiteHelper.COLUMN_CHECK_LIST_ID, UmbrellaSQLiteHelper.COLUMN_CHECK_LIST_TITLE, UmbrellaSQLiteHelper.COLUMN_CHECK_LIST_TEXT, UmbrellaSQLiteHelper.COLUMN_CHECK_LIST_CHECKED, UmbrellaSQLiteHelper.COLUMN_CHECK_LIST_PARENT, UmbrellaSQLiteHelper.COLUMN_CHECK_LIST_CATEGORY };

    public CheckListDataSource(Context context) {
        dbHelper = new UmbrellaSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public CheckItem createItem(String title, String text, int value, long parent, int category) {
        ContentValues values = new ContentValues();
        values.put(UmbrellaSQLiteHelper.COLUMN_CHECK_LIST_TITLE, title);
        values.put(UmbrellaSQLiteHelper.COLUMN_CHECK_LIST_TEXT, text);
        values.put(UmbrellaSQLiteHelper.COLUMN_CHECK_LIST_CHECKED, value);
        values.put(UmbrellaSQLiteHelper.COLUMN_CHECK_LIST_PARENT, parent);
        values.put(UmbrellaSQLiteHelper.COLUMN_CHECK_LIST_CATEGORY, category);
        long insertId = database.insert(UmbrellaSQLiteHelper.TABLE_CHECK_ITEMS, null,
                values);
        Log.i("insert id", String.valueOf(insertId));
        Cursor cursor = database.query(UmbrellaSQLiteHelper.TABLE_CHECK_ITEMS,
                allColumns, UmbrellaSQLiteHelper.COLUMN_CHECK_LIST_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        CheckItem newItem = cursorToCheckItem(cursor);
        cursor.close();
        return newItem;
    }

    public CheckItem insertItem(CheckItem item) {
        return createItem(item.getTitle(), item.getText(), item.getIntValue(), item.getParent(), item.getCategory());
    }

    public void deleteItem(CheckItem item) {
        long id = item.getId();
        database.delete(UmbrellaSQLiteHelper.TABLE_CHECK_ITEMS, UmbrellaSQLiteHelper.COLUMN_CHECK_LIST_ID
                + " = " + id, null);
    }

    public void deleteAllItems() {
        database.delete(UmbrellaSQLiteHelper.TABLE_CHECK_ITEMS, UmbrellaSQLiteHelper.COLUMN_CHECK_LIST_ID
                + " > " + 0, null);
    }

    public CheckItem getItemById(int itemNumber) {
        Cursor cursor = null;
        CheckItem checkItem = new CheckItem();
        try{
            cursor = database.rawQuery("SELECT * FROM "+UmbrellaSQLiteHelper.TABLE_CHECK_ITEMS+" WHERE "+UmbrellaSQLiteHelper.COLUMN_CHECK_LIST_ID+"=?", new String[] {itemNumber + ""});
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                checkItem = cursorToCheckItem(cursor);
            }
            return checkItem;
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void updateChecked(long itemId, int checked) {
        ContentValues cv = new ContentValues();
        cv.put(UmbrellaSQLiteHelper.COLUMN_CHECK_LIST_CHECKED, checked);
        database.update(UmbrellaSQLiteHelper.TABLE_CHECK_ITEMS, cv, UmbrellaSQLiteHelper.COLUMN_CHECK_LIST_ID + " = ?", new String[] {String.valueOf(itemId)});
    }

    public ArrayList<CheckItem> getAllItems() {
        ArrayList<CheckItem> checkItems = new ArrayList<CheckItem>();
        Cursor cursor = database.query(UmbrellaSQLiteHelper.TABLE_CHECK_ITEMS,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CheckItem checkItem = cursorToCheckItem(cursor);
            checkItems.add(checkItem);
            cursor.moveToNext();
        }
        cursor.close();
        return checkItems;
    }

    public ArrayList<CheckItem> getAllItemsByCategory(int category) {
        ArrayList<CheckItem> checkItems = new ArrayList<CheckItem>();
        Cursor cursor = database.query(UmbrellaSQLiteHelper.TABLE_CHECK_ITEMS,
                allColumns, UmbrellaSQLiteHelper.COLUMN_CHECK_LIST_CATEGORY + "=?", new String[] {String.valueOf(category)}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            CheckItem checkItem = cursorToCheckItem(cursor);
            checkItems.add(checkItem);
            cursor.moveToNext();
        }
        cursor.close();
        return checkItems;
    }

    private CheckItem cursorToCheckItem(Cursor cursor) {
        CheckItem item = new CheckItem();
        item.setId(cursor.getLong(0));
        item.setTitle(cursor.getString(1));
        item.setText(cursor.getString(2));
        item.setValue(cursor.getInt(3));
        item.setParent(cursor.getLong(4));
        item.setCategory(cursor.getInt(5));
        return item;
    }
}