package com.bsoft.mob.ienr.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.adapter.DailyWorkAdapter;
import com.bsoft.mob.ienr.api.DailyWorkApi;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.fragment.base.LeftMenuItemFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.dailywork.DailyWork;
import com.bsoft.mob.ienr.model.dailywork.DailyWorkCount;
import com.bsoft.mob.ienr.util.AgainLoginUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 今日工作
 */
public class DailyWorkFragment extends LeftMenuItemFragment {
     // 标题工具栏
    private ListView cardsList;
    private DailyWorkAdapter adapter;

    private List<DailyWork> l_yzjh;
    private List<DailyWork> l_bdyz;
    private List<DailyWork> l_jycj;
    private List<DailyWork> l_fxpg;

    private CheckBox cb_default;
    private CheckBox cb_all;
    private int filter = 1;//1我的病人;0全部病人


    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }
    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_daily_work;
    }

    @Override
    protected void initView(View rootView, Bundle savedInstanceState) {
        cardsList = (ListView) rootView.findViewById(R.id.id_lv);

        EmptyViewHelper.setEmptyView(cardsList,"cardsList");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout,cardsList);

        TextView id_tv = (TextView) rootView.findViewById(R.id.id_tv);
        id_tv.setText("病人类型：");
        cb_default = (CheckBox) rootView.findViewById(R.id.id_cb);
        cb_default.setText("我的病人");
        cb_all = (CheckBox) rootView.findViewById(R.id.id_cb_2);
        cb_all.setText("全部病人");

        // 初始化标题工具栏
        initActionbar();
        initCheck();

        toRefreshData();
    }

    @Override
    protected void toRefreshData() {
        refreshData(filter);
    }

    private void initCheck() {
        cb_default.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    filter = 1;
                    cb_all.setChecked(!isChecked);

                    cardsList.setAdapter(null);
                    toRefreshData();

                } else {
                    if (!cb_all.isChecked()) {
                        cb_default.setChecked(!isChecked);
                    }
                }
            }
        });
        cb_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    filter = 0;
                    cb_default.setChecked(!isChecked);

                    cardsList.setAdapter(null);
                    toRefreshData();
                } else {
                    if (!cb_default.isChecked()) {
                        cb_all.setChecked(!isChecked);
                    }
                }
            }
        });
    }

    private void initActionbar() {
        actionBar.setTitle("今日工作");
    }

    @SuppressWarnings("unchecked")
    private DailyWorkAdapter createAdapter() {
        ArrayList<String> items = new ArrayList<String>();
        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 0:
                    items.add("病区当前需执行医嘱计划");
                    break;
                case 1:
                    items.add("病区当前变动医嘱");
                    break;
                case 2:
                    items.add("病区当前检验采集");
                    break;
                case 3:
                    items.add("病区当前风险提醒");
                    break;
                default:
            }

        }
        l_yzjh = l_yzjh == null ? new ArrayList<DailyWork>() : l_yzjh;
        l_bdyz = l_bdyz == null ? new ArrayList<DailyWork>() : l_bdyz;
        l_jycj = l_jycj == null ? new ArrayList<DailyWork>() : l_jycj;
        l_fxpg = l_fxpg == null ? new ArrayList<DailyWork>() : l_fxpg;
        adapter = new DailyWorkAdapter(getActivity(), items, l_yzjh, l_bdyz,
                l_jycj, l_fxpg);
        return adapter;
    }

    private void refreshData(int filter) {
        GetDataTask task = new GetDataTask();
        tasks.add(task);
        task.execute(filter);
    }

    class GetDataTask extends AsyncTask<Integer, Void, Response<DailyWorkCount>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<DailyWorkCount> doInBackground(Integer... params) {
            int id = params[0];
            String hsgh = "";
            if (id == 1) {
                hsgh = mAppApplication.user.YHID;
            } else if (id == 0) {
                hsgh = "";
            }

            String jgid = mAppApplication.jgId;
            String bqdm = mAppApplication.getAreaId();
            String nowDate = DateTimeHelper.getServerDate();
            String gzrq = nowDate;
            return DailyWorkApi.getInstance(getActivity()).getWorkList(bqdm,
                    gzrq, hsgh, jgid, Constant.sysType);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Response<DailyWorkCount> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：请求参数错误");
                return;
            }
            l_yzjh = new ArrayList<>();
            l_bdyz = new ArrayList<>();
            l_jycj = new ArrayList<>();
            l_fxpg = new ArrayList<>();

            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication).showLoginDialog();
                return;
            } else if (result.ReType == 0) {
                l_yzjh = result.Data.plan;
                l_bdyz = result.Data.changeAdvice;
                l_jycj = result.Data.inspection;
                l_fxpg = result.Data.risk;
                cardsList.setAdapter(createAdapter());
            }
        }
    }


}
