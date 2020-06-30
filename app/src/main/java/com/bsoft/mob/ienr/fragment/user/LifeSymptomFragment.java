package com.bsoft.mob.ienr.fragment.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.LifeSymptomInquiryActivity;
import com.bsoft.mob.ienr.activity.user.UserModelActivity;
import com.bsoft.mob.ienr.api.LifeSignApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.DateTimeTool;
import com.bsoft.mob.ienr.dynamicui.lifesymptom.LifeSymptomViewFactoryNew;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignSaveData;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignSaveDataItem;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignSync;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignTimeEntity;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignTypeItem;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.FormSyncUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;
import com.bsoft.mob.ienr.view.floatmenu.menu.IFloatMenuItem;
import com.bsoft.mob.ienr.view.floatmenu.menu.TextFloatMenuItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-11 上午11:20:14
 * @类说明 单个病人体征录入
 */
public class LifeSymptomFragment extends BaseUserFragment {

    private LinearLayout mainview;

    private LifeSymptomViewFactoryNew mLifeSymptomViewFactory;
    private View root;

    private Spinner mSpinner;

    // 体温小键盘支持 start01
    private KeyboardView keyboardView;

    // 体温小键盘支持 end01
    private boolean customIsSync;

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_life_symptom;
    }

    @Override
    protected void initView(View rootLayout, Bundle savedInstanceState) {
        root = rootLayout;
        mainview = (LinearLayout) root.findViewById(R.id.view);
        // 体温小键盘支持 start02
        keyboardView = (KeyboardView) root.findViewById(R.id.keyboard_view);
        // 体温小键盘支持 end02

        initActionBar();
        CheckBox id_cb_sp = (CheckBox) root
                .findViewById(R.id.id_cb_sp);
        id_cb_sp.setText("同步");
        id_cb_sp.setChecked(customIsSync);
        id_cb_sp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView == null || !buttonView.isPressed()) {
                    //不响应非点击引起的改变
                    return;
                }
                customIsSync = isChecked;
            }
        });
        SpinnerLayout startTimeLayout = (SpinnerLayout) root.findViewById(R.id.id_spinner_layout);
        mSpinner = startTimeLayout.getSpinner();
        // 体温小键盘支持 start04
        String yyyyMMddHHmm = DateTimeHelper.getServer_yyyyMMddHHmm00();
        mLifeSymptomViewFactory = new LifeSymptomViewFactoryNew(getActivity(), root, true,
                yyyyMMddHHmm , mAppApplication);
        // 体温小键盘支持 end04

        toRefreshData();
    }

    @Override
    protected void toRefreshData() {
        actionGetTimesTask();
    }

    public boolean isKeyboardShowing() {
        return keyboardView.getVisibility() == View.VISIBLE;
    }

    public void hideKeyboard() {
        keyboardView.setVisibility(View.GONE);
    }

    // 体温小键盘支持 end03


    @Override
    protected List<IFloatMenuItem> configFloatMenuItems() {
        final int[] itemDrawables = {R.drawable.menu_view,
                R.drawable.menu_fresh, R.drawable.menu_save};
        final int[][] itemStringDrawables = {
                {R.drawable.menu_view, R.string.comm_menu_view},
                {R.drawable.menu_fresh, R.string.comm_menu_refresh},
                {R.drawable.menu_save, R.string.comm_menu_save}};
        List<IFloatMenuItem> floatMenuItemList = new ArrayList<>();
   /*     for (int itemDrawableResid : itemDrawables) {
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
            int textResId = itemDrawableRes[1];
            String text = textResId > 0 ? getString(textResId) : null;
            IFloatMenuItem floatMenuItem = new TextFloatMenuItem(itemDrawableResid, text) {
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
            toRefreshData();
        } else if (drawableRes == R.drawable.menu_view) {// 历史
            Intent intent = new Intent(getActivity(),
                    LifeSymptomInquiryActivity.class);
            startActivity(intent);
        } else if (drawableRes == R.drawable.menu_save) {
            actionSaveTask();
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initBroadCast();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP
                        && keyCode == KeyEvent.KEYCODE_BACK) {

                    if (keyboardView.getVisibility() == View.GONE)
                        return false;
                    else {
                        keyboardView.setVisibility(View.GONE);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private void initActionBar() {

        actionBar.setTitle("体征录入");
        actionBar.setPatient(mAppApplication.sickPersonVo.BRCH
                + mAppApplication.sickPersonVo.BRXM);


        final Action saveAction = new Action() {

            @Override
            public void performAction(View view) {
                actionSaveTask();
            }

            @Override
            public String getText() {
                return "保存";
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }
        };
        actionBar.addAction(saveAction);

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


    // 获取时间列表
    private void actionGetTimesTask() {
        GetTimesTask task = new GetTimesTask();
        tasks.add(task);
        task.execute();
    }

    public void importTimeList(ArrayList<LifeSignTimeEntity> list) {

        if (list == null) {
            return;
        }
        ArrayAdapter<LifeSignTimeEntity> adapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_spinner_item);
        adapter.addAll(list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
            /*
        升级编号【56010031】============================================= start
        默认选中最近的时间点，通过时间点获取数据
        ================= Classichu 2017/11/20 17:23
        */
        //选择当前最近的一个时间点
        int minC_pos = getNearTimePos(list);
        mSpinner.setSelection(minC_pos);
        /* =============================================================== end */
        actionGetTask();
    }

    /*
    升级编号【56010031】============================================= start
    默认选中最近的时间点，通过时间点获取数据
    ================= Classichu 2017/11/20 17:23
    */
    private int getNearTimePos(ArrayList<LifeSignTimeEntity> list) {
        //milliseconds
        long millisecondsCut = DateTimeTool.compareTo(DateTimeHelper.getServerDateTime(), DateTimeHelper.getServerDate() + " 00:00:00");
        int cutM = (int) (millisecondsCut / (1000 * 60));
        int minC_pos = 0;
        int minC = -1;
        for (int i = 0; i < list.size(); i++) {
            int dis = Math.abs(list.get(i).VALUE - cutM);
            minC = minC == -1 ? dis : minC;
            if (minC > dis) {
                minC = dis;
                minC_pos = i;
            }
        }
        return minC_pos;
    }

    /* =============================================================== end */
    void hidden() {
        if (null != getActivity().getCurrentFocus()
                && null != getActivity().getCurrentFocus().getWindowToken()) {
            ((InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    class GetTask extends AsyncTask<Void, Void, Response<ArrayList<LifeSignTypeItem>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //###  showSwipeRefreshLayout();
            mainview.removeAllViews();
        }

        @Override
        protected Response<ArrayList<LifeSignTypeItem>> doInBackground(Void... arg0) {

            Response<ArrayList<LifeSignTypeItem>> type = LifeSignApi.getInstance(
                    getActivity()).getLifeSignTypeItemList(mAppApplication.sickPersonVo.ZYH,
                    mAppApplication.getAreaId(), mAppApplication.jgId, Constant.sysType);
            return type;
        }

        @Override
        protected void onPostExecute(Response<ArrayList<LifeSignTypeItem>> sresult) {
            super.onPostExecute(sresult);
            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (null != sresult) {
                if (sresult.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            actionGetTask();
                        }
                    }).showLoginDialog();
                    return;
                } else if (sresult.ReType == 0) {
                    if (mAppApplication.sickPersonVo != null) {
                        View result = mLifeSymptomViewFactory.build(sresult.Data,
                                mAppApplication.sickPersonVo.ZYH, mAppApplication.jgId);
                        mainview.addView(result);
                        result.setOnTouchListener(new OnTouchListener() {
                            @Override
                            public boolean onTouch(View arg0, MotionEvent arg1) {
                                hidden();
                                mLifeSymptomViewFactory.hidden();
                                return false;
                            }
                        });
                    }
                } else {
                    showMsgAndVoice(sresult.Msg);
                }
            }

        }
    }

    private void actionGetTask() {
        GetTask task = new GetTask();
        tasks.add(task);
        task.execute();
    }

    /**
     * 加载时间过滤列
     */
    private class GetTimesTask extends AsyncTask<Void, Void, Response<List<LifeSignTimeEntity>>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<LifeSignTimeEntity>> doInBackground(Void... params) {

            String bqid = mAppApplication.getAreaId();
            String jgid = mAppApplication.jgId;
            int sysType = Constant.sysType;
            LifeSignApi api = LifeSignApi.getInstance(getActivity());
            return api.GetTimePointList(bqid, jgid, sysType);
        }

        @Override
        protected void onPostExecute(Response<List<LifeSignTimeEntity>> result) {

            //####hideSwipeRefreshLayout();
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
                    ArrayList<LifeSignTimeEntity> list = (ArrayList<LifeSignTimeEntity>) result.Data;
                    LifeSignTimeEntity first = new LifeSignTimeEntity();
                    first.NAME = "";
                    first.VALUE = 0;
                    list.add(0, first);
                    importTimeList(list);
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

    class SaveTask extends AsyncTask<Void, Void, Response<LifeSignSync>> {

        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.saveing);
        }

        @Override
        protected Response<LifeSignSync> doInBackground(Void... params) {

            if (mAppApplication.user == null || mAppApplication.sickPersonVo == null
                    || mLifeSymptomViewFactory == null) {
                return null;
            }

            Response<LifeSignSync> response = new Response<>();
            response.ReType = -2;

            List<LifeSignSaveDataItem> itemList = mLifeSymptomViewFactory.getRealSaveData();
            if (null == itemList || itemList.size() == 0) {
                response.Msg = "没有要保存的数据";
                return response;

            }
            String data = "";
            LifeSignSaveData lifeSignSaveData = new LifeSignSaveData();
            try {
                lifeSignSaveData.URID = mAppApplication.user.YHID;
                lifeSignSaveData.BQID = mAppApplication.getAreaId();
                lifeSignSaveData.ZYH = mAppApplication.sickPersonVo.ZYH;
                lifeSignSaveData.IsTemp = mLifeSymptomViewFactory.isTemp();
                lifeSignSaveData.TempTime = mLifeSymptomViewFactory.getTmpTime();
                lifeSignSaveData.JGID = mAppApplication.jgId;
                lifeSignSaveData.lifeSignSaveDataItemList = itemList;
                lifeSignSaveData.customIsSync = customIsSync;
                data = JsonUtil.toJson(lifeSignSaveData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            LifeSignApi api = LifeSignApi.getInstance(getActivity());
            response = api.lifeSymptomSave(data);
            return response;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Response<LifeSignSync> result) {
            hideLoadingDialog();
            tasks.remove(this);
            if (result != null) {
                if (result.ReType == -2) {
                    showMsgAndVoice(result.Msg);
                } else if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            actionSaveTask();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    showMsgAndVoice(R.string.project_save_success);
                    //todo 暂未实现同步模块
//					if (result.tableMap.containsKey("BCXX")) {
//						List<SyncBoolean> isSync = result.getList("BCXX");
//						boolean sync = isSync.get(0).IsSync == 0 ? false : true;
//						if (sync) {
//							if (result.tableMap.containsKey("TBXX")) {
//								List<SyncRecord> list = result.getList("TBXX");
//								new FormSyncUtil().InvokeAsync(getActivity(), list,
//										application.jgId, tasks);
//							}
//						}
//					}
                    LifeSignSync lifeSignSync = result.Data;
                    if (lifeSignSync.IsSync) {
                        FormSyncUtil syncUtil = new FormSyncUtil();
                        syncUtil.setOnDialogClickListener(
                                new FormSyncUtil.onCancelClickListener() {

                                    @Override
                                    public void onCancel() {

                                    }
                                }, new FormSyncUtil.onConfirmClickListener() {

                                    @Override
                                    public void onConfirm() {

                                    }
                                });
                        syncUtil.InvokeSync(getActivity(),
                                lifeSignSync.SyncData, mAppApplication.jgId, tasks);

                    }
                    // 数据情空
                    mLifeSymptomViewFactory.clearData();
                } else {
                    showMsgAndVoiceAndVibrator("保存数据失败!");
                }
            } else {
                showMsgAndVoiceAndVibrator("保存数据失败!");
            }
        }
    }

    private void actionSaveTask() {

        Object obj = mSpinner.getSelectedItem();
        mLifeSymptomViewFactory.setTimeValue(obj);

        SaveTask task = new SaveTask();
        tasks.add(task);
        task.execute();
    }


}
