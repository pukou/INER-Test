package com.bsoft.mob.ienr.activity.user;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.SignActivity;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.api.NurseRecordApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.nurserecord.NrTree;
import com.bsoft.mob.ienr.model.nurserecord.SignatureDataRequest;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.classichu.vectortextview.ClassicVectorTextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 护理结构树 Created by hy on 14-3-24.
 */
public class CareRecordHistoryActivity extends BaseBarcodeActivity {

	private static final int RQ_GET_USERID = 0;




	private ExpandableListView listView;

	private ArrayList<NrTree> list;

	private NrTree item;
	private CareRecordNrTreeAdapter adapter;
	private String yhid1;
	private String extra_value;


	private void initView() {


		listView = (ExpandableListView) findViewById(R.id.id_elv);


		EmptyViewHelper.setEmptyView(listView,"listView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout,listView);
		initListView();

		initActionBar();
	}

	private void initActionBar() {

		actionBar.setTitle("查看护理记录");
		actionBar.setPatient(mAppApplication.sickPersonVo.XSCH + mAppApplication.sickPersonVo.BRXM);

	}

	@Override
	protected int setupLayoutResId() {
		return R.layout.activity_care_record_history;
	}

	@Override
	protected void initView(Bundle savedInstanceState) {

		initView();
		toRefreshData();
	}

	@Override
	protected int configSwipeRefreshLayoutResId() {
		return R.id.id_swipe_refresh_layout;
	}

	@Override
	protected void toRefreshData() {

		GetNrTreeTask task = new GetNrTreeTask();
		tasks.add(task);
		task.execute();
	}

	private void initListView() {


		listView.setOnChildClickListener(
				onChildClickListener);
	}

	private OnChildClickListener onChildClickListener = new OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {

			View view = v.findViewById(R.id.care_record_nrtree_dlt_ibtn);
			NrTree item = (NrTree) view.getTag();
			if (item != null) {

				Intent data = new Intent();
				data.putExtra("jlbh", item.JLBH);
				data.putExtra("jgbh", item.JGBH);
				data.putExtra("jlsj", item.JLSJ);
				data.putExtra("wczt", item.WCZT);
				setResult(RESULT_OK, data);
				finish();
			}
			return false;
		}

	};



	@SuppressWarnings("rawtypes")
	class GetNrTreeTask extends AsyncTask<String, String, Response> {

		@Override
		protected void onPreExecute() {
			showSwipeRefreshLayout();
		}

		@Override
		protected Response doInBackground(String... params) {

			if (mAppApplication.sickPersonVo == null) {
				return null;
			}
			String zyh = mAppApplication.sickPersonVo.ZYH;
			String jgid = mAppApplication.jgId;

			NurseRecordApi api = NurseRecordApi
					.getInstance(getApplicationContext());

			Response<List<NrTree>> response = api.GetNrTree(zyh, jgid,
					Constant.sysType);
			return response;

		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(Response result) {

			hideSwipeRefreshLayout();
			tasks.remove(this);

//			if (result == null) {
//				showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
//				return;
//			}
//			if (result.ReType == 1) {
//				showMsgAndVoice(result.Msg);
//				return;
//			}
//
//			if (result.Data == null) {
//				return;
//			}
//
//			list = (ArrayList<NrTree>) result.Data;
//			importList(list);
			if (result != null){
				if (result.ReType == 100){
					new AgainLoginUtil(CareRecordHistoryActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
						@Override
						public void LoginSucessEvent() {
							toRefreshData();
						}
					}).showLoginDialog();
				}else if (result.ReType == 0){
					list = (ArrayList<NrTree>) result.Data;
					importList(list);

				}else {
					showMsgAndVoice(result.Msg);
					/*MediaUtil.getInstance(CareRecordHistoryActivity.this).playSound(
							R.raw.wrong, CareRecordHistoryActivity.this);*/
				}
			}else {
				showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
				return;
			}

		}
	}

	@SuppressWarnings("rawtypes")
	class DeleteTask extends AsyncTask<String, String, Response<String>> {

		@Override
		protected void onPreExecute() {
			showLoadingDialog(getString(R.string.deleteing));
		}

		@Override
		protected Response<String> doInBackground(String... params) {

			if (params == null || params.length < 1) {
				return null;
			}


			if (mAppApplication.sickPersonVo == null) {
				return null;
			}
			if (mAppApplication.user == null) {
				return null;
			}
			String zyh = mAppApplication.sickPersonVo.ZYH;
			String jgid = mAppApplication.jgId;
			String yhid = mAppApplication.user.YHID;
			String yhxm = mAppApplication.user.YHXM;
			String jlbh = params[0];

			NurseRecordApi api = NurseRecordApi
					.getInstance(getApplicationContext());

//			Response<Boolean> response = api.Delete(zyh, jlbh, yhid, yhxm,
//					jgid, Constant.sysType);
			Response<String> response = api.Delete(zyh, jlbh, yhid, yhxm,
					jgid, Constant.sysType);
			if (response.Data != null ) {
				publishProgress(jlbh);
			}
			return response;

		}

