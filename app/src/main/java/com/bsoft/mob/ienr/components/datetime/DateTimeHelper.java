package com.bsoft.mob.ienr.components.datetime;


import android.os.SystemClock;
import android.util.Log;

import java.util.Calendar;

/**
 * 要点1
 * 支持传入服务端时间计数 后期使用并且不随用户手动修改系统时间影响，如果是 Android 可以不用系统时间，直接用 SystemClock.elapsedRealtime()  结合服务器时间取值
 * 要点2
 * 服务器时间大体分为 【服务器系统时间】和【服务器数据库时间】，
 * 一般需要提供特别准备的准确的【服务器数据库时间】
 */
public class DateTimeHelper {
    private static final String TAG = "DateTimeHelper";

    private static long mLastServerDateTimeInMillis = 0L;
    //系统启动到现在的毫秒数，包含休眠时间,不随用户修改系统设置而改变
    private static long mLastGetElapsedRealtimeInMillis = 0L;

    static {
        mLastGetElapsedRealtimeInMillis = SystemClock.elapsedRealtime();
        //未[initServerDateTime]时默认为系统时间
        mLastServerDateTimeInMillis = System.currentTimeMillis();
    }

    /**
     * 初始化 服务器系统北京时间
     *
     * @param serverDateTime
     */
    public static void initServerDateTime(String serverDateTime) {

        mLastGetElapsedRealtimeInMillis = SystemClock.elapsedRealtime();
        //服务器时间
        mLastServerDateTimeInMillis =
                DateTimeFactory.getInstance().dateTime2DateTimeInMillis(serverDateTime);
    }


    public static long getServerDateTimeInMillis() {
        //系统启动到现在的毫秒数，包含休眠时间,不随用户修改系统设置而改变
        long nowGetElapsedRealtimeInMillis = SystemClock.elapsedRealtime();
        //流逝的时间/走过的时间
        long elapsedTime = nowGetElapsedRealtimeInMillis - mLastGetElapsedRealtimeInMillis;
        //推算出现在的服务器时间
        return mLastServerDateTimeInMillis + elapsedTime;
    }

    public static String getServerDateTime() {
        long nowServerDateTimeInMillis = getServerDateTimeInMillis();
        String nowServerDateTime = DateTimeFactory.getInstance().getDateTime(nowServerDateTimeInMillis);
        String nowSystemDateTime = DateTimeFactory.getInstance().getDateTime();
        Log.e(TAG, "\n服务器时间: " + nowServerDateTime);
        Log.e(TAG, "\n客户端时间: " + nowSystemDateTime);
        return nowServerDateTime;
    }

    public static String getServerDate() {
        long nowServerDateTimeInMillis = getServerDateTimeInMillis();
        String nowServerDate = DateTimeFactory.getInstance().getDate(nowServerDateTimeInMillis);
        String nowSystemDate = DateTimeFactory.getInstance().getDate();
        Log.e(TAG, "\n服务器日期: " + nowServerDate);
        Log.e(TAG, "\n客户端日期: " + nowSystemDate);
        return nowServerDate;
    }

    public static int compareTo(String anotherDateTime) {
        return DateTimeFactory.getInstance().compareTo(getServerDateTime(), anotherDateTime);
    }

    public static boolean dateTimeBefore(String anotherDateTime) {
        return DateTimeFactory.getInstance().dateTimeBefore(getServerDateTime(), anotherDateTime);
    }

    public static boolean dateTimeAfter(String anotherDateTime) {
        return DateTimeFactory.getInstance().dateTimeAfter(getServerDateTime(), anotherDateTime);
    }

    public static boolean dateBefore(String anotherDate) {
        return DateTimeFactory.getInstance().dateBefore(getServerDate(), anotherDate);
    }

    public static boolean dateAfter(String anotherDate) {
        return DateTimeFactory.getInstance().dateAfter(getServerDate(), anotherDate);
    }

    public static YmdHMs dateTime2YmdHMs() {
        YmdHMs ymdHMs = DateTimeFactory.getInstance().dateTime2YmdHMs(getServerDateTime());
        return ymdHMs;
    }

