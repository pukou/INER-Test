package com.bsoft.mob.ienr.fragment.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.ShowChartActivity;
import com.bsoft.mob.ienr.activity.user.UserModelActivity;
import com.bsoft.mob.ienr.adapter.ExamineAdapter;
import com.bsoft.mob.ienr.adapter.InspectionAdapter;
import com.bsoft.mob.ienr.adapter.InspectionDetailAdapter;
import com.bsoft.mob.ienr.adapter.MyPagerAdapter;
import com.bsoft.mob.ienr.api.InspectionApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.inspection.ExamineDetailVo;
import com.bsoft.mob.ienr.model.inspection.ExamineVo;
import com.bsoft.mob.ienr.model.inspection.InspectionDetailVo;
import com.bsoft.mob.ienr.model.inspection.InspectionVo;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.StringUtil;
import com.bsoft.mob.ienr.view.BsoftActionBar;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;
import com.classichu.adapter.widget.ClassicEmptyView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-11 上午11:20:14
 * @类说明 检查查询
 */
public class InspectionFragment extends BaseUserFragment {


    private ListView listView1;
    private ListView listView2;
    private TextView but1, but2;

    private InspectionAdapter adapter1;
    private ExamineAdapter adapter2;
    private ClassicEmptyView classicEmptyView1;
    private ClassicEmptyView classicEmptyView2;

    private AlertDialog mAlertDialog1, mAlertDialog2;
    private View viewDialog1, viewDialog2;
    private BsoftActionBar actionBar1, actionBar2;
    private ListView dListView;
    private InspectionDetailAdapter dAdapter;
    private ProgressBar emptyProgress1, emptyProgress2;
    private TextView JCBX, JCZD;
    private ViewPager id_vp;

    // 当前选择的模块，刷新时使用
    private int current = 1;


    /*
         升级编号【56010025】============================================= start
         检验检查：检验List项目数据趋势图，项目分类查看
         ================= Classichu 2017/10/18 9:34
         */
    private SpinnerLayout spinnerLayout;
    private Spinner id_spinner;
    private ViewPager viewPager;

    /* =============================================================== end */

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_inspection;
    }

    @Override
    protected void initView(View mainView, Bundle savedInstanceState) {
       /*
         升级编号【56010025】============================================= start
         检验检查：检验List项目数据趋势图，项目分类查看
         ================= Classichu 2017/10/18 9:34
         */
        spinnerLayout = (SpinnerLayout) mainView.findViewById(R.id.id_spinner_layout);
        id_spinner = spinnerLayout.getSpinner();
          /* =============================================================== end */
        viewPager = (ViewPager) mainView.findViewById(R.id.id_vp);
        List<View> viewList = new ArrayList<>();
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_view_pager_list, null, false);
        View view2 = LayoutInflater.from(mContext).inflate(R.layout.layout_view_pager_list, null, false);
        listView1 = view.findViewById(R.id.id_lv);
        listView2 = view2.findViewById(R.id.id_lv);

        EmptyViewHelper.setEmptyView(listView1,"listView1");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout,listView1);
        EmptyViewHelper.setEmptyView(listView2,"listView2");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout,listView2);

        viewList.add(view);
        /* ========================【fixme】 change 2018-04-12 17:33:00 start */
        viewList.add(view2);
        /* ======================== end */
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(viewList);
        viewPager.setAdapter(myPagerAdapter);

        but1 = (TextView) mainView.findViewById(R.id.id_tv);
        but2 = (TextView) mainView.findViewById(R.id.id_tv_2);
        but1.setText("检验");
        /* ========================【fixme】 change 2018-04-12 17:33:00 start */
//        but1.setVisibility(View.GONE);
        but2.setText("检查");
