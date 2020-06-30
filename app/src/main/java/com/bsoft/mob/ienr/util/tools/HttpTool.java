package com.bsoft.mob.ienr.util.tools;

import android.os.Environment;
import android.util.Log;
import android.util.Pair;

import com.bsoft.mob.ienr.Constant;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by louisgeek on 2016/11/5.
 */

public class HttpTool {

    private static final String TAG = "HttpTool";
    private static final int HTTP_CONN_TIME_OUT = Constant.HTTP_TIME_OUT;
    private static ExecutorService mExecutorService = Executors.newFixedThreadPool(5);

    public static void postUrlBackString(final String webUrl, final String paramsStr, final Map<String, String> headersMap, final OnUrlBackStringCallBack onUrlBackStringCallBack) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                //
                postUrlBackStr(webUrl, paramsStr, headersMap, onUrlBackStringCallBack);
                //
            }
        });
    }

    /**
     * @param webUrl
     * @param paramsStr
     * @return
     */
    private static void postUrlBackStr(String webUrl, String paramsStr, Map<String, String> headersMap, OnUrlBackStringCallBack onUrlBackStringCallBack) {
        boolean isSuccess = false;
        String message;
        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        try {
            URL url = new URL(webUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设定请求的方法为"POST"，默认是GET
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(HTTP_CONN_TIME_OUT);
            connection.setReadTimeout(HTTP_CONN_TIME_OUT);
            // User-Agent  IE11 的标识
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.3; Trident/7.0;rv:11.0) like Gecko");
            connection.setRequestProperty("Accept-Language", "zh-CN");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", "UTF-8");
            //set header
            if (headersMap != null) {
                for (String key : headersMap.keySet()) {
                    connection.setRequestProperty(key, headersMap.get(key));
                }
            }
            /**
             * 当我们要获取我们请求的http地址访问的数据时就是使用connection.getInputStream().read()方式时我们就需要setDoInput(true)，
             根据api文档我们可知doInput默认就是为true。我们可以不用手动设置了，如果不需要读取输入流的话那就setDoInput(false)。

             当我们要采用非get请求给一个http网络地址传参 就是使用connection.getOutputStream().write() 方法时我们就需要setDoOutput(true), 默认是false
             */
            // 设置是否从httpUrlConnection读入，默认情况下是true;
            connection.setDoInput(true);
            // 设置是否向httpUrlConnection输出，如果是post请求，参数要放在http正文内，因此需要设为true, 默认是false;
            connection.setDoOutput(true);
            connection.setUseCaches(false);


            /**
             * the first way to set  params
             * OutputStream
             */
         /*   byte[] bytesParams = paramsStr.getBytes();
            // 发送请求params参数
            OutputStream outStream=connection.getOutputStream();
            outStream.write(bytesParams);
            outStream.flush();
            */

            /**
             * the second  way  to set  params
             * PrintWriter
             */
           /* PrintWriter printWriter = new PrintWriter(connection.getOutputStream());
            //PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(),"UTF-8"));
            // 发送请求params参数
            printWriter.write(paramsStr);
            printWriter.flush();*/

            /**
             * the third way to set  params
             * OutputStreamWriter
             */
            OutputStreamWriter out = new OutputStreamWriter(
                    connection.getOutputStream(), "UTF-8");
            // 发送请求params参数
            out.write(paramsStr);
            out.flush();


            connection.connect();//
            int contentLength = connection.getContentLength();
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();//会隐式调用connect()
                baos = new ByteArrayOutputStream();
                int readLen;
                byte[] bytes = new byte[1024];
                while ((readLen = inputStream.read(bytes)) != -1) {
                    baos.write(bytes, 0, readLen);
                }
                String backStr = baos.toString();
                Log.i(TAG, "backStr:" + backStr);

                message = backStr;
                isSuccess = true;
            } else {
                // Log.e(TAG, "请求失败 code:" + connection.getResponseCode());
                message = "请求失败 code:" + connection.getResponseCode();
            }

        } catch (ConnectException e) {
            message = "服务器请求超时:" + e.getMessage();
        } catch (SocketTimeoutException e) {
            message = "服务器响应超时:" + e.getMessage();
        } catch (Exception e) {
            message = e.getMessage();
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                message = e.getMessage();
                e.printStackTrace();
            }
        }
        boolean finalIsSuccess = isSuccess;
        String finalMessage = message;
        //
        ThreadTool.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //
                if (finalIsSuccess) {
                    onUrlBackStringCallBack.onSuccess(finalMessage);
                } else {
                    onUrlBackStringCallBack.onError(finalMessage);
                }

            }
        });


    }

    public static Pair<Boolean, String> postUrlBackStringSync(String webUrl, Map<String, String> headersMap, String paramsStr) {
        boolean isSuccess = false;
        String message;
        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        try {
            URL url = new URL(webUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设定请求的方法为"POST"，默认是GET
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(HTTP_CONN_TIME_OUT);
            connection.setReadTimeout(HTTP_CONN_TIME_OUT);
            // User-Agent  IE11 的标识
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.3; Trident/7.0;rv:11.0) like Gecko");
            connection.setRequestProperty("Accept-Language", "zh-CN");
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Charset", "UTF-8");
            //set header
            if (headersMap != null) {
                for (String key : headersMap.keySet()) {
                    connection.setRequestProperty(key, headersMap.get(key));
                }
            }
            /**
             * 当我们要获取我们请求的http地址访问的数据时就是使用connection.getInputStream().read()方式时我们就需要setDoInput(true)，
             根据api文档我们可知doInput默认就是为true。我们可以不用手动设置了，如果不需要读取输入流的话那就setDoInput(false)。

             当我们要采用非get请求给一个http网络地址传参 就是使用connection.getOutputStream().write() 方法时我们就需要setDoOutput(true), 默认是false
             */
            // 设置是否从httpUrlConnection读入，默认情况下是true;
            connection.setDoInput(true);
            // 设置是否向httpUrlConnection输出，如果是post请求，参数要放在http正文内，因此需要设为true, 默认是false;
            connection.setDoOutput(true);
            connection.setUseCaches(false);


            /**
             * set  params one way   OutputStream
             */
         /*   byte[] bytesParams = paramsStr.getBytes();
            // 发送请求params参数
            OutputStream outputStream=connection.getOutputStream()
            outputStream.write(bytesParams);
            outputStream.flush();
            */

            /**
             * set  params two way  PrintWriter
             */
           /* PrintWriter printWriter = new PrintWriter(connection.getOutputStream());
            //PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(),"UTF-8"));
            // 发送请求params参数
            printWriter.write(paramsStr);
            printWriter.flush();*/

            /**
             * set  params three way  OutputStreamWriter
             */
            OutputStreamWriter out = new OutputStreamWriter(
                    connection.getOutputStream(), "UTF-8");
            // 发送请求params参数
            out.write(paramsStr);
            out.flush();


            connection.connect();//
            int contentLength = connection.getContentLength();
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();//会隐式调用connect()
                baos = new ByteArrayOutputStream();
                int readLen;
                byte[] bytes = new byte[1024];
                while ((readLen = inputStream.read(bytes)) != -1) {
                    baos.write(bytes, 0, readLen);
                }
                String backStr = baos.toString();
                Log.i(TAG, "backStr:" + backStr);
                String responseCookie = connection.getHeaderField("Set-Cookie");//取到所有的Cookie
                if (responseCookie != null) {
                    //取出sessionId
                    String sessionId = responseCookie.substring(0, responseCookie.indexOf(";"));
                    Log.i(TAG, " responseCookie sessionId:" + sessionId);
                }


                message = backStr;
                isSuccess = true;
            } else {
                // Log.e(TAG, "请求失败 code:" + connection.getResponseCode());
                message = "请求失败 code:" + connection.getResponseCode();
            }

        } catch (ConnectException e) {
            message = "服务器请求超时:" + e.getMessage();
        } catch (SocketTimeoutException e) {
            message = "服务器响应超时:" + e.getMessage();
        } catch (Exception e) {
            message = e.getMessage();
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                message = e.getMessage();
                e.printStackTrace();
            }
        }
        return Pair.create(isSuccess, message);
    }

    public static void getUrlBackString(final String webUrl, final Map<String, String> headersMap, final OnUrlBackStringCallBack onUrlBackStringCallBack) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                //
                getUrlBackStr(webUrl, headersMap, onUrlBackStringCallBack);
                //
            }
        });
       /* new Thread(new Runnable() {
            @Override
            public void run() {
                //
                getUrlBackStr(webUrl, headersMap, onUrlBackStringCallBack);
                //
            }
        }).start();*/
    }

    private static void getUrlBackStr(String webUrl, Map<String, String> headersMap, OnUrlBackStringCallBack onUrlBackStringCallBack) {
        boolean isSuccess = false;
        String message;

        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        try {
            URL url = new URL(webUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设定请求的方法为"POST"，默认是GET
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(HTTP_CONN_TIME_OUT);
            connection.setReadTimeout(HTTP_CONN_TIME_OUT);
            // User-Agent  IE11 的标识
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.3; Trident/7.0;rv:11.0) like Gecko");
            connection.setRequestProperty("Accept-Language", "zh-CN");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Connection", "Keep-Alive");
            //set header
            if (headersMap != null) {
                for (String key : headersMap.keySet()) {
                    connection.setRequestProperty(key, headersMap.get(key));
                }
            }
            /**
             * 当我们要获取我们请求的http地址访问的数据时就是使用connection.getInputStream().read()方式时我们就需要setDoInput(true)，
             根据api文档我们可知doInput默认就是为true。我们可以不用手动设置了，如果不需要读取输入流的话那就setDoInput(false)。

             当我们要采用非get请求给一个http网络地址传参 就是使用connection.getOutputStream().write() 方法时我们就需要setDoOutput(true), 默认是false
             */
            // 设置是否从httpUrlConnection读入，默认情况下是true;
            connection.setDoInput(true);
            // 设置是否向httpUrlConnection输出，如果是post请求，参数要放在http正文内，因此需要设为true, 默认是false;
            //connection.setDoOutput(true);//Android  4.0 GET时候 用这句会变成POST  报错java.io.FileNotFoundException
            connection.setUseCaches(false);
            connection.connect();//
            int contentLength = connection.getContentLength();
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();//会隐式调用connect()
                baos = new ByteArrayOutputStream();
                int readLen;
                byte[] bytes = new byte[1024];
                while ((readLen = inputStream.read(bytes)) != -1) {
                    baos.write(bytes, 0, readLen);
                }
                String backStr = baos.toString();
                Log.i(TAG, " backStr:" + backStr);

                message = backStr;
                isSuccess = true;
            } else {
                //Log.e(TAG, "请求失败 code:" + connection.getResponseCode());
                message = "请求失败 code:" + connection.getResponseCode();
            }

        } catch (ConnectException e) {
            message = "服务器请求超时:" + e.getMessage();
        } catch (SocketTimeoutException e) {
            message = "服务器响应超时:" + e.getMessage();
        } catch (Exception e) {
            message = e.getMessage();
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                message = e.getMessage();
                e.printStackTrace();
            }
        }

        boolean finalIsSuccess = isSuccess;
        String finalMessage = message;
        //
        ThreadTool.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //
                if (finalIsSuccess) {
                    onUrlBackStringCallBack.onSuccess(finalMessage);
                } else {
                    onUrlBackStringCallBack.onError(finalMessage);
                }

            }
        });

    }


    public static Pair<Boolean, String> getUrlBackStringSync(String webUrl, Map<String, String> headersMap) {
        boolean isSuccess = false;
        String message;

        InputStream inputStream = null;
        ByteArrayOutputStream baos = null;
        try {
            URL url = new URL(webUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设定请求的方法为"POST"，默认是GET
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(HTTP_CONN_TIME_OUT);
            connection.setReadTimeout(HTTP_CONN_TIME_OUT);
            // User-Agent  IE11 的标识
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.3; Trident/7.0;rv:11.0) like Gecko");
            connection.setRequestProperty("Accept-Language", "zh-CN");
            connection.setRequestProperty("Charset", "UTF-8");
            connection.setRequestProperty("Connection", "Keep-Alive");
            //set header
            if (headersMap != null) {
                for (String key : headersMap.keySet()) {
                    connection.setRequestProperty(key, headersMap.get(key));
                }
            }

            /**
             * 当我们要获取我们请求的http地址访问的数据时就是使用connection.getInputStream().read()方式时我们就需要setDoInput(true)，
             根据api文档我们可知doInput默认就是为true。我们可以不用手动设置了，如果不需要读取输入流的话那就setDoInput(false)。

             当我们要采用非get请求给一个http网络地址传参 就是使用connection.getOutputStream().write() 方法时我们就需要setDoOutput(true), 默认是false
             */
            // 设置是否从httpUrlConnection读入，默认情况下是true;
            connection.setDoInput(true);
            // 设置是否向httpUrlConnection输出，如果是post请求，参数要放在http正文内，因此需要设为true, 默认是false;
            //connection.setDoOutput(true);//Android  4.0 GET时候 用这句会变成POST  报错java.io.FileNotFoundException
            connection.setUseCaches(false);
            connection.connect();//
            int contentLength = connection.getContentLength();
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();//会隐式调用connect()
                baos = new ByteArrayOutputStream();
                int readLen;
                byte[] bytes = new byte[1024];
                while ((readLen = inputStream.read(bytes)) != -1) {
                    baos.write(bytes, 0, readLen);
                }
                String backStr = baos.toString();
                Log.i(TAG, " backStr:" + backStr);
                String responseCookie = connection.getHeaderField("Set-Cookie");//取到所用的Cookie
                if (responseCookie != null) {
                    //取出sessionId
                    String sessionId = responseCookie.substring(0, responseCookie.indexOf(";"));
                    Log.i(TAG, " responseCookie sessionId:" + sessionId);
                }

                message = backStr;
                isSuccess = true;
            } else {
                //Log.e(TAG, "请求失败 code:" + connection.getResponseCode());
                message = "请求失败 code:" + connection.getResponseCode();
            }

        } catch (ConnectException e) {
            message = "服务器请求超时:" + e.getMessage();
        } catch (SocketTimeoutException e) {
            message = "服务器响应超时:" + e.getMessage();
        } catch (Exception e) {
            message = e.getMessage();
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                message = e.getMessage();
                e.printStackTrace();
            }
        }
        return Pair.create(isSuccess, message);
    }

    /**
     * private static String sd_path = Environment.getExternalStorageDirectory() + File.separator;
     * private static String filePath = String.format("%sMyFileDir%sTest%s", sd_path, File.separator, File.separator);
     * private static String saveFileAllName = filePath + "app.apk";
     * <p>
     * getDownloadFile: sd_path:/storage/emulated/0/
     * getDownloadFile: filePath:/storage/emulated/0/MyFileDir/Test/
     * getDownloadFile: saveFileAllName:/storage/emulated/0/MyFileDir/Test/app.apk
     */
    public static void getUrlDownloadFile(String webUrl, OnUrlDownloadFileCallBack onUrlDownloadFileCallBack) {
        String sd_path = Environment.getExternalStorageDirectory() + File.separator;
        String filePath = String.format("%sMyFileDir%sDownFile%s", sd_path, File.separator, File.separator);
        String fileNameWithExt = "app.apk";
        getUrlDownloadFile(webUrl, filePath, fileNameWithExt, onUrlDownloadFileCallBack);
    }

    public static void getUrlDownloadFile(final String webUrl, final String filePath, final String fileNameWithExt, final OnUrlDownloadFileCallBack onUrlDownloadFileCallBack) {
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                ///////////
                int mPogressTemp = 0;
                boolean isSuccess = false;
                String message;

                if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
                    //Log.e(TAG, "getDownloadFile: 无SD卡");
                    message = "SD卡有误";
                    //
                    String finalMessage = message;
                    //
                    ThreadTool.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //
                           onUrlDownloadFileCallBack.onError(finalMessage);
                        }
                    });
                    return;
                }
                String saveFileAllPath = filePath + fileNameWithExt;
                //
                InputStream inputStream = null;
                FileOutputStream fileOutputStream = null;
                try {
                    URL url = new URL(webUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    // 设定请求的方法为"POST"，默认是GET
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(HTTP_CONN_TIME_OUT);
                    connection.setReadTimeout(HTTP_CONN_TIME_OUT);
                    // User-Agent  IE11 的标识
                    connection.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.3; Trident/7.0;rv:11.0) like Gecko");
                    connection.setRequestProperty("Accept-Language", "zh-CN");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("Charset", "UTF-8");
                    /**
                     * 当我们要获取我们请求的http地址访问的数据时就是使用connection.getInputStream().read()方式时我们就需要setDoInput(true)，
                     根据api文档我们可知doInput默认就是为true。我们可以不用手动设置了，如果不需要读取输入流的话那就setDoInput(false)。

                     当我们要采用非get请求给一个http网络地址传参 就是使用connection.getOutputStream().write() 方法时我们就需要setDoOutput(true), 默认是false
                     */
                    // 设置是否从httpUrlConnection读入，默认情况下是true;
                    connection.setDoInput(true);
                    // 设置是否向httpUrlConnection输出，如果是post请求，参数要放在http正文内，因此需要设为true, 默认是false;
                    //connection.setDoOutput(true);
                    connection.setUseCaches(false);

                    connection.connect();//
                    int contentLength = connection.getContentLength();

                    if (connection.getResponseCode() == 200) {

                        File file_dir = new File(filePath);
                        // 判断文件目录是否存在
                        if (!file_dir.exists()) {
                            file_dir.mkdirs();
                        }
                        //file.mkdir();//只能生成单层目录

                        inputStream = connection.getInputStream();//会隐式调用connect()

                        File myFile = new File(saveFileAllPath);
                /*if (!myFile.exists()){
                    myFile.mkdir();
                }*/
                        //输出流
                        fileOutputStream = new FileOutputStream(myFile);

                        long totalReaded = 0;
                        int readLen;
                        byte[] bytes = new byte[1024];
                        while ((readLen = inputStream.read(bytes)) != -1) {
                            //
                            totalReaded += readLen;
                            // Log.i("XXXX", "totalReaded:" + totalReaded);
                            final long progress = totalReaded * 100 / contentLength;
                            // Log.i("XXXX", "progress:" + progress);
                            //
                            fileOutputStream.write(bytes, 0, readLen);
                            //
                            int progressInt = (int) progress;

                            //更新不要太频繁  进度会卡慢
                            if (progress != mPogressTemp) {
                                mPogressTemp = progressInt;
                                /**
                                 *
                                 */
                                //
                                int finalProgressInt = progressInt;
                                //
                                ThreadTool.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //
                                        onUrlDownloadFileCallBack.OnProgress(finalProgressInt);
                                    }
                                });
                            }


                        }
                        //下载完成
                        String savedFilePath = myFile.getAbsolutePath();
                        Log.i(TAG, "getDownloadFile: savedFilePath:" + savedFilePath);
                        isSuccess = true;
                        message = savedFilePath;
                    } else {
                        //Log.e(TAG, "getDownloadFile: 请求失败 code:" + connection.getResponseCode());
                        message = "请求失败code:" + connection.getResponseCode();
                    }


                } catch (Exception e) {
                    message = e.getMessage();
                    e.printStackTrace();
                } finally {
                    try {
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                        if (inputStream != null) {
                            inputStream.close();
                        }
                    } catch (IOException e) {
                        message = e.getMessage();
                        e.printStackTrace();
                    }
                }

                boolean finalIsSuccess = isSuccess;
                String finalMessage = message;
                //
                ThreadTool.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //
                        if (finalIsSuccess) {
                            onUrlDownloadFileCallBack.onSuccess(finalMessage);
                        } else {
                            onUrlDownloadFileCallBack.onError(finalMessage);
                        }

                    }
                });

                /////////////////
            }
        });

    }

    public interface OnUrlDownloadFileCallBack {
        void onSuccess(String savedFilePath);

        void onError(String errorMsg);

        void OnProgress(int progress);
    }

    public interface OnUrlBackStringCallBack {
        void onSuccess(String backStr);

        void onError(String errorMsg);
    }

}
