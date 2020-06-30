package com.bsoft.mob.ienr.util.tools;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

/**
 *  Created by louisgeek on 2016/12/5.
 */
public class ThreadTool {

    public static void runOnUiThread(View view, Runnable runnable) {
        view.post(runnable);
    }

    public static void runOnUiThread(Activity activity, Runnable runnable) {
        activity.runOnUiThread(runnable);
    }

    public static void runOnUiThread(Runnable runnable) {
        Handler mHandler = new Handler(Looper.getMainLooper());
        //使用Looper类判断
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            mHandler.post(runnable);
        }
    }
}
