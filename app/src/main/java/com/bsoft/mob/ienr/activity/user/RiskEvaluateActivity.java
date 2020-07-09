/**
 * @Title: RiskEvaluateActivity.java
 * @Package com.bsoft.mob.ienr.activity.user
 * @Description: 风险评估操作页
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-12-9 上午9:57:12
 * @version V1.0
 */
package com.bsoft.mob.ienr.activity.user;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.api.NurseFormApi;
import com.bsoft.mob.ienr.api.OffLineApi;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.dynamicui.riskevaluate.RiskEvaluateViewFactory;
import com.bsoft.mob.ienr.event.RiskEvaluationEvent;
import com.bsoft.mob.ienr.fragment.user.NurseEvaluateTemplateFragment;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.evaluate.EvaluateTempDataBean;
import com.bsoft.mob.ienr.model.risk.DEPGHBean;
import com.bsoft.mob.ienr.model.risk.DERecordPostData;
import com.bsoft.mob.ienr.model.risk.FactorGoal;
import com.bsoft.mob.ienr.model.risk.QualityControl;
import com.bsoft.mob.ienr.model.risk.RiskFactor;
import com.bsoft.mob.ienr.model.risk.RiskRecord;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FormSyncUtil;
import com.bsoft.mob.ienr.util.FormSyncUtil.onCancelClickListener;
import com.bsoft.mob.ienr.util.FormSyncUtil.onConfirmClickListener;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.OffLineUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;
import com.bsoft.mob.ienr.view.floatmenu.menu.IFloatMenuItem;
import com.bsoft.mob.ienr.view.floatmenu.menu.TextFloatMenuItem;

import org.apache.commons.lang3.ArrayUtils;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @ClassName: RiskEvaluateActivity
 * @Description: 风险评估操作页
 * @date 2015-12-9 上午9:57:12
 */
public class RiskEvaluateActivity extends BaseBarcodeActivity {

    private LinearLayout form_body;
    private FrameLayout form_template_body;

    private LinearLayout root;
    private TextView tv_time;
    private ImageView iv_time;
    private TextView tv_goal;
    private TextView tv_level;
    private TextView tv_check;
    private String pgdh;
    private String pgxh = "0";
    private String pglx;
    private String bdmc;
    private boolean isAdd;
    private boolean fromOut;// 是否外部进入（目前是评估单）
    private boolean isChanged = false;// 是否发生了变动
    private RiskRecord record;

    private String Txsj;//参数：护理评估模块填写时间
    private String hszqm1;
    private List<DEPGHBean> pgdList = new ArrayList<>();

