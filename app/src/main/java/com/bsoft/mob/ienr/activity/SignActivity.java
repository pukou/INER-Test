package com.bsoft.mob.ienr.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.adapter.AgencyAdapter;
import com.bsoft.mob.ienr.activity.adapter.AutoCompleteAdapter;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.api.UserApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.db.Database;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.LoginResponse;
import com.bsoft.mob.ienr.model.LoginUser;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.kernel.Agency;
import com.bsoft.mob.ienr.util.DisplayUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;

import java.util.ArrayList;

/**
 * 签名页,支持单签和双签
 *
 * @author hy
 */
public class SignActivity extends BaseBarcodeActivity implements
        OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DIALOG_CHOICE_AGENT_MENU = 0;

    public static final String EXTRA_STRING_KEY = "string_key";

    public static final String EXTRA_YHID_KEY_1 = "yhid_key_1";

    public static final String EXTRA_YHID_KEY_2 = "yhid_key_2";

    private String extra_value = null;

    /**
     * action for start SignActivity,the value is
     * {@link #ACTION_EXTRA_SIGN_SIGNLE} or {@link #ACTION_EXTRA_SING_BOUSE}
     */
    public static final String ACTION_SIGN = "com.bsoft.mob.ienr.activity.SignActivity";

    public static final int ACTION_EXTRA_SIGN_SIGNLE = 0;

    public static final int ACTION_EXTRA_SING_BOUSE = 1;

    private int actionType;

    /**
     * 用户名
     */
    AutoCompleteTextView tvUname;
    /**
     * 密码
     */
    EditText etPwd;

    /**
     * 第二个用户名
     */
    AutoCompleteTextView tvUname2;
    /**
     * 第二个用户密码
     */
    EditText etPwd2;
    /**
     * 登录按钮
     */
    // Button btnLogin;

    // Button cancelBtn;


    ImageView clearUsrImg;
    ImageView clearPwdImg;

    ImageView clearUsrImg2;
    ImageView clearPwdImg2;

    AutoCompleteAdapter adapter;

    /******** 双签 时，缓存数据 *******/
    /**
     * 当前已登录用户数
     */
    private int mLoginTimes = 0;

    /**
     * 第二个登录用户信息
     */
    private LoginUser user;


    private void setSecondVisiable() {

        View secondView = findViewById(R.id.sign_second_ll);
        if (actionType == ACTION_EXTRA_SING_BOUSE) {
            secondView.setVisibility(View.VISIBLE);
        } else {
            secondView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        setExtra(intent);
    }

    private void setExtra(Intent intent) {

        if (intent != null) {
            extra_value = intent.getStringExtra(EXTRA_STRING_KEY);
            actionType = intent.getIntExtra(ACTION_SIGN,
                    ACTION_EXTRA_SIGN_SIGNLE);
        }
        setSecondVisiable();
        mLoginTimes = 0;
        user = null;
    }

    /**
     * 设置长宽显示参数
     */
    private void setLayoutParams() {

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.height = LayoutParams.WRAP_CONTENT;
        params.width = DisplayUtil.getWidthPixels(this) - 20;
        this.getWindow().setAttributes(params);
    }

    /**
     * 解决点击框外消失问题
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Rect dialogBounds = new Rect();
        getWindow().getDecorView().getHitRect(dialogBounds);

        if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
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

    private void toastConfirm(String tip) {
        showSnack(tip);
        //Crouton.showText(this, tip, Style.CONFIRM);
    }

    /**
     * 条码扫描登录
     *
     * @param bar 条码
     */
    public void loginWithBarcode(String bar) {

        if (EmptyTool.isBlank(bar)) {
            showMsgAndVoiceAndVibrator("条码为空");
            return;
        }

        LoginAsyncTask task = new LoginAsyncTask(
                LoginAsyncTask.LOGIN_WITH_BARCODE);
        task.execute(bar);
        tasks.add(task);
    }

    /**
     * 异步登录.
     */
    public class LoginAsyncTask extends AsyncTask<String, String, Response<LoginResponse>> {

        public static final byte LOGIN_WITH_PWD = 0;

        public static final byte LOGIN_WITH_BARCODE = 1;

        private byte mType = LOGIN_WITH_PWD;

        public LoginAsyncTask(byte type) {
            mType = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(R.string.signing);
        }

        @Override
        protected Response<LoginResponse> doInBackground(String... params) {

            if (params == null || params.length < 1) {
                return null;
            }

            UserApi api = UserApi.getInstance(getApplicationContext());
            switch (mType) {
                case LOGIN_WITH_BARCODE:
                    String barcode = params[0];
                    String[] arr = barcode.split("_");
                    String jgid = "1";
                    if (arr.length == 2) {
                        jgid = barcode.split("_")[1];
                    } else {
                        jgid = EmptyTool.isBlank(mAppApplication.jgId) ? "1" : mAppApplication.jgId;
                    }
                    // 条码登录
                    return api.SannerLogin(barcode, jgid);

                case LOGIN_WITH_PWD:
                    if (params.length < 3) {
                        return null;
                    }
                    String usrname = params[0];
                    String password = params[1];
                    jgid = params[2];
                    Response<LoginResponse> model = api.login(usrname, password, jgid);
//                    if (model != null && model.isOK()) {
//                        publishProgress(jgid);
//                    }
                    return model;
                default:
                    return null;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {

            if (values != null && values.length > 0) {
                mAppApplication.jgId = values[0];
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Response<LoginResponse> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (result == null) {
                showMsgAndVoiceAndVibrator("认证失败！");
            }

            LoginUser loginUser = null;

            if (result == null || result.ReType != 0) {
                showMsgAndVoice(result.Msg);
                return;
            } else {
                loginUser = result.Data.LonginUser;
            }

            if (actionType == ACTION_EXTRA_SING_BOUSE && mLoginTimes == 0) {

                user = loginUser;
                mLoginTimes++;

                String username = tvUname2.getText().toString();
                if (!EmptyTool.isBlank(username)) {
                    actionGetAgent(username);
                    return;
                }
                toastConfirm("请继续输入或扫描第二个用户信息，进行双签名");
                return;
            }

            if (loginUser == null) {
                showMsgAndVoiceAndVibrator("签名失败！");
                return;
            }

            if (actionType == ACTION_EXTRA_SING_BOUSE && user == null) {
                showMsgAndVoiceAndVibrator("签名失败！");
                return;
            }

            if (actionType == ACTION_EXTRA_SING_BOUSE
                    && user.YHID == loginUser.YHID) {
                showMsgAndVoiceAndVibrator("签名用户相同，请重新输入或扫描第二个用户信息");
                return;
            }

            Intent intent = new Intent();

            if (user != null) {// 双签
                intent.putExtra(EXTRA_YHID_KEY_1, user.YHID);
                intent.putExtra(EXTRA_YHID_KEY_2, loginUser.YHID);
            } else {// 单签
                intent.putExtra(EXTRA_YHID_KEY_1, loginUser.YHID);
            }

            intent.putExtra(EXTRA_STRING_KEY, extra_value);
            setResult(RESULT_OK, intent);
            finish();

        }
    }


    void findView() {


        initActionBar();

        tvUname = (AutoCompleteTextView) findViewById(R.id.tvUname);
        etPwd = (EditText) findViewById(R.id.etPwd);

        tvUname2 = (AutoCompleteTextView) findViewById(R.id.tvUname2);
        etPwd2 = (EditText) findViewById(R.id.etPwd2);

        findViewById(R.id.sign_second_ll).setVisibility(View.VISIBLE);
        findViewById(R.id.btnLogin).setVisibility(View.GONE);

        initAutoTxt();

        clearUsrImg = (ImageView) findViewById(R.id.login_clear_usr_img);
        clearPwdImg = (ImageView) findViewById(R.id.login_clear_pwd_img);
        clearUsrImg.setOnClickListener(this);
        clearPwdImg.setOnClickListener(this);

        clearUsrImg2 = (ImageView) findViewById(R.id.login_clear_usr_img2);
        clearPwdImg2 = (ImageView) findViewById(R.id.login_clear_pwd_img2);
        clearUsrImg2.setOnClickListener(this);
        clearPwdImg2.setOnClickListener(this);

        tvUname.setOnFocusChangeListener(new UsrFocusChangeListener());
        etPwd.setOnFocusChangeListener(new PwdFocusChangeListener());
        tvUname.addTextChangedListener(new UsrTextWatcher());
        etPwd.addTextChangedListener(new PwdTextWatcher());

        tvUname2.setOnFocusChangeListener(new Usr2FocusChangeListener());
        etPwd2.setOnFocusChangeListener(new Pwd2FocusChangeListener());
        tvUname2.addTextChangedListener(new Usr2TextWatcher());
        etPwd2.addTextChangedListener(new Pwd2TextWatcher());

    }

    private void initActionBar() {

        actionBar.setTitle("用户验证");
        actionBar.addAction(new Action() {
            @Override
            public String getText() {
                return "保存";
            }
            @Override
            public void performAction(View view) {
                onSaveBtnClick();
            }

            @Override
            public int getDrawable() {

                return R.drawable.ic_done_black_24dp;

            }
        });
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_sign;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        findView();

        setLayoutParams();
        setExtra(getIntent());

    }

    /**
     * 前置条件：UI控件已初始化
     */
    private void onSaveBtnClick() {

        String username = tvUname.getText().toString();
        String username2 = tvUname2.getText().toString();

        if (EmptyTool.isBlank(username)) {
            tvUname.setError("用户名不能为空");
            tvUname.requestFocus();
            return;
        }

        if (EmptyTool.isBlank(username2)) {
            tvUname2.setError("用户名不能为空");
            tvUname2.requestFocus();
            return;
        }

        // 双签
        if (actionType == ACTION_EXTRA_SING_BOUSE) {

            if (username.equals(username2)) {
                tvUname2.setError("请输入不同用户账号");
                tvUname2.requestFocus();
                return;
            }
        }
        // 获取机构列表
        actionGetAgent(username);

    }

    private void actionGetAgent(String username) {

        if (EmptyTool.isBlank(mAppApplication.jgId)) {
            // 获取机构列表
            GetAgencyTask task = new GetAgencyTask();
            task.execute(username);
            tasks.add(task);
        } else {
            remoteLogin(mAppApplication.jgId, mLoginTimes);
        }

    }

    public class GetAgencyTask extends AsyncTask<String, Integer, Response<ArrayList<Agency>>> {

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response doInBackground(String... params) {

            if (params == null || params.length < 1) {
                return null;
            }
            UserApi api = UserApi.getInstance(getApplicationContext());
            return api.GetAgency(params[0]);

        }

        @Override
        protected void onPostExecute(Response<ArrayList<Agency>> result) {

            hideSwipeRefreshLayout();

            if (tasks.contains(this)) {
                tasks.remove(this);
            }

            if (null == result) {
                showMsgAndVoiceAndVibrator("验证失败");
                return;
            }

            if (null != result && result.ReType == 0) {
                ArrayList<Agency> list = result.Data;
                boolean ok = analyzeAgencyList(list);
                if (!ok) {
                    showMsgAndVoiceAndVibrator("验证失败");
                }
            } else {
                showMsgAndVoiceAndVibrator("验证失败");
                return;
            }

        }

        /**
         * 解析机构列表
         *
         * @param list
         * @return
         */
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
                remoteLogin(jgid, mLoginTimes);
            }
            return true;
        }
    }

    /**
     * @param jgid
     * @param which 0 读取第一个用户，1读取第二个用户
     */
    public void remoteLogin(String jgid, int which) {

        String username = tvUname.getText().toString();
        String password = etPwd.getText().toString();
        if (which != 0) {
            username = tvUname2.getText().toString();
            password = etPwd2.getText().toString();
        }
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

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Agency agency = list.get(which);
                        if (agency != null) {
                            remoteLogin(agency.JGID, mLoginTimes);
                        }
                    }

                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                StringBuilder sb = new StringBuilder(mLoginTimes == 0 ? "首个用户"
                        : "第二个用户");
                sb.append(getResources().getString(R.string.login_select_agency));
                builder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext, sb.toString()))
                        .setSingleChoiceItems(adapter, -1, onClickListener)
                        .setNegativeButton("取消验证",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                    }
                                });

                builder.setCancelable(false);

                builder.create().show();
                break;
            default:

        }
    }

    private void initAutoTxt() {

        if (tvUname != null) {
            tvUname.setThreshold(1);
            new NameTask(NameTask.GET_NAME).execute();
            // 注意此处是getSupportLoaderManager()，而不是getLoaderManager()方法。
            getSupportLoaderManager().initLoader(0, null, this);
        }
        if (tvUname2 != null) {
            tvUname2.setThreshold(1);
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
            }
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            switch (mType) {
                case GET_NAME:
                    tvUname.setText(s);
                default:
                    break;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        etPwd.setText(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

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
            tvUname2.setAdapter(adapter);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (adapter != null) {
            adapter.changeCursor(null);
        }
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.login_clear_usr_img) {
            tvUname.setText(null);

        } else if (id == R.id.login_clear_pwd_img) {
            etPwd.setText(null);
        } else if (id == R.id.login_clear_usr_img2) {
            tvUname2.setText(null);

        } else if (id == R.id.login_clear_pwd_img2) {
            etPwd2.setText(null);
        }
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
                clearPwdImg.setVisibility(View.INVISIBLE);
                clearUsrImg2.setVisibility(View.INVISIBLE);
                clearPwdImg2.setVisibility(View.INVISIBLE);
                Editable edit = ((EditText) v).getText();
                if (EmptyTool.isBlank(edit)) {
                    clearUsrImg.setVisibility(View.INVISIBLE);
                } else {
                    clearUsrImg.setVisibility(View.VISIBLE);
                }
            } else {
                clearUsrImg.setVisibility(View.INVISIBLE);
            }
        }

    }

    /**
     * 监听用户名EditView输入变化
     *
     * @author hy
     */
    protected class Usr2FocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (hasFocus) {
                clearPwdImg2.setVisibility(View.INVISIBLE);
                clearUsrImg2.setVisibility(View.INVISIBLE);
                clearPwdImg.setVisibility(View.INVISIBLE);
                Editable edit = ((EditText) v).getText();
                if (EmptyTool.isBlank(edit)) {
                    clearUsrImg2.setVisibility(View.INVISIBLE);
                } else {
                    clearUsrImg2.setVisibility(View.VISIBLE);
                }
            } else {
                clearUsrImg2.setVisibility(View.INVISIBLE);
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
                clearUsrImg.setVisibility(View.INVISIBLE);
                clearUsrImg2.setVisibility(View.INVISIBLE);
                clearPwdImg2.setVisibility(View.INVISIBLE);
                Editable edit = ((EditText) v).getText();
                if (EmptyTool.isBlank(edit)) {
                    clearPwdImg.setVisibility(View.INVISIBLE);
                } else {
                    clearPwdImg.setVisibility(View.VISIBLE);
                }
            } else {
                clearPwdImg.setVisibility(View.INVISIBLE);
            }
        }

    }

    /**
     * 监听密码 EditView输入变化
     *
     * @author hy
     */
    protected class Pwd2FocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (hasFocus) {
                clearUsrImg2.setVisibility(View.INVISIBLE);
                clearUsrImg.setVisibility(View.INVISIBLE);
                clearPwdImg.setVisibility(View.INVISIBLE);
                Editable edit = ((EditText) v).getText();
                if (EmptyTool.isBlank(edit)) {
                    clearPwdImg2.setVisibility(View.INVISIBLE);
                } else {
                    clearPwdImg2.setVisibility(View.VISIBLE);
                }
            } else {
                clearPwdImg2.setVisibility(View.INVISIBLE);
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

            if (!EmptyTool.isBlank(s.toString())) {
                clearUsrImg.setVisibility(View.VISIBLE);
            } else {
                clearUsrImg.setVisibility(View.INVISIBLE);
            }
        }

    }

    /**
     * 监听用户名EditView字数变化
     *
     * @author hy
     */
    protected class Usr2TextWatcher implements TextWatcher {

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

            if (!EmptyTool.isBlank(s.toString())) {
                clearUsrImg2.setVisibility(View.VISIBLE);
            } else {
                clearUsrImg2.setVisibility(View.INVISIBLE);
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

            if (!EmptyTool.isBlank(s.toString())) {
                clearPwdImg.setVisibility(View.VISIBLE);
            } else {
                clearPwdImg.setVisibility(View.INVISIBLE);
            }

        }

    }

    /**
     * 监听密码EditView字数变化
     *
     * @author hy
     */
    protected class Pwd2TextWatcher implements TextWatcher {

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

            if (!EmptyTool.isBlank(s.toString())) {
                clearPwdImg2.setVisibility(View.VISIBLE);
            } else {
                clearPwdImg2.setVisibility(View.INVISIBLE);
            }

        }

    }

}
