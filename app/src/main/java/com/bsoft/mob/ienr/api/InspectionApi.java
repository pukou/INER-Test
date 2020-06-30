package com.bsoft.mob.ienr.api;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.inspection.*;
import com.bsoft.mob.ienr.model.kernel.SickPersonVo;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
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
public class InspectionApi extends BaseApi {

    public String url;

    public InspectionApi(AppHttpClient httpClient, Context mContext) {
        super(httpClient, mContext);
    }

    public InspectionApi(AppHttpClient httpClient, Context mContext, String url) {
        super(httpClient, mContext);
        this.url = url;
    }

    public static InspectionApi getInstance(Context localContext) {
        // Context localContext = AppContext.getContext();
        InspectionApi api = (InspectionApi) localContext
                .getSystemService("com.bsoft.mob.ienr.api.inspection");
        if (api == null)
            api = (InspectionApi) localContext.getApplicationContext()
                    .getSystemService("com.bsoft.mob.ienr.api.inspection");
        if (api == null)
            throw new IllegalStateException("api not available");
        return api;
    }
    public Response<List<InspectionXMBean>> GetInspectionXMBeanList(String xmid,String zyh, String jgid, int sysType) {
        Response<List<InspectionXMBean>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
//        String urlTemp = "http://172.29.1.1:8080/NIS/mobile/expense/";
        String uri = new StringBuffer(url).append("GetInspectionXMBeanList?zyh=")
                .append(zyh).append("&jgid=").append(jgid)
                .append("&xmid=").append(xmid)
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
                        new TypeReference<Response<List<InspectionXMBean>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }
    /**
     * 检验结果查询
     *
     * @param zyh
     * @return
     */
    public Response<List<InspectionVo>> GetInspectionList(String zyh, String jgid, int sysType) {
        Response<List<InspectionVo>> response = new Response<List<InspectionVo>>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
//        String urlTemp = "http://172.29.1.1:8080/NIS/mobile/expense/";
        String uri = new StringBuffer(url).append("GetInspectionList?zyh=")
                .append(zyh).append("&jgid=").append(jgid)
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
                        new TypeReference<Response<List<InspectionVo>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 检查结果查询
     *
     * @param zyh
     * @return
     */
    public Response<List<ExamineVo>> GetExamineResultList(String zyh, String jgid, int sysType) {
        Response<List<ExamineVo>> response = new Response<List<ExamineVo>>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("GetExamineResultList?zyh=")
                .append(zyh).append("&jgid=").append(jgid)
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
                        new TypeReference<Response<List<ExamineVo>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 获取检验详细信息
     *
     * @param inspectionNumber
     * @return
     */
    public Response<List<InspectionDetailVo>> GetInspectionDetail(String inspectionNumber,
                                                                  String jgid, int sysType) {
        Response<List<InspectionDetailVo>> response = new Response<List<InspectionDetailVo>>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url)
                .append("GetInspectionDetail?inspectionNumber=")
                .append(inspectionNumber).append("&jgid=").append(jgid)
                .append("&sysType=").append(sysType).toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
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
                        new TypeReference<Response<List<InspectionDetailVo>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 获取检查结果详情
     *
     * @param examineNumber
     * @param type          1：ris，0：uis
     * @return
     */
    public Response<List<ExamineDetailVo>> GetExamineResultDetail(String examineNumber,
                                                                  String type, String jgid, int sysType) {
        Response<List<ExamineDetailVo>> response = new Response<List<ExamineDetailVo>>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url)
                .append("GetExamineResultDetail?examineNumber=")
                .append(examineNumber).append("&type=").append(type)
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
                        new TypeReference<Response<List<ExamineDetailVo>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 标本采集
     *
     * @param zyh
     * @return
     */
    public Response<List<SpecimenVo>> GetSpecimenList(String zyh, String jgid,int sysType) {
        Response<List<SpecimenVo>> response = new Response<List<SpecimenVo>>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url)
                .append("GetSpecimenList?zyh=")
                .append(zyh).append("&jgid=").append(jgid)
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

        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<List<SpecimenVo>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 检验执行
     *
     * @param zyh
     * @param urid
     * @param isScan
     * @param systype
     * @param jgid
     * @return
     */
    public Response<String> ExecuteSpecimen(String zyh, String urid, String tmbh,
                                            String isScan, String sbmc, String jgid, int sysType) {
        Response<String> response = new Response<String>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url)
                .append("ExecuteSpecimen?zyh=")
                .append(zyh).append("&urid=").append(urid).append("&tmbh=")
                .append(tmbh).append("&isScan=").append(isScan)
                .append("&sbmc=").append(sbmc)
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

    public Response<List<SpecimenVo>> GetHistorySpecimenList(String zyh, String start,
                                                             String end, String jgid, int sysType) {

        Response<List<SpecimenVo>> response = new Response<List<SpecimenVo>>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        StringBuffer sb = new StringBuffer(url)
                .append("GetHistorySpecimenList?zyh=").append(zyh)
                .append("&jgid=").append(jgid)
                .append("&sysType=")
                .append(sysType);

        if (!EmptyTool.isBlank(start)) {
            sb.append("&start=").append(start);
        }

        if (!EmptyTool.isBlank(end)) {
            sb.append("&end=").append(end);
        }
        String uri = sb.toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }

        HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
        if (!isRequestSuccess(httpString.first)) {
            response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
            return response;
        }
        String entity = httpString.second;

        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<List<SpecimenVo>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 取消执行
     *
     * @param urid
     * @param tmbh
     * @param zyh
     * @param jgid
     * @param sysType
     * @return
     */
    public Response<String> CancelSpecimen(String urid, String tmbh, String zyh,String sbmc,
                                           String jgid, int sysType) {

        Response<String> response = new Response<String>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url)
                .append("CancelSpecimen?urid=")
                .append(urid).append("&tmbh=").append(tmbh)
                .append("&sbmc=").append(sbmc).append("&zyh=")
                .append(zyh).append("&jgid=").append(jgid).append("&sysType=")
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
    public Response<List<CYInfoBean>> GetCYInfoByTMBH(String brid, String tmbh,  String jgid, int sysType) {

        Response<List<CYInfoBean>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url)
                .append("GetCYInfoByTMBH?brid=")
                .append(brid).append("&tmbh=").append(tmbh).append("&jgid=").append(jgid).append("&sysType=")
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

        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<List<CYInfoBean>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }
    public Response<String> Delivery(String zyh, String urid, String tmbh,
                                     String isScan, String jgid, int sysType) {

        Response<String> response = new Response<String>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url)
                .append("Delivery?zyh=").append(zyh)
                .append("&urid=").append(urid).append("&tmbh=").append(tmbh)
                .append("&isScan=").append(isScan).append("&jgid=")
                .append(jgid).append("&sysType=").append(sysType).toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
        if (!isRequestSuccess(httpString.first)) {
            response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
            return response;
        }
        String entity = httpString.second;

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

    public Response<List<SickPersonVo>> GetPatientList(String bqdm, String jgid, int sysType,int mCheckBoxFiter,int mTypeFilterPos) {
        Response<List<SickPersonVo>> response = new Response<List<SickPersonVo>>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url)
                .append("GetPatientList?bqdm=")
                .append(bqdm).append("&jgid=").append(jgid)
                .append("&mCheckBoxFiter=").append(mCheckBoxFiter)
                .append("&mTypeFilterPos=").append(mTypeFilterPos)
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

}
