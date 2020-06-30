/**   
 * @Title: ZBKBluetoothImpl.java 
 * @Package com.bsoft.mob.barcode 
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2016-3-15 下午4:51:37 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.barcode;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Toast;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.BluetoothChooseActivity;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BSToast;
import com.smartshell.bluetooth.SmartshellBt;

/**
 * @ClassName: ZBKBluetoothImpl
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2016-3-15 下午4:51:37
 * 
 */
@Deprecated //待确认
public class ZBKBluetoothImpl implements IBarCode {
	public Context mContext;
	public SmartshellBt smartshellbtobj;
	private static final String ACTION_SMARTSHELL_DEVICE_DATA = "action.broadcast.smartshell.data";
	private static final String ACTION_SMARTSHELL_DEVICE_ACK = "com.smartshell.device.ack";
	private static final String ACTION_SMARTSHELL_DEVICE_REQ = "com.smartshell.device.command";
	private BluetoothAdapter mBluetoothAdapter = null;
	private MediaPlayer mCurrentMediaPlayer;

	@Override
	public void setType(int type) {

	}

	@Override
	public void start(Context context) throws Exception {
		this.mContext = context;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			Toast.makeText(mContext, "蓝牙未打开！", Toast.LENGTH_LONG).show();
			return;
		}
		smartshellbtobj = new SmartshellBt(context);

		// 注册接收条码数据的receiver
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_SMARTSHELL_DEVICE_DATA);
		filter.addAction(ACTION_SMARTSHELL_DEVICE_ACK);
		mContext.registerReceiver(myReceiver, filter);

		// 启动蓝牙数据连接服务
		SharedPreferences sharedata = mContext.getSharedPreferences("data", 0);
		String address = sharedata.getString("btaddress", null);
		if (EmptyTool.isBlank(address)) {
			mContext.startActivity(new Intent(mContext,
					BluetoothChooseActivity.class));
		} else {
			linkDevice(address);
		}

	}

	@Override
	public void close() throws Exception {
		smartshellbtobj.ExitJob();
		mContext.unregisterReceiver(myReceiver);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event, Context context)
			throws Exception {
		return false;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event, Context context)
			throws Exception {
		return false;
	}

	// 收到数据的处理函数
	private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String newstr, utfstr;

			// 处理蓝牙传过来的数据信息，根据不同的分类可以处理不同的信息
			if (ACTION_SMARTSHELL_DEVICE_DATA.equals(action)) {

				int datakind = intent.getIntExtra("kind", 0);

				if (datakind == 1) // 条码数据
				{
					newstr = intent.getStringExtra("smartshell_data") + "\n";
				} else if (datakind == 2) // rfid数据
				{
					newstr = intent.getStringExtra("smartshell_data") + "\n";
				} else if (datakind == 3) // 红外数据
				{
					newstr = intent.getStringExtra("smartshell_data") + "\n";
				} else // 其他数据
				{
					newstr = intent.getStringExtra("smartshell_data") + "\n";
				}
				if (!EmptyTool.isBlank(newstr)) {
					String[] result = newstr.split(":");
					if (result.length == 2) {
						Intent it = new Intent(mContext,
								AnalyseCodeService.class);
						if (datakind == 1) {
							it.putExtra(AnalyseCodeService.SCAN_TYPE, 0);
						} else if (datakind == 2) {
							it.putExtra(AnalyseCodeService.SCAN_TYPE, 1);
						}
						it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA,
								result[1]);
						mContext.startService(it);
					}
				} else {
					playSound(R.raw.wrong);
					BSToast.showToast(mContext, "条码扫描出错！", BSToast.LENGTH_LONG);

				}
			}

			// 处理蓝牙传过来的响应信息
			if (ACTION_SMARTSHELL_DEVICE_ACK.equals(action)) {
				int ack = intent.getIntExtra("ack", 0);

				if (ack == 1000) // 蓝牙连接正常
				{
					newstr = "bluetooth link OK" + "\n";
				} else if (ack == 1001) // 蓝牙连接异常
				{
					newstr = "bluetooth link err" + "\n";
				} else {
				}
			}
		}
	};

	private void playSound(int resId) {
		// Stop current player, if there's one playing
		if (null != mCurrentMediaPlayer) {
			mCurrentMediaPlayer.stop();
			mCurrentMediaPlayer.release();
		}

		mCurrentMediaPlayer = MediaPlayer.create(mContext, resId);
		if (null != mCurrentMediaPlayer) {
			mCurrentMediaPlayer.start();
		}
	}

	private void linkDevice(String address) {
		smartshellbtobj.SetSmartlink(true);
		smartshellbtobj.DoJob(address);
	}
}
