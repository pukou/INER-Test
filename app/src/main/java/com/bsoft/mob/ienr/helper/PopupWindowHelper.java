package com.bsoft.mob.ienr.helper;

import android.os.Build;
import android.support.v4.widget.PopupWindowCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.classichu.popupwindow.util.ScreenUtil;

/**
 * Created by classichu on 2018/3/23.
 */

public class PopupWindowHelper {
    public static void show(PopupWindow popupWindow, View anchor) {
        show(popupWindow,anchor,0);
    }
    public static void show(PopupWindow popupWindow, View anchor, int yoff) {
        boolean isNeedShowAsTop = isNeedShowAsTop(popupWindow, anchor);
        if (isNeedShowAsTop) {
            showAsTop_Left(popupWindow, anchor, yoff);
        } else {
            showAsBottom_Left(popupWindow, anchor, yoff);
        }
    }

    private static void showAsBottom_Left(PopupWindow popupWindow, View anchor, int yoff) {
        showAsDropDownCompat(popupWindow, anchor, 0, yoff, Gravity.TOP | Gravity.START);
    }

    private static void showAsTop_Left(PopupWindow popupWindow, View anchor, int yoff) {
        int contentViewHeight = getContentViewHeight(popupWindow);
        yoff = -(anchor.getHeight() + contentViewHeight) + yoff;
        showAsDropDownCompat(popupWindow, anchor, 0, yoff, Gravity.TOP | Gravity.START);
    }

    private static void showAsDropDownCompat(PopupWindow popupWindow, View anchor, int xoff, int yoff, int gravity) {
        if (Build.VERSION.SDK_INT == 24 || Build.VERSION.SDK_INT == 25 || Build.VERSION.SDK_INT == 26) {
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            if (popupWindow.getHeight() == ViewGroup.LayoutParams.MATCH_PARENT) {
                int screenHeight = ScreenUtil.getScreenHeight();
                popupWindow.setHeight(screenHeight - y - anchor.getHeight() - yoff);
            }
        }
       /* if (isNeedShowAsTop){
            //临时方案 显示上面太少
            int screenHeight = ScreenUtil.getScreenHeight();
            popupWindow.setHeight((int) (screenHeight * 0.5f));
        }*/
        PopupWindowCompat.showAsDropDown(popupWindow, anchor, xoff, yoff, gravity);
    }

    private static int getContentViewHeight(PopupWindow popupWindow) {
        if (popupWindow.getHeight() > 0) {//如果是精确高度
            return popupWindow.getHeight();//注意不是contentView.getHeight()
        }
        View contentView = popupWindow.getContentView();
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int contentViewHeight = contentView.getMeasuredHeight();
        return contentViewHeight;
    }

    private static int getContentViewWidth(PopupWindow popupWindow) {
        if (popupWindow.getWidth() > 0) {//如果是精确高度
            return popupWindow.getWidth();//注意不是contentView.getWidth()
        }
        View contentView = popupWindow.getContentView();
        contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int contentViewWidth = contentView.getMeasuredWidth();
        return contentViewWidth;
    }

    private static boolean isNeedShowAsTop(PopupWindow popupWindow, View anchor) {
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        int x = location[0];
        int y = location[1];
        //
        int contentViewHeight = getContentViewHeight(popupWindow);
        //
        int screenHeight = ScreenUtil.getScreenHeight();
        if (screenHeight - (y + anchor.getHeight()) < contentViewHeight) {//下面的高度不够显示全部
            if (y < contentViewHeight) {//但是上面也不够，那还是显示下面
                return false;
            }
            return true;
        }
        return false;
    }


}
