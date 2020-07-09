package com.bsoft.mob.ienr.activity.user;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseActivity;
import com.bsoft.mob.ienr.activity.user.adapter.TradOneRVAdapter;
import com.bsoft.mob.ienr.activity.user.adapter.TradTwoListAdapter;
import com.bsoft.mob.ienr.adapter.MyPagerAdapter;
import com.bsoft.mob.ienr.api.TradApi;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.trad.JSJL;
import com.bsoft.mob.ienr.model.trad.SHFF_HLJS;
import com.bsoft.mob.ienr.model.trad._HLJS;
import com.bsoft.mob.ienr.model.trad._SHFF;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.bsoft.mob.ienr.R.id.id_lv;

public class TraditionalNursingActivity extends BaseActivity {

    private int type = 1;
    private TextView id_tv;
    private TextView id_tv_2;
    private RecyclerView id_rv;
    private ListView id_lv_2;
    private ViewPager viewPager;
    private boolean isAllCanEdit;
    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_traditional_nursing;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        isAllCanEdit=getIntent().getBooleanExtra("isAllCanEdit",true);

        actionBar.addAction(new BsoftActionBar.Action() {
            @Override
            public int getDrawable() {
                return R.drawable.ic_check_black_24dp;
            }
            @Override
            public String getText() {
                return "保存";
            }
            @Override
            public void performAction(View view) {
                actionSave();
            }
        });
        actionBar.setTitle("中医护理明细");
        actionBar.setPatient(mAppApplication.sickPersonVo.XSCH + mAppApplication.sickPersonVo.BRXM);
        id_tv = (TextView) findViewById(R.id.id_tv);
        id_tv.setText("施护方法");
        id_tv_2 = (TextView) findViewById(R.id.id_tv_2);
        id_tv_2.setText("护理技术");
    /*    id_ll = (LinearLayout) findViewById(R.id.id_ll);
        id_ll_2 = (LinearLayout) findViewById(R.id.id_ll_2);
*/
        viewPager = (ViewPager) findViewById(R.id.id_vp);
        List<View> viewList = new ArrayList<>();
        //### View view = LayoutInflater.from(mContext).inflate(R.layout.layout_view_pager_recycler, null, false);
        View view2 = LayoutInflater.from(mContext).inflate(R.layout.layout_view_pager_list_spinner, null, false);
        id_rv = ViewBuildHelper.buildRecyclerView(mContext);
        id_lv_2 = view2.findViewById(id_lv);
        viewList.add(id_rv);
        viewList.add(view2);
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(viewList);
        viewPager.setAdapter(myPagerAdapter);

