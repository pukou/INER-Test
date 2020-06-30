package com.bsoft.mob.ienr.barcode;

import android.content.Context;
import android.content.Intent;
import android.hardware.BarcodeManager;
import android.os.RemoteException;
import android.service.lachesis.barcode.BarcodeListener;
import android.view.KeyEvent;

/**
 * 联新
 * 
 * @author Tank
 */
@Deprecated //待确认
public class LianBarCodeImpl implements IBarCode {

	// private SoundPool sp1 = null;
	// private SoundPool sp2 = null;
	// private SoundPool sp3 = null;
	// private int music1 = 0;
	// private int music2 = 0;
	// private int music3 = 0;

	private BarcodeManager mBarcodeManager;

	// private Dialog alertdialog;
	public Context context;

	@Override
	public void start(Context context) throws Exception {
		this.context = context;
		// sp1 = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
		// sp2 = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
		// sp3 = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
		//
		// music1 = sp1.load(context, R.raw.sound_key, 1);
		// music2 = sp2.load(context, R.raw.sound_ok, 1);
		// music3 = sp3.load(context, R.raw.sound_over_time, 1);
		//
		// alertdialog = new AlertDialog.Builder(context).setTitle(getString(R.string.project_tips))
		// .setMessage("扫描中...").create();
		// alertdialog.setCancelable(false);
		mBarcodeManager = BarcodeManager.getInstance();
		mBarcodeManager.addListener(barcodeListener);
	}

	@Override
	public void close() throws Exception {
		mBarcodeManager.removeListener(barcodeListener);
	}

	private BarcodeListener barcodeListener = new BarcodeListener() {
		@Override
		public void onGetString(final String value) {

			Intent it = new Intent(context, AnalyseCodeService.class);
			it.putExtra(AnalyseCodeService.SCAN_RESULT_EXTRA, value);
			context.startService(it);
			// new GetTask().execute(value);
		}

		@Override
		public void onGetBytes(final byte[] value) {
		}

		@Override
		public void onOpenError() throws RemoteException {
		}
	};

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

	//
	// class GetTask extends AsyncTask<String, Void, ReturnMo> {
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// //alertdialog.show();
	// }
	//
	// @Override
	// protected ReturnMo doInBackground(String... arg0) {
	// try {
	// ReturnMo mo = new ReturnMo();
	// BarCodeInfo bc = new BarCodeInfo(arg0[0]);
	// if (bc.barCode.id == 1) {
	// mo.result = KernelApi
	// .getInstance()
	// .GetPatientForScan(
	// bc.codeString,
	// bc.barCode.prefix,
	// ((AppApplication) ((Activity) context)
	// .getApplication())
	// .getAreaId());
	//
	// }
	// mo.bc = bc;
	// return mo;
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(ReturnMo result) {
	// parserUser(result);
	// //alertdialog.dismiss();
	// }
	// }
	//
	// void parserUser(ReturnMo mo) {
	// if (null == mo) {
	// if (Config.VIBRATOR) {
	// VibratorUtil.vibrator(context);
	// }
	// Toast.makeText(context, "扫描失败", Toast.LENGTH_SHORT).show();
	// sp3.play(music3, 1, 1, 0, 0, 1);
	// return;
	// }
	// if (null != mo.result) {
	// if (mo.bc.barCode.id == 1) {
	// //病人信息特殊处理
	// if (mo.result.isOK()) {
	// ArrayList<SickPersonVo> tList = mo.result.tableMap
	// .get("Table1");
	// if (null != tList && tList.size() > 0) {
	// AppApplication.sickPersonVo = tList.get(0);
	// Intent intent = new Intent(IBarCode.Refresh);
	// context.sendBroadcast(intent);
	// sp2.play(music2, 1, 1, 0, 0, 1);
	// } else {
	// if (Config.VIBRATOR) {
	// VibratorUtil.vibrator(context);
	// }
	// Toast.makeText(context, "无法查找到病人信息", Toast.LENGTH_SHORT)
	// .show();
	// sp3.play(music3, 1, 1, 0, 0, 1);
	// }
	// } else {
	// mo.result.showToast(context);
	// sp3.play(music3, 1, 1, 0, 0, 1);
	// }
	// } else {
	// Intent intent = new Intent(IBarCode.Bar_Get);
	// intent.putExtra("barinfo", mo.bc);
	// context.sendBroadcast(intent);
	// sp2.play(music2, 1, 1, 0, 0, 1);
	// }
	// } else {
	// if (Config.VIBRATOR) {
	// VibratorUtil.vibrator(context);
	// }
	// Toast.makeText(context, "无法查找到病人信息", Toast.LENGTH_SHORT).show();
	// sp3.play(music3, 1, 1, 0, 0, 1);
	// }
	// }

	// void parserUser(ReturnMo mo) {
	// if (null == mo) {
	// Toast.makeText(context, "扫描失败", Toast.LENGTH_SHORT).show();
	// return;
	// }
	// if (null != mo.result) {
	// if (mo.result.isOK()) {
	// ArrayList<SickPersonVo> tList = mo.result.tableMap
	// .get("Table1");
	// if (null != tList && tList.size() > 0) {
	// AppApplication.sickPersonVo = tList.get(0);
	// Intent intent = new Intent(IBarCode.Refresh);
	// context.sendBroadcast(intent);
	// } else {
	// Toast.makeText(context, "无法查找到病人信息", Toast.LENGTH_SHORT)
	// .show();
	// }
	// } else {
	// mo.result.showToast(context);
	// }
	// } else {
	// Toast.makeText(context, "无法查找到病人信息", Toast.LENGTH_SHORT).show();
	// }
	// }

	// class ReturnMo {
	// BarCodeInfo bc;
	// ParserModel result;
	// }

	@Override
	public void setType(int type) {

	}

}
