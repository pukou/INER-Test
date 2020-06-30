package com.bsoft.mob.ienr.api;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.BCRYBean;
import com.bsoft.mob.ienr.model.BCSZBean;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.kernel.PatientDetailResponse;
import com.bsoft.mob.ienr.model.kernel.SickPersonVo;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignSync;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Description:
 * User: 田孝鸣(tianxm@bsoft.com.cn)
 * Date: 2016-11-22
 * Time: 09:38
 * Version:
 */

public class PatientApi extends BaseApi {

    public String url;

    public PatientApi(AppHttpClient httpClient, Context mContext) {
        super(httpClient, mContext);
    }

    public PatientApi(AppHttpClient httpClient, Context mContext, String url) {
        super(httpClient, mContext);
        this.url = url;
    }

    @Override
    public AppHttpClient getHttpClient() {
        return httpClient;
    }

    public static PatientApi getInstance(Context localContext) {
        PatientApi api = (PatientApi) localContext
                .getSystemService("com.bsoft.mob.ienr.api.PatientApi");
        if (api == null)
            api = (PatientApi) localContext.getApplicationContext()
                    .getSystemService("com.bsoft.mob.ienr.api.PatientApi");
        if (api == null)
            throw new IllegalStateException("api not available");
        return api;
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
    public Response<ArrayList<SickPersonVo>> GetPatientList(String bqid, int filter, int starttime,
                                                            int endtime, String hsgh, String jgid) {
        Response<ArrayList<SickPersonVo>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/list?bqid=")
                .append(bqid).append("&type=").append(filter)
                .append("&starttime=").append(starttime)
                .append("&endtime=").append(endtime).append("&hsgh=")
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
    public Response<String> SaveGroupRYList(String data,String ygdm,String bqdm) {

        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("post/group_ry_list_save?ygdm=")
                .append(ygdm).append("&bqdm=").append(bqdm)
                .toString();
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
                        new TypeReference<Response<String>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }
    @Deprecated
    public Response<List<BCRYBean>> GetGroupRYList(String bqid,  String hsgh, String jgid) {
        Response<List<BCRYBean>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/group_ry_list?bqid=")
                .append(bqid).append("&hsgh=")
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
                        new TypeReference<Response<List<BCRYBean>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    public Response<List<BCSZBean>> GetGroupCfgList(String ygdm,String bqdm,  String jgid) {
        Response<List<BCSZBean>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/group_cfg_list?ygdm=")
                .append(ygdm).append("&bqdm=").append(bqdm).append("&jgid=").append(jgid).toString();
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
                        new TypeReference<Response<List<BCSZBean>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }
    /**
     * 获取我的病人列表
     *
     * @param zyh  住院号
     * @param jgid 机构ID
     * @return
     */
    public Response<PatientDetailResponse> GetPatientDetail(String zyh, String jgid) {
        Response<PatientDetailResponse> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(url).append("get/detail?zyh=")
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
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<PatientDetailResponse>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }
/*升级编号【56010038】============================================= start
                外出管理PDA上只有登记功能，查询需要找到具体的人再查询，不太方便，最好能有一个查询整个病区外出病人的列表
            ================= classichu 2018/3/7 19:49
            */
    /**
     *
     * @param zyh
     * @param jgid
     * @return
     */
    public Response<SickPersonVo> getPatientForHand(String zyh, String jgid) {
        Response<SickPersonVo> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String entity = null;
        String uri = new StringBuffer(url)
                .append("get/getPatientForHand?zyh=").append(zyh)
                .append("&jgid=").append(jgid).toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
        if (!isRequestSuccess(httpString.first)) {
            response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
            return response;
        }
         entity = httpString.second;
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<SickPersonVo>>() {
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
     * 扫描病人
     *
     * @param barcode
     * @param prefix
     * @param jgid
     * @return
     */
    public Response<Object> GetPatientForScan(String barcode, String prefix, String jgid) {
        Response<Object> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String entity = null;
        String uri = new StringBuffer(url)
                .append("get/getPatientForScan?barcode=").append(barcode)
                .append("&prefix=").append(prefix).append("&jgid=").append(jgid).toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
        if (!isRequestSuccess(httpString.first)) {
            response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
            return response;
        }
         entity = httpString.second;
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<SickPersonVo>>() {
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
     * @param @param  jgid 机构id
     * @param @return
     * @return Response<String>
     * @throws
     * @Title: CheckRFID
     * @Description: 检查病人是否绑定RFID
     */
    public Response<String> GetRFID(String zyh, String jgid) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String entity = null;
        String uri = new StringBuffer(url).append("get/getRFID?zyh=")
                .append(zyh).append("&jgid=").append(jgid).toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
        if (!isRequestSuccess(httpString.first)) {
            response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
            return response;
        }
         entity = httpString.second;
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
     * @param @param  zyh 住院号
     * @param @param  jgid 机构id
     * @param @return
     * @return Response<String>
     * @throws
     * @Title: CheckRFID
     * @Description: 给病人绑定rfid
     */
    public Response<String> PatientBindRFID(String zyh, String sbid, String yhid, String bqid, String jgid) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String entity = null;

        String uri = new StringBuffer(url).append("get/patientBindRFID?bqid=")
                .append(bqid).append("&sbid=").append(sbid)
                .append("&zyh=").append(zyh).append("&yhid=").append(yhid)
                .append("&jgid=").append(jgid).toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
        if (!isRequestSuccess(httpString.first)) {
            response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
            return response;
        }
         entity = httpString.second;
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
     * @param @param  zyh 住院号
     * @param @param  jgid 机构id
     * @param @return
     * @return Response<String>
     * @throws
     * @Title: CheckRFID
     * @Description: 给病人取消绑定rfid
     */
    public Response<String> PatientUnBindRFID(String zyh, String sbid, String shbs, String yhid, String bqid, String jgid) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String entity = null;

        String uri = new StringBuffer(url).append("get/patientBindRFID?bqid=")
                .append(bqid).append("&sbid=").append(sbid).append("&shbs=").append(shbs)
                .append("&zyh=").append(zyh).append("&yhid=").append(yhid)
                .append("&jgid=").append(jgid).toString();
        if (Constant.LOG_URI) {
            Log.d(Constant.TAG, uri);
        }
        HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
        if (!isRequestSuccess(httpString.first)) {
            response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
            return response;
        }
         entity = httpString.second;
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
