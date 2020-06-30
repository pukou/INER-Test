package com.bsoft.mob.ienr.fragment.user;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.UserModelActivity;
import com.bsoft.mob.ienr.adapter.BTAdapter;
import com.bsoft.mob.ienr.adapter.TransfusionBloodTourRecordAdapter;
import com.bsoft.mob.ienr.api.BloodTransfusionApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.blood.BloodTransfusionInfo;
import com.bsoft.mob.ienr.model.blood.BloodTransfusionTourInfo;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;
import com.classichu.dialogview.helper.DialogFragmentShowHelper;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-11 上午11:20:14
 * @类说明 输血巡视
 */
public class TransfusionBloodTourFragment extends BaseUserFragment {


    private ListView mListView;

    private TextView stime, etime;

    private View sltStimeView;
    private View sltEtimeView;

    private ImageView searchBtn;

    private ArrayList<BloodTransfusionInfo> mList = null;
    private ArrayList<BloodTransfusionInfo> resultList = null;

    private BTAdapter mAdapter;

    private static BloodTransfusionTourInfo Previous_Info;


    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_transfusion_blood_tour;
    }

    @Override
    protected void initView(View root, Bundle savedInstanceState) {

        mListView = (ListView) root
                .findViewById(R.id.id_lv);

        EmptyViewHelper.setEmptyView(mListView, "mListView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mListView);
        stime = (TextView) root.findViewById(R.id.stime);
        etime = (TextView) root.findViewById(R.id.etime);
        TextView stimeTitle = (TextView) root.findViewById(R.id.stime_title);
        TextView etimeTitle = (TextView) root.findViewById(R.id.etime_title);
        stimeTitle.setText(R.string.start_time);
        etimeTitle.setText(R.string.end_time);
        sltStimeView = root.findViewById(R.id.slt_stime_ly);
        sltEtimeView = root.findViewById(R.id.slt_etime_ly);

        searchBtn = (ImageView) root.findViewById(R.id.search);


        initAcionBar();
        initTime();
        initSearchBtn();

        toRefreshData();
    }


    private void initTime() {

        String nowDate = DateTimeHelper.getServerDate();
        // 当天
        String eTimeStr = nowDate;
        etime.setText(eTimeStr);

        // 前天
        String startDate = DateTimeHelper.dateAddedDays(nowDate, -7);
        String sTimeStr = startDate;
        stime.setText(sTimeStr);

        sltStimeView.setOnClickListener(onClickListener);
        sltEtimeView.setOnClickListener(onClickListener);


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

    private void initTimeTxt(String dateStr, int viewId) {
        String timeStr = dateStr;
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


    private void initAcionBar() {

        actionBar.setTitle("输血巡视");
        actionBar.setPatient(mAppApplication.sickPersonVo.BRCH
                + mAppApplication.sickPersonVo.BRXM);
        actionBar.addAction(new Action() {
            @Override
            public String getText() {
                return "执行";
            }

            @Override
            public void performAction(View view) {
                if (mAdapter == null || mAdapter.checkedPostion == -1) {
                    showMsgAndVoiceAndVibrator("你还未选择");
                    return;
                }
                actionGGTask(resultList.get(mAdapter.checkedPostion).SXDH);
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }
        });


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initBroadCast();

    }

    @Override
    protected void toRefreshData() {

        GetHttpTask getHttpTask = new GetHttpTask();
        tasks.add(getHttpTask);
        getHttpTask.execute();
    }


    private void initBroadCast() {

        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {

                String action = intent.getAction();
                if (BarcodeActions.Refresh.equals(action)) {
                    sendUserName();
                    actionBar.setPatient(mAppApplication.sickPersonVo.BRCH
                            + mAppApplication.sickPersonVo.BRXM);
                    toRefreshData();
                } else if (BarcodeActions.Bar_Get.equals(action)) {

                    BarcodeEntity entity = (BarcodeEntity) intent
                            .getParcelableExtra("barinfo");
                    if (FastSwitchUtils.needFastSwitch(entity)) {
                        FastSwitchUtils.fastSwith(
                                (UserModelActivity) getActivity(), entity);
                    }

                }
            }
        };
    }

    void actionOperateTask(String... params) {
        OperateTask getTask = new OperateTask();
        tasks.add(getTask);
        getTask.execute(params);
    }

    // 操作
    class OperateTask extends AsyncTask<String, Void, Response<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(R.string.saveing);
        }

        @Override
        protected Response<String> doInBackground(String... params) {

            if (mAppApplication.user == null || mAdapter == null) {
                return null;
            }
            if (params == null || params.length < 2) {
                return null;
            }

            String speed = params[0];
            String blfy = params[1];
            String bzxx = params[2];
            if (Previous_Info == null) {
                Previous_Info = new BloodTransfusionTourInfo();
                Previous_Info.SXDH = resultList.get(mAdapter.checkedPostion).SXDH;
                Previous_Info.XSGH = mAppApplication.user.YHID;
                Previous_Info.SXSD = speed;
                Previous_Info.BLFY = blfy;
                Previous_Info.BZ = bzxx;
                Previous_Info.OperType = "0";
                Previous_Info.JGID = mAppApplication.jgId;
            } else {
                Previous_Info.SXSD = speed;
                Previous_Info.BLFY = blfy;
                Previous_Info.BZ = bzxx;
                Date date = DateUtil.getDateCompat(Previous_Info.XSRQ);
                String dateStr = DateUtil.format_yyyyMMdd_HHmmss.format(date);
                Previous_Info.XSRQ = dateStr;
            }
            String json;
            try {
                json = JsonUtil.toJson(Previous_Info);
                // 调用api
                return BloodTransfusionApi.getInstance(getActivity()).saveBloodTransfusionTourInfo(json);
            } catch (IOException e) {
                showMsgAndVoiceAndVibrator("出错了");
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Response<String> result) {

            tasks.remove(this);
            hideSwipeRefreshLayout();

            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    showMsgAndVoice(result.Msg);

                } else {
                    showMsgAndVoice(result.Msg);
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：参数错误");
                return;
            }
        }
    }

    public void actionGGTask(String... params) {

        GGTask task = new GGTask();
        tasks.add(task);
        task.execute(params);
    }

    // 巡视记录与反应列表
    class GGTask extends AsyncTask<String, Void, Response<List<BloodTransfusionTourInfo>>> {

        @Override
        protected void onPreExecute() {

            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<BloodTransfusionTourInfo>> doInBackground(String... params) {
            if (params == null || params.length < 1) {
                return null;
            }
            String jgid = mAppApplication.jgId;
            return BloodTransfusionApi.getInstance(getActivity())
                    .getBloodTransfusionTourInfoList(params[0], jgid);

        }

        @Override
        protected void onPostExecute(Response<List<BloodTransfusionTourInfo>> result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            actionGGTask(resultList.get(mAdapter.checkedPostion).SXDH);
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {

                    // 记录
                    @SuppressWarnings("unchecked")
                    ArrayList<BloodTransfusionTourInfo> list = (ArrayList<BloodTransfusionTourInfo>) result.Data;
                    showDetailFragment(DetailFragment.TYPE_OF_RECORD, list);
                } else {
                    showMsgAndVoice(result.Msg);
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：参数错误");
                return;
            }

        }
    }


    private static DetailFragment newFragment = null;

    public void showDetailFragment(byte mType, ArrayList list) {

        newFragment = DetailFragment.newInstance(mType, list, new WeakReference<Context>(mContext), new DetailFragment.ClickListener() {
            @Override
            public void showDe_REACTION() {
                ArrayList<BloodTransfusionTourInfo> list = getBloodTransfusionTourInfoList();
                showDetailFragment(DetailFragment.TYPE_OF_REACTION, list);
            }

            @Override
            public void showMsg(String msg) {
                showMsgAndVoice(msg);
            }

            @Override
            public void actionOperateTaskNO() {
                actionOperateTask(Previous_Info.SXSD, Previous_Info.BLFY, Previous_Info.BZ);
            }

            @Override
            public void actionOperateTaskHas(String speed, String blfy, String bzxx) {
                actionOperateTask(speed, blfy, bzxx);
            }
        });

        DialogFragmentShowHelper.show(getChildFragmentManager(), newFragment, "DetailFragment");
    /*    try {
            getFragmentManager().beginTransaction()
                    .add(newFragment, "DetailFragment")
                    .commitAllowingStateLoss();
        } catch (Exception ex) {
            Log.e(Constant.TAG, ex.getMessage(), ex);
        }*/
    }

    public static class DetailFragment extends DialogFragment {
        private static WeakReference<Context> sWeakReferenceContext;
        public static final byte TYPE_OF_RECORD = 0;

        public static final byte TYPE_OF_REACTION = 1;

        private static ArrayList list;

        private static byte showType;
        private static ClickListener mClickListener;

        public interface ClickListener {
            void showDe_REACTION();

            void showMsg(String msg);


            void actionOperateTaskHas(String speed, String blfy, String bzxx);

            void actionOperateTaskNO();
        }

        public static DetailFragment newInstance(byte showTypeT, ArrayList listT, WeakReference<Context> weakReference, ClickListener clickListener) {
            DetailFragment fragment = new DetailFragment();
            Bundle bundle = new Bundle();
            fragment.setArguments(bundle);

            showType = showTypeT;
            list = listT;
            sWeakReferenceContext = weakReference;
            mClickListener = clickListener;
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            switch (showType) {
                case TYPE_OF_RECORD:
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            getActivity());
                    Context context = sWeakReferenceContext.get();
                    LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_root_linear, null, false);
                    ListView listView = new ListView(context);

                    linearLayout.addView(listView);     EmptyViewHelper.setEmptyView(listView, "listView");
                    View txt = ViewBuildHelper.buildDialogTitleTextView(context, "巡视记录");
                    builder.setView(linearLayout)
                            //   .setTitle("巡视记录")
                            .setCustomTitle(txt);

                    builder.setPositiveButton("操作",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Previous_Info = null;

                                    if (mClickListener != null) {
                                        mClickListener.showDe_REACTION();
                                    }
                                /*    ArrayList<BloodTransfusionTourInfo> list = getBloodTransfusionTourInfoList();
                                   showDetailFragment(DetailFragment.TYPE_OF_REACTION, list);*/

                                }
                            });
                    builder.setNegativeButton("关闭窗口", null);
                    @SuppressWarnings("unchecked") final ArrayList<BloodTransfusionTourInfo> mTList = (ArrayList<BloodTransfusionTourInfo>) list;
                    TransfusionBloodTourRecordAdapter adapter = new TransfusionBloodTourRecordAdapter(
                            getActivity(), mTList);
                    listView.setAdapter(adapter);
                    listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                            if (AppApplication.getInstance().user.YHID.equals(mTList.get(position).XSGH)) {
                                Previous_Info = mTList.get(position);
                                Previous_Info.OperType = "2";
//                                ArrayList<BloodTransfusionTourInfo> list = getBloodTransfusionTourInfoList();
                                try {
                                    getFragmentManager().beginTransaction()
                                            .remove(newFragment)
                                            .commitAllowingStateLoss();
                                } catch (Exception ex) {
                                    Log.e(Constant.TAG, ex.getMessage(), ex);
                                }
                                if (mClickListener != null) {
                                    mClickListener.actionOperateTaskNO();
                                }
//                                actionOperateTask(Previous_Info.SXSD, Previous_Info.BLFY, Previous_Info.BZ);

                            } else {
                                if (mClickListener != null) {
                                    mClickListener.showMsg("不允许删除别人录入的巡视数据!");
                                }
//                                showMsgAndVoice("不允许删除别人录入的巡视数据!");
                            }
                            return false;
                        }
                    });
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (AppApplication.getInstance().user.YHID.equals(mTList.get(position).XSGH)) {
                                Previous_Info = mTList.get(position);
                                Previous_Info.OperType = "1";

                                try {
                                    getFragmentManager().beginTransaction()
                                            .remove(newFragment)
                                            .commitAllowingStateLoss();
                                } catch (Exception ex) {
                                    Log.e(Constant.TAG, ex.getMessage(), ex);
                                }
                                DialogFragmentShowHelper.show(getChildFragmentManager(), newFragment,
                                        "BloodTransfusionTourInfo");

                                if (mClickListener != null) {
                                    mClickListener.showDe_REACTION();
                                }
                              /*  ArrayList<BloodTransfusionTourInfo> list = getBloodTransfusionTourInfoList();
                                showDetailFragment(DetailFragment.TYPE_OF_REACTION, list);*/

                            } else {

                                if (mClickListener != null) {
                                    mClickListener.showMsg("不允许修改别人录入的巡视数据!");
                                }
//                                showMsgAndVoice("不允许修改别人录入的巡视数据!");
                            }

                        }
                    });
                    return builder.create();

                case TYPE_OF_REACTION:

                    @SuppressWarnings("unchecked") final ArrayList<BloodTransfusionTourInfo> mList = (ArrayList<BloodTransfusionTourInfo>) list;

                    builder = new AlertDialog.Builder(getActivity());
                    View contentView = LayoutInflater.from(getActivity()).inflate(
                            R.layout.layout_transfusion_tour_record, null, false);
                    View txt22 = ViewBuildHelper.buildDialogTitleTextView(sWeakReferenceContext.get(), "巡视记录");
                    builder.setView(contentView)
                            //   .setTitle("巡视记录")
                            .setCustomTitle(txt22);

                    final LinearLayout linearLayout_Bzxx = (LinearLayout) contentView
                            .findViewById(R.id.linearLayout_bzxx);
                    linearLayout_Bzxx.setVisibility(View.VISIBLE);

                    final EditText et_bzxx = (EditText) contentView.findViewById(R.id.et_bzxx);

                    final ListView mlistView = (ListView) contentView
                            .findViewById(R.id.id_lv);

                    final EditText SYDS = (EditText) contentView
                            .findViewById(R.id.SYDS);
                    SYDS.setText("20");

                    builder.setNegativeButton("关闭窗口", null);

                    // 设置光标，默认是“20”
                    SYDS.setSelection(2);
                    final Button add = (Button) contentView.findViewById(R.id.add);
                    final Button sub = (Button) contentView.findViewById(R.id.sub);

                    add.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            SYDS.setText(String.valueOf(Integer.valueOf(SYDS
                                    .getText().toString()) + 1));
                        }
                    });
                    sub.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View arg0) {
                            SYDS.setText(String.valueOf(Integer.valueOf(SYDS
                                    .getText().toString()) - 1));
                        }
                    });

                    if (mList == null) {
                        return super.onCreateDialog(savedInstanceState);
                    }
                    String[] arr_more = new String[mList.size()];
                    for (int i = 0; i < mList.size(); i++) {
                        arr_more[i] = mList.get(i).FYMC;
                    }
                    ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(
                            getActivity(),
                            android.R.layout.simple_list_item_single_choice,
                            arr_more);
                    mlistView.setItemsCanFocus(false);
                    // checked/activated
                    mlistView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                    mlistView.setAdapter(mAdapter);
                    mlistView.setItemChecked(0, true);
                    if (Previous_Info != null) {
                        et_bzxx.setText(Previous_Info.BZ);
                        SYDS.setText(Previous_Info.SXSD);
                        if (Previous_Info.BLFY.equals("1")) {
                            mlistView.setItemChecked(0, true);
                        } else {
                            mlistView.setItemChecked(1, true);
                        }
                    }

                    // 暂停
                    final Button but1 = (Button) contentView
                            .findViewById(R.id.but1);
                    but1.setVisibility(View.GONE);

                    final Button but2 = (Button) contentView
                            .findViewById(R.id.but2);
                    but2.setVisibility(View.GONE);

                    final Button but3 = (Button) contentView
                            .findViewById(R.id.but3);

                    OnClickListener onClickListener = new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            int position = mlistView.getCheckedItemPosition();
                            if (position != -1) {
                                BloodTransfusionTourInfo item = mList
                                        .get(position);
                                String speed = SYDS.getText().toString().trim();
                                String bzxx = et_bzxx.getText().toString().trim();
//                                actionOperateTask(speed, item.BLFY, bzxx);
                                if (mClickListener != null) {
                                    mClickListener.actionOperateTaskHas(speed, item.BLFY, bzxx);
                                }
                            } else {
//                                showMsgAndVoice("请先选择反应项");
                                if (mClickListener != null) {
                                    mClickListener.showMsg("请先选择反应项!");
                                }
                            }
                        }
                    };
                    but3.setOnClickListener(onClickListener);

                    return builder.create();
                default:
            }

            return super.onCreateDialog(savedInstanceState);

        }
    }

    private ArrayList<BloodTransfusionTourInfo> getBloodTransfusionTourInfoList() {
        ArrayList<BloodTransfusionTourInfo> list = new ArrayList<BloodTransfusionTourInfo>();
        BloodTransfusionTourInfo info1 = new BloodTransfusionTourInfo();
        info1.BLFY = "1";
        info1.FYMC = "有不良反应";
        list.add(info1);
        BloodTransfusionTourInfo info2 = new BloodTransfusionTourInfo();
        info2.BLFY = "0";
        info2.FYMC = "无不良反应";
        list.add(info2);
        return list;
    }

    class GetHttpTask extends AsyncTask<String, Integer, Response<List<BloodTransfusionInfo>>> {

        @Override
        protected void onPreExecute() {

            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<BloodTransfusionInfo>> doInBackground(String... params) {

            BloodTransfusionApi api = BloodTransfusionApi.getInstance(getActivity());
            if (mAppApplication.sickPersonVo == null) {
                return null;
            }
            String sTimeStr = stime.getText().toString();
            String eTimeStr = etime.getText().toString();
            //加一天
           String dateTime = DateTimeHelper.dateTimeAddedDays(eTimeStr, 1);
            eTimeStr = DateTimeFactory.getInstance().dateTime2Date(dateTime);
            String jgid = mAppApplication.jgId;
            String zyh = mAppApplication.sickPersonVo.ZYH;
            return api.GetBloodTransfusionList(sTimeStr, eTimeStr, zyh, jgid);
        }

        @Override
        protected void onPostExecute(Response<List<BloodTransfusionInfo>> result) {

            hideSwipeRefreshLayout();

            tasks.remove(this);
            if (null != result) {
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
                    ArrayList<BloodTransfusionInfo> list = (ArrayList<BloodTransfusionInfo>) result.Data;
                    mList = list;
                    filterAndSetList();

                } else {
                    showMsgAndVoice(result.Msg);
                /*    MediaUtil.getInstance(getActivity()).playSound(
                            R.raw.wrong, getActivity());*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    protected void filterAndSetList() {

        if (mList == null) {
            showMsgAndVoice("暂无输血记录");
            return;
        }

        resultList = new ArrayList<BloodTransfusionInfo>();

        for (BloodTransfusionInfo entity : mList) {
            boolean sxrExisted = !EmptyTool.isBlank(entity.SXR1)
                    || !EmptyTool.isBlank(entity.SXR2);
            if (sxrExisted && EmptyTool.isBlank(entity.JSR)) {
                //输血中
                resultList.add(entity);
            }
        }

        if (resultList.size() == 0) {
            mAdapter = null;
        } else {
            mAdapter = new BTAdapter(getActivity(), resultList);
        }
        mListView.setAdapter(mAdapter);

    }

}
