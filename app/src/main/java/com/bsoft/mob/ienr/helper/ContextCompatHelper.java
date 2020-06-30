package com.bsoft.mob.ienr.helper;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import com.bsoft.mob.ienr.R;

/**
 * Created by Classichu on 2017/11/30.
 */
public class ContextCompatHelper {
    public static Drawable getDrawable(Context context, int vectorOrImageResId, int colorFilterResId) {
        Drawable drawable = VectorOrImageResHelper.getDrawable(context, vectorOrImageResId);
        if (colorFilterResId > 0) {
            drawable.setColorFilter(ContextCompat.getColor(context, colorFilterResId), PorterDuff.Mode.SRC_ATOP);
        }
        return drawable;
    }

    public static Drawable getDrawable(Context context, int vectorOrImageResId) {
        Drawable drawable = VectorOrImageResHelper.getDrawable(context, vectorOrImageResId);
        drawable.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        return drawable;
    }
}
