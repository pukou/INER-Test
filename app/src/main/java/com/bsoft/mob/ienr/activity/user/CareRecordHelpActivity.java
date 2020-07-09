package com.bsoft.mob.ienr.activity.user;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.activity.user.adapter.CareRecordHelpLeafAdapter;
import com.bsoft.mob.ienr.activity.user.adapter.CareRecordHelpTreeAdapter;
import com.bsoft.mob.ienr.api.NurseRecordApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.nurserecord.HelpLeaf;
import com.bsoft.mob.ienr.model.nurserecord.HelpTree;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 护理记录助手
 * 
 * @author hy
 * 
 */
public class CareRecordHelpActivity extends BaseBarcodeActivity {

	private static final String ROOT = "root";

	private TextView mtitleTxt;

	private ListView mTreelistView;

	private ListView mLeaflistView;

	/**
	 * key 目录编号
	 */
	private HashMap<String, List<HelpTree>> treeMap = new HashMap<String, List<HelpTree>>();

	/**
	 * key 目录编号
	 */
	private HashMap<String, List<HelpLeaf>> leafMap = new HashMap<String, List<HelpLeaf>>();

	private ArrayList<HelpTree> tree = new ArrayList<HelpTree>();

	private String jgbh = null;
	private String ysbh = null;
	private String kjh = null;

	/**
	 * 父目录编号
	 */
	private String mCurFLBH = null;

	/**
	 * leaf 列表 所属目录编号
	 */
	private String mCurMLBH = null;


	@Override
	protected int setupLayoutResId() {
		return R.layout.activity_care_record_help;
	}

	@Override
	protected void initView(Bundle savedInstanceState) {

		initView();
		initParams(getIntent());
	}

	@Override
	protected int configSwipeRefreshLayoutResId() {
		return R.id.id_swipe_refresh_layout;
	}

	private void initParams(Intent intent) {

		if (intent == null) {
			return;
		}
		jgbh = intent.getStringExtra("jgbh");
		ysbh = intent.getStringExtra("ysbh");
		kjh = intent.getStringExtra("kjh");
		getTreeWithJGBH(jgbh, ysbh, kjh);

	}

	/**
	 * 根据结构编号，获取助手目录树
	 * 
	 */
	private void getTreeWithJGBH(String... params) {

		if (params == null || params.length < 3 || EmptyTool.isBlank(params[0])
				|| EmptyTool.isBlank(params[1]) || EmptyTool.isBlank(params[2])) {
			showMsgAndVoiceAndVibrator("请求错误:未传入请求参数");
			return;
		}
		actionGetTreeTask(params);
	}

	private void initView() {

		actionBar.setTitle("助手");
		actionBar.setPatient(mAppApplication.sickPersonVo.XSCH + mAppApplication.sickPersonVo.BRXM);
		 LinearLayout leftLayout = (LinearLayout) actionBar
				.findViewById(R.id.leftLayout);
		 /*ImageView leftImageView = (ImageView) actionBar
				.findViewById(R.id.leftImageView);*/
		TextView leftTextView = (TextView) actionBar
				.findViewById(R.id.leftTextView);
		mtitleTxt = (TextView) actionBar.findViewById(R.id.titleTextView);

		mTreelistView = (ListView) findViewById(R.id.id_lv);



		mLeaflistView = (ListView) findViewById(R.id.id_lv_2);


		EmptyViewHelper.setEmptyView(mLeaflistView,"mLeaflistView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout,mLeaflistView);
		EmptyViewHelper.setEmptyView(mTreelistView,"mTreelistView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout,mTreelistView);
		initListView();


//		leftImageView.setImageResource(R.drawable.ic_arrow_back_black_24dp);
		leftTextView.setText(getString(R.string.menu_back));
		leftLayout.setVisibility(View.VISIBLE);
		leftLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mLeaflistView.getVisibility() == View.VISIBLE) {
					showLeafListView(false);
					resetNames();
					return;
				}

				if (ROOT.equals(mCurFLBH)) {
					finish();
				}

				if (!EmptyTool.isBlank(mCurFLBH)) {

					actionFindTask(mCurFLBH);
					resetNames();
					return;
				}

			}
		});
	}



	private void actionFindTask(String mCurFLBH) {

		FindTask task = new FindTask();
		tasks.add(task);
		task.execute(mCurFLBH);
	}

	class FindTask extends AsyncTask<String, Integer, String> {

		@Override
		protected void onPreExecute() {
			showSwipeRefreshLayout();
		}

		@Override
		protected String doInBackground(String... params) {

			if (params == null || params.length < 1 || tree == null) {
				return null;
			}
			String flbh = params[0];
			return findFFLMB(flbh, tree);
		}

		@Override
		protected void onPostExecute(String result) {

			hideSwipeRefreshLayout();
			tasks.remove(this);

			if (result == null) {
				importTreeList(ROOT, tree);
			} else {
				List<HelpTree> tree = treeMap.get(result);
				importTreeList(result, tree);
			}
		}
	}

	/**
	 * 递归查找爷 节点 MLBL
	 * 
	 * @param FLMB
	 * @param tree
	 * @return
	 */
	private String findFFLMB(String FLMB, List<HelpTree> tree) {

		if (EmptyTool.isBlank(FLMB) || tree == null || tree.size() == 0) {
			return null;
		}
		for (HelpTree item : tree) {
			if (item.MLBH.equals(FLMB)) {
				return item.FLBH;
			}
			if (item.Items != null && item.Items.size() > 0) {
				String result = findFFLMB(FLMB, item.Items);
				if (result != null) {
					return result;
				}
			}
		}
		return null;

	}

	@Override
	protected void toRefreshData() {
		getTreeWithJGBH(jgbh, ysbh, kjh);
	}

	private void initListView() {


		mTreelistView.setOnItemClickListener(onTreeListItemClickLister);

		mLeaflistView.setOnItemClickListener(onLeafListItemClickLister);
		showLeafListView(false);
	}

	private void setFileNames(String name, String fLBH) {

		if (EmptyTool.isBlank(name)) {
			mtitleTxt.setText("助手");
		} else {
			mtitleTxt.setText(name);
		}
		mtitleTxt.setTag(fLBH);
	}

	private void resetNames() {

		String filesStr = mtitleTxt.getText().toString();
		if (EmptyTool.isBlank(filesStr)) {
			return;
		}
		String[] names = filesStr.split("/");
		if (names.length < 1) {
			return;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < names.length - 1; i++) {

			if (EmptyTool.isBlank(names[i])) {
				continue;
			}
			sb.append(names[i]);
			if (i < (names.length - 2)) {
				sb.append("/");
			}
		}
		setFileNames(sb.toString(), null);
	}

	private void addName(String FLBH, String MLMC) {

		if (EmptyTool.isBlank(MLMC)) {
			return;
		}

		String filesStr = mtitleTxt.getText().toString();
		StringBuilder sb = new StringBuilder();
		if (EmptyTool.isBlank(filesStr)) {
			sb.append(MLMC).toString();
			setFileNames(sb.toString(), FLBH);
			return;
		}

		String[] names = filesStr.split("/");
		if (names.length < 1) {
			return;
		}

		String last = names[names.length - 1];

		Object tag = mtitleTxt.getTag();
		if (tag != null && tag.toString().equals(FLBH) && last.equals(MLMC)) {
			return;
		}
		sb.append(filesStr).append("/").append(MLMC);
		setFileNames(sb.toString(), FLBH);
	}

	private OnItemClickListener onTreeListItemClickLister = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			HelpTree item = (HelpTree) mTreelistView
					.getAdapter().getItem(position);

			if (item.Items == null || item.Items.isEmpty()) {
				// 获取内容
				String mlbh = item.MLBH;
				loadLeafList(mlbh);
				addName(item.FLBH, item.MLMC);
			} else {
				importTreeList(item.MLBH, item.Items);
				addName(item.FLBH, item.MLMC);
			}
		}
	};

	private OnItemClickListener onLeafListItemClickLister = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			HelpLeaf item = (HelpLeaf) mLeaflistView
					.getAdapter().getItem(position);

			if (item != null) {
				showDetailDialog(item.ZSMC, item.ZSNR);
			}

		}
	};

	/**
	 * 获取指定级别列表项
	 * @param params
	 */
	protected void actionGetTreeTask(String... params) {

		GetTreeTask task = new GetTreeTask();
		tasks.add(task);
		task.execute(params);
	}

	protected void loadLeafList(String mlbh) {

		if (leafMap != null && leafMap.containsKey(mlbh)) {
			showLeafListView(true);
			importLeafList(mlbh, leafMap.get(mlbh));
			return;
		}
		actionGetDetailTask(jgbh, ysbh, kjh, mlbh);

		// showDetailDialog(item.MLMC, item.ZSNR);
	}

	@SuppressWarnings("rawtypes")
	class GetTreeTask extends AsyncTask<String, String, Response> {

		@Override
		protected void onPreExecute() {
			showSwipeRefreshLayout();
		}

		@Override
		protected Response doInBackground(String... params) {

			if (params == null || params.length < 3) {
				return null;
			}

			if (mAppApplication.user == null) {
				return null;
			}

			NurseRecordApi api = NurseRecordApi
					.getInstance(getApplicationContext());
			String jgbh = params[0];
			String ysbh = params[1];
			String xmbh = params[2];
			String ygdm = mAppApplication.user.YHID;
			String bqid = mAppApplication.getAreaId();
			String jgid = mAppApplication.jgId;
			int sysType = Constant.sysType;

//			Response<List<HelpTree>> response = api.GetHelperContent(ysbh,
//					xmbh, jgbh, ygdm, bqid, jgid, sysType);
			Response<List<HelpTree>> response = api.GetHelperContent(ysbh,
					xmbh, jgbh, ygdm, bqid, jgid, sysType);
			return response;

		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(Response result) {

			hideSwipeRefreshLayout();
			tasks.remove(this);

			showLeafListView(false);

//			if (result == null) {
//				showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
//				return;
//			}
//
//			if (result.ReType != 0) {
//				showMsgAndVoice(result.Msg);
//				return;
//			}
//
//			if (result.Data == null) {
//				return;
//			}
// 			tree = (ArrayList<HelpTree>) result.Data;
//			importTreeList("root", tree);
			if (result != null){
				if (result.ReType == 100){
					new AgainLoginUtil(CareRecordHelpActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
						@Override
						public void LoginSucessEvent() {
							getTreeWithJGBH(jgbh, ysbh, kjh);
						}
					}).showLoginDialog();
				}else if (result.ReType == 0){
					tree = (ArrayList<HelpTree>) result.Data;

					importTreeList("root", tree);
				}else {
					showMsgAndVoice(result.Msg);
					/*MediaUtil.getInstance(CareRecordHelpActivity.this).playSound(
							R.raw.wrong, CareRecordHelpActivity.this);*/
				}
			}else {
				showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
				return;
			}



		}
	}

	@SuppressWarnings("rawtypes")
	class GetDetailTask extends AsyncTask<String, String, Response> {

		// private String leafMlbh = null;

		@Override
		protected void onPreExecute() {
			showSwipeRefreshLayout();
		}

		@Override
		protected Response doInBackground(String... params) {

			if (params == null || params.length < 4) {
				return null;
			}


			if (mAppApplication.user == null) {
				return null;
			}

			NurseRecordApi api = NurseRecordApi
					.getInstance(getApplicationContext());
			String jgbh = params[0];
			String ysbh = params[1];
			String xmbh = params[2];
			mCurMLBH = params[3];
			String ygdm = mAppApplication.user.YHID;
			String bqid = mAppApplication.getAreaId();
			String jgid = mAppApplication.jgId;
			int sysType = Constant.sysType;

			Response<List<HelpLeaf>> response = api.GetHelper(ysbh, xmbh, jgbh,
					mCurMLBH, ygdm, bqid, jgid, sysType);

			return response;

		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(Response result) {

			hideSwipeRefreshLayout();
			tasks.remove(this);
			showLeafListView(true);

//			if (result == null) {
//				showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
//				return;
//			}
//
//			if (result.ReType != 0) {
//				showMsgAndVoice(result.Msg);
//				return;
//			}
//
//			if (result.Data == null) {
//				return;
//			}
//
//			List<HelpLeaf> list = (List<HelpLeaf>) result.Data;
//			importLeafList(mCurMLBH, list);
			if (result != null){
				if (result.ReType == 100){
					new AgainLoginUtil(CareRecordHelpActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
						@Override
						public void LoginSucessEvent() {
							actionGetDetailTask(jgbh, ysbh, kjh, mCurMLBH);
						}
					}).showLoginDialog();
				}else if (result.ReType == 0){
					List<HelpLeaf> list = (List<HelpLeaf>) result.Data;
					importLeafList(mCurMLBH, list);

				}else {
					showMsgAndVoice(result.Msg);
					/*MediaUtil.getInstance(CareRecordHelpActivity.this).playSound(
							R.raw.wrong, CareRecordHelpActivity.this);*/
				}
			}else {
				showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
				return;
			}

		}
	}

	private void actionGetDetailTask(String... params) {

		GetDetailTask task = new GetDetailTask();
		tasks.add(task);
		task.execute(params);
	}



	public void importTreeList(String MLBH, List<HelpTree> tree) {
		CareRecordHelpTreeAdapter adapter = new CareRecordHelpTreeAdapter(this,
				tree);
		mTreelistView.setAdapter(adapter);
		importTreeMapValues(MLBH, tree);
	}

	public void importLeafList(String MLBH, List<HelpLeaf> tree) {
		CareRecordHelpLeafAdapter adapter = new CareRecordHelpLeafAdapter(this,
				tree);
		mLeaflistView.setAdapter(adapter);
		importLeafMapValues(MLBH, tree);
	}

	private void showLeafListView(boolean show) {

		mLeaflistView.setVisibility(show ? View.VISIBLE : View.GONE);
		mTreelistView.setVisibility(show ? View.GONE : View.VISIBLE);
	}

	private void importTreeMapValues(String mLBH, List<HelpTree> tree) {

		if (treeMap == null || EmptyTool.isBlank(mLBH)) {
			return;
		}
		mCurFLBH = mLBH;

		if (!treeMap.containsKey(mLBH)) {
			treeMap.put(mLBH, tree);
		}

	}

	/**
	 * 导入叶子结点列表
	 * 
	 * @param mLBH
	 * @param tree
	 */
	private void importLeafMapValues(String mLBH, List<HelpLeaf> tree) {

		if (leafMap == null || EmptyTool.isBlank(mLBH)) {
			return;
		}
		if (!leafMap.containsKey(mLBH)) {
			leafMap.put(mLBH, tree);
		}

	}

	protected void showDetailDialog(String title, String content) {
		DetailDialogFragment newFragment = DetailDialogFragment.newInstance(
				title, content);
		newFragment.show(getSupportFragmentManager(), "DetailDialogFragment");
	}

	public static class DetailDialogFragment extends DialogFragment {

		public static DetailDialogFragment newInstance(String title,
				String content) {
			DetailDialogFragment frag = new DetailDialogFragment();
			Bundle args = new Bundle();
			args.putString("title", title);
			args.putString("content", content);
			frag.setArguments(args);
			return frag;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			final String title = getArguments().getString("title");

			final String content = getArguments().getString("content");
            View txt = ViewBuildHelper.buildDialogTitleTextView(getContext(), title);
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(content)
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {

									Activity activity = getActivity();
									Intent intent = new Intent();
									intent.putExtra("help_content", content);
									activity.setResult(RESULT_OK, intent);
									activity.finish();
								}
							})
					.setNegativeButton(android.R.string.cancel,
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {

								}
							})
                   // .setTitle(title)
                    .setCustomTitle(txt);
				//.setIcon(android.R.drawable.ic_dialog_info);
			return builder.create();

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
