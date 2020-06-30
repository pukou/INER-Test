/**   
 * @Title: HealthGuidCustomActivity.java
 * @Package com.bsoft.mob.ienr.activity.user 
 * @Description: 健康教育自定义项目操作页类文件
 * @author 田孝鸣 tianxm@bsoft.com.cn
 * @date 2015-12-07 上午9:30:04
 * @version V1.0   
 */
package com.bsoft.mob.ienr.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;

/**
 * @ClassName: HealthGuidCustomActivity
 * @Description: 健康教育自定义项目操作页Activity
 * @author 田孝鸣 tianxm@bsoft.com.cn
 * @date 2015-12-07 上午9:30:04
 * 
 */
public class HealthGuidCustomActivity extends BaseBarcodeActivity {

	private TextView tv_Type;
	private EditText et_Item;
	private Button bt_yes;
	private Button bt_no;


	@Override
	public void initBarBroadcast() {


	}
	@Override
	protected int configSwipeRefreshLayoutResId() {
		return R.id.id_swipe_refresh_layout;
	}

	/**
	 * @Title: initView
	 * 
	 * @Description: 初始化界面
	 * 
	 * @param @param view
	 * 
	 * @return void
	 * 
	 * @throws
	 */
	private void initView() {

		tv_Type = (TextView) findViewById(R.id.healthguid_custom_type);
		tv_Type.setText(getIntent().getStringExtra("type"));

		et_Item = (EditText) findViewById(R.id.healthguid_custom_item);

		bt_yes = (Button) findViewById(R.id.id_btn_date_ok);
		bt_yes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("item", et_Item.getText().toString());
				HealthGuidCustomActivity.this.setResult(RESULT_OK, intent);
				finish();
			}
		});

		bt_no = (Button) findViewById(R.id.id_btn_date_cancel);
		bt_no.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				HealthGuidCustomActivity.this.setResult(RESULT_CANCELED);
				finish();
			}
		});
	}


	/**
	 * 
	 * @Title: initActionBar
	 * @Description: 初始化工具条
	 * @param
	 * @return void
	 * @throws
	 */
	private void initActionBar() {
		actionBar.setTitle("自定义项目");
//		actionBar.setPatient(mAppApplication.sickPersonVo.BRCH + mAppApplication.sickPersonVo.BRXM);
//		actionBar.setBackAction(new Action() {
//
//			@Override
//			public void performAction(View view) {
//				finish();
//			}
//
//			@Override
//			public int getDrawable() {
//				return R.drawable.ic_arrow_back_black_24dp;
//			}
//		});
	}

	@Override
	protected int setupLayoutResId() {
		return R.layout.activity_health_guid_custom;
	}

	@Override
	protected void initView(Bundle savedInstanceState) {

		initView();
		initActionBar();
	}


}
