package com.bsoft.mob.ienr.barcode.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.KeyEvent;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.barcode.AnalyseCodeService;
import com.bsoft.mob.ienr.barcode.IBarCode;
import com.bsoft.mob.ienr.contancts.MotoActions;

/**
 * 基于Moto MC40. 监听条码扫描结果，并对条码作解析和预处理。
 */
public class Moto_MC40_Impl implements IBarCode {

    private static final String ACTION_RECEIVE_SCAN_RESULT = "com.bsoft.mob.ienr.MOTO";

    private MotoScanReceiver motoScanReceiver;

    private Context mContext;

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
        motoScanReceiver = new MotoScanReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_RECEIVE_SCAN_RESULT);
        mContext.registerReceiver(motoScanReceiver, filter);

    }

    @Override
    public void close() throws Exception {

        if (motoScanReceiver != null && mContext != null) {
            mContext.unregisterReceiver(motoScanReceiver);
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

    public class MotoScanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // get the source of the data
            // String source = intent.getStringExtra(MotoActions.SOURCE_TAG);

            // check if the data has come from the barcode scanner
            // if (source.equalsIgnoreCase("scanner")) {
            // get the data from the intent
            String data = intent.getStringExtra(MotoActions.DATA_STRING_TAG);
            Intent it = new Intent(mContext, AnalyseCodeService.class);
            it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, data);
            mContext.startService(it);
            // }
        }
    }

}
