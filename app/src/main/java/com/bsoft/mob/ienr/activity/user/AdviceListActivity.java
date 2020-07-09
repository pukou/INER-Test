package com.bsoft.mob.ienr.activity.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.activity.user.adapter.AdviceDetailAdapter;
import com.bsoft.mob.ienr.api.AdviceApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.advice.AdviceData;
import com.bsoft.mob.ienr.model.advice.AdviceDetail;
import com.bsoft.mob.ienr.model.advice.AdviceVo;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.StringUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-13 上午11:37:00
 * @类说明 医嘱查询
 */
public class AdviceListActivity extends BaseBarcodeActivity {

    private static final int DIALOG_DETAIL = 0;


    private ListView listView;

    /**
     * ChoseView choseView;
     */
    private LinearLayout choseViewLay;

    private CheckBox lsyz, wxbz;

    private ArrayList<AdviceVo> list = new ArrayList<AdviceVo>();

    // private AdviceListAdapter adapter;

    private View sltStimeView;
    private View sltEtimeView;

    private TextView stime, etime;


    private void performGetTask(byte type, String... params) {

        GetDataTask getDataTask = new GetDataTask(type);
        tasks.add(getDataTask);
        getDataTask.execute(params);
    }

    public void findView() {

        lsyz = (CheckBox) findViewById(R.id.id_cb);
        lsyz.setChecked(false);
        wxbz = (CheckBox) findViewById(R.id.id_cb_2);
        wxbz.setChecked(false);
        choseViewLay = (LinearLayout) findViewById(R.id.id_ll_controller);
        choseViewLay.setVisibility(View.VISIBLE);
        listView = (ListView) findViewById(R.id.id_lv);

        EmptyViewHelper.setEmptyView(listView, "listView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, listView);
        stime = (TextView) findViewById(R.id.stime);
        etime = (TextView) findViewById(R.id.etime);

        sltStimeView = findViewById(R.id.slt_stime_ly);
        sltEtimeView = findViewById(R.id.slt_etime_ly);


        lsyz.setText(R.string.advice_tmp);
        wxbz.setText(R.string.advice_invalid);

        initActionBar();

        initCheckButtons();
        initSearchBtn();
        initTime();
    }

    void initTime() {

        String nowDate = DateTimeHelper.getServerDate();
        // 当天
        String eTimeStr = nowDate;
        etime.setText(eTimeStr);

        // 前天
        // 改为当天 2015-6-25 by lvzc
        String sTimeStr = nowDate;
        stime.setText(sTimeStr);

        sltStimeView.setOnClickListener(onClickListener);
        sltEtimeView.setOnClickListener(onClickListener);

        final TextView stimeTitle = (TextView) sltStimeView
                .findViewById(R.id.stime_title);
        final TextView etimeTitle = (TextView) sltEtimeView
                .findViewById(R.id.etime_title);

        stimeTitle.setText(R.string.start_time);
        etimeTitle.setText(R.string.end_time);
    }

    public View.OnClickListener onClickListener = new OnClickListener() {

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

    private void initTimeTxt(String nowDate, int viewId) {
        String timeStr = nowDate;
        if (viewId == R.id.slt_etime_ly) {
            etime.setText(timeStr);
        } else if (viewId == R.id.slt_stime_ly) {
            stime.setText(timeStr);
        }

    }

    private void initSearchBtn() {

        final ImageView searchBtn = (ImageView) findViewById(R.id.search);

        searchBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String start = stime.getText().toString();
                String end = etime.getText().toString();

                performGetTask(GetDataTask.GET_LIST, start, end);
            }
        });
    }

    private void performChangeTask() {
        String ls = lsyz.isChecked() ? "1" : "0";
        String wx = wxbz.isChecked() ? "true" : "false";
        ChangeTask changeTask = new ChangeTask();
        tasks.add(changeTask);
        changeTask.execute(ls, wx);
    }

    private void initCheckButtons() {

        lsyz.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean arg1) {
                if (compoundButton == null || !compoundButton.isPressed()) {
                    //不响应非点击引起的改变
                    return;
                }
                performChangeTask();
            }
        });
        wxbz.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean arg1) {
                if (compoundButton == null || !compoundButton.isPressed()) {
                    //不响应非点击引起的改变
                    return;
                }
                performChangeTask();
            }
        });
    }

    private void initActionBar() {

        actionBar.setTitle("医嘱查询");
        actionBar.setPatient(mAppApplication.sickPersonVo.XSCH + mAppApplication.sickPersonVo.BRXM);
        actionBar.addAction(new Action() {
            @Override
            public void performAction(View view) {
                if (choseViewLay.getVisibility() == View.GONE) {
                    choseViewLay.setVisibility(View.VISIBLE);
                } else {
                    choseViewLay.setVisibility(View.GONE);
                }
            }

            @Override
            public String getText() {
                return "筛选";
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_more_horiz_black_24dp;
            }
        });


    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_advice_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findView();

        toRefreshData();
    }


    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected void toRefreshData() {
        String start = stime.getText().toString();
        String end = etime.getText().toString();

        performGetTask(GetDataTask.GET_LIST, start, end);
    }

    /**
     * 异步加载
     */
    private class GetDataTask extends AsyncTask<String, Void, Response<AdviceData>> {

        public static final byte GET_LIST = 0;

        public static final byte GET_DETAIL = 1;

        private byte mRequestType = GET_LIST;

        public GetDataTask(byte rqtType) {
            this.mRequestType = rqtType;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();

        }

        /**
         * 传入时间
         */
        @Override
        protected Response<AdviceData> doInBackground(String... params) {


            String jgid = mAppApplication.jgId;

            switch (mRequestType) {
                case GET_LIST:

                    if (mAppApplication.sickPersonVo == null) {
                        return null;
                    }

                    String ZYH = mAppApplication.sickPersonVo.ZYH;
                    String start = params[0];
                    String end = params[1];

                    return AdviceApi.getInstance(getApplicationContext())
                            .GetAdviceList(ZYH, -1, -1, start, end, jgid);
                case GET_DETAIL:
                    if (params == null || params.length < 1) {
                        return null;
                    }
                    String jlxh = params[0];
                    return AdviceApi.getInstance(getApplicationContext())
                            .GetAdviceDetil(jlxh, jgid);
                default:
            }
            return null;

        }

        @SuppressWarnings({"unchecked", "deprecation"})
        @Override
        protected void onPostExecute(Response<AdviceData> result) {
            super.onPostExecute(result);
            tasks.remove(this);
            hideSwipeRefreshLayout();
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(AdviceListActivity.this, mAppApplication).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    switch (mRequestType) {
                        case GET_LIST:
                            list = result.Data.AdviceVoList;

                            AdviceListAdapter adapter = new AdviceListAdapter(
                                    getApplicationContext(), list);
                            listView.setAdapter(adapter);
                            performChangeTask();
                            break;
                        case GET_DETAIL:
                            ArrayList<AdviceDetail> list = result.Data.DetailList;
                            if (null != list) {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("list", list);
                                showCreateDialogCompat(DIALOG_DETAIL, bundle);
                            } else {

                                showMsgAndVoiceAndVibrator("列表为空");
                            }
                            break;
                        default:
                    }
                } else {
                    showMsgAndVoice(result.Msg);
                    return;
                }
            } else {
                showMsgAndVoiceAndVibrator("加载失败");
                return;
            }
        }
    }

    protected void showCreateDialogCompat(int id, Bundle args) {

        switch (id) {

            case DIALOG_DETAIL:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                Context context = mContext;
                LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_root_linear, null, false);
                ListView listView = new ListView(context);

                linearLayout.addView(listView);
                EmptyViewHelper.setEmptyView(listView, "listView");
                @SuppressWarnings("unchecked")
                ArrayList<AdviceDetail> list = (ArrayList<AdviceDetail>) args
                        .getSerializable("list");
                View txt = ViewBuildHelper.buildDialogTitleTextView(mContext, "执行详情");
                builder.setView(linearLayout)
                        // .setTitle("执行详情")
                        .setCustomTitle(txt);


                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AdviceDetailAdapter adapter = new AdviceDetailAdapter(this, list);
                listView.setAdapter(adapter);
                builder.create().show();
                break;
            default:

        }

    }

    /**
     * 异步加载
     */
    private class ChangeTask extends AsyncTask<String, Void, ArrayList<AdviceVo>> {

        int ls = 0;
        String wx = "false";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected ArrayList<AdviceVo> doInBackground(String... params) {
            ls = Integer.valueOf(params[0]);
            wx = params[1];
            ArrayList<AdviceVo> datas = new ArrayList<AdviceVo>();
            if (list == null) {
                list = datas;
            }
            for (AdviceVo vo : list) {
                if (vo.LSYZ == ls && wx.equals(vo.WXBZ)) {
                    datas.add(vo);
                }
            }
            return datas;
        }

        @Override
        protected void onPostExecute(ArrayList<AdviceVo> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (null != result) {

                AdviceListAdapter adapter = new AdviceListAdapter(
                        getApplicationContext(), result);
                listView.setAdapter(adapter);
            } else {

                showMsgAndVoiceAndVibrator("加载失败");
            }

        }
    }

    // @Override
    // public void initBarBroadcast() {
    // barBroadcast = new BroadcastReceiver() {
    // @Override
    // public void onReceive(Context arg0, Intent intent) {
    // if (IBarCode.Refresh.equals(intent.getAction())) {
    // performGetTask(GetDataTask.GET_LIST);
    // }
    // }
    // };
    // }

    public class AdviceListAdapter extends BaseAdapter implements
            OnClickListener {

        private ArrayList<AdviceVo> list;
        private LayoutInflater inflater;
        /**
         * 记录Item是否选中
         */
        SparseBooleanArray map = new SparseBooleanArray();

        /**
         * 颜色分组
         */
        SparseIntArray groupArray = new SparseIntArray();

        public AdviceListAdapter(Context context, ArrayList<AdviceVo> list) {
            this.list = list;
            inflater = LayoutInflater.from(context);
            init(list);
        }

        private void init(ArrayList<AdviceVo> list) {

            if (list == null) {
                return;
            }

            for (int i = 0; i < list.size(); i++) {
                map.put(i, false);
                if (i == 0) {
                    groupArray.put(i, 1);
                } else {
                    if (list.get(i).YZZH.equals(list.get(i - 1).YZZH)) {
                        groupArray.put(i, groupArray.get(i - 1));
                    } else {
                        if (groupArray.get(i - 1) == 1) {
                            groupArray.put(i, 0);
                        } else {
                            groupArray.put(i, 1);
                        }
                    }
                }
            }
        }

        @Override
        public int getCount() {
            return list != null ? list.size() : 0;
        }

        @Override
        public AdviceVo getItem(int arg0) {
            return list.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            final ViewHolder vHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_list_advice_list, parent, false);
                vHolder = new ViewHolder();
                vHolder.view = convertView.findViewById(R.id.view);
                vHolder.boxView = convertView.findViewById(R.id.boxView);
                vHolder.YZMC = (TextView) convertView.findViewById(R.id.YZMC);
                vHolder.YCJLMS = (TextView) convertView
                        .findViewById(R.id.YCJLMS);
                vHolder.YCSLMS = (TextView) convertView
                        .findViewById(R.id.YCSLMS);
                vHolder.YPYFMC = (TextView) convertView
                        .findViewById(R.id.YPYFMC);
                vHolder.SYPCMC = (TextView) convertView
                        .findViewById(R.id.SYPCMC);
                vHolder.KSSJ = (TextView) convertView.findViewById(R.id.KSSJ);
                vHolder.TZSJ = (TextView) convertView.findViewById(R.id.TZSJ);
                vHolder.BZXX = (TextView) convertView.findViewById(R.id.BZXX);
                vHolder.arrowImg = (ImageView) convertView
                        .findViewById(R.id.advice_arrow_img);
                vHolder.mVisitBtn = (ImageView) convertView
                        .findViewById(R.id.advice_detail_btn);

                vHolder.mVisitBtn.setOnClickListener(this);

                vHolder.view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        if (vHolder.boxView.getVisibility() == View.GONE) {
                            map.put(position, true);
                            vHolder.boxView.setVisibility(View.VISIBLE);
                            vHolder.arrowImg
                                    .setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                        } else {
                            map.put(position, false);
                            vHolder.arrowImg
                                    .setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                            vHolder.boxView.setVisibility(View.GONE);
                        }
                    }
                });

                convertView.setTag(vHolder);
            } else {
                vHolder = (ViewHolder) convertView.getTag();
            }

            AdviceVo vo = list.get(position);
            vHolder.YZMC.setText(vo.YZMC);
            vHolder.YCJLMS.setText(StringUtil.getText("剂量：", vo.YCJLMS));
            vHolder.YCSLMS.setText(StringUtil.getText("数量：", vo.YCSLMS));
            vHolder.YPYFMC.setText(StringUtil.getText("用法：", vo.YPYFMC));
            vHolder.SYPCMC.setText(StringUtil.getText("频次：", vo.SYPCMC));
            Date date = DateUtil.getDateCompat(vo.KZSJ);
            String dateStr = DateUtil.format_yyyyMMdd_HHmm.format(date);
            vHolder.KSSJ.setText(StringUtil.getText("开始时间：", dateStr));
            if (vo.TZSJ != null) {
                date = DateUtil.getDateCompat(vo.TZSJ);
                dateStr = DateUtil.format_yyyyMMdd_HHmm.format(date);
                vHolder.TZSJ.setText(StringUtil.getText("停嘱时间：", dateStr));
            }else{
                vHolder.TZSJ.setText("停嘱时间：");
            }
            vHolder.BZXX.setText(StringUtil.getText("备注：", vo.BZXX));

            if (map.get(position)) {

                vHolder.boxView.setVisibility(View.VISIBLE);
            } else {
                vHolder.boxView.setVisibility(View.GONE);
            }

            if (groupArray.get(position) == 1) {
                vHolder.view.setBackgroundResource(R.color.classicViewBg);
                vHolder.boxView.setBackgroundResource(R.color.classicViewBg);
            } else {
                vHolder.view.setBackgroundResource(R.color.white);
                vHolder.boxView.setBackgroundResource(R.color.white);
            }

            vHolder.mVisitBtn.setTag(vo.JLXH);
            return convertView;
        }

        class ViewHolder {
            public View view, boxView;
            public TextView YZMC, YCJLMS, YCSLMS, YPYFMC, SYPCMC, KSSJ, TZSJ,
                    BZXX;
            public ImageView arrowImg;
            public ImageView mVisitBtn;
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.advice_detail_btn) {
                String jlxh = (String) v.getTag();

                performGetTask(GetDataTask.GET_DETAIL, jlxh);
            }
        }

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
