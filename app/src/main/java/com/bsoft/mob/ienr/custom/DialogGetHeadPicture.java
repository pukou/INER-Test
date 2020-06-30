package com.bsoft.mob.ienr.custom;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.util.StringUtil;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * Created by ThinkPad on 2017/3/16.
 */

public abstract class DialogGetHeadPicture extends Dialog implements View.OnClickListener{
private Activity activity;
    private String one;
    private String two;
    private String three;
    private String cancle;
    private TextView tv_saoma;
    private TextView tv_shuru;
    private Window window;

    public DialogGetHeadPicture(Activity context, String one, String two, String cancle) {
        super(context);
        this.activity = context;
        this.one=one;
        this.two=two;
        this.cancle =cancle;
    }

    public DialogGetHeadPicture(Activity context, int themeResId, String one, String two, String three, String cancle) {
        super(context, themeResId);
        this.activity = context;
        this.one=one;
        this.two=two;
        this.three=three;
        this.cancle =cancle;
    }

    protected DialogGetHeadPicture(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    @Override
            protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_setting_photo);

        FrameLayout flt_amble_upload=(FrameLayout)findViewById(R.id.flt_amble_upload);
        FrameLayout flt_take_photo_upload=(FrameLayout)findViewById(R.id.flt_take_photo_upload);
        FrameLayout flt_three=(FrameLayout)findViewById(R.id.flt_three);
        TextView btn_cancel=(TextView)findViewById(R.id.btn_cancel);
        TextView tv_three=(TextView)findViewById(R.id.tv_three);
        tv_saoma = (TextView) findViewById(R.id.tv_saoma);
        tv_shuru = (TextView) findViewById(R.id.tv_shuru);
        tv_saoma.setText(one);
        tv_shuru.setText(two);
        if (!TextUtils.isEmpty(three)){
            tv_three.setText(three);
            flt_three.setVisibility(View.VISIBLE);
        }

        btn_cancel.setText(cancle);
        flt_amble_upload.setOnClickListener(this);
        flt_take_photo_upload.setOnClickListener(this);
        flt_three.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

    setViewLocation();
    setCanceledOnTouchOutside(true);//外部点击取消 
       }

            /**  
          * 设置dialog位于屏幕底部  
          */
     private void setViewLocation(){
        DisplayMetrics dm = new DisplayMetrics();
       activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int height = dm.heightPixels;

         window = this.getWindow();
        WindowManager.LayoutParams lp= window.getAttributes();

        lp.x=0;
        lp.y=height;
        lp.width= ViewGroup.LayoutParams.MATCH_PARENT;
        lp.height= ViewGroup.LayoutParams.WRAP_CONTENT;

        // 设置显示位置  
            onWindowAttributesChanged(lp);
         window.setWindowAnimations(R.style.dialogWindowAnim);
        }

            @Override
            public void onClick(View v){
        switch(v.getId()){
           case R.id.flt_amble_upload:
               new RxPermissions(activity)
                       .request(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                       .subscribe(new Observer<Boolean>() {
                           @Override
                           public void onSubscribe(Disposable d) {}
                           @Override
                           public void onNext(Boolean aBoolean) {
                            if (aBoolean){
                                one();
                            }else{
                                StringUtil.showToast("请前往设置页面打开相机权限");
                            }
                           }

                           @Override
                           public void onError(Throwable e) {}
                           @Override
                           public void onComplete() {}
                       });

          this.cancel();
           break;
            case R.id.flt_take_photo_upload:
                new RxPermissions(activity)
                        .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe(new Observer<Boolean>() {
                            @Override
                            public void onSubscribe(Disposable d) {}
                            @Override
                            public void onNext(Boolean aBoolean) {
                                if (aBoolean){
                                    two();
                                }else{
                                    StringUtil.showToast("请前往设置页面打开读写权限");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {}
                            @Override
                            public void onComplete() {}
                        });

            this.cancel();
            break;
            case R.id.btn_cancel:
            this.cancel();
            break;
            case R.id.flt_three:
                three();
                this.cancel();
                break;
            }
        }

            public abstract void one();
            public abstract void two();
            public abstract void three();
}
