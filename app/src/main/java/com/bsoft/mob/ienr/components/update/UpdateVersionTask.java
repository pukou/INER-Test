package com.bsoft.mob.ienr.components.update;

import android.content.Context;
import android.os.AsyncTask;


/**
 * @author Tank   E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-15 上午12:20:25
 * @类说明 更新任务
 */
@Deprecated
public class UpdateVersionTask extends AsyncTask<Void, Object, Boolean> {
	UpdateVersion uv;

	// public UpdateVersionTask(UpdateVersion uv) {
	// this.uv = uv;
	// }

	public UpdateVersionTask(Context context, String dir) {
		this.uv = new UpdateVersion(context, dir);
	}

	@Override
	protected Boolean doInBackground(Void... params) {

		if (null != uv) {
			return uv.isUpdate();
		}
		return false;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if (result) {
			uv.doNewVersionUpdate();
		}
	}

}
