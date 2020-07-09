/**
 * @Title: NursePlanActivity.java
 * @Package com.bsoft.mob.ienr.activity.user
 * @Description: 护理计划填写填写页
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-11-19 下午2:48:09
 * @version V1.0
 */
package com.bsoft.mob.ienr.activity.user;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.activity.user.adapter.Gist_PlanRVAdapter;
import com.bsoft.mob.ienr.activity.user.adapter.GoalRVAdapter;
import com.bsoft.mob.ienr.activity.user.adapter.MeasureRVAdapter;
import com.bsoft.mob.ienr.activity.user.adapter.Measure_PlanRVAdapter;
import com.bsoft.mob.ienr.activity.user.adapter.RelevantFactorRVAdapter;
import com.bsoft.mob.ienr.api.NursePlanApi;
import com.bsoft.mob.ienr.api.OffLineApi;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.helper.RecyclerViewHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.nurseplan.DiagnosticBasis;
import com.bsoft.mob.ienr.model.nurseplan.Goal;
import com.bsoft.mob.ienr.model.nurseplan.Measure;
import com.bsoft.mob.ienr.model.nurseplan.Problem;
import com.bsoft.mob.ienr.model.nurseplan.ProblemSaveData;
import com.bsoft.mob.ienr.model.nurseplan.RelevantFactor;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FormSyncUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.OffLineUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;
import com.bsoft.mob.ienr.view.floatmenu.menu.IFloatMenuItem;
import com.bsoft.mob.ienr.view.floatmenu.menu.TextFloatMenuItem;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @ClassName: NursePlanActivity
 * @Description: 护理计划填写页
 * @date 2015-11-19 下午2:48:09
 */
public class NursePlanActivity extends BaseBarcodeActivity {

    public static final int REQUEST_CODE = 1;

    private String wtxh;
    private String gllx;
    private String glxh;
    private boolean isAdd;
    private List<Problem> mProblems;
    private NestedScrollView scroller;
    private RecyclerView measureList, goalList, gistList, relevantFactorList;// 措施，目标列表视图,依据,相关因素
    private EditText problem;// 问题
    private TextView evaStatus;// 评价状态
    private Spinner startTime;// 开始时间
    private ImageView id_iv_for_bar_spinner;// 设置时间
    private ImageView targetAdd, measureAddGroup;// 目标自定义，按组添加措施
    public Problem curProblem;// 当前选中的护理计划问题；
    private int selectedIndex;// 当前选择的位置
    private boolean addedNew;// 是否添加了新的记录
    private int tempCszh = 0;
    private Measure_PlanRVAdapter mMeasure_planRVAdapter;

