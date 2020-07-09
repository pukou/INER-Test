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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.UserModelActivity;
import com.bsoft.mob.ienr.activity.user.adapter.TransfusionAdapter;
import com.bsoft.mob.ienr.adapter.TransfusionTourRecordAdapter;
import com.bsoft.mob.ienr.api.AdviceApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.advice.TransfusionData;
import com.bsoft.mob.ienr.model.advice.TransfusionInfoVo;
import com.bsoft.mob.ienr.model.advice.TransfusionTourReactionVo;
import com.bsoft.mob.ienr.model.advice.TransfusionTourRecordVo;
import com.bsoft.mob.ienr.model.advice.TransfusionVo;
import com.bsoft.mob.ienr.model.advice.execut.ExecutVo;
import com.bsoft.mob.ienr.model.advice.execut.RequestBodyInfo;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;
import com.classichu.dialogview.helper.DialogFragmentShowHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-11 上午11:20:14
 * @类说明 输液巡视
 */
public class TransfusionTourFragment extends BaseUserFragment {


    private ListView mListView;

    private TextView time;

    private View sltDateView;

    private TransfusionAdapter adapter;

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_transfusion_tour;
    }

    @Override
    protected void initView(View root, Bundle savedInstanceState) {

        mListView = (ListView) root
                .findViewById(R.id.id_lv);

        EmptyViewHelper.setEmptyView(mListView, "mListView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mListView);
        time = (TextView) root.findViewById(R.id.time);
        sltDateView = root.findViewById(R.id.slt_date_ly);

        final View trans_mode = root.findViewById(R.id.trans_mode);
        trans_mode.setVisibility(View.INVISIBLE);
        final View image = root.findViewById(R.id.image);
        image.setVisibility(View.GONE);

        initAcionBar();
        initTime();
    }


    private void initTime() {

        String date = DateTimeHelper.getServerDate();
        initTimeTxt(date, R.id.time);

        sltDateView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                String dateStr = time.getText().toString();
                showDatePickerCompat(dateStr, time.getId());
            }
        });

    }

    private void initTimeTxt(String ymdHM, int viewId) {

        if (viewId == R.id.time) {
            time.setText(ymdHM);
            time.postDelayed(new Runnable() {

                @Override
                public void run() {
                    toRefreshData();
                }
            }, 400);
        }
    }

    @Override
    public void onDateSet(int year, int monthOfYear, int dayOfMonth, int viewId) {

        String date = DateTimeFactory.getInstance().ymd2Date(year, monthOfYear, dayOfMonth);
        initTimeTxt(date, viewId);
    }


    void setTitleTxt(String title) {
        final TextView mTitleTxt = (TextView) actionBar
                .findViewById(R.id.titleTextView);
        mTitleTxt.setText(title);
    }

    private void initAcionBar() {

        actionBar.setTitle("输液巡视");
        actionBar.setPatient(mAppApplication.sickPersonVo.XSCH
                + mAppApplication.sickPersonVo.BRXM);
        actionBar.addAction(new Action() {
            @Override
            public String getText() {
                return "执行";
            }
            @Override
            public void performAction(View view) {

                if (null == adapter || !adapter.hasCheckedItm()) {

                    showMsgAndVoiceAndVibrator("你还未选择");
                    return;
                }
                actionGGTask(GGTask.GET_RECORD, adapter.mSYDH);
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
                    actionBar.setPatient(mAppApplication.sickPersonVo.XSCH
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

    void actionOperateTask(byte id, String... params) {
        OperateTask getTask = new OperateTask(id);
        tasks.add(getTask);
        getTask.execute(params);
    }

    // 操作
    class OperateTask extends AsyncTask<String, ExecutVo, ExecutVo> {

        byte id;

        public static final byte ACTION_PUASE = 0;

        public static final byte ACTION_STOP = 1;

        public static final byte ACTION_DropSpeed = 2;

        public OperateTask(byte id) {
            this.id = id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(R.string.saveing);
        }

        @Override
        protected ExecutVo doInBackground(String... params) {

            ExecutVo executVo = null;
            switch (id) {
                case ACTION_PUASE:
                    if (adapter == null || mAppApplication.sickPersonVo == null
                            || mAppApplication.user == null) {
                        return null;
                    }
                    String realData = "";
                    try {
                        RequestBodyInfo info = new RequestBodyInfo();
                        info.ZYH = mAppApplication.sickPersonVo.ZYH;
                        info.YHID = mAppApplication.user.YHID;
                        info.QRDH = adapter.mSYDH;
                        info.JGID = mAppApplication.jgId;
                        realData = JsonUtil.toJson(info);
                    } catch (Exception e) {
                        Log.e(Constant.TAG, e.getMessage(), e);
                        return null;
                    }
                    executVo = AdviceApi.getInstance(getActivity()).TransfusePause(realData);
                    break;
                case ACTION_STOP:

                    if (adapter == null || mAppApplication.sickPersonVo == null
                            || mAppApplication.user == null) {
                        return null;
                    }
                    try {
                        RequestBodyInfo info = new RequestBodyInfo();
                        info.ZYH = mAppApplication.sickPersonVo.ZYH;
                        info.YHID = mAppApplication.user.YHID;
                        info.QRDH = adapter.mSYDH;
                        info.QZJS = true;
                        info.SYBX = false;
                        info.JGID = mAppApplication.jgId;
                        realData = JsonUtil.toJson(info);
                    } catch (Exception e) {
                        Log.e(Constant.TAG, e.getMessage(), e);
                        return null;
                    }
                    executVo = AdviceApi.getInstance(getActivity())
                            .TransfuseContinue(realData);
                    break;
                case ACTION_DropSpeed:

                    if (mAppApplication.user == null || adapter == null) {
                        return null;
                    }
                    if (params == null || params.length < 2) {
                        return null;
                    }
                    try {
                        String speed = params[0];
                        String DYXH = params[1];
                        TransfusionTourRecordVo info = new TransfusionTourRecordVo();
                        info.SYDH = adapter.mSYDH;
                        info.XSGH = mAppApplication.user.YHID;
                        info.JGID = mAppApplication.jgId;
                        info.SYDS = speed;
                        info.SYFY = DYXH;
                        realData = JsonUtil.toJson(info);
                    } catch (Exception e) {
                        Log.e(Constant.TAG, e.getMessage(), e);
                        return null;
                    }
                    executVo = AdviceApi.getInstance(getActivity()).DropSpeedInput(realData);
                    break;
                default:
                    break;
            }
            return executVo;
        }

        @Override
        protected void onPostExecute(ExecutVo result) {

            tasks.remove(this);
            hideLoadingDialog();

            // 输液停止单独已在onProgressUpdate方法中处理
            if (id == ACTION_STOP) {
                toRefreshData();
                return;
            }

            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：参数错误");
                return;
            }

            if (!result.isOK()) {
                result.showToast(getActivity());
                return;
            }

            if (id == ACTION_PUASE) {
                toRefreshData();
            }
            showMsgAndVoice("请求成功");
        }
    }

    public void actionGGTask(byte mType, String... params) {

        GGTask task = new GGTask(mType);
        tasks.add(task);
        task.execute(params);
    }

    // 巡视记录与反应列表
    class GGTask extends AsyncTask<String, Void, Response<TransfusionData>> {

        public static final byte GET_RECORD = 0;

        public static final byte GET_REACTION = 1;

        private byte mType = GET_RECORD;

        public GGTask(byte mType) {
            this.mType = mType;
        }

        @Override
        protected void onPreExecute() {

            showSwipeRefreshLayout();
        }

        @Override
        protected Response<TransfusionData> doInBackground(String... params) {

            switch (mType) {
                case GET_RECORD:
                    if (params == null || params.length < 1) {
                        return null;
                    }
                    String jgid = mAppApplication.jgId;
                    return AdviceApi.getInstance(getActivity()).GetTransfusion(
                            params[0], jgid);
                case GET_REACTION:
                    String bqid = mAppApplication.getAreaId();
                    jgid = mAppApplication.jgId;
                    return AdviceApi.getInstance(getActivity())
                            .GetTransfusionReaction(bqid, jgid);

            }
            return null;

        }

        @Override
        protected void onPostExecute(Response<TransfusionData> result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    switch (mType) {
                        case GET_RECORD:

                            // 记录
                            @SuppressWarnings("unchecked")
                            ArrayList<TransfusionTourRecordVo> list = (ArrayList<TransfusionTourRecordVo>) result.Data.SYXS;
                            showDetailFragment(DetailFragment.TYPE_OF_RECORD, list);
                            break;
                        case GET_REACTION:
                            // 反应
                            @SuppressWarnings("unchecked")
                            ArrayList<TransfusionTourReactionVo> dlist = (ArrayList<TransfusionTourReactionVo>) result.Data.SYFY;
                            showDetailFragment(DetailFragment.TYPE_OF_REACTION, dlist);
                            break;
                    }
                } else {
                    showMsgAndVoice(result.Msg);

                    return;
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：参数错误");

                return;
            }
        }
    }


    // 医嘱计划
    class GetHttpTask extends AsyncTask<Void, Void, Response<TransfusionData>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<TransfusionData> doInBackground(Void... arg0) {

            if (mAppApplication.sickPersonVo == null) {
                return null;
            }
            String zyh = mAppApplication.sickPersonVo.ZYH;
            String jgid = mAppApplication.jgId;
            String sTime = time.getText().toString();
            AdviceApi api = AdviceApi.getInstance(getActivity());

            Response<TransfusionData> response = api.GetTransfusionListPatient(zyh, sTime,
                    "2", jgid);
            return response;

        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Response<TransfusionData> result) {

            super.onPostExecute(result);

            tasks.remove(this);
            hideSwipeRefreshLayout();
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {

                    ArrayList<TransfusionVo> tList = (ArrayList<TransfusionVo>) result.Data.SYD;

                    ArrayList<TransfusionInfoVo> infoList = (ArrayList<TransfusionInfoVo>) result.Data.SYMX;
                    if (null != tList) {
                        adapter = new TransfusionAdapter(getActivity(), tList,
                                infoList);
                        mListView.setAdapter(adapter);
                    } else {
                        showMsgAndVoiceAndVibrator("该病人未在输液");
                        adapter = null;
                        mListView.setAdapter(null);
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

    public  void showDetailFragment(byte mType, ArrayList list) {

        @SuppressWarnings({"rawtypes", "unchecked"})
        DetailFragment newFragment =  DetailFragment.newInstance(mType, list, new WeakReference<>(mContext), new DetailFragment.ClickListener() {
            @Override
            public void actionGG() {
                actionGGTask(GGTask.GET_REACTION);
            }

            @Override
            public void showMsg() {
                showMsgAndVoiceAndVibrator("请先选择反应项");
            }

            @Override
            public void actionDropSpeed(String speed, String dyxh) {
                actionOperateTask(OperateTask.ACTION_DropSpeed,
                        speed, dyxh);
            }

            @Override
            public void actionSTOP() {
                actionOperateTask(OperateTask.ACTION_STOP);
            }

            @Override
            public void actionPUASE() {
                actionOperateTask(OperateTask.ACTION_PUASE);
            }
        });
      /*  try {
            getFragmentManager().beginTransaction()
                    .add(newFragment, "DetailFragment")
                    .commitAllowingStateLoss();
        } catch (Exception ex) {
            Log.e(Constant.TAG, ex.getMessage(), ex);
        }*/
        DialogFragmentShowHelper.show(getChildFragmentManager(), newFragment, "DetailFragment");
    }

    public static class DetailFragment extends DialogFragment {

        private static WeakReference<Context> sContextWeakReference;
        public static final byte TYPE_OF_RECORD = 0;

        public static final byte TYPE_OF_REACTION = 1;

        private static ArrayList list;

        private static byte showType;

        private static ClickListener mClickListener;
        public interface ClickListener{

            void actionGG();


            void showMsg();

            void actionDropSpeed(String speed, String dyxh);

            void actionSTOP();

            void actionPUASE();
        }

        public static DetailFragment newInstance(byte showTypeT, ArrayList listT,WeakReference<Context> weakReference,ClickListener clickListener) {
            DetailFragment fragment = new DetailFragment();
            Bundle bundle = new Bundle();
            fragment.setArguments(bundle);

            showType = showTypeT;
            list = listT;
            sContextWeakReference = weakReference;
            mClickListener = clickListener;
            return fragment;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            switch (showType) {
                case TYPE_OF_RECORD:
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            getActivity());
                    Context context = getContext();
                    LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_root_linear, null, false);
                    ListView listView = new ListView(context);
                    linearLayout.addView(listView);
                    EmptyViewHelper.setEmptyView(listView, "listView");
                    View txt = ViewBuildHelper.buildDialogTitleTextView(sContextWeakReference.get(), "巡视记录");
                    builder.setView(linearLayout)
                            //.setTitle("巡视记录")
                            .setCustomTitle(txt);

                    builder.setPositiveButton("操作",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    if (mClickListener!=null){
                                        mClickListener.actionGG();
                                    }
//                                    actionGGTask(GGTask.GET_REACTION);
                                }
                            });
                    builder.setNegativeButton("关闭窗口", null);
                    @SuppressWarnings("unchecked") final ArrayList<TransfusionTourRecordVo> mTList = (ArrayList<TransfusionTourRecordVo>) list;
                    TransfusionTourRecordAdapter adapter = new TransfusionTourRecordAdapter(
                            getActivity(), mTList);
                    listView.setAdapter(adapter);
                    return builder.create();

                case TYPE_OF_REACTION:

                    @SuppressWarnings("unchecked") final ArrayList<TransfusionTourReactionVo> mList = (ArrayList<TransfusionTourReactionVo>) list;

                    builder = new AlertDialog.Builder(getActivity());
                    View contentView = LayoutInflater.from(getActivity()).inflate(
                            R.layout.layout_transfusion_tour_record, null, false);

                    View txt33 = ViewBuildHelper.buildDialogTitleTextView(sContextWeakReference.get(), "巡视操作");
                    builder.setView(contentView)
                            //.setTitle("记录巡视")
                            .setCustomTitle(txt33);

                    final ListView mlistView = (ListView) contentView
                            .findViewById(R.id.id_lv);

                    final EditText SYDS = (EditText) contentView
                            .findViewById(R.id.SYDS);

                    builder.setNegativeButton("关闭窗口", null);

                    // 设置光标，默认是“60”
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
                        arr_more[i] = mList.get(i).DYMS;
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

                    // 暂停
                    final Button but1 = (Button) contentView
                            .findViewById(R.id.but1);

                    final Button but2 = (Button) contentView
                            .findViewById(R.id.but2);

                    final Button but3 = (Button) contentView
                            .findViewById(R.id.but3);

                    View.OnClickListener onClickListener = new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            int id = v.getId();
                            if (id == R.id.but1) {// 暂停
                                if (mClickListener!=null){
                                    mClickListener.actionPUASE();
                                }
//                                actionOperateTask(OperateTask.ACTION_PUASE);
                            } else if (id == R.id.but2) {// 结束
                                if (mClickListener!=null){
                                    mClickListener.actionSTOP();
                                }
//                                actionOperateTask(OperateTask.ACTION_STOP);
                            } else if (id == R.id.but3) {// 记录
                                int position = mlistView.getCheckedItemPosition();
                                if (position != -1) {
                                    TransfusionTourReactionVo item = mList
                                            .get(position);
                                String dyxh = item.DYXH;
                                  String speed = SYDS.getText().toString();
                                 /*     actionOperateTask(OperateTask.ACTION_DropSpeed,
                                            speed, dyxh);*/
                                    if (mClickListener!=null){
                                        mClickListener.actionDropSpeed(speed,dyxh);
                                    }
                                } else {
                                    if (mClickListener!=null){
                                        mClickListener.showMsg();
                                    }
//                                    showMsgAndVoice("请先选择反应项");
                                }
                            }
                        }
                    };

                    but1.setOnClickListener(onClickListener);
                    but2.setOnClickListener(onClickListener);
                    but3.setOnClickListener(onClickListener);

                    return builder.create();
                    default:
            }

            return super.onCreateDialog(savedInstanceState);

        }
    }

}
