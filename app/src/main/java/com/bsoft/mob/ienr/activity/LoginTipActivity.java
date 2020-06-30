package com.bsoft.mob.ienr.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseActivity;
import com.bsoft.mob.ienr.service.LogoutService;

/**
 * 扫描切换登录提示Activity
 * 
 * @author admin
 *
 */
public class LoginTipActivity extends BaseActivity {

	String barcode;


	@Override
	protected int setupLayoutResId() {
		return R.layout.activity_login_tip;
	}

	@Override
	protected void initView(Bundle savedInstanceState) {

		actionBar.setTitle("胸卡扫描");
		barcode = getIntent().getStringExtra("barcode");
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		barcode = intent.getStringExtra("barcode");
	}

	public void onClick(View view) {

		int id = view.getId();
		if (id == R.id.id_btn_date_ok) {
			Intent service = new Intent(LoginTipActivity.this,
					LogoutService.class);
			LoginTipActivity.this.startService(service);

			// 启动登录Activity
			Intent intent = new Intent(LoginTipActivity.this,
					LoginActivity.class);
			intent.putExtra("barcode", barcode);
			startActivity(intent);
			finish();
		} else if (id == R.id.id_btn_date_cancel) {
			finish();
		}
	}

	/**
	 * 保证点击框外面 ，界面不消失
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		Rect dialogBounds = new Rect();
		getWindow().getDecorView().getHitRect(dialogBounds);

		if (!dialogBounds.contains((int) ev.getX(), (int) ev.getY())) {
			return true;
		}
		return super.dispatchTouchEvent(ev);
	}

}
