package com.bsoft.mob.ienr.helper;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;


public class SpannableStringBuilderHelper {
    public static CharSequence getTextColoredCharSequence(String text, String key, int color) {
        if (text == null) {
            return null;
        }
        if (!text.contains(key)) {
            return null;
        }
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        int indexStart = -1;
        do {
            indexStart = text.indexOf(key, indexStart + 1);
            int indexEnd = indexStart + key.length();
            if (indexStart >= 0) {
                style.setSpan(new ForegroundColorSpan(color), indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
        } while (indexStart >= 0);
        return style;
    }

    public static CharSequence getBackgroundColoredCharSequence(String text, String key, int color) {
        if (text == null) {
            return null;
        }
        if (!text.contains(key)) {
            return null;
        }
        SpannableStringBuilder style = new SpannableStringBuilder(text);
        int indexStart = -1;
        do {
            indexStart = text.indexOf(key, indexStart + 1);
            int indexEnd = indexStart + key.length();
            if (indexStart >= 0) {
                style.setSpan(new ForegroundColorSpan(color), indexStart, indexEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
        } while (indexStart >= 0);
        return style;
    }
}
