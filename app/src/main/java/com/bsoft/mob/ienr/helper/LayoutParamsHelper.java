package com.bsoft.mob.ienr.helper;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by Classichu on 2018/1/15.
 */

public class LayoutParamsHelper {

    public static LinearLayout buildLinearWrapWrap_V(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(ll_lp);
        return linearLayout;
    }

    public static LinearLayout buildLinearWrapWrap_H(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(ll_lp);
        return linearLayout;
    }

    public static LinearLayout buildLinearMatchWrap_H(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(ll_lp);
        return linearLayout;
    }

    public static LinearLayout buildLinearMatchMatch_H(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(ll_lp);
        return linearLayout;
    }

    public static LinearLayout buildLinearMatchWrap_V(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(ll_lp);
        return linearLayout;
    }

    public static LinearLayout buildLinearMatchMatch_V(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(ll_lp);
        return linearLayout;
    }

    public static LinearLayout buildLinearAutoWrap_H(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        ll_lp.weight = 1.0f;
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(ll_lp);
        return linearLayout;
    }

    public static LinearLayout buildLinearAutoMatch_H(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        ll_lp.weight = 1.0f;
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(ll_lp);
        return linearLayout;
    }

    public static LinearLayout buildLinearAutoWrap_V(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        ll_lp.weight = 1.0f;
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(ll_lp);
        return linearLayout;
    }

    public static LinearLayout buildLinearAutoMatch_V(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        ll_lp.weight = 1.0f;
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(ll_lp);
        return linearLayout;
    }

    public static LinearLayout buildLinearMatchAuto_H(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        ll_lp.weight = 1.0f;
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(ll_lp);
        return linearLayout;
    }

    public static LinearLayout buildLinearMatchAuto_V(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        ll_lp.weight = 1.0f;
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(ll_lp);
        return linearLayout;
    }

    public static LinearLayout buildLinearWrapAuto_H(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        ll_lp.weight = 1.0f;
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(ll_lp);
        return linearLayout;
    }

    public static LinearLayout buildLinearWrapAuto_V(Context context) {
        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 0);
        ll_lp.weight = 1.0f;
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(ll_lp);
        return linearLayout;
    }
}
