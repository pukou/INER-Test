package com.bsoft.mob.ienr.activity;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.adapter.AgencyAdapter;
import com.bsoft.mob.ienr.activity.adapter.AutoCompleteAdapter;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.api.UserApi;
import com.bsoft.mob.ienr.barcode.BarCodeFactory;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.barcode.Devices;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.mqtt.MQTTTool;
import com.bsoft.mob.ienr.components.tts.SpeechSynthesizerFactory;
import com.bsoft.mob.ienr.components.update.UpdateService;
import com.bsoft.mob.ienr.components.wifi.WifiService;
import com.bsoft.mob.ienr.components.wifi.WifiUtil;
import com.bsoft.mob.ienr.db.Database;
import com.bsoft.mob.ienr.dynamicui.DialogListener;
import com.bsoft.mob.ienr.helper.ViewAnimatorHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.AuthenticationVo;
import com.bsoft.mob.ienr.model.LoginResponse;
import com.bsoft.mob.ienr.model.LoginUser;
import com.bsoft.mob.ienr.model.PDAInfo;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.SignData;
import com.bsoft.mob.ienr.model.TimeVo;
import com.bsoft.mob.ienr.model.kernel.Agency;
import com.bsoft.mob.ienr.model.kernel.AreaVo;
import com.bsoft.mob.ienr.receiver.UpdateBroadcastReceiver;
import com.bsoft.mob.ienr.service.FloatingWindowService;
import com.bsoft.mob.ienr.service.SettingService;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.LogUtil;
import com.bsoft.mob.ienr.util.prefs.SettingUtils;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.util.tools.KeyBoardTool;
import com.bsoft.mob.ienr.view.WritePadDialog;
import com.classichu.dialogview.listener.OnBtnClickListener;
import com.classichu.dialogview.manager.DialogManager;
import com.classichu.popupwindow.ui.ClassicPopupWindow;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 上午11:43:05
 * @类说明 登录
 */
