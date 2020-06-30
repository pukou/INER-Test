package com.bsoft.mob.ienr.view.floatmenu.menu;

import android.view.View;

public abstract class IFloatMenuItem {
    public int resid;

    public IFloatMenuItem(int resid) {
        this.resid = resid;
    }

    public abstract void actionClick(View view, int resid);
}
