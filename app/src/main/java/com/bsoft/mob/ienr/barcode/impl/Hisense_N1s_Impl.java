package com.bsoft.mob.ienr.barcode.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.KeyEvent;

import com.bsoft.mob.ienr.barcode.AnalyseCodeService;
import com.bsoft.mob.ienr.barcode.IBarCode;

/**
 * 海信PDA接口
 */
public class Hisense_N1s_Impl implements IBarCode {
    private static final String TAG = "Hisense_N1s_Impl";
    private static String BAR_READ_ACTION = "android.provider.sdlMessage"; // 接收Service广播发来
    private static final String EXTRA_BARCODE_STRING = "msg";
    // 的条码数据
    private BroadcastReceiver mBarCodeBroadcastReceiver;

    private Context mContext;

    @Override
    public void setType(int type) {

    }

    @Override
    public void start(Context context) throws Exception {
        if (context == null) {
            Log.e(TAG, "context is null in Hisense_N1s_Impl's start");
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
            String barcode = intent.getStringExtra(EXTRA_BARCODE_STRING);
            Intent it = new Intent(mContext, AnalyseCodeService.class);
            it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, barcode);
            mContext.startService(it);
        }
    }

}