package com.bsoft.mob.ienr.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.adapter.SickerSkinTestAdapter;
import com.bsoft.mob.ienr.adapter.SickerSkinTestHistoryAdapter;
import com.bsoft.mob.ienr.api.SkinTestApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.fragment.base.LeftMenuItemFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.ListViewScrollHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.helper.TestDataHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.kernel.SickPersonVo;
import com.bsoft.mob.ienr.model.skintest.SickerPersonSkinTest;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;
import com.classichu.dialogview.listener.OnBtnClickListener;
import com.classichu.dialogview.manager.DialogManager;
import com.classichu.dialogview.ui.ClassicDialogFragment;

import java.util.ArrayList;
import java.util.List;


/**
 * 皮试
 */
public class SkinTestFragment extends LeftMenuItemFragment {


    private ListView mPersonListView;
    private ListView mHistoryListView;


    private SickerSkinTestAdapter mSickerSkinTestAdapter;
    private SickerSkinTestHistoryAdapter mSickerSkinTestHistoryAdapter;
    private String nowSelectedSickerZyh;
    private SickerPersonSkinTest nowSickerPersonSkinTest;
    private BarcodeEntity nowBarcodeEntity;
    private boolean isScan;

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_skin_test;
    }

    private boolean isAll = false;

    @Override
    protected void initView(View root, Bundle savedInstanceState) {

        mPersonListView = (ListView) root
                .findViewById(R.id.id_lv);

        mHistoryListView = (ListView) root
                .findViewById(R.id.id_lv_2);
        EmptyViewHelper.setEmptyView(mPersonListView, "mPersonListView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mPersonListView);

        EmptyViewHelper.setEmptyView(mHistoryListView, "mHistoryListView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mHistoryListView);

        TextView id_tv = (TextView) root
                .findViewById(R.id.id_tv);
        id_tv.setText("筛选");

        CheckBox id_cb = (CheckBox) root
                .findViewById(R.id.id_cb);
        id_cb.setText("筛选");

        CheckBox id_cb_2 = (CheckBox) root
                .findViewById(R.id.id_cb_2);

        id_tv.setText("筛选");
        id_cb.setText("待皮试");
        id_cb_2.setText("全部");
        id_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView == null || !buttonView.isPressed()) {
                    //不响应非点击引起的改变
                    return;
                }
                if (isChecked) {
                    id_cb_2.setChecked(false);
                    isAll = false;
                    toRefreshData();
                }
            }
        });
        id_cb_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView == null || !buttonView.isPressed()) {
                    //不响应非点击引起的改变
                    return;
                }
                if (isChecked) {
                    id_cb.setChecked(false);
                    isAll = true;
                    toRefreshData();
                }
            }
        });


        initActionBar();
        initPersonListView();

        initBroadCast();
        //
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
                    if (entity.TMFL == 8) {
                        nowBarcodeEntity = entity;
                        doPsOper_Scan();
                    } else if (FastSwitchUtils.needFastSwitch(entity)) {
                        if (mAppApplication.sickPersonVo == null) {
                            Log.e("tag", "sickPersonVo is null");
                            return;
                        }
                        FastSwitchUtils.fastSwith(getActivity(), entity);
                    } else {
                        Log.e("tag", "onReceive: no-op");
                    }

                }
            }
        };
    }

    private void doPsOper_Scan() {

        if (mPersonListView == null || mSickerSkinTestHistoryAdapter == null) {
            showMsgAndVoice("皮试列表数据未就绪，请重试");
            return;
        }
        if (mPersonListView.getCheckedItemPosition() < 0) {
            showMsgAndVoiceAndVibrator("请选择病人");
            return;
        }
        String barcode = nowBarcodeEntity.TMQZ + nowBarcodeEntity.TMNR;

        int pos = -1;
        for (int i = 0; i < mSickerSkinTestHistoryAdapter.getCount(); i++) {
            if (barcode.equals(mSickerSkinTestHistoryAdapter.getItem(i).PSTM)) {
                //
                pos = i;
                break;
            }
        }
        if (pos > -1) {
            nowSickerPersonSkinTest = mSickerSkinTestHistoryAdapter.getItem(pos);
            //
            isScan = true;
            doPsOper();
        } else {
            showMsgAndVoice("皮试列表数据不存在该条码!");
        }
    }

    /**
     * 扫描定位病人,并查询医嘱
     */
    public void scanSltPerson() {

        SickPersonVo person = mAppApplication.sickPersonVo;
        if (person == null || mSickerSkinTestAdapter == null) {
           /* VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "扫描的病人不在此列表中", getActivity());*/
            showMsgAndVoiceAndVibrator("扫描的病人不在此列表中");
            return;
        }
        int position = mSickerSkinTestAdapter.getPersonPostion(person.ZYH);
        if (position == -1) {
           /* VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "扫描的病人不在此列表中", getActivity());*/
            showMsgAndVoiceAndVibrator("扫描的病人不在此列表中");
            return;
        }
        ListViewScrollHelper.smoothScrollToPosition(mPersonListView, position);
        //
        personSelected(position);
    }


    private void initPersonListView() {
        mPersonListView.setTextFilterEnabled(true);
        // checked/activated
        mPersonListView.setChoiceMode(
                AbsListView.CHOICE_MODE_SINGLE);
        mPersonListView.setOnItemClickListener(onPersonItemClickListener);
    }

    public OnItemClickListener onPersonItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            personSelected(position);
        }
    };

    private void initActionBar() {
        actionBar.setTitle("皮试管理");
      /*  actionBar.addAction(new Action() {
            @Override
            public String getText() {
                return "皮试";
            }

            @Override
            public void performAction(View view) {
                *//*onSaveAction(ExecutTask.HANDLE_EXCUTE, Boolean.toString(false),
                        Boolean.toString(true));*//*
//                showPS_End_Dialog();
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_mode_edit_black_24dp;
            }
        });*/
    }

    private ClassicDialogFragment dialogFragment;

    private void showPS_End_Dialog() {
        if (mPersonListView.getCheckedItemPosition() < 0) {
            showMsgAndVoiceAndVibrator("请先选择病人");
            return;
        }

        if (dialogFragment != null) {
            dialogFragment.dismiss();
        }
        View layout_root = LayoutInflater.from(mContext).inflate(R.layout.layout_skintest, null, false);
        SpinnerLayout id_spinner_layout = layout_root.findViewById(R.id.id_spinner_layout);
        Spinner spinner = id_spinner_layout.getSpinner();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext,
                android.R.layout.simple_spinner_item);
        //-1、阴性 1、阳性 0、未做 2、续注
