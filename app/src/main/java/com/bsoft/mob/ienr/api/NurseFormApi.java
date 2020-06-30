/**
 * @Title: NurseFormApi.java
 * @Package com.bsoft.mob.ienr.api
 * @Description: 护理表单
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-11-19 上午11:11:05
 * @version V1.0
 */
package com.bsoft.mob.ienr.api;

import android.content.Context;
import android.util.Log;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.nursingeval.KeyValue;
import com.bsoft.mob.ienr.model.risk.*;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;
import com.fasterxml.jackson.core.type.TypeReference;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @ClassName: NurseFormApi
 * @Description: 风险评估
 * @date 2015-11-19 上午11:11:05
 */
public class NurseFormApi extends BaseApi {
    public String url;

    /**
     * <p>
     * Title: NurseFormApi
     * </p>
     * <p>
     * Description: 构造函数
     * </p>
     *
     * @param httpClient
     * @param mContext
     */
    public NurseFormApi(AppHttpClient httpClient, Context mContext) {
        super(httpClient, mContext);
    }

    public NurseFormApi(AppHttpClient httpClient, Context mContext, String url) {
        super(httpClient, mContext);
        this.url = url;
    }

    /**
     * @param @param  context
     * @param @return
     * @param @throws IllegalStateException
     * @return NurseFormApi
     * @throws
     * @Title: getInstance
     * @Description: 单利模式，获取对象实例
     */
    public static NurseFormApi getInstance(Context context)
            throws IllegalStateException {
        NurseFormApi api = (NurseFormApi) context
                .getSystemService("com.bsoft.mob.ienr.api.NurseFormApi");
        if (api == null)
            api = (NurseFormApi) context.getApplicationContext()
                    .getSystemService("com.bsoft.mob.ienr.api.NurseFormApi");
        if (api == null)
            throw new IllegalStateException("api not available");
        return api;
    }


    // TODO: 2016/12/5 新接口

