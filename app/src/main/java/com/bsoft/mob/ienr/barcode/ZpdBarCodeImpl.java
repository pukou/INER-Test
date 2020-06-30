package com.bsoft.mob.ienr.barcode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.KeyEvent;

/**
 * 中普达
 */
@Deprecated //待确认
public class ZpdBarCodeImpl implements IBarCode {
	private static final String TAG = "ZpdBarCodeImpl";

	private static String BAR_READ_ACTION = "com.android.action.BARCODE"; // 接收Service广播发来


	BroadcastReceiver mBarCodeBroadcastReceiver;

	Context mContext;



	@Override
	public void start(Context context) throws Exception {

		if (context == null) {
			Log.e(TAG, "context is null in ZpdBarCodeImpl's start");
			return;
		}

		this.mContext = context;
		// 监听

		mBarCodeBroadcastReceiver = new BarCodeBroadcastReceiver();
		IntentFilter barcodeFilter = new IntentFilter();
		barcodeFilter.addAction(BAR_READ_ACTION);

		mContext.registerReceiver(mBarCodeBroadcastReceiver, barcodeFilter);

	}
	@Override
	public void setType(int type) {

	}

	@Override
	public void close() throws Exception {

		if (mBarCodeBroadcastReceiver != null && mContext != null) {
			mContext.unregisterReceiver(mBarCodeBroadcastReceiver);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event, Context context)
			throws Exception {
		return false;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event, Context context)
			throws Exception {
		return false;
	}

	private class BarCodeBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String data = intent.getStringExtra("data");

			Intent it = new Intent(mContext, AnalyseCodeService.class);

			it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, data);
			mContext.startService(it);
		}
	}

}