//        arrayAdapter.add("未做");
        arrayAdapter.add("阴性");
        arrayAdapter.add("阳性");
        arrayAdapter.add("续注");
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        TextView titleTextView = ViewBuildHelper.buildDialogTitleTextView(mContext, "皮试结果记录");
        dialogFragment = new ClassicDialogFragment.Builder(mContext)
//                        .setTitle("皮试结果记录")
                .setCustomTitleView(titleTextView)
                .setContentView(layout_root)
                .setOkText(getString(R.string.project_operate_ok))
                .setCancelText(getString(R.string.project_operate_cancel))
                .setOnBtnClickListener(new OnBtnClickListener() {
                    @Override
                    public void onBtnClickOk(DialogInterface dialogInterface) {
                        super.onBtnClickOk(dialogInterface);
                        //     String PSJG = id_ed.getText().toString();
//                        String xxx2 = id_ed_2.getText().toString();
                        String PSJG = "0";
                        String sss = spinner.getSelectedItem().toString();
                        switch (sss) {
                            case "阴性":
                                PSJG = "-1";
                                break;
                            case "阳性":
                                PSJG = "1";
                                break;
                            /*case "未做":
                                PSJG = "0";
                                break;*/
                            case "续注":
                                PSJG = "2";
                                break;
                            default:
                        }
                        //结束
                        nowSickerPersonSkinTest.PSJG = PSJG;
                        nowSickerPersonSkinTest.JSGH = mAppApplication.user.YHID;
                        nowSickerPersonSkinTest.JSSJ = DateTimeHelper.getServerDateTime();
                        nowSickerPersonSkinTest.PSZT = "1";//已皮试
                        //保存
                        onSaveAction();

                    }

                })
                .build();
        dialogFragment.show(getChildFragmentManager(), "showPS_End_Dialog");

    }

    @Override
    protected void toRefreshData() {
        GetPersonTask task = new GetPersonTask();
        tasks.add(task);
        task.execute();
    }

    void onSaveAction() {
        SaveTask saveTask = new SaveTask();
        tasks.add(saveTask);
        saveTask.execute();
    }

    private class SaveTask extends AsyncTask<Void, Void, Response<String>> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<String> doInBackground(Void... params) {

            String json = null;
            try {
                json = JsonUtil.toJson(nowSickerPersonSkinTest);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return SkinTestApi.getInstance(mContext).saveSkinTest(json);
        }

        @Override
        protected void onPostExecute(Response<String> result) {
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
                    //保存成功
                    showMsgAndVoiceAndVibrator("保存成功");
                    //刷新
                    toRefreshData();
                } else {
                    showMsgAndVoice(result.Msg);
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
            }
        }
    }

   /* private class ScanExecuePsTask extends AsyncTask<Void, Void, Response<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<String> doInBackground(Void... params) {

            String json = null;
            try {
                json = JsonUtil.toJson(nowBarcodeEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return SkinTestApi.getInstance(mContext).scanExecuePs(json);
        }

        @Override
        protected void onPostExecute(Response<String> result) {
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
                    //保存成功
                    showMsgAndVoiceAndVibrator("执行成功");
                    //刷新
                    toRefreshData();
                } else {
                    showMsgAndVoice(result.Msg);
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
            }
        }
    }
*/

    /**
     * 病人列表异步加载
     */
    private class GetPersonTask extends AsyncTask<Void, Void, Response<List<SickerPersonSkinTest>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<SickerPersonSkinTest>> doInBackground(Void... params) {
            String areaId = mAppApplication.getAreaId();
            String jgid = mAppApplication.jgId;
            if (params == null) {
                return null;
            }
           /* if (params.length<0){
                return null;
            }*/
            String type = isAll ? "1" : "2";
            return SkinTestApi.getInstance(mContext).getSkinTest(null, type, areaId, jgid);
        }

        @Override
        protected void onPostExecute(Response<List<SickerPersonSkinTest>> result) {
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
                    List<SickerPersonSkinTest> list = result.Data;
                    if (EmptyTool.isEmpty(list)) {
                        list = new ArrayList<>();
                        TestDataHelper.buidTestData(SickerPersonSkinTest.class, list);
                        //##toastInfo("病人列表为空", Style.INFO, R.id.actionbar);
                    }
                    List<SickerPersonSkinTest> listNew = new ArrayList<>();
                    //去重复
                    for (SickerPersonSkinTest skinTest : list) {
                        if (!hasAdd(skinTest.ZYH, listNew)) {
                            listNew.add(skinTest);
                        }
                    }
                    importPersons(listNew);
                } else {
                    showMsgAndVoice(result.Msg);
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
            }
        }
    }

    private boolean hasAdd(String zyh, List<SickerPersonSkinTest> listNew) {
        if (TextUtils.isEmpty(zyh) || listNew == null) {
            return false;
        }
        for (SickerPersonSkinTest skinTest : listNew) {
            if (zyh.equals(skinTest.ZYH)) {
                return true;
            }
        }
        return false;
    }

    private class GetHistoryTask extends AsyncTask<Void, Void, Response<List<SickerPersonSkinTest>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<SickerPersonSkinTest>> doInBackground(Void... params) {
            String areaId = mAppApplication.getAreaId();
            String jgid = mAppApplication.jgId;
            if (params == null) {
                return null;
            }
           /* if (params.length<0){
                return null;
            }*/
            String type = isAll ? "1" : "2";
            String zyh = nowSelectedSickerZyh;
            return SkinTestApi.getInstance(mContext).getSkinTest(zyh, type, areaId, jgid);
        }

        @Override
        protected void onPostExecute(Response<List<SickerPersonSkinTest>> result) {
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
                    List<SickerPersonSkinTest> list = result.Data;
                    if (EmptyTool.isEmpty(list)) {
                        list = new ArrayList<>();
                        TestDataHelper.buidTestData(SickerPersonSkinTest.class, list);
                    }
                    importHistory(list);
                } else {
                    showMsgAndVoice(result.Msg);
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
            }
        }
    }


    private void importHistory(List<SickerPersonSkinTest> list) {
        if (list == null || list.size() < 1) {
            mSickerSkinTestHistoryAdapter = null;
        } else {
            mSickerSkinTestHistoryAdapter = new SickerSkinTestHistoryAdapter(getActivity(), list);
        }
        mHistoryListView.setAdapter(mSickerSkinTestHistoryAdapter);
        mHistoryListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //
                nowSickerPersonSkinTest = mSickerSkinTestHistoryAdapter.getItem(position);
                //
                isScan = false;
                doPsOper();
            }
        });
    }

    private void doPsOper() {
        //
        if ("1".equals(nowSickerPersonSkinTest.PSZT)) {
            showMsgAndVoiceAndVibrator("已皮试，不能操作");
            return;
        }
        if (nowSickerPersonSkinTest.PSZT == null || "0".equals(nowSickerPersonSkinTest.PSZT)) {
            if (isScan) {
                //扫描直接保存
                //开始
                nowSickerPersonSkinTest.KSGH = mAppApplication.user.YHID;
                nowSickerPersonSkinTest.KSSJ = DateTimeHelper.getServerDateTime();
                nowSickerPersonSkinTest.PSZT = "2";//皮试中
                //保存
                onSaveAction();
                //
            } else {
                //手动点击 未皮试 提示是否开始
                DialogManager.showClassicDialog(mFragmentActivity, "温馨提示", "是否确定开始皮试?", new OnBtnClickListener() {
                    @Override
                    public void onBtnClickOk(DialogInterface dialogInterface) {
//                    super.onBtnClickOk(dialogInterface);
                        //开始
                        nowSickerPersonSkinTest.KSGH = mAppApplication.user.YHID;
                        nowSickerPersonSkinTest.KSSJ = DateTimeHelper.getServerDateTime();
                        nowSickerPersonSkinTest.PSZT = "2";//皮试中
                        //保存
                        onSaveAction();
                    }
                });
            }
        } else if ("2".equals(nowSickerPersonSkinTest.PSZT)) {
            //皮试中
            //
            showPS_End_Dialog();

        }

    }

    private void importPersons(List<SickerPersonSkinTest> list) {
        //先清空右侧列表
        mSickerSkinTestHistoryAdapter = new SickerSkinTestHistoryAdapter(getActivity(), new ArrayList<>());
        mHistoryListView.setAdapter(mSickerSkinTestHistoryAdapter);
        //
        if (list == null || list.size() < 1) {
            mSickerSkinTestAdapter = null;
        } else {
            mSickerSkinTestAdapter = new SickerSkinTestAdapter(getActivity(), list);
        }
        mPersonListView.setAdapter(mSickerSkinTestAdapter);
        //选中之前选中的病人
        if (nowSelectedSickerZyh != null && list != null) {
            int position = -1;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).ZYH.equals(nowSelectedSickerZyh)) {
                    position = i;
                }
            }
            //add 2018-6-21 17:18:13
            if (position < 0 && list.size() > 0) {
                //当前选择的病人不再需要显示时候，且还存在病人，默认选择第一个病人
                position = 0;
            }
            //add 2018-6-21 17:18:13
            if (position >= 0) {
                //  定位病人
                ListViewScrollHelper.smoothScrollToPosition(mPersonListView, position);
                personSelected(position);
            }

        }
    }


  /*  private class HttpTask extends AsyncTask<Void, Void, Response<List<SickerPersonSkinTest>>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }
        @Override
        protected Response<List<SickerPersonSkinTest>> doInBackground(Void... params) {
            String areaId = mAppApplication.getAreaId();
            String jgid = mAppApplication.jgId;
            return SkinTestApi.getInstance(mContext).getSkinTest(null, areaId, jgid);
        }
        @Override
        protected void onPostExecute(Response<List<SickerPersonSkinTest>> result) {
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
                    List<SickerPersonSkinTest>list = result.Data;
                    if (EmptyTool.isEmpty(list)) {
                        list = new ArrayList<>();
                        TestDataHelper.buidTestData(SickerPersonSkinTest.class, list);
                        //##toastInfo("病人列表为空", Style.INFO, R.id.actionbar);
                    }
                    importPersons(list);
                } else {
                    showMsgAndVoice(result.Msg);
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
            }
        }
    }
*/


    void actionHttpTask() {
        //
        GetHistoryTask task = new GetHistoryTask();
        tasks.add(task);
        task.execute();

    }

    private void personSelected(int position) {

        mPersonListView.setItemChecked(position,
                true);
        //

        if (position == AdapterView.INVALID_POSITION) {
       /*     VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "请选择病人", getActivity());*/
            showMsgAndVoiceAndVibrator("请选择病人");
            return;
        }
        SickerPersonSkinTest skinTest = (SickerPersonSkinTest) mPersonListView
                .getAdapter().getItem(position);
        if (skinTest == null) {
         /*   VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "请选择病人", getActivity());*/
            showMsgAndVoiceAndVibrator("请选择病人");
            return;
        }
        //
        nowSelectedSickerZyh = skinTest.ZYH;
        actionHttpTask();

    }

}
