package com.sam.music.player.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;

public class ChannelUtils {

	private static ApplicationInfo mAppInfo = null;
	private final static String APP_CHANNEL = "UMENG_CHANNEL";

	/*
	 * Get Wifi mac address
	 * 
	 * @param context
	 * 
	 * @return
	 */
	public static String GetChannel(Context context) {

		try {
			if (mAppInfo == null) {
				try {
					mAppInfo = (context).getPackageManager()
							.getApplicationInfo((context).getPackageName(),
									PackageManager.GET_META_DATA);
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			if (mAppInfo != null) {
				Bundle bundle = mAppInfo.metaData;
				if (bundle != null && bundle.containsKey(APP_CHANNEL)) {
					return (String) bundle.get(APP_CHANNEL);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
