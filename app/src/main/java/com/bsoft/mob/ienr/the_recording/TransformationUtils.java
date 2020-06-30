package com.bsoft.mob.ienr.the_recording;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bsoft.mob.ienr.AppApplication;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

public class TransformationUtils extends SimpleTarget<Bitmap> {
    private ImageView target;
    public TransformationUtils(ImageView view) {
        this.target = view;
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
        return s / 6;
    }

    @Override
    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
        ViewGroup.LayoutParams params = target.getLayoutParams();
        //获取原图的宽高
        float width = resource.getWidth();
        float height = resource.getHeight();
        if (width>height){
            float ratio= getBitmapWidth()/width;//计算出比例
            float h1=height*ratio;
            params.height = (int)h1;
            params.width=(int)getBitmapWidth();
        }else{
            float h= getBitmapHeight();
            float ratio= h / height;//计算出比例
            float h1=width * ratio;
            params.height = (int)getBitmapHeight();
            params.width=(int)h1;
        }

        target.setLayoutParams(params);
        target.setImageBitmap(resource);
    }
}
