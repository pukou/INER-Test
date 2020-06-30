package com.bsoft.mob.ienr.http;

import android.text.TextUtils;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.http.client.FileBody;
import com.bsoft.mob.ienr.http.client.MultipartEntity;
import com.bsoft.mob.ienr.http.client.StringBody;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class AppHttpClient {

    private final DefaultHttpClient mHttpClient;
    private AppApplication application;

    public AppHttpClient(AppApplication application) {
        this.application = application;
        SchemeRegistry supportedSchemes = new SchemeRegistry();
        SocketFactory sf = PlainSocketFactory.getSocketFactory();
        supportedSchemes.register(new Scheme("http", sf, 80));
        supportedSchemes.register(new Scheme("https", SSLSocketFactory
                .getSocketFactory(), 443));

        HttpParams httpParams = createHttpParams();

        HttpClientParams.setRedirecting(httpParams, false);
        HttpProtocolParams.setUseExpectContinue(httpParams, false);
        HttpConnectionParams.setConnectionTimeout(httpParams, Constant.HTTP_TIME_OUT );
        HttpConnectionParams.setSoTimeout(httpParams,Constant.HTTP_TIME_OUT );

        final ClientConnectionManager ccm = new ThreadSafeClientConnManager(
                httpParams, supportedSchemes);
        mHttpClient = new DefaultHttpClient(ccm, httpParams);
        // 解决部分PDA在网络不好的情况下重连多次的问题
        mHttpClient
                .setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(
                        3, false));
    }

    /**
     * Create the default HTTP protocol parameters.
     */
    private static final HttpParams createHttpParams() {
        final HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setStaleCheckingEnabled(params, false);

        HttpConnectionParams.setConnectionTimeout(params, Constant.HTTP_TIME_OUT );
        HttpConnectionParams.setSoTimeout(params, Constant.HTTP_TIME_OUT );
        HttpConnectionParams.setSocketBufferSize(params, 8192);

        // 将每个路由的最大连接数增加到200
        ConnManagerParams.setMaxTotalConnections(params, 200);
        // 将每个路由的默认连接数设置为20
        ConnPerRouteBean connPerRoute = new ConnPerRouteBean(20);
        ConnManagerParams.setMaxConnectionsPerRoute(params, connPerRoute);

        return params;
    }

    public String executeHttpRequests(String url) throws Exception {
       // System.out.println("url : " + url);
        HttpGet get = createHttpGet(url);
        if (application != null) {
            get.addHeader("Authorization", application.authorizationString);
            get.addHeader("JSESSIONID", application.JSESSIONID);
        }
        return executeHttpRequests(executeHttpRequest(get));
    }

    public String executeHttpRequests(String url,
                                      NameValuePair... nameValuePairs) throws Exception {
        HttpGet get = createHttpGet(url, nameValuePairs);
        return executeHttpRequests(executeHttpRequest(get));
    }

    public String executeHttpPostRequests(String url, String params)
            throws Exception {
        return doHttpPost(url, params);
    }

    public String executeHttpRequests(HttpResponse response) throws Exception {
        int statusCode = response.getStatusLine().getStatusCode();
        switch (statusCode) {
            case 200:
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            case 400:
                throw new ApiHttpException(response.getStatusLine().toString(),
                        EntityUtils.toString(response.getEntity()));
            case 401:
                response.getEntity().consumeContent();
                handleNotAutheration(response.getStatusLine().getReasonPhrase());
                throw new ApiHttpException(response.getStatusLine().toString());
            case 404:
                response.getEntity().consumeContent();
                throw new ApiHttpException(response.getStatusLine().toString());

            case 500:
                response.getEntity().consumeContent();
                throw new ApiHttpException("Service is down. Try again later.");

            default:
                response.getEntity().consumeContent();
                throw new ApiHttpException("Error connecting to Service: "
                        + statusCode + ". Try again later.");
        }
    }

    public String doHttpPost(String url, NameValuePair... nameValuePairs)
            throws Exception {
        HttpPost httpPost = createHttpPost(url, nameValuePairs);
        HttpResponse response = executeHttpRequest(httpPost);
        switch (response.getStatusLine().getStatusCode()) {
            case 200:
                try {
                    return EntityUtils.toString(response.getEntity());
                } catch (ParseException e) {
                    throw new ApiHttpException(e.getMessage());
                }

            case 401:
                handleNotAutheration(response.getStatusLine().getReasonPhrase());
                response.getEntity().consumeContent();
                throw new ApiHttpException(response.getStatusLine().toString());

            case 404:
                response.getEntity().consumeContent();
                throw new ApiHttpException(response.getStatusLine().toString());

            default:
                response.getEntity().consumeContent();
                throw new ApiHttpException(response.getStatusLine().toString());
        }
    }

    /**
     * execute() an httpRequest catching exceptions and returning null instead.
     *
     * @param httpRequest
     * @return
     * @throws IOException
     */
    public HttpResponse executeHttpRequest(HttpRequestBase httpRequest)
            throws IOException {
        try {
            // 关闭过期的连接
            mHttpClient.getConnectionManager().closeExpiredConnections();
            // 关闭空闲时间超过30秒的连接
            mHttpClient.getConnectionManager().closeIdleConnections(30,
                    TimeUnit.SECONDS);
            return mHttpClient.execute(httpRequest);
        } catch (IOException e) {
            httpRequest.abort();
            throw e;
        }
    }

    public HttpGet createHttpGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        return httpGet;
    }

    public HttpGet createHttpGet(String url, NameValuePair... nameValuePairs) {
        String query = URLEncodedUtils.format(stripNulls(nameValuePairs),
                HTTP.UTF_8);
        HttpGet httpGet = new HttpGet(url + "?" + query);
        return httpGet;
    }

    public HttpPost createHttpPost(String url, NameValuePair... nameValuePairs) {
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(
                    stripNulls(nameValuePairs), HTTP.UTF_8));

        } catch (UnsupportedEncodingException e1) {
            throw new IllegalArgumentException(
                    "Unable to encode http parameters.");
        }
        return httpPost;
    }

    public HttpPost createHttpFilePost(String url,
                                       NameValuePair... nameValuePairs) {
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(
                    stripNulls(nameValuePairs), HTTP.UTF_8));
        } catch (UnsupportedEncodingException e1) {
            throw new IllegalArgumentException(
                    "Unable to encode http parameters.");
        }
        return httpPost;
    }

    private List<NameValuePair> stripNulls(NameValuePair... nameValuePairs) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        for (int i = 0; i < nameValuePairs.length; i++) {
            NameValuePair param = nameValuePairs[i];
            if (param.getValue() != null) {
                params.add(param);
            }
        }
        return params;
    }

    public String doHttpPost(String url, String srcPath,
                             NameValuePair... nameValuePairs) throws Exception {
        HttpPost httpPost = new HttpPost(url);

        MultipartEntity multipartEntity = new MultipartEntity();
        multipartEntity.addPart("file", new FileBody(new File(srcPath)));

        if (null != nameValuePairs) {
            for (int i = 0; i < nameValuePairs.length; i++) {
                multipartEntity.addPart(
                        nameValuePairs[i].getName(),
                        new StringBody(URLEncoder.encode(
                                nameValuePairs[i].getValue(), "UTF-8")));
            }
        }

        httpPost.setEntity(multipartEntity);
        HttpResponse response = mHttpClient.execute(httpPost);

        switch (response.getStatusLine().getStatusCode()) {
            case 200:
                try {
                    return EntityUtils.toString(response.getEntity());
                } catch (ParseException e) {
                    throw new ApiHttpException(e.getMessage());
                }

            case 401:
                handleNotAutheration(response.getStatusLine().getReasonPhrase());
                response.getEntity().consumeContent();
                throw new ApiHttpException(response.getStatusLine().toString());

            case 404:
                response.getEntity().consumeContent();
                throw new ApiHttpException(response.getStatusLine().toString());

            default:
                response.getEntity().consumeContent();
                throw new ApiHttpException(response.getStatusLine().toString());
        }
    }

    public String doHttpPostUTF8(String url, List<NameValuePair> nameValuePairs)
            throws Exception {
        HttpPost httpPost = new HttpPost(url);

        MultipartEntity multipartEntity = new MultipartEntity();

        if (null != nameValuePairs) {
            for (int i = 0; i < nameValuePairs.size(); i++) {
                multipartEntity.addPart(nameValuePairs.get(i).getName(),
                        new StringBody(nameValuePairs.get(i).getValue()));
            }
        }

        httpPost.setEntity(multipartEntity);
        HttpResponse response = mHttpClient.execute(httpPost);

        switch (response.getStatusLine().getStatusCode()) {
            case 200:
                try {
                    return EntityUtils.toString(response.getEntity());
                } catch (ParseException e) {
                    throw new ApiHttpException(e.getMessage());
                }

            case 401:
                handleNotAutheration(response.getStatusLine().getReasonPhrase());
                response.getEntity().consumeContent();
                throw new ApiHttpException(response.getStatusLine().toString());

            case 404:
                response.getEntity().consumeContent();
                throw new ApiHttpException(response.getStatusLine().toString());

            default:
                response.getEntity().consumeContent();
                throw new ApiHttpException(response.getStatusLine().toString());
        }
    }

    private String doHttpPost(String url, String param) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
        if (application != null) {
            httpPost.addHeader("Authorization", application.authorizationString);
            httpPost.addHeader("JSESSIONID", application.JSESSIONID);
        }
        httpPost.setEntity(new StringEntity(param, HTTP.UTF_8));
        HttpResponse response = mHttpClient.execute(httpPost);
        switch (response.getStatusLine().getStatusCode()) {
            case 200:
                try {
                    return EntityUtils.toString(response.getEntity());
                } catch (ParseException e) {
                    throw new ApiHttpException(e.getMessage());
                }
            case 401:
                handleNotAutheration(response.getStatusLine().getReasonPhrase());
                response.getEntity().consumeContent();
                throw new ApiHttpException(response.getStatusLine().toString());
            case 404:
                response.getEntity().consumeContent();
                throw new ApiHttpException(response.getStatusLine().toString());
            default:
                response.getEntity().consumeContent();
                throw new ApiHttpException(response.getStatusLine().toString());
        }
    }

    public String doPostRequestInput(String urlStr, String psotString,
                                     NameValuePair... nameValuePairs) {
        HttpURLConnection uc = null;
        try {
            for (int i = 0; i < nameValuePairs.length; i++) {
                if (i == 0) {
                    urlStr += "?" + nameValuePairs[i].getName() + "=";
                    urlStr += nameValuePairs[i].getValue();
                } else {
                    urlStr += "&" + nameValuePairs[i].getName() + "=";
                    urlStr += nameValuePairs[i].getValue();
                }
            }
            URL url = new URL(urlStr);

            uc = (HttpURLConnection) url.openConnection();
            uc.setRequestProperty("content-type", "application/json");

            uc.setDoInput(true);
            uc.setDoOutput(true);
            uc.setConnectTimeout(1000 * 10);
            uc.setReadTimeout(1000 * 10);
            uc.setRequestMethod("POST");

            uc.getOutputStream().write(psotString.getBytes("UTF-8"));
            uc.getOutputStream().close();

            InputStream content = uc.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    content, "UTF-8"));
            String line = in.readLine();
            if (line != null) {
                return line.trim();
            }
            in.close();
        } catch (FileNotFoundException fe) {
            fe.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (null != uc) {
                uc.disconnect();
            }
        }
        return null;
    }

    private void handleNotAutheration(final String code) {

        Timer timer = new Timer();// 实例化Timer类
        timer.schedule(new TimerTask() {
            public void run() {
                String msg = "";
                if (TextUtils.equals(code, "Error")) {
                    msg = "授权验证出错，错误信息未知";
                } else if (TextUtils.equals(code, "Resolve")) {
                    msg = "授权验证出错，参数解析失败";
                } else if (TextUtils.equals(code, "Illegal")) {
                    msg = "不是授权设备";
                } else if (TextUtils.equals(code, "Authorization")) {
                    msg = "授权验证有误";
                } else if (TextUtils.equals(code, "MaxAuthority")) {
                    msg = "超过最大的授权设备数";
                } else if (TextUtils.equals(code, "Expired")) {
                    msg = "授权到期";
                } else if (TextUtils.equals(code, "ServiceNotRegisted")) {
                    msg = "服务未注册";
                } else {
                    msg = "授权出错，但服务端未知名错误类型";
                }
                application.reboot(msg);
                this.cancel();
            }
        }, 500);
    }
}
