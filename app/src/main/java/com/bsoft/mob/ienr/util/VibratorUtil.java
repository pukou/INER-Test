package com.bsoft.mob.ienr.util;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import static android.content.Context.VIBRATOR_SERVICE;

/**
 * @author Xinggj E-mail:sixtynine@live.cn
 * @version Time：2014-2-24 上午11:47:46
 * @类说明 振动提示类
 */

public class VibratorUtil {

    public static long VIBRATOR_LONG = 500;
    public static long VIBRATOR_SHORT = 100;

    public static void vibrator(Context context, long milliseconds) {
        if (context == null) {
            return;
        }
        ((Vibrator) context.getSystemService(VIBRATOR_SERVICE))
                .vibrate(milliseconds);
    }


    private static void vibratorCompat(Context contex,long milliseconds) {
        if (Build.VERSION.SDK_INT >= 26) {
            ((Vibrator) contex.getSystemService(VIBRATOR_SERVICE)).vibrate(VibrationEffect.createOneShot(milliseconds,
                    VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            ((Vibrator) contex.getSystemService(VIBRATOR_SERVICE)).vibrate(milliseconds);
        }
    }
    public static void vibrator(Context context, boolean vib) {
        if (context == null) {
            return;
        }
        if (vib) {
            vibratorCompat(context,VIBRATOR_SHORT);
        }
    }


}
