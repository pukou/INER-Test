package com.bsoft.mob.ienr.barcode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.KeyEvent;

import com.bsoft.mob.ienr.Constant;

/**
 * 基于希伯姆PDA. 监听条码扫描结果，并对条码作解析和预处理。
 */
@Deprecated //待确认
public class SimBarcodeImpl implements IBarCode {

	public static final String ACTION_RECEIVE_SCAN_RESULT = "com.sim.action.SIMSCAN";

	/*
	 * 
	 */

	SimScanReceiver simScanReceiver;

	Context mContext;

	@Override
	public void setType(int type) {

	}

	@Override
	public void start(Context context) throws Exception {

		if (context == null) {
			Log.e(Constant.TAG, "context is null in Moto_MC40_Impl's start");
			return;
		}

		this.mContext = context;

		// 监听
		simScanReceiver = new SimScanReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_RECEIVE_SCAN_RESULT);
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		mContext.registerReceiver(simScanReceiver, filter);

	}

	@Override
	public void close() throws Exception {

		if (simScanReceiver != null && mContext != null) {
			mContext.unregisterReceiver(simScanReceiver);
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

	public class SimScanReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			// get the source of the data
			this.abortBroadcast();
			final String barcodeStr = intent.getStringExtra("value");
			Intent it = new Intent(mContext, AnalyseCodeService.class);
			it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, barcodeStr);
			mContext.startService(it);
		}
	}

}
