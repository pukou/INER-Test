package com.bsoft.mob.ienr.util.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.bsoft.mob.ienr.R;

/**
 * WIFI preference辅助类 Created by hy on 14-3-25.
 */
public class UserGuidePrefUtils {

	public static final String SETTING_PREF = "user_guide_pref";

	private static SharedPreferences preferences;

	/**
	 * 获取当前所选导航列,默认为true
	 * 
	 * @param mContext
	 * @return
	 */
	public static boolean[] getsltMenus(Context mContext) {

		if (!initPref(mContext)) {
			return null;
		}

		String[] items = mContext.getResources().getStringArray(
				R.array.user_menu_class_array);
		if (items == null) {
			return null;
		}
		boolean[] result = new boolean[items.length];
		for (int i = 0; i < items.length; i++) {
			result[i] = preferences.getBoolean(items[i], true);
		}
		return result;
	}

	public static boolean saveMenus(Context mContext, boolean[] state) {

		if (!initPref(mContext)) {
			return false;
		}

		String[] items = mContext.getResources().getStringArray(
				R.array.user_menu_class_array);

		if (state == null || items == null || state.length != items.length) {
			return false;
		}

		SharedPreferences.Editor editor = preferences.edit();

		for (int i = 0; i < items.length; i++) {

			editor.putBoolean(items[i], state[i]);
		}
		return editor.commit();

	}

	public static boolean initPref(Context mContext) {

		if (mContext == null) {
			return false;
		}
		if (preferences == null) {
			preferences = mContext.getSharedPreferences(SETTING_PREF,
					Context.MODE_PRIVATE);
		}
		return preferences != null;
	}
}
