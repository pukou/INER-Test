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
 * 基于东软S511. 监听条码扫描结果，并对条码作解析和预处理。
 */
public class Neusoft_S511_Impl implements IBarCode {
    private static final String TAG = "Neusoft_S511_Impl";

    private static final String ACTION_RECEIVE_SCAN_RESULT = "com.neusoft.action.scanner.read";


    private NeuScanReceiver NeuScanReceiver;

    private Context mContext;

    @Override
    public void setType(int type) {

    }

    @Override
    public void start(Context context) throws Exception {

        if (context == null) {
            Log.e(TAG, "context is null in Neusoft_S511_Impl's start");
            return;
        }

        this.mContext = context;

        // 监听
        NeuScanReceiver = new NeuScanReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_RECEIVE_SCAN_RESULT);
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        mContext.registerReceiver(NeuScanReceiver, filter);

    }

    @Override
    public void close() throws Exception {

        if (NeuScanReceiver != null && mContext != null) {
            mContext.unregisterReceiver(NeuScanReceiver);
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

    public class NeuScanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            // get the source of the data
            this.abortBroadcast();
            final String barcodeStr = intent.getStringExtra("scanner_value");
            Intent it = new Intent(mContext, AnalyseCodeService.class);
            it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, barcodeStr);
            mContext.startService(it);
        }
    }

}
