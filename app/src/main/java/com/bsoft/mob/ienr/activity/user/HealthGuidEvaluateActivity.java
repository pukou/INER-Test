/**
 * @Title: HealthGuidCustomActivity.java
 * @Package com.bsoft.mob.ienr.activity.user
 * @Description: 健康教育自定义项目操作页类文件
 * @author 田孝鸣 tianxm@bsoft.com.cn
 * @date 2015-12-07 上午9:30:04
 * @version V1.0
 */
package com.bsoft.mob.ienr.activity.user;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.activity.user.adapter.HealthGuidDetailListAdapter;
import com.bsoft.mob.ienr.activity.user.adapter.HealthGuidOperListAdapter;
import com.bsoft.mob.ienr.activity.user.adapter.HealthGuidTypeListAdapter;
import com.bsoft.mob.ienr.adapter.ViewPagerAdapter;
import com.bsoft.mob.ienr.api.HealthGuidApi;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.healthguid.HealthGuidDetail;
import com.bsoft.mob.ienr.model.healthguid.HealthGuidEvaluateData;
import com.bsoft.mob.ienr.model.healthguid.HealthGuidOper;
import com.bsoft.mob.ienr.model.healthguid.HealthGuidOperItem;
import com.bsoft.mob.ienr.model.healthguid.HealthGuidType;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 田孝鸣 tianxm@bsoft.com.cn
 * @ClassName: HealthGuidCustomActivity
 * @Description: 健康教育自定义项目操作页Activity
 * @date 2015-12-07 上午9:30:04
 */
public class HealthGuidEvaluateActivity extends BaseBarcodeActivity {

    private Button bt_yes;
    private Button bt_no;
    private TextView timePageView;

    private String operType;
    private String type;
    private String xh;
    private String lxbh;

    private HealthGuidOperListAdapter mAdapterDefaultOper;
    private HealthGuidTypeListAdapter mAdapterType;
    private HealthGuidDetailListAdapter mAdapterDetail;
    private List<HealthGuidOper> mListOper;
    private List<HealthGuidDetail> mListDetail;
    private ListView listViewHealthGuidType;
    private ListView listViewHealthGuidDetail;
    private ListView defaultListView;
    private ViewPager pager;
    private TextView iPageName;

    // 当前宣教类型名称
    private String nowHealthGuidType;

    private HealthGuidEvaluateData healthGuidEvaluateData;

    @Override
    public void initBarBroadcast() {


    }


