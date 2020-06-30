package com.bsoft.mob.ienr.api;

import android.content.Context;
import android.util.Config;
import android.util.Log;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.visit.VisitCount;
import com.bsoft.mob.ienr.model.visit.VisitHistory;
import com.bsoft.mob.ienr.model.visit.VisitPerson;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by king on 2016/11/22.
 */
public class VisitApi extends BaseApi {

    public String url;

    public VisitApi(AppHttpClient httpClient, Context mContext) {
        super(httpClient, mContext);
    }

    public VisitApi(AppHttpClient httpClient, Context mContext, String url) {
        super(httpClient, mContext);
        this.url = url;
    }

    public static VisitApi getInstance(Context localContext) {
        VisitApi api = (VisitApi) localContext
                .getSystemService("com.bsoft.mob.ienr.api.VisitApi");
        if (api == null)
            api = (VisitApi) localContext.getApplicationContext()
                    .getSystemService("com.bsoft.mob.ienr.api.VisitApi");
        if (api == null)
            throw new IllegalStateException("api not available");
        return api;
    }

    @Override
    public AppHttpClient getHttpClient() {
        return httpClient;
    }

    /**
     * 手动保存巡视记录
     *
     * @param urid
     * @param zyh     住院号
     * @param xsqk    巡视类别
     * @param jgid    机构ID
     * @param sysType 1为android
     * @return
     */
    public Response<List<VisitPerson>> SetPatrol(String isScan, String brbq, String urid, String zyh,
                                                 String xsqk, String jgid, int sysType) {
        Response<List<VisitPerson>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("post/SetPatrol?brbq=")
                .append(brbq).append("&urid=").append(urid)
                .append("&isScan=").append(isScan).append("&zyh=")
                .append(zyh).append("&xsqk=").append(xsqk).append("&jgid=")
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
                        new TypeReference<Response<List<VisitPerson>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 扫描保存巡视记录
     *
     * @param urid
     * @param sanStr  住院号
     * @param xsqk    巡视类别
     * @param jgid    机构ID
     * @param sysType 1为android
     * @return
     */
    public Response<List<VisitPerson>> SetPatrolForScan(String brbq, String urid,
                                                        String sanStr, String xsqk, String jgid, int sysType) {
        Response<List<VisitPerson>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("post/SetPatrolForScan?brbq=")
                .append(brbq).append("&urid=").append(urid)
                .append("&sanStr=").append(sanStr).append("&xsqk=")
                .append(xsqk).append("&jgid=").append(jgid)
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
                        new TypeReference<Response<List<VisitPerson>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;

    }

    /**
     * @param urid
     * @param ksdm    科室代码
     * @param jgid    机构ID
     * @param sysType 1为android
     * @return
     */
    public Response<VisitCount> GetPatrol(String urid, String ksdm, String jgid,
                                          int sysType) {

        Response<VisitCount> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/GetPatrol?urid=")
                .append(urid).append("&ksdm=").append(ksdm)
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
                        new TypeReference<Response<VisitCount>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    public Response<List<VisitHistory>> GetPatrolHistory(String zyh, String xsrq, String jgid,
                                                         int sysType) {

        Response<List<VisitHistory>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/GetPatrolHistory?zyh=")
                .append(zyh).append("&xsrq=").append(xsrq).append("&jgid=")
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
                        new TypeReference<Response<List<VisitHistory>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /*升级编号【56010027】============================================= start
  处理房间条码、获取房间病人处理
                                  ================= classichu 2018/3/22 10:23
                                  */
    public Response<VisitCount> GetRoomPatientList(String ksdm, String fjhm, String jgid) {
        Response<VisitCount> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getRoomPatientList?fjhm=")
                .append(fjhm).append("&ksdm=").append(ksdm).append("&jgid=").append(jgid).toString();
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
                        new TypeReference<Response<VisitCount>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    public Response<List<VisitPerson>> SetPatrol_Some(String brbq, String urid, String zyh_list,
                                                      String xsqk, String jgid, int sysType) {
        Response<List<VisitPerson>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("post/SetPatrol_Some?brbq=")
                .append(brbq).append("&urid=").append(urid).append("&zyh_list=")
                .append(zyh_list).append("&xsqk=").append(xsqk).append("&jgid=")
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
                        new TypeReference<Response<List<VisitPerson>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }
    /* =============================================================== end */

}
