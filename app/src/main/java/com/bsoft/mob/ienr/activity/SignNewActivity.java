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
import android.text.TextUtils;
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
public class SignNewActivity extends BaseBarcodeActivity implements
        OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DIALOG_CHOICE_AGENT_MENU = 0;

    public static final String MY_EXTRA_YHID_KEY = "MY_EXTRA_YHID_KEY";
    public static final String MY_EXTRA_YHXM_KEY = "MY_EXTRA_YHXM_KEY";

    AutoCompleteTextView tvUname;
    EditText etPwd;
    ImageView clearUsrImg;
    ImageView clearPwdImg;
    AutoCompleteAdapter adapter;





    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setExtra(intent);
    }

    private void setExtra(Intent intent) {
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
                return;
            }

            LoginUser loginUser_NowSign = null;

            if (result.ReType != 0) {
                showMsgAndVoice(result.Msg);
                return;
            } else {
                loginUser_NowSign = result.Data.LonginUser;
            }

            if (loginUser_NowSign == null) {
                showMsgAndVoiceAndVibrator("签名失败！");
                return;
            }
            Intent intent = new Intent();
            intent.putExtra(MY_EXTRA_YHID_KEY, loginUser_NowSign.YHID);
            intent.putExtra(MY_EXTRA_YHXM_KEY, loginUser_NowSign.YHXM);
            //////
            setResult(RESULT_OK, intent);
            finish();

        }
    }


    void findView() {


        initActionBar();

        tvUname = (AutoCompleteTextView) findViewById(R.id.tvUname);
        etPwd = (EditText) findViewById(R.id.etPwd);
        findViewById(R.id.btnLogin).setVisibility(View.GONE);

        initAutoTxt();

        clearUsrImg = (ImageView) findViewById(R.id.login_clear_usr_img);
        clearPwdImg = (ImageView) findViewById(R.id.login_clear_pwd_img);
        clearUsrImg.setOnClickListener(this);
        clearPwdImg.setOnClickListener(this);

        tvUname.setOnFocusChangeListener(new UsrFocusChangeListener());
        etPwd.setOnFocusChangeListener(new PwdFocusChangeListener());
        tvUname.addTextChangedListener(new UsrTextWatcher());
        etPwd.addTextChangedListener(new PwdTextWatcher());

    }

    private void initActionBar() {

        actionBar.setTitle("用户验证");
        actionBar.addAction(new Action() {

            @Override
            public void performAction(View view) {
                onSaveBtnClick();
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
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_sign_new;
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

        if (EmptyTool.isBlank(username)) {
            tvUname.setError("用户名不能为空");
            tvUname.requestFocus();
            return;
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
            remoteLogin(mAppApplication.jgId);
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
                showCreateDialogCompat(DIALOG_CHOICE_AGENT_MENU, args);

            } else { // 单独一条， 远程登录

                String jgid = list.get(0).JGID;
                remoteLogin(jgid);
            }
            return true;
        }
    }

    /**
     * @param jgid
     */
    public void remoteLogin(String jgid) {
        String username = tvUname.getText().toString();
        String password = etPwd.getText().toString();
        LoginAsyncTask asyncTask = new LoginAsyncTask(
                LoginAsyncTask.LOGIN_WITH_PWD);
        asyncTask.execute(username, password, jgid);
        tasks.add(asyncTask);
    }



    protected void showCreateDialogCompat(int id, Bundle args) {

        switch (id) {

            case DIALOG_CHOICE_AGENT_MENU:

                final ArrayList<Agency> list = args.getParcelableArrayList("list");

                AgencyAdapter adapter = new AgencyAdapter(this, list);

                DialogInterface.OnClickListener onClickListener = new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Agency agency = list.get(which);
                        if (agency != null) {
                            remoteLogin(agency.JGID);
                        }
                    }

                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                StringBuilder sb = new StringBuilder("用户");
                sb.append(getResources().getString(R.string.login_select_agency));
                builder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext,sb.toString()))
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
     * 监听密码 EditView输入变化
     *
     * @author hy
     */
    protected class PwdFocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {

            if (hasFocus) {
                clearUsrImg.setVisibility(View.INVISIBLE);
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
}
