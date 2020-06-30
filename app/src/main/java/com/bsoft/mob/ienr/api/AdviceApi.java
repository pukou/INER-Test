package com.bsoft.mob.ienr.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.advice.*;
import com.bsoft.mob.ienr.model.kernel.SickPersonVo;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;
import com.fasterxml.jackson.core.type.TypeReference;

import org.dom4j.DocumentException;

import android.content.Context;
import android.util.Log;

import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.Statue;
import com.bsoft.mob.ienr.model.advice.execut.ExecutVo;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午2:47:35
 * @类说明 医嘱接口
 */
public class AdviceApi extends BaseApi {

	public String url;

	public AdviceApi(AppHttpClient httpClient, Context mContext) {
		super(httpClient, mContext);
	}

	public AdviceApi(AppHttpClient httpClient, Context mContext, String url) {
		super(httpClient, mContext);
		this.url = url;
	}

	public static AdviceApi getInstance(Context mContext) {
		// Context localContext = AppContext.getContext();
		AdviceApi api = (AdviceApi) mContext
				.getSystemService("com.bsoft.mob.ienr.api.AdviceApi");
		if (api == null) api = (AdviceApi) mContext.getApplicationContext()
				.getSystemService("com.bsoft.mob.ienr.api.AdviceApi");
		if (api == null) throw new IllegalStateException("api not available");
		return api;
	}

