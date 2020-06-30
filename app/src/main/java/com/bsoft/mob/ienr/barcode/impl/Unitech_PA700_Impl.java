package com.bsoft.mob.ienr.barcode.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.barcode.AnalyseCodeService;
import com.bsoft.mob.ienr.barcode.IBarCode;
import com.bsoft.mob.ienr.contancts.UnitechActions;

/**
 * unitech PA700
 *
 * @author Tank
 */
public class Unitech_PA700_Impl implements IBarCode {

    private UitechScanReceiver uniScanReceiver;

    private Context mContext;

    @Override
    public void setType(int type) {

    }

    @Override
    public void start(Context context) throws Exception {

        if (context == null) {
            Log.e(Constant.TAG, "context is null in Unitech_PA700_Impl's start");
            return;
        }

        this.mContext = context;

        // 开启
        Intent sendIntent = new Intent(UnitechActions.ACTION_SETTING_SCAN2KEY);
        sendIntent.putExtra(UnitechActions.EXTRA_SETTING_SCAN2KEY_BOOLEAN,
                false);

        // 初始化扫描枪
        Intent sendIntent1 = new Intent(UnitechActions.INIT_INTENT);
        sendIntent1.putExtra(
                UnitechActions.EXTRA_SETTING_SCAN2KEY_INIT_BOOLEAN, true);

        mContext.sendBroadcast(sendIntent);
        mContext.sendBroadcast(sendIntent1);

        // 监听
        uniScanReceiver = new UitechScanReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UnitechActions.ACTION_RECEIVER_DATE);
        mContext.registerReceiver(uniScanReceiver, filter);

    }

    @Override
    public void close() throws Exception {

        if (uniScanReceiver != null && mContext != null) {
            mContext.unregisterReceiver(uniScanReceiver);
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

    public class UitechScanReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (UnitechActions.ACTION_RECEIVER_DATE.equals(intent.getAction())) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    String data = bundle.getString("text").trim();
                    Intent it = new Intent(mContext, AnalyseCodeService.class);
                    it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, data);
                    mContext.startService(it);
                }
            }
        }
    }

}
