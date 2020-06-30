package com.bsoft.mob.ienr.activity.user.execut;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.activity.user.execut.adapter.SYExecutAdapter;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.advice.execut.SYModel;
import com.bsoft.mob.ienr.util.DisplayUtil;

import java.util.ArrayList;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-13 上午11:37:00
 * @类说明
 */
public class SYExecutActivity extends BaseBarcodeActivity {

	ListView listView;
	ProgressBar emptyProgress;

	TextView text;
	ArrayList<SYModel> list;

	SYExecutAdapter adapter;



	@Override
	protected int setupLayoutResId() {
		return R.layout.layout_content_list;
	}

	@Override
	protected void initView(Bundle savedInstanceState) {

		list = (ArrayList<SYModel>) getIntent().getSerializableExtra("mAdvicePlanVoArrayList");
		setLayoutParams();
		findView();
	}

	public void findView() {
		listView = (ListView) findViewById(R.id.id_lv);
		adapter = new SYExecutAdapter(this);
		listView.setAdapter(adapter);
		if (null != list && list.size() > 0) {
			adapter.addData(list);
		}

		emptyProgress = (ProgressBar) findViewById(R.id.emptyProgress);
		text = (TextView) findViewById(R.id.id_txt);
		text.setText("请扫描所要接瓶的瓶贴条码,或者点击下列组中的任意一个药品");
		actionBar.setTitle("接瓶");
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final SYModel vo = list.get(position);
				StringBuilder sb = new StringBuilder();
				for (SYModel sm : list) {
					if (sm.TMBH == vo.TMBH) {
						sb.append(sm.YZMC).append("\n");
					}
				}
                View txt = ViewBuildHelper.buildDialogTitleTextView(mContext, "接瓶确认");
				new AlertDialog.Builder(SYExecutActivity.this)
                     //   .setTitle("接瓶确认")
                        .setCustomTitle(txt)
						.setMessage(sb.toString())
						.setPositiveButton("是", new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								getIntent().putExtra("sydh", vo.SYDH);
								getIntent().putExtra("qrdh", vo.QRDH);
								SYExecutActivity.this.setResult(RESULT_OK,
										getIntent());
								finish();
							}
						}).setNegativeButton(getString(R.string.project_operate_cancel), new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						}).create().show();

			}
		});
	}

	/**
	 * 设置长宽显示参数
	 */
	private void setLayoutParams() {

		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.height = DisplayUtil.getHeightPixels(this) * 4 / 5;
		params.width = DisplayUtil.getWidthPixels(this) * 5 / 6;
		this.getWindow().setAttributes(params);
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
					SYModel vo = adapter.contains(entity.TMQZ + entity.TMNR);
					if (null != vo) {
						getIntent().putExtra("sydh", vo.SYDH);
						getIntent().putExtra("qrdh", vo.QRDH);
						SYExecutActivity.this.setResult(RESULT_OK, getIntent());
						finish();
					} else {

						showMsgAndVoiceAndVibrator("未找到对应条码");
					}
				}
			}
		};
	}
}
