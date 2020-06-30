package com.bsoft.mob.ienr.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.MainActivity;
import com.bsoft.mob.ienr.adapter.CatheterAdapter;
import com.bsoft.mob.ienr.adapter.CatheterHistoryAdapter;
import com.bsoft.mob.ienr.adapter.PersonAdapter;
import com.bsoft.mob.ienr.api.CatheterApi;
import com.bsoft.mob.ienr.dynamicui.catheterDrainage.LeftSlideDeleteListView;
import com.bsoft.mob.ienr.fragment.base.LeftMenuItemFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.catheter.CatheterBeenMeasurData;
import com.bsoft.mob.ienr.model.catheter.CatheterMeasurData;
import com.bsoft.mob.ienr.model.catheter.CatheterRespose;
import com.bsoft.mob.ienr.model.kernel.SickPersonVo;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.view.BsoftActionBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * 导管引流  Created by hy on 14-3-21.
 */
public class CatheterFragment extends LeftMenuItemFragment {

    private ListView mListView;
    private LeftSlideDeleteListView historyView;
    private ListView catheterView;

    private CatheterAdapter adapter;
    private CatheterHistoryAdapter historyAdapter;
    private PersonAdapter mAdatper;

    private ItemDelete itemDeleteListner = new ItemDelete();

