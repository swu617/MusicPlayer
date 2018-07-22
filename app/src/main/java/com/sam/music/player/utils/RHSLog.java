/*
 * Created by i301487 on 2014/9/11
 * Copyright (c) 2014 SAP. All rights reserved.
 */

package com.sam.music.player.utils;

import android.util.Log;

import com.sam.music.player.BuildConfig;

public class RHSLog {
    private final static String TAG = "** Red Horse Studio **";

    public static void d(Object tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG + " " + tag.toString(), msg);
        }
    }

    public static void i(Object tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG + " " + tag.toString(), msg);
        }
    }


    public static void e(String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, msg);
        }
    }

    public static void e(Object tag, String msg) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG + " " + tag.toString(), msg);
        }
    }

    public static void e(Object tag, String msg, Throwable tr) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG + " " + tag.toString(), msg, tr);
        }
    }

    //For simply use, just use default TAG.
    public static void d(String msg) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (BuildConfig.DEBUG) {
            Log.i(TAG, msg);
        }
    }

}
