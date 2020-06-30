package com.bsoft.mob.ienr.barcode.impl;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.barcode.AnalyseCodeService;
import com.bsoft.mob.ienr.barcode.IBarCode;
import com.landicorp.android.eptapi.DeviceService;
import com.landicorp.android.eptapi.device.InnerScanner;

/**
 * Created by louisgeek on 2018/1/17.
 * Liandi_P950_V2
 */

public class Landi_P950_V2_Impl implements IBarCode {
    private static final String TAG = "Landi_P950_V2_Impl";
    private static final int SCAN_TIMEOUT = 30;
    private Context mContext;

    @Override
    public void setType(int type) {

    }

    @Override
    public void start(Context context) throws Exception {
        mContext = context;
        if (context == null) {
            Log.e(TAG, "context is null in Landi_P950_V2_Impl's start");
            return;
        }
        DeviceService.login(context);
    }

    @Override
    public void close() throws Exception {
        DeviceService.logout();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event, Context context)
            throws Exception {
        if (keyCode==80) {//侧面2个扫码键  键盘上的Scan键的keyCode是119
            startScan(SCAN_TIMEOUT);
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event, Context context)
            throws Exception {
        if (keyCode==80) {//侧面2个扫码键  键盘上的Scan键的keyCode是119
            stopScan();
            return true;
        }
        return false;
    }

    private InnerScanner scanner = InnerScanner.getInstance();
    private boolean started = false;

    public void startScan(int timeout) {
        if (started) {
            return;
        }
        started = true;
        scanner.setOnScanListener(new InnerScanner.OnScanListener() {
            @Override
            public void onScanSuccess(String scan) {
                 started = false;
                //
                String data = scan;
                //
                Log.e(Constant.TAG, data);
                Intent it = new Intent(mContext, AnalyseCodeService.class);
                it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, data);
                mContext.startService(it);
            }

            @Override
            public void onScanFail(int i) {
                started = false;
                //
                //error 错误码可参考 OnScanListener.ERROR_xxx
                Log.e(Constant.TAG, "CODE:"+i);
            }

            @Override
            public void onCrash() {
                started = false;
                //
                Log.e(Constant.TAG, "onCrash:");
            }
        });
        scanner.start(timeout);
    }

    public void stopScan()

    {
        started = false;
        scanner.stopListen();
        scanner.stop();
    }
}
