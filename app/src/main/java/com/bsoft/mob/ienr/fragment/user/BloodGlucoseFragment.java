package com.bsoft.mob.ienr.fragment.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.BloodGlucoseHistoryActivity;
import com.bsoft.mob.ienr.adapter.DingBaseAdapter;
import com.bsoft.mob.ienr.api.BloodGlucoseApi;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.bloodglucose.BGSavePostData;
import com.bsoft.mob.ienr.model.bloodglucose.BloodGlucoseDetail;
import com.bsoft.mob.ienr.model.bloodglucose.BloodGlucoseRecord;
import com.bsoft.mob.ienr.model.bloodglucose.GlucoseTime;
import com.bsoft.mob.ienr.model.bloodglucose.GlucoseTimeData;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;
import com.bsoft.mob.ienr.view.floatmenu.menu.IFloatMenuItem;
import com.bsoft.mob.ienr.view.floatmenu.menu.TextFloatMenuItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ding.pengqiang
 * on 2016/12/28.
 */
@Deprecated //废弃
public class BloodGlucoseFragment extends BaseUserFragment {

    // 眉栏工具条

    //血糖按钮
    private Button mBtn1;
    private ImageView mImg1;
    private ListView mRefresh1;


    //胰岛素按钮
    private Button mBtn2;
    private ImageView mImg2;
    private ListView mRefresh2;

    // 当前选择的模块，刷新时使用
    int current = 1;

    //
    private Spinner mTimeSpinner;

    //当天日期
    private String eTimeStr;
    //时间段
    private String xmxh;
    private String xmnr;
    int xh = -1;
    private ArrayAdapter<String> adapter;
    private GlucoseAdapter adapter1;
    private GlucoseCompleteAdapter adapter2;
    private InsulinAdapter adapter3;
    private InsulinCompleteAdapter adapter4;

    private LinearLayout layout;
    private LinearLayout mTemporary;
    private EditText mInput;
    private TextView mUnit;
    private ImageView mSearch;

    private BloodGlucoseDetail detail;
    private ArrayList<GlucoseTime> glucosetime;
    private BloodGlucoseRecord recordData;


    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_bloodsugar_monitor;
    }

    @Override
    protected void initView(View rootLayout, Bundle savedInstanceState) {
        SpinnerLayout spinnerLayout = (SpinnerLayout) rootLayout.findViewById(R.id.id_spinner_layout);
        mTimeSpinner = spinnerLayout.getSpinner();

        mBtn1 = (Button) rootLayout.findViewById(R.id.glucose_but1);
        mBtn2 = (Button) rootLayout.findViewById(R.id.glucose_but2);
        mImg1 = (ImageView) rootLayout.findViewById(R.id.glucose_img1);
        mImg2 = (ImageView) rootLayout.findViewById(R.id.glucose_img2);

        mRefresh1 = (ListView) rootLayout.findViewById(R.id.id_lv);
        mRefresh2 = (ListView) rootLayout.findViewById(R.id.id_lv_2);
        mSearch = (ImageView) rootLayout.findViewById(R.id.glucose_search);

//        layout = LinearLayout)rootView.findViewById(R.id.glucose_layout) ;
        mInput = (EditText) rootLayout.findViewById(R.id.item_batch_glucose_input);
        mUnit = (TextView) rootLayout.findViewById(R.id.item_batch_glucose_unit);

        mRefresh2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (mBtn2.isSelected()) {
                    return false;
                }
                final BloodGlucoseDetail itemAtPosition = (BloodGlucoseDetail) parent.getItemAtPosition(position);
                new AlertDialog.Builder(getActivity())
                        //.setTitle("提示！")
                        .setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(getActivity(), getString(R.string.project_tips)))
                        .setMessage("确定要删除该记录吗？")
                        .setPositiveButton(getString(R.string.project_operate_ok), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showDeleteDialog(itemAtPosition);
                            }
                        })
                        .setNegativeButton(getString(R.string.project_operate_cancel), null)
                        .show();


                return true;
            }
        });
        initActionBar();
        initBroadCast();
        initButtons();
        initImg();
