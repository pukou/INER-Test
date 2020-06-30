package com.bsoft.mob.ienr.barcode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.example.scandemo.SerialPort;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 深圳市攀凌科技 H900
 * 
 *
 */
@Deprecated //待确认
public class H900BarCodeImpl implements IBarCode {

	Context mContext;
	SerialPort mSerialPort;
	InputStream mInputStream;
	FunkeyListener receiver; // 功能键广播接收者
	boolean run;
	Timer timer = null;

	MediaPlayer mCurrentMediaPlayer;

	@Override
	public void setType(int type) {

	}

	@Override
	public void start(Context context) throws Exception {

		if (context == null) {
			Log.e(Constant.TAG, "context is null in Unitech_PA700_Impl's start");
			return;
		}

		this.mContext = context;

		try {
			mSerialPort = new SerialPort(0, 9600, 0);// scaner
			mSerialPort.scaner_poweron();
			run = true;
			mInputStream = mSerialPort.getInputStream();
			new ReadThread().start();

			receiver = new FunkeyListener();
			// 代码注册功能键广播接收者
			IntentFilter filter = new IntentFilter();
			filter.addAction("android.intent.action.FUN_KEY");
			mContext.registerReceiver(receiver, filter);
		} catch (SecurityException e) {
			Log.e(Constant.TAG, e.getMessage(), e);
		} catch (Exception e) {
			Log.e(Constant.TAG, e.getMessage(), e);
		}

	}

	@Override
	public void close() throws Exception {

		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		run = false;
		if (mInputStream != null) {
			try {
				mInputStream.close();
			} catch (Exception e) {
				Log.e(Constant.TAG, e.getMessage(), e);
			}
		}
		mSerialPort.scaner_poweroff(); // 关闭电源
		mSerialPort.close(14); // 关闭串口

		if (receiver != null) {
			mContext.unregisterReceiver(receiver);
			receiver = null;
		}

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

	/**
	 * 读线程 ,读取设备返回的信息，将其回传
	 * 
	 * 
	 *
	 */
	private class ReadThread extends Thread {

		@Override
		public void run() {

			while (run) {
				int size;
				try {
					byte[] buffer = new byte[512];
					if (mInputStream == null)
						return;
					size = mInputStream.read(buffer);
					if (size > 0) {
						// data = Tools.Bytes2HexString(buffer, size);
						/* 中文字符编码 */
						// data = new String(buffer, 0, size, "GB2312");
						String data = new String(buffer, 0, size, "UTF-8");
						// String data = new String(buffer, 0, size);
						Log.e(Constant.TAG, data);
						Log.e(Constant.TAG, data.length() + "long");
						// PDA 会在 scaner_poweron 方法后，输出一个长度的字符串
						if (!EmptyTool.isBlank(data) && data.length() > 2) {
							playSound(R.raw.sound_key);
							Intent it = new Intent(mContext,
									AnalyseCodeService.class);
							it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA,
									data);
							mContext.startService(it);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}
			}
		}
	}

	// 功能按键广播监听
	private class FunkeyListener extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			boolean defaultdown = false;
			int keycode = intent.getIntExtra("keycode", 0);
			boolean keydown = intent.getBooleanExtra("keydown", defaultdown);
			Log.i("ServiceDemo", "receiver:keycode=" + keycode + "keydown="
					+ keydown);
			// 左侧按键
			if (keycode == 133 && keydown) {
				sendCmd();
			}
			// 如果要用右键，则把下面注解去掉，并把上面左侧代码注解
			// 右侧按键
			// if (keycode == 134 && keydown) {
			// sendCmd();
			// }

		}

	}

	public void sendCmd() {

		if (timer != null) {
			timer.cancel();
			timer = null;
		}
		if (mSerialPort.scaner_trig_stat() == true) {
			mSerialPort.scaner_trigoff();
		}
		mSerialPort.scaner_trigon(); // 触发扫描

		timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				mSerialPort.scaner_trigoff(); // 设置5s超时
			}
		}, 5000);
	}

	public void playSound(int resId) {
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

}
