package com.bsoft.mob.ienr.barcode.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;

import com.bsoft.mob.ienr.barcode.AnalyseCodeService;
import com.bsoft.mob.ienr.barcode.IBarCode;

/**
 * 深圳成为科技有限公司 C70
 * @author MengDW
 *
 */
public class ChainWay_Wtk_C70_Impl implements IBarCode {
	public final static String ACTION_RECEIVER_DATA = "com.scanner.broadcast";
	public final static String EXTRA_RECEIVER_DATA = "data";
	Context mContext;

	BarcodeReceiver 	mReceiver;

	@Override
	public void setType(int type) {

	}

	@Override
	public void start(Context context) throws Exception {
		this.mContext = context;

		// 监听
		mReceiver = new BarcodeReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_RECEIVER_DATA);
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

	public class BarcodeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (ACTION_RECEIVER_DATA.equals(intent.getAction())) {
				String data = intent.getStringExtra(EXTRA_RECEIVER_DATA);
//				Log.e(Config.TAG, data);
				Intent it = new Intent(mContext, AnalyseCodeService.class);
				it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, data);
				mContext.startService(it);
			}
		}
	}

}
