/**
 * @Title: RiskMeasureActivity.java
 * @Package com.bsoft.mob.ienr.activity.user
 * @Description: 风险措施页
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-12-14 上午9:06:35
 * @version V1.0
 */
package com.bsoft.mob.ienr.activity.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.adapter.RiskMeasureSpinnerAdapter;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.api.NurseFormApi;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.LayoutParamsHelper;
import com.bsoft.mob.ienr.helper.ListViewScrollHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.nursingeval.KeyValue;
import com.bsoft.mob.ienr.model.risk.Measure;
import com.bsoft.mob.ienr.model.risk.MeasureRecord;
import com.bsoft.mob.ienr.model.risk.MeasureRecordPostData;
import com.bsoft.mob.ienr.model.risk.RiskEvaluate;
import com.bsoft.mob.ienr.model.risk.RiskMeasure;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FormSyncUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;
import com.bsoft.mob.ienr.view.floatmenu.menu.IFloatMenuItem;
import com.bsoft.mob.ienr.view.floatmenu.menu.TextFloatMenuItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @ClassName: RiskMeasureActivity
 * @Description: 风险措施页
 * @date 2015-12-14 上午9:06:35
 */
public class RiskMeasureActivity extends BaseBarcodeActivity {

    // 眉栏工具条
    // 全局应用程序对象
    private MeasureAdapter mMeasureAdapter;
    // ##   private LinearLayout evaluateForm;
    // 卫星按钮
    //private RayMenu menu;
    private ListView mListView;
    // ##     private Spinner mSpinner;
    private TextView tv_mTime;// 措施时间
    // ## private TextView tv_eTime;// 评价时间
    private ImageView iv_mSet;// 措施时间设置按钮
    // ##   private ImageView iv_eSet;// 评价时间设置按钮
    private TextView tv_state;// 评价状态
    private TextView tv_eNurse;// 评价护士
    private TextView tv_mNurse;// 执行护士
    private String pgdh;
    private String pglx;
    private String pgxh;
    private String jlxh;
    //
    private TextView id_tv_for_bar_check;
    private TextView healthguid_datetime_txt;
    private CheckBox healthguid_cbpre;

    private MeasureRecord mRecord;
    private MeasureRecord mPreOneRecord;
    private List<RiskEvaluate> mEvaluate;

    private boolean customIsSync;

    @Override
    public void initBarBroadcast() {
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    /*
     * (非 Javadoc) <p>Title: onInputCompleteed</p> <p>Description: </p>
     *
     * @param content
     *
     * @param viewId
     *
     * @see
     * com.bsoft.mob.ienr.activity.base.BaseActivity#onInputCompleteed(java.lang.
     * String, int)
     */
    @Override
    public void onInputCompleteed(String content, int viewId) {
        if (mRecord.CSXM == null) {
            mRecord.CSXM = new ArrayList<>();
        }
        if (!EmptyTool.isBlank(content)) {
            boolean hasCustom = false;
            for (int i = mRecord.CSXM.size() - 1; i >= 0; i--) {
                if ("自定义措施".equals(mRecord.CSXM.get(i).ZMC)) {
                    hasCustom = true;
                    break;
                }
            }
            if (!hasCustom) {
                Measure m = new Measure();
                m.ZMC = "自定义措施";
                mRecord.CSXM.add(m);
            }
            Measure measure = new Measure();
            measure.ZDYBZ = "1";
            measure.XMNR = content;
            measure.ZMC = "自定义措施";
            measure.CSXH = "0";
            mRecord.CSXM.add(measure);
            //add
            dealAllBtnStatusAndSet();
            //add
            mMeasureAdapter.notifyDataSetChanged();
            ListViewScrollHelper.smoothScrollToBottom(mListView);
          /*  mListView.postDelayed(new Runnable() {

                @Override
                public void run() {
                    mListView.smoothScrollToPosition(mRecord.CSXM.size() - 1);
                }
            }, 500);*/
        }
    }

    /*
     * (非 Javadoc) <p>Title: onDateTimeSet</p> <p>Description: </p>
     *
     * @param year
     *
     * @param monthOfYear
     *
     * @param dayOfMonth
     *
     * @param hourOfDay
     *
     * @param minute
     *
     * @param viewId
     *
     * @see com.bsoft.mob.ienr.activity.base.BaseActivity#onDateTimeSet(int, int,
     * int, int, int, int)
     */
    @Override
    public void onDateTimeSet(int year, int month, int dayOfMonth,
                              int hourOfDay, int minute, int viewId) {
        String datetime = DateTimeFactory.getInstance()
                .ymdhms2DateTime(year, month, dayOfMonth,
                        hourOfDay, minute, 0);
        //
        switch (viewId) {
            case R.id.id_tv_2_for_bar_image:
                tv_mTime.setText(datetime);
                break;
          /*  case R.id.id_tv_2_for_bar_image_copy:
                tv_eTime.setText(time);
                break;*/
            case 666666:
                timePJView.setText(datetime);
                break;
            default:
        }
    }

    private void getParams() {
        pgdh = getIntent().getStringExtra("PGDH");
        pglx = getIntent().getStringExtra("PGLX");
        pgxh = getIntent().getStringExtra("PGXH");
        jlxh = getIntent().getStringExtra("JLXH");
        if (pgdh == null) {
            pgdh = "0";
        }
        if (pglx == null) {
            pglx = "0";
        }
        if (pgxh == null) {
            pgxh = "0";
        }
        if (jlxh == null) {
            jlxh = "0";
        }
        Log.d("tes", "getParams: pgdh" + pgdh);
        Log.d("tes", "getParams: pglx" + pglx);
        Log.d("tes", "getParams: pgxh" + pgxh);
        Log.d("tes", "getParams: jlxh" + jlxh);
        getMeasureList();
    }

    @Override
    protected void toRefreshData() {
        getParams();
    }


    private void initActionBar() {
        actionBar.setTitle("风险评估措施");
        actionBar.setPatient(mAppApplication.sickPersonVo.XSCH + mAppApplication.sickPersonVo.BRXM);
        actionBar.addAction(new Action() {
            @Override
            public String getText() {
                return "历史";
            }

            @Override
            public void performAction(View view) {
                Intent intent = new Intent(RiskMeasureActivity.this,
                        RiskMeasureListActivity.class);
                intent.putExtra("PGDH", pgdh);
                intent.putExtra("PGXH", pgxh);
                startActivity(intent);
            }

            @Override
            public int getDrawable() {
                return R.drawable.menu_history_n;
            }
        });
        actionBar.addAction(new Action() {
            @Override
            public String getText() {
                return "保存";
            }

            @Override
            public void performAction(View view) {
                saveRiskMeasure();
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }
        });
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_risk_measure;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {


        LinearLayout evaluateForm = (LinearLayout) findViewById(R.id.evaluate_form);
        evaluateForm.setVisibility(View.GONE);
        mListView = (ListView) findViewById(R.id.id_lv);

        //解决下拉刷新冲突
        EmptyViewHelper.setEmptyView(mListView, "mListView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mListView);

        //menu = (RayMenu) findViewById(R.id.ray_menu);
        id_tv_for_bar_check = findViewById(R.id.id_tv_for_bar_check);
        healthguid_datetime_txt = findViewById(R.id.healthguid_datetime_txt);
        //
        healthguid_cbpre = findViewById(R.id.healthguid_cbpre);
        healthguid_cbpre.setText("全选");
        healthguid_cbpre.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView == null || !buttonView.isPressed()) {
                    //不响应非点击引起的改变
                    return;
                }
                checkAll(isChecked);
            }
        });
       /*//##   SpinnerLayout spinnerLayout = (SpinnerLayout) findViewById(R.id.id_spinner_layout);
        TextView tv_evl = (TextView) findViewById(R.id.id_tv_for_bar_spinner);
        tv_evl.setText("评价：");
        ImageView iv_btn = (ImageView) findViewById(R.id.id_iv_for_bar_spinner);
        iv_btn.setVisibility(View.GONE);
        mSpinner = spinnerLayout.getSpinner();

        tv_eTime = (TextView) findViewById(R.id.id_tv_2_for_bar_image_copy);
        iv_eSet = (ImageView) findViewById(R.id.id_iv_for_bar_image_copy);*/
        tv_mTime = (TextView) findViewById(R.id.id_tv_2_for_bar_image);
        iv_mSet = (ImageView) findViewById(R.id.id_iv_for_bar_image);

