package com.bsoft.mob.ienr.activity.user;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
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
import com.bsoft.mob.ienr.activity.user.adapter.GistRVAdapter;
import com.bsoft.mob.ienr.activity.user.adapter.MeasureRVAdapter;
import com.bsoft.mob.ienr.api.NursePlanApi;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.helper.HtmlCompatHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.nurseplan.CSMS_DataWrapper;
import com.bsoft.mob.ienr.model.nurseplan.DiagnosticBasis;
import com.bsoft.mob.ienr.model.nurseplan.FXPG_InfoDataWrapper;
import com.bsoft.mob.ienr.model.nurseplan.Measure;
import com.bsoft.mob.ienr.model.nurseplan.Problem;
import com.bsoft.mob.ienr.model.nurseplan.ProblemSaveData;
import com.bsoft.mob.ienr.model.nurseplan.ZDMS_DataWrapper;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FormSyncUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.ObjectUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;
import com.bsoft.mob.ienr.view.floatmenu.menu.IFloatMenuItem;
import com.bsoft.mob.ienr.view.floatmenu.menu.TextFloatMenuItem;
import com.fondesa.recyclerviewdivider.RecyclerViewDivider;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 护理焦点
 *
 * @author Administrator
 */

public class NurseFocusActivity extends BaseBarcodeActivity {
    public static final int REQUEST_CODE = 1;

    private String wtxh;
    private String gllx;
    private String glxh;
    private boolean isAdd;
    private List<Problem> mProblems;
    //private ScrollView scroller;
    private RecyclerView measureRecyclerView;// 措施，目标列表视图,依据
    private RecyclerView gistRecyclerView;// 措施，目标列表视图,依据
    private EditText problem;// 问题
    private TextView evaStatus;// 评价状态
    private Spinner startTime;// 开始时间
    private ImageView id_iv_for_bar_spinner;// 设置时间
    private ImageView gistAdd, measureAddGroup;// 诊断依据自定义，按组添加措施
    private Problem curProblem;// 当前选中的护理计划问题；
    private int selectedIndex;// 当前选择的位置
    private boolean addedNew;// 是否添加了新的记录
    private int tempCszh = 0;// 模板用临时组号
    private MeasureRVAdapter measureRVAdapter;
    private GistRVAdapter gistRVAdapter;
    private static final String STATUE_NEED_SAVE = "<font color='red'>待保存</font>";

