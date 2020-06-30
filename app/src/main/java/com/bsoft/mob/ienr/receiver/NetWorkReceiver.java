package com.bsoft.mob.ienr.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.bsoft.mob.ienr.components.wifi.WifiUtil;
import com.bsoft.mob.ienr.util.prefs.WifiPrefUtils;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BSToast;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 监听WIFI扫描、网络变化等事件
 */
public class NetWorkReceiver extends BroadcastReceiver {

	private Handler handler_ = new Handler();
	private Executor executor = Executors.newCachedThreadPool();

	@Override
	public void onReceive(Context context, Intent intent) {
		analyWifi(intent, context);
	}

	public void analyWifi(final Intent intent, final Context context) {

		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					analyWifiInThread(intent, context);
				} catch (RuntimeException e) {
					Log.e("NetWorkReceiver",
							"A runtime exception was thrown while executing code in a runnable",
							e);
				}
			}

		});
	}

	private void analyWifiInThread(Intent intent, Context context) {

		String strAction = intent.getAction();

		if ("android.net.wifi.SCAN_RESULTS".equals(strAction)) {

			/*********/
			String ssidStr = WifiPrefUtils.getSSID(context);
			String ssidPwd = WifiPrefUtils.getPassword(context);
			/*********/
			if (EmptyTool.isBlank(ssidStr)) {
				return;
			}

			WifiManager oManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			List<ScanResult> oList = oManager.getScanResults();
			int nMax = 0;
			ScanResult oBest = null;

			// 获取最佳AP
			for (int i = 0; oList != null && i < oList.size(); i++) {
				ScanResult oResult = oList.get(i);

				if (oResult == null)
					continue;

				// 解决在某些机型上，ssid包括引号BUG
				String ssidefualt = "\"" + ssidStr + "\"";
				if (oResult.SSID.equals(ssidStr)
						|| oResult.SSID.equals(ssidefualt)) {

					if (nMax != 0) {
						if (WifiManager.compareSignalLevel(nMax, oResult.level) < 0) {
							nMax = oResult.level;
							oBest = oResult;
						}
					} else {
						nMax = oResult.level;
						oBest = oResult;
					}
				}
			}
			// 如果热点有效
			if (oBest != null) {
				// 判断原来是否已经有连接
				String BSSID = WifiUtil.getBSSID(context);
				boolean wifiConnected = WifiUtil.isWifiConnected(context);

				// 如果原来有连接
				if (BSSID != null) {
					// 连接的地址和新的地址相同
					if (BSSID.equals(oBest.BSSID)) {
						return;
					} else {
						showToast("原来连接的 " + BSSID + "替换为" + oBest.BSSID,
								context);
					}
				}

				WifiUtil.CreateNetConfig(oBest.SSID, ssidPwd, oBest.BSSID,
						context);

			}
		} else if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(strAction)) {

			int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
					WifiManager.WIFI_STATE_UNKNOWN);
			if (state == WifiManager.WIFI_STATE_DISABLED) {
				WifiUtil.openWIFI(context);
			}

		} else { // 网络连接改变

			// SharedPreferences preferences =
			// context.getSharedPreferences(WifiPrefUtils.SETTING_PREF,
			// Context.MODE_PRIVATE);
			String ssidDefault = WifiPrefUtils.getSSID(context);
			if (EmptyTool.isBlank(ssidDefault)) {
				return;
			}
			String ssid = WifiUtil.getSSID(context);
			String ssidefualt = "\"" + ssidDefault + "\"";
			boolean isApOK = ssidDefault.equals(ssid)
					|| ssidefualt.equals(ssid);
			if (!isApOK || !WifiUtil.isWifiConnected(context)) {
				WifiUtil.scanWifi(context);
			}
		}
	}

	private void showToast(final String s, final Context context) {

		handler_.post(new Runnable() {

			@Override
			public void run() {
				try {
					BSToast.showToast(context, s, BSToast.LENGTH_LONG);
				} catch (RuntimeException e) {
					Log.e("WifiService",
							"A runtime exception was thrown while executing code in a runnable",
							e);
				}
			}

		});
	}

}