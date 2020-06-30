package com.bsoft.mob.ienr.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.fragment.base.LeftMenuListFragment;
import com.bsoft.mob.ienr.fragment.SickPersonListFragment;
import com.bsoft.mob.ienr.fragment.base.BaseFragment;
import com.bsoft.mob.ienr.view.menus.SlidingFragmentActivity;
import com.bsoft.mob.ienr.view.menus.SlidingMenu;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 上午11:43:05 类说明
 */
public class MainActivity extends SlidingFragmentActivity {

	public SlidingMenu sm;


	@Override
	protected int setupLayoutResId() {
		return R.layout.activity_main;
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		// 今日工作气泡提示功能添加
		// 若启用气泡拖拽爆破效果请先取消这里的的注释，在去修改相应代码
		// CoverManager.getInstance().init(this);
		// CoverManager.getInstance().setMaxDragDistance(150);
		// CoverManager.getInstance().setExplosionTime(150);

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.id_frame_layout, new SickPersonListFragment()).commit();
		// getSupportFragmentManager().beginTransaction()
		// .replace(R.id.activity_frame_content, new LeftMenuListFragment()).commit();

		setBehindContentView(R.layout.layout_root_frame);
		FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();

		t.replace(R.id.id_fl_container, new LeftMenuListFragment());
		t.commit();

		sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.sliding_menu_shadow_width);
		//###sm.setShadowDrawable(R.drawable.img_shadow);
		sm.setBehindOffsetRes(R.dimen.sliding_menu_behind_offset);
		sm.setFadeDegree(0.30f);
		sm.setMode(SlidingMenu.LEFT);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		// sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		// sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_VIEWPAGER);
		sm.setTouchModeBehind(SlidingMenu.TOUCHMODE_MARGIN);
	}

	public void setTouchModeAbove(int type) {
		sm.setTouchModeAbove(type);
	}

	public void switchContent(Fragment fragment) {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.id_frame_layout, fragment).commit();
		// getSlidingMenu().showAbove();
		sm.showContent();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 彻底关闭程序
		//todo 此功能慎用，因扫描护士胸卡会退出
		//android.os.Process.killProcess(android.os.Process.myPid());
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

	}

	/**
	 * 响应date picker fragment dialog 结果广播
	 * 
	 * @param year
	 * @param month
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
