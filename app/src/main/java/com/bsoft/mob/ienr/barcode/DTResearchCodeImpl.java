package com.bsoft.mob.ienr.barcode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.KeyEvent;
@Deprecated //待确认
public class DTResearchCodeImpl implements IBarCode {

	private Context mContext;

	private DTResearchReceiver mReceiver;

	public static final String ACTION_RECEIVE_SCAN_RESULT = "com.dtr.action.scanner";

	@Override
	public void setType(int type) {

	}

	@Override
	public void start(Context context) throws Exception {
		mContext = context;
		// 监听
		mReceiver = new DTResearchReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_RECEIVE_SCAN_RESULT);
		mContext.registerReceiver(mReceiver, filter);

	}

	@Override
	public void close() throws Exception {
		if (mReceiver != null) {
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

	public class DTResearchReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			String strData = intent.getExtras().getString("scanner");

			Intent it = new Intent(mContext, AnalyseCodeService.class);
			it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, strData);
			mContext.startService(it);
		}
	}

}
