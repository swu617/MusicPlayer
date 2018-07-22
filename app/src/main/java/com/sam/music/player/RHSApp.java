/*
 * Created by i308844 on 2014/10/11
 * Copyright (c) 2014 SAP. All rights reserved.
 */

package com.sam.music.player;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAgentListener;
import com.sam.music.player.utils.Bean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RHSApp extends Application {

//    public static List<Bean> resList = new ArrayList<>(Arrays.asList(
//            new Bean(R.string.kids, R.drawable.kids),
//            new Bean(R.string.baby, R.drawable.baby),
//            new Bean(R.string.fetal, R.drawable.fetal),
//            new Bean(R.string.add_more, R.drawable.album_bg)
//    ));

    private static RHSApp context;

    public static Context getAppContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        RHSApp.context = this;

        FlurryAgent.setVersionName(BuildConfig.VERSION_NAME);
        new FlurryAgent.Builder().build(context, "XHWXFRSGCSK9QFR29JB5");
    }
}
