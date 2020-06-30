package com.bsoft.mob.ienr.barcode.impl;

import android.content.Context;
import android.view.KeyEvent;

import com.bsoft.mob.ienr.barcode.IBarCode;

public class NoBarCodeImpl implements IBarCode {

    @Override
    public void setType(int type) {

    }

    @Override
    public void start(Context context) throws Exception {

    }

    @Override
    public void close() throws Exception {

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

}
