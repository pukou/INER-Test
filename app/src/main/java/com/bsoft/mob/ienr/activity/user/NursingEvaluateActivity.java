package com.bsoft.mob.ienr.activity.user;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.SignNewActivity;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.api.EvaluateApi;
import com.bsoft.mob.ienr.api.OffLineApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.dynamicui.evaluate.nursingeval.NursingEvaluateViewFactory;
import com.bsoft.mob.ienr.helper.HtmlCompatHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.nursingeval.KeyValue;
import com.bsoft.mob.ienr.model.nursingeval.NursingEvaluateItem;
import com.bsoft.mob.ienr.model.nursingeval.NursingEvaluateRecord;
import com.bsoft.mob.ienr.model.nursingeval.NursingEvaluateRecordDetail;
import com.bsoft.mob.ienr.model.nursingeval.NursingEvaluateStyte;
import com.bsoft.mob.ienr.model.nursingeval.RelationDataParam;
import com.bsoft.mob.ienr.model.nursingeval.RelationDataParamItem;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.OffLineUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.util.tools.KeyBoardTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;
import com.bsoft.mob.ienr.view.floatmenu.menu.IFloatMenuItem;
import com.bsoft.mob.ienr.view.floatmenu.menu.TextFloatMenuItem;
import com.fasterxml.jackson.core.type.TypeReference;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * 护理评估页
 */
public class NursingEvaluateActivity extends BaseBarcodeActivity {

    public String from_style_ysxh;
    public String from_style_yslx;
    public String from_style_bbh;

    public String nowJLXH;

    private TextView evaluate_state_txt;
    /**
     * 请求获取已记录评估单列表
     */
    private static final int REQUEST__GET_RECORD_FORM_LIST = 0;
    /**
     * 请求获取签名用户信息
     */
    private static final int REQUEST_CODE_GET_USER_INFO = 1;
    private static final int REQUEST_CODE_GET_USER_INFO_FROM_ITEM = 2;


    //    private static final String STATUE_NEED_SAVE = "当前状态：待保存";
    private static final String STATUE_NEED_SAVE = "当前状态：<font color='red'>待保存</font>";
    private static final String STATUE_NEED_SIGN = "当前状态：待签名";
    private static final String STATUE_NEED_REVIEW = "当前状态：待审阅";
    private static final String STATUE_SAVEED = "当前状态：已保存";
    private static final String STATUE_SIGNED = "当前状态：已签名";
    private static final String STATUE_REVIEWED = "当前状态：已审阅";
    //
    private boolean isCreate = false;
    private boolean isSigned = false;
    private boolean isReviewed = false;
    private boolean globalInputAble = true;//默认可编辑
    private NestedScrollView mScrollView;
    private View timePageView;
    private TextView mTimeView;
    private boolean isShowReviewMenu = false;
    private NursingEvaluateStyte nursingEvaluateStyte;
    private NursingEvaluateRecord nursingEvaluateRecord;
    private NursingEvaluateRecord nursingEvaluateRecordRaw;
    private NursingEvaluateViewFactory mNursingEvaluateViewFactory;

    private String nowSignMode;
    private String nowSignWho;


    private boolean isGlobalInputAble(String qmgh, String sygh) {
        if (!EmptyTool.isBlank(sygh)) {
            //已审阅
            return false;
        }
        if (EmptyTool.isBlank(qmgh)) {
            return true;
        }
        if (mAppApplication.user == null) {
            return true;
        }
        String txgh = mAppApplication.user.YHID;
        if (qmgh.equals(txgh)) {
            return true;
        } else {
            return false;
        }
    }


