package com.bsoft.mob.ienr.activity.user;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.api.NurseFormApi;
import com.bsoft.mob.ienr.dynamicui.riskevaluate.PlanViewUtils;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.risk.PEOption;
import com.bsoft.mob.ienr.model.risk.PERecordPostData;
import com.bsoft.mob.ienr.model.risk.PainEvaluate;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.DisplayUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 疼痛综合评估
 *
 * @author Ding
 * @date 2016-12-14
 */
public class RiskComprehensiveActivity extends BaseBarcodeActivity {


    private LinearLayout root;
    private String pgxh;
    private String pgdh;
    private String pglx;
    //    private RiskRecord record;
    private List<PEOption> mPEOption;
    private List<PainEvaluate> painEvaluates;
    private String hszqz;


    @Override
    public void initBarBroadcast() {

    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    /**
     * 设置长宽显示参数
     */
    private void setLayoutParams() {

        WindowManager.LayoutParams params = getWindow().getAttributes();
//        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        params.width = DisplayUtil.getWidthPixels(this) - 20;
        this.getWindow().setAttributes(params);
    }

    private void initView() {
        initActionBar();
        root = (LinearLayout) findViewById(R.id.risk_comprehensive_root);

    }

    private void initActionBar() {
        actionBar.setTitle("综合评估");
        actionBar.setPatient(application.sickPersonVo.XSCH + application.sickPersonVo.BRXM);


        actionBar.addAction(new BsoftActionBar.Action() {
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
        return R.layout.activity_risk_comprehensive;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {


        Intent intent = getIntent();
        pgxh = intent.getStringExtra("PGXH");
        pgdh = intent.getStringExtra("PGDH");
        pglx = intent.getStringExtra("PGLX");
        hszqz = intent.getStringExtra("HSZQM");
        setLayoutParams();
        initView();
        toRefreshData();
    }

    @Override
    protected void toRefreshData() {
        AddTask task = new AddTask();
        tasks.add(task);
        task.execute();
    }

    private void saveTask() {
        if (!EmptyTool.isBlank(hszqz)) {
            showMsgAndVoiceAndVibrator("护士长已经签名，不能修改");
            return;
        }

        SaveTask task = new SaveTask();
        tasks.add(task);
        task.execute();
    }

    private void alertMeasure() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                showConfirmDialog( "是否现在请填写措施单？",
//                                        "MEASURE");
                showDialog();
            }
        }, 1000);
    }

    private void showDialog() {
        new AlertDialog.Builder(this)
                //.setTitle("请输入")
                .setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext, "请输入"))
                .setMessage("是否现在请填写措施单？")

                .setPositiveButton(getString(R.string.project_operate_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (EmptyTool.isBlank(pgxh) || pgxh.equals("0"))
                            return;
                        showToast("正在跳转……", Toast.LENGTH_LONG);
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                invokeMeasure();
                            }
                        }, 1000);
                    }
                })
                .setNegativeButton(getString(R.string.project_operate_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .show();
    }

    @Override
    public void onConfirmSet(String action) {
        if (action.equals("MEASURE")) {
            if (EmptyTool.isBlank(pgxh) || pgxh.equals("0"))
                return;
            showToast("正在跳转……", Toast.LENGTH_LONG);
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    invokeMeasure();
                }
            }, 1000);
        }

    }

    private void invokeMeasure() {
        if (EmptyTool.isBlank(pgxh)) {
            showMsgAndVoiceAndVibrator("请先保存评估记录");
            return;
        }
        Intent intent = new Intent(this, RiskMeasureActivity.class);
        intent.putExtra("PGDH", pgdh);
        intent.putExtra("PGLX", pglx);
        intent.putExtra("PGXH", pgxh);
        startActivity(intent);
        RiskComprehensiveActivity.this.finish();
    }

    /**
     * 添加一张疼痛综合评估
     */
    class AddTask extends AsyncTask<Void, Void, Response<List<PainEvaluate>>> {

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<PainEvaluate>> doInBackground(Void... params) {
            return NurseFormApi.getInstance(RiskComprehensiveActivity.this)
                    .getRiskPain(application.jgId, pgxh);
        }

        @Override
        protected void onPostExecute(Response<List<PainEvaluate>> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(RiskComprehensiveActivity.this, application,
                            new AgainLoginUtil.LoginSucessListener() {
                                @Override
                                public void LoginSucessEvent() {
                                    toRefreshData();
                                }
                            }).showLoginDialog();
                } else if (result.ReType == 0) {
                    painEvaluates = result.Data;

                    new PlanViewUtils(RiskComprehensiveActivity.this, root, painEvaluates).build();

                } else {
                    showMsgAndVoiceAndVibrator("请求错误");
                   /* MediaUtil.getInstance(RiskComprehensiveActivity.this).playSound(
                            R.raw.wrong, RiskComprehensiveActivity.this);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    class SaveTask extends AsyncTask<Void, Void, Response<List<PainEvaluate>>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(R.string.saveing);
        }

        @Override
        protected Response<List<PainEvaluate>> doInBackground(Void... params) {
            String data = "";
            PERecordPostData peRecord = new PERecordPostData();
            List<PEOption> peOptions = new ArrayList<>();

            try {
                for (int i = 0; i < painEvaluates.size(); i++) {
                    List<PEOption> pgxx = painEvaluates.get(i).PGXX;
                    peOptions.addAll(pgxx);
                }
                peRecord.RECORDS = peOptions;
                peRecord.JGID = application.jgId;
                peRecord.PGXH = pgxh;
                peRecord.ZYH = application.sickPersonVo.ZYH;

                data = JsonUtil.toJson(peRecord);

            } catch (IOException e) {
                e.printStackTrace();
            }
            return NurseFormApi.getInstance(RiskComprehensiveActivity.this)
                    .saveRiskPain(data);
        }

        @Override
        protected void onPostExecute(Response<List<PainEvaluate>> result) {
            hideLoadingDialog();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(RiskComprehensiveActivity.this, application, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            saveTask();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    showMsgAndVoice(R.string.project_save_success);
                    alertMeasure();

                } else {
                    showMsgAndVoiceAndVibrator(R.string.project_save_failed);
                /*    MediaUtil.getInstance(RiskComprehensiveActivity.this).playSound(
                            R.raw.wrong, RiskComprehensiveActivity.this);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }


}