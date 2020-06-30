package com.bsoft.mob.ienr.components.mqtt;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.api.APIUrlConfig;
import com.bsoft.mob.ienr.util.DeviceUtil;
import com.bsoft.mob.ienr.util.prefs.WifiPrefUtils;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * An example of how to implement an MQTT client in Android, able to receive
 * push notifications and publish topic message from an MQTT message broker
 * server.
 */
public class MQTTService extends Service implements MqttCallback {

    // something unique to identify your app - used for stuff like accessing
    // application preferences
    public static final String TAG = "MQTTService";

    // constants used to notify the Activity UI of received messages
    public static final String MQTT_MSG_RECEIVED_INTENT = "com.bsoft.android.mqtt.MSGRECVD";
    public static final String MQTT_MSG_RECEIVED_TOPIC = "com.bsoft.android.mqtt.MSGRECVD_TOPIC";
    public static final String MQTT_MSG_RECEIVED_MSG = "com.bsoft.android.mqtt.MSGRECVD_MSGBODY";

    // constants used to tell the Activity UI the connection status
    public static final String MQTT_STATUS_INTENT = "com.bsoft.android.mqtt.STATUS";
    public static final String MQTT_STATUS_MSG = "com.bsoft.android.mqtt.STATUS_MSG";

    // constant used internally to schedule the next ping event
    public static final String MQTT_PING_ACTION = "com.bsoft.android.mqtt.PING";

    // constants used by status bar notifications
    public static final int MQTT_NOTIFICATION_ONGOING = 1;
    public static final int MQTT_NOTIFICATION_UPDATE = 2;

    // constants used to notify unsubscribe topic
    /**
     * 广播取消主题
     */
    public static final String MQTT_UNSUBSCRIBE_INTENT = "com.bsoft.android.mqtt.UNSUBSCRIBE";
    /**
     * 所有主题列表(值类型为：ArrayList<String>),作为{@link #MQTT_UNSUBSCRIBE_INTENT}EXTRA传递
     */
    public static final String MQTT_TOPICS = "com.bsoft.android.mqtt.TOPICS";

    // constants used to define MQTT connection status
    public enum MQTTConnectionStatus {
        INITIAL, // initial status
        CONNECTING, // attempting to connect
        CONNECTED, // connectedf
        NOTCONNECTED_WAITINGFORINTERNET, // can't connect because the phone
        // does not have Internet access
        NOTCONNECTED_USERDISCONNECT, // user has explicitly requested
        // disconnection
        NOTCONNECTED_DATADISABLED, // can't connect because the user
        // has disabled data access
        NOTCONNECTED_UNKNOWNREASON // failed to connect for some reason
    }

    // MQTT constants
    public static final int MAX_MQTT_CLIENTID_LENGTH = 22;

    // status of MQTT client connection
    private MQTTConnectionStatus connectionStatus = MQTTConnectionStatus.INITIAL;

    // host name of the server we're receiving push notifications from
    // public static final String brokerHostName = "192.168.10.2";
    // public static final String brokerHostName = "192.168.1.10";
    // public static final String brokerHostName = "101.68.72.186";

    // mqtt url deport
    public static final int brokerPortNumber = 1883;

    // all topics
    private ArrayList<String> topics;

    // This is how the Android client app will identify itself to the
    // message broker.
    // It has to be unique to the broker - two clients are not permitted to
    // connect to the same broker using the same client ID.
    private String mqttClientId = null;

    // connection to the message broker
    // private IMqttClient mqttClient = null;
    private IMqttAsyncClient mqttClient = null;

    // receiver that notifies the Service when the phone gets data connection
    private NetworkConnectionIntentReceiver netConnReceiver;

    // Background thread executor service
    ExecutorService es;

    public ExecutorService getEs() {

        if (es == null) {
            es = Executors.newSingleThreadExecutor();
        }
        return es;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // create a binder that will let the Activity UI send
        // commands to the Service
        mBinder = new LocalBinder<MQTTService>(this);

        initConnect();
    }

