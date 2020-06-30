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
 * Cilico 富立叶 ALPS JW16-05
 */
public class Cilico_ALPS_JW16_05_Impl implements IBarCode {
    /**
     *
     */
    private static final String BARCODE_ACTION = "com.barcode.sendBroadcast";
    /**
     *
     */
    private static final String BARCODE_PARAM = "BARCODE";

    private Context mContext;

    private ScanBroadcastReceiver mReceiver;

    @Override
    public void setType(int type) {

    }

    @Override
    public void start(Context context) throws Exception {
        this.mContext = context;

        // 监听
        mReceiver = new ScanBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BARCODE_ACTION);
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

    public class ScanBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (BARCODE_ACTION.equals(intent.getAction())) {
                String data = intent
                        .getStringExtra(BARCODE_PARAM);
                Log.e(Constant.TAG, data);
                Intent it = new Intent(mContext, AnalyseCodeService.class);
                it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, data);
                mContext.startService(it);
            }
        }
    }

}
