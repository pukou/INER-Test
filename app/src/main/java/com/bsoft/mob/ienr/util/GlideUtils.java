package com.bsoft.mob.ienr.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.api.APIUrlConfig;
import com.bsoft.mob.ienr.api.BaseApi;
import com.bsoft.mob.ienr.the_recording.TransformationUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;

import java.util.Date;


/**
 * 图片加载公共类
 */

public class GlideUtils {

    private static RequestOptions options;
    private static RequestOptions options1;
    private static RequestOptions options2;
    private static RequestOptions options3;
    private static TransformationUtils transformationUtils;
    private static GlideUtils glideUtils;

    public static GlideUtils getGlideUtils(){
        if (glideUtils==null){
            synchronized (GlideUtils.class) {
                if (glideUtils == null) {
                    glideUtils = new GlideUtils();
                }
            }
        }
        return glideUtils;
    }
    public static void loadImageView(Object path, ImageView mImageView) {
        if (path!=null) {
            if (options==null) {
                options = new RequestOptions()
                        .placeholder(R.drawable.img_image_no)    //加载成功之前占位图
                        .error(R.drawable.img_image_no)    //加载错误之后的错误图
                        //指定图片的缩放类型为fitCenter （等比例缩放图片，宽或者是高等于ImageView的宽或者是高。）
//                .fitCenter()
                        //指定图片的缩放类型为centerCrop （等比例缩放图片，直到图片的狂高都大于等于ImageView的宽度，然后截取中间的显示。）
//                .centerCrop()
//                .circleCrop()//指定图片的缩放类型为centerCrop （圆形）
                        .skipMemoryCache(false)    //跳过内存缓存
//                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)	//缓存所有版本的图像
//                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)    //跳过磁盘缓存
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            }
            Glide.with(AppApplication.getContext()).load(path).apply(options).into(mImageView);
        }
    }
    public static void roundnessLoadImageView(Object path, ImageView mImageView) {
        if (path!=null) {
            if (options1==null) {

                options1 = new RequestOptions()
                        .placeholder(R.drawable.glide_error)    //加载成功之前占位图
                        .error(R.drawable.glide_error)    //加载错误之后的错误图
                        //指定图片的缩放类型为fitCenter （等比例缩放图片，宽或者是高等于ImageView的宽或者是高。）
                        //                .fitCenter()
                        //指定图片的缩放类型为centerCrop （等比例缩放图片，直到图片的狂高都大于等于ImageView的宽度，然后截取中间的显示。）
                        .centerCrop()
                        .circleCrop()//指定图片的缩放类型为centerCrop （圆形）
                        .skipMemoryCache(false)    //跳过内存缓存
                        //                .diskCacheStrategy(DiskCacheStrategy.ALL)	//缓存所有版本的图像
                        //                    .diskCacheStrategy(DiskCacheStrategy.NONE)    //跳过磁盘缓存
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            }
            Glide.with(AppApplication.getContext()).load(path).apply(options1).into(mImageView);
        }
    }
    public static  void ratioImage( String url,final ImageView imageView,int w,int h){
        options2 = new RequestOptions();
        if (!TextUtils.isEmpty(url)) {
                    options2.skipMemoryCache(false)    //跳过内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                        if (w!=0&&h!=0){
                            options2 .override(w, h);
                            Glide.with(AppApplication.getContext()).asBitmap().load(url).apply(options2).into(imageView);
                        }else{
                            Glide.with(AppApplication.getContext()).asBitmap().load(url).apply(options2).into(new TransformationUtils(imageView));
                        }



        }
    }
    public static  void filletImage(Object path, ImageView mImageView) {
        if (path != null) {
            if (options3 == null) {

                options3 = new RequestOptions()
                        //指定图片的缩放类型为fitCenter （等比例缩放图片，宽或者是高等于ImageView的宽或者是高。）
                        //                .fitCenter()
                        //指定图片的缩放类型为centerCrop （等比例缩放图片，直到图片的狂高都大于等于ImageView的宽度，然后截取中间的显示。）
                        .centerCrop()
                        .transform(new RoundedCorners(20))
                        .skipMemoryCache(false)    //跳过内存缓存
                        //                .diskCacheStrategy(DiskCacheStrategy.ALL)	//缓存所有版本的图像
                        //                    .diskCacheStrategy(DiskCacheStrategy.NONE)    //跳过磁盘缓存
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            }
            Glide.with(AppApplication.getContext()).load(path).apply(options3).into(mImageView);
        }
    }
    public static String getImagePath(String path,String extName){
        Date date=new Date();
        return new StringBuffer()
                .append(BaseApi.fileUpUrl)
                .append(APIUrlConfig.DpwnLoad)
                .append("path=")
                .append(path)
                .append("&fileName=")
                .append(date.getTime())
                .append(".")
                .append(extName).toString();
    }
}
