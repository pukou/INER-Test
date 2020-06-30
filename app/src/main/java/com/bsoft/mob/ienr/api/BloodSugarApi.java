package com.bsoft.mob.ienr.api;

import android.content.Context;
import android.util.Log;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.bloodsugar.BloodSugar;
import com.bsoft.mob.ienr.model.bloodsugar.PersonBloodSugar;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Locale;

/**
 * Description:
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2017-05-24
 * Time: 10:28
 * Version:
 */

public class BloodSugarApi extends BaseApi {
    public String url;

    public BloodSugarApi(AppHttpClient httpClient, Context mContext) {
        super(httpClient, mContext);
    }

    public BloodSugarApi(AppHttpClient httpClient, Context mContext,
                         String url) {
        super(httpClient, mContext);
        this.url = url;
    }

    public static BloodSugarApi getInstance(Context mContext) {
        BloodSugarApi api = (BloodSugarApi) mContext
                .getSystemService("com.bsoft.mob.ienr.api.BloodSugarApi");
        if (api == null)
            api = (BloodSugarApi) mContext
                    .getApplicationContext()
                    .getSystemService("com.bsoft.mob.ienr.api.BloodSugarApi");
        if (api == null)
            throw new IllegalStateException("api not available");
        return api;
    }

    /**
     * 获取测量时点列表数据
     *
     * @return
     */
    public Response<List<String>> GetClsdList() {
        Response<List<String>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getClsdList").toString();
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
                        new TypeReference<Response<List<String>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 获取血糖治疗列表数据
     *
     * @param zyh
     * @param start
     * @param end
     * @param jgid
     * @return
     */
    public Response<List<BloodSugar>> GetBloodSugarList(String zyh, String start, String end, String jgid) {
        Response<List<BloodSugar>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getBloodSugarList?zyh=").append(zyh)
                .append("&start=").append(start).append("&end=").append(end)
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
                        new TypeReference<Response<List<BloodSugar>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }
    public Response<List<PersonBloodSugar>> getNeedGetBloodSugarListArr(String brbq, String clsds,String jgid) {
        Response<List<PersonBloodSugar>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getNeedGetBloodSugarListArr?brbq=").append(brbq)
                .append("&clsds=").append(clsds)
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
                        new TypeReference<Response<List<PersonBloodSugar>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }


    public Response<List<PersonBloodSugar>> getNeedGetBloodSugarList(String brbq, String clsd,String jgid,String preClsdArrStr,String nowZyh) {
        Response<List<PersonBloodSugar>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/getNeedGetBloodSugarList?brbq=").append(brbq)
                .append("&clsd=").append(clsd)
                .append("&preClsdArrStr=").append(preClsdArrStr)
                .append("&nowZyh=").append(nowZyh)
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
                        new TypeReference<Response<List<PersonBloodSugar>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 添加血糖治疗数据
     *
     * @param zyh  住院号
     * @param sxbq 书写病区
     * @param brch 病人床号
     * @param sxsj 书写时间
     * @param sxgh 书写工号
     * @param cjgh 创建工号
     * @param clsd 测量时点
     * @param clz  测量值
     * @param jgid 机构id
     * @return
     */
    public Response<String> AddBloodSugar(String zyh, String sxbq, String brch, String sxsj, String sxgh,
                                       String cjgh, String clsd, String clz, String jgid) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/addBloodSugar?zyh=").append(zyh)
                .append("&sxbq=").append(sxbq).append("&brch=").append(brch)
                .append("&sxsj=").append(sxsj).append("&sxgh=").append(sxgh)
                .append("&cjgh=").append(cjgh)
                .append("&clsd=").append(clsd).append("&clz=").append(clz)
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
     * 修改血糖治疗数据
     *
     * @param jlxh
     * @param clsd
     * @param sxsj
     * @param clz
     * @return
     */
    public Response<String> EditBloodSugar(String jlxh, String clsd, String sxsj, String clz) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/editBloodSugar?jlxh=").append(jlxh)
                .append("&clsd=").append(clsd).append("&sxsj=").append(sxsj)
                .append("&clz=").append(clz).toString();
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
     * 删除血糖治疗数据
     *
     * @param jlxh
     * @return
     */
    public Response<String> DeleteBloodSugar(String jlxh) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/deleteBloodSugar?jlxh=").append(jlxh).toString();
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
