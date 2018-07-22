package com.sam.music.player.utils;

import android.os.Handler;

/**
 * Created by i301487 on 3/31/16.
 */
public class RHSTimer {

    private static Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
        }
    };

    static void start(int interval, Runnable runnable){

        handler.postAtTime(runnable, System.currentTimeMillis() + interval);
        handler.postDelayed(runnable, interval);
    }
}
