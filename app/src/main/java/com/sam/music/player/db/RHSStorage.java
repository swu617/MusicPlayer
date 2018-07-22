/*
 * Created by I310736 on 2014/10/30
 * Copyright (c) 2014 SAP. All rights reserved.
 */

package com.sam.music.player.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;


import com.sam.music.player.db.provider.AlbumProvider;
import com.sam.music.player.db.provider.Provider;
import com.sam.music.player.db.provider.SongsProvider;

import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteDatabase;

import java.util.HashMap;

public class RHSStorage extends ContentProvider {

    public static final String AUTHORITY = "com.sam.music.player.db.RHSProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    private static final String TAG = "RHSProvider";
    private static RHSStorage instance;
    public DatabaseHelper mHelper;
    private HashMap<String, Provider> providerMap = new HashMap<>();

    public static RHSStorage getInstance() {
        return instance;
    }

    public static void clear() {
        // clear all the tables
        instance.mHelper.deleteDB(DatabaseHelper.DATABASE_NAME);
        instance.mHelper = null;
    }

    @Override
    public boolean onCreate() {
        instance = this;

        // Dbiollo - reset the database on every startup so that we don't have to deal with migrations yet.
        //RHSStorage.clear();

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return getProvider(uri).query(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        return getProvider(uri).insert(uri, initialValues);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int numInserted = 0;
        SQLiteDatabase sqlDB = instance.mHelper.getWritableDatabase();
        String tableName = getProvider(uri).getTableName();

        sqlDB.beginTransaction();
        try {
            for (ContentValues cv : values) {
                long newID = sqlDB.insertOrThrow(tableName, null, cv);
                if (newID <= 0) {
                    throw new SQLException("Failed to insert row into " + uri);
                }
            }
            sqlDB.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(uri, null);
            numInserted = values.length;
        } finally {
            sqlDB.endTransaction();
        }
        return numInserted;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        return getProvider(uri).delete(uri, where, whereArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        return getProvider(uri).update(uri, values, where, whereArgs);
    }

    public void init() {

        if (mHelper == null) {
            mHelper = new DatabaseHelper(getContext(), providerMap);

            //add all the providers into the map
            mHelper.put(SongsProvider.TABLE_NAME, new SongsProvider(getContext()));
            mHelper.put(AlbumProvider.TABLE_NAME, new AlbumProvider(getContext()));


            SQLiteDatabase.loadLibs(getContext());
            SQLiteDatabase writableDb = mHelper.getWritableDatabase();
            for (Provider provider : providerMap.values()) {
                provider.setDBHandle(writableDb);
            }
        }
    }

    private Provider getProvider(Uri uri) {
        Provider provider = providerMap.get((uri.getPathSegments()).get(0));
        if (provider == null) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return provider;
    }
}
