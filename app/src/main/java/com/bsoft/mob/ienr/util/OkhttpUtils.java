package com.bsoft.mob.ienr.util;

import android.content.Context;

import android.os.Handler;
import android.os.Looper;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.api.APIUrlConfig;
import com.bsoft.mob.ienr.api.BaseApi;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 请求网络框架
 */

public class OkhttpUtils {
    private static OkhttpUtils mInstance;
    private static OkHttpClient okHttpClient;
    private static Handler okHttpHandler;
    private static Gson gson;

    private OkhttpUtils() {
        this.okHttpHandler = new Handler(Looper.getMainLooper());

    }
    public static OkhttpUtils getInstance()
    {
        if (mInstance == null)
        {
            synchronized (OkhttpUtils.class)
            {
                if (mInstance == null)
                {
                    mInstance = new OkhttpUtils();

                }
            }
        }
        return mInstance;
    }
    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("application/json;charset=utf-8");
    public static void doPost( final String url, final Map<String, String> map,final MyCallback callback)throws NetException,SocketTimeoutException{
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            if (gson==null) {
                gson = new Gson();
            }
            String param = gson.toJson(map);
            okHttpClient= new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .cache(new Cache(new File(AppApplication.getContext().getCacheDir(),"okhttpcache"),5*10240*1024)).build();
            Request request=new Request.Builder()
                            .url(url)
                    .addHeader("Accept","*/*")
                    .cacheControl(new CacheControl.Builder().maxAge(5,TimeUnit.SECONDS)
                    .maxStale(5,TimeUnit.SECONDS).build())
                    .post(RequestBody.create(MEDIA_TYPE_MARKDOWN,param))
                    .build();
//                            .addHeader()
                   Call call=  okHttpClient.newCall(request);
                   call.enqueue(new Callback() {
                       @Override
                       public void onFailure(Call call, IOException e) {
                           emitter.onError(e);
                       }

                       @Override
                       public void onResponse(Call call, Response response) throws IOException {
                           if (response.code()==200) {
                               emitter.onNext(response.body().string());
                               emitter.onComplete();
                           }else{
                               emitter.onError(null);
                           }
                       }
                   });
        }).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<String>() {
                @Override
                public void onSubscribe(Disposable d) {
                    callback.onStart();
                }

                @Override
                public void onNext(String response) {
                    callback.onSuccess(response);

                }

                @Override
                public void onError(Throwable e) {
                    if (e!=null) {
                        callback.onFailture(e.getMessage());
                    }else{
                        callback.onFailture("请求服务器失败");
                    }
                    callback.onFinish();
                }

                @Override
                public void onComplete() {
                    callback.onFinish();
                }
            });
    }
    public static void doGet(final String url,final MyCallback callback)throws NetException{
        Observable.create((ObservableOnSubscribe<String>) emitter -> {
            okHttpClient = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .cache(new Cache(new File(AppApplication.getContext().getCacheDir(),"okhttpcache"),5*10240*1024)).build();
            Request.Builder builder =  new Request.Builder().url(url)
                    .addHeader("Accept","*/*")
                    .addHeader("Content-Type","application/json;charset=utf-8");
            final Request build = builder.build();
            Call call = okHttpClient.newCall(build);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    emitter.onError(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.code()==200) {
                        emitter.onNext(response.body().string());
                        emitter.onComplete();
                    }else{
                        emitter.onError(null);
                    }
                }
            });
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        callback.onStart();
                    }

                    @Override
                    public void onNext(String response) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e!=null) {
                            callback.onFailture(e.getMessage());
                        }else{
                            callback.onFailture("请求服务器失败");
                        }
                        callback.onFinish();
                    }

                    @Override
                    public void onComplete() {
                        callback.onFinish();
                    }
                });

        if (callback!=null){

        }
    }
    public void upLoad(String filePath,String extName,final MyCallback callback){
        String baseUrl= BaseApi.fileUpUrl;
       String fileName= String.valueOf(new Date().getTime());
        String url=new StringBuffer(baseUrl)
                .append(APIUrlConfig.UpLoad)
                .append("extName=")
                .append(extName)
                .toString();
        Observable.create((ObservableOnSubscribe<String>)emitter -> {
            okHttpClient = new OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .cache(new Cache(new File(AppApplication.getContext().getCacheDir(),"okhttpcache"),5*10240*1024)).build();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", fileName,
                            RequestBody.create(MediaType.parse("multipart/form-data"), new File(filePath)))
                    .build();
            Request request = new Request.Builder()
                    .header("Accept", "*/*")
                    .url(url)
                    .post(requestBody)
                    .build();
            Call call=  okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    emitter.onError(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.code()==200) {
                        emitter.onNext(response.body().string());
                        emitter.onComplete();
                    }else{
                        emitter.onError(null);
                    }
                }
            });
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        callback.onStart();
                    }

                    @Override
                    public void onNext(String response) {
                        callback.onSuccess(response);

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e!=null) {
                            callback.onFailture(e.getMessage());
                        }else{
                            callback.onFailture("请求服务器失败");
                        }
                        callback.onFinish();
                    }

                    @Override
                    public void onComplete() {
                        callback.onFinish();
                    }
                });
    }
    public interface MyCallback {
    void onSuccess(String body);
    void onFailture(String e);
    void onStart();
    void onFinish();
}
}
