package com.bsoft.mob.ienr.util.tools;

import android.content.Context;
import android.content.res.Resources;
import com.bsoft.mob.ienr.AppApplication;
/**
 * Created by louisgeek on 2016/12/5.
 */
public class DimensionTool {
    /**
     *
     * @param resId
     * @return
     */
    @Deprecated
    public static int getDimensionPx(int resId) {
        try {
            return Resources.getSystem().getDimensionPixelOffset(resId);
        } catch (Exception e) {
            return AppApplication.getInstance().getResources().getDimensionPixelOffset(resId);
        }
    }
    /**
     * @param resId
     * @return
     */
    public static int getDimensionPx(Context context, int resId) {
        return context.getResources().getDimensionPixelOffset(resId);
    }
}
