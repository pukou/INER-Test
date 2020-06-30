/**   
 * @Title: OffLineUtil.java 
 * @Package com.bsoft.mob.ienr.util
 * @Description: 离线保存工具类 
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-12-24 上午10:52:59 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.util;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import com.bsoft.mob.ienr.db.Database;

import java.util.Date;

/**
 * @ClassName: OffLineUtil
 * @Description: 离线保存工具类
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2015-12-24 上午10:52:59
 * 
 */
public class OffLineUtil {

	/**
	 * @Description: 离线保存
	 * @param @param context 上下文
	 * @param @param url 服务端接口的url
	 * @param @param type 请求类型（1：get；2：post）
	 * @param @param param POST请求参数
	 * @param @param patient 病人姓名
	 * @param @param recordname 表单名称
	 * @param @param createnurse 创建护士
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	public static boolean offLineSave(Context context, String url, int type,
			String param, String patient, String recordname, String createnurse) {
		Uri uri = Database.OffLine.CONTENT_URI;

		ContentValues values = new ContentValues();
		values.put(Database.OffLine.URL, url);
		values.put(Database.OffLine.TYPE, type);
		values.put(Database.OffLine.PARAM, param);
		values.put(Database.OffLine.PATIENT, patient);
		values.put(Database.OffLine.RECODENAME, recordname);
		values.put(Database.OffLine.CREATETIME,
				DateUtil.format_yyyyMMdd_HHmm.format(new Date()));
		values.put(Database.OffLine.CREATE_NURSE, createnurse);
		Uri ret = context.getContentResolver().insert(uri, values);
		return ret.toString().length() > uri.toString().length();
	}

	public static boolean WifiConnected(Context context) {
		ConnectivityManager con = (ConnectivityManager) context
				.getSystemService(Activity.CONNECTIVITY_SERVICE);
		boolean wifi = con.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.isConnectedOrConnecting();
		boolean internet = false;
		NetworkInfo info = con.getActiveNetworkInfo();
		if (info != null)
			internet = info.isConnectedOrConnecting();
		if (wifi | internet) {
			return true;
		}
		return false;
	}
}
