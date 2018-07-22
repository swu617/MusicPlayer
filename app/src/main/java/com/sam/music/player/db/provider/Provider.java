/*
 * Created by I310736 on 2014/10/15
 * Copyright (c) 2014 SAP. All rights reserved.
 */

package com.sam.music.player.db.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import net.sqlcipher.database.SQLiteDatabase;

/**
 * Created by I310736 on 10/15/2014.
 */

//the interface for simpletableprovider
public interface Provider extends BaseColumns {
    /**
     * @return the fileName of the database table
     */
    String getTableName();

    void onCreate(SQLiteDatabase db);

    void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    void resetContents(SQLiteDatabase db);

    void setDBHandle(SQLiteDatabase database);

    Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder);

    Uri insert(Uri uri, ContentValues values);

    int update(Uri uri, ContentValues values, String where, String[] whereArgs);

    int delete(Uri uri, String selection, String[] selectionArgs);

    int bulkInsert(Uri uri, ContentValues[] valuesArray);
}
