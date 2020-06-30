package com.bsoft.mob.ienr.barcode;

import android.content.Context;
import android.view.KeyEvent;


public interface IBarCode {

	// 昵称切换
	public static final String Name_Change = "com.barcode.code.name_change";

	// 扫描状态切换
	public void setType(int type);

	public void start(Context context) throws Exception;

	// public BarCodeInfo read() throws Exception;

	public void close() throws Exception;

	/**
	 * 快捷键按钮按下
	 * 
	 * @param keyCode
	 * @param event
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event, Context context)
			throws Exception;

	/**
	 * 快捷键按钮提起
	 * 
	 * @param keyCode
	 * @param event
	 * @param context
	 * @return
	 * @throws Exception
	 */
	public boolean onKeyUp(int keyCode, KeyEvent event, Context context)
			throws Exception;
}
