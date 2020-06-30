/**   
 * @Title: FloatingWindowService.java 
 * @Package com.bsoft.mob.ienr.service 
 * @Description: 悬浮窗serivice 
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2016-3-31 上午11:11:31 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.BluetoothChooseActivity;
import com.bsoft.mob.ienr.activity.MipcaCaptureActivity;
import com.bsoft.mob.ienr.barcode.BarCodeFactory;
import com.bsoft.mob.ienr.barcode.Devices;
import com.smartshell.bluetooth.SmartshellBt;

import java.util.concurrent.Executors;

/**
 * @ClassName: FloatingWindowService
 * @Description: 悬浮窗serivice
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2016-3-31 上午11:11:31
 * 
 */
public class FloatingWindowService extends Service {
	private static final String TAG = "FloatingWindowService";
	// ZBK蓝牙扫描枪相关
	private static final String ACTION_SMARTSHELL_DEVICE_REQ = "com.smartshell.device.command";
	public SmartshellBt smartshellbtobj;

	private static final String WINDOWX = "window_x";
	private static final String WINDWOY = "window_y";
	private SharedPreferences preferences;
	// 定义浮动窗口布局
	LinearLayout mFloatLayout;
	WindowManager.LayoutParams wmParams;
	// 创建浮动窗口设置布局参数的对象
	WindowManager mWindowManager;

	ImageView mFloatView, mSettingView;

	boolean isMove;
	private float mTouchStartX;
	private float mTouchStartY;
	private float x;
	private float y;

	@Override
	public void onCreate() {
		super.onCreate();
		if (BarCodeFactory.getBarCodeStr().equals(Devices.D_02_ZBKBluetooth)) {
			smartshellbtobj = new SmartshellBt(this);
			// 启动蓝牙数据连接服务
			SharedPreferences sharedata = getSharedPreferences("data", 0);
			String address = sharedata.getString("btaddress", null);
			smartshellbtobj.SetSmartlink(true);
			smartshellbtobj.DoJob(address);
		}
		preferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);
		createFloatView();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private void createFloatView() {
		wmParams = new WindowManager.LayoutParams();
		// 获取的是WindowManagerImpl.CompatModeWrapper
		mWindowManager = (WindowManager) getApplication().getSystemService(
				getApplication().WINDOW_SERVICE);
		// 设置window type
		wmParams.type = LayoutParams.TYPE_PHONE;
		// 设置图片格式，效果为背景透明
		wmParams.format = PixelFormat.RGBA_8888;
		// 设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
		// 调整悬浮窗显示的停靠位置为左侧置顶
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		// 以屏幕左上角为原点，设置x、y初始值，相对于gravity
		wmParams.x = preferences.getInt(WINDOWX, 300);
		wmParams.y = preferences.getInt(WINDOWX, 600);

		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

		LayoutInflater inflater = LayoutInflater.from(getApplication());
		// 获取浮动窗口视图所在布局
		mFloatLayout = (LinearLayout) inflater.inflate(
				R.layout.layout_floating, null);
		// 添加mFloatLayout
		mWindowManager.addView(mFloatLayout, wmParams);
		// 浮动窗口按钮
		mFloatView = (ImageView) mFloatLayout.findViewById(R.id.float_id);
		mSettingView = (ImageView) mFloatLayout.findViewById(R.id.float_set);
		mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		// 设置监听浮动窗口的触摸移动;
		mFloatView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
				showSetting();
				x = event.getRawX();
				y = event.getRawY();
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					isMove = false;
					// 获取相对View的坐标，即以此View左上角为原点
					mTouchStartX = event.getX();
					mTouchStartY = event.getY();
					break;
				case MotionEvent.ACTION_MOVE:
					isMove = true;
					// getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
					wmParams.x = (int) event.getRawX()
							- mFloatView.getMeasuredWidth() / 2;
					// 减25为状态栏的高度
					wmParams.y = (int) event.getRawY()
							- mFloatView.getMeasuredHeight() / 2 - 25;
					// 刷新
					mWindowManager.updateViewLayout(mFloatLayout, wmParams);

					save();
					break; // 此处必须返回false，否则OnClickListener获取不到监听
				case MotionEvent.ACTION_UP:
					doWork();
					break;
				}
				return true;
			}
		});

		mSettingView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(FloatingWindowService.this,
						BluetoothChooseActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mFloatLayout != null) {
			// 移除悬浮窗口
			mWindowManager.removeView(mFloatLayout);
		}
		if (BarCodeFactory.getBarCodeStr().equals(Devices.D_02_ZBKBluetooth))
			smartshellbtobj.ExitJob();
	}

	private void doWork() {
		if (!isMove) {
			if (BarCodeFactory.getBarCodeStr().equals(Devices.D_02_ZBKBluetooth)){
				Intent intent = new Intent();
				intent.setAction(ACTION_SMARTSHELL_DEVICE_REQ);
				intent.putExtra("cmd", 1); // 不同的参数表示不同的命令 1 ----触发软件扫描
				sendBroadcast(intent);
			} else if (BarCodeFactory.getBarCodeStr().equals(Devices.D_01_NoBar)) {
				Intent intent = new Intent(FloatingWindowService.this,
						MipcaCaptureActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
			isMove = false;
		}
	}

	private void showSetting() {
		if (BarCodeFactory.getBarCodeStr().equals(Devices.D_02_ZBKBluetooth)) {
			mSettingView.setVisibility(View.VISIBLE);
			mSettingView.postDelayed(new Runnable() {

				@Override
				public void run() {
					mSettingView.setVisibility(View.GONE);
				}
			}, 3000);
		}
	}

	private void save() {
		Executors.newSingleThreadExecutor().execute(new Runnable() {
			@Override
			public void run() {
				preferences.edit().putInt(WINDOWX, (int) (x - mTouchStartX))
						.apply();
				preferences.edit().putInt(WINDWOY, (int) (y + mTouchStartY))
						.apply();
			}
		});
	}
}