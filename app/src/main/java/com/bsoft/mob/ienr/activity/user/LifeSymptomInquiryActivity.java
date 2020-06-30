package com.bsoft.mob.ienr.activity.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.adapter.LifeSymptomInquiryAdapter;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.api.LifeSignApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.LayoutParamsHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignHistoryData;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignHistoryDataItem;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignHistoryDataType;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.DensityUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-12-13 上午11:37:00
 * @类说明 体征查询
 */
public class LifeSymptomInquiryActivity extends BaseBarcodeActivity {

    protected static final int DIALOG_DELETE = 0;
    protected static final int DIALOG_UPDATE = 1;

    private ListView mPullToRefreshListView;
    private ListView pullListView;

    private ArrayList<LifeSignHistoryDataItem> list;
    private ArrayList<LifeSignHistoryDataType> dList;

    private LifeSymptomInquiryAdapter adapter;
    private PullAdapter pullAdapter;

    private TextView sTimeTxt;
    private TextView eTimeTxt;

    private View sltStimeView;
    private View sltEtimeView;

    private int currentType = 0;

    // actionBar
    private LinearLayout leftLayout;
    private TextView textname;
    private PopupWindow pop;
    // actionBar

    private ImageView searchBtn;


    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_life_symptom_inquiry;
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

