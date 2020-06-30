package com.bsoft.mob.ienr.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.bloodglucose.BGHistoryData;
import com.bsoft.mob.ienr.model.bloodglucose.BloodGlucoseDetail;
import com.bsoft.mob.ienr.model.bloodglucose.BloodGlucoseRecord;
import com.bsoft.mob.ienr.model.bloodglucose.GlucoseTimeData;
import com.bsoft.mob.ienr.model.kernel.SickPersonVo;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;


/**
 * Created by Ding.pengqiang
 * on 2016/12/27.
 */
public class BloodGlucoseApi extends BaseApi {

    public String url;

    public BloodGlucoseApi(AppHttpClient httpClient, Context mContext) {
        super(httpClient, mContext);
    }

    public BloodGlucoseApi(AppHttpClient httpClient, Context mContext,
                               String url) {
        super(httpClient, mContext);
        this.url = url;
    }

    public static BloodGlucoseApi getInstance(Context mContext) {
        // Context localContext = AppContext.getContext();
        BloodGlucoseApi api = (BloodGlucoseApi) mContext
                .getSystemService("com.bsoft.mob.ienr.api.BloodGlucoseApi");
        if (api == null)
            api = (BloodGlucoseApi) mContext
                    .getApplicationContext()
                    .getSystemService("com.bsoft.mob.ienr.api.BloodGlucoseApi");
        if (api == null)
            throw new IllegalStateException("api not available");
        return api;
    }

    /**
     * 获取血糖治疗的时间点
     * @return
     */
    public Response<GlucoseTimeData> getGlucoseTimes() {
        Response<GlucoseTimeData> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url)
                .append("get/getGlucoseTimes").toString();
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
                        new TypeReference<Response<GlucoseTimeData>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     *  获取血糖治疗列表
     *
     * @param xmlx 项目类型  1 血糖  2 胰岛素
     * @param zyh  住院号
     * @param jhrq 查询日期  'yyyy-MM-dd'
     * @param brbq 病人病区
     * @param jgid 机构id
     * @param xmxh 项目序号(时间点)
     * @return

     */
    public Response<BloodGlucoseRecord> getBGList(String xmlx, String zyh,
                                                  String jhrq, String brbq,
                                                  String jgid, String xmxh) {
        Response<BloodGlucoseRecord> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url)
                .append("get/getBGList?xmlx=").append(xmlx)
                .append("&zyh=").append(zyh).append("&jhrq=").append(jhrq)
                .append("&brbq=").append(brbq).append("&jgid=").append(jgid)
                .append("&xmxh=").append(xmxh).toString();
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
                        new TypeReference<Response<BloodGlucoseRecord>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 保存血糖记录明细
     * @param data
     * @return
     */
    public Response<BloodGlucoseRecord> saveBloodGlucose(String data) {
        Response<BloodGlucoseRecord> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url)
                .append("post/saveBloodGlucose").toString();
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
                        new TypeReference<Response<BloodGlucoseRecord>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 根据xmlx和xmxh获取未执行的记录
     * @param zyh
     * @param jhrq
     * @param brbq
     * @return
     */
    public Response<BloodGlucoseRecord> getUnexecutedBG(String xmlx,
                                 String zyh,String jhrq,String brbq,String xmxh) {
        Response<BloodGlucoseRecord> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url)
                .append("get/getUnexecutedBG?xmlx=").append(xmlx)
                .append("&zyh=").append(zyh)
                .append("&jhrq=").append(jhrq).append("&brbq=").append(brbq)
                .append("&xmxh=").append(xmxh).toString();
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
                        new TypeReference<Response<BloodGlucoseRecord>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 获取血糖治疗历史记录
     * @param zyh
     * @param kssj
     * @param jssj
     * @param brbq
     * @param cxlx
     * @return
     */
    public Response<BGHistoryData> getBloodGlucoseHistory(String zyh,
                                                          String kssj, String jssj, String brbq, String jgid) {
        Response<BGHistoryData> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url)
                .append("get/getBGHistory?zyh=").append(zyh)
                .append("&kssj=").append(kssj).append("&jssj=").append(jssj)
                .append("&brbq=").append(brbq).append("&jgid=").append(jgid)
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
                        new TypeReference<Response<BGHistoryData>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

	/**
	 * 获取血糖治疗病人列表
	 *
	 * @param bqid
	 * @param jhrq  计划日期
	 * @param xmlx  项目类型  1 血糖  2 胰岛素
	 * @param xmxh  项目序号(时间点)
	 * @param hsgh
	 * @param jgid
	 * @return
	 */
	public Response<ArrayList<SickPersonVo>> GetPatientList(String bqid, String jhrq, String xmlx, String xmxh, String hsgh, String jgid) {
        Response<ArrayList<SickPersonVo>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/GetPatientList?bqid=")
                .append(bqid).append("&jhrq=").append(jhrq)
                .append("&xmlx=").append(xmlx)
                .append("&xmxh=").append(xmxh).append("&hsgh=")
                .append(EmptyTool.isBlank(hsgh) ? "" : hsgh)
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
                        new TypeReference<Response<ArrayList<SickPersonVo>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    public Response<String> deleteDetail(String mxxh) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/deleteDetail?mxxh=")
                .append(mxxh).toString();
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

    public Response<BloodGlucoseDetail> addDetail(String zyh,String jhrq,
                                                  String brbq,String jgid
                                                    ,String xmxh,String xmnr) {
        Response<BloodGlucoseDetail> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url)
                .append("get/addDetail?xmnr=").append(xmnr)
                .append("&zyh=").append(zyh).append("&jhrq=").append(jhrq)
                .append("&brbq=").append(brbq).append("&jgid=").append(jgid)
                .append("&xmxh=").append(xmxh).toString();
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
                        new TypeReference<Response<BloodGlucoseDetail>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }
}
