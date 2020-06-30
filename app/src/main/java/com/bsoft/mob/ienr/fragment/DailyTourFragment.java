package com.bsoft.mob.ienr.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.adapter.PersonListAdapter;
import com.bsoft.mob.ienr.adapter.VisitAdapter;
import com.bsoft.mob.ienr.adapter.VisitHistoryAdapter;
import com.bsoft.mob.ienr.api.VisitApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.fragment.base.LeftMenuItemFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.ListViewScrollHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.kernel.SickPersonVo;
import com.bsoft.mob.ienr.model.visit.CheckState;
import com.bsoft.mob.ienr.model.visit.VisitCount;
import com.bsoft.mob.ienr.model.visit.VisitHistory;
import com.bsoft.mob.ienr.model.visit.VisitPerson;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BSToast;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 日常巡视  Created by hy on 14-3-21.
 */
public class DailyTourFragment extends LeftMenuItemFragment {

    private ExpandableListView mListView;

    private RadioGroup mRadioGroup;

    // private EditText mRecordEdit;

    private VisitAdapter adapter;

    private VisitHistoryAdapter historyAdapter;
    private ListView historyList;

    private LinearLayout ll_history, ll_visit;// 巡视记录，待巡视

    boolean vib = true;

    private int previousGroup = -1;

    // private int childPosition = -1;

