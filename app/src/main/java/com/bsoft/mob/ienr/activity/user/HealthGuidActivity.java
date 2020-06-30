/**
 * @Title: HealthGuidActivity.java
 * @Package com.bsoft.mob.ienr.activity.user
 * @Description: 健康教育操作页类文件
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-11-13 上午9:30:04
 * @version V1.0
 */
package com.bsoft.mob.ienr.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.SignActivity;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.activity.user.adapter.HealthGuidDetailListAdapter;
import com.bsoft.mob.ienr.activity.user.adapter.HealthGuidOperListAdapter;
import com.bsoft.mob.ienr.activity.user.adapter.HealthGuidTypeListAdapter;
import com.bsoft.mob.ienr.adapter.ViewPagerAdapter;
import com.bsoft.mob.ienr.api.HealthGuidApi;
import com.bsoft.mob.ienr.api.OffLineApi;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.event.HealthGuidEvent;
import com.bsoft.mob.ienr.helper.ContextCompatHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.healthguid.HealthGuidData;
import com.bsoft.mob.ienr.model.healthguid.HealthGuidDetail;
import com.bsoft.mob.ienr.model.healthguid.HealthGuidOper;
import com.bsoft.mob.ienr.model.healthguid.HealthGuidSaveData;
import com.bsoft.mob.ienr.model.healthguid.HealthGuidType;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FormSyncUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.OffLineUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;
import com.bsoft.mob.ienr.view.floatmenu.menu.IFloatMenuItem;
import com.bsoft.mob.ienr.view.floatmenu.menu.TextFloatMenuItem;

import org.apache.commons.lang3.ArrayUtils;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @ClassName: HealthGuidActivity
 * @Description: 健康教育操作页Activity
 * @date 2015-11-13 上午9:30:04
 */
public class HealthGuidActivity extends BaseBarcodeActivity {
    private boolean isOnlyOneType = true;//是否只允许选择一个类别
    private String operType;//操作类型 不能用healthGuidData.OperType代替（因为后者的值会更改）

    private ListView listView;
    private ViewPager pager;
    private TextView timePageView;
    private CheckBox checkBox;
    private ListView defaultListView;
    private ImageView addItemImageView;
    private TextView iPageName;
    private LinearLayout timeBox;
    private Spinner startTime;// 开始时间

    // 当前宣教类型名称
    private String nowHealthGuidType;

    private HealthGuidOperListAdapter mAdapterOper;
    private List<HealthGuidOper> mListOper;

    private HealthGuidOperListAdapter mAdapterDefaultOper;
    private List<HealthGuidOper> mListDefaultOper;

    private HealthGuidTypeListAdapter mAdapterType;
    private HealthGuidDetailListAdapter mAdapterDetail;
    private List<HealthGuidDetail> mListDetail;
    private ListView listViewHealthGuidType;
    private ListView listViewHealthGuidDetail;

    private HealthGuidData healthGuidData;

    private static final int STARTCUSTOMACTIVITYCODE = 1;
    private static final int RQ_GET_USERID = 2;
    private static final int STARTEVALUATEACTIVITYCODE = 3;

    private int SIGNATURE = 1;// 1:签名 2:取消签名

    private String Txsj;//参数：护理评估模块填写时间

    @Override
    public void initBarBroadcast() {


    }


    /**
     * @param @param view
     * @return void
     * @throws
     * @Title: initView
     * @Description: 初始化界面
     */
    private void initView() {

        defaultListView = (ListView) findViewById(R.id.id_lv);
        listView = (ListView) findViewById(R.id.id_lv_2);
        pager = (ViewPager) findViewById(R.id.id_vp);
        //   menu = (RayMenu) findViewById(R.id.ray_menu);
        timeBox = (LinearLayout) findViewById(R.id.id_ll_container);
        SpinnerLayout startTimeLayout = (SpinnerLayout) findViewById(R.id.id_spinner_layout);
        startTime = startTimeLayout.getSpinner();
    }

    private void initTimePageView() {

        timePageView = (TextView) findViewById(R.id.healthguid_datetime_txt);
        timePageView.setOnClickListener(onClickListener);

        String yyyyMMddHHmm = DateTimeHelper.getServer_yyyyMMddHHmm00();
        initTimeTxt(yyyyMMddHHmm);
    }