		@Override
		protected void onProgressUpdate(String... values) {

			if (list == null) {
				return;
			}

			if (values == null || values.length < 1) {
				return;
			}

			String jlbh = values[0];
			for (NrTree item : list) {

				for (NrTree zml : item.ZML) {
					if (zml.JLBH.equals(jlbh)) {
						item.ZML.remove(zml);
						importList(list);
						return;
					}
				}
			}

		}

		@Override
		protected void onPostExecute(Response<String> result) {

			hideLoadingDialog();
			tasks.remove(this);

//			if (result == null) {
//				showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
//				return;
//			}
//			if (result.ReType == 1) {
//				showMsgAndVoice(result.Msg);
//				return;
//			}
//
//			if (result.Data == null) {
//				return;
//			}
//
//			Boolean success = (Boolean) result.Data;
//			showMsgAndVoice(success ? "删除成功" : "删除失败");
			if (null != result) {
				if (result.ReType == 100) {
					new AgainLoginUtil(CareRecordHistoryActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
						@Override
						public void LoginSucessEvent() {
							actionDltTask(item.JLBH);
						}
					}).showLoginDialog();
					return;
				} else if (result.ReType == 0) {
					showMsgAndVoice("删除成功");
					setResult(Activity.RESULT_OK);
//					finish();
					toRefreshData();
				} else {
					showMsgAndVoice(result.Msg);
					/*MediaUtil.getInstance(CareRecordHistoryActivity.this).playSound(
							R.raw.wrong, CareRecordHistoryActivity.this);*/
				}
			} else {
				showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
				return;
			}

		}
	}



	public void importList(List<NrTree> list) {

//		CareRecordNrTreeAdapter adapter = new CareRecordNrTreeAdapter(
//				getApplicationContext(), list);
		adapter = new CareRecordNrTreeAdapter(
				getApplicationContext(), list);
		listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
			@Override
			public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
				TextView ListHeader = (TextView) v.findViewById(R.id.id_tv);
				ListHeader.setSelected(!ListHeader.isSelected());
				return false;
			}
		});
		listView.setAdapter(adapter);
	}

	/**
	 * 护理记录历史
	 * 
	 * @author hy
	 * 
	 */
	public class CareRecordNrTreeAdapter extends BaseExpandableListAdapter
			implements OnClickListener {

		Context mContext;

		List<NrTree> list;

		/**
		 * key 为group position , value 为child
		 */
		// SparseArrayCompat<ArrayList<InspectResult>> childs;

		public CareRecordNrTreeAdapter(Context mContext, List<NrTree> list) {

			this.mContext = mContext;
			this.list = list;
		}

		@Override
		public int getGroupCount() {
			return list != null ? list.size() : 0;
		}

		@Override
		public int getChildrenCount(int groupPosition) {

			return list.get(groupPosition) != null ? (list.get(groupPosition).ZML != null ? list
					.get(groupPosition).ZML.size() : 0)
					: 0;
		}

		@Override
		public NrTree getGroup(int groupPosition) {
			return list.get(groupPosition);
		}

		@Override
		public NrTree getChild(int groupPosition, int childPosition) {

			List<NrTree> zml = list.get(groupPosition).ZML;
			return zml.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {

			if (convertView == null) {
				LayoutInflater layoutInflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = layoutInflater.inflate(
						R.layout.item_list_group_primary, parent,false);
			}

			TextView groupTextView = (TextView) convertView
					.findViewById(R.id.id_tv);
			NrTree nrTree = getGroup(groupPosition);

			groupTextView.setText(nrTree.XSNR);

			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			ViewHolder vHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.item_list_care_record_nrtree, parent,false);
				vHolder = new ViewHolder();
				vHolder.tv_time = (TextView) convertView
						.findViewById(R.id.care_record_nrtree_time_txt);

				vHolder.img_dlt = (ImageView) convertView
						.findViewById(R.id.care_record_nrtree_dlt_ibtn);

				vHolder.img_sign = (ClassicVectorTextView) convertView
						.findViewById(R.id.care_record_nrtree_sign_ibtn);

				convertView.setTag(vHolder);
				vHolder.img_dlt.setOnClickListener(this);
				vHolder.img_sign.setOnClickListener(this);
			} else {
				vHolder = (ViewHolder) convertView.getTag();
			}

			NrTree tree = getChild(groupPosition, childPosition);
			String time = tree.XSNR;
			// 简单判断
			if (!EmptyTool.isBlank(time) && !time.contains("-")) {
				StringBuilder sb = new StringBuilder("\b\b\b\b\b\b\b\b\b\b")
						.append(time);
				time = sb.toString();
			}
			vHolder.tv_time.setText(time);
			vHolder.img_dlt.setTag(tree);
			vHolder.img_sign.setTag(tree);

			if (tree.WCZT) {
				vHolder.img_sign.setVisibility(View.GONE);
			} else {
				vHolder.img_sign.setVisibility(View.VISIBLE);
			}
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

		class ViewHolder {
			public TextView tv_time;
			public ImageView img_dlt;
			public ClassicVectorTextView img_sign;
		}

		@Override
		public void onClick(View v) {
			 item = (NrTree) v.getTag();
			int id = v.getId();
			if (id == R.id.care_record_nrtree_dlt_ibtn) {

//				NrTree item = (NrTree) v.getTag();
				// 删除
				if (item != null) {
					if (!mAppApplication.user.YHID.equals(item.SXHS)){
						showMsgAndVoiceAndVibrator("不允许删除别人录入的数据");
						return;
					}
					actionDltTask(item.JLBH);
				}
			} else if (id == R.id.care_record_nrtree_sign_ibtn) {

//				NrTree item = (NrTree) v.getTag();
				// 签名
				if (item != null) {
					startSignActivity(item.JLBH);
				}
			}

		}
	}

	private void startSignActivity(String jLBH) {
		Intent intent = new Intent(this, SignActivity.class);
		intent.putExtra(SignActivity.EXTRA_STRING_KEY, jLBH);
		startActivityForResult(intent, RQ_GET_USERID);
	}

	private void actionDltTask(String... params) {
		DeleteTask task = new DeleteTask();
		tasks.add(task);
		task.execute(params);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode != Activity.RESULT_OK || data == null) {
			return;
		}

		if (requestCode == RQ_GET_USERID) {
			yhid1 = data.getStringExtra(SignActivity.EXTRA_YHID_KEY_1);
			extra_value = data
					.getStringExtra(SignActivity.EXTRA_STRING_KEY);
			actionSign(yhid1, extra_value);
			return;
		}
	}

	private void actionSign(String... params) {

		SignTask task = new SignTask();
		tasks.add(task);
		task.execute(params);
	}

	@SuppressWarnings("rawtypes")
	class SignTask extends AsyncTask<String, String, Response> {

		private String jlbh = null;

		@Override
		protected void onPreExecute() {
			showLoadingDialog(R.string.signing);
		}

		@Override
		protected Response doInBackground(String... params) {

			if (params == null || params.length < 2) {
				return null;
			}


			NurseRecordApi api = NurseRecordApi
					.getInstance(getApplicationContext());

			String yhid = params[0];
			jlbh = params[1];
			String jgid = mAppApplication.jgId;
//			JSONObject obj = new JSONObject();
//
//			try {
//				obj.put("JLBH", jlbh);
//				obj.put("YHID", yhid);
//				obj.put("JGID", jgid);
//			} catch (JSONException e) {
//				e.printStackTrace();
//				return null;
//			}
			SignatureDataRequest obj = new SignatureDataRequest();
			obj.JLBH = jlbh;
			obj.YHID = yhid;
			obj.JGID = jgid;
			String data = null;
			try {
				data = JsonUtil.toJson(obj);
			} catch (IOException e) {
				e.printStackTrace();

				showMsgAndVoiceAndVibrator("请求失败：解析错误");
			}
//			Response<String> response = api.SignName(obj.toString(),
//					Constant.sysType);

			Response<String> response = api.SignName(data);
			return response;

		}

		@Override
		protected void onPostExecute(Response result) {

			hideLoadingDialog();
			tasks.remove(this);

//			if (result != null && result.ReType == 0) {
//				showMsgAndVoice("签名成功");
//				// 成功后，刷新
//				actionGetNrTreeTask();
//				return;
//			}
//
//			if (result == null) {
//				showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
//				return;
//			}
//			if (result.ReType == 1) {
//				showMsgAndVoice(result.Msg);
//				startSignActivity(jlbh);
//				return;
//			}
			if (null != result) {
				if (result.ReType == 100) {
					new AgainLoginUtil(CareRecordHistoryActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
						@Override
						public void LoginSucessEvent() {
							actionSign(yhid1, extra_value);
						}
					}).showLoginDialog();
					return;
				} else if (result.ReType == 0) {
					showMsgAndVoice("签名成功");
					// 成功后，刷新
					toRefreshData();
				} else {
					showMsgAndVoice(result.Msg);
					/*MediaUtil.getInstance(CareRecordHistoryActivity.this).playSound(
							R.raw.wrong, CareRecordHistoryActivity.this);*/
				}
			} else {
				showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
				return;
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

}
