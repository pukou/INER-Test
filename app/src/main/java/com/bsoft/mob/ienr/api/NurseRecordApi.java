package com.bsoft.mob.ienr.api;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.model.nurserecord.*;

import android.content.Context;
import android.util.Log;

import com.bsoft.mob.ienr.dynamicui.nurserecord.UIView;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 病人巡视
 * 
 * @author hy
 * 
 */
public class NurseRecordApi extends BaseApi {

	public String url;

	public NurseRecordApi(AppHttpClient httpClient, Context mContext) {
		super(httpClient, mContext);
	}

	public NurseRecordApi(AppHttpClient httpClient, Context mContext, String url) {
		super(httpClient, mContext);
		this.url = url;
	}

	public static NurseRecordApi getInstance(Context mContext)
			throws IllegalStateException {
		// Context localContext = AppContext.getContext();
		NurseRecordApi api = (NurseRecordApi) mContext
				.getSystemService("com.bsoft.mob.ienr.api.NurseRecordApi");
		if (api == null)
			api = (NurseRecordApi) mContext.getApplicationContext()
					.getSystemService("com.bsoft.mob.ienr.api.NurseRecordApi");
		if (api == null)
			throw new IllegalStateException("api not available");
		return api;
	}

	// TODO: 2016/11/24 以下为修改的新接口
	/**
	 *
	 * 获取护理记录类别列表
	 * @param bqid
	 * @param jgid
	 * @param sysType
     * @return
     */
	public Response<List<Structure>> GetStructureClassificationList(String bqid, String jgid, int sysType) {

		Response<List<Structure>> response = new Response<List<Structure>>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";

		String uri = new StringBuffer(url)
				.append("get/lblb?bqid=")
				.append(bqid).append("&jgid=").append(jgid)
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
//		Log.e("获取护理记录类别列表", "toGetStructureClassificationList: "+entity );
		// 外面包含了双引号
		if (null != entity && entity.length() > 2) {
			try {
				response = JsonUtil.fromJson(entity,
						new TypeReference<Response<List<Structure>>>() {
						});
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败：解析错误";
			}
		}
		return response;
	}

	/**
	 * 获取护理结构列表
	 * @param jgId
	 * @param lbbh
	 * @param bqid
	 * @param sysType
     * @return
     */
	public Response<List<Template>> GetStructureList(String jgId, String lbbh,
													 String bqid, int sysType) {

		Response<List<Template>> response = new Response<List<Template>>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";

		String uri = new StringBuffer(url).append("get/jglb?jgid=")
				.append(jgId).append("&lbbh=").append(lbbh).append("&bqid=")
				.append(bqid).append("&sysType=").append(sysType).toString();
		if (Constant.LOG_URI) {
			Log.d(Constant.TAG, uri);

		}
		HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
		if (!isRequestSuccess(httpString.first)) {
			response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
			return response;
		}
		String entity = httpString.second;
//		Log.e("获取护理结构列表", "toGetStructureList: "+entity );
		// 外面包含了双引号
		if (null != entity && entity.length() > 2) {
			try {
				response = JsonUtil.fromJson(entity,
						new TypeReference<Response<List<Template>>>() {
						});
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败：解析错误";
			}
		}
		return response;
	}

	/**
	 * 根据记录编号获取护理记录控件列表
	 * @param zyh
	 * @param jlbh
	 * @param jgid
	 * @param sysType
     * @return
     */
	public Response<List<UIView>> GetCtrlListByJlbh(String zyh, String jlbh,
													String jgid, int sysType) {
		Response<List<UIView>> response = new Response<List<UIView>>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";
		String uri = new StringBuffer(url).append("get/controlls/jlbh?zyh=")
				.append(zyh).append("&jlbh=").append(jlbh).append("&jgid=")
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
//		Log.e("根据记录编号获取护理记录控件列表", "toGetCtrlListByJlbh: "+entity );
		// 外面包含了双引号
		if (null != entity && entity.length() > 2) {
			try {
				response = JsonUtil.fromJson(entity,
						new TypeReference<Response<List<UIView>>>() {
						});
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败：解析错误";
			}
		}
		return response;
	}