public class LoginActivity extends BaseBarcodeActivity implements
        OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "LoginActivity";
    private static final int RQT_SET_NET = 1;
    private AuthenticationVo authVo = new AuthenticationVo();
    protected static final int DIALOG_CHOICE_AGENT_MENU = 0;
    //IenrProgressDialog progressDialog;

    /**
     * 用户名
     */
    private AutoCompleteTextView tvUname;
    /**
     * 密码
     */
    private EditText etPwd;
    /**
     * 登录按钮
     */
    private Button btnLogin;

    private ImageView ivSign;
    private ImageView ivSign2;
    private WritePadDialog writePadDialog;


    // AsyncTask<Void, Void, ParserModel> asyncTask = null;

    ImageView clearUsrImg;
    ImageView clearPwdImg;

    AutoCompleteAdapter adapter;

    // CheckBox settingCk;

    // private SpinnerDataInfo mSpinner;

    // ArrayList<Agency> agencies = new ArrayList<Agency>();

    SwitchCompat mSwitch;


    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_login;
    }

    private UpdateBroadcastReceiver mUpdateBroadcastReceiver;

    //启动摄像头扫描二维护条码悬浮按钮
    private void startFlotWin(){
        if (BarCodeFactory.getBarCodeStr().equals(Devices.D_01_NoBar)
                || BarCodeFactory.getBarCodeStr().equals(Devices.D_02_ZBKBluetooth)) {
            Intent intent = new Intent(this, FloatingWindowService.class);
            startService(intent);
            Log.i(TAG, "initView: ");
        }
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findView();
        setAuthVo();

        getPDAInfo();

        //login update
        mUpdateBroadcastReceiver = new UpdateBroadcastReceiver(this);
        mUpdateBroadcastReceiver.registerReceiver();


        // 更新
//        PgyUpdateManager.register(this);
        // new UpdateVersionTask(this, DeviceUtil.getRootDir(this)).execute();
        Intent service = new Intent(this, UpdateService.class);
        startService(service);



        // 启动悬浮窗Service
        /*if (BarCodeFactory.getBarCodeStr().equals(Devices.D_01_NoBar)
                || BarCodeFactory.getBarCodeStr().equals(Devices.D_02_ZBKBluetooth)) {
            Intent intent = new Intent(this, FloatingWindowService.class);
            startService(intent);
            Log.i(TAG, "initView: ");
        }*/

        // 移动其他app调用移动护理 start
        // parseBarCode(getIntent());
        HandleInvoke();
        // 移动其他app调用移动护理end
        LogUtil.i(this, "da", "dafd");


    }


    // 移动其他app调用移动护理 start

    /**
     * 处理其他应用的调用
     *
     * @param
     * @return void 返回类型
     * @throws
     * @Title: HandleInvoke
     * @Description: 处理其他应用的调用
     */
    private void HandleInvoke() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        String action = intent.getAction();
        if (action == null) {
            return;
        }
        if (action.equals("com.bsoft.mob.ienr.Login")) {
            invokeLogin(intent);
        }
    }

    // 移动其他app调用移动护理 end

    // 移动其他app调用移动护理 start

    /**
     * 调用登录方法
     *
     * @param @param intent
     * @return void
     * @throws
     * @Title: invokeLogin
     * @Description: 调用登录方法
     */
    private void invokeLogin(Intent intent) {
        String username = intent.getStringExtra("extra_username");
        String password = intent.getStringExtra("extra_password");
        String jgid = intent.getStringExtra("extra_jgid");
        LoginAsyncTask asyncTask = new LoginAsyncTask(
                LoginAsyncTask.LOGIN_WITH_PWD);
        asyncTask.execute(username, password, jgid);
        tasks.add(asyncTask);
    }

    // 移动其他app调用移动护理 end

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 移动其他app调用移动护理 start
        if (intent == null) {
            return;
        } else if (intent.hasExtra("barcode")) {
            parseBarCode(intent);
        } else if (intent.getAction() != null) {
            if (intent.getAction().equals("com.bsoft.mob.ienr.Login")) {
                invokeLogin(intent);
            } else if (intent.getAction().equals(
                    "com.bsoft.mob.ienr.SwitchLogin")) {
                String barcode = intent.getStringExtra("extra_barcode");
                loginWithBarcode(barcode);
            }
        }
        // 移动其他app调用移动护理 end
    }

    private void setAuthVo() {
        Build bd = new Build();
        authVo.DeviceName = bd.DEVICE;
        authVo.ProductName = bd.PRODUCT;
        authVo.MAC = WifiUtil.getMacAress(this);
        authVo.IP = WifiUtil.getLocalIpAddress();
    }

    /**
     * 处理扫描登录（来自胸卡切换）
     *
     * @param intent
     */
    private void parseBarCode(Intent intent) {

        String barcode = intent.getStringExtra("barcode");
        loginWithBarcode(barcode);

    }


    @Override
    public void initBarBroadcast() {

        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {

                if (BarcodeActions.Bar_Get.equals(intent.getAction())) {
                    BarcodeEntity entity = (BarcodeEntity) intent
                            .getParcelableExtra("barinfo");
                    if (entity.TMFL == 3) {
                        loginWithBarcode(entity.source);
                    }
                }
            }
        };
    }

    /**
     * 条码扫描登录
     *
     * @param bar 条码
     */
    public void loginWithBarcode(String bar) {

        if (EmptyTool.isBlank(bar)) {
            showMsgAndVoiceAndVibrator(getString(R.string.tip_barcode_null));

            return;
        }
        if (mSwitch.isChecked()) {
            showMsgAndVoiceAndVibrator(getString(R.string.tip_barcode_admin));

            return;
        }

        LoginAsyncTask task = new LoginAsyncTask(
                LoginAsyncTask.LOGIN_WITH_BARCODE);
        task.execute(bar);
        tasks.add(task);
    }


    void setActTitle() {
        if (actionBar != null) {
            actionBar.setTitle(mSwitch != null && mSwitch.isChecked() ? "管理登录" : "用户登录");
        }
    }

    void findView() {
        showBarcodeInfo();
        setActTitle();
        actionBar.setBackAction(null);
       /* actionBar.setBackAction(new BsoftActionBar.Action() {
            @Override
            public int getDrawable() {
                return 0;
            }
            @Override
            public String getText() {
                return getString(R.string.menu_back);
            }
            @Override
            public void performAction(View view) {

            }
        });*/
     /*   actionBar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String sda = null;
                Log.d("zfq", "onClick: "+sda.getBytes());
            }
        });*/
     /*升级编号【56010059】============================================= start
                      PDA 自选扫码的简单实现
                 ================= classichu 2018/3/22 11:21
                 */
        actionBar.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("设备厂商:");
                stringBuilder.append(Build.MANUFACTURER);
                stringBuilder.append("\n");
                stringBuilder.append("设备型号:");
                stringBuilder.append(Build.MODEL);
                stringBuilder.append("\n");
                stringBuilder.append("序列号:");
                stringBuilder.append(mAppApplication.getSerialNumber());
                stringBuilder.append("\n");
                stringBuilder.append("扫码标志:\n");
                stringBuilder.append(BarCodeFactory.getBarCodeStr());
                showTipDialog(stringBuilder.toString());
                //new AlertDialog.Builder(view.getContext()).setMessage(stringBuilder.toString()).create().show();
             /*   String InternalStorageAvailableSpace = LogicalInternalStorageTool.getInternalStorageAvailableSpaceFixed(mContext);
                String InternalStorageTotalSpace = LogicalInternalStorageTool.getInternalStorageTotalSpaceFixed(mContext);
                String ExternalStorageAvailableSpace = LogicalExternalStorageTool.getExternalStorageAvailableSpaceFixed(mContext);
                String ExternalStorageTotalSpace = LogicalExternalStorageTool.getExternalStorageTotalSpaceFixed(mContext);
                Log.d("test", "\r\nInternalStorageAvailableSpace: "+InternalStorageAvailableSpace);
                Log.d("test", "InternalStorageTotalSpace: "+InternalStorageTotalSpace);
                Log.d("test", "ExternalStorageAvailableSpace: "+ExternalStorageAvailableSpace);
                Log.d("test", "ExternalStorageTotalSpace: "+ExternalStorageTotalSpace);*/
                return true;
            }
        });
        /* =============================================================== end */

        tvUname = (AutoCompleteTextView) findViewById(R.id.tvUname);
        initAutoTxt();

        etPwd = (EditText) findViewById(R.id.etPwd);
        // settingCk = (CheckBox) findViewById(R.id.login_type_cb);
        mSwitch = (SwitchCompat) findViewById(R.id.login_switch);
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setActTitle();
            }
        });
        clearUsrImg = (ImageView) findViewById(R.id.login_clear_usr_img);
        clearPwdImg = (ImageView) findViewById(R.id.login_clear_pwd_img);
        clearUsrImg.setOnClickListener(this);
        clearPwdImg.setOnClickListener(this);

        tvUname.setOnFocusChangeListener(new UsrFocusChangeListener());
        etPwd.setOnFocusChangeListener(new PwdFocusChangeListener());
        tvUname.addTextChangedListener(new UsrTextWatcher());
        etPwd.addTextChangedListener(new PwdTextWatcher());
        btnLogin = (Button) findViewById(R.id.btnLogin);
        ivSign = (ImageView) findViewById(R.id.ivSign);
        ivSign.setVisibility(View.GONE);
        ivSign.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                writePadDialog = new WritePadDialog(LoginActivity.this, getApplication(), new DialogListener() {
                    @Override
                    public void refreshActivity(Object object) {
                        ivSign.setImageBitmap((Bitmap) object);
                    }

                    @Override
                    public void saveImage(byte[] bytes) {
                        SignData signData = new SignData();
                        signData.HSGH = "1";
                        signData.ZYH = "1";
                        signData.BQDM = "1";
                        signData.Image = bytes;
                        signData.Type = "1";
                        signData.GSLX = "1";
                        signData.QMDH = "1";
                        signData.JGID = "999";
                        writePadDialog.SaveImage(signData);
                    }
                }, true);
                writePadDialog.show();
            }
        });
        ivSign2 = (ImageView) findViewById(R.id.ivSign2);
        ivSign2.setVisibility(View.GONE);
        ivSign2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SignData signData = new SignData();
                signData.HSGH = "1";
                signData.ZYH = "1";
                signData.BQDM = "1";
                signData.Type = "1";
                signData.GSLX = "1";
                signData.QMDH = "1";
                signData.JGID = "999";
                writePadDialog = new WritePadDialog(
                        LoginActivity.this, getApplication(), ivSign2, true);
                writePadDialog.GetImage(signData);
            }
        });

        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTimeHelper.getServerDateTime();

                String username = tvUname.getText().toString();
                String password = etPwd.getText().toString();

                if (EmptyTool.isBlank(username)) {
                    // edit by hy
                    //##  tvUname.setError("用户名不能为空");
                    showPopupError(clearUsrImg, "用户名不能为空");
                    tvUname.requestFocus();
                    return;
                }

                if (mSwitch.isChecked()) {
                    // 本地登录
                    if (EmptyTool.isBlank(password)) {
                        //### etPwd.setError("密码不能为空");
                        showPopupError(clearPwdImg, "密码不能为空");
                        etPwd.requestFocus();
                        return;
                    }
                    new LocalLoginTask().execute(username, password);
                    return;
                }
                //
                KeyBoardTool.hideKeyboard(v);
                if ("root".equals(username)) {
                    DialogManager.showClassicDialog(mFragmentActivity, "温馨提示",
                            "当前账号root,但未选择管理登录\n是否继续用户登录？",
                            new OnBtnClickListener() {

                                @Override
                                public void onBtnClickOk(DialogInterface dialogInterface) {

                                    goToLoginLogic(username);

                                }
                            });
                } else {
                    goToLoginLogic(username);
                }

            }
        });
    }

    private void showBarcodeInfo() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("设备厂商:");
        stringBuilder.append(Build.MANUFACTURER);
        stringBuilder.append("\n");
        stringBuilder.append("设备型号:");
        stringBuilder.append(Build.MODEL);
        stringBuilder.append("\n");
        stringBuilder.append("扫码标志:");
        stringBuilder.append(BarCodeFactory.getBarCodeStr());
        Log.e(TAG, "showBarcodeInfo: " + stringBuilder.toString());
    }

    private void goToLoginLogic(String username) {
        // 测试
        if (Constant.DEBUG_LOCAL) {
            remoteLogin("1");
        } else {
            // 获取机构列表
            GetAgencyTask task = new GetAgencyTask();
            task.execute(username);
            tasks.add(task);
        }
    }

    private ClassicPopupWindow mClassicPopupWindow;

    private void hidePopupError() {
        if (mClassicPopupWindow != null) {
            mClassicPopupWindow.dismiss();
        }
    }

    private void showPopupError(View anchor, CharSequence errorMsg) {
        hidePopupError();
        final TextView textView = new TextView(mContext);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setText(errorMsg);
        int padding = 10;
        textView.setPadding(padding, padding, padding, padding);
        textView.setBackgroundColor(Color.WHITE);
        mClassicPopupWindow = new ClassicPopupWindow.Builder(mContext).setEnableOutsideTouchDismiss(false).setView(textView).build();
        mClassicPopupWindow.showAsLeft_AnchorCenter_Center(anchor);
    }

    private void initAutoTxt() {

        if (tvUname != null) {
            tvUname.setThreshold(1);
            new NameTask(NameTask.GET_NAME).execute();
            // 注意此处是getSupportLoaderManager()，而不是getLoaderManager()方法。
            getSupportLoaderManager().initLoader(0, null, this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (etPwd != null) {
            etPwd.setText(null);
        }
    }

    @Override
    public void onStop() {
        /*if (progressDialog != null) {
            progressDialog.stop();
        }*/
        super.onStop();
        hideLoadingDialog();
    }

    @Override
    public void onStart() {
        super.onStart();
        //## fixme etPwd.setText(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //
        mUpdateBroadcastReceiver.unregisterReceiver();
        //
//        PgyUpdateManager.unregister();
        // 彻底关闭程序
        stopService(new Intent(this, FloatingWindowService.class));
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri uri = Database.User.CONTENT_URI;
        String[] projection = {BaseColumns._ID, Database.User.USER_NAME};
        String selection = Database.User.USER_NAME + " IS NOT NULL";
        CursorLoader cursorLoader = new CursorLoader(this, uri, projection,
                selection, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            adapter = new AutoCompleteAdapter(this, cursor);
            tvUname.setAdapter(adapter);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (adapter != null) {
            adapter.changeCursor(null);
        }
    }

    /**
     *获取PDAInfo信息
     * @return
     */
    public int getPDAInfo(){
        if(!SettingUtils.isCustomBarcode(this)) return -1;
        PDAInfo pdaInfo = application.pdaInfo;

        if(pdaInfo != null) {

            if (!Build.MANUFACTURER.toLowerCase().equals((StringUtils.isEmpty(pdaInfo.MANUER) ? "" : pdaInfo.MANUER).toLowerCase())
                    || !Build.MODEL.toLowerCase().equals((StringUtils.isEmpty(pdaInfo.MODEL) ? "" : pdaInfo.MODEL).toLowerCase())) {
                return -1;
            }
        }

        PDAInfoAsyncTask asyncTask = new PDAInfoAsyncTask();
        asyncTask.execute(Build.MANUFACTURER, Build.MODEL);
        tasks.add(asyncTask);

        return 1;
    }

    /**
     * 异步PDAInfo获取
     */
    public class PDAInfoAsyncTask extends AsyncTask<String, Void, Response<PDAInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(R.string.tip_login_load_pdainfo);
        }

        @Override
        protected Response<PDAInfo> doInBackground(String... params) {
            UserApi api = UserApi.getInstance(getApplicationContext());
            return api.GetPDAInfo(params[0], params[1]);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Response<PDAInfo> result) {
            super.onPostExecute(result);
            hideLoadingDialog();
            if (tasks.contains(this)) {
                tasks.remove(this);
            }

            if (null != result) {
                if (result.ReType == 0) {
                    AppApplication app = (AppApplication) getApplication();
                    app.pdaInfo = result.Data;

                    if(app.pdaInfo == null){
                        startFlotWin();
                    }

                    if(result.Data != null){
                        barCode = BarCodeFactory.getBarCode();
                    }else{
                        //startFlotWin();
                    }
                }
            } else {
                showTipDialog("获取扫描头接口信息失败");
                SpeechSynthesizerFactory.getInstance().speak("获取扫描头接口信息失败");
            }

        }
    }

    /**
     * 异步登录.
     */
    public class LoginAsyncTask extends AsyncTask<String, Void, Response<LoginResponse>> {

        public static final byte LOGIN_WITH_PWD = 0;

        public static final byte LOGIN_WITH_BARCODE = 1;

        private byte mType = LOGIN_WITH_PWD;

        public LoginAsyncTask(byte type) {
            mType = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(R.string.tip_login_ing);
        }

        @Override
        protected Response<LoginResponse> doInBackground(String... params) {
            String data = "";
            if (params == null || params.length < 1) {
                return null;
            }

            UserApi api = UserApi.getInstance(getApplicationContext());
            switch (mType) {
                case LOGIN_WITH_BARCODE:
                    // 条码登录
                    String barcode = params[0];
                    authVo.Barcode = barcode;
                    String[] arr = barcode.split("_");
                    String jgid = "1";
                    if (arr.length == 2) {
                        jgid = barcode.split("_")[1];
                    }
                    try {
                        data = JsonUtil.toJson(authVo);
                        data = "Scan" + data;
                        data = URLEncoder.encode(data, "UTF-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mAppApplication.authorizationString = data;
                    return api.SannerLogin(params[0], jgid);

                case LOGIN_WITH_PWD:
                    if (params.length < 3) {
                        return null;
                    }
                    String usrname = params[0];
                    String password = params[1];
                    jgid = params[2];
                    authVo.Account = params[0];
                    authVo.PWD = params[1];
                    authVo.JGID = params[2];
                    try {
                        data = JsonUtil.toJson(authVo);
                        data = "Basic" + data;
                        data = URLEncoder.encode(data, "UTF-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mAppApplication.authorizationString = data;
                    Response<LoginResponse> model = api.login(usrname, password, jgid);
                    return model;
                default:
                    return null;
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Response<LoginResponse> result) {
            super.onPostExecute(result);
            hideLoadingDialog();
            if (tasks.contains(this)) {
                tasks.remove(this);
            }
            if (null != result) {
                if (result.ReType == 0) {
                    int ret = getPDAInfo();
                    if(ret < 1){
                        startFlotWin();
                    }

                    LoginUser user = result.Data.LonginUser;
                    authVo.Account = user.YHDM;
                    authVo.Name = user.YHXM;
                    String data = "";
                    try {
                        data = JsonUtil.toJson(authVo);
                        data = "Scan" + data;
                        data = URLEncoder.encode(data, "UTF-8");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mAppApplication.authorizationString = data;
                    saveUser(user);

                    List<AreaVo> aList = result.Data.Areas;
                    Vector<AreaVo> vector = new Vector<AreaVo>(aList);
                    mAppApplication.setAreaList(vector);

                    TimeVo timeVo = result.Data.TimeVo;
                    String serverDateTime = timeVo.Time;
                    if (!TextUtils.isEmpty(serverDateTime)) {
                        DateTimeHelper.initServerDateTime(serverDateTime);
                        mAppApplication.updateSystemDateTime(serverDateTime);
                    }

                    mAppApplication.JSESSIONID = result.Data.SessionId;
                    // Log.i(Constant.TAG_COMM, "zfq JSESSIONID: " + mAppApplication.JSESSIONID);
                    mAppApplication.userConfig = result.Data.userConfig;

                    startMqtt();
                    startSettingService();

                    Intent intent = new Intent(LoginActivity.this,
                            MainActivity.class);
                  /*  Intent intent = new Intent(LoginActivity.this,
                            com.bsoft.mob.ienr.MainActivity.class);*/
                    startActivity(intent);

                } else {
                    showTipDialog(R.string.tip_login_error);
                    SpeechSynthesizerFactory.getInstance().speak(R.string.tip_login_error);
                }
            } else {
                showTipDialog(R.string.tip_login_error);
                SpeechSynthesizerFactory.getInstance().speak(R.string.tip_login_error);
            }

        }

        private void saveUser(LoginUser user) {

            if (null == user) {
                return;
            }
            mAppApplication.user = user;
            mAppApplication.jgId = user.JGID;
            new NameTask(NameTask.SAVE_NAME).execute(user);
        }
    }

    @Override
    public void onConfirmSet(String action) {
        if (TextUtils.equals(action, "againLogin")) {
            new AgainLoginUtil(LoginActivity.this, mAppApplication).showLoginDialog();
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.login_clear_usr_img) {
            tvUname.setText(null);

        } else if (id == R.id.login_clear_pwd_img) {
            etPwd.setText(null);
        }
    }

    /**
     * 启动设置服务
     */
    public void startSettingService() {

        Intent service = new Intent(getApplicationContext(),
                SettingService.class);
        startService(service);
    }

    public void startMqtt() {

        ArrayList<String> topics = new ArrayList<String>();

        // String topic1 = "{APP:\"BS-iENR\"}";
        // topics.add(topic1);

        String topic2 = "{APP:\"BS-iENR\",JGID:" + mAppApplication.jgId + "}";
        topics.add(topic2);

        // String topic1 = "{APP:\"BS-iENR\",JGID:" + application.jgId
        // + ",BQID:31}";
        // topics.add(topic1);

        String topic3 = "{APP:\"BS-iENR\",JGID:" + mAppApplication.jgId + ",YHID:\""
                + mAppApplication.user.YHID + "\"}";
        topics.add(topic3);

        MQTTTool.getInstance(getApplicationContext()).subscribeTopics(topics,
                null);

    }


    /**
     * 监听用户名EditView输入变化
     *
     * @author hy
     */
    protected class UsrFocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (hasFocus) {
                ViewAnimatorHelper.hideView_INVISIBLE(clearPwdImg);
                Editable edit = ((EditText) v).getText();
                if (EmptyTool.isBlank(edit)) {
                    ViewAnimatorHelper.hideView_INVISIBLE(clearUsrImg);
                } else {
                    ViewAnimatorHelper.showView(clearUsrImg);
                }
            } else {
                ViewAnimatorHelper.hideView_INVISIBLE(clearUsrImg);
            }
        }

    }

    /**
     * 监听密码 EditView输入变化
     *
     * @author hy
     */
    protected class PwdFocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (hasFocus) {
                ViewAnimatorHelper.hideView_INVISIBLE(clearUsrImg);
                Editable edit = ((EditText) v).getText();
                if (EmptyTool.isBlank(edit)) {
                    ViewAnimatorHelper.hideView_INVISIBLE(clearPwdImg);
                } else {
                    ViewAnimatorHelper.showView(clearPwdImg);
                }
            } else {
                ViewAnimatorHelper.hideView_INVISIBLE(clearPwdImg);
            }
        }

    }

    /**
     * 监听用户名EditView字数变化
     *
     * @author hy
     */
    protected class UsrTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {

            hidePopupError();
            if (!EmptyTool.isBlank(s.toString())) {
                ViewAnimatorHelper.showView(clearUsrImg);
            } else {
                ViewAnimatorHelper.hideView_INVISIBLE(clearUsrImg);
            }
        }

    }

    /**
     * 监听密码EditView字数变化
     *
     * @author hy
     */
    protected class PwdTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {

            hidePopupError();
            if (!EmptyTool.isBlank(s.toString())) {
                ViewAnimatorHelper.showView(clearPwdImg);
            } else {
                ViewAnimatorHelper.hideView_INVISIBLE(clearPwdImg);
            }

        }

    }

    public class NameTask extends AsyncTask<LoginUser, Integer, String> {

        public static final byte GET_NAME = 1;
        public static final byte SAVE_NAME = 2;
        private byte mType = GET_NAME;

        public NameTask(byte mType) {
            this.mType = mType;
        }

        @Override
        protected String doInBackground(LoginUser... params) {

            switch (mType) {
                case GET_NAME:
                    SharedPreferences preferences = getSharedPreferences(
                            "user_pref", Context.MODE_PRIVATE);
                    return preferences.getString("user_name", "");
                case SAVE_NAME:
                    if (params == null || params.length < 1 || params[0] == null) {
                        return null;
                    }
                    LoginUser user = params[0];

                    saveUserToDB(user);
                    saveUserToPre(user.YHDM);
                default:
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            switch (mType) {
                case GET_NAME:
                    tvUname.setText(s);
                    tvUname.setSelection(s.length());
                default:
                    break;
            }
        }
    }

    class LocalLoginTask extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(R.string.tip_login_ing);
        }

        @Override
        protected Boolean doInBackground(String... params) {

            if (params == null || params.length < 2) {
                return null;
            }

            return validateUser(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(Boolean result) {

           /* if (progressDialog != null) {
                progressDialog.stop();
                progressDialog = null;
            }*/
            hideLoadingDialog();
            if (tasks.contains(this)) {
                tasks.remove(this);
            }
            if (result == null) {
                Log.e(Constant.TAG, "LocalLoginTask result is null");
                return;
            }
            if (result) {

                LoginUser user = new LoginUser();
                user.YHID = tvUname.getText().toString();
                user.YHXM = tvUname.getText().toString();
                user.JGID = "-1";
                mAppApplication.user = user;
                Intent intent = new Intent(LoginActivity.this,
                        SettingActivity.class);
                intent.putExtra(SettingActivity.START_TYPE_KEY,
                        SettingActivity.ADMIN_START);
                startActivityForResult(intent, RQT_SET_NET);
            } else {
                showTipDialog(R.string.tip_login_error);
                SpeechSynthesizerFactory.getInstance().speak(R.string.tip_login_error);
            }

        }

    }

    /**
     * 保存用户名至数据库
     *
     * @param user
     */
    public void saveUserToDB(LoginUser user) {

        if (user == null) {
            return;
        }
        Uri uri = Database.User.CONTENT_URI;
        String[] projection = {BaseColumns._ID};
        String selection = Database.User.REMOTE_ID + "=? AND "
                + Database.User.AGENT_ID + "=? ";
        String[] selectionArgs = {user.YHID, user.JGID};
        Cursor cursor = getContentResolver().query(uri, projection, selection,
                selectionArgs, null);
        if (cursor.getCount() < 1) {
            ContentValues values = new ContentValues();
            values.put(Database.User.USER_NAME, user.YHDM);
            values.put(Database.User.AGENT_ID, user.JGID);
            values.put(Database.User.REMOTE_ID, user.YHID);
            getContentResolver().insert(uri, values);
        }
        cursor.close();

    }

    /**
     * 验证用户本地是否存在
     *
     * @param username
     * @param password
     * @return 存在返回true, 否则返回false
     */
    public boolean validateUser(String username, String password) {

        if (EmptyTool.isBlank(username) || EmptyTool.isBlank(password)) {
            return false;
        }
        boolean isRight = false;
        Uri uri = Database.User.CONTENT_URI;
        Log.e("zfq", "validateUser: uri:" + uri);
        String[] projection = {Database.User.USER_NAME, Database.User.PASSWORD};
        String selection = Database.User.USER_NAME + "=?";
        String[] selectionArgs = {username};
        try {
            Cursor cursor = getContentResolver().query(uri, projection, selection,
                    selectionArgs, null);
            if (cursor.moveToNext()) {
                String pwdStr = cursor.getString(1);
                isRight = password.equals(pwdStr);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e("zfq", "validateUser: " + e.getMessage());

            for (StackTraceElement stackTraceElement :
                    e.getStackTrace()) {
                Log.e("zfq", "validateUser: " + stackTraceElement);
            }
        }

        return isRight;
    }

    /**
     * 保存用户名至preferences
     *
     * @param username
     * @return
     */
    public boolean saveUserToPre(String username) {

        if (EmptyTool.isBlank(username)) {
            return false;
        }

        SharedPreferences preferences = getSharedPreferences("user_pref",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("user_name", username);
        return editor.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RQT_SET_NET && resultCode == RESULT_OK) {

            if (WifiUtil.openWifi(getApplicationContext())) {
                // 自动连接到配制的WIFI SSID
                WifiService.actionStart(getApplicationContext());
            }

            application.initOverallField();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {

            application.unRegistWifiReceiver();
        }
        return super.onKeyDown(keyCode, event);
    }

    public class GetAgencyTask extends AsyncTask<String, Integer, Response<ArrayList<Agency>>> {

        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.tip_login_load_agency);
        }

        @Override
        protected Response<ArrayList<Agency>> doInBackground(String... params) {

            if (params == null || params.length < 1) {
                return null;
            }
            String data = "";
            authVo.Account = tvUname.getText().toString();
            authVo.PWD = etPwd.getText().toString();
            authVo.JGID = null;
            try {
                data = JsonUtil.toJson(authVo);
                data = "Basic" + data;
                data = URLEncoder.encode(data, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            mAppApplication.authorizationString = data;
            UserApi api = UserApi.getInstance(getApplicationContext());
            return api.GetAgency(params[0]);

        }

        @Override
        protected void onPostExecute(Response<ArrayList<Agency>> result) {

          /*  if (progressDialog != null) {
                progressDialog.stop();
                progressDialog = null;
            }*/
            hideLoadingDialog();
            if (tasks.contains(this)) {
                tasks.remove(this);
            }
            if (null != result && result.ReType == 0) {
                ArrayList<Agency> list = result.Data;
                boolean ok = analyzeAgencyList(list);
                if (!ok) {
                    showTipDialog(R.string.tip_analyze_agency_error);
                    SpeechSynthesizerFactory.getInstance().speak(R.string.tip_analyze_agency_error);
                }
            } else {
                showTipDialog(R.string.tip_get_agency_error);
                SpeechSynthesizerFactory.getInstance().speak(R.string.tip_get_agency_error);
                return;
            }

        }

        /**
         * 解析机构列表
         *
         * @param list
         * @return
         */
        @SuppressWarnings("deprecation")
        private boolean analyzeAgencyList(ArrayList<Agency> list) {

            if (list == null || list.size() < 1) {
                return false;
            }
            if (list.size() > 1) {

                Bundle args = new Bundle();
                args.putParcelableArrayList("list", list);
                showDialogCompat(DIALOG_CHOICE_AGENT_MENU, args);

            } else { // 单独一条， 远程登录

                String jgid = list.get(0).JGID;
                remoteLogin(jgid);
            }
            return true;
        }
    }

    public void remoteLogin(String jgid) {

        String username = tvUname.getText().toString();
        String password = etPwd.getText().toString();
        LoginAsyncTask asyncTask = new LoginAsyncTask(
                LoginAsyncTask.LOGIN_WITH_PWD);
        asyncTask.execute(username, password, jgid);
        tasks.add(asyncTask);
    }

    protected void showDialogCompat(int id, Bundle args) {

        switch (id) {

            case DIALOG_CHOICE_AGENT_MENU:

                final ArrayList<Agency> list = args.getParcelableArrayList("list");

                AgencyAdapter adapter = new AgencyAdapter(this, list);

                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {

                    @SuppressWarnings("deprecation")
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Agency agency = list.get(which);
                        if (agency != null) {
                            remoteLogin(agency.JGID);
                        }
                    }

                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext, getString(R.string.login_select_agency)))
                        .setSingleChoiceItems(adapter, -1, onClickListener)
                        .setNegativeButton("取消登录",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                    }
                                });

                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            default:
        }

    }

}
