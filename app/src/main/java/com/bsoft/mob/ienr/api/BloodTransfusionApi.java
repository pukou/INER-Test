package com.bsoft.mob.ienr.api;

import android.content.Context;
import android.util.Log;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.blood.BloodReciveInfo;
import com.bsoft.mob.ienr.model.blood.BloodTransfusionInfo;
import com.bsoft.mob.ienr.model.blood.BloodTransfusionTourInfo;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Locale;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午2:47:35
 * @类说明 医嘱接口
 */
public class BloodTransfusionApi extends BaseApi {

    public String url;

    public BloodTransfusionApi(AppHttpClient httpClient, Context mContext) {
        super(httpClient, mContext);
    }

    public BloodTransfusionApi(AppHttpClient httpClient, Context mContext,
                               String url) {
        super(httpClient, mContext);
        this.url = url;
    }

    public static BloodTransfusionApi getInstance(Context mContext) {
        // Context localContext = AppContext.getContext();
        BloodTransfusionApi api = (BloodTransfusionApi) mContext
                .getSystemService("com.bsoft.mob.ienr.api.BloodTransfusionApi");
        if (api == null)
            api = (BloodTransfusionApi) mContext
                    .getApplicationContext()
                    .getSystemService("com.bsoft.mob.ienr.api.BloodTransfusionApi");
        if (api == null)
            throw new IllegalStateException("api not available");
        return api;
    }

    /**
     * 获取输血列表
     *
     * @param start
     * @param end
     * @param zyh
     * @param jgid
     * @return
     */
    public Response<List<BloodTransfusionInfo>> GetBloodTransfusionList(String start, String end,
                                                                        String zyh, String jgid) {
        Response<List<BloodTransfusionInfo>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String entity;
        if (Constant.DEBUG_LOCAL) {
            entity = getLocalXml("GetBloodTransfusionList.xml");
        } else {
            String uri = new StringBuffer(url)
                    .append("get/getBloodTransfusionPlanList?start=").append(start)
                    .append("&end=").append(end).append("&zyh=").append(zyh)
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
                        new TypeReference<Response<List<BloodTransfusionInfo>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 输血执行
     *
     * @param xdh
     * @param xdxh
     * @param zyh
     * @param hdgh
     * @param zxgh
     * @param operationType 0 开始；3结束
     * @param jgid
     * @return
     */
    public Response<List<BloodTransfusionInfo>> ExcueteBloodTransfusion(String xdh, String xdxh, String zyh, String hdgh,
                                                                        String zxgh, int operationType, String jgid) {
        Response<List<BloodTransfusionInfo>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String entity;
        if (Constant.DEBUG_LOCAL) {
            entity = getLocalXml("ExcueteBloodTransfusion.xml");
        } else {
            String uri = new StringBuffer(url)
                    .append("get/excueteBloodTransfusion?xdh=").append(xdh)
                    .append("&xdxh=").append(xdxh).append("&zyh=").append(zyh)
                    .append("&hdgh=").append(hdgh).append("&zxgh=").append(zxgh)
                    .append("&operationType=").append(operationType)
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
                        new TypeReference<Response<List<BloodTransfusionInfo>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 获取输血签收列表
     *
     * @param start
     * @param end
     * @param bqid
     * @param status 0:未签收，1：已签收
     * @param jgid
     * @return
     */
    public Response<List<BloodReciveInfo>> getBloodRecieveList(String bqid, String status, String start, String end, String jgid) {
        Response<List<BloodReciveInfo>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String entity;
        if (Constant.DEBUG_LOCAL) {
            entity = getLocalXml("GetBloodTransfusionList.xml");
        } else {
            String uri = new StringBuffer(url)
                    .append("get/getBloodRecieveList?bqid=").append(bqid)
                    .append("&status=").append(status)
                    .append("&start=").append(start)
                    .append("&end=").append(end)
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
                        new TypeReference<Response<List<BloodReciveInfo>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 签收
     *
     * @param xmid
     * @param jgid
     * @return
     */
    public Response<String> devliyBloodRecieve(String xmid, String jgid) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String entity;
        if (Constant.DEBUG_LOCAL) {
            entity = getLocalXml("DevliyBoolRecieve.xml");
        } else {
            String uri = new StringBuffer(url).append("get/devliyBloodRecieve?xmid=")
                    .append(xmid).append("&jgid=").append(jgid).toString();

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

    /**
     * 血液交接
     *
     * @param json 要保存的数据对象
     * @return
     */
    public Response<String> saveBloodRecieve(String json) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String entity;
        if (Constant.DEBUG_LOCAL) {
            entity = getLocalXml("saveBloodRecieve.xml");
        } else {
            String uri = new StringBuffer(url).append("post/saveBloodRecieve").toString();
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
     * 获取输血巡视记录
     *
     * @param sxdh 输血单号
     * @param jgid
     * @return
     */
    public Response<List<BloodTransfusionTourInfo>> getBloodTransfusionTourInfoList(String sxdh, String jgid) {
        Response<List<BloodTransfusionTourInfo>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String entity;
        if (Constant.DEBUG_LOCAL) {
            entity = getLocalXml("getBloodTransfusionTourInfoList.xml");
        } else {
            String uri = new StringBuffer(url).append("get/getBloodTransfusionTourInfoList?sxdh=")
                    .append(sxdh).append("&jgid=").append(jgid).toString();

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
                        new TypeReference<Response<List<BloodTransfusionTourInfo>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 血液交接
     *
     * @param json 要保存的数据对象
     * @return
     */
    public Response<String> saveBloodTransfusionTourInfo(String json) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String entity;
        if (Constant.DEBUG_LOCAL) {
            entity = getLocalXml("saveBloodTransfusionTourInfo.xml");
        } else {
            String uri = new StringBuffer(url).append("post/saveBloodTransfusionTourInfo").toString();
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
     * 血袋上交
     *
     * @param sxdh 输血单号
     * @param jgid
     * @return
     */
    public Response<String> saveBloodBagRecieve(String sxdh, String yhid, String jgid) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String entity;
        if (Constant.DEBUG_LOCAL) {
            entity = getLocalXml("saveBloodBagRecieve.xml");
        } else {
            String uri = new StringBuffer(url).append("get/saveBloodBagRecieve?sxdh=")
                    .append(sxdh).append("&yhid=").append(yhid).append("&jgid=").append(jgid).toString();

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

}
