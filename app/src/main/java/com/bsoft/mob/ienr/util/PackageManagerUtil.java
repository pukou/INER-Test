package com.bsoft.mob.ienr.util;

import java.io.File;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

/**
 * 
 * 应用包管理辅助类
 * 
 */
public class PackageManagerUtil {

	/**
	 * 获取APP getPackageManager
	 * 
	 * @param mContext
	 * @return 失败返回null
	 */
	public static PackageManager getPackageManager(Context mContext) {

		if (mContext == null) {
			return null;
		}
		return mContext.getPackageManager();
	}

	/**
	 * 获取指定路径的APP PackageInfo
	 * 
	 * @param mContext
	 * @param strpath
	 * @return 失败返回null
	 */
	public static PackageInfo getPackageInfoFromApk(Context mContext,
			String strpath) {

		if (mContext == null || strpath == null) {
			return null;
		}

		File file = new File(strpath);

		if (!file.exists()) {
			return null;
		}

		PackageManager oManager = getPackageManager(mContext);
		PackageInfo packageInfo = null;
		if (oManager != null) {
			packageInfo = oManager.getPackageArchiveInfo(strpath, 0);
		}
		return packageInfo;
	}

	/**
	 * 获取 APP PackageInfo
	 * 
	 * @param mContext
	 * @return 失败返回null
	 */
	public static PackageInfo getPackageInfo(Context mContext) {

		if (mContext == null) {
			return null;
		}

		PackageManager oManager = getPackageManager(mContext);
		PackageInfo packageInfo = null;
		if (oManager != null) {
			packageInfo = oManager.getPackageArchiveInfo(
					mContext.getPackageName(), 0);
		}
		return packageInfo;
	}

	/**
	 * 获取指定包名的APP PackageInfo
	 * 
	 * @param mContext
	 * @param strpath
	 * @return 失败返回null
	 */
	public static PackageInfo getPackageInfoFromPackageName(Context mContext,
			String packageName) {

		if (mContext == null || packageName == null) {
			return null;
		}

		PackageManager oManager = getPackageManager(mContext);
		PackageInfo packageInfo = null;
		if (oManager != null) {
			try {
				packageInfo = oManager.getPackageInfo(packageName, 0);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return packageInfo;
	}

	/**
	 * 获取指定路径的APP 应用包信息
	 * 
	 * @param mContext
	 * @param strpath
	 * @return 失败返回null
	 */
	public static String getPackage(Context mContext, String strpath) {

		PackageInfo packageInfo = getPackageInfoFromApk(mContext, strpath);
		if (packageInfo != null) {
			return packageInfo.packageName;
		}
		return null;
	}

	/**
	 * 获取指定路径的APP ShareUserID信息
	 * 
	 * @param mContext
	 * @param strpath
	 * @return 失败返回null
	 */
	public static String getShareDUserId(Context mContext, String strpath) {

		PackageInfo packageInfo = getPackageInfoFromApk(mContext, strpath);
		if (packageInfo != null) {
			return packageInfo.sharedUserId;
		}
		return null;
	}

	/**
	 * 获取指定路径的APP ApplicationInfo
	 * 
	 * @param mContext
	 * @param strpath
	 * @return 失败返回null
	 */
	public static ApplicationInfo getApplicationInfo(Context mContext,
			String strpath) {

		PackageManager oManager = getPackageManager(mContext);
		ApplicationInfo applicationInfo = null;
		if (oManager != null) {
			try {
				applicationInfo = oManager.getApplicationInfo(
						getPackage(mContext, strpath), 0);
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
		}
		return applicationInfo;
	}
}
