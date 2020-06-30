/**   
 * @Title: BluetoothChooseActivity.java 
 * @Package com.bsoft.mob.ienr.activity 
 * @Description: 蓝牙扫描枪设备选择界面 
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2016-3-30 下午5:15:20 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseActivity;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.Set;

/**
 * @ClassName: BluetoothChooseActivity
 * @Description: 蓝牙扫描枪设备选择界面
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2016-3-30 下午5:15:20
 * 
 */
public class BluetoothChooseActivity extends BaseActivity implements
		OnClickListener {
	private static final int BLUETOOTH_SETTING_FINISH = 0;
	private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
	private static final int REQUEST_ENABLE_BT = 2;
	private static String EXTRA_DEVICE_ADDRESS = "device_address";
	private ListView mPairedBluetoothList, mNewBluetoothList;
	private TextView mBluetoothAddress;
	private Button mScan, mBack;
	private String mAddress;
	private ArrayAdapter<String> mPairedDevicesArrayAdapter;
	private ArrayAdapter<String> mNewDevicesArrayAdapter;
	// Local Bluetooth adapter
	private BluetoothAdapter mBluetoothAdapter = null;

	@Override
	protected int setupLayoutResId() {
		return R.layout.activity_bluetooth_choose;
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		// Get local Bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// If the adapter is null, then Bluetooth is not supported
		if (mBluetoothAdapter == null) {
			showMsgAndVoiceAndVibrator("当前设备不支持蓝牙！");
		}

		// Register for broadcasts when a device is discovered
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		this.registerReceiver(mReceiver, filter);

		// Register for broadcasts when discovery has finished
		filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		this.registerReceiver(mReceiver, filter);
		initView();
	}

	@Override
	protected void onStart() {
		super.onStart();
		// 如果蓝牙未开，启动本机蓝牙
		// If BT is not on, request that it be enabled.
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Make sure we're not doing discovery anymore
		if (mBluetoothAdapter != null) {
			mBluetoothAdapter.cancelDiscovery();
		}

		// Unregister broadcast listeners
		this.unregisterReceiver(mReceiver);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case REQUEST_CONNECT_DEVICE_SECURE:
			if (resultCode == BLUETOOTH_SETTING_FINISH) {
				scan();
			}
			break;
		case REQUEST_ENABLE_BT:
			if (resultCode == Activity.RESULT_OK) {
				scan();
			}
			default:
		}
	}

	private void initView() {
		actionBar.setTitle("选择蓝牙设备");
		mPairedBluetoothList = (ListView) findViewById(R.id.id_lv);
		mPairedBluetoothList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// Get the device MAC address, which is the last 17 chars in the
				// View
				String info = ((TextView) arg1).getText().toString();
				if (TextUtils.equals("没有配对设备", info)) {
					if (mBluetoothAdapter.isEnabled()) {
						startActivityForResult(new Intent(
								Settings.ACTION_BLUETOOTH_SETTINGS),
								REQUEST_CONNECT_DEVICE_SECURE);
					} else {
						Intent enableIntent = new Intent(
								BluetoothAdapter.ACTION_REQUEST_ENABLE);
						startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
					}
				} else if (info.contains("\n")) {
					mAddress = info.substring(info.indexOf("\n")).trim();
					connectDevice(mAddress);
					mBluetoothAddress.setText(mAddress);
				}

			}
		});
		mNewBluetoothList = (ListView) findViewById(R.id.id_lv_2);
		mNewBluetoothList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				if (mBluetoothAdapter.isEnabled()) {
					startActivityForResult(new Intent(
							Settings.ACTION_BLUETOOTH_SETTINGS),
							REQUEST_CONNECT_DEVICE_SECURE);
				} else {
					Intent enableIntent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
				}
			}
		});
		mBluetoothAddress = (TextView) findViewById(R.id.txtbtaddress);
		mScan = (Button) findViewById(R.id.btnScan);
		mScan.setOnClickListener(this);
		mBack = (Button) findViewById(R.id.btnOk);
		mBack.setOnClickListener(this);
		SharedPreferences sharedata = getSharedPreferences("data", 0);
		mAddress = sharedata.getString("btaddress", null);

		if (mAddress != null) {
			mBluetoothAddress.setText(mAddress);
		}
		mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.item_list_text_one_primary);
		mNewDevicesArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.item_list_text_one_primary);
		// Get a set of currently paired devices
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter
				.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				mPairedDevicesArrayAdapter.add(device.getName() + "\n"
						+ device.getAddress());
			}
		} else {
			mPairedDevicesArrayAdapter.add("没有配对设备");
		}
		mPairedBluetoothList.setAdapter(mPairedDevicesArrayAdapter);
		mNewBluetoothList.setAdapter(mNewDevicesArrayAdapter);
	}

	private void scan() {
		showInfoDialog("正在搜索蓝牙设备");
		// If we're already discovering, stop it
		if (mBluetoothAdapter.isDiscovering()) {
			mBluetoothAdapter.cancelDiscovery();
		}
		mPairedDevicesArrayAdapter.clear();
		mNewDevicesArrayAdapter.clear();
		// Request discover from BluetoothAdapter
		mBluetoothAdapter.startDiscovery();
	}

	// 需要启动服务
	private void connectDevice(String address) {
		SharedPreferences.Editor sharedata = getSharedPreferences("data", 0)
				.edit();
		sharedata.putString("btaddress", address);
		sharedata.commit();
	}

	// The BroadcastReceiver that listens for discovered devices and
	// changes the title when discovery is finished
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// If it's already paired, skip it, because it's been listed
				// already

				if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
					mNewDevicesArrayAdapter.add(device.getName() + "\n"
							+ device.getAddress());
				} else {
					mPairedDevicesArrayAdapter.add(device.getName() + "\n"
							+ device.getAddress());
				}
				// When discovery is finished, change the Activity title
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				hideSwipeRefreshLayout();
				if (mPairedDevicesArrayAdapter.getCount() == 0) {
					mPairedDevicesArrayAdapter.add("没有配对设备");
				}
				if (mNewDevicesArrayAdapter.getCount() == 0) {
					mNewDevicesArrayAdapter.add("附近没有可用的蓝牙设备");
				}
			}
		}
	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnScan:
			scan();
			break;
		case R.id.btnOk:
			if (EmptyTool.isBlank(mAddress) || TextUtils.equals("无", mAddress)) {

				showMsgAndVoiceAndVibrator("当前为蓝牙扫描枪模式，必须链接蓝牙设备才可以使用该系统！");
				return;
			}
			finish();
			break;
		}
	}
}
