package com.bsoft.mob.ienr.photo.photopicker.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;


import com.bsoft.mob.ienr.AppApplication;

import java.io.File;
import java.util.Date;

/**
 * Created by myc on 2016/12/14.
 * More Code on 1101255053@qq.com
 * Description:
 */
public class FileUtils {
    public static boolean fileIsExists(String path) {
        if (path == null || path.trim().length() <= 0) {
            return false;
        }
        try {
            File f = new File(path);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        Log.e("TMG",path+"file not exists");
        return true;
    }
    public static boolean isExist(String url) {
        File file = new File(getCachePath(AppApplication.getInstance()) + "/" + getFileName(url));
        return file.exists();
    }
    public static String getCachePath(@NonNull Context context) {
        File externalCacheDir = context.getExternalCacheDir();
        if (null != externalCacheDir) {
            String path = externalCacheDir.getAbsolutePath() + "/uploadImg/";
            setMkdirs(path);
            return path;
        } else {
            File cacheDir = context.getCacheDir();
            String path = cacheDir.getAbsolutePath() + "/uploadImg/";
            setMkdirs(path);
            return path;
        }
    }
    /**
     * 创建目录
     *
     * @param path
     */
    public static void setMkdirs(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
            Log.d("file", "目录不存在  创建目录    ");
        } else {
            Log.d("file", "目录存在" + path);
        }
    }
    /**
     * 获取目录名称
     *
     * @param url
     * @return FileName
     */
    public static String getFileName(String url) {
        int lastIndexStart = url.lastIndexOf("/");
        if (lastIndexStart != -1) {
            return url.substring(lastIndexStart + 1, url.length());
        } else {
            return new Date().getTime() + "";
        }
    }
    public static String getImgName(String fileType) {
        return new Date().getTime() + fileType;
    }
}
