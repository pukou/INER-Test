/**
 * @Title: LogUitl.java
 * @Package com.bsoft.mob.ienr.util
 * @Description: 日志文件记录工具
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2016-1-19 下午3:29:05
 * @version V1.0
 */
package com.bsoft.mob.ienr.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.util.prefs.SettingUtils;
import com.bsoft.mob.ienr.util.prefs.WifiPrefUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * @ClassName: LogUitl
 * @Description: 日志文件记录工具
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2016-1-19 下午3:29:05
 *
 */
public class LogUtil {

    private static Boolean MYLOG_WRITE_TO_FILE = true;// 日志写入文件开关
    private static char MYLOG_TYPE = 'v';// 输入日志类型，w代表只输出告警信息等，v代表输出所有信息
    private static String MYLOGFILEName = "Log.txt";// 本类输出的日志文件名称


    public static void w(Context context, String tag, Object msg) { // 警告信息
        log(context, tag, msg.toString(), 'w');
    }

    public static void e(Context context, String tag, Object msg) { // 错误信息
        log(context, tag, msg.toString(), 'e');
    }

    public static void d(Context context, String tag, Object msg) {// 调试信息
        log(context, tag, msg.toString(), 'd');
    }

    public static void i(Context context, String tag, Object msg) {//
        log(context, tag, msg.toString(), 'i');
    }

    public static void v(Context context, String tag, Object msg) {
        log(context, tag, msg.toString(), 'v');
    }

    public static void w(Context context, String tag, String text) {
        log(context, tag, text, 'w');
    }

    public static void e(Context context, String tag, String text) {
        log(context, tag, text, 'e');
    }

    public static void d(Context context, String tag, String text) {
        log(context, tag, text, 'd');
    }

    public static void i(Context context, String tag, String text) {
        log(context, tag, text, 'i');
    }

    public static void v(Context context, String tag, String text) {
        log(context, tag, text, 'v');
    }

    /**
     * 根据tag, msg和等级，输出日志
     *
     * @param tag
     * @param msg
     * @param level
     * @return void
     * @since v 1.0
     */
    private static void log(Context context, String tag, String msg, char level) {
        if (context.getSharedPreferences(WifiPrefUtils.WEB_PREF,
                Context.MODE_PRIVATE).getBoolean(SettingUtils.LOG_KEY, false)) {
            if ('e' == level && ('e' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) { // 输出错误信息
                Log.e(tag, msg);
            } else if ('w' == level && ('w' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
                Log.w(tag, msg);
            } else if ('d' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
                Log.d(tag, msg);
            } else if ('i' == level && ('d' == MYLOG_TYPE || 'v' == MYLOG_TYPE)) {
                Log.i(tag, msg);
            } else {
                Log.v(tag, msg);
            }
            if (MYLOG_WRITE_TO_FILE){
                writeLogtoFile(context, String.valueOf(level), tag, msg);
            }

        }
    }

    /**
     * 打开日志文件并写入日志
     *
     * @return
     * **/
    private static void writeLogtoFile(Context context, String mylogtype,
                                       String tag, String text) {// 新建或打开日志文件
     String date=   DateTimeHelper.getServerDate();
     String dateTime=   DateTimeHelper.getServerDateTime();
        String needWriteFiel = date;
        String needWriteMessage =dateTime + "    " + mylogtype
                + "    " + tag + "    " + text;

        String path = DeviceUtil.getMyAppDir(context) + File.separator + "system_log" + File.separator +
                DateTimeHelper.getServerDate();
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(path, needWriteFiel + MYLOGFILEName);
        try {
            FileWriter filerWriter = new FileWriter(file, true);// 后面这个参数代表是不是要接上文件中原来的数据，不进行覆盖
            BufferedWriter bufWriter = new BufferedWriter(filerWriter);
            bufWriter.write(needWriteMessage);
            bufWriter.newLine();
            bufWriter.close();
            filerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