    protected LinkedList<AsyncTask<?, ?, ?>> tasks = new LinkedList<AsyncTask<?, ?, ?>>();
    private int jlxh;
    private String zyh;

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_catheter;
    }

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {

        mListView = (ListView) rootView.findViewById(R.id.id_lv);
        EmptyViewHelper.setEmptyView(mListView, "mListView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mListView);
        historyView = (LeftSlideDeleteListView) rootView.findViewById(R.id.catheter_history_list);
        EmptyViewHelper.setEmptyView(historyView, "historyView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, historyView);
        catheterView = (ListView) rootView.findViewById(R.id.id_lv_2);
        EmptyViewHelper.setEmptyView(catheterView, "id_lv_2");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, catheterView);
        initPersonListView();
        initActionBar();
        historyView.setOnListViewItemDeleteClikListener(itemDeleteListner);
        toRefreshData();
    }

    private void initActionBar() {


        actionBar.setTitle("导管引流");
        actionBar.setBackAction(new BsoftActionBar.Action() {
            @Override
            public void performAction(View view) {
                ((MainActivity) getActivity()).toggle();
            }

            @Override
            public String getText() {
                return getString(R.string.menu_back);
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_menu_black_24dp;
            }
        });

        actionBar.addAction(new BsoftActionBar.Action() {

            @Override
            public void performAction(View view) {
                onSaveAction();
            }

            @Override
            public String getText() {
                return "保存";
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }
        });

    }

    @Override
    protected void toRefreshData() {
        GetPersonTask task = new GetPersonTask();
        tasks.add(task);
        task.execute();
    }

    private void initPersonListView() {


        mListView.setTextFilterEnabled(true);
        // checked/activated
        mListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        mListView.setOnItemClickListener(onPersonItemClickListener);
    }


    public AdapterView.OnItemClickListener onPersonItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            doSickerSelect(position);
        }
    };

    private void doSickerSelect(int position) {
        mListView.setItemChecked(position,
                true);
        //获取该病人数据
        SickPersonVo person = getSelectPerson();
        if (person != null) {
            zyh = person.ZYH;
            getCatheterTask(zyh);
        }
    }

    private void onSaveAction(String... parms) {
        int position = mListView
                .getCheckedItemPosition();

        if (position == AdapterView.INVALID_POSITION) {
           /* VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "请选择病人", getActivity());*/
            showMsgAndVoiceAndVibrator("请选择病人");
            return;
        }

        SickPersonVo person = getSelectPerson();
        if (person == null) {
           /* VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "请选择病人", getActivity());*/
            showMsgAndVoiceAndVibrator("请选择病人");
            return;
        }
        String zyh = person.ZYH;
        ArrayList<CatheterBeenMeasurData> list = new ArrayList<>();
        for (int i = 0; i < catheterView.getAdapter().getCount(); i++) {
            CatheterBeenMeasurData saveData = new CatheterBeenMeasurData();
            CatheterMeasurData data = (CatheterMeasurData) catheterView.getAdapter().getItem(i);
            saveData.YLGXH = data.YPXH;
            saveData.YLGMC = data.YZMC;
            saveData.YZXH = data.YZXH;
            saveData.TZXM = data.DZXH;
            saveData.YLL = data.YLL;
            saveData.BRBQ = mAppApplication.getAreaId();
            saveData.JGID = mAppApplication.jgId;
            saveData.JLGH = mAppApplication.user.YHID;
            saveData.ZYH = zyh;

            if (saveData.YLL == null || saveData.YLL.equals("") || saveData.TZXM == null || saveData.TZXM.equals("")) {
                showMsgAndVoiceAndVibrator("请确保所有用量以及关联项目都已填写");
                return;
            }
            list.add(saveData);
        }
        String data = null;
        try {
            data = JsonUtil.toJson(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
        saveCatheterTask(data);
    }

    private void cancelCatheterTask(String... parms) {
        CancleTask cancleTask = new CancleTask();
        tasks.add(cancleTask);
        cancleTask.execute(parms);
    }

    private void saveCatheterTask(String... parms) {
        SaveTask saveTask = new SaveTask();
        tasks.add(saveTask);
        saveTask.execute(parms);

    }

    private void getCatheterTask(String... parms) {
        CatheterTask catheterTask = new CatheterTask();
        tasks.add(catheterTask);
        catheterTask.execute(parms);

    }

    private SickPersonVo getSelectPerson() {

        int position = mListView
                .getCheckedItemPosition();
        if (position == AdapterView.INVALID_POSITION) {
            return null;
        }
        return (SickPersonVo) mListView
                .getAdapter().getItem(position);
    }

    //删除task
    private class CancleTask extends AsyncTask<String, String, Response<String>> {
        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.saveing);
        }

        @Override
        protected Response<String> doInBackground(String... params) {
            if (params == null) {
                return null;
            }
            CatheterApi api = CatheterApi.getInstance(getActivity());
            String jgid = mAppApplication.jgId;
            return api.CancelCatheter(params[0], jgid);
        }

        @Override
        protected void onPostExecute(Response<String> result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }

            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        cancelCatheterTask(String.valueOf(jlxh));
                    }
                }).showLoginDialog();
                return;
            }
            if (result.ReType != 0) {
                showMsgAndVoice(result.Msg);
              /*  MediaUtil.getInstance(getActivity()).playSound(
                        R.raw.wrong, getActivity());*/
            } else {
                showMsgAndVoice("删除成功");
                //重新加载
                SickPersonVo person = getSelectPerson();
                if (person != null) {
                    String zyh = person.ZYH;
                    getCatheterTask(zyh);
                }
            }

        }
    }

    //保存task
    private class SaveTask extends AsyncTask<String, String, Response<String>> {
        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.saveing);
        }

        @Override
        protected Response<String> doInBackground(String... params) {
            if (params == null) {
                return null;
            }
            CatheterApi api = CatheterApi.getInstance(getActivity());
            return api.SaveCatheter(params[0]);
        }

        @Override
        protected void onPostExecute(Response<String> result) {

            hideLoadingDialog();
            tasks.remove(this);

            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }

            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        onSaveAction();
                    }
                }).showLoginDialog();
                return;
            }
            if (result.ReType != 0) {
                showMsgAndVoice(result.Msg);
               /* MediaUtil.getInstance(getActivity()).playSound(
                        R.raw.wrong, getActivity());*/
            } else {
                showMsgAndVoice(R.string.project_save_success);
                //重新加载
                SickPersonVo person = getSelectPerson();
                if (person != null) {
                    String zyh = person.ZYH;
                    getCatheterTask(zyh);
                }
            }

        }
    }

    //加载改病人需测和已测数据
    private class CatheterTask extends AsyncTask<String, String, Response<CatheterRespose>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<CatheterRespose> doInBackground(String... parms) {
            String zyh = parms[0];
            String bqdm = mAppApplication.getAreaId();
            String jgid = mAppApplication.jgId;
            int sysType = Constant.sysType;
            CatheterApi api = CatheterApi.getInstance(getActivity());
            return api.getCatheter(bqdm, jgid, zyh, sysType);
        }

        @Override
        protected void onPostExecute(Response<CatheterRespose> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();
            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        getCatheterTask(zyh);
                    }
                }).showLoginDialog();
                return;
            }
            if (result.ReType == 0) {
                ArrayList<CatheterMeasurData> list1 = result.Data.Table1;
                ArrayList<CatheterBeenMeasurData> list2 = result.Data.Table2;
                catheterView.setAdapter(null);
                historyView.setAdapter(null);
                if (null != list1 && list1.size() > 0) {
                    //展现需测列表
                    importCatheter(list1);
                } else {
                    toastInfo("没有需测数据");
                }
                if (null != list2 && list2.size() > 0) {
                    //展现历史列表
                    importHistory(list2);
                }

            } else {
                showTipDialog(result.Msg);
//                AlertBox.Show(getActivity(), getString(R.string.project_tips), result.Msg, getString(R.string.project_operate_ok));
            }

        }
    }


    /**
     * 病人列表异步加载
     */
    class GetPersonTask extends AsyncTask<Void, Void, Response<List<SickPersonVo>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<SickPersonVo>> doInBackground(Void... params) {
            String bqdm = mAppApplication.getAreaId();
            String jgid = mAppApplication.jgId;
            int sysType = Constant.sysType;

            CatheterApi api = CatheterApi.getInstance(getActivity());
            return api.GetpationtList(bqdm, jgid, sysType);
        }


        @Override
        protected void onPostExecute(Response<List<SickPersonVo>> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：请求参数错误");
                return;
            }

            adapter = null;
            historyAdapter = null;
            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        toRefreshData();
                    }
                }).showLoginDialog();
                return;
            }
            if (result.ReType == 0) {
                ArrayList<SickPersonVo> list = (ArrayList<SickPersonVo>) result.Data;
                if (list == null) {
                    list = new ArrayList<>();
                    //toastInfo("病人列表为空", Style.INFO, R.id.catheter_person_ll);
                }
                importPersons(list);
            } else {
                showTipDialog(result.Msg);
//                    AlertBox.Show(getActivity(), getString(R.string.project_tips), result.Msg, getString(R.string.project_operate_ok));
            }

        }
    }


    private void importPersons(ArrayList<SickPersonVo> list) {
        mAdatper = new PersonAdapter(getActivity(), list);
        mListView.setAdapter(mAdatper);
        //选中之前选中的病人
        if (!TextUtils.isEmpty(zyh)) {
            //
            int pos = -1;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).ZYH.equals(zyh)) {
                    pos = i;
                }
            }
            if (pos >= 0) {
                doSickerSelect(pos);
            }
        }

    }


    private void importCatheter(ArrayList<CatheterMeasurData> list) {
        adapter = new CatheterAdapter(getActivity(), list);
        catheterView.setAdapter(adapter);
    }


    private void importHistory(ArrayList<CatheterBeenMeasurData> list) {
        historyAdapter = new CatheterHistoryAdapter(getActivity(), list);
        historyView.setAdapter(historyAdapter);
        historyView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //消费 掉
            }
        });

    }


    public void toastInfo(String msg) {
        //Crouton.showText(getActivity(), msg, style, viewGroupId);
        showSnack(msg);
    }


    //删除记录数据的操作
    public class ItemDelete implements LeftSlideDeleteListView.OnListViewItemDeleteClikListener {
        @Override
        public void onListViewItemDeleteClick(int position) {
            //删除操作
            jlxh = (int) historyView.getAdapter().getItem(position);
            cancelCatheterTask(String.valueOf(jlxh));
        }
    }
}
