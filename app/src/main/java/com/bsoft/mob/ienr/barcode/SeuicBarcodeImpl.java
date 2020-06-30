package com.bsoft.mob.ienr.barcode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.KeyEvent;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.contancts.SeuicActions;

/**
 * 基于江苏东大集成PDA. 监听条码扫描结果，并对条码作解析和预处理。
 */
@Deprecated //待确认
public class SeuicBarcodeImpl implements IBarCode {

	Context mContext;

	SeuicScanReceiver mReceiver;

	@Override
	public void setType(int type) {

	}

	@Override
	public void start(Context context) throws Exception {
		this.mContext = context;

		// 监听
		mReceiver = new SeuicScanReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(SeuicActions.ACTION_RECEIVER_DATE);
		mContext.registerReceiver(mReceiver, filter);
	}

	@Override
	public void close() throws Exception {

		if (mReceiver != null && mContext != null) {
			mContext.unregisterReceiver(mReceiver);
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

	public class SeuicScanReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (SeuicActions.ACTION_RECEIVER_DATE.equals(intent.getAction())) {
				String data = intent
						.getStringExtra(SeuicActions.EXTRA_RECEIVER_DATE);
				Log.e(Constant.TAG, data);
				Intent it = new Intent(mContext, AnalyseCodeService.class);
				it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, data);
				mContext.startService(it);
			}
		}
	}

}
