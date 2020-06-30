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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.base.BaseBarcodeActivity;
import com.bsoft.mob.ienr.activity.user.adapter.DrugMedicalAdapter;
import com.bsoft.mob.ienr.activity.user.adapter.OperationAdapter;
import com.bsoft.mob.ienr.activity.user.adapter.SignAdapter;
import com.bsoft.mob.ienr.api.NurseRecordApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.YmdHMs;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.nurserecord.DrugMedical;
import com.bsoft.mob.ienr.model.nurserecord.Operation;
import com.bsoft.mob.ienr.model.nurserecord.Sign;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;

import java.util.ArrayList;
import java.util.List;

public class CareRecordReferActivity extends BaseBarcodeActivity {

    private ListView listView;

    private View id_yz_layout;
    private SpinnerLayout mSpinnerLayout;
    private Spinner mSpinner;

    public static final int QRT_GET_Medical = 0;
    public static final int QRT_GET_Operation = 1;
    public static final int QRT_GET_Sign = 2;
    // public static final int QRT_GET_Other = 3;

    private View sltStimeView;
    private View sltEtimeView;

    private TextView stime;
    private TextView etime;

    private ImageView searchBtn;

    private BaseAdapter mAdapter;

    private View timeRootView;
    /* ======== 修改编号【fixme】 ======== start*/

    private List<DrugMedical> mList;

