package com.bsoft.mob.ienr.api;

import android.content.Context;
import android.util.Log;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.dailywork.DailyWorkCount;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.Locale;

public class DailyWorkApi extends BaseApi {
    public String url;

    public DailyWorkApi(AppHttpClient httpClient, Context mContext) {
        super(httpClient, mContext);
    }

    public DailyWorkApi(AppHttpClient httpClient, Context mContext, String url) {
        super(httpClient, mContext);
        this.url = url;
    }

    public static DailyWorkApi getInstance(Context mContext) {
        DailyWorkApi api = (DailyWorkApi) mContext
                .getSystemService("com.bsoft.mob.ienr.api.DailyWorkApi");
        if (api == null)
            api = (DailyWorkApi) mContext.getApplicationContext()
                    .getSystemService("com.bsoft.mob.ienr.api.DailyWorkApi");
        if (api == null)
            throw new IllegalStateException("api not available");
        return api;
    }

	public Response<DailyWorkCount> getWorkList(String bqdm, String gzsj,String hsgh, String jgid,
			int sysType) {
		Response<DailyWorkCount> response = new Response<>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";
			String uri = new StringBuffer(url).append("get/GetMission?brbq=")
					.append(bqdm)
                    .append("&hsgh=").append(hsgh)
                    .append("&gzsj=").append(gzsj)
					.append("&jgid=").append(jgid)
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
                        new TypeReference<Response<DailyWorkCount>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }
}
