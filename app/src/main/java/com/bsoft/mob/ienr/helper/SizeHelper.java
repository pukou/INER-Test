package com.bsoft.mob.ienr.helper;

import android.view.View;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.util.tools.DimensionTool;
import com.bsoft.mob.ienr.util.tools.SizeTool;

/**
 * Created by Classichu on 2018/1/15.
 */

public class SizeHelper {

    //============ Comm ===========================
    public static int getPaddingBig() {
        return  DimensionTool.getDimensionPx(AppApplication.getInstance(),R.dimen.classic_item_comm_padding_big);
    }
    public static int getPaddingPrimary() {
        return  DimensionTool.getDimensionPx(AppApplication.getInstance(),R.dimen.classic_item_comm_padding_primary);
    }
    public static int getPaddingSecondary() {
        return  DimensionTool.getDimensionPx(AppApplication.getInstance(),R.dimen.classic_item_comm_padding_secondary);
    }
    public static int getPaddingTertiary() {
        return  DimensionTool.getDimensionPx(AppApplication.getInstance(),R.dimen.classic_item_comm_padding_tertiary);
    }
    public static int getPaddingQuaternary() {
        return DimensionTool.getDimensionPx(AppApplication.getInstance(),R.dimen.classic_item_comm_padding_quaternary);
    }

    public static int getMarginPrimary() {
        return DimensionTool.getDimensionPx(AppApplication.getInstance(),R.dimen.classic_item_comm_margin_primary);
    }
    public static int getMarginSecondary() {
        return DimensionTool.getDimensionPx(AppApplication.getInstance(),R.dimen.classic_item_comm_margin_secondary);
    }

    public static int getMarginTertiary() {
        return DimensionTool.getDimensionPx(AppApplication.getInstance(),R.dimen.classic_item_comm_margin_tertiary);
    }

    public static int getMarginQuaternary() {
        return DimensionTool.getDimensionPx(AppApplication.getInstance(),R.dimen.classic_item_comm_margin_quaternary);
    }


    //============ Text ===========================
    public static int getTextPaddingPrimary() {
        return getPaddingSecondary();
    }
   /* public static int getTextPaddingSecondary() {
        return getPaddingQuaternary();
    }*/
    //============ Edit ===========================
    public static int getEditPaddingPrimary() {
        return getPaddingPrimary();
//        return getPaddingBig();
    }
    public static int getEditMinWidth() {
        return SizeTool.dp2px(50);
    }

    public static int getEditMarginPrimary() {
        return getMarginTertiary();
    }
  /*  public static int getEditMarginSecondary() {
        return getMarginQuaternary();
    }*/



    //==============
    public static void setPadding(View view, int size) {
        view.setPadding(SizeTool.dp2px(size), SizeTool.dp2px(size),
                SizeTool.dp2px(size), SizeTool.dp2px(size));
    }

    public static void setPaddingLeftRight(View view, int size) {
        view.setPadding(SizeTool.dp2px(size), view.getPaddingTop(),
                SizeTool.dp2px(size), view.getPaddingBottom());
    }
    public static void setPaddingLeftTopRight(View view, int size) {
        view.setPadding(SizeTool.dp2px(size), SizeTool.dp2px(size),
                SizeTool.dp2px(size), view.getPaddingBottom());
    }

    public static void setPaddingLeftTopRight(View view, int leftRightSize,int topSize) {
        view.setPadding(SizeTool.dp2px(leftRightSize), SizeTool.dp2px(topSize),
                SizeTool.dp2px(leftRightSize), view.getPaddingBottom());
    }

    public static void setPaddingTopBottom(View view, int size) {
        view.setPadding(view.getPaddingLeft(), SizeTool.dp2px(size),
                view.getPaddingRight(), SizeTool.dp2px(size));
    }
}