    @Override
    protected List<IFloatMenuItem> configFloatMenuItems() {

        /*final int[] itemDrawables = {R.drawable.menu_create,
                R.drawable.menu_view};*/
        final int[] itemDrawables = {R.drawable.menu_create};
        final int[][] itemStringDrawables = {
                {R.drawable.menu_create, R.string.comm_menu_add},
        };
        List<IFloatMenuItem> floatMenuItemList = new ArrayList<>();
   /*     for (int itemDrawableResid : itemDrawables) {
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

    private void initTimePageView() {

        mTimeView = (TextView) timePageView
                .findViewById(R.id.nurse_datetime_txt);

        mTimeView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                int viewId = v.getId();

                String dateStr = mTimeView.getText().toString();
                if (EmptyTool.isBlank(dateStr)) {
                    return;
                }
                // 导入年月数据
                YmdHMs ymdHMs = DateTimeHelper.dateTime2YmdHMs(dateStr);
                showPickerDateTimeCompat(ymdHMs, viewId);
            }
        });

        String yyyyMMddHHmm = DateTimeHelper.getServer_yyyyMMddHHmm00();
        initTimeTxt(yyyyMMddHHmm, R.id.nurse_datetime_txt);
    }

    private void getData() {
        Intent intent = getIntent();
        if (intent != null) {
            String yyyyMMddHHmm = DateTimeHelper.getServer_yyyyMMddHHmm00();
            initTimeTxt(yyyyMMddHHmm, mTimeView.getId());

            from_style_ysxh = intent.getStringExtra("YSXH");
            from_style_yslx = intent.getStringExtra("YSLX");
            from_style_bbh = intent.getStringExtra("BBH");

           /* nowJLXH = intent.getStringExtra("JLXH");
            from_record_txsj = intent.getStringExtra("TXSJ");*/

            ationGetDateTask();

        }
    }

    private void onMenuItemClick(int drawableId) {

        if (drawableId == R.drawable.menu_create) {// 新增护理记录
            finish();
        } /*else if (drawableId == R.drawable.menu_view) {// 查看先前记录

            goToEvaluateFormListActivity();
        }*/ else if (drawableId == R.drawable.menu_help) {// 查看先前记录
            if (mNursingEvaluateViewFactory == null) {
                showMsgAndVoiceAndVibrator("请先打开评估单，再进行操作");
                return;
            }
            mNursingEvaluateViewFactory.setAllEmptyValue(mScrollView, "/");
        } else if (drawableId == R.drawable.menu_sign) {// 签名
            if (nursingEvaluateRecord == null) {
                showMsgAndVoiceAndVibrator("请先打开评估单，再进行操作");
                return;
            }
            if (EmptyTool.isBlank(nursingEvaluateRecord.JLXH)) {
                showMsgAndVoiceAndVibrator("请先保存评估单，再进行签名操作");
                return;
            }
            startSignNewActivityForResult("sign", "1");
        } else if (drawableId == R.drawable.menu_cancel_sign) {//取消 签名
            if (nursingEvaluateRecord == null) {
                showMsgAndVoiceAndVibrator("请先打开评估单，再进行操作");
                return;
            }
            if (EmptyTool.isBlank(nursingEvaluateRecord.JLXH)) {
                showMsgAndVoiceAndVibrator("请先保存评估单，再进行取消签名操作");
                return;
            }
            startSignNewActivityForResult("unsign", "1");
        } else if (drawableId == R.drawable.menu_fresh) {// 刷新
            ationGetDateTask();
        } else if (drawableId == R.drawable.menu_review) {// 审阅
            if (nursingEvaluateRecord == null) {
                showMsgAndVoiceAndVibrator("请先打开评估单，再进行操作");
                return;
            }
            if (EmptyTool.isBlank(nursingEvaluateRecord.JLXH)) {
                showMsgAndVoiceAndVibrator("请先保存评估单，再进行审阅操作");
                return;
            }
            startSignNewActivityForResult("sign", "2");
        } else if (drawableId == R.drawable.menu_cancel_review) {// 取消 审阅
            if (nursingEvaluateRecord == null) {
                showMsgAndVoiceAndVibrator("请先打开评估单，再进行操作");
                return;
            }
            if (EmptyTool.isBlank(nursingEvaluateRecord.JLXH)) {
                showMsgAndVoiceAndVibrator("请先保存评估单，再进行取消审阅操作");
                return;
            }
            startSignNewActivityForResult("unsign", "2");
        } else if (drawableId == R.drawable.menu_save) {

            actionSaveTask();
        }
    }

    private void goToEvaluateFormListActivity() {
        startEvaluateFormListActivity(NurseEvaluateFormListActivity.TYPE_RECORD_FORM,
                REQUEST__GET_RECORD_FORM_LIST);
    }


    private void startEvaluateFormListActivity(int startType, int requestCode) {
        Intent intent = new Intent(this, NursingEvaluateFormListActivity.class);
        intent.putExtra(NursingEvaluateFormListActivity.EXTRE_INT_START_TYPE,
                startType);
        startActivityForResult(intent, requestCode);
    }

    private void actionSaveTaskInnerContinue() {
        // 离线保存
        if (!OffLineUtil.WifiConnected(this)) {

            if (mNursingEvaluateViewFactory == null) {
                return;
            }
            String data = "";
            try {
                data = JsonUtil.toJson(nursingEvaluateRecord);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String url = OffLineApi.getInstance(this).url;
            String uri = url + "evaluation/post/SaveEvaluation";
            if (OffLineUtil.offLineSave(this, uri, 2, data,
                    mAppApplication.sickPersonVo.BRXM, "护理评估",
                    mAppApplication.user.YHXM)) {
                showMsgAndVoice("当前网络未连接，已为您保存在本地。网络连接好后，请到【离线保存】菜单中提交。");
                return;
            }
        }
        ///
        actionSaveTaskInner();
    }

    private void actionSaveTask() {
        //
        KeyBoardTool.hideKeyboard(mScrollView);
        //
        if (isReviewed) {
            showMsgAndVoiceAndVibrator("已审阅的评估单，不允许修改");
            return;
        }
        if (mNursingEvaluateViewFactory == null) {
            //todo
            return;
        }

        nursingEvaluateRecord = mNursingEvaluateViewFactory.getAllValueSimple(mScrollView).first;
        List<NursingEvaluateItem> neiListBack = mNursingEvaluateViewFactory.getAllValueSimple(mScrollView).second;
        if (nursingEvaluateRecord == null) {
            //todo
            return;
        }
        //
        noValueMap.clear();
        checkHasValue(neiListBack);
        //////
        if (noValueMap.isEmpty()) {
            //都填写了
            actionSaveTaskInner();
            return;
        }
        List<String> noValueList = new ArrayList<>();
        for (String key : noValueMap.keySet()) {
            String value = noValueMap.get(key);
            //if (!noValueList.contains(value)) {
            noValueList.add(value);
            // }
        }
        ////
        StringBuilder content = new StringBuilder();
        for (int i = 0; i < noValueList.size(); i++) {
            content.append(noValueList.get(i));
            if (i != noValueList.size() - 1) {
                content.append("\n");
            }
        }
        //显示确认对话框
        showConfirmWarn(content.toString());

    }

    private Map<String, String> noValueMap = new LinkedHashMap<>();

    private boolean hasValue(Map<String, String> childValueMap) {
        if (childValueMap != null && !childValueMap.isEmpty()) {
            for (String key : childValueMap.keySet()) {
                String value = childValueMap.get(key);
                if (!EmptyTool.isBlank(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void checkHasValue(List<NursingEvaluateItem> neiList) {
        if (neiList == null || neiList.isEmpty()) {
            return;
        }
        for (NursingEvaluateItem nei : neiList) {
            if (!nei.childViewIsGone) {

                switch (nei.XJKJLX) {//XJKJLX
                    case "1":
                        if (!hasValue(nei.childValueMap)) {
                            String myKey = nei.XMXH;
                            noValueMap.put(myKey, nei.XMMC);
                        }
                        break;
                    case "2":
                        if (!hasValue(nei.childValueMap)) {
                            String myKey = nei.XMXH;
                            noValueMap.put(myKey, nei.XMMC);
                        }
                        break;
                    case "3":
                        if (!hasValue(nei.childValueMap)) {
                            String myKey = nei.XMXH;
                            noValueMap.put(myKey, nei.XMMC);
                        }
                        break;
                    case "4":
                        if (!hasValue(nei.childValueMap)) {
                            String myKey = nei.XMXH;
                            noValueMap.put(myKey, nei.XMMC);
                        }
                        break;
                    case "5":
                        if (!hasValue(nei.childValueMap)) {
                            String myKey = nei.XMXH;
                            noValueMap.put(myKey, nei.XMMC);
                        }
                        break;
                    case "6":
                        break;
                    case "7":
                        break;
                    case "9":
                        break;

                }
            }
            //
            checkHasValue(nei.childItems);
        }

    }


    private void actionSaveTaskInner() {
        SaveDateTask task = new SaveDateTask();
        tasks.add(task);
        task.execute();
    }


    private void showConfirmWarn(String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this); // 先得到构造器
        builder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext, "是否保存")); // 设置标题
        builder.setMessage("以下项目未填写：\n" + content); // 设置内容
        builder.setPositiveButton(getString(R.string.project_operate_ok),
                new DialogInterface.OnClickListener() { // 设置确定按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                        actionSaveTaskInnerContinue();
                    }
                });
        builder.setNegativeButton("继续填写",
                new DialogInterface.OnClickListener() { // 设置取消按钮
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        // 参数都设置完成了，创建并显示出来
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == NursingEvaluateViewFactory.REQ_CODE_LIFE) {
            // 生命体征
            String CJZH = data.getStringExtra("CJZH");
            actionGetRelationData(CJZH);
            return;
        } else if (requestCode == NursingEvaluateViewFactory.REQ_CODE_RISK) {
            // 风险
            String PGXH = data.getStringExtra("PGXH");
            actionGetRelationData(PGXH);
            return;
        }

        if (data == null) {
            return;
        }

        if (requestCode == REQUEST__GET_RECORD_FORM_LIST) {

           /* String txsj = data.getStringExtra("TXSJ");
            String time = DateUtil.get12str(txsj);
            if (!EmptyTool.isBlank(time)) {
                mTimeView.setText(time);
            }*/

            String jlxh = data.getStringExtra("JLXH");
            //
            updateNowJLXH(jlxh);

            ationGetDateTask_JLXH();
            return;
        }

        if (requestCode == REQUEST_CODE_GET_USER_INFO) {
            String userid = data.getStringExtra(SignNewActivity.MY_EXTRA_YHID_KEY);
            String username = data.getStringExtra(SignNewActivity.MY_EXTRA_YHXM_KEY);
            actionSignOrReview(userid, username);
            return;
        }
        if (requestCode == REQUEST_CODE_GET_USER_INFO_FROM_ITEM) {
            String userid = data.getStringExtra(SignNewActivity.MY_EXTRA_YHID_KEY);
            String username = data.getStringExtra(SignNewActivity.MY_EXTRA_YHXM_KEY);
            actionSignOrReview(userid, username);
            return;
        }

    }

    private void actionGetRelationData(String... params) {
        GetRelationDataTask task = new GetRelationDataTask();
        tasks.add(task);
        task.execute(params);
    }

    private class GetRelationDataTask extends AsyncTask<String, String, Response<NursingEvaluateRecord>> {
        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<NursingEvaluateRecord> doInBackground(String... params) {

            if (params == null || params.length < 1) {
                return null;
            }
            String param = params[0];

            List<RelationDataParam> relationDataParamList = buildRelationDataParams(param);

            String data = null;
            try {
                data = JsonUtil.toJson(relationDataParamList);
            } catch (IOException e) {
                e.printStackTrace();
            }
            //
            EvaluateApi api = EvaluateApi.getInstance(NursingEvaluateActivity.this);
            Response<NursingEvaluateRecord> response = api.GetRelationData_V56Update1(data);
            return response;

        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Response<NursingEvaluateRecord> result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(NursingEvaluateActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        return;
                    }
                }).showLoginDialog();

            }
            if (result.ReType != 0) {
                showMsgAndVoice(result.Msg);
                /*MediaUtil.getInstance(NursingEvaluateActivity.this).playSound(
                        R.raw.wrong, NursingEvaluateActivity.this);*/
                return;
            }

            ///###showMsgAndVoice(result.Msg);
            NursingEvaluateRecord nursingEvaluateRecord = result.Data;

            //附加值
            mNursingEvaluateViewFactory.setAllValueByTag(mScrollView, nursingEvaluateRecord.detailList);
            //2 评估时间最近的有效记录
            //1 第一次操作的有效记录
            //3 当前创建的记录
            Log.i("zzffqq", "onPostExecute: 策略SJHQFS：" + nursingEvaluateStyte.SJHQFS);
        }
    }

    private List<RelationDataParam> buildRelationDataParams(String param) {
        Map<String, List<RelationDataParamItem>> relationDataParamItemListMap = mNursingEvaluateViewFactory.getRelationDataParamItemListMap();
        List<RelationDataParam> relationDataParamList = new ArrayList<>();
        for (String ywlb : relationDataParamItemListMap.keySet()) {
            RelationDataParam relationDataParam = new RelationDataParam();
            relationDataParam.YWLB = ywlb;
            relationDataParam.YSXH = nursingEvaluateStyte.YSXH;
            relationDataParam.YSLX = nursingEvaluateStyte.YSLX;
            /**
             * 填写评估单时间 yyyy-mm-dd hh:mi:ss 格式
             */
            relationDataParam.TXSJ = nursingEvaluateRecord.TXSJ;
            relationDataParam.ZYH = nursingEvaluateRecord.ZYH;
            relationDataParam.JGID = nursingEvaluateRecord.JGID;
            /**
             * 业务类别关联的明细项目
             */
            List<RelationDataParamItem> YWLBMX = relationDataParamItemListMap.get(ywlb);
            /**
             * 业务类别:对应评估单项目扩展 2|8 中前半部分信息
             * 2：风险评估 3：宣教 5：生命体征
             */
            switch (ywlb) {
                case "2":
                    //风险评估
                    for (RelationDataParamItem ywlbmx : YWLBMX) {
                        ywlbmx.DZBDJL = param;//PGXH//todo
                    }
                    break;
                case "5":
                    //生命体征
                    /**
                     * 针对生命体征项目，第三种策略时，返回CJZH
                     */
                    relationDataParam.CJZH = param;//CJZH//todo
                    break;
                default:
            }
            relationDataParam.YWLBMX = YWLBMX;
            relationDataParamList.add(relationDataParam);
        }
        return relationDataParamList;
    }


    private void actionSignOrReview(String... params) {
        if (nursingEvaluateRecord == null) {
            showMsgAndVoiceAndVibrator("请先打开评估单，再进行操作");
            return;
        }
        if (EmptyTool.isBlank(nursingEvaluateRecord.JLXH)) {
            showMsgAndVoiceAndVibrator("请先保存评估单，再进行操作");
            return;
        }
        SignOrReviewTask task = new SignOrReviewTask();
        tasks.add(task);
        task.execute(params);
    }

    private class SignOrReviewTask extends AsyncTask<String, String, Response<String>> {
        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.signing);
        }


        @Override
        protected Response<String> doInBackground(String... params) {

            if (params == null || params.length < 2) {
                return null;
            }

            String jlxh = nursingEvaluateRecord.JLXH;
            String userid = params[0];
            String username = params[1];
            EvaluateApi api = EvaluateApi.getInstance(NursingEvaluateActivity.this);
            try {
                if (!EmptyTool.isBlank(username)) {
                    username = URLEncoder.encode(username, "UTF-8");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Response<String> response = api.SignNursingEvaluation_V56Update1(nowSignMode, jlxh, nowSignWho, userid, username);
            return response;

        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Response<String> result) {

            hideLoadingDialog();
            tasks.remove(this);

            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(NursingEvaluateActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        return;
                    }
                }).showLoginDialog();

            }
            if (result.ReType != 0) {
                showMsgAndVoice(result.Msg);
              /*  MediaUtil.getInstance(NursingEvaluateActivity.this).playSound(
                        R.raw.wrong, NursingEvaluateActivity.this);*/
                return;
            }

            ///###showMsgAndVoice(result.Msg);
            String msg = EmptyTool.isBlank(result.Data) ? "请求完成" : result.Data;
            if (nowSignWho.equals("2")) {
                showMsgAndVoice(msg.replace("签名", "审阅"));
            } else {
                showMsgAndVoice(msg);
            }

            // 直接刷新纪录
            updateNowJLXHFromNursingEvaluateRecord();
            ationGetDateTask_JLXH();
        }
    }

    private void updateNowJLXH(String jlxh) {
        nowJLXH = jlxh;
    }

    private void updateNowJLXHFromNursingEvaluateRecord() {
        if (nursingEvaluateRecord == null) {
            nowJLXH = null;
            return;
        }
        nowJLXH = nursingEvaluateRecord.JLXH;
    }

    public void startREQ_RISKActivityForResult(String pglx) {
        Intent intent = new Intent(this, RiskEvaluateActivity.class);
          /* 2017年4月27日08:46:43  remove
           intent.putExtra("PGDH", node.Dzbd);
           intent.putExtra("PGLX", node.Dzbdlx);*/
        intent.putExtra("PGDH", "");
        intent.putExtra("PGLX", pglx);
        intent.putExtra("FROMOUT", true);
        // intent.putExtra("TXSJ", Txsj);
        startActivityForResult(intent, NursingEvaluateViewFactory.REQ_CODE_RISK);
    }

    public void startREQ_LIFEActivityForResult(String xmh) {
        Intent intent = new Intent(this, LifeSymptomActivity.class);
        //intent.putExtra("TXSJ", Txsj);
        startActivityForResult(intent, NursingEvaluateViewFactory.REQ_CODE_LIFE);
    }

    public void startSignNewActivityForResultFromItem(String mode, String signWho) {
        nowSignMode = mode;
        nowSignWho = signWho;
        Intent intent = new Intent(this, SignNewActivity.class);
        startActivityForResult(intent, REQUEST_CODE_GET_USER_INFO_FROM_ITEM);
    }

    public void startSignNewActivityForResult(String mode, String signWho) {
        nowSignMode = mode;
        nowSignWho = signWho;
        Intent intent = new Intent(this, SignNewActivity.class);
        startActivityForResult(intent, REQUEST_CODE_GET_USER_INFO);
    }

    @Override
    public void onDateTimeSet(int year, int monthOfYear, int dayOfMonth,
                              int hourOfDay, int minute, int viewId) {

        String dateTime = DateTimeFactory.getInstance().ymdhms2DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minute, 0);
        initTimeTxt(dateTime,viewId);
    }

    private void initTimeTxt(String yyyyMMddHHmm, int viewId) {
        View timeView = findViewById(viewId);
        if (timeView != null && timeView instanceof TextView) {
            ((TextView) timeView).setText(yyyyMMddHHmm);
        }
    }


    private void initActionBar() {

        actionBar.setTitle("护理评估");
        actionBar.setPatient(mAppApplication.sickPersonVo.BRCH
                + mAppApplication.sickPersonVo.BRXM);

        actionBar.addAction(new Action() {
            @Override
            public int getDrawable() {
                return R.drawable.menu_history_n;
            }

            @Override
            public String getText() {
                return "历史";
            }

            @Override
            public void performAction(View view) {
                goToEvaluateFormListActivity();
            }
        });
        actionBar.addAction(new Action() {
            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }

            @Override
            public String getText() {
                return "保存";
            }

            @Override
            public void performAction(View view) {
                actionSaveTask();
            }
        });

    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_nursing_evaluate;
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected void toRefreshData() {
        super.toRefreshData();
        getData();
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mScrollView = (NestedScrollView) findViewById(R.id.id_sv);
        timePageView = findViewById(R.id.id_ll_controller);
        evaluate_state_txt = (TextView) findViewById(R.id.evaluate_state_txt);
        //add  by louis
        //api.GetNewEvaluationList 的 sql语句中  yslx已经<90  所以单子都不会显示
        //api.GetNewEvaluationListForYslx  会出现不<90的 所以要隐藏掉审阅按钮
        String yslx = getIntent().getStringExtra("YSLX");
        try {
            int yslx_int = Integer.valueOf(yslx);
            isShowReviewMenu = yslx_int < 90;//大于90的不显示
        } catch (Exception e) {
            //e.printStackTrace();
        }
        initActionBar();
        initTimePageView();
        toRefreshData();
    }

    class SaveDateTask extends
            AsyncTask<Void, Response<NursingEvaluateRecord>, Response<NursingEvaluateRecord>> {

        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.saveing);
        }

        @Override
        protected Response<NursingEvaluateRecord> doInBackground(
                Void... params) {

            if (params == null || mAppApplication.sickPersonVo == null
                    || mAppApplication.user == null) {
                return null;
            }

            if (!isCreate) {
                //都是非新增状态处理status

                //查找设置 add update ignore
                if (nursingEvaluateRecord.detailList != null) {
                    for (NursingEvaluateRecordDetail detail : nursingEvaluateRecord.detailList) {
                        findNeedAddUpdateIgnoreDetailInRaw(detail);
                    }
                }
                //查找 delete
                if (nursingEvaluateRecordRaw != null && nursingEvaluateRecordRaw.detailList != null) {
                    List<NursingEvaluateRecordDetail> needDeleteDetailList = new ArrayList<>();
                    for (NursingEvaluateRecordDetail detailRaw : nursingEvaluateRecordRaw.detailList) {
                        boolean needDelete = findNeedDeleteDetailRaw(detailRaw, nursingEvaluateRecord.detailList);
                        if (needDelete) {
                            needDeleteDetailList.add(detailRaw);
                        }
                    }
                    //设置 delete
                    nursingEvaluateRecord.detailList.addAll(needDeleteDetailList);
                }
            }
           /* if (nursingEvaluateRecordRaw!=null&&nursingEvaluateRecordRaw.detailList!=null){
                for (NursingEvaluateRecordDetail detailRaw : nursingEvaluateRecordRaw.detailList) {
                    findDetailInRaw(detailRaw);
                }
            }*/


          /*  View cRootView = mScrollView.getChildAt(0);
            if (cRootView == null) {
                return null;
            }*/
          /*  EvaluateSaveRespose mRespose = getSaveString(cRootView, params[0]);
            mRespose.bqdm = mAppApplication.getAreaId();
            mRespose.jgid = mAppApplication.jgId;*/
            String data = null;
            try {
                data = JsonUtil.toJson(nursingEvaluateRecord);
            } catch (IOException e) {
                e.printStackTrace();
            }

            EvaluateApi api = EvaluateApi.getInstance(NursingEvaluateActivity.this);
            Response<NursingEvaluateRecord> response = api.SaveNursingEvaluation_V56Update1(data);

            return response;
        }

        @Override
        protected void onPostExecute(Response<NursingEvaluateRecord> result) {

            hideLoadingDialog();
            tasks.remove(this);

            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：解析数据错误");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(NursingEvaluateActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        //
                        actionSaveTaskInnerContinue();
                    }
                }).showLoginDialog();
                return;
            }
            if (result.ReType != 0) {
                showMsgAndVoice(result.Msg);
             /*   MediaUtil.getInstance(NursingEvaluateActivity.this).playSound(
                        R.raw.wrong, NursingEvaluateActivity.this);*/
            } else {
//                showMsgAndVoice(R.string.project_save_success);
                showMsgAndVoice(result.Msg);
                isCreate = false;
                //更新数据
                nursingEvaluateRecord = result.Data;
                //刷新视图数据
                buildViews(null);
            }
        }
    }

    private boolean findNeedDeleteDetailRaw(NursingEvaluateRecordDetail detailRaw, List<NursingEvaluateRecordDetail> recordDetailList) {
        boolean needDelete = true;
        for (NursingEvaluateRecordDetail detail : recordDetailList) {
            if (detailRaw.XMXH != null && detailRaw.XMXH.equals(detail.XMXH)) {
                needDelete = false;
                break;
            }
        }
        if (needDelete) {
            detailRaw.Status = "delete";
        }
        return needDelete;
    }

    private void findNeedAddUpdateIgnoreDetailInRaw(NursingEvaluateRecordDetail detail) {
        if (nursingEvaluateRecordRaw == null || nursingEvaluateRecordRaw.detailList == null) {
            return;
        }
        boolean hasInRaw = false;
        for (NursingEvaluateRecordDetail detailRaw : nursingEvaluateRecordRaw.detailList) {
            if (detailRaw.XMXH != null && detailRaw.XMXH.equals(detail.XMXH)) {
                if (detailRaw.XMNR != null && detailRaw.XMNR.equals(detail.XMNR)) {
                    detail.Status = "ignore";
                } else {
                    detail.Status = "update";
                }
                //
                hasInRaw = true;
                break;
            }
        }
        if (!hasInRaw) {
            detail.Status = "add";
        }
    }


    class GetDateTask_YSXH extends AsyncTask<String, Void, Response<KeyValue<List<NursingEvaluateStyte>, Map<String, String>>>> {
        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<KeyValue<List<NursingEvaluateStyte>, Map<String, String>>> doInBackground(String... params) {

            if (params == null) {
                return null;
            }
            EvaluateApi api = EvaluateApi.getInstance(NursingEvaluateActivity.this);
            if (mAppApplication.sickPersonVo == null) {
                return null;
            }
            String zyh = mAppApplication.sickPersonVo.ZYH;
            String jgid = mAppApplication.jgId;
            String brbq = mAppApplication.getAreaId();
            Response<KeyValue<List<NursingEvaluateStyte>, Map<String, String>>> response =
                    api.GetNewNursingEvaluation_V56Update1(zyh, from_style_ysxh,
                            from_style_bbh, brbq, jgid);
            Log.i(Constant.TAG_COMM, "GetNursingEvaluation_V56Update1_from_style_ysxh: " + from_style_ysxh);
            return response;
        }

        @Override
        protected void onPostExecute(Response<KeyValue<List<NursingEvaluateStyte>, Map<String, String>>> result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：解析数据错误");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(NursingEvaluateActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                       toRefreshData();
                    }
                }).showLoginDialog();
                return;
            }
            if (result.ReType != 0) {
                showMsgAndVoice(result.Msg);
                /*MediaUtil.getInstance(NursingEvaluateActivity.this).playSound(
                        R.raw.wrong, NursingEvaluateActivity.this);*/
                return;
            }
            //评估单样式
            List<NursingEvaluateStyte> nursingEvaluateStyteList = result.Data.key;
            if (nursingEvaluateStyteList != null && !nursingEvaluateStyteList.isEmpty()) {
                nursingEvaluateStyte = nursingEvaluateStyteList.get(0);
            }

            //baseInfoMap
            Map<String, String> baseInfoMap = result.Data.value;
            //创建数据
            nursingEvaluateRecord = createNursingEvaluateRecord();
            isCreate = true;
            //
            //构建视图数据
            buildViews(baseInfoMap);

        }
    }

    private void buildViews(Map<String, String> baseInfoMap) {
        if (!isCreate) {
            //不是新增
            globalInputAble = isGlobalInputAble(nursingEvaluateRecord.QMGH, nursingEvaluateRecord.SYGH);
            isSigned = !EmptyTool.isBlank(nursingEvaluateRecord.QMGH);
            isReviewed = !EmptyTool.isBlank(nursingEvaluateRecord.SYGH);
        }

        //拷贝原数据备用
        deepCopyNursingEvaluateRecord();
        //
        refreshMenu();

        if (mScrollView.getChildCount() > 0) {
            mScrollView.removeAllViews();
        }
        if (mNursingEvaluateViewFactory == null) {
            mNursingEvaluateViewFactory = new NursingEvaluateViewFactory(NursingEvaluateActivity.this, nursingEvaluateStyte.itemList);
        }
        mNursingEvaluateViewFactory.clearAll();
        mNursingEvaluateViewFactory.parseViewRoot(mScrollView, globalInputAble);
        mNursingEvaluateViewFactory.hideAllOrganize();
        //设置值
        mNursingEvaluateViewFactory.setAllValueSimple(mScrollView, nursingEvaluateRecord, baseInfoMap, isCreate);
        //更新值
        updateNowJLXHFromNursingEvaluateRecord();
    }

    private void deepCopyNursingEvaluateRecord() {
        try {
            String nursingEvaluateRecord_json = JsonUtil.toJson(nursingEvaluateRecord);
            if (!EmptyTool.isBlank(nursingEvaluateRecord_json)) {
                nursingEvaluateRecordRaw = JsonUtil.fromJson(nursingEvaluateRecord_json,
                        new TypeReference<NursingEvaluateRecord>() {
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private NursingEvaluateRecord createNursingEvaluateRecord() {
        NursingEvaluateRecord nursingEvaluateRecord = new NursingEvaluateRecord();
        // nursingEvaluateRecord.JLXH = ;
        nursingEvaluateRecord.ZYH = mAppApplication.sickPersonVo.ZYH;
        nursingEvaluateRecord.BRBQ = mAppApplication.getAreaId();
        nursingEvaluateRecord.YSXH = nursingEvaluateStyte.YSXH;
        nursingEvaluateRecord.BBH = from_style_bbh;
        nursingEvaluateRecord.YSLX = nursingEvaluateStyte.YSLX;

        nursingEvaluateRecord.TXGH = mAppApplication.user.YHID;
        nursingEvaluateRecord.TXSJ = mTimeView.getText().toString() + ":00";
        nursingEvaluateRecord.TXXM = mAppApplication.user.YHXM;
        //###nursingEvaluateRecord.JLSJ = DateUtil.getNowDateTime();//服务端提供
        //nursingEvaluateRecord.QMGH="";
        //nursingEvaluateRecord.QMSJ="";
        //nursingEvaluateRecord.QMXM="";
        //nursingEvaluateRecord.SYZT="";
        //nursingEvaluateRecord.SYGH="";
        //nursingEvaluateRecord.SYXM="";
        //nursingEvaluateRecord.SYSJ="";
        //nursingEvaluateRecord.DYCS="";
        //nursingEvaluateRecord.ZFBZ="";
        nursingEvaluateRecord.JGID = nursingEvaluateStyte.JGID;
        //nursingEvaluateRecord.JGID = mAppApplication.jgId;
        //nursingEvaluateRecord.PGNR="";
        //####
        nursingEvaluateRecord.detailList = new ArrayList<>();
        //
        return nursingEvaluateRecord;
    }

    class GetDateTask_JLXH extends AsyncTask<String, Void, Response<KeyValue<List<NursingEvaluateStyte>, NursingEvaluateRecord>>> {
        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<KeyValue<List<NursingEvaluateStyte>, NursingEvaluateRecord>> doInBackground(String... params) {

            if (params == null) {
                return null;
            }
            EvaluateApi api = EvaluateApi.getInstance(NursingEvaluateActivity.this);
            Response<KeyValue<List<NursingEvaluateStyte>, NursingEvaluateRecord>> response =
                    api.GetNursingEvaluation_V56Update1(nowJLXH);
            Log.i(Constant.TAG_COMM, "GetNursingEvaluation_V56Update1_nowJLXH: " + nowJLXH);
            return response;
        }

        @Override
        protected void onPostExecute(Response<KeyValue<List<NursingEvaluateStyte>, NursingEvaluateRecord>> result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：解析数据错误");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(NursingEvaluateActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                   toRefreshData();
                    }
                }).showLoginDialog();
                return;
            }
            if (result.ReType != 0) {
                showMsgAndVoice(result.Msg);
              /*  MediaUtil.getInstance(NursingEvaluateActivity.this).playSound(
                        R.raw.wrong, NursingEvaluateActivity.this);*/
                return;
            }
            //评估单样式
            List<NursingEvaluateStyte> nursingEvaluateStyteList = result.Data.key;

            if (nursingEvaluateStyteList != null && !nursingEvaluateStyteList.isEmpty()) {
                nursingEvaluateStyte = nursingEvaluateStyteList.get(0);
            }

            //NursingEvaluateRecord
            nursingEvaluateRecord = result.Data.value;
            //处理是否新增
            isCreate = false;
            if (nursingEvaluateRecord == null) {
                nursingEvaluateRecord = createNursingEvaluateRecord();
                isCreate = true;
            }
            //刷新视图数据
            buildViews(null);

        }

    }


    private void refreshMenu() {
        int[] itemDrawables = {};
        int[][] itemStringDrawables = {};
     /*   itemDrawables = ArrayUtils.addAll(itemDrawables,
                R.drawable.menu_create, R.drawable.menu_view,
                R.drawable.menu_fresh, R.drawable.menu_help);*/
        itemDrawables = ArrayUtils.addAll(itemDrawables,
                R.drawable.menu_create,
                R.drawable.menu_fresh, R.drawable.menu_help);

        itemStringDrawables = ArrayUtils.addAll(itemStringDrawables,
                new int[]{R.drawable.menu_create, R.string.comm_menu_add},
                new int[]{R.drawable.menu_fresh, R.string.comm_menu_refresh},
                new int[]{R.drawable.menu_help, R.string.comm_menu_help});
        /**
         *   未保存——可以保存
         *   已保存、未签名——可以保存、可以签名、可以审阅
         *   已保存、已签名、未审阅——可以保存、可以取消签名、可以审阅
         *   已保存、已签名、已审阅——可以取消审阅
         */
        if (nursingEvaluateRecord == null || EmptyTool.isBlank(nursingEvaluateRecord.JLXH)) {
            //未保存
            itemDrawables = ArrayUtils.add(itemDrawables, R.drawable.menu_save);
            itemStringDrawables = ArrayUtils.addAll(itemStringDrawables,
                    new int[]{R.drawable.menu_save, R.string.comm_menu_save});
            evaluate_state_txt.setText(HtmlCompatHelper.fromHtml(STATUE_NEED_SAVE));
        } else {
            //已保存
            if (isSigned) {
                //已保存、已签名
                if (isReviewed) {
                    //已保存、已签名、已审阅
                    itemDrawables = ArrayUtils.add(itemDrawables, R.drawable.menu_cancel_review);
                    itemStringDrawables = ArrayUtils.addAll(itemStringDrawables,
                            new int[]{R.drawable.menu_cancel_review, R.string.comm_menu_cancel_review});
                    evaluate_state_txt.setText(STATUE_REVIEWED);
                } else {
                    //已保存、已签名、未审阅
                    if (isShowReviewMenu) {
                        itemDrawables = ArrayUtils.addAll(itemDrawables, R.drawable.menu_save, R.drawable.menu_cancel_sign, R.drawable.menu_review);
                        itemStringDrawables = ArrayUtils.addAll(itemStringDrawables,
                                new int[]{R.drawable.menu_save, R.string.comm_menu_save},
                                new int[]{R.drawable.menu_cancel_sign, R.string.comm_menu_cancel_sign},
                                new int[]{R.drawable.menu_review, R.string.comm_menu_review}
                        );
                        evaluate_state_txt.setText(STATUE_NEED_REVIEW);
                    } else {
                        itemDrawables = ArrayUtils.addAll(itemDrawables, R.drawable.menu_save, R.drawable.menu_cancel_sign);
                        itemStringDrawables = ArrayUtils.addAll(itemStringDrawables,
                                new int[]{R.drawable.menu_save, R.string.comm_menu_save},
                                new int[]{R.drawable.menu_cancel_sign, R.string.comm_menu_cancel_sign}
                        );
                        evaluate_state_txt.setText(STATUE_SIGNED);
                    }
                }
            } else {
                //已保存、未签名
                if (isReviewed) {
                    //已保存、未签名、已审阅
                    itemDrawables = ArrayUtils.addAll(itemDrawables, R.drawable.menu_save, R.drawable.menu_sign, R.drawable.menu_cancel_review);
                    itemStringDrawables = ArrayUtils.addAll(itemStringDrawables,
                            new int[]{R.drawable.menu_save, R.string.comm_menu_save},
                            new int[]{R.drawable.menu_sign, R.string.comm_menu_sign},
                            new int[]{R.drawable.menu_cancel_review, R.string.comm_menu_cancel_review}
                    );
                    evaluate_state_txt.setText(STATUE_REVIEWED);
                } else {
                    //已保存、未签名、未审阅
                    if (isShowReviewMenu) {
                        itemDrawables = ArrayUtils.addAll(itemDrawables, R.drawable.menu_save, R.drawable.menu_sign, R.drawable.menu_review);
                        itemStringDrawables = ArrayUtils.addAll(itemStringDrawables,
                                new int[]{R.drawable.menu_save, R.string.comm_menu_save},
                                new int[]{R.drawable.menu_sign, R.string.comm_menu_sign},
                                new int[]{R.drawable.menu_review, R.string.comm_menu_review}
                        );
                        evaluate_state_txt.setText(STATUE_NEED_SIGN);
                    } else {
                        itemDrawables = ArrayUtils.addAll(itemDrawables, R.drawable.menu_save, R.drawable.menu_sign);
                        itemStringDrawables = ArrayUtils.addAll(itemStringDrawables,
                                new int[]{R.drawable.menu_save, R.string.comm_menu_save},
                                new int[]{R.drawable.menu_sign, R.string.comm_menu_sign}
                        );
                        evaluate_state_txt.setText(STATUE_NEED_SIGN);
                    }
                }
            }
        }
        /////
        List<IFloatMenuItem> floatMenuItemList = new ArrayList<>();
 /*       for (int itemDrawableResid : itemDrawables) {

            FloatMenuItem floatMenuItem = new FloatMenuItem(itemDrawableResid) {
                @Override
                public void actionClick(View view, int resid) {
                    onMenuItemClick(resid);
                }
            };
            floatMenuItemList.add(floatMenuItem);
        } */
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



 /*   private ValueForm parseRelate(Form form) {
        try {
            String json = JsonUtil.toJson(form, JsonSerialize.Inclusion.NON_NULL);
            String regex = "Input\":\\[(.*?)\\]";
            ValueForm entity = new ValueForm();
            jsonInterception(json, regex, entity);
            regex = "Numeric\":\\[(.*?)\\]";
            jsonInterception(json, regex, entity);
            return entity;
        } catch (Exception e) {
            Log.e(Config.TAG, e.getMessage(), e);
        }
        return null;
    }*/

    /* private void jsonInterception(String json, String regex, ValueForm entity)
             throws Exception {
         java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
         Matcher matcher = pattern.matcher(json);
         while (!matcher.hitEnd() && matcher.find()) {
             String str = matcher.group(1);
             JSONObject obj = new JSONObject(str);
             Iterator ite = obj.keys();
             ValueEntity node = new ValueEntity();
             while (ite.hasNext()) {
                 String key = ite.next().toString();
                 if (key.equals("ID")) {
                     node.ID = Integer.parseInt(obj.getString(key));
                 }
                 if (key.equals("GroupId")) {
                     node.GroupId = Integer.parseInt(obj.getString(key));
                 }
                 if (key.equals("dzbdjl_my")) {
                     //add
                     if (obj.getString(key) != null && !obj.getString(key).equals("null")) {
                         node.dzbdjl_my = obj.getString(key);
                     }
                 } else if (key.equals("Value")) {
                     if (obj.getString(key).equals("null")) {
                         continue;
                     } else {
                         node.VALUE = obj.getString(key);
                     }
                 }
             }
             if (node.VALUE != null && !node.VALUE.equals("")) {
                 entity.root.values.add(node);
             }
         }
     }
 */


    private void ationGetDateTask() {
        if (!EmptyTool.isBlank(nowJLXH)) {
            ationGetDateTask_JLXH();
        } else {
            ationGetDateTask_YSXH();
        }

    }

    private void ationGetDateTask_YSXH() {
        GetDateTask_YSXH task = new GetDateTask_YSXH();
        tasks.add(task);
        task.execute();
    }

    private void ationGetDateTask_JLXH() {
        GetDateTask_JLXH task = new GetDateTask_JLXH();
        tasks.add(task);
        task.execute();
    }

    @Override
    public void initBarBroadcast() {
        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (BarcodeActions.Refresh.equals(intent.getAction())) {
                    //todo

                }
            }
        };
    }
}
