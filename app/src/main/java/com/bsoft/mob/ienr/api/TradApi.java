package com.bsoft.mob.ienr.api;

import android.content.Context;
import android.util.Log;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.trad.SHFF_HLJS;
import com.bsoft.mob.ienr.model.trad.JSJL;
import com.bsoft.mob.ienr.model.trad.Traditional_ZZJL;
import com.bsoft.mob.ienr.model.trad.Traditional_ZZFJ;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.util.Locale;


public class TradApi extends BaseApi {

    public String url;

    public TradApi(AppHttpClient httpClient, Context mContext) {
        super(httpClient, mContext);
    }

    public TradApi(AppHttpClient httpClient, Context mContext,
                   String url) {
        super(httpClient, mContext);
        this.url = url;
    }

    public static TradApi getInstance(Context mContext) {
        // Context localContext = AppContext.getContext();
        TradApi api = (TradApi) mContext
                .getSystemService("com.bsoft.mob.ienr.api.TradApi");
        if (api == null)
            api = (TradApi) mContext
                    .getApplicationContext()
                    .getSystemService("com.bsoft.mob.ienr.api.TradApi");
        if (api == null)
            throw new IllegalStateException("api not available");
        return api;
    }

    public Response<List<Traditional_ZZJL>> getZYZZList(String zyh, String brbq, String jgid) {
        Response<List<Traditional_ZZJL>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String entity=null;
        if (Constant.DEBUG_LOCAL) {
            //entity = getLocalXml("ss.xml");
        } else {
            String uri = new StringBuffer(url)
                    .append("get/getZYZZJL?brbq=").append(brbq)
                    .append("&zyh=").append(zyh)
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
        }
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<List<Traditional_ZZJL>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }

    public Response<List<Traditional_ZZFJ>> getZZFJList(String zzbh) {
        Response<List<Traditional_ZZFJ>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String entity=null;
        if (Constant.DEBUG_LOCAL) {
            //entity = getLocalXml("ss.xml");
        } else {
            String uri = new StringBuffer(url)
                    .append("get/getZYZZFJ?zzbh=").append(zzbh).toString();
            if (Constant.LOG_URI) {
                Log.d(Constant.TAG, uri);
            }
            HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
            if (!isRequestSuccess(httpString.first)) {
                response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
                return response;
            }
             entity = httpString.second;
        }
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<List<Traditional_ZZFJ>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }


    public Response<List<JSJL>> getZYSHJSJL(String zyh, String jgid) {
        Response<List<JSJL>> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String entity=null;
        if (Constant.DEBUG_LOCAL) {
            //entity = getLocalXml("ss.xml");
        } else {
            String uri = new StringBuffer(url)
                    .append("get/getZYSHJSJL?zyh=").append(zyh)
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
        }
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<List<JSJL>>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }


    public Response<SHFF_HLJS> getSHFF_HLJS(String zyh,String brbq,String jgid) {
        Response<SHFF_HLJS> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String entity=null;
        if (Constant.DEBUG_LOCAL) {
            //entity = getLocalXml("ss.xml");
        } else {
            String uri = new StringBuffer(url)
                    .append("get/getSHFF_HLJS?zyh=").append(zyh)
                    .append("&brbq=").append(brbq)
                    .append("&jgid=").append(jgid)
                    .toString();
            if (Constant.LOG_URI) {
                Log.d(Constant.TAG, uri);
            }
//            HttpBackMsg<Integer, String, String> httpString = getHttpString_WithoutFormatDateString(uri);
            HttpBackMsg<Integer, String, String> httpString = getHttpString(uri);
            if (!isRequestSuccess(httpString.first)) {
                response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
                return response;
            }
             entity = httpString.second;
        }
        // 外面包含了双引号
        if (null != entity && entity.length() > 2) {
            try {
                response = JsonUtil.fromJson(entity,
                        new TypeReference<Response<SHFF_HLJS>>() {
                        });
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                response.Msg = "请求失败：解析错误";
            }
        }
        return response;
    }


    public Response<String> saveSHFF(String json) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String entity=null;
        if (Constant.DEBUG_LOCAL) {
            // entity = getLocalXml("XXX.xml");
        } else {
            String uri = new StringBuffer(url).append("post/saveSHFF").toString();
            if (Constant.LOG_URI) {
                Log.d(Constant.TAG, uri);
            }
            HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,json);
            if (!isRequestSuccess(httpString.first)) {
                response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
                return response;
            }
             entity = httpString.second;
        }
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

    public Response<String> saveHLJS(String json) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String entity=null;
        if (Constant.DEBUG_LOCAL) {
            // entity = getLocalXml("XXX.xml");
        } else {
            String uri = new StringBuffer(url).append("post/saveHLJS").toString();
            if (Constant.LOG_URI) {
                Log.d(Constant.TAG, uri);
            }
            HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,json);
            if (!isRequestSuccess(httpString.first)) {
                response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
                return response;
            }
             entity = httpString.second;
        }
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
     *
     * @param json 要保存的数据对象
     * @return
     */
    public Response<String> saveZYPJInfo(String json) {
        Response<String> response = new Response<>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String entity=null;
        if (Constant.DEBUG_LOCAL) {
           // entity = getLocalXml("XXX.xml");
        } else {
            String uri = new StringBuffer(url).append("post/saveZYPJInfo").toString();
            if (Constant.LOG_URI) {
                Log.d(Constant.TAG, uri);
            }
            HttpBackMsg<Integer, String, String> httpString = postHttpJson(uri,json);
            if (!isRequestSuccess(httpString.first)) {
                response.Msg = String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
                return response;
            }
             entity = httpString.second;
        }
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
