package com.j256.ormlite.sqlcipher.android.compat;

import android.database.Cursor;

import net.sqlcipher.database.SQLiteDatabase;

/**
 * Basic class which provides no-op methods for all Android version.
 * 
 * @author graywatson
 */
public class BasicApiCompatibility implements ApiCompatibility {

	public Cursor rawQuery(SQLiteDatabase db, String sql, String[] selectionArgs, CancellationHook cancellationHook) {
		// NOTE: cancellationHook will always be null
		return db.rawQuery(sql, selectionArgs);
	}

	public CancellationHook createCancellationHook() {
		return null;
	}
}
