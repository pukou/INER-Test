package com.bsoft.mob.ienr.fragment.base;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.IBarCode;
import com.bsoft.mob.ienr.helper.BarCodeHelper;

/**
 * 支持和响应条码扫描操作事件的 Base Fragment
 * 
 * @author hy
 * 
 */
public abstract class BaseBarcodeFragment extends BaseFragment {
	protected BroadcastReceiver barBroadcast;

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (Constant.DEBUG) {
			if (actionBar != null) {
				actionBar.setOnLongClickListener(new View.OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						//
						BarCodeHelper.testBarcode(v.getContext());
						return true;
					}
				});
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (null != barBroadcast) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(BarcodeActions.Bar_Get);
			filter.addAction(BarcodeActions.RFID_Get);
			filter.addAction(BarcodeActions.Refresh);
			getActivity().registerReceiver(barBroadcast, filter);
		}
	}

	@Override
	public void onPause() {
		if (null != barBroadcast) {
			getActivity().unregisterReceiver(barBroadcast);
		}
		super.onPause();
	}

	public void sendUserName() {
		Intent intent = new Intent(IBarCode.Name_Change);
		getActivity().sendBroadcast(intent);
	}

}
