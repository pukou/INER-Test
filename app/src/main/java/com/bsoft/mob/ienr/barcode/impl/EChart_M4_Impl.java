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

/**
 * 基于中标软件M4. 监听条码扫描结果，并对条码作解析和预处理。
 */
public class EChart_M4_Impl implements IBarCode {

    private static final String ACTION_RECEIVE_SCAN_RESULT = "cs2c.com.cn.serialscan";


    private EChartScanReceiver scanReceiver;

    private Context mContext;


    @Override
    public void setType(int type) {

    }

    @Override
    public void start(Context context) throws Exception {

        if (context == null) {
            Log.e(Constant.TAG, "context is null in EChart_M4_Impl's start");
            return;
        }

        this.mContext = context;


        // 监听
        scanReceiver = new EChartScanReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_RECEIVE_SCAN_RESULT);
        mContext.registerReceiver(scanReceiver, filter);

    }

    @Override
    public void close() throws Exception {

        if (scanReceiver != null && mContext != null) {
            mContext.unregisterReceiver(scanReceiver);
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

    public class EChartScanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            // get the source of the data
            String barcodeStr = (String) intent.getStringExtra("serial");

            Intent it = new Intent(mContext, AnalyseCodeService.class);
            it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, barcodeStr);
            mContext.startService(it);
        }
    }

}

