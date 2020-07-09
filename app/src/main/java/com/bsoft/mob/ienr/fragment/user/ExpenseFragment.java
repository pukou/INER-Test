package com.bsoft.mob.ienr.fragment.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.UserModelActivity;
import com.bsoft.mob.ienr.adapter.ExpenseAdapter;
import com.bsoft.mob.ienr.adapter.ExpenseDetailAdapter;
import com.bsoft.mob.ienr.adapter.MyPagerAdapter;
import com.bsoft.mob.ienr.api.ExpenseApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.expense.ExpenseDaysDetail;
import com.bsoft.mob.ienr.model.expense.ExpenseRespose;
import com.bsoft.mob.ienr.model.expense.ExpenseTotal;
import com.bsoft.mob.ienr.model.expense.ExpenseVo;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-11 上午11:20:14
 * @类说明 费用查询
 */
public class ExpenseFragment extends BaseUserFragment {

    //
    private View mainView;
    private ListView listView1;
    private ListView listView2;
    private TextView ZJJE, ZFJE, JKJE, FYYE;
    private TextView but1, but2;
    private LinearLayout timeLay;
    //private  LinearLayout lineLay;
    //private  DateTimeSlider sDateTimeSlider, eDateTimeSlider;
    private TextView stime, etime;
    // 当前时间
    //private  String sTime, eTime;

    //private  SickPersonVo vo;
    private ExpenseAdapter adapter1;
    private ExpenseDetailAdapter adapter2;
//    private ArrayList<ExpenseVo> list1;
//    private ArrayList<ExpenseDaysDetail> list2;

    // GetHttpTask getHttpTask;
    // ChangeTask changeTask;

    // 当前选择的模块，刷新时使用
    int current = 1;


    private View sltStimeView;
    private View sltEtimeView;

