package com.bsoft.mob.ienr.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.UserModelActivity;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;

/**
 * 用户相关页面基类，主要响应actionbar 右边按钮点击事件、显示dialog fragment
 *
 * @author hy
 */
public abstract class BaseUserFragment extends BaseBarcodeFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (getActivity() instanceof UserModelActivity) {
            actionBar.addAction(new Action() {

                @Override
                public void performAction(View view) {
                    ((UserModelActivity) getActivity()).toggle();
                }
                @Override
                public String getText() {
                    return "菜单┇";
                }
                @Override
                public int getDrawable() {
                    return R.drawable.ic_more_vert_black_24dp;
                }
            });
        }
        return view;
    }
}
