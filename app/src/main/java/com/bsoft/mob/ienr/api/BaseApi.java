package com.bsoft.mob.ienr.api;

import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.util.XmlParser;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;
import com.bsoft.mob.ienr.util.tools.OkHttpTool;
import com.fasterxml.jackson.core.type.TypeReference;

import org.apache.commons.lang3.StringEscapeUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-3 下午5:28:37
 * @类说明
 */
public class BaseApi {
    AppHttpClient httpClient;
    XmlParser parser = new XmlParser();
    protected Context mContext;

    private final String split = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.0";
    private final String split2 = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:00.0";

    public static String baseUrl;// = "http://172.16.152.16:9335/";
    public static String advisoryUrl="http://10.8.3.59:9393";
    public static String fileUpUrl="http://10.8.3.59:9392";

    public BaseApi(AppHttpClient httpClient, Context mContext) {
        this.httpClient = httpClient;
        this.mContext = mContext;
    }

    public AppHttpClient getHttpClient() {
        return httpClient;
    }

    public String getLocalXml(String filename) {
        try {
            InputStream localInputStream = mContext.getAssets().open(
                    "xml/" + filename);
            byte[] arrayOfByte = new byte[localInputStream.available()];
            localInputStream.read(arrayOfByte);
            localInputStream.close();
            String str = new String(arrayOfByte, "utf-8");
            return str;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    protected boolean isRequestSuccess(int code) {
        return code >= 200 && code < 300;
    }
    // getHttpXml —>getHttpString
    public HttpBackMsg<Integer,String,String> getHttpString(String url) {
       // System.out.println("url : " + url);
        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("Authorization", AppApplication.getInstance().authorizationString);
        headersMap.put("Cookie", "SESSION=" + AppApplication.getInstance().JSESSIONID);
        //  System.out.println("Cookie:" + AppApplication.getInstance().JSESSIONID);
       /* Pair<Boolean, String> result = HttpTool.getUrlBackStringSync(url, headersMap);
        if (result != null && result.first) {
            return formatDateString(result.second);
        }*/
        HttpBackMsg<Integer,String,String> backMsg = new OkHttpTool().headers(headersMap).doGetUrlSync(url);
        //
        if (backMsg == null) {
            return null;
        }
        if (isRequestSuccess(backMsg.first)) {
            String secondNew= formatDateString(backMsg.second);
            return HttpBackMsg.create(backMsg.first, secondNew, backMsg.third);
        }
        return backMsg;
    }

    @Deprecated
    //部分环境下 底层报java.net.SocketException: recvfrom failed: ECONNRESET (Connection reset by peer)
    public String getHttpXmlOld(String url) {
        String xml = null;
        try {
            xml = httpClient.executeHttpRequests(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null != xml && xml.length() > 0) {
            return formatDateString(xml);
        }
        return null;
    }

    private String formatDateString(String s) {
        //Pattern p = Pattern.compile(split);
        //Matcher m = p.matcher(s);
        //if (m.find()) {
        //    String ss = m.group();
            /*if (ss.matches(split2)) {
                ss = ss.substring(0, 16);
			} else {
				ss = ss.substring(0, 19);
			}*/
        //    ss = ss.substring(0, 19);
        //    s = s.replaceFirst(split, ss);
        //    s = formatDateString(s);
        //}
        s = s.replaceAll("(\\d{2}:\\d{2}:\\d{2})[.]\\d{1,3}", "$1");
        return s;
    }

    public HttpBackMsg<Integer,String,String> getHttpString_WithoutFormatDateString(String url) {
        //System.out.println("url : " + url);
        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("Authorization", AppApplication.getInstance().authorizationString);
        headersMap.put("Cookie", "SESSION=" + AppApplication.getInstance().JSESSIONID);
        //  System.out.println("Cookie:" + AppApplication.getInstance().JSESSIONID);
       /* Pair<Boolean, String> result = HttpTool.getUrlBackStringSync(url, headersMap);
        if (result != null && result.first) {
            return result.second;
        }*/
        HttpBackMsg<Integer,String,String> backMsg = new OkHttpTool().headers(headersMap).doGetUrlSync(url);
        //
        //
        if (backMsg == null) {
            return null;
        }
        return backMsg;
    }

    public HttpBackMsg<Integer,String,String> postHttpJson(String url, String params) {
       // System.out.println("url : " + url);
        Map<String, String> headersMap = new HashMap<>();
        headersMap.put("Accept", "application/json");
        headersMap.put("Content-Type", "application/json; charset=utf-8");
        headersMap.put("Authorization", AppApplication.getInstance().authorizationString);
        headersMap.put("Cookie", "SESSION=" + AppApplication.getInstance().JSESSIONID);
        System.out.println("Cookie:" + AppApplication.getInstance().JSESSIONID);
      /* Pair<Boolean, String> result = HttpTool.postUrlBackStringSync(url, headersMap,params);
        if (result != null && result.first) {
            return formatDateString(result.second);
        }*/
        //paramsJsonString
        HttpBackMsg<Integer,String,String> backMsg = new OkHttpTool().headers(headersMap).paramsJsonString(params).doPostJsonStringUrlSync(url);
        //
        if (backMsg == null) {
            return null;
        }
        if (isRequestSuccess(backMsg.first)) {
            String secondNew= formatDateString(backMsg.second);
            return HttpBackMsg.create(backMsg.first, secondNew, backMsg.third);
        }
        return backMsg;
    }

    public String decodeString(String strData) {
        strData = strData.replaceAll("&lt;", "<");
        strData = strData.replaceAll("&gt;", ">");
        return strData;
    }

    public boolean perserBooleanResponse(String xmlStr)
            throws XmlPullParserException, IOException {

        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(new StringReader(xmlStr));
        int event = parser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_DOCUMENT:
                    break;
                case XmlPullParser.START_TAG:
                    if ("boolean".equals(parser.getName())) {

                        String value = parser.nextText();
                        if ("true".equals(value)) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
            }
            event = parser.next();
        }
        return false;
    }

    public Response<String> sync(String data, String jgid) {
        Response<String> response = new Response<String>();
        response.ReType = 1;
        response.Msg = "请求失败：网络错误";
        String uri = new StringBuffer(baseUrl)
                .append(APIUrlConfig.SyncFormService + "SaveSynchroData?data=")
                .append(data).append("&jgid=").append(jgid)
                .append(Constant.sysType).toString();
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
                // 去除转义符
                entity = StringEscapeUtils.unescapeJson(entity);
                // 去除双引号
                entity = entity.substring(1, entity.length() - 1);
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
