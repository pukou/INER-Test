package com.bsoft.mob.ienr.util.tools;

import android.util.Log;

import com.bsoft.mob.ienr.Constant;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by louisgeek on 2016/12/6.
 */

public class OkHttpSingleton {

    /**
     * ====================================================
     */
    private static volatile OkHttpSingleton mInstance;

    /* 私有构造方法，防止被实例化 */
    private OkHttpSingleton() {
        /*
       OkHTTP 3  不能用  后期赋值不了 也添加不了拦截器
       mOkHttpClient.newBuilder().connectTimeout(30, TimeUnit.SECONDS);
        mOkHttpClient.newBuilder().readTimeout(30, TimeUnit.SECONDS);
        mOkHttpClient.newBuilder().writeTimeout(30, TimeUnit.SECONDS);*/

        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(Constant.HTTP_TIME_OUT, TimeUnit.MILLISECONDS)
                .readTimeout(Constant.HTTP_TIME_OUT, TimeUnit.MILLISECONDS)
                .writeTimeout(Constant.HTTP_TIME_OUT, TimeUnit.MILLISECONDS)
                .build();
    }

    public HttpBackMsg<Integer, String, String> handleException(Exception e) {
        String error = e.getMessage() == null ? "" : e.getMessage();
        //测试
        if (Constant.DEBUG) {
            StringBuilder sb = new StringBuilder(error);
            sb.append("【StackTrace】").append("\n");
            StackTraceElement[] stackTraceElements = e.getStackTrace();
            for (StackTraceElement stackTraceElement : stackTraceElements) {
                sb.append(" ").append(stackTraceElement.toString()).append("\n");
            }
            Throwable causeThrowable = e.getCause();
            if (causeThrowable != null) {
                sb.append("【Cause StackTrace】").append("\n");
                StackTraceElement[] causeStackTraceElements = causeThrowable.getStackTrace();
                for (StackTraceElement causeStackTraceElement : causeStackTraceElements) {
                    sb.append(" ").append(causeStackTraceElement.toString()).append("\n");
                }
            }
            error = sb.toString();
            Log.e(TAG, "handleException: " + error);
        }
        if (error.startsWith("Canceled")) {
            return HttpBackMsg.create(OKHTTP_CALL_CANCEL, "请求取消", error);
        }
        if (e instanceof ConnectException) {
            return HttpBackMsg.create(-9999, "服务器请求超时", error);
        } else if (e instanceof SocketException) {
            return HttpBackMsg.create(-9998, "服务器请求超时", error);
        } else if (e instanceof SocketTimeoutException) {
            return HttpBackMsg.create(-9997, "服务器响应超时", error);
        } else if (e instanceof TimeoutException) {
            return HttpBackMsg.create(-9996, "网络请求超时", error);
        } else if (e instanceof UnknownHostException) {
            return HttpBackMsg.create(-9995, "无法识别主机", error);
        } else if (e instanceof IOException) {
            return HttpBackMsg.create(-9994, "服务异常", error);
        } else {
            return HttpBackMsg.create(OKHTTP_CALL_ERROR, "服务错误", error);
        }
    }

