
package com.sam.music.player.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.sam.music.player.db.models.ODataItem;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * Generic class for parsing and saving data from database.
 *
 */
public class DBParser {

    private static final String TAG = "DBParser";

    //TODO:this method has been tested with complex data type,
    public static Object getJamOData(Cursor cursor, ODataItem oDataItem) {
        Class<?> c = oDataItem.getClass();

        for (Field field : c.getDeclaredFields()) {
            try {
                if (field.isAnnotationPresent(ODataItem.Column.class)) {
                    String dbColumnName = field.getAnnotation(ODataItem.Column.class).value();
                    setSingleFieldValue(cursor, field, dbColumnName, oDataItem);
                } else {
                    if (field.isAnnotationPresent(ODataItem.SubModule.class)) {
                        Class<?> superClass = field.getType().getSuperclass();
                        if (superClass != null && superClass.toString().equals(ODataItem.class.toString())) {
                            ODataItem.SubModule annotation = field.getAnnotation(ODataItem.SubModule.class);
                            String[] subFields = annotation.fields();
                            String[] subColumns = annotation.columns();
                            setSubOData(cursor, (ODataItem) field.get(oDataItem), subFields, subColumns);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                RHSLog.d(TAG, "Error:" + e.getLocalizedMessage());
            }
        }

        return oDataItem;
    }

    private static void setSubOData(Cursor cursor, ODataItem oDataItem, String[] subFields, String[] subColumns) throws IllegalAccessException {
        Class<?> c = oDataItem.getClass();
        for (Field field : c.getDeclaredFields()) {
            if (field.isAnnotationPresent(ODataItem.JsonProperty.class)) {
                String jsonFieldName = field.getAnnotation(ODataItem.JsonProperty.class).value();
                String dbColumnName = getColumnName(jsonFieldName, subFields, subColumns);
                if (dbColumnName != null) {
                    setSingleFieldValue(cursor, field, dbColumnName, oDataItem);
                }
            }
        }
    }

    //TODO:this method has been verified with complex data type.
    public static void saveJamData(Context context, final ODataItem oDataItem, Uri uri) {

        int nSize = oDataItem.itemList.size();
        if (nSize > 0) {
            for (int index = 0; index < nSize; index++) {
                ODataItem oDataSubItem = (ODataItem) oDataItem.itemList.get(index);
                saveJamData(context, oDataSubItem, uri);
            }
        } else {

            ContentResolver contentResolver = context.getContentResolver();
            ContentValues value = populateValues(oDataItem);

            if (value.size() > 0) {
                contentResolver.insert(uri, value);
            }
        }
    }

    public static ContentValues populateValues(ODataItem oDataItem) {
        Class<?> c = oDataItem.getClass();

        ContentValues values = new ContentValues();
        for (Field field : c.getDeclaredFields()) {
            try {

                boolean existInDbCols = field.isAnnotationPresent(ODataItem.Column.class);
                if (existInDbCols) {
                    String dbColumnName = field.getAnnotation(ODataItem.Column.class).value();
                    values.putAll(getSingleFieldValue(dbColumnName, field, oDataItem));
                } else if (field.isAnnotationPresent(ODataItem.SubModule.class)) {
                    Class<?> superClass = field.getType().getSuperclass();
                    if (superClass != null && superClass.toString().equals(ODataItem.class.toString())) {
                        ODataItem.SubModule annotation = field.getAnnotation(ODataItem.SubModule.class);
                        String[] subFields = annotation.fields();
                        String[] subColumns = annotation.columns();
                        ContentValues subValues = populateSubValues((ODataItem) field.get(oDataItem), subFields, subColumns);
                        values.putAll(subValues);
                    }
                }
            } catch (IllegalAccessException e) {
                RHSLog.d(TAG, "Error:" + e.getLocalizedMessage());
            }
        }

        return values;
    }

    private static ContentValues populateSubValues(ODataItem oDataItem, String[] subFields, String[] subColumns) throws IllegalAccessException {
        Class<?> c = oDataItem.getClass();

        ContentValues values = new ContentValues();
        for (Field field : c.getDeclaredFields()) {
            if (field.isAnnotationPresent(ODataItem.JsonProperty.class)) {
                String jsonFieldName = field.getAnnotation(ODataItem.JsonProperty.class).value();
                String dbColumnName = getColumnName(jsonFieldName, subFields, subColumns);
                if (dbColumnName != null) {
                    values.putAll(getSingleFieldValue(dbColumnName, field, oDataItem));
                }
            }
        }

        return values;
    }

    private static void setSingleFieldValue(Cursor cursor, Field field, String name, ODataItem oDataItem) throws IllegalAccessException {
        Class<?> type = field.getType();
        int index;
        if (type.toString().equals(String.class.toString()) || type.toString().equals(Object.class.toString())) {
            index = cursor.getColumnIndex(name);
            if (index != -1) {
                field.set(oDataItem, cursor.getString(index));
            }
        } else if (type.toString().equals(int.class.toString())) {
            index = cursor.getColumnIndex(name);
            if (index != -1) {
                field.set(oDataItem, cursor.getInt(index));
            }
        } else if (type.toString().equals(boolean.class.toString())) {
            index = cursor.getColumnIndex(name);
            if (index != -1) {
                field.set(oDataItem, (cursor.getInt(index) == 1));
            }
        } else if (type.toString().equals(long.class.toString())) {
            index = cursor.getColumnIndex(name);
            if (index != -1) {
                field.set(oDataItem, cursor.getLong(index));
            }
        } else if (type.toString().equals(Date.class.toString())) {
            index = cursor.getColumnIndex(name);
            if (index != -1) {
                field.set(oDataItem, new Date(cursor.getLong(index)));
            }
        }
    }

    private static ContentValues getSingleFieldValue(String colmnName, Field field, ODataItem oDataItem) throws IllegalAccessException {
        ContentValues singleValue = new ContentValues();
        Class<?> fieldType = field.getType();
        if (fieldType.toString().equals(String.class.toString())) {
            singleValue.put(colmnName, (String) field.get(oDataItem));
        } else if (fieldType.toString().equals(int.class.toString())) {
            singleValue.put(colmnName, field.getInt(oDataItem));
        } else if (fieldType.toString().equals(boolean.class.toString())) {
            singleValue.put(colmnName, field.getBoolean(oDataItem));
        } else if (fieldType.toString().equals(long.class.toString())) {
            singleValue.put(colmnName, field.getLong(oDataItem));
        } else if (fieldType.toString().equals(Date.class.toString())) {
            singleValue.put(colmnName, ((Date) field.get(oDataItem)).getTime());
        } else if (fieldType.toString().equals(Object.class.toString())) {
            Object obj = field.get(oDataItem);
            if (obj != null) {
                singleValue.put(colmnName, obj.toString());
            }
        }
        return singleValue;
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