    // TODO 优化读取Preference
    private void initConnect() {
        // reset status variable to initial state
        connectionStatus = MQTTConnectionStatus.INITIAL;
        // define the connection to the broker
        String brokerHostName = WifiPrefUtils.getPushIP(this);
        if (EmptyTool.isBlank(brokerHostName)) {
            brokerHostName = APIUrlConfig.DEFAULT_IP;
        }
        defineConnectionToBroker(brokerHostName);
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, final int startId) {

        if (intent == null) {
            return super.onStartCommand(intent, flags, startId);
        }

        String doType = intent.getStringExtra(MQTTServiceConfig.WITH_MQTT_TYPE);
        // Do an appropriate action based on the intent.
        if (MQTTServiceConfig.MQTT_STOP.equals(doType)) {
            // disconnect();
            stopSelf();
        } else if (MQTTServiceConfig.MQTT_START.equals(doType)) {

            getEs().execute(new Runnable() {

                @Override
                public void run() {
                    handleStart(intent, startId);
                }
            });

        } else if (MQTTServiceConfig.MQTT_UNSUBSCRIBE.equals(doType)) {

            getEs().execute(new Runnable() {

                @Override
                public void run() {
                    handleUnsubscribe(intent, startId);
                }
            });

        } else if (MQTTServiceConfig.MQTT_PUBLISH.equals(doType)) {

            getEs().execute(new Runnable() {

                @Override
                public void run() {
                    publish(intent, startId);
                }
            });

        }
        return START_REDELIVER_INTENT;// 返回时返回START_STICKY就可以了
    }

    /**
     * public topic message
     *
     * @param intent
     * @param startId
     */
    private void publish(Intent intent, int startId) {

        // TODO 实现异步通知
        if (isAlreadyConnected()) {

            String msg = intent.getStringExtra("msg");
            String topic = intent.getStringExtra("topic");
            try {
                mqttClient.publish(topic, msg.getBytes(), 2, true);
            } catch (MqttPersistenceException e) {
                Log.e("mqtt", "publish failed - MQTT not publish", e);
            } catch (MqttException e) {
                Log.e("mqtt", "publish failed - MQTT not publish", e);
            }
        }
    }

    /**
     * handle cancel subscribe request
     *
     * @param intent
     * @param startId
     */
    protected void handleUnsubscribe(Intent intent, int startId) {

        if (mqttClient == null) {
            return;
        }

        if (isAlreadyConnected()) {

            ArrayList<String> unsubTopics = intent
                    .getStringArrayListExtra("topics");
            String appkey = intent
                    .getStringExtra(MQTTServiceConfig.MQTT_UNSUBSCRIBE_APP_EXTRA);
            unsubscribe(unsubTopics, appkey);
        }
    }

    /**
     * cancel subscribe with the topics
     *
     * @param topics subscribe topics
     * @param appkey the application key that request cancel subscribe,generated by
     *               bsoft bill system
     */
    private void unsubscribe(ArrayList<String> topics, String appkey) {

        if (isOnline()) {
            unsubscribeToTopic(topics, appkey);
        } else {
            // we can't do anything now because we don't have a working
            // data connection
            connectionStatus = MQTTConnectionStatus.NOTCONNECTED_WAITINGFORINTERNET;
            // inform the app that we are not connected
            // broadcastServiceStatus("信使服务连接失败-网络不可用");
            notifyUser("信使服务连接失败-网络不可用");
        }
    }