    @Override
    public void initBarBroadcast() {
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    private void getPGDListTask() {
        GetPGDListTask getPGDListTask = new GetPGDListTask();
        tasks.add(getPGDListTask);
        getPGDListTask.execute();
    }

    @Override
    public void onDateTimeSet(int year, int month, int dayOfMonth,
                              int hourOfDay, int minute, int viewId) {
        String datetime = DateTimeFactory.getInstance()
                .ymdhms2DateTime(year, month, dayOfMonth,
                        hourOfDay, minute, 0);
        //
        tv_time.setText(datetime);
    }

    @Override
    public void onConfirmSet(String action) {
        if (action.equals("DELETE")) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    DeleteTask task = new DeleteTask();
                    tasks.add(task);
                    task.execute();
                }
            }, 600);

        } else if (action.equals("CHECK")) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    CheckTask task = new CheckTask();
                    tasks.add(task);
                    task.execute();
                }
            }, 600);

        } else if (action.equals("MEASURE")) {
            if (EmptyTool.isBlank(pgxh) || pgxh.equals("0"))
                return;
            showToast("正在跳转……", Toast.LENGTH_SHORT);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    invokeMeasure();
                }
            }, 600);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (isChanged)
                setResult(Activity.RESULT_OK);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }


    private void getParamsAfter() {
        if (fromOut) {
            getOrAdd();
        } else {
            if (isAdd) {
                addTask();
            } else {
                pgxh = getIntent().getStringExtra("PGXH");
                getRecord();
            }
        }
    }

    private void getParams() {
        pgdh = getIntent().getStringExtra("PGDH");
        pglx = getIntent().getStringExtra("PGLX");
        bdmc = getIntent().getStringExtra("BDMC");
        Txsj = getIntent().getStringExtra("TXSJ");
        /*remove by louis
        if (pgdh == null)
            pgdh = "0";*/
        if (pglx == null) {
            pglx = "0";
        }
        if (bdmc == null) {
            bdmc = "风险评估单";
        }
        isAdd = getIntent().getBooleanExtra("ISADD", false);
        fromOut = getIntent().getBooleanExtra("FROMOUT", false);

        if (EmptyTool.isBlank(pgdh)) {
            //获取选择
            getPGDListTask();
        } else {
            //继续
            getParamsAfter();
        }

    }


    private void initActionBar() {
        actionBar.setTitle(EmptyTool.isBlank(bdmc) ? "风险评估" : bdmc);
        actionBar.setPatient(mAppApplication.sickPersonVo.XSCH + mAppApplication.sickPersonVo.BRXM);
        actionBar.setBackAction(new Action() {
            @Override
            public String getText() {
                return getString(R.string.menu_back);
            }

            @Override
            public void performAction(View view) {
                if (isChanged) {
                    Intent intent = new Intent();
                    intent.putExtra("PGXH", pgxh);
                    setResult(Activity.RESULT_OK, intent);
                }
                finish();
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_arrow_back_black_24dp;
            }
        });
       /* ## 2018-04-27 17:48:04
        actionBar.addAction(new Action() {
            @Override
            public String getText() {
                return "历史";
            }
            @Override
            public void performAction(View view) {
                *//*Intent intent = new Intent(RiskEvaluateActivity.this,
                        RiskMeasureListActivity.class);
                          intent.putExtra("PGDH", pgdh);
                intent.putExtra("PGXH", record.PGXH);
                startActivity(intent);
                *//*
                Intent intent = new Intent(RiskEvaluateActivity.this,
                        RiskEvaluateListActivity.class);
                startActivity(intent);
            }

            @Override
            public int getDrawable() {
                return R.drawable.menu_history_n;
            }
        });*/
        actionBar.addAction(new Action() {
            @Override
            public String getText() {
                return "保存";
            }

            @Override
            public void performAction(View view) {
                saveTask();
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }
        });
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_risk_evalute;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        getParams();
        form_body = (LinearLayout) findViewById(R.id.form_body);
        form_template_body = (FrameLayout) findViewById(R.id.form_template_body);
        root = (LinearLayout) findViewById(R.id.risk_edit_root);
        tv_time = (TextView) findViewById(R.id.id_tv_2_for_bar_image);
        iv_time = (ImageView) findViewById(R.id.id_iv_for_bar_image);
        tv_check = (TextView) findViewById(R.id.risk_check);
        tv_goal = (TextView) findViewById(R.id.risk_edit_goal);
        tv_level = (TextView) findViewById(R.id.risk_edit_level);
        //add 2017年4月28日17:02:42 tv_time.setText(DateUtil.format_yyyyMMdd_HHmm.format(mAppApplication.getServiceFixedTime()) + ":00");
        if (!EmptyTool.isBlank(Txsj)) {
            String dateTime = DateTimeHelper.dateTimeAddedMinutes(Txsj, -1);
            tv_time.setText(dateTime);
        } else {
            tv_time.setText(DateTimeHelper.getServer_yyyyMMddHHmm00());
        }
        iv_time.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String datetime = tv_time
                        .getText().toString();
                YmdHMs ymdHMs = DateTimeHelper.dateTime2YmdHMs(datetime);
                showPickerDateTimeCompat(ymdHMs, R.id.id_tv_2_for_bar_image);
            }
        });
        initActionBar();

        ArrayList<Integer> itemDrawables = new ArrayList<Integer>();

        itemDrawables.add(R.drawable.menu_delete);
        itemDrawables.add(R.drawable.menu_check);
        itemDrawables.add(R.drawable.menu_create);
        itemDrawables.add(R.drawable.menu_risk);
