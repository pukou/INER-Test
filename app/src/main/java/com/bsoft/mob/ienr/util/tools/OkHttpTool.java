package com.bsoft.mob.ienr.util.tools;


import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Callback;

/**
 * Created by louisgeek on 2016/12/6.
 */
public class OkHttpTool {
    private static final String TAG = "OkHttpTool";
    private Map<String, String> mDefaultHeadersMap;
    private Map<String, String> mCustomHeadersMap;
    private StringBuffer mParamsStringBuffer;
    private Callback mResponseCallback;
    private Object mTag;

    public OkHttpTool() {
        /**
         * 默认headers
         */
        if (mDefaultHeadersMap == null) {
            mDefaultHeadersMap = new HashMap<>();
        }
        mDefaultHeadersMap.put("userKey", "XXX");
        mDefaultHeadersMap.put("userToken", "XXX");
        /**
         *
         */
        if (mCustomHeadersMap == null) {
            mCustomHeadersMap = new HashMap<>();
        }
        /**
         * 默认params
         */
        Map<String, String> defaultParamsMap = new HashMap<>();
    /*    defaultParamsMap.put("showapi_appid", ApiUrls.SHOWAPI_APPID);
        defaultParamsMap.put("showapi_sign", ApiUrls.SHOWAPI_SIGN_SIMPLE);*/

        mParamsStringBuffer = new StringBuffer();
        mParamsStringBuffer.append(ParamsTool.paramsMapToStr(defaultParamsMap));
    }

    public OkHttpTool headers(Map<String, String> headersMap) {
        mCustomHeadersMap.clear();
        mCustomHeadersMap.putAll(headersMap);
        return this;
    }

    public OkHttpTool headers(String headersStr) {
        return headers(ParamsTool.paramsStrToMap(headersStr));
    }

    public OkHttpTool paramsJsonString(String paramsJsonString) {
        mParamsStringBuffer.append(paramsJsonString);
        return this;
    }
    public OkHttpTool params(String params) {
        /**
         * 最后一个是&
         */
        if (mParamsStringBuffer.length() > 0 && mParamsStringBuffer.lastIndexOf("&") == mParamsStringBuffer.length() - 1) {
            /* do nothing */
        } else {
            mParamsStringBuffer.append("&");
        }
        mParamsStringBuffer.append(params);
//        CLog.d("params:" + mParamsStringBuffer.toString());
        return this;
    }

    public OkHttpTool params(Map<String, String> paramsMap) {
        return params(ParamsTool.paramsMapToStr(paramsMap));
    }

    public OkHttpTool callback(Callback responseCallback) {
        mResponseCallback = responseCallback;
        return this;
    }

    public OkHttpTool tag(Object tag) {
        mTag = tag;
        return this;
    }

    public HttpBackMsg<Integer,String,String> doGetUrlSync(String webUrl) {
//        CLog.d("url:" + webUrl);
        return OkHttpSingleton.getInstance().doGetSync(webUrl, mDefaultHeadersMap, mCustomHeadersMap, mTag);
    }

    public HttpBackMsg<Integer,String,String> doPostUrlSync(String webUrl) {
//        CLog.d("url:" + webUrl);
        return OkHttpSingleton.getInstance().doPostSync(webUrl, mDefaultHeadersMap, mCustomHeadersMap, mParamsStringBuffer.toString(), mTag);
    }
    public HttpBackMsg<Integer,String,String> doPostJsonStringUrlSync(String webUrl) {
//        CLog.d("url:" + webUrl);
        return OkHttpSingleton.getInstance().doPostJsonStringSync(webUrl, mDefaultHeadersMap, mCustomHeadersMap, mParamsStringBuffer.toString(), mTag);
    }
    public void doGetUrl(String webUrl) {
//        CLog.d("url:" + webUrl);
        OkHttpSingleton.getInstance().doGet(webUrl, mDefaultHeadersMap, mCustomHeadersMap, mTag, mResponseCallback);
    }

    public void doPostUrl(String webUrl) {
//        CLog.d("url:" + webUrl);
        OkHttpSingleton.getInstance().doPost(webUrl, mDefaultHeadersMap, mCustomHeadersMap, mParamsStringBuffer.toString(), mTag, mResponseCallback);
    }

    public void cancelByTag(Object tag) {
        Log.i(TAG, "cancelByTag: " + tag);
        OkHttpSingleton.getInstance().cancelByTag(tag);
    }

    public void cancelAll() {
        OkHttpSingleton.getInstance().cancelAll();
    }
}
