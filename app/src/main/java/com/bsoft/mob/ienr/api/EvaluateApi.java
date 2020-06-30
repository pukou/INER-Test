package com.bsoft.mob.ienr.api;

import android.content.Context;
import android.util.Log;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.evaluate.EvaluateFormItem;
import com.bsoft.mob.ienr.model.evaluate.EvaluateRecordItem;
import com.bsoft.mob.ienr.model.evaluate.EvaluateResponse;
import com.bsoft.mob.ienr.model.nursingeval.KeyValue;
import com.bsoft.mob.ienr.model.nursingeval.NursingEvaluateRecord;
import com.bsoft.mob.ienr.model.nursingeval.NursingEvaluateStyte;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 护理评估
 *
 * @author hy
 */
public class EvaluateApi extends BaseApi {

    public String url;

    public EvaluateApi(AppHttpClient httpClient, Context mContext) {
        super(httpClient, mContext);
    }

    public EvaluateApi(AppHttpClient httpClient, Context mContext, String url) {
        super(httpClient, mContext);
        this.url = url;
    }

    public static EvaluateApi getInstance(Context mContext)
            throws IllegalStateException {

        EvaluateApi api = (EvaluateApi) mContext
                .getSystemService("com.bsoft.mob.ienr.api.evalute");
        if (api == null)
            api = (EvaluateApi) mContext.getApplicationContext()
                    .getSystemService("com.bsoft.mob.ienr.api.evalute");
        if (api == null)
            throw new IllegalStateException("api not available");
        return api;
    }

    /**
     * 获取评估单列表
     */
    public Response<List<EvaluateRecordItem>> GetNewEvaluationList(String bqdm, String jgid,String zyh,
                                                                   int sysType) {

        Response<List<EvaluateRecordItem>> response = new Response<List<EvaluateRecordItem>>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("GetNewEvaluationList?bqdm=")
                .append(bqdm).append("&jgid=").append(jgid).append("&zyh=").append(zyh).append("&sysType=")
                .append(sysType).toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
//        HttpBackMsg<Integer, String, String> httpString = getHttpString_WithoutFormatDateString(uri);
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
                        new TypeReference<Response<List<EvaluateRecordItem>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;

    }

    /**
     * 获取评估单列表
     */
    public Response<List<EvaluateRecordItem>> GetNewEvaluationListForYslx(String yslx, String bqdm, String jgid,
                                                                          int sysType) {
        Response<List<EvaluateRecordItem>> response = new Response<List<EvaluateRecordItem>>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("GetNewEvaluationListForYslx?yslx=")
                .append(yslx).append("&bqdm=").append(bqdm)
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
                        new TypeReference<Response<List<EvaluateRecordItem>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }



    /**
     * 获取评估单记录列表
     *
     * @param start
     * @param end
     * @param zyh
     * @param jgid
     * @param sysType
     * @return
     */
    public Response<List<EvaluateRecordItem>> GetEvaluationList(String start, String end, String zyh,
                                                                String jgid, int sysType) {
        Response<List<EvaluateRecordItem>> response = new Response<List<EvaluateRecordItem>>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("GetEvaluationList?start=")
                .append(start).append("&end=").append(end).append("&zyh=")
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
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<List<EvaluateRecordItem>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    public Response<EvaluateFormItem> GetEvaluation(String jlxh, String jgid, String lybs,
                                                    String ysxh, String txsj, int sysType) {
        Response<EvaluateFormItem> response = new Response<EvaluateFormItem>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("GetEvaluation?jlxh=")
                .append(jlxh).append("&ysxh=").append(ysxh)
                .append("&lybs=").append(lybs)
                .append("&txsj=").append(txsj)
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
                        new TypeReference<Response<EvaluateFormItem>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    public Response<KeyValue<List<NursingEvaluateStyte>, NursingEvaluateRecord>> GetNursingEvaluation_V56Update1(String jlxh) {
        Response<KeyValue<List<NursingEvaluateStyte>, NursingEvaluateRecord>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("GetNursingEvaluation_V56Update1?jlxh=")
                .append(jlxh).toString();
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
                        new TypeReference<Response<KeyValue<List<NursingEvaluateStyte>, NursingEvaluateRecord>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    public Response<List<NursingEvaluateStyte>> GetNursingEvaluationStyleList_V56Update1(String zyh, String brbq, String jgid) {
        Response<List<NursingEvaluateStyte>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("GetNursingEvaluationStyleList_V56Update1?zyh=")
                .append(zyh).append("&brbq=").append(brbq).append("&jgid=").append(jgid)
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
                        new TypeReference<Response<List<NursingEvaluateStyte>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    public Response<List<NursingEvaluateRecord>> GetNursingEvaluationRecordList_V56Update1(String zyh, String brbq, String jgid) {
        Response<List<NursingEvaluateRecord>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("GetNursingEvaluationRecordList_V56Update1?zyh=")
                .append(zyh).append("&brbq=").append(brbq).append("&jgid=").append(jgid)
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
                        new TypeReference<Response<List<NursingEvaluateRecord>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    public Response<String> getSJHQFS(String yslx, String jgid) {
        Response<String> response = new Response<String>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("getSJHQFS?yslx=").append(yslx)
                .append("&jgid=").append(jgid)
                .append("&sysType=")
                .append(Constant.sysType).toString();
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

    public Response<EvaluateFormItem> GetNewEvaluation(boolean isZKNotCheckBQ,boolean isNewPage,String bqdm, String zyh, String ysxh,
                                                       String txsj, String jgid, int sysType) {
        Response<EvaluateFormItem> response = new Response<EvaluateFormItem>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("GetNewEvaluation?bqdm=")
                .append(bqdm).append("&zyh=").append(zyh)
                .append("&ysxh=").append(ysxh)
                .append("&txsj=").append(txsj)
                .append("&jgid=").append(jgid)
                .append("&isZKNotCheckBQ=").append(isZKNotCheckBQ)
                .append("&isNewPage=").append(isNewPage)
                .append("&sysType=")
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
                        new TypeReference<Response<EvaluateFormItem>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }


    /***
     *
     * @param mode  sign   unsign
     * @param jlxh
     * @param signWho  1  护士签名    2  护士长签名、审阅
     * @param signUserCode
     * @param signUserName
     * @return
     */
    public Response<String> SignNursingEvaluation_V56Update1(String mode, String jlxh, String signWho, String signUserCode, String signUserName) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";

        String uri = new StringBuffer(url).append("SignNursingEvaluation_V56Update1?mode=")
                .append(mode).append("&jlxh=").append(jlxh)
                .append("&signWho=").append(signWho)
                .append("&signUserCode=").append(signUserCode)
                .append("&signUserName=").append(signUserName)
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
                        new TypeReference<Response<String>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    public Response<NursingEvaluateRecord> GetRelationData_V56Update1(String data) {
        Response<NursingEvaluateRecord> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";

        String uri = new StringBuffer(url).append("post/GetRelationData_V56Update1").toString();
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
                        new TypeReference<Response<NursingEvaluateRecord>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }


    public Response<NursingEvaluateRecord> SaveNursingEvaluation_V56Update1(String data) {
        Response<NursingEvaluateRecord> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";

        String uri = new StringBuffer(url).append("post/SaveNursingEvaluation_V56Update1").toString();
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
                        new TypeReference<Response<NursingEvaluateRecord>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    public Response<KeyValue<List<NursingEvaluateStyte>, Map<String, String>>> GetNewNursingEvaluation_V56Update1(String zyh, String ysxh,
                                                                                                                  String bbh, String brbq, String jgid) {
        Response<KeyValue<List<NursingEvaluateStyte>, Map<String, String>>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("GetNewNursingEvaluation_V56Update1?zyh=")
                .append(zyh).append("&ysxh=").append(ysxh)
                .append("&bbh=").append(bbh)
                .append("&brbq=").append(brbq)
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
                        new TypeReference<Response<KeyValue<List<NursingEvaluateStyte>, Map<String, String>>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 审阅评估单
     *
     * @param jlxh
     * @param sygh
     * @param jgid
     * @param sysType
     * @return
     */
    public Response<EvaluateResponse> EvaluationReview(String jlxh, String sygh, String jgid,
                                                       int sysType) {
        Response<EvaluateResponse> response = new Response<EvaluateResponse>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("EvaluationReview?jlxh=")
                .append(jlxh).append("&sygh=").append(sygh).append("&jgid=")
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
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<EvaluateResponse>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 签名
     *
     * @param jlxh
     * @param ysfl
     * @param hsqm1
     * @param hsqm2
     * @param dlbz
     * @param qmbz
     * @param jgid
     * @param sysType
     * @return
     */
    public Response<EvaluateResponse> EvaluationSignature(String jlxh, String ysxh,
                                                          String ysfl, String lybs, String hsqm1, String hsqm2, String dlbz,
                                                          String qmbz, String jgid, int sysType) {
        Response<EvaluateResponse> response = new Response<EvaluateResponse>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("EvaluationSignature?jlxh=")
                .append(jlxh).append("&ysxh=").append(ysxh).append("&ysfl=")
                .append(ysfl).append("&lybs=").append(lybs).append("&hsqm1=")
                .append(hsqm1).append("&hsqm2=").append(hsqm2).append("&dlbz=")
                .append(dlbz).append("&qmbz=").append(qmbz).append("&jgid=")
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
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<EvaluateResponse>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 取消签名
     *
     * @param jlxh
     * @param ysfl
     * @param hsqm1
     * @param hsqm2
     * @param dlbz
     * @param qmbz
     * @param jgid
     * @param sysType
     * @return
     */
    public Response<EvaluateResponse> CancelEvaluationSignature(String jlxh, String ysxh,
                                                                String ysfl, String lybs, String hsqm1, String hsqm2, String dlbz,
                                                                String qmbz, String jgid, int sysType) {
        Response<EvaluateResponse> response = new Response<EvaluateResponse>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url)
                .append("CancelEvaluationSignature?jlxh=").append(jlxh)
                .append("&ysxh=").append(ysxh).append("&ysfl=").append(ysfl)
                .append("&lybs=").append(lybs).append("&hsqm1=").append(hsqm1)
                .append("&hsqm2=").append(hsqm2).append("&dlbz=").append(dlbz)
                .append("&qmbz=").append(qmbz).append("&jgid=").append(jgid)
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
                        new TypeReference<Response<EvaluateResponse>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    public Response<EvaluateFormItem> SaveEvaluation(String data) {
        Response<EvaluateFormItem> response = new Response<EvaluateFormItem>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";

        String uri = new StringBuffer(url).append("post/SaveEvaluation").toString();
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
                        new TypeReference<Response<EvaluateFormItem>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 取消审阅
     *
     * @param jlxh
     * @param sygh
     * @param jgid
     * @param sysType
     * @return
     */
    public Response<EvaluateResponse> CancelEvaluationReview(String jlxh, String sygh,
                                                             String jgid, int sysType) {
        Response<EvaluateResponse> response = new Response<EvaluateResponse>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url)
                .append("CancelEvaluationReview?jlxh=").append(jlxh)
                .append("&sygh=").append(sygh).append("&jgid=").append(jgid)
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
                        new TypeReference<Response<EvaluateResponse>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 作废表单
     *
     * @param jlxh
     * @param jgid
     * @param sysType
     * @return
     */
    public Response<EvaluateResponse> CancelEvaluation(String jlxh, String jgid, int sysType) {
        Response<EvaluateResponse> response = new Response<EvaluateResponse>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("CancelEvaluation?jlxh=")
                .append(jlxh).append("&jgid=").append(jgid).append("&sysType=")
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
                        new TypeReference<Response<EvaluateResponse>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * @param @param  zyh 住院号
     * @param @param  ysxh 样式序号
     * @param @param  dzlx 对照类型（目前支持3类，2：风险；3：宣教；5：体征
     * @param @param  bqdm 病区代码
     * @param @param  jgid 机构id
     * @param @param  sysType 系统类型
     * @param @return
     * @return ParserModel
     * @throws
     * @Description: 获取对照项目的数据
     */
    public Response<EvaluateFormItem> GetRelatvieData(String zyh, String ysxh, String dzlx,
                                                      String bqdm, String txsj, String jgid) {
        Response<EvaluateFormItem> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("GetRelativeData?zyh=")
                .append(zyh).append("&ysxh=").append(ysxh)
                .append("&dzlx=").append(dzlx)
                .append("&bqdm=").append(bqdm)
                .append("&txsj=").append(txsj)
                .append("&jgid=").append(jgid).append("&sysType=")
                .append(Constant.sysType).toString();
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
                        new TypeReference<Response<EvaluateFormItem>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }
}
