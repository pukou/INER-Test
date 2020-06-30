package com.bsoft.mob.ienr.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;

public class UpdateBroadcastReceiver extends BroadcastReceiver {
    private Activity activity;

    public UpdateBroadcastReceiver(Activity activity) {
        this.activity = activity;
    }
    public void  registerReceiver() {
        if (activity!=null) {
            IntentFilter barcodeFilter = new IntentFilter();
            barcodeFilter.addAction(Constant.ACTION_SHOW_UPDATE);
            activity.registerReceiver(this, barcodeFilter);
        }
    }
    public void  unregisterReceiver() {
        if (activity!=null) {
            activity.unregisterReceiver(this);
        }
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Constant.ACTION_SHOW_UPDATE.equals(intent.getAction())){
            String Tip=intent.getStringExtra("Tip");
            String Url= intent.getStringExtra("Url");
            String FileName=intent.getStringExtra("FileName");

            //
            Activity aty = AppApplication.getInstance().getShowingActivity();
            if (aty!=null){
                showDialog(aty,Tip,Url,FileName);
            }

        }
    }

    private AlertDialog mAlertDialog;
    private void showDialog(Activity aty,String message, final String url,
                            final String versionName) {
        if (mAlertDialog!=null){
            mAlertDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(aty);
        View txt = ViewBuildHelper.buildDialogTitleTextView(aty, "软件更新");
        builder.setMessage(message)
                //.setTitle("软件更新")
                .setCustomTitle(txt);

        builder.setPositiveButton("更新",// 设置确定按钮
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(Constant.ACTION_DOWNLOAD_UPDATE);
                        intent.putExtra("url", url);
                        intent.putExtra("versionName", versionName);
                        aty.sendBroadcast(intent);
                    }

                });

        builder.setNegativeButton("退出系统",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // 点击getString(R.string.project_operate_cancel)按钮之后退出程序
                        System.exit(0);
                    }
                });// 创建
        builder.setCancelable(false);
        // 显示对话框
        mAlertDialog = builder.create();
        mAlertDialog.show();
    }
}
