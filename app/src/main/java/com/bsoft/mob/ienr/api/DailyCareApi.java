package com.bsoft.mob.ienr.api;

import android.content.Context;
import android.util.Log;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.daily.DailySecondItem;
import com.bsoft.mob.ienr.model.daily.DailyTopItem;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

/**
 * Created by king on 2016/11/22.
 */
public class DailyCareApi extends BaseApi {

    public String url;

    public DailyCareApi(AppHttpClient httpClient, Context mContext) {
        super(httpClient, mContext);
    }
    public DailyCareApi(AppHttpClient httpClient, Context mContext, String url) {
        super(httpClient, mContext);
        this.url = url;
    }

    @Override
    public AppHttpClient getHttpClient() {
        return httpClient;
    }

    public static DailyCareApi getInstance(Context localContext) {
        DailyCareApi api = (DailyCareApi) localContext
                .getSystemService("com.bsoft.mob.ienr.api.DailyCareApi");
        if (api == null)
            api = (DailyCareApi) localContext.getApplicationContext()
                    .getSystemService("com.bsoft.mob.ienr.api.DailyCareApi");
        if (api == null)
            throw new IllegalStateException("api not available");
        return api;
    }

    /**
     * 获取护理常规一级列表
     *
     * @param ksdm
     * @param jgid
     * @param sysType
     * @return
     */
    public Response<List<DailyTopItem>> GetDailyNurseType(String ksdm, String jgid, int sysType) {
        Response<List<DailyTopItem>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri =  new StringBuffer(url)
                .append("get/GetDailyNurseType?ksdm=").append(ksdm)
                .append("&jgid=").append(jgid).append("&sysType=")
                .append(sysType).toString();
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
                        new TypeReference<Response<List<DailyTopItem>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;

    }

    /**
     * 获取护理常规二级列表
     *
     * @param type
     * @param jgid
     * @param sysType
     * @return
     */
    public Response<List<DailySecondItem>> GetDailyNurseList(String type, String jgid, int sysType) {
        Response<List<DailySecondItem>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url)
                .append("get/GetDailyNurseList?type=").append(type)
                .append("&jgid=").append(jgid).append("&sysType=")
                .append(sysType).toString();
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
                    new TypeReference<Response<List<DailySecondItem>>>() {
                    });
        } catch (Exception e) {
            Log.e(Constant.TAG, e.getMessage(), e);
            response.Msg = "请求失败：解析错误";
        }
    }
    return response;
}

    public Response<String> SaveDailyNurseItems(String brbq, String zyh,
                                                String listXMBS, String urid, String jgid, int sysType) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";

        try {
            listXMBS = URLEncoder.encode(listXMBS, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String uri = new StringBuffer(url)
                .append("post/SaveDailyNurseItems?brbq=").append(brbq)
                .append("&zyh=").append(zyh).append("&listXMBS=")
                .append(listXMBS).append("&urid=").append(urid)
                .append("&jgid=").append(jgid).append("&sysType=")
                .append(sysType).toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
        if (!isRequestSuccess(httpString.first)) {
            response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
            return response;
        }
        String entity = httpString.second;

        if (null != entity && entity.length() > 0) {
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
}
