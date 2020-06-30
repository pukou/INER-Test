package com.bsoft.mob.ienr.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.BuildConfig;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.barcode.BarCodeFactory;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.fragment.base.LeftMenuItemFragment;
import com.bsoft.mob.ienr.util.FastSwitchUtils;

/**
 * 关于我们
 */
public class AboutFragment extends LeftMenuItemFragment {

    private View mainView;

    private TextView versionName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_about;
    }

    @Override
    protected void initView(View rootLayout, Bundle savedInstanceState) {
        mainView = rootLayout;
        versionName = (TextView) mainView.findViewById(R.id.tazhoukanversion);
        PackageManager pm = getActivity().getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(getActivity().getPackageName(), 0);
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            String productFlavor = BarCodeFactory.getBarCodeStr();
            String verName = pi.versionName;
            int code = pi.versionCode;

            String cont = "BUILD_TYPE:"+ BuildConfig.BUILD_TYPE+"\n"+"DEBUG:"+ BuildConfig.DEBUG+"\n"+"FLAVOR:"+ BuildConfig.FLAVOR+"\n"
            +"Aid:"+ BuildConfig.APPLICATION_ID;
            versionName.setText(productFlavor + "_" + verName + " (" + code + ")"+"\n"+cont);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        actionBar.setTitle("关于我们");

        initBroadCast();
    }

    private void initBroadCast() {

        barBroadcast = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if (BarcodeActions.Refresh.equals(action)) {
                    FastSwitchUtils.switchUser(AboutFragment.this);
                } else if (BarcodeActions.Bar_Get.equals(action)) {

                    AppApplication app = (AppApplication) getActivity()
                            .getApplication();
                    if (mAppApplication.sickPersonVo == null) {
                        return;
                    }

                    BarcodeEntity entity = (BarcodeEntity) intent
                            .getParcelableExtra("barinfo");
                    if (FastSwitchUtils.needFastSwitch(entity)) {
                        FastSwitchUtils.fastSwith(getActivity(), entity);
                    }

                }
            }
        };
    }

}
