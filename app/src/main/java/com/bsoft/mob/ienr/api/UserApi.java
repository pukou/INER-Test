package com.bsoft.mob.ienr.api;

import android.content.Context;
import android.util.Log;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.LoginResponse;
import com.bsoft.mob.ienr.model.LoginUser;
import com.bsoft.mob.ienr.model.PDAInfo;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.kernel.Agency;
import com.bsoft.mob.ienr.model.kernel.AreaVo;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Description:
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2016-11-22
 * Time: 09:28
 * Version:
 */

public class UserApi extends BaseApi {

    public String url;

    public UserApi(AppHttpClient httpClient, Context mContext) {
        super(httpClient, mContext);
    }

    public UserApi(AppHttpClient httpClient, Context mContext, String url) {
        super(httpClient, mContext);
        this.url = url;
    }

    @Override
    public AppHttpClient getHttpClient() {
        return httpClient;
    }

    public static UserApi getInstance(Context localContext) {
        UserApi api = (UserApi) localContext
                .getSystemService("com.bsoft.mob.ienr.api.UserApi");
        if (api == null)
            api = (UserApi) localContext.getApplicationContext()
                    .getSystemService("com.bsoft.mob.ienr.api.UserApi");
        if (api == null)
            throw new IllegalStateException("api not available");
        return api;
    }

    //获取PDAInfo信息
    public Response<PDAInfo> GetPDAInfo(String manuer, String model){
        Response<PDAInfo> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";

        String uri = new StringBuffer(url.replace("auth/", "")).append("get/pdainfo?manuer=")
                .append(manuer).append("&model=").append(model).toString();

        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
        if (!isRequestSuccess(httpString.first)) {
            response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
            return response;
        }
        String entity = httpString.second;
        //外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<PDAInfo>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 获取机构列表
     *
     * @param urid
     * @return
     */
    public Response<ArrayList<Agency>> GetAgency(String urid) {

        Response<ArrayList<Agency>> response = new Response<ArrayList<Agency>>();

        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url.replace("auth/", "")).append("get/agencys?urid=")
                .append(urid).toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
        if (!isRequestSuccess(httpString.first)) {
            response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
            return response;
        }
        String entity = httpString.second;
        //外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<ArrayList<Agency>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 登录
     *
     * @param urid
     * @param pwd
     * @return
     */
    public Response<LoginResponse> login(String urid, String pwd, String jgid) {
        Response<LoginResponse> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url.replace("auth/", "")).append("login?")
                .append("urid=").append(urid).append("&pwd=").append(pwd)
                .append("&jgid=").append(jgid).toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
        if (!isRequestSuccess(httpString.first)) {
            response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
            return response;
        }
        String entity = httpString.second;
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<LoginResponse>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 扫描登录
     *
     * @param barcode
     * @return
     */
    public Response<LoginResponse> SannerLogin(String barcode, String jgid) {

        Response<LoginResponse> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url.replace("auth/", "")).append("login/scan?")
                .append("guid=").append(barcode).append("&jgid=").append(jgid).toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
        if (!isRequestSuccess(httpString.first)) {
            response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
            return response;
        }
        String entity = httpString.second;
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<LoginResponse>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 根据胸卡条码内容获取用户信息
     *
     * @param barcode
     * @return
     */
    public Response<LoginUser> getLoginUserByXk(String barcode, String jgid) {

        Response<LoginUser> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url.replace("auth/", "")).append("get/login/user/by/xk?")
                .append("guid=").append(barcode).append("&jgid=").append(jgid).toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
        if (!isRequestSuccess(httpString.first)) {
            response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
            return response;
        }
        String entity = httpString.second;
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<LoginUser>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 登录
     *
     * @param urid
     * @param jgid
     * @return
     */
    public Response<String> logout(String urid, String jgid) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url.replace("auth/", "")).append("logout?")
                .append("urid=").append(urid)
                .append("&jgid=").append(jgid).toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
        if (!isRequestSuccess(httpString.first)) {
            response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
            return response;
        }
        String entity = httpString.second;
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<String>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    public Response<Object> GetBarcodeInfo(String barcode, String jgid) {

        Response<Object> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String entity = null;
        String uri = new StringBuffer(url.replace("auth/", ""))
                .append("getBarcodeInfo?barcode=").append(barcode)
                .append("&jgid=").append(jgid).toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
        if (!isRequestSuccess(httpString.first)) {
            response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
            return response;
        }
         entity = httpString.second;
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<BarcodeEntity>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    public Response<String> GetBarcodeSettings(String jgid) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";

        String entity = null;
        if (Constant.DEBUG_LOCAL) {
            entity = getLocalXml("GetBarcodeSettings.xml");
        } else {
            String uri = new StringBuffer(url)
                    .append("getBarcodeSetting?jgid=").append(jgid).toString();
            if (Constant.LOG_URI) {
                Log.d(Constant.TAG, uri);
            }
            HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
            if (!isRequestSuccess(httpString.first)) {
                response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
                return response;
            }
             entity = httpString.second;
        }
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<String>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    public Response<List<AreaVo>> getAreaVoForSurgery(String jgid) {
        Response<List<AreaVo>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";

        String uri = new StringBuffer(url)
                .append("getAreaVoForSurgery?jgid=").append(jgid).toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
        if (!isRequestSuccess(httpString.first)) {
            response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
            return response;
        }
        String entity = httpString.second;
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<List<AreaVo>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }


}
