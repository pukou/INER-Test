package com.bsoft.mob.ienr.api;

import android.content.Context;
import android.util.Log;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.UpdateInfo;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.Locale;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午2:47:35
 * @类说明 更新接口
 */
public class UpdateApi extends BaseApi {

    public String url;

    public UpdateApi(AppHttpClient httpClient, Context mContext) {
        super(httpClient, mContext);
    }

    public UpdateApi(AppHttpClient httpClient, Context mContext, String url) {
        super(httpClient, mContext);
        this.url = url;
    }

    public static UpdateApi getInstance(Context localContext) {
        // Context localContext = AppContext.getContext();
        UpdateApi api = (UpdateApi) localContext
                .getSystemService("com.bsoft.mob.ienr.api.UpdateApi");
        if (api == null)
            api = (UpdateApi) localContext.getApplicationContext()
                    .getSystemService("com.bsoft.mob.ienr.api.UpdateApi");
        if (api == null)
            throw new IllegalStateException("api not available");
        return api;
    }

    /**
     * 获取更新信息
     *
     * @param proName
     * @return
     */
    public Response<UpdateInfo> updateInfo(String proName) {

        Response<UpdateInfo> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("getVersionInfo?appname=")
                .append(proName).toString();
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
                        new TypeReference<Response<UpdateInfo>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }
}
