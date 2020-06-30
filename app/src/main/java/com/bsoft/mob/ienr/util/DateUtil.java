package com.bsoft.mob.ienr.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-4 下午3:38:01
 * @类说明 时间处理类
 */
// TODO 优化审明的SimpleDateFormat对象，合并所有SimpleDateFormat对象为一个
@SuppressLint("SimpleDateFormat")
public class DateUtil {
    public static final SimpleDateFormat format_yyyyMMdd_HHmmssSSS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    public static final SimpleDateFormat format_yyyyMMdd_HHmmssS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
    public static final SimpleDateFormat format_yyyyMMdd_HHmmss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat format_yyyyMMdd_HHmm = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public static final SimpleDateFormat format_yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat format_MMdd_HHmm = new SimpleDateFormat("MM-dd HH:mm");
    public static final SimpleDateFormat format_HHmmss = new SimpleDateFormat("HH:mm:ss");
    public static final SimpleDateFormat format_HHmm = new SimpleDateFormat("HH:mm");

    //
    public static Date baseDate = null;

    public static String dateToString(Date date, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return format.format(date);
    }

    public static String dateToString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    // 得到分钟数
    public static int getMinuteTime(String time) {
        if (null != time && time.length() > 0) {
            try {
                if (null == baseDate) {
                    baseDate = format_HHmm.parse("00:00");
                }
                Date dt = format_HHmm.parse(time);
                return (int) ((dt.getTime() - baseDate.getTime()) / 60000);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Date now = new Date();
        return now.getHours() * 60 + now.getMinutes();
    }

    public static Date getDateCompat(String time) {
        if (TextUtils.isEmpty(time)) {
            return null;
        }
        Date date = null;
        try {
            date = format_yyyyMMdd_HHmmssSSS.parse(time);
        } catch (ParseException e) {
            try {
                date = format_yyyyMMdd_HHmmssS.parse(time);
            } catch (ParseException e1) {
                try {
                    date = format_yyyyMMdd_HHmmss.parse(time);
                } catch (ParseException e2) {
                    try {
                        date = format_yyyyMMdd_HHmm.parse(time);
                    } catch (ParseException e3) {
                        try {
                            date = format_yyyyMMdd.parse(time);
                        } catch (ParseException e4) {
                            try {
                                date = format_MMdd_HHmm.parse(time);
                            } catch (ParseException e5) {
                                try {
                                    date = format_HHmmss.parse(time);
                                } catch (ParseException e6) {
                                    try {
                                        date = format_HHmm.parse(time);
                                    } catch (ParseException e7) {
                                        e7.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return date;
    }

    /**
     * 验证时间字符串格式输入是否正确
     *
     * @param timeFormatStr "2016-5-2 08:02:02"  true
     *                      "2016-02-29 08:02:02" true
     *                      "2015-02-29 08:02:02" false 15年 没有29号
     *                      "2016-02-02 082:02" false
     * @return
     */
    public static boolean validateTimeWithFormat(String timeFormatStr) {
        String format = "((19|20)[0-9]{2})-(0?[1-9]|1[012])-(0?[1-9]|[12][0-9]|3[01]) "
                + "([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]";
        Pattern pattern = Pattern.compile(format);
        Matcher matcher = pattern.matcher(timeFormatStr);
        if (matcher.matches()) {
            pattern = Pattern.compile("(\\d{4})-(\\d+)-(\\d+).*");
            matcher = pattern.matcher(timeFormatStr);
            if (matcher.matches()) {
                int y = Integer.valueOf(matcher.group(1));
                int m = Integer.valueOf(matcher.group(2));
                int d = Integer.valueOf(matcher.group(3));
                if (d > 28) {
                    Calendar c = Calendar.getInstance();
                    c.set(y, m - 1, 1);
                    int lastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
                    return (lastDay >= d);
                }
            }
            return true;
        }
        return false;
    }
}
