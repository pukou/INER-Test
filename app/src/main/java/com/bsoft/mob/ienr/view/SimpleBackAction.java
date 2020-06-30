package com.bsoft.mob.ienr.view;

import android.app.Activity;
import android.view.View;

import com.bsoft.mob.ienr.R;

/**
 * Created by Classichu on 2018/1/10.
 */

public class SimpleBackAction implements BsoftActionBar.Action {
    @Override
    public int getDrawable() {
        return R.drawable.ic_arrow_back_black_24dp;
    }
    @Override
    public String getText() {
        return "< 返回";
    }
    @Override
    public void performAction(View view) {
        if (view.getContext() instanceof Activity) {
            ((Activity) view.getContext()).finish();
        }
    }
}
