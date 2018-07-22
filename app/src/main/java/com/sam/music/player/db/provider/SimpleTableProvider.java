/*
 * Created by I310736 on 2014/10/15
 * Copyright (c) 2014 SAP. All rights reserved.
 */

package com.sam.music.player.db.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.sam.music.player.db.ConflictType;
import com.sam.music.player.db.RHSStorage;
import com.sam.music.player.db.models.ODataItem;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteQueryBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

abstract public class SimpleTableProvider implements Provider {
    protected Context mContext;
    protected SQLiteDatabase mDatabase;

    protected SimpleTableProvider(Context context) {
        mContext = context;
    }

    protected String getCreateTableSql(Class<?> c) {
        StringBuilder sb = new StringBuilder();

        sb.append("CREATE TABLE ").append(getTableName()).append(" (");
        sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY, ");

        for (Field field : c.getDeclaredFields()) {
            if (field.isAnnotationPresent(ODataItem.Column.class)) {
                ODataItem.Column annotation = field.getAnnotation(ODataItem.Column.class);
                String dbType = getDbType(field.getType());
                sb.append(annotation.value()).append(" ").append(dbType).append(", ");
            } else if (field.isAnnotationPresent(ODataItem.SubModule.class)) {
                Class<?> superClass = field.getType().getSuperclass();
                if (superClass != null && superClass.toString().equals(ODataItem.class.toString())) {
                    ODataItem.SubModule annotation = field.getAnnotation(ODataItem.SubModule.class);
                    String[] subFields = annotation.fields();
                    String[] subColumns = annotation.columns();
                    sb.append(subColumnsSql(field.getType(), subFields, subColumns));

                }
            }
        }

        if (getUniqueColumns() != null) {
            sb.append(" UNIQUE (").append(getUniqueColumns()).append(") ").append(getConflictString()).append(")");
        } else {
            int lastComma = sb.lastIndexOf(",");    // there's a dangling comma at the end. delete and close the table
            sb.deleteCharAt(lastComma);
            sb.append(")");
        }

        return sb.toString();
    }

    private String subColumnsSql(Class<?> c, String[] subFields, String[] subColumns) {
        StringBuilder sb = new StringBuilder();
        for (Field field : c.getDeclaredFields()) {

            if (field.isAnnotationPresent(ODataItem.JsonProperty.class)) {
                String jsonFieldName = field.getAnnotation(ODataItem.JsonProperty.class).value();

                String dbColumnName = getColumnName(jsonFieldName, subFields, subColumns);
                if (dbColumnName != null) {
                    String dbType = getDbType(field.getType());
                    sb.append(dbColumnName).append(" ").append(dbType).append(", ");
                }
            }
        }
        return sb.toString();
    }

    private String getDbType(Type fieldType) {
        String dbType = "";
        if (fieldType.toString().equals(String.class.toString()) || fieldType.toString().equals(Object.class.toString())) {
            dbType = "VARCHAR";
        } else if (fieldType.toString().equals(int.class.toString()) || fieldType.toString().equals(long.class.toString()) || fieldType.toString().equals(boolean.class.toString())) {
            dbType = "INTEGER";
        }
        return dbType;
    }

    public abstract String getTableName();

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        resetContents(db);
    }

    @Override
    public void resetContents(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + getTableName());
        onCreate(db);
    }

    public void setDBHandle(SQLiteDatabase database) {
        mDatabase = database;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        String tableName = getTableName();

        SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
        qBuilder.setTables(tableName);

        Cursor c = qBuilder.query(mDatabase, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(mContext.getContentResolver(), uri);

        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String tableName = getTableName();

        long rowId = mDatabase.insertOrThrow(tableName, null, values);
        notifyChange(uri);

        return getBaseContentUri().buildUpon().appendPath(tableName).appendPath(Long.toString(rowId)).build();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String tableName = getTableName();

        int count = mDatabase.update(tableName, values, selection, selectionArgs);
        notifyChange(uri);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String tableName = getTableName();

        int count = mDatabase.delete(tableName, selection, selectionArgs);
        notifyChange(uri);
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] valuesArray) {
        // We need to override this to ensure only a single notification is fired!
        // The default behavior calls insert N times which would trigger N notifications!
        String tableName = getTableName();

        mDatabase.beginTransaction();
        try {
            for (ContentValues values : valuesArray) {
                mDatabase.insertOrThrow(tableName, null, values);
            }

            mDatabase.setTransactionSuccessful();
        } finally {
            mDatabase.endTransaction();
        }

        notifyChange(uri);

        return valuesArray.length;
    }

    protected String getUniqueColumns() {
        return null;
    }

    protected String getConflictString() {
        return ConflictType.getConflictValue(ConflictType.CONFLICT_REPLACE);
    }

    /**
     * @return the base content uri for the associated ContentProvider
     */
    protected Uri getBaseContentUri() {
        return RHSStorage.CONTENT_URI;
    }

    private void notifyChange(Uri uri) {
        mContext.getContentResolver().notifyChange(uri, null, false);
    }

    public static String getColumnName(String jsonFieldName, String[] jsonFields, String[] dbColumns) {
        if (jsonFields.length != dbColumns.length) {
            return null;
        }
        for (int i = 0; i < jsonFields.length; i++) {
            if (jsonFieldName.equals(jsonFields[i])) {
                return dbColumns[i];
            }
        }
        return null;
    }
}