    /**
     * 获取风险评估列表
     *
     * @param zyh  住院号
     * @param jgid 机构id
     * @param bqid 病区id
     * @return
     */
    public Response<List<RiskOverview>> togetRiskList(String zyh, String jgid, String bqid) {
        Response<List<RiskOverview>> response = new Response<List<RiskOverview>>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getDEList?")
                .append("zyh=").append(zyh).append("&jgid=").append(jgid)
                .append("&bqid=").append(bqid)
                .append("&sysType=").append(Constant.sysType).toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
        if (!isRequestSuccess(httpString.first)) {
            response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
            return response;
        }
        String entity = httpString.second;
//        Log.e("", "togetRiskList: "+uri );
//        Log.e("", "togetRiskList: "+entity );
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<List<RiskOverview>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 添加一条风险评估
     *
     * @param pgdh 评估单号
     * @param pglx 评估类型
     * @param jgid 机构id
     * @return
     */
    public Response<RiskRecord> togetNewRisk(String pgdh, String pglx, String jgid) {
        Response<RiskRecord> response = new Response<RiskRecord>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/addDE?").append("pgdh=")
                .append(pgdh).append("&pglx=").append(pglx).append("&jgid=")
                .append(jgid).append("&sysType=").append(Constant.sysType)
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
                        new TypeReference<Response<RiskRecord>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    public Response<List<DEPGHBean>> getPGHList(String pglx, String jgid) {
        Response<List<DEPGHBean>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getFXPGList?pglx=").append(pglx).append("&jgid=")
                .append(jgid).append("&sysType=").append(Constant.sysType)
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
                        new TypeReference<Response<List<DEPGHBean>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 获取第一条风险记录,不存在就添加一条
     *
     * @param zyh  住院号
     * @param pgdh 评估单号
     * @param pglx 评估类型
     * @param jgid 机构id
     * @return
     */

    public Response<RiskRecord> togetOrAddRisk(String zyh, String pgdh,
                                               String pglx, String jgid, String hqfs) {
        Response<RiskRecord> response = new Response<RiskRecord>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/addOrGetDE?")
                .append("zyh=").append(zyh).append("&pgdh=").append(pgdh)
                .append("&pglx=").append(pglx).append("&jgid=").append(jgid).append("&hqfs=").append(hqfs)
                .append("&sysType=").append(Constant.sysType).toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        //http://10.0.26.18:82/NIS/mobile/dangerevaluate/
        // get/addOrGetDE?zyh=605797&pgdh=10006&pglx=1&jgid=1&sysType=1
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
                        new TypeReference<Response<RiskRecord>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 保存风险评估记录
     *
     * @param data JSON格式的保存数据，包含DERecord,ZYH,BQID,JGID
     * @return
     */
    public Response<RiskRecord> tosaveRisk(String data) {
        Response<RiskRecord> response = new Response<RiskRecord>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("post/saveDE").toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri, data);
        if (!isRequestSuccess(httpString.first)) {
            response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
            return response;
        }
        String entity = httpString.second;
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<RiskRecord>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 获取评估单记录
     *
     * @param pgxh 　　评估序号
     * @param jgid 机构id
     * @return
     */
    public Response<RiskRecord> togetRiskRecord(String pgxh, String jgid) {
        Response<RiskRecord> response = new Response<RiskRecord>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getDERecord?")
                .append("pgxh=").append(pgxh).append("&jgid=").append(jgid)
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
                        new TypeReference<Response<RiskRecord>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 删除评估记录
     *
     * @param pgxh 　　评估序号
     * @param jgid 机构id
     * @return
     */
    public Response<String> todeleteRisk(String pgxh, String jgid) {
        Response<String> response = new Response<String>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/deleteDERecord?")
                .append("pgxh=").append(pgxh).append("&jgid=").append(jgid).toString();
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
     * 护士长审阅
     *
     * @param pgxh  评估序号
     * @param hszgh 护士长
     * @param jgid  机构id
     * @return
     */
    public Response<RiskRecord> tocheckRisk(String pgxh, String hszgh, String jgid) {
        Response<RiskRecord> response = new Response<RiskRecord>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/checkDERecord?")
                .append("pgxh=").append(pgxh).append("&hszgh=").append(hszgh)
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
                        new TypeReference<Response<RiskRecord>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 添加一条风险评估措施，如果已经存在则返回记录
     *
     * @param pgdh 评估单号
     * @param pglx 评估类型
     * @param pgxh 评估序号
     * @param jlxh 记录序号
     * @param jgid 机构id
     * @return
     */
    public Response<RiskMeasure> toaddRiskMeasure(String pgdh, String pglx,
                                                  String pgxh, String jlxh, String jgid) {
        Response<RiskMeasure> response = new Response<RiskMeasure>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/addDEMeasure?")
                .append("pgdh=").append(pgdh).append("&pglx=").append(pglx)
                .append("&pgxh=").append(pgxh).append("&jlxh=").append(jlxh)
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
                        new TypeReference<Response<RiskMeasure>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 保存风险措施
     *
     * @param data JSON格式的保存数据，包含MeasureRecord,PGDH,ZYH,BQID,JGID
     * @return
     */
    public Response<RiskMeasure> tosaveRiskMeasure(String data) {
        Response<RiskMeasure> response = new Response<RiskMeasure>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("post/saveDEMeasure")
                .toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri, data);
        if (!isRequestSuccess(httpString.first)) {
            response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
            return response;
        }
        String entity = httpString.second;
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<RiskMeasure>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 风险措施评价
     *
     * @param jlxh 记录序号
     * @param pjsj 评估时间
     * @param pjjg （评论序号）
     * @param pjr  评估人
     * @param pgdh 评估单号
     * @param pgxh 评估序号
     * @param jgid 机构id
     * @return
     */
    public Response<RiskMeasure> toevaluteRiskMeasure(String jlxh, String pjsj,
                                                      String pjjg, String pjr, String pgdh, String pgxh, String jgid) {
        Response<RiskMeasure> response = new Response<RiskMeasure>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/evaluateMeasure?")
                .append("jlxh=").append(jlxh).append("&pjsj=").append(pjsj)
                .append("&pjjg=").append(pjjg).append("&pjr=").append(pjr)
                .append("&pgdh=").append(pgdh).append("&pgxh=").append(pgxh)
                .append("&jgid=").append(jgid).toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        //
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
                        new TypeReference<Response<RiskMeasure>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    public Response<KeyValue<String,String>> getPreOneCSJL(String zyh, String csdh, String pgxh, String jgid) {
        Response<KeyValue<String,String>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getPreOneCSJL?")
                .append("zyh=").append(zyh).append("&csdh=").append(csdh).append("&pgxh=").append(pgxh)
                .append("&jgid=").append(jgid)
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
                        new TypeReference<Response<KeyValue<String,String>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 删除措施评价
     *
     * @param jlxh 记录序号
     * @param jgid 机构id
     * @return
     */
    public Response<String> todeleteRiskMeasure(String jlxh, String jgid) {
        Response<String> response = new Response<String>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/deleteDEMeasure?")
                .append("jlxh=").append(jlxh).append("&jgid=").append(jgid)
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

    /**
     * 获取评估措施列表
     *
     * @param pgdh 评估单号
     * @param pgxh 评估序号
     * @param zyh  住院号
     * @param jgid 机构id
     * @return
     */
    public Response<List<MeasureOverview>> togetMeasureList(String pgdh,
                                                            String pgxh, String zyh, String jgid) {
        Response<List<MeasureOverview>> response = new Response<List<MeasureOverview>>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getDEMeasureList?")
                .append("pgdh=").append(pgdh).append("&pgxh=").append(pgxh)
                .append("&zyh=").append(zyh).append("&jgid=").append(jgid).toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
        if (!isRequestSuccess(httpString.first)) {
            response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
            return response;
        }
        String entity = httpString.second;
//        Log.e("12获取评估措施列表", "togetMeasureList: "+uri );
//        Log.e("12获取评估措施列表", "togetMeasureList: "+entity );
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<List<MeasureOverview>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    public Response<List<RiskEvaluate>> toGetEvaluateist(String csdh, String jgid) {
        Response<List<RiskEvaluate>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getCSPJList?")
                .append("csdh=").append(csdh).append("&jgid=").append(jgid).toString();
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
                        new TypeReference<Response<List<RiskEvaluate>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 获取当前的疼痛综合评估记录，没有时则新增
     *
     * @param jgid 机构id
     * @param pgxh 评估序号
     * @return
     */
    public Response<List<PainEvaluate>> getRiskPain(String jgid, String pgxh) {
        Response<List<PainEvaluate>> response = new Response<List<PainEvaluate>>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/addOrGetPE?")
                .append("jgid=").append(jgid).append("&pgxh=").append(pgxh)
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
                        new TypeReference<Response<List<PainEvaluate>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 保存疼痛综合评估记录
     *
     * @param data JSON格式的保存数据，包含PEOption,ZYH,JGID,PGXH
     * @return
     */
    public Response<List<PainEvaluate>> saveRiskPain(String data) {
        Response<List<PainEvaluate>> response = new Response<List<PainEvaluate>>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("post/savePE").toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri, data);
        if (!isRequestSuccess(httpString.first)) {
            response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
            return response;
        }
        String entity = httpString.second;
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<List<PainEvaluate>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 调用表单后的第二次同步
     *
     * @param data
     * @return
     */
    public Response<String> synchronRepeat(String data) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("post/synchronRepeat").toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri, data);
        if (!isRequestSuccess(httpString.first)) {
            response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
            return response;
        }
        String entity = httpString.second;
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                // 去除转义符
                entity = StringEscapeUtils.unescapeJson(entity);
                // 去除双引号
                entity = entity.substring(1, entity.length() - 1);
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
