package com.bsoft.mob.ienr.view.floatmenu.menu;

import android.view.View;

public abstract class TextFloatMenuItem extends IFloatMenuItem {
    public String textStr;
    public TextFloatMenuItem(int resid) {
        super(resid);
    }
    public TextFloatMenuItem(int resid,String resStr) {
        super(resid);
        textStr = resStr;
    }
}