//        but2.setVisibility(View.GONE);
        /* ======================== end */
        initBroadCast();
        initListView1();
        initListView2();
        initActionBar();
        initButtons();
        changeStatusView();
        //
        toRefreshData();
    }


    @Override
    protected void toRefreshData() {
        if (viewPager.getCurrentItem() == 0) {
            performHttpTask();
        } else if (viewPager.getCurrentItem() == 1) {
            performHttpTask2();
        }
    }

    private void initActionBar() {

        actionBar.setTitle("检验检查");
        actionBar.setPatient(application.sickPersonVo.BRCH + application.sickPersonVo.BRXM);

    }

    private void performHttpTask() {
        GetHttpTask getHttpTask = new GetHttpTask();
        tasks.add(getHttpTask);
        getHttpTask.execute();
    }

    private void performHttpTask2() {
        GetHttpTask2 getHttpTask = new GetHttpTask2();
        tasks.add(getHttpTask);
        getHttpTask.execute();
    }

    private void changeStatusView() {
        if (current == 1) {
            but1.setSelected(true);
            but2.setSelected(false);
            viewPager.setCurrentItem(0);
            /* ========================【fixme】 change 2018-04-12 17:33:00 start */
            spinnerLayout.setVisibility(View.VISIBLE);
            /* ======================== end */
        } else {
            but1.setSelected(false);
            but2.setSelected(true);
            viewPager.setCurrentItem(1);
            /* ========================【fixme】 change 2018-04-12 17:33:00 start */
            spinnerLayout.setVisibility(View.GONE);
            /* ======================== end */
        }
    }

    private void initButtons() {

        but1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (current != 1) {
                    current = 1;
                    if (null == adapter1) {
                        toRefreshData();
                    }
                    changeStatusView();
                }
            }
        });
        but2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (current != 2) {
                    current = 2;
                    if (null == adapter2) {
                        performHttpTask2();
                    }
                    changeStatusView();
                }
            }
        });
    }

    private void initListView2() {
/*
        listView2.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {

                String label = DateUtils.formatDateTime(getActivity(),
                        System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE
                                | DateUtils.FORMAT_ABBREV_ALL);

                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                performHttpTask2();
            }
        });*/

        listView2.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                viewDialog2 = LayoutInflater.from(getActivity()).inflate(
                        R.layout.layout_dialog_examine_detail,  null,false);

                builder2.setView(viewDialog2);
                mAlertDialog2 = builder2.create();
                mAlertDialog2.show();
                actionBar2 = (BsoftActionBar) viewDialog2
                        .findViewById(R.id.actionbar);
                actionBar2.setTitle("检查详情");
                actionBar2.setBackAction(new Action() {
                    @Override
                    public void performAction(View view) {
                        mAlertDialog2.dismiss();
                    }
                    @Override
                    public String getText() {
                        return getString(R.string.menu_back);
                    }
                    @Override
                    public int getDrawable() {
                        return R.drawable.ic_arrow_back_black_24dp;
                    }
                });
                emptyProgress2 = (ProgressBar) viewDialog2
                        .findViewById(R.id.emptyProgress);
                JCBX = (TextView) viewDialog2.findViewById(R.id.JCBX);
                JCZD = (TextView) viewDialog2.findViewById(R.id.JCZD);

                new AsyncTask<Void, Void, Response<List<ExamineDetailVo>>>() {
                    @Override
                    protected void onPreExecute() {
                        emptyProgress2.setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected Response<List<ExamineDetailVo>> doInBackground(Void... arg0) {

                        String jgid = mAppApplication.jgId;
                        int sysType = Constant.sysType;
                        return InspectionApi.getInstance(getActivity())
                                .GetExamineResultDetail(
                                        adapter2.getItem(position).YBHM,
                                        adapter2.getItem(position).JCLX,
                                        jgid, sysType);
                    }

                    @Override
                    protected void onPostExecute(Response<List<ExamineDetailVo>> result) {
                        if (null != result) {
                            if (result.ReType == 0) {
                                ArrayList<ExamineDetailVo> tList = (ArrayList<ExamineDetailVo>) result.Data;
                                if (null != tList && tList.size() > 0) {
                                    ExamineDetailVo eVo = tList.get(0);
                                    if (null != eVo.JCZD
                                            && eVo.JCZD.length() > 0) {
                                        String jczdStr = StringUtil
                                                .replaceBlank(eVo.JCZD);
                                        JCZD.setText("诊断：" + jczdStr);
                                    }
                                    if (null != eVo.JCBX
                                            && eVo.JCBX.length() > 0) {
                                        String jcbxStr = StringUtil
                                                .replaceBlank(eVo.JCBX);
                                        JCBX.setText("表现：" + jcbxStr);
                                    }
                                }

                            } else {
                                /*
         升级编号【56010025】============================================= start
         检验检查：检验List项目数据趋势图，项目分类查看
         ================= Classichu 2017/10/18 9:34
         */
                                AlertBoxShow(result.Msg);
                                  /* =============================================================== end */
                            }
                        }
                        emptyProgress2.setVisibility(View.GONE);
                    }
                }.execute();
            }
        });
    }

    /*
         升级编号【56010025】============================================= start
         检验检查：检验List项目数据趋势图，项目分类查看
         ================= Classichu 2017/10/18 9:34
         */
    private void AlertBoxShow(String Msg) {
        if (getActivity() != null) {
                showTipDialog(Msg);
               /* AlertBox.Show(getActivity(), getString(R.string.project_tips), Msg,
                        getString(R.string.project_operate_ok));*/
        }
    }

    /* =============================================================== end */
    private void initListView1() {


        listView1.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                viewDialog1 = LayoutInflater.from(getActivity()).inflate(
                        R.layout.layout_content_list,  null,false);
                TextView id_txt = (TextView) viewDialog1.findViewById(R.id.id_txt);
                id_txt.setVisibility(View.GONE);

                builder1.setView(viewDialog1);
                mAlertDialog1 = builder1.create();
                mAlertDialog1.show();
                actionBar1 = (BsoftActionBar) viewDialog1
                        .findViewById(R.id.actionbar);
                actionBar1.setTitle("检验详情");
                actionBar1.setBackAction(new Action() {
                    @Override
                    public void performAction(View view) {
                        mAlertDialog1.dismiss();
                    }
                    @Override
                    public String getText() {
                        return getString(R.string.menu_back);
                    }
                    @Override
                    public int getDrawable() {
                        return R.drawable.ic_arrow_back_black_24dp;
                    }
                });
                emptyProgress1 = (ProgressBar) viewDialog1
                        .findViewById(R.id.emptyProgress);
                dListView = (ListView) viewDialog1.findViewById(R.id.id_lv);
                dAdapter = new InspectionDetailAdapter(getActivity());
                dListView.setAdapter(dAdapter);
                dListView.addHeaderView(LayoutInflater.from(getContext()).inflate(R.layout.item_list_inspection_detail_header,null,false));
                EmptyViewHelper.setEmptyView(dListView);
                /*
         升级编号【56010025】============================================= start
         检验检查：检验List项目数据趋势图，项目分类查看
         ================= Classichu 2017/10/18 9:34
         */
                dListView.setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                        //adapterView.getAdapter().getItem(pos); 针对headerview的list也正确
                        InspectionDetailVo inspectionDetailVo = (InspectionDetailVo) adapterView.getAdapter().getItem(position);
                        Intent intent = new Intent(getActivity(), ShowChartActivity.class);
                        intent.putExtra("xmid", inspectionDetailVo.XMID);
                        intent.putExtra("zwmc", inspectionDetailVo.ZWMC);
                        startActivity(intent);
                    }
                });
                  /* =============================================================== end */

                new AsyncTask<Void, Void, Response<List<InspectionDetailVo>>>() {
                    @Override
                    protected void onPreExecute() {
                        emptyProgress1.setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected Response<List<InspectionDetailVo>> doInBackground(Void... arg0) {

                        String jgid = mAppApplication.jgId;
                        int sysType = Constant.sysType;
                        return InspectionApi.getInstance(getActivity())
                                .GetInspectionDetail(
                                        adapter1.getItem(position).YBHM,
                                        jgid, sysType);
                    }

                    @Override
                    protected void onPostExecute(Response<List<InspectionDetailVo>> result) {
                        if (null != result) {
                            if (result.ReType == 0) {
                                ArrayList<InspectionDetailVo> tList = (ArrayList<InspectionDetailVo>) result.Data;
                                if (null != tList && tList.size() > 0) {
                                    dAdapter.addData(tList);
                                }
                            } else {
                                /*
         升级编号【56010025】============================================= start
         检验检查：检验List项目数据趋势图，项目分类查看
         ================= Classichu 2017/10/18 9:34
         */
                                AlertBoxShow(result.Msg);
                                  /* =============================================================== end */
                            }
                        }
                        emptyProgress1.setVisibility(View.GONE);
                    }
                }.execute();
            }
        });
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
                    if (current == 1) {
                        toRefreshData();
                        adapter1 = null;
                        adapter2 = null;
                    } else {
                        performHttpTask2();
                        adapter1 = null;
                        adapter2 = null;
                    }
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

    class GetHttpTask extends AsyncTask<Void, Void, Response<List<InspectionVo>>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<InspectionVo>> doInBackground(Void... arg0) {

            if (mAppApplication.sickPersonVo == null) {
                return null;
            }
            String zyh = mAppApplication.sickPersonVo.ZYH;
            String jgid = mAppApplication.jgId;
            int sysType = Constant.sysType;
            return InspectionApi.getInstance(getActivity()).GetInspectionList(
                    zyh, jgid, sysType);
        }

        @Override
        protected void onPostExecute(Response<List<InspectionVo>> result) {

            super.onPostExecute(result);
            hideSwipeRefreshLayout();

            if (null == adapter1) {
                adapter1 = new InspectionAdapter(getActivity());
                listView1.setAdapter(adapter1);
            } else {
                adapter1.clearData();
            }
            if (result == null) {
                showMsgAndVoiceAndVibrator("加载失败");

                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        toRefreshData();
                    }
                }).showLoginDialog();
                return;
            } else if (result.ReType == 0) {
                final ArrayList<InspectionVo> tList = (ArrayList<InspectionVo>) result.Data;
                if (null != tList && tList.size() > 0) {
                    adapter1.addData(tList);
                    /*
         升级编号【56010025】============================================= start
         检验检查：检验List项目数据趋势图，项目分类查看
         ================= Classichu 2017/10/18 9:34
         */
                    //
                    final List<String> stringList = new ArrayList<>();
                    stringList.add("全部");
                    for (InspectionVo inspectionVo : tList) {
                        if (!stringList.contains(inspectionVo.XMMC)) {
                            stringList.add(inspectionVo.XMMC);
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, stringList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    id_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) {
                                adapter1.clearData();
                                adapter1.addData(tList);
                                return;
                            }
                            String type = stringList.get(position);
                            ArrayList<InspectionVo> tListFitter = new ArrayList<>();
                            for (InspectionVo inspectionVo : tList) {
                                if (inspectionVo.XMMC.contains(type)) {
                                    tListFitter.add(inspectionVo);
                                }
                            }
                            adapter1.clearData();
                            adapter1.addData(tListFitter);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    id_spinner.setAdapter(adapter);
                    //
                      /* =============================================================== end */
                }
            } else {
                /*
         升级编号【56010025】============================================= start
         检验检查：检验List项目数据趋势图，项目分类查看
         ================= Classichu 2017/10/18 9:34
         */
                AlertBoxShow(result.Msg);
                  /* =============================================================== end */
            }

        }
    }

    class GetHttpTask2 extends AsyncTask<Void, Void, Response<List<ExamineVo>>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<ExamineVo>> doInBackground(Void... arg0) {

            if (mAppApplication.sickPersonVo == null) {
                return null;
            }
            String zyh = mAppApplication.sickPersonVo.ZYH;
            String jgid = mAppApplication.jgId;
            int sysType = Constant.sysType;
            return InspectionApi.getInstance(getActivity())
                    .GetExamineResultList(zyh, jgid, sysType);
        }

        @Override
        protected void onPostExecute(Response<List<ExamineVo>> result) {

            super.onPostExecute(result);
            hideSwipeRefreshLayout();

            if (null == adapter2) {
                adapter2 = new ExamineAdapter(getActivity());
                listView2.setAdapter(adapter2);
            } else {
                adapter2.clearData();
            }
            if (null == result) {
                showMsgAndVoiceAndVibrator("加载失败");

                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        performHttpTask2();
                    }
                }).showLoginDialog();
                return;
            } else if (result.ReType == 0) {
                @SuppressWarnings("unchecked") final ArrayList<ExamineVo> tList = (ArrayList<ExamineVo>) result.Data;
                if (null != tList && tList.size() > 0) {
                    adapter2.addData(tList);
                    /*
         升级编号【56010025】============================================= start
         检验检查：检验List项目数据趋势图，项目分类查看
         ================= Classichu 2017/10/18 9:34
         */
                    //
                    final List<String> stringList = new ArrayList<>();
                    stringList.add("全部");
                    for (ExamineVo examineVo : tList) {
                        if (!stringList.contains(examineVo.JCMC)) {
                            stringList.add(examineVo.JCMC);
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, stringList);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    id_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position == 0) {
                                adapter2.clearData();
                                adapter2.addData(tList);
                                return;
                            }
                            String type = stringList.get(position);
                            ArrayList<ExamineVo> tListFitter = new ArrayList<>();
                            for (ExamineVo examineVo : tList) {
                                if (examineVo.JCMC.contains(type)) {
                                    tListFitter.add(examineVo);
                                }
                            }
                            adapter2.clearData();
                            adapter2.addData(tListFitter);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    id_spinner.setAdapter(adapter);
                    //
                      /* =============================================================== end */
                }
            } else {
                /*
         升级编号【56010025】============================================= start
         检验检查：检验List项目数据趋势图，项目分类查看
         ================= Classichu 2017/10/18 9:34
         */
                AlertBoxShow(result.Msg);
                  /* =============================================================== end */
            }

        }
    }

}
