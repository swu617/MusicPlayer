/*
 * Created by i301487 on 2015/9/1
 * Copyright (c) 2015 SAP. All rights reserved.
 */

package com.sam.music.player.db;

import android.content.Context;
import android.net.Uri;


import com.sam.music.player.RHSApp;
import com.sam.music.player.db.provider.Provider;
import com.sam.music.player.utils.RHSLog;
import com.sam.music.player.utils.SecurityUtils;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.io.File;
import java.util.HashMap;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String AUTHORITY = "com.sam.music.player.db.RHSProvider";
    public static final String DATABASE_NAME = "RHS.db";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final int VER_INITIAL = 1; //Initial version for internal use.
    private static final int DB_VERSION = VER_INITIAL;
    private HashMap<String, Provider> providerMap;

    public DatabaseHelper(Context context, HashMap<String, Provider> providerMap) {
        super(context, DATABASE_NAME, null, DB_VERSION);
        this.providerMap = providerMap;
    }

    public static SQLiteDatabase getWritableDatabase(SQLiteOpenHelper openHelper, String databaseName) {

        String databaseKey = getDatabaseKey();
        try {
            return openHelper.getWritableDatabase(databaseKey);
        } catch (SQLiteException exception) {
            RHSLog.i(exception.getLocalizedMessage());
            return null;
        }
    }

    public static String getDatabaseKey() {
        String key = ""; //default value before getting real key
        String appSG = SecurityUtils.getAppSignature();

        if (appSG != null && appSG.length() > 0) {
            key = String.valueOf(appSG.hashCode());
        }

        return key;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        for (Provider provider : providerMap.values()) {
            provider.onCreate(db);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int version = oldVersion;
        switch (oldVersion) {
            case VER_INITIAL:
//                new IdeaProvider(context).resetContents(db);
//                new GroupDetailProvider(context).resetContents(db);
//            case VER_1505_PRE_RELEASE:
//            case VER_1508_RELEASE:
//            case VER_1511_RELEASE:
//                new GroupDetailProvider(context).resetContents(db);
//            case VER_1602_RELEASE:
//                version = VER_1602_RELEASE;
                break;
        }

        if (version != DB_VERSION) {
            for (Provider provider : providerMap.values()) {
                provider.resetContents(db);
            }
        }

        return;
    }

    public SQLiteDatabase getWritableDatabase() {
        return getWritableDatabase(this, DATABASE_NAME);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Future-proofing; rolling back to from a newer version should blow away the database
        for (Provider provider : providerMap.values()) {
            provider.resetContents(db);
        }
    }

    public void put(String key, Provider provider) {
        providerMap.put(key, provider);
    }

    public void deleteDB(String databaseName) {
        File originalFile = RHSApp.getAppContext().getDatabasePath(databaseName);
        if (originalFile.exists()) {
            originalFile.delete();
        }
    }
}