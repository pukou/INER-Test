package com.bsoft.mob.ienr.activity;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.api.PatientApi;
import com.bsoft.mob.ienr.barcode.BarCodeFactory;
import com.bsoft.mob.ienr.barcode.IBarCode;
import com.bsoft.mob.ienr.db.Database;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.BCCWBean;
import com.bsoft.mob.ienr.model.BCRYBean;
import com.bsoft.mob.ienr.model.BCSZBean;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.prefs.MainGuidePrefUtils;
import com.bsoft.mob.ienr.util.prefs.SettingUtils;
import com.bsoft.mob.ienr.util.prefs.UserGuidePrefUtils;
import com.bsoft.mob.ienr.util.prefs.WifiPrefUtils;
import com.bsoft.mob.ienr.util.tools.KeyBoardTool;
import com.bsoft.mob.ienr.util.tools.OkHttpTool;
import com.bsoft.mob.ienr.util.tools.StringOkHttpCallback;
import com.bsoft.mob.ienr.util.tools.ThreadTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;
import com.classichu.dialogview.manager.DialogManager;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置页
 */
public class SettingActivity extends BaseBarcodeActivity {

    /**
     * intent key ,用于标识启动SettingActivity意图。value 为 {@link #ADMIN_START}
     * 表明是管理员启动设置页；value 为 {@link #USER_START}表明是普通用户启动设置页
     */
    public static final String START_TYPE_KEY = "start_type_key";
    public static final byte ADMIN_START = 1;
    public static final byte USER_START = 0;

    public static final String TAG = "SettingActivity";
    private static final int DIALOG_CHOICE_MAIN_MENU = 2;
    private static final int DIALOG_CHOICE_USER_MENU = 3;
    private static final int RQT_GET_SSID = 0;

    EditText ipEdit_Java;
    EditText portEdit_Java;
    EditText ipEdit;
    EditText portEdit;
    EditText ssidEdit;
    EditText pwdEdit;
    ImageButton setting_test_btn;
    Spinner localIpSpinner;
    LinearLayout stub;
    Spinner mSecretSpinner;
    /*升级编号【56010059】============================================= start
                 PDA 自选扫码的简单实现
            ================= classichu 2018/3/22 11:21
            */
    Spinner mPDASpinner;
    /* =============================================================== end */

    EditText pushIpEdit;
    EditText pushPortEdit;

