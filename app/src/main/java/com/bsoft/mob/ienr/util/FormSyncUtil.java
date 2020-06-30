/**   
 * @Title: FormSyncUtil.java 
 * @Package com.bsoft.mob.ienr.util
 * @Description: 表单同步工具类
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2016-1-15 下午5:06:35 
 * @version V1.0   
 */
package com.bsoft.mob.ienr.util;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.api.NurseFormApi;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.SelectResult;
import com.bsoft.mob.ienr.model.Sheet;
import com.bsoft.mob.ienr.model.SyncRecord;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName: FormSyncUtil
 * @Description: 表单同步工具类
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2016-1-15 下午5:06:35
 * 
 */
public class FormSyncUtil {
	private onCancelClickListener mCancelListener;
	private onConfirmClickListener mConfirmListenr;

	public void setOnDialogClickListener(onCancelClickListener cancelListener,
			onConfirmClickListener confirmClickListener) {
		mCancelListener = cancelListener;
		mConfirmListenr = confirmClickListener;
	}

	public void InvokeAsync(final Context context, final List<SyncRecord> list,
			final String jgid, final LinkedList<AsyncTask<?, ?, ?>> tasks) {

		class ViewHolder {
			CheckBox cb;
		}

		/**
		 * @ClassName: AsyncAdapter
		 * @Description: 选择同步表单的dialog的适配器
		 * @author 吕自聪 lvzc@bsoft.com.cn
		 * @date 2016-1-15 下午3:11:44
		 * 
		 */
		class AsyncAdapter extends BaseAdapter {
			private List<SyncRecord> mList;

			public AsyncAdapter(List<SyncRecord> list) {
				this.mList = list;
			}

			@Override
			public int getCount() {
				return mList.size();
			}

			public SyncRecord getItem(int position) {
				return mList.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(final int position, View convertView,
					ViewGroup parent) {
				ViewHolder vHolder;
				if (convertView == null) {
					convertView = LayoutInflater.from(context).inflate(
							R.layout.item_list_bar_check_text,  parent,false);
					vHolder = new ViewHolder();
					vHolder.cb = (CheckBox) convertView
							.findViewById(R.id.checkBox);
					convertView.setTag(vHolder);
				} else {
					vHolder = (ViewHolder) convertView.getTag();
				}
				vHolder.cb.setText(mList.get(position).MC);
				vHolder.cb
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								mList.get(position).IsSelected = isChecked;
							}
						});
				return convertView;
			}
		}

		class SyncTask extends AsyncTask<String, Void, Response<String>> {

			@Override
			protected Response<String> doInBackground(String... params) {
				if (params.length < 1)
					return null;
				return NurseFormApi.getInstance(context).sync(params[0], jgid);
			}

			@Override
			protected void onPostExecute(Response<String> result) {
				if (mConfirmListenr != null)
					mConfirmListenr.onConfirm();
			}
		}

		if (list != null && list.size() > 0) {
			ListView listView = new ListView(context);
			listView.setAdapter(new AsyncAdapter(list));
			new AlertDialog.Builder(context)
					//.setTitle("请选择要同步到哪些表单中去：")
                    .setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(context,"请选择要同步到哪些表单中去："))
					.setView(listView)
					.setPositiveButton(context.getString(R.string.project_operate_ok),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String data = "";
									try {
										data = JsonUtil.toJson(list);
										data = URLEncoder.encode(data, "UTF-8");
									} catch (IOException e) {
										e.printStackTrace();
									}
									if (!EmptyTool.isBlank(data)) {
										SyncTask task = new SyncTask();
										tasks.add(task);
										task.execute(data);
									}
								}
							})
					.setNegativeButton(context.getString(R.string.project_operate_cancel),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (mCancelListener != null)
										mCancelListener.onCancel();
								}
							}).show();
		}
	}

	public void InvokeSync(final Context context, final SelectResult selectResult,
			final String jgid, final LinkedList<AsyncTask<?, ?, ?>> tasks) {

		// 选中的表单
		final List<Sheet> sheets = new ArrayList<>();

		class ViewHolder {
			CheckBox cb;
		}

		/**
		 * @ClassName: AsyncAdapter
		 * @Description: 选择同步表单的dialog的适配器
		 * @author 吕自聪 lvzc@bsoft.com.cn
		 * @date 2016-1-15 下午3:11:44
		 *
		 */
		class AsyncAdapter extends BaseAdapter {
			private List<Sheet> mList = selectResult.sheets;

			public AsyncAdapter(List<Sheet> list) {
				this.mList = list;
			}

			@Override
			public int getCount() {
				return mList.size();
			}

			public Sheet getItem(int position) {
				return mList.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(final int position, View convertView,
					ViewGroup parent) {
				ViewHolder vHolder;
				if (convertView == null) {
					convertView = LayoutInflater.from(context).inflate(
							R.layout.item_list_bar_check_text,  parent,false);
					vHolder = new ViewHolder();
					vHolder.cb = (CheckBox) convertView
							.findViewById(R.id.checkBox);
					convertView.setTag(vHolder);
				} else {
					vHolder = (ViewHolder) convertView.getTag();
				}
				vHolder.cb.setText(mList.get(position).bdmc);
				vHolder.cb
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {
							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								sheets.add(mList.get(position));
							}
						});
				return convertView;
			}
		}

		class SyncTask extends AsyncTask<String, Void, Response<String>> {

			@Override
			protected Response<String> doInBackground(String... params) {
				if (params.length < 1)
					return null;
				return NurseFormApi.getInstance(context).synchronRepeat(params[0]);
			}

			@Override
			protected void onPostExecute(Response<String> result) {
				if (mConfirmListenr != null)
					mConfirmListenr.onConfirm();
			}
		}

		if (selectResult.sheets != null && selectResult.sheets.size() > 0) {
			ListView listView = new ListView(context);
			listView.setAdapter(new AsyncAdapter(selectResult.sheets));
			new AlertDialog.Builder(context)
					//.setTitle("请选择要同步到哪些表单中去：")
                    .setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(context,"请选择要同步到哪些表单中去："))
					.setView(listView)
					.setPositiveButton(context.getString(R.string.project_operate_ok),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									String data = "";
									SelectResult result = new SelectResult();
									result.sheets = sheets;
									result.UUID = selectResult.UUID;
									try {
										data = JsonUtil.toJson(result);
									} catch (IOException e) {
										e.printStackTrace();
									}
									if (!EmptyTool.isBlank(data)) {
										SyncTask task = new SyncTask();
										tasks.add(task);
										task.execute(data);
									}
								}
							})
					.setNegativeButton(context.getString(R.string.project_operate_cancel),
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									if (mCancelListener != null)
										mCancelListener.onCancel();
								}
							}).show();
		}
	}

	public interface onCancelClickListener {
		void onCancel();
	}

	public interface onConfirmClickListener {
		void onConfirm();
	}
}
