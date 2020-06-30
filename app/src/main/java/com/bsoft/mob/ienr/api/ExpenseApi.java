package com.bsoft.mob.ienr.api;

import android.content.Context;
import android.util.Log;
import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.expense.ExpenseRespose;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.Locale;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-25 上午11:16:54
 * @类说明 费用
 */
public class ExpenseApi extends BaseApi {

	public String url;

	public ExpenseApi(AppHttpClient httpClient, Context mContext) {
		super(httpClient, mContext);
	}

	public ExpenseApi(AppHttpClient httpClient, Context mContext, String url) {
		super(httpClient, mContext);
		this.url = url;
	}

	public static ExpenseApi getInstance(Context localContext) {

		// Context localContext = AppContext.getContext();
		ExpenseApi api = (ExpenseApi) localContext
				.getSystemService("com.bsoft.mob.ienr.api.expense");
		if (api == null)
			api = (ExpenseApi) localContext.getApplicationContext()
					.getSystemService("com.bsoft.mob.ienr.api.expense");
		if (api == null)
			throw new IllegalStateException("api not available");
		return api;
	}

	/**
	 * 获取费用
	 *
	 * @param zyh
	 * @param start
	 * @param end
	 * @return
	 */
	public Response<ExpenseRespose> GetCharge(String zyh, String start, String end,
			String jgid, int sysType) {
		Response<ExpenseRespose> response = new Response<ExpenseRespose>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";
		String uri = new StringBuffer(url).append("GetCharge?zyh=")
				.append(zyh).append("&start=").append(start)
				.append("&end=").append(end).append("&jgid=").append(jgid)
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
						new TypeReference<Response<ExpenseRespose>>() {
						});
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败：解析错误";
			}
		}
		return response;
	}



	/**
	 * 按天获取到的项目明细
	 *
	 * @param zyh
	 * @param start
	 * @param end
	 * @return
	 */
	public Response<ExpenseRespose> GetDetailOneDay(String zyh, String start, String end,
			String jgid, int sysType) {
		Response<ExpenseRespose> response = new Response<ExpenseRespose>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";
		String uri = new StringBuffer(url).append("GetDetailOneDay?zyh=")
					.append(zyh).append("&start=").append(start)
					.append("&end=").append(end).append("&jgid=").append(jgid)
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
						new TypeReference<Response<ExpenseRespose>>() {
						});
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败："+e.getMessage();
			}
		}
		return response;
	}

}
