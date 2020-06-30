package com.bsoft.mob.ienr.api;

import android.content.Context;
import android.util.Log;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.advicecheck.AdviceCheckList;
import com.bsoft.mob.ienr.model.advicecheck.CheckDetail;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.Locale;

public class AdviceCheckApi extends BaseApi {
    public String url;

    public AdviceCheckApi(AppHttpClient httpClient, Context mContext) {
        super(httpClient, mContext);
    }

    public AdviceCheckApi(AppHttpClient httpClient, Context mContext, String url) {
        super(httpClient, mContext);
        this.url = url;
    }

    public static AdviceCheckApi getInstance(Context mContext) {
        AdviceCheckApi api = (AdviceCheckApi) mContext
                .getSystemService("com.bsoft.mob.ienr.api.AdviceCheckApi");
        if (api == null)
            api = (AdviceCheckApi) mContext.getApplicationContext()
                    .getSystemService("com.bsoft.mob.ienr.api.AdviceCheckApi");
        if (api == null)
            throw new IllegalStateException("api not available");
        return api;
    }

    /**
     * @param bqdm
     * @param jhrq
     * @param gslx   -1：全部，4：输液，5：注射
     * @param status 0:未核对，1：已核对
     * @param type   1:摆药；2：加药
     * @param jgid
     * @return
     */
    public Response<AdviceCheckList> getAdviceFrom(String bqdm, String jhrq, String gslx,
                                                   String status, String type, String jgid) {
        Response<AdviceCheckList> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url)
                .append("get/GetDosingCheckList?bqdm=").append(bqdm)
                .append("&syrq=").append(jhrq).append("&gslx=")
                .append(gslx).append("&type=").append(type)
                .append("&status=").append(status).append("&jgid=")
                .append(jgid).toString();
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
                        new TypeReference<Response<AdviceCheckList>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;

    }

    public Response<CheckDetail> getFormDetail(String sydh, String gslx, String userId,
                                               String type, String jgid) {
        Response<CheckDetail> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url)
                .append("get/HandExecuteDoskingCheck?sydh=").append(sydh)
                .append("&gslx=").append(gslx).append("&userId=")
                .append(userId).append("&isCheck=").append("false")
                .append("&type=").append(type).append("&jgid=")
                .append(jgid).toString();
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
                        new TypeReference<Response<CheckDetail>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;

    }


    public Response<CheckDetail> HandExecute(String sydh, String gslx, String userId,
                                             String type, String jgid) {
        Response<CheckDetail> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url)
                .append("get/HandExecuteDoskingCheck?sydh=").append(sydh)
                .append("&gslx=").append(gslx).append("&userId=")
                .append(userId).append("&isCheck=").append("true")
                .append("&type=").append(type).append("&jgid=")
                .append(jgid).toString();
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
                        new TypeReference<Response<CheckDetail>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;

    }

    public Response<CheckDetail> scanExecute(String tmbh, String prefix, String userId,
                                             String type, String jgid) {
        Response<CheckDetail> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url)
                .append("get/ScanExecuteDoskingCheck?tmbh=").append(tmbh)
                .append("&prefix=").append(prefix).append("&userId=")
                .append(userId).append("&isCheck=").append("true")
                .append("&type=").append(type).append("&jgid=")
                .append(jgid).toString();
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
                        new TypeReference<Response<CheckDetail>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;

    }
}
