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
import com.bsoft.mob.ienr.model.PDAInfo;
import com.bsoft.mob.ienr.util.StringUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * 自定义条码扫描
 */
public class CustomBarcodeImpl implements IBarCode {

    private static String BAR_READ_ACTION = "SYSTEM_BAR_READ"; // 接收Service广播发来
    private static String BAR_READ_DATA = "DATA";
    // 的条码数据
    // RFID支持 start01
    private static String RFID_READ_ACTION = "SYSTEM_RFID_READ"; // 接受RFID广播发来的条码数据
    private static String RFID_READ_DATA = "DATA2";
    // RDID支持 end01

    private BroadcastReceiver mBarCodeBroadcastReceiver;

    private Context mContext;
    private PDAInfo pdaInfo;


    public CustomBarcodeImpl(){
        pdaInfo = null;
    }
    public CustomBarcodeImpl(PDAInfo pdaInfo){
        this.pdaInfo = pdaInfo;
        BAR_READ_ACTION = pdaInfo.ACTION;
        BAR_READ_DATA = pdaInfo.DATA;
        RFID_READ_ACTION = pdaInfo.ACTION2;
        RFID_READ_DATA = pdaInfo.DATA2;
    }

    @Override
    public void setType(int type) {

    }

    @Override
    public void start(Context context) throws Exception {
        if(pdaInfo == null){
            Log.e(Constant.TAG, "PdaInfo is null in start");
            return;
        }

        if (context == null) {
            Log.e(Constant.TAG, "context is null in " + pdaInfo.MANUER + "_" + pdaInfo.MODEL + "_Impl's start");
            return;
        }

        this.mContext = context;
        // 监听

        mBarCodeBroadcastReceiver = new BarCodeBroadcastReceiver();
        IntentFilter barcodeFilter = new IntentFilter();

        if(!StringUtils.isEmpty(BAR_READ_ACTION)){
            barcodeFilter.addAction(BAR_READ_ACTION);
        }
        // RFID支持 start02
        if(!StringUtils.isEmpty(RFID_READ_ACTION)) {
            barcodeFilter.addAction(RFID_READ_ACTION);
        }
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
                data = intent.getStringExtra(BAR_READ_DATA);
                it.putExtra(AnalyseCodeService.SCAN_TYPE, 0);
            } else if (intent.getAction().equals(RFID_READ_ACTION)) {
                data = intent.getStringExtra(RFID_READ_DATA);
                it.putExtra(AnalyseCodeService.SCAN_TYPE, 1);
            }
            // RFID支持 end03
            it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, data);
            mContext.startService(it);
        }
    }

}