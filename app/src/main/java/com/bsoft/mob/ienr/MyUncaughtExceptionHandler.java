package com.bsoft.mob.ienr;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.helper.AndroidEmailHelper;
import com.bsoft.mob.ienr.helper.ExecutorServiceHelper;
import com.bsoft.mob.ienr.util.LogicalExternalStorageTool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

//import com.bsoft.mob.ienr.helper.CommonsEmailHelper;

/**
 * Created by classichu on 2018/3/12.
 */

public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "MyUncaughtExceptionHand";
    private static Context mAppContext;

    /**
     * 静态内部类实现单例
     */
    private static class SingletonHolder {
        private static final MyUncaughtExceptionHandler INSTANCE = new MyUncaughtExceptionHandler();
    }

    private MyUncaughtExceptionHandler() {
    }

    public static MyUncaughtExceptionHandler getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private String mTitle;
    private String mContent;

    //重启App
    public void restartApp() {
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        Log.e(TAG, "zfq:uncaughtException: ", e);
        //
        boolean deal = handleException(e);
        if (deal) {
            if (!Constant.DEBUG) {
                ExecutorServiceHelper.execute(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Toast.makeText(mAppContext, "出现异常，重启App中...", Toast.LENGTH_LONG).show();
                        Looper.loop();
                    }
                });
            }
            try {
                //考虑到发邮件 延长一点
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            if (!Constant.DEBUG) {
                // 退出程序
                restartApp();
            }
        } else {
            //原来的默认处理
            if (mDefaultUncaughtExceptionHandler != null) {
                mDefaultUncaughtExceptionHandler.uncaughtException(t, e);
            }
        }
    }

    private boolean handleException(Throwable e) {
        if (e == null) {
            return false;
        }
        //build mContent
        saveInfoToSdcard(e);
        Log.e(TAG, "handleException:===== saveInfoToSdcard === end ===");
        sendInfoToWeb();
        Log.e(TAG, "handleException:===== sendInfoToWeb === end ===");
        sendInfoByEmail();
        Log.e(TAG, "handleException:===== sendInfoByEmail === end ===");
        return true;
    }


    private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;

    protected void init(Context context) {
        mAppContext = context.getApplicationContext();
        mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置自定义实现类
        Thread.setDefaultUncaughtExceptionHandler(this);

    }


    private void sendInfoToWeb() {

    }

    private void sendInfoByEmail() {

        //不用settext 就需要 replace "\n"
//        mContent = mContent.replace("\n", "<br/>");
//        AndroidEmailHelper.sendByNeteaseSmtp(mTitle, mContent);


    }

    private File saveFile;

    private void saveInfoToSdcard(Throwable throwable) {
        //
        String date = DateTimeHelper.getServerDate();
        String dateTime = DateTimeHelper.getServerDateTime();
        String time = DateTimeFactory.getInstance().dateTime2Time(dateTime);

        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append(LogicalExternalStorageTool.getExternalCacheDirPath(mAppContext)).append(File.separator);
        stringBuilder.append(LogicalExternalStorageTool.getExternalStorageDirectoryPath()).append(File.separator);
        stringBuilder.append("ienr").append(File.separator);
        stringBuilder.append("crashes").append(File.separator);
        stringBuilder.append("info_").append(date).append(File.separator);
        String filePath = stringBuilder.toString();
        String fileName = String.format("crash_%s.txt", time);
        File fileDir = new File(filePath);
        if (!fileDir.exists()) {
            fileDir.mkdirs();
        }
        //
        StringBuilder sb = new StringBuilder();
        sb.append("============== bsoft_ienr 测试信息 ==============").append("\n");
        sb.append("=================== Start ==================").append("\n");
        sb.append("============= ").append(dateTime).append(" =============").append("\n");
        sb.append(throwable.toString()).append("\n");
        sb.append("==================  StackTrace ===============").append("\n");
        StackTraceElement[] stackTraceElements = throwable.getStackTrace();
        for (StackTraceElement stackTraceElement : stackTraceElements) {
            sb.append(" ").append(stackTraceElement.toString()).append("\n");
        }
        Throwable causeThrowable = throwable.getCause();
        if (causeThrowable != null) {
            sb.append("=============== Cause StackTrace =============").append("\n");
            StackTraceElement[] causeStackTraceElements = causeThrowable.getStackTrace();
            for (StackTraceElement causeStackTraceElement : causeStackTraceElements) {
                sb.append(" ").append(causeStackTraceElement.toString()).append("\n");
            }
        }
        sb.append("================== DeviceInfo ==============").append("\n");
        sb.append("Build.ID : ").append(Build.ID).append("\n");
        sb.append("SDK_INT : ").append(Build.VERSION.SDK_INT).append("\n");
        sb.append("Android : ").append(Build.VERSION.RELEASE).append("\n");
        sb.append("Manufacturer : ").append(Build.MANUFACTURER).append("\n");
        sb.append("Model : ").append(Build.MODEL).append("\n");
        sb.append("Brand : ").append(Build.BRAND).append("\n");
        String packageName = mAppContext.getPackageName();
        sb.append("PackageName : ").append(packageName).append("\n");
        PackageManager packageManager = mAppContext.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            sb.append("VersionCode : ").append(packageInfo.versionCode).append("\n");
            sb.append("VersionName : ").append(packageInfo.versionName).append("\n");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        sb.append("================== DeviceInfoDetail ==============").append("\n");
        //
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                sb.append(field.getName()).append(" : ").append(field.get(null).toString()).append("\n");
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        sb.append("=================== End ==================").append("\n");
        //
        mTitle = throwable.toString();
        mContent = sb.toString();
        try {
            saveFile = new File(filePath, fileName);
            if (!saveFile.exists()) {
                saveFile.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(saveFile);
            fileWriter.write(mContent);
            fileWriter.close();
            //
            Log.e(TAG, "saveInfoToSdcard: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
