/**
 * @Title: NursePlanEvaluateActivity.java
 * @Package com.bsoft.mob.ienr.activity.user
 * @Description: 护理计划评价
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-12-4 下午1:46:48
 * @version V1.0
 */
package com.bsoft.mob.ienr.activity.user;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.api.NursePlanApi;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.helper.ContextCompatHelper;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.nurseplan.Evaluate;
import com.bsoft.mob.ienr.model.nurseplan.EvaluateAndRecord;
import com.bsoft.mob.ienr.model.nurseplan.ProblemEvaluateSaveData;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.DisplayUtil;
import com.bsoft.mob.ienr.util.FormSyncUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;
import com.classichu.vectortextview.ClassicVectorTextView;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @ClassName: NursePlanEvaluateActivity
 * @Description: 护理计划评价
 * @date 2015-12-4 下午1:46:48
 */
public class NursePlanEvaluateActivity extends BaseBarcodeActivity {
    public static final int REQUEST_CODE = 1;
    private EvaluateAndRecord itemAndHistory;
    private ListView history;
    private ListView evalute;
    private TextView tv_time;
    private ImageView iv_select;
    private String jlwt;
    private String wtxh;
    private String evalutetime;
    private boolean isEvaluted;
    private int i = 1;
    private int[] backgroundColor = {R.color.classicViewBg, R.color.white};
    private Map<Integer, Integer> colorMap = new HashMap<Integer, Integer>();
    private Map<String, String> discribMap = new HashMap<String, String>();
    private String wtlx;

    /*
     * (非 Javadoc) <p>Title: initBarBroadcast</p> <p>Description: </p>
     *
     * @see com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity#initBarBroadcast()
     */
    @Override
    public void initBarBroadcast() {
    }

    /**
     * 设置长宽显示参数
     */
    private void setLayoutParams() {

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = DisplayUtil.getHeightPixels(this) * 4 / 5;
        params.width = DisplayUtil.getWidthPixels(this) * 9 / 10;
        this.getWindow().setAttributes(params);
    }

    private void getParams() {
        jlwt = getIntent().getStringExtra("JLWT");
        wtxh = getIntent().getStringExtra("WTXH");
        wtlx = getIntent().getStringExtra("WTLX");
        initData();
    }

    private void initData() {
        GetEvaluteTask task = new GetEvaluteTask();
        tasks.add(task);
        task.execute();
    }

