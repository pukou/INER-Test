package com.bsoft.mob.ienr.components.datetime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * 要点1
 * Date 是没有时区概念的，只是存放的当前时间和 1970。。。的时间差,代码中尽量少用 Date ，
 * 用 Calendar 代替，用 Calendar 的 API 去处理时间加减、时间比较等
 * 要点2
 * 当 Windows 设置【北京、重庆、香港特别行政区、乌鲁木齐】时,
 * TimeZone.getDefault().getID() 返回是 【Asia/Shanghai】，可以考虑 TimeZone.setDefault() 修改时区，
 * 但是推荐还是每次获取时候明确设置最保险，也比较放心
 * 要点3
 * Calendar 是抽象类，需要通过不同国家地区、时区对应的实现类去使用
 * 设置完时区后，此时不能用 calendar.getTime() 来直接获取 Date，需要用 calendar.get(Calendar.YEAR)
 * 等方法单独获取各种时间，calendar 的 setTime 和 getTime 用于操作和时区无关的 入 和 出，
 * 存放的是没有时区概念的 Date 的 milliseconds 值  或者用 String.format 格式化 calendar 处理得到当前时间戳
 * 要点4
 * 支持传入，服务器时间 e.g. 2018-06-15 20:33:55  转换 ，此时场景只考虑 转换，不考虑 B S 时间差
 * 要点5
 * 通过 Calendar.MONTH 得到的月份是从 0 开始的,部分地方使用需要注意
 */
public class DateTimeTool {
    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("Asia/Shanghai");
    //    private static TimeZone TIME_ZONE = TimeZone.getDefault();

    public static class Format {

        public static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
        public static final String FORMAT_DATE = "yyyy-MM-dd";
        public static final String FORMAT_TIME = "HH:mm:ss";

        public String HHmm = "HH:mm";
        public String MM_dd = "MM-dd";
        public String MM_dd_HHmm = "MM-dd HH:mm";
        public String yyyy_MM_dd_HHmm = "yyyy-MM-dd HH:mm";
        public String yyyy_MM_dd_HHmmss_S = "yyyy-MM-dd HH:mm:ss.S";
        public String yyyy_MM_dd_HHmmSS = "yyyy-MM-dd HH:mm:SS";
        //
        public String yyyy_MM_dd_T_HHmm = "yyyy-MM-dd'T'HH:mm";
        public String yyyy_MM_dd_T_HHmmss_SSSZ = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
        public String yyyy_MM_dd_T_HHmmss_Z = "yyyy-MM-dd'T'HH:mm:ssZ";
    }

    private DateTimeTool() {
        System.out.println("北京时区 TIME_ZONE.getID():" + TIME_ZONE.getID());
    }

    @Deprecated
    public static DateFormat getDateFormat(String pattern) {
        DateFormat dateTimeFormat = new SimpleDateFormat(pattern, Locale.CHINA);
        dateTimeFormat.setTimeZone(TIME_ZONE);//这里设置时区最关键
        return dateTimeFormat;
    }

