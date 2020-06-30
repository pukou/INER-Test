/**
 * @Title: LifeSymptomActivity.java
 * @Package com.bsoft.mob.ienr.activity.user
 * @Description: 生命体征
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-12-28 上午11:06:04
 * @version V1.0
 */
package com.bsoft.mob.ienr.activity.user;

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
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.api.LifeSignApi;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.dynamicui.lifesymptom.LifeSymptomViewFactoryNew;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignRealSaveDataItem;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignSaveData;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignSaveDataItem;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignSync;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignTypeItem;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSymptomTempDataBean;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FormSyncUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;
import com.bsoft.mob.ienr.view.floatmenu.menu.IFloatMenuItem;
import com.bsoft.mob.ienr.view.floatmenu.menu.TextFloatMenuItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @ClassName: LifeSymptomActivity
 * @Description: 生命体征
 * @date 2015-12-28 上午11:06:04
 */
public class LifeSymptomActivity extends BaseBarcodeActivity {


    private LinearLayout mainview;
    private LifeSymptomViewFactoryNew mLifeSymptomViewFactory;
    private KeyboardView keyboardView;
    private View root;
    private boolean isSaveed = false;

    private String Txsj;//参数：护理评估模块填写时间
    private String CJZH;

    @Override
    public void initBarBroadcast() {
    }


    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return backHidKeyboard();
    }

    /**
     * @param @return
     * @return boolean
     * @throws
     * @Title: backHidKeyboard
     * @Description: 按返回键关闭体温小键盘
     */
    private boolean backHidKeyboard() {
        if (keyboardView.getVisibility() == View.GONE)
            return true;
        else {
            keyboardView.setVisibility(View.GONE);
            return true;
        }
    }

    @Override
    protected List<IFloatMenuItem> configFloatMenuItems() {
        final int[] itemDrawables = {R.drawable.menu_view,
                R.drawable.menu_fresh};
        final int[][] itemStringDrawables = {
                {R.drawable.menu_view, R.string.comm_menu_view},
                {R.drawable.menu_fresh, R.string.comm_menu_refresh}};
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
            Intent intent = new Intent(this, LifeSymptomInquiryActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        root.setFocusableInTouchMode(true);
        root.requestFocus();
    }

    private void initActionBar() {

        actionBar.setTitle("体征录入");
        actionBar.setPatient(mAppApplication.sickPersonVo.BRCH + mAppApplication.sickPersonVo.BRXM);
        actionBar.setBackAction(new Action() {
            @Override
            public String getText() {
                return getString(R.string.menu_back);
            }

            @Override
            public void performAction(View view) {
                if (isSaveed) {
                    Intent intent = new Intent();
                    intent.putExtra("CJZH", CJZH);
                    setResult(RESULT_OK, intent);
                }
                finish();
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_arrow_back_black_24dp;
            }
        });

        final Action saveAction = new Action() {
            @Override
            public String getText() {
                return "保存";
            }

            @Override
            public void performAction(View view) {
                actionSaveTask();
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }
        };
        actionBar.addAction(saveAction);

    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_life_symptom;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        root = getWindow().getDecorView().findViewById(android.R.id.content);
        mainview = (LinearLayout) findViewById(R.id.view);
        ImageView id_iv_for_bar_spinner = (ImageView) findViewById(R.id.id_iv_for_bar_spinner);
        id_iv_for_bar_spinner.setVisibility(View.GONE);
        keyboardView = (KeyboardView) findViewById(R.id.keyboard_view);


        Txsj = getIntent().getStringExtra("TXSJ");
        initView();
    }

    private void initView() {

        initActionBar();

        String dateTime = DateTimeHelper.getServer_yyyyMMddHHmm00();
        if (!EmptyTool.isBlank(Txsj)) {
            dateTime = DateTimeHelper.dateTimeAddedMinutes(Txsj, -1);
        }
        mLifeSymptomViewFactory = new LifeSymptomViewFactoryNew(this, root, true, dateTime, mAppApplication);

        toRefreshData();
    }

    @Override
    protected void toRefreshData() {
        GetTask task = new GetTask();
        tasks.add(task);
        task.execute();
    }

    void hidden() {
        if (null != getCurrentFocus()
                && null != getCurrentFocus().getWindowToken()) {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    class GetTask extends AsyncTask<Void, Void, Response<ArrayList<LifeSignTypeItem>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
            mainview.removeAllViews();
        }

        @Override
        protected Response<ArrayList<LifeSignTypeItem>> doInBackground(Void... arg0) {

            Response<ArrayList<LifeSignTypeItem>> type = LifeSignApi.getInstance(
                    LifeSymptomActivity.this).getLifeSignTypeItemList(mAppApplication.sickPersonVo.ZYH,
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
                    new AgainLoginUtil(LifeSymptomActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            toRefreshData();
                        }
                    }).showLoginDialog();
                    return;
                }
                if (sresult.ReType == 0) {
                    ArrayList<LifeSignTypeItem> lifeSignTypeItems = sresult.Data;

                    if (mAppApplication.sickPersonVo != null) {
                        View result = mLifeSymptomViewFactory.build(lifeSignTypeItems, mAppApplication.sickPersonVo.ZYH,
                                mAppApplication.jgId);
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
                    return;
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：网络错误!");
                return;
            }

        }
    }


    // add 2017年4月25日17:20:37
    List<LifeSignSaveDataItem> mItemList4Temp;

    class SaveTask extends AsyncTask<Void, Void, Response<LifeSignSync>> {

        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.saveing);
        }

        @Override
        protected Response<LifeSignSync> doInBackground(Void... params) {

            if (mAppApplication.user == null || mAppApplication.sickPersonVo == null || mLifeSymptomViewFactory == null) {
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
            //2017年4月25日17:11:18 add
            mItemList4Temp = new ArrayList<>(itemList);
            //
            try {
                lifeSignSaveData.URID = mAppApplication.user.YHID;
                lifeSignSaveData.BQID = mAppApplication.getAreaId();
                lifeSignSaveData.ZYH = mAppApplication.sickPersonVo.ZYH;
                lifeSignSaveData.IsTemp = mLifeSymptomViewFactory.isTemp();
                lifeSignSaveData.TempTime = mLifeSymptomViewFactory.getTmpTime();
                lifeSignSaveData.JGID = mAppApplication.jgId;
                lifeSignSaveData.lifeSignSaveDataItemList = itemList;
                data = JsonUtil.toJson(lifeSignSaveData);
//                data = "{\"lifeSignSaveData\":" + data + "}";
            } catch (IOException e) {
                e.printStackTrace();
            }
            LifeSignApi api = LifeSignApi.getInstance(LifeSymptomActivity.this);
            response = api.lifeSymptomSave(data);
            return response;
        }

        @Override
        protected void onPostExecute(Response<LifeSignSync> result) {
            hideLoadingDialog();
            tasks.remove(this);
            if (result != null) {
                if (result.ReType == -2) {
                    showMsgAndVoice(result.Msg);
                } else if (result.ReType == 100) {
                    new AgainLoginUtil(LifeSymptomActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            actionSaveTask();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {

                    showMsgAndVoice(R.string.project_save_success);
                    LifeSignSync lifeSignSync = result.Data;


                    //add 2017年4月25日17:20:20
               /*     Map<String, LifeSymptomTempDataBean> tempDataBeanMap = new HashMap<>();
                    if (mItemList4Temp != null && mItemList4Temp.size() > 0) {
                        Log.i(Constant.TAG_COMM, "onPostExecute: mItemList4Temp.size():"+mItemList4Temp.size());
                        for (int i = 0; i < mItemList4Temp.size(); i++) {
                            LifeSymptomTempDataBean tempData = new LifeSymptomTempDataBean();
                            tempData.TZXM = mItemList4Temp.get(i).TZXM;
                            tempData.YCBZ = mItemList4Temp.get(i).YCBZ;
                            tempData.Data = mItemList4Temp.get(i).Data;
                            tempDataBeanMap.put(tempData.TZXM, tempData);
                        }
                    }*/
                    // add by louis 2017年6月25日14:22:10
                    List<LifeSignRealSaveDataItem> lifeSignRealSaveDataItems = lifeSignSync.mLifeSignRealSaveDataItemList;
                    if (lifeSignRealSaveDataItems != null) {
                        Map<String, LifeSymptomTempDataBean> tempDataBeanMap = new HashMap<>();
                        for (LifeSignRealSaveDataItem lifeSignRealSaveDataItem :
                                lifeSignRealSaveDataItems) {
                            CJZH = lifeSignRealSaveDataItem.CJZH;
                            String cjh = lifeSignRealSaveDataItem.CJH;
                            String nowData = lifeSignRealSaveDataItem.TZNR;
                            String tzxm = lifeSignRealSaveDataItem.XMH;
                            LifeSymptomTempDataBean tempData = new LifeSymptomTempDataBean();
                            tempData.CJH = cjh;
                            tempData.Data = nowData;
                            tempData.TZXM = tzxm;
                            tempDataBeanMap.put(tempData.TZXM, tempData);

                        }
                        if (mAppApplication.mLifeSymptomTempDataBean == null) {
                            mAppApplication.mLifeSymptomTempDataBean = new HashMap<>();
                        }
                        mAppApplication.mLifeSymptomTempDataBean.putAll(tempDataBeanMap);
                        //
                    }


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
                        syncUtil.InvokeSync(LifeSymptomActivity.this,
                                lifeSignSync.SyncData, mAppApplication.jgId, tasks);

                    }

                    isSaveed = true;
                    // 数据情空0
                    mLifeSymptomViewFactory.clearData();


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

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    private void actionSaveTask() {
        SaveTask task = new SaveTask();
        tasks.add(task);
        task.execute();
    }


}
