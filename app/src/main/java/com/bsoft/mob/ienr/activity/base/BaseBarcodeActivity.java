package com.bsoft.mob.ienr.activity.base;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.barcode.BarCodeFactory;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.IBarCode;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 上午11:43:05 类说明 应用程序Activity的基类
 *          <p/>
 *          条码扫描需要实现 BarcodeListener
 */
public abstract class BaseBarcodeActivity extends BaseActivity {

	public IBarCode barCode;

	protected BroadcastReceiver barBroadcast;

	public abstract void initBarBroadcast();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBarBroadcast();
		barCode = BarCodeFactory.getBarCode();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (null != barCode) {
			try {
				if (barCode.onKeyDown(keyCode, event, this)) {
					return true;
				}
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (null != barCode) {
			try {
				if (barCode.onKeyUp(keyCode, event, this)) {
					return true;
				}
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (null != barCode) {
			try {
				barCode.start(this);
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				// e.printStackTrace();
			}
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (null != barBroadcast) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(BarcodeActions.Bar_Get);
			filter.addAction(BarcodeActions.Refresh);
			registerReceiver(barBroadcast, filter);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (null != barCode) {
			try {
				barCode.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	protected void onStop() {
		if (null != barBroadcast) {
			unregisterReceiver(barBroadcast);
		}
		super.onStop();
	}


}
