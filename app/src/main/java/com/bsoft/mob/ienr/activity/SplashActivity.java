package com.bsoft.mob.ienr.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.components.wifi.WifiService;
import com.bsoft.mob.ienr.components.wifi.WifiUtil;

/**
 * Splash页 主要功能包括：
 * <p>
 * 1 验证WIFI 连接 2 创建快捷方式
 * </p>
 * Created by hy on 14-3-25.
 */
public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);


		/* if (!hasShortCut()) {
		 addShortcutToDesktop();
		 }*/

		if (WifiUtil.openWifi(getApplicationContext())) {
			// 自动连接到配制的WIFI SSID
			WifiService.actionStart(SplashActivity.this);
		}

		Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();

	}

	/**
	 * 增加快捷键
	 */
	public void addShortcutToDesktop() {

		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// 不允许重建
		shortcut.putExtra("duplicate", false);
		// 设置名字
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				this.getString(R.string.app_name));
		// 设置图标
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(this,

				R.drawable.ic_launcher));

		// 设置意图和快捷方式关联程序
		ComponentName comp = new ComponentName(this.getPackageName(),
				this.getPackageName() + "." + this.getLocalClassName());
		Intent intent = new Intent(Intent.ACTION_MAIN).setComponent(comp);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);

		// 发送广播
		sendBroadcast(shortcut);
	}

	/**
	 * 判断是否已添加快捷键
	 * 
	 * @return
	 */
	public boolean hasShortCut() {

		String url = "";
		if (android.os.Build.VERSION.SDK_INT < 8) {
			url = "content://com.android.launcher.settings/favorites?notify=true";
		} else {
			url = "content://com.android.launcher2.settings/favorites?notify=true";
		}
		ContentResolver resolver = getContentResolver();
		Cursor cursor = resolver.query(Uri.parse(url), null, "title=?",
				new String[] { getString(R.string.app_name) }, null);

		if (cursor != null && cursor.moveToNext()) {
			cursor.close();
			return true;
		}

		return false;
	}

}