        findView();
        actionBar.setTitle(getString(R.string.lsi_title));
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
                finish();
            }
        });
        toRefreshData();
    }

    private void initSearchBtn() {

        searchBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toRefreshData();
            }
        });
    }


    public void actionDeleteTask(int position, Integer... params) {
        DeleteDataTask task = new DeleteDataTask(position);
        tasks.add(task);
        task.execute(params);
    }

    public void actionChangeTask(Integer... params) {
        ChangeDataTask task = new ChangeDataTask();
        tasks.add(task);
        task.execute(params);
    }

    public void actionUpdateTask(int position, String value, Integer... params) {
        UpdateDataTask task = new UpdateDataTask(position, value);
        tasks.add(task);
        task.execute(params);
    }

    public void findView() {

        searchBtn = (ImageView) findViewById(R.id.search);

        sTimeTxt = (TextView) findViewById(R.id.stime);
        eTimeTxt = (TextView) findViewById(R.id.etime);
        sltStimeView = findViewById(R.id.slt_stime_ly);
        sltEtimeView = findViewById(R.id.slt_etime_ly);

        final TextView stimeTitle = (TextView) sltStimeView
                .findViewById(R.id.stime_title);
        final TextView etimeTitle = (TextView) sltEtimeView
                .findViewById(R.id.etime_title);

        stimeTitle.setText(R.string.start_time);
        etimeTitle.setText(R.string.end_time);

        mPullToRefreshListView = (ListView) findViewById(R.id.id_lv);

        EmptyViewHelper.setEmptyView(mPullToRefreshListView, "mPullToRefreshListView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mPullToRefreshListView);
        //
        leftLayout = (LinearLayout) findViewById(R.id.leftLayout);
        leftLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });


        textname = (TextView) findViewById(R.id.textname);
        textname.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (null == pop) {
                    pop = new PopupWindow(pullListView, DensityUtil.dp2px(
                            LifeSymptomInquiryActivity.this, 80),
                            android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                    pop.showAsDropDown(textname);
                } else {
                    if (pop.isShowing()) {
                        pop.dismiss();
                    } else {
                        pop.showAsDropDown(textname);
                    }
                }
            }
        });

        initTime();
        initListView();
        initSearchBtn();
    }

    @Override
    protected void toRefreshData() {
        GetDataTask getDataTask = new GetDataTask();
        tasks.add(getDataTask);
        getDataTask.execute();
    }

    private void initListView() {

        adapter = new LifeSymptomInquiryAdapter(this);
        mPullToRefreshListView.setAdapter(adapter);
        mPullToRefreshListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String cjgh = adapter.getItem(position).CJGH;
                if (!TextUtils.isEmpty(cjgh)) {
                    if (!cjgh.equals(mAppApplication.user.YHID)) {
                        showMsgAndVoiceAndVibrator("该体征信息只有本人可以修改，您不能修改");
                        return;
                    }
                }
                //
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                showDialogCompat(DIALOG_UPDATE, bundle);
            }
        });
        mPullToRefreshListView.setOnItemLongClickListener(
                new OnItemLongClickListener() {

                    @SuppressWarnings("deprecation")
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,
                                                   View view, final int position, long id) {

                        Bundle bundle = new Bundle();
                        bundle.putInt("position", position);
                        showDialogCompat(DIALOG_DELETE, bundle);
                        return true;
                    }
                });


    }

    protected void showDialogCompat(int id, Bundle args) {

        switch (id) {
            case DIALOG_DELETE:
                final int position = args.getInt("position");
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                View txt = ViewBuildHelper.buildDialogTitleTextView(mContext, getString(R.string.project_tips));
                builder.setMessage("确定要删除此体征记录吗?")
                        // .setTitle(getString(R.string.project_tips))
                        .setCustomTitle(txt)
                        .setIcon(android.R.drawable.ic_dialog_alert);

                builder.setPositiveButton(android.R.string.ok,
                        new android.content.DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                actionDeleteTask(position,
                                        adapter.getItem(position).XMH,
                                        adapter.getItem(position).CJH);

                            }
                        });
                builder.setNegativeButton(android.R.string.cancel,
                        new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                // 3. Get the AlertDialog from create()
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case DIALOG_UPDATE:
                final int posit = args.getInt("position");

                String tznr = adapter.getItem(posit).TZNR;
                String xmmc = adapter.getItem(posit).XMMC;
                LinearLayout contair = LayoutParamsHelper.buildLinearMatchWrap_V(mContext);
                contair.setPadding(50, 0, 50, 0);
                //
                EditText editText = ViewBuildHelper.buildEditTextMatchWrap(this, tznr);
                editText.setHint("输入体征内容");
                editText.setSelection(tznr.length());
                //
                contair.addView(editText);
                ///
                new AlertDialog.Builder(this)
//                        .setMessage(xmmc)
                        // .setTitle(getString(R.string.project_tips))
                        .setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext, "修改" + xmmc))
                        .setView(contair)
                        .setPositiveButton(android.R.string.ok,
                                new android.content.DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String vvv = editText.getText().toString();
                                        if (TextUtils.isEmpty(vvv)) {
                                            showMsgAndVoiceAndVibrator("修改值不能为空");
                                            return;
                                        }
                                        actionUpdateTask(posit, vvv,
                                                adapter.getItem(posit).XMH,
                                                adapter.getItem(posit).CJH);


                                    }
                                }).setNegativeButton(android.R.string.cancel,
                        new android.content.DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                break;
            default:
        }

    }

    void initTime() {
        String nowDate = DateTimeHelper.getServerDate();
        // 当天
        String eTimeStr = nowDate;
        eTimeTxt.setText(eTimeStr);

        // 前天
        String startDate= DateTimeHelper.dateAddedDays(nowDate,-1);
        String sTimeStr = startDate;
        sTimeTxt.setText(sTimeStr);

        sltStimeView.setOnClickListener(onClickListener);
        sltEtimeView.setOnClickListener(onClickListener);
    }

    public View.OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            String dateStr = null;
            int viewId = v.getId();
            if (viewId == R.id.slt_stime_ly) {
                dateStr = sTimeTxt.getText().toString();
            } else if (viewId == R.id.slt_etime_ly) {
                dateStr = eTimeTxt.getText().toString();
            }
            if (EmptyTool.isBlank(dateStr)) {
                return;
            }
            YmdHMs ymdHMs = DateTimeHelper.date2YmdHMs(dateStr);
            showPickerDateCompat(ymdHMs, viewId);
        }
    };

    @Override
    public void onDateSet(int year, int month, int dayOfMonth, int viewId) {
        String nowDate = DateTimeFactory.getInstance().ymd2Date(year, month, dayOfMonth);
        if (viewId == R.id.slt_stime_ly) {
            //选择开始时间
            String endDate = eTimeTxt.getText().toString();
            boolean after = DateTimeFactory.getInstance().dateAfter(nowDate, endDate);
            if (after) {
                showMsgAndVoiceAndVibrator("开始时间后于结束时间,请重新选择!");
                return;
            }
        } else if (viewId == R.id.slt_etime_ly) {
            //选择结束时间
            String startDate = sTimeTxt.getText().toString();
            boolean before = DateTimeFactory.getInstance().dateBefore(nowDate, startDate);
            if (before) {
                showMsgAndVoiceAndVibrator("结束时间先于开始时间，请重新选择!");
                return;
            }
        }
        initTimeTxt(nowDate, viewId);
    }

    private void initTimeTxt(String nowDate, int viewId) {
        String timeStr = nowDate;
        if (viewId == R.id.slt_etime_ly) {
            eTimeTxt.setText(timeStr);
        } else if (viewId == R.id.slt_stime_ly) {
            sTimeTxt.setText(timeStr);
        }

    }

    /**
     * 异步加载
     */
    private class GetDataTask extends AsyncTask<Void, Void, Response<LifeSignHistoryData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        // 传入时间
        @Override
        protected Response<LifeSignHistoryData> doInBackground(Void... params) {


            if (mAppApplication.sickPersonVo == null) {
                return null;
            }
            String ZYH = mAppApplication.sickPersonVo.ZYH;
            String sTime = sTimeTxt.getText().toString();
            String eTime = eTimeTxt.getText().toString();
            String jgid = mAppApplication.jgId;

            return LifeSignApi.getInstance(getApplicationContext())
                    .LifeSymptomQuery(sTime, eTime, ZYH, jgid);
        }

        @Override
        protected void onPostExecute(Response<LifeSignHistoryData> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();
            tasks.remove(this);
            adapter.clearData();
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(LifeSymptomInquiryActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            toRefreshData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    LifeSignHistoryData lifeSignHistoryData = result.Data;
                    ArrayList<LifeSignHistoryDataItem> lifeSignHistoryDataItems = (ArrayList<LifeSignHistoryDataItem>) lifeSignHistoryData.lifeSignHistoryDataItem;
                    if (null != lifeSignHistoryDataItems && lifeSignHistoryDataItems.size() > 0) {
                        list = lifeSignHistoryDataItems;
                        adapter.addData(lifeSignHistoryDataItems);
                    }
                    ArrayList<LifeSignHistoryDataType> lifeSignHistoryDataTypes = (ArrayList<LifeSignHistoryDataType>) lifeSignHistoryData.lifeSignHistoryDataType;
                    if (null != lifeSignHistoryDataTypes && lifeSignHistoryDataTypes.size() > 0) {
                        dList = lifeSignHistoryDataTypes;
                        textname.setText(dList.get(0).XMMC);
                        pullListView = new ListView(
                                LifeSymptomInquiryActivity.this);
                        pullListView.setBackgroundColor(ContextCompat.getColor(LifeSymptomInquiryActivity.this, R.color.windowBackground));
                        pullAdapter = new PullAdapter();
                        pullListView.setAdapter(pullAdapter);
                        currentType = 0;
                    }
                } else {
                    showMsgAndVoice(result.Msg);
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：参数错误");
                return;
            }
        }
    }

    /**
     * 异步加载
     */
    private class ChangeDataTask extends
            AsyncTask<Integer, Void, ArrayList<LifeSignHistoryDataItem>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        // 传入时间
        @Override
        protected ArrayList<LifeSignHistoryDataItem> doInBackground(
                Integer... params) {
            if (0 == params[0]) {
                return list;
            }
            if (list == null) {
                return null;
            }
            ArrayList<LifeSignHistoryDataItem> data = new ArrayList<LifeSignHistoryDataItem>();
            for (LifeSignHistoryDataItem vo : list) {
                if (vo.XMH == params[0]) {
                    data.add(vo);
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<LifeSignHistoryDataItem> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (result != null) {
                adapter.clearData();
                adapter.addData(result);
            }

        }
    }


    /**
     * 删除体征
     */
    private class DeleteDataTask extends AsyncTask<Integer, Void, Response<String>> {

        private int index;

        public DeleteDataTask(int index) {
            this.index = index;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(getString(R.string.deleteing));
        }

        @Override
        protected Response<String> doInBackground(Integer... params) {

            if (params == null || params.length < 2) {
                return null;
            }

            if (application.user == null) {
                return null;
            }
            String yhid = application.user.YHID;
            Integer rid = params[0];
            Integer cjh = params[1];
            String jgid = application.jgId;

            return LifeSignApi.getInstance(getApplicationContext())
                    .LifeSymptomDelete(yhid, rid, cjh, jgid, Constant.sysType);
        }

        @Override
        protected void onPostExecute(Response<String> result) {
            super.onPostExecute(result);

            hideLoadingDialog();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(LifeSymptomInquiryActivity.this, mAppApplication).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    adapter.remove(index);
                    showMsgAndVoice("删除成功");
                } else {
                    showMsgAndVoice(result.Msg);
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：参数错误");
                return;
            }

        }
    }


    private class UpdateDataTask extends AsyncTask<Integer, Void, Response<String>> {

        private int index;
        private String value;

        public UpdateDataTask(int index, String value) {
            this.index = index;
            this.value = value;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(getString(R.string.doing));
        }

        @Override
        protected Response<String> doInBackground(Integer... params) {

            if (params == null || params.length < 2) {
                return null;
            }

            if (application.user == null) {
                return null;
            }
            String yhid = application.user.YHID;
            Integer rid = params[0];
            Integer cjh = params[1];
            String jgid = application.jgId;
            try {
                value = URLEncoder.encode(value, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return LifeSignApi.getInstance(getApplicationContext())
                    .LifeSymptomUpdate(value, yhid, rid, cjh, jgid, Constant.sysType);
        }

        @Override
        protected void onPostExecute(Response<String> result) {
            super.onPostExecute(result);

            hideLoadingDialog();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(LifeSymptomInquiryActivity.this, mAppApplication).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    showMsgAndVoice("修改成功");
                    toRefreshData();
                } else {
                    showMsgAndVoice(result.Msg);
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：参数错误");
                return;
            }

        }
    }

    class PullAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return dList.size();
        }

        @Override
        public LifeSignHistoryDataType getItem(int position) {
            return dList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.item_list_text_one, parent, false);
                holder.text_row = (TextView) convertView
                        .findViewById(R.id.text_row);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.text_row.setText(dList.get(position).XMMC);
            holder.text_row.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (currentType != position) {
                        currentType = position;
                        textname.setText(dList.get(position).XMMC);
                        actionChangeTask(dList.get(position).XMH);
                    }
                    pop.dismiss();
                }
            });
            return convertView;
        }

        public final class ViewHolder {
            public TextView text_row;
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
