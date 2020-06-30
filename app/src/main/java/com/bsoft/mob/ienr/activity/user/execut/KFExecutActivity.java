package com.bsoft.mob.ienr.activity.user.execut;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.activity.user.execut.adapter.KFExecutAdapter;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.model.advice.execut.KFModel;

import java.util.ArrayList;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-13 上午11:37:00
 * @类说明
 */
public class KFExecutActivity extends BaseBarcodeActivity {

	ListView listView;
	ProgressBar emptyProgress;

	TextView text;
	ArrayList<KFModel> list;

	KFExecutAdapter adapter;



	@Override
	protected int setupLayoutResId() {
		return R.layout.layout_content_list;
	}

	@Override
	protected void initView(Bundle savedInstanceState) {

		list = (ArrayList<KFModel>) getIntent().getSerializableExtra("mAdvicePlanVoArrayList");
		findView();
	}

	public void findView() {
		listView = (ListView) findViewById(R.id.id_lv);
		adapter = new KFExecutAdapter(this);
		listView.setAdapter(adapter);
		text = (TextView) findViewById(R.id.id_txt);
		if (null != list && list.size() > 0) {
			adapter.addData(list);
			text.setText("本次共有" + list.size() + "包药需要执行，请全部扫描");
		} else {
			text.setText("本次共有0包药需要执行，请全部扫描");
		}
		emptyProgress = (ProgressBar) findViewById(R.id.emptyProgress);
		actionBar.setTitle("医嘱执行");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void initBarBroadcast() {
		barBroadcast = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent intent) {

				BarcodeEntity entity = (BarcodeEntity) intent
						.getParcelableExtra("barinfo");
				if (entity == null) {
					return;
				}
				if (BarcodeActions.Bar_Get.equals(intent.getAction())) {
					KFModel vo = adapter.changeStatue(entity.TMNR);
					if (null != vo) {
						adapter.notifyDataSetChanged();

						int count = adapter.getExecutCount();
						if (count > 0) {
							text.setText("本次共有" + count + "包药需要执行，请全部扫描");
						} else {
							getIntent().putExtra("qrdh", vo.QRDH);
							KFExecutActivity.this.setResult(RESULT_OK,
									getIntent());
							finish();
						}

					} else {

						showMsgAndVoiceAndVibrator("条码不在此列表中");

                    }

				}
			}
		};
	}

}