    private VisitPerson mCurPerson;


    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_daily_visit;
    }

    @Override
    protected void initView(View root, Bundle savedInstanceState) {
        mListView = (ExpandableListView) root
                .findViewById(R.id.id_elv);
        historyList = (ListView) root.findViewById(R.id.id_lv);

        ll_history = (LinearLayout) root.findViewById(R.id.history_panel);
        ll_visit = (LinearLayout) root.findViewById(R.id.visit_panel);


        // mRecordEdit = (EditText) root.findViewById(R.id.visit_record);
        mRadioGroup = (RadioGroup) root.findViewById(R.id.visit_state_rp);

        vib = mAppApplication.getSettingConfig().vib;

        initActoin();
        initListView();

        toRefreshData();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initBroadCast();
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
                    /*升级编号【56010027】============================================= start
处理房间条码、获取房间病人处理
                                ================= classichu 2018/3/22 10:23
                                */
                    String roomBarCode = entity.source;
                    if (!EmptyTool.isBlank(roomBarCode) && roomBarCode.toUpperCase().contains("FJ")) {
                        roomBarCode = roomBarCode.toUpperCase().replace("FJ", "");
                        dealRoomBarCode(roomBarCode);
                        return;
                    }
                    /* =============================================================== end */

                    if (FastSwitchUtils.needFastSwitch(entity)) {

                        AppApplication app = (AppApplication) getActivity()
                                .getApplication();
                        if (mAppApplication.sickPersonVo == null) {
                            return;
                        }
                        FastSwitchUtils.fastSwith(getActivity(), entity);
                    }
                } else {
//					VibratorUtil.vibratorMsg(vib, "扫描的病人不在此列表中", getActivity());
                    showMsgAndVoiceAndVibrator("扫描的病人不在此列表中");
                }
            }
        };
    }

    /*升级编号【56010027】============================================= start
处理房间条码、获取房间病人处理
                              ================= classichu 2018/3/22 10:23
                              */
    private void dealRoomBarCode(String roomBarCode) {

        actionGetRoomSickPersonTask task = new actionGetRoomSickPersonTask();
        tasks.add(task);
        task.execute(roomBarCode);
    }

    class actionGetRoomSickPersonTask extends AsyncTask<String, Void, Response<VisitCount>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(R.string.loading);
        }

        @Override
        protected Response<VisitCount> doInBackground(String... params) {
            if (params == null || params.length < 0) {
                return null;
            }
            String roomBarCode = params[0];
            VisitApi api = VisitApi.getInstance(getActivity());
            String jgid = application.jgId;
            String ksdm = application.getAreaId();
            return api.GetRoomPatientList(ksdm, roomBarCode, jgid);
        }

        @Override
        protected void onPostExecute(Response<VisitCount> result) {
            super.onPostExecute(result);
            hideLoadingDialog();
            if (result == null) {
                Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            if (result.ReType == 0) {
                VisitCount visitCount = result.Data;
                List<VisitPerson> roomSickList = visitCount.xyxs;
                List<CheckState> checkStateList = visitCount.xsqk;
                if (roomSickList == null || roomSickList.size() <= 0) {
                    BSToast.showToast(getActivity(), "该房间暂无需要巡视的病人！", BSToast.LENGTH_SHORT);
                } else {
                    showRoomSickDialog(roomSickList, checkStateList);
                }
            } else {
                BSToast.showToast(getActivity(), result.Msg, BSToast.LENGTH_SHORT);
            }

        }
    }

    private AlertDialog mAlertDialog;

    private void showRoomSickDialog(List<VisitPerson> roomSickList, List<CheckState> checkStates) {
        Context context = getActivity();
        if (mAlertDialog != null) {
            mAlertDialog.hide();
        }
        //
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        ListView listView = new ListView(context);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


            }
        });
        PersonListAdapter adapter = new PersonListAdapter(context, roomSickList);
        listView.setAdapter(adapter);
        //
        RadioGroup radioGroup = new RadioGroup(context);
        // 根据X排序
        Collections.sort(checkStates, new SortByZFBZ());
        int hold_id = 0;
        for (CheckState state : checkStates) {
            int id = state.DYXH;
            RadioButton rb = new AppCompatRadioButton(getActivity());
            rb.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            rb.setTextSize(12);
            rb.setId(state.DYXH);
            if (state.ZFBZ == 1) {
                rb.setVisibility(View.GONE);
                rb.setClickable(false);
                rb.setText(state.DYMS + "-已作废");
            } else {
                rb.setText(state.DYMS);
            }
            if (state.DYMS.equals("正常")) {
                hold_id = id;
            }
            rb.setTag(state.ZFBZ);
            radioGroup.addView(rb);
        }
        radioGroup.check(hold_id);
        linearLayout.addView(radioGroup);
        linearLayout.addView(listView);
        builder.setView(linearLayout).setTitle("批量巡视");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int checkedid = radioGroup.getCheckedRadioButtonId();
                submitSome(roomSickList, String.valueOf(checkedid));
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        mAlertDialog = builder.create();
        mAlertDialog.show();

    }

    private void submitSome(List<VisitPerson> roomSickList, String checkedid) {
        StringBuilder stringBuilder = new StringBuilder();
        for (VisitPerson visitPerson : roomSickList) {
            stringBuilder.append(visitPerson.ZYH);
            stringBuilder.append(",");
        }
        actionSubmitSomeTask(stringBuilder.toString(), checkedid);
    }


    private void actionSubmitSomeTask(String zyh_list, String xsqk) {
        SubmitSomeTask task = new SubmitSomeTask();
        tasks.add(task);
        task.execute(zyh_list, xsqk);
    }

    class SubmitSomeTask extends AsyncTask<String, Void, Response<List<VisitPerson>>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(getResources().getString(R.string.saveing));
        }

        @Override
        protected Response<List<VisitPerson>> doInBackground(String... params) {
            if (params == null || params.length < 0) {
                return null;
            }
            String zyh_list = params[0];
            String xsqk = params[1];

            VisitApi api = VisitApi.getInstance(getActivity());
            String jgid = application.jgId;
            String brbq = application.getAreaId();
            String urid = application.user.YHID;
            return api.SetPatrol_Some(brbq, urid, zyh_list, xsqk, jgid, Constant.sysType);
        }

        @Override
        protected void onPostExecute(Response<List<VisitPerson>> result) {
            super.onPostExecute(result);
            hideLoadingDialog();
            if (result == null) {
                Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT)
                        .show();
                return;
            }
            if (result.ReType == 0) {
                List<VisitPerson> visitPersonList = result.Data;
                //刷新
                toRefreshData();

            } else {
                BSToast.showToast(getActivity(), result.Msg, BSToast.LENGTH_SHORT);
            }

        }
    }
    /* =============================================================== end */

    private void scanSltPerson() {

        SickPersonVo person = mAppApplication.sickPersonVo;
        if (person == null || adapter == null) {
//			VibratorUtil.vibratorMsg(vib, "扫描的病人不在此列表中", getActivity());
            showMsgAndVoiceAndVibrator("扫描的病人不在此列表中");
            return;
        }

        int position = adapter.findPosition(previousGroup, person.ZYH);
        if (position == -1) {
//			VibratorUtil.vibratorMsg(vib, "扫描的病人不在此列表中", getActivity());
            showMsgAndVoiceAndVibrator("扫描的病人不在此列表中");
            return;
        }

        int index = mListView.getFlatListPosition(
                ExpandableListView.getPackedPositionForChild(previousGroup,
                        position));
        mListView.setItemChecked(index, true);
//		mListView.smoothScrollToPosition(index);
        ListViewScrollHelper.smoothScrollToPosition(mListView, index);

        mCurPerson = (VisitPerson) adapter.getChild(previousGroup, position);
        mCurPerson.isScanSlt = true;
        // 不直接扫描保存
        // String lxbs = getlxbs();
        // String zyh = person.ZYH;
        // String brbq = application.getAreaId();
        // saveVisitRecord(SaveTask.SCAN_SAVE, zyh, lxbs, brbq);

    }

    /**
     * 获取当前病人状态标识,即radiobutton id
     *
     * @return
     */
    private String getlxbs() {

        int id = mRadioGroup.getCheckedRadioButtonId();
        // RadioButton rb = (RadioButton) mRadioGroup.findViewById(position);
        return String.valueOf(id);
    }

    /**
     * 保存巡视记录至服务器
     */
    private void saveVisitRecord(byte type, String... params) {

        SaveTask task = new SaveTask(type);
        tasks.add(task);
        task.execute(params);
    }

    @Override
    protected void toRefreshData() {

        GetDataTask task = new GetDataTask();
        tasks.add(task);
        task.execute();
    }

    /**
     * 初始化列表状态
     */
    private void initListView() {

        mListView.setItemsCanFocus(true);
        // checked/activated
        mListView.setChoiceMode(
                AbsListView.CHOICE_MODE_SINGLE);
        mListView.setOnChildClickListener(
                onChildClickListener);
        mListView.setOnGroupExpandListener(
                onGroupExpandListener);
        EmptyViewHelper.setEmptyView(mListView, "mListView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mListView);
    }


    public OnGroupExpandListener onGroupExpandListener = new OnGroupExpandListener() {

        // int previousGroup = -1;

        @Override
        public void onGroupExpand(int groupPosition) {
            if (groupPosition != previousGroup) {
                mListView.collapseGroup(previousGroup);
            }
            previousGroup = groupPosition;
            switch (groupPosition) {
                case 0:
                    ll_visit.setVisibility(View.VISIBLE);
                    ll_history.setVisibility(View.GONE);
                    break;
                case 1:
                    ll_visit.setVisibility(View.GONE);
                    ll_history.setVisibility(View.VISIBLE);
                    break;
                default:
                    ll_visit.setVisibility(View.VISIBLE);
                    ll_history.setVisibility(View.GONE);
                    break;
            }
        }
    };

    public OnChildClickListener onChildClickListener = new OnChildClickListener() {

        @Override
        public boolean onChildClick(ExpandableListView parent, View v,
                                    int groupPosition, int childPosition, long id) {

            int index = parent.getFlatListPosition(ExpandableListView
                    .getPackedPositionForChild(groupPosition, childPosition));
            parent.setItemChecked(index, true);

            VisitPerson item = (VisitPerson) adapter.getChild(groupPosition,
                    childPosition);
            // application.sickPersonVo = item.person;
            mCurPerson = item;
            mCurPerson.isScanSlt = false;
            if (groupPosition > 0) {
                if (groupPosition == 0) {
                    importRecord(item);
                } else if (groupPosition == 1) {
                    getHisttory(item.ZYH);
                }

            } else {
                freshPanelPage();
            }

            return true;
        }

    };

    // 获取当天巡视记录历史数据
    private void getHisttory(String zyh) {
        GetHistoryTask hsTask = new GetHistoryTask();
        tasks.add(hsTask);
        hsTask.execute(zyh);
    }

    class GetHistoryTask extends AsyncTask<String, Void, Response<List<VisitHistory>>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Response<List<VisitHistory>> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();

            if (result == null) {
                if (Constant.DEBUG_LOCAL) {
                    Log.e(Constant.TAG, "获取巡视记录历史数据失败");
                }
                return;
            }

            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication).showLoginDialog();
                return;
            } else if (result.ReType == 0) {
                ArrayList<VisitHistory> history = (ArrayList<VisitHistory>) result.Data;
                if (history != null) {
                    historyAdapter = new VisitHistoryAdapter(getActivity(), history);
                    historyList.setAdapter(historyAdapter);
                } else {
                    historyList.setAdapter(null);
                }


            } else {
                showMsgAndVoice(result.Msg);
            }

            if (tasks.contains(this)) {
                tasks.remove(this);
            }
        }

        @Override
        protected Response<List<VisitHistory>> doInBackground(String... params) {
            if (params == null || params.length < 1
                    || EmptyTool.isBlank(params[0])) {
                return null;
            }
            String zyh = params[0];

            String nowDate = DateTimeHelper.getServerDate();
            String xsrq = nowDate;
            String jgid = mAppApplication.jgId;

            return VisitApi.getInstance(getActivity()).GetPatrolHistory(zyh,
                    xsrq, jgid, Constant.sysType);
        }

    }

    /**
     * 主要包括group
     */
    public void freshPanelPage() {

        boolean checked = false;

        if (mRadioGroup.getChildCount() > 0) {

            int count = mRadioGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                AppCompatRadioButton rbtn = (AppCompatRadioButton) mRadioGroup.getChildAt(i);
                int notUserd = (Integer) rbtn.getTag();
                if (notUserd == 1) {
                    rbtn.setVisibility(View.GONE);
                } else if (!checked) {
                    mRadioGroup.check(rbtn.getId());
                    checked = true;
                }
            }
        }

    }

    /**
     * 导入数据
     *
     * @param person
     */
    private void importRecord(VisitPerson person) {

        if (person == null) {
            return;
        }
        showDisableRadioButton();
        mRadioGroup.check(person.XSQK);
    }

    private void showDisableRadioButton() {
        if (mRadioGroup.getChildCount() > 0) {

            int count = mRadioGroup.getChildCount();
            for (int i = 0; i < count; i++) {
                AppCompatRadioButton rbtn = (AppCompatRadioButton) mRadioGroup.getChildAt(i);
                int notUserd = (Integer) rbtn.getTag();
                if (notUserd == 1) {
                    rbtn.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    private void initActoin() {

        actionBar.setTitle("日常巡视");
        actionBar.addAction(new Action() {
            @Override
            public String getText() {
                return "保存";
            }

            @Override
            public void performAction(View view) {
                if (previousGroup < 1) {
                    int count = mListView
                            .getCheckedItemPosition();
                    if (count == AdapterView.INVALID_POSITION
                            || mCurPerson == null) {
                        //	VibratorUtil.vibratorMsg(vib, "请选择病人", getActivity());
                        showMsgAndVoiceAndVibrator("请选择病人");
                        return;
                    }
                    String lxbs = getlxbs();
                    String zyh = mCurPerson.ZYH;
                    String brbq = mAppApplication.getAreaId();
                    saveVisitRecord(SaveTask.MANUAL_SAVE, zyh, lxbs, brbq);
                } else {
//					VibratorUtil.vibratorMsg(vib, "要巡视病人请点击待巡视", getActivity());
                    showMsgAndVoiceAndVibrator("要巡视病人请点击【待巡病人】");
                }

            }

            @Override
            public int getDrawable() {

                return R.drawable.ic_done_black_24dp;
            }
        });
    }

    /**
     * 病人列表异步加载
     */
    class GetDataTask extends AsyncTask<Void, Void, Response<VisitCount>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<VisitCount> doInBackground(Void... params) {

            String urid = mAppApplication.user.YHID;
            String ksdm = mAppApplication.getAreaId();
            String jgid = mAppApplication.jgId;

            return VisitApi.getInstance(getActivity()).GetPatrol(urid, ksdm,
                    jgid, Constant.sysType);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Response<VisitCount> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();

            if (result == null) {
                if (Constant.DEBUG_LOCAL) {
                    Log.e(Constant.TAG, " GetDataTask GetDataTask onPostExecute");
                }
                return;
            }

            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication).showLoginDialog();
                return;
            } else if (result.ReType == 0) {
                ArrayList<VisitPerson> wait = (ArrayList<VisitPerson>) result.Data.xyxs;
                ArrayList<VisitPerson> visit = (ArrayList<VisitPerson>) result.Data.yjxs;
                ArrayList<CheckState> states = (ArrayList<CheckState>) result.Data.xsqk;

                // adapter.addData(list, isShow);
                initPersonList(wait, visit);
                initCheckGroup(states);

            } else {
                showMsgAndVoice(result.Msg);
            }

            if (tasks.contains(this)) {
                tasks.remove(this);
            }
        }

    }

    /**
     * 初始化列表，已巡列表为空
     *
     * @param wait
     * @param visited
     */
    public void initPersonList(ArrayList<VisitPerson> wait,
                               ArrayList<VisitPerson> visited) {

        if (wait == null && visited == null) {
            return;
        }

        String[] groups = {"待巡病人", "已巡病人"};
        VisitPerson[][] childs = new VisitPerson[2][];

        if (wait != null) {
            childs[0] = wait.toArray(new VisitPerson[wait.size()]);
        }

        if (visited != null) {
            childs[1] = visited.toArray(new VisitPerson[visited.size()]);
        }

        adapter = new VisitAdapter(groups, childs, getActivity());
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                TextView ListHeader = (TextView) v.findViewById(R.id.id_tv);
                ListHeader.setSelected(!ListHeader.isSelected());
                return false;
            }
        });
        mListView.setAdapter(adapter);
        mListView.expandGroup(0);

    }

    public class SaveTask extends AsyncTask<String, Integer, Response<List<VisitPerson>>> {

        // private VisitResult result;
        @Deprecated
        public static final byte SCAN_SAVE = 1;
        public static final byte MANUAL_SAVE = 0;

        private byte mtype = MANUAL_SAVE;

        private String zyh;

        public SaveTask(byte mtype) {
            this.mtype = mtype;
        }

        @Override
        protected Response<List<VisitPerson>> doInBackground(String... params) {

            if (params == null || params.length < 3
                    || EmptyTool.isBlank(params[0])
                    || EmptyTool.isBlank(params[1])
                    || EmptyTool.isBlank(params[2])) {
                return null;
            }

            zyh = params[0];
            String xsqk = params[1];
            String brbq = params[2];
            String urid = mAppApplication.user.YHID;
            String jgid = mAppApplication.jgId;

            // 调用保存接口
            VisitApi api = VisitApi.getInstance(getActivity());
          /*  if (mtype == SCAN_SAVE) {
                return api.SetPatrolForScan(brbq, urid, zyh, xsqk, jgid,
                        Constant.sysType);
            } else if (mtype == MANUAL_SAVE) {
                return api.SetPatrol(brbq, urid, zyh, xsqk, jgid,
                        Constant.sysType);
            }*/
            //入口都改成手动执行MANUAL_SAVE，改用 mCurPerson.isScanSlt 标志
            if (mtype == MANUAL_SAVE) {
                String isScan = mCurPerson.isScanSlt ? "1" : "0";
                    return api.SetPatrol(isScan,brbq, urid, zyh, xsqk, jgid,
                            Constant.sysType);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            showLoadingDialog(getResources().getString(R.string.saveing));
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Response<List<VisitPerson>> result) {
            hideLoadingDialog();
            if (result == null) {
                if (Constant.DEBUG_LOCAL) {
                    Log.e(Constant.TAG, " DailyTourFragment SaveTask onPostExecute");
                }
                return;
            }

            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication).showLoginDialog();
                return;
            } else if (result.ReType == 0) {
                // ArrayList<VisitPerson> wait = result.getList("Table1");
                ArrayList<VisitPerson> visit = (ArrayList<VisitPerson>) result.Data;
                if (adapter != null) {
                    adapter.changeItem(0, zyh, 1, visit);
                    mListView.setItemChecked(-1, true);
                }
                showMsgAndVoice("执行成功！");
            } else {
                showMsgAndVoice(result.Msg);
            }

            // if (adapter != null && previousGroup == 0) {
            // mListView.setItemChecked(-1, true);
            // }
            tasks.remove(this);
        }
    }

    private void initCheckGroup(ArrayList<CheckState> states) {

        if (states == null) {
            return;
        }

        // 根据X排序
        Collections.sort(states, new SortByZFBZ());

        for (CheckState state : states) {

            int id = state.DYXH;
            AppCompatRadioButton rb = (AppCompatRadioButton) mRadioGroup.findViewById(id);
            if (rb == null) {
                rb = new AppCompatRadioButton(getActivity());

                rb.setId(state.DYXH);
                if (state.ZFBZ == 1) {
                    rb.setVisibility(View.GONE);
                    rb.setClickable(false);
                    rb.setText(state.DYMS + "-已作废");
                } else {
                    rb.setText(state.DYMS);
                }
                rb.setTag(state.ZFBZ);
                mRadioGroup.addView(rb);
            }
        }
        freshPanelPage();
    }

    public static class SortByZFBZ implements Comparator<CheckState> {

        @Override
        public int compare(CheckState obj1, CheckState obj2) {

            if (obj1.ZFBZ > obj2.ZFBZ) {
                return 1;
            } else if (obj1.ZFBZ == obj2.ZFBZ) {
                return 0;
            } else {
                return -1;
            }

        }
    }
}
