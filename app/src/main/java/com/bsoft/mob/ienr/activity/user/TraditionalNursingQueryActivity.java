package com.bsoft.mob.ienr.activity.user;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseActivity;
import com.bsoft.mob.ienr.activity.user.adapter.TradRecordListAdapter;
import com.bsoft.mob.ienr.api.TradApi;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.trad.JSJL;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;

import java.util.ArrayList;
import java.util.List;

public class TraditionalNursingQueryActivity extends BaseActivity {

    private ListView id_lv;
    private TradRecordListAdapter tradRecordListAdapter;

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_traditional_nursing_query;
    }

    @Override
    protected void toRefreshData() {
        HttpTask ht = new HttpTask();
        tasks.add(ht);
        ht.execute();
    }


    class HttpTask extends AsyncTask<String, Integer, Response<List<JSJL>>> {


        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<JSJL>> doInBackground(String... params) {

            TradApi api = TradApi.getInstance(mContext);
            String zyh = mAppApplication.sickPersonVo.ZYH;
//            String brbq = mAppApplication.getAreaId();
            String jgid = mAppApplication.jgId;
            return api.getZYSHJSJL(zyh, jgid);
        }

        @Override
        protected void onPostExecute(Response<List<JSJL>> result) {

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
                } else if (result.ReType == 0) {

                    parseData(result.Data);

                } else {
                    showMsgAndVoice(result.Msg);
                   /* MediaUtil.getInstance(mContext).playSound(
                            R.raw.wrong, mContext);*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
            }
        }
    }

    private void parseData(List<JSJL> data) {
        listRaw = new ArrayList<>(data);
        tradRecordListAdapter.refreshData(data);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        actionBar.setTitle("记录查询");
        id_lv = (ListView) findViewById(R.id.id_lv);

        SpinnerLayout id_spinner_layout = (SpinnerLayout) findViewById(R.id.id_spinner_layout);
        Spinner spinner = id_spinner_layout.getSpinner();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(mContext,
                android.R.layout.simple_spinner_item);
        arrayAdapter.add("全部");
        arrayAdapter.add("施护方法");
        arrayAdapter.add("护理技术");
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (position == 0) {
                    fiterData("0");
                } else if (position == 1) {
                    fiterData("1");
                } else {
                    fiterData("2");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        View headView = LayoutInflater.from(mContext).inflate(
                R.layout.item_list_text_five_primary, null,false);
        id_lv.addHeaderView(headView);

        List<JSJL> list = new ArrayList<>();
        tradRecordListAdapter = new TradRecordListAdapter(mContext, list);
        id_lv.setAdapter(tradRecordListAdapter);
        EmptyViewHelper.setEmptyView(id_lv, "id_lv");
        id_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //adapterView.getAdapter().getItem(pos); 针对headerview的list也正确

            }
        });

        toRefreshData();
    }

    private List<JSJL> listRaw;

    private void fiterData(String type) {
        if (listRaw == null || listRaw.isEmpty()) {
            return;
        }
        List<JSJL> listNew = new ArrayList<>();
        switch (type) {
            case "0":
                listNew.addAll(listRaw);
                break;
            case "1":
                for (JSJL traditional_shjs : listRaw) {
                    if (type.equals(traditional_shjs.XMLB)) {
                        listNew.add(traditional_shjs);
                    }
                }
                break;
            case "2":
                for (JSJL traditional_shjs : listRaw) {
                    if (type.equals(traditional_shjs.XMLB)) {
                        listNew.add(traditional_shjs);
                    }
                }
                break;
            default:
        }
        tradRecordListAdapter.refreshData(listNew);
    }
}
