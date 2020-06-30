package com.bsoft.mob.ienr.util;

import android.content.Context;

/**
 * Created by classichu on 2018/3/2.
 * <p>
 * 逻辑上的 Internal storage:
 * 1 总是可用的
 * 2 这里的文件默认只能被我们的app所访问。
 * 3 当用户卸载app的时候，系统会把internal内该app相关的文件都清除干净。
 * 4 Internal是我们在想确保不被用户与其他app所访问的最佳存储区域。
 * <p>
 * data 目录
 * <p>
 * 这下面的文件会在用户卸载我们的app时被系统删除
 */

public class LogicalInternalStorageTool {

    /**
     * app 的 internal 缓存目录
     * <p>
     * /data/data/<application package>/cache
     * miui等系统应用多开 时候
     * /data/user/0/<application package>/cache
     * miui等系统应用多开 时候 小号
     * /data/user/999/<application package>/cache
     *
     * @param context
     * @return
     */
    public static String getCacheDirPath(Context context) {
        return context.getCacheDir().getAbsolutePath();
    }

    /**
     * app 的 internal 目录
     * <p>
     * /data/data/<application package>/files
     * <p>
     * miui等系统应用多开 时候
     * /data/user/0/<application package>/files
     * miui等系统应用多开 时候 小号
     * /data/user/999/<application package>/files
     *
     * @param context
     * @return
     */
    public static String getFilesDirPath(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }
}
