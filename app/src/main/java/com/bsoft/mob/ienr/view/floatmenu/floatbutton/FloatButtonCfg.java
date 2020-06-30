package com.bsoft.mob.ienr.view.floatmenu.floatbutton;

import android.graphics.drawable.Drawable;

public class FloatButtonCfg {
    public Drawable mIcon;
    public int mSize;
    public int mEdge_hide_delayMillis;
    public float mEdge_hide_width_percent;
    public int mFirstShowPlace;
    public int mFirstShowPlaceYOffset;

    public FloatButtonCfg(int size, Drawable icon) {
        this(size, icon, 0.65f);
    }

    public FloatButtonCfg(int size, Drawable icon, float edge_hide_percent) {
        //方便显示基础的 Snackbar  94px
        this(size, icon, 2 * 1000, edge_hide_percent, LEFT_BOTTOM,94);
    }

    public FloatButtonCfg(int size, Drawable icon, int edge_hide_delayMillis, float edge_hide_width_percent, int firstShowPlace,int firstShowPlaceYOffset) {
        mSize = size;
        mIcon = icon;
        mEdge_hide_delayMillis = edge_hide_delayMillis;
        mEdge_hide_width_percent = edge_hide_width_percent;
        mFirstShowPlace = firstShowPlace;
        mFirstShowPlaceYOffset = firstShowPlaceYOffset;
    }

    public static final int LEFT_TOP = 1;
    public static final int LEFT_CENTER = 2;
    public static final int LEFT_BOTTOM = 3;
    public static final int RIGHT_TOP = 4;
    public static final int RIGHT_CENTER = 5;
    public static final int RIGHT_BOTTOM = 6;
}
