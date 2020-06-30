/**   
 * @Title: NFCBridgeActivity.java 
 * @Package com.bsoft.mob.ienr.activity 
 * @Description: NFC桥接activity用来将读取的的信息发送出去 
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-12-30 下午2:17:29 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.activity;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;

import com.bsoft.mob.ienr.barcode.AnalyseCodeService;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BSToast;

/**
 * @ClassName: NFCBridgeActivity
 * @Description: NFC桥接activity用来将读取的的信息发送出去
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2015-12-30 下午2:17:29
 * 
 */
public class NFCBridgeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Tag tagFromIntent = getIntent()
				.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		String data = tagFromIntent.getId().toString();
		BSToast.showToast(getApplicationContext(), data, BSToast.LENGTH_LONG);

		if (!EmptyTool.isBlank(data)) {
			Intent it = new Intent(this, AnalyseCodeService.class);
			it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, data);
			it.putExtra(AnalyseCodeService.SCAN_TYPE, 1);
			startService(it);
		}
		finish();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);

	}
}