//  ###      itemDrawables.add(R.drawable.menu_save);

        int[][] itemStringDrawables = {
                {R.drawable.menu_delete, R.string.comm_menu_delete},
                {R.drawable.menu_check, R.string.comm_menu_check},
                {R.drawable.menu_create, R.string.comm_menu_add},
                {R.drawable.menu_risk, R.string.comm_menu_risk}/*,
                {R.drawable.menu_save,R.string.comm_menu_save}*/
        };


        if (pglx.equals("1")) {
            //压疮评估
            itemDrawables.add(R.drawable.menu_more);
            itemStringDrawables = ArrayUtils.addAll(itemStringDrawables,
                    new int[]{R.drawable.menu_more, R.string.comm_menu_jiankong}
            );
        }
        if (pglx.equals("4")) {
            //查询疼痛评估保存的记录
            itemDrawables.add(R.drawable.menu_view);
            itemStringDrawables = ArrayUtils.addAll(itemStringDrawables,
                    new int[]{R.drawable.menu_view, R.string.comm_menu_view}
            );
        }
        List<IFloatMenuItem> floatMenuItemList = new ArrayList<>();
      /*  for (int itemDrawableResid : itemDrawables) {
            FloatMenuItem floatMenuItem = new FloatMenuItem(itemDrawableResid) {
                @Override
                public void actionClick(View view, int resid) {
                    onMenuItemClick(resid);
                }
            };
            floatMenuItemList.add(floatMenuItem);
        }*/
        for (int[] itemDrawableRes : itemStringDrawables) {
            int itemDrawableResid = itemDrawableRes[0];
            int textResId = itemDrawableRes[1];
            String text = textResId > 0 ? getString(textResId) : null;
            IFloatMenuItem floatMenuItem = new TextFloatMenuItem(itemDrawableResid, text) {
                @Override
                public void actionClick(View view, int resid) {
                    onMenuItemClick(resid);
                }
            };
            floatMenuItemList.add(floatMenuItem);
        }
        updateFloatMenuItems(floatMenuItemList);
    }


    @Override
    protected List<IFloatMenuItem> configFloatMenuItems() {

        ArrayList<Integer> itemDrawables = new ArrayList<Integer>();
        itemDrawables.add(R.drawable.menu_delete);
        itemDrawables.add(R.drawable.menu_check);
        itemDrawables.add(R.drawable.menu_create);
        itemDrawables.add(R.drawable.menu_risk);
//        itemDrawables.add(R.drawable.menu_save);
        final int[][] itemStringDrawables = {
                {R.drawable.menu_delete, R.string.comm_menu_delete},
                {R.drawable.menu_check, R.string.comm_menu_check},
                {R.drawable.menu_create, R.string.comm_menu_add},
                {R.drawable.menu_risk, R.string.comm_menu_risk}/*,
                {R.drawable.menu_save, R.string.comm_menu_save}*/
        };

       /*!!!!!! if (pglx.equals("1")) {
            itemDrawables.add(R.drawable.menu_more);
        }
        if (pglx.equals("4")) {
            //查询疼痛评估保存的记录
            itemDrawables.add(R.drawable.menu_view);
        }*/

        List<IFloatMenuItem> floatMenuItemList = new ArrayList<>();
      /*  for (int itemDrawableResid : itemDrawables) {
            FloatMenuItem floatMenuItem = new FloatMenuItem(itemDrawableResid) {
                @Override
                public void actionClick(View view, int resid) {
                    onMenuItemClick(resid);
                }
            };
            floatMenuItemList.add(floatMenuItem);
        }*/
        for (int[] itemDrawableRes : itemStringDrawables) {
            int itemDrawableResid = itemDrawableRes[0];
            int textResId = itemDrawableRes[1];
            String text = textResId > 0 ? getString(textResId) : null;
            IFloatMenuItem floatMenuItem = new TextFloatMenuItem(itemDrawableResid, text) {
                @Override
                public void actionClick(View view, int resid) {
                    onMenuItemClick(resid);
                }
            };
            floatMenuItemList.add(floatMenuItem);
        }
        return floatMenuItemList;
    }

    /**
     * 响应RayMenu item点击
     */
    private void onMenuItemClick(int drawableRes) {

        if (drawableRes == R.drawable.menu_create) {
            isAdd = true;
            if (!EmptyTool.isBlank(Txsj)) {
                String dateTime = DateTimeHelper.dateTimeAddedMinutes(Txsj, -1);
                tv_time.setText(dateTime);
                showMsgAndVoice(tv_time.getText().toString());

            }
            addTask();
        }/* else if (drawableRes == R.drawable.menu_save) {
            saveTask();
        }*/ else if (drawableRes == R.drawable.menu_delete) {
            if (EmptyTool.isBlank(record.PGXH)) {
                showMsgAndVoiceAndVibrator("尚未保存，无需删除");
                return;
            }
            if (!EmptyTool.isBlank(record.HSZQM)) {
                showMsgAndVoiceAndVibrator("该表单已经审核，不能删除");
                return;
            }
            showConfirmDialog("确定要删除这张风险评估单吗？", "DELETE");
        } else if (drawableRes == R.drawable.menu_check) {
            if (EmptyTool.isBlank(record.PGXH)) {
                showMsgAndVoiceAndVibrator("请先保存");
                return;
            }
            if (!EmptyTool.isBlank(record.HSZQM)) {
                showMsgAndVoiceAndVibrator("该表单已经审核过了");
                return;
            }
            showConfirmDialog("确定要进行审核么", "CHECK");

        } else if (drawableRes == R.drawable.menu_risk) {
            invokeMeasure();
        } else if (drawableRes == R.drawable.menu_more) {
            //压疮监控
            NurseEvaluateTemplateFragment evaluateTemplateFragment = new NurseEvaluateTemplateFragment();
            Bundle bundle = new Bundle();
            bundle.putCharSequence("yslx", "91");
            evaluateTemplateFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.form_template_body, evaluateTemplateFragment).commit();
            form_body.setVisibility(View.GONE);
            form_template_body.setVisibility(View.VISIBLE);

        } else if (drawableRes == R.drawable.menu_view) {
            if (EmptyTool.isBlank(record.PGXH)) {
                showMsgAndVoiceAndVibrator("请先保存评估记录");
                return;
            }
            Intent intent = new Intent(this, RiskComprehensiveActivity.class);
            intent.putExtra("PGXH", pgxh);
            intent.putExtra("PGDH", record.PGDH);
            intent.putExtra("PGLX", record.PGLX);
            intent.putExtra("HSZQM", hszqm1);
            startActivity(intent);
        }
    }

    private void invokeMeasure() {
        if (EmptyTool.isBlank(record.PGXH)) {
            showMsgAndVoiceAndVibrator("请先保存评估记录");
            return;
        }
        Intent intent = new Intent(this, RiskMeasureActivity.class);
        intent.putExtra("PGDH", record.PGDH);
        intent.putExtra("PGLX", record.PGLX);
        intent.putExtra("PGXH", record.PGXH);
        startActivity(intent);
    }


    public void showInfo(String info) {
        showInfoDialog(info);
    }

    private void addTask() {
        AddTask task = new AddTask();
        tasks.add(task);
        task.execute();
    }

    private void getOrAdd() {
        AddOrGetTask task = new AddOrGetTask();
        tasks.add(task);
        task.execute();
    }

    @Override
    public void onListSelected(String key, String value) {
        String pgdhTemp = key;
        configPGDHAfterGetPgdh(pgdhTemp);
    }

    public void configPGDHAfterGetPgdh(String pgdhTemp) {
        pgdh = pgdhTemp;
        //
        getParamsAfter();
    }

    private void saveTask() {
        if (record.FXYZ != null && record.FXYZ.size() > 0) {
            //协和 必选标志
            boolean bxbzAllHasSelected = true;
            for (RiskFactor riskFactor : record.FXYZ) {
                if (riskFactor != null && riskFactor.YZPF != null
                        && riskFactor.YZPF.size() > 0) {
                    boolean tempHasSelected = false;
                    for (FactorGoal factorGoal : riskFactor.YZPF) {
                        if (factorGoal.SELECT) {
                            tempHasSelected = true;
                            break;
                        }
                    }
                    if ("1".equals(riskFactor.BXBZ) && !tempHasSelected) {
                        bxbzAllHasSelected = false;
                        break;
                    }
                }
            }
            if (!bxbzAllHasSelected) {
                showMsgAndVoiceAndVibrator("请选择 * 标识的必填风险因子");
                return;
            }
            //
            boolean hasSelected = false;
            for (RiskFactor riskFactor :
                    record.FXYZ) {
                if (riskFactor != null && riskFactor.YZPF != null && riskFactor.YZPF.size() > 0) {
                    for (FactorGoal factorGoal :
                            riskFactor.YZPF) {
                        if (factorGoal.SELECT) {
                            hasSelected = true;
                            break;
                        }
                    }
                }
            }
            if (!hasSelected) {
                showMsgAndVoiceAndVibrator("请选择相应风险因子的评分");
                return;
            }
        }
        if (!EmptyTool.isBlank(record.HSZQM)) {
            showMsgAndVoiceAndVibrator("护士长已经签名，不能修改");
            return;
        }
        // 离线保存
        if (!OffLineUtil.WifiConnected(this)) {
            String data = "";

            DERecordPostData deRecordPostData = new DERecordPostData();
            record.PGSJ = tv_time.getText().toString();
            record.PGGH = mAppApplication.user.YHID;
            record.PGZF = tv_goal.getText().toString();
            record.ZKMS = tv_level.getText().toString();

            record.BRCH = mAppApplication.sickPersonVo.XSCH;
            record.BRXM = mAppApplication.sickPersonVo.BRXM;
            record.BDMC = "评估";//todo
            try {
                RiskRecord riskRecord = new RiskRecord();
                riskRecord.PGSJ = record.PGSJ;
                riskRecord.PGGH = record.PGGH;
                riskRecord.PGZF = record.PGZF;
                riskRecord.BRCH = record.BRCH;
                riskRecord.BRXM = record.BRXM;
                riskRecord.BDMC = record.BDMC;
                riskRecord.ZKMS = record.ZKMS;
                deRecordPostData.BQID = mAppApplication.getAreaId();
                deRecordPostData.JGID = mAppApplication.jgId;
                deRecordPostData.ZYH = mAppApplication.sickPersonVo.ZYH;
                deRecordPostData.DERecord = record;

                data = JsonUtil.toJson(deRecordPostData);
//                data = URLEncoder.encode(data, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            String url = OffLineApi.getInstance(this).url;
            String uri = url + "dangerevaluate/post/saveDE";
            if (OffLineUtil.offLineSave(this, uri, 2, data,
                    mAppApplication.sickPersonVo.BRXM, bdmc, mAppApplication.user.YHXM))
                showInfo("当前网络未连接，已为您保存在本地。网络连接好后，请到【离线保存】菜单中提交。");
            return;
        }
        SaveTask task = new SaveTask();
        tasks.add(task);
        task.execute();
    }

    private void getRecord() {
        GetRecordTask task = new GetRecordTask();
        tasks.add(task);
        task.execute();
    }

    /**
     * @param @return
     * @return int
     * @throws
     * @Title: getGoal
     * @Description: 获取评估界面的总分
     */
    public int getGoal() {
        String goal = tv_goal.getText().toString();
        if (EmptyTool.isBlank(goal))
            return 0;
        if (TextUtils.isDigitsOnly(goal))
            return Integer.parseInt(goal);
        return 0;
    }

    /**
     * @param @param goal
     * @param @param level
     * @return void
     * @throws
     * @Title: setGoalAndLevel
     * @Description: 设置评估界面的分值和等级
     */
    public void setGoalAndLevel(String goal) {
        tv_goal.setText(goal);
        tv_level.setText(getLevel(Integer.parseInt(goal)));
    }

    private String getLevel(int goal) {
        for (QualityControl qc : record.ZKGZ) {
            if (qc.FZSX >= goal && qc.FZXX <= goal)
                return qc.ZKMS;
        }
        return "";
    }

    private void clearView() {
        root.removeAllViews();
        tv_goal.setText("0");
        tv_level.setText("");
        if (isAdd) {
            tv_check.setText("待保存");
            tv_check.setTextColor(ContextCompat.getColor(mContext, R.color.red));
        }

    }

    private void handleResult(Response<RiskRecord> result) {
        record = result.Data;
        root.removeAllViews();
        pgxh = record.PGXH;
        record.BDMC = EmptyTool.isBlank(bdmc) ? "风险评估" : bdmc;
        record.BRCH = mAppApplication.sickPersonVo.XSCH;
        record.BRXM = mAppApplication.sickPersonVo.BRXM;
        new RiskEvaluateViewFactory(RiskEvaluateActivity.this, root, record.ZKGZ, record.FXYZ)
                .build();
//        tv_time.setText(DateUtil.get8To7Sstr(EmptyTool.isBlank(record.PGSJ) ? DateUtil.format_yyyyMMdd_HHmm
//                .format(mAppApplication.getServiceFixedTime()) : record.PGSJ)
//                + ":00");
        if (!EmptyTool.isBlank(record.PGXH)) {
            //change by louis 2017-5-19 14:54:53
            // 如果有评估时间  就是算修改  然后设置PGSJ
            tv_time.setText(EmptyTool.isBlank(record.PGSJ) ? DateTimeHelper.getServer_yyyyMMddHHmm00() : record.PGSJ);
        }
        setGoalAndLevel(EmptyTool.isBlank(record.PGZF) ? "0" : record.PGZF);
        if (EmptyTool.isBlank(record.HSZQM)) {
            tv_check.setText("待审核");
            tv_check.setTextColor(ContextCompat.getColor(mContext, R.color.blue));
        } else {
            tv_check.setText("已审核");
            tv_check.setBackgroundColor(getResources().getColor(R.color.green));
            hszqm1 = record.HSZQM;
        }
    }

    private void showDialog() {
        View txt = ViewBuildHelper.buildDialogTitleTextView(mContext, "请输入");
        new AlertDialog.Builder(this)
                //.setTitle("请输入")
                .setCustomTitle(txt)
                .setMessage("是否填写疼痛综合评估措施单？")

                .setPositiveButton(getString(R.string.project_operate_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (EmptyTool.isBlank(pgxh) || pgxh.equals("0"))
                            return;
                        showToast("正在跳转……", Toast.LENGTH_SHORT);
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                goActivity();
                            }
                        }, 1000);
                    }
                })
                .setNegativeButton(getString(R.string.project_operate_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void goActivity() {

        Intent intent = new Intent(this, RiskComprehensiveActivity.class);
        intent.putExtra("PGXH", pgxh);
        intent.putExtra("PGDH", record.PGDH);
        intent.putExtra("PGLX", record.PGLX);
        intent.putExtra("HSZQM", record.HSZQM);
        startActivity(intent);
    }

    private void alertMeasure() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isDigitsOnly(record.PGZF))
                    for (QualityControl qc : record.ZKGZ) {
                        int zf = Integer.parseInt(record.PGZF);
                        if (zf >= qc.FZXX && zf <= qc.FZSX)
                            if (qc.CSBZ.equals("1")) {
                                showConfirmDialog(qc.ZKMS + "！是否现在请填写措施单？",
                                        "MEASURE");
                                break;
                            }
                    }
            }
        }, 600);
    }

    /**
     * @author 吕自聪 lvzc@bsoft.com.cn
     * @ClassName: AddTask
     * @Description: 添加一张风险评估单
     * @date 2015-12-9 下午1:40:15
     */
    class AddTask extends AsyncTask<Void, Void, Response<RiskRecord>> {

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<RiskRecord> doInBackground(Void... params) {
            return NurseFormApi.getInstance(RiskEvaluateActivity.this)
                    .togetNewRisk(pgdh, pglx, mAppApplication.jgId);
        }

        @Override
        protected void onPostExecute(Response<RiskRecord> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(RiskEvaluateActivity.this, application,
                            new AgainLoginUtil.LoginSucessListener() {
                                @Override
                                public void LoginSucessEvent() {
                                    addTask();
                                }
                            }).showLoginDialog();

                } else if (result.ReType == 0) {
                    clearView();
                    record = result.Data;
                    new RiskEvaluateViewFactory(RiskEvaluateActivity.this, root, record.ZKGZ,
                            record.FXYZ).build();

                } else {
                    showMsgAndVoiceAndVibrator("请求错误");
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    class GetPGDListTask extends AsyncTask<Void, Void, Response<List<DEPGHBean>>> {

        @Override
        protected void onPreExecute() {
            //showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<DEPGHBean>> doInBackground(Void... params) {
            return NurseFormApi.getInstance(RiskEvaluateActivity.this)
                    .getPGHList(pglx, mAppApplication.jgId);
        }

        @Override
        protected void onPostExecute(Response<List<DEPGHBean>> result) {
            // hideSwipeRefreshLayout();
            tasks.remove(this);

            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(RiskEvaluateActivity.this, application,
                            new AgainLoginUtil.LoginSucessListener() {
                                @Override
                                public void LoginSucessEvent() {
                                    addTask();
                                }
                            }).showLoginDialog();

                } else if (result.ReType == 0) {

                    pgdList = result.Data;
                    Map<String, String> PGDHMap = new HashMap<>();
                    for (DEPGHBean depghBean :
                            pgdList) {
                        PGDHMap.put(depghBean.PGDH, depghBean.PGDMC);
                    }
                    if (PGDHMap != null && PGDHMap.size() > 0) {
                        if (PGDHMap.size() > 1) {
                            showListDialog("选择", 1, PGDHMap);
                        } else {
                            //唯一1条
                            String pgdhTemp = "";
                            for (String KEY : PGDHMap.keySet()) {
                                //String value = PGDHMap.get(KEY);
                                pgdhTemp = KEY;
                            }
                            //得到后
                            configPGDHAfterGetPgdh(pgdhTemp);
                        }
                    } else {
                        showMsgAndVoiceAndVibrator("无数据");
                    }
                } else {
                    showMsgAndVoiceAndVibrator("请求错误");
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    /**
     * @author 吕自聪 lvzc@bsoft.com.cn
     * @ClassName: SaveTask
     * @Description: 保存风险评估
     * @date 2015-12-10 下午2:45:22
     */
    class SaveTask extends AsyncTask<Void, Void, Response<RiskRecord>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(R.string.saveing);
        }

        @Override
        protected Response<RiskRecord> doInBackground(Void... params) {
            String data = "";
            DERecordPostData deRecordPostData = new DERecordPostData();

            record.PGSJ = tv_time.getText().toString();
            record.PGGH = mAppApplication.user.YHID;
            record.PGZF = tv_goal.getText().toString();
            record.ZKMS = tv_level.getText().toString();

            record.BRCH = mAppApplication.sickPersonVo.XSCH;
            record.BRXM = mAppApplication.sickPersonVo.BRXM;
            record.BDMC = "评估";//todo
            try {
                RiskRecord riskRecord = new RiskRecord();
                riskRecord.PGSJ = record.PGSJ;
                riskRecord.PGGH = record.PGGH;
                riskRecord.PGZF = record.PGZF;
                riskRecord.BRCH = record.BRCH;
                riskRecord.BRXM = record.BRXM;
                riskRecord.BDMC = record.BDMC;
                riskRecord.ZKMS = record.ZKMS;
                deRecordPostData.BQID = mAppApplication.getAreaId();
                deRecordPostData.JGID = mAppApplication.jgId;
                deRecordPostData.ZYH = mAppApplication.sickPersonVo.ZYH;
                deRecordPostData.DERecord = record;

                data = JsonUtil.toJson(deRecordPostData);
//                data = URLEncoder.encode(data, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return NurseFormApi.getInstance(RiskEvaluateActivity.this)
                    .tosaveRisk(data);
        }

        @Override
        protected void onPostExecute(Response<RiskRecord> result) {
            hideLoadingDialog();
            tasks.remove(this);

            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(RiskEvaluateActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            saveTask();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {

                    //add 2017年4月25日17:20:20
                    if (mAppApplication.mEvaluateTempDataBean == null) {
                        mAppApplication.mEvaluateTempDataBean = new HashMap<>();
                    }
                    if (result.Data != null) {
                        EvaluateTempDataBean evaluateTempDataBean = new EvaluateTempDataBean();
                        evaluateTempDataBean.PGZF = result.Data.PGZF;
                        evaluateTempDataBean.PGXH = result.Data.PGXH;
                        mAppApplication.mEvaluateTempDataBean.put(result.Data.PGLX, evaluateTempDataBean);
                        pgxh = result.Data.PGXH;
                    }

                    isChanged = true;
                    showMsgAndVoice(R.string.project_save_success);
                    //
                    EventBus.getDefault().post(new RiskEvaluationEvent());
                    handleResult(result);
                    if (record.IsSync) {
                        FormSyncUtil syncUtil = new FormSyncUtil();
                        syncUtil.setOnDialogClickListener(
                                new onCancelClickListener() {

                                    @Override
                                    public void onCancel() {

                                        if (pglx.equals("4")) {
                                            showDialog();
                                        } else {
                                            alertMeasure();
                                        }
                                    }
                                }, new onConfirmClickListener() {

                                    @Override
                                    public void onConfirm() {
                                        if (pglx.equals("4")) {
                                            showDialog();
                                        } else {
                                            alertMeasure();
                                        }
                                    }
                                });
                        syncUtil.InvokeSync(RiskEvaluateActivity.this,
                                record.SyncData, mAppApplication.jgId, tasks);

                    } else {
                        if (pglx.equals("4")) {
                            showDialog();
                        } else {
                            alertMeasure();
                        }
                    }

                } else {
                    showMsgAndVoiceAndVibrator(R.string.project_save_failed);
                    /*MediaUtil.getInstance(RiskEvaluateActivity.this).playSound(
                            R.raw.wrong, RiskEvaluateActivity.this);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    /**
     * @author 吕自聪 lvzc@bsoft.com.cn
     * @ClassName: GetRecordTask
     * @Description: 获取评估记录
     * @date 2015-12-11 上午11:25:11
     */
    class GetRecordTask extends AsyncTask<Void, Void, Response<RiskRecord>> {
        /*
         * (非 Javadoc) <p>Title: onPreExecute</p> <p>Description: </p>
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        /*
         * (非 Javadoc) <p>Title: doInBackground</p> <p>Description: </p>
         *
         * @param params
         *
         * @return
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Response<RiskRecord> doInBackground(Void... params) {
            return NurseFormApi.getInstance(RiskEvaluateActivity.this)
                    .togetRiskRecord(pgxh, mAppApplication.jgId);
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description: </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Response<RiskRecord> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(RiskEvaluateActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            getRecord();
                        }

                    }).showLoginDialog();
                } else if (result.ReType == 0) {
                    handleResult(result);
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    /**
     * @author 吕自聪 lvzc@bsoft.com.cn
     * @ClassName: DeleteTask
     * @Description: 删除评估记录
     * @date 2015-12-11 上午11:25:32
     */
    class DeleteTask extends AsyncTask<Void, Void, Response<String>> {

        /*
         * (非 Javadoc) <p>Title: doInBackground</p> <p>Description: </p>
         *
         * @param params
         *
         * @return
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Response<String> doInBackground(Void... params) {
            return NurseFormApi.getInstance(RiskEvaluateActivity.this)
                    .todeleteRisk(pgxh, mAppApplication.jgId);
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description: </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Response<String> result) {
            tasks.remove(this);

            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(RiskEvaluateActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            DeleteTask task = new DeleteTask();
                            tasks.add(task);
                            task.execute();
                        }
                    }).showLoginDialog();
                } else if (result.ReType == 0) {
                    isChanged = true;
                    showMsgAndVoice(result.Data);
                    /*setResult(Activity.RESULT_OK);
                    finish();*/
                    //
                    EventBus.getDefault().post(new RiskEvaluationEvent());
                } else {
                    showMsgAndVoice(result.Data);
                    /*MediaUtil.getInstance(RiskEvaluateActivity.this).playSound(
                            R.raw.wrong, RiskEvaluateActivity.this);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    class CheckTask extends AsyncTask<Void, Void, Response<RiskRecord>> {


        /*
         * (非 Javadoc) <p>Title: onPreExecute</p> <p>Description: </p>
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.saveing);
        }

        /*
         * (非 Javadoc) <p>Title: doInBackground</p> <p>Description: </p>
         *
         * @param params
         *
         * @return
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Response<RiskRecord> doInBackground(Void... params) {
            return NurseFormApi.getInstance(RiskEvaluateActivity.this)
                    .tocheckRisk(pgxh, mAppApplication.user.YHID, mAppApplication.jgId);
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description: </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Response<RiskRecord> result) {
            tasks.remove(this);
            hideSwipeRefreshLayout();

            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(RiskEvaluateActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            CheckTask task = new CheckTask();
                            tasks.add(task);
                            task.execute();
                        }
                    }).showLoginDialog();
                } else if (result.ReType == 0) {
                    showMsgAndVoice("审阅成功");
                    handleResult(result);
                } else {
                    showMsgAndVoiceAndVibrator("审阅失败");
                    /*MediaUtil.getInstance(RiskEvaluateActivity.this).playSound(
                            R.raw.wrong, RiskEvaluateActivity.this);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    /**
     * @author 吕自聪 lvzc@bsoft.com.cn
     * @ClassName: AddOrGetTask
     * @Description: 获取第一条风险记录，不存在则添加一条
     * @date 2015-12-29 上午10:29:22
     */
    class AddOrGetTask extends AsyncTask<Void, Void, Response<RiskRecord>> {

        /*
         * (非 Javadoc) <p>Title: onPreExecute</p> <p>Description: </p>
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        /*
         * (非 Javadoc) <p>Title: doInBackground</p> <p>Description: </p>
         *
         * @param params
         *
         * @return
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Response<RiskRecord> doInBackground(Void... params) {
            return NurseFormApi.getInstance(RiskEvaluateActivity.this)
                    .togetOrAddRisk(mAppApplication.sickPersonVo.ZYH, pgdh, pglx, mAppApplication.jgId, application.mHQFSTemp);
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description: </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Response<RiskRecord> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(RiskEvaluateActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            getOrAdd();
                        }

                    }).showLoginDialog();
                } else if (result.ReType == 0) {
                    clearView();
                    record = result.Data;
                    //add 2017年4月28日10:20:02
             /*       if (EmptyTool.isBlank(record.PGDH)) {
                        //add
                        GetPGDListTask getPGDListTask = new GetPGDListTask();
                        tasks.add(getPGDListTask);
                        getPGDListTask.execute();
                    } else {*/
                    //  pgdh = record.PGDH;
                    // record.PGSJ=Txsj;
                    //
                    handleResult(result);
                       /*remove 2017年4月28日16:00:59
                        new LifeSymptomViewFactory(RiskEvaluateActivity.this,root,record.ZKGZ
                                ,record.FXYZ).build();*/
                    //}

                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

}
