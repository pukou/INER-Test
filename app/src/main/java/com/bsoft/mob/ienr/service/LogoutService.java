package com.bsoft.mob.ienr.service;

import android.app.IntentService;
import android.content.Intent;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.api.UserApi;
import com.bsoft.mob.ienr.components.mqtt.MQTTTool;

public class LogoutService extends IntentService {

    public LogoutService() {
        super(LogoutService.class.getName());
    }

    public LogoutService(String name) {
        super(LogoutService.class.getName());
    }

    AppApplication mAppApplication;
    @Override
    protected void onHandleIntent(Intent intent) {

        stopMqtt();
        logout();
        clearCache();

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        mAppApplication = (AppApplication) getApplication();
        /*stopMqtt();
        logout();
        clearCache();*/
    }

    /**
     * 清除缓存
     */
    private void clearCache() {

        mAppApplication.setAreaId(null);
        mAppApplication.user = null;
        mAppApplication.sickPersonVo = null;
        mAppApplication.jgId = null;
    }

    private void stopMqtt() {
        MQTTTool.getInstance(getApplicationContext()).stopMQTT();
    }

    private void logout() {


        if (mAppApplication.user != null) {
            String urid = mAppApplication.user.YHID;
//			String rid = String.valueOf(mAppApplication.user.MRBZ);
            String jgid = mAppApplication.jgId;

            UserApi api = UserApi.getInstance(getApplicationContext());
            api.logout(urid, jgid);
        }

    }
}