//        initListView1();
//        initListView2();
        initTime();
    }


    private void showDeleteDialog(BloodGlucoseDetail itemAtPosition) {
        if (itemAtPosition.JHBZ.equals("0")) {

            /**
             * 记录工号是员工工号  才可以进行删除操作
             */
            if (itemAtPosition.JLGH.equals(mAppApplication.user.YHID)) {
                String mxxh = itemAtPosition.MXXH;
                deleteDetail(mxxh);
                if (xh == Integer.parseInt(itemAtPosition.XMXH)) {
                    xh = -1;
                }
                performGetForm(mAppApplication.sickPersonVo.ZYH);
            } else {
                showMsgAndVoiceAndVibrator("记录和操作用户不同，无法删除");
            }

        } else {
            showMsgAndVoiceAndVibrator("不是临时记录，无法删除！");
        }
    }


    private void initImg() {
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performGetForm(mAppApplication.sickPersonVo.ZYH);
            }
        });
    }

    private void initActionBar() {

        actionBar.setTitle("血糖 / 胰岛素");
        String brch = EmptyTool.isBlank(mAppApplication.sickPersonVo.XSCH) ? "" : mAppApplication.sickPersonVo.XSCH;
        actionBar.setPatient(brch + mAppApplication.sickPersonVo.BRXM);
        actionBar.setBackAction(new BsoftActionBar.Action() {

            @Override
            public void performAction(View view) {

                getActivity().finish();

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
        final BsoftActionBar.Action saveAction = new BsoftActionBar.Action() {

            @Override
            public void performAction(View view) {
                saveData();
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }
            @Override
            public String getText() {
                return "执行";
            }
        };
        actionBar.addAction(saveAction);
    }

    /**
     * 初始化AppBar
     */


    /**
     * 切换两个按钮加载不同的数据
     */
    private void initButtons() {
        mBtn1.setSelected(true);

        if (mBtn1.isSelected()) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    actionGetTimesTask();
                }
            }, 100);

        }

        mBtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (current != 1) {
                    current = 1;
                    actionGetTimesTask();
                    mBtn1.setSelected(true);
                    mBtn2.setSelected(false);
                    mImg1.setVisibility(View.VISIBLE);
                    mImg2.setVisibility(View.INVISIBLE);
                    mBtn1.setTextColor(getResources().getColor(
                            R.color.colorPrimaryDark));
                    mBtn2.setTextColor(getResources().getColor(
                            android.R.color.black));

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            performGetForm(mAppApplication.sickPersonVo.ZYH);
                        }
                    }, 1000);
                }
            }
        });
        mBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (current != 2) {
                    current = 2;
                    mBtn2.setSelected(true);
                    mBtn1.setSelected(false);
                    mImg2.setVisibility(View.VISIBLE);
                    mImg1.setVisibility(View.INVISIBLE);
                    mBtn2.setTextColor(getResources().getColor(
                            R.color.colorPrimaryDark));
                    mBtn1.setTextColor(getResources().getColor(
                            android.R.color.black));
                    actionGetTimesTask();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            performGetForm(mAppApplication.sickPersonVo.ZYH);
                        }
                    }, 1000);
                }
            }
        });

    }


    @Override
    protected List<IFloatMenuItem> configFloatMenuItems() {
        final int[] itemDrawables = {R.drawable.menu_fresh,
                R.drawable.menu_view, R.drawable.menu_add,
                R.drawable.menu_save};
        final int[][] itemStringDrawables = {
                {R.drawable.menu_fresh, R.string.comm_menu_refresh},
                {R.drawable.menu_view, R.string.comm_menu_view},
                {R.drawable.menu_add, R.string.comm_menu_add},
                {R.drawable.menu_save, R.string.comm_menu_save}};

        List<IFloatMenuItem> floatMenuItemList = new ArrayList<>();
      /*  for (int itemDrawableResid : itemDrawables) {
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
            int textResId=itemDrawableRes[1];
            String text = textResId > 0 ? getString(textResId) : null;
            IFloatMenuItem floatMenuItem = new TextFloatMenuItem(itemDrawableResid,text) {
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


        if (drawableRes == R.drawable.menu_fresh) {// 刷新
            performGetForm(mAppApplication.sickPersonVo.ZYH);
        } else if (drawableRes == R.drawable.menu_view) {// 历史
            Intent intent = new Intent(getActivity(),
                    BloodGlucoseHistoryActivity.class);
            startActivity(intent);
        } else if (drawableRes == R.drawable.menu_add) {
            if (current == 1) {
                if (xmnr != null) {
                    boolean isAdd = false;
                    if (!xmnr.equals("临时")) {
                        if (adapter1 != null) {

                            //首先判断时间是否是空
                            if (adapter != null) {
                                if (recordData.DETAILS.size() == 0) {


                                    if (xh != Integer.parseInt(xmxh)) {
                                        xh = Integer.parseInt(xmxh);
//                                        adapter1.addItem(xmnr,xmxh);
                                        addDetailTask(xmxh, xmnr);
                                    }
                                } else {
                                    showMsgAndVoiceAndVibrator("无法新增");
                                }

                            }
                        }
                    } else {
                        showMsgAndVoiceAndVibrator("无法新增");
                    }

                }
            } else {
                showMsgAndVoiceAndVibrator("没有权限");
//                layout.setVisibility(View.GONE);
            }

        } else if (drawableRes == R.drawable.menu_save) {
            saveData();
        }

    }


    /**
     * 设置当天时间
     */
    private void initTime() {
        String nowDate = DateTimeHelper.getServerDate();
        // 当天
        eTimeStr = nowDate;

    }

    // 获取时间列表
    private void actionGetTimesTask() {
        GetTimesTask task = new GetTimesTask();
        tasks.add(task);
        task.execute();
    }

    /**
     * 加载时间过滤列
     */
    private class GetTimesTask extends AsyncTask<Void, Void, Response<GlucoseTimeData>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<GlucoseTimeData> doInBackground(Void... params) {

            return BloodGlucoseApi.getInstance(getActivity()).getGlucoseTimes();
        }

        @Override
        protected void onPostExecute(Response<GlucoseTimeData> result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            actionGetTimesTask();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    GlucoseTimeData timeData = result.Data;
                    //血糖的时间点
                    glucosetime = (ArrayList<GlucoseTime>) timeData.GLUCOSETIME;
                    //胰岛素的时间点
                    ArrayList<GlucoseTime> insulintime = (ArrayList<GlucoseTime>) timeData.INSULINTIME;
                    if (mBtn1.isSelected()) {
                        importTimeList(glucosetime);
                    } else if (mBtn2.isSelected()) {
                        importTimeList(insulintime);
                    }

                } else {
                    showMsgAndVoice(result.Msg);
                    return;
                }
            } else {
                showMsgAndVoiceAndVibrator("加载时间列表失败");
                return;
            }
        }
    }


    public void importTimeList(final ArrayList<GlucoseTime> list) {

        if (list == null) {
            return;
        }
        ArrayList<String> time = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String xmnr = list.get(i).XMNR;
            time.add(xmnr);
        }
        adapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item, time);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTimeSpinner.setAdapter(adapter);
        mTimeSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                xmxh = list.get(position).XMXH;
                xmnr = list.get(position).XMNR;
                performGetForm(mAppApplication.sickPersonVo.ZYH);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        actionGetTask();
    }

    private void performGetForm(String zyh) {
        GetFormTask task = new GetFormTask();
        tasks.add(task);
        task.execute(zyh);
    }

    private void performGetRefresh(String zyh) {
        GetRefreshTask task = new GetRefreshTask();
        tasks.add(task);
        task.execute(zyh);
    }

    class GetRefreshTask extends AsyncTask<String, Void, Response<BloodGlucoseRecord>> {
        String zyh;

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
//            addView.removeAllViews();
        }

        @Override
        protected Response<BloodGlucoseRecord> doInBackground(String... params) {
            if (params == null || params.length < 1 || params[0] == null) {
                return null;
            }
            zyh = params[0];
//            String time = mTime.getText().toString();

            String cxpb = "1";

            String xmlx = "1";
            if (mBtn1.isSelected()) {
                xmlx = "1";

            } else if (mBtn2.isSelected()) {
                xmlx = "2";
            }
            zyh = mAppApplication.sickPersonVo.ZYH;
            String time = eTimeStr;
            String brbq = mAppApplication.getAreaId().toString();


            return BloodGlucoseApi.getInstance(getActivity())
                    .getUnexecutedBG(xmlx, zyh, time, brbq, xmxh);
        }

        @Override
        protected void onPostExecute(Response<BloodGlucoseRecord> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();
            tasks.remove(this);
//            layout.setVisibility(View.GONE);
            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication,
                            new AgainLoginUtil.LoginSucessListener() {
                                @Override
                                public void LoginSucessEvent() {
                                    performGetRefresh(zyh);
                                }
                            }).showLoginDialog();

                } else if (result.ReType == 0) {

                    BloodGlucoseRecord data = result.Data;

                    if (data.DETAILS != null && data.HISTORYS != null) {
                        mRefresh1.setVisibility(View.VISIBLE);
                        mRefresh2.setVisibility(View.VISIBLE);
                        importList(data);

                    } else {

//                        mRefresh2.setVisibility(View.INVISIBLE);
                        mRefresh1.setVisibility(View.INVISIBLE);
                        showMsgAndVoiceAndVibrator("没有可加载的数据...");

                        return;
                    }

                } else {
                    showMsgAndVoiceAndVibrator("请求错误");
//                    BloodGlucoseRecord data = result.Data;
//                    importList(data);
                   /* MediaUtil.getInstance(getActivity()).playSound(
                            R.raw.wrong, getActivity());*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }

        }

    }

    private void deleteDetail(String mxxh) {
        DeleteDetailTask task = new DeleteDetailTask();
        tasks.add(task);
        task.execute(mxxh);
    }

    class DeleteDetailTask extends AsyncTask<String, Void, Response<String>> {
        String zyh;

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
//            addView.removeAllViews();
        }

        @Override
        protected Response<String> doInBackground(String... params) {
            if (params == null || params.length < 1 || params[0] == null) {
                return null;
            }
            String mxxh = params[0];
            return BloodGlucoseApi.getInstance(getActivity())
                    .deleteDetail(mxxh);
        }

        @Override
        protected void onPostExecute(Response<String> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication,
                            new AgainLoginUtil.LoginSucessListener() {
                                @Override
                                public void LoginSucessEvent() {

                                    return;
                                }
                            }).showLoginDialog();

                } else if (result.ReType == 0) {
                    showMsgAndVoice(result.Msg);
                } else {
                    showMsgAndVoiceAndVibrator("请求错误");
//                    BloodGlucoseRecord data = result.Data;
//                    importList(data);
                /*    MediaUtil.getInstance(getActivity()).playSound(
                            R.raw.wrong, getActivity());*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }

        }

    }

    class GetFormTask extends
            AsyncTask<String, Void, Response<BloodGlucoseRecord>> {


        String zyh;

        @Override
        protected void onPreExecute() {
//            showSwipeRefreshLayout();
//            addView.removeAllViews();
        }

        @Override
        protected Response<BloodGlucoseRecord> doInBackground(String... params) {
            if (params == null || params.length < 1 || params[0] == null) {
                return null;
            }
            zyh = params[0];
//            String time = mTime.getText().toString();

            String cxpb = "1";

            String xmlx = "1";
            if (mBtn1.isSelected()) {
                xmlx = "1";

            } else if (mBtn2.isSelected()) {
                xmlx = "2";
            }
            zyh = mAppApplication.sickPersonVo.ZYH;
            String time = eTimeStr;
            String brbq = mAppApplication.getAreaId();
            String jgid = mAppApplication.jgId;
//            String xmxh = xmxh;
            return BloodGlucoseApi.getInstance(getActivity())
                    .getBGList(xmlx, zyh, time, brbq, jgid, xmxh);
        }

        @Override
        protected void onPostExecute(Response<BloodGlucoseRecord> result) {
            super.onPostExecute(result);
//            hideSwipeRefreshLayout();
            tasks.remove(this);
//            layout.setVisibility(View.GONE);
            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication,
                            new AgainLoginUtil.LoginSucessListener() {
                                @Override
                                public void LoginSucessEvent() {
                                    performGetForm(zyh);
                                }
                            }).showLoginDialog();

                } else if (result.ReType == 0) {

                    BloodGlucoseRecord data = result.Data;
                    recordData = result.Data;

                    if (data.DETAILS != null && data.HISTORYS != null) {
                        mRefresh1.setVisibility(View.VISIBLE);
                        mRefresh2.setVisibility(View.VISIBLE);
                        importList(data);
                    } else {

                        mRefresh2.setVisibility(View.INVISIBLE);
                        mRefresh1.setVisibility(View.INVISIBLE);
                        showMsgAndVoiceAndVibrator("没有可加载的数据...");

                        return;
                    }

                } else {
                    showMsgAndVoiceAndVibrator("请求错误");
//                    BloodGlucoseRecord data = result.Data;
//                    importList(data);
                    /*MediaUtil.getInstance(getActivity()).playSound(
                            R.raw.wrong, getActivity());*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }

        }


    }

    private void importList(BloodGlucoseRecord data) {
        List<BloodGlucoseDetail> details = data.DETAILS;
        List<BloodGlucoseDetail> historys = data.HISTORYS;

        if (historys == null) {
            return;
        }
        if (mBtn1.isSelected()) {

            if (details == null) {
                return;
            }
            adapter1 = new GlucoseAdapter(getActivity(), null, R.layout.item_list_batch_glucose);
            mRefresh1.setAdapter(adapter1);

            adapter1.updateRes(details);

            adapter2 = new GlucoseCompleteAdapter(getActivity(), null, R.layout.item_list_batch_glucose_complete);
            mRefresh2.setAdapter(adapter2);
            adapter2.updateRes(historys);

        } else if (mBtn2.isSelected()) {

            adapter3 = new InsulinAdapter(getActivity(), null, R.layout.item_list_check_batch_insulin);
            mRefresh1.setAdapter(adapter3);
            adapter3.addRes(details);

            adapter4 = new InsulinCompleteAdapter(getActivity(), null, R.layout.item_list_batch_insulin_complete);
            mRefresh2.setAdapter(adapter4);
            adapter4.addRes(historys);

        }


    }

    private void addDetailTask(String xmxh, String xmnr) {
        AddDetailTask task = new AddDetailTask();
        tasks.add(task);
        task.execute(xmxh, xmnr);
    }

    class AddDetailTask extends
            AsyncTask<String, Void, Response<BloodGlucoseDetail>> {


        String zyh;

        @Override
        protected void onPreExecute() {
//            showSwipeRefreshLayout();
//            addView.removeAllViews();
        }

        @Override
        protected Response<BloodGlucoseDetail> doInBackground(String... params) {
            if (params == null || params.length < 1 || params[0] == null) {
                return null;
            }
            String xmxh = params[0];
            String xmnr = params[1];
            zyh = mAppApplication.sickPersonVo.ZYH;
            String time = eTimeStr;
            String brbq = mAppApplication.getAreaId();
            String jgid = mAppApplication.jgId;
            return BloodGlucoseApi.getInstance(getActivity())
                    .addDetail(zyh, time, brbq, jgid, xmxh, xmnr);
        }

        @Override
        protected void onPostExecute(Response<BloodGlucoseDetail> result) {
            super.onPostExecute(result);
//            hideSwipeRefreshLayout();
            tasks.remove(this);
//            layout.setVisibility(View.GONE);
            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication,
                            new AgainLoginUtil.LoginSucessListener() {
                                @Override
                                public void LoginSucessEvent() {

                                }
                            }).showLoginDialog();

                } else if (result.ReType == 0) {
                    BloodGlucoseDetail data = result.Data;
                    if (adapter1 != null) {
                        adapter1.add(data);
                    }

                } else {
                    showMsgAndVoiceAndVibrator("请求错误");
                    xh = -1;
                  /*  MediaUtil.getInstance(getActivity()).playSound(
                            R.raw.wrong, getActivity());*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }

        }


    }

    private void saveData() {
        String mBtn1_isSelected = mBtn1.isSelected() ? "1" : "0";
        String mBtn2_isSelected = mBtn2.isSelected() ? "1" : "0";
        String mInput_txt = mInput.getText().toString();
        String adapter1_editText = adapter1.editText.getText().toString();
        String adapter3_title_isChecked = adapter3.title.isChecked() ? "1" : "0";


        SaveTask task = new SaveTask();
        tasks.add(task);
        task.execute(mBtn1_isSelected, mBtn2_isSelected, mInput_txt, adapter1_editText, adapter3_title_isChecked);
    }


    class SaveTask extends AsyncTask<String, Void, Response<BloodGlucoseRecord>> {
        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.doing);
        }

        protected Response<BloodGlucoseRecord> doInBackground(String... arg0) {

            if (arg0 == null || arg0.length < 1) {
                return null;
            }
            String mBtn1_isSelected = arg0[0];
            String mBtn2_isSelected = arg0[1];
            String mInput_txt = arg0[2];
            String adapter1_editText = arg0[3];
            String adapter3_title_isChecked = arg0[4];


            Response<BloodGlucoseRecord> response = new Response<>();
            response.ReType = -2;
            BloodGlucoseRecord record = new BloodGlucoseRecord();

            String data = "";
            BGSavePostData save = new BGSavePostData();

            save.ZYH = mAppApplication.sickPersonVo.ZYH;
            save.JGID = mAppApplication.jgId;
            save.BRBQ = mAppApplication.getAreaId();
            save.JHRQ = eTimeStr;
            if ("1".equals(mBtn1_isSelected)) {

                if (adapter1 != null) {
                    save.XMLX = "1";
                    save.XMXH = adapter1.xmxh;

                    if (adapter1.editText == null) {
                        if (xmnr.equals("临时")) {

                            String s = mInput_txt;
                            save.XMXH = "9";
                            save.XMLX = "1";

                        } else {
                            response.Msg = "没有数据可保存！";
                            return response;
                        }

                    }
                    if (EmptyTool.isBlank(adapter1_editText)) {
                        response.Msg = "请填写数据后再保存！";
                        return response;
                    }

                    detail.JHNR = adapter1_editText;
                    detail.JLGH = mAppApplication.user.YHID;
                }
            } else if ("1".equals(mBtn2_isSelected)) {

                save.XMLX = "2";
                save.XMXH = adapter3.xmxh;
                if (adapter3.title == null) {
                    response.Msg = "没有数据可保存！";
                    return response;
                }
                if ("1".equals(adapter3_title_isChecked)) {
                    detail.ZTBZ = "1";

                    detail.JLXM = mAppApplication.user.YHXM;
                    detail.JLGH = mAppApplication.user.YHID;
                } else {
                    response.Msg = "请选择后再保存！";
                    return response;
                }
            }

            ArrayList<BloodGlucoseDetail> bloodGlucoseDetails = new ArrayList<>();

            bloodGlucoseDetails.add(detail);

            save.DETAILS = bloodGlucoseDetails;
            if (!bloodGlucoseDetails.get(0).JLGH.equals(mAppApplication.user.YHID)) {
                showMsgAndVoiceAndVibrator("记录用户和操作用户不同，不能修改");
                return response;
            }
            try {
                data = JsonUtil.toJson(save);
//                data = URLEncoder.encode(data, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }

            response = BloodGlucoseApi.getInstance(getActivity())
                    .saveBloodGlucose(data);
            return response;
        }

        @Override
        protected void onPostExecute(Response<BloodGlucoseRecord> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);
//
            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            saveData();
                        }

                    }).showLoginDialog();
                } else if (result.ReType == 0) {
                    if (result.Data != null) {
                        showMsgAndVoice(result.Msg);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                performGetForm(mAppApplication.sickPersonVo.ZYH);
                            }
                        }, 100);
                    }
                } else {
                    showMsgAndVoice(result.Msg);
                   /* MediaUtil.getInstance(getActivity())
                            .playSound(R.raw.wrong, getActivity());*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }

    }

    public class GlucoseAdapter extends DingBaseAdapter<BloodGlucoseDetail> {

        EditText editText;
        TextView textView;
        private String jlxh;
        private String xmxh;
        private String mxxh;
        int index = -1;

        public GlucoseAdapter(Context context, List<BloodGlucoseDetail> data, int layoutResId) {
            super(context, data, layoutResId);

        }

        public void add(BloodGlucoseDetail item) {
            if (data != null) {
                data.add(item);
                notifyDataSetChanged();
            }
        }

        public void addItem(String xmnr, String xmxh) {

            BloodGlucoseDetail item = null;
            if (item == null) {
                item = new BloodGlucoseDetail();
                item.JLXH = jlxh;
                item.XMXH = xmxh;
                item.XMNR = xmnr;
                item.XMDW = "mmol/L";
                item.XMLX = "1";
                item.JHBZ = "0";
                data.add(item);
            }
            notifyDataSetChanged();

        }

        @Override
        protected void bindData(ViewHolder holder, BloodGlucoseDetail item) {
            editText = (EditText) holder.getView(R.id.item_batch_glucose_input);
            textView = (TextView) holder.getView(R.id.item_batch_glucose_unit);
            /**
             * 判断
             */
            if (EmptyTool.isBlank(item.JHNR)) {
                editText.setHint("请输入剂量");
            } else {
                editText.setText(item.JHNR);
            }

            textView.setText(item.XMDW);
            if (item != null) {
                jlxh = item.JLXH;
                xmxh = item.XMXH;
                mxxh = item.MXXH;

                detail = new BloodGlucoseDetail();
                detail = item;
            }


            solutionFocus(editText);


        }

        /**
         * 下面所有代码是解决ListView中，点击EditText弹出软键盘后失去焦点的问题
         */
        private void solutionFocus(EditText editText) {

            editText.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        index = 0;// 0 为position 当前就只有一个所以设置为0
                    }
                    return false;
                }
            });
            editText.clearFocus();
            if (index != -1 && index == 0) {
                // 如果当前的行下标和点击事件中保存的index一致，手动为EditText设置焦点。
                editText.requestFocus();
            }
            editText.setSelection(editText.getText().length());
        }
    }

    public class GlucoseCompleteAdapter extends DingBaseAdapter<BloodGlucoseDetail> {


        public GlucoseCompleteAdapter(Context context, List<BloodGlucoseDetail> data, int layoutResId) {
            super(context, data, layoutResId);

        }

        @Override
        protected void bindData(ViewHolder holder, BloodGlucoseDetail item) {
            TextView content = (TextView) holder.getView(R.id.item_batch_glucose_complete_content);
            TextView input = (TextView) holder.getView(R.id.item_batch_glucose_complete_input);
            TextView unit = (TextView) holder.getView(R.id.item_batch_glucose_complete_unit);

            TextView name = (TextView) holder.getView(R.id.item_batch_glucose_complete_username);
            TextView now = (TextView) holder.getView(R.id.item_batch_glucose_complete_nowtime);

            if (item.JHBZ.equals("0") && !item.XMXH.equals("9")) {
                content.setText(item.XMNR + "(临时)");
            } else {
                content.setText(item.XMNR);
            }

            input.setText(item.JHNR);
            unit.setText(item.XMDW);

            String jlxm = item.JLXM == null ? "" : item.JLXM;
            name.setText("执行护士:\t" + jlxm);
            String jlsj = item.JLSJ == null ? "" : item.JLSJ;
            now.setText("执行时间:\t" + jlsj);
        }
    }

    public class InsulinAdapter extends DingBaseAdapter<BloodGlucoseDetail> {

        CheckBox title;
        private String jlxh;
        private String xmxh;

        public InsulinAdapter(Context context, List<BloodGlucoseDetail> data, int layoutResId) {
            super(context, data, layoutResId);

        }

        @Override
        protected void bindData(ViewHolder holder, BloodGlucoseDetail item) {
            title = (CheckBox) holder.getView(R.id.item_batch_insulin_title);
//            TextView number = (TextView) holder.getView(R.id.item_batch_insulin_number);
            TextView input = (TextView) holder.getView(R.id.item_batch_insulin_input);
            TextView unit = (TextView) holder.getView(R.id.item_batch_insulin_unit);
            if (item != null) {
                title.setText(item.YDSMC + "\t\t");
                input.setText("剂量：" + item.JHNR + "\t\t");
                unit.setText(item.XMDW);
                jlxh = item.JLXH;
                xmxh = item.XMXH;

                detail = new BloodGlucoseDetail();
                detail = item;
            }


        }
    }

    public class InsulinCompleteAdapter extends DingBaseAdapter<BloodGlucoseDetail> {


        public InsulinCompleteAdapter(Context context, List<BloodGlucoseDetail> data, int layoutResId) {
            super(context, data, layoutResId);

        }

        @Override
        protected void bindData(ViewHolder holder, BloodGlucoseDetail item) {
            TextView time = (TextView) holder.getView(R.id.item_batch_insulin_complete_time);
            TextView title = (TextView) holder.getView(R.id.item_batch_insulin_complete_title);
            TextView input = (TextView) holder.getView(R.id.item_batch_insulin_complete_number);
            TextView name = (TextView) holder.getView(R.id.item_batch_insulin_complete_username);
            TextView now = (TextView) holder.getView(R.id.item_batch_insulin_complete_nowtime);
            time.setText(item.XMNR);
            title.setText(item.YDSMC);
            input.setText(item.JHNR + "\t\t" + item.XMDW);
            String jlxm = item.JLXM == null ? "" : item.JLXM;
            name.setText("执行护士:\t" + jlxm);
            String jlsj = item.JLSJ == null ? "" : item.JLSJ;
            now.setText("执行时间:\t" + jlsj);
        }
    }


    private void initBroadCast() {

        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {

            }
        };
    }


}
