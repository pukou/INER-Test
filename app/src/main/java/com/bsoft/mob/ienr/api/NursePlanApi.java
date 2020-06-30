package com.bsoft.mob.ienr.api;

import android.content.Context;
import android.util.Log;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.nurseplan.*;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Description: 护理计划api
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2016-11-21
 * Time: 15:40
 * Version:
 */

public class NursePlanApi extends BaseApi {

    public String url;

    /**
     * <p>
     * Title: NursePlanApi
     * </p>
     * <p>
     * Description: 构造函数
     * </p>
     *
     * @param httpClient
     * @param mContext
     */
    public NursePlanApi(AppHttpClient httpClient, Context mContext) {
        super(httpClient, mContext);
    }

    public NursePlanApi(AppHttpClient httpClient, Context mContext, String url) {
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
    public static NursePlanApi getInstance(Context context)
            throws IllegalStateException {
        NursePlanApi api = (NursePlanApi) context
                .getSystemService("com.bsoft.mob.ienr.api.NursePlanApi");
        if (api == null)
            api = (NursePlanApi) context.getApplicationContext()
                    .getSystemService("com.bsoft.mob.ienr.api.NursePlanApi");
        if (api == null)
            throw new IllegalStateException("api not available");
        return api;
    }

    /**
     * @param @param  bqdm
     * @param @param  zyh
     * @param @param  jgid
     * @param @return
     * @return Response<List<Plan>>
     * @throws
     * @Title: getPlanList
     * @Description: 获取计划问题列表
     */
    public Response<List<Plan>> getPlanList(String zyh, String bqdm, String jgid) {
        Response<List<Plan>> response = new Response<List<Plan>>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getPlanList?")
                .append("zyh=").append(zyh)
                .append("&bqid=").append(bqdm)
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
                        new TypeReference<Response<List<Plan>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * @param @param  bqdm
     * @param @param  zyh
     * @param @param  jgid
     * @param @return
     * @return Response<List<Plan>>
     * @throws
     * @Title: getFocusList
     * @Description: 获取焦点问题列表
     */
    public Response<List<Plan>> getFocusList(String zyh, String bqdm, String jgid) {
        Response<List<Plan>> response = new Response<List<Plan>>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getFocusList?")
                .append("zyh=").append(zyh)
                .append("&bqid=").append(bqdm)
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
                        new TypeReference<Response<List<Plan>>>() {
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
     * @return
     */
    public Response<List<FocusRelevanceGroupBean>> getFocusRelevanceGroupList(String zyh,String bqdm,String jgid,boolean isqueryedited) {
        Response<List<FocusRelevanceGroupBean>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getFocusRelevanceGroupList?")
                .append("zyh=").append(zyh)
                .append("&bqdm=").append(bqdm)
                .append("&jgid=").append(jgid)
                .append("&isqueryedited=").append(isqueryedited)
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
                        new TypeReference<Response<List<FocusRelevanceGroupBean>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    @Deprecated
    public Response<List<JD_GL_TZXM_Bean>> getJD_GL_TZXM_List(String bqdm, String jgid) {
        Response<List<JD_GL_TZXM_Bean>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getJD_GL_TZXM_List?")
               /* .append("zyh=").append(zyh)*/
              //  .append("&bqid=").append(bqdm)
                .append("bqid=").append(bqdm)
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
                        new TypeReference<Response<List<JD_GL_TZXM_Bean>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * @param @param  wtxh 问题序号
     * @param @param  jgid 机构id
     * @param @return
     * @return Response<Problem>
     * @throws
     * @Title: addPlanProblem
     * @Description: 添加护理问题记录
     */
    public Response<Problem> addPlanProblem(String wtxh, String jgid) {
        Response<Problem> response = new Response<Problem>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getNewProblem?")
                .append("wtxh=").append(wtxh)
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
                        new TypeReference<Response<Problem>>() {
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
     * @param @param  glxh 关联序号
     * @param @param  wtxh 问题序号
     * @param @param  jgid 机构id
     * @param @return
     * @return Response<Problem>
     * @throws
     * @Title: getPlanProblem
     * @Description: 获取问题记录
     */
    public Response<List<Problem>> getPlanProblemList(String zyh, String glxh,
                                                      String wtxh, String jgid) {
        Response<List<Problem>> response = new Response<List<Problem>>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getPlanProblemList?")
                .append("zyh=").append(zyh).append("&glxh=").append(glxh)
                .append("&wtxh=").append(wtxh).append("&jgid=").append(jgid)
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
                        new TypeReference<Response<List<Problem>>>() {
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
     * @param @param  glxh 关联序号
     * @param @param  wtxh 问题序号
     * @param @param  jgid 机构id
     * @param @return
     * @return Response<Problem>
     * @throws
     * @Title: getPlanProblem
     * @Description: 获取问题记录
     */
    public Response<List<Problem>> getFocusProblemList(String zyh, String glxh,
                                                       String wtxh, String jgid) {
        Response<List<Problem>> response = new Response<List<Problem>>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getFocusProblemList?")
                .append("zyh=").append(zyh).append("&glxh=").append(glxh)
                .append("&wtxh=").append(wtxh).append("&jgid=").append(jgid)
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
                        new TypeReference<Response<List<Problem>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * @param @param  data 要保存的数据
     * @param @return
     * @return Response<List<Problem>>
     * @Description: 保存问题
     */
    public Response<List<Problem>> saveNursePlanProblem(String data) {
        Response<List<Problem>> response = new Response<List<Problem>>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("post/saveNursePlanProblem").toString();

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
                        new TypeReference<Response<List<Problem>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * @param @param  data 要保存的数据
     * @param @return
     * @return Response<List<Problem>>
     * @Description: 保存问题
     */
    public Response<List<Problem>> saveNurseFocusProblem(String data) {
        Response<List<Problem>> response = new Response<List<Problem>>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("post/saveNurseFocusProblem").toString();

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
                        new TypeReference<Response<List<Problem>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    public Response<List<Problem>> terminateNursePlanProblem(String jlwt, String ygdm,
                                                             String zyh, String glxh, String wtxh, String jgid) {
        Response<List<Problem>> response = new Response<List<Problem>>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/terminateNursePlanProblem?")
                .append("jlwt=").append(jlwt).append("&yhid=").append(ygdm)
                .append("&zyh=").append(zyh).append("&glxh=").append(glxh)
                .append("&wtxh=").append(wtxh).append("&jgid=").append(jgid)
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
                        new TypeReference<Response<List<Problem>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    public Response<List<Problem>> terminateNurseFocusProblem(String jlwt, String ygdm,
                                                              String zyh, String glxh, String wtxh, String jgid) {
        Response<List<Problem>> response = new Response<List<Problem>>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/terminateNurseFocusProblem?")
                .append("jlwt=").append(jlwt).append("&yhid=").append(ygdm)
                .append("&zyh=").append(zyh).append("&glxh=").append(glxh)
                .append("&wtxh=").append(wtxh).append("&jgid=").append(jgid)
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
                        new TypeReference<Response<List<Problem>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * @param @param  jlwt 记录问题
     * @param @param  jgid 机构id
     * @param @return
     * @return Response<String>
     * @throws
     * @Title: deleteProblem
     * @Description: 删除问题
     */
    public Response<String> deleteNursePlanProblem(String jlwt, String jgid) {
        Response<String> response = new Response<String>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/deleteNursePlanProblem?")
                .append("&jlwt=").append(jlwt).append("&jgid=").append(jgid)
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
     * @param @param  jlwt 记录问题
     * @param @param  jgid 机构id
     * @param @return
     * @return Response<String>
     * @throws
     * @Title: deleteProblem
     * @Description: 删除问题
     */
    public Response<String> deleteNurseFocusProblem(String jlwt, String jgid) {
        Response<String> response = new Response<String>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/deleteNurseFocusProblem?")
                .append("&jlwt=").append(jlwt).append("&jgid=").append(jgid)
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
     * @param @param  jlwt 记录问题
     * @param @param  wtxh 问题序号
     * @param @param  jgid 机构id
     * @param @return
     * @return Response<EvaluateAndRecord>
     * @throws
     * @Title: getEvaluateList
     * @Description: 获取评价列表(记录及评价项目)
     */
    public Response<EvaluateAndRecord> getPlanEvaluateList(String jlwt,
                                                           String wtxh, String jgid) {
        Response<EvaluateAndRecord> response = new Response<EvaluateAndRecord>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getPlanEvaluateList?")
                .append("jlwt=").append(jlwt).append("&wtxh=").append(wtxh)
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
                        new TypeReference<Response<EvaluateAndRecord>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * @param @param  jlwt 记录问题
     * @param @param  wtxh 问题序号
     * @param @param  jgid 机构id
     * @param @return
     * @return Response<EvaluateAndRecord>
     * @throws
     * @Title: getEvaluateList
     * @Description: 获取评价列表(记录及评价项目)
     */
    public Response<EvaluateAndRecord> getFocusEvaluateList(String jlwt,
                                                            String wtxh, String jgid) {
        Response<EvaluateAndRecord> response = new Response<EvaluateAndRecord>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getFocusEvaluateList?")
                .append("jlwt=").append(jlwt).append("&wtxh=").append(wtxh)
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
                        new TypeReference<Response<EvaluateAndRecord>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * @param @param  data 要保存的数据
     * @param @return
     * @return Response<EvaluateAndRecord>
     * @throws
     * @Title: EvaluteProblem
     * @Description: 保存问题评价
     */
    public Response<EvaluateAndRecord> savePlanProblemEvaluate(String data) {
        Response<EvaluateAndRecord> response = new Response<EvaluateAndRecord>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("post/savePlanProblemEvaluate").toString();
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
                        new TypeReference<Response<EvaluateAndRecord>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * @param @param  data 要保存的数据
     * @param @return
     * @return Response<EvaluateAndRecord>
     * @throws
     * @Title: EvaluteProblem
     * @Description: 保存问题评价
     */
    public Response<EvaluateAndRecord> saveFocusProblemEvaluate(String data) {
        Response<EvaluateAndRecord> response = new Response<EvaluateAndRecord>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("post/saveFocusProblemEvaluate").toString();
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
                        new TypeReference<Response<EvaluateAndRecord>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * @param @param  jlwt 记录问题
     * @param @param  jlpj 记录评价
     * @param @param  jgid 机构id
     * @param @return
     * @return Response<String>
     * @throws
     * @Title: deleteEvaluate
     * @Description: 删除问题评价
     */
    public Response<String> deletePlanProblemEvaluate(String jlwt, String jlpj, String jgid) {
        Response<String> response = new Response<String>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/deletePlanProblemEvaluate?")
                .append("jlwt=").append(jlwt).append("&jlpj=").append(jlpj)
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

    /**
     * @param @param  jlwt 记录问题
     * @param @param  jlpj 记录评价
     * @param @param  jgid 机构id
     * @param @return
     * @return Response<String>
     * @throws
     * @Title: deleteEvaluate
     * @Description: 删除问题评价
     */
    public Response<String> deleteFocusProblemEvaluate(String jlwt, String jlpj, String jgid) {
        Response<String> response = new Response<String>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/deleteFocusProblemEvaluate?")
                .append("jlwt=").append(jlwt).append("&jlpj=").append(jlpj)
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
