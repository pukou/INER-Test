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
 * 新大陆 Newland NLS-MT90
 */
public class Newland_NLS_MT90_Impl implements IBarCode {
    //条码
    private static final String EXTRA_SCAN_RESULT_ONE_BYTES = "scan_result_one_bytes";
    //没用上
    private static final String EXTRA_SCAN_RESULT_TWO_BYTES = "scan_result_two_bytes";
    /**
     *
     */
    private static final String BARCODE_ACTION = "com.android.action.SEND_SCAN_RESULT";
    /**
     *
     */
    //    private static final String BARCODE_PARAM = "BARCODE";
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
                byte[] bvalue1 = intent.getByteArrayExtra(EXTRA_SCAN_RESULT_ONE_BYTES);
                byte[] bvalue2 = intent.getByteArrayExtra(EXTRA_SCAN_RESULT_TWO_BYTES);
                String value = "";
                String value2 = "";
                try {
                    if (bvalue1 != null) {
                        value = new String(bvalue1, "GBK");
                    }
                    if (bvalue2 != null) {
                        value2 = new String(bvalue1, "GBK");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //
              /*  String data = intent
                        .getStringExtra(BARCODE_PARAM);*/
                String data = value;
                Log.e(Constant.TAG, data);
                Intent it = new Intent(mContext, AnalyseCodeService.class);
                it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, data);
                mContext.startService(it);
            }
        }
    }

}
