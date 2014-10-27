package org.secfirst.umbrella.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import org.secfirst.umbrella.models.Segment;

import java.util.ArrayList;

public class SegmentsDataSource {

    private SQLiteDatabase database;
    private UmbrellaSQLiteHelper dbHelper;
    private String[] allColumns = { UmbrellaSQLiteHelper.COLUMN_ID, UmbrellaSQLiteHelper.COLUMN_TITLE, UmbrellaSQLiteHelper.COLUMN_SUBTITLE, UmbrellaSQLiteHelper.COLUMN_BODY, UmbrellaSQLiteHelper.COLUMN_CATEGORY };

    public SegmentsDataSource(Context context) {
        dbHelper = new UmbrellaSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Segment createSegment(String title, String subtitle, String body, int category) {
        ContentValues values = new ContentValues();
        values.put(UmbrellaSQLiteHelper.COLUMN_TITLE, title);
        values.put(UmbrellaSQLiteHelper.COLUMN_SUBTITLE, subtitle);
        values.put(UmbrellaSQLiteHelper.COLUMN_BODY, body);
        values.put(UmbrellaSQLiteHelper.COLUMN_CATEGORY, category);
        long insertId = database.insert(UmbrellaSQLiteHelper.TABLE_SEGMENTS, null,
                values);
        Cursor cursor = database.query(UmbrellaSQLiteHelper.TABLE_SEGMENTS,
                allColumns, UmbrellaSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Segment newSegment = cursorToSegment(cursor);
        cursor.close();
        return newSegment;
    }

    public Segment insertSegment(Segment segment) {
        return createSegment(segment.getTitle(), segment.getSubtitle(), segment.getBody(), segment.getCategory());
    }

    public void deleteSegment(Segment segment) {
        long id = segment.getId();
        System.out.println("Segment deleted with id: " + id);
        database.delete(UmbrellaSQLiteHelper.TABLE_SEGMENTS, UmbrellaSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public void deleteAllSegments() {
        database.delete(UmbrellaSQLiteHelper.TABLE_SEGMENTS, UmbrellaSQLiteHelper.COLUMN_ID
                + " > " + 0, null);
    }

    public Segment getSegmentById(int segmentNumber) {
        Cursor cursor = null;
        Segment segment = new Segment();
        try{
            cursor = database.rawQuery("SELECT * FROM "+UmbrellaSQLiteHelper.TABLE_SEGMENTS+" WHERE "+UmbrellaSQLiteHelper.COLUMN_ID+"=?", new String[] {segmentNumber + ""});

            if(cursor.getCount() > 0) {

                cursor.moveToFirst();
                segment = cursorToSegment(cursor);
            }
            return segment;
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public ArrayList<Segment> getAllSegments() {
        ArrayList<Segment> segments = new ArrayList<Segment>();

        Cursor cursor = database.query(UmbrellaSQLiteHelper.TABLE_SEGMENTS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Segment comment = cursorToSegment(cursor);
            segments.add(comment);
            cursor.moveToNext();
        }
        cursor.close();
        return segments;
    }

    public ArrayList<Segment> getAllSegmentsByCategory(int category) {
        ArrayList<Segment> segments = new ArrayList<Segment>();

        Cursor cursor = database.query(UmbrellaSQLiteHelper.TABLE_SEGMENTS,
                allColumns, UmbrellaSQLiteHelper.COLUMN_CATEGORY + "=?", new String[] {String.valueOf(category)}, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Segment segment = cursorToSegment(cursor);
            segments.add(segment);
            cursor.moveToNext();
        }
        cursor.close();
        return segments;
    }

    private Segment cursorToSegment(Cursor cursor) {
        Segment segment = new Segment();
        segment.setId(cursor.getLong(0));
        segment.setTitle(cursor.getString(1));
        segment.setSubtitle(cursor.getString(2));
        segment.setBody(cursor.getString(3));
        segment.setCategory(cursor.getInt(4));
        return segment;
    }
}
