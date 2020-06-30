package com.bsoft.mob.ienr.util.prefs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * WIFI preference辅助类 Created by hy on 14-3-25.
 */
public class WifiPrefUtils {

	public static final String WEB_PREF = "web_pref";
	public static final String IP_KEY_JAVA = "ip_java";
	public static final String PORT_KEY_JAVA = "port_java";
	public static final String IP_KEY = "ip";
	public static final String PORT_KEY = "port";
	public static final String SSID_KEY = "ssid";
	public static final String SSID_PWD_KEY = "ssid_pwd";
	public static final String CNN_TYPE_KEY = "cnn_type";
	public static final String SECRET_TYPE_KEY = "secret_type";
	public static final String PUSH_IP_KEY = "push_ip";
	public static final String PUSH_PORT_KEY = "push_port";

	private static SharedPreferences preferences;

	/**
	 * 获取WIFI SSID
	 * 
	 * @param mContext
	 *            上下文
	 * @return 失败返回null, 默认为""字符串
	 */
	public static String getSSID(Context mContext) {

		if (!initPref(mContext)) {
			return null;
		}
		return preferences.getString(SSID_KEY, "");
	}

	/**
	 * 获取WIFI 密码
	 * 
	 * @param mContext
	 *            上下文
	 * @return WIFI 密码,默认为""字符串，失败返回null。
	 */
	public static String getPassword(Context mContext) {

		if (!initPref(mContext)) {
			return null;
		}
		return preferences.getString(SSID_PWD_KEY, "");
	}

	/**
	 * 获取IP
	 * 
	 * @param mContext
	 *            上下文
	 * @return IP,默认为""字符串，失败返回null。
	 */
	public static String getIP(Context mContext) {

		if (!initPref(mContext)) {
			return null;
		}
		return preferences.getString(IP_KEY, "");
	}

	/**
	 * 获取IP
	 *
	 * @param mContext
	 *            上下文
	 * @return IP,默认为""字符串，失败返回null。
	 */
	public static String getIPForJava(Context mContext) {

		if (!initPref(mContext)) {
			return null;
		}
		return preferences.getString(IP_KEY_JAVA, "");
	}

	/**
	 * 获取IP
	 * 
	 * @param mContext
	 *            上下文
	 * @return IP,默认为""字符串，失败返回null。
	 */
	public static String getPushIP(Context mContext) {

		if (!initPref(mContext)) {
			return null;
		}
		return preferences.getString(PUSH_IP_KEY, "");
	}

	/**
	 * 获取服务端端口
	 * 
	 * @param mContext
	 *            上下文
	 * @return 服务端端口,默认为""字符串，失败返回null。
	 */
	public static String getPort(Context mContext) {

		if (!initPref(mContext)) {
			return null;
		}
		return preferences.getString(PORT_KEY, "");
	}

	/**
	 * 获取服务端端口
	 *
	 * @param mContext
	 *            上下文
	 * @return 服务端端口,默认为""字符串，失败返回null。
	 */
	public static String getPortForJava(Context mContext) {

		if (!initPref(mContext)) {
			return null;
		}
		return preferences.getString(PORT_KEY_JAVA, "");
	}

	/**
	 * 获取推送服务端端口
	 * 
	 * @param mContext
	 *            上下文
	 * @return 服务端端口,默认为""字符串，失败返回null。
	 */
	public static String getPushPort(Context mContext) {

		if (!initPref(mContext)) {
			return null;
		}
		return preferences.getString(PUSH_PORT_KEY, "");
	}

	/**
	 * 获取 加密类型
	 * 
	 * @param mContext
	 *            上下文
	 * @return 加密类型 ,默认为""字符串，失败返回null。
	 */
	public static int getSecretType(Context mContext) {

		if (!initPref(mContext)) {
			return 0;
		}
		return preferences.getInt(SECRET_TYPE_KEY, 0);
	}

	/**
	 * 获取连接类型
	 * 
	 * @param mContext
	 *            上下文
	 * @return 0为动态，1为静态
	 */
	public static int getCnnType(Context mContext) {

		if (!initPref(mContext)) {
			return 0;
		}
		return preferences.getInt(CNN_TYPE_KEY, 0);
	}

	public static boolean initPref(Context mContext) {

		if (mContext == null) {
			return false;
		}
		if (preferences == null) {
			preferences = mContext.getSharedPreferences(WEB_PREF,
					Context.MODE_PRIVATE);
		}
		return preferences != null;
	}
}
