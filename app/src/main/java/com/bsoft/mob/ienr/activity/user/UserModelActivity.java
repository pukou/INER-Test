package com.bsoft.mob.ienr.activity.user;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.fragment.base.BaseFragment;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.fragment.user.DrugsAdviceFragment;
import com.bsoft.mob.ienr.fragment.user.OutControlFragment;
import com.bsoft.mob.ienr.fragment.user.SickPersonInfoFragment;
import com.bsoft.mob.ienr.fragment.user.SpecimenFragment;
import com.bsoft.mob.ienr.fragment.base.RightMenuListFragment;
import com.bsoft.mob.ienr.model.MemuVo;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.menus.SlidingFragmentActivity;
import com.bsoft.mob.ienr.view.menus.SlidingMenu;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 上午11:43:05
 * @类说明 病人模块导航
 */
public class UserModelActivity extends SlidingFragmentActivity {

	public SlidingMenu sm;

	@Override
	protected int setupLayoutResId() {
		return R.layout.activity_user_model;
	}

	@Override
	protected void initView(Bundle savedInstanceState) {

		setIsMainView(false);

		setBehindContentView(R.layout.layout_root_frame);
		FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();

		t.replace(R.id.id_fl_container, new RightMenuListFragment());
		t.commit();

		sm = getSlidingMenu();

		sm.setShadowWidthRes(R.dimen.sliding_menu_shadow_width);
		//###sm.setShadowDrawable(R.drawable.img_shadow);
		sm.setBehindOffsetRes(R.dimen.sliding_menu_behind_offset);
		sm.setFadeDegree(0.30f);
		sm.setMode(SlidingMenu.RIGHT);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		// sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		// sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_VIEWPAGER);
		sm.setTouchModeBehind(SlidingMenu.TOUCHMODE_MARGIN);
		fastSwitch(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		fastSwitch(intent);
	}

	/**
	 * 响应快速切换
	 * 
	 * @param intent
	 */
	public void fastSwitch(Intent intent) {
		boolean needSaveUserModelItemPos = true;

		Fragment fragment = null;
		if (intent.hasExtra("barinfo")) {
			BarcodeEntity entity = (BarcodeEntity) intent
					.getParcelableExtra("barinfo");
			Bundle args = new Bundle();
			args.putParcelable("barinfo", entity);
			if (entity.TMFL == 2) {
				fragment = new DrugsAdviceFragment();
			} else if (entity.TMFL == 7) {
				fragment = new SpecimenFragment();
			}
			if (fragment != null) {
				fragment.setArguments(args);
			}
		}else if (intent.getBooleanExtra("refresh", false)) {
			fragment = new SickPersonInfoFragment();
			/*升级编号【56010038】============================================= start
                外出管理PDA上只有登记功能，查询需要找到具体的人再查询，不太方便，最好能有一个查询整个病区外出病人的列表
            ================= classichu 2018/3/7 19:49
            */
		} else if (intent.getBooleanExtra("outcontrol", false)) {
			fragment = new OutControlFragment();
			needSaveUserModelItemPos = false;
		} else {
			/* =============================================================== end */

			fragment = application.getUserModelFragment();
		}
		if (fragment != null) {
			switchContent(fragment,needSaveUserModelItemPos);
		}
	}

	public void setTouchModeAbove(int type) {
		sm.setTouchModeAbove(type);
	}

	public void switchContent(Fragment fragment) {
		switchContent(fragment,true);
	}
	public void switchContent(Fragment fragment,boolean needSaveUserModelItemPos) {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.id_frame_layout, fragment).commit();
		// getSlidingMenu().showAbove();
		sm.showContent();
		RightMenuListFragment menu = (RightMenuListFragment) getSupportFragmentManager()
				.findFragmentById(R.id.id_fl_container);


		int position = getPosition(fragment.getClass().getCanonicalName(),
				application.getUserModel());
		if (menu == null) {
			FragmentTransaction t = this.getSupportFragmentManager()
					.beginTransaction();
			if (needSaveUserModelItemPos) {
				application.userModelItem = position;
			}
			t.replace(R.id.id_fl_container, new RightMenuListFragment());
		} else {
			menu.changePostion(position);
		}
	}

	/**
	 * 
	 * @param canonicalName
	 * @param userModel
	 * @return
	 */
	private int getPosition(String canonicalName, ArrayList<MemuVo> userModel) {

		if (EmptyTool.isBlank(canonicalName) || userModel == null) {
			return 0;
		}
		int size = userModel.size();
		for (int i = 0; i < size; i++) {
			MemuVo menu = userModel.get(i);
			if (menu.tclass.equals(canonicalName)) {
				return i;
			}
		}
		return 0;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		BaseFragment fragment = (BaseFragment) getSupportFragmentManager()
				.findFragmentById(R.id.id_frame_layout);
		if (!fragment.onKeyBackPressed()){
			super.onBackPressed();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public void initBarBroadcast() {

		// 去掉监听
		// barBroadcast = new BroadcastReceiver() {
		// @Override
		// public void onReceive(Context arg0, Intent intent) {
		// if (IBarCode.Bar_Get.equals(intent.getAction())) {
		// barinfo = (BarCodeInfo) intent
		// .getSerializableExtra("barinfo");
		// switch (barinfo.barCode.id) {
		// case 2:
		// if (application.userModelItem != 2) {
		// switchContent(new DrugsAdviceFragment());
		// }
		// break;
		// case 4:
		// if (application.userModelItem != 4) {
		// switchContent(new SpecimenFragment());
		// }
		// break;
		// default:
		// break;
		// }
		// }
		// }
		// };
	}

	/**
	 * 响应date picker fragment dialog 结果广播
	 * 
	 * @param year
	 * @param monthOfYear
	 * @param dayOfMonth
	 * @param viewId
	 */
	@Override
	public void onDateSet(int year, int month, int dayOfMonth, int viewId) {

		BaseFragment fragment = (BaseFragment) getSupportFragmentManager()
				.findFragmentById(R.id.id_frame_layout);
		fragment.onDateSet(year, month, dayOfMonth, viewId);
	}

	/**
	 * 响应date time picker fragment dialog 结果广播
	 */
	@Override
	public void onDateTimeSet(int year, int month, int dayOfMonth,
			int hourOfDay, int minute, int viewId) {

		BaseFragment fragment = (BaseFragment) getSupportFragmentManager()
				.findFragmentById(R.id.id_frame_layout);
		fragment.onDateTimeSet(year, month, dayOfMonth, hourOfDay,
				minute, viewId);
	}

	@Override
	public void onTimeSet(int hourOfDay, int minute, int viewId) {

		BaseFragment fragment = (BaseFragment) getSupportFragmentManager()
				.findFragmentById(R.id.id_frame_layout);
		fragment.onTimeSet(hourOfDay, minute, viewId);
	}

}
