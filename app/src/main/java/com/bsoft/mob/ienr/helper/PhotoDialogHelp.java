package com.bsoft.mob.ienr.helper;

import android.app.Activity;
import android.content.Intent;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.custom.DialogGetHeadPicture;
import com.bsoft.mob.ienr.photo.photopicker.PhotoPicker;
import com.bsoft.mob.ienr.photo.photopicker.utils.ImageCaptureManager;

import java.io.IOException;

public class PhotoDialogHelp {
    private Activity context;
    private ImageCaptureManager captureManager;

    public PhotoDialogHelp(Activity context,ImageCaptureManager captureManager){
        this.context=context;
        this.captureManager =captureManager;
    }
    public void showDialog(){
        new DialogGetHeadPicture(context, R.style.customDialog, "相机",
                "相册",null, "取消") {
            @Override
            public void one() {
                //拍照
                try {
                    Intent intent = captureManager.dispatchTakePictureIntent();
                    context.startActivityForResult(intent, ImageCaptureManager.REQUEST_TAKE_PHOTO);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void two() {
                //选择图库
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(false)
                        .setPreviewEnabled(false)
                        .start(context);

            }
            @Override
            public void three() {
            }
        }.show();
    }

}
