package com.bsoft.mob.ienr.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.activity.user.AdviceListActivity;
import com.bsoft.mob.ienr.activity.user.RiskEvaluateActivity;
import com.bsoft.mob.ienr.activity.user.UserModelActivity;
import com.bsoft.mob.ienr.adapter.DailyWorkDetailAdapter;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.model.dailywork.DailyWork;
import com.bsoft.mob.ienr.model.kernel.SickPersonVo;
import com.bsoft.mob.ienr.util.DisplayUtil;

import java.util.ArrayList;

public class DailyWorkDetailActivity extends BaseBarcodeActivity {
    private BarcodeEntity entity;
    private ListView list;
    private ArrayList<DailyWork> worklist;
    private DailyWorkDetailAdapter adapter;
    private int location;
    private SickPersonVo sickPersonVo;

    @Override
    public void initBarBroadcast() {
        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (BarcodeActions.Bar_Get.equals(intent.getAction())) {
                    entity = (BarcodeEntity) intent
                            .getParcelableExtra("barinfo");
                }
            }
        };

    }


    /**
     * 设置长宽显示参数
     */
    private void setLayoutParams() {

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = DisplayUtil.getHeightPixels(this) * 4 / 5;
        params.width = DisplayUtil.getWidthPixels(this) * 4 / 5;
        this.getWindow().setAttributes(params);
    }

    @SuppressWarnings("unchecked")
    private void getData() {
        Intent intent = getIntent();
        worklist = (ArrayList<DailyWork>) intent.getSerializableExtra("DETAIL");
        location = intent.getIntExtra("POSITION", 0);
        sickPersonVo = new SickPersonVo();
    }

    private void initView() {

        list = (ListView) findViewById(R.id.id_lv);
       /* View headView = LayoutInflater.from(mContext).inflate(
                R.layout.item_list_daily_work_detail_header, null);*/
        TextView tv = (TextView) findViewById(R.id.work_zyhm);
        tv.setText("住院号");
        TextView id_tv_2 = (TextView) findViewById(R.id.work_brch);
        id_tv_2.setText("床号");
        TextView id_tv_3 = (TextView) findViewById(R.id.work_brxm);
        id_tv_3.setText("姓名");
//        list.addHeaderView(headView);
        adapter = new DailyWorkDetailAdapter(worklist,
                DailyWorkDetailActivity.this);
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout,list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                sickPersonVo = new SickPersonVo();
                sickPersonVo.ZYH = worklist.get(position).ZYH;
                sickPersonVo.BRCH = worklist.get(position).BRCH;
                sickPersonVo.BRXM = worklist.get(position).BRXM;
                mAppApplication.sickPersonVo = sickPersonVo;
                String[] names = getResources().getStringArray(
                        R.array.setting_user_menu_array);
                Intent intent;
                switch (location) {
                    case 0:// 医嘱计划
                        for (int i = 0; i < names.length; i++) {
                            if (names[i].equals("医嘱执行")) {
                                mAppApplication.userModelItem = i;
                                intent = new Intent(DailyWorkDetailActivity.this,
                                        UserModelActivity.class);
                                intent.putExtra("vo", mAppApplication.sickPersonVo);
                                startActivity(intent);
                                break;
                            } else {
                                if (i == names.length) {

                                    showMsgAndVoiceAndVibrator("未启用药品医嘱功能模块");
                                }
                            }
                        }

                        break;
                    case 1:// 变动医嘱
                        intent = new Intent(DailyWorkDetailActivity.this,
                                AdviceListActivity.class);
                        startActivity(intent);
                        break;
                    case 2:// 标本采集
                        for (int i = 0; i < names.length; i++) {
                            if (names[i].equals("标本采集")) {
                                mAppApplication.userModelItem = i;

                                intent = new Intent(DailyWorkDetailActivity.this,
                                        UserModelActivity.class);
                                intent.putExtra("vo", mAppApplication.sickPersonVo);
                                startActivity(intent);
                                break;
                            } else {
                                if (i == names.length) {

                                    showMsgAndVoiceAndVibrator("未启用标本采集功能模块");
                                }
                            }
                        }
                        break;
                    case 3:
                        for (int i = 0; i < names.length; i++) {
                            if (names[i].equals("风险评估")) {
                                mAppApplication.userModelItem = i;
                                String pgdh = worklist.get(position).PGDH;
                                String pglx = worklist.get(position).PGLX;
                                String pgsj = worklist.get(position).PGSJ;
                                String pgxh = worklist.get(position).PGXH;
                                String bdmc = worklist.get(position).BDMC;
                                intent = new Intent(DailyWorkDetailActivity.this,
                                        RiskEvaluateActivity.class);
                                intent.putExtra("vo", mAppApplication.sickPersonVo);

                                intent.putExtra("PGDH", pgdh);
                                intent.putExtra("PGLX", pglx);
//							intent.putExtra("TXSJ",pgsj);
                                intent.putExtra("PGXH", pgxh);
                                intent.putExtra("BDMC", bdmc);
                                intent.putExtra("ISADD", true);
                                startActivity(intent);
                                break;
                            } else {
                                if (i == names.length) {

                                    showMsgAndVoiceAndVibrator("未启用风险评估功能模块");
                                }
                            }
                        }
                        break;
                    default:
                        //点击后关闭
                        finish();
                }

            }
        });
    }

    private void initActionBar() {
        switch (location) {
            case 0:
                actionBar.setTitle("医嘱计划");
                break;
            case 1:
                actionBar.setTitle("变动医嘱");
                break;
            case 2:
                actionBar.setTitle("检验采集");
                break;
            case 3:
                actionBar.setTitle("风险提醒");
                break;
            default:
                actionBar.setTitle("医嘱计划");
        }


    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_daily_work_detail;
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected void toRefreshData() {
        getData();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setLayoutParams();
        toRefreshData();
        initView();
        initActionBar();
    }
}
