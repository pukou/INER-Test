/**   
 * @Title: OffLineFragment.java 
 * @Package com.bsoft.mob.ienr.fragment 
 * @Description: 离线保存页 
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2015-12-24 下午1:00:29 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AlertDialog.Builder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.api.BaseApi;
import com.bsoft.mob.ienr.db.Database;
import com.bsoft.mob.ienr.fragment.base.LeftMenuItemFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.http.AppHttpClient;
import com.bsoft.mob.ienr.model.ParserModel;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.reflect.ReflectVo;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.XmlParser;
import com.bsoft.mob.ienr.util.tools.HttpBackMsg;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.util.Locale;

/**
 * @ClassName: OffLineFragment
 * @Description: 离线保存
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2015-12-24 下午1:00:29
 * 
 */
public class OffLineFragment extends LeftMenuItemFragment implements
		LoaderManager.LoaderCallbacks<Cursor> {

	private ListView refreshView;
	private DateChangeObserver observer;
	private DataAdapter mAdapter;
	private String curID = "0";// 当前选中的离线数据的id
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		observer = new DateChangeObserver(null);
	}
	@Override
	protected int configSwipeRefreshLayoutResId() {
		return R.id.id_swipe_refresh_layout;
	}
	@Override
	protected int setupLayoutResId() {
		return R.layout.fragment_offline;
	}

	@Override
	protected void initView(View root, Bundle savedInstanceState) {
		refreshView = (ListView) root.findViewById(R.id.id_lv);
		EmptyViewHelper.setEmptyView(refreshView,"refreshView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout,refreshView);
		initActionBar();
		toRefreshData();
	}


	@Override
	public void onResume() {
		super.onResume();
		getActivity().getContentResolver().registerContentObserver(
				Database.OffLine.CONTENT_URI, true, observer);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (observer != null) {
			getActivity().getContentResolver().unregisterContentObserver(
					observer);
		}

	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		Uri uri = Database.OffLine.CONTENT_URI;
		String[] projection = { Database.OffLine._ID, Database.OffLine.URL,
				Database.OffLine.TYPE, Database.OffLine.PARAM,
				Database.OffLine.PATIENT, Database.OffLine.RECODENAME };
		String sortOrder = Database.OffLine.CREATETIME + " desc";
		CursorLoader cursorLoader = new CursorLoader(getActivity(), uri,
				projection, null, null, sortOrder);
		return cursorLoader;
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
		if (arg1 != null) {

			hideSwipeRefreshLayout();
			mAdapter = new DataAdapter(getActivity(), arg1, true);
			refreshView.setAdapter(mAdapter);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		if (mAdapter != null) {
			mAdapter.changeCursor(null);
		}
	}

	/**
	 * 初始化action bar
	 */
	private void initActionBar() {

		actionBar.setTitle("离线保存");

	}

	@Override
	protected void toRefreshData() {
		actionLoader();
	}

	private void actionLoader() {
		showSwipeRefreshLayout();
		getLoaderManager().restartLoader(0, null, OffLineFragment.this);
	}

	class DateChangeObserver extends ContentObserver {

		public DateChangeObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			if (mAdapter != null) {
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	class DataAdapter extends CursorAdapter {

		public DataAdapter(Context context, Cursor c, boolean autoRequery) {
			super(context, c, autoRequery);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			ViewHolder vHolder;
			Object tag = view.getTag();
			if (tag == null) {
				vHolder = new ViewHolder();
				vHolder.ib_del = (ImageView) view.findViewById(R.id.delete);
				vHolder.ib_save = (ImageView) view.findViewById(R.id.submit);
				vHolder.tv_form = (TextView) view.findViewById(R.id.form);
				vHolder.tv_patient = (TextView) view.findViewById(R.id.patient);
				view.setTag(vHolder);
			} else {
				vHolder = (ViewHolder) tag;
			}
			final DataItem item = new DataItem();
			item.ID = String.valueOf(cursor.getInt(cursor
					.getColumnIndex(Database.OffLine._ID)));
			item.TYPE = String.valueOf(cursor.getInt(cursor
					.getColumnIndex(Database.OffLine.TYPE)));
			item.URL = cursor.getString(cursor
					.getColumnIndex(Database.OffLine.URL));
			item.PARAM = cursor.getString(cursor
					.getColumnIndex(Database.OffLine.PARAM));
			item.PATIENT = cursor.getString(cursor
					.getColumnIndex(Database.OffLine.PATIENT));
			item.RECODENAME = cursor.getString(cursor
					.getColumnIndex(Database.OffLine.RECODENAME));
			vHolder.tv_form.setText(item.RECODENAME);
			vHolder.tv_patient.setText(item.PATIENT);
			vHolder.ib_del.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					curID = item.ID;
					dialog("确定要删除这条同步数据么", 1, null);
				}
			});

			vHolder.ib_save.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					curID = String.valueOf(item.ID);
					dialog("确定要将这条数据提交到服务器吗？", 2, item);
				}
			});
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup parent) {
			return LayoutInflater.from(getActivity()).inflate(
					R.layout.item_list_offline,  parent,false);
		}

	}

	class ViewHolder {
		ImageView ib_del;
		ImageView ib_save;
		TextView tv_patient;
		TextView tv_form;
	}
	private boolean isRequestSuccess(int code) {
		return code >= 200 && code < 300;
	}
	@SuppressWarnings("rawtypes")
	class SubmitTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showLoadingDialog(R.string.doing);
		}

		@Override
		protected String doInBackground(String... params) {
			if (params.length < 1)
            {return null;}
			String type = params[0];
			if (type.equals("1")) {
				if (params.length < 2)
                {	return null;}
				String url = params[1];
				HttpBackMsg<Integer, String, String> httpString = new BaseApi(new AppHttpClient(mAppApplication), getActivity())
						.getHttpString(url);
				if (!isRequestSuccess(httpString.first)) {
					return String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
				}
				return httpString.second;
			} else if (type.equals("2")) {
				if (params.length < 3)
                {return null;}
				HttpBackMsg<Integer, String, String> httpString = new BaseApi(new AppHttpClient(mAppApplication), getActivity())
						.postHttpJson(params[1], params[2]);
				if (!isRequestSuccess(httpString.first)) {
					return String.format(Locale.CHINA, "%s%s", httpString.second, httpString.third);
				}
				return httpString.second;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			hideLoadingDialog();
			if (result != null) {
				if (result.contains("ReType")) {// json格式
					Response response = null;
					// 外面包含了双引号
					if (result.length() > 2) {
						try {
							// 去除转义符
//							result = StringEscapeUtils.unescapeJson(result);
							// 去除双引号
//							result = result.substring(1, result.length() - 1);
							response = JsonUtil.fromJson(result,
									new TypeReference<Response>() {
									});
						} catch (Exception e) {
							Log.e(Constant.TAG, e.getMessage(), e);
						}
					}
					if (response != null && response.ReType == 0) {
                        showMsgAndVoice(R.string.project_save_success);
						deleteData();
					} else {
						showMsgAndVoiceAndVibrator(R.string.project_save_failed);

						/*MediaUtil.getInstance(getActivity()).playSound(
								R.raw.wrong, getActivity());*/
					}

				} else if (result.contains("DataSet")) {// xml格式
					XmlParser parser = new XmlParser();
					ParserModel model = parser.parserTable(result, (ReflectVo) null);
					if (model != null && model.isOK()) {
                        showMsgAndVoice(R.string.project_save_success);

						deleteData();
					} else {
						showMsgAndVoiceAndVibrator(R.string.project_save_failed);
					/*	MediaUtil.getInstance(getActivity()).playSound(
								R.raw.wrong, getActivity());*/
					}
				}
			} else {
				showMsgAndVoiceAndVibrator(R.string.project_save_failed);
			}
		}
	}

	private void deleteData() {
		Uri url = Database.OffLine.CONTENT_URI;
		String where = Database.OffLine._ID + "=?";
		String[] selectionArgs = { curID };
		int num = getActivity().getContentResolver().delete(url, where,
				selectionArgs);
		if (num > 0 && mAdapter != null)
		{mAdapter.notifyDataSetChanged();}
		toRefreshData();
	}

	private void save(DataItem item) {
		SubmitTask task = new SubmitTask();
		tasks.add(task);
		String[] params = { item.TYPE, item.URL, item.PARAM };
		task.execute(params);
	}

	class DataItem {

		public String ID;

		public String URL;

		public String TYPE;

		public String PARAM;

		public String PATIENT;

		public String RECODENAME;

		public String FLAG;

		public String CREATETIME;

		public String CREATE_NURSE;

		public String SYNCHRO_TIME;

		public String SYNCHRO_NURSE;
	}

	/**
	 * @Description: 对话框
	 * @param @param msg 提示信息
	 * @param @param action 做什么操作：1：删除；2：保存
	 * @return void
	 * @throws
	 */
	protected void dialog(String msg, final int action, final DataItem item) {
		AlertDialog.Builder builder = new Builder(getActivity());
		builder.setMessage(msg);

		builder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext,getString(R.string.project_tips)));

		builder.setPositiveButton(R.string.project_operate_ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (action == 1) {
					deleteData();
				} else if (action == 2) {
					if (item != null)
						save(item);
				}
			}
		});

		builder.setNegativeButton(getString(R.string.project_operate_cancel), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.create().show();
	}

}
