package com.bsoft.mob.ienr.util;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.api.UserApi;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.LoginResponse;
import com.bsoft.mob.ienr.model.LoginUser;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.TimeVo;
import com.bsoft.mob.ienr.model.kernel.AreaVo;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.AlertBox;
import com.bsoft.mob.ienr.view.IenrProgressDialog;

import java.util.List;
import java.util.Vector;

/**
 * Description:
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2016-10-14
 * Time: 09:25
 * Version:
 */

public class AgainLoginUtil {

    private Context mContext;
    View view;
    AlertDialog dialog;


    IenrProgressDialog progressDialog;

    public interface LoginSucessListener {
        public void LoginSucessEvent();
    }

    private LoginSucessListener loginSucessListener;
    AppApplication application;

    public AgainLoginUtil(Context context, AppApplication appApplication, LoginSucessListener loginSucessListener) {

        mContext = context;
        application = appApplication;
        this.loginSucessListener = loginSucessListener;
        initView();
    }

    public AgainLoginUtil(Context context, AppApplication appApplication) {

        mContext = context;
        application = appApplication;
        initView();
    }

    private void initView() {

        view = LayoutInflater.from(mContext).inflate(R.layout.fragment_login, null);
        final AutoCompleteTextView username = (AutoCompleteTextView) view.findViewById(R.id.tvUname);
        if (application.user != null) {
            username.setText(application.user.YHDM);
        }
        final EditText userpwd = (EditText) view.findViewById(R.id.etPwd);
        Button btnAgainLogin = (Button) view.findViewById(R.id.btnLogin);
        final ImageView clearUserName = (ImageView) view.findViewById(R.id.login_clear_usr_img);
        final ImageView clearUserPwd = (ImageView) view.findViewById(R.id.login_clear_pwd_img);

        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    clearUserPwd.setVisibility(View.INVISIBLE);

                    Editable edit = ((EditText) v).getText();
                    if (EmptyTool.isBlank(edit.toString())) {
                        clearUserName.setVisibility(View.INVISIBLE);
                    } else {
                        clearUserName.setVisibility(View.VISIBLE);
                    }
                } else {
                    clearUserName.setVisibility(View.INVISIBLE);
                }

            }
        });
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!EmptyTool.isBlank(s.toString())) {
                    clearUserName.setVisibility(View.VISIBLE);
                } else {
                    clearUserName.setVisibility(View.INVISIBLE);
                }

            }
        });
        userpwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    clearUserName.setVisibility(View.INVISIBLE);

                    Editable edit = ((EditText) v).getText();
                    if (EmptyTool.isBlank(edit)) {
                        clearUserPwd.setVisibility(View.INVISIBLE);
                    } else {
                        clearUserPwd.setVisibility(View.VISIBLE);
                    }
                } else {
                    clearUserPwd.setVisibility(View.INVISIBLE);
                }

            }
        });
        userpwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (!EmptyTool.isBlank(s.toString())) {
                    clearUserPwd.setVisibility(View.VISIBLE);
                } else {
                    clearUserPwd.setVisibility(View.INVISIBLE);
                }

            }
        });
        btnAgainLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行异步请求
                LoginAsyncTask task = new LoginAsyncTask(LoginAsyncTask.LOGIN_WITH_PWD);
                task.execute(username.getText().toString(), userpwd.getText().toString(), application.jgId);
            }
        });
        clearUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username.setText(null);
            }
        });
        clearUserPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userpwd.setText(null);
            }
        });

    }

    public void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext, "用户登录"));
        builder.setView(view);
        dialog = builder.show();
    }

    /**
     * 异步登录.
     */
    public class LoginAsyncTask extends AsyncTask<String, Void, Response<LoginResponse>> {

        public static final byte LOGIN_WITH_PWD = 0;

        private byte mType = LOGIN_WITH_PWD;

        public LoginAsyncTask(byte type) {
            mType = type;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog == null) {
                progressDialog = new IenrProgressDialog(mContext,
                        "登录中...");
            }
            progressDialog.start();
        }

        @Override
        protected Response<LoginResponse> doInBackground(String... params) {
            if (params == null || params.length < 1) {
                return null;
            }

            UserApi api = UserApi.getInstance(mContext);
            switch (mType) {
                case LOGIN_WITH_PWD:
                    if (params.length < 3) {
                        return null;
                    }
                    String usrname = params[0];
                    String password = params[1];
                    String jgid = params[2];
                    Response model = api.login(usrname, password, jgid);
                    return model;
                default:
                    return null;
            }
        }

        @Override
        protected void onPostExecute(Response<LoginResponse> result) {
            super.onPostExecute(result);
            if (progressDialog != null) {
                progressDialog.stop();
                progressDialog = null;
            }
            if (null != result) {
                if (result.ReType == 0) {
                    LoginUser user = result.Data.LonginUser;
                    application.user = user;
                    List<AreaVo> aList = result.Data.Areas;
                    Vector<AreaVo> vector = new Vector<>(aList);
                    application.setAreaList(vector);
                    application.JSESSIONID = result.Data.SessionId;
                    TimeVo timeVo = result.Data.TimeVo;
                    String serverDateTime = timeVo.Time;
                    if (!TextUtils.isEmpty(serverDateTime)) {
                        DateTimeHelper.initServerDateTime(serverDateTime);
                        application.updateSystemDateTime(serverDateTime);
                    }
                    application.userConfig = result.Data.userConfig;
                    //
                    if (dialog != null) {
                        dialog.dismiss();
                        dialog.cancel();
                        dialog = null;
                    }
                    if (loginSucessListener != null) {

                        loginSucessListener.LoginSucessEvent();
                    }

                } else {
                    if (mContext != null) {
                        AlertBox.Show(mContext, mContext.getString(R.string.project_tips), "登录失败！", mContext.getString(R.string.project_operate_ok));
                    }
                }
            } else {
                if (mContext != null) {
                    AlertBox.Show(mContext, mContext.getString(R.string.project_tips), "登录失败！", mContext.getString(R.string.project_operate_ok));
                }
            }

        }
    }
}