    public static OkHttpSingleton getInstance() {
        if (mInstance == null) {
            synchronized (OkHttpSingleton.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpSingleton();
                }
            }
        }
        return mInstance;
    }

    /**
     * ==========================================
     */
    private static final String TAG = "OkHttpSingleton";
    //-99,  -100   自己定义的。。。
    public static final int OKHTTP_CALL_CANCEL = 50;
    public static final int OKHTTP_CALL_ERROR = -10000;
    /**
     * "application/x-www-form-urlencoded"，他是默认的MIME内容编码类型，一般可以用于所有的情况。但是他在传输比较大的二进制或者文本数据时效率极低。
     * 这种情况应该使用"multipart/form-data"。如上传文件或者二进制数据和非ASCII数据。
     */
    public static final MediaType MEDIA_TYPE_NORAML_FORM = MediaType.parse("application/x-www-form-urlencoded;charset=utf-8");
    //既可以提交普通键值对，也可以提交(多个)文件键值对。
    public static final MediaType MEDIA_TYPE_MULTIPART_FORM = MediaType.parse("multipart/form-data;charset=utf-8");
    //只能提交二进制，而且只能提交一个二进制，如果提交文件的话，只能提交一个文件,后台接收参数只能有一个，而且只能是流（或者字节数组）
    public static final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");
    public static final MediaType MEDIA_TYPE_TEXT = MediaType.parse("text/plain;charset=utf-8");
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=utf-8");
    public static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";

    private OkHttpClient mOkHttpClient;

    /**
     * Posting form parameters FormBody （okhttp3 新增）  中文有问题  暂时别用
     */
    @Deprecated
    public void doPostOld(String url, Map<String, String> headersMap, Map<String, String> paramsMap, Callback responseCallback) {
        Log.d(TAG, "doPostAsyncOld: url:" + url);
        StringBuilder stringBuilder = new StringBuilder("params:");
        for (String key : paramsMap.keySet()) {
            String value = paramsMap.get(key);
            stringBuilder.append(key);
            stringBuilder.append("=");
            stringBuilder.append(value);
            stringBuilder.append("&");
            //  Log.d(TAG, "doPostAsync: key:"+key+",value:"+paramsMap.get(key));
        }
        Log.d(TAG, "doPostAsyncOld:" + stringBuilder.toString());
        //表单数据
       /* RequestBody formBody = new FormBody.Builder()
                .add("ID", "1")
                .add("ID2", "2")
                .build();*/
        FormBody formBody = null;

        if (paramsMap != null) {
            FormBody.Builder formBodyBuilder = new FormBody.Builder();
            for (String key : paramsMap.keySet()) {
                // System.out.println("key= "+ key + " and value= " + paramsMap.get(key));
                String value = paramsMap.get(key);
             /*   try {
                    key= URLEncoder.encode(key,DEFAULT_PARAMS_ENCODING);
                    value=URLEncoder.encode(value,DEFAULT_PARAMS_ENCODING);
                    Log.d(TAG, "doPostAsync: encode key:"+key);
                    Log.d(TAG, "doPostAsync: encode value:"+value);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.e(TAG, "doPostAsync: encode UnsupportedEncodingException");
                }*/
                //###   formBodyBuilder.add(key,value);
                formBodyBuilder.addEncoded(key, value);
                formBodyBuilder.add(key, value);

            }
            formBody = formBodyBuilder.build();
        }
      /*  Request request=new Request.Builder()
                .url("http://api.lvseeds.com:8080/lvseeds/Melonplant/GetOther")
                .headers(Headers.of(getHeaders()))
                .post(formBody)
                .build();*/
        //
        Request request;
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);

        //默认需要加的验证
        ////#### requestBuilder.headers(Headers.of(setupDefaultHeaders()));

        if (headersMap != null) {
            for (Map.Entry<String, String> entry : headersMap.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        if (formBody != null) {
            requestBuilder.post(formBody);
        }
        request = requestBuilder.build();

        mOkHttpClient.newCall(request).enqueue(responseCallback);
    }

    public void cancelAll() {
        mOkHttpClient.dispatcher().cancelAll();
    }

    /**
     * 如果正在写请求或者读响应 java.io.IOException: Canceled
     *
     * @param tag
     */
    public synchronized void cancelByTag(Object tag) {
        if (tag == null) {
            return;
        }
        for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    public void doPost(String url, Map<String, String> defaultHeadersMap,
                       Map<String, String> customHeadersMap,
                       String paramsStr, Object tag, Callback responseCallback) {
        //创建一个请求对象
        Request request;
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        if (tag != null) {
            requestBuilder.tag(tag);
        }
        //
        if (defaultHeadersMap != null && defaultHeadersMap.size() > 0) {
            //默认需要加的验证
            requestBuilder.headers(Headers.of(defaultHeadersMap));
        }
        if (customHeadersMap != null && customHeadersMap.size() > 0) {
            for (Map.Entry<String, String> entry : customHeadersMap.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        if (paramsStr != null) {
            RequestBody requestBody = RequestBody.create(MEDIA_TYPE_NORAML_FORM, paramsStr);
            if (requestBody != null) {
                requestBuilder.post(requestBody);
            }
        }
        //
        request = requestBuilder.build();
        //
        mOkHttpClient.newCall(request).enqueue(responseCallback);
    }

    public void doPostString(String url, Map<String, String> defaultHeadersMap,
                             Map<String, String> customHeadersMap,
                             String strContent, Object tag, Callback responseCallback) {
        //创建一个请求对象
        Request request;
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        if (tag != null) {
            requestBuilder.tag(tag);
        }
        //
        if (defaultHeadersMap != null && defaultHeadersMap.size() > 0) {
            //默认需要加的验证
            requestBuilder.headers(Headers.of(defaultHeadersMap));
        }
        if (customHeadersMap != null && customHeadersMap.size() > 0) {
            for (Map.Entry<String, String> entry : customHeadersMap.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        if (strContent != null) {
            RequestBody requestBody = RequestBody.create(MEDIA_TYPE_TEXT, strContent);
            if (requestBody != null) {
                requestBuilder.post(requestBody);
            }
        }
        //
        request = requestBuilder.build();
        //
        mOkHttpClient.newCall(request).enqueue(responseCallback);
    }

    public void doPostJsonString(String url, Map<String, String> defaultHeadersMap,
                                 Map<String, String> customHeadersMap, String jsonStr,
                                 Object tag, Callback responseCallback) {
        //创建一个请求对象
        Request request;
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        if (tag != null) {
            requestBuilder.tag(tag);
        }
        //
        if (defaultHeadersMap != null && defaultHeadersMap.size() > 0) {
            //默认需要加的验证
            requestBuilder.headers(Headers.of(defaultHeadersMap));
        }
        if (customHeadersMap != null && customHeadersMap.size() > 0) {
            for (Map.Entry<String, String> entry : customHeadersMap.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        if (jsonStr != null) {
            RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, jsonStr);
            if (requestBody != null) {
                requestBuilder.post(requestBody);
            }
        }
        //
        request = requestBuilder.build();
        //
        mOkHttpClient.newCall(request).enqueue(responseCallback);
    }

    public void doGet(String url, Map<String, String> defaultHeadersMap,
                      Map<String, String> customHeadersMap, Object tag, Callback responseCallback) {
      /*  Request request=new Request.Builder()
                .url("http://api.lvseeds.com:8080/lvseeds/Melonplant/GetOther")
                .headers(Headers.of(getHeaders()))
                .post(formBody)
                .build();*/
        //
        Request request;
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        if (tag != null) {
            requestBuilder.tag(tag);
        }
        //
        if (defaultHeadersMap != null && defaultHeadersMap.size() > 0) {
            //默认需要加的验证
            requestBuilder.headers(Headers.of(defaultHeadersMap));
        }
        if (customHeadersMap != null && customHeadersMap.size() > 0) {
            for (Map.Entry<String, String> entry : customHeadersMap.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        request = requestBuilder.build();
        //
        mOkHttpClient.newCall(request).enqueue(responseCallback);
    }

    public HttpBackMsg<Integer, String, String> doGetSync(String url, Map<String, String> defaultHeadersMap,
                                                          Map<String, String> customHeadersMap, Object tag) {
      /*  Request request=new Request.Builder()
                .url("http://api.lvseeds.com:8080/lvseeds/Melonplant/GetOther")
                .headers(Headers.of(getHeaders()))
                .post(formBody)
                .build();*/
        //
        Request request;
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        if (tag != null) {
            requestBuilder.tag(tag);
        }
        //
        if (defaultHeadersMap != null && defaultHeadersMap.size() > 0) {
            //默认需要加的验证
            requestBuilder.headers(Headers.of(defaultHeadersMap));
        }
        if (customHeadersMap != null && customHeadersMap.size() > 0) {
            for (Map.Entry<String, String> entry : customHeadersMap.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        request = requestBuilder.build();
        //
        //
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                String response_body_result = responseBody.string();
                responseBody.close();
                return HttpBackMsg.create(response.code(), response_body_result, "");
            } else {
                return HttpBackMsg.create(response.code(), response.message(), "");
            }
        } catch (Exception e) {
            return handleException(e);
        }
    }

    public HttpBackMsg<Integer, String, String> doPostSync(String url, Map<String, String> defaultHeadersMap,
                                                           Map<String, String> customHeadersMap,
                                                           String paramsStr, Object tag) {
        //创建一个请求对象
        Request request;
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        if (tag != null) {
            requestBuilder.tag(tag);
        }
        //
        if (defaultHeadersMap != null && defaultHeadersMap.size() > 0) {
            //默认需要加的验证
            requestBuilder.headers(Headers.of(defaultHeadersMap));
        }
        if (customHeadersMap != null && customHeadersMap.size() > 0) {
            for (Map.Entry<String, String> entry : customHeadersMap.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        if (paramsStr != null) {
            RequestBody requestBody = RequestBody.create(MEDIA_TYPE_NORAML_FORM, paramsStr);
            if (requestBody != null) {
                requestBuilder.post(requestBody);
            }
        }
        //
        request = requestBuilder.build();
        //
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                String response_body_result = responseBody.string();
                responseBody.close();
                return HttpBackMsg.create(response.code(), response_body_result, "");
            } else {
                return HttpBackMsg.create(response.code(), response.message(), "");
            }
        } catch (Exception e) {
            return handleException(e);
        }
    }

    public HttpBackMsg<Integer, String, String> doPostJsonStringSync(String url, Map<String, String> defaultHeadersMap,
                                                                     Map<String, String> customHeadersMap, String jsonStr,
                                                                     Object tag) {
        //创建一个请求对象
        Request request;
        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);
        if (tag != null) {
            requestBuilder.tag(tag);
        }
        //
        if (defaultHeadersMap != null && defaultHeadersMap.size() > 0) {
            //默认需要加的验证
            requestBuilder.headers(Headers.of(defaultHeadersMap));
        }
        if (customHeadersMap != null && customHeadersMap.size() > 0) {
            for (Map.Entry<String, String> entry : customHeadersMap.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        if (jsonStr != null) {
            RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, jsonStr);
            if (requestBody != null) {
                requestBuilder.post(requestBody);
            }
        }
        //
        request = requestBuilder.build();
        //
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                ResponseBody responseBody = response.body();
                String response_body_result = responseBody.string();
                responseBody.close();
                return HttpBackMsg.create(response.code(), response_body_result, "");
            } else {
                return HttpBackMsg.create(response.code(), response.message(), "");
            }

        } catch (Exception e) {
            return handleException(e);
        }

    }

}
