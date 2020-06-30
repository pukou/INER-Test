package com.bsoft.mob.ienr.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.MainActivity;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;

/**
 * 主导航子页基类
 *
 * @author hy
 */
public abstract class LeftMenuItemFragment extends BaseBarcodeFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        actionBar.setBackAction(new Action() {
            @Override
            public void performAction(View view) {
                ((MainActivity) getActivity()).toggle();
            }
            @Override
            public String getText() {
                return "≡菜单";
            }
            @Override
            public int getDrawable() {
                return R.drawable.ic_menu_black_24dp;
            }
        });
        return view;
    }
}
