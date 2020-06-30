package com.bsoft.mob.ienr.components.update;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import com.bsoft.mob.ienr.R;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-15 上午12:22:59
 * @类说明
 */
public class Config {
	private static final String TAG = "Config";

//	public static final String UPDATE_APKNAME = "download/SodaoShow.apk";
//	public static final String UPDATE_VERJSON = "download/version.txt";
//	public static final String UPDATE_SAVENAME = "SodaoShow_new.apk";

	public static int getVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager()
					.getPackageInfo("com.bsoft.mob.ienr", 0).versionCode;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}
		return verCode;
	}

	public static String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager()
					.getPackageInfo("com.bsoft.mob.ienr", 0).versionName;
		} catch (NameNotFoundException e) {
			Log.e(TAG, e.getMessage());
		}
		return verName;

	}

	public static String getAppName(Context context) {
		String verName = context.getResources().getText(R.string.app_name)
				.toString();
		return verName;
	}

}
