/**   
 * @Title: HoneywellBarcodeImpl.java 
 * @Package com.bsoft.mob.ienr.barcode 
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2016-2-26 下午1:33:29 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.barcode;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.bsoft.mob.ienr.Constant;
import com.honeywell.aidc.AidcManager;
import com.honeywell.aidc.AidcManager.CreatedCallback;
import com.honeywell.aidc.BarcodeFailureEvent;
import com.honeywell.aidc.BarcodeReadEvent;
import com.honeywell.aidc.BarcodeReader;
import com.honeywell.aidc.BarcodeReader.BarcodeListener;
import com.honeywell.aidc.BarcodeReader.TriggerListener;
import com.honeywell.aidc.ScannerUnavailableException;
import com.honeywell.aidc.TriggerStateChangeEvent;
import com.honeywell.aidc.UnsupportedPropertyException;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: HoneywellBarcodeImpl
 * @Description: 霍尼韦尔扫描接口
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2016-2-26 下午1:33:29
 * 
 */
@Deprecated //待确认
public class HoneywellBarcodeImpl implements IBarCode,
		BarcodeReader.BarcodeListener, BarcodeReader.TriggerListener {
	Context mContext;
	private static BarcodeReader barcodeReader;
	private AidcManager manager;
	private BarcodeListener barcodeListener;
	private TriggerListener triggerListener;

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

		// create the AidcManager providing a Context and a
		// CreatedCallback implementation.
		AidcManager.create(mContext, new CreatedCallback() {

			@Override
			public void onCreated(AidcManager aidcManager) {
				manager = aidcManager;
				barcodeReader = manager.createBarcodeReader();
				initBarcodeReader();
			}
		});
	}

	private void initBarcodeReader() {
		if (barcodeReader != null) {

			// register bar code event listener
			barcodeReader.addBarcodeListener(this);

			// set the trigger mode to client control
			try {
				barcodeReader.setProperty(
						BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
						BarcodeReader.TRIGGER_CONTROL_MODE_AUTO_CONTROL);
			} catch (UnsupportedPropertyException e) {
				Toast.makeText(mContext, "Failed to apply properties",
						Toast.LENGTH_SHORT).show();
			}
			// register trigger state change listener
			barcodeReader.addTriggerListener(this);

			Map<String, Object> properties = new HashMap<String, Object>();
			// Set Symbologies On/Off
			properties.put(BarcodeReader.PROPERTY_CODE_128_ENABLED, true);
			properties.put(BarcodeReader.PROPERTY_GS1_128_ENABLED, true);
			properties.put(BarcodeReader.PROPERTY_QR_CODE_ENABLED, true);
			properties.put(BarcodeReader.PROPERTY_CODE_39_ENABLED, true);
			properties.put(BarcodeReader.PROPERTY_DATAMATRIX_ENABLED, true);
			properties.put(BarcodeReader.PROPERTY_UPC_A_ENABLE, true);
			properties.put(BarcodeReader.PROPERTY_EAN_13_ENABLED, false);
			properties.put(BarcodeReader.PROPERTY_AZTEC_ENABLED, false);
			properties.put(BarcodeReader.PROPERTY_CODABAR_ENABLED, false);
			properties
					.put(BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED, false);
			properties.put(BarcodeReader.PROPERTY_PDF_417_ENABLED, false);
			// Set Max Code 39 barcode length
			properties.put(BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH, 10);
			// Turn on center decoding
			properties.put(BarcodeReader.PROPERTY_CENTER_DECODE, true);
			// Enable bad read response
			properties.put(
					BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED, true);
			// Apply the settings
			barcodeReader.setProperties(properties);

			try {
				barcodeReader.claim();
			} catch (ScannerUnavailableException e) {
				e.printStackTrace();
				Toast.makeText(mContext, "Scanner unavailable",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void close() throws Exception {
		if (barcodeReader != null) {
			// unregister barcode event listener
			barcodeReader.removeBarcodeListener(barcodeListener);

			// unregister trigger state change listener
			barcodeReader.removeTriggerListener(triggerListener);
		}
		if (barcodeReader != null) {
			// close BarcodeReader to clean up resources.
			barcodeReader.close();
			barcodeReader = null;
		}

		if (manager != null) {
			// close AidcManager to disconnect from the scanner service.
			// once closed, the object can no longer be used.
			manager.close();
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

	@Override
	public void onTriggerEvent(TriggerStateChangeEvent event) {
	}

	@Override
	public void onBarcodeEvent(BarcodeReadEvent event) {
		String data = event.getBarcodeData().trim();
		Intent it = new Intent(mContext, AnalyseCodeService.class);
		it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, data);
		mContext.startService(it);
	}

	@Override
	public void onFailureEvent(BarcodeFailureEvent arg0) {
	}

}
