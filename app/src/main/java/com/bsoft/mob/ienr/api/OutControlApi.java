package com.bsoft.mob.ienr.api;

import android.content.Context;
import android.util.Log;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.outcontrol.OutControl;
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
public class OutControlApi extends BaseApi {

    public String url;

    public OutControlApi(AppHttpClient httpClient, Context mContext) {
        super(httpClient, mContext);
    }

    public OutControlApi(AppHttpClient httpClient, Context mContext, String url) {
        super(httpClient, mContext);
        this.url = url;
    }

    @Override
    public AppHttpClient getHttpClient() {
        return httpClient;
    }

    public static OutControlApi getInstance(Context localContext) {
        OutControlApi api = (OutControlApi) localContext
                .getSystemService("com.bsoft.mob.ienr.api.OutControlApi");
        if (api == null)
            api = (OutControlApi) localContext.getApplicationContext()
                    .getSystemService("com.bsoft.mob.ienr.api.OutControlApi");
        if (api == null)
            throw new IllegalStateException("api not available");
        return api;
    }

    /**
     * 获取病人当前外出状态
     *
     * @param zyh
     * @param brbq
     * @param jgid
     * @param sysType
     * @return
     */
    public Response<List<OutControl>> GetPatientStatus(String zyh, String brbq, String jgid,
                                                       int sysType) {
        Response<List<OutControl>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url)
                .append("get/GetPatientStatus?zyh=").append(zyh)
                .append("&brbq=").append(brbq).append("&jgid=")
                .append(jgid).append("&sysType=").append(sysType)
                .toString();
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
                        new TypeReference<Response<List<OutControl>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;

    }

    /**
     * 外出登记
     *
     * @param data
     * @return
     */
    public Response<String> RegisterOutPatient(String data) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("post/RegisterOutPatient").toString();
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
     * 回床登记
     *
     * @param jlxh    记录序号
     * @param hcdjsj  时间
     * @param hcdjhs  护士
     * @param jgid    机构
     * @param sysType 类型
     * @return
     */
    public Response<String> RegisterBackToBed(String jlxh, String hcdjsj,
                                              String hcdjhs, String jgid, int sysType) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";

        try {
            hcdjsj = URLEncoder.encode(hcdjsj, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String uri =  new StringBuffer(url)
                .append("get/RegisterBackToBed?jlxh=").append(jlxh)
                .append("&hcdjsj=").append(hcdjsj).append("&hcdjhs=")
                .append(hcdjhs).append("&jgid=").append(jgid)
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
    /*升级编号【56010038】============================================= start
                外出管理PDA上只有登记功能，查询需要找到具体的人再查询，不太方便，最好能有一个查询整个病区外出病人的列表
            ================= classichu 2018/3/7 19:49
            */
    public Response<List<OutControl>> GetAllOutPatients( String brbq, String jgid,
                                                         int sysType) {
        Response<List<OutControl>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url)
                .append("get/getAllOutPatients?brbq=").append(brbq)
                .append("&jgid=")
                .append(jgid).append("&sysType=").append(sysType)
                .toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
        if (!isRequestSuccess(httpString.first)) {
            response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
            return response;
        }
        String entity = httpString.second;
        Log.i(Constant.TAG_COMM, "GetAllOutPatients: entity"+entity);
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<List<OutControl>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }
    /* =============================================================== end */

    public Response<List<OutControl>> GetOutPatientByZyh(String zyh, String brbq, String jgid,
                                                         int sysType) {
        Response<List<OutControl>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url)
                .append("get/GetOutPatientByZyh?zyh=").append(zyh)
                .append("&brbq=").append(brbq).append("&jgid=")
                .append(jgid).append("&sysType=").append(sysType)
                .toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
        if (!isRequestSuccess(httpString.first)) {
            response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
            return response;
        }
        String entity = httpString.second;
        Log.i(Constant.TAG_COMM, "GetOutPatientByZyh: entity"+entity);
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<List<OutControl>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }
}