	/**
	 * 根据结构编号获取护理记录控件列表
	 * @param jgbh
	 * @param zyh
	 * @param jgid
	 * @param sysType
     * @return
     */
	public Response<List<UIView>> GetCtrlListByJgbh(String jgbh, String zyh,
																String jgid, int sysType) {

		Response<List<UIView>> response = new Response<List<UIView>>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";

		String uri = new StringBuffer(url).append("get/controlls/jgbh?jgbh=")
				.append(jgbh).append("&zyh=").append(zyh).append("&jgid=")
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
//		Log.e("根据结构编号获取护理记录控件列表", "toGetCtrlListByJgbh: "+entity);
		// 外面包含了双引号
		if (null != entity && entity.length() > 2) {
			try {
				response = JsonUtil.fromJson(entity,
						new TypeReference<Response<List<UIView>>>() {
						});
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败：解析错误";
			}
		}
		return response;
	}
	/*
升级编号【56010022】============================================= start
护理记录:可以查看项目最近3次的记录，可以选择其中一次的数据到当前的护理记录单上。
================= Classichu 2017/10/18 10:33
*/
	public Response<List<LastDataBean>> getlastXMData(String xmbh, String zyh,String hsgh,
													  String jgid, int sysType) {

		Response<List<LastDataBean>> response = new Response<>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";

		String uri = new StringBuffer(url).append("get/getlastXMData?xmbh=")
				.append(xmbh).append("&zyh=").append(zyh).append("&hsgh=").append(hsgh).append("&jgid=")
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
						new TypeReference<Response<List<LastDataBean>>>() {
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
	 * 获取护理记录助手目录
	 * @param ysbh
	 * @param xmbh
	 * @param jgbh
	 * @param ygdm
	 * @param bqid
	 * @param jgid
     * @param sysType
     * @return
     */
	public Response<List<HelpTree>> GetHelperContent(String ysbh, String xmbh,
													 String jgbh, String ygdm, String bqid, String jgid, int sysType) {

		Response<List<HelpTree>> response = new Response<List<HelpTree>>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";

		String uri = new StringBuffer(url).append("help/get/content?ysbh=")
				.append(ysbh).append("&xmbh=").append(xmbh).append("&jgbh=")
				.append(jgbh).append("&ygdm=").append(ygdm).append("&bqid=")
				.append(bqid).append("&jgid=").append(jgid).append("&sysType=")
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
//		Log.e("获取护理记录助手目录", "toGetHelperContent: "+entity );
		// 外面包含了双引号
		if (null != entity && entity.length() > 2) {
			try {
				response = JsonUtil.fromJson(entity,
						new TypeReference<Response<List<HelpTree>>>() {
						});
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败：解析错误";
			}
		}
		return response;
	}

	/**
	 * 获取护理记录助手内容
	 * @param ysbh
	 * @param xmbh
	 * @param jgbh
	 * @param mlbh
	 * @param ygdm
	 * @param bqid
	 * @param jgid
     * @param sysType
     * @return
     */
	public Response<List<HelpLeaf>> GetHelper(String ysbh, String xmbh,
											  String jgbh, String mlbh, String ygdm, String bqid, String jgid,
											  int sysType) {

		Response<List<HelpLeaf>> response = new Response<List<HelpLeaf>>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";

		String uri = new StringBuffer(url).append("help/get/help?ysbh=")
				.append(ysbh).append("&xmbh=").append(xmbh).append("&jgbh=")
				.append(jgbh).append("&mlbh=").append(mlbh).append("&ygdm=")
				.append(ygdm).append("&bqid=").append(bqid).append("&jgid=")
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
//		Log.e("获取护理记录助手内容", "toGetHelper: "+entity );
		// 外面包含了双引号
		if (null != entity && entity.length() > 2) {
			try {
				response = JsonUtil.fromJson(entity,
						new TypeReference<Response<List<HelpLeaf>>>() {
						});
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败：解析错误";
			}
		}
		return response;
	}

	/**
	 * 保存记录
	 * @param data
     * @return
     */
	public Response<String> SaveData(String data) {
		Response<String> response = new Response<String>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";
		String uri = new StringBuffer(url).append("save/record").toString();
		if (Constant.LOG_URI) {
			Log.d(Constant.TAG, uri);
		}
		HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,data);
		if (!isRequestSuccess(httpString.first)) {
			response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
			return response;
		}
		String entity = httpString.second;
//		Log.e("保存记录", "toSaveData: "+entity );

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
	 * 更新记录
	 * @param data
     * @return
     */
	public Response<String> UpdateData(String data) {

		Response<String> response = new Response<String>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";

		String uri = new StringBuffer(url).append("update/record").toString();
		if (Constant.LOG_URI) {
			Log.d(Constant.TAG, uri);
		}
		HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,data);
		if (!isRequestSuccess(httpString.first)) {
			response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
			return response;
		}
		String entity = httpString.second;
//		Log.e("更新记录", "toUpdateData: "+entity );
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
	 * 查找记录
	 * @param zyh
	 * @param jgid
	 * @param sysType
     * @return
     */
	public Response<List<NrTree>> GetNrTree(String zyh, String jgid, int sysType) {

		Response<List<NrTree>> response = new Response<List<NrTree>>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";

		String uri = new StringBuffer(url).append("get/record/content?zyh=").append(zyh)
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
//		Log.e("查找记录", "toGetNrTree: "+entity );

		// 外面包含了双引号
		if (null != entity && entity.length() > 2) {
			try {
				response = JsonUtil.fromJson(entity,
						new TypeReference<Response<List<NrTree>>>() {
						});
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败：解析错误";
			}
		}
		return response;
	}

	/**
	 * 删除记录
	 * @param zyh
	 * @param jlbh
	 * @param yhid
	 * @param yhxm
	 * @param jgid
     * @param sysType
     * @return
     */
	public Response<String> Delete(String zyh, String jlbh, String yhid,
									String yhxm, String jgid, int sysType) {

		Response<String> response = new Response<String>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";

		String uri = new StringBuffer(url).append("delete/record?zyh=").append(zyh)
				.append("&jlbh=").append(jlbh).append("&yhid=").append(yhid)
				.append("&yhxm=").append(yhxm).append("&jgid=").append(jgid)
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
//		Log.e("删除记录", "toDelete: "+entity );
		// 外面包含了双引号
		if (null != entity && entity.length() > 2) {
			try {
				response = JsonUtil.fromJson(entity,
						new TypeReference<Response<Boolean>>() {
						});
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败：解析错误";
			}
		}
		return response;
	}

	/**
	 * 护理记录签名
	 * @param data
	 * @return
     */
	public Response<String> SignName(String data) {

		Response<String> response = new Response<String>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";
//   http://10.0.26.180:84/NIS/auth/mobile/nurserecord/signname/record
		String uri = new StringBuffer(url).append("signname/record").toString();
		if (Constant.LOG_URI) {
			Log.d(Constant.TAG, uri);
		}
		HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,data);
		if (!isRequestSuccess(httpString.first)) {
			response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
			return response;
		}
		String entity = httpString.second;
//		Log.e("护理记录签名", "toSignName: "+entity );
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
	 * 获取特殊符号，用于引用
	 * @param dmlb
     * @return
     */
	public Response<List<OtherRefer>> GetOtherList(String dmlb) {

		Response<List<OtherRefer>> response = new Response<List<OtherRefer>>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";

		String uri = new StringBuffer(url).append("get/ref/others?dmlb=")
				.append(dmlb).toString();
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
						new TypeReference<Response<List<OtherRefer>>>() {
						});
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败：解析错误";
			}
		}
		return response;
	}

	/**
	 * 获取病人过敏药物，用于引用
	 * @param zyh       住院号
	 * @param startime  开始时间
	 * @param endtime   结束时间
	 * @param jgid      机构id
     * @return
     */
	public Response<List<DrugMedical>> GetDrugMedicalAdviceList(String zyh,
																String startime, String endtime, String jgid) {

		Response<List<DrugMedical>> response = new Response<List<DrugMedical>>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";

		String uri = new StringBuffer(url)
				.append("get/ref/drug/medical/advices?zyh=").append(zyh)
				.append("&startime=").append(startime).append("&endtime=")
				.append(endtime).append("&jgid=").append(jgid)
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
//		Log.e("过敏药物", "toGetDrugMedicalAdviceList: "+ entity);
		// 外面包含了双引号
		if (null != entity && entity.length() > 2) {
			try {
				response = JsonUtil.fromJson(entity,
						new TypeReference<Response<List<DrugMedical>>>() {
						});
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败：解析错误";
			}
		}
		return response;
	}

	/**
	 * 获取病人手术，用于引用
	 *
	 * @param zyh   住院号
	 * @param jgid  机构id
     * @return
     */
	public Response<List<Operation>> GetOperationList(String zyh, String jgid) {

		Response<List<Operation>> response = new Response<List<Operation>>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";

		String uri = new StringBuffer(url).append("get/ref/operations?zyh=")
				.append(zyh).append("&jgid=").append(jgid).toString();
		if (Constant.LOG_URI) {
			Log.d(Constant.TAG, uri);
		}
		HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
		if (!isRequestSuccess(httpString.first)) {
			response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
			return response;
		}
		String entity = httpString.second;
//		Log.e("病人手术 ", "toGetSignList: "+entity );
		// 外面包含了双引号
		if (null != entity && entity.length() > 2) {
			try {
				response = JsonUtil.fromJson(entity,
						new TypeReference<Response<List<Operation>>>() {
						});
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败：解析错误";
			}
		}
		return response;
	}

	/**
	 * 获取病人体征，用于引用
	 *
	 * @param zyh       住院号
	 * @param startime	开始时间
	 * @param endtime	结束时间
	 * @param jgid		机构id
     * @return
     */
	public Response<List<Sign>> GetSignList(String zyh, String startime,
											String endtime, String jgid) {

		Response<List<Sign>> response = new Response<List<Sign>>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";

		String uri = new StringBuffer(url).append("get/ref/lifesigns?zyh=")
				.append(zyh).append("&starttime=").append(startime)
				.append("&endtime=").append(endtime).append("&jgid=")
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
//		Log.e("病人体征", "toGetSignList: "+entity );
		// 外面包含了双引号
		if (null != entity && entity.length() > 2) {
			try {
				response = JsonUtil.fromJson(entity,
						new TypeReference<Response<List<Sign>>>() {
						});
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败：解析错误";
			}
		}
		return response;
	}

	/**
	 * 获取生命体征/风险评估分页数据------引用体征数据
	 * @param zyh       
	 * @param yslx
	 * @param yskz
	 * @param pageIndex
	 * @param jgid
     * @return
     */
	public Response<Association> getAssociation(String zyh, String yslx,
												String yskz, int pageIndex, String jgid) {
		Response<Association> response = new Response<Association>();
		response.ReType = 1;
		response.Msg = "请求失败：网络错误";
		String uri = new StringBuffer(url).append("get/association?zyh=")
				.append(zyh).append("&yslx=").append(yslx).append("&yskz=")
				.append(yskz).append("&pageIndex=").append(pageIndex)
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
//		Log.e("", "togetAssociation: "+pageIndex );
//		Log.e("分页数据", "togetAssociation: "+entity );
		// 外面包含了双引号
		if (null != entity && entity.length() > 2) {
			try {
				response = JsonUtil.fromJson(entity,
						new TypeReference<Response<Association>>() {
						});
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
				response.Msg = "请求失败：解析错误";
			}
		}
		return response;
	}
}
