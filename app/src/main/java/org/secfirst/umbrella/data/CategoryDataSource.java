package org.secfirst.umbrella.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.secfirst.umbrella.models.Category;

import java.util.ArrayList;

public class CategoryDataSource {

    private SQLiteDatabase database;
    private UmbrellaSQLiteHelper dbHelper;
    private String[] allColumns = { UmbrellaSQLiteHelper.COLUMN_CATEGORY_ID, UmbrellaSQLiteHelper.COLUMN_CATEGORY_PARENT, UmbrellaSQLiteHelper.COLUMN_CATEGORY };

    public CategoryDataSource(Context context) {
        dbHelper = new UmbrellaSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Category createItem(int parent, String category) {
        ContentValues values = new ContentValues();
        values.put(UmbrellaSQLiteHelper.COLUMN_CATEGORY_PARENT, parent);
        values.put(UmbrellaSQLiteHelper.COLUMN_CATEGORY_CATEGORY, category);
        long insertId = database.insert(UmbrellaSQLiteHelper.TABLE_CATEGORIES, null,
                values);
        Log.i("insert id", String.valueOf(insertId));
        Cursor cursor = database.query(UmbrellaSQLiteHelper.TABLE_CATEGORIES,
                allColumns, UmbrellaSQLiteHelper.COLUMN_CATEGORY_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Category newItem = cursorToCategory(cursor);
        cursor.close();
        return newItem;
    }

    public Category insertItem(Category item) {
        return createItem(item.getParent(), item.getCategory());
    }

    public void deleteItem(Category item) {
        long id = item.getId();
        database.delete(UmbrellaSQLiteHelper.TABLE_CATEGORIES, UmbrellaSQLiteHelper.COLUMN_CATEGORY_ID
                + " = " + id, null);
    }

    public void deleteAllItems() {
        database.delete(UmbrellaSQLiteHelper.TABLE_CATEGORIES, UmbrellaSQLiteHelper.COLUMN_CATEGORY_ID
                + " > " + 0, null);
    }

    public Category getItemById(int itemNumber) {
        Cursor cursor = null;
        Category category = new Category();
        try{
            cursor = database.rawQuery("SELECT * FROM "+UmbrellaSQLiteHelper.TABLE_CATEGORIES+" WHERE "+UmbrellaSQLiteHelper.COLUMN_CATEGORY_ID+"=?", new String[] {itemNumber + ""});
            if(cursor.getCount() > 0) {
                cursor.moveToFirst();
                category = cursorToCategory(cursor);
            }
            return category;
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public ArrayList<Category> getAllItems() {
        ArrayList<Category> categories = new ArrayList<Category>();
        Cursor cursor = database.query(UmbrellaSQLiteHelper.TABLE_CATEGORIES,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Category category = cursorToCategory(cursor);
            categories.add(category);
            cursor.moveToNext();
        }
        cursor.close();
        return categories;
    }

    private Category cursorToCategory(Cursor cursor) {
        Category item = new Category();
        item.setId(cursor.getInt(0));
        item.setParent(cursor.getInt(1));
        item.setCategory(cursor.getString(2));
        return item;
    }

    public void dropTable() {
        database.execSQL("DROP TABLE IF EXISTS " + UmbrellaSQLiteHelper.TABLE_CATEGORIES);
    }

    public void createTable() {
        database.execSQL(UmbrellaSQLiteHelper.DATABASE_CREATE3);
    }

}
