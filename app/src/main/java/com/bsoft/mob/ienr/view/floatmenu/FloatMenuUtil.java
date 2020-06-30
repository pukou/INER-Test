package com.bsoft.mob.ienr.view.floatmenu;

import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.WindowManager;


public class FloatMenuUtil {

    public static WindowManager.LayoutParams getMyLayoutParams() {
        return getMyLayoutParams(false);
    }

    public static WindowManager.LayoutParams getMyLayoutParams(boolean backKeyCanHide) {
        WindowManager.LayoutParams mLayoutParams = new WindowManager.LayoutParams();
        /** 旧版本 2018-04-10 21:56:28
         *   if (backKeyCanHide) {
         mLayoutParams.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
         | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
         } else {
         mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
         | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
         }

         */
        if (backKeyCanHide) {
            //超出屏幕
            mLayoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            // 反转FLAG_NOT_FOCUSABLE的交互状态.
            //即, 如果同时设置了本flag和FLAG_NOT_FOCUSABLE, 则窗口表现为需要同输入法交互, 同时会被至于输入法之下
            //如果设置了本flag而没有设置FLAG_NOT_FOCUSABLE, 则窗口表现为不需要同输入法交互, 同时会被至于输入法之上
            //| WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
            //如果设置了FLAG_NOT_TOUCH_MODAL, 那么可以同时设置此flag来接收窗口之外发生的MotionEvent.ACTION_OUTSIDE事件
            //注意, 你不会接收到完整的down/move/up手势, 只会接收到按下位置的ACTION_OUTSIDE事件
            //| WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
            ;
        } else {
            //超出屏幕
            mLayoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
                    //聚焦
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;//FLAG_NOT_TOUCH_MODAL 同时会被启用
        }

        final int sdkInt = Build.VERSION.SDK_INT;
        //
        if (sdkInt < Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            //15/4.0.3  以下不考虑了
        } else if (sdkInt == Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            //15/4.0.3  会导致 dialog 显示不出来 所以去掉
//                mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else if (sdkInt == Build.VERSION_CODES.JELLY_BEAN) {
            //16/4.1
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else if (sdkInt == Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //17/4.2  会导致 dialog 显示不出来 所以去掉
//                mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else if (sdkInt == Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //18/4.3
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else if (sdkInt == Build.VERSION_CODES.KITKAT) {
            //19/4.4  会导致 dialog 显示不出来 所以去掉
//             mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else if (sdkInt == Build.VERSION_CODES.KITKAT_WATCH) {
            //20/4.4w
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else if (sdkInt == Build.VERSION_CODES.LOLLIPOP) {
            //21/5.0
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else if (sdkInt == Build.VERSION_CODES.LOLLIPOP_MR1) {
            //22/5.1  dialog 可用
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else if (sdkInt == Build.VERSION_CODES.M) {
            //23/6.0  dialog 可用
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else if (sdkInt == Build.VERSION_CODES.N) {
            //24/7.0
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else if (sdkInt == Build.VERSION_CODES.N_MR1) {
            //25/7.1 dialog 可用  ！！！TYPE_PHONE
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        } else if (sdkInt == Build.VERSION_CODES.O) {
            //26/8.0  未测试
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else if (sdkInt == Build.VERSION_CODES.O_MR1) {
            //27/8.1 未测试
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            //27/8.1 以后
            mLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        }
        mLayoutParams.format = PixelFormat.RGBA_8888;
        // 悬浮窗默认显示以左上角为起始坐标
        mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        return mLayoutParams;
    }
}
