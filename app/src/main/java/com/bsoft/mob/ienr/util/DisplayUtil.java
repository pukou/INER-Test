package com.bsoft.mob.ienr.util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 与显示相关辅助API
 * Created by hy on 14-3-10.
 */
@Deprecated
public class DisplayUtil {

    /**
     * 获取终端设备像素宽度
     *
     * @param mContxt
     * @return 终端设备宽度
     */
    public static int getWidthPixels(Context mContxt) {

        if (mContxt == null) {
            return 0;
        }
        WindowManager localWindowManager = (WindowManager) mContxt.getSystemService("window");
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        localWindowManager.getDefaultDisplay().getMetrics(
                localDisplayMetrics);
        return localDisplayMetrics.widthPixels;

    }

    /**
     * 获取终端设备像素高度
     *
     * @param mContent
     * @return 终端设备高度
     */
    public static int getHeightPixels(Context mContent) {

        if (mContent == null) {
            return 0;
        }
        WindowManager localWindowManager = (WindowManager) mContent.getSystemService("window");
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        localWindowManager.getDefaultDisplay().getMetrics(
                localDisplayMetrics);
        return localDisplayMetrics.heightPixels;
    }
}
