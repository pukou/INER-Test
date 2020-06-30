package com.bsoft.mob.ienr.components.mqtt;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseActivity;
import com.bsoft.mob.ienr.util.DisplayUtil;

public class TopicsActivity extends BaseActivity {

	protected TextView topicsTxt;

	public static final String KEY_FOR_TOPICS = "topics";


	private void initActionBar() {

		actionBar.setTitle("消息服务");


	}

	@Override
	protected int setupLayoutResId() {
		return R.layout.activity_topics;
	}

	@Override
	protected void initView(Bundle savedInstanceState) {
		setLayoutParams();
		final TextView topicsTxt = (TextView) findViewById(R.id.topics_txt);
		Intent intent = getIntent();
		String msg = intent.getStringExtra(KEY_FOR_TOPICS);
		topicsTxt.setText(msg);

		initActionBar();
	}

	/**
	 * 设置长宽显示参数
	 */
	private void setLayoutParams() {

		WindowManager.LayoutParams params = getWindow().getAttributes();
		params.height = LayoutParams.WRAP_CONTENT;
		params.width = DisplayUtil.getWidthPixels(this) - 20;
		this.getWindow().setAttributes(params);
	}

}
