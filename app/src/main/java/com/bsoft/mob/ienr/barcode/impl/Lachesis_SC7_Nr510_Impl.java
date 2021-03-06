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

public class Lachesis_SC7_Nr510_Impl implements IBarCode {
    private static final String TAG = "Lachesis_SC7_Nr510_Impl";
    private static final String ACTION_DECODE_DATA = "lachesis_barcode_value_notice_broadcast";
    private static final String EXTRA_BARCODE_STRING = "lachesis_barcode_value_notice_broadcast_data_string";

    private BroadcastReceiver mBarCodeBroadcastReceiver;

    private Context mContext;

    @Override
    public void setType(int type) {

    }

    @Override
    public void start(Context context) throws Exception {
        if (context == null) {
            Log.e(TAG, "context is null in Lachesis_SC7_Nr510_Impl's start");
            return;
        }

        this.mContext = context;
        // 监听
        mBarCodeBroadcastReceiver = new BarCodeBroadcastReceiver();
        IntentFilter barcodeFilter = new IntentFilter();
        barcodeFilter.addAction(ACTION_DECODE_DATA);
        mContext.registerReceiver(mBarCodeBroadcastReceiver, barcodeFilter);
    }

    @Override
    public void close() throws Exception {
        if (mBarCodeBroadcastReceiver != null && mContext != null) {
            mContext.unregisterReceiver(mBarCodeBroadcastReceiver);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event, Context context) throws Exception {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event, Context context) throws Exception {
        return false;
    }

    public class BarCodeBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_DECODE_DATA.equals(intent.getAction())) {
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