    private ImageView searchBtn;
    private ViewPager viewPager;

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_expense;
    }

    @Override
    protected void initView(View rootLayout, Bundle savedInstanceState) {
        mainView = rootLayout;

        listView1 = (ListView) mainView
                .findViewById(R.id.id_lv);

        ZJJE = (TextView) mainView.findViewById(R.id.ZJJE);
        ZFJE = (TextView) mainView.findViewById(R.id.ZFJE);
        JKJE = (TextView) mainView.findViewById(R.id.JKJE);
        FYYE = (TextView) mainView.findViewById(R.id.FYYE);
        stime = (TextView) mainView.findViewById(R.id.stime);
        etime = (TextView) mainView.findViewById(R.id.etime);
        timeLay = (LinearLayout) mainView.findViewById(R.id.id_layout_double_time);
        but1 = (TextView) mainView.findViewById(R.id.id_tv);
        but1.setText("汇总");
        but2 = (TextView) mainView.findViewById(R.id.id_tv_2);
        but2.setText("明细");
        // lineLay = (LinearLayout) mainView.findViewById(R.id.lineLay);


        sltStimeView = mainView.findViewById(R.id.slt_stime_ly);
        sltEtimeView = mainView.findViewById(R.id.slt_etime_ly);

        final TextView stimeTitle = (TextView) sltStimeView
                .findViewById(R.id.stime_title);
        final TextView etimeTitle = (TextView) sltEtimeView
                .findViewById(R.id.etime_title);

        stimeTitle.setText(R.string.start_time);
        etimeTitle.setText(R.string.end_time);


        viewPager = (ViewPager) mainView.findViewById(R.id.id_vp);
        List<View> viewList = new ArrayList<>();
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_view_pager_list, null, false);
        View view2 = LayoutInflater.from(mContext).inflate(R.layout.layout_view_pager_list, null, false);
        listView1 = view.findViewById(R.id.id_lv);
        listView2 = view2.findViewById(R.id.id_lv);

        EmptyViewHelper.setEmptyView(listView1, "listView1");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout,listView1);
        EmptyViewHelper.setEmptyView(listView2, "listView2");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout,listView2);
        viewList.add(view);
        viewList.add(view2);
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(viewList);
        viewPager.setAdapter(myPagerAdapter);


        // listView2.setOnRefreshListener(onDRefreshListener);

        adapter1 = new ExpenseAdapter(getActivity());
        listView1.setAdapter(adapter1);
        adapter2 = new ExpenseDetailAdapter(getActivity());
        listView2.setAdapter(adapter2);

        initActionBar();
        initTime();
        initBroadCast();
        initCheckBtns();

        searchBtn = (ImageView) mainView.findViewById(R.id.search);
        searchBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                performSearch();
            }
        });
        changeStatueView();
        toRefreshData();
    }


    @Override
    protected void toRefreshData() {
        performHttpTask();
    }
    public void performHttpTask() {
        GetHttpTask getHttpTask = new GetHttpTask();
        tasks.add(getHttpTask);
        getHttpTask.execute();
    }

    public void performSearch() {

        ChangeTask changeTask = new ChangeTask();
        tasks.add(changeTask);
        changeTask.execute();

    }


    private void initActionBar() {

        actionBar.setTitle("费用查询");
        actionBar.setPatient(application.sickPersonVo.XSCH + application.sickPersonVo.BRXM);

    }

    private void changeStatueView() {
        if (current == 1) {
            but1.setSelected(true);
            but2.setSelected(false);
            timeLay.setVisibility(View.GONE);
            viewPager.setCurrentItem(0);
        } else {
            but1.setSelected(false);
            but2.setSelected(true);
            timeLay.setVisibility(View.VISIBLE);
            viewPager.setCurrentItem(1);
        }


    }

    private void initCheckBtns() {

        but1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (current == 1) {
                    return;
                }
                current = 1;
                changeStatueView();

            }
        });
        but2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (current == 2) {
                    return;
                }
                current = 2;
                changeStatueView();
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
                    actionBar.setPatient(mAppApplication.sickPersonVo.XSCH
                            + mAppApplication.sickPersonVo.BRXM);
                    toRefreshData();
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



    void setTotal(ExpenseTotal vo) {
        if (null != vo) {
            ZJJE.setText("总费用:" + vo.ZJJE);
            ZFJE.setText("自负金额:" + vo.ZFJE);
            JKJE.setText("交款金额:" + vo.JKJE);
            FYYE.setText("费用余额:" + vo.FYYE);
        }
    }

    void initTime() {

        String nowDate = DateTimeHelper.getServerDate();
        // 当天
        String eTimeStr = nowDate;
        etime.setText(eTimeStr);

        // 前天
        String startDate= DateTimeHelper.dateAddedDays(nowDate,-1);
        String sTimeStr = startDate;
        stime.setText(sTimeStr);

        sltStimeView.setOnClickListener(onClickListener);
        sltEtimeView.setOnClickListener(onClickListener);
    }

    public OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            String dateStr = null;
            int id = v.getId();
            if (id == R.id.slt_stime_ly) {
                dateStr = stime.getText().toString();
            } else if (id == R.id.slt_etime_ly) {
                dateStr = etime.getText().toString();
            }
            showDatePickerCompat(dateStr, id);

        }
    };

    // private DateSlider.OnDateSetListener sDateTimeSetListener = new
    // DateSlider.OnDateSetListener() {
    // @Override
    // public void onDateSet(DateSlider view, Calendar selectedDate) {
    // sTime = String.format("%tY-%tm-%td", selectedDate, selectedDate,
    // selectedDate);
    // stime.setText(sTime);
    // changeTask = new ChangeTask();
    // changeTask.execute();
    // }
    // };
    //
    // private DateSlider.OnDateSetListener eDateTimeSetListener = new
    // DateSlider.OnDateSetListener() {
    // @Override
    // public void onDateSet(DateSlider view, Calendar selectedDate) {
    // eTime = String.format("%tY-%tm-%td", selectedDate, selectedDate,
    // selectedDate);
    // etime.setText(eTime);
    // changeTask = new ChangeTask();
    // changeTask.execute();
    // }
    // };

    @Override
    public void onDateSet(int year, int monthOfYear, int dayOfMonth, int viewId) {
        String nowDate = DateTimeFactory.getInstance().ymd2Date(year, monthOfYear, dayOfMonth);
        if (viewId == R.id.slt_stime_ly) {
            //选择开始时间
            String endDate = etime.getText().toString();
            boolean after = DateTimeFactory.getInstance().dateAfter(nowDate, endDate);
            if (after) {
                showMsgAndVoiceAndVibrator("开始时间后于结束时间,请重新选择!");
                return;
            }
        } else if (viewId == R.id.slt_etime_ly) {
            //选择结束时间
            String startDate = stime.getText().toString();
            boolean before = DateTimeFactory.getInstance().dateBefore(nowDate, startDate);
            if (before) {
                showMsgAndVoiceAndVibrator("结束时间先于开始时间，请重新选择!");
                return;
            }
        }
        initTimeTxt(nowDate, viewId);
        //选择时间后 自动搜索
        searchBtn.performClick();
    }

    private void initTimeTxt(String dateStr, int viewId) {
        String timeStr = dateStr;
        if (viewId == R.id.slt_etime_ly) {
            etime.setText(timeStr);
        } else if (viewId == R.id.slt_stime_ly) {
            stime.setText(timeStr);
        }

    }

    @SuppressWarnings("unchecked")
    class GetHttpTask extends AsyncTask<Void, Void, Response<ExpenseRespose>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<ExpenseRespose> doInBackground(Void... arg0) {

            if (mAppApplication.sickPersonVo == null) {
                return null;
            }

            String sTime = stime.getText().toString();
            String jgid = mAppApplication.jgId;
            int sysType = Constant.sysType;
            String eTime = etime.getText().toString();
            return ExpenseApi.getInstance(getActivity()).GetCharge(
                    mAppApplication.sickPersonVo.ZYH, sTime, eTime, jgid, sysType);
        }

        @Override
        protected void onPostExecute(Response<ExpenseRespose> result) {
            super.onPostExecute(result);
            tasks.remove(this);
            if (null != adapter1) {
                adapter1.clearData();
            }
            if (null != adapter2) {
                adapter2.clearData();
            }
            hideSwipeRefreshLayout();
            if (result == null) {
                showMsgAndVoiceAndVibrator("加载失败");
                return;
            } else {
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
                    ExpenseTotal expenseTotal = result.Data.Table1;
                    setTotal(expenseTotal);
                    ArrayList<ExpenseVo> list = (ArrayList<ExpenseVo>) result.Data.Table2;
                    if (null != list && list.size() > 0) {
                        adapter1.addData(list);
                    }
                    ArrayList<ExpenseDaysDetail> tList = (ArrayList<ExpenseDaysDetail>) result.Data.Table3;
                    if (null != tList && tList.size() > 0) {
                        adapter2.addData(tList);
                    }
                } else {showTipDialog(result.Msg);
//                    AlertBox.Show(getActivity(), getString(R.string.project_tips), result.Msg, getString(R.string.project_operate_ok));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    class ChangeTask extends AsyncTask<Void, Void, Response<ExpenseRespose>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<ExpenseRespose> doInBackground(Void... arg0) {

            if (mAppApplication.sickPersonVo == null) {
                return null;
            }
            String jgid = mAppApplication.jgId;
            int sysType = Constant.sysType;
            String sTime = stime.getText().toString();
            String eTime = etime.getText().toString();
            return ExpenseApi.getInstance(getActivity()).GetDetailOneDay(
                    mAppApplication.sickPersonVo.ZYH, sTime, eTime, jgid, sysType);
        }

        @Override
        protected void onPostExecute(Response<ExpenseRespose> result) {
            super.onPostExecute(result);
            adapter2.clearData();
            tasks.remove(this);
            hideSwipeRefreshLayout();
            if (result == null) {
                showMsgAndVoiceAndVibrator("加载失败");

                return;
            } else {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            performSearch();
                        }
                    }).showLoginDialog();
                    return;
                }
                if (result.ReType == 0) {
                    ArrayList<ExpenseDaysDetail> tList = (ArrayList<ExpenseDaysDetail>) result.Data.Table3;
                    if (null != tList && tList.size() > 0) {
                        adapter2.addData(tList);
                    }
                } else {showTipDialog(result.Msg);
//                    AlertBox.Show(getActivity(), getString(R.string.project_tips), result.Msg, getString(R.string.project_operate_ok));
                }
            }
        }
    }

    // class RefreshTask extends AsyncTask<String, Void, ParserModel> {
    //
    // @Override
    // protected void onPreExecute() {
    // super.onPreExecute();
    // emptyProgress.setVisibility(View.VISIBLE);
    // }
    //
    // @Override
    // protected ParserModel doInBackground(String... arg0) {
    // return KernelApi.getInstance(getActivity()).GetPatientForScan(
    // arg0[0], arg0[1], application.getAreaId());
    // }
    //
    // @SuppressWarnings("unchecked")
    // @Override
    // protected void onPostExecute(ParserModel result) {
    // super.onPostExecute(result);
    // if (null != result) {
    // if (result.isOK()) {
    // ArrayList<SickPersonVo> tList = result.tableMap
    // .get("Table1");
    // if (null != tList && tList.size() > 0) {
    // application.sickPersonVo = tList.get(0);
    // sendUserName();actionBar.setPatient(mAppApplication.sickPersonVo.BRCH
    //                        + mAppApplication.sickPersonVo.BRXM);
    // performHttpTask();
    // }
    // } else {
    // result.showToast(getActivity());
    // }
    // } else {
    //     showMsgAndVoice("加载失败");
    // .show();
    // }
    // emptyProgress.setVisibility(View.GONE);
    // }
    //
    // }
}