    @Override
    public void initBarBroadcast() {

    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    private void getParamsAfter() {
        //setContentView(R.layout.activity_nurse_focus);
        initView();
        initActionBar();

    }

    private List<FXPG_InfoDataWrapper.FXPG_InfoData> fxpg_infoDataList;
    private String need_replace;
    private String JLGLLX;
    private String GLJL;

    private void getParamsPre() {
        FXPG_InfoDataWrapper fXPG_InfoDataWrapper = (FXPG_InfoDataWrapper) getIntent().getSerializableExtra("fXPG_InfoDataWrapper");
        need_replace = getIntent().getStringExtra("need_replace");
        if (fXPG_InfoDataWrapper != null && fXPG_InfoDataWrapper.getFXPG_InfoDataList() != null && fXPG_InfoDataWrapper.getFXPG_InfoDataList().size() > 0) {
            fxpg_infoDataList = fXPG_InfoDataWrapper.getFXPG_InfoDataList();
            if (fxpg_infoDataList.size() > 1) {
                Map<String, String> selectMap = new HashMap<>();
                for (FXPG_InfoDataWrapper.FXPG_InfoData infoData :
                        fxpg_infoDataList) {
                    selectMap.put(infoData.WTXH, infoData.WTMS);
                }
                showListDialog("选择", 2, selectMap);
            } else {
                //单条
                getParamsHasData(fxpg_infoDataList.get(0).WTXH, fxpg_infoDataList.get(0).GLLX, fxpg_infoDataList.get(0).GLXH);
                getParamsAfter();
            }
        } else {
            getParams();
            getParamsAfter();
        }
    }

    @Override
    public void onListSelected(String key, String value) {
        super.onListSelected(key, value);
        FXPG_InfoDataWrapper.FXPG_InfoData infoData = null;
        for (int i = 0; i < fxpg_infoDataList.size(); i++) {
            infoData = fxpg_infoDataList.get(i);
            if (infoData.WTXH.equals(key)) {
                break;
            }
        }
        if (infoData != null) {
            getParamsHasData(infoData.WTXH, infoData.GLLX, infoData.GLXH);
            getParamsAfter();
        }
    }

    private ZDMS_DataWrapper zdms_dataWrapper;
    private CSMS_DataWrapper csms_dataWrapper;

    // 获取NursePlanFragment传递的参数
    private void getParams() {
        //
        zdms_dataWrapper = (ZDMS_DataWrapper) getIntent().getSerializableExtra("ZDMS_DataWrapper");
        csms_dataWrapper = (CSMS_DataWrapper) getIntent().getSerializableExtra("CSMS_DataWrapper");
        //add
        GLJL = getIntent().getStringExtra("GLJL");
        JLGLLX = getIntent().getStringExtra("JLGLLX");
        //add
        wtxh = getIntent().getStringExtra("WTXH");
        gllx = getIntent().getStringExtra("GLLX");
        glxh = getIntent().getStringExtra("GLXH");
        if ("1".equals(gllx)) {
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

    private void getParamsHasData(String WTXH, String GLLX, String GLXH) {
        //
        zdms_dataWrapper = (ZDMS_DataWrapper) getIntent().getSerializableExtra("ZDMS_DataWrapper");
        csms_dataWrapper = (CSMS_DataWrapper) getIntent().getSerializableExtra("CSMS_DataWrapper");
        //add
        GLJL = getIntent().getStringExtra("GLJL");
        JLGLLX = getIntent().getStringExtra("JLGLLX");
        //add
        wtxh = WTXH;
        gllx = GLLX;
        glxh = GLXH;
        if ("1".equals(gllx)) {
            glxh = "0";
        }
        isAdd = getIntent().getBooleanExtra("ISADD", false);

        toRefreshData();
    }

    /**
     * @param @param view
     * @return void
     * @throws
     * @Title: initView
     * @Description: 初始化界面
     */
    private void initView() {

        // scroller = (NestedScrollView) findViewById(R.id.id_sv);
        gistRecyclerView = (RecyclerView) findViewById(R.id.id_rv);
        gistRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        gistRecyclerView.setHasFixedSize(true);
        gistRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.setBackgroundResource(R.drawable.shape_classic_bg_shadow);
        //hideLastDivider
        RecyclerViewDivider.with(mContext).color(Color.parseColor("#21000000")).hideLastDivider()
                .build().addTo(gistRecyclerView);
        //
        List<DiagnosticBasis> diagnosticBases = new ArrayList<>();
        gistRVAdapter = new GistRVAdapter(diagnosticBases, R.layout.item_list_nurseplan_measure, need_replace,
                zdms_dataWrapper);
        gistRecyclerView.setAdapter(gistRVAdapter);

        measureRecyclerView = (RecyclerView) findViewById(R.id.id_rv_2);
        measureRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        measureRecyclerView.setHasFixedSize(true);
        measureRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.setBackgroundResource(R.drawable.shape_classic_bg_shadow);
        //hideLastDivider
        RecyclerViewDivider.with(mContext).color(Color.parseColor("#21000000")).hideLastDivider()
                .build().addTo(measureRecyclerView);

        List<Measure> te = new ArrayList<>();
        measureRVAdapter = new MeasureRVAdapter(te, R.layout.item_list_nurseplan_measure);
        measureRVAdapter.setOnEditClickListener(new MeasureRVAdapter.OnEditClickListener() {
            @Override
            public void onEditClick(View view, int pos) {
                String cszh = curProblem.JHCS.get(pos).CSZH;
                goWithEditClick(cszh);
            }
        });
        measureRecyclerView.setAdapter(measureRVAdapter);

        //
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
                if (EmptyTool.isBlank(s.toString()))
                    return;
                if (curProblem != null)
                    curProblem.WTMS = s.toString();
            }
        });
        evaStatus = (TextView) findViewById(R.id.nurseform_evaluatestaus);
        if (isAdd) {
            evaStatus.setText(HtmlCompatHelper.fromHtml(STATUE_NEED_SAVE));
        }
        SpinnerLayout spinnerLayout = (SpinnerLayout) findViewById(R.id.id_spinner_layout);
        startTime = spinnerLayout.getSpinner();
        id_iv_for_bar_spinner = (ImageView) findViewById(R.id.id_iv_for_bar_spinner);
        TextView id_tv_bar_spinner = (TextView) findViewById(R.id.id_tv_for_bar_spinner);
        id_tv_bar_spinner.setText("开始时间");
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
                    //
                    gistRVAdapter.refreshDataList(curProblem.ZDYJ);
                }
                if (curProblem.JHCS != null) {
                    List<Measure> JHCSTemplateNew = new ArrayList<>();
                    for (int i = 0; i < curProblem.JHCSTemplate.size(); i++) {
                        Measure measure = null;
                        try {
                            measure = ObjectUtil.deepCopy(curProblem.JHCSTemplate.get(i));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        JHCSTemplateNew.add(measure);
                    }
                    //add 赋值
                    if (csms_dataWrapper != null && csms_dataWrapper.getCsms_beanList() != null) {
                        for (int i = 0; i < csms_dataWrapper.getCsms_beanList().size(); i++) {
                            String csxh = csms_dataWrapper.getCsms_beanList().get(i).CSXH;
                            String yhxm = mAppApplication.user.YHXM;
                            for (int j = 0; j < JHCSTemplateNew.size(); j++) {
                                Measure measureTemp = JHCSTemplateNew.get(j);
                                if (measureTemp.CSXH.equals(csxh)) {
                                    measureTemp.KSXM = yhxm;
                                    measureTemp.KSSJ = csms_dataWrapper.getCsms_beanList().get(i).KSSJ;
                                    measureTemp.KSGH = csms_dataWrapper.getCsms_beanList().get(i).KSGH;
                                    measureTemp.JLCS = csms_dataWrapper.getCsms_beanList().get(i).JLCS;
                                    measureTemp.JSGH = csms_dataWrapper.getCsms_beanList().get(i).JSGH;
                                    measureTemp.JSSJ = csms_dataWrapper.getCsms_beanList().get(i).JSSJ;
                                    if (!EmptyTool.isBlank(measureTemp.JSGH)) {
                                        measureTemp.JSXM = yhxm;
                                    }
                                    measureTemp.XJBZ = csms_dataWrapper.getCsms_beanList().get(i).XJBZ;
                                    measureTemp.ZDYBZ = csms_dataWrapper.getCsms_beanList().get(i).ZDYBZ;
                                    measureTemp.CSZH = csms_dataWrapper.getCsms_beanList().get(i).CSZH;
                                    measureTemp.CSMS = csms_dataWrapper.getCsms_beanList().get(i).CSMS;
                                    measureTemp.SELECTED = true;
                                    curProblem.JHCS.add(measureTemp);
                                    break;
                                }
                            }
                        }
                    }
                    //add
                    measureRVAdapter.refreshDataList(curProblem.JHCS);
                }

                if (curProblem != null) {
                    if (curProblem.IsSync) {
                        new FormSyncUtil().InvokeSync(NurseFocusActivity.this,
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
                    YmdHMs ymdHMs = DateTimeHelper.dateTime2YmdHMs(curProblem.KSSJ);
                    showPickerDateTimeCompat(ymdHMs, v.getId());
                } else {
                    showMsgAndVoiceAndVibrator("请先添加问题");
                }
            }
        });
        gistAdd = (ImageView) findViewById(R.id.nurseplan_gist_add);
        gistAdd.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showInputDiaolog("请输入诊断依据", 1);
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
                Intent intent = new Intent(NurseFocusActivity.this,
                        NursePlanMeasureActivity.class);
                intent.putExtra("measureList", (Serializable) curProblem.JHCSTemplate);
                startActivityForResult(intent,
                        NursePlanMeasureActivity.REQUEST_CODE);
            }
        });
    }

    @Override
    public void onDateTimeSet(int year, int month, int dayOfMonth,
                              int hourOfDay, int minute, int viewId) {
        String dateTime = DateTimeFactory.getInstance().ymdhms2DateTime(year, month, dayOfMonth, hourOfDay, minute, 0);

        if (curProblem != null) {
            curProblem.KSSJ = dateTime;
            ((TimeAdapter) startTime.getAdapter()).notifyDataSetChanged();
        }
    }

    @Override
    public void onInputCompleteed(String content, int viewId) {
        super.onInputCompleteed(content, viewId);
        if (curProblem == null)
            return;

        switch (viewId) {

            case 1:
                if (curProblem.ZDYJ == null) {
                    curProblem.ZDYJ = new ArrayList<DiagnosticBasis>();
                }
                if (!EmptyTool.isBlank(content)) {
                    DiagnosticBasis gist = new DiagnosticBasis();
                    gist.ZDMS = content;
                    gist.ZDYBZ = "1";
                    gist.ZDXH = "0";
                    gist.SELECTED = true;
                    curProblem.ZDYJ.add(gist);
                    gistRVAdapter.refreshDataList(curProblem.ZDYJ);
                    gistRecyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // gistRecyclerView.smoothScrollToPosition(curProblem.ZDYJ.size()
                            // - 1);
                          /*##  scroller.smoothScrollTo(gistRecyclerView.getLeft(),
                                    gistRecyclerView.getBottom() - 150);*/
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
        actionBar.setTitle("护理焦点");
        String brch = EmptyTool.isBlank(mAppApplication.sickPersonVo.BRCH) ? "" : mAppApplication.sickPersonVo.BRCH;
        actionBar.setPatient(brch + mAppApplication.sickPersonVo.BRXM);
        actionBar.setBackAction(new Action() {

            @Override
            public void performAction(View view) {
                backClick();
            }

            @Override
            public String getText() {
                return getString(R.string.menu_back);
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_arrow_back_black_24dp;
            }
        });
        actionBar.addAction(new Action() {

            @Override
            public void performAction(View view) {
                saveProblem();
            }

            @Override
            public String getText() {
                return "保存";
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }
        });
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_nurse_focus;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        getParamsPre();
       /* getParams();
        getParamsAfter();*/
    }

    private void backClick() {
        if (addedNew) {
            setResult(RESULT_OK);
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backClick();
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
       /* for (int itemDrawableResid : itemDrawables) {
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
            toRefreshData();
            if (evaStatus != null) {
                evaStatus.setText(HtmlCompatHelper.fromHtml(STATUE_NEED_SAVE));
                evaStatus.setTextColor(ContextCompat.getColor(mContext,
                        R.color.red));
            }
        } else if (drawableRes == R.drawable.menu_save) {
            saveProblem();
        } else if (drawableRes == R.drawable.menu_evaluate) {
            if (curProblem == null) {
                showMsgAndVoiceAndVibrator("当前没有要评价的点焦点问题");
                /*MediaUtil.getInstance(NurseFocusActivity.this).playSound(
                        R.raw.wrong, NurseFocusActivity.this);*/
                return;
            }
            if (EmptyTool.isBlank(curProblem.JLWT)) {
                showMsgAndVoiceAndVibrator("该焦点问题尚未保存，请先保存");
               /* MediaUtil.getInstance(NurseFocusActivity.this).playSound(
                        R.raw.wrong, NurseFocusActivity.this);*/
                return;
            }
            if (TextUtils.equals("2", curProblem.PJZT)) {
                showMsgAndVoiceAndVibrator("改焦点已经结束，不能再对其进行操作");
               /* MediaUtil.getInstance(NurseFocusActivity.this).playSound(
                        R.raw.wrong, NurseFocusActivity.this);*/
                return;
            }
            Intent intent = new Intent(NurseFocusActivity.this,
                    NursePlanEvaluateActivity.class);
            // add by louis 2017年5月24日15:00:31
            // JLGLLX 从焦点关联传过来的  "2"代表 护理计划
            if ("2".equals(JLGLLX) && "1".equals(curProblem.WTLX)) {
                //护理计划 的内容就变成 充当焦点的内容
                //也就是计划已经变成了焦点
                curProblem.WTLX = "2";//赋值2   wtlx 1 代表 PlanEvaluate  2 代表 FocusEvaluate
            }
            intent.putExtra("JLWT", curProblem.JLWT);
            intent.putExtra("WTXH", curProblem.WTXH);
            intent.putExtra("WTLX", curProblem.WTLX);
            startActivityForResult(intent,
                    NursePlanEvaluateActivity.REQUEST_CODE);
        } else if (drawableRes == R.drawable.menu_delete) {
            showConfirmDialog("确定要删除这条记录么？", "DELETE");
        } else if (drawableRes == R.drawable.menu_terminate) {
            showConfirmDialog("结束之后焦点将不能被编辑和删除，确定要结束这条焦点么？", "TREMINATE");
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
                measureRVAdapter.refreshDataList(curProblem.JHCS);
            }

        }
    }

    @Override
    public void onConfirmSet(String action) {
        if (TextUtils.equals(action, "DELETE")) {
            delProblem();
        } else if (TextUtils.equals(action, "TREMINATE")) {
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
            if (curProblem.PJZT.equals("2")) {
                showMsgAndVoiceAndVibrator("已结束，不能修改");
             /*   MediaUtil.getInstance(NurseFocusActivity.this).playSound(
                        R.raw.wrong, NurseFocusActivity.this);*/
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
            showMsgAndVoiceAndVibrator("改焦点已经结束，不能再对其进行操作");
            /*MediaUtil.getInstance(NurseFocusActivity.this).playSound(
                    R.raw.wrong, NurseFocusActivity.this);*/
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
            showMsgAndVoiceAndVibrator("改焦点已经结束，不能再对其进行操作");
            /*MediaUtil.getInstance(NurseFocusActivity.this).playSound(
                    R.raw.wrong, NurseFocusActivity.this);*/
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
            measureRVAdapter.refreshDataList(new ArrayList<Measure>());
            gistRVAdapter.refreshDataList(new ArrayList<DiagnosticBasis>());
           /* measureRecyclerView.setAdapter(null);
            gistRecyclerView.setAdapter(null);*/
        }
        if (selectedIndex < mProblems.size()) {
            startTime.setSelection(selectedIndex);
        }
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
            return NursePlanApi.getInstance(NurseFocusActivity.this)
                    .addPlanProblem(wtxh, mAppApplication.jgId);
        }

        @Override
        protected void onPostExecute(Response<Problem> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(NurseFocusActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            isAdd = true;
                            toRefreshData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    if (result.Data != null) {
                        Problem p = result.Data;
                        p.GLXH = glxh;
                        mProblems = new ArrayList<Problem>();
                        p.KSSJ = DateTimeHelper.getServer_yyyyMMddHHmm00();
                        mProblems.add(p);
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
            return NursePlanApi.getInstance(NurseFocusActivity.this)
                    .getFocusProblemList(mAppApplication.sickPersonVo.ZYH, glxh, wtxh, mAppApplication.jgId);
        }

        @Override
        protected void onPostExecute(Response<List<Problem>> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(NurseFocusActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
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
                //
                problemSaveData.JLGLLX = JLGLLX;
                problemSaveData.GLJL = GLJL;
                //
                data = JsonUtil.toJson(problemSaveData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return NursePlanApi.getInstance(NurseFocusActivity.this)
                    .saveNurseFocusProblem(data);
        }

        @Override
        protected void onPostExecute(Response<List<Problem>> result) {
            hideLoadingDialog();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(NurseFocusActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            saveProblem();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    showMsgAndVoice(R.string.project_save_success);
                    if (isAdd)
                        addedNew = true;
                    if (result.Data != null) {
                        handleResult(result.Data);
                    }
                } else {
                    showMsgAndVoice(result.Msg);
                    /*MediaUtil.getInstance(NurseFocusActivity.this).playSound(
                            R.raw.wrong, NurseFocusActivity.this);*/
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
     * @date 2016-5-20 下午2:32:55
     */
    class DelTask extends AsyncTask<Void, Void, Response<String>> {

        @Override
        protected Response<String> doInBackground(Void... params) {
            return NursePlanApi.getInstance(NurseFocusActivity.this)
                    .deleteNurseFocusProblem(curProblem.JLWT, mAppApplication.jgId);
        }

        @Override
        protected void onPostExecute(Response<String> result) {
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(NurseFocusActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
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
                    /*MediaUtil.getInstance(NurseFocusActivity.this).playSound(
                            R.raw.wrong, NurseFocusActivity.this);*/
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
            return NursePlanApi.getInstance(NurseFocusActivity.this)
                    .terminateNurseFocusProblem(curProblem.JLWT, mAppApplication.user.YHID,
                            mAppApplication.sickPersonVo.ZYH, curProblem.GLXH,
                            curProblem.WTXH, mAppApplication.jgId);
        }

        @Override
        protected void onPostExecute(Response<List<Problem>> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(NurseFocusActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
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
                    
                   
                    /*MediaUtil.getInstance(NurseFocusActivity.this).playSound(
                            R.raw.wrong, NurseFocusActivity.this);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    /**
     * @param cszh
     */
    private void goWithEditClick(String cszh) {
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
        Intent intent = new Intent(NurseFocusActivity.this,
                NursePlanMeasureActivity.class);
        intent.putExtra("measureList", (Serializable) templateList);
        startActivityForResult(intent,
                NursePlanMeasureActivity.REQUEST_CODE);
    }


    /*  class MeasureAdapter extends BaseAdapter {
          private List<Measure> list;
          private String previousCszh = "";
          private int cszhCount = 0;
          private int color0 = Color.WHITE;
          private int color1 = Color.LTGRAY;

          boolean isFirst = true;

          public MeasureAdapter(List<Measure> list) {
              super();
              this.list = list;
          }

          @Override
          public int getCount() {
              return list.size();
          }

          @Override
          public Measure getItem(int arg0) {
              return list.get(arg0);
          }

          @Override
          public long getItemId(int arg0) {
              return arg0;
          }

          @Override
          public View getView(final int position, View convertView, ViewGroup parent) {
              final ViewHolder vHolder;
              if (convertView == null) {
                  convertView = LayoutInflater.from(NurseFocusActivity.this)
                          .inflate(R.layout.item_list_nurseplan_measure, parent, false);
                  vHolder = new ViewHolder();
                  vHolder.tv_name = (EditText) convertView
                          .findViewById(R.id.nurseplan_item_name);
                  vHolder.spinnerLayout = (SpinnerLayout) convertView.findViewById(R.id.id_spinner_layout);
                  vHolder.sp_type = vHolder.spinnerLayout.getSpinner();
                  vHolder.cb_selected = (CheckBox) convertView
                          .findViewById(R.id.checkBox);
                  vHolder.tv_starttime = (TextView) convertView
                          .findViewById(R.id.nurseplan_item_starttime);
                  vHolder.tv_endtime = (TextView) convertView
                          .findViewById(R.id.nurseplan_item_endtime);
                  vHolder.tv_startperson = (TextView) convertView
                          .findViewById(R.id.nurseplan_item_startperson);
                  vHolder.tv_endperson = (TextView) convertView
                          .findViewById(R.id.nurseplan_item_endjperson);
                  vHolder.iv_edit = (ImageView) convertView
                          .findViewById(R.id.nurseplan_item_edit);
                  convertView.setTag(vHolder);
              } else {
                  vHolder = (ViewHolder) convertView.getTag();
              }
              isFirst = true;
              //赋值 措施描述
          *//*    if (csms_dataWrapper!=null&&csms_dataWrapper.getCsms_beanList()!=null){
                for (int i = 0; i <csms_dataWrapper.getCsms_beanList().size(); i++) {
                   String csxh= csms_dataWrapper.getCsms_beanList().get(i).CSXH;
                   String csms= csms_dataWrapper.getCsms_beanList().get(i).CSMS;
                   if (list.get(position).CSXH.equals(csxh)){
                       list.get(position).CSMS=csms;
                       list.get(position).SELECTED=true;
                       break;
                   }
                }
            }*//*
            vHolder.tv_name.setText(list.get(position).CSMS);
            isFirst = false;
            vHolder.tv_name.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!EmptyTool.isBlank(s.toString()) && !isFirst) {
                        list.get(position).CSMS = s.toString();
                        list.get(position).MODIFIED = true;
                    }
                }
            });
            vHolder.spinnerLayout.setVisibility(View.VISIBLE);
            vHolder.spinnerLayout.setEnabled(mDataList.get(position).ZDYBZ != 0);
            vHolder.sp_type.setEnabled(list.get(position).ZDYBZ != 0);
            vHolder.sp_type.setSelection(list.get(position).XJBZ);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                    convertView.getContext(), R.array.plan_measure,
                    android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            vHolder.sp_type.setAdapter(adapter);
            vHolder.sp_type
                    .setOnItemSelectedListener(new OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> arg0,
                                                   View arg1, int arg2, long arg3) {
                            list.get(position).XJBZ = arg2;
                            list.get(position).MODIFIED = true;
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> arg0) {

                        }
                    });
            vHolder.cb_selected.setChecked(list.get(position).SELECTED);
            vHolder.cb_selected.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (vHolder.cb_selected.isChecked()) {
                        list.get(position).SELECTED = true;
                        vHolder.cb_selected.setChecked(true);
                    } else {
                        list.get(position).SELECTED = false;
                        vHolder.cb_selected.setChecked(false);
                    }

                }
            });
            vHolder.tv_starttime.setText(list.get(position).KSSJ);
            vHolder.tv_endtime.setText(list.get(position).JSSJ);
            vHolder.tv_startperson.setText(list.get(position).KSXM);
            vHolder.tv_endperson.setText(list.get(position).JSXM);
            vHolder.iv_edit.setVisibility(View.VISIBLE);
            vHolder.iv_edit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String cszh = curProblem.JHCS.get(position).CSZH;
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
                    Intent intent = new Intent(NurseFocusActivity.this,
                            NursePlanMeasureActivity.class);
                    intent.putExtra("measureList", (Serializable) templateList);
                    startActivityForResult(intent,
                            NursePlanMeasureActivity.REQUEST_CODE);
                }
            });
            if (!previousCszh.equals(list.get(position).CSZH)) {
                previousCszh = list.get(position).CSZH;
                cszhCount++;
            }
            if (cszhCount % 2 == 0) {
                convertView.setBackgroundColor(color0);
            } else {
                convertView.setBackgroundColor(color1);
            }
            return convertView;
        }

    }
*/
   /* class GistAdapter extends BaseAdapter {
        List<DiagnosticBasis> list;

        public GistAdapter(List<DiagnosticBasis> list) {
            super();
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public DiagnosticBasis getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            final ViewHolder vHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(NurseFocusActivity.this)
                        .inflate(R.layout.item_list_nurseplan_measure, parent, false);
                vHolder = new ViewHolder();
                vHolder.tv_name = (EditText) convertView
                        .findViewById(R.id.nurseplan_item_name);
                vHolder.cb_selected = (CheckBox) convertView
                        .findViewById(R.id.checkBox);
                convertView.setTag(vHolder);
            } else {
                vHolder = (ViewHolder) convertView.getTag();
            }
            //赋值 诊断描述
            if (zdms_dataWrapper != null && zdms_dataWrapper.getZdms_beanList() != null) {
                for (int i = 0; i < zdms_dataWrapper.getZdms_beanList().size(); i++) {
                    String zdxh = zdms_dataWrapper.getZdms_beanList().get(i).ZDXH;
                    String zdms = zdms_dataWrapper.getZdms_beanList().get(i).ZDMS;
                    if (list.get(position).ZDXH.equals(zdxh)) {
                        list.get(position).ZDMS = zdms;
                        list.get(position).SELECTED = true;
                        break;
                    }
                }
            }

            list.get(position).ZDMS = list.get(position).ZDMS == null ? "" : list.get(position).ZDMS;
            if (need_replace != null) {
                //赋值  需要赋值的地方
                String replace_key = "(*)";
                if (list.get(position).ZDMS.contains(replace_key)) {
                    list.get(position).SELECTED = true;
                }
                list.get(position).ZDMS = list.get(position).ZDMS.replace(replace_key, need_replace);
            }
            vHolder.tv_name.setText(list.get(position).ZDMS);

            vHolder.cb_selected.setChecked(list.get(position).SELECTED);
            vHolder.cb_selected.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (vHolder.cb_selected.isChecked()) {
                        list.get(position).SELECTED = true;
                        vHolder.cb_selected.setChecked(true);
                    } else {
                        list.get(position).SELECTED = false;
                        vHolder.cb_selected.setChecked(false);
                    }
                }
            });
            vHolder.tv_name.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (!EmptyTool.isBlank(s.toString())) {
                        list.get(position).ZDMS = s.toString();
                        list.get(position).MODIFIED = true;
                    }
                }
            });
            return convertView;
        }

    }
*/

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

   /* class ViewHolder {
        EditText tv_name;
        SpinnerLayout spinnerLayout;
        Spinner sp_type;
        CheckBox cb_selected;
        TextView tv_starttime;
        TextView tv_endtime;
        TextView tv_startperson;
        TextView tv_endperson;
        ImageView iv_edit;
    }*/


}
