package com.bsoft.mob.ienr.api;

import android.content.Context;
import android.util.Log;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.clinicalevent.ClinicalEventType;
import com.bsoft.mob.ienr.model.kernel.SickPersonVo;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignDoubleCheckHistoryData;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignHistoryData;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignHistoryInfo;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignInputItem;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignSync;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignTimeEntity;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignTypeItem;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午2:47:35
 * @类说明 生命体征API
 */
public class LifeSignApi extends BaseApi {

    public String url;

    public LifeSignApi(AppHttpClient httpClient, Context mContext) {
        super(httpClient, mContext);
    }

    public LifeSignApi(AppHttpClient httpClient, Context mContext, String url) {
        super(httpClient, mContext);
        this.url = url;
    }

    public static LifeSignApi getInstance(Context localContext) {
        // Context localContext = AppContext.getContext();
        LifeSignApi api = (LifeSignApi) localContext
                .getSystemService("com.bsoft.mob.ienr.api.LifeSignApi");
        if (api == null)
            api = (LifeSignApi) localContext.getApplicationContext()
                    .getSystemService("com.bsoft.mob.ienr.api.LifeSignApi");
        if (api == null)
            throw new IllegalStateException("api not available");
        return api;
    }

    /**
     * 动态体征
     */
    public Response<ArrayList<LifeSignTypeItem>> getLifeSignTypeItemList(String zyh, String bqid,
                                                                         String jgid, int sysType) {

        Response<ArrayList<LifeSignTypeItem>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getLifeSignTypeItemList?")
                .append("zyh=").append(zyh).append("&bqid=").append(bqid)
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
                        new TypeReference<Response<ArrayList<LifeSignTypeItem>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 动态控件
     */
    public Response<LifeSignInputItem> getLifeSignItem(String srxh, String zyh, String jgid, int sysType) {

        Response<LifeSignInputItem> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getLifeSignItem?")
                .append("srxh=").append(srxh).append("&zyh=").append(zyh)
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
                        new TypeReference<Response<LifeSignInputItem>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 历史体温
     */
    public Response<LifeSignDoubleCheckHistoryData> GetLifeHistoryItem(String tzxm, String zyh, String jgid, int sysType) {

        Response<LifeSignDoubleCheckHistoryData> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getLifeSignDoubleCheckHistoryData?")
                .append("tzxm=").append(tzxm).append("&zyh=").append(zyh).append("&jgid=").append(jgid).toString();
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
                        new TypeReference<Response<LifeSignDoubleCheckHistoryData>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 体征保存
     */
    public Response<LifeSignSync> lifeSymptomSave(String data) {

        Response<LifeSignSync> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("post/saveLifeSignData").toString();
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
                        new TypeReference<Response<LifeSignSync>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 体征查询
     *
     * @param start
     * @param end
     * @param zyh
     * @param jgid
     * @return
     */
    public Response<LifeSignHistoryData> LifeSymptomQuery(String start, String end,
                                                          String zyh, String jgid) {

        Response<LifeSignHistoryData> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getLifeSignHistoryData?")
                .append("start=").append(start).append("&end=").append(end)
                .append("&zyh=").append(zyh)
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
                        new TypeReference<Response<LifeSignHistoryData>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 删除记录
     *
     * @param urid
     * @param rid
     * @param cjh
     * @param sysType
     * @param jgid
     * @return
     */
    public Response<String> LifeSymptomDelete(String urid, int rid, int cjh,
                                              String jgid, int sysType) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/deleteLifeSignHistoryData?cjh=")
                .append(cjh).append("&jgid=").append(jgid).toString();
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

    public Response<String> LifeSymptomUpdate(String value,String urid, int rid, int cjh,
                                              String jgid, int sysType) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/updateLifeSignHistoryData?cjh=")
                .append(cjh).append("&jgid=").append(jgid).append("&value=").append(value).toString();
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

    /**
     * 获取体征采集时刻表
     *
     * @param bqid
     * @param jgid
     * @param sysType
     * @return
     */
    public Response<List<LifeSignTimeEntity>> GetTimePointList(String bqid, String jgid, int sysType) {
        Response<List<LifeSignTimeEntity>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getTimePointList?bqid=")
                .append(bqid).append("&jgid=").append(jgid).toString();
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
                        new TypeReference<Response<List<LifeSignTimeEntity>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 获取临床事件补充数据列表
     *
     * @param zyh
     * @param yhid
     * @param bqid
     * @param jgid
     * @return
     */
    public Response<List<ClinicalEventType>> getClinicalEventTypeList(String zyh, String yhid, String bqid, String jgid) {
        Response<List<ClinicalEventType>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getClinicalEventTypeList?")
                .append("zyh=").append(zyh)
                .append("&yhid=").append(yhid)
                .append("&bqid=").append(bqid)
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
                        new TypeReference<Response<List<ClinicalEventType>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 临床事件保存
     */
    public Response<List<ClinicalEventType>> clinicalEventSave(String data) {
        Response<List<ClinicalEventType>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("post/saveClinicalEventData").toString();
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
                        new TypeReference<Response<List<ClinicalEventType>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 删除临床事件补充数据列表
     *
     * @param zyh
     * @param bqid
     * @param jgid
     * @return
     */
    public Response<List<ClinicalEventType>> clinicalEventDelete(String sjxh, String zyh, String yhid, String bqid, String jgid) {
        Response<List<ClinicalEventType>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/deleteClinicalEventData?")
                .append("sjxh=").append(sjxh)
                .append("&zyh=").append(zyh)
                .append("&yhid=").append(yhid)
                .append("&bqid=").append(bqid)
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
                        new TypeReference<Response<List<ClinicalEventType>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    public Response<ArrayList<SickPersonVo>> GetPatientList(String areaId,  int start, int end, int type,String jgid) {

        Response<ArrayList<SickPersonVo>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("GetPatientList?")
                .append("bqid=").append(areaId)
                .append("&start=").append(start)
                .append("&end=").append(end)
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

    public Response<List<LifeSignHistoryInfo>> getLifeSignHistoryInfo(String zyh, String xmh, String jgid) {

        Response<List<LifeSignHistoryInfo>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getLifeSignHistoryInfo?")
                .append("zyh=").append(zyh).append("&xmh=").append(xmh)
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
                        new TypeReference<Response<List<LifeSignHistoryInfo>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }
}
