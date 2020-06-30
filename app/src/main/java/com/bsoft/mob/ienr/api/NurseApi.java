package com.bsoft.mob.ienr.api;

import android.content.Context;
import android.util.Log;
import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.daily.DailySecondItem;
import com.bsoft.mob.ienr.model.daily.DailyTopItem;
import com.bsoft.mob.ienr.model.outcontrol.OutControl;
import com.bsoft.mob.ienr.model.visit.VisitCount;
import com.bsoft.mob.ienr.model.visit.VisitHistory;
import com.bsoft.mob.ienr.model.visit.VisitPerson;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

/**
 * 病人巡视
 *
 * @author hy
 */
public class NurseApi extends BaseApi {

    public String url;

    public NurseApi(AppHttpClient httpClient, Context mContext) {
        super(httpClient, mContext);
    }

    public NurseApi(AppHttpClient httpClient, Context mContext, String url) {
        super(httpClient, mContext);
        this.url = url;
    }

    public static NurseApi getInstance(Context mContext)
            throws IllegalStateException {
        // Context localContext = AppContext.getContext();
        NurseApi api = (NurseApi) mContext
                .getSystemService("com.bsoft.mob.ienr.api.nurse");
        if (api == null)
            api = (NurseApi) mContext.getApplicationContext().getSystemService(
                    "com.bsoft.mob.ienr.api.nurse");
        if (api == null)
            throw new IllegalStateException("api not available");
        return api;
    }

    String Ip = "10.0.26.38";
    String Port = "8080";

