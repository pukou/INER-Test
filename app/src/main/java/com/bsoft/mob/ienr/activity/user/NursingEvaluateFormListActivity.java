package com.bsoft.mob.ienr.activity.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.api.EvaluateApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.nursingeval.NursingEvaluateRecord;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;

import java.util.Date;
import java.util.List;

/**
 * 评估表单列表页,包含了已记录和未记录表单项
 *
 * @author hy
 */
public class NursingEvaluateFormListActivity extends BaseBarcodeActivity {


    private ListView listView;

    public static final String EXTRE_INT_START_TYPE = "start_type";


    /**
     * 获取记录列表类型
     */
    public static final int TYPE_RECORD_FORM = 1;

    /**
     * 获取样式列表类型
     */
    public static final int TYPE_FORM = 0;

    private int mCurrType = TYPE_FORM;

    private View sltStimeView;
    private View sltEtimeView;

    private TextView stime;
    private TextView etime;

    private ImageView searchBtn;


    private void setTimeParentVisibility(int mCurrType) {
        findViewById(R.id.id_layout_double_time).setVisibility(
                mCurrType == TYPE_FORM ? View.GONE : View.VISIBLE);
    }

    private void initView() {


        listView = (ListView) findViewById(R.id.id_lv);

        EmptyViewHelper.setEmptyView(listView, "listView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, listView);
        sltStimeView = findViewById(R.id.slt_stime_ly);
        sltEtimeView = findViewById(R.id.slt_etime_ly);

        stime = (TextView) findViewById(R.id.stime);
        etime = (TextView) findViewById(R.id.etime);


        initListView();
        initTime();
        initSearchBtn();
        initActionBar();

        final TextView stimeTitle = (TextView) findViewById(R.id.stime_title);
        final TextView etimeTitle = (TextView) findViewById(R.id.etime_title);

        stimeTitle.setText(R.string.start_time);
        etimeTitle.setText(R.string.end_time);
    }

    void initTime() {


        String nowDate = DateTimeHelper.getServerDate();
        // 当天
        String eTimeStr = nowDate;
        etime.setText(eTimeStr);

        // 前天
        String startDate= DateTimeHelper.dateAddedDays(nowDate,-6);
        String sTimeStr = startDate;
        stime.setText(sTimeStr);

        sltStimeView.setOnClickListener(onClickListener);
        sltEtimeView.setOnClickListener(onClickListener);
    }

    private void initSearchBtn() {
        searchBtn = (ImageView) findViewById(R.id.search);

        searchBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toRefreshData();
            }
        });
    }

    public OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            String dateStr = null;
            int viewId = v.getId();
            if (viewId == R.id.slt_stime_ly) {
                dateStr = stime.getText().toString();
            } else if (viewId == R.id.slt_etime_ly) {
                dateStr = etime.getText().toString();
            }
            if (EmptyTool.isBlank(dateStr)) {
                return;
            }
            YmdHMs ymdHMs = DateTimeHelper.date2YmdHMs(dateStr);
            showPickerDateCompat(ymdHMs, viewId);
        }
    };

    @Override
    public void onDateSet(int year, int month, int dayOfMonth, int viewId) {
        String nowDate = DateTimeFactory.getInstance().ymd2Date(year, month, dayOfMonth);
        if (viewId == R.id.slt_stime_ly) {
            String endDate = etime.getText().toString();
            boolean after = DateTimeFactory.getInstance().dateAfter(nowDate, endDate);
            if (after) {
                showMsgAndVoiceAndVibrator("开始时间后于结束时间,请重新选择!");
                return;
            }
        } else if (viewId == R.id.slt_etime_ly) {
            String startDate = stime.getText().toString();
            boolean before = DateTimeFactory.getInstance().dateBefore(nowDate, startDate);
            if (before) {
                showMsgAndVoiceAndVibrator("结束时间先于开始时间，请重新选择!");
                return;
            }
        }
        initTimeTxt(nowDate, viewId);

    }

    ;

    private void initTimeTxt(String nowDate, int viewId) {
        String timeStr = nowDate;
        if (viewId == R.id.slt_etime_ly) {
            etime.setText(timeStr);
        } else if (viewId == R.id.slt_stime_ly) {
            stime.setText(timeStr);
        }

    }

    private void initActionBar() {

        if (mCurrType == TYPE_FORM) {
            actionBar.setTitle("评估单样式列表");
        } else if (mCurrType == TYPE_RECORD_FORM) {
            actionBar.setTitle("评估单记录列表");
        }
        actionBar.setPatient(mAppApplication.sickPersonVo.XSCH + mAppApplication.sickPersonVo.BRXM);
        actionBar.setBackAction(new Action() {

            @Override
            public void performAction(View view) {

                finish();
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
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_nursing_evaluate_form_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        Intent intent = getIntent();
        if (intent != null) {
            mCurrType = intent.getIntExtra(EXTRE_INT_START_TYPE, TYPE_FORM);
        }

        initView();
        setTimeParentVisibility(mCurrType);
        toRefreshData();
    }

    private void initListView() {


        listView.setOnItemClickListener(onItemClickListener);
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected void toRefreshData() {

        GetFormTask task = new GetFormTask();
        tasks.add(task);
        task.execute(mCurrType);
    }

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            NursingEvaluateRecord item = (NursingEvaluateRecord) listView
                    .getAdapter().getItem(position);
            if (item != null) {
                Intent data = new Intent();
                data.putExtra("JLXH", item.JLXH);
                data.putExtra("YSXH", item.YSXH);
                data.putExtra("TXSJ", item.TXSJ);
                ///###data.putExtra("LYBS", item.LYBS);
                setResult(RESULT_OK, data);
                finish();
            }
        }
    };


    class GetFormTask extends AsyncTask<Integer, String, Response<List<NursingEvaluateRecord>>> {

        private int type;

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<NursingEvaluateRecord>> doInBackground(Integer... params) {

            if (params == null || params.length < 1 || params[0] == null) {
                return null;
            }

            if (mAppApplication.sickPersonVo == null) {
                return null;
            }

            String jgid = mAppApplication.jgId;

            EvaluateApi api = EvaluateApi.getInstance(getApplicationContext());

            type = params[0];
            if (type == TYPE_RECORD_FORM) {
                String start = stime.getText().toString();
                String end = etime.getText().toString();
                String zyh = mAppApplication.sickPersonVo.ZYH;

             /*   Response<List<EvaluateRecordItem>> response = api.GetEvaluationList(start, end, zyh,
                        jgid, Config.sysType);*/
                String bqdm = mAppApplication.getAreaId();
                Response<List<NursingEvaluateRecord>> response = api.GetNursingEvaluationRecordList_V56Update1(zyh, bqdm, jgid);
                return response;
            } else if (type == TYPE_FORM) {
              /*  String bqdm = mAppApplication.getAreaId();
                Response<List<EvaluateRecordItem>> response = api.GetNewEvaluationList(bqdm, jgid,
                        Config.sysType);*/
                String zyh = mAppApplication.sickPersonVo.ZYH;
                String bqdm = mAppApplication.getAreaId();
                Response<List<NursingEvaluateRecord>> response = api.GetNursingEvaluationRecordList_V56Update1(zyh, bqdm, jgid);
                return response;
            }
            return null;

        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Response<List<NursingEvaluateRecord>> result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(NursingEvaluateFormListActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        toRefreshData();
                        return;
                    }
                }).showLoginDialog();

            } else if (result.ReType == 0) {
                List<NursingEvaluateRecord> list = result.Data;
                importList(list);
            } else {
                showTipDialog(result.Msg);
//                AlertBox.Show(mContext, getString(R.string.project_tips), result.Msg, getString(R.string.project_operate_ok));
            }

        }
    }

    class ListAdapter extends BaseAdapter {

        private List<NursingEvaluateRecord> list;

        private Context mContext;

        public ListAdapter(Context context, List<NursingEvaluateRecord> _list) {
            this.list = _list;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return list != null ? list.size() : 0;
        }

        @Override
        public NursingEvaluateRecord getItem(int arg0) {
            return list.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_list_text_two_primary_icon, parent, false);

                vHolder = new ViewHolder();

                vHolder.nameView = (TextView) convertView
                        .findViewById(R.id.name);
                vHolder.timeView = (TextView) convertView
                        .findViewById(R.id.time);

                convertView.setTag(vHolder);
            } else {
                vHolder = (ViewHolder) convertView.getTag();
            }

            NursingEvaluateRecord vo = list.get(position);
            vHolder.nameView.setText(vo.YSMC + "_" + vo.JLXH);

            String timeStr = null;
            if (!EmptyTool.isBlank(vo.TXSJ)) {
                Date date = DateUtil.getDateCompat(vo.TXSJ);
                String dateStr = DateUtil.format_MMdd_HHmm.format(date);
                timeStr = "填写时间:" +dateStr;
            }
            vHolder.timeView.setText(timeStr);

            return convertView;
        }

        class ViewHolder {
            public TextView nameView;
            public TextView timeView;
        }

    }


    public void importList(List<NursingEvaluateRecord> list) {

        ListAdapter adapter = new ListAdapter(getApplicationContext(), list);
        listView.setAdapter(adapter);
    }

    @Override
    public void initBarBroadcast() {

        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (BarcodeActions.Bar_Get.equals(intent.getAction())) {

                    BarcodeEntity entity = (BarcodeEntity) intent
                            .getParcelableExtra("barinfo");
                    if (FastSwitchUtils.needFastSwitch(entity)) {
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
