package com.bsoft.mob.ienr.api;

import android.content.Context;
import android.util.Log;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.skintest.SickerPersonSkinTest;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Locale;


public class SkinTestApi extends BaseApi {

    public String url;

    public SkinTestApi(AppHttpClient httpClient, Context mContext) {
        super(httpClient, mContext);
    }

    public SkinTestApi(AppHttpClient httpClient, Context mContext,
                       String url) {
        super(httpClient, mContext);
        this.url = url;
    }

    public static SkinTestApi getInstance(Context mContext) {
        // Context localContext = AppContext.getContext();
        SkinTestApi api = (SkinTestApi) mContext
                .getSystemService("com.bsoft.mob.ienr.api.SkinTestApi");
        if (api == null)
            api = (SkinTestApi) mContext
                    .getApplicationContext()
                    .getSystemService("com.bsoft.mob.ienr.api.SkinTestApi");
        if (api == null)
            throw new IllegalStateException("api not available");
        return api;
    }

    public Response<List<SickerPersonSkinTest>> getSkinTest(String zyh,String type, String brbq, String jgid) {
        Response<List<SickerPersonSkinTest>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String entity=null;
        if (Constant.DEBUG_LOCAL) {
            //entity = getLocalXml("ss.xml");
        } else {
            String uri = new StringBuffer(url)
                    .append("get/getSkinTest?brbq=").append(brbq)
                    .append("&zyh=").append(zyh)
                    .append("&type=").append(type)
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
        }
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<List<SickerPersonSkinTest>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }


    public Response<String> scanExecuePs(String json) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String entity=null;
        if (Constant.DEBUG_LOCAL) {
            // entity = getLocalXml("XXX.xml");
        } else {
            String uri = new StringBuffer(url).append("post/scanExecuePs").toString();
            if (Constant.LOG_URI) {
                Log.d(Constant.TAG, uri);
            }
            HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,json);
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

    /**
     *
     * @param json 要保存的数据对象
     * @return
     */
    public Response<String> saveSkinTest(String json) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String entity=null;
        if (Constant.DEBUG_LOCAL) {
           // entity = getLocalXml("XXX.xml");
        } else {
            String uri = new StringBuffer(url).append("post/saveSkinTest").toString();
            if (Constant.LOG_URI) {
                Log.d(Constant.TAG, uri);
            }
            HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,json);
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



}
