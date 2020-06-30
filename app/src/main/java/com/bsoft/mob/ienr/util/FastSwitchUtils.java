package com.bsoft.mob.ienr.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.bsoft.mob.ienr.activity.user.UserModelActivity;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.fragment.user.DrugsAdviceFragment;
import com.bsoft.mob.ienr.fragment.user.SpecimenFragment;

/**
 * 快速扫描切换辅助类
 * 
 * @author admin
 *
 */
public class FastSwitchUtils {

	/**
	 * 快速切换
	 * 
	 * @param a
	 * @param entity
	 */
	public static void fastSwith(Activity a, BarcodeEntity entity) {

		if (a == null || entity == null || entity.TMFL != 2) {
			return;
		}
		Bundle args = new Bundle();
		args.putParcelable("barinfo", entity);
		Fragment fragment = null;
		if (entity.FLBS == 3 || entity.FLBS == 4 || entity.FLBS == 5) {
			fragment = new DrugsAdviceFragment();
		} else if (entity.TMFL == 2) {
			fragment = new SpecimenFragment();
			// 获取全名
		}
		if (fragment != null) {
			fragment.setArguments(args);
			if (a instanceof UserModelActivity) {
				((UserModelActivity) a).switchContent(fragment);
			} else {
				Intent result = new Intent(a, UserModelActivity.class);
				result.putExtra("barinfo", entity);
				a.startActivity(result);
			}
		}
	}

	/**
	 * 打开用户相关页，并切换用户(主要来自非单个用户相关Fragment调用)
	 */
	public static void switchUser(Fragment fragment) {
		if (fragment == null) {
			return;
		}
		Activity activity = fragment.getActivity();
		if (activity == null) {
			return;
		}
		if (!(activity instanceof UserModelActivity)) {
			Intent result = new Intent(activity, UserModelActivity.class);
			result.putExtra("refresh", true);
			activity.startActivity(result);
		}
	}

	/**
	 * 判断是否需要快速切换界面（当前包括医嘱条码、标本采集）
	 * 
	 * @param entity
	 * @return
	 */
	public static boolean needFastSwitch(BarcodeEntity entity) {

		if (entity == null || entity.TMFL != 2) {
			return false;
		}
		switch (entity.FLBS) {
		case 2:
			return true;
		case 3:
			return true;
		case 4:
			return true;
		case 5:
			return true;
		}
		return false;

	}

}
