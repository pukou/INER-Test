package com.bsoft.mob.ienr.components.update;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.api.UpdateApi;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.UpdateInfo;

/**
 * 用UpdateService类进行APP更新
 *
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-15 上午12:21:51
 * @类说明
 */
@Deprecated
public class UpdateVersion {
    // 工程名称
    public static final String proName = "NIS";

    public Context context;
    UpdateInfo updateInfo;
    String downloadDir;

    public UpdateVersion(Context context, String downloadDir) {
        this.context = context;
        this.downloadDir = downloadDir;
    }

    public boolean isUpdate() {
        if (getServerVerCode()) {
            int vercode = Config.getVerCode(context);
            if (updateInfo.VersionCode != vercode) {
                return true;
            }
        }
        return false;
    }

    public boolean getServerVerCode() {

        Response<UpdateInfo> response = UpdateApi.getInstance(context).updateInfo(proName);

        if (null != response && response.ReType == 0) {
            updateInfo = response.Data;
            return true;
        }

        return false;
    }

    public void notNewVersionShow() {
        int verCode = Config.getVerCode(context);
        String verName = Config.getVerName(context);
        StringBuffer sb = new StringBuffer();
        sb.append("当前版本:");
        sb.append(verName);
        sb.append(" Code:");
        sb.append(verCode);
        sb.append(",\n已是最新版,无需更新!");
        View txt = ViewBuildHelper.buildDialogTitleTextView(context, "软件更新");

        Dialog dialog = new AlertDialog.Builder(context)
              //  .setTitle("软件更新")
                .setCustomTitle(txt)
                .setMessage(sb.toString())// 设置内容
                .setPositiveButton(context.getString(R.string.project_operate_ok),// 设置确定按钮
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // finish();
                            }
                        }).create();// 创建
        // 显示对话框
        dialog.show();
    }

    public void doNewVersionUpdate() {
        String verName = Config.getVerName(context);
        StringBuffer sb = new StringBuffer();
        sb.append("当前版本:");
        sb.append(verName);
        sb.append("\n");
        sb.append("发现新版本:");
        sb.append(updateInfo.VersionName);
        sb.append("\n");
        if (null != updateInfo.Description) {
            sb.append(updateInfo.Description);
            sb.append("\n");
        }
        sb.append("是否更新?");
        Dialog dialog = new AlertDialog.Builder(context)
               // .setTitle("软件更新")
                .setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(context, "软件更新"))
                .setMessage(sb.toString())
                // 设置内容
                .setPositiveButton("更新",// 设置确定按钮
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                downFile();
                            }

                        })
                .setNegativeButton("暂不更新",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                // 点击getString(R.string.project_operate_cancel)按钮之后退出程序
                                // finish();
                            }
                        }).create();// 创建
        // 显示对话框
        dialog.show();
    }

    void downFile() {
        UpdateThread updateTh = new UpdateThread(context, downloadDir,
                updateInfo);
        updateTh.start();
    }

    // void downFile() {
    // // download.show();
    // // downThread = new DownThread();
    // // downThread.start();
    // Intent intent = new Intent();
    // intent.setAction("android.intent.action.VIEW");
    // Uri content_url = Uri.parse(new StringBuffer(ShowApi.url).append(
    // Config.UPDATE_APKNAME).toString());
    // intent.setData(content_url);
    // context.startActivity(intent);
    // }

    // void install() {
    // Intent intent = new Intent(Intent.ACTION_VIEW);
    // intent.setDataAndType(Uri.fromFile(new File(downloadDir,
    // Config.UPDATE_SAVENAME)),
    // "application/vnd.android.package-archive");
    // context.startActivity(intent);
    // }

}
