package com.bsoft.mob.ienr.activity.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.activity.user.adapter.OutAdapter;
import com.bsoft.mob.ienr.api.OutControlApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.outcontrol.OutControl;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 外出历史页 Created by hy on 14-3-21.
 */
public class OutControllerHistoryActivity extends BaseBarcodeActivity {
	private ListView mListView;
	private void initActionBar() {
		actionBar.setTitle("外出记录");
		actionBar.setPatient(mAppApplication.sickPersonVo.BRCH + mAppApplication.sickPersonVo.BRXM);

	}

	@Override
	protected int setupLayoutResId() {
		return R.layout.activity_out_controller_history;
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		mListView = (ListView) findViewById(R.id.id_lv);
		EmptyViewHelper.setEmptyView(mListView,"mListView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout,mListView);
		initActionBar();
		toRefreshData();
	}
	@Override
	protected int configSwipeRefreshLayoutResId() {
		return R.id.id_swipe_refresh_layout;
	}
	@Override
	protected void toRefreshData() {
		GetDateTask task = new GetDateTask();
		task.execute();
		tasks.add(task);
	}


	/**
	 * 查询数据
	 * 
	 * @author hy
	 * 
	 */
	public class GetDateTask extends AsyncTask<String, Integer, Response<List<OutControl>>> {

		@Override
		protected void onPreExecute() {
			showSwipeRefreshLayout();
		}

		@Override
		protected Response<List<OutControl>> doInBackground(String... params) {

			if (mAppApplication.sickPersonVo == null) {
				return null;
			}

			String zyh = mAppApplication.sickPersonVo.ZYH;
			String brbq = mAppApplication.getAreaId();
			String jgid = mAppApplication.jgId;
			int sysType = Constant.sysType;
			Response<List<OutControl>> result = OutControlApi.getInstance(getApplicationContext())
					.GetOutPatientByZyh(zyh, brbq, jgid, sysType);
			return result;
		}

		@Override
		protected void onPostExecute(Response<List<OutControl>> result) {

			hideSwipeRefreshLayout();
			tasks.remove(this);

			if (null == result) {
				showMsgAndVoiceAndVibrator("加载失败");
				return;
			}
			if (result.ReType == 100) {
				new AgainLoginUtil(OutControllerHistoryActivity.this, mAppApplication).showLoginDialog();
				return;
			} else if (result.ReType == 0) {
				@SuppressWarnings("unchecked")
				ArrayList<OutControl> list = (ArrayList<OutControl>)result.Data;
				OutAdapter adapter = new OutAdapter(OutControllerHistoryActivity.this,
						list);
				mListView.setAdapter(adapter);
			} else {
                showMsgAndVoice(result.Msg);
			}
		}
	}

	@Override
	public void initBarBroadcast() {

		barBroadcast = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {

				if (BarcodeActions.Bar_Get.equals(intent.getAction())) {

					BarcodeEntity entity = (BarcodeEntity) intent
							.getParcelableExtra("barinfo");
					if (FastSwitchUtils.needFastSwitch(entity)) {
						Intent result = new Intent(context,
								UserModelActivity.class);
						result.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						result.putExtra("barinfo", entity);
						startActivity(result);
					}

				} else if (BarcodeActions.Refresh.equals(intent.getAction())) {
					Intent result = new Intent(context, UserModelActivity.class);
					result.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					result.putExtra("refresh", true);
					startActivity(result);
				}
			}
		};
	}

	// @Override
	// public void initBarBroadcast() {
	//
	// barBroadcast = new BroadcastReceiver() {
	// @Override
	// public void onReceive(Context arg0, Intent intent) {
	// if (IBarCode.Refresh.equals(intent.getAction())) {
	//
	// performGetTask();
	// }
	// }
	// };
	// }

}
