package com.bsoft.mob.ienr.helper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatDrawableManager;


/**
 * Created by louisgeek on 2017/2/20.
 */
public class VectorOrImageResHelper {
    /**
     * static {
     AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
     }
     * @param context
     * @param imageOrVectorResId
     * @return
     */
    public static Drawable getDrawable(Context context, int imageOrVectorResId) {
        /**
         * 自动处理VectorDrawable  or  Image  否则5.0以下使用Vector会报错
         */
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context.getApplicationContext(), imageOrVectorResId);
        //####  Drawable drawable = ContextCompat.getDrawable(appContext, android.R.mipmap.sym_def_app_icon);
        return drawable;
    }
}
