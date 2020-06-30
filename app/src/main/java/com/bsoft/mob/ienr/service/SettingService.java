package com.bsoft.mob.ienr.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.db.Database;
import com.bsoft.mob.ienr.components.setting.AdminSetting;

/**
 * 读取用户设置项，并进行APP配制
 * 
 * @author hy
 * 
 */
public class SettingService extends IntentService {

	public SettingService() {
		super(SettingService.class.getName());
	}

	public SettingService(String name) {
		super(SettingService.class.getName());
	}

	AppApplication mAppApplication;
	@Override
	protected void onHandleIntent(Intent intent) {

		if (mAppApplication.user == null) {
			return;
		}
		readSettingsInDb(getLocalId());
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mAppApplication = (AppApplication) getApplication();
	}

	private void readSettingsInDb(String userid) {

		int vib = -1;
		Uri uri = Database.Setting.CONTENT_URI;

		String[] projection = { Database.Setting.VIB };
		String selection = Database.Setting.USER + "=?";
		String[] selectionArgs = { userid };
		ContentResolver crl = getContentResolver();
		Cursor cursor = crl.query(uri, projection, selection, selectionArgs,
				null);
		if (cursor.moveToNext()) {
			vib = cursor.getInt(0);
		}
		cursor.close();


		String localId = getLocalId(AdminSetting.USERNAME, "-1");
		if (localId.equals(userid)) {
			mAppApplication.getSettingConfig().vib = vib != 1 ? false : true;
			return;
		}
		if (vib == -1) {
			readSettingsInDb(getLocalId(AdminSetting.USERNAME, "-1"));
		}
	}

	public String getLocalId() {


		String id = "-1";
		if (mAppApplication.user == null) {
			return id;
		}
		String[] selectionArgs = { mAppApplication.user.YHID, mAppApplication.user.JGID };
		return getLocalId(selectionArgs);
	}

	public String getLocalId(String... selectionArgs) {

		String id = "-1";

		Uri uri = Database.User.CONTENT_URI;

		String[] projection = { Database.User._ID };

		String selection = Database.User.USER_NAME + "=? " + "AND "
				+ Database.User.AGENT_ID + "=? ";
		ContentResolver crl = getContentResolver();
		Cursor cursor = crl.query(uri, projection, selection, selectionArgs,
				null);
		if (cursor.moveToNext()) {
			id = cursor.getString(0);
		}
		cursor.close();
		return id;
	}
}
