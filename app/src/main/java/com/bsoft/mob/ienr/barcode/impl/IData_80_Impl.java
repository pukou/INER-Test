package com.bsoft.mob.ienr.barcode.impl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.bsoft.mob.ienr.barcode.AnalyseCodeService;
import com.bsoft.mob.ienr.barcode.IBarCode;
import com.bsoft.mob.ienr.barcode.ext.IData80ScannerInterface;

/**
 * 无锡盈达聚力科技有限公司 IData 4.3 广播方式
 * 机型：IData 80
 * 重要提示！！！
 * 官方文档和 demo 需要这么写，可实际会和自带的 IData 4.3 服务程序冲突：打开使用 api 调用的界面后，全局扫码失效了。。。
 * 解决方案：
 * 1 确保设备能够全局扫码：任意界面都能通过按钮启动扫码【扫码指示灯亮】
 * 2 如果不亮的话，下拉通知栏点击 iScan 程序 ==》高级设置 ==》恢复默认设置，验证是否能够全局扫码
 * 3 然后打开咱们的【移动护理】验证是否能正常使用
 * 4 如果咱们的【移动护理】不能正常使用，可以尝试下拉通知栏点击 iScan 程序 ==》高级设置 ==》扫描结果发送模式，
 * 修改成（发送扫描结果为广播）
 * 5 回到咱们的【移动护理】验证是否能使用
 */
public class IData_80_Impl implements IBarCode {
    private static final String TAG = "IData_80_Impl";
    private static final String ACTION_DECODE_DATA = "android.intent.action.SCANRESULT";
    private static final String EXTRA_BARCODE_STRING = "value";

    private BroadcastReceiver mBarCodeBroadcastReceiver;

    private Context mContext;
//    private IData80ScannerInterface scanner;

    @Override
    public void setType(int type) {

    }


    private void initScanner(Context context) {
//        scanner = new IData80ScannerInterface(context);
//        scanner.resultScan();
//        scanner.open();    //打开扫描头上电   scanner.close();//打开扫描头下电
//        scanner.enablePlayBeep(true);//是否允许蜂鸣反馈
//        scanner.enableFailurePlayBeep(false);//扫描失败蜂鸣反馈
//        scanner.enablePlayVibrate(true);//是否允许震动反馈
//        scanner.enableAddKeyValue(1);/**附加无、回车、Teble、换行*/
//        scanner.timeOutSet(2);//设置扫描延时2秒
//        scanner.intervalSet(1000); //设置连续扫描间隔时间
//        scanner.lightSet(true);//右上角扫描指示灯
//        scanner.enablePower(true);//省电模式
        //		scanner.addPrefix("AAA");//添加前缀
        //		scanner.addSuffix("BBB");//添加后缀
        //		scanner.interceptTrimleft(2); //截取条码左边字符
        //		scanner.interceptTrimright(3);//截取条码右边字符
        //		scanner.filterCharacter("R");//过滤特定字符
//        scanner.SetErrorBroadCast(true);//扫描错误换行
        //scanner.resultScan();//恢复iScan默认设置

//        scanner.lockScanKey();
        //锁定设备的扫描按键,通过iScan定义扫描键扫描，用户也可以自定义按键。
//        scanner.unlockScanKey();
        //释放扫描按键的锁定，释放后iScan无法控制扫描按键，用户可自定义按键扫描。

        /**设置扫描结果的输出模式，参数为0和1：
         * 0为模拟输出（在光标停留的地方输出扫描结果）；
         * 1为广播输出（由应用程序编写广播接收者来获得扫描结果，并在指定的控件上显示扫描结果）
         * 这里采用接收扫描结果广播并在TextView中显示*/
//        scanner.setOutputMode(1);
//        scanSet();
    }

    //自定义按键设置
    public void scanSet() {
       /* scanner.scanKeySet(KeyEvent.KEYCODE_F9, 1);
        scanner.scanKeySet(KeyEvent.KEYCODE_F10, 1);
        scanner.scanKeySet(KeyEvent.KEYCODE_F11, 1);*/
    }

    /**
     * 结束扫描
     */
    private void finishScanner() {
//        scanner.scan_stop();
//        scanner.close();    //关闭iscan  非正常关闭会造成iScan异常退出
//        scanner.continceScan(false);
    }

    @Override
    public void start(Context context) throws Exception {
        if (context == null) {
            Log.e(TAG, "context is null in IData_80_Impl's start");
            return;
        }
        //
        initScanner(context);
        //
        this.mContext = context;
        // 监听
        mBarCodeBroadcastReceiver = new BarCodeBroadcastReceiver();
        IntentFilter barcodeFilter = new IntentFilter();
        barcodeFilter.addAction(ACTION_DECODE_DATA);
        mContext.registerReceiver(mBarCodeBroadcastReceiver, barcodeFilter);
    }

    @Override
    public void close() throws Exception {
        if (mBarCodeBroadcastReceiver != null && mContext != null) {
            mContext.unregisterReceiver(mBarCodeBroadcastReceiver);
        }
        finishScanner();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event, Context context) throws Exception {
        if (keyCode == KeyEvent.KEYCODE_F9 || keyCode == KeyEvent.KEYCODE_F10
                || keyCode == KeyEvent.KEYCODE_F11) {
//            scanner.scan_start();
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event, Context context) throws Exception {
        if (keyCode == KeyEvent.KEYCODE_F9 || keyCode == KeyEvent.KEYCODE_F10
                || keyCode == KeyEvent.KEYCODE_F11) {
//            scanner.scan_stop();
            return true;
        }
        return false;
    }

    public class BarCodeBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_DECODE_DATA.equals(intent.getAction())) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    String data = bundle.getString(EXTRA_BARCODE_STRING).trim();
                    Intent it = new Intent(mContext, AnalyseCodeService.class);
                    it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, data);
                    mContext.startService(it);
                }
            }
        }
    }
}