    /**
     * @param @param view
     * @return void
     * @throws
     * @Title: initView
     * @Description: 初始化界面
     */
    private void initView() {
        defaultListView = (ListView) findViewById(R.id.id_lv);
        pager = (ViewPager) findViewById(R.id.id_vp);

        bt_yes = (Button) findViewById(R.id.id_btn_date_ok);
        bt_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        bt_no = (Button) findViewById(R.id.id_btn_date_cancel);
        bt_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HealthGuidEvaluateActivity.this.setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    /**
     * @param
     * @return void 返回类型
     * @throws
     * @Title: initPager
     * @Description: 初始化滑动页
     */
    private void initPager() {
        ArrayList<View> pagelist = new ArrayList<View>();
        LayoutInflater mInflater = getLayoutInflater();
        View cView = mInflater
                .inflate(R.layout.layout_health_guid_cpage, null);
        View iView = mInflater
                .inflate(R.layout.layout_health_guid_ipage, null);
        pagelist.add(cView);
        pagelist.add(iView);
        pager.setAdapter(new ViewPagerAdapter(pagelist));
        pager.setCurrentItem(pagelist.size() - 1);// 选择最后一个view
        pager.setOnPageChangeListener(new PageChangeListener());

        if (healthGuidEvaluateData.GLLX.equals("2")) {
            cView.setVisibility(View.GONE);
        }

        listViewHealthGuidType = (ListView) cView
                .findViewById(R.id.id_lv);
        listViewHealthGuidDetail = (ListView) iView
                .findViewById(R.id.id_lv);
        iPageName = (TextView) iView.findViewById(R.id.healthguid_ipage_name);
        toRefreshData();
    }


    /**
     * @param
     * @return void
     * @throws
     * @Title: initActionBar
     * @Description: 初始化工具条
     */
    private void initActionBar() {
        actionBar.setTitle("独立评价");
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_health_guid_evaluate;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {


        Intent intent = getIntent();
        operType = intent.getStringExtra("operType");//回传参数
        type = intent.getStringExtra("type");//所需参数
        xh = intent.getStringExtra("xh");//所需参数
        lxbh = intent.getStringExtra("lxbh");//所需参数

        healthGuidEvaluateData = new HealthGuidEvaluateData();

        initView();
        initActionBar();
        initTimePageView();
        initPager();
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: saveData
     * @Description: 保存数据
     */
    private void saveData() {
        SaveDataTast task = new SaveDataTast();
        tasks.add(task);
        task.execute();
    }

    class SaveDataTast extends AsyncTask<Void, Void, Response<String>> {

        /*
         * (非 Javadoc) <p>Title: onPreExecute</p> <p>Description: 网络请求前</p>
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            showLoadingDialog(R.string.saveing);
        }

        /*
         * (非 Javadoc) <p>Title: doInBackground</p> <p>Description:执行网络请求 </p>
         *
         * @param params
         *
         * @return
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Response<String> doInBackground(Void... params) {

            String data = "";
            try {

                //====
                //由于mListOper是深拷贝后等到的list
                // 在 checkbox监听改变的ISCHECK 后  healthGuidEvaluateData 里面没改
                //为了 不影响 原有逻辑  在提交的时候  遍历 赋值ISCHECK
                //add by louis todo 优化 2017-5-23 10:20:00
                if (healthGuidEvaluateData != null && healthGuidEvaluateData.HealthGuidTypes != null && healthGuidEvaluateData.HealthGuidTypes.size() > 0) {
                    for (HealthGuidType healthGuidType :
                            healthGuidEvaluateData.HealthGuidTypes) {
                        if (healthGuidType == null || !healthGuidType.ISCHECK.equals("1")) {
                            continue;
                        }
                        for (HealthGuidDetail healthGuidDetail :
                                healthGuidType.HealthGuidDetails) {
                            if (healthGuidDetail == null || !healthGuidDetail.ISCHECK.equals("1")) {
                                continue;
                            }
                            if (healthGuidDetail.HealthGuidOpers.get(0) != null) {
                                //web 端也是 直接get(0) 2017-5-23 10:40:32
                                List<HealthGuidOperItem> healthGuidOperItems = healthGuidDetail.HealthGuidOpers.get(0).HealthGuidOperItems;
                                if (healthGuidOperItems != null && healthGuidOperItems.size() > 0) {
                                    for (HealthGuidOperItem itemOut : healthGuidOperItems) {
                                        if (itemOut != null && mListOper != null && mListOper.size() > 0 && healthGuidEvaluateData != null) {
                                            for (HealthGuidOper oper : mListOper) {
                                                if (oper != null && oper.HealthGuidOperItems != null && oper.HealthGuidOperItems.size() > 0) {
                                                    for (HealthGuidOperItem item :
                                                            oper.HealthGuidOperItems) {
                                                        if (item != null && item.XH != null && item.XH.equals(itemOut.XH)
                                                                && item.MS != null && item.MS.equals(itemOut.MS)
                                                                ) {
                                                            itemOut.ISCHECK = item.ISCHECK;
                                                            break;
                                                        }
                                                    }
                                                }
                                            }
                                            //
                                        }
                                        //
                                    }
                                }
                            }
                        }
                        //
                    }
                }


                //====
                data = JsonUtil.toJson(healthGuidEvaluateData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return HealthGuidApi.getInstance(HealthGuidEvaluateActivity.this)
                    .SaveHealthGuidEvaluateDataPost(data);
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description:网络请求后 </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Response<String> result) {
            hideLoadingDialog();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(HealthGuidEvaluateActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            saveData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    Intent intent = new Intent();
                    intent.putExtra("operType", operType);
                    intent.putExtra("type", type);
                    intent.putExtra("lxbh", lxbh);
                    intent.putExtra("xh", xh);
                    HealthGuidEvaluateActivity.this.setResult(RESULT_OK, intent);
                    finish();

                } else {
                    showMsgAndVoice(result.Msg);
                    /*MediaUtil.getInstance(HealthGuidEvaluateActivity.this).playSound(
                            R.raw.wrong, HealthGuidEvaluateActivity.this);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }


    @Override
    protected void toRefreshData() {
        GetDataTast task = new GetDataTast();
        tasks.add(task);
        task.execute();
    }

    class GetDataTast extends AsyncTask<Void, Void, Response<HealthGuidEvaluateData>> {

        /*
         * (非 Javadoc) <p>Title: onPreExecute</p> <p>Description: 网络请求前</p>
         *
         * @see android.os.AsyncTask#onPreExecute()
         */
        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        /*
         * (非 Javadoc) <p>Title: doInBackground</p> <p>Description:执行网络请求 </p>
         *
         * @param params
         *
         * @return
         *
         * @see android.os.AsyncTask#doInBackground(Params[])
         */
        @Override
        protected Response<HealthGuidEvaluateData> doInBackground(Void... params) {

            return HealthGuidApi.getInstance(HealthGuidEvaluateActivity.this)
                    .GetHealthGuidEvaluateData(lxbh, xh,
                            type, mAppApplication.jgId);
        }

        /*
         * (非 Javadoc) <p>Title: onPostExecute</p> <p>Description:网络请求后 </p>
         *
         * @param result
         *
         * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
         */
        @Override
        protected void onPostExecute(Response<HealthGuidEvaluateData> result) {
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(HealthGuidEvaluateActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            toRefreshData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    realGetData(result.Data);
                } else {
                    showMsgAndVoice(result.Msg);
                  /*  MediaUtil.getInstance(HealthGuidEvaluateActivity.this).playSound(
                            R.raw.wrong, HealthGuidEvaluateActivity.this);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    private void realGetData(HealthGuidEvaluateData par) {
        healthGuidEvaluateData = par;
        //  add by louis 2017-5-23 11:29:50
        //处理 当健康教育什么都不选保存后  去评价 直接崩溃
        if (healthGuidEvaluateData != null && healthGuidEvaluateData.HealthGuidTypes != null
                && healthGuidEvaluateData.HealthGuidTypes.size() > 0) {
            //change  todo 优化

            mListDetail = healthGuidEvaluateData.HealthGuidTypes.get(0).HealthGuidDetails;
            nowHealthGuidType = healthGuidEvaluateData.HealthGuidTypes.get(0).MS;
            if (mListDetail != null
                    && mListDetail.size() > 0) {
                mListOper = deepCopyList(mListDetail.get(0).HealthGuidOpers);
            }

            for (int i = 0; i < healthGuidEvaluateData.HealthGuidTypes.size(); i++) {
                HealthGuidType healthGuidType = healthGuidEvaluateData.HealthGuidTypes
                        .get(i);
                if (healthGuidType.ISCHECK.equals("1")) {
                    for (int j = 0; j < healthGuidType.HealthGuidDetails
                            .size(); j++) {
                        if (healthGuidType.HealthGuidDetails.get(j).ISCHECK
                                .equals("1")) {
                            mListDetail = healthGuidType.HealthGuidDetails;
                            mListOper = deepCopyList(healthGuidType.HealthGuidDetails
                                    .get(j).HealthGuidOpers);
                            nowHealthGuidType = healthGuidType.MS;
                            break;
                        }
                    }
                    break;
                }
            }
            //

            setMAdapterType();
            setMAdapterDetail(true);

            setMAdaperDefaultOper();
            //
        }
    }

    private void setMAdapterType() {
        mAdapterType = new HealthGuidTypeListAdapter(HealthGuidEvaluateActivity.this,
                healthGuidEvaluateData.HealthGuidTypes);
        listViewHealthGuidType.setAdapter(mAdapterType);
        listViewHealthGuidType
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        String isCheck = healthGuidEvaluateData.HealthGuidTypes
                                .get(position).ISCHECK;
                        if (isCheck.equals("1")) {
                            int count = 0;
                            for (int i = 0; i < healthGuidEvaluateData.HealthGuidTypes.size(); i++) {
                                if (healthGuidEvaluateData.HealthGuidTypes.get(i).ISCHECK.equals("1")) {
                                    count++;
                                }
                            }
                            //保证至少选中一个类别
                            if (count > 1) {
                                healthGuidEvaluateData.HealthGuidTypes.get(position).ISCHECK = "0";
                            }
                            int checkPostion = 0;
                            for (int i = 0; i < healthGuidEvaluateData.HealthGuidTypes.size(); i++) {
                                if (healthGuidEvaluateData.HealthGuidTypes.get(i).ISCHECK.equals("1")) {
                                    //有多个类别被选中时：只记录最前面一个被选中的类别
                                    checkPostion = i;
                                    break;
                                }
                            }
                            nowHealthGuidType = healthGuidEvaluateData.HealthGuidTypes
                                    .get(checkPostion).MS;
                            mListDetail = new ArrayList<HealthGuidDetail>();
                            mListDetail = healthGuidEvaluateData.HealthGuidTypes
                                    .get(checkPostion).HealthGuidDetails;
                            pager.setCurrentItem(1);// 选择第二个view（宣教项目）
                            setMAdapterDetail(false);
                        } else {
                            nowHealthGuidType = healthGuidEvaluateData.HealthGuidTypes
                                    .get(position).MS;
                            healthGuidEvaluateData.HealthGuidTypes.get(position).ISCHECK = "1";

                            mListDetail = new ArrayList<HealthGuidDetail>();
                            mListDetail = healthGuidEvaluateData.HealthGuidTypes
                                    .get(position).HealthGuidDetails;
                            pager.setCurrentItem(1);// 选择第二个view（宣教项目）
                            setMAdapterDetail(false);
                        }
                        mAdapterType.notifyDataSetChanged();
                    }
                });
    }

    private void setMAdapterDetail(boolean addItemClick) {
        if (EmptyTool.isBlank(nowHealthGuidType)) {
            iPageName.setText("<<宣教项目");
            iPageName.setTextSize(20);
        } else {
            iPageName.setText("<<宣教项目-" + nowHealthGuidType);
            iPageName.setTextSize(18);
        }
        mAdapterDetail = new HealthGuidDetailListAdapter(
                HealthGuidEvaluateActivity.this, mListDetail);
        listViewHealthGuidDetail.setAdapter(mAdapterDetail);
        if (addItemClick) {
            listViewHealthGuidDetail
                    .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent,
                                                View view, int position, long id) {
                            if (mListDetail.get(position).ISOPER.equals("1")) {
                                return;
                            }
                            if (mListDetail.get(position).ISCHECK.equals("1")) {
                                if (mListDetail.get(position).ZDYBZ.equals("1")
                                        || mListDetail.get(position).DLBZ
                                        .equals("1")) {
                                    mListDetail.get(position).ISCHECK = "0";
                                } else {
                                    for (int i = 0; i < mListDetail.size(); i++) {
                                        if (mListDetail.get(i).XMZH
                                                .equals(mListDetail
                                                        .get(position).XMZH)) {
                                            mListDetail.get(i).ISCHECK = "0";
                                        }
                                    }
                                }
                            } else {
                                mListDetail.get(position).HealthGuidOpers = deepCopyList(mListOper);

                                if (mListDetail.get(position).ZDYBZ.equals("1")
                                        || mListDetail.get(position).DLBZ
                                        .equals("1")) {
                                    mListDetail.get(position).ISCHECK = "1";
                                    mListDetail.get(position).XJSJ = timePageView.getText().toString();
                                    mListDetail.get(position).XJGH = mAppApplication.user.YHID;
                                    mListDetail.get(position).PJSJ = timePageView.getText().toString();
                                    mListDetail.get(position).PJGH = mAppApplication.user.YHID;
                                } else {
                                    for (int i = 0; i < mListDetail.size(); i++) {
                                        if (mListDetail.get(i).XMZH
                                                .equals(mListDetail
                                                        .get(position).XMZH)) {
                                            mListDetail.get(i).ISCHECK = "1";
                                            mListDetail.get(i).XJSJ = timePageView.getText().toString();
                                            mListDetail.get(i).XJGH = mAppApplication.user.YHID;
                                            mListDetail.get(i).PJSJ = timePageView.getText().toString();
                                            mListDetail.get(i).PJGH = mAppApplication.user.YHID;
                                            mListDetail.get(i).HealthGuidOpers = mListDetail.get(position).HealthGuidOpers;
                                        }
                                    }
                                }
                            }
                            mAdapterDetail.notifyDataSetChanged();
                        }
                    });
        }
    }

    private void setMAdaperDefaultOper() {
        mAdapterDefaultOper = new HealthGuidOperListAdapter(
                HealthGuidEvaluateActivity.this, mListOper);
        defaultListView.setAdapter(mAdapterDefaultOper);
    }

    private void initTimePageView() {

        timePageView = (TextView) findViewById(R.id.id_tv_2);
//        timePageView = (TextView) findViewById(R.id.healthguid_datetime_txt);
        timePageView.setOnClickListener(onClickListener);

        String yyyyMMddHHmm = DateTimeHelper.getServer_yyyyMMddHHmm00();
        initTimeTxt(yyyyMMddHHmm);
    }

    private void initTimeTxt(String yyyyMMddHHmm) {
        timePageView.setText(yyyyMMddHHmm);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            int viewId = v.getId();

            String dateStr = timePageView.getText().toString();
            if (EmptyTool.isBlank(dateStr)) {
                return;
            }
            // 导入年月数据
            YmdHMs ymdHMs = DateTimeHelper.dateTime2YmdHMs(dateStr);
            showPickerDateCompat(ymdHMs, viewId);
        }
    };

    @Override
    public void onDateTimeSet(int year, int month, int dayOfMonth,
                              int hourOfDay, int minute, int viewId) {

        String dateTime = DateTimeFactory.getInstance().ymdhms2DateTime(year, month, dayOfMonth, hourOfDay, minute, 0);
        initTimeTxt(dateTime);
    }

    ;

    /**
     * 将templatesList中的数据深度copy到targetList中
     *
     * @param templatesList 模版数组
     */
    private ArrayList<HealthGuidOper> deepCopyList(List<HealthGuidOper> templatesList) {
        ArrayList<HealthGuidOper> targetList = new ArrayList<HealthGuidOper>();
        if (templatesList == null) {
            return targetList;
        }
        try {
            for (int i = 0; i < templatesList.size(); i++) {
                targetList.add(templatesList.get(i).DeepClone());
            }
        } catch (IOException io) {
            showMsgAndVoice(io.getMessage());
        } catch (ClassNotFoundException nf) {
            showMsgAndVoice(nf.getMessage());
        }
        return targetList;
    }

    private class PageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {
            if (healthGuidEvaluateData.GLLX.equals("2")) {
                pager.setCurrentItem(1);// 选择最后一个view
            }
        }

    }


}
