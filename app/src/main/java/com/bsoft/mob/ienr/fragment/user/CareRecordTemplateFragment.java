/**
 * @Title: CareRecordTemplate.java
 * @Package com.bsoft.mob.ienr.fragment.user
 * @author 吕自聪  lvzc@bsoft.com.cn
 * @date 2016-4-7 上午10:47:10
 * @version V1.0
 */
package com.bsoft.mob.ienr.fragment.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.CareRecordActivity;
import com.bsoft.mob.ienr.activity.user.UserModelActivity;
import com.bsoft.mob.ienr.activity.user.adapter.CareRecordStructAdapter;
import com.bsoft.mob.ienr.activity.user.adapter.CareRecordTemplateAdapter;
import com.bsoft.mob.ienr.api.NurseRecordApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.nurserecord.Structure;
import com.bsoft.mob.ienr.model.nurserecord.Template;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.view.BsoftActionBar;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: CareRecordTemplate
 * @author 吕自聪 lvzc@bsoft.com.cn
 * @date 2016-4-7 上午10:47:10
 *
 */
public class CareRecordTemplateFragment extends BaseUserFragment {
    private ListView listView;

    /**
     * key 树级别
     */
    private SparseArray<List<?>> map = new SparseArray<List<?>>();

    /**
     * 当前树级别
     */
	private int mCurrLeve = 1;

