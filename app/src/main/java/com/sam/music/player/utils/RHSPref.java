package com.sam.music.player.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.sam.music.player.RHSApp;

/**
 * Created by i301487 on 1/4/16.
 */
public class RHSPref {

    public static void write(String key, Object value) {
        SharedPreferences sp = getSp();
        SharedPreferences.Editor editor = sp.edit();
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        }
        editor.apply();
    }

    public static void write(int keyRes, Object value) {
        String key = RHSApp.getAppContext().getResources().getString(keyRes);
        write(key, value);
    }

    public static String readString(String key) {
        return getSp().getString(key, "");
    }

    public static String readString(int keyRes) {
        String key = RHSApp.getAppContext().getResources().getString(keyRes);
        return readString(key);
    }

    public static boolean readBoolean(String key) {
        return getSp().getBoolean(key, false);
    }

    public static boolean readBoolean(int keyResId) {
        String key = RHSApp.getAppContext().getResources().getString(keyResId);
        return readBoolean(key);
    }

    public static int readInt(String key) {
        return getSp().getInt(key, -1);
    }

    public static int readInt(int keyResId) {
        String key = RHSApp.getAppContext().getResources().getString(keyResId);
        return readInt(key);
    }

    public static long readLong(String key) {
        return getSp().getLong(key, 0);
    }

    public static long readLong(int keyResId) {
        String key = RHSApp.getAppContext().getResources().getString(keyResId);
        return readLong(key);
    }

    public static SharedPreferences getSp() {
        return PreferenceManager.getDefaultSharedPreferences(RHSApp.getAppContext());
    }
}
