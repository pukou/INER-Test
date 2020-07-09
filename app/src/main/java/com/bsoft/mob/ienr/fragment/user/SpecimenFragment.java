package com.bsoft.mob.ienr.fragment.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.UserModelActivity;
import com.bsoft.mob.ienr.adapter.SpecimenListAdapter;
import com.bsoft.mob.ienr.api.InspectionApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.inspection.CYInfoBean;
import com.bsoft.mob.ienr.model.inspection.SpecimenVo;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.SpecimenUtil;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;
import com.classichu.dialogview.listener.OnBtnClickListener;
import com.classichu.dialogview.manager.DialogManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-11 上午11:20:14
 * @类说明 标本采集
 */
public class SpecimenFragment extends BaseUserFragment {


    private  ListView mListView;

    private  SpecimenListAdapter adapter;

    protected Spinner mSpinner;

    private   View sltStimeView;
    private  View sltEtimeView;

    private  TextView stime;
    private  TextView etime;

    private ImageView searchBtn;

    private  View timeView;

    private  BarcodeEntity barinfo; // 快速扫描时传入，待获取数据后，执行
    private  SpecimenVo item;

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }
    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_specimen;
    }

    @Override
    protected void initView(View mainView, Bundle savedInstanceState) {

        mListView = (ListView) mainView
                .findViewById(R.id.id_lv);

        EmptyViewHelper.setEmptyView(mListView,"mListView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout,mListView);

        SpinnerLayout spinnerLayout = (SpinnerLayout) mainView.findViewById(R.id.id_spinner_layout);
        mSpinner = spinnerLayout.getSpinner();

        sltStimeView = mainView.findViewById(R.id.slt_stime_ly);
        sltEtimeView = mainView.findViewById(R.id.slt_etime_ly);

        stime = (TextView) mainView.findViewById(R.id.stime);
        etime = (TextView) mainView.findViewById(R.id.etime);

        TextView  stimeTitle = (TextView) mainView.findViewById(R.id.stime_title);
        TextView etimeTitle = (TextView) mainView.findViewById(R.id.etime_title);
        stimeTitle.setText(R.string.start_time);
        etimeTitle.setText(R.string.end_time);

        searchBtn = (ImageView) mainView.findViewById(R.id.search);

        timeView = mainView.findViewById(R.id.id_layout_double_time);

        initSpinner();
        initListView();
        initActionBar();

        initBarBroadCast();
        initSearchBtn();
        initTime();

    }


    private void initTime() {

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

    public OnClickListener onClickListener = new OnClickListener() {

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

    private void initTimeTxt(String dateStr, int viewId) {
        String timeStr = dateStr;
        if (viewId == R.id.slt_etime_ly) {
            etime.setText(timeStr);
        } else if (viewId == R.id.slt_stime_ly) {
            stime.setText(timeStr);
        }

    }

    @Override
    public void onDateSet(int year, int monthOfYear, int dayOfMonth, int viewId) {
        String nowDate = DateTimeFactory.getInstance().ymd2Date(year, monthOfYear, dayOfMonth);
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

    @Override
    protected void toRefreshData() {
        queryWithAction(mSpinner.getSelectedItemPosition());
    }

    private void initSearchBtn() {

        searchBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toRefreshData();
            }
        });
    }


    private void initActionBar() {

        actionBar.setTitle("标本采集");
        actionBar.setPatient(application.sickPersonVo.XSCH + application.sickPersonVo.BRXM);
    }

    @Override
    public void onStart() {
        super.onStart();
        showSnack(R.string.long_save_tip);
    }

    private void performGetTask(Byte type) {
        String typeStr = String.valueOf(type);
        String start = stime.getText().toString();
        String end = etime.getText().toString();

        performGetTaskInner(typeStr, start, end);
    }

    private void performGetTaskInner(String... params) {
        GetHttpTask getHttpTask = new GetHttpTask();
        tasks.add(getHttpTask);
        getHttpTask.execute(params);
    }

    private void performActionTask(byte type, String... params) {
        ActionSpecimenTask task = new ActionSpecimenTask(type);
        tasks.add(task);
        task.execute(params);
    }

    private void initListView() {

        adapter = new SpecimenListAdapter(getActivity(), null);
        mListView.setAdapter(adapter);
        mListView.setOnItemLongClickListener(
                new OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,
                                                   View view, int position, long id) {

                        item = adapter.getItem(position);
                        if (item != null) {

                            actionWithType(item, false);
                            return true;
                        }
                        return false;
                    }
                });


    }

    private void initSpinner() {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.speciment_actions_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(onOSListener);
        /*
        升级编号【56010012】============================================= start
        打开默认选中‘待采集列表’;
        ================= Classichu 2017/10/16 15:31
        */
        //选中【待采集列表】
        if (mSpinner.getCount() > 1) {
            mSpinner.setSelection(1, true);
        }
        /* =============================================================== end */
    }

    private OnItemSelectedListener onOSListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {

            toRefreshData();

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    };

    private void queryWithAction(int position) {

        if (position == 2) {// 已执行
            timeView.setVisibility(View.VISIBLE);
            performGetTask(GetHttpTask.GET_ALREALY_ACTION_SPECIMENT);
        } else if (position == 0) {// 待发放
            timeView.setVisibility(View.GONE);
            performGetTask(GetHttpTask.GET_WAIT_DELIEVERY_SPECIMENT);
        } else if (position == 1) {// 待采集
            timeView.setVisibility(View.GONE);
            performGetTask(GetHttpTask.GET_WAIT_ACTION_SPECIMENT);
        }

    }

    /**
     * 根据当前状态，执行相应操作
     *
     * @param item
     */
    private void actionWithType(SpecimenVo item, boolean isScan) {

        if (item == null) {
            return;
        }
        if (item.FFZT != -1) { // 执行和发放

            int positon = mSpinner.getSelectedItemPosition();
            if (positon == 0) {
                performActionTask(ActionSpecimenTask.ACTION_DELIVERY_SPECIMENT,
                        item.TMBH, String.valueOf(isScan));
            } else if (positon == 1) {
                performActionTask(ActionSpecimenTask.ACTION_EXECUTE_SPECIMENT,
                        item.TMBH, String.valueOf(isScan));
            }
        } else { // 取消
            DialogManager.showClassicDialog(mFragmentActivity, "", "是否确认取消?", new OnBtnClickListener() {
                @Override
                public void onBtnClickOk(DialogInterface dialogInterface) {
                    //
                    performActionTask(ActionSpecimenTask.ACTION_CANCEL_SPECIMENT,
                            item.TMBH);
                    //
                }
            });


        }
    }

    private void initBarBroadCast() {

        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {

                String action = intent.getAction();
                if (BarcodeActions.Refresh.equals(action)) {
                    sendUserName();
                    actionBar.setPatient(mAppApplication.sickPersonVo.XSCH
                            + mAppApplication.sickPersonVo.BRXM);
                    toRefreshData();
                } else if (BarcodeActions.Bar_Get.equals(action)) {

                    BarcodeEntity entity = (BarcodeEntity) intent
                            .getParcelableExtra("barinfo");

                    if (entity.TMFL == 2 && entity.FLBS == 2) {
                        scanAction(entity.source);
                    } else if (FastSwitchUtils.needFastSwitch(entity)) {
                        FastSwitchUtils.fastSwith(
                                (UserModelActivity) getActivity(), entity);
                    }
                }
            }
        };
    }

    protected void scanAction(String barcode) {

        if (adapter == null) {
            return;
        }
 /*
            升级编号【56010013】============================================= start
            标本采集：是否需求转换条码:需要加入参数控制：是否需求转换条码
            ================= Classichu 2017/10/18 9:34
            */
        String tmbh = SpecimenUtil.getTmbh(getActivity(), barcode,
                DateTimeHelper.getServerDate());
           /* =============================================================== end */
        if (tmbh == null) {
            /*VibratorUtil.vibratorMsg(
                    mAppApplication.getSettingConfig().vib,
                    "转换条码编号失败", getActivity());*/
            showMsgAndVoiceAndVibrator("转换条码编号失败");
            return;
        }
        SpecimenVo vo = adapter.contain(tmbh);
        if (null != vo) {
            actionWithType(vo, true);
        } else {
            /*VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "条码不在此列表中", getActivity());*/
//         ############   showMsgAndVoice("条码不在此列表中");
            doActionQueryTask(tmbh);
        }
    }

    class GetHttpTask extends AsyncTask<String, Void, Response<List<SpecimenVo>>> {

        /**
         * 待采集
         */
        public static final byte GET_WAIT_ACTION_SPECIMENT = 0;

        /**
         * 已执行
         */
        public static final byte GET_ALREALY_ACTION_SPECIMENT = GET_WAIT_ACTION_SPECIMENT + 1;

        /**
         * 待发放
         */
        public static final byte GET_WAIT_DELIEVERY_SPECIMENT = GET_ALREALY_ACTION_SPECIMENT + 1;

        private byte mType = GET_WAIT_ACTION_SPECIMENT;

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<SpecimenVo>> doInBackground(String... params) {

            if (params == null || params.length < 1 || params[0] == null) {

                return null;
            }

            if (mAppApplication.sickPersonVo == null) {
                return null;
            }

            mType = Byte.valueOf(params[0]);
            String start = params[1];
            String end = params[2];

            String jgid = mAppApplication.jgId;
            String zyh = mAppApplication.sickPersonVo.ZYH;
            InspectionApi api = InspectionApi.getInstance(getActivity());

            switch (mType) {
                case GET_WAIT_DELIEVERY_SPECIMENT:
                case GET_WAIT_ACTION_SPECIMENT:
                    Response<List<SpecimenVo>> response = api.GetSpecimenList(zyh, jgid,
                            Constant.sysType);
                    return response;
                case GET_ALREALY_ACTION_SPECIMENT:

                    response = api.GetHistorySpecimenList(zyh, start, end, jgid,
                            Constant.sysType);
                    return response;
                default:
            }
            return null;
        }

        @Override
        protected void onPostExecute(Response<List<SpecimenVo>> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();
            if (adapter != null) {
                adapter.clearData();
            } else {
                adapter = new SpecimenListAdapter(getActivity(), null);
                mListView.setAdapter(adapter);
            }

            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：请求参数错误");
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

                @SuppressWarnings("unchecked")
                ArrayList<SpecimenVo> tList = (ArrayList<SpecimenVo>) result.Data;

                if (mType == GET_WAIT_DELIEVERY_SPECIMENT) {
                    tList = filterListWithFFZT(tList);
                }
                if (null != tList && tList.size() > 0) {
                    adapter.addData(tList);
                    if (barinfo != null) {
                        scanAction(barinfo.TMNR);
                        barinfo = null;
                    }
                }
            } else {showTipDialog(result.Msg);
//                AlertBox.Show(getActivity(), getString(R.string.project_tips), result.Msg, getString(R.string.project_operate_ok));
            }

        }
    }

    /**
     * 过滤列表，选择状态为待发放项目
     *
     * @param tList
     * @return
     */
    private ArrayList<SpecimenVo> filterListWithFFZT(ArrayList<SpecimenVo> tList) {

        if (tList == null) {
            return null;
        }
        ArrayList<SpecimenVo> result = new ArrayList<SpecimenVo>();

        for (SpecimenVo item : tList) {
            // 待发放
            if (item.FFZT == 0) {
                result.add(item);
            }
        }
        return result;
    }

    class ActionSpecimenTask extends AsyncTask<String, Void, Response<String>> {

        /**
         * 执行
         */
        public static final byte ACTION_EXECUTE_SPECIMENT = 1;

        /**
         * 取消
         */
        public static final byte ACTION_CANCEL_SPECIMENT = ACTION_EXECUTE_SPECIMENT + 1;

        /**
         * 发放
         */
        public static final byte ACTION_DELIVERY_SPECIMENT = ACTION_CANCEL_SPECIMENT + 1;

        private byte actionType;

        public ActionSpecimenTask(byte actionType) {

            this.actionType = actionType;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(getResources().getString(R.string.doing));
        }

        @Override
        protected Response<String> doInBackground(String... params) {

            if (mAppApplication.sickPersonVo == null || mAppApplication.user == null) {
                return null;
            }

            String jgid = mAppApplication.jgId;
            String zyh = mAppApplication.sickPersonVo.ZYH;
            String urid = mAppApplication.user.YHID;

            InspectionApi api = InspectionApi.getInstance(getActivity());

            switch (actionType) {
                case ACTION_EXECUTE_SPECIMENT:
                    if (params == null || params.length < 2) {
                        return null;
                    }
                    String tmbh = params[0];
                    try {
                        // url 编码
                        tmbh = URLEncoder.encode(tmbh, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String isScan = params[1];
                    return api.ExecuteSpecimen(zyh, urid, tmbh, isScan,mAppApplication.getSerialNumber(), jgid,
                            Constant.sysType);

                case ACTION_CANCEL_SPECIMENT:
                    if (params == null || params.length < 1) {
                        return null;
                    }
                    tmbh = params[0];
                    try {
                        // url 编码
                        tmbh = URLEncoder.encode(tmbh, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return api
                            .CancelSpecimen(urid, tmbh, zyh,mAppApplication.getSerialNumber(), jgid, Constant.sysType);
                case ACTION_DELIVERY_SPECIMENT:
                    if (params == null || params.length < 2) {
                        return null;
                    }
                    tmbh = params[0];
                    try {
                        // url 编码
                        tmbh = URLEncoder.encode(tmbh, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    isScan = params[1];
                    return api.Delivery(zyh, urid, tmbh, isScan, jgid,
                            Constant.sysType);
                default:

            }

            return null;

        }

        @Override
        protected void onPostExecute(Response<String> result) {
            super.onPostExecute(result);

            hideLoadingDialog();

            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：请求参数错误");
                return;
            }
           // MediaUtil.getInstance(getActivity()).playSound(result.ReType == 0 ? R.raw.success : R.raw.wrong, getActivity());
            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        actionWithType(item, false);
                    }
                }).showLoginDialog();
                return;
            } else if (result.ReType == 0) {
                // 提示完刷新
                showMsgAndVoice("操作成功");
                toRefreshData();
            } else {showTipDialog(result.Msg);
//                AlertBox.Show(getActivity(), getString(R.string.project_tips), result.Msg, getString(R.string.project_operate_ok));
            }

        }
    }

   /* public void toastInfo(String msg, Style style) {
        showSnack(msg);
        *//*Crouton.makeText(getActivity(), msg, style, R.id.actionbar)
                .show();*//*
    }*/

    public  void doActionQueryTask(String tmbh){
        ActionQueryTask actionQueryTask = new ActionQueryTask();
        tasks.add(actionQueryTask);
        actionQueryTask.execute(tmbh);

    }
    class ActionQueryTask extends AsyncTask<String, String, Response<List<CYInfoBean>>> {
        String tmbh;

        @Override
        protected Response<List<CYInfoBean>> doInBackground(String... params) {
            String jgid = mAppApplication.jgId;
            String bqid = mAppApplication.getAreaId();
            String zyh = mAppApplication.sickPersonVo.ZYH;
            String brid = mAppApplication.sickPersonVo.ZYHM;
//            String zyh = person.ZYH;//1119982
//            String zyhm = person.ZYHM;//1004973
            InspectionApi api = InspectionApi.getInstance(getActivity());
            if (params == null || params.length < 1) {
                return null;
            }
            tmbh = params[0];
            try {
                // url 编码
                tmbh = URLEncoder.encode(tmbh, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return api.GetCYInfoByTMBH(brid, tmbh, jgid, Constant.sysType);

        }

        @Override
        protected void onPostExecute(Response<List<CYInfoBean>> result) {

            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：请求参数错误");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        doActionQueryTask(tmbh);
                    }
                }).showLoginDialog();
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            if (result.ReType == 0) {
                if (result.Data != null && !result.Data.isEmpty()) {
                    for (CYInfoBean datum : result.Data) {
                        if (!TextUtils.isEmpty(datum.CYRQ)
                                && !TextUtils.isEmpty(datum.CYR) && !TextUtils.isEmpty(datum.CYBZ)) {
                        /*    stringBuilder.append("条码: ");
                            stringBuilder.append(tmbh);
                            stringBuilder.append(" 不在当前列表中 ");
                            stringBuilder.append(datum.CYR);
                            stringBuilder.append(" 于 ");
                            stringBuilder.append(datum.CYRQ);
                            stringBuilder.append(" ");
                            stringBuilder.append("1".equals(datum.CYBZ)?"已采样":"未采样");
                            stringBuilder.append("\n");*/
                            stringBuilder.append("该样本");
                            stringBuilder.append("1".equals(datum.CYBZ)?"已采样":"未采样");
                            stringBuilder.append("采样人:");
                            stringBuilder.append(datum.CYR);
                            stringBuilder.append("采样时间;");
                            stringBuilder.append(datum.CYRQ);
                            stringBuilder.append("\n");
                        }
                    }
                }
            }else{
                showMsgAndVoiceAndVibrator("获取信息失败");
                return;
            }
            //
            String sxxx = stringBuilder.toString();
            if (!TextUtils.isEmpty(sxxx)){
                showTipDialog(sxxx);
            }else{
                showMsgAndVoiceAndVibrator("条码不在当前列表中");
            }

        }
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey("barinfo")) {
            BarcodeEntity entity = (BarcodeEntity) args
                    .getParcelable("barinfo");
            if (entity.TMFL == 2 && entity.FLBS == 2) {
                barinfo = entity;
            }
        }
    }
}
