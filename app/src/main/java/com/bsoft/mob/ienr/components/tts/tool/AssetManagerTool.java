package com.bsoft.mob.ienr.components.tts.tool;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AssetManagerTool {
    private static final String TAG = "AssetManagerTool";

    /**
     * @param filePath       "/storage/emulated/0/temp/"
     * @param fileName       “aaa.txt"
     * @param assetsFilePath "temp/aaa.txt"
     * @return
     */
    public static String copyAssetsFile(Context context, String filePath, String fileName, String assetsFilePath) {
        //创建目录
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        //绝对路径
        String destFilename = filePath + fileName;
        try {
            copyFromAssets(context, destFilename, assetsFilePath);
        } catch (Exception e) {
            // e.printStackTrace();
            Log.e(TAG, "文件复制失败");
            return null;
        }

        Log.i(TAG, "文件复制成功：" + destFilename);
        return destFilename;
    }

    /**
     * @param context
     * @param filePath       "/storage/emulated/0/temp/aaa.txt"
     * @param assetsFilePath "temp/aaa.txt"
     */
    private static void copyFromAssets(Context context, String filePath, String assetsFilePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                AssetManager assetManager = context.getApplicationContext().getAssets();
                is = assetManager.open(assetsFilePath);
                fos = new FileOutputStream(filePath);
                byte[] buffer = new byte[1024];
                //循环来读取该文件中的数据
                int len;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                fos.flush();// 刷新缓冲区
                fos.close();
                is.close();
            } catch (Exception e) {
                throw new RuntimeException("copyFromAssets Exception:" + e.getMessage());
            }
        }
    }
}
