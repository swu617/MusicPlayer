package com.sam.music.player.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Created by i301487 on 1/2/16.
 */
public class MetricsUtils {
    private static DisplayMetrics displayMetric = null;

    /*
     * Get display values
     *
     * @param context
     * @return String
     */
    public static String getResolution(Context context) {

        if (displayMetric == null)
            InitInstance(context);

        if (displayMetric == null)
            return null;

        int nWidth = Math.min(displayMetric.widthPixels, displayMetric.heightPixels);
        int nHeight = Math.max(displayMetric.widthPixels, displayMetric.heightPixels);

        return String.format("%dx%d", nWidth, nHeight);
    }

    /*
     * Get display width values
     *
     * @param context
     * @return int
     */
    public static int getScreenWidth(Context context) {

        if (displayMetric == null)
            InitInstance(context);

        if (displayMetric == null)
            return -1;

        return Math.min(displayMetric.widthPixels, displayMetric.heightPixels);
    }

    /*
     * Get display height values
     *
     * @param context
     * @return int
     */
    public static int getScreenHeight(Context context) {

        if (displayMetric == null)
            InitInstance(context);

        if (displayMetric == null)
            return -1;

        return Math.max(displayMetric.widthPixels, displayMetric.heightPixels);
    }

    /*
     * Get DPI
     *
     * @param context
     * @return int
     */
    public static int getDPI(Context context) {
        if (displayMetric == null)
            InitInstance(context);

        if (displayMetric == null)
            return 0;

        return displayMetric.densityDpi;
    }

    /*
     * Get DPI
     *
     * @param context
     * @return
     */
    public static float getScaledDensity(Context context) {
        if (displayMetric == null)
            InitInstance(context);

        if (displayMetric == null)
            return 0;

        return displayMetric.scaledDensity;
    }

    private static void InitInstance(Context context) {

        if (displayMetric == null) {
            displayMetric = new DisplayMetrics();
        }

        try {
            ((Activity) context).getWindowManager().
                    getDefaultDisplay().getMetrics(displayMetric);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