    /**
     * 当前一级所选项
     */
    private Structure mStructure;


    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_care_record_template;
    }

    @Override
    protected void initView(View rootLayout, Bundle savedInstanceState) {
        listView = (ListView) rootLayout
                .findViewById(R.id.id_lv);
        EmptyViewHelper.setEmptyView(listView, "listView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, listView);
        initListView();
        initBroadCast();
        initActionBar();
        toRefreshData();
    }

    @Override
    protected void toRefreshData() {
        actionGetTreeTask(mCurrLeve);
    }

    private void initActionBar() {

        actionBar.setTitle("选择护理记录模板");
        actionBar.setPatient(mAppApplication.sickPersonVo.BRCH + mAppApplication.sickPersonVo.BRXM);

    }

    @Override
    public void onStart() {
        super.onStart();
        actionBar.setBackAction(new BsoftActionBar.Action() {
            @Override
            public int getDrawable() {
                return R.drawable.ic_arrow_back_black_24dp;
            }

            @Override
            public String getText() {
                return getString(R.string.menu_back);
            }

            @Override
            public void performAction(View view) {
				if (mCurrLeve==2){
					mCurrLeve--;
					toRefreshData();
				}else {
                getActivity().finish();
				}

            }
        });
    }

    private void initListView() {

        listView.setOnItemClickListener(onListItemClickLister);
    }

    private void setFileNames(String name) {
        actionBar.setTitle(name);
    }

    private OnItemClickListener onListItemClickLister = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            Object item = listView.getAdapter()
                    .getItem(position);
            if (mCurrLeve == 1 && item != null) {
                getChildList((Structure) item);
            } else if (mCurrLeve == 2) {
                Template template = (Template) item;
                Intent intent = new Intent(getActivity(),
                        CareRecordActivity.class);
                intent.putExtra("jgbh", template.JGBH);
                startActivity(intent);
            }

        }
    };

    /**
     * 获取指定级别列表项
     *
     * @param level
     *            列表级别
     */
    protected void actionGetTreeTask(int level) {

        GetTreeTask task = new GetTreeTask(level);
        tasks.add(task);

        if (mStructure != null) {
            String[] params = {mStructure.LBBH, mStructure.LBMC};
            task.execute(params);
            return;
        }
        task.execute();
    }

    protected void getChildList(Structure item) {
        mStructure = item;
        actionGetTreeTask(2);
    }

    @SuppressWarnings("rawtypes")
    class GetTreeTask extends AsyncTask<String, String, Response> {

        private int mLevel = 0;

        public GetTreeTask(int level) {
            mLevel = level;
        }

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response doInBackground(String... params) {

            NurseRecordApi api = NurseRecordApi.getInstance(getActivity());

            if (mLevel == 1) {
                Response<List<Structure>> response = api
                        .GetStructureClassificationList(mAppApplication.getAreaId(), mAppApplication.jgId, Constant.sysType);
                return response;
            } else if (mLevel == 2) {

                if (params == null || params.length < 2) {
					return null;
				}
                    String bqid = mAppApplication.getAreaId();
                    Response<List<Template>> response = api.GetStructureList(
                            mAppApplication.jgId, params[0], bqid, Constant.sysType);

                    if (response.ReType == 0) {
                        List date = response.Data;
                        if (date != null && date.size() > 0) {
                            publishProgress(params[1]);
                        }
                    }
                    return response;
                }

            return null;

        }

        @Override
        protected void onProgressUpdate(String... values) {
            setFileNames(values[0]);
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
//			List date = (List) result.Data;
//			if (date == null || date.isEmpty()) {
//				if (mLevel == 1) {
//					showMsgAndVoice("列表为空");
//				} else if (mLevel == 2) {
//					showMsgAndVoice("当前类型下不存在模板，请重新选择");
//				}
//				return;
//			}
//
//			mCurrLeve = mLevel;
//			if (mLevel == 1) {
//				List<Structure> list = (List<Structure>) result.Data;
//				importStructs(list);
//				mStructure = null;
//			} else if (mLevel == 2) {
//				List<Template> list = (List<Template>) result.Data;
//				importTemplate(list);
//			}

            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            toRefreshData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    List date = (List) result.Data;
                    if (date == null || date.isEmpty()) {
                        if (mLevel == 1) {
                            showMsgAndVoiceAndVibrator("列表为空");
                        } else if (mLevel == 2) {
                            showMsgAndVoiceAndVibrator("当前类型下不存在模板，请重新选择");
                        }
                        return;
                    }

                    mCurrLeve = mLevel;
                    if (mLevel == 1) {
                        List<Structure> list = (List<Structure>) result.Data;
                        importStructs(list);
                        //###!!!mStructure = null;
                    } else if (mLevel == 2) {
                        List<Template> list = (List<Template>) result.Data;
                        //XH
                        List<Template> listNew = new ArrayList<>();
                        for (Template template : list) {
                            if (TextUtils.isEmpty(template.BZXX)) {
                                listNew.add(template);
                            } else if (!template.BZXX.contains("(PDA上不显示)")) {
                                listNew.add(template);
                            }
                        }
                        importTemplate(list);
                    }
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }


    public void importTemplate(List<Template> list) {

        CareRecordTemplateAdapter adapter = new CareRecordTemplateAdapter(
                getActivity(), list);
        listView.setAdapter(adapter);
        importMapValues(list);
    }

    /**
     * 导入一级列表
     *
     * @param list
     */
    private boolean needOut = false;//标记位

    public void importStructs(List<Structure> list) {
        if (list != null && list.size() == 1) {
            if (!needOut) {
                needOut = true;
                //只有一条 直接进入下一级
                getChildList(list.get(0));
            } else {
                //只有一条 直接返回上级
                getActivity().finish();
            }
            return;
        }
        CareRecordStructAdapter adapter = new CareRecordStructAdapter(
                getActivity(), list);
        listView.setAdapter(adapter);
        importMapValues(list);
    }

    private void importMapValues(List<?> list) {

        if (map != null) {

            if (map.indexOfKey(mCurrLeve) >= 0) {
                map.remove(mCurrLeve);
            }
            map.put(mCurrLeve, list);
        }
    }

    private void initBroadCast() {
        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {

                String action = intent.getAction();
                if (BarcodeActions.Refresh.equals(action)) {
                    sendUserName();
                    actionBar.setPatient(mAppApplication.sickPersonVo.BRCH
                            + mAppApplication.sickPersonVo.BRXM);
                } else if (BarcodeActions.Bar_Get.equals(action)) {

                    BarcodeEntity entity = (BarcodeEntity) intent
                            .getParcelableExtra("barinfo");
                    if (FastSwitchUtils.needFastSwitch(entity)) {
                        FastSwitchUtils.fastSwith(
                                (UserModelActivity) getActivity(), entity);
                    }

                }
            }
        };
    }


	@Override
	public boolean onKeyBackPressed() {
		if (mCurrLeve==2){
			mCurrLeve--;
			toRefreshData();
			return true;
		}
		return false;
	}
}