    SwitchCompat mSwitch;
    SwitchCompat logSwitch;
    SwitchCompat barcodeSwitch;
    SwitchCompat  customSwitch;
    /*
       升级编号【56010049】============================================= start
       病人列表:管理员可设置是否禁用病人列表点击进入：默认不禁用
       ================= Classichu 2017/10/18 9:34
       */
    SwitchCompat itemCanNotSwitch;
    /* =============================================================== end */
    private int mType;


    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findView();
        init();
    }

    public void findView() {


        ipEdit_Java = (EditText) findViewById(R.id.setting_set_ip_edit_java);
        portEdit_Java = (EditText) findViewById(R.id.setting_set_port_edit_java);

        ipEdit = (EditText) findViewById(R.id.setting_set_ip_edit);
        portEdit = (EditText) findViewById(R.id.setting_set_port_edit);

        pushIpEdit = (EditText) findViewById(R.id.setting_push_ip_edit);
        pushPortEdit = (EditText) findViewById(R.id.setting_push_port_edit);

        ssidEdit = (EditText) findViewById(R.id.setting_set_ssid_edit);
        pwdEdit = (EditText) findViewById(R.id.setting_set_ssid_password_edit);

        setting_test_btn = (ImageButton) findViewById(R.id.setting_test_btn);
        setting_test_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyBoardTool.hideKeyboard(v);
                //
                String ip_temp = ipEdit_Java.getText().toString();
                String port_temp = portEdit_Java.getText().toString();
                if (!TextUtils.isEmpty(ip_temp) && !TextUtils.isEmpty(port_temp)) {
                    String url = String.format("http://%s:%s/NIS", ip_temp, port_temp);

                    DialogManager.showLoadingDialog(mFragmentActivity,
                            getString(R.string.doing), true);
                    new OkHttpTool().tag(this).callback(new StringOkHttpCallback() {
                        @Override
                        public void OnSuccess(String result, int statusCode) {
                            ThreadTool.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideLoadingDialog();
                                    showSnack(R.string.setting_test_url_success);
                                }
                            });
                        }

                        @Override
                        public void OnError(String errorMsg, int statusCode) {
                            ThreadTool.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    hideLoadingDialog();
                                    showTipDialog(R.string.setting_test_url_failed);
                                    //
                                    KeyBoardTool.showKeyboard(portEdit_Java);
//                        portEdit_Java.requestFocus();
                                }
                            });
                        }
                    }).doGetUrl(url);
                } else {
                    showSnack("请输入ip地址和端口！");
                }
            }
        });

        SpinnerLayout id_spinner_layout_ip_local = (SpinnerLayout) findViewById(R.id.id_spinner_layout_ip_local);
        localIpSpinner = id_spinner_layout_ip_local.getSpinner();
        stub = (LinearLayout) findViewById(R.id.setting_stub);
        SpinnerLayout id_spinner_layout_secret_type = (SpinnerLayout) findViewById(R.id.id_spinner_layout_secret_type);
        mSecretSpinner = id_spinner_layout_secret_type.getSpinner();
             /*升级编号【56010059】============================================= start
                      PDA 自选扫码的简单实现
                 ================= classichu 2018/3/22 11:21
                 */
        SpinnerLayout id_spinner_layout_pda_type = (SpinnerLayout) findViewById(R.id.id_spinner_layout_pda_type);
        mPDASpinner = id_spinner_layout_pda_type.getSpinner();
        /* =============================================================== end */

        mSwitch = (SwitchCompat) findViewById(R.id.setting_vib_switch);
        logSwitch = (SwitchCompat) findViewById(R.id.setting_log_switch);
        barcodeSwitch = (SwitchCompat) findViewById(R.id.setting_parse_barcode_switch);
        customSwitch = (SwitchCompat) findViewById(R.id.setting_pda_custom_barcode);
        itemCanNotSwitch = (SwitchCompat) findViewById(R.id.setting_sicker_item_can_not_click_switch);
        Intent intent = getIntent();
        mType = intent.getByteExtra(START_TYPE_KEY, USER_START);
        if (mType == USER_START) {
            findViewById(R.id.setting_admin_layout).setVisibility(View.GONE);
        }

    }

    private void init() {
        actionBar.setTitle("设置");
        actionBar.addAction(new Action() {

            @Override
            public void performAction(View view) {
                new PreferTask().execute(PreferTask.WRITE_PREF);
            }

            @Override
            public String getText() {
                return "保存";
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }
        });


        // init spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.setting_ip_array,
                android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        localIpSpinner.setAdapter(adapter);
        localIpSpinner.setOnItemSelectedListener(onLSListener);

        // init spinner
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(
                this, R.array.setting_secret_type_array,
                android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSecretSpinner.setAdapter(adapter2);

        new PreferTask().execute(PreferTask.READ_PREF);
        logSwitch.setChecked(SettingUtils.isJL_Log(mContext));
         /*
            升级编号【56010013】============================================= start
            标本采集：是否需求转换条码:需要加入参数控制：是否需求转换条码
            ================= Classichu 2017/10/18 9:34
            */
        barcodeSwitch.setChecked(SettingUtils.isNeedParseBarcode(mContext));
        customSwitch.setChecked(SettingUtils.isCustomBarcode(mContext));
        /* =============================================================== end */
             /*
            升级编号【56010049】============================================= start
            病人列表:管理员可设置是否禁用病人列表点击进入：默认不禁用
            ================= Classichu 2017/10/18 9:34
            */
        itemCanNotSwitch.setChecked(SettingUtils.isSickerItemCanNotClick(SettingActivity.this));
        /* =============================================================== end */
             /*升级编号【56010059】============================================= start
                      PDA 自选扫码的简单实现
                 ================= classichu 2018/3/22 11:21
                 */
        List<String> pdaTypeList = new ArrayList<>();
        List<Pair<String, IBarCode>> pairList = BarCodeFactory.getPdaPairList();
        for (Pair<String, IBarCode> stringIBarCodePair : pairList) {
            pdaTypeList.add(stringIBarCodePair.first);
        }
        ArrayAdapter<String> adapter_pda = new ArrayAdapter<>(SettingActivity.this,
                android.R.layout.simple_spinner_item,
                android.R.id.text1, pdaTypeList);
        adapter_pda.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPDASpinner.setAdapter(adapter_pda);
        /* =============================================================== end */

    }

    private OnItemSelectedListener onLSListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            // 静态
            if (position == 1) {
                stub.setVisibility(View.VISIBLE);
            } else { // 动态
                stub.setVisibility(View.GONE);
            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    };

/*	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.slide_up_in1,
					R.anim.slide_down_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}*/

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.slide_up_in1,
                R.anim.slide_down_out);
        super.onBackPressed();
    }

    /**
     * 用于读取和写入Preference
     */
    class PreferTask extends AsyncTask<Byte, Integer, Boolean> {

        public static final byte READ_PREF = 1;
        public static final byte WRITE_PREF = 2;

        private byte mCurType = READ_PREF;
        private String ipStr_Java;
        private String port_Java;
        private String ipStr;
        private String port;
        private String pushIpStr;
        private String pushPortStr;
        private String ssidStr;
        private String ssidPwd;
        private int cnnType;
        private int secretType;
        private int vib = 1;
        private boolean custom_barcode = true;
        /*
       升级编号【56010049】============================================= start
       病人列表:管理员可设置是否禁用病人列表点击进入：默认不禁用
       ================= Classichu 2017/10/18 9:34
       */
        private boolean is_can_not_clcik;
        /* =============================================================== end */
             /*升级编号【56010059】============================================= start
                      PDA 自选扫码的简单实现
                 ================= classichu 2018/3/22 11:21
                 */
        private int pdaTypePos;
        /* =============================================================== end */

        @Override
        protected Boolean doInBackground(Byte... params) {

            if (null == params || params.length < 1) {
                return null;
            }
            mCurType = params[0];

            switch (mCurType) {
                case READ_PREF:
                    ipStr_Java = WifiPrefUtils.getIPForJava(SettingActivity.this);
                    port_Java = WifiPrefUtils.getPortForJava(SettingActivity.this);
                    ipStr = WifiPrefUtils.getIP(SettingActivity.this);
                    port = WifiPrefUtils.getPort(SettingActivity.this);
                    ssidStr = WifiPrefUtils.getSSID(SettingActivity.this);
                    ssidPwd = WifiPrefUtils.getPassword(SettingActivity.this);
                    cnnType = WifiPrefUtils.getCnnType(SettingActivity.this);
                    secretType = WifiPrefUtils.getSecretType(SettingActivity.this);
                    pushIpStr = WifiPrefUtils.getPushIP(SettingActivity.this);
                    pushPortStr = WifiPrefUtils.getPushPort(SettingActivity.this);
                     /*
            升级编号【56010049】============================================= start
            病人列表:管理员可设置是否禁用病人列表点击进入：默认不禁用
            ================= Classichu 2017/10/18 9:34
            */
                    is_can_not_clcik = SettingUtils.isSickerItemCanNotClick(SettingActivity.this);
                    /* =============================================================== end */
                     /*升级编号【56010059】============================================= start
                      PDA 自选扫码的简单实现
                 ================= classichu 2018/3/22 11:21
                 */
                    pdaTypePos = BarCodeFactory.loadPDATypePos_SharedPrefe();

                    custom_barcode = SettingUtils.isCustomBarcode(SettingActivity.this);
                    /* =============================================================== end */

                    readSettingsInDb();
                    return true;
                case WRITE_PREF:
                    //
                         /*升级编号【56010059】============================================= start
                      PDA 自选扫码的简单实现
                 ================= classichu 2018/3/22 11:21
                 */
                    int pda_type_pos = mPDASpinner.getSelectedItemPosition();
                    BarCodeFactory.savePDATypePos_SharedPrefe(pda_type_pos);
                    /* =============================================================== end */

                    //
                    boolean vib = mSwitch.isChecked();
                    saveSettingsInDb(vib);
                    return saveSettingsInPref();
                default:

            }

            return null;
        }

        private void readSettingsInDb() {

            Uri uri = Database.Setting.CONTENT_URI;

            String[] projection = {Database.Setting.VIB};
            String selection = Database.Setting.USER + "=?";
            String[] selectionArgs = {getLocalUserId()};
            ContentResolver crl = getContentResolver();
            Cursor cursor = crl.query(uri, projection, selection,
                    selectionArgs, null);
            if (cursor.moveToNext()) {
                vib = cursor.getInt(0);
            }
            cursor.close();
        }

        public String getLocalUserId() {

            String id = "-1";


            Uri uri = Database.User.CONTENT_URI;

            String[] projection = {Database.User._ID};

            String selection = Database.User.USER_NAME + "=? " + "AND "
                    + Database.User.AGENT_ID + "=? ";
            String[] selectionArgs = {mAppApplication.user.YHID, mAppApplication.user.JGID};
            ContentResolver crl = getContentResolver();
            Cursor cursor = crl.query(uri, projection, selection,
                    selectionArgs, null);
            if (cursor.moveToNext()) {
                id = cursor.getString(0);
            }
            cursor.close();
            return id;
        }

        /**
         * 保存数据至Preference
         *
         * @return
         */
        private boolean saveSettingsInPref() {

            SharedPreferences pref = getSharedPreferences(
                    WifiPrefUtils.WEB_PREF, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(WifiPrefUtils.IP_KEY_JAVA, ipEdit_Java.getText().toString()
                    .trim());
            editor.putString(WifiPrefUtils.PORT_KEY_JAVA, portEdit_Java.getText()
                    .toString().trim());
            editor.putString(WifiPrefUtils.IP_KEY, ipEdit.getText().toString()
                    .trim());
            editor.putString(WifiPrefUtils.PORT_KEY, portEdit.getText()
                    .toString().trim());
            editor.putString(WifiPrefUtils.PUSH_IP_KEY, pushIpEdit.getText()
                    .toString().trim());
            editor.putString(WifiPrefUtils.PUSH_PORT_KEY, pushPortEdit
                    .getText().toString().trim());

            editor.putString(WifiPrefUtils.SSID_KEY, ssidEdit.getText()
                    .toString().trim());
            editor.putString(WifiPrefUtils.SSID_PWD_KEY, pwdEdit.getText()
                    .toString().trim());
            editor.putInt(WifiPrefUtils.CNN_TYPE_KEY,
                    localIpSpinner.getSelectedItemPosition());
            editor.putInt(WifiPrefUtils.SECRET_TYPE_KEY,
                    mSecretSpinner.getSelectedItemPosition());

            SettingUtils.setJL_Log(mContext, logSwitch.isChecked());
             /*
            升级编号【56010013】============================================= start
            标本采集：是否需求转换条码:需要加入参数控制：是否需求转换条码
            ================= Classichu 2017/10/18 9:34
            */
            SettingUtils.setNeedParseBarcode(mContext, barcodeSwitch.isChecked());
            SettingUtils.saveCustomBarcode(mContext, customSwitch.isChecked());

            /* =============================================================== end */
                 /*
            升级编号【56010049】============================================= start
            病人列表:管理员可设置是否禁用病人列表点击进入：默认不禁用
            ================= Classichu 2017/10/18 9:34
            */
            SettingUtils.saveSickerItemCanNotClick(SettingActivity.this, itemCanNotSwitch.isChecked());
            /* =============================================================== end */
            return editor.commit();
        }

        /**
         * 保存数据至数据库
         *
         * @param vib
         */
        private void saveSettingsInDb(boolean vib) {


            mAppApplication.getSettingConfig().vib = vib;
            ContentResolver crl = getContentResolver();
            Uri uri = Database.Setting.CONTENT_URI;
            ContentValues values = new ContentValues();
            values.put(Database.Setting.VIB, vib);

            String userId = getLocalUserId();
            if (hasExist()) {
                String where = Database.Setting.USER + "=?";
                String[] selectionArgs = {userId};
                crl.update(uri, values, where, selectionArgs);
            } else {
                values.put(Database.Setting.USER, userId);
                crl.insert(uri, values);
            }

        }

        /**
         * 判断在数据库中是否存在一条记录
         *
         * @return
         */
        public boolean hasExist() {

            boolean exist = false;
            Uri uri = Database.Setting.CONTENT_URI;

            String[] projection = {Database.Setting._ID};
            String selection = Database.Setting.USER + "=?";
            String[] selectionArgs = {getLocalUserId()};
            ContentResolver crl = getContentResolver();
            Cursor cursor = crl.query(uri, projection, selection,
                    selectionArgs, null);
            if (cursor.getCount() > 0) {
                exist = true;
            }
            cursor.close();
            return exist;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            hideSwipeRefreshLayout();
            if (aBoolean == null) {
                Log.e(TAG, "PreferTask result is null");
                return;
            }
            switch (mCurType) {
                case READ_PREF:
                    ipEdit_Java.setText(ipStr_Java);
                    portEdit_Java.setText(port_Java);
                    ipEdit_Java.setText(ipStr_Java);
                    portEdit_Java.setText(port_Java);
                    ipEdit.setText(ipStr);
                    portEdit.setText(port);
                    ssidEdit.setText(ssidStr);
                    pwdEdit.setText(ssidPwd);
                    localIpSpinner.setSelection(cnnType);
                    mSwitch.setChecked(vib != 0);
                    itemCanNotSwitch.setChecked(is_can_not_clcik);
                    pushIpEdit.setText(pushIpStr);
                    pushPortEdit.setText(pushPortStr);
                         /*升级编号【56010059】============================================= start
                      PDA 自选扫码的简单实现
                 ================= classichu 2018/3/22 11:21
                 */
                    //pdaTypePos
                    mPDASpinner.setSelection(pdaTypePos);
                    /* =============================================================== end */

                    break;
                case WRITE_PREF:
                    setResult(RESULT_OK);
                    finish();
                    break;
                default:
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }
    }

    protected void showCreateDialogCompat(int id) {

        switch (id) {
            case DIALOG_CHOICE_MAIN_MENU:


                final boolean[] slt = MainGuidePrefUtils.getsltMenus(this);

                DialogInterface.OnMultiChoiceClickListener onMultiChoiceClickListener = new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        if (isChecked) {
                            slt[which] = true;
                        } else {
                            slt[which] = false;
                        }
                    }
                };
                DialogInterface.OnClickListener okClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        new UpdateMenusTask(slt)
                                .execute(UpdateMenusTask.UPDATE_MAIN_MENU);
                    }
                };

                DialogInterface.OnClickListener cancelClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext, getString(R.string.setting_main_menu)))
                        .setMultiChoiceItems(R.array.setting_main_menu_array, slt,
                                onMultiChoiceClickListener)
                        .setPositiveButton(android.R.string.ok, okClickListener)
                        .setNegativeButton(android.R.string.cancel,
                                cancelClickListener);
                builder.create().show();
                break;

            case DIALOG_CHOICE_USER_MENU:

                final boolean[] slted = UserGuidePrefUtils.getsltMenus(this);

                onMultiChoiceClickListener = new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which,
                                        boolean isChecked) {
                        if (isChecked) {
                            slted[which] = true;
                        } else {
                            slted[which] = false;
                        }
                    }
                };
                okClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        new UpdateMenusTask(slted)
                                .execute(UpdateMenusTask.UPDATE_USER_MENU);
                    }
                };
                cancelClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                };
                builder = new AlertDialog.Builder(this);
                builder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext, getString(R.string.setting_user_menu)))
                        .setMultiChoiceItems(R.array.setting_user_menu_array,
                                slted, onMultiChoiceClickListener)
                        .setPositiveButton(android.R.string.ok, okClickListener)
                        .setNegativeButton(android.R.string.cancel,
                                cancelClickListener);
                builder.create().show();
                break;
            default:

        }
    }

    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.setting_set_main_menu) {

            showCreateDialogCompat(DIALOG_CHOICE_MAIN_MENU);

        } else if (id == R.id.setting_set_user_menu) {

            showCreateDialogCompat(DIALOG_CHOICE_USER_MENU);
        } else if (id == R.id.setting_stub) {

            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
        } else if (id == R.id.setting_scan_btn) {
            Intent intent = new Intent(this, WifiScanActivity.class);
            startActivityForResult(intent, RQT_GET_SSID);
        } else if (id == R.id.setting_set_my_sick) {
            //设置我负责的病人
            actionGetMyGroupDataTask();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RQT_GET_SSID && resultCode == RESULT_OK) {
            ScanResult result = data.getParcelableExtra("result");
            analyScanResult(result);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void analyScanResult(ScanResult result) {

        if (result == null) {
            return;
        }
        ssidEdit.setText(result.SSID);
        getSecretType(result.capabilities);
    }

    /**
     * 获取加密类型
     *
     * @param capabilities
     */
    @SuppressLint("DefaultLocale")
    private void getSecretType(String capabilities) {

        if (capabilities == null) {
            mSecretSpinner.setSelection(2);
        } else if (capabilities.toLowerCase().contains("wpa")) {
            mSecretSpinner.setSelection(0);
        } else if (capabilities.toLowerCase().contains("wep")) {
            mSecretSpinner.setSelection(1);
        }
    }

    class UpdateMenusTask extends AsyncTask<Byte, Integer, Boolean> {

        public static final byte UPDATE_MAIN_MENU = 0;
        public static final byte UPDATE_USER_MENU = 1;

        private boolean[] states;

        private byte mType = UPDATE_MAIN_MENU;

        public UpdateMenusTask(boolean[] states) {
            this.states = states;
        }

        @Override
        protected Boolean doInBackground(Byte... params) {

            if (params == null || params.length < 1) {
                return false;
            }
            mType = params[0];
            boolean result = false;

            if (mType == UPDATE_USER_MENU) {
                result = UserGuidePrefUtils.saveMenus(getApplicationContext(),
                        states);
                mAppApplication.updateUserModel();
            } else if (mType == UPDATE_MAIN_MENU) {
                result = MainGuidePrefUtils.saveMenus(getApplicationContext(),
                        states);
                mAppApplication.updateMainModel();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            showMsgAndVoice(aBoolean ? "保存导航成功"
                    : "保存导航失败");

        }

    }


    private String[] mMyGroupItemStr;
    private boolean[] mMyGroupItemSelected;

    private void actionGetMyGroupDataTask() {
        GetMyGroupDataTask task = new GetMyGroupDataTask();
        tasks.add(task);
        task.execute();
    }

    private void actionSaveMyGroupDataTask() {
        SaveMyGroupDataTask task = new SaveMyGroupDataTask();
        tasks.add(task);
        task.execute();
    }
    class GetMyGroupDataTask extends AsyncTask<String, String, Response<List<BCSZBean>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(R.string.loading);
        }

        @Override
        protected Response<List<BCSZBean>> doInBackground(String... params) {
            if (mAppApplication == null) {
                return null;
            }
            String hsgh = mAppApplication.user.YHID;
            String jgid = mAppApplication.jgId;
            String bqdm = mAppApplication.getAreaId();
            //
            return PatientApi.getInstance(mContext).GetGroupCfgList(hsgh, bqdm, jgid);
        }

        @Override
        protected void onPostExecute(Response<List<BCSZBean>> result) {
            hideLoadingDialog();
            tasks.remove(this);
            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(mContext, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        actionGetMyGroupDataTask();
                    }
                }).showLoginDialog();
                return;
            } else if (result.ReType == 0) {
                //
                //显示
//                showCreateDialogCompat(DIALOG_CHOICE_MY_SICK_MENU);

                List<BCSZBean>  zCSZBeanList = new ArrayList<>();
                if (result.Data != null) {

                    List<BCSZBean> bcszBeans = result.Data;
                    //
                    zCSZBeanList.addAll(bcszBeans);
                }

                ExpandableListView expandableListView = new ExpandableListView(mContext);
                mRiskGroupListAdapter = new RiskGroupListAdapter();
                expandableListView.setAdapter(mRiskGroupListAdapter);
                //
                mRiskGroupListAdapter.refreshData(zCSZBeanList);

              /*  LinearLayout rroot = LayoutParamsHelper.buildLinearMatchWrap_V(mContext);
                for (BCSZBean bcszBean : mBCSZBeanList) {
                    CheckBox checkBox = new AppCompatCheckBox(mContext);
                    checkBox.setText(bcszBean.BCMC);
                    checkBox.setChecked(bcszBean.selected);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (buttonView == null || !buttonView.isPressed()) {
                                //不响应非点击引起的改变
                                return;
                            }
                            //赋值
                            bcszBean.selected = isChecked;
                        }
                    });
                    //
                    rroot.addView(checkBox);
                }*/
                //
                new AlertDialog.Builder(mContext)
                        .setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext, getString(R.string.setting_my_group_menu)))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //保存
                                actionSaveMyGroupDataTask();
                            }
                        })
                        .setView(expandableListView)
                        .setNegativeButton(android.R.string.cancel,
                                null).create().show();
            }


        }

    }


   RiskGroupListAdapter mRiskGroupListAdapter;

    class SaveMyGroupDataTask extends AsyncTask<String, String, Response<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(R.string.loading);
        }

        @Override
        protected Response<String> doInBackground(String... params) {
            String hsgh = mAppApplication.user.YHID;
            String jgid = mAppApplication.jgId;
            String bqdm = mAppApplication.getAreaId();
            List<BCRYBean> bcryBeans = new ArrayList<>();
            for (BCSZBean bcszBean : mRiskGroupListAdapter.getBcszBeanList()) {
                if (bcszBean.selected) {
                    BCRYBean bcryBean = new BCRYBean();
                    bcryBean.JLXH = null;
                    bcryBean.YGDM = hsgh;
                    bcryBean.JGID = jgid;
                    bcryBean.BCBH = bcszBean.BCBH;
                    bcryBean.BCMC = bcszBean.BCMC;
                    bcryBean.BQDM = bcszBean.BQDM;
                    bcryBeans.add(bcryBean);
                }
            }
            String json = null;
            try {
                json = JsonUtil.toJson(bcryBeans);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return PatientApi.getInstance(mContext).SaveGroupRYList(json,hsgh,bqdm);
        }

        @Override
        protected void onPostExecute(Response<String> result) {
            hideLoadingDialog();
            tasks.remove(this);
            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(mContext, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        actionSaveMyGroupDataTask();
                    }
                }).showLoginDialog();
                return;
            } else if (result.ReType == 0) {
                //
//                showMsgAndVoice(result.Msg);
                showMsgAndVoice("保存成功");
            }

        }

    }

    @Override
    public void initBarBroadcast() {
    }