    private void initDefaultOperPageView() {

        checkBox = (CheckBox) findViewById(R.id.healthguid_cbpre);
        Drawable btnDrawable = ContextCompatHelper.getDrawable(mContext, R.drawable.selector_classic_icon_up_down);
        checkBox.setButtonDrawable(btnDrawable);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                defaultListView.setVisibility(isChecked ? View.VISIBLE
                        : View.GONE);
                pager.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                listView.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                // menu.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                if (!isChecked) {
                    mListOper = deepCopyList(mListDefaultOper);
                    setMAdaperOper();
                }
            }
        });
    }

    private void initTimeTxt(String yyyyMMddHHmm) {
        timePageView.setText(yyyyMMddHHmm);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            int viewId = v.getId();

            String dateStr = timePageView.getText().toString();
            if (EmptyTool.isBlank(dateStr)) {
                return;
            }

            // 导入年月数据
            YmdHMs ymdHMs = DateTimeHelper.dateTime2YmdHMs(dateStr);
            showPickerDateTimeCompat(ymdHMs, viewId);
        }
    };

    @Override
    public void onDateTimeSet(int year, int month, int dayOfMonth,
                              int hourOfDay, int minute, int viewId) {

        String dateTime = DateTimeFactory.getInstance().ymdhms2DateTime(year, month, dayOfMonth, hourOfDay, minute, 0);
        initTimeTxt(dateTime);
    }

    ;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK || data == null) {
            return;
        }
        if (requestCode == STARTCUSTOMACTIVITYCODE) {
            String value = data.getStringExtra("item");
            if (!EmptyTool.isBlank(value.trim())) {
                HealthGuidDetail healthGuidDetail = new HealthGuidDetail();
                healthGuidDetail.XH = "0";
                healthGuidDetail.MS = value;
                healthGuidDetail.ISOPER = "0";
                healthGuidDetail.ISCHECK = "1";
                healthGuidDetail.ZDYBZ = "1";
                healthGuidDetail.DLBZ = "1";
                healthGuidDetail.XMZH = "0";
                healthGuidDetail.XJSJ = timePageView.getText().toString();
                healthGuidDetail.XJGH = mAppApplication.user.YHID;
                healthGuidDetail.HealthGuidOpers = deepCopyList(mListOper);
                mListDetail.add(healthGuidDetail);
                mAdapterDetail.notifyDataSetChanged();
            }
        }
        if (requestCode == RQ_GET_USERID) {

            String yhid1 = data.getStringExtra(SignActivity.EXTRA_YHID_KEY_1);
            String extra = data.getStringExtra(SignActivity.EXTRA_STRING_KEY);

            // 全局签名
            if (EmptyTool.isBlank(extra)) {
                if (EmptyTool.isBlank(healthGuidData.QMGH)) {// 签名
                    healthGuidData.QMGH = yhid1;
                    SIGNATURE = 1;
                } else {// 取消签名
                    healthGuidData.QMGH = null;
                    SIGNATURE = 2;
                }
                signature();
            }
        }
        if (requestCode == STARTEVALUATEACTIVITYCODE) {

            healthGuidData = new HealthGuidData();
            operType = data.getStringExtra("operType");
            healthGuidData.GLLX = data.getStringExtra("type");
            healthGuidData.XH = data.getStringExtra("xh");
            healthGuidData.GLXH = data.getStringExtra("lxbh");
            getData();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            setResult(Activity.RESULT_OK);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }

    private void signature() {
        // 执行签名/取消签名操作
        SignatureTast task = new SignatureTast();
        tasks.add(task);
        task.execute();
    }

    class SignatureTast extends AsyncTask<Void, Void, Response<HealthGuidData>> {

        /*
         * (非 Javadoc) <p>Title: onPreExecute</p> <p>Description: 网络请求前</p>
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            showLoadingDialog(SIGNATURE == 1 ? R.string.signing : R.string.cancel_signing);
        }

        /*
         * (非 Javadoc) <p>Title: doInBackground</p> <p>Description:执行网络请求 </p>
         *
         * @param params
         *
         * @return
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Response<HealthGuidData> doInBackground(Void... params) {
            if (SIGNATURE == 1) {
                return HealthGuidApi.getInstance(HealthGuidActivity.this)
                        .Signature(healthGuidData.XH, mAppApplication.user.YHID, healthGuidData.GLXH, healthGuidData.GLLX, mAppApplication.jgId);
            } else {
                return HealthGuidApi.getInstance(HealthGuidActivity.this)
                        .CancleSignature(healthGuidData.XH, healthGuidData.GLXH, healthGuidData.GLLX, mAppApplication.jgId);
            }
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description:网络请求后 </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Response<HealthGuidData> result) {
            hideLoadingDialog();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(HealthGuidActivity.this, mAppApplication).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    String msg = SIGNATURE == 1 ? "签名成功" : "取消签名成功";
                    showMsgAndVoice(msg);
                    realGetData(result.Data);
                } else {
                    String msg = SIGNATURE == 1 ? "签名失败" : "取消签名失败";
                    showMsgAndVoice(msg);
                    /*MediaUtil.getInstance(HealthGuidActivity.this).playSound(
                            R.raw.wrong, HealthGuidActivity.this);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }

        }
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: initActionBar
     * @Description: 初始化工具条
     */
    private void initActionBar() {
        actionBar.setTitle("健康教育");
        actionBar.setPatient(mAppApplication.sickPersonVo.BRCH + mAppApplication.sickPersonVo.BRXM);
        actionBar.setBackAction(new Action() {
            @Override
            public String getText() {
                return getString(R.string.menu_back);
            }

            @Override
            public void performAction(View view) {
                setResult(Activity.RESULT_OK);
                finish();
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_arrow_back_black_24dp;
            }
        });
        actionBar.addAction(new Action() {
            @Override
            public String getText() {

                return "保存";
            }

            @Override
            public void performAction(View view) {
                saveData();
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }
        });
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_health_guid;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        healthGuidData = new HealthGuidData();
        Intent intent = getIntent();
        operType = intent.getStringExtra("operType");
        healthGuidData.OperType = operType;
        healthGuidData.GLLX = intent.getStringExtra("type");
        healthGuidData.XH = intent.getStringExtra("xh");
        healthGuidData.GLXH = intent.getStringExtra("lxbh");
        Txsj = getIntent().getStringExtra("TXSJ");

        initView();
        initActionBar();
        initTimePageView();
        initDefaultOperPageView();
        initPager();
        toRefreshData();
    }

    /**
     * @param
     * @return void 返回类型
     * @throws
     * @Title: initPager
     * @Description: 初始化滑动页
     */
    private void initPager() {
        ArrayList<View> pagelist = new ArrayList<View>();
        LayoutInflater mInflater = getLayoutInflater();
        View cView = mInflater
                .inflate(R.layout.layout_health_guid_cpage, null, false);
        View iView = mInflater
                .inflate(R.layout.layout_health_guid_ipage, null, false);
        pagelist.add(cView);
        pagelist.add(iView);
        pager.setAdapter(new ViewPagerAdapter(pagelist));
        pager.setCurrentItem(pagelist.size() - 1);// 选择最后一个view
        pager.setOnPageChangeListener(new PageChangeListener());

        listViewHealthGuidType = (ListView) cView
                .findViewById(R.id.id_lv);
        listViewHealthGuidDetail = (ListView) iView
                .findViewById(R.id.id_lv);
        addItemImageView = (ImageView) iView
                .findViewById(R.id.healthguid_addItem);
        iPageName = (TextView) iView.findViewById(R.id.healthguid_ipage_name);
        initAddItemImageView();
//        getData();
    }

    private void initAddItemImageView() {
        addItemImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCustomItem();
            }
        });
    }

    private void addCustomItem() {
        Intent intent = new Intent(HealthGuidActivity.this,
                HealthGuidCustomActivity.class);
        if (EmptyTool.isBlank(nowHealthGuidType)) {
            showMsgAndVoiceAndVibrator("请先选择宣教类别");
            return;
        }
        intent.putExtra("type", nowHealthGuidType);
        startActivityForResult(intent, STARTCUSTOMACTIVITYCODE);
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected List<IFloatMenuItem> configFloatMenuItems() {
        ArrayList<Integer> itemDrawables = new ArrayList<Integer>();
        itemDrawables.add(R.drawable.menu_delete);
        itemDrawables.add(R.drawable.menu_add_item);
        itemDrawables.add(R.drawable.menu_create);
        itemDrawables.add(R.drawable.menu_save);

        final int[][] itemStringDrawables = {
                {R.drawable.menu_delete, R.string.comm_menu_delete},
                {R.drawable.menu_add_item, R.string.comm_menu_add_item},
                {R.drawable.menu_create, R.string.comm_menu_add},
                {R.drawable.menu_save, R.string.comm_menu_save}};
    /* !!!!   if (!EmptyTool.isBlank(healthGuidData.QMGH)) {
            itemDrawables.add(R.drawable.menu_cancel_sign);
        } else {
            itemDrawables.add(R.drawable.menu_sign);
        }
        if (healthGuidData.XJDLPJ.equals("1")) {
            //独立评价按钮
            itemDrawables.add(R.drawable.menu_evaluate);
        }*/

        List<IFloatMenuItem> floatMenuItemList = new ArrayList<>();
        /*for (int itemDrawableResid : itemDrawables) {
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
        if (drawableRes == R.drawable.menu_create) {
            if (!EmptyTool.isBlank(Txsj)) {
                String dateTime = DateTimeHelper.dateTimeAddedMinutes(Txsj,-1);
                initTimeTxt(dateTime);
            }
            addData();
        } else if (drawableRes == R.drawable.menu_add_item) {
            addCustomItem();
        } else if (drawableRes == R.drawable.menu_save) {
            saveData();
        } else if (drawableRes == R.drawable.menu_evaluate) {
            evaluateData();
        } else if (drawableRes == R.drawable.menu_delete) {
            if (EmptyTool.isBlank(healthGuidData.XH)
                    || healthGuidData.XH.equals("0")) {
                showMsgAndVoiceAndVibrator("请先去保存数据，再删除！");
                return;
            }
            if (!EmptyTool.isBlank(healthGuidData.QMGH)) {
                showMsgAndVoiceAndVibrator("当前宣教单已签名，不允许删除！");
                return;
            }
            delData();
        } else if (drawableRes == R.drawable.menu_sign
                || drawableRes == R.drawable.menu_cancel_sign) {// 签名
            if (EmptyTool.isBlank(healthGuidData.XH)
                    || healthGuidData.XH.equals("0")) {
                showMsgAndVoiceAndVibrator("请先去保存数据，再签名！");
                return;
            }
            // 全局签名
            startSignActivity(false, null);
        }
    }

    private void startSignActivity(boolean doubleSign, String extra) {
        Intent intent = new Intent(HealthGuidActivity.this, SignActivity.class);

        if (doubleSign) {
            intent.putExtra(SignActivity.ACTION_SIGN,
                    SignActivity.ACTION_EXTRA_SING_BOUSE);
        } else {
            intent.putExtra(SignActivity.ACTION_SIGN,
                    SignActivity.ACTION_EXTRA_SIGN_SIGNLE);
        }
        intent.putExtra(SignActivity.EXTRA_STRING_KEY, extra);
        startActivityForResult(intent, RQ_GET_USERID);
    }

    private void delData() {
        // 执行删除操作
        DelDataTast task = new DelDataTast();
        tasks.add(task);
        task.execute();
    }

    class DelDataTast extends AsyncTask<Void, Void, Response<String>> {

        /*
         * (非 Javadoc) <p>Title: onPreExecute</p> <p>Description: 网络请求前</p>
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.deleteing);
        }

        /*
         * (非 Javadoc) <p>Title: doInBackground</p> <p>Description:执行网络请求 </p>
         *
         * @param params
         *
         * @return
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Response<String> doInBackground(Void... params) {

            return HealthGuidApi.getInstance(HealthGuidActivity.this)
                    .DelHealthGuiData(healthGuidData.XH, mAppApplication.jgId);
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description:网络请求后 </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Response<String> result) {
            hideLoadingDialog();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(HealthGuidActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            delData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    showMsgAndVoice("删除成功");
                    //
                    EventBus.getDefault().post(new HealthGuidEvent());
                    //
                    setResult(Activity.RESULT_OK);
                    finish();
                } else {
                    showMsgAndVoice(result.Msg);
                    /*MediaUtil.getInstance(HealthGuidActivity.this).playSound(
                            R.raw.wrong, HealthGuidActivity.this);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    private void saveData() {
        if (!EmptyTool.isBlank(healthGuidData.QMGH)) {
            showMsgAndVoiceAndVibrator("护士长已经签名，不能修改");
            return;
        }
        // 离线保存
        if (!OffLineUtil.WifiConnected(HealthGuidActivity.this)) {
            String data = "";
            HealthGuidSaveData healthGuidSaveData = new HealthGuidSaveData();
            try {
                HealthGuidData par = new HealthGuidData();
                par.XH = healthGuidData.XH;
                par.GLXH = healthGuidData.GLXH;
                par.OperType = healthGuidData.OperType;
                par.GLLX = healthGuidData.GLLX;
                par.JLSJ = healthGuidData.JLSJ;
                par.JLGH = healthGuidData.JLGH;
                par.QMGH = healthGuidData.QMGH;
                par.HealthGuidTypes = healthGuidData.HealthGuidTypes;
                par.HealthGuidDefaultOpers = null;
                if (healthGuidData.OperType.equals("1")) {
                    par.JLGH = mAppApplication.user.YHID;
                    par.JLSJ = timePageView.getText().toString();
                }
                healthGuidSaveData.BQID = mAppApplication.getAreaId();
                healthGuidSaveData.ZYH = mAppApplication.sickPersonVo.ZYH;
                healthGuidSaveData.JGID = mAppApplication.jgId;
                healthGuidSaveData.HealthGuidData = par;
                data = JsonUtil.toJson(healthGuidSaveData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String url = OffLineApi.getInstance(this).url;
            String uri = url + "healthguid/post/saveHealthGuidData";
            if (OffLineUtil.offLineSave(HealthGuidActivity.this, uri, 2, data,
                    mAppApplication.sickPersonVo.BRXM, "健康宣教",
                    mAppApplication.user.YHXM))
                showMsgAndVoice("当前网络未连接，已为您保存在本地。网络连接好后，请到【离线保存】菜单中提交。");

            return;
        }
        // 执行保存操作
        SaveDataTast task = new SaveDataTast();
        tasks.add(task);
        task.execute();
    }

    class SaveDataTast extends AsyncTask<Void, Void, Response<HealthGuidData>> {

        /*
         * (非 Javadoc) <p>Title: onPreExecute</p> <p>Description: 网络请求前</p>
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.saveing);
        }

        /*
         * (非 Javadoc) <p>Title: doInBackground</p> <p>Description:执行网络请求 </p>
         *
         * @param params
         *
         * @return
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Response<HealthGuidData> doInBackground(Void... params) {
            String data = "";
            HealthGuidSaveData healthGuidSaveData = new HealthGuidSaveData();
            try {
                HealthGuidData par = new HealthGuidData();
                par.XH = healthGuidData.XH;
                par.GLXH = healthGuidData.GLXH;
                par.OperType = healthGuidData.OperType;
                par.GLLX = healthGuidData.GLLX;
                par.JLSJ = healthGuidData.JLSJ;
                par.JLGH = healthGuidData.JLGH;
                par.QMGH = healthGuidData.QMGH;
                par.HealthGuidTypes = healthGuidData.HealthGuidTypes;
                par.HealthGuidDefaultOpers = null;
                if (healthGuidData.OperType.equals("1")) {
                    par.JLGH = mAppApplication.user.YHID;
                    par.JLSJ = timePageView.getText().toString();
                }
                healthGuidSaveData.BQID = mAppApplication.getAreaId();
                healthGuidSaveData.ZYH = mAppApplication.sickPersonVo.ZYH;
                healthGuidSaveData.JGID = mAppApplication.jgId;
                healthGuidSaveData.HealthGuidData = par;
                data = JsonUtil.toJson(healthGuidSaveData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return HealthGuidApi.getInstance(HealthGuidActivity.this)
                    .SaveHealthGuidDataPost(data);
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description:网络请求后 </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(final Response<HealthGuidData> result) {
            hideLoadingDialog();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(HealthGuidActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            saveData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    showMsgAndVoice(R.string.project_save_success);
                    //
                    EventBus.getDefault().post(new HealthGuidEvent());
                    if (healthGuidData.IsSync) {
                        FormSyncUtil syncUtil = new FormSyncUtil();
                        syncUtil.setOnDialogClickListener(
                                new FormSyncUtil.onCancelClickListener() {

                                    @Override
                                    public void onCancel() {
                                        realGetData(result.Data);
                                    }
                                }, new FormSyncUtil.onConfirmClickListener() {

                                    @Override
                                    public void onConfirm() {
                                        realGetData(result.Data);
                                    }
                                });
                        syncUtil.InvokeSync(HealthGuidActivity.this,
                                healthGuidData.SyncData, mAppApplication.jgId, tasks);

                    } else {
                        realGetData(result.Data);
                    }

                } else {
                    showMsgAndVoice(result.Msg);
                   /* MediaUtil.getInstance(HealthGuidActivity.this).playSound(
                            R.raw.wrong, HealthGuidActivity.this);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    private void addData() {
        // 执行新增操作
        healthGuidData.XH = "0";
        healthGuidData.OperType = "1";
        if (healthGuidData.GLLX.equals("1")) {
            getData();
        } else if (healthGuidData.GLLX.equals("2")) {
            getDataSpecial();
        }
    }

    private void evaluateData() {
        if (healthGuidData.OperType.equals("1")) {
            showMsgAndVoiceAndVibrator("请先去保存数据，再进行评价！");
            return;
        }
        if (!EmptyTool.isBlank(healthGuidData.QMGH)) {
            showMsgAndVoiceAndVibrator("已签名，无法进行评价！");
            return;
        }

        //启动独立评价窗体
        Intent intent = new Intent(HealthGuidActivity.this, HealthGuidEvaluateActivity.class);
        intent.putExtra("operType", operType);
        intent.putExtra("type", healthGuidData.GLLX);
        intent.putExtra("xh", healthGuidData.XH);
        intent.putExtra("lxbh", healthGuidData.GLXH);
        startActivityForResult(intent, STARTEVALUATEACTIVITYCODE);
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: getData
     * @Description: 获取健康宣教列表
     */
    private void getData() {
        GetDataTast task = new GetDataTast();
        tasks.add(task);
        task.execute();
    }

    class GetDataTast extends AsyncTask<Void, Void, Response<HealthGuidData>> {

        /*
         * (非 Javadoc) <p>Title: onPreExecute</p> <p>Description: 网络请求前</p>
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        /*
         * (非 Javadoc) <p>Title: doInBackground</p> <p>Description:执行网络请求 </p>
         *
         * @param params
         *
         * @return
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Response<HealthGuidData> doInBackground(Void... params) {
            //注意：operType不能用healthGuidData.OperType代替（因为后者的值会更改）
            return HealthGuidApi.getInstance(HealthGuidActivity.this)
                    .GetHealthGuidData(healthGuidData.GLXH, healthGuidData.XH,
                            healthGuidData.GLLX, operType, mAppApplication.jgId);
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description:网络请求后 </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Response<HealthGuidData> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(HealthGuidActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            getData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    realGetData(result.Data);
                } else {
                    showMsgAndVoice(result.Msg);
                    return;
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: getData
     * @Description: 获取健康宣教列表
     * 归类类型新增专用：通过归类类型先找到对应的样式序号然后进行新增
     * 比getData多一步操作：通过归类类型找到样式序号
     */
    private void getDataSpecial() {
        GetDataSpecialTast task = new GetDataSpecialTast();
        tasks.add(task);
        task.execute();
    }

    class GetDataSpecialTast extends AsyncTask<Void, Void, Response<HealthGuidData>> {

        /*
         * (非 Javadoc) <p>Title: onPreExecute</p> <p>Description: 网络请求前</p>
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        /*
         * (非 Javadoc) <p>Title: doInBackground</p> <p>Description:执行网络请求 </p>
         *
         * @param params
         *
         * @return
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Response<HealthGuidData> doInBackground(Void... params) {
            return HealthGuidApi.getInstance(HealthGuidActivity.this)
                    .GetHealthGuidDataSpecial(healthGuidData.GLXH, mAppApplication.jgId);
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description:网络请求后 </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Response<HealthGuidData> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(HealthGuidActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            getDataSpecial();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    realGetData(result.Data);
                } else {
                    showMsgAndVoice(result.Msg);
                    return;
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: getBZXXData
     * @Description: 获取健康宣教宣教项目备注信息
     */
    private void getBZXXData(String... params) {
        GetBZXXDataTast task = new GetBZXXDataTast();
        tasks.add(task);
        task.execute(params);
    }

    class GetBZXXDataTast extends AsyncTask<String, Void, Response<String>> {

        /*
         * (非 Javadoc) <p>Title: onPreExecute</p> <p>Description: 网络请求前</p>
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            // showSwipeRefreshLayout();
        }

        /*
         * (非 Javadoc) <p>Title: doInBackground</p> <p>Description:执行网络请求 </p>
         *
         * @param params
         *
         * @return
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Response<String> doInBackground(String... params) {
            String xmxh = params[0];
            return HealthGuidApi.getInstance(HealthGuidActivity.this)
                    .GetHealthGuidRemark(xmxh);
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description:网络请求后 </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Response<String> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(HealthGuidActivity.this, mAppApplication).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    showInfoDialog(result.Data);
                } else {
                    showInfoDialog(result.Msg);
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    private void realGetData(HealthGuidData par) {
        healthGuidData = par;
        mListDetail = healthGuidData.HealthGuidTypes.get(0).HealthGuidDetails;
        mListOper = deepCopyList(healthGuidData.HealthGuidTypes.get(0).HealthGuidDetails
                .get(0).HealthGuidOpers);
        nowHealthGuidType = healthGuidData.HealthGuidTypes.get(0).MS;
        if (!EmptyTool.isBlank(healthGuidData.JLSJ)) {
            timePageView.setText(healthGuidData.JLSJ);
        }

        for (int i = 0; i < healthGuidData.HealthGuidTypes.size(); i++) {
            HealthGuidType healthGuidType = healthGuidData.HealthGuidTypes
                    .get(i);
            if (healthGuidType.ISCHECK.equals("1")) {
                for (int j = 0; j < healthGuidType.HealthGuidDetails
                        .size(); j++) {
                    if (healthGuidType.HealthGuidDetails.get(j).ISCHECK
                            .equals("1")) {
                        mListDetail = healthGuidType.HealthGuidDetails;
                        mListOper = deepCopyList(healthGuidType.HealthGuidDetails
                                .get(j).HealthGuidOpers);
                        nowHealthGuidType = healthGuidType.MS;
                        break;
                    }
                }
                break;
            }
        }

        mListDefaultOper = healthGuidData.HealthGuidDefaultOpers;

        setMAdapterType();
        setMAdapterDetail(true);

        setMAdaperOper();

        setMAdaperDefaultOper();


        ////=============================================
        ArrayList<Integer> itemDrawables = new ArrayList<Integer>();
        itemDrawables.add(R.drawable.menu_delete);
        itemDrawables.add(R.drawable.menu_add_item);
        itemDrawables.add(R.drawable.menu_create);
        itemDrawables.add(R.drawable.menu_save);

        int[][] itemStringDrawables = {
                {R.drawable.menu_delete, R.string.comm_menu_delete},
                {R.drawable.menu_add_item, R.string.comm_menu_add_item},
                {R.drawable.menu_create, R.string.comm_menu_add},
                {R.drawable.menu_save, R.string.comm_menu_save}
        };

        if (!EmptyTool.isBlank(healthGuidData.QMGH)) {
            itemDrawables.add(R.drawable.menu_cancel_sign);
            itemStringDrawables = ArrayUtils.addAll(itemStringDrawables,
                    new int[]{R.drawable.menu_cancel_sign, R.string.comm_menu_cancel_sign}
            );
        } else {
            itemDrawables.add(R.drawable.menu_sign);
            itemStringDrawables = ArrayUtils.addAll(itemStringDrawables,
                    new int[]{R.drawable.menu_sign, R.string.comm_menu_sign}
            );
        }
        if (healthGuidData.XJDLPJ.equals("1")) {
            //独立评价按钮
            itemDrawables.add(R.drawable.menu_evaluate);
            itemStringDrawables = ArrayUtils.addAll(itemStringDrawables,
                    new int[]{R.drawable.menu_evaluate, R.string.comm_menu_evaluate}
            );
        }

        List<IFloatMenuItem> floatMenuItemList = new ArrayList<>();
       /* for (int itemDrawableResid : itemDrawables) {
            FloatMenuItem floatMenuItem = new FloatMenuItem(itemDrawableResid) {
                @Override
                public void actionClick(View view, int resid) {
                    onMenuItemClick(resid);
                }
            };
            floatMenuItemList.add(floatMenuItem);
        } */
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
        updateFloatMenuItems(floatMenuItemList);
        ////=============================================
    }

    @Override
    protected void toRefreshData() {
        super.toRefreshData();
        getData();
    }

    private void setMAdaperOper() {
        mAdapterOper = new HealthGuidOperListAdapter(HealthGuidActivity.this,
                mListOper);
        listView.setAdapter(mAdapterOper);
    }

    private void setMAdaperDefaultOper() {
        mAdapterDefaultOper = new HealthGuidOperListAdapter(
                HealthGuidActivity.this, mListDefaultOper);
        defaultListView.setAdapter(mAdapterDefaultOper);
    }

    private void setMAdapterType() {
        if (isOnlyOneType) {
            //是否第一个选中类别
            boolean isFirstCheck = false;
            //下面循环就为了保证只选择一个宣教类别--只选择最上面一个选中的类别
            for (int i = 0; i < healthGuidData.HealthGuidTypes.size(); i++) {
                if (!isFirstCheck && healthGuidData.HealthGuidTypes.get(i).ISCHECK.equals("1")) {
                    isFirstCheck = true;
                } else {
                    healthGuidData.HealthGuidTypes.get(i).ISCHECK = "0";
                }
            }
        }
        mAdapterType = new HealthGuidTypeListAdapter(HealthGuidActivity.this,
                healthGuidData.HealthGuidTypes);
        listViewHealthGuidType.setAdapter(mAdapterType);
        listViewHealthGuidType
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        String isCheck = healthGuidData.HealthGuidTypes
                                .get(position).ISCHECK;
                        if (isCheck.equals("1")) {
                            int count = 0;
                            for (int i = 0; i < healthGuidData.HealthGuidTypes.size(); i++) {
                                if (healthGuidData.HealthGuidTypes.get(i).ISCHECK.equals("1")) {
                                    count++;
                                }
                            }
                            //保证至少选中一个类别
                            if (count > 1) {
                                healthGuidData.HealthGuidTypes.get(position).ISCHECK = "0";
                            }
                            int checkPostion = 0;
                            for (int i = 0; i < healthGuidData.HealthGuidTypes.size(); i++) {
                                if (healthGuidData.HealthGuidTypes.get(i).ISCHECK.equals("1")) {
                                    //有多个类别被选中时：只记录最前面一个被选中的类别
                                    checkPostion = i;
                                    break;
                                }
                            }
                            nowHealthGuidType = healthGuidData.HealthGuidTypes
                                    .get(checkPostion).MS;
                            mListDetail = new ArrayList<HealthGuidDetail>();
                            mListDetail = healthGuidData.HealthGuidTypes
                                    .get(checkPostion).HealthGuidDetails;
                            pager.setCurrentItem(1);// 选择第二个view（宣教项目）
                            setMAdapterDetail(false);
                        } else {
                            nowHealthGuidType = healthGuidData.HealthGuidTypes
                                    .get(position).MS;
                            healthGuidData.HealthGuidTypes.get(position).ISCHECK = "1";

                            if (isOnlyOneType) {
                                //下面循环就为了保证只选择一个宣教类别
                                for (int i = 0; i < healthGuidData.HealthGuidTypes.size(); i++) {
                                    if (i != position) {
                                        healthGuidData.HealthGuidTypes.get(i).ISCHECK = "0";
                                    }
                                }
                            }

                            mListDetail = new ArrayList<HealthGuidDetail>();
                            mListDetail = healthGuidData.HealthGuidTypes
                                    .get(position).HealthGuidDetails;
                            pager.setCurrentItem(1);// 选择第二个view（宣教项目）
                            setMAdapterDetail(false);
                        }
                        mAdapterType.notifyDataSetChanged();
                    }
                });
    }

    private void setMAdapterDetail(boolean addItemClick) {
        if (EmptyTool.isBlank(nowHealthGuidType)) {
            iPageName.setText("<<宣教项目");
            iPageName.setTextSize(20);
            addItemImageView.setVisibility(View.GONE);
        } else {
            iPageName.setText("<<宣教项目-" + nowHealthGuidType);
            iPageName.setTextSize(18);
            addItemImageView.setVisibility(View.VISIBLE);
        }
        mAdapterDetail = new HealthGuidDetailListAdapter(
                HealthGuidActivity.this, mListDetail);
        listViewHealthGuidDetail.setAdapter(mAdapterDetail);
        if (addItemClick) {
            listViewHealthGuidDetail.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    //showMsgAndVoice("长按事件");
                    if (mListDetail.get(position).ZDYBZ.equals("0")) {
                        getBZXXData(mListDetail.get(position).XH);
                    } else {
                        showInfoDialog("自定义项目，没有备注信息！");
                    }
                    return true;
                }
            });
            listViewHealthGuidDetail
                    .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent,
                                                View view, int position, long id) {
                            if (mListDetail.get(position).ISOPER.equals("1")) {
                                return;
                            }
                            if (mListDetail.get(position).ISCHECK.equals("1")) {
                                if (mListDetail.get(position).ZDYBZ.equals("1")
                                        || mListDetail.get(position).DLBZ
                                        .equals("1")) {
                                    mListDetail.get(position).ISCHECK = "0";
                                } else {
                                    for (int i = 0; i < mListDetail.size(); i++) {
                                        if (mListDetail.get(i).XMZH
                                                .equals(mListDetail
                                                        .get(position).XMZH)) {
                                            mListDetail.get(i).ISCHECK = "0";
                                        }
                                    }
                                }
                            } else {
                                mListDetail.get(position).HealthGuidOpers = deepCopyList(mListOper);

                                if (mListDetail.get(position).ZDYBZ.equals("1")
                                        || mListDetail.get(position).DLBZ
                                        .equals("1")) {
                                    mListDetail.get(position).ISCHECK = "1";
                                    mListDetail.get(position).XJSJ = timePageView.getText().toString();
                                    mListDetail.get(position).XJGH = mAppApplication.user.YHID;
                                } else {
                                    for (int i = 0; i < mListDetail.size(); i++) {
                                        if (mListDetail.get(i).XMZH
                                                .equals(mListDetail
                                                        .get(position).XMZH)) {
                                            mListDetail.get(i).ISCHECK = "1";
                                            mListDetail.get(i).XJSJ = timePageView.getText().toString();
                                            mListDetail.get(i).XJGH = mAppApplication.user.YHID;
                                            mListDetail.get(i).HealthGuidOpers = mListDetail.get(position).HealthGuidOpers;
                                        }
                                    }
                                }
                            }
                            mAdapterDetail.notifyDataSetChanged();
                        }
                    });
        }
    }

    /**
     * 将templatesList中的数据深度copy到targetList中
     *
     * @param templatesList 模版数组
     */
    private ArrayList<HealthGuidOper> deepCopyList(List<HealthGuidOper> templatesList) {
        ArrayList<HealthGuidOper> targetList = new ArrayList<HealthGuidOper>();
        if (templatesList == null) {
            return targetList;
        }
        try {
            for (int i = 0; i < templatesList.size(); i++) {
                targetList.add(templatesList.get(i).DeepClone());
            }
        } catch (IOException io) {
            showMsgAndVoice(io.getMessage());
        } catch (ClassNotFoundException nf) {
            showMsgAndVoice(nf.getMessage());
        }
        return targetList;
    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            if (healthGuidData.GLLX.equals("2")) {
                //pager.setCurrentItem(1);// 选择最后一个view
            }
        }

    }


}
