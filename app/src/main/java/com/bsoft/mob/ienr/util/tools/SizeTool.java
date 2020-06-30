package com.bsoft.mob.ienr.util.tools;

import android.content.res.Resources;
import android.util.TypedValue;

/**
 * Created by Classichu on 2017/10/30.
 */
public class SizeTool {
    public static int dp2px(float dp) {
        float scale = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static int dp2px_(float dp) {
        float value = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
        return (int) value;
    }

}