/*

    class RiskGroupListAdapter extends BaseAdapter {
        private List<BCSZBean> bcszBeanList = new ArrayList<>();

        public List<BCSZBean> getBcszBeanList() {
            return bcszBeanList;
        }

        public void refreshData(List<BCSZBean> bcszBeans) {
            bcszBeanList.addAll(bcszBeans);
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return bcszBeanList.size();
        }

        @Override
        public Object getItem(int position) {
            return bcszBeanList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GroupHolder vHolder;
            if (convertView == null) {
                convertView = LayoutInflater
                        .from(mContext).inflate(
                                R.layout.layout_item_bar_check, parent, false);
                vHolder = new GroupHolder();
                vHolder.tv_name = (TextView) convertView
                        .findViewById(R.id.id_tv_for_bar_check);
                vHolder.tv_desc = (TextView) convertView
                        .findViewById(R.id.healthguid_datetime_txt);
                vHolder.checkBox = (android.widget.CheckBox) convertView
                        .findViewById(R.id.healthguid_cbpre);
                convertView.setTag(vHolder);
            } else {
                vHolder = (GroupHolder) convertView.getTag();
            }
            BCSZBean bcszBean = bcszBeanList.get(position);

            vHolder.tv_name.setText(bcszBean.BCMC);
//            vHolder.tv_desc.setText(bcszBean.BCBH);
            vHolder.checkBox.setSelected(bcszBean.selected);
            vHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView == null || !buttonView.isPressed()) {
                        //不响应非点击引起的改变
                        return;
                    }
                    //赋值
                    bcszBean.selected = isChecked;
                }
            });

            return convertView;
        }
    }

*/


    class RiskGroupListAdapter extends BaseExpandableListAdapter {

        private List<BCSZBean> bcszBeanList = new ArrayList<>();

        public List<BCSZBean> getBcszBeanList() {
            return bcszBeanList;
        }

        public void refreshData(List<BCSZBean> bcszBeans) {
            bcszBeanList.addAll(bcszBeans);
            this.notifyDataSetChanged();
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return bcszBeanList.get(groupPosition).bccwBeans.get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            ChildHolder vHolder;
            if (convertView == null) {
                convertView = LayoutInflater
                        .from(mContext).inflate(
                                R.layout.item_list_text_two_primary, parent, false);
                //!!!
                convertView.setBackgroundResource(R.color.classicViewBg);
                vHolder = new ChildHolder();
                vHolder.name = (TextView) convertView
                        .findViewById(R.id.name);
                vHolder.time = (TextView) convertView
                        .findViewById(R.id.time);

                //
                convertView.setTag(vHolder);
            } else {
                vHolder = (ChildHolder) convertView.getTag();
            }
            BCCWBean bccwBean = bcszBeanList.get(groupPosition).bccwBeans.get(childPosition);
            vHolder.name.setText(bccwBean.CWHM);
            //            vHolder.time.setText(getChild(groupPosition, childPosition).BCBH);

            return convertView;
        }


        @Override
        public int getChildrenCount(int groupPosition) {
            return bcszBeanList.get(groupPosition).bccwBeans.size();
        }


        @Override
        public Object getGroup(int groupPosition) {
            return bcszBeanList.get(groupPosition);
        }


        @Override
        public int getGroupCount() {
            return bcszBeanList.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }


        @Override
        public View getGroupView(final int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            GroupHolder vHolder;
            if (convertView == null) {
                convertView = LayoutInflater
                        .from(mContext).inflate(
                                R.layout.layout_item_bar_check, parent, false);
                vHolder = new GroupHolder();
                vHolder.tv_name = (TextView) convertView
                        .findViewById(R.id.id_tv_for_bar_check);
                vHolder.tv_desc = (TextView) convertView
                        .findViewById(R.id.healthguid_datetime_txt);
                vHolder.checkBox = (android.widget.CheckBox) convertView
                        .findViewById(R.id.healthguid_cbpre);
                convertView.setTag(vHolder);
            } else {
                vHolder = (GroupHolder) convertView.getTag();
            }
            BCSZBean bcszBean = bcszBeanList.get(groupPosition);

            vHolder.tv_name.setText(bcszBean.BCMC);
            //            vHolder.tv_desc.setText(bcszBean.BCBH);
            vHolder.checkBox.setChecked(bcszBean.selected);
            vHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView == null || !buttonView.isPressed()) {
                        //不响应非点击引起的改变
                        return;
                    }
                    //赋值
                    bcszBean.selected = isChecked;
                }
            });

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }


        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }

    }

    class GroupHolder {
        TextView tv_name;
        TextView tv_desc;
        android.widget.CheckBox checkBox;
    }

    class ChildHolder {
        TextView name;
        TextView time;
    }
}
