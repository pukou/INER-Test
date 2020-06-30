package com.bsoft.mob.ienr.components.mqtt;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.bsoft.mob.ienr.AppApplication;
import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.components.mqtt.MQTTService.MQTTServiceConfig;

import java.util.ArrayList;

/**
 * MQTT 服务工具类
 * 
 */
public class MQTTTool {
	private static ArrayList<String>    bqTopic = null;       //病区主题
	private Context mContext;

	private MQTTTool(Context mContext) {

		this.mContext = mContext;

	}

	private MQTTTool() {

	}

	private static MQTTTool instance_;

	private static final byte[] obj = new byte[0];

	public static MQTTTool getInstance(Context mContext) {

		if (instance_ == null) {
			synchronized (obj) {
				if (instance_ == null) {
					instance_ = new MQTTTool(mContext);
				}
				return instance_;
			}
		}
		return instance_;
	}
	//设置病区主题(先取消之前病区订阅的主题，再设置当前病区订阅的主题)
	public void setBqTopic(String  bqTopicStr){
		if(TextUtils.isEmpty(bqTopicStr)) return;

		if(bqTopic != null){
			if(bqTopic.size() > 0){
				unsubscirbeTopic(bqTopic, ""); //移除旧的病区主题
				bqTopic.clear();
			}
		}else{
			bqTopic = new ArrayList<String>();
		}

		bqTopic.add(bqTopicStr);
		subscribeTopics(bqTopic, "");  //增加主题
	}
	/**
	 * 启动MQTT服务
	 * 
	 * @param topics
	 *            订阅主题列表,非空
	 * 
	 * @param mqttClientId
	 *            MQTT 连接client id ,如果为NULL，系统默认生成一个clientid.
	 *            <p>
	 *            注意：当系统中存在一个MQTT连接时，如果当前连接的clientid与传入的mqttClientId不相等，不会取消当前连接
	 *            ，只是增加订阅主题
	 *            </p>
	 * 
	 */
	public void subscribeTopics(ArrayList<String> topics, String mqttClientId) {

		if (topics == null || topics.size() == 0 || mContext == null) {
			Log.e(Constant.TAG, "params is not right in topics method");
		}

		Intent service = new Intent(MQTTServiceConfig.MQTT_SERVICE_ACTION);
		service.putStringArrayListExtra("topics", topics);
		service.putExtra("mqttClientId", mqttClientId);
		service.putExtra(MQTTServiceConfig.WITH_MQTT_TYPE,
				MQTTServiceConfig.MQTT_START);
		service.setPackage(AppApplication.getInstance().getPackageName());
		mContext.startService(service);

	}

	/**
	 * 取消订阅主题
	 * 
	 * @param topics
	 *            订阅主题数组
	 * @param appKey
	 *            应用APP KEY
	 */
	public void unsubscirbeTopic(ArrayList<String> topics, String appKey) {

		if (topics == null || topics.size() == 0 || mContext == null) {
			Log.e(Constant.TAG, "params is not right in unsubscirbeTopic method");
		}
		Intent service = new Intent(MQTTServiceConfig.MQTT_SERVICE_ACTION);
		service.putStringArrayListExtra("topics", topics);
		service.putExtra(MQTTServiceConfig.WITH_MQTT_TYPE,
				MQTTServiceConfig.MQTT_UNSUBSCRIBE);
		service.putExtra(MQTTServiceConfig.MQTT_UNSUBSCRIBE_APP_EXTRA, appKey);
		service.setPackage(AppApplication.getInstance().getPackageName());
		mContext.startService(service);

	}

	/**
	 * 停止MQTT服务
	 */
	public void stopMQTT() {

		if (mContext == null) {
			return;
		}
		// MQTTService.actionStop(mContext);
		Intent service = new Intent(MQTTServiceConfig.MQTT_SERVICE_ACTION);
		service.putExtra(MQTTServiceConfig.WITH_MQTT_TYPE,
				MQTTServiceConfig.MQTT_STOP);
		service.setPackage(AppApplication.getInstance().getPackageName());
		mContext.startService(service);

	}

}