    /* ================================ end*/
    private void initSearchBtn() {

        searchBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toRefreshData();
            }
        });
    }


    void initTime() {

        String nowDate = DateTimeHelper.getServerDate();
        // 当天
        String eTimeStr = nowDate;
        etime.setText(eTimeStr);

        // 前天
        String startDate = DateTimeHelper.dateAddedDays(nowDate, -3);
        String sTimeStr = startDate;
        stime.setText(sTimeStr);

        sltStimeView.setOnClickListener(onClickListener);
        sltEtimeView.setOnClickListener(onClickListener);

        final TextView stimeTitle = (TextView) sltStimeView
                .findViewById(R.id.stime_title);
        final TextView etimeTitle = (TextView) sltEtimeView
                .findViewById(R.id.etime_title);

        stimeTitle.setText(R.string.start_time);
        etimeTitle.setText(R.string.end_time);
    }

    public View.OnClickListener onClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {

            String dateStr = null;
            int viewId = v.getId();
            if (viewId == R.id.slt_stime_ly) {
                dateStr = stime.getText().toString();
            } else if (viewId == R.id.slt_etime_ly) {
                dateStr = etime.getText().toString();
            }
            if (EmptyTool.isBlank(dateStr)) {
                return;
            }

            YmdHMs ymdHMs = DateTimeHelper.date2YmdHMs(dateStr);
            showPickerDateCompat(ymdHMs, viewId);

        }
    };

    private void initTimeTxt(String nowDate, int viewId) {
        String timeStr = nowDate;
        if (viewId == R.id.slt_etime_ly) {
            etime.setText(timeStr);
        } else if (viewId == R.id.slt_stime_ly) {
            stime.setText(timeStr);
        }

    }

    @Override
    public void onDateSet(int year, int month, int dayOfMonth, int viewId) {
        String nowDate = DateTimeFactory.getInstance().ymd2Date(year, month, dayOfMonth);
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
    }

    private void initSpinner() {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.care_record_refer_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(onSListener);
    }

    private OnItemSelectedListener onSListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {

            // 查找记录
            toRefreshData();

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    };

    @SuppressWarnings("rawtypes")
    class GetDateTask extends AsyncTask<Void, String, Response> {

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
            mSpinnerLayout.setVisibility(View.GONE);
            if (mSpinner.getSelectedItemPosition() == QRT_GET_Operation) {
                timeRootView.setVisibility(View.GONE);
            } else {
                timeRootView.setVisibility(View.VISIBLE);
            }
            if (mSpinner.getSelectedItemPosition() == QRT_GET_Medical) {
                id_yz_layout.setVisibility(View.VISIBLE);
            } else {
                id_yz_layout.setVisibility(View.GONE);
            }
        }

        @Override
        protected Response doInBackground(Void... params) {


            if (mAppApplication.sickPersonVo == null) {
                return null;
            }

            String zyh = mAppApplication.sickPersonVo.ZYH;
            String jgid = mAppApplication.jgId;
            int sysType = Constant.sysType;

            NurseRecordApi api = NurseRecordApi
                    .getInstance(getApplicationContext());

            String sTime = stime.getText().toString();
            String eTime = etime.getText().toString();

            switch (mSpinner.getSelectedItemPosition()) {

                case QRT_GET_Medical: // 过敏药

                    Response<List<DrugMedical>> response = api
                            .GetDrugMedicalAdviceList(zyh, sTime, eTime, jgid);
                    return response;
                case QRT_GET_Operation: // 手术记录

                    Response<List<Operation>> oResponse = api.GetOperationList(zyh,
                            jgid);
                    return oResponse;
                case QRT_GET_Sign: // 体征记录

                    Response<List<Sign>> sResponse = api.GetSignList(zyh, sTime,
                            eTime, jgid);
                    return sResponse;
                // case QRT_GET_Other: // 其他
                // String dmlb = null;
                // Response<List<OtherRefer>> orther = api.GetOtherList(dmlb,
                // jgid, sysType);
                // return orther;
                default:
            }

            return null;

        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onPostExecute(Response result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);
            mSpinnerLayout.setVisibility(View.VISIBLE);
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
//				mAdapter = null;
//				listView.setAdapter(mAdapter);
//				return;
//			}
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(CareRecordReferActivity.this, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            toRefreshData();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    switch (mSpinner.getSelectedItemPosition()) {

                        case QRT_GET_Medical: // 过敏药

                            List<DrugMedical> list = (List<DrugMedical>) result.Data;
                            /* ======== 修改编号【fixme】 ======== start*/
                            mList = list;
                            /* ================================= end*/
                            importDrugMedicalList(list);
                            break;
                        case QRT_GET_Operation: // 手术记录

                            List<Operation> olist = (List<Operation>) result.Data;
                            importOperationList(olist);
                            break;
                        case QRT_GET_Sign: // 体征记录

                            List<Sign> slist = (List<Sign>) result.Data;
                            importSignList(slist);
                            break;
                        // case QRT_GET_Other: // 其他
                        // List<OtherRefer> otherList = (List<OtherRefer>) result.Data;
                        // importOtherList(otherList);
                        // break;
                        default:
                    }
                } else {
                    showMsgAndVoice(result.Msg);
                    return;
                }

            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }

        }
    }


    // public void importOtherList(List<OtherRefer> otherList) {
    //
    // SignAdapter adapter = new SignAdapter(this, otherList);
    // listView.setAdapter(adapter);
    // }

    public void importSignList(List<Sign> slist) {

        SignAdapter adapter = new SignAdapter(this, slist);
        mAdapter = adapter;
        listView.setAdapter(adapter);
    }

    public void importOperationList(List<Operation> olist) {

        OperationAdapter adapter = new OperationAdapter(this, olist);
        mAdapter = adapter;
        listView.setAdapter(adapter);
    }

    public void importDrugMedicalList(List<DrugMedical> list) {
        List<DrugMedical> listNew = new ArrayList<>();
        if (LSYZ == -1) {
            //全部
            listNew.addAll(list);
        } else if (LSYZ == 0) {
            //长期医嘱
            for (DrugMedical drugMedical : list) {
                if ("0".equals(drugMedical.LSYZ)) {
                    listNew.add(drugMedical);
                }
            }
        } else if (LSYZ == 1) {
            //临时医嘱
            for (DrugMedical drugMedical : list) {
                if ("1".equals(drugMedical.LSYZ)) {
                    listNew.add(drugMedical);
                }
            }
        }
        //
        DrugMedicalAdapter adapter = new DrugMedicalAdapter(this, listNew);
        mAdapter = adapter;
        listView.setAdapter(adapter);
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected void toRefreshData() {
        GetDateTask changeTask = new GetDateTask();
        tasks.add(changeTask);
        changeTask.execute();
    }

    private void initListView() {


        listView.setOnItemClickListener(onItemClickLister);

    }

    private OnItemClickListener onItemClickLister = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            if (mAdapter != null) {
                if (mAdapter instanceof DrugMedicalAdapter) {
                    DrugMedical item = (DrugMedical) mAdapter
                            .getItem(position);
                    /* ======== 修改编号【fixme】 ======== start*/
                    String yzmc = "";
                    String yynr = "";
                    if (mList != null) {
                        for (DrugMedical drugMedical : mList) {
                            if (drugMedical.YZZH.equals(item.YZZH)) {
                                yzmc += drugMedical.YZMC + "\n";
                                yynr += drugMedical.YZMC + "|||" + drugMedical.YCJL + "|||" + drugMedical.JLDW + "|||" + drugMedical.YPYF + "\n";
                            }
                        }
                        yzmc = yzmc.substring(0, yzmc.length() - 1);
                        yynr = yynr.substring(0, yynr.length() - 1);

                    } else {
                        yzmc = item.YZMC;
                        yynr = item.YYNR;
                    }
                    showDetailDialog(yzmc, yynr);
                    /* ================================= end*/
                } else if (mAdapter instanceof SignAdapter) {
                    Sign item = (Sign) mAdapter.getItem(position);
                    showDetailDialog(item.YYMC, item.YYNR);
                } else if (mAdapter instanceof OperationAdapter) {
                    Operation item = (Operation) mAdapter.getItem(position);
                    showDetailDialog("手术记录", item.YYNR);
                }
            }
        }
    };

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
                    //.setTitle(title)
                    .setCustomTitle(txt);
            //.setIcon(android.R.drawable.ic_dialog_info);


            return builder.create();

        }
    }

    private void initActionBar() {

        actionBar.setTitle("引用查询");
        actionBar.setPatient(mAppApplication.sickPersonVo.BRCH + mAppApplication.sickPersonVo.BRXM);

    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.activity_care_record_refer;
    }

    CheckBox id_cb;
    CheckBox id_cb_2;
    CheckBox id_cb_3;
    int LSYZ = -1;//全部 -1  长期医嘱 0 临时医嘱 1

    @Override
    protected void initView(Bundle savedInstanceState) {

        listView = (ListView) findViewById(R.id.id_lv);
        id_yz_layout = findViewById(R.id.id_yz_layout);
        id_cb = findViewById(R.id.id_cb);
        id_cb.setText("全部");
        id_cb_2 = findViewById(R.id.id_cb_2);
        id_cb_2.setText("临时");//LSYZ=1
        id_cb_3 = findViewById(R.id.id_cb_3);
        id_cb_3.setText("长期");//LSYZ=0
        id_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView == null || !buttonView.isPressed()) {
                    //不响应非点击引起的改变
                    return;
                }
                if (isChecked) {
                    id_cb_2.setChecked(false);
                    id_cb_3.setChecked(false);
                    LSYZ = -1;
                    toRefreshData();
                }
            }
        });
        id_cb_2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView == null || !buttonView.isPressed()) {
                    //不响应非点击引起的改变
                    return;
                }
                if (isChecked) {
                    id_cb.setChecked(false);
                    id_cb_3.setChecked(false);
                    LSYZ = 1;
                    toRefreshData();
                }
            }
        });
        id_cb_3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView == null || !buttonView.isPressed()) {
                    //不响应非点击引起的改变
                    return;
                }
                if (isChecked) {
                    id_cb.setChecked(false);
                    id_cb_2.setChecked(false);
                    LSYZ = 0;
                    toRefreshData();
                }
            }
        });
        mSpinnerLayout = (SpinnerLayout) findViewById(R.id.id_spinner_layout);
        mSpinner = mSpinnerLayout.getSpinner();


        EmptyViewHelper.setEmptyView(listView, "listView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, listView);

        sltStimeView = findViewById(R.id.slt_stime_ly);
        sltEtimeView = findViewById(R.id.slt_etime_ly);

        stime = (TextView) findViewById(R.id.stime);
        etime = (TextView) findViewById(R.id.etime);

        searchBtn = (ImageView) findViewById(R.id.search);

        timeRootView = findViewById(R.id.id_layout_double_time);

        initActionBar();
        initListView();
        initSpinner();
        initTime();

        initSearchBtn();
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
