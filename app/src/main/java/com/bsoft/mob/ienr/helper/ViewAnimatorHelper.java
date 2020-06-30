package com.bsoft.mob.ienr.helper;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.view.View;

public class ViewAnimatorHelper {

    public static void hideView(View view) {
        ViewCompat.animate(view).cancel();
        ViewCompat.animate(view)
                .alpha(0f)
                .setDuration(200L)
                .setInterpolator(new FastOutLinearInInterpolator())
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(View view) {
                        view.setVisibility(View.GONE);
                    }
                }).start();

    }

    public static void hideView_INVISIBLE(View view) {
        ViewCompat.animate(view).cancel();
        ViewCompat.animate(view)
                .alpha(0f)
                .setDuration(200L)
                .setInterpolator(new FastOutLinearInInterpolator())
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(View view) {
                        view.setVisibility(View.INVISIBLE);
                    }
                }).start();

    }

    public static void showView(View view) {
        ViewCompat.animate(view).cancel();
        ViewCompat.animate(view)
                .alpha(1f)
                .setDuration(200L)
                .setInterpolator(new LinearOutSlowInInterpolator())
                .setListener(new ViewPropertyAnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(View view) {
                        view.setVisibility(View.VISIBLE);
                    }
                }).start();

    }
}
