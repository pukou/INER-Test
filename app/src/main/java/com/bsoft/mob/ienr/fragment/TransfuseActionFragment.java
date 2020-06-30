package com.bsoft.mob.ienr.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog.Builder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.execut.SYExecutActivity;
import com.bsoft.mob.ienr.adapter.PersonAdapter;
import com.bsoft.mob.ienr.adapter.SickPersonAdviceAdapter;
import com.bsoft.mob.ienr.api.AdviceApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeFormat;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.fragment.base.LeftMenuItemFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.ListViewScrollHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.helper.TestDataHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.Statue;
import com.bsoft.mob.ienr.model.advice.AdvicePlanData;
import com.bsoft.mob.ienr.model.advice.AdvicePlanVo;
import com.bsoft.mob.ienr.model.advice.AdviceRefuseReasonVo;
import com.bsoft.mob.ienr.model.advice.AdviceUtils;
import com.bsoft.mob.ienr.model.advice.execut.ExecutVo;
import com.bsoft.mob.ienr.model.advice.execut.ExecutVo.ExecutType;
import com.bsoft.mob.ienr.model.advice.execut.PlanArgInfo;
import com.bsoft.mob.ienr.model.advice.execut.REModel;
import com.bsoft.mob.ienr.model.advice.execut.RequestBodyInfo;
import com.bsoft.mob.ienr.model.advice.execut.SJModel;
import com.bsoft.mob.ienr.model.advice.execut.SQModel;
import com.bsoft.mob.ienr.model.advice.execut.SYZTModel;
import com.bsoft.mob.ienr.model.kernel.SickPersonVo;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.StringUtil;
import com.bsoft.mob.ienr.util.VibratorUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;
import com.classichu.dialogview.helper.DialogFragmentShowHelper;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 输液单 Created by hy on 14-3-24.
 */
public class TransfuseActionFragment extends LeftMenuItemFragment {

    private View sltStimeView;
    private View sltEtimeView;

    private TextView stime;
    private TextView etime;

    private ImageView searchBtn;


    private ListView mPersonListView;

    private ListView mPtrFormListView;

    private SickPersonAdviceAdapter adapter;

    private PersonAdapter pAdatper;

    private BarcodeEntity barinfo;

    private List<AdvicePlanVo> planList;

