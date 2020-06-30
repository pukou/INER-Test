package com.bsoft.mob.ienr.helper;

import android.view.View;

/**
 * Created by classichu on 2018/3/20.
 */

public class ViewHelper {

    public static boolean canScrollVerticallyUp(View view) {
        return view.canScrollVertically(-1);
    }

    public static boolean canScrollVerticallyDown(View view) {
        return view.canScrollVertically(1);
    }
}
