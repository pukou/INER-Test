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
 * 基于赫盛5020. 监听条码扫描结果，并对条码作解析和预处理。
 */
public class Mx_5020_Impl implements IBarCode {

    private static final String ACTION_RECEIVE_SCAN_RESULT = "com.android.server.scannerservice.broadcast";

    private MxScanReceiver scanReceiver;

    private Context mContext;

    @Override
    public void setType(int type) {

    }

    @Override
    public void start(Context context) throws Exception {

        if (context == null) {
            Log.e(Constant.TAG, "context is null in Mx_5020_Impl's start");
            return;
        }

        this.mContext = context;

        // 设置底层接口
        Intent intent = new Intent("com.android.server.scannerservice.onoff");
        intent.putExtra("scanneronoff", 1);
        mContext.sendBroadcast(intent);
        // 改变上层图标
        intent = new Intent("android.intent.action.ACTION_SCANNER_ENABLE");
        intent.putExtra("state", true);
        mContext.sendBroadcast(intent);

        // 监听
        scanReceiver = new MxScanReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_RECEIVE_SCAN_RESULT);
        mContext.registerReceiver(scanReceiver, filter);

    }

    @Override
    public void close() throws Exception {

        if (scanReceiver != null && mContext != null) {
            mContext.unregisterReceiver(scanReceiver);
        }

        // 设置底层接口
        Intent intent = new Intent("com.android.server.scannerservice.onoff");
        intent.putExtra("scanneronoff", 0);
        mContext.sendBroadcast(intent);
        // 改变上层图标
        intent = new Intent("android.intent.action.ACTION_SCANNER_ENABLE");
        intent.putExtra("state", false);
        mContext.sendBroadcast(intent);
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

    public class MxScanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(ACTION_RECEIVE_SCAN_RESULT)) {
                String barcode = intent.getExtras().getString("scannerdata");
                Intent it = new Intent(mContext, AnalyseCodeService.class);
                it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, barcode);
                mContext.startService(it);
            }

        }
    }

}