    private void initView() {
        history = (ListView) findViewById(R.id.id_lv);
        evalute = (ListView) findViewById(R.id.id_lv_2);

        //防止下拉刷新滑动冲突
        EmptyViewHelper.setEmptyView(evalute, "listView");
        //不需要
        id_swipe_refresh_layout.setEnabled(false);
//        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, evalute);

        tv_time = (TextView) findViewById(R.id.id_tv_2_for_bar_image);
        tv_time.setText(evalutetime);
        iv_select = (ImageView) findViewById(R.id.id_iv_for_bar_image);
        iv_select.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String datetime = tv_time
                        .getText().toString();
                YmdHMs ymdHMs = DateTimeHelper.dateTime2YmdHMs(datetime);
                showPickerDateTimeCompat(ymdHMs, R.id.id_tv_2_for_bar_image);
            }
        });
    }

    private void initActionBar() {
        actionBar.setTitle("计划评价");
        actionBar.setBackAction(new Action() {
            @Override
            public String getText() {
                return getString(R.string.menu_back);
            }

            @Override
            public void performAction(View view) {
                if (isEvaluted)
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
                saveEvaluate();
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }
        });
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_nurse_plan_evluate;
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        evalutetime = DateTimeHelper.getServer_yyyyMMddHHmm00();
        getParams();
        setLayoutParams();
        initView();
        initActionBar();
    }

    // 保存问题评价
    private void saveEvaluate() {
        EvaluateTask task = new EvaluateTask();
        tasks.add(task);
        task.execute();
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
        String dateTime = DateTimeFactory.getInstance().ymdhms2DateTime(year, month, dayOfMonth, hourOfDay, minute, 0);

        evalutetime = dateTime;
        tv_time.setText(evalutetime);
        for (Evaluate ev : itemAndHistory.PJXM) {
            ev.PJSJ = evalutetime;
        }
    }

    private void handleResult(EvaluateAndRecord result) {
        itemAndHistory = result;
        if (itemAndHistory.PJLS != null && itemAndHistory.PJLS.size() > 0) {
            colorMap.put(itemAndHistory.PJLS.get(0).PJZH, R.color.white);
            for (int position = 0; position < itemAndHistory.PJLS.size(); position++) {
                if (position > 0) {
                    if (itemAndHistory.PJLS.get(position).PJZH != itemAndHistory.PJLS
                            .get(position - 1).PJZH) {
                        if (--i < 0)
                            i = 1;
                        colorMap.put(itemAndHistory.PJLS.get(position).PJZH,
                                backgroundColor[i]);
                    }
                }
            }
            history.setAdapter(new EvaluteHistoryAdatper());
        } else
            history.setAdapter(null);
        if (itemAndHistory.PJXM != null) {
            for (Evaluate evaluate : itemAndHistory.PJXM)
                discribMap.put(evaluate.PJXH, evaluate.PJMS);
            evalute.setAdapter(new EvaluteAdapter());
        }
        if (itemAndHistory != null) {
            if (itemAndHistory.IsSync) {
                new FormSyncUtil().InvokeSync(NursePlanEvaluateActivity.this,
                        itemAndHistory.SyncData, mAppApplication.jgId, tasks);
            }
        }
    }

    class GetEvaluteTask extends
            AsyncTask<Void, Void, Response<EvaluateAndRecord>> {
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
        protected Response<EvaluateAndRecord> doInBackground(Void... params) {

            if (wtlx.equals("1")) {
                return NursePlanApi.getInstance(NursePlanEvaluateActivity.this)
                        .getPlanEvaluateList(jlwt, wtxh, mAppApplication.jgId);
            } else if (wtlx.equals("2")) {
                return NursePlanApi.getInstance(NursePlanEvaluateActivity.this)
                        .getFocusEvaluateList(jlwt, wtxh, mAppApplication.jgId);
            } else {
                return null;
            }
        }

        protected void onPostExecute(Response<EvaluateAndRecord> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(NursePlanEvaluateActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            initData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    if (result.Data != null)
                        handleResult(result.Data);
                } else {
                    showMsgAndVoice(result.Msg);
                   /* MediaUtil.getInstance(NursePlanEvaluateActivity.this)
                            .playSound(R.raw.wrong, NursePlanEvaluateActivity.this);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    /**
     * @author 吕自聪 lvzc@bsoft.com.cn
     * @Description: 保存问题评价
     * @date 2015-12-4 上午11:33:27
     */
    class EvaluateTask extends
            AsyncTask<Void, Void, Response<EvaluateAndRecord>> {
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
        protected Response<EvaluateAndRecord> doInBackground(Void... params) {
            String data = "";
            try {
                ProblemEvaluateSaveData saveData = new ProblemEvaluateSaveData();
                saveData.JLWT = jlwt;
                saveData.WTXH = wtxh;
                saveData.ZYH = mAppApplication.sickPersonVo.ZYH;
                saveData.YHID = mAppApplication.user.YHID;
                saveData.BQID = mAppApplication.getAreaId();
                saveData.JGID = mAppApplication.jgId;
                saveData.evaluateList = itemAndHistory.PJXM;
                data = JsonUtil.toJson(saveData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (wtlx.equals("1")) {
                return NursePlanApi.getInstance(NursePlanEvaluateActivity.this)
                        .savePlanProblemEvaluate(data);
            } else if (wtlx.equals("2")) {
                return NursePlanApi.getInstance(NursePlanEvaluateActivity.this)
                        .saveFocusProblemEvaluate(data);
            } else {
                return null;
            }
        }

        protected void onPostExecute(Response<EvaluateAndRecord> result) {
            hideLoadingDialog();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(NursePlanEvaluateActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            saveEvaluate();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    isEvaluted = true;
                    showMsgAndVoice(R.string.project_save_success);
                    if (result.Data != null)
                        handleResult(result.Data);
                } else {
                    showMsgAndVoice(result.Msg);
                  /*  MediaUtil.getInstance(NursePlanEvaluateActivity.this)
                            .playSound(R.raw.wrong, NursePlanEvaluateActivity.this);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    // 评价记录适配器
    class EvaluteHistoryAdatper extends BaseAdapter {

        /*
         * (非 Javadoc) <p>Title: getCount</p> <p>Description: </p>
         *
         * @return
         *
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() {
            return itemAndHistory.PJLS.size();
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
        public Evaluate getItem(int position) {
            return itemAndHistory.PJLS.get(position);
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
            EvaluteHistoryView vHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(
                        NursePlanEvaluateActivity.this).inflate(
                        R.layout.item_list_text_two_vert_primary_icon,
                        parent, false);
                vHolder = new EvaluteHistoryView();
                vHolder.tv_item = (TextView) convertView
                        .findViewById(R.id.id_tv_one);
                vHolder.tv_info = (TextView) convertView
                        .findViewById(R.id.id_tv_two);
                vHolder.del = (ClassicVectorTextView) convertView
                        .findViewById(R.id.id_tv_more);
                Drawable delDraw = ContextCompatHelper.getDrawable(NursePlanEvaluateActivity.this, R.drawable.ic_delete_forever_black_24dp);
                vHolder.del.setCompoundDrawablesWithIntrinsicBounds(null, null, delDraw, null);
                convertView.setTag(vHolder);
            } else {
                vHolder = (EvaluteHistoryView) convertView.getTag();
            }
            convertView.setBackgroundResource(colorMap
                    .get(itemAndHistory.PJLS.get(position).PJZH));
            vHolder.tv_item.setText(itemAndHistory.PJLS.get(position).PJMS);
            if (!EmptyTool.isBlank(itemAndHistory.PJLS.get(position).PJSJ)) {
                Date date= DateUtil.getDateCompat(itemAndHistory.PJLS.get(position).PJSJ);
                String time=DateUtil.format_yyyyMMdd_HHmm.format(date);
                vHolder.tv_info
                        .setText(itemAndHistory.PJLS.get(position).PJHS
                                + " 于 "+ time);
            } else {

                vHolder.tv_info
                        .setText(itemAndHistory.PJLS.get(position).PJHS
                                + " 于 "
                                + DateTimeHelper.getServer_yyyyMMddHHmm00());
            }
            vHolder.del.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    DelTask task = new DelTask();
                    tasks.add(task);
                    task.execute(itemAndHistory.PJLS.get(position).JLPJ);
                }
            });
            return convertView;
        }
    }

    // 评价适配器
    class EvaluteAdapter extends BaseAdapter {

        boolean isEditTextFirstInit = true;

        /*
         * (非 Javadoc) <p>Title: getCount</p> <p>Description: </p>
         *
         * @return
         *
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() {
            return itemAndHistory.PJXM.size();
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
        public Evaluate getItem(int position) {
            return itemAndHistory.PJXM.get(position);
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

            final EvaluteView vHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(
                        NursePlanEvaluateActivity.this).inflate(
                        R.layout.item_list_bar_check_edit_lines, parent, false);
                vHolder = new EvaluteView();
                vHolder.cb_selected = (CheckBox) convertView
                        .findViewById(R.id.checkBox);
                vHolder.et_item = (EditText) convertView
                        .findViewById(R.id.nurseplan_evluate);
                convertView.setTag(vHolder);
            } else {
                vHolder = (EvaluteView) convertView.getTag();
            }
            isEditTextFirstInit = true;
            vHolder.et_item.setText(discribMap.get(itemAndHistory.PJXM
                    .get(position).PJXH));

            isEditTextFirstInit = false;
            vHolder.et_item.setTag(itemAndHistory.PJXM.get(position).PJXH);
            if (itemAndHistory.PJXM.get(position).PJWS.equals("0"))
                vHolder.et_item.setEnabled(false);
            vHolder.cb_selected
                    .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                                     boolean isChecked) {
                            itemAndHistory.PJXM.get(position).selected = isChecked;
                        }
                    });
            vHolder.et_item.setTag(R.id.id_hold_view_pos, position);
            vHolder.et_item.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    // final int readPos = evalute.getPositionForView((View) vHolder.et_item.getParent());
                    int realPos = (int) vHolder.et_item.getTag(R.id.id_hold_view_pos);
                    if (!EmptyTool.isBlank(s.toString().trim()) && !isEditTextFirstInit) {
                        itemAndHistory.PJXM.get(realPos).PJMS = s.toString().trim();
                        Log.i(Constant.TAG_COMM, "afterTextChanged: position:" + position);
                        Log.i(Constant.TAG_COMM, "afterTextChanged: realPos:" + realPos);
                    }
                }
            });
            return convertView;
        }

    }

    class EvaluteHistoryView {
        LinearLayout ll_container;
        ClassicVectorTextView del;
        TextView tv_item;
        TextView tv_info;
    }

    class EvaluteView {
        CheckBox cb_selected;
        EditText et_item;
    }

    class DelTask extends AsyncTask<String, Void, Response<String>> {

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
        protected Response<String> doInBackground(String... params) {
            if (params.length < 1)
                return null;
            if (wtlx.equals("1")) {
                return NursePlanApi.getInstance(NursePlanEvaluateActivity.this)
                        .deletePlanProblemEvaluate(jlwt, params[0], mAppApplication.jgId);

            } else if (wtlx.equals("2")) {
                return NursePlanApi.getInstance(NursePlanEvaluateActivity.this)
                        .deleteFocusProblemEvaluate(jlwt, params[0], mAppApplication.jgId);

            } else {
                return null;
            }
        }

        protected void onPostExecute(Response<String> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(NursePlanEvaluateActivity.this, mAppApplication).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    isEvaluted = true;
                    showMsgAndVoice(result.Data);
                    history.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            initData();
                        }
                    }, 500);
                } else {
                    showMsgAndVoice(TextUtils.isEmpty(result.Msg)?"删除成功":result.Msg);
                  /*  MediaUtil.getInstance(NursePlanEvaluateActivity.this)
                            .playSound(R.raw.wrong, NursePlanEvaluateActivity.this);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }


}
