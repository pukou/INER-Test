package com.bsoft.mob.ienr.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.api.AdviceCheckApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.fragment.AdviceCheckFragment;
import com.bsoft.mob.ienr.helper.BarCodeHelper;
import com.bsoft.mob.ienr.helper.TestDataHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.advicecheck.AdviceForm;
import com.bsoft.mob.ienr.model.advicecheck.AdviceFormDetail;
import com.bsoft.mob.ienr.model.advicecheck.CheckDetail;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.DisplayUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdviceCheckDetailActivity extends BaseBarcodeActivity {


    private String check_mode = null;
    private String isDispend = "0";
    private AdviceForm form = null;
    private TextView tv_brxm, tv_brch, tv_brxb, tv_brnl, tv_ypyf, tv_sypc;
    private TextView tv_jhrq, tv_byhd, tv_jyhd;
    private ListView list;
    private boolean isScan = false;
    private BarcodeEntity entity;

    @Override
    public void initBarBroadcast() {
        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (BarcodeActions.Bar_Get.equals(intent.getAction())) {
                    entity = (BarcodeEntity) intent
                            .getParcelableExtra("barinfo");
                    if (entity.TMFL == 2) {
                        if (entity.FLBS == 4 || entity.FLBS == 5) {
                            isScan = true;
                            CheckTask task = new CheckTask();
                            tasks.add(task);
                            task.execute();
                        } else {

                            showMsgAndVoiceAndVibrator("条码不正确");
                         /*   MediaUtil.getInstance(
                                    AdviceCheckDetailActivity.this)
                                    .playSound(R.raw.wrong,
                                            AdviceCheckDetailActivity.this);*/
                        }
                    }
                }
            }
        };
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_advice_check_detail;
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        Intent intent = getIntent();



        check_mode = intent.getStringExtra("MODE");
        form = (AdviceForm) intent.getSerializableExtra("FORM");
        isDispend = intent.getStringExtra("isDispend");
        setLayoutParams();

        initView();

        initActionBar();

        toRefreshData();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initActionBar() {
        //if ("2".equals(check_mode)) {
        //change  2019-1-30 和下方ui对上
        if ("1".equals(check_mode)) {
            actionBar.setTitle("摆药核对");
        } else {
            actionBar.setTitle("加药核对");
        }
        actionBar.addAction(new Action() {

            @Override
            public void performAction(View view) {
                CheckTask task = new CheckTask();
                tasks.add(task);
                task.execute();
            }
            @Override
            public int getDrawable() {

                return R.drawable.ic_done_black_24dp;

            }

            @Override
            public String getText() {
                return "执行";
            }
        });

        if (Constant.DEBUG) {
            if (actionBar != null) {
                actionBar.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //
                        BarCodeHelper.testBarcode(v.getContext());
                        return true;
                    }
                });
            }
        }
    }


    /**
     * 设置长宽显示参数
     */
    private void setLayoutParams() {

        WindowManager.LayoutParams params = getWindow().getAttributes();
        //params.height = DisplayUtil.getHeightPixels(this) * 2 / 3;
        params.height = DisplayUtil.getHeightPixels(this) * 7 / 8;
        params.width = DisplayUtil.getWidthPixels(this) * 14 / 15;
        this.getWindow().setAttributes(params);
    }

    private void initView() {
        tv_brxm = (TextView) findViewById(R.id.form_brxm);
        tv_brch = (TextView) findViewById(R.id.form_brch);
        tv_brxb = (TextView) findViewById(R.id.form_brxb);
        tv_brnl = (TextView) findViewById(R.id.form_brnl);
        tv_ypyf = (TextView) findViewById(R.id.form_ypyf);
        tv_sypc = (TextView) findViewById(R.id.form_sypc);
        tv_jhrq = (TextView) findViewById(R.id.form_jhrq);
        tv_jyhd = (TextView) findViewById(R.id.form_jyhd);
        tv_byhd = (TextView) findViewById(R.id.form_byhd);

        if(id_swipe_refresh_layout != null) {
            id_swipe_refresh_layout.setEnabled(false);
        }

        list = (ListView) findViewById(R.id.id_lv);
        if (form != null) {
            tv_brxm.setText(notNullCheck(form.BRXM));
            tv_brch.setText(notNullCheck(form.BRCH).contains("床") ? notNullCheck(form.BRCH)
                    : notNullCheck(form.BRCH) + "床");
            String brxb = notNullCheck(form.BRXB);
            if (brxb.equals("1")) {
                brxb = "男";
            } else if (brxb.equals("2")) {
                brxb = "女";
            } else {
                brxb = "未知";
            }
            tv_brxb.setText(brxb);
            //tv_brnl.setText(notNullCheck(form.BRNL) + "岁");
            tv_brnl.setText(notNullCheck(form.BRNL));
            tv_jyhd.setText("加药:\n    " + notNullCheck(form.JYHDR) + "\n    "
                    + parserDatetime(form.JYHDSJ));
            if (isDispend.equals("1")) {
                tv_byhd.setText("摆药:\n    " + notNullCheck(form.BYHDR)
                        + "\n    " + parserDatetime(form.BYHDSJ));
            } else {
                tv_byhd.setVisibility(View.GONE);
                /*((ImageView) findViewById(R.id.form_tail_split1))
                        .setVisibility(View.GONE);*/
            }

            tv_jhrq.setText(parserDate(form.SYSJ) + "日用");
        } else {
            toastInfo("传入参数有误");
        }
    }

    @Override
    protected void toRefreshData() {
        GetDataTask task = new GetDataTask();
        tasks.add(task);
        task.execute();
    }


    private String parserDatetime(String datetime) {
        if ("".equals(datetime) || EmptyTool.isBlank(datetime)) {
            return "";
        } else {
            String dateTimeStr = "";
            try {
                dateTimeStr = datetime.substring(5, 16);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dateTimeStr;
        }
    }

    private String parserDate(String date) {
        if ("".equals(date) || EmptyTool.isBlank(date)) {
            return "";
        } else {
            String dateStr = "";
            try {
                dateStr = date.substring(5, 10);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return dateStr;
        }
    }

    private String notNullCheck(String value) {
        if (EmptyTool.isBlank(value)) {
            return "";
        } else if (value == null) {
            return "";
        } else {
            return value;
        }

    }

    public void toastInfo(String msg) {
        showSnack(msg);
        // Crouton.showText(this, msg, style, viewGroupId);
    }

    class GetDataTask extends AsyncTask<Void, Void, Response<CheckDetail>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<CheckDetail> doInBackground(Void... params) {
            String jgid = mAppApplication.jgId;
            String userId = mAppApplication.user.YHID;

            return AdviceCheckApi.getInstance(AdviceCheckDetailActivity.this)
                    .getFormDetail(form.SYDH, form.GSLX, userId, check_mode,
                            jgid);
        }

        @Override
        protected void onPostExecute(Response<CheckDetail> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (result.ReType == 100) {
                new AgainLoginUtil(AdviceCheckDetailActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        toRefreshData();
                    }
                }).showLoginDialog();
                return;
            } else if (result.ReType == 0) {
                @SuppressWarnings("unchecked")
                ArrayList<AdviceFormDetail> detailList = (ArrayList<AdviceFormDetail>) result.Data.adviceFormDetails;
                List<Map<String, String>> yz = new ArrayList<Map<String, String>>();
                for (AdviceFormDetail item : detailList) {
                    Map<String, String> map = new HashMap<String, String>();
                    StringBuilder mx = new StringBuilder();
                    String num = "";
                    if (!EmptyTool.isBlank(item.YZMC)) {
                        mx.append(item.YZMC);
                    }
                    // if (!EmptyTool.isBlank(item.YCJL)
                    // && !EmptyTool.isBlank(item.JLDW))
                    // mx.append("/" + item.YCJL + ":" + item.JLDW);
                    if (!EmptyTool.isBlank(item.YCSL)
                            && !EmptyTool.isBlank(item.SLDW)) {
                        num = "数量:" + item.YCSL + item.SLDW;
                    }

                    if(!"".equals(num)) {
                        num += "/";
                    }

                    if (!EmptyTool.isBlank(item.YCJL)  && !EmptyTool.isBlank(item.JLDW)) {
                        num = num + "剂量" + item.YCJL + ":" + item.JLDW;
                    }

                    map.put("name", mx.toString());
                    map.put("num", num);
                    yz.add(map);
                    tv_ypyf.setText(item.YFMC);
                    tv_sypc.setText(item.SYPC);
                }
                if (EmptyTool.isEmpty(detailList)) {
                    detailList = new ArrayList<>();
                    TestDataHelper.buidTestData(AdviceFormDetail.class, detailList);
                }
                list.setAdapter(new SimpleAdapter(
                        AdviceCheckDetailActivity.this, yz,
                        R.layout.item_list_text_advice_check, new String[]{
                        "name", "num"}, new int[]{
                        R.id.detail_name, R.id.detail_num}));
            }
        }
    }

    class CheckTask extends AsyncTask<Void, Void, Response<CheckDetail>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(R.string.doing);
        }

        @Override
        protected Response<CheckDetail> doInBackground(Void... params) {
            String jgid = mAppApplication.jgId;
            String userId = mAppApplication.user.YHID;
            if (isScan) {
                return AdviceCheckApi.getInstance(
                        AdviceCheckDetailActivity.this).scanExecute(
                        entity.TMNR, entity.TMQZ, userId, check_mode, jgid);
            } else {
                return AdviceCheckApi.getInstance(
                        AdviceCheckDetailActivity.this).HandExecute(form.SYDH,
                        form.GSLX, userId, check_mode, jgid);
            }

        }



        @Override
        protected void onPostExecute(Response<CheckDetail> result) {
            super.onPostExecute(result);
            hideLoadingDialog();
            tasks.remove(this);
            if (result.ReType == 100) {
                new AgainLoginUtil(AdviceCheckDetailActivity.this, mAppApplication).showLoginDialog();
                return;
            } else if (result.ReType == 0) {
                if (!EmptyTool.isBlank(result.Msg)) {
                    showMsgAndVoice(result.Msg);
                } else {

                    showMsgAndVoice("操作成功");
                }
                /*MediaUtil.getInstance(AdviceCheckDetailActivity.this)
                        .playSound(R.raw.success,
                                AdviceCheckDetailActivity.this);*/
                if (!result.Data.IsFalse.equals("IsTrue")) {
                    setResult(AdviceCheckFragment.RESPONSE_OK);
                    finish();
                }

            } else {
                if (!EmptyTool.isBlank(result.Msg)) {

                    showMsgAndVoice(result.Msg);
                } else {
                    showMsgAndVoiceAndVibrator("操作失败");
                }
               /* MediaUtil.getInstance(AdviceCheckDetailActivity.this)
                        .playSound(R.raw.wrong, AdviceCheckDetailActivity.this);*/

            }
        }
    }
}
