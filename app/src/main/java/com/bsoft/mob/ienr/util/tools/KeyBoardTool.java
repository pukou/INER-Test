/**
 * @version 1.0  2015年1月27日
 */
package com.bsoft.mob.ienr.util.tools;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 键盘操作工具类
 *
 * @author louisgeek
 * 2016年9月22日13:21:20
 * 避免输入法面板遮挡
 * 在manifest.xml中activity中设置
 * android:windowSoftInputMode="adjustPan"
 */
/** 避免输入法面板遮挡
 在manifest.xml中activity中设置
 android:windowSoftInputMode="adjustPan"
 */

/**
 * 不自动弹出键盘
 * 方法1
 * 在mainfest文件中把对应的activity设置
 android:windowSoftInputMode="stateHidden" 或者android:windowSoftInputMode="stateUnchanged"。
 方法2
 让不是输入框的view requsetFocus
 */
public class KeyBoardTool {

    /**
     * 动态隐藏软键盘
     *
     * @param activity activity
     */
    @Deprecated
    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            /**
             * flag有0，HIDE_IMPLICIT_ONLY 和 HIDE_NOT_ALWAYS
             * HIDE_IMPLICIT_ONLY 当前的软键盘应当只在其不是被用户显式的显示的时候才隐藏
             * HIDE_NOT_ALWAYS
             */
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    /**
     * 动态隐藏软键盘
     *
     * @param view 视图
     */
    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /**
     * 动态显示软键盘
     *
     * @param view
     */
    public static void showKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.requestFocus();
            /**
             * flag有0，SHOW_IMPLICIT 和 SHOW_FORCED
             * SHOW_IMPLICIT  表示隐式的显示，非用户显式的显示
             * SHOW_FORCED   表示强制显示
             */
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }

    /**
     * 动态显示软键盘
     *
     * @param activity activity
     */
    @Deprecated
    public static void showKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm != null) {
            /**
             * flag有0，SHOW_IMPLICIT 和 SHOW_FORCED
             * SHOW_IMPLICIT  表示隐式的显示，非用户显式的显示
             * SHOW_FORCED   表示强制显示
             */
            imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }


    /**
     * 切换键盘显示与否状态
     */
    public static void toggleSoftInput(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }
    /**
     * 切换键盘显示与否状态
     */
    public static void toggleSoftInput2(View view) {
        // boolean isOpen=imm.isActive();
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

}
