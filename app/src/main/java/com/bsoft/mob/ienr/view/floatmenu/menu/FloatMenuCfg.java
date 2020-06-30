package com.bsoft.mob.ienr.view.floatmenu.menu;

import com.bsoft.mob.ienr.view.floatmenu.runner.IFloatMenuExpandAction;

public class FloatMenuCfg {
    public int mSize;
    public int mItemSize;
    public boolean mBackKeyCanHide;

    public FloatMenuCfg(int size, int itemSize) {
        this(size, itemSize, true);
    }

    public FloatMenuCfg(int size, int itemSize, boolean backKeyCanHide) {
        mSize = size;
        mItemSize = itemSize;
        mBackKeyCanHide = backKeyCanHide;
    }
}