    synchronized void handleStart(Intent intent, int startId) {
        // before we start - check for a couple of reasons why we should stop

        if (mqttClient == null) {
            initConnect();
        }

        if (MQTTConnectionStatus.NOTCONNECTED_WAITINGFORINTERNET == connectionStatus) {

            if (isOnline()) {
                // we have an internet connection - have another try at
                // connecting
                if (connectToBroker()) {
                    // we subscribe to a topic - registering to receive push
                    // notifications with a particular key
                    subscribeToTopic(topics);
                }
            }
        }

        // if the Service was already running and we're already connected - we
        // don't need to do anything
        if (isAlreadyConnected() == false) {
            // set the status to show we're trying to connect
            connectionStatus = MQTTConnectionStatus.CONNECTING;

            mqttClientId = intent.getStringExtra("mqttClientId");
            ArrayList<String> subTopics = intent
                    .getStringArrayListExtra("topics");
            // before we attempt to connect - we check if the phone has a
            // working data connection
            topics = ListUtil.filterList(subTopics);
            subscribe(topics, true);
        } else {// 已连接

            ArrayList<String> newTopics = intent
                    .getStringArrayListExtra("topics");
            addTopics(newTopics);
        }

        // changes to the phone's network - such as bouncing between WiFi
        // and mobile data networks - can break the MQTT connection
        // the MQTT connectionLost can be a bit slow to notice, so we use
        // Android's inbuilt notification system to be informed of
        // network changes - so we can reconnect immediately, without
        // haing to wait for the MQTT timeout
        if (netConnReceiver == null) {
            netConnReceiver = new NetworkConnectionIntentReceiver();
            IntentFilter filter = new IntentFilter(
                    ConnectivityManager.CONNECTIVITY_ACTION);
            // IntentFilter filter = new IntentFilter();
            // filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            registerReceiver(netConnReceiver, filter);

        }

    }

    private void addTopics(ArrayList<String> newTopics) {

        if (newTopics != null) {
            if (topics == null || topics.isEmpty()) {
                topics = ListUtil.filterList(newTopics);
                subscribe(topics, false);
            } else {
                List<String> add = new ArrayList<String>();
                for (String topic : newTopics) {
                    if (!topics.contains(topic)) {
                        add.add(topic);
                    }
                }
                if (add.size() > 0) {
                    topics.addAll(add);
                    subscribe(add, false);
                }
            }
        }

    }

    public void subscribe(List<String> newTopics, boolean connectToBroker) {

        if (isOnline()) {
            if (connectToBroker) {
                if (connectToBroker()) {
                    subscribeToTopic(newTopics);
                }
            } else {
                subscribeToTopic(newTopics);
            }
        } else {
            // we can't do anything now because we don't have a working
            // data connection
            connectionStatus = MQTTConnectionStatus.NOTCONNECTED_WAITINGFORINTERNET;
            // inform the app that we are not connected
            // broadcastServiceStatus("信使服务连接失败-网络不可用");
            notifyUser("信使服务连接失败-网络不可用");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        disconnect();

        if (mBinder != null) {
            mBinder.close();
            mBinder = null;
        }

        stopForeground(true);

    }

    // methods used to notify the Activity UI of something that has happened
    // so that it can be updated to reflect status and the data received
    // from the server

    private void broadcastServiceStatus(String statusDescription) {
        // inform the app (for times when the Activity UI is running /
        // active) of the current MQTT connection status so that it
        // can update the UI accordingly
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MQTT_STATUS_INTENT);
        broadcastIntent.putExtra(MQTT_STATUS_MSG, statusDescription);
        sendBroadcast(broadcastIntent);
    }

    private void broadcastReceivedMessage(String topic, String message) {
        // pass a message received from the MQTT server on to the Activity UI
        // (for times when it is running / active) so that it can be displayed
        // in the app GUI
        Intent broadcastIntent = new Intent();

        broadcastIntent.setAction(MQTT_MSG_RECEIVED_INTENT);
        // broadcastIntent.setAction(ManifestMetaData.getString(this,
        // "bsoft_APPKEY"));
        broadcastIntent.putExtra(MQTT_MSG_RECEIVED_TOPIC, topic);
        broadcastIntent.putExtra(MQTT_MSG_RECEIVED_MSG, message);
        sendBroadcast(broadcastIntent);
    }

