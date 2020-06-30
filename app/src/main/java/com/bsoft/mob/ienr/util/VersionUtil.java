package com.bsoft.mob.ienr.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class VersionUtil {
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            int version = info.versionCode;
            return String.valueOf(version);
        } catch (Exception e) {
            e.printStackTrace();
            return String.valueOf(0);
        }
    }
}