    private List<AdviceRefuseReasonVo> reasonList;


    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_transfuse_action;
    }

    @Override
    protected void initView(View root, Bundle savedInstanceState) {
        sltStimeView = root.findViewById(R.id.slt_stime_ly);
        sltEtimeView = root.findViewById(R.id.slt_etime_ly);

        stime = (TextView) root.findViewById(R.id.stime);
        etime = (TextView) root.findViewById(R.id.etime);
      /*  CheckBox image = (CheckBox) root.findViewById(R.id.image);
        Drawable btnDrawable= VectorOrImageResHelper.getDrawable(root.getContext(),R.drawable.selector_classic_item_expand);
        btnDrawable.setColorFilter(ContextCompat.getColor(root.getContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        image.setButtonDrawable(btnDrawable);*/
        searchBtn = (ImageView) root.findViewById(R.id.search);


        mPersonListView = (ListView) root
                .findViewById(R.id.id_lv);
        mPtrFormListView = (ListView) root
                .findViewById(R.id.id_lv_2);

        EmptyViewHelper.setEmptyView(mPersonListView, "mPersonListView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mPersonListView);
        EmptyViewHelper.setEmptyView(mPtrFormListView, "mPtrFormListView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mPtrFormListView);

        initSearchBtn();
        initActionBar();
        initTime();
        initPersonListView();
        initBroadCast();
        toRefreshData();
    }


    private void initBroadCast() {

        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (BarcodeActions.Refresh.equals(intent.getAction())) {
                    scanSltPerson();
                } else if (BarcodeActions.Bar_Get.equals(intent.getAction())) {

                    BarcodeEntity entity = (BarcodeEntity) intent
                            .getParcelableExtra("barinfo");
                    if (entity.TMFL == 2) {
                        if (entity.FLBS == 4) {
                            barinfo = entity;
                            // 默认扫描执行
                            onSaveAction(ExecutTask.SCAN_EXCUTE, entity.TMNR,
                                    entity.TMQZ, "true");
                        } else if (FastSwitchUtils.needFastSwitch(entity)) {

                            AppApplication app = (AppApplication) getActivity()
                                    .getApplication();
                            if (mAppApplication.sickPersonVo == null) {
                                return;
                            }

                            FastSwitchUtils.fastSwith(getActivity(), entity);
                        }
                    }
                }
            }
        };
    }

    /**
     * 扫描定位病人,并查询医嘱
     */
    public void scanSltPerson() {

        SickPersonVo person = mAppApplication.sickPersonVo;
        if (person == null || pAdatper == null) {
        /*    VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "扫描的病人不在此列表中", getActivity());*/
            showMsgAndVoiceAndVibrator("扫描的病人不在此列表中");
            return;
        }
        int position = pAdatper.getPersonPostion(person.ZYH);
        if (position == -1) {
            /*VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "扫描的病人不在此列表中", getActivity());*/
            showMsgAndVoiceAndVibrator("扫描的病人不在此列表中");
            return;
        }
        ListViewScrollHelper.smoothScrollToPosition(mPersonListView, position);

        mPersonListView.setItemChecked(position,
                true);
        actionHttpTask(person.ZYH);
    }

    private void initPersonListView() {


        mPersonListView.setTextFilterEnabled(true);
        // checked/activated
        mPersonListView.setChoiceMode(
                AbsListView.CHOICE_MODE_SINGLE);

        mPersonListView.setOnItemClickListener(onPersonItemClickListener);
    }

    @Override
    protected void toRefreshData() {
        GetPersonTask task = new GetPersonTask();
        tasks.add(task);
        task.execute();
    }

    public OnItemClickListener onPersonItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            mPersonListView.setItemChecked(position,
                    true);
            onAction();

        }
    };

    private void onAction() {

        int position = mPersonListView
                .getCheckedItemPosition();

        if (position == AdapterView.INVALID_POSITION) {
            /*VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "请选择病人", getActivity());*/
            showMsgAndVoiceAndVibrator("请选择病人");
            return;
        }

        SickPersonVo person = (SickPersonVo) mPersonListView
                .getAdapter().getItem(position);
        if (person == null) {
           /* VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "请选择病人", getActivity());*/
            showMsgAndVoiceAndVibrator("请选择病人");
            return;
        }
        actionHttpTask(person.ZYH);

    }

    void actionHttpTask(String zyh) {

        GetHttpTask task = new GetHttpTask();
        tasks.add(task);
        task.execute(zyh);
    }


    /*
     * 加载医嘱计划
     */
    class GetHttpTask extends AsyncTask<String, Void, Response<AdvicePlanData>> {

        @Override

        protected void onPreExecute() {
            super.onPreExecute();
            // emptyProgress.setVisibility(View.VISIBLE);
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<AdvicePlanData> doInBackground(String... params) {
            if (params == null || params.length < 1) {
                return null;
            }
            String nowDate = DateTimeHelper.getServerDate();
            String zyh = params[0];
            String jgid = mAppApplication.jgId;
            return AdviceApi.getInstance(getActivity()).getPlanList(zyh, nowDate,
                    "4", jgid);
        }

        @Override
        protected void onPostExecute(Response<AdvicePlanData> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();

            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            onAction();
                        }
                    }).showLoginDialog();
                } else if (result.ReType == 0) {
                    AdvicePlanData data = result.Data;

                    ArrayList<AdvicePlanVo> planList = (ArrayList<AdvicePlanVo>) data.PlanInfoList;
                    if (EmptyTool.isEmpty(planList)) {
                        planList = new ArrayList<>();
                        //
                        TestDataHelper.buidTestData(AdvicePlanVo.class, planList);
                    }

                    adapter = new SickPersonAdviceAdapter(getActivity(), planList);
                    mPtrFormListView.setAdapter(adapter);

                } else {
                    showMsgAndVoice(result.Msg);
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
            }
        }
    }


    private void initActionBar() {

        actionBar.setTitle("输液单");
        actionBar.addAction(new Action() {
            @Override
            public String getText() {
                return "执行";
            }

            @Override
            public void performAction(View view) {
                onSaveAction(ExecutTask.HANDLE_EXCUTE, Boolean.toString(false),
                        Boolean.toString(true));
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }
        });
    }

    void onSaveAction(byte mType, String... params) {

        int position = mPersonListView
                .getCheckedItemPosition();

        if (position == AdapterView.INVALID_POSITION) {
         /*   VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "请选择病人", getActivity());*/
            showMsgAndVoiceAndVibrator("请选择病人");
            return;
        }

        SickPersonVo person = (SickPersonVo) mPersonListView
                .getAdapter().getItem(position);
        if (person == null) {
            /*VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "请选择病人", getActivity());*/
            showMsgAndVoiceAndVibrator("请选择病人");
            return;
        }

        params = ArrayUtils.add(params, person.ZYH);
        performExcute(mType, params);
    }

    private void initTime() {

        final TextView stimeTitle = (TextView) sltStimeView
                .findViewById(R.id.stime_title);
        final TextView etimeTitle = (TextView) sltEtimeView
                .findViewById(R.id.etime_title);

        stimeTitle.setText(R.string.start_time);
        etimeTitle.setText(R.string.end_time);

        String nowDateTime = DateTimeHelper.getServerDateTime();
        // 当前时间
        //开始时间和结束时间段根据当前时间自动适应 start01
        String time = DateTimeFactory.getInstance().dateTime2Custom(nowDateTime,DateTimeFormat.HHmm);
        stime.setText(time);

        // 向后四小时
        String tempDateTime = DateTimeHelper.dateTimeAddedHours(nowDateTime, 4);
        String timeEnd = DateTimeFactory.getInstance().dateTime2Custom(tempDateTime,DateTimeFormat.HHmm);
        etime.setText(timeEnd);

        sltStimeView.setOnClickListener(onClickListener);
        sltEtimeView.setOnClickListener(onClickListener);

    }

    public OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            String dateTimeStr = null;
            int id = v.getId();
            if (id == R.id.slt_stime_ly) {
                dateTimeStr = stime.getText().toString();
            } else if (id == R.id.slt_etime_ly) {
                dateTimeStr = etime.getText().toString();
            }
            showTimePickerCompat(dateTimeStr, id);

        }
    };

    @Override
    public void onTimeSet(int hourOfDay, int minute, int viewId) {

        YmdHMs ymdHMs = DateTimeFactory.getInstance().date2Ymd(DateTimeHelper.getServerDate());
        String nowSelectDateTime = DateTimeFactory.getInstance().ymdhms2DateTime(ymdHMs.year, ymdHMs.month, ymdHMs.day,
                hourOfDay, minute, 0);
        if (viewId == R.id.slt_stime_ly) {
            //选择开始时间
            //查看结束时间
            String eStr = etime.getText().toString();
            String nowEndDateTimeHHmm = DateTimeHelper.getServerDate() + " " + eStr;
            String nowEndDateTime = DateTimeFactory.getInstance().custom2DateTime(nowEndDateTimeHHmm, DateTimeFormat.yyyy_MM_dd_HHmm);
            if (DateTimeFactory.getInstance().dateTimeAfter(nowSelectDateTime, nowEndDateTime)) {
                showMsgAndVoiceAndVibrator("开始时间后于结束时间，请重新选择");
                return;
            }
        } else if (viewId == R.id.slt_etime_ly) {
            //选择结束时间
            //查看开始时间
            String sStr = stime.getText().toString();
            String nowStartDateTimeHHmm = DateTimeHelper.getServerDate() + " " + sStr;
            String nowStartDateTime = DateTimeFactory.getInstance().custom2DateTime(nowStartDateTimeHHmm, DateTimeFormat.yyyy_MM_dd_HHmm);
            if (DateTimeFactory.getInstance().dateTimeBefore(nowSelectDateTime, nowStartDateTime)) {
                showMsgAndVoiceAndVibrator("结束时间先于开始时间,请重新选择");
                return;
            }
        }
        String time = DateTimeFactory.getInstance().dateTime2Custom(nowSelectDateTime, DateTimeFormat.HHmm);
        setTimeTxt(time, viewId);
    }

    private void setTimeTxt(String dateTime, int viewId) {
        String timeStr = dateTime;
        if (viewId == R.id.slt_etime_ly) {
            etime.setText(timeStr);
        } else if (viewId == R.id.slt_stime_ly) {
            stime.setText(timeStr);
        }

    }

    private void initSearchBtn() {

        searchBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toRefreshData();
            }
        });
    }

    /**
     * 病人列表异步加载
     */
    private class GetPersonTask extends AsyncTask<Void, Void, Response<ArrayList<SickPersonVo>>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override

        protected Response<ArrayList<SickPersonVo>> doInBackground(Void... params) {

            String areaId = mAppApplication.getAreaId();
            int start = getSeconds(stime.getText().toString());
            int end = getSeconds(etime.getText().toString());
            String jgid = mAppApplication.jgId;
            int sysType = Constant.sysType;
            return AdviceApi.getInstance(getActivity()).GetPatientList(areaId,
                    5, start, end, null, jgid);
        }

        private int getSeconds(String time) {
            String dateTimeHHmm = DateTimeHelper.getServerDate() + " " + time;
            String dateTime = DateTimeFactory.getInstance().custom2DateTime(dateTimeHHmm, DateTimeFormat.yyyy_MM_dd_HHmm);
            YmdHMs ymdHMs = DateTimeHelper.dateTime2YmdHMs(dateTime);
            int seconds = ymdHMs.hour * 60 + ymdHMs.minute;
            return seconds;
        }

        @Override
        protected void onPostExecute(Response<ArrayList<SickPersonVo>> result) {
            super.onPostExecute(result);

            hideSwipeRefreshLayout();

            tasks.remove(this);
            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            toRefreshData();
                        }
                    }).showLoginDialog();
                } else if (result.ReType == 0) {
                    ArrayList<SickPersonVo> list = result.Data;
                    importPersons(list);
                } else {
                    showMsgAndVoice(result.Msg);
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
            }
        }
    }


   /* public void toastInfo(String msg, Style style, int viewGroupId) {
     //   Crouton.showText(getActivity(), msg, style, viewGroupId);
        showSnack(msg);
    }
*/


    private void importPersons(ArrayList<SickPersonVo> list) {

        if (EmptyTool.isEmpty(list)) {
            list = new ArrayList<>();
            //
            TestDataHelper.buidTestData(SickPersonVo.class, list);
        }
        pAdatper = new PersonAdapter(getActivity(), list);
        mPersonListView.setAdapter(pAdatper);

        //adapter  每次左侧重新获取后  右侧数据执行一次清空
        if (planList != null && planList.size() > 0) {
            planList.clear();
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 医嘱执行Task
     *
     * @author hy
     */
    class ExecutTask extends AsyncTask<String, Void, ExecutVo> {

        /**
         * 手动
         */
        public static final byte HANDLE_EXCUTE = 0;

        /**
         * 扫描
         */
        public static final byte SCAN_EXCUTE = HANDLE_EXCUTE + 1;

        /**
         * 输液
         */
        public static final byte TRANSFUSE_EXCUTE = SCAN_EXCUTE + 1;

        /**
         * 继续输液
         */
        public static final byte Transfuse_Continue_EXCUTE = TRANSFUSE_EXCUTE + 1;

        private byte mType = HANDLE_EXCUTE;

        public ExecutTask(byte mType) {
            this.mType = mType;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(R.string.doing);
        }

        @Override
        protected ExecutVo doInBackground(String... params) {

            switch (mType) {
                case HANDLE_EXCUTE:
                    if (params == null || params.length < 2) {
                        return null;
                    }
                    return handleExecute(params[0], params[1], params[2]);
                case SCAN_EXCUTE:
                    if (params == null || params.length < 4) {
                        return null;
                    }
                    return scanExecute(params[0], params[1], params[2], params[3]);

                case TRANSFUSE_EXCUTE:

                    if (params == null || params.length < 3) {
                        return null;
                    }
                    return executeTransfuse(params[0], params[1], params[2]);

                case Transfuse_Continue_EXCUTE:

                    if (params == null || params.length < 3) {
                        return null;
                    }
                    return transfuseContinue(params[0], params[1], params[2]);
            }
            return null;
        }

        /**
         * 继续输液
         *
         * @param sydh
         * @param qzjsStr
         * @return
         */
        private ExecutVo transfuseContinue(String sydh, String qzjsStr,
                                           String zyh) {

            if (EmptyTool.isBlank(sydh) || EmptyTool.isBlank(qzjsStr)
                    || EmptyTool.isBlank(zyh)) {
                return null;
            }
            String realData = "";
            try {
                RequestBodyInfo info = new RequestBodyInfo();
                info.ZYH = zyh;
                info.YHID = mAppApplication.user.YHID;
                info.QRDH = sydh;
                info.QZJS = Boolean.valueOf(qzjsStr);
                info.SYBX = false;
                info.JGID = mAppApplication.jgId;
                realData = JsonUtil.toJson(info);
            } catch (IOException e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                e.printStackTrace();
                return null;
            }

            return AdviceApi.getInstance(getActivity()).TransfuseContinue(realData);

        }

        /**
         * 输液非常规执行(并行接瓶) 多瓶
         *
         * @param SYDH
         * @param QRDH
         * @param zyh
         * @return
         */
        private ExecutVo executeTransfuse(String SYDH, String QRDH, String zyh) {

            if (EmptyTool.isBlank(SYDH) || EmptyTool.isBlank(QRDH)
                    || EmptyTool.isBlank(zyh)) {
                return null;
            }

            if (mAppApplication.user == null) {
                return null;
            }

            String yhid = mAppApplication.user.YHID;
            String jgid = mAppApplication.jgId;

            AdviceApi api = AdviceApi.getInstance(getActivity());
            // ExecutVo executVo = api.TransfuseExecut(zyh,
            // yhid, QRDH, SYDH, true, jgid, Constant.sysType);
            ExecutVo executVo = api.TransfuseExecut(zyh, yhid, QRDH, SYDH,
                    false, jgid, Constant.sysType);

            return executVo;

        }

        /**
         * 扫描执行
         *
         * @param barcode
         * @param prefix
         * @param checkTime
         * @return
         */
        private ExecutVo scanExecute(String barcode, String prefix,
                                     String checkTime, String zyh) {

            if (EmptyTool.isBlank(barcode) || EmptyTool.isBlank(prefix)
                    || EmptyTool.isBlank(checkTime) || EmptyTool.isBlank(zyh)) {
                return null;
            }

            if (mAppApplication.user == null) {
                return null;
            }

            String data = "";
            try {
                RequestBodyInfo info = new RequestBodyInfo();
                info.ZYH = zyh;
                info.YHID = mAppApplication.user.YHID;
                info.TMNR = barcode;
                info.TMQZ = prefix;
                info.SYBX = false;
                info.JYSJ = Boolean.valueOf(checkTime);
                info.JGID = mAppApplication.jgId;
                data = JsonUtil.toJson(info);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AdviceApi.getInstance(getActivity()).ScanExecut(data);

        }

        /**
         * 手动执行
         *
         * @param transfuseBXStr 是否多瓶
         * @param checkTimeStr
         * @return
         */
        private ExecutVo handleExecute(String transfuseBXStr,
                                       String checkTimeStr, String zyh) {

            if (mAppApplication.user == null) {
                return null;
            }
            String yhid = mAppApplication.user.YHID;
            String jgid = mAppApplication.jgId;

            boolean transfuseBX = Boolean.valueOf(transfuseBXStr);
            boolean checkTime = Boolean.valueOf(checkTimeStr);

            ArrayList<AdvicePlanVo> data = adapter.getValue();
            if (null != data && data.size() > 0) {
                if (adapter.isOneType(data)) {
                    List<PlanArgInfo> planArgInfoList = new ArrayList<>();
                    for (AdvicePlanVo vo : data) {
                        PlanArgInfo info = new PlanArgInfo();
                        info.JHH = vo.JHH;
                        planArgInfoList.add(info);
                    }
                    String realData = "";
                    try {
                        RequestBodyInfo info = new RequestBodyInfo();
                        info.ZYH = zyh;
                        info.YHID = yhid;
                        info.SYBX = transfuseBX;
                        info.JYSJ = checkTime;
                        info.PlanArgInfoList = planArgInfoList;
                        info.JGID = jgid;
                        realData = JsonUtil.toJson(info);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return AdviceApi.getInstance(getActivity()).HandExecut(realData);
                } else {
                    return new ExecutVo(Statue.Special);
                }
            } else {
                return new ExecutVo(Statue.NO_Chose);
            }
        }

        @Override
        protected void onPostExecute(final ExecutVo result) {
            super.onPostExecute(result);
            hideLoadingDialog();
            tasks.remove(this);

            if (null == result) {
                showMsgAndVoiceAndVibrator("请求失败：参数错误");
                return;
            }
            if (result.statue == Statue.NO_Chose) {
                boolean vib = mAppApplication.getSettingConfig().vib;
                //  VibratorUtil.vibratorMsg(vib, "你没有选中啊", getActivity());
                showMsgAndVoiceAndVibrator("您没有选中");
                return;
            }
            if (result.statue == Statue.Special) {
                boolean vib = mAppApplication.getSettingConfig().vib;
                //  VibratorUtil.vibratorMsg(vib, "选择的不是同一类型", getActivity());
                showMsgAndVoiceAndVibrator("选择的不是同一类型");
                return;
            }
            mPtrFormListView.postDelayed(new Runnable() {

                @Override
                public void run() {
                    toDo(result);
                }
            }, 500);

        }
    }


    // 处理执行结果
    public void toDo(final ExecutVo result) {

        // 判断是否要显示Dialog
        if (null != result && result.isOK()
                && ExecutType.RE == result.executType) {
            boolean isShow = false;
            for (int i = 0; i < result.size(); i++) {
                REModel vo = (REModel) result.get(i);
                if (vo.YCLX != 0) {
                    isShow = true;
                    break;
                }
            }
            if (!isShow) {
                showMsgAndVoice("执行成功");

              /*  MediaUtil.getInstance(getActivity()).playSound(R.raw.success,
                        getActivity());*/
                // 执行成功刷新
                onAction();
                return;
            }
        }
       /* MediaUtil.getInstance(getActivity()).playSound(R.raw.wrong,
                getActivity());*/
        showAdviceDialog(result);

    }

    protected void showAdviceDialog(ExecutVo result) {

        AdviceDialogFragment newFragment = AdviceDialogFragment.newInstance(result,
                new WeakReference<>(mContext), new AdviceDialogFragment.ClickListener() {
                    @Override
                    public void saveActionSYZTContinue() {
                        onSaveAction(
                                ExecutTask.Transfuse_Continue_EXCUTE,
                                ((SYZTModel) result.get(0)).QRDH,
                                Boolean.toString(false));
                    }

                    @Override
                    public void saveActionSYZTCancel() {
                        onSaveAction(
                                ExecutTask.Transfuse_Continue_EXCUTE,
                                ((SYZTModel) result.get(0)).QRDH,
                                Boolean.toString(true));
                    }

                    @Override
                    public void action() {
                        onAction();
                    }

                    @Override
                    public void sy() {
                        startSYActivity(result.list);
                    }

                    @Override
                    public void sj() {
                        if (null != barinfo) {
                            onSaveAction(ExecutTask.SCAN_EXCUTE,
                                    barinfo.TMNR, barinfo.TMQZ, "false");
                        }
                    }

                });
      /*  try {
            getFragmentManager().beginTransaction()
                    .add(newFragment, "AdviceDialogFragment")
                    .commitAllowingStateLoss();
        } catch (Exception ex) {
            Log.e(Constant.TAG, ex.getMessage(), ex);
        }*/
        DialogFragmentShowHelper.show(getChildFragmentManager(), newFragment, "AdviceDialogFragment");

    }

    /**
     * @author xinggj E-mail:sixtynine@live.cn
     * @version Time：2014年2月28日 上午11:12:04 类说明
     */
    public static class AdviceDialogFragment extends DialogFragment {

        public static final String TAG = "AdviceDialogFragment";

        protected static ExecutVo result;
        protected static WeakReference<Context> sContextWeakReference;
        private static ClickListener mClickListener;

        public interface ClickListener {
            void saveActionSYZTContinue();

            void saveActionSYZTCancel();

            void action();

            void sj();

            void sy();
        }

        /**
         * 用于显示执行结果
         *
         * @param resultT
         */
        public static AdviceDialogFragment newInstance(ExecutVo resultT, WeakReference<Context> weakReference, ClickListener clickListener) {
            AdviceDialogFragment adviceDialogFragment = new AdviceDialogFragment();
            Bundle args = new Bundle();
            adviceDialogFragment.setArguments(args);
            //
            result = resultT;
            sContextWeakReference = weakReference;
            mClickListener = clickListener;
            return adviceDialogFragment;
        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            if (result != null) {
                // return getResultDialog(result);

                Dialog dialog = getResultDialog(result);
                if (null != dialog) {
                    return dialog;
                } else {
                    dismiss();
                }
            }
            return super.onCreateDialog(savedInstanceState);
        }

        private Dialog getResultDialog(final ExecutVo result) {

            boolean vib = AppApplication.getInstance().getSettingConfig().vib;

            if (null == result) {
                VibratorUtil.vibrator(getActivity(), vib);
                return show(getActivity(), getString(R.string.project_tips), "失败");
            }

            if (!result.isOK()) {
                VibratorUtil.vibrator(getActivity(), vib);
                return showAlert(getActivity());
            }

            // 成功

            Builder builder = null;
            StringBuffer buf = null;
            switch (result.executType) {
                case RE:
                    buf = new StringBuffer();
                    for (int i = 0; i < result.size(); i++) {
                        REModel vo = (REModel) result.get(i);
                        System.out.println("vo.YCLX  :  " + vo.YCLX);
                        if (vo.YCLX != 0) {
                            buf.append(
                                    StringUtil.getStringLength(vo.YZMC == null ? ""
                                            : vo.YZMC, 11))
                                    .append("\n提示：")
                                    .append(StringUtil
                                            .getUnEmptText(vo.YCXX == null ? AdviceUtils
                                                    .getYCXXString(vo.YCLX)
                                                    : vo.YCXX)).append("\n");
                        }
                    }
                    if (buf.length() > 0) {
                        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                              /*  // 确认之后再刷新
                                onAction();*/
                                if (mClickListener != null) {
                                    mClickListener.action();
                                }
                            }
                        };
                        View txt = ViewBuildHelper.buildDialogTitleTextView(sContextWeakReference.get(), getString(R.string.project_tips));

                        return new Builder(getActivity())
                                // .setTitle(getString(R.string.project_tips))
                                .setCustomTitle(txt)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setMessage(buf.toString())
                                .setPositiveButton(android.R.string.ok, listener)
                                .create();

                    }

                    return null;
                case SJ:
                    buf = new StringBuffer();
                    for (int i = 0; i < result.list.size(); i++) {
                        SJModel vo = (SJModel) result.get(i);
                        buf.append(vo.MES).append("\n");
                    }
                    builder = new Builder(getActivity());
                    builder.setMessage(buf.toString());
                    builder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(sContextWeakReference.get(), getString(R.string.project_tips)));
                    builder.setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                    if (mClickListener != null) {
                                        mClickListener.sj();
                                    }
                                   /* if (null != barinfo) {
                                        onSaveAction(ExecutTask.SCAN_EXCUTE,
                                                barinfo.TMNR, barinfo.TMQZ, "false");
                                    }*/
                                }
                            });
                    builder.setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                }
                            });
                    return builder.create();
                // break;

                case SYZT:
                    builder = new Builder(getActivity());
                    builder.setMessage("输液处于暂停状态，执行继续还是结束?");
                    builder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(sContextWeakReference.get(), getString(R.string.project_tips)));
                    builder.setPositiveButton("继续",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                    if (mClickListener != null) {
                                        mClickListener.saveActionSYZTContinue();
                                    }
                                    /*onSaveAction(
                                            ExecutTask.Transfuse_Continue_EXCUTE,
                                            ((SYZTModel) result.get(0)).QRDH,
                                            Boolean.toString(false));*/
                                }
                            });
                    builder.setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.dismiss();
                                    if (mClickListener != null) {
                                        mClickListener.saveActionSYZTCancel();
                                    }
                                   /* onSaveAction(
                                            ExecutTask.Transfuse_Continue_EXCUTE,
                                            ((SYZTModel) result.get(0)).QRDH,
                                            Boolean.toString(true));*/
                                }
                            });
                    return builder.create();
                case SY:
                    if (mClickListener != null) {
                        mClickListener.sy();
                    }
