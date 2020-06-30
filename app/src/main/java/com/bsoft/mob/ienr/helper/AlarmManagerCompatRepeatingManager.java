package com.bsoft.mob.ienr.helper;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.support.v4.app.AlarmManagerCompat;
import android.util.Log;

/**
 * Created by louisgeek on 2018/7/31.
 * AlarmManager 实现定时服务
 * 兼容 Android 7/8
 * mAlarmManager.setRepeating
 * 在新版本设备上不准确
 * 所以采用自我实现重复定时任务实现
 */
public abstract class AlarmManagerCompatRepeatingManager {
    private static final String TAG = "AMCRepeatingManager";
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mAction_AlarmManagerCompatRepeating.equals(intent.getAction())) {
                //需要自我实现重复定时任务 restart
                AlarmManagerCompat.setExactAndAllowWhileIdle(mAlarmManager, AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        SystemClock.elapsedRealtime() + mIntervalMillis, mPendingIntent);
                //硬件系统时间 RTC_WAKEUP & System.currentTimeMillis()
                //物理真实时间 ELAPSED_REALTIME_WAKEUP & SystemClock.elapsedRealtime()
                // AlarmManagerCompat.setExactAndAllowWhileIdle(mAlarmManager, AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ mIntervalMillis, mPendingIntent);
                //
//                Log.d(TAG, "onAlarmManagerReceive");
                onAlarmManagerReceive();
            }
        }
    };
    private AlarmManager mAlarmManager;
    private long mIntervalMillis;
    private PendingIntent mPendingIntent;
    private Context mContext;
    private String mAction_AlarmManagerCompatRepeating;

/*
    public AlarmManagerCompatRepeatingManager(Context mContext) {
        this.mIntervalMillis = 60 * 1000 * 3;//3分
        this.mContext = mContext;
    }
*/

    /**
     * @param context
     * @param intervalMillis 时间差值
     */
    public AlarmManagerCompatRepeatingManager(Context context, long intervalMillis) {
        this.mIntervalMillis = intervalMillis;
        this.mContext = context;
        mAction_AlarmManagerCompatRepeating = context.getPackageName() + ".action.AlarmManagerCompatRepeatingManager";
        Log.d(TAG, mAction_AlarmManagerCompatRepeating);
    }

    public void cancel() {
        if (mAlarmManager != null) {
            mAlarmManager.cancel(mPendingIntent);
        }
        if (mBroadcastReceiver != null) {
            mContext.unregisterReceiver(mBroadcastReceiver);
        }
    }

    /**
     * @param immediateExecute 是否立即执行
     */
    public void init(boolean immediateExecute) {
        //registerReceiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(mAction_AlarmManagerCompatRepeating);
        mContext.registerReceiver(mBroadcastReceiver, intentFilter);

        //
        mAlarmManager = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(mAction_AlarmManagerCompatRepeating);
        mPendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//      mPendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
        //start AlarmManager
        if (immediateExecute) {
            AlarmManagerCompat.setExactAndAllowWhileIdle(mAlarmManager, AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime(), mPendingIntent);
        } else {
            AlarmManagerCompat.setExactAndAllowWhileIdle(mAlarmManager, AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    SystemClock.elapsedRealtime() + mIntervalMillis, mPendingIntent);
        }
        //硬件系统时间 RTC_WAKEUP & System.currentTimeMillis()
        //物理真实时间 ELAPSED_REALTIME_WAKEUP & SystemClock.elapsedRealtime()
//      AlarmManagerCompat.setExactAndAllowWhileIdle(mAlarmManager, AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), mPendingIntent);
    }

    protected abstract void onAlarmManagerReceive();
}
