package com.bsoft.mob.ienr.helper;

import android.os.Build;
import android.text.Html;

/**
 * Created by Classichu on 2018/2/27.
 */

public class HtmlCompatHelper {
    public static CharSequence fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }
}
