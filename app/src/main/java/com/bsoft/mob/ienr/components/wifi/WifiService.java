package com.bsoft.mob.ienr.components.wifi;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.bsoft.mob.ienr.helper.ExecutorServiceHelper;
import com.bsoft.mob.ienr.util.prefs.WifiPrefUtils;
import com.bsoft.mob.ienr.view.BSToast;

/**
 * 管理WIFI连接 Created by hy on 14-3-20.
 */
public class WifiService extends IntentService {

	public WifiService() {
		super(WifiService.class.getName());

	}

	public WifiService(String name) {
		super(WifiService.class.getName());

	}

	public static final String TAG = "WifiService";
	private static final String ACTION_START = TAG + ".START";

	private Handler handler_ = new Handler();

	// Static method to start the service
	public static void actionStart(Context ctx) {
		Intent svc = new Intent(ctx, WifiService.class);
		svc.setAction(ACTION_START);
		ctx.startService(svc);
	}

	private void handleStart() {
		ExecutorServiceHelper.execute(new Runnable() {
			@Override
			public void run() {
				String ssidStr = WifiPrefUtils.getSSID(getApplicationContext());
				String ssidPwd = WifiPrefUtils
						.getPassword(getApplicationContext());
				wifimanager(ssidStr, ssidPwd);
			}
		});
	/*	new Thread() {

			@Override
			public void run() {
				String ssidStr = WifiPrefUtils.getSSID(getApplicationContext());
				String ssidPwd = WifiPrefUtils
						.getPassword(getApplicationContext());
				wifimanager(ssidStr, ssidPwd);
			}
		}.start();*/
	}

	public void wifimanager(String ssidStr, String ssidPwd) {

		String ssid = WifiUtil.getSSID(this);

		String ssidefualt = "\"" + ssidStr + "\"";
		// 正确连接到指定AP,
		if (ssidStr.equals(ssid) || ssidefualt.equals(ssid)) {

			return;

		}
		// 进行扫描，并尝试连接到指定AP
		if (!WifiUtil.scanWifi(this)) {
			showToast("扫描WIFI失败，请确认WIFI已开启");
			Log.w(TAG, "扫描WIFI失败，请确认WIFI已开启");
		}
	}

	private void showToast(final String s) {

		handler_.post(new Runnable() {

			@Override
			public void run() {
				try {
					BSToast.showToast(getApplicationContext(), s, BSToast.LENGTH_LONG);
				} catch (RuntimeException e) {
					Log.e("WifiService",
							"A runtime exception was thrown while executing code in a runnable",
							e);
				}
			}

		});
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		handleStart();
	}

}