        CheckBox id_cb_sp = (CheckBox)findViewById(R.id.id_cb_sp);
        id_cb_sp.setText("同步");
        id_cb_sp.setChecked(customIsSync);
        id_cb_sp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView == null || !buttonView.isPressed()) {
                    //不响应非点击引起的改变
                    return;
                }
                customIsSync = isChecked;
            }
        });
        tv_mNurse = (TextView) findViewById(R.id.riskmeasure_nurse);
        tv_state = (TextView) findViewById(R.id.riskevalute_state);
        tv_eNurse = (TextView) findViewById(R.id.riskevaluate_nurse);
        tv_mNurse.setText(mAppApplication.user.YHXM);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                itemClick(position);
            }
        });
        iv_mSet.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String dateTime = tv_mTime.getText().toString();
                if (EmptyTool.isBlank(dateTime)) {
                    dateTime = DateTimeHelper.getServer_yyyyMMddHHmm00();
                }
                int viewId = R.id.id_tv_2_for_bar_image;
                YmdHMs ymdHMs = DateTimeHelper.dateTime2YmdHMs(dateTime);
                showPickerDateTimeCompat(ymdHMs, viewId);
            }
        });

        /*##  iv_eSet.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String time = tv_eTime.getText().toString();
                if (EmptyTool.isBlank(time)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    time = sdf.format(mAppApplication.getServiceFixedTime());
                }
                Calendar calendar = DateUtil.getFormat7Instance(time);
                int y = calendar.get(Calendar.YEAR);
                int m = calendar.get(Calendar.MONTH);
                int d = calendar.get(Calendar.DAY_OF_MONTH);
                int h = calendar.get(Calendar.HOUR_OF_DAY);
                int mi = calendar.get(Calendar.MINUTE);
                showPickerDateTimeCompat(y, m, d, h, mi, R.id.id_tv_2_for_bar_image_copy);
            }
        });
        mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                if (arg2 == 0) {
                    tv_eNurse.setText("");
                } else {
                    tv_eNurse.setText(mAppApplication.user.YHXM);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });*/
        initActionBar();
        toRefreshData();
    }

    private void dealAllBtnStatusAndSet() {
        //根据 【Item】 状态 设置 【分组】状态
        for (int i = 0; i < mRecord.CSXM.size(); i++) {
            if (EmptyTool.isBlank(mRecord.CSXM.get(i).CSXH)) {
                //组checkbox
                String nowZMC = mRecord.CSXM.get(i).ZMC;
                if (nowZMC == null) {
                    continue;
                }
                boolean isAllChecked = true;
                for (int j = 0; j < mRecord.CSXM.size(); j++) {
                    if (!EmptyTool.isBlank(mRecord.CSXM.get(j).CSXH)) {
                        //item checkbox
                        if (nowZMC.equals(mRecord.CSXM.get(j).ZMC)) {
                            //有一个没选就
                            if (!mRecord.CSXM.get(j).SELECT) {
                                isAllChecked = false;
                                break;
                            }
                        }
                    }
                }
                //设置该组的状态
                mRecord.CSXM.get(i).SELECT = isAllChecked;
            }
        }
        //### mMeasureAdapter.notifyDataSetChanged();

        //根据 【分组】状态  设置 【全选】 状态
        boolean isGroupAllChecked = true;
        for (int i = 0; i < mRecord.CSXM.size(); i++) {
            if (EmptyTool.isBlank(mRecord.CSXM.get(i).CSXH)) {
                //组checkbox
                if (!"转归".equals(mRecord.CSXM.get(i).ZMC)) {
                    if (!mRecord.CSXM.get(i).SELECT) {
                        isGroupAllChecked = false;
                    }
                }
            }
        }
        if (healthguid_cbpre != null) {
            //条件是不响应非点击的改变的时事件  所以可以这么设置
            healthguid_cbpre.setChecked(isGroupAllChecked);
        }
    }

    private void checkAll(boolean isChecked) {
        for (int i = 0; i < mRecord.CSXM.size(); i++) {
            if (!EmptyTool.isBlank(mRecord.CSXM.get(i).CSXH)) {
                //不是组checkbox
                if (!"转归".equals(mRecord.CSXM.get(i).ZMC)) {
                    mRecord.CSXM.get(i).SELECT = isChecked;
                }
            }
        }
        dealAllBtnStatusAndSet();
        mMeasureAdapter.notifyDataSetChanged();
    }

    @Override
    protected List<IFloatMenuItem> configFloatMenuItems() {
        final int[] itemDrawables = {R.drawable.menu_delete,
                R.drawable.menu_add_item, R.drawable.menu_add,
                R.drawable.menu_check, R.drawable.menu_save};
        final int[][] itemStringDrawables = {
                {R.drawable.menu_delete, R.string.comm_menu_delete},
                {R.drawable.menu_add_item, R.string.comm_menu_add_item_zdcs},
                {R.drawable.menu_add, R.string.comm_menu_add},
                {R.drawable.menu_check, R.string.comm_menu_check},
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

        if (drawableRes == R.drawable.menu_save) {
            saveRiskMeasure();
        } else if (drawableRes == R.drawable.menu_delete) {
            deleteRecord();
        } else if (drawableRes == R.drawable.menu_check) {
            evaluteMeasure();
        } else if (drawableRes == R.drawable.menu_add_item) {
            if (mRecord == null) {
                showMsgAndVoiceAndVibrator("请先添加措施记录");
                return;
            }
            showInputDiaolog("请输入自定义措施", 0);
        } else if (drawableRes == R.drawable.menu_add) {
            addMeasure();
        }
    }


    private void getMeasureList() {
        GetMeasureListTask task = new GetMeasureListTask();
        tasks.add(task);
        task.execute();
    }

    private void getPreOneMeasure(String preJLXH, String prePGXH) {
        GetPreOneMeasureListTask task = new GetPreOneMeasureListTask(preJLXH);
        tasks.add(task);
        task.execute(prePGXH);
    }

    private void addMeasure() {
        jlxh = "needAdd";//写死，服务端处理强制新增
        getMeasureList();
    }

    private void saveRiskMeasure() {
        if (!EmptyTool.isBlank(mRecord.HSZQM)) {
            showMsgAndVoiceAndVibrator("已经评价，不能修改");
            return;
        }
        /**
         * 有选中项目就执行保存操作
         */
        List<Measure> csxm = mRecord.CSXM;
        for (Measure m : csxm) {
            if (m.SELECT) {
                saveCS();
                return;
            }
        }
        /**
         * 再次修改后，没有选择项目，就直接删除该记录
         */
        if (!"0".equals(mRecord.JLXH) && mRecord.JLXH != null) {
            deleteRecord();
            return;

        }
        showMsgAndVoiceAndVibrator("没有选中项目,无法进行保存");
    }

    private void deleteRecord() {
        if (mRecord == null) {
            return;
        }
        if (EmptyTool.isBlank(mRecord.JLXH)) {
            showMsgAndVoiceAndVibrator("尚未保存，无需删除");
            return;
        }
        DeleteTask task = new DeleteTask();
        tasks.add(task);
        task.execute();
    }

    private void evaluteMeasure() {

        if (EmptyTool.isBlank(pgdh) || "0".equals(pgxh)) {
            showTipDialog("pgdh 或者 pgxh为 0,请联系管理员");
            return;
        }

        if (EmptyTool.isBlank(mRecord.JLXH)) {
            showMsgAndVoiceAndVibrator("请先保存措施记录");
            return;
        }
       /*## if (mSpinner.getSelectedItemPosition() == 0) {
            showMsgAndVoice("请先选择评价");
            return;
        }*/

      /*##  EvaluateTask task = new EvaluateTask();
        tasks.add(task);
        task.execute();*/
    }

    private void handleResult(Response<RiskMeasure> result) {
        mRecord = result.Data.RECORD;
        mEvaluate = result.Data.EVALUATE;
        EvaluateSpinnerAdapter evaluateAdapter = new EvaluateSpinnerAdapter();
        jlxh = mRecord.JLXH;
        Log.d("tes", "handleResult: jlxh" + jlxh);
        if (mRecord == null || mEvaluate == null) {
            return;
        }
     /*##   if (!EmptyTool.isBlank(mRecord.SFPJ) && mRecord.SFPJ.equals("1")) {
            evaluateForm.setVisibility(View.VISIBLE);
        } else {
            evaluateForm.setVisibility(View.GONE);
        }*/
        if (mRecord.CSXM != null) {
            //新增的时候
            if (TextUtils.isEmpty(mRecord.JLXH) || "0".equals(mRecord.JLXH)) {
                //item项目 未选中的,有必填标志的->需要默认选中
                for (int i = 0; i < mRecord.CSXM.size(); i++) {
                    if (!mRecord.CSXM.get(i).SELECT && mRecord.CSXM.get(i).BTBZ) {
                        mRecord.CSXM.get(i).SELECT = true;
                    }
                    //准备数据问题
                    if (mRecord.CSXM.get(i).ZMC == null) {
                        mRecord.CSXM.get(i).ZMC = "";
                    }
                }
                //
            }
            mMeasureAdapter = new MeasureAdapter();
            mListView.setAdapter(mMeasureAdapter);
            //adddd
            dealAllBtnStatusAndSet();
            mMeasureAdapter.notifyDataSetChanged();
            //adddd
        }
       /*## if (mEvaluate != null) {
            mSpinner.setAdapter(evaluateAdapter);
        }*/
        if (EmptyTool.isBlank(mRecord.HSZQM)) {
            tv_state.setText("未审");
            tv_state.setTextColor(ContextCompat.getColor(mContext, R.color.green));
            tv_eNurse.setText("");
// ##           mSpinner.setSelection(0);
        } else {
         /*###   if (TextUtils.isDigitsOnly(mRecord.CSPJ)) {
                //mSpinner.setSelection(Integer.parseInt(mRecord.CSPJ));
                //根据值, 设置spinner默认选中:
                int k = evaluateAdapter.getCount();
                for (int i = 0; i < k; i++) {
                    if (mRecord.CSPJ.equals(evaluateAdapter.getItem(i).PJXH)) {
                        mSpinner.setSelection(i, true);// 默认选中项
                        break;
                    }
                }
            }*/
            tv_state.setText("已审");
            tv_state.setTextColor(ContextCompat.getColor(mContext, R.color.red));
            if (!EmptyTool.isBlank(mRecord.HSZXM)) {
                tv_eNurse.setText(mRecord.HSZXM);
            }
        }
//        tv_mTime.setText(DateUtil.get8To7Sstr(mRecord.CSSJ) + ":00");
        String yyyyMMddHHmm00 = DateTimeHelper.getServer_yyyyMMddHHmm00(mRecord.CSSJ);
        tv_mTime.setText(yyyyMMddHHmm00);

        String pjsj = mRecord.HSZQMSJ;
        if (EmptyTool.isBlank(pjsj)) {
            pjsj = DateTimeHelper.getServerDateTime();
        }
//        String pjsj = DateUtil.get8To7Sstr(mRecord.HSZQMSJ) + ":00";
//
//        if (pjsj.equals("0001-01-01 00:00:00")) {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            pjsj = sdf.format(mAppApplication.getServiceFixedTime());
//        }
//##        tv_eTime.setText(pjsj);
        tv_mNurse.setText(mRecord.HSXM);
        if (EmptyTool.isBlank(mRecord.JLXH)) {
            tv_mNurse.setText(mAppApplication.user.YHXM);
        }

        //提前加载 数据备用
        if (mRecord == null) {
            showMsgAndVoiceAndVibrator("内部错误");
            return;
        }
        String csdhTemp = mRecord.BDXH;
        if (TextUtils.isEmpty(csdhTemp)) {
            showMsgAndVoiceAndVibrator("数据错误");
            return;
        }
        getPJDialogDownData(false, csdhTemp);


    }

    private void getPJDialogDownData(boolean isPreOne, String csdh) {
        GetPJDialogDownDataListTask task = new GetPJDialogDownDataListTask(isPreOne);
        tasks.add(task);
        task.execute(csdh);
    }

    private Map<String, Pair<Spinner, Boolean>> stringSpinnerMap;

    private TextView timePJView;
    private void goToShowPJDialog(boolean isPre, List<RiskEvaluate> riskEvaluateList) {
        if (riskEvaluateList == null) {
            showMsgAndVoiceAndVibrator("评价信息数据获取失败,请重试");
            return;
        }
        String CSPJ;
        String ZGQK=null;
        String HSZQMSJ;
        if (isPre) {
            CSPJ = mPreOneRecord.CSPJ;
            //   ###         ZGQK = mPreOneRecord.ZGQK;
            HSZQMSJ = mPreOneRecord.HSZQMSJ==null?mPreOneRecord.CSSJ:mPreOneRecord.HSZQMSJ;
        } else {
            CSPJ = mRecord.CSPJ;
            //   ###        ZGQK = mRecord.ZGQK;
            HSZQMSJ = mRecord.HSZQMSJ==null?mRecord.CSSJ:mRecord.HSZQMSJ;
        }
        stringSpinnerMap = new HashMap<>();
        LinearLayout rootLayout = LayoutParamsHelper.buildLinearMatchWrap_V(mContext);

        Map<String, List<RiskEvaluate>> keyValueMap = new HashMap<>();
        for (RiskEvaluate riskEvaluate : riskEvaluateList) {
            if (!keyValueMap.containsKey(riskEvaluate.PJLB)) {
                //没有key
                List<RiskEvaluate> valueList = new ArrayList<>();
                valueList.add(riskEvaluate);
                //
                keyValueMap.put(riskEvaluate.PJLB, valueList);
            } else {
                //有key
                List<RiskEvaluate> valueList = keyValueMap.get(riskEvaluate.PJLB);
                if (valueList == null) {
                    valueList = new ArrayList<>();
                }
                valueList.add(riskEvaluate);
            }
        }
        if (keyValueMap.size() <= 0) {
            showMsgAndVoiceAndVibrator("获取评价信息失败！");
            return;
        }
        for (String key : keyValueMap.keySet()) {
            List<RiskEvaluate> valueList = keyValueMap.get(key);
            //
            LinearLayout rootLay = LayoutParamsHelper.buildLinearMatchWrap_H(mContext);
            rootLay.setPadding(25, 15, 25, 15);
            TextView titleView = ViewBuildHelper.buildTextView(mContext, key);
            SpinnerLayout spinnerLayout = new SpinnerLayout(mContext);
            spinnerLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            //添加空选项
            valueList.add(0, new RiskEvaluate());
            RiskMeasureSpinnerAdapter adapter = new RiskMeasureSpinnerAdapter(valueList);
            Spinner spinner = spinnerLayout.getSpinner();
            spinner.setAdapter(adapter);

            //福建协和 是否发生项目存入该表CSPJ
            if ("是否发生".equals(key)) {
                //必选
                stringSpinnerMap.put(key, Pair.create(spinner, true));
                //
                if (!TextUtils.isEmpty(CSPJ)) {
                    for (int i = 0; i < valueList.size(); i++) {
                        if (CSPJ.equals(adapter.getItem(i).PJXH)) {
                            spinner.setSelection(i, false);// 默认选中项
                            break;
                        }
                    }
                }
            } else if ("转归".equals(key)) {
                //转归项目存入该表ZGQK中  //非必选
                stringSpinnerMap.put(key, Pair.create(spinner, false));
                //
                if (!TextUtils.isEmpty(ZGQK)) {
                    for (int i = 0; i < valueList.size(); i++) {
                        if (ZGQK.equals(adapter.getItem(i).PJXH)) {
                            spinner.setSelection(i, false);// 默认选中项
                            break;
                        }
                    }
                }
            } else {
                //非必选
                stringSpinnerMap.put(key, Pair.create(spinner, false));
            }
            //
            rootLay.addView(titleView);
            rootLay.addView(spinnerLayout);
            rootLayout.addView(rootLay);
        }
        //
        //加入时间选择
        LinearLayout rootLay = LayoutParamsHelper.buildLinearMatchWrap_H(mContext);
        rootLay.setPadding(25, 15, 25, 15);
        timePJView = ViewBuildHelper.buildTimeTextView(mContext, HSZQMSJ);
        timePJView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        timePJView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                YmdHMs ymdHMs = DateTimeHelper.dateTime2YmdHMs(HSZQMSJ);
                showPickerDateTimeCompat(ymdHMs,666666);
            }
        });
        rootLay.addView(timePJView);
        rootLayout.addView(rootLay);
        //
        new AlertDialog.Builder(mContext)
                .setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext, isPre ? "上次措施评价" : "评价"))
                .setView(rootLayout)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (stringSpinnerMap.size() <= 0) {
                            showMsgAndVoiceAndVibrator("没有要保存的数据");
                            return;
                        }
                        //必填验证
                        String mustInputButEmpty = null;
                        for (String pjlb : stringSpinnerMap.keySet()) {
                            Pair<Spinner, Boolean> pair = stringSpinnerMap.get(pjlb);
                            if (pair != null && pair.second) {
                                if (pair.first.getSelectedItem() != null) {
                                    RiskEvaluate riskEvaluate = (RiskEvaluate) pair.first.getSelectedItem();
                                    if (TextUtils.isEmpty(riskEvaluate.PJMS)) {
                                        mustInputButEmpty = pjlb;
                                        break;
                                    }
                                }
                            }
                        }
                        if (!TextUtils.isEmpty(mustInputButEmpty)) {
                            showMsgAndVoiceAndVibrator(mustInputButEmpty + "是必填项目");
                            return;
                        }
                        //保存数据
                        if (stringSpinnerMap.containsKey("是否发生")) {
                            Spinner spinner = stringSpinnerMap.get("是否发生").first;
                            RiskEvaluate riskEvaluate = (RiskEvaluate) spinner.getSelectedItem();
                            if (!isPre) {
                                if (mRecord == null) {
                                    showMsgAndVoiceAndVibrator("没有要保存的数据");
                                    return;
                                }
                                mRecord.CSPJ = riskEvaluate == null ? null : riskEvaluate.PJXH;
                            } else {
                                mPreOneRecord.CSPJ = riskEvaluate == null ? null : riskEvaluate.PJXH;
                            }
                        }
                        if (stringSpinnerMap.containsKey("转归")) {
                            Spinner spinner = stringSpinnerMap.get("转归").first;
                            RiskEvaluate riskEvaluate = (RiskEvaluate) spinner.getSelectedItem();
                            if (!isPre) {
                                if (mRecord == null) {
                                    showMsgAndVoiceAndVibrator("没有要保存的数据");
                                    return;
                                }
                                // ###       mRecord.ZGQK = riskEvaluate == null ? null : riskEvaluate.PJXH;
                            } else {
                                // ###        mPreOneRecord.ZGQK = riskEvaluate == null ? null : riskEvaluate.PJXH;
                            }
                        }
                        //
                        if (!isPre) {
                            mRecord.HSZQMSJ = timePJView.getText().toString();
                            mRecord.HSZQM = mAppApplication.user.YHID;
                        }else{
                            mPreOneRecord.HSZQMSJ = timePJView.getText().toString();
                            mPreOneRecord.HSZQM = mAppApplication.user.YHID;
                        }
                        //TODO: 2018/5/2  保存更多的字段
                        if (!isPre) {
                            saveCS();
                        } else {
                            savePreCS(mPreOneRecord.PGXH);
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create().show();

    }

    private void saveCS() {
        SaveTask task = new SaveTask();
        tasks.add(task);
        task.execute();
    }

    private void savePreCS(String pgxh) {
        SavePrePjTask task = new SavePrePjTask(pgxh);
        tasks.add(task);
        task.execute();
    }

    /**
     * @author 吕自聪 lvzc@bsoft.com.cn
     * @ClassName: GetMeasureListTask
     * @Description: 获取风险评估措施列表
     * @date 2015-12-14 下午2:41:43
     */
    class GetMeasureListTask extends
            AsyncTask<String, Void, Response<RiskMeasure>> {
        /*
         * (非 Javadoc) <p>Title: onPreExecute</p> <p>Description: </p>
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
        protected Response<RiskMeasure> doInBackground(String... params) {

            return NurseFormApi.getInstance(mContext).toaddRiskMeasure(pgdh,
                    pglx, pgxh, jlxh, mAppApplication.jgId);
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description: </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Response<RiskMeasure> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(RiskMeasureActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            getMeasureList();
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

    class GetPreOneMeasureListTask extends
            AsyncTask<String, Void, Response<RiskMeasure>> {
        public GetPreOneMeasureListTask(String prejlxh) {
            this.prejlxh = prejlxh;
        }

        String prejlxh;
        String prePGXH;

        @Override
        protected Response<RiskMeasure> doInBackground(String... params) {
            if (params.length < 1) {
                return null;
            }
            prePGXH = params[0];
            Log.d("tes", "doInBackground: prejlxh" + prejlxh);
            return NurseFormApi.getInstance(mContext).toaddRiskMeasure(pgdh,
                    pglx, prePGXH, prejlxh, mAppApplication.jgId);
        }

        @Override
        protected void onPostExecute(Response<RiskMeasure> result) {
//            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(RiskMeasureActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            //
                            getPreOneMeasure(prejlxh, prePGXH);
                        }

                    }).showLoginDialog();
                } else if (result.ReType == 0) {
                    if (result.Data != null) {
                        mPreOneRecord = result.Data.RECORD;
                        //
                        //有上一次措施单
                        new AlertDialog.Builder(mContext).setMessage("是否对上次措施进行评价")
                                .setPositiveButton("评价", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String csdhTemp = mPreOneRecord.BDXH;
                                        getPJDialogDownData(true, csdhTemp);
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
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

    class GetPJDialogDownDataListTask extends
            AsyncTask<String, Void, Response<List<RiskEvaluate>>> {
        String csdhTemp;

        public GetPJDialogDownDataListTask(boolean isPreOne) {
            this.isPreOne = isPreOne;
        }

        boolean isPreOne;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//           showLoadingDialog(R.string.loading);
        }

        @Override
        protected Response<List<RiskEvaluate>> doInBackground(String... params) {
            if (params.length < 1) {
                return null;
            }
            csdhTemp = params[0];
            return NurseFormApi.getInstance(mContext).toGetEvaluateist(csdhTemp, mAppApplication.jgId);
        }

        @Override
        protected void onPostExecute(Response<List<RiskEvaluate>> result) {
//            hideLoadingDialog();
            tasks.remove(this);
            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(RiskMeasureActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            getPJDialogDownData(isPreOne, csdhTemp);
                        }

                    }).showLoginDialog();
                } else if (result.ReType == 0) {
                    if (isPreOne) {
                        goToShowPJDialog(true, result.Data);
                    } else {
                        initPjView(result.Data);
                    }
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    private void initPjView(List<RiskEvaluate> riskEvaluateList) {
        //初始化
        id_tv_for_bar_check.setText("待保存");
        healthguid_datetime_txt.setText("");
        if (!EmptyTool.isBlank(mRecord.SFPJ)) {
            if ("1".equals(mRecord.SFPJ)) {
                //启用评价
                id_tv_for_bar_check.setText("已启用评价");
                String cspj_str = "";
                String zgqk_str = "";
                for (RiskEvaluate evaluate : riskEvaluateList) {
                    if (TextUtils.isEmpty(evaluate.PJXH)) {
                        continue;
                    }
                    if ("是否发生".equals(evaluate.PJLB) && evaluate.PJXH.equals(mRecord.CSPJ)) {
                        cspj_str = evaluate.PJMS;
                    }
                 /*###   if ("转归".equals(evaluate.PJLB) && evaluate.PJXH.equals(mRecord.ZGQK)) {
                        zgqk_str = evaluate.PJMS;
                    }*/

                }

                if (!TextUtils.isEmpty(cspj_str) || !TextUtils.isEmpty(zgqk_str)) {
                    healthguid_datetime_txt.setText("是否发生：" + cspj_str + " 转归：" + zgqk_str);
                } else {
                    healthguid_datetime_txt.setText("点击评价");
                }

                healthguid_datetime_txt.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToShowPJDialog(false, riskEvaluateList);
                    }
                });
            } else if ("0".equals(mRecord.SFPJ)) {
                id_tv_for_bar_check.setText("未启用评价");
            }
        }

    }

    /**
     * @author 吕自聪 lvzc@bsoft.com.cn
     * @ClassName: MeasureAdapter
     * @Description: 风险措施列表适配器
     * @date 2015-12-14 下午2:42:28
     */
    class MeasureAdapter extends BaseAdapter {

        /*
         * (非 Javadoc) <p>Title: getCount</p> <p>Description: </p>
         *
         * @return
         *
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() {
            return mRecord.CSXM.size();
        }

        /*
         * (非 Javadoc) <p>Title: getItem</p> <p>Description: </p>
         *
         * @param arg0
         *
         * @return
         *
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public Measure getItem(int arg0) {
            return mRecord.CSXM.get(arg0);
        }

        /*
         * (非 Javadoc) <p>Title: getItemId</p> <p>Description: </p>
         *
         * @param position
         *
         * @return
         *
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /*
         * (非 Javadoc) <p>Title: getView</p> <p>Description: </p>
         *
         * @param position
         *
         * @param convertView
         *
         * @param parent
         *
         * @return
         *
         * @see android.widget.Adapter#getView(int, android.view.View,
         * android.view.ViewGroup)
         */
        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            final ViewHolder vHolder = new ViewHolder();
            if (EmptyTool.isBlank(mRecord.CSXM.get(position).CSXH)) {
                //组
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.layout_item_bar_check, parent, false);
                vHolder.cb_select = (CheckBox) convertView
                        .findViewById(R.id.healthguid_cbpre);
                vHolder.tv_txt = (TextView) convertView
                        .findViewById(R.id.healthguid_datetime_txt);
                vHolder.tv_txt.setText(mRecord.CSXM.get(position).ZMC);
            } else {
                //项目
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_list_bar_check_text_start, parent, false);
                vHolder.cb_select = (CheckBox) convertView
                        .findViewById(R.id.id_cb);
                vHolder.tv_txt = (TextView) convertView
                        .findViewById(R.id.id_tv_name);
                vHolder.tv_txt.setText(mRecord.CSXM.get(position).XMNR);
                vHolder.tv_flag = (TextView) convertView
                        .findViewById(R.id.id_tv);

                if (mRecord.CSXM.get(position).BTBZ) {
                    vHolder.tv_flag.setText("*");
                    vHolder.tv_flag.setTextColor(Color.RED);
                } else {
                    vHolder.tv_flag.setText("");
                }
            }
            vHolder.cb_select.setChecked(mRecord.CSXM.get(position).SELECT);
            //
            vHolder.cb_select.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (EmptyTool.isBlank(mRecord.CSXM.get(position).CSXH)) {
                        //点击的是组
                        groupClick(position);
                    } else {
                        //点击的是项
                        itemClick(position);
                    }
                }
            });

            return convertView;
        }

        class ViewHolder {
            TextView tv_txt;
            CheckBox cb_select;
            TextView tv_flag;
        }
    }

    private void itemClick(int position) {
        if (mRecord.CSXM.get(position).SELECT) {
            mRecord.CSXM.get(position).SELECT = false;
            //todo 如果需要组选 请打开下列注释行
//                        for (int i = 0; i < mRecord.CSXM.size(); i++) {
//                            if (mRecord.CSXM.get(arg2).XMZH.equals(mRecord.CSXM.get(i).XMZH)) {
//                                mRecord.CSXM.get(i).SELECT = false;
//                            }
//                        }
        } else {
            mRecord.CSXM.get(position).SELECT = true;
            //todo 如果需要组选 请打开下列注释行
//                        for (int i = 0; i < mRecord.CSXM.size(); i++) {
//                            if (mRecord.CSXM.get(arg2).XMZH.equals(mRecord.CSXM.get(i).XMZH)) {
//                                mRecord.CSXM.get(i).SELECT = true;
//                            }
//                        }
        }
        dealAllBtnStatusAndSet();
        mMeasureAdapter.notifyDataSetChanged();
    }

    private void groupClick(int position) {
        if (mRecord.CSXM.get(position).SELECT) {
            mRecord.CSXM.get(position).SELECT = false;
            for (int i = position + 1; i < mRecord.CSXM.size(); i++) {
                if (TextUtils.equals(mRecord.CSXM.get(i).ZMC,
                        mRecord.CSXM.get(position).ZMC)) {
                    mRecord.CSXM.get(i).SELECT = false;
                } else {
                    break;
                }
            }
        } else {
            mRecord.CSXM.get(position).SELECT = true;
            for (int i = position + 1; i < mRecord.CSXM.size(); i++) {
                if (TextUtils.equals(mRecord.CSXM.get(i).ZMC,
                        mRecord.CSXM.get(position).ZMC)) {
                    mRecord.CSXM.get(i).SELECT = true;
                } else {
                    break;
                }
            }
        }
        dealAllBtnStatusAndSet();
        mMeasureAdapter.notifyDataSetChanged();
    }

    /**
     * @author 吕自聪 lvzc@bsoft.com.cn
     * @ClassName: EvaluateAdapter
     * @Description: 风险措施评价
     * @date 2015-12-15 上午9:17:34
     */
    class EvaluateSpinnerAdapter extends BaseAdapter {

        /*
         * (非 Javadoc) <p>Title: getCount</p> <p>Description: </p>
         *
         * @return
         *
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() {
            return mEvaluate.size();
        }

        /*
         * (非 Javadoc) <p>Title: getItem</p> <p>Description: </p>
         *
         * @param position
         *
         * @return
         *
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public RiskEvaluate getItem(int position) {
            return mEvaluate.get(position);
        }

        /*
         * (非 Javadoc) <p>Title: getItemId</p> <p>Description: </p>
         *
         * @param position
         *
         * @return
         *
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.simple_spinner_dropdown_item, parent, false);
            TextView tv = (TextView) convertView.findViewById(R.id.text1);
            tv.setText(mEvaluate.get(position).PJMS);
            return convertView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.simple_spinner_item, parent, false);
            TextView tv = (TextView) convertView.findViewById(R.id.text1);
            tv.setText(mEvaluate.get(position).PJMS);
            return convertView;
        }

    }

    /**
     * @author 吕自聪 lvzc@bsoft.com.cn
     * @ClassName: SaveTask
     * @Description: 保存措施记录
     * @date 2015-12-15 上午11:24:57
     */
    class SaveTask extends AsyncTask<Void, Void, Response<RiskMeasure>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
        protected Response<RiskMeasure> doInBackground(Void... params) {
            mRecord.PGXH = pgxh;
            mRecord.CSGH = mAppApplication.user.YHID;
            mRecord.CSSJ = tv_mTime.getText().toString();
            mRecord.CustomIsSync = customIsSync;

            String data = "";
            MeasureRecord measureRecord = new MeasureRecord();
            measureRecord.PGXH = mRecord.PGXH;
            measureRecord.CSGH = mRecord.CSGH;
            measureRecord.CSSJ = mRecord.CSSJ;
            measureRecord.CSXM = mRecord.CSXM;
            measureRecord.BDXH = mRecord.BDXH;
            measureRecord.JLXH = mRecord.JLXH;
            measureRecord.SFPJ = mRecord.SFPJ;
            // ###       measureRecord.ZGQK = mRecord.ZGQK;
            measureRecord.CSPJ = mRecord.CSPJ;
            measureRecord.HSXM = mRecord.HSXM;
            //ADD 2018-7-3 14:29:11
            measureRecord.HSZQM = mRecord.HSZQM;
            measureRecord.HSZQMSJ = mRecord.HSZQMSJ;
            // add 2020-7-21 21:29:33  by ling
            measureRecord.CustomIsSync = mRecord.CustomIsSync;

            MeasureRecordPostData mRPostData = new MeasureRecordPostData();
            mRPostData.PGDH = pgdh;
            mRPostData.BQID = mAppApplication.getAreaId();
            mRPostData.JGID = mAppApplication.jgId;
            mRPostData.ZYH = mAppApplication.sickPersonVo.ZYH;
            mRPostData.MeasureRecord = measureRecord;


            try {
                data = JsonUtil.toJson(mRPostData);
//                data = URLEncoder.encode(data, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
//            return NurseFormApi.getInstance(mContext).tosaveRiskMeasure(data,
//                    pgdh, mAppApplication.sickPersonVo.ZYH, mAppApplication.getAreaId(), mAppApplication.jgId);
            return NurseFormApi.getInstance(mContext).tosaveRiskMeasure(data);
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description: </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Response<RiskMeasure> result) {
            hideLoadingDialog();
            tasks.remove(this);

            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(RiskMeasureActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            saveRiskMeasure();
                        }

                    }).showLoginDialog();
                } else if (result.ReType == 0) {
                    if (result.Data != null) {
                        showMsgAndVoice(R.string.project_save_success);
                     /*###不用评价上次
                        协和需求
                        if (TextUtils.isEmpty(mRecord.JLXH) || "0".equals(mRecord.JLXH)) {
                            //新增的情况 而且只有压疮评估显示上次评价对话框
                            if ("1".equals(pglx)) {
                                //取上一次
                                String csdhTemp = result.Data.RECORD.BDXH;
                                String pgxhTemp = result.Data.RECORD.PGXH;
                                getPreCSJLTask(csdhTemp, pgxhTemp);
                            }
                        }*/
                        handleResult(result);

                        if (result.Data.IsSync) {
                            FormSyncUtil syncUtil = new FormSyncUtil();
                            syncUtil.setOnDialogClickListener(
                                    new FormSyncUtil.onCancelClickListener() {
                                        @Override
                                        public void onCancel() {
                                        }
                                    }, new FormSyncUtil.onConfirmClickListener() {
                                        @Override
                                        public void onConfirm() {
                                        }
                                    }
                            );
                            syncUtil.InvokeSync(RiskMeasureActivity.this,
                                    result.Data.SyncData, mAppApplication.jgId, tasks);
                        }
                    }
                } else {
                    showMsgAndVoiceAndVibrator(R.string.project_save_failed);
                  /*  MediaUtil.getInstance(mContext)
                            .playSound(R.raw.wrong, mContext);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
            }
        }
    }


    class SavePrePjTask extends AsyncTask<Void, Void, Response<RiskMeasure>> {
        public SavePrePjTask(String nowPgxh) {
            this.pgxhOfPreData = nowPgxh;
        }

        private String pgxhOfPreData;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(R.string.saveing);
        }

        @Override
        protected Response<RiskMeasure> doInBackground(Void... params) {
            String data = "";
            MeasureRecord measureRecord = new MeasureRecord();
            measureRecord.PGXH = pgxhOfPreData;//！！！！
            measureRecord.CSGH = mAppApplication.user.YHID;
// ###!!!不更新时间         measureRecord.CSSJ = tv_mTime.getText().toString();
            measureRecord.CSSJ = mPreOneRecord.CSSJ;
            measureRecord.CSXM = mPreOneRecord.CSXM;
            measureRecord.BDXH = mPreOneRecord.BDXH;
            measureRecord.JLXH = mPreOneRecord.JLXH;
            measureRecord.SFPJ = mPreOneRecord.SFPJ;
            // ###             measureRecord.ZGQK = mPreOneRecord.ZGQK;
            measureRecord.CSPJ = mPreOneRecord.CSPJ;
            measureRecord.HSXM = mPreOneRecord.HSXM;
            //ADD 2018-7-3 14:29:11
            measureRecord.HSZQM = mPreOneRecord.HSZQM;
            measureRecord.HSZQMSJ = mPreOneRecord.HSZQMSJ;

            MeasureRecordPostData mRPostData = new MeasureRecordPostData();
            mRPostData.PGDH = pgdh;
            mRPostData.BQID = mAppApplication.getAreaId();
            mRPostData.JGID = mAppApplication.jgId;
            mRPostData.ZYH = mAppApplication.sickPersonVo.ZYH;
            mRPostData.MeasureRecord = measureRecord;


            try {
                data = JsonUtil.toJson(mRPostData);
//                data = URLEncoder.encode(data, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
//            return NurseFormApi.getInstance(mContext).tosaveRiskMeasure(data,
//                    pgdh, mAppApplication.sickPersonVo.ZYH, mAppApplication.getAreaId(), mAppApplication.jgId);
            return NurseFormApi.getInstance(mContext).tosaveRiskMeasure(data);
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description: </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Response<RiskMeasure> result) {
            hideLoadingDialog();
            tasks.remove(this);

            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(RiskMeasureActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            saveRiskMeasure();
                        }

                    }).showLoginDialog();
                } else if (result.ReType == 0) {
                    if (result.Data != null) {
                        showMsgAndVoice("评价上次措施成功");
                    }
                } else {
                    showMsgAndVoiceAndVibrator(R.string.project_save_failed);
                  /*  MediaUtil.getInstance(mContext)
                            .playSound(R.raw.wrong, mContext);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
            }
        }
    }

    private void getPreCSJLTask(String csdhTemp, String pgxhTemp) {
        if (TextUtils.isEmpty(csdhTemp)) {
            return;
        }
        GetPreCSJLTask preCSJLTask = new GetPreCSJLTask(csdhTemp, pgxhTemp);
        tasks.add(preCSJLTask);
        preCSJLTask.execute();
    }

    class GetPreCSJLTask extends AsyncTask<Void, Void, Response<KeyValue<String, String>>> {
        private String csdhTemp;
        private String pgxhTemp;

        public GetPreCSJLTask(String csdh, String pgxh) {
            this.csdhTemp = csdh;
            this.pgxhTemp = pgxh;
        }

        @Override
        protected Response<KeyValue<String, String>> doInBackground(Void... params) {
            //pgxhTemp 服务端没有用，以内协和需求：需要跨评估记录 去评价
            return NurseFormApi.getInstance(mContext).getPreOneCSJL(
                    mAppApplication.sickPersonVo.ZYH, csdhTemp, pgxhTemp, mAppApplication.jgId);
        }

        @Override
        protected void onPostExecute(Response<KeyValue<String, String>> result) {
//            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(RiskMeasureActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            getPreCSJLTask(csdhTemp, pgxhTemp);
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    if (result.Data != null) {
                        String jlxh = result.Data.key;
                        String pgxh = result.Data.value;
                        if (!TextUtils.isEmpty(jlxh) && !TextUtils.isEmpty(pgxh)) {
                            getPreOneMeasure(jlxh, pgxh);
                        }
                    }

                } else {
                    showMsgAndVoice(result.Msg);
                 /*   MediaUtil.getInstance(mContext)
                            .playSound(R.raw.wrong, mContext);*/
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
     * @Description: 删除风险措施
     * @date 2015-12-16 上午9:59:29
     */
    class DeleteTask extends AsyncTask<Void, Void, Response<String>> {
        /*
         * (非 Javadoc) <p>Title: onPreExecute</p> <p>Description: </p>
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(R.string.deleteing);
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
        protected Response<String> doInBackground(Void... params) {
            return NurseFormApi.getInstance(mContext).todeleteRiskMeasure(
                    mRecord.JLXH, mAppApplication.jgId);
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
            hideLoadingDialog();
            tasks.remove(this);

            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(RiskMeasureActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {

                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    showMsgAndVoice(result.Data);
                    finish();
                } else {
                    showMsgAndVoice(result.Msg);
                 /*   MediaUtil.getInstance(mContext)
                            .playSound(R.raw.wrong, mContext);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    /*## class EvaluateTask extends AsyncTask<Void, Void, Response<RiskMeasure>> {
     *//*
     * (非 Javadoc) <p>Title: onPreExecute</p> <p>Description: </p>
     *
     * @see android.os.AsyncTask#onPreExecute()
     *//*
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(R.string.saveing);
        }

        *//*
     * (非 Javadoc) <p>Title: doInBackground</p> <p>Description: </p>
     *
     * @param params
     *
     * @return
     *
     * @see android.os.AsyncTask#doInBackground(Params[])
     *//*
        @Override
        protected Response<RiskMeasure> doInBackground(Void... params) {
            return NurseFormApi.getInstance(mContext).toevaluteRiskMeasure(
                    mRecord.JLXH,
                    tv_eTime.getText().toString().replace(" ", "T"),
                    mEvaluate.get(mSpinner.getSelectedItemPosition()).PJXH,
                    mAppApplication.user.YHID, pgdh, pgxh, mAppApplication.jgId);
        }

        *//*
     * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description: </p>
     *
     * @param result
     *
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     *//*
        @Override
        protected void onPostExecute(Response<RiskMeasure> result) {
            hideLoadingDialog();
            tasks.remove(this);

            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(RiskMeasureActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            evaluteMeasure();
                        }
                    }).showLoginDialog();
                } else if (result.ReType == 0) {

                    showMsgAndVoice(R.string.project_save_success);
                    handleResult(result);

                } else {
                    showMsgAndVoice(R.string.project_save_failed);
                    *//*MediaUtil.getInstance(mContext)
                            .playSound(R.raw.wrong, mContext);*//*
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }*/
}
