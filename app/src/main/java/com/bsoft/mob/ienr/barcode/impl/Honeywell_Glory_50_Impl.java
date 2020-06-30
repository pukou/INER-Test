
package com.bsoft.mob.ienr.barcode.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.bsoft.mob.ienr.barcode.AnalyseCodeService;
import com.bsoft.mob.ienr.barcode.IBarCode;

/**
 * Created by louisgeek on 2017/10/17.
 * Glory 50
 */
public class Honeywell_Glory_50_Impl implements IBarCode {
    private static final String TAG = "Honeywell_Glory_50_Impl";

    private static final String INTENT_ACTION_SCAN_RESULT = "com.honeywell.tools.action.scan_result";
    private static final String EXTRA_BARCODE_STRING = "decode_rslt";

    private BroadcastReceiver mBarCodeBroadcastReceiver;

    private Context mContext;

    @Override
    public void setType(int type) {

    }

    @Override
    public void start(Context context) throws Exception {

        if (context == null) {
            Log.e(TAG, "context is null in Honeywell_Glory_50_Impl's start");
            return;
        }

        this.mContext = context;
        // 监听

        mBarCodeBroadcastReceiver = new BarCodeBroadcastReceiver();
        IntentFilter barcodeFilter = new IntentFilter();
        barcodeFilter.addAction(INTENT_ACTION_SCAN_RESULT);
        mContext.registerReceiver(mBarCodeBroadcastReceiver, barcodeFilter);

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

    public class BarCodeBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (INTENT_ACTION_SCAN_RESULT.equals(intent.getAction())) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    String data = bundle.getString(EXTRA_BARCODE_STRING).trim();
                    Intent it = new Intent(mContext, AnalyseCodeService.class);
                    it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, data);
                    mContext.startService(it);
                }
            }
        }
    }

}