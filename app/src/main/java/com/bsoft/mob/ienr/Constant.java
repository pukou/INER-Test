package com.bsoft.mob.ienr;


public class Constant {

    public static final String TAG = "bsienr";
    public static final String DEFAULT_STRING_NEGATIVE = "-666";
    public static final String DEFAULT_STRING_EMPTY = "";
    public static final String DEFAULT_STRING_NULL = null;
    /**
     * 访问后台接口所需，后台以此值区别android 和window mobile客户端
     */
    public static final int sysType = 1;
    /**
     * 本地调试
     */
    public static final boolean DEBUG_LOCAL = false;
    /**
     * 输出URI
     */
    public static final boolean LOG_URI = true;

//    public static final boolean DEBUG = BuildConfig.DEBUG_LOCAL;
    public static final boolean DEBUG = BuildConfig.DEBUG;//用户
    public static final boolean DEBUG_DEVELOP_TEST_DATA = false;

    public static final String TAG_COMM = "zfq";
    public static final String CHARSET_COMM = "utf-8";


    public static final long SECOND_IN_MILLIS = 1000;
    public static final long MINUTE_IN_MILLIS = SECOND_IN_MILLIS * 60;
    public static final long HOUR_IN_MILLIS = MINUTE_IN_MILLIS * 60;
    public static final long DAY_IN_MILLIS = HOUR_IN_MILLIS * 24;
    public static final long WEEK_IN_MILLIS = DAY_IN_MILLIS * 7;
    //
    public static final int HTTP_TIME_OUT = (int) (60 * SECOND_IN_MILLIS);
    public static final String ACTION_SHOW_UPDATE="ACTION_SHOW_UPDATE";
    public static final String ACTION_DOWNLOAD_UPDATE="ACTION_DOWNLOAD_UPDATE";
    // public static final int HTTP_TIME_OUT = (int) (100);

}