    public static String YmdHMs2DateTime(YmdHMs ymdHMs) {
        int year = ymdHMs.year;
        int month = ymdHMs.month;
        int day = ymdHMs.day;
        int hour = ymdHMs.hour;
        int minute = ymdHMs.minute;
        String dateTime = DateTimeFactory.getInstance().
                ymdhms2DateTime(year, month, day, hour, minute, 0);
        return dateTime;
    }

    public static YmdHMs dateTime2YmdHMs(String dateTime) {
        if (DateTimeFormat.Judge.is_yyyy_MM_dd_HH_mm(dateTime)) {
            dateTime = dateTime + ":00";
            return DateTimeFactory.getInstance().dateTime2YmdHMs(dateTime);
        }
        return DateTimeFactory.getInstance().dateTime2YmdHMs(dateTime);
    }


    public static YmdHMs date2YmdHMs() {
        YmdHMs ymdHMs = DateTimeFactory.getInstance().date2Ymd(getServerDate());
        return ymdHMs;
    }

    public static YmdHMs date2YmdHMs(String date) {
        YmdHMs ymdHMs = DateTimeFactory.getInstance().date2Ymd(date);
        return ymdHMs;
    }

    public static String getServer_yyyyMMddHHmm00() {
        String temp = DateTimeFactory.getInstance().dateTime2Custom(getServerDateTime(),
                DateTimeFormat.yyyy_MM_dd_HHmm) + ":00";
        return temp;
    }

    public static String getServer_yyyyMMddHHmm00(String dateTime) {
        if (DateTimeFormat.Judge.is_yyyy_MM_dd_HH_mm(dateTime)) {
            dateTime = dateTime + ":00";
            return dateTime;
        }
        String temp = DateTimeFactory.getInstance().dateTime2Custom(dateTime,
                DateTimeFormat.yyyy_MM_dd_HHmm) + ":00";
        return temp;
    }

    public static String dateAddedDays(String strDate, int days) {
        String strDateTime = DateTimeFactory.getInstance().date2DateTime(strDate);
        String dateTime = DateTimeFactory.getInstance().dateTimeAddDays(strDateTime, days);
        String date = DateTimeFactory.getInstance().dateTime2Date(dateTime);
        return date;
    }

    public static String dateTimeAddedDays(String strDateTime, int days) {
        if (DateTimeFormat.Judge.is_yyyy_MM_dd_HH_mm(strDateTime)) {
            strDateTime = strDateTime + ":00";
        }
        String dateTime = DateTimeFactory.getInstance().dateTimeAddDays(strDateTime, days);
        return dateTime;
    }

    public static String dateTimeAddedHours(String strDateTime, int hours) {
        if (DateTimeFormat.Judge.is_yyyy_MM_dd_HH_mm(strDateTime)) {
            strDateTime = strDateTime + ":00";
        }
        String dateTime = DateTimeFactory.getInstance().dateTimeAddHours(strDateTime, hours);
        return dateTime;
    }

    public static String dateTimeAddedMinutes(String strDateTime, int minutes) {
        if (DateTimeFormat.Judge.is_yyyy_MM_dd_HH_mm(strDateTime)) {
            strDateTime = strDateTime + ":00";
        }
        String dateTime = DateTimeFactory.getInstance().dateTimeAddMinutes(strDateTime, minutes);
        return dateTime;
    }

    public static String dateTimeAddedSeconds(String strDateTime, int seconds) {
        if (DateTimeFormat.Judge.is_yyyy_MM_dd_HH_mm(strDateTime)) {
            strDateTime = strDateTime + ":00";
        }
        String dateTime = DateTimeFactory.getInstance().dateTimeAddSeconds(strDateTime, seconds);
        return dateTime;
    }
    public static String dateTimeAddMilliseconds(String serverDateTime, int milliseconds) {
        Calendar calendar = DateTimeTool.dateTime2Calendar(serverDateTime);
        calendar.add(Calendar.MILLISECOND, milliseconds);
        return DateTimeTool.calendar2DateTime(calendar);
    }
}
