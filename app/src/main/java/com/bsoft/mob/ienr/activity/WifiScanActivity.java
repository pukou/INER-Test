/*
 * Wifi Connecter
 * 
 * Copyright (c) 20101 Kevin Yuan (farproc@gmail.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 **/

package com.bsoft.mob.ienr.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TwoLineListItem;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;

import java.util.List;

public class WifiScanActivity extends BaseBarcodeActivity {

	private WifiManager mWifiManager;
	private List<ScanResult> mScanResults;

	private ListView mListView;



	@Override
	protected int setupLayoutResId() {
		return R.layout.activity_wifi_scan;
	}

	@Override
	protected void toRefreshData() {
		super.toRefreshData();
	}

	@Override
	protected void initView(Bundle savedInstanceState) {


		mWifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);

		mListView = (ListView) findViewById(R.id.id_lv);
		actionBar.setTitle("WIFI热点列表");


		mListView.setAdapter(mListAdapter);

		mListView.setOnItemClickListener(mItemOnClick);
	}

	@Override
	public void onResume() {
		super.onResume();
		final IntentFilter filter = new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		registerReceiver(mReceiver, filter);
		mWifiManager.startScan();
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(mReceiver);
	}

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			final String action = intent.getAction();
			if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				mScanResults = mWifiManager.getScanResults();
				mListAdapter.notifyDataSetChanged();

				mWifiManager.startScan();
			}

		}
	};

	private BaseAdapter mListAdapter = new BaseAdapter() {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null
					|| !(convertView instanceof TwoLineListItem)) {
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_list_text_two_vert_primary, null);
			}

			final ScanResult result = mScanResults.get(position);
			((TextView)convertView.findViewById(R.id.id_tv_one)).setText(result.SSID);
			((TextView)convertView.findViewById(R.id.id_tv_two)).setText(String.format("%s  %d\n%s", result.BSSID, result.level,
							result.capabilities));
			return convertView;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public int getCount() {
			return mScanResults == null ? 0 : mScanResults.size();
		}
	};

	private OnItemClickListener mItemOnClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			final ScanResult result = mScanResults.get(position);
			Intent intent = new Intent();
			intent.putExtra("result", result);
			setResult(RESULT_OK, intent);
			finish();
		}
	};

	@Override
	public void initBarBroadcast() {

	}

}
