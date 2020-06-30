package com.bsoft.mob.ienr.components.mqtt;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.MessageActivity;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.db.Database;
import com.bsoft.mob.ienr.model.message.Message;
import com.bsoft.mob.ienr.model.message.Topic;
import com.bsoft.mob.ienr.model.message.TopicConfig;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.MessageUtils;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 接收服务推消息
 */
public class PushMsgReceiver extends BroadcastReceiver {

    protected Context context;

    private MediaPlayer mCurrentMediaPlayer;

    ExecutorService es = Executors.newSingleThreadExecutor();

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        if (MQTTService.MQTT_MSG_RECEIVED_INTENT.equals(intent.getAction())) {

            final String topic = intent
                    .getStringExtra(MQTTService.MQTT_MSG_RECEIVED_TOPIC);
            final String massage = intent
                    .getStringExtra(MQTTService.MQTT_MSG_RECEIVED_MSG);

            es.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        analyPushMsg(topic, massage);
                    } catch (Exception e) {
                        Log.e(Constant.TAG, e.getMessage(), e);
                    }
                }
            });

            //Toast.makeText(context, massage, Toast.LENGTH_LONG).show();
        }

    }

    /**
     * 分析推送的消息,空实现
     *
     * @param topic   订阅的主题
     * @param massage 推送过来的消息
     */
    public void analyPushMsg(String topic, String massage) {

        try {

            Topic tp = JsonUtil.fromJson(topic, Topic.class);

            if (EmptyTool.isBlank(tp.APP)
                    || !TopicConfig.APP_VALUE.equals(tp.APP)) {
                return;
            }
            if (EmptyTool.isBlank(tp.JGID)) {
                return;
            }

            com.bsoft.mob.ienr.model.message.Message msg = JsonUtil.fromJson(
                    massage, com.bsoft.mob.ienr.model.message.Message.class);

            boolean saved = hasExisted(msg.MsgId);
            if (saved) {
                return;
            }
            playSound(R.raw.personalinfo);

           /* // 危险值直接弹框
            if (msg.MsgType == 2) {
                startMessageActivity(msg.Content, msg.MsgType, msg.MsgId);
            } else {
                notifyUser(msg.Content, msg.MsgType, msg.MsgId);
            }*/

            if (TextUtils.isEmpty(msg.ZDTX)) msg.ZDTX = "0";

// 危险值(或者主动提醒的消息)直接弹框
//if (msg.MsgType == 2) {
            if (msg.MsgType == 3 || "1".equals(msg.ZDTX)) {
                startMessageActivity(msg.Content, msg.MsgType, msg.MsgId, msg.LXMC);
            } else {
                notifyUser(msg.Content, msg.MsgType, msg.MsgId, msg.LXMC);
            }

//MengDW 发送过来的消息无时间时,避免无法保存在本地,造成一直提醒(此处很重要)
            if (TextUtils.isEmpty(msg.Time)) {
                msg.Time = DateTimeHelper.getServerDateTime();
            }


            // 保存topic
            saveMsgTopic(msg.MsgType);

            // 简单判断：
            if (!EmptyTool.isBlank(tp.YHID)) {// 个人
                savePrivateMsg(msg, tp.YHID, tp.JGID, context);
            } else {// 院级或病区级
                saveSystemMsg(msg, context);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void saveMsgTopic(int msgType) {

        Uri url = Database.Topic.CONTENT_URI;

        String[] projection = {BaseColumns._ID};
        String selection = Database.Topic.TOPIC + "=?";
        String[] selectionArgs = {String.valueOf(msgType)};
        Cursor cursor = context.getContentResolver().query(url, projection,
                selection, selectionArgs, null);

        if (cursor.getCount() > 0) {
            cursor.close();
            return;
        }
        cursor.close();

        ContentValues values = new ContentValues();
        values.put(Database.Topic.TOPIC, msgType);
        context.getContentResolver().insert(url, values);
    }

    /**
     * 弹出消息对话框
     *
     * @param msgId
     */
    private void startMessageActivity(String body, int topic, long msgId, String lxmc)
    {

    Intent resultIntent = new Intent(context, MessageActivity.class);
        resultIntent.putExtra(MessageActivity.KEY_FOR_CONTENT, body);
        resultIntent.putExtra(MessageActivity.KEY_FOR_TITLE, topic);
        resultIntent.putExtra(MessageActivity.KEY_FOR_ID, msgId);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        resultIntent.putExtra(MessageActivity.KEY_FOR_LXMC, lxmc);

        context.startActivity(resultIntent);
    }

    private void notifyUser(String body, int topic, long msgId, String lxmc) {

        // TODO 后期通过保存next,确认唯一id
        java.util.Random r = new java.util.Random();
        int next = r.nextInt(1000);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                context).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(MessageUtils.getTopicChars(topic))
                .setContentText(body)
                .setWhen(System.currentTimeMillis()); //设置通知时间
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, MessageActivity.class);
        resultIntent.putExtra(MessageActivity.KEY_FOR_CONTENT, body);
        resultIntent.putExtra(MessageActivity.KEY_FOR_ID, msgId);
        resultIntent.putExtra(MessageActivity.KEY_FOR_NOTIF_ID, next);

        //增加类型名称
        resultIntent.putExtra(MessageActivity.KEY_FOR_LXMC, lxmc);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                context.getApplicationContext(), next, resultIntent,
                Intent.FLAG_ACTIVITY_NEW_TASK);

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(next, mBuilder.build());

    }

    // 保存院级系统消息
    private void saveSystemMsg(Message msg, Context context) {

        try {
            Uri url = Database.Message.CONTENT_URI;
            ContentValues values = new ContentValues();
            values.put(Database.Message.AGENT_ID, msg.Agency);
            values.put(Database.Message.CONTENT, msg.Content);
            values.put(Database.Message.STATE, 0);
            // values.put(Database.Message.USER, msg.UserId);
            values.put(Database.Message.RECEIVE_TIME, msg.Time);
            values.put(Database.Message.BUSINESS_ID, msg.BusinessId);
            values.put(Database.Message.TOPIC, msg.MsgType);
            values.put(Database.Message.REMOTE_ID, msg.MsgId);
            context.getContentResolver().insert(url, values);

        } catch (Exception e) {
            Log.e(Constant.TAG, e.getMessage(), e);
        }

    }

    // 保存个人消息
    private void savePrivateMsg(Message msg, String yhid, String jgid,
                                Context context) {
        try {
            Uri url = Database.Message.CONTENT_URI;
            ContentValues values = new ContentValues();
            values.put(Database.Message.AGENT_ID, msg.Agency);
            values.put(Database.Message.CONTENT, msg.Content);
            values.put(Database.Message.STATE, 0);
            values.put(Database.Message.USER, queryUser_ID(yhid, jgid, context));
            values.put(Database.Message.RECEIVE_TIME, msg.Time);
            values.put(Database.Message.BUSINESS_ID, msg.BusinessId);
            values.put(Database.Message.TOPIC, msg.MsgType);
            values.put(Database.Message.REMOTE_ID, msg.MsgId);
            context.getContentResolver().insert(url, values);

        } catch (Exception e) {
            Log.e(Constant.TAG, e.getMessage(), e);
        }

    }

    private boolean hasExisted(long msgId) {

        Uri url = Database.Message.CONTENT_URI;
        String[] projection = {BaseColumns._ID};
        String selection = Database.Message.REMOTE_ID + "=?";
        String[] selectionArgs = {String.valueOf(msgId)};
        Cursor cursor = context.getContentResolver().query(url, projection,
                selection, selectionArgs, null);
        if (cursor.moveToNext()) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    private int queryUser_ID(String yhid, String jgid, Context context) {

        Uri url = Database.User.CONTENT_URI;
        String[] projection = {BaseColumns._ID};
        String selection = Database.User.REMOTE_ID + "=? AND "
                + Database.User.AGENT_ID + "=?";
        String[] selectionArgs = {yhid, jgid};
        Cursor cursor = context.getContentResolver().query(url, projection,
                selection, selectionArgs, null);

        int _id = 0;
        if (cursor.moveToNext()) {
            _id = cursor.getInt(0);
        }
        cursor.close();
        return _id;

    }

    private void playSound(int resId) {
        // Stop current player, if there's one playing
        if (null != mCurrentMediaPlayer) {
            mCurrentMediaPlayer.stop();
            mCurrentMediaPlayer.release();
        }

        mCurrentMediaPlayer = MediaPlayer.create(context, resId);
        if (null != mCurrentMediaPlayer) {
            mCurrentMediaPlayer.start();
        }
    }
}
