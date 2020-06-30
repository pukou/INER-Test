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
 * 北京首信锐普 C568
 */
public class Copipod_C568_Impl implements IBarCode {

    private static final String ACTION = "com.android.action.BARCODE_DATA";

    private CopipodScanReceiver mReceiver;

    private Context mContext;

    @Override
    public void setType(int type) {

    }

    @Override
    public void start(Context context) throws Exception {

        if (context == null) {
            Log.e(Constant.TAG, "context is null in Cilico_ALPS_JW16_05_Impl's start");
            return;
        }
        this.mContext = context.getApplicationContext();
        // 监听
        mReceiver = new CopipodScanReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION);
        mContext.registerReceiver(mReceiver, filter);
    }

    @Override
    public void close() throws Exception {
        if (mReceiver != null && mContext != null) {
            mContext.unregisterReceiver(mReceiver);
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

    public class CopipodScanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (ACTION.equals(intent.getAction())) {
                String data = intent.getStringExtra("data");
                Intent it = new Intent(mContext, AnalyseCodeService.class);
                it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, data);
                mContext.startService(it);
            }
        }
    }

}
