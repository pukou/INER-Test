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
 * 识凌G3 ,需要Bn_V1.5.03-r 程序（默认ROM包已安装）
 */
public class BayNexus_G03_Impl implements IBarCode {

    private static String BAR_READ_ACTION = "SYSTEM_BAR_READ"; // 接收Service广播发来
    // 的条码数据
    // RFID支持 start01
    private static String RFID_READ_ACTION = "SYSTEM_RFID_READ"; // 接受RFID广播发来的条码数据
    // RDID支持 end01

    private BroadcastReceiver mBarCodeBroadcastReceiver;

    private Context mContext;

    @Override
    public void setType(int type) {

    }

    @Override
    public void start(Context context) throws Exception {

        if (context == null) {
            Log.e(Constant.TAG, "context is null in BayNexus_G03_Impl's start");
            return;
        }

        this.mContext = context;
        // 监听

        mBarCodeBroadcastReceiver = new BarCodeBroadcastReceiver();
        IntentFilter barcodeFilter = new IntentFilter();
        barcodeFilter.addAction(BAR_READ_ACTION);
        // RFID支持 start02
        barcodeFilter.addAction(RFID_READ_ACTION);
        // RFID支持 end02
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
            String data = "";
            // RFID支持 start03
            Intent it = new Intent(mContext, AnalyseCodeService.class);
            if (intent.getAction().equals(BAR_READ_ACTION)) {
                data = intent.getStringExtra("BAR_VALUE");
                it.putExtra(AnalyseCodeService.SCAN_TYPE, 0);
            } else if (intent.getAction().equals(RFID_READ_ACTION)) {
                data = intent.getStringExtra("RFID_VALUE");
                it.putExtra(AnalyseCodeService.SCAN_TYPE, 1);
            }
            // RFID支持 end03
            it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, data);
            mContext.startService(it);
        }
    }

}