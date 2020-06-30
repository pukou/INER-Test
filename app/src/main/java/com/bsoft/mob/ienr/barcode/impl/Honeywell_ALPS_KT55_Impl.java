
package com.bsoft.mob.ienr.barcode.impl;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.barcode.AnalyseCodeService;
import com.bsoft.mob.ienr.barcode.IBarCode;
import com.scandecode.ScanDecode;
import com.scandecode.inf.ScanInterface;

/**
 * Created by louisgeek on 2017/8/17.
 * ALPS KT55
 */
public class Honeywell_ALPS_KT55_Impl implements IBarCode {

	private ScanInterface scanDecode;
	private Context mContext;
	@Override
	public void setType(int type) {

	}

	@Override
	public void start(Context context) throws Exception {
		if (context == null) {
			Log.e(Constant.TAG, "context is null in Honeywell_ALPS_KT55_Impl's start");
			return;
		}
		mContext=context;
		//
		scanDecode = new ScanDecode(context);
		scanDecode.initService("true");//初始化扫描服务
		scanDecode.getBarCode(new ScanInterface.OnScanListener() {
			@Override
			public void getBarcode(String data) {
				//
				Intent intent = new Intent(mContext, AnalyseCodeService.class);
				intent.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, data);
				mContext.startService(intent);
			}
		});
	}

	@Override
	public void close() throws Exception {
		if (scanDecode!=null){
		scanDecode.onDestroy();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event, Context context) throws Exception {
		if (keyCode==KeyEvent.KEYCODE_F4||keyCode==KeyEvent.KEYCODE_F5){
			scanDecode.starScan();//启动扫描
			return true;
		}
		return false;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event, Context context) throws Exception {
		if (keyCode==KeyEvent.KEYCODE_F4||keyCode==KeyEvent.KEYCODE_F5){
			scanDecode.stopScan();
			return true;
		}
		return false;
	}

}
