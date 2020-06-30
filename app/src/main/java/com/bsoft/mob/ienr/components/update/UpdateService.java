package com.bsoft.mob.ienr.components.update;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.api.UpdateApi;
import com.bsoft.mob.ienr.components.update.custom.DownloadRequest;
import com.bsoft.mob.ienr.components.update.custom.DownloadStatusListener;
import com.bsoft.mob.ienr.components.update.custom.ThinDownloadManager;
import com.bsoft.mob.ienr.helper.AlarmManagerCompatRepeatingManager;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.UpdateInfo;
import com.bsoft.mob.ienr.util.DeviceUtil;
import com.bsoft.mob.ienr.util.PackageManagerUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.io.File;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 更新应用service
 *
 * @author hy
 */
public class UpdateService extends Service {
    private static final String TAG = "UpdateService";
    public static final String proName = "NIS";

    private long mReference = -2L;

    // Background thread executor service
    ExecutorService es = Executors.newSingleThreadExecutor();

    DowloadReceiver receiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        // 已启动下载
        if (mReference != -2L) {
            return super.onStartCommand(intent, flags, startId);
        }

        if (DeviceUtil.sdMounted()) {
            if (receiver == null) {
                receiver = new DowloadReceiver();
            }
            IntentFilter filter = new IntentFilter(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            registerReceiver(receiver, filter);
            if (msgBackToDownloadReceiver == null) {
                msgBackToDownloadReceiver = new MsgBackToDownloadReceiver();
            }
            msgBackToDownloadReceiver.register();
        }
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                connServer();
            }
        });
        startAppCompatAlarmManager(10 * 60 * 1000);//10分钟
        return START_NOT_STICKY;
    }

    private AlarmManagerCompatRepeatingManager mAlarmManagerCompatRepeatingManager;
    private void startAppCompatAlarmManager(long intervalMillis) {
        mAlarmManagerCompatRepeatingManager = new AlarmManagerCompatRepeatingManager(this, intervalMillis) {
            @Override
            protected void onAlarmManagerReceive() {
                //
                if(Constant.DEBUG){
                    Log.d(TAG, "自动检测程序是否更新");
                }
                Executors.newSingleThreadExecutor().execute(new Runnable() {
                    @Override
                    public void run() {
                        connServer();
                    }
                });

            }
        };
        mAlarmManagerCompatRepeatingManager.init(false);

    }

    @Deprecated
    private void showDialog(String message, final String url,
                            final String versionName) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        View txt = ViewBuildHelper.buildDialogTitleTextView(this, "软件更新");
        builder.setMessage(message)
                //.setTitle("软件更新")
                .setCustomTitle(txt);

        builder.setPositiveButton("更新",// 设置确定按钮
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (DeviceUtil.sdMounted()) {// sdcard可用
                            downAPP(url, versionName);
                        } else {
                            downAppWithCustom(url, versionName);
                        }
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
        AlertDialog dialog = builder.create();
        dialog.getWindow().setType(
                (WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
        dialog.show();
    }

    void downAppWithCustom(String url, String versionName) {

        Uri downloadUri = Uri.parse(url);
        final String path = DeviceUtil.getRootDir(this) + File.separator
                + versionName;
        Uri destinationUri = Uri.parse(path);
        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                .setDestinationURI(destinationUri)
                .setPriority(DownloadRequest.Priority.HIGH)
                .setDownloadListener(new DownloadStatusListener() {
                    @Override
                    public void onDownloadComplete(int id) {
                        if (mReference == id) {
                            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            manager.cancel((int) mReference);
                            String cmd = "chmod 777 " + path;
                            try {
                                Runtime.getRuntime().exec(cmd);
                                install(path);
                            } catch (Exception e) {
                                Log.e(Constant.TAG, e.getMessage(), e);
                            }
                        }
                    }

                    @Override
                    public void onDownloadFailed(int id, int errorCode,
                                                 String errorMessage) {
                        stopSelfWapper();
                    }

                    @Override
                    public void onProgress(int id, long totalBytes,
                                           long downlaodedBytes, int progress) {
                        if (mReference == id) {
                            if (progress % 5 != 0) {
                                return;
                            }
                            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                                    UpdateService.this)
                                    .setSmallIcon(R.drawable.ic_launcher)
                                    .setContentTitle("应用升级")
                                    .setContentText(progress + "%")
                                    .setProgress(100, progress, false);
                            Intent resultIntent = new Intent();
                            PendingIntent resultPendingIntent = PendingIntent
                                    .getActivity(getApplicationContext(), 0,
                                            resultIntent,
                                            Intent.FLAG_ACTIVITY_NEW_TASK);
                            mBuilder.setContentIntent(resultPendingIntent);
                            manager.notify((int) mReference, mBuilder.build());

                        }
                    }
                });
        ThinDownloadManager downloadManager = new ThinDownloadManager();
        mReference = downloadManager.add(downloadRequest);
    }

    public void stopSelfWapper() {
// 不结束        stopSelf();
    }

    public void connServer() {

        Future<UpdateInfo> future = es.submit(new ConnectTask());
        UpdateInfo updateInfo = null;
        try {
            updateInfo = future.get();
        } catch (Exception e) {
            Log.e(Constant.TAG, e.getMessage(), e);
            stopSelfWapper();
            return;
        }

        if (updateInfo == null) {
            stopSelfWapper();
            return;
        }

        PackageInfo pInfo = PackageManagerUtil.getPackageInfoFromPackageName(
                this, getPackageName());
        if (pInfo == null) {
            stopSelfWapper();
            return;
        }

        int versionCode = pInfo.versionCode;

        if (versionCode < updateInfo.VersionCode) {

            StringBuilder sb = new StringBuilder();
            sb.append("当前版本:");
            sb.append(pInfo.versionName);
            sb.append("\n");
            sb.append("发现新版本:");
            sb.append(updateInfo.VersionName);
            sb.append("\n");
            if (null != updateInfo.Description) {
                sb.append(updateInfo.Description);
                sb.append("\n");
            }
            sb.append("是否更新?");

//            showDialog(sb.toString(), updateInfo.Url, updateInfo.FileName);
            Intent intent = new Intent(Constant.ACTION_SHOW_UPDATE);
            intent.putExtra("Tip", sb.toString());
            intent.putExtra("Url", updateInfo.Url);
            intent.putExtra("FileName", updateInfo.FileName);
            sendBroadcast(intent);

        }
    }

    // Test method
    public void connServerTest() {

        StringBuilder sb = new StringBuilder();

        sb.append("是否更新?");

        String url = "http://cdn2.down.apk.gfan.com/asdf/Pfiles/2014/4/23/175399_213da4bf-053a-4ce7-b689-4ab68b3d4953.apk";
        //showDialog(sb.toString(), url, "test.apk");

    }

    class ConnectTask implements Callable<UpdateInfo> {

        @Override
        public UpdateInfo call() throws Exception {

            UpdateInfo updateInfo = null;

            Response<UpdateInfo> response = UpdateApi.getInstance(getApplicationContext()).updateInfo(proName);
            if (null != response && response.ReType == 0) {
                updateInfo = response.Data;
            }
            return updateInfo;
        }
    }

    private void downAPP(String url, String versionName) {

        String serviceString = Context.DOWNLOAD_SERVICE;
        DownloadManager downloadManager = (DownloadManager) getSystemService(serviceString);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new Request(uri);

        //解决移动网络下无法更新的问题
        //request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
        request.setAllowedNetworkTypes(Request.NETWORK_WIFI | Request.NETWORK_MOBILE);

        if (DeviceUtil.sdMounted()) {
            // 专有文件夹
            request.setDestinationInExternalFilesDir(this,
                    Environment.DIRECTORY_DOWNLOADS, versionName);
        }

        mReference = downloadManager.enqueue(request);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    class DowloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            long reference = intent.getLongExtra(
                    DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (mReference == reference) {
                queryAndOpen(reference);
            }
        }
    }

    MsgBackToDownloadReceiver msgBackToDownloadReceiver;

    class MsgBackToDownloadReceiver extends BroadcastReceiver {
        public void register() {
            IntentFilter barcodeFilter = new IntentFilter();
            barcodeFilter.addAction(Constant.ACTION_DOWNLOAD_UPDATE);
            registerReceiver(this, barcodeFilter);
        }

        public void unregister() {
            unregisterReceiver(this);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constant.ACTION_DOWNLOAD_UPDATE.equals(intent.getAction())) {
                String url = intent.getStringExtra("url");
                String versionName = intent.getStringExtra("versionName");
                Toast.makeText(context, "新版本已开始下载！请稍候...", Toast.LENGTH_SHORT).show();
                if (DeviceUtil.sdMounted()) {// sdcard可用
                    downAPP(url, versionName);
                } else {
                    downAppWithCustom(url, versionName);
                }
            }
        }
    }

    public boolean install(String uri) {

        if (EmptyTool.isBlank(uri)) {
            return false;
        }

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.fromFile(new File(uri)),
                "application/vnd.android.package-archive");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        stopSelfWapper();
        return true;

    }

    public void queryAndOpen(long reference) {
        String serviceString = Context.DOWNLOAD_SERVICE;
        DownloadManager downloadManager = (DownloadManager) getSystemService(serviceString);


        Query myDownloadQuery = new Query();
        myDownloadQuery.setFilterById(reference);

        Cursor myDownload = downloadManager.query(myDownloadQuery);
        // Cursor myDownload = downloadManager.getUriForDownloadedFile(myDownloadQuery);
        if (myDownload.moveToFirst()) {
            int fileUriIdx = myDownload
                    .getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
            String fileUri = myDownload.getString(fileUriIdx);
            fileUri = getFilePathFromUri(Uri.parse(fileUri));
            install(fileUri);
        }
        myDownload.close();

        //
/*        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;*/

    }

    @Override
    public void onDestroy() {

        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        if (msgBackToDownloadReceiver != null) {
            msgBackToDownloadReceiver.unregister();
        }
        super.onDestroy();
        if (mAlarmManagerCompatRepeatingManager != null) {
            mAlarmManagerCompatRepeatingManager.cancel();
        }
    }

    public String getFilePathFromUri(Uri uri) {

        String filePath = null;
        if ("content".equals(uri.getScheme())) {
            String[] filePathColumn = {MediaColumns.DATA};
            ContentResolver contentResolver = getContentResolver();

            Cursor cursor = contentResolver.query(uri, filePathColumn, null,
                    null, null);

            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        } else if ("file".equals(uri.getScheme())) {
            filePath = new File(uri.getPath()).getAbsolutePath();
        }
        return filePath;
    }

}
