package com.bsoft.mob.ienr.api;


import android.content.Context;
import android.util.Log;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.catheter.CatheterRespose;
import com.bsoft.mob.ienr.model.kernel.SickPersonVo;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-25 上午11:16:54
 * @类说明
 */
public class CatheterApi extends BaseApi {

    public String url;

    public CatheterApi(AppHttpClient httpClient, Context mContext) {
        super(httpClient, mContext);
    }

    public CatheterApi(AppHttpClient httpClient, Context mContext, String url) {
        super(httpClient, mContext);
        this.url = url;
    }

    public static CatheterApi getInstance(Context localContext) {
        CatheterApi api = (CatheterApi) localContext
                .getSystemService("com.bsoft.mob.ienr.api.catheter");
        if (api == null) {
            api = (CatheterApi) localContext.getApplicationContext()
                    .getSystemService("com.bsoft.mob.ienr.api.catheter");
        }
        if (api == null) {
            throw new IllegalStateException("api not available");
        }
        return api;
    }

    /**
     * 获取病人列表
     *
     * @param brbq
     * @param jgid
     * @param sysType
     * @return
     */
    public Response<List<SickPersonVo>> GetpationtList(String brbq, String jgid, int sysType) {
        Response<List<SickPersonVo>> response = new Response<List<SickPersonVo>>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("getpationtList?brbq=")
                .append(brbq).append("&jgid=").append(jgid)
                .append("&sysType=").append(sysType).toString();
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
                        new TypeReference<Response<List<SickPersonVo>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 获取病人信息数据
     *
     * @param brbq
     * @param jgid
     * @param sysType
     * @return
     */
    public Response<CatheterRespose> getCatheter(String brbq, String jgid, String zyh, int sysType) {
        Response<CatheterRespose> response = new Response<CatheterRespose>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("getCatheter?brbq=")
                .append(brbq).append("&jgid=").append(jgid).append("&zyh=").append(zyh)
                .append("&sysType=").append(sysType).toString();
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
                        new TypeReference<Response<CatheterRespose>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 保存数据
     *
     * @return
     */
    public Response<String> SaveCatheter(String data) {
        Response<String> response = new Response<String>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("saveCatheter").toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,data);
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


    /**
     * 删除数据
     *
     * @return
     */
    public Response<String> CancelCatheter(String jlxh, String jgid) {
        Response<String> response = new Response<String>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("cancelCatheter?jlxh=").append(jlxh)
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
}
