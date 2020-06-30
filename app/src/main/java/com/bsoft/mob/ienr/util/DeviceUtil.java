package com.bsoft.mob.ienr.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * android设备管理辅助类
 */
public class DeviceUtil {

    /**
     * 获取app 根目录
     *
     * @param context
     * @return
     */
    public static String getMyAppDir(Context context) {

        String rootDir = getRootDir(context);
        if (rootDir == null) {
            return null;
        }
        File file = new File(rootDir + "bsoft" + File.separator + "ienr");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getPath();
    }
    public static  String getSerial4Logic_XH(){
        String se=getSerial();
        if (!TextUtils.isEmpty(se)){
            //数据库存放字段长度 20
            int max=15;//大概  给 "PDA" 留一点
            if (se.length()>=max){
                se= se.substring(se.length() - max, se.length());
            }
        }
        return "PDA" + se;
    }
    public static  String getSerial4Logic(){
        String se=getSerial();
        return "PDA" + se;
    }
    public static  String getSerial(){
        String result = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            result=Build.getSerial();
        } else {
            result=Build.SERIAL;
        }
        return result;
    }
    public static  String getSerialNO(){
        String serial = "";
        try {
            Class<?> c =Class.forName("android.os.SystemProperties");

            Method get =c.getMethod("get", String.class);

            serial = (String)get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serial;

    }
    public static boolean sdMounted() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static String getRootDir(Context context) {

        if (context == null) {
            return null;
        }
        if (sdMounted()) {
            return Environment.getExternalStorageDirectory().getPath();
        } else {
            return context.getCacheDir().getPath();
        }
    }

    public static List<String> getDeviceInfo(Context context) {
        List<String> infos = new ArrayList<String>();
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        infos.add("/nDeviceId(IMEI) = " + tm.getDeviceId());

        infos.add("/nDeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion());

        infos.add("/nLine1Number = " + tm.getLine1Number());

        infos.add("/nNetworkCountryIso = " + tm.getNetworkCountryIso());

        infos.add("/nNetworkOperator = " + tm.getNetworkOperator());

        infos.add("/nNetworkOperatorName = " + tm.getNetworkOperatorName());

        infos.add("/nNetworkType = " + tm.getNetworkType());

        infos.add("/nPhoneType = " + tm.getPhoneType());

        infos.add("/nSimCountryIso = " + tm.getSimCountryIso());

        infos.add("/nSimOperator = " + tm.getSimOperator());

        infos.add("/nSimOperatorName = " + tm.getSimOperatorName());

        infos.add("/nSimSerialNumber = " + tm.getSimSerialNumber());

        infos.add("/nSimState = " + tm.getSimState());

        infos.add("/nSubscriberId(IMSI) = " + tm.getSubscriberId());

        infos.add("/nVoiceMailNumber = " + tm.getVoiceMailNumber());

//		android 获取当前手机型号：

        infos.add(Build.MODEL + ":" + Build.DEVICE + ":" + Build.PRODUCT);

        return infos;
    }
}