    @Override
    public void initBarBroadcast() {
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    // 获取NursePlanFragment传递的参数
    private void getParams() {
        wtxh = getIntent().getStringExtra("WTXH");
        gllx = getIntent().getStringExtra("GLLX");
        glxh = getIntent().getStringExtra("GLXH");
        if (gllx.equals("1")) {
            glxh = "0";
        }
        isAdd = getIntent().getBooleanExtra("ISADD", false);
        toRefreshData();
    }

    @Override
    protected void toRefreshData() {
        if (isAdd) {
            addProblem();
        } else {
            getProblem();
        }
    }

    /**
     * @param @param view
     * @return void
     * @throws
     * @Title: initView
     * @Description: 初始化界面
     */
    private Gist_PlanRVAdapter gist_planRVAdapter;
    private GoalRVAdapter mGoalRVAdapter;
    private RelevantFactorRVAdapter mRelevantFactorRVAdapter;

    private void initView() {
        scroller = (NestedScrollView) findViewById(R.id.id_sv);

        relevantFactorList = (RecyclerView) findViewById(R.id.id_rv);
        RecyclerViewHelper.init(relevantFactorList);
        mRelevantFactorRVAdapter = new RelevantFactorRVAdapter(new ArrayList<RelevantFactor>(), R.layout.item_list_nurseplan_measure);
        relevantFactorList.setAdapter(mRelevantFactorRVAdapter);

        gistList = (RecyclerView) findViewById(R.id.id_rv_2);
        RecyclerViewHelper.init(gistList);
        gist_planRVAdapter = new Gist_PlanRVAdapter(new ArrayList<DiagnosticBasis>(), R.layout.item_list_nurseplan_measure);
        gistList.setAdapter(gist_planRVAdapter);

        goalList = (RecyclerView) findViewById(R.id.id_rv_3);
        RecyclerViewHelper.init(goalList);
        mGoalRVAdapter = new GoalRVAdapter(new ArrayList<Goal>(), R.layout.item_list_nurseplan_measure);
        goalList.setAdapter(mGoalRVAdapter);

        measureList = (RecyclerView) findViewById(R.id.id_rv_4);
        RecyclerViewHelper.init(measureList);
        mMeasure_planRVAdapter = new Measure_PlanRVAdapter(new ArrayList<Measure>(), R.layout.item_list_nurseplan_measure);
        mMeasure_planRVAdapter.setOnEditClickListener(new MeasureRVAdapter.OnEditClickListener() {
            @Override
            public void onEditClick(View view, int pos) {
                String cszh = curProblem.JHCS.get(pos).CSZH;
                goEditClick(cszh);
            }
        });
        measureList.setAdapter(mMeasure_planRVAdapter);

        problem = (EditText) findViewById(R.id.nurseform_problem);
        problem.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (EmptyTool.isBlank(s.toString())) {
                    return;
                }
                if (curProblem != null) {
                    curProblem.WTMS = s.toString();
                }
            }
        });
        evaStatus = (TextView) findViewById(R.id.nurseform_evaluatestaus);
        if (isAdd) {
            evaStatus.setText("待保存");
        }
        SpinnerLayout startTimeLayout = (SpinnerLayout) findViewById(R.id.id_spinner_layout);
        startTime = startTimeLayout.getSpinner();
        id_iv_for_bar_spinner = (ImageView) findViewById(R.id.id_iv_for_bar_spinner);

        startTime.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                selectedIndex = arg2;
                curProblem = mProblems.get(arg2);
                curProblem.GLXH = glxh;
                problem.setText(curProblem.WTMS);
                if (curProblem.PJZT != null)
                    if (curProblem.PJZT.equals("1")) {
                        evaStatus.setText("已评价");
                        evaStatus.setTextColor(ContextCompat.getColor(mContext,
                                R.color.textColorSecondary));
                    } else if (curProblem.PJZT.equals("2")) {
                        evaStatus.setText("已结束");
                        evaStatus.setTextColor(ContextCompat.getColor(mContext,
                                R.color.blue));
                    } else {
                        evaStatus.setText("待评价");
                        evaStatus.setTextColor(ContextCompat.getColor(mContext,
                                R.color.red));
                    }
                if (curProblem.ZDYJ != null) {

                    gist_planRVAdapter.refreshDataList(curProblem.ZDYJ);
                }

                if (curProblem.JHCS != null) {
                    mMeasure_planRVAdapter.refreshDataList(curProblem.JHCS);
                }
                if (curProblem.JHMB != null) {
                    mGoalRVAdapter.refreshDataList(curProblem.JHMB);
                }
                if (curProblem.XGYSList != null) {
                    mRelevantFactorRVAdapter.refreshDataList(curProblem.XGYSList);
                }
                if (curProblem != null) {
                    if (curProblem.IsSync) {
                        new FormSyncUtil().InvokeSync(NursePlanActivity.this,
                                curProblem.SyncData, mAppApplication.jgId, tasks);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        id_iv_for_bar_spinner.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (curProblem != null) {
                    String dateTime = DateTimeHelper.getServer_yyyyMMddHHmm00(curProblem.KSSJ);
                    YmdHMs ymdHMs = DateTimeHelper.dateTime2YmdHMs(dateTime);
                    showPickerDateTimeCompat(ymdHMs, v.getId());
                } else {
                    showMsgAndVoiceAndVibrator("请先添加问题");
                }
            }
        });
        targetAdd = (ImageView) findViewById(R.id.nurseplan_gist_add);
        targetAdd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showInputDiaolog("请输入护理目标", 0);
            }
        });
        measureAddGroup = (ImageView) findViewById(R.id.nurseplan_measure_addGroup);
        measureAddGroup.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                tempCszh--;
                for (Measure measure : curProblem.JHCSTemplate) {
                    measure.CSZH = String.valueOf(tempCszh);
                }
                Intent intent = new Intent(NursePlanActivity.this,
                        NursePlanMeasureActivity.class);
                intent.putExtra("measureList", (Serializable) curProblem.JHCSTemplate);
                startActivityForResult(intent,
                        NursePlanMeasureActivity.REQUEST_CODE);
            }
        });
    }

    private void goEditClick(String cszh) {
        List<Measure> templateList = new ArrayList<Measure>();
        templateList.addAll(curProblem.JHCSTemplate);
        for (int i = 0; i < curProblem.JHCS.size(); i++) {
            Measure measure = curProblem.JHCS.get(i);
            if (cszh.equals(measure.CSZH)) {
                for (int j = 0; j < templateList.size(); j++) {
                    templateList.get(j).CSZH = cszh;
                    if (measure.CSXH.equals(templateList.get(j).CSXH)) {
                        templateList.remove(j);
                        templateList.add(j, measure);
                    }
                }
            }
        }
        Intent intent = new Intent(NursePlanActivity.this,
                NursePlanMeasureActivity.class);
        intent.putExtra("measureList", (Serializable) templateList);
        startActivityForResult(intent,
                NursePlanMeasureActivity.REQUEST_CODE);
    }

    @Override
    public void onDateTimeSet(int year, int monthOfYear, int dayOfMonth,
                              int hourOfDay, int minute, int viewId) {
        super.onDateTimeSet(year, monthOfYear, dayOfMonth, hourOfDay, minute,
                viewId);
        if (curProblem != null) {
            curProblem.KSSJ = year
                    + "-"
                    + ((monthOfYear + 1) < 10 ? ("0" + (monthOfYear + 1))
                    : (monthOfYear + 1)) + "-"
                    + (dayOfMonth < 10 ? ("0" + dayOfMonth) : dayOfMonth) + " "
                    + (hourOfDay < 10 ? ("0" + hourOfDay) : hourOfDay) + ":"
                    + (minute < 10 ? ("0" + minute) : minute) + ":00";
            ((TimeAdapter) startTime.getAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    public void onInputCompleteed(String content, int viewId) {
        super.onInputCompleteed(content, viewId);
        if (curProblem == null) {
            return;
        }

        switch (viewId) {
            case 0:
                if (curProblem.JHMB == null) {
                    curProblem.JHMB = new ArrayList<Goal>();
                }
                if (!EmptyTool.isBlank(content)) {
                    Goal goal = new Goal();
                    goal.MBMS = content;
                    goal.ZDYBZ = "1";
                    goal.SELECTED = true;
                    goal.MBXH = curProblem.WTXH;
                    curProblem.JHMB.add(goal);

                    mGoalRVAdapter.refreshDataList(curProblem.JHMB);
                    goalList.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // goalList.smoothScrollToPosition(curProblem.JHMB.size()
                            // - 1);
                        /*    scroller.smoothScrollTo(goalList.getLeft(),
                                    goalList.getBottom() - 150);*/
                        }
                    }, 500);
                }
                break;
            default:
                break;
        }

    }

    /**
     * @Title: initActionBar @Description: 初始化工具条 @param @return void @throws
     */
    private void initActionBar() {
        actionBar.setTitle("护理计划");
        String brch = EmptyTool.isBlank(mAppApplication.sickPersonVo.XSCH) ? "" : mAppApplication.sickPersonVo.XSCH;
        actionBar.setPatient(brch + mAppApplication.sickPersonVo.BRXM);
        actionBar.setBackAction(new Action() {
            @Override
            public String getText() {
                return getString(R.string.menu_back);
            }
            @Override
            public void performAction(View view) {
                if (addedNew)
                    setResult(RESULT_OK);
                finish();
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_arrow_back_black_24dp;
            }
        });
        actionBar.addAction(new Action() {
            @Override
            public String getText() {
                return "保存";
            }
            @Override
            public void performAction(View view) {
                saveProblem();
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }
        });
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_nurse_plan;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        getParams();
        initView();
        initActionBar();

    }

    @Override
    protected List<IFloatMenuItem> configFloatMenuItems() {
        final int[] itemDrawables = {R.drawable.menu_delete,
                R.drawable.menu_create, R.drawable.menu_evaluate,
                R.drawable.menu_terminate, R.drawable.menu_save};
        final int[][] itemStringDrawables = {
                {R.drawable.menu_delete, R.string.comm_menu_delete},
                {R.drawable.menu_create, R.string.comm_menu_add},
                {R.drawable.menu_evaluate, R.string.comm_menu_evaluate},
                {R.drawable.menu_terminate, R.string.comm_menu_terminate},
                {R.drawable.menu_save, R.string.comm_menu_save}};
        List<IFloatMenuItem> floatMenuItemList = new ArrayList<>();
        /*for (int itemDrawableResid : itemDrawables) {
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
            int textResId=itemDrawableRes[1];
            String text = textResId > 0 ? getString(textResId) : null;
            IFloatMenuItem floatMenuItem = new TextFloatMenuItem(itemDrawableResid,text) {
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
            toRefreshData();
        } else if (drawableRes == R.drawable.menu_save) {
            saveProblem();
        } else if (drawableRes == R.drawable.menu_evaluate) {
            if (curProblem == null) {
                showMsgAndVoiceAndVibrator("当前没有要评价的计划问题");
            /*    MediaUtil.getInstance(NursePlanActivity.this).playSound(
                        R.raw.wrong, NursePlanActivity.this);*/
                return;
            }
            if (EmptyTool.isBlank(curProblem.JLWT)) {
                showMsgAndVoiceAndVibrator("该计划问题尚未保存，请先保存");
               /* MediaUtil.getInstance(NursePlanActivity.this).playSound(
                        R.raw.wrong, NursePlanActivity.this);*/
                return;
            }
            if (TextUtils.equals("2", curProblem.PJZT)) {
                showMsgAndVoiceAndVibrator("改计划已经结束，不能再对其进行操作");
               /* MediaUtil.getInstance(NursePlanActivity.this).playSound(
                        R.raw.wrong, NursePlanActivity.this);*/
                return;
            }
            Intent intent = new Intent(NursePlanActivity.this,
                    NursePlanEvaluateActivity.class);
            intent.putExtra("JLWT", curProblem.JLWT);
            intent.putExtra("WTXH", curProblem.WTXH);
            intent.putExtra("WTLX", curProblem.WTLX);
            startActivityForResult(intent,
                    NursePlanEvaluateActivity.REQUEST_CODE);
        } else if (drawableRes == R.drawable.menu_delete) {
            showConfirmDialog("确定要删除这条计划问题么？", "DELETE");
        } else if (drawableRes == R.drawable.menu_terminate) {
            showConfirmDialog("结束之后，改计划问题将不能被编辑或删除，确定要删除这条记录么？", "TERMINATE");
        }
    }

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        super.onActivityResult(arg0, arg1, arg2);
        if (arg0 == NursePlanEvaluateActivity.REQUEST_CODE && arg1 == RESULT_OK) {
            isAdd = false;
            toRefreshData();
        } else if (arg0 == NursePlanMeasureActivity.REQUEST_CODE && arg1 == RESULT_OK) {
            List<Measure> measures = (List<Measure>) arg2.getSerializableExtra("measureList");
            if (measures != null && measures.size() > 0) {
                for (int i = 0; i < curProblem.JHCS.size(); i++) {
                    if (curProblem.JHCS.get(i).CSZH.equals(measures.get(0).CSZH)) {
                        curProblem.JHCS.remove(i);
                        i--;
                    }
                }
                curProblem.JHCS.addAll(measures);

                mMeasure_planRVAdapter.refreshDataList(curProblem.JHCS);
            }

        }
    }

    @Override
    public void onConfirmSet(String action) {
        if (TextUtils.equals(action, "DELETE")) {
            delProblem();
        } else if (TextUtils.equals(action, "TERMINATE")) {
            terminateProblem();
        }
    }

    // 添加一条新的问题记录
    private void addProblem() {
        AddProblemTask task = new AddProblemTask();
        tasks.add(task);
        task.execute();
    }

    // 获取问题记录
    private void getProblem() {
        GetProblemTask task = new GetProblemTask();
        tasks.add(task);
        task.execute();
    }

    // 保存计划问题
    private void saveProblem() {
        if (curProblem == null) {
            showMsgAndVoiceAndVibrator("没有要保存的数据");
            return;
        }
        // 等服务端写 结束状态2
        if (!EmptyTool.isBlank(curProblem.PJZT))
            if ("2".equals(curProblem.PJZT)) {
                showMsgAndVoiceAndVibrator("已结束，不能修改");
                /*MediaUtil.getInstance(NursePlanActivity.this).playSound(
                        R.raw.wrong, NursePlanActivity.this);*/
                return;
            }

        // 离线保存
        if (!OffLineUtil.WifiConnected(this)) {
            String data = "";
            try {
                ProblemSaveData problemSaveData = new ProblemSaveData();
                problemSaveData.Problem = curProblem;
                problemSaveData.ZYH = mAppApplication.sickPersonVo.ZYH;
                problemSaveData.YHID = mAppApplication.user.YHID;
                problemSaveData.BQID = mAppApplication.getAreaId();
                problemSaveData.JGID = mAppApplication.jgId;
                problemSaveData.GLLX = gllx;
                data = JsonUtil.toJson(problemSaveData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String url = OffLineApi.getInstance(this).url;
            String uri = url + "nurseplan/post/saveNursePlanProblem";
            if (OffLineUtil.offLineSave(NursePlanActivity.this, uri, 2, data,
                    mAppApplication.sickPersonVo.BRXM, "护理计划",
                    mAppApplication.user.YHXM))

                showMsgAndVoice("当前网络未连接，已为您保存在本地。网络连接好后，请到【离线保存】菜单中提交。");

            return;
        }

        SavePlanTask task = new SavePlanTask();
        tasks.add(task);
        task.execute();
    }

    // 删除计划问题
    private void delProblem() {
        if (curProblem == null) {
            showMsgAndVoiceAndVibrator("当前没有要删除的计划问题！");
            return;
        }

        if (EmptyTool.isBlank(curProblem.JLWT)) {
            showMsgAndVoiceAndVibrator("计划问题尚未保存，无需删除！");
            return;
        }
        if (TextUtils.equals("2", curProblem.PJZT)) {
            showMsgAndVoiceAndVibrator("改计划已经结束，不能再对其进行操作");
           /* MediaUtil.getInstance(NursePlanActivity.this).playSound(
                    R.raw.wrong, NursePlanActivity.this);*/
            return;
        }
        DelTask task = new DelTask();
        tasks.add(task);
        task.execute();

    }

    // 结束问题
    private void terminateProblem() {
        if (curProblem == null) {
            showMsgAndVoiceAndVibrator("当前没有要结束的计划问题！");
            return;
        }

        if (EmptyTool.isBlank(curProblem.JLWT)) {
            showMsgAndVoiceAndVibrator("计划问题尚未保存，无需结束！");
            return;
        }
        if (TextUtils.equals("2", curProblem.PJZT)) {
            showMsgAndVoiceAndVibrator("改计划已经结束，不能再对其进行操作");
        /*    MediaUtil.getInstance(NursePlanActivity.this).playSound(
                    R.raw.wrong, NursePlanActivity.this);*/
            return;
        }
        id_iv_for_bar_spinner.postDelayed(new Runnable() {

            @Override
            public void run() {
                TerminateTask task = new TerminateTask();
                tasks.add(task);
                task.execute();
            }
        }, 500);
    }

    private void handleResult(List<Problem> result) {
        mProblems = result;
        startTime.setAdapter(new TimeAdapter());
        if (mProblems.size() == 0) {
            curProblem = null;
            gist_planRVAdapter.refreshDataList(new ArrayList<DiagnosticBasis>());
            mGoalRVAdapter.refreshDataList(new ArrayList<Goal>());
            mMeasure_planRVAdapter.refreshDataList(new ArrayList<Measure>());
            mRelevantFactorRVAdapter.refreshDataList(new ArrayList<RelevantFactor>());
        }
        if (selectedIndex < mProblems.size())
            startTime.setSelection(selectedIndex);
    }

    /**
     * @author 吕自聪 lvzc@bsoft.com.cn
     * @Description: 调用webservice添加一条新的问题记录
     * @date 2015-11-30 下午3:38:13
     */
    class AddProblemTask extends AsyncTask<Void, Void, Response<Problem>> {

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<Problem> doInBackground(Void... params) {
            return NursePlanApi.getInstance(NursePlanActivity.this)
                    .addPlanProblem(wtxh, mAppApplication.jgId);
        }

        @Override
        protected void onPostExecute(Response<Problem> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(NursePlanActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            isAdd = true;
                            toRefreshData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    if (result.Data != null) {
                        curProblem = result.Data;
                        curProblem.GLXH = glxh;
                        curProblem.KSSJ = DateTimeHelper.getServer_yyyyMMddHHmm00();
                        mProblems = new ArrayList<>();
                        mProblems.add(curProblem);
                        startTime.setAdapter(new TimeAdapter());
                    }
                } else {
                    showMsgAndVoice(result.Msg);
                    return;
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    /**
     * @author 吕自聪 lvzc@bsoft.com.cn
     * @Description: 获取计划问题记录
     * @date 2015-12-4 上午9:24:03
     */
    class GetProblemTask extends AsyncTask<Void, Void, Response<List<Problem>>> {
        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<Problem>> doInBackground(Void... params) {
            return NursePlanApi.getInstance(NursePlanActivity.this)
                    .getPlanProblemList(mAppApplication.sickPersonVo.ZYH, glxh, wtxh, mAppApplication.jgId);
        }

        @Override
        protected void onPostExecute(Response<List<Problem>> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(NursePlanActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            isAdd = false;
                            toRefreshData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    if (result.Data != null) {
                        handleResult(result.Data);
                    }
                } else {
                    showMsgAndVoice(result.Msg);
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    /**
     * @author 吕自聪 lvzc@bsoft.com.cn
     * @Description: 保存计划问题
     * @date 2015-12-1 下午1:56:01
     */
    class SavePlanTask extends AsyncTask<Void, Void, Response<List<Problem>>> {
        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.saveing);
        }

        @Override
        protected Response<List<Problem>> doInBackground(Void... params) {
            String data = "";
            try {
                ProblemSaveData problemSaveData = new ProblemSaveData();
                problemSaveData.Problem = curProblem;
                problemSaveData.ZYH = mAppApplication.sickPersonVo.ZYH;
                problemSaveData.YHID = mAppApplication.user.YHID;
                problemSaveData.BQID = mAppApplication.getAreaId();
                problemSaveData.JGID = mAppApplication.jgId;
                problemSaveData.GLLX = gllx;
                data = JsonUtil.toJson(problemSaveData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return NursePlanApi.getInstance(NursePlanActivity.this)
                    .saveNursePlanProblem(data);
        }

        @Override
        protected void onPostExecute(Response<List<Problem>> result) {
            hideLoadingDialog();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(NursePlanActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            saveProblem();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    showMsgAndVoice(R.string.project_save_success);
//                    if (isAdd)
//                        addedNew = true;
//                    if (result.Data != null) {
//                        handleResult(result.Data);
//                    }
                    isAdd = false;
                    toRefreshData();
                } else {
                    showMsgAndVoice(result.Msg);
                    /*MediaUtil.getInstance(NursePlanActivity.this).playSound(
                            R.raw.wrong, NursePlanActivity.this);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }

    }

    /**
     * @author 吕自聪 lvzc@bsoft.com.cn
     * @Description: 删除问题
     * @ClassName: DelTask
     * @date 2016-5-20 下午2:09:11
     */
    class DelTask extends AsyncTask<Void, Void, Response<String>> {

        @Override
        protected Response<String> doInBackground(Void... params) {
            return NursePlanApi.getInstance(NursePlanActivity.this)
                    .deleteNursePlanProblem(curProblem.JLWT, mAppApplication.jgId);
        }

        @Override
        protected void onPostExecute(Response<String> result) {
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(NursePlanActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            delProblem();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    showMsgAndVoice(result.Data);
                    addedNew = true;
                    isAdd = false;
                    toRefreshData();
                } else {
                    showMsgAndVoice(result.Msg);
                  /*  MediaUtil.getInstance(NursePlanActivity.this).playSound(
                            R.raw.wrong, NursePlanActivity.this);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    /**
     * @author 吕自聪 lvzc@bsoft.com.cn
     * @Description: 结束问题
     * @ClassName: TerminateTask
     * @date 2016-5-20 下午2:08:34
     */
    class TerminateTask extends AsyncTask<Void, Void, Response<List<Problem>>> {
        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<Problem>> doInBackground(Void... params) {
            return NursePlanApi.getInstance(NursePlanActivity.this)
                    .terminateNursePlanProblem(curProblem.JLWT, mAppApplication.user.YHID,
                            mAppApplication.sickPersonVo.ZYH, curProblem.GLXH,
                            curProblem.WTXH, mAppApplication.jgId);
        }

        @Override
        protected void onPostExecute(Response<List<Problem>> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(NursePlanActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            TerminateTask task = new TerminateTask();
                            tasks.add(task);
                            task.execute();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    showMsgAndVoice("操作成功");
                    if (isAdd)
                        addedNew = true;
                    if (result.Data != null) {
                        handleResult(result.Data);
                    }
                } else {
                    showMsgAndVoice(result.Msg);
               /*     MediaUtil.getInstance(NursePlanActivity.this).playSound(
                            R.raw.wrong, NursePlanActivity.this);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    /**
     * @author 吕自聪 lvzc@bsoft.com.cn
     * @Description: 时间下拉适配器
     * @date 2015-12-7 上午10:04:44
     */
    class TimeAdapter extends BaseAdapter implements SpinnerAdapter {

        @Override
        public int getCount() {
            return mProblems.size();
        }

        @Override
        public Problem getItem(int position) {
            return mProblems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            DropDownViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new DropDownViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.simple_spinner_dropdown_item, parent, false);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.text1);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (DropDownViewHolder) convertView.getTag();
            }
            mProblems.get(position).KSSJ = mProblems.get(position).KSSJ
                    .replace("T", " ");
            viewHolder.textView.setText(mProblems.get(position).KSSJ);
            return convertView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SelectedViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new SelectedViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.simple_spinner_item, parent, false);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.text1);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (SelectedViewHolder) convertView.getTag();
            }
            mProblems.get(position).KSSJ = mProblems.get(position).KSSJ
                    .replace("T", " ");
            viewHolder.textView.setText(mProblems.get(position).KSSJ);
            return convertView;
        }

    }

    class DropDownViewHolder {
        TextView textView;
    }

    class SelectedViewHolder {
        TextView textView;
    }


}
