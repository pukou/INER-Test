package com.bsoft.mob.ienr.view.floatmenu.runner;


import android.content.Context;

public interface IFloatAction {
    Context getContext();

    void onMove(int lastX, int lastY, int curX, int curY);

    void onDone();

    boolean post(Runnable runnable);

    boolean removeCallbacks(Runnable action);
}
