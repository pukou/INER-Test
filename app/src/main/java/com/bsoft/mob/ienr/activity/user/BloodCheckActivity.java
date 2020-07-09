package com.bsoft.mob.ienr.activity.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.model.blood.BloodTransfusionInfo;
import com.bsoft.mob.ienr.util.DisplayUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;

import java.io.Serializable;

public class BloodCheckActivity extends BaseBarcodeActivity {

    private BloodTransfusionInfo entity;


    private void setView() {

        final TextView BRXM = (TextView) findViewById(R.id.BRXM);
        final TextView ZYHM = (TextView) findViewById(R.id.ZYHM);
        final TextView XDH = (TextView) findViewById(R.id.XDH);
        final TextView XDZL = (TextView) findViewById(R.id.XDZL);
        final TextView XJL = (TextView) findViewById(R.id.XJL);
        final TextView XX = (TextView) findViewById(R.id.XX);

        BRXM.setText("病人姓名：" + entity.BRXM);
        ZYHM.setText("住院号码：" + entity.ZYHM);
        XDH.setText("血袋号：" + entity.XDH);
        XDZL.setText("血液种类：" + entity.MC);
        XJL.setText("血剂量：" + entity.XL + entity.BAOZHUANG);
        XX.setText("血型：" + entity.ABO);

        final CheckBox BRXM_cb = (CheckBox) findViewById(R.id.BRXM_cb);
        final CheckBox ZYHM_cb = (CheckBox) findViewById(R.id.ZYHM_cb);
        final CheckBox XDH_cb = (CheckBox) findViewById(R.id.XDH_cb);
        final CheckBox XDZL_cb = (CheckBox) findViewById(R.id.XDZL_cb);
        final CheckBox XJL_cb = (CheckBox) findViewById(R.id.XJL_cb);
        final CheckBox XX_cb = (CheckBox) findViewById(R.id.XX_cb);
        final CheckBox ALL_cb = (CheckBox) findViewById(R.id.ALL_cb);

        ALL_cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                BRXM_cb.setChecked(isChecked);
                ZYHM_cb.setChecked(isChecked);
                XDH_cb.setChecked(isChecked);
                XDZL_cb.setChecked(isChecked);
                XJL_cb.setChecked(isChecked);
                XX_cb.setChecked(isChecked);
            }
        });


        initActionBar();
    }

    private void initExtra(Intent intent) {

        Serializable obj = getIntent().getSerializableExtra("entity");
        if (obj == null) {
            finish();
        }

        entity = (BloodTransfusionInfo) obj;
    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        initExtra(intent);
    }

    private void initActionBar() {

        actionBar.setTitle("输血核对");
        actionBar.setPatient(mAppApplication.sickPersonVo.XSCH + mAppApplication.sickPersonVo.BRXM);


        actionBar.addAction(new Action() {
            @Override
            public String getText() {
                return "保存";
            }
            @Override
            public void performAction(View view) {

                setResult(RESULT_OK);
                finish();
            }

            @Override
            public int getDrawable() {

                return R.drawable.ic_done_black_24dp;

            }
        });
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_blood_check;
    }
    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }
    @Override
    protected void initView(Bundle savedInstanceState) {

        initExtra(getIntent());

        setLayoutParams();
        setView();
    }

    /**
     * 设置长宽显示参数
     */
    private void setLayoutParams() {

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = LayoutParams.WRAP_CONTENT;
        params.width = DisplayUtil.getWidthPixels(this) - 20;
        this.getWindow().setAttributes(params);
    }

    @Override
    public void initBarBroadcast() {

        barBroadcast = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if (BarcodeActions.Bar_Get.equals(action)) {

                    BarcodeEntity bentity = (BarcodeEntity) intent
                            .getParcelableExtra("barinfo");
                    if (bentity.FLBS == 6) {
                        if (entity == null) {
                            return;
                        }

                        boolean xdhRight = false;
                        if (!EmptyTool.isBlank(entity.XDH)
                                && entity.XDH.equals(bentity.source)) {
                            final CheckBox XDH_cb = (CheckBox) findViewById(R.id.XDH_cb);
                            XDH_cb.setChecked(true);
                            xdhRight = true;

                        }

                        boolean xylxRight = false;
                        if (!EmptyTool.isBlank(entity.XYLX)
                                && entity.XYLX.equals(bentity.source)) {
                            final CheckBox XDZL_cb = (CheckBox) findViewById(R.id.XDZL_cb);
                            XDZL_cb.setChecked(true);
                            xylxRight = true;

                        }
                        if (!xylxRight && !xdhRight) {
                            showMsgAndVoiceAndVibrator("核对失败：条码不匹配");
                        }
                    } else if (FastSwitchUtils.needFastSwitch(bentity)) {
                        Intent result = new Intent(context,
                                UserModelActivity.class);
                        result.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        result.putExtra("barinfo", entity);
                        startActivity(result);
                    }

                } else if (BarcodeActions.Refresh.equals(intent.getAction())) {
                    Intent result = new Intent(context, UserModelActivity.class);
                    result.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    result.putExtra("refresh", true);
                    startActivity(result);
                }
            }

        };
    }


}
