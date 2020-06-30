/**
 * @Title: BSToast.java
 * @Description: 自定义的Toast
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-11-25 上午9:30:07
 * @version V1.0
 */
package com.bsoft.mob.ienr.view;

import android.content.Context;
import android.widget.Toast;

/**
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @ClassName: BSToast
 * @Description: 自定义的Toast
 * @date 2015-11-25 上午9:30:07
 */
public class BSToast {
    private static String oldMsg = "";
    protected static Toast toast = null;
    private static long oneTime = 0;
    private static long twoTime = 0;

    public static final int LENGTH_LONG = 1;
    public static final int LENGTH_SHORT = 0;

    /**
     * @param @param context 上下文
     * @param @param s 要显示的字符串
     * @param @param duration 要显示的时长，值为：LENGTH_LONG或LENGTH_SHORT
     * @return void
     * @throws
     * @Title: showToast
     * @Description: 根据传入的字符串显示toast
     */
    public static void showToast(Context context, String s, int duration) {
        try {
            if (toast == null) {
                toast = Toast.makeText(context, s, duration);
                toast.show();

                oneTime = System.currentTimeMillis();
            } else {
                twoTime = System.currentTimeMillis();
                if (s.equals(oldMsg)) {
                    if (twoTime - oneTime > duration) {
                        toast.show();
                    }
                } else {
                    oldMsg = s;
                    toast.setText(s);
                    toast.show();
                }
            }
            oneTime = twoTime;
        } catch (RuntimeException e) {
            e.getMessage();
        }
    }

    /**
     * @param @param context 上下文
     * @param @param resId 资源id
     * @param @param duration 要显示的时长，值为：LENGTH_LONG或LENGTH_SHORT
     * @return void 返回类型
     * @throws
     * @Title: showToast
     * @Description: 根据配置的字符串资源id显示toast
     */
    public static void showToast(Context context, int resId, int duration) {
        showToast(context, context.getString(resId), duration);
    }

}
