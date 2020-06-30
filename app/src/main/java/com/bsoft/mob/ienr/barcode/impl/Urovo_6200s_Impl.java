package com.bsoft.mob.ienr.barcode.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanManager;
import android.util.Log;
import android.view.KeyEvent;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.barcode.AnalyseCodeService;
import com.bsoft.mob.ienr.barcode.IBarCode;

/**
 * 基于优博讯 6200s. 监听条码扫描结果，并对条码作解析和预处理。
 */
public class Urovo_6200s_Impl implements IBarCode {

    private static final String ACTION_RECEIVE_SCAN_RESULT = "urovo.rcv.message";

    private UrovoScanReceiver motoScanReceiver;

    private Context mContext;

    private ScanManager mScanManager;

    private boolean scanPowerState;
    private boolean lockTrigglerState;

    @Override
    public void setType(int type) {

    }

    @Override
    public void start(Context context) throws Exception {

        if (context == null) {
            Log.e(Constant.TAG, "context is null in Urovo_6200s_Impl's start");
            return;
        }

        this.mContext = context;

        initScan();
        // 监听
        motoScanReceiver = new UrovoScanReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_RECEIVE_SCAN_RESULT);
        mContext.registerReceiver(motoScanReceiver, filter);

    }

    @Override
    public void close() throws Exception {

        if (mScanManager != null) {
            mScanManager.stopDecode();
        }
        if (mScanManager != null && lockTrigglerState) {
            mScanManager.lockTriggler();
        }

        if (mScanManager != null && !scanPowerState) {
            mScanManager.closeScanner();
        }

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

    public void initScan() {

        mScanManager = new ScanManager();
        mScanManager.openScanner();
        // 扫描头状态，控制开关
        scanPowerState = mScanManager.getScannerState();
        if (!scanPowerState) {
            mScanManager.openScanner();
        }

        // 按键状态
        lockTrigglerState = mScanManager.getTriggerLockState();
        if (!lockTrigglerState) {
            mScanManager.unlockTriggler();// 解锁按键
        }
        lockTrigglerState = mScanManager.getTriggerLockState();

        // 获取输出模式，0为广播模式，1为键盘输出模式
        // outPut = mScanManager.getOutputMode();
        // 设置输出模式，0为广播模式，1为键盘输出模式 （0  禁止 填充输入框，1 可以）
        mScanManager.switchOutputMode(0);

    }

    public class UrovoScanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            // get the source of the data
            byte[] barocode = intent.getByteArrayExtra("barocode");
            int barocodelen = intent.getIntExtra("length", 0);
            byte temp = intent.getByteExtra("barcodeType", (byte) 0);
            String barcodeStr = new String(barocode, 0, barocodelen);

            Intent it = new Intent(mContext, AnalyseCodeService.class);
            it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, barcodeStr);
            mContext.startService(it);
        }
    }

}