    // // methods used to notify the user of what has happened for times when
    // // the app Activity UI isn't running
    //
    private void notifyUser(final String body) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(body);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, TopicsActivity.class);
        resultIntent.putExtra(TopicsActivity.KEY_FOR_TOPICS, body);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                getApplicationContext(), 0, resultIntent,
                Intent.FLAG_ACTIVITY_NEW_TASK);

        mBuilder.setContentIntent(resultPendingIntent);

        startForeground(MQTT_NOTIFICATION_UPDATE, mBuilder.build());

    }

    // trying to do local binding while minimizing leaks - code thanks to
    // Geoff Bruckner - which I found at
    // http://groups.google.com/group/cw-android/browse_thread/thread/d026cfa71e48039b/c3b41c728fedd0e7?show_docid=c3b41c728fedd0e7

    private LocalBinder<MQTTService> mBinder;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder<S> extends Binder {
        private WeakReference<S> mService;

        public LocalBinder(S service) {
            mService = new WeakReference<S>(service);
        }

        public S getService() {
            return mService.get();
        }

        public void close() {
            mService = null;
        }
    }

    /**
     * public methods that can be used by Activities that bind to the Service
     *
     * @return
     */
    public MQTTConnectionStatus getConnectionStatus() {
        return connectionStatus;
    }

    public void rebroadcastStatus() {
        String status = "";

        switch (connectionStatus) {
            case INITIAL:
                status = "Please wait";
                break;
            case CONNECTING:
                status = "Connecting...";
                break;
            case CONNECTED:
                status = "Connected";
                break;
            case NOTCONNECTED_UNKNOWNREASON:
                status = "Not connected - waiting for network connection";
                break;
            case NOTCONNECTED_USERDISCONNECT:
                status = "Disconnected";
                break;
            case NOTCONNECTED_DATADISABLED:
                status = "Not connected - background data disabled";
                break;
            case NOTCONNECTED_WAITINGFORINTERNET:
                status = "Unable to connect";
                break;
        }

        //
        // inform the app that the Service has successfully connected
        broadcastServiceStatus(status);
    }

    public void disconnect() {
        disconnectFromBroker();

        // set status
        connectionStatus = MQTTConnectionStatus.NOTCONNECTED_USERDISCONNECT;

        // inform the app that the app has successfully disconnected
        // broadcastServiceStatus("信使服务连接已断开");
        notifyUser("信使服务连接已断开");
    }

    /**
     * Create a client connection object that defines our connection to a
     * message broker server
     */
    private void defineConnectionToBroker(String brokerHostName) {

        String brokerPortNumber = WifiPrefUtils.getPushPort(this);
        if (EmptyTool.isBlank(brokerPortNumber)) {
            brokerPortNumber = APIUrlConfig.DEFAULT_PUSH_PORT;
        }
        String mqttConnSpec = "tcp://" + brokerHostName + ":"
                + brokerPortNumber;

        try {
            // define the connection to the broker
            mqttClient = new MqttAsyncClient(mqttConnSpec, generateClientId(),
                    new MemoryPersistence());
            // mqttClient = new MqttClient(mqttConnSpec, generateClientId(),
            // new MemoryPersistence());

            // register this client app has being able to receive messages
            mqttClient.setCallback(this);
        } catch (MqttException e) {
            // something went wrong!
            mqttClient = null;
            connectionStatus = MQTTConnectionStatus.NOTCONNECTED_UNKNOWNREASON;

            //
            // inform the app that we failed to connect so that it can update
            // the UI accordingly
            // broadcastServiceStatus("信使服务连接失败-连接参数无效");
            notifyUser("信使服务连接失败-连接参数无效");

            //
            // inform the user (for times when the Activity UI isn't running)
            // that we failed to connect
            // notifyUser("Unable to connect", "MQTT", "Unable to connect");
        }
    }

    /**
     * (Re-)connect to the message broker
     */
    private boolean connectToBroker() {

        // boolean success = false;

        try {
            if (mqttClient == null) {
                initConnect();
            }

            if (mqttClient == null) {
                return false;
            }
            if (mqttClient.isConnected()) {
                return true;
            }
            // 创建一个连接对象
            MqttConnectOptions conOptions = new MqttConnectOptions();
            // 设置清除会话信息
            conOptions.setCleanSession(true);
            // 设置超时时间
            conOptions.setConnectionTimeout(5000);

            // 设置会话心跳时间
            // conOptions.setKeepAliveInterval(20000);
            // try to connect
            mqttClient.connect(conOptions).waitForCompletion();

            notifyUser("信使服务已连接");
            // we are connected
            connectionStatus = MQTTConnectionStatus.CONNECTED;

            return true;
        } catch (MqttException e) {
            Log.e(TAG, e.getMessage(), e);
            connectionStatus = MQTTConnectionStatus.NOTCONNECTED_UNKNOWNREASON;

            //
            // inform the app that we failed to connect so that it can
            // update
            // the UI accordingly
            notifyUser("信使服务连接失败");
            return false;
        } catch (OutOfMemoryError e) {
            Log.e(TAG, e.getMessage(), e);
            connectionStatus = MQTTConnectionStatus.NOTCONNECTED_UNKNOWNREASON;

            //
            // inform the app that we failed to connect so that it can
            // update
            // the UI accordingly
            notifyUser("信使服务连接失败");
            return false;
        }

        // Future<Boolean> future = getEs().submit(new ConnectTask());
        //
        // try {
        // success = future.get();
        // if (success) {
        // notifyUser("信使服务已连接");
        // // we are connected
        // connectionStatus = MQTTConnectionStatus.CONNECTED;
        //
        // } else {
        // connectionStatus = MQTTConnectionStatus.NOTCONNECTED_UNKNOWNREASON;
        //
        // //
        // // inform the app that we failed to connect so that it can
        // // update
        // // the UI accordingly
        // notifyUser("信使服务连接失败");
        // }
        //
        // } catch (Exception e) {
        // Log.e(TAG, e.getMessage(), e);
        // }
        // return success;
    }

    /**
     * Send a request to the message broker to be sent messages published with
     * the specified topic name. Wildcards are allowed.
     */
    private void subscribeToTopic(List<String> topics) {
        boolean subscribed = false;

        if (isAlreadyConnected() == false) {
            // quick sanity check - don't try and subscribe if we
            // don't have a connection
            Log.e("mqtt", "Unable to subscribe as we are not connected");
        } else {
            try {

                if (topics != null && !topics.isEmpty()) {
                    String[] topicsArray = topics.toArray(new String[0]);
                    int[] intArray = new int[topicsArray.length];
                    // 默认所有连接质量都为2
                    for (int i = 0; i < intArray.length; i++) {
                        intArray[i] = 2;
                    }
                    mqttClient.subscribe(topicsArray, intArray);
                    notifySubTopics();
                }
                subscribed = true;
            } catch (MqttException e) {
                Log.e("mqtt", "subscribe failed - MQTT exception", e);
            } catch (Exception e) {
                Log.e("mqtt", "unsubscribe failed -  exception", e);
            }
        }

        if (subscribed == false) {

            // inform the app of the failure to subscribe so that the UI can
            // display an error
            // broadcastServiceStatus("Unable to subscribe");
            StringBuilder sb = new StringBuilder();
            if (topics != null) {
                for (String topic : topics) {
                    sb.append(topic).append(" ");
                }
            }
            notifyUser("Unable to subscribe " + sb.toString());
            // inform the user (for times when the Activity UI isn't running)
            // notifyUser("Unable to subscribe", "MQTT", "Unable to subscribe");
        }
    }

    public void notifySubTopics() {

        if (topics == null || topics.isEmpty()) {
            notifyUser("当前没有订阅任何主题 ");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (String topic : topics) {
            sb.append(topic).append("\n");
        }
        notifyUser("当前订阅主题包括：\n " + sb.toString());
    }

    /**
     * Send a request to the message broker to be sent messages published with
     * the specified topic name. Wildcards are allowed.
     */
    private void unsubscribeToTopic(ArrayList<String> topics, String appkey) {
        boolean unsubscribed = false;

        if (isAlreadyConnected() == false) {
            // quick sanity check - don't try and subscribe if we
            // don't have a connection

            Log.e("mqtt", "Unable to unsubscribe as we are not connected");
        } else {
            try {
                ArrayList<String> result = ListUtil.filterList(topics);
                if (result != null && !result.isEmpty()) {
                    String[] topicsArray = result.toArray(new String[0]);
                    mqttClient.unsubscribe(topicsArray);
                    removeTopics(result);
                    notifySubTopics();
                }
                unsubscribed = true;

            } catch (MqttException e) {
                Log.e("mqtt", "unsubscribe failed - MQTT exception", e);
            } catch (Exception e) {
                Log.e("mqtt", "unsubscribe failed -  exception", e);
            }
        }

        if (unsubscribed == false) {
            //
            // inform the app of the failure to subscribe so that the UI can
            // display an error
            // broadcastServiceStatus("Unable to subscribe");
            StringBuilder sb = new StringBuilder();
            if (topics != null) {
                for (String topic : topics) {
                    sb.append(topic).append(" ");
                }
            }
            notifyUser("Unable to unsubscribe " + sb.toString());
            //
            // inform the user (for times when the Activity UI isn't running)
            // notifyUser("Unable to subscribe", "MQTT", "Unable to subscribe");
        } else {
            // 广播用户取消主题
            Intent intent = new Intent(MQTT_UNSUBSCRIBE_INTENT);
            intent.putStringArrayListExtra(MQTT_TOPICS, topics);
            intent.putExtra(MQTTServiceConfig.MQTT_UNSUBSCRIBE_APP_EXTRA,
                    appkey);
            sendBroadcast(intent);
        }
    }

    private void removeTopics(ArrayList<String> result) {

        if (result != null) {
            if (topics != null && !topics.isEmpty()) {
                for (String topic : result) {
                    if (topics.contains(topic)) {
                        topics.remove(topic);
                    }
                }
            }
        }
    }

    /**
     * Terminates a connection to the message broker.
     */
    private void disconnectFromBroker() {
        // if we've been waiting for an Internet connection, this can be
        // cancelled - we don't need to be told when we're connected now
        try {
            if (netConnReceiver != null) {
                unregisterReceiver(netConnReceiver);
                netConnReceiver = null;
            }
        } catch (Exception eee) {
            // probably because we hadn't registered it
            Log.e("mqtt", "unregister failed", eee);
        }

        getEs().execute(new Runnable() {

            @Override
            public void run() {
                unConnect();
            }
        });

    }

    /**
     * Checks if the MQTT client thinks it has an active connection
     */
    private boolean isAlreadyConnected() {
        return ((mqttClient != null) && (mqttClient.isConnected() == true));
    }

    /*
     * Called in response to a change in network connection - after losing a
     * connection to the server, this allows us to wait until we have a usable
     * data connection again
     */
    private class NetworkConnectionIntentReceiver extends BroadcastReceiver {
        @SuppressLint("Wakelock")
        @Override
        public void onReceive(Context ctx, Intent intent) {

            // we protect against the phone switching off while we're doing this
            // by requesting a wake lock - we request the minimum possible wake
            // lock - just enough to keep the CPU running until we've finished
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            WakeLock wl = pm
                    .newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MQTT");
            wl.acquire();

            if (isOnline()) {

                getEs().execute(new Runnable() {

                    @Override
                    public void run() {
                        // we have an internet connection - have another try at
                        // connecting
                        if (connectToBroker()) {
                            // we subscribe to a topic - registering to receive
                            // push
                            // notifications with a particular key
                            subscribeToTopic(topics);
                        }
                    }
                });

            }

            // we're finished - if the phone is switched off, it's okay for the
            // CPU
            // to sleep now
            wl.release();
        }
    }

    private String generateClientId() {
        // generate a unique client id if we haven't done so before, otherwise
        // re-use the one we already have

        if (TextUtils.isEmpty(mqttClientId)) {
            // generate a unique client ID - I'm basing this on a combination of
            // the phone device id and the current timestamp

           String timestamp = "" + (new Date()).getTime();
            String android_id = Settings.System.getString(getContentResolver(),
                    Secure.ANDROID_ID);
            if (TextUtils.isEmpty(android_id)){
                android_id = DeviceUtil.getSerial();
            }
            mqttClientId = timestamp + android_id;

            // truncate - MQTT spec doesn't allow client ids longer than 23
            // chars
            if (mqttClientId.length() > MAX_MQTT_CLIENTID_LENGTH) {
                mqttClientId = mqttClientId.substring(0,
                        MAX_MQTT_CLIENTID_LENGTH);
            }
        }

        return mqttClientId;
    }


    @Deprecated //may case java.lang.RuntimeException: Error receiving broadcast Intent { act=android.net.conn.CONNECTIVITY_CHANGE flg=0x4000010 (has extras) } in org.eclipse.paho.android.service.MqttService$NetworkConnectionIntentReceiver@46f509a
    // Attempt to invoke virtual method 'boolean android.net.NetworkInfo.isConnected()' on a null object reference

    private boolean isOnlineOld() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isConnected()) {
            return true;
        }
        return false;
    }
    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null
                && networkInfo.isAvailable()
                && networkInfo.isConnected() ) {
            return true;
        }

        return false;
    }

    public class MQTTServiceConfig {

        public static final String WITH_MQTT_TYPE = "mqtt.TYPE";

        public static final String MQTT_START = "mqtt.START";
        public static final String MQTT_STOP = "mqtt.STOP";
        public static final String MQTT_UNSUBSCRIBE = "mqtt.UNSUBSCRIBE";
        public static final String MQTT_PUBLISH = "mqtt.PUBLISH";

        public static final String MQTT_SERVICE_ACTION = "com.bsoft.mob.mqtt.MQTTService";

        /**
         * 取消MQTTT 主题订阅 app key
         */
        public static final String MQTT_UNSUBSCRIBE_APP_EXTRA = "mqtt.UNSUBSCRIBE_APP_EXTRA";
    }

    // /**
    // * 异步连接MQTT Client
    // *
    // */
    // class ConnectTask implements Callable<Boolean> {
    //
    // @Override
    // public Boolean call() throws Exception {
    // try {
    // if (mqttClient == null) {
    // initConnect();
    // }
    //
    // if (mqttClient == null) {
    // return false;
    // }
    // if (mqttClient.isConnected()) {
    // return true;
    // }
    // // 创建一个连接对象
    // MqttConnectOptions conOptions = new MqttConnectOptions();
    // // 设置清除会话信息
    // conOptions.setCleanSession(true);
    // // 设置超时时间
    // conOptions.setConnectionTimeout(5000);
    //
    // // 设置会话心跳时间
    // // conOptions.setKeepAliveInterval(20000);
    // // try to connect
    // mqttClient.connect(conOptions).waitForCompletion();
    // return true;
    // } catch (MqttException e) {
    // Log.e(TAG, e.getMessage(), e);
    // return false;
    // } catch (OutOfMemoryError e) {
    // Log.e(TAG, e.getMessage(), e);
    // return false;
    // }
    // }
    // }

    // class UnConnectTask implements Callable<Boolean> {
    //
    // @Override
    // public Boolean call() throws Exception {
    //
    // try {
    // if (mqttClient != null) {
    // if (topics != null) {
    // mqttClient.unsubscribe(topics.toArray(new String[topics
    // .size()]));
    // }
    // mqttClient.disconnect();
    // }
    // return true;
    // } catch (MqttException e) {
    // Log.e("mqtt", "disconnect failed - persistence exception", e);
    // e.printStackTrace();
    // } finally {
    // mqttClient = null;
    // }
    // return false;
    // }
    // }

    public void unConnect() {

        try {
            if (mqttClient != null) {
                if (topics != null) {
                    mqttClient.unsubscribe(topics.toArray(new String[topics
                            .size()]));
                }
                mqttClient.disconnect();
            }
        } catch (MqttException e) {
            Log.e("mqtt", "disconnect failed - persistence exception", e);

        } finally {
            mqttClient = null;
        }
    }

    @SuppressLint("Wakelock")
    @Override
    public void connectionLost(Throwable arg0) {

        // we protect against the phone switching off while we're doing this
        // by requesting a wake lock - we request the minimum possible wake
        // lock - just enough to keep the CPU running until we've finished
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MQTT");
        wl.acquire();

        //
        // have we lost our data connection?
        //

        if (isOnline() == false) {
            connectionStatus = MQTTConnectionStatus.NOTCONNECTED_WAITINGFORINTERNET;

            // inform the app that we are not connected any more
            // broadcastServiceStatus("信使服务连接丢失 - 网络不可用");
            notifyUser("信使服务连接丢失 - 网络不可用");

            //
            // inform the user (for times when the Activity UI isn't running)
            // that we are no longer able to receive messages
            // notifyUser("Connection lost - no network connection", "MQTT",
            // "Connection lost - no network connection");

            //
            // wait until the phone has a network connection again, when we
            // the network connection receiver will fire, and attempt another
            // connection to the broker
        } else {
            //
            // we are still online
            // the most likely reason for this connectionLost is that we've
            // switched from wifi to cell, or vice versa
            // so we try to reconnect immediately
            //

            connectionStatus = MQTTConnectionStatus.NOTCONNECTED_UNKNOWNREASON;

            // inform the app that we are not connected any more, and are
            // attempting to reconnect
            // broadcastServiceStatus("Connection lost - reconnecting...");
            notifyUser("信息服务连接丢失-重新连接中...");

            getEs().execute(new Runnable() {

                @Override
                public void run() {
                    // try to reconnect
                    if (connectToBroker()) {
                        subscribeToTopic(topics);
                    }
                }
            });

        }

        // we're finished - if the phone is switched off, it's okay for the CPU
        // to sleep now
        wl.release();
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // TODO 发布完成响应函数

    }

    @SuppressLint("Wakelock")
    @Override
    public void messageArrived(String topic, MqttMessage message)
            throws Exception {

        // we protect against the phone switching off while we're doing this
        // by requesting a wake lock - we request the minimum possible wake
        // lock - just enough to keep the CPU running until we've finished
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MQTT");
        wl.acquire();

        //
        // I'm assuming that all messages I receive are being sent as strings
        // this is not an MQTT thing - just me making as assumption about what
        // data I will be receiving - your app doesn't have to send/receive
        // strings - anything that can be sent as bytes is valid
        String messageBody = new String(message.getPayload());

        //
        // inform the app (for times when the Activity UI is running) of the
        // received message so the app UI can be updated with the new data
        broadcastReceivedMessage(topic, messageBody);

        //
        // for times when the app's Activity UI is not running, the Service
        // will need to safely store the data that it receives
        // if (addReceivedMessageToStore(topic, messageBody)) {
        // // this is a new message - a value we haven't seen before
        //
        // //
        // // inform the app (for times when the Activity UI is running) of the
        // // received message so the app UI can be updated with the new data
        // broadcastReceivedMessage(topic, messageBody);
        //
        // //
        // // inform the user (for times when the Activity UI isn't running)
        // // that there is new data available
        // // notifyUser("New data received", topic, messageBody);
        // }

        // we're finished - if the phone is switched off, it's okay for the CPU
        // to sleep now
        wl.release();
    }
}