        //###  EmptyViewHelper.setEmptyView(id_rv,"id_rv");
        //   SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout,id_rv);
        EmptyViewHelper.setEmptyView(id_lv_2, "id_lv_2");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, id_lv_2);

        SpinnerLayout id_spinner_layout = (SpinnerLayout) view2.findViewById(R.id.id_spinner_layout);
        Spinner spinner = id_spinner_layout.getSpinner();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext,
                android.R.layout.simple_spinner_item);
        arrayAdapter.add("全部未执行");
        arrayAdapter.add("当日未执行");
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                //
                fiterData(position);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        initCheckable();//初始化
        //

        mSHFF_HLJS = new SHFF_HLJS();
        mSHFF_HLJS.shffList = new ArrayList<>();
        mSHFF_HLJS.hljsList = new ArrayList<>();
        tradOneRVAdapter = new TradOneRVAdapter(mContext, mSHFF_HLJS.shffList,isAllCanEdit);
        tradOneRVAdapter.setOnInfoClickListener(new TradOneRVAdapter.OnInfoClickListener() {
            @Override
            public void onInfoClick(View view, String text) {
                //
                showTipDialog(text);
            }
        });
        id_rv.setAdapter(tradOneRVAdapter);
        tradTwoListAdapter = new TradTwoListAdapter(mContext, mSHFF_HLJS.hljsList,isAllCanEdit);
        id_lv_2.setAdapter(tradTwoListAdapter);

        // 显示和和隐藏相关的控件
        id_tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                type = 1;
                initCheckable();
            }
        });
        id_tv_2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                type = 2;
                initCheckable();
            }
        });
        toRefreshData();
    }

    @Override
    protected void toRefreshData() {
        HttpTask ht = new HttpTask();
        tasks.add(ht);
        ht.execute();
    }

    private void fiterData(int position) {
        if (position == 0) {
            tradTwoListAdapter.refreshData(mSHFF_HLJS.hljsList);
        } else if (position == 1) {
            List<_HLJS> hljsFiterList = new ArrayList<>();
            for (_HLJS hljs : mSHFF_HLJS.hljsList) {
                if (!EmptyTool.isBlank(hljs.JHRQ)) {
                    //
                    String nowDay = DateTimeHelper.getServerDate();
                    if (hljs.JHRQ.startsWith(nowDay)) {
                        hljsFiterList.add(hljs);
                    }
                    /*
                    String nextDay = Format.getNextDayDate();
                    if (Format.timeCompare(hljs.JHRQ, nowDay) >= 0 && Format.timeCompare(hljs.JHRQ, nextDay) <= 0) {
                        hljsFiterList.add(hljs);
                    }*/
                }
            }
            tradTwoListAdapter.refreshData(hljsFiterList);
        }
    }

    private SHFF_HLJS mSHFF_HLJS;

    private void actionSave() {

        if (type == 1) {
            //
            List<_SHFF> shffBackList = new ArrayList<>();
            for (_SHFF shff : mSHFF_HLJS.shffList) {
                List<_SHFF.SHFF_Check> checkedBackList = new ArrayList<>();
                for (_SHFF.SHFF_Check sHFF_Check : shff.shffCheckList) {
                    if ("1".equals(sHFF_Check.status)) {
                        checkedBackList.add(sHFF_Check);
                    }
                }
                if (!checkedBackList.isEmpty()) {
                    _SHFF shffNew = new _SHFF();
                    shffNew.code = shff.code;
                    shffNew.name = shff.name;
                    shffNew.ZZBH = shff.ZZBH;
                    shffNew.ZZMC = shff.ZZMC;
                    shffNew.FAJL = shff.FAJL;
                    shffNew.ZZJL = shff.ZZJL;
                    shffNew.shffCheckList = new ArrayList<>(checkedBackList);
                    shffBackList.add(shffNew);
                }
            }
            if (shffBackList.isEmpty()) {
                showMsgAndVoiceAndVibrator("没有需要保存的数据");
                return;
            }
            List<JSJL> jsjlList_shff = new ArrayList<>();
            for (_SHFF shffBack : shffBackList) {
                for (_SHFF.SHFF_Check shff_check : shffBack.shffCheckList) {
                    JSJL jsjl = new JSJL();
                    jsjl.FAJL = shffBack.FAJL;
                    jsjl.ZZJL = shffBack.ZZJL;
                    jsjl.ZZBH = shffBack.ZZBH;
                    jsjl.ZZMC = shffBack.ZZMC;
                    jsjl.JSBH = shff_check.JSBH;//
                    jsjl.FFMC = shff_check.name;//FFMC可以自定义 用name 详见adapter中
                    jsjl.XMLB = shff_check.XMLB;
                    jsjl.ZXZT = "1";
                   //服务端设置 jsjl.CZSJ = Format.getNowDayDateTime();
                    jsjl.CZGH = mAppApplication.user.YHID;
                    //服务端设置 jsjl.CZRQ = Format.getNowDayDate();
                    jsjl.GLLX = null;
                    jsjl.GLJL = null;
                    jsjl.ZDYBZ = shff_check.XGBZ;
                    jsjl.JCXMH = shff_check.JCXMH;
                    //
                    jsjlList_shff.add(jsjl);
                }


            }


            try {
                String json = JsonUtil.toJson(jsjlList_shff);
                Log.i(Constant.TAG_COMM, "actionSave: json:" + json);
                actionSaveHttpTask(type, json);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            //
            List<_HLJS> hljsBackList = new ArrayList<>();
            for (_HLJS hljs : mSHFF_HLJS.hljsList) {
                if ("1".equals(hljs.status)) {
                    hljsBackList.add(hljs);
                }
            }
            if (hljsBackList.isEmpty()) {
                showMsgAndVoiceAndVibrator("没有需要保存的数据");
                return;
            }
            List<JSJL> jsjlList_hljs = new ArrayList<>();
            for (_HLJS hljs : hljsBackList) {
                JSJL jsjl = new JSJL();
                jsjl.FAJL = hljs.FAJL;
                jsjl.ZZJL = hljs.ZZJL;
                jsjl.JSBH = hljs.JSBH;
                jsjl.FFMC = hljs.FFMC;
                jsjl.XMLB = hljs.XMLB;
                jsjl.ZXZT = "1";
                //服务端设置 jsjl.CZSJ = Format.getNowDayDateTime();
                jsjl.CZGH = mAppApplication.user.YHID;
                //服务端设置  jsjl.CZRQ = Format.getNowDayDate();
                jsjl.GLLX = hljs.GLLX;
                jsjl.GLJL = "1".equals(hljs.GLLX) ? hljs.JHH : null;
                jsjl.ZDYBZ = hljs.XGBZ;
                jsjl.JCXMH = hljs.JCXMH;
                //
                jsjl.ZYH = mAppApplication.sickPersonVo.ZYH;
                jsjl.JGID = mAppApplication.jgId;
                jsjl.JHH = hljs.JHH;
                //

                jsjlList_hljs.add(jsjl);
            }
            try {
                String json = JsonUtil.toJson(jsjlList_hljs);
                Log.i(Constant.TAG_COMM, "actionSave: json:" + json);
                actionSaveHttpTask(type, json);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private TradOneRVAdapter tradOneRVAdapter;
    private TradTwoListAdapter tradTwoListAdapter;


    private void actionSaveHttpTask(int type, String json) {
        SaveHttpTask ht = new SaveHttpTask(type);
        tasks.add(ht);
        ht.execute(json);
    }

    class HttpTask extends AsyncTask<String, Integer, Response<SHFF_HLJS>> {


        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<SHFF_HLJS> doInBackground(String... params) {

            TradApi api = TradApi.getInstance(mContext);
            String zyh = mAppApplication.sickPersonVo.ZYH;
            String brbq = mAppApplication.getAreaId();
            String jgid = mAppApplication.jgId;
            return api.getSHFF_HLJS(zyh, brbq, jgid);
        }

        @Override
        protected void onPostExecute(Response<SHFF_HLJS> result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(mContext, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            toRefreshData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    //
                    parseData(result.Data);
                } else {
                    showMsgAndVoice(result.Msg);
                   /* MediaUtil.getInstance(mContext).playSound(
                            R.raw.wrong, mContext);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }


    class SaveHttpTask extends AsyncTask<String, Integer, Response<String>> {
        int type = 1;
        String json;

        public SaveHttpTask(int type) {
            this.type = type;
        }

        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.saveing);
        }

        @Override
        protected Response<String> doInBackground(String... params) {

            TradApi api = TradApi.getInstance(mContext);
            json = params[0];
            String zyh = mAppApplication.sickPersonVo.ZYH;
            String brbq = mAppApplication.getAreaId();
            String jgid = mAppApplication.jgId;
            if (type == 1) {
                return api.saveSHFF(json);
            } else {
                return api.saveHLJS(json);
            }

        }

        @Override
        protected void onPostExecute(Response<String> result) {
            hideLoadingDialog();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(mContext, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            actionSaveHttpTask(type, json);
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    //
                    //parseData(result.Data);
                    showMsgAndVoice("执行成功");
                    //重新加载
                    toRefreshData();
                } else {
                    showMsgAndVoice(result.Msg);
                   /* MediaUtil.getInstance(mContext).playSound(
                            R.raw.wrong, mContext);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    private void parseData(SHFF_HLJS data) {
        mSHFF_HLJS = data;
        tradOneRVAdapter.refreshData(data.shffList);
        //
        tradTwoListAdapter.refreshData(data.hljsList);
        initCheckable();
    }


    private void initCheckable() {
        if (type == 1) {
            id_tv.setSelected(true);
            id_tv_2.setSelected(false);

            viewPager.setCurrentItem(0);
            Log.i("zzffqq", "initCheckable: left");
        } else {
            id_tv.setSelected(false);
            id_tv_2.setSelected(true);
            viewPager.setCurrentItem(1);
            Log.i("zzffqq", "initCheckable: right");
        }
    }
}