    //本机系统时间 会随用户设置而改变
    public static String getDateTime() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeZone(TIME_ZONE);
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar2DateTime(calendar);
    }

    public static String getDateTime(long timeInMillis) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeZone(TIME_ZONE);
        calendar.setTimeInMillis(timeInMillis);
        return calendar2DateTime(calendar);
    }

    //本机系统时间 会随用户设置而改变
    public static String getDate() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeZone(TIME_ZONE);
        calendar.setTimeInMillis(System.currentTimeMillis());
        return calendar2Date(calendar);
    }

    public static String getDate(long timeInMillis) {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeZone(TIME_ZONE);
        calendar.setTimeInMillis(timeInMillis);
        return calendar2Date(calendar);
    }

    /**
     * 此方法只考虑处理时间转换，不考虑时间是否需要修正
     * 如果有准确时间需求的，这时候入参就要准确时间
     * e.g. sqlTime "2018-06-15"  是准确的时间
     *
     * @param serverDate "yyyy-MM-dd" "2018-06-15"
     * @return
     */
    public static Calendar date2Calendar(String serverDate) {
        DateFormat dateFormat = getDateFormat(Format.FORMAT_DATE);
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeZone(TIME_ZONE);
        try {
            calendar.setTime(dateFormat.parse(serverDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    /**
     * 此方法只考虑处理时间转换，不考虑时间是否需要修正
     * 如果有准确时间需求的，这时候入参就要准确时间
     * e.g. sqlTime "2018-06-15 20:33:55"  是准确的时间
     *
     * @param serverDateTime "yyyy-MM-dd HH:mm:ss" "2018-06-15 20:33:55"
     * @return
     */
    public static Calendar dateTime2Calendar(String serverDateTime) {
        DateFormat dateFormat = getDateFormat(Format.FORMAT_DATE_TIME);
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeZone(TIME_ZONE);
        try {
            calendar.setTime(dateFormat.parse(serverDateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }
    public static String dateTimeAddDays(String serverDateTime, int days) {
        Calendar calendar = dateTime2Calendar(serverDateTime);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        return calendar2DateTime(calendar);
    }
    public static String dateTimeAddHours(String serverDateTime, int hours) {
        Calendar calendar = dateTime2Calendar(serverDateTime);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar2DateTime(calendar);
    }
    public static String dateTimeAddMinutes(String serverDateTime, int minutes) {
        Calendar calendar = dateTime2Calendar(serverDateTime);
        calendar.add(Calendar.MINUTE, minutes);
        return calendar2DateTime(calendar);
    }
    public static String dateTimeAddSeconds(String serverDateTime, int seconds) {
        Calendar calendar = dateTime2Calendar(serverDateTime);
        calendar.add(Calendar.SECOND, seconds);
        return calendar2DateTime(calendar);
    }

    public static Calendar ymd2Calendar(int year, int month, int dayOfMonth) {
        int monthOfYear = month < 1 ? 0 : month - 1;
        //
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeZone(TIME_ZONE);
        calendar.set(year, monthOfYear, dayOfMonth);
        return calendar;
    }

    public static Calendar ymdhms2Calendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
        int monthOfYear = month < 1 ? 0 : month - 1;
        //
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeZone(TIME_ZONE);
        calendar.set(year, monthOfYear, dayOfMonth, hourOfDay, minute, second);
        return calendar;
    }

    public static String ymd2Date(int year, int month, int dayOfMonth) {
        return calendar2Date(ymd2Calendar(year, month, dayOfMonth));
    }

    public static String ymdhms2DateTime(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
        return calendar2DateTime(ymdhms2Calendar(year, month, dayOfMonth, hourOfDay, minute, second));
    }

    public static String ymdhms2Custom(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second, String pattern) {
        return calendar2Custom(ymdhms2Calendar(year, month, dayOfMonth, hourOfDay, minute, second), pattern);
    }

    public static String calendar2DateTime(Calendar calendar) {
//        return String.format(Locale.CHINA,"%tF %tT", calendar, calendar);
        /**
         * 常规类型、字符类型和数值类型的格式说明符的语法：
         * %[参数索引位置$][转换标识符][最小官渡][.保留精度位数]转换方式
         * 日期语法：
         * %[参数索引位置$][t或T]转换方式
         */
        return String.format(Locale.CHINA, "%1$tF %1$tT", calendar);
    }

    public static String calendar2Date(Calendar calendar) {
        return String.format(Locale.CHINA, "%tF", calendar);
    }

    public static String calendar2Time(Calendar calendar) {
        return String.format(Locale.CHINA, "%tT", calendar);
    }

    public static String calendar2Custom(Calendar calendar, String pattern) {
        String dateTime = calendar2DateTime(calendar);
        return dateTime2Custom(dateTime, pattern);
    }

    public static String dateTime2Date(String serverDateTime) {
        Calendar calendar = dateTime2Calendar(serverDateTime);
        return calendar2Date(calendar);
    }

    public static String dateTime2Time(String serverDateTime) {
        Calendar calendar = dateTime2Calendar(serverDateTime);
        return calendar2Time(calendar);
    }

    public static String dateTime2Custom(String serverDateTime, String pattern) {
        DateFormat dateTimeFormatIn = getDateFormat(Format.FORMAT_DATE_TIME);
        DateFormat dateTimeFormatOut = getDateFormat(pattern);
        Date date = null;
        try {
            date = dateTimeFormatIn.parse(serverDateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTimeFormatOut.format(date);
    }

    public static String date2DateTime(String serverDate) {
        Calendar calendar = date2Calendar(serverDate);
        return calendar2DateTime(calendar);
    }

    public static String date2Custom(String serverDate, String pattern) {
        DateFormat dateTimeFormatIn = getDateFormat(Format.FORMAT_DATE);
        DateFormat dateTimeFormatOut = getDateFormat(pattern);
        Date date = null;
        try {
            date = dateTimeFormatIn.parse(serverDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTimeFormatOut.format(date);
    }

    public static String custom2Custom(String serverStrCustom, String patternSrc, String pattern) {
        DateFormat dateTimeFormatIn = getDateFormat(patternSrc);
        DateFormat dateTimeFormatOut = getDateFormat(pattern);
        Date date = null;
        try {
            date = dateTimeFormatIn.parse(serverStrCustom);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTimeFormatOut.format(date);
    }

    public static Calendar custom2Calendar(String serverStrCustom, String patternSrc) {
        String dateTime = custom2DateTime(serverStrCustom, patternSrc);
        Calendar calendar = dateTime2Calendar(dateTime);
        return calendar;
    }

    public static String custom2Date(String serverStrCustom, String patternSrc) {
        DateFormat dateTimeFormatIn = getDateFormat(patternSrc);
        DateFormat dateTimeFormatOut = getDateFormat(Format.FORMAT_DATE);
        Date date = null;
        try {
            date = dateTimeFormatIn.parse(serverStrCustom);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTimeFormatOut.format(date);
    }

    public static String custom2Time(String serverStrCustom, String patternSrc) {
        DateFormat dateTimeFormatIn = getDateFormat(patternSrc);
        DateFormat dateTimeFormatOut = getDateFormat(Format.FORMAT_TIME);
        Date date = null;
        try {
            date = dateTimeFormatIn.parse(serverStrCustom);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTimeFormatOut.format(date);
    }

    public static String custom2DateTime(String serverStrCustom, String patternSrc) {
        DateFormat dateTimeFormatIn = getDateFormat(patternSrc);
        DateFormat dateTimeFormatOut = getDateFormat(Format.FORMAT_DATE_TIME);
        Date date = null;
        try {
            date = dateTimeFormatIn.parse(serverStrCustom);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateTimeFormatOut.format(date);
    }

    public static int compareTo(String dateTime, String anotherDateTime) {
        Calendar calendar = DateTimeTool.dateTime2Calendar(dateTime);
        Calendar anotherCalendar = DateTimeTool.dateTime2Calendar(anotherDateTime);
        return calendar.compareTo(anotherCalendar);
    }

    public static boolean before(String dateTime, String anotherDateTime) {
        Calendar calendar = DateTimeTool.dateTime2Calendar(dateTime);
        Calendar anotherCalendar = DateTimeTool.dateTime2Calendar(anotherDateTime);
        return calendar.before(anotherCalendar);
    }

    public static boolean after(String dateTime, String anotherDateTime) {
        Calendar calendar = DateTimeTool.dateTime2Calendar(dateTime);
        Calendar anotherCalendar = DateTimeTool.dateTime2Calendar(anotherDateTime);
        return calendar.after(anotherCalendar);
    }


}
