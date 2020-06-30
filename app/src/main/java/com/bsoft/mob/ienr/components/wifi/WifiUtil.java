package com.bsoft.mob.ienr.components.wifi;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

public class WifiUtil {

	/**
	 * 判断网络是否连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isConnected(Context context) {

		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity != null) {

				// 获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();

				if (info != null && info.isConnected()) {
					// 判断当前网络是否已经连接
					if (info.getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			Log.e(Constant.TAG, e.toString(), e);
		}
		return false;
	}

	/**
	 * 创建或修改WIFI配制,并进行连接
	 * 
	 * @param strname
	 *            SSID
	 * @param strpass
	 *            password
	 * @param strBSSID
	 *            BSSID
	 * @param mContext
	 *            Context
	 * @return 成功返回true
	 */
	public static boolean CreateNetConfig(String strname, String strpass,
			String strBSSID, Context mContext) {

		if (strname == null || strpass == null || strBSSID == null
				|| mContext == null) {
			return false;
		}

		// 判断是否已经存在
		WifiManager conManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		List<WifiConfiguration> config2 = conManager.getConfiguredNetworks();
		for (WifiConfiguration wifiConfiguration : config2) {
			// 如果已经存在要进行清除
			if (wifiConfiguration.SSID.equals("\"" + strname + "\"")) {
				conManager.removeNetwork(wifiConfiguration.networkId);
				break;
			}
		}

		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.BSSID = strBSSID;
		config.SSID = "\"" + strname + "\"";
		config.preSharedKey = "\"" + strpass + "\"";
		config.hiddenSSID = true;
		config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
		// Ciphers
		config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		config.allowedPairwiseCiphers
				.set(WifiConfiguration.PairwiseCipher.TKIP);
		config.allowedPairwiseCiphers
				.set(WifiConfiguration.PairwiseCipher.CCMP);
		// Pairwise
		config.status = WifiConfiguration.Status.ENABLED;
		int nnetid = conManager.addNetwork(config);
		if (nnetid != -1) {
			conManager.saveConfiguration();
			if (conManager.enableNetwork(nnetid, true)) {
				conManager.reconnect();
				return true;
			}
		}
		return false;
	}

	/**
	 * get wifiInfo
	 * 
	 * @param mContext
	 * @return
	 */
	public static WifiInfo GetWifiInfo(Context mContext) {

		if (mContext == null) {
			return null;
		}
		// 判断是否已经存在
		WifiManager conManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo oinfo = conManager.getConnectionInfo();
		return oinfo;
	}

	/**
	 * get wifi BSSID
	 * 
	 * @param mContext
	 * @return
	 */
	public static String getBSSID(Context mContext) {

		WifiInfo oinfo = GetWifiInfo(mContext);
		if (oinfo != null) {
			return oinfo.getBSSID();
		}
		return null;
	}

	/**
	 * get wifi SSID
	 * 
	 * @param mContext
	 * @return
	 */
	public static String getSSID(Context mContext) {

		WifiInfo oinfo = GetWifiInfo(mContext);
		if (oinfo != null) {
			return oinfo.getSSID();
		}
		return null;
	}

	/**
	 * get MAC ADDRESS
	 * 
	 * @param mContext
	 * @return
	 */
	public static String getMacAress(Context mContext) {

		WifiInfo oinfo = GetWifiInfo(mContext);
		if (oinfo != null) {
			return oinfo.getMacAddress();
		}
		return null;
	}

	/**
	 * 判断WIFI网络是否连接
	 * 
	 * @param mContext
	 * @return
	 */
	public static boolean isWifiConnected(Context mContext) {

		if (mContext == null) {
			return false;
		}
		ConnectivityManager connectivityManager = (ConnectivityManager) mContext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null
				&& activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI
				&& activeNetInfo.isConnected()) {
			return true;
		}
		return false;
	}

	/**
	 * 扫描WIFI,如果wifi未打开，则打开
	 * 
	 * @param mContext
	 * @return
	 */
	public static boolean scanWifi(Context mContext) {

		if (mContext == null) {
			return false;
		}
		// 判断是否已经打开WIFI开关
		WifiManager conManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		if (!openWIFI(conManager)) {
			return false;
		}
		// 开始扫描无线热点
		return conManager.startScan();
	}

	public static boolean openWIFI(Context mContext) {

		if (mContext == null) {
			return false;
		}
		// 判断是否已经打开WIFI开关
		WifiManager conManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		return openWIFI(conManager);
	}

	public static boolean reOpenWIFI(Context mContext) {

		if (mContext == null) {
			return false;
		}
		// 判断是否已经打开WIFI开关
		WifiManager conManager = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		if (conManager != null) {
			conManager.setWifiEnabled(false);
		}
		return openWIFI(conManager);
	}

	public static boolean openWIFI(WifiManager conManager) {

		if (conManager == null) {
			return false;
		}
		// conManager.setWifiEnabled(false);
		if (!conManager.isWifiEnabled()) {
			return conManager.setWifiEnabled(true);
		} else {
			return true;
		}
	}

	/**
	 * 判断是否要打开WIFI
	 * 
	 * @return true，需要打开，false 不需要
	 */
	public static boolean openWifi(Context mContext) {

		if (mContext == null) {
			return false;
		}

		String ssidSeted = getSSID(mContext);
		if (EmptyTool.isBlank(ssidSeted)) {
			return false;
		}

		if (WifiUtil.isWifiConnected(mContext)) {

			// 当前所连SSID
			String ssid = WifiUtil.getSSID(mContext);
			// 在android 4.2中的 SSID
			String ssidIn17 = "\"" + ssidSeted + "\"";
			// 正确连接到指定SSID
			if (ssidSeted.equals(ssid) || ssidIn17.equals(ssid)) {
				return false;
			}
		}
		return true;

	}

	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface
					.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf
						.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					//ipv6地址
//					if (!inetAddress.isLoopbackAddress()) {
//						return inetAddress.getHostAddress().toString();
//					}
					// ipv4地址
					if (!inetAddress.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(inetAddress
									.getHostAddress())) {
						return inetAddress.getHostAddress();

					}

				}
			}
		} catch (SocketException ex) {
			Log.e("WifiPreference IpAddress", ex.toString());
		}
		return null;
	}
}
