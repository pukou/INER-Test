package com.bsoft.mob.ienr.barcode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.contancts.EiiActions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 易迈海
 * unitech Eii
 * 
 * @author zhw
 */
@Deprecated //待确认
public class EiiBarCodeImpl implements IBarCode {

	EiiScanReceiver eiiScanReceiver;

	Context mContext;

	@Override
	public void setType(int type) {

	}

	@Override
	public void start(Context context) throws Exception {

		if (context == null) {
			Log.e(Constant.TAG, "context is null in EiiBarCodeImpl's start");
			return;
		}

		this.mContext = context;

		// 开启
		//Intent sendIntent = new Intent(EiiActions.ACTION_SETTING_SCAN2KEY);
		///sendIntent
		//		.putExtra(EiiActions.EXTRA_SETTING_SCAN2KEY_BOOLEAN, true);
		//mContext.sendBroadcast(sendIntent);

		// 监听
		eiiScanReceiver = new EiiScanReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(EiiActions.ACTION_RECEIVER_DATE);
		mContext.registerReceiver(eiiScanReceiver, filter);

	}

	@Override
	public void close() throws Exception {

		if (eiiScanReceiver != null && mContext != null) {
			mContext.unregisterReceiver(eiiScanReceiver);
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

	public class EiiScanReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (EiiActions.ACTION_RECEIVER_DATE.equals(intent.getAction())) {
				Bundle bundle = intent.getExtras();
				if (bundle != null) {
				//	String data = bundle.getString("text");
					String data = bundle.getString("value");
					Pattern p = Pattern.compile("\\s*|\t|\r|\n");  // 去掉多余的回车符号  //zhw
			        Matcher m = p.matcher(data);          
			           data = m.replaceAll(""); 
					Intent it = new Intent(mContext, AnalyseCodeService.class);
					it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, data);
					mContext.startService(it);
				}
			}
		}
	}

}