    /**
     * 手动保存巡视记录
     *
     * @param urid
     * @param zyh     住院号
     * @param xsqk    巡视类别
     * @param jgid    机构ID
     * @param sysType 1为android
     * @return
     */
//    public ParserModel SetPatrol(String brbq, String urid, String zyh,
//                                 String xsqk, String jgid, int sysType) {
//        String xml = null;
//        if (Constant.DEBUG_LOCAL) {
//            xml = getLocalXml("SetPatrol.xml");
//        } else {
//            String uri = new StringBuffer(url).append("SetPatrol?brbq=")
//                    .append(brbq).append("&urid=").append(urid).append("&zyh=")
//                    .append(zyh).append("&xsqk=").append(xsqk).append("&jgid=")
//                    .append(jgid).append("&sysType=").append(sysType)
//                    .toString();
//            if (Constant.LOG_URI) {
//                Log.d(Constant.TAG, uri);
//            }
//            xml = getHttpXml(uri);
//        }
//        if (null != xml && xml.length() > 0) {
//            return parser.parserTable(xml, new ReflectVo(VisitPerson.class,
//                    "Table1"));
//        } else {
//            // 网络失败
//            return new ParserModel(Statue.NET_ERROR);
//        }
//    }
    public Response<List<VisitPerson>> SetPatrol(String brbq, String urid, String zyh,
                                                 String xsqk, String jgid, int sysType) {
        Response<List<VisitPerson>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String urlTemp = "http://" + Ip + ":" + Port + "/NIS/auth/mobile/visit/post/SetPatrol";
        String uri = new StringBuffer(urlTemp).append("?brbq=")
                .append(brbq).append("&urid=").append(urid).append("&zyh=")
                .append(zyh).append("&xsqk=").append(xsqk).append("&jgid=")
                .append(jgid).append("&sysType=").append(sysType)
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
                        new TypeReference<Response<List<VisitPerson>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 扫描保存巡视记录
     *
     * @param urid
     * @param sanStr  住院号
     * @param xsqk    巡视类别
     * @param jgid    机构ID
     * @param sysType 1为android
     * @return
     */
//    public ParserModel SetPatrolForScan(String brbq, String urid,
//                                        String sanStr, String xsqk, String jgid, int sysType) {
//        String xml = null;
//        if (Constant.DEBUG_LOCAL) {
//            xml = getLocalXml("SetPatrolForScan.xml");
//        } else {
//
//            String uri = new StringBuffer(url).append("SetPatrolForScan?brbq=")
//                    .append(brbq).append("&urid=").append(urid)
//                    .append("&sanStr=").append(sanStr).append("&xsqk=")
//                    .append(xsqk).append("&jgid=").append(jgid)
//                    .append("&sysType=").append(sysType).toString();
//            if (Constant.LOG_URI) {
//                Log.d(Constant.TAG, uri);
//            }
//            xml = getHttpXml(uri);
//        }
//        if (null != xml && xml.length() > 0) {
//            return parser.parserTable(xml, new ReflectVo(VisitPerson.class,
//                    "Table1"), new ReflectVo(VisitPerson.class, "Table2"));
//        } else {
//            // 网络失败
//            return new ParserModel(Statue.NET_ERROR);
//        }
//    }
    public Response<List<VisitPerson>> SetPatrolForScan(String brbq, String urid,
                                                        String sanStr, String xsqk, String jgid, int sysType) {
        Response<List<VisitPerson>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String urlTemp = "http://" + Ip + ":" + Port + "/NIS/auth/mobile/visit/post/SetPatrolForScan";
        String uri = new StringBuffer(urlTemp).append("?brbq=")
                .append(brbq).append("&urid=").append(urid)
                .append("&sanStr=").append(sanStr).append("&xsqk=")
                .append(xsqk).append("&jgid=").append(jgid)
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
                        new TypeReference<Response<List<VisitPerson>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;

    }

    /**
     * @param urid
     * @param ksdm    科室代码
     * @param jgid    机构ID
     * @param sysType 1为android
     * @return
     */
//    public ParserModel GetPatrol(String urid, String ksdm, String jgid,
//                                 int sysType) {
//        String xml = null;
//        if (Constant.DEBUG_LOCAL) {
//            xml = getLocalXml("GetPatrol.xml");
//        } else {
//            String uri = new StringBuffer(url).append("GetPatrol?urid=")
//                    .append(urid).append("&ksdm=").append(ksdm)
//                    .append("&jgid=").append(jgid).append("&sysType=")
//                    .append(sysType).toString();
//            if (Constant.LOG_URI) {
//                Log.d(Constant.TAG, uri);
//            }
//            xml = getHttpXml(uri);
//        }
//        if (null != xml && xml.length() > 0) {
//            return parser.parserTable(xml, new ReflectVo(VisitPerson.class,
//                            "Table1"), new ReflectVo(VisitPerson.class, "Table2"),
//                    new ReflectVo(CheckState.class, "Table3"));
//        } else {
//            // 网络失败
//            return new ParserModel(Statue.NET_ERROR);
//        }
//    }
    public Response<VisitCount> GetPatrol(String urid, String ksdm, String jgid,
                                          int sysType) {

        Response<VisitCount> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String urlTemp = "http://" + Ip + ":" + Port + "/NIS/auth/mobile/visit/get/GetPatrol";
        String uri = new StringBuffer(urlTemp).append("?urid=")
                .append(urid).append("&ksdm=").append(ksdm)
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

        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<VisitCount>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }
//    public ParserModel GetPatrolHistory(String zyh, String xsrq, String jgid,
//                                        int sysType) {
//        String xml = null;
//        if (Constant.DEBUG_LOCAL) {
//            xml = getLocalXml("GetPatrol.xml");
//        } else {
//            String uri = new StringBuffer(url).append("GetPatrolHistory?zyh=")
//                    .append(zyh).append("&xsrq=").append(xsrq).append("&jgid=")
//                    .append(jgid).append("&sysType=").append(sysType)
//                    .toString();
//            if (Constant.LOG_URI) {
//                Log.d(Constant.TAG, uri);
//            }
//            xml = getHttpXml(uri);
//        }
//        if (null != xml && xml.length() > 0) {
//            return parser.parserTable(xml, new ReflectVo(VisitHistory.class,
//                    "Table1"));
//        } else {
//            // 网络失败
//            return new ParserModel(Statue.NET_ERROR);
//        }
//    }

    public Response<List<VisitHistory>> GetPatrolHistory(String zyh, String xsrq, String jgid,
                                                         int sysType) {

        Response<List<VisitHistory>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String urlTemp = "http://" + Ip + ":" + Port + "/NIS/auth/mobile/visit/get/GetPatrolHistory";
        String uri = new StringBuffer(urlTemp).append("?zyh=")
                .append(zyh).append("&xsrq=").append(xsrq).append("&jgid=")
                .append(jgid).append("&sysType=").append(sysType)
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
                        new TypeReference<Response<List<VisitHistory>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    /**
     * 获取护理常规一级列表
     *
     * @param ksdm
     * @param jgid
     * @param sysType
     * @return
     */
    public Response<List<DailyTopItem>> GetDailyNurseType(String ksdm, String jgid, int sysType) {
        Response<List<DailyTopItem>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String urlTemp = "http://" + Ip + ":" + Port + "/NIS/auth/mobile/dailycare/get/GetDailyNurseType";
        String uri = new StringBuffer(urlTemp)
                .append("?ksdm=").append(ksdm)
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

        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<List<DailyTopItem>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;

    }

    /**
     * 获取护理常规二级列表
     *
     * @param type
     * @param jgid
     * @param sysType
     * @return
     */
    public Response<List<DailySecondItem>> GetDailyNurseList(String type, String jgid, int sysType) {
        Response<List<DailySecondItem>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String urlTemp = "http://" + Ip + ":" + Port + "/NIS/auth/mobile/dailycare/get/GetDailyNurseList";
        String uri = new StringBuffer(urlTemp)
                .append("?type=").append(type)
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

        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<List<DailySecondItem>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    public Response<String> SaveDailyNurseItems(String brbq, String zyh,
                                                String listXMBS, String urid, String jgid, int sysType) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String urlTemp = "http://" + Ip + ":" + Port + "/NIS/auth/mobile/dailycare/post/SaveDailyNurseItems";

        try {
            listXMBS = URLEncoder.encode(listXMBS, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String uri = new StringBuffer(urlTemp)
                .append("?brbq=").append(brbq)
                .append("&zyh=").append(zyh).append("&listXMBS=")
                .append(listXMBS).append("&urid=").append(urid)
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

        if (null != entity && entity.length() > 0) {
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
     * 获取病人当前外出状态
     *
     * @param zyh
     * @param brbq
     * @param jgid
     * @param sysType
     * @return
     */
    public Response<List<OutControl>> GetPatientStatus(String zyh, String brbq, String jgid,
                                                       int sysType) {
        Response<List<OutControl>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String urlTemp = "http://" + Ip + ":" + Port + "/NIS/auth/mobile/outcontrol/get/GetPatientStatus";
        String uri = new StringBuffer(urlTemp)
                .append("?zyh=").append(zyh)
                .append("&brbq=").append(brbq).append("&jgid=")
                .append(jgid).append("&sysType=").append(sysType)
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
                        new TypeReference<Response<List<OutControl>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;

    }

    /**
     * 外出登记
     *
     * @param data
     * @return
     */
    public Response<String> RegisterOutPatient(String data) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String urlTemp = "http://" + Ip + ":" + Port + "/NIS/auth/mobile/outcontrol/post/RegisterOutPatient";
        String uri = new StringBuffer(urlTemp).toString();
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

    /**
     * 回床登记
     *
     * @param jlxh    记录序号
     * @param hcdjsj  时间
     * @param hcdjhs  护士
     * @param jgid    机构
     * @param sysType 类型
     * @return
     */
    public Response<String> RegisterBackToBed(String jlxh, String hcdjsj,
                                              String hcdjhs, String jgid, int sysType) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String urlTemp = "http://" + Ip + ":" + Port + "/NIS/auth/mobile/outcontrol/get/RegisterBackToBed";

        try {
            hcdjsj = URLEncoder.encode(hcdjsj, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String uri = new StringBuffer(urlTemp)
                .append("?jlxh=").append(jlxh)
                .append("&hcdjsj=").append(hcdjsj).append("&hcdjhs=")
                .append(hcdjhs).append("&jgid=").append(jgid)
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

        if (null != entity && entity.length() > 0) {
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

    public Response<List<OutControl>> GetOutPatientByZyh(String zyh, String brbq, String jgid,
                                                         int sysType) {
        Response<List<OutControl>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String urlTemp = "http://" + Ip + ":" + Port + "/NIS/auth/mobile/outcontrol/get/GetOutPatientByZyh";
        String uri = new StringBuffer(urlTemp)
                .append("?zyh=").append(zyh)
                .append("&brbq=").append(brbq).append("&jgid=")
                .append(jgid).append("&sysType=").append(sysType)
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
                        new TypeReference<Response<List<OutControl>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }
}