	/**
	 * 计划列表
	 *
	 * @param zyh
	 * @param day
	 * @param gslx 3为口服药 4 输液 5注射
	 * @param jgid
	 * @return
	 */
	public Response<AdvicePlanData> getPlanList(String zyh, String day, String gslx,
			String jgid) {
		Response<AdvicePlanData> response = new Response<>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";
		String uri = new StringBuffer(url).append("get/GetPlanList?zyh=").append(zyh)
				.append("&today=").append(day).append("&gslx=").append(gslx).append("&jgid=")
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
				response = JsonUtil
						.fromJson(entity, new TypeReference<Response<AdvicePlanData>>() {
						});
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败：解析错误";
			}
		}
		return response;
	}

	/**
	 * 医嘱列表
	 *
	 * @param zyh   住院号
	 * @param lsyz  历史医嘱
	 * @param wxbz  无效标志
	 * @param start
	 * @param end
	 * @param jgid
	 * @return
	 */
	public Response<AdviceData> GetAdviceList(String zyh, int lsyz, int wxbz, String start,
			String end, String jgid) {
		Response<AdviceData> response = new Response<>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";
		String uri = new StringBuffer(url).append("get/getAdviceList?zyh=").append(zyh)
				.append("&lsyz=").append(lsyz).append("&wxbz=").append(wxbz).append("&kssj=")
				.append(start).append("&jssj=").append(end).append("&jgid=").append(jgid)
				.toString();
		if (Constant.LOG_URI) {
			Log.d(Constant.TAG, uri);
		}
		HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
		if (!isRequestSuccess(httpString.first)) {
			response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
			return response;
		}
		String xml = httpString.second;
		if (null != xml && xml.length() > 2) {
			try {
				Response<ArrayList<AdviceVo>> tempResponse = JsonUtil
						.fromJson(xml, new TypeReference<Response<ArrayList<AdviceVo>>>() {
						});
				if (tempResponse.ReType == 0) {
					AdviceData adviceData = new AdviceData();
					adviceData.AdviceVoList = tempResponse.Data;
					response.ReType = 0;
					response.Data = adviceData;
				}

			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败：解析错误";
			}
		}
		return response;
	}

	/**
	 * 获取病人输液列表
	 *
	 * @param zyh
	 * @param day
	 * @param syzt 2为巡视，empty为所有输液单
	 * @param jgid
	 * @return
	 */
	public Response<TransfusionData> GetTransfusionListPatient(String zyh, String day,
			String syzt, String jgid) {
		Response<TransfusionData> response = new Response<>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";
		String uri = new StringBuffer(url).append("get/getTransfusionListPatient?zyh=")
				.append(zyh).append("&ksrq=").append(day).append("&syzt=").append(syzt)
				.append("&jgid=").append(jgid).toString();
		if (Constant.LOG_URI) {
			Log.d(Constant.TAG, uri);
		}
		HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
		if (!isRequestSuccess(httpString.first)) {
			response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
			return response;
		}
		String xml = httpString.second;
		if (null != xml && xml.length() > 0) {
			try {
				response = JsonUtil
						.fromJson(xml, new TypeReference<Response<TransfusionData>>() {
						});
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败：解析错误";
			}
		}
		return response;
	}

	/**
	 * 医嘱执行
	 *
	 * @return
	 * @throws DocumentException
	 */
	@Deprecated
	public ExecutVo HandExecut(String data) {
		String uri = new StringBuffer(url).append("post/HandExecut").toString();
		if (Constant.LOG_URI) {
			Log.d(Constant.TAG, uri);
		}
		HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,data);
		if (!isRequestSuccess(httpString.first)) {
			ExecutVo executVo = new ExecutVo(Statue.ERROR);
			executVo.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
			return executVo;
		}
		String xml = httpString.second;
		if (null != xml && xml.length() > 0) {
			return parser.parserExecutTable(xml);
		} else {
			// 网络失败
			return new ExecutVo(Statue.NET_ERROR);
		}
	}

	public ExecutVo HandExecutNew(String data) {
		String uri = new StringBuffer(url).append("post/HandExecutNew").toString();
		if (Constant.LOG_URI) {
			Log.d(Constant.TAG, uri);
		}
		HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,data);
		if (!isRequestSuccess(httpString.first)) {
			ExecutVo executVo = new ExecutVo(Statue.ERROR);
			executVo.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
			return executVo;
		}
		String xml = httpString.second;
		if (null != xml && xml.length() > 0) {
			return parser.parserExecutTable(xml);
		} else {
			// 网络失败
			return new ExecutVo(Statue.NET_ERROR);
		}
	}
	/*
	升级编号【56010053】============================================= start
	多瓶超过2瓶转接瓶后提示选择接哪瓶的问题
	================= Classichu 2017/11/14 16:12
	*/
	public Response<List<PlanAndTransfusion>> getTransfusionInfoListByZyh4TransfuseExecut(String zyh,String jgid,String syrq) {
		Response<List<PlanAndTransfusion>> response = new Response<>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";
		String uri = new StringBuffer(url).append("get/getTransfusionInfoListByZyh4TransfuseExecut?zyh=")
				.append(zyh).append("&jgid=").append(jgid).append("&syrq=").append(syrq).toString();
		if (Constant.LOG_URI) {
			Log.d(Constant.TAG, uri);
		}
		HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
		if (!isRequestSuccess(httpString.first)) {
			response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
			return response;
		}
		String xml = httpString.second;
		if (null != xml && xml.length() > 0) {
			try {
				response = JsonUtil
						.fromJson(xml, new TypeReference<Response<List<PlanAndTransfusion>>>() {
						});
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败：解析错误";
			}
		}
		return response;
	}

	public Response<List<PlanAndTransfusion>> getTransfusionInfoListByZyh4TransfuseExecutAll(String zyh,String jgid,String syrq) {
		Response<List<PlanAndTransfusion>> response = new Response<>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";
		String uri = new StringBuffer(url).append("get/getTransfusionInfoListByZyh4TransfuseExecutAll?zyh=")
				.append(zyh).append("&jgid=").append(jgid).append("&syrq=").append(syrq).toString();
		if (Constant.LOG_URI) {
			Log.d(Constant.TAG, uri);
		}
		HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
		if (!isRequestSuccess(httpString.first)) {
			response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
			return response;
		}
		String xml = httpString.second;
		if (null != xml && xml.length() > 0) {
			try {
				response = JsonUtil
						.fromJson(xml, new TypeReference<Response<List<PlanAndTransfusion>>>() {
						});
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败：解析错误";
			}
		}
		return response;
	}
	public Response<PlanAndTransfusion> getTransfusionInfoByBarcode4TransfuseExecut(String barcode,String prefix,String jgid) {
		Response<PlanAndTransfusion> response = new Response<>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";
		String uri = new StringBuffer(url).append("get/getTransfusionInfoByBarcode4TransfuseExecut?barcode=")
				.append(barcode).append("&jgid=").append(jgid).append("&prefix=").append(prefix).toString();
		if (Constant.LOG_URI) {
			Log.d(Constant.TAG, uri);
		}
		HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
		if (!isRequestSuccess(httpString.first)) {
			response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
			return response;
		}
		String xml = httpString.second;
		if (null != xml && xml.length() > 0) {
			try {
				response = JsonUtil
						.fromJson(xml, new TypeReference<Response<PlanAndTransfusion>>() {
						});
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败：解析错误";
			}
		}
		return response;
	}
	/* =============================================================== end */
	/**
	 * 扫描执行
	 *
	 * @param data
	 * @return
	 */
	@Deprecated
	public ExecutVo ScanExecut(String data) {
		String xml = null;
		if (Constant.DEBUG_LOCAL) {
			return null;
		} else {
			String uri = new StringBuffer(url).append("post/ScanExecut").toString();
			if (Constant.LOG_URI) {
				Log.d(Constant.TAG, uri);
			}
			HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,data);
			if (!isRequestSuccess(httpString.first)) {
				ExecutVo executVo = new ExecutVo(Statue.ERROR);
				executVo.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
				return executVo;
			}
			 xml = httpString.second;
		}
		if (null != xml && xml.length() > 0) {
			return parser.parserExecutTable(xml);
		} else {
			// 网络失败
			return new ExecutVo(Statue.NET_ERROR);
		}
	}

	public ExecutVo ScanExecutNew(String data) {
		String xml = null;
		if (Constant.DEBUG_LOCAL) {
			return null;
		} else {
			String uri = new StringBuffer(url).append("post/ScanExecutNew").toString();
			if (Constant.LOG_URI) {
				Log.d(Constant.TAG, uri);
			}
			HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,data);
			if (!isRequestSuccess(httpString.first)) {
				ExecutVo executVo = new ExecutVo(Statue.ERROR);
				executVo.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
				return executVo;
			}
			xml = httpString.second;
		}
		if (null != xml && xml.length() > 0) {
			return parser.parserExecutTable(xml);
		} else {
			// 网络失败
			return new ExecutVo(Statue.NET_ERROR);
		}
	}

	public ExecutVo FJXHReadDoSync(String data) {
		String xml = null;
		if (Constant.DEBUG_LOCAL) {
			return null;
		} else {
			String uri = new StringBuffer(url).append("post/FJXH_RealDoSync").toString();
			if (Constant.LOG_URI) {
				Log.d(Constant.TAG, uri);
			}
			HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,data);
			if (!isRequestSuccess(httpString.first)) {
				ExecutVo executVo = new ExecutVo(Statue.ERROR);
				executVo.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
				return executVo;
			}
			xml = httpString.second;
		}
		if (null != xml && xml.length() > 0) {
			return parser.parserExecutTable(xml);
		} else {
			// 网络失败
			return new ExecutVo(Statue.NET_ERROR);
		}
	}

	/**
	 * 多包口服药执行
	 *
	 * @param data
	 * @return
	 */
	public ExecutVo TransfuseExecutCancelEnd2Ing(String data) {
		String xml = null;
		if (Constant.DEBUG_LOCAL) {
			return null;
		} else {
			String uri = new StringBuffer(url).append("post/TransfuseExecutCancelEnd2Ing").toString();
			if (Constant.LOG_URI) {
				Log.d(Constant.TAG, uri);
			}
			HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,data);
			if (!isRequestSuccess(httpString.first)) {
				ExecutVo executVo = new ExecutVo(Statue.ERROR);
				executVo.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
				return executVo;
			}
			 xml = httpString.second;
		}
		if (null != xml && xml.length() > 0) {
			return parser.parserExecutTable(xml);
		} else {
			// 网络失败
			return new ExecutVo(Statue.NET_ERROR);
		}
	}

	public ExecutVo TransfuseExecutCancelEnd2Start(String data) {
		String xml = null;
		if (Constant.DEBUG_LOCAL) {
			return null;
		} else {
			String uri = new StringBuffer(url).append("post/TransfuseExecutCancelEnd2Start").toString();
			if (Constant.LOG_URI) {
				Log.d(Constant.TAG, uri);
			}
			HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,data);
			if (!isRequestSuccess(httpString.first)) {
				ExecutVo executVo = new ExecutVo(Statue.ERROR);
				executVo.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
				return executVo;
			}
			xml = httpString.second;
		}
		if (null != xml && xml.length() > 0) {
			return parser.parserExecutTable(xml);
		} else {
			// 网络失败
			return new ExecutVo(Statue.NET_ERROR);
		}
	}
	public ExecutVo OralMedicationExecut(String data) {
		String xml = null;
		if (Constant.DEBUG_LOCAL) {
			return null;
		} else {
			String uri = new StringBuffer(url).append("post/OralMedicationExecut").toString();
			if (Constant.LOG_URI) {
				Log.d(Constant.TAG, uri);
			}
			HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,data);
			if (!isRequestSuccess(httpString.first)) {
				ExecutVo executVo = new ExecutVo(Statue.ERROR);
				executVo.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
				return executVo;
			}
			xml = httpString.second;
		}
		if (null != xml && xml.length() > 0) {
			return parser.parserExecutTable(xml);
		} else {
			// 网络失败
			return new ExecutVo(Statue.NET_ERROR);
		}
	}

	public ExecutVo KFExecutCancelEnd2Start(String data) {
		String xml = null;
		if (Constant.DEBUG_LOCAL) {
			return null;
		} else {
			String uri = new StringBuffer(url).append("post/KFExecutCancelEnd2Start").toString();
			if (Constant.LOG_URI) {
				Log.d(Constant.TAG, uri);
			}
			HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,data);
			if (!isRequestSuccess(httpString.first)) {
				ExecutVo executVo = new ExecutVo(Statue.ERROR);
				executVo.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
				return executVo;
			}
			xml = httpString.second;
		}
		if (null != xml && xml.length() > 0) {
			return parser.parserExecutTable(xml);
		} else {
			// 网络失败
			return new ExecutVo(Statue.NET_ERROR);
		}
	}
	public ExecutVo ZSExecutCancelEnd2Start(String data) {
		String xml = null;
		if (Constant.DEBUG_LOCAL) {
			return null;
		} else {
			String uri = new StringBuffer(url).append("post/ZSExecutCancelEnd2Start").toString();
			if (Constant.LOG_URI) {
				Log.d(Constant.TAG, uri);
			}
			HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,data);
			if (!isRequestSuccess(httpString.first)) {
				ExecutVo executVo = new ExecutVo(Statue.ERROR);
				executVo.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
				return executVo;
			}
			xml = httpString.second;
		}
		if (null != xml && xml.length() > 0) {
			return parser.parserExecutTable(xml);
		} else {
			// 网络失败
			return new ExecutVo(Statue.NET_ERROR);
		}
	}
	/**
	 * 拒绝执行
	 *
	 * @param data
	 * @return
	 */
	public ExecutVo RefuseExecut(String data) {
		String xml = null;
		if (Constant.DEBUG_LOCAL) {
			return null;
		} else {
			String uri = new StringBuffer(url).append("post/RefuseExecut").toString();
			if (Constant.LOG_URI) {
				Log.d(Constant.TAG, uri);
			}
			HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,data);
			if (!isRequestSuccess(httpString.first)) {
				ExecutVo executVo = new ExecutVo(Statue.ERROR);
				executVo.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
				return executVo;
			}
			 xml = httpString.second;
		}
		if (null != xml && xml.length() > 0) {
			return parser.parserExecutTable(xml);
		} else {
			// 网络失败
			return new ExecutVo(Statue.NET_ERROR);
		}
	}

	/**
	 * 输液滴速记录
	 *
	 * @param data
	 * @return
	 */
	public ExecutVo DropSpeedInput(String data) {
		String uri = new StringBuffer(url).append("post/DropSpeedInput").toString();
		if (Constant.LOG_URI) {
			Log.d(Constant.TAG, uri);
		}
		HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,data);
		if (!isRequestSuccess(httpString.first)) {
			ExecutVo executVo = new ExecutVo(Statue.ERROR);
			executVo.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
			return executVo;
		}
		String xml = httpString.second;
		if (null != xml && xml.length() > 0) {
			return parser.parserExecutTable(xml);
		} else {
			// 网络失败
			return new ExecutVo(Statue.NET_ERROR);
		}
	}

	/**
	 * 继续输液
	 *
	 * @param data
	 * @return
	 */
	public ExecutVo TransfuseContinue(String data) {
		String uri = new StringBuffer(url).append("post/TransfuseContinue").toString();
		if (Constant.LOG_URI) {
			Log.d(Constant.TAG, uri);
		}
		HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,data);
		if (!isRequestSuccess(httpString.first)) {
			ExecutVo executVo = new ExecutVo(Statue.ERROR);
			executVo.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
			return executVo;
		}
		String xml = httpString.second;

		if (null != xml && xml.length() > 0) {
			return parser.parserExecutTable(xml);
		} else {
			// 网络失败
			return new ExecutVo(Statue.NET_ERROR);
		}
	}

	/**
	 * 输液非常规执行(并行接瓶)
	 *
	 * @param zyh
	 * @param urid
	 * @param qrdh        确认单号
	 * @param qrdh_jp     接瓶 输液单号
	 * @param transfuseBX
	 * @param sysType
	 * @param jgid
	 * @return
	 */
	@Deprecated
	public ExecutVo TransfuseExecut(String zyh, String urid, String qrdh, String qrdh_jp,
			boolean transfuseBX, String jgid, int sysType) {
		String xml = null;
		if (Constant.DEBUG_LOCAL) {
			return null;
		} else {
			String uri = new StringBuffer(url).append("TransfuseExecut?zyh=").append(zyh)
					.append("&urid=").append(urid).append("&qrdh=").append(qrdh)
					.append("&qrdhjp=").append(qrdh_jp).append("&transfuseBX=")
					.append(transfuseBX).append("&jgid=").append(jgid).append("&sysType=")
					.append(sysType).toString();
			if (Constant.LOG_URI) {
				Log.d(Constant.TAG, uri);
			}
			HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
			if (!isRequestSuccess(httpString.first)) {
				ExecutVo executVo = new ExecutVo(Statue.ERROR);
				executVo.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
				return executVo;
			}
			 xml = httpString.second;
		}
		if (null != xml && xml.length() > 0) {
			return parser.parserExecutTable(xml);
		} else {
			// 网络失败
			return new ExecutVo(Statue.NET_ERROR);
		}
	}

	public ExecutVo TransfuseExecutNew(String zyh, String urid, String qrdh, String qrdh_jp,
									boolean transfuseBX, String jgid, int sysType) {
		String xml = null;
		if (Constant.DEBUG_LOCAL) {
			return null;
		} else {
			String uri = new StringBuffer(url).append("TransfuseExecutNew?zyh=").append(zyh)
					.append("&urid=").append(urid).append("&qrdh=").append(qrdh)
					.append("&qrdhjp=").append(qrdh_jp).append("&transfuseBX=")
					.append(transfuseBX).append("&jgid=").append(jgid).append("&sysType=")
					.append(sysType).toString();
			if (Constant.LOG_URI) {
				Log.d(Constant.TAG, uri);
			}
			HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
			if (!isRequestSuccess(httpString.first)) {
				ExecutVo executVo = new ExecutVo(Statue.ERROR);
				executVo.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
				return executVo;
			}
			xml = httpString.second;
		}
		if (null != xml && xml.length() > 0) {
			return parser.parserExecutTable(xml);
		} else {
			// 网络失败
			return new ExecutVo(Statue.NET_ERROR);
		}
	}



	/**
	 * 获取输液项详情
	 *
	 * @param sydh
	 * @param jgid
	 * @return
	 */
	public Response<TransfusionData> GetTransfusion(String sydh, String jgid) {

		Response<TransfusionData> response = new Response<>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";
		String uri = new StringBuffer(url).append("get/GetTransfusion?sydh=").append(sydh)
				.append("&jgid=").append(jgid).toString();

		if (Constant.LOG_URI) {
			Log.d(Constant.TAG, uri);
		}
		HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
		if (!isRequestSuccess(httpString.first)) {
			response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
			return response;
		}
		String xml = httpString.second;

		if (null != xml && xml.length() > 0) {
			try {
				response = JsonUtil
						.fromJson(xml, new TypeReference<Response<TransfusionData>>() {
						});
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败：解析错误";
			}
		}
		return response;
	}

	/**
	 * 获取医嘱执行详细
	 *
	 * @param jlxh
	 * @param jgid
	 * @return
	 */
	public Response<AdviceData> GetAdviceDetil(String jlxh, String jgid) {
		Response<AdviceData> response = new Response<>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";

		String uri = new StringBuffer(url).append("get/getAdviceDetail?jlxh=").append(jlxh)
				.append("&jgid=").append(jgid).toString();
		if (Constant.LOG_URI) {
			Log.d(Constant.TAG, uri);
		}
		HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
		if (!isRequestSuccess(httpString.first)) {
			response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
			return response;
		}
		String xml = httpString.second;
		if (null != xml && xml.length() > 2) {
			try {
				Response<ArrayList<AdviceDetail>> tempResponse = JsonUtil
						.fromJson(xml, new TypeReference<Response<ArrayList<AdviceDetail>>>() {
						});
				if (tempResponse.ReType == 0) {
					AdviceData adviceData = new AdviceData();
					adviceData.DetailList = tempResponse.Data;
					response.ReType = 0;
					response.Data = adviceData;
				}
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败：解析错误";
			}
		}
		return response;

	}

	/**
	 * 获取输液反应类型
	 *
	 * @param bqid
	 * @param jgid
	 * @return
	 */
	public Response<TransfusionData> GetTransfusionReaction(String bqid, String jgid) {

		Response<TransfusionData> response = new Response<>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";

		String uri = new StringBuffer(url).append("get/GetTransfusionReaction?bqid=")
				.append(bqid).append("&jgid=").append(jgid).toString();
		if (Constant.LOG_URI) {
			Log.d(Constant.TAG, uri);
		}
		HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
		if (!isRequestSuccess(httpString.first)) {
			response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
			return response;
		}
		String xml = httpString.second;

		if (null != xml && xml.length() > 0) {
			try {
				response = JsonUtil
						.fromJson(xml, new TypeReference<Response<TransfusionData>>() {
						});
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败：解析错误";
			}
		}
		return response;
	}

	/**
	 * 输液暂停
	 *
	 * @param data
	 * @return
	 */
	public ExecutVo TransfusePause(String data) {
		String uri = new StringBuffer(url).append("post/TransfuseStop").toString();
		if (Constant.LOG_URI) {
			Log.d(Constant.TAG, uri);
		}
		HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,data);
		if (!isRequestSuccess(httpString.first)) {
			ExecutVo executVo = new ExecutVo(Statue.ERROR);
			executVo.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
			return executVo;
		}
		String xml = httpString.second;

		if (null != xml && xml.length() > 0) {
			return parser.parserExecutTable(xml);
		} else {
			// 网络失败
			return new ExecutVo(Statue.NET_ERROR);
		}
	}
	public ExecutVo TransfuseEnd(String data) {
		String uri = new StringBuffer(url).append("post/TransfuseEnd").toString();
		if (Constant.LOG_URI) {
			Log.d(Constant.TAG, uri);
		}
		HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,data);
		if (!isRequestSuccess(httpString.first)) {
			ExecutVo executVo = new ExecutVo(Statue.ERROR);
			executVo.ExceptionMessage = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
			return executVo;
		}
		String xml = httpString.second;

		if (null != xml && xml.length() > 0) {
			return parser.parserExecutTable(xml);
		} else {
			// 网络失败
			return new ExecutVo(Statue.NET_ERROR);
		}
	}
	/**
	 * 获取我的病人列表
	 *
	 * @param bqid      病区ID
	 * @param filter    0病区病人;1体温单 ;2 医嘱;3口服单;4注射单;5 输液单;
	 * @param starttime GetTimePoints接口返回的int time ,不需要则传-1
	 * @param endtime
	 * @param hsgh      护士工号 当需要获取我的病人时，传入护士工号，否则传null
	 * @param jgid      机构ID
	 * @return
	 */
	public Response<ArrayList<SickPersonVo>> GetPatientList(String bqid, int filter,
			int starttime, int endtime, String hsgh, String jgid) {
		Response<ArrayList<SickPersonVo>> response = new Response<>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";
		String uri = new StringBuffer(url).append("get/GetPatientList?bqid=").append(bqid)
				.append("&type=").append(filter).append("&starttime=").append(starttime)
				.append("&endtime=").append(endtime).append("&hsgh=")
				.append(EmptyTool.isBlank(hsgh) ? "" : hsgh).append("&jgid=").append(jgid)
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
						new TypeReference<Response<ArrayList<SickPersonVo>>>() {
						});
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败：解析错误";
			}
		}
		return response;
	}
}
