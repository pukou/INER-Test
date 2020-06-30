/**
 * @Title: NurseEvaluateTemplateFragment.java
 * @Package com.bsoft.mob.ienr.fragment.user
 * @Description: 护理评估模板页
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2016-4-7 下午12:09:09
 * @version V1.0
 */
package com.bsoft.mob.ienr.fragment.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.NurseEvaluateActivity;
import com.bsoft.mob.ienr.api.EvaluateApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.evaluate.EvaluateRecordItem;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @ClassName: NurseEvaluateTemplateFragment
 * @Description: 护理评估模板页
 * @date 2016-4-7 下午12:09:09
 */
public class NurseEvaluateTemplateFragment extends BaseUserFragment {


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

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_nurse_evaluate_template;
    }

    @Override
    protected void initView(View rootLayout, Bundle savedInstanceState) {


        listView = (ListView) rootLayout
                .findViewById(R.id.id_lv);


        EmptyViewHelper.setEmptyView(listView, "listView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, listView);
        sltStimeView = rootLayout.findViewById(R.id.slt_stime_ly);
        sltEtimeView = rootLayout.findViewById(R.id.slt_etime_ly);

        stime = (TextView) rootLayout.findViewById(R.id.stime);
        etime = (TextView) rootLayout.findViewById(R.id.etime);

        searchBtn = (ImageView) rootLayout.findViewById(R.id.search);
        initBroadCast();
        initListView();
        initTime();
        initSearchBtn();
        initActionBar();

        //福建协和  隐藏
        rootLayout.findViewById(R.id.id_layout_double_time).setVisibility(
                mCurrType == TYPE_FORM ? View.GONE : View.VISIBLE);

        toRefreshData();

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

        searchBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toRefreshData();
            }
        });
    }

    @Override
    protected void toRefreshData() {
        //##actionFormTask(mCurrType);
        GetFormTaskNew getFormTaskNew = new GetFormTaskNew();
        getFormTaskNew.execute();
        tasks.add(getFormTaskNew);
    }

    public View.OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            String dateStr = null;
            int id = v.getId();
            if (id == R.id.slt_stime_ly) {
                dateStr = stime.getText().toString();
            } else if (id == R.id.slt_etime_ly) {
                dateStr = etime.getText().toString();
            }
            showDatePickerCompat(dateStr, id);

        }
    };

    @Override
    public void onDateSet(int year, int month, int dayOfMonth, int viewId) {
        String nowDate = DateTimeFactory.getInstance().ymd2Date(year, month, dayOfMonth);
        if (viewId == R.id.slt_stime_ly) {
            //选择开始时间
            String endDate = etime.getText().toString();
            boolean after = DateTimeFactory.getInstance().dateAfter(nowDate, endDate);
            if (after) {
                showMsgAndVoiceAndVibrator("开始时间后于结束时间,请重新选择!");
                return;
            }
        } else if (viewId == R.id.slt_etime_ly) {
            //选择结束时间
            String startDate = stime.getText().toString();
            boolean before = DateTimeFactory.getInstance().dateBefore(nowDate, startDate);
            if (before) {
                showMsgAndVoiceAndVibrator("结束时间先于开始时间，请重新选择!");
                return;
            }
        }
        initTimeTxt(nowDate, viewId);

    }


    private void initTimeTxt(String dateStr, int viewId) {
        String timeStr = dateStr;
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
        actionBar.setPatient(mAppApplication.sickPersonVo.BRCH + mAppApplication.sickPersonVo.BRXM);
    }

    private void initListView() {


        listView.setOnItemClickListener(onItemClickListener);
    }

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            EvaluateRecordItem item = (EvaluateRecordItem) listView
                    .getAdapter().getItem(position);
            if (item != null) {
                //add 2017年4月28日17:13:37  每点一次评估单  重新来过
                if (mAppApplication.mEvaluateTempDataBean != null) {
                    mAppApplication.mEvaluateTempDataBean.clear();
                }
                if (mAppApplication.mLifeSymptomTempDataBean != null) {
                    mAppApplication.mLifeSymptomTempDataBean.clear();
                }
                //add 2017年4月28日17:13:37
                Intent intent = new Intent(getActivity(),
                        NurseEvaluateActivity.class);
                intent.putExtra("JLXH", item.JLXH);
                intent.putExtra("YSXH", item.YSXH);
                intent.putExtra("YSLX", item.YSLX);
                intent.putExtra("TXSJ", item.TXSJ);
                intent.putExtra("LYBS", item.LYBS);
                intent.putExtra("isZKNotCheckBQ", item.isZKNotCheckBQ);

                startActivity(intent);
            }
        }

    };

    protected void actionFormTask(Integer type) {
        String typeStr = String.valueOf(type);
        String start = stime.getText().toString();
        String end = etime.getText().toString();
        actionFormTaskInner(typeStr, start, end);
    }

    protected void actionFormTaskInner(String... params) {
        GetFormTask task = new GetFormTask();
        tasks.add(task);
        task.execute(params);
    }

    @Deprecated
    class GetFormTask extends AsyncTask<String, String, Response<List<EvaluateRecordItem>>> {

        private int type;

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<EvaluateRecordItem>> doInBackground(String... params) {

            if (params == null || params.length < 1 || params[0] == null) {
                return null;
            }

            if (mAppApplication.sickPersonVo == null) {
                return null;
            }

            String jgid = mAppApplication.jgId;

            EvaluateApi api = EvaluateApi.getInstance(getActivity());

            type = Integer.valueOf(params[0]);
            String start = params[1];
            String end = params[2];
            if (type == TYPE_RECORD_FORM) {

                String zyh = mAppApplication.sickPersonVo.ZYH;

                Response<List<EvaluateRecordItem>> response = api.GetEvaluationList(start, end, zyh,
                        jgid, Constant.sysType);
                return response;
            } else if (type == TYPE_FORM) {
                String bqdm = mAppApplication.getAreaId();

                String yslx = "";
                Bundle bundle = getArguments();
                if (bundle != null) {
                    yslx = bundle.getString("yslx");
                }
                Response<List<EvaluateRecordItem>> response;
                if (EmptyTool.isBlank(yslx)) {
                    response = api.GetNewEvaluationList(bqdm, jgid, mAppApplication.sickPersonVo.ZYH,
                            Constant.sysType);
                } else {
                    response = api.GetNewEvaluationListForYslx(yslx, bqdm, jgid,
                            Constant.sysType);
                }
                return response;
            }
            return null;

        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Response<List<EvaluateRecordItem>> result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        toRefreshData();
                    }
                }).showLoginDialog();
                return;
            } else if (result.ReType == 0) {
                List<EvaluateRecordItem> list = result.Data;
                importList(list);
            } else {
                showTipDialog(result.Msg);
//                AlertBox.Show(mContext, getString(R.string.project_tips), result.Msg, getString(R.string.project_operate_ok));
            }

        }
    }

    class GetFormTaskNew extends AsyncTask<String, String, Response<List<EvaluateRecordItem>>> {
        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<EvaluateRecordItem>> doInBackground(String... params) {
            if (mAppApplication.sickPersonVo == null) {
                return null;
            }
            String jgid = mAppApplication.jgId;
            String zyh = mAppApplication.sickPersonVo.ZYH;
            EvaluateApi api = EvaluateApi.getInstance(mContext);
            String bqdm = mAppApplication.getAreaId();
            String yslx = "";
            Bundle bundle = getArguments();
            if (bundle != null) {
                //有可能风险评估模块传过来的 压疮监控 "91"
                yslx = bundle.getString("yslx");
            }
            Response<List<EvaluateRecordItem>> response;
            if (EmptyTool.isBlank(yslx)) {
                //加载普通列表
                response = api.GetNewEvaluationList(bqdm, jgid, zyh,
                        Constant.sysType);
            } else {
                //加载 压疮监控 "91" 相关的  todo 2018-5-9 14:27:39
                response = api.GetNewEvaluationListForYslx(yslx, bqdm, jgid,
                        Constant.sysType);
            }
            return response;

        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Response<List<EvaluateRecordItem>> result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        toRefreshData();
                    }
                }).showLoginDialog();
                return;
            } else if (result.ReType == 0) {
                List<EvaluateRecordItem> list = result.Data;
                importList(list);
            } else {
                showTipDialog(result.Msg);
//                AlertBox.Show(mContext, getString(R.string.project_tips), result.Msg, getString(R.string.project_operate_ok));
            }

        }
    }


    class ListAdapter extends BaseAdapter {

        private List<EvaluateRecordItem> list;

        private Context mContext;

        public ListAdapter(Context context, List<EvaluateRecordItem> _list) {
            this.list = _list;
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return list != null ? list.size() : 0;
        }

        @Override
        public EvaluateRecordItem getItem(int arg0) {
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

            EvaluateRecordItem vo = list.get(position);
            vHolder.nameView.setText(vo.YSMS);

            String timeStr = null;
            if (!EmptyTool.isBlank(vo.TXSJ)) {
                Date date = DateUtil.getDateCompat(vo.TXSJ);
                String dateStr = DateUtil.format_yyyyMMdd_HHmm.format(date);
                timeStr = "填写时间：" + dateStr;
                vHolder.timeView.setVisibility(View.VISIBLE);
            } else {
                vHolder.timeView.setVisibility(View.INVISIBLE);
            }
            vHolder.timeView.setText(timeStr);

            return convertView;
        }

        class ViewHolder {
            public TextView nameView;
            public TextView timeView;
        }

    }


    public void importList(List<EvaluateRecordItem> list) {
        List<EvaluateRecordItem> listNew = new ArrayList<>();
        for (EvaluateRecordItem evaluateRecordItem : list) {
            if (TextUtils.isEmpty(evaluateRecordItem.BZXX)){
                listNew.add(evaluateRecordItem);
            }else if (!evaluateRecordItem.BZXX.contains("(PDA上不显示)")){
                listNew.add(evaluateRecordItem);
            }
        }
        ListAdapter adapter = new ListAdapter(getActivity(), listNew);
        listView.setAdapter(adapter);

    }

    private void initBroadCast() {
        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                if (BarcodeActions.Refresh.equals(intent.getAction())) {
                    sendUserName();
                    actionBar.setPatient(mAppApplication.sickPersonVo.BRCH
                            + mAppApplication.sickPersonVo.BRXM);
                }
            }
        };
    }

}
