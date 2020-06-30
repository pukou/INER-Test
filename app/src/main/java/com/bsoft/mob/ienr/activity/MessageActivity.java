package com.bsoft.mob.ienr.activity;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseActivity;
import com.bsoft.mob.ienr.db.Database;
import com.bsoft.mob.ienr.util.MessageUtils;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

/**
 * 消息提醒页 Created by hy on 14-3-24.
 */
public class MessageActivity extends BaseActivity {
	//String LXMC
	public static final String KEY_FOR_LXMC = "lxmc";
	// String
	public static final String KEY_FOR_CONTENT = "content";
	// Long
	public static final String KEY_FOR_ID = "id";
	// int
	public static final String KEY_FOR_NOTIF_ID = "notify_id";
	// String
	public static final String KEY_FOR_TITLE = "title";

	// protected TextView mContentTxt;



	@Override
	protected int setupLayoutResId() {
		return R.layout.activity_message;
	}

	@Override
	protected void initView(Bundle savedInstanceState) {

		int topic = getIntent().getIntExtra(KEY_FOR_TITLE, -1);
		String content = getIntent().getStringExtra(KEY_FOR_CONTENT);
		int notifyId = getIntent().getIntExtra(KEY_FOR_NOTIF_ID, -1);
		long msgId = getIntent().getLongExtra(KEY_FOR_ID, -1);

//类型名称
		String lxmc = getIntent().getStringExtra(KEY_FOR_LXMC);

		initContent(content);
		cancelMessage(notifyId);
		new DBTask().execute(msgId);

		initTitle(topic, lxmc);
	}


	private void initTitle(int topic, String lxmc) {
		String title = "";

		if(!TextUtils.isEmpty(lxmc)){
			title = lxmc;
		}else{
			title = MessageUtils.getTopicChars(topic).toString();
		}
		actionBar.setTitle(title);
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

	private void cancelMessage(int msgId) {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(msgId);

	}

	class DBTask extends AsyncTask<Long, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(Long... params) {

			if (params == null || params.length < 1) {
				return null;
			}
			Uri uri = Database.Message.CONTENT_URI;
			ContentValues values = new ContentValues();
			values.put(Database.Message.STATE, 1);
			String where = Database.Message.REMOTE_ID + "=?";
			String[] selectionArgs = { String.valueOf(params[0]) };
			getContentResolver().update(uri, values, where, selectionArgs);
			return null;
		}

	}

	private void initContent(String content) {
		final TextView mContentTxt = (TextView) findViewById(R.id.msg_content);
		if (EmptyTool.isBlank(content)){
			content="暂无消息";
		}
		mContentTxt.setText(content);
	}

}