//                    startSYActivity(result.list);
                    break;
                case SQ:

                    buf = new StringBuffer();
                    for (int i = 0; i < result.list.size(); i++) {
                        SQModel vo = (SQModel) result.get(i);
                        buf.append(vo.MES).append("\n");
                    }
                    return show(getActivity(), getString(R.string.project_tips), buf.toString());
                default:
                    break;
            }

            return null;
        }

        public Dialog showAlert(Context context) {

            switch (result.statue) {
                case Statue.NET_ERROR:
                    return showError(context, "网络加载失败");
                // break;
                case Statue.ERROR:
                    return showError(
                            context,
                            null != result.ExceptionMessage
                                    && result.ExceptionMessage.length() > 0 ? result.ExceptionMessage
                                    : "请求失败");

                case Statue.PARSER_ERROR:
                    return showError(context, "解析失败");

                case Statue.NO_Chose:
                    return showError(context, "没有选择");

                default:
                    return showError(context, "失败");

            }
        }

        public Dialog showError(Context context, String msg) {

            boolean vib = AppApplication.getInstance().getSettingConfig().vib;
            VibratorUtil.vibrator(getActivity(), vib);
            return show(context, getString(R.string.project_tips), msg);
        }

        public Dialog show(Context context, String title, String msg) {
            View txt = ViewBuildHelper.buildDialogTitleTextView(sContextWeakReference.get(), title);

            return new Builder(context)
                    // .setTitle(title)
                    .setCustomTitle(txt)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(msg)
                    .setPositiveButton(android.R.string.ok, null).create();
        }
    }

    public void performExcute(byte mType, String... params) {

        ExecutTask task = new ExecutTask(mType);
        tasks.add(task);
        task.execute(params);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 990) {

            // SY
            String sydh = data.getStringExtra("sydh");
            String qrdh = data.getStringExtra("qrdh");
            if (null != sydh && null != qrdh) {

                onSaveAction(ExecutTask.TRANSFUSE_EXCUTE, sydh, qrdh);
            } else {
                boolean vib = mAppApplication.getSettingConfig().vib;
                //  VibratorUtil.vibratorMsg(vib, "输液单号或者确认单号为空", getActivity());
                showMsgAndVoiceAndVibrator("输液单号或者确认单号为空");
            }

        }
    }

    void startSYActivity(ArrayList list) {

        Intent syintent = new Intent(getActivity(), SYExecutActivity.class);
        syintent.putExtra("list", list);
        startActivityForResult(syintent, 990);
    }

}
