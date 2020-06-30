package com.bsoft.mob.ienr.the_recording;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Display;
import android.view.WindowManager;

import com.bsoft.mob.ienr.AppApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Map;

public class BitmapUtils {

    private ByteArrayOutputStream baos;

    public int[] getImage(String imagePath){
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        Bitmap bgimage=BitmapFactory.decodeFile(imagePath,opt);

        float width = opt.outWidth;
        float height = opt.outHeight;
        float imageWidth;
        float imageHeight;
        if (width>height){
            float ratio= getBitmapWidth()/width;//计算出比例
            float h1=height*ratio;
            imageHeight= h1;
            imageWidth=getBitmapWidth();
        }else{
            float h= getBitmapHeight();
            float ratio= h / height;//计算出比例
            float h1=width * ratio;
            imageHeight = getBitmapHeight();
            imageWidth=h1;
        }
        int []so=new int[]{(int)imageWidth,(int)imageHeight};

        return so;
    }
    // 获取屏幕的宽度
    @SuppressWarnings("deprecation")
    public float getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    // 获取屏幕的高度
    @SuppressWarnings("deprecation")
    public float getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }

    public float getBitmapWidth() {
        return getScreenWidth(AppApplication.getContext()) / 3;
    }

    public float getBitmapHeight() {
        float s=getScreenHeight(AppApplication.getContext());
        return s / 5;
    }
}
