package com.bsoft.mob.ienr.fragment.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.UserModelActivity;
import com.bsoft.mob.ienr.api.BloodSugarApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.DateTimeTool;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.bloodsugar.BloodSugar;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.expand.ClassicDropSelectEditView;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 血糖治疗
 *
 * @author 田孝鸣
 * @ClassName: BloodSugarFragment
 * @Description: 血糖治疗
 * @date 2017-05-22 下午3:16:41
 */
public class BloodSugarFragment extends BaseUserFragment {


    private View mainView;
    private ListView refreshView;
    private List<BloodSugar> mBloodSugarList;
    private List<String> mClsdList;
    private BloodSugarAdapter mBloodSugarAdapter;

    private View sltStimeView;
    private View sltEtimeView;
    private TextView stime, etime;
    private ImageView searchBtn;

    private Button id_btn;

    int itemId;


    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_blood_sugar;
    }

    @Override
    protected void initView(View rootLayout, Bundle savedInstanceState) {
        mainView = rootLayout;
        refreshView = (ListView) mainView
                .findViewById(R.id.id_lv);

        EmptyViewHelper.setEmptyView(refreshView, "refreshView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, refreshView);
        stime = (TextView) mainView.findViewById(R.id.stime);
        etime = (TextView) mainView.findViewById(R.id.etime);

        sltStimeView = mainView.findViewById(R.id.slt_stime_ly);
        sltEtimeView = mainView.findViewById(R.id.slt_etime_ly);


        final TextView stimeTitle = (TextView) sltStimeView
                .findViewById(R.id.stime_title);
        final TextView etimeTitle = (TextView) sltEtimeView
                .findViewById(R.id.etime_title);

        stimeTitle.setText(R.string.start_time);
        etimeTitle.setText(R.string.end_time);

        searchBtn = (ImageView) mainView.findViewById(R.id.search);


        initActionBar();
        initListView();
        initSearchBtn();
        initTime();

        initBroadCast();
        toRefreshData();
        //
        id_btn = (Button) mainView.findViewById(R.id.id_btn);
        id_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //新增
                BloodSugar item = new BloodSugar();
                item.ZYH = application.sickPersonVo.ZYH;
                item.SXBQ = application.getAreaId();
                item.BRCH = application.sickPersonVo.XSCH;
                item.SXSJ = DateTimeHelper.getServer_yyyyMMddHHmm00();
                item.SXGH = application.user.YHID;
                item.SXXM = application.user.YHXM;
                item.CJGH = application.user.YHID;
                item.CLSD = "";
                item.CLZ = "";
                item.JGID = application.jgId;
                //
                showEditDialog(item);
            }
        });
    }

    private void initSearchBtn() {

        searchBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                performHttpTask();
            }
        });
    }

    void initTime() {

        String nowDate = DateTimeHelper.getServerDate();
        // 当天
        String eTimeStr = nowDate;
        etime.setText(eTimeStr);

        // 前天
        String sTimeStr = nowDate;
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

    @Override
    public void onDateTimeSet(int year, int month, int dayOfMonth,
                              int hourOfDay, int minute, int viewId) {

        String datetime = DateTimeFactory.getInstance()
                .ymdhms2DateTime(year, month, dayOfMonth,
                        hourOfDay, minute, 0);
//        mBloodSugarAdapter.refreshDataList(mBloodSugarList);
        if (viewHolder != null) {
            viewHolder.id_tv_2_for_bar_image.setText(datetime);
        }
    }

    private void initTimeTxt(String dateStr, int viewId) {
        String timeStr = dateStr;
        if (viewId == R.id.slt_etime_ly) {
            etime.setText(timeStr);
        } else if (viewId == R.id.slt_stime_ly) {
            stime.setText(timeStr);
        }

    }

    /*public OnRefreshListener<ListView> onRefreshListener = new OnRefreshListener<ListView>() {

        @Override
        public void onRefresh(PullToRefreshBase<ListView> refreshView) {

            String label = DateUtils.formatDateTime(getActivity(),
                    System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                            | DateUtils.FORMAT_SHOW_DATE
                            | DateUtils.FORMAT_ABBREV_ALL);
            refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

            performHttpTask();

        }
    };*/


    @Override
    protected void toRefreshData() {
        performHttpTask();
    }

    private void initActionBar() {

        actionBar.setTitle("血糖监测");
        actionBar.setPatient(application.sickPersonVo.XSCH
                + application.sickPersonVo.BRXM);
    }

    private void initBroadCast() {
        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {

                String action = intent.getAction();
                if (BarcodeActions.Refresh.equals(action)) {
                    sendUserName();
                    actionBar.setPatient(application.sickPersonVo.XSCH
                            + application.sickPersonVo.BRXM);
                    performHttpTask();
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

    private void initListView() {


        mBloodSugarAdapter = new BloodSugarAdapter(getActivity(), new ArrayList<>());
        refreshView.setAdapter(mBloodSugarAdapter);
        refreshView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BloodSugar vo = (BloodSugar) parent.getAdapter().getItem(position);
                itemId = position;
                showEditDialog(vo);
            }
        });
    }

    private class ViewHolder {
        //        private EditText CLZ;
        private TextView CLR;
        private EditText CLSJ_CUSTOM;
        private SpinnerLayout id_spinner_layout_CLSD;
        private ClassicDropSelectEditView CLZ;
        private TextView id_tv_for_bar_image;
        private TextView id_tv_2_for_bar_image;
        private ImageView id_iv_for_bar_image;
       /* private TextView id_tv_for_bar_image_copy;
        private TextView id_tv_2_for_bar_image_copy;
        private ImageView id_iv_for_bar_image_copy;*/
    }

    private boolean isCodeSetText = false;
    private ViewHolder viewHolder;
    private boolean isFirst;

    private void showEditDialog(BloodSugar vo) {
        isFirst = true;
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_bloodsugar, null, false);
        viewHolder = new ViewHolder();
        viewHolder.CLZ = (ClassicDropSelectEditView) convertView.findViewById(R.id.CLZ);
        viewHolder.CLR = (TextView) convertView.findViewById(R.id.CLR);
        viewHolder.id_spinner_layout_CLSD = (SpinnerLayout) convertView.findViewById(R.id.id_spinner_layout_CLSD);
        viewHolder.CLSJ_CUSTOM = (EditText) convertView.findViewById(R.id.CLSJ_CUSTOM);

        viewHolder.id_tv_for_bar_image = (TextView) convertView.findViewById(R.id.id_tv_for_bar_image);
        viewHolder.id_tv_2_for_bar_image = (TextView) convertView.findViewById(R.id.id_tv_2_for_bar_image);
        viewHolder.id_iv_for_bar_image = (ImageView) convertView.findViewById(R.id.id_iv_for_bar_image);

     /*   viewHolder.id_tv_for_bar_image_copy = (TextView) convertView.findViewById(R.id.id_tv_for_bar_image_copy);
        viewHolder.id_tv_2_for_bar_image_copy = (TextView) convertView.findViewById(R.id.id_tv_2_for_bar_image_copy);
        viewHolder.id_iv_for_bar_image_copy = (ImageView) convertView.findViewById(R.id.id_iv_for_bar_image_copy);

        viewHolder.id_tv_for_bar_image.setText("测量时间点");
        viewHolder.id_tv_for_bar_image_copy.setText("测量时间");*/
        Pair<String, String> stringStringPair1 = Pair.create("1", "拒测");
        Pair<String, String> stringStringPair2 = Pair.create("2", "不在");
        List<Pair<String, String>> pairList = new ArrayList<>();
        pairList.add(stringStringPair1);
        pairList.add(stringStringPair2);
        viewHolder.CLZ.setupDropDownSelectData(pairList);
        viewHolder.id_tv_for_bar_image.setText("测量时间");
        if (!vo.SXGH.equals(application.user.YHID)) {
            viewHolder.CLZ.setEnabled(false);
            Toast.makeText(mContext, "书写工号不一致，不修改测量值", Toast.LENGTH_SHORT).show();
        }
//        viewHolder.id_tv_2_for_bar_image.setText(vo.CLSD);
//        viewHolder.id_tv_2_for_bar_image_copy.setText(vo.SXSJ.replace("/", "-"));
//        viewHolder.id_tv_2_for_bar_image_copy.setText(TextUtils.isEmpty(vo.SXSJ) ? "" : vo.SXSJ.replace("/", ""));
        viewHolder.id_tv_2_for_bar_image.setText(TextUtils.isEmpty(vo.SXSJ) ? "" : vo.SXSJ.replace("/", ""));
        viewHolder.CLR.setText(vo.SXXM);
        isCodeSetText = true;
        viewHolder.CLZ.setText(vo.CLZ);
        isCodeSetText = false;
        EditText clz_edit = viewHolder.CLZ.getClassicInputLayout().getInput();
        clz_edit.setSelection(TextUtils.isEmpty(vo.CLZ) ? 0 : vo.CLZ.length());
        //
        clz_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isCodeSetText) {
                    return;
                }
                String vss = editable.toString();
                if (!TextUtils.isEmpty(vss)) {
                    vss = vss.trim();
                }
                vo.CLZ = vss;
            }
        });

        viewHolder.id_iv_for_bar_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vo.SXGH.equals(application.user.YHID)) {
                    showDateTimePickerCompat(vo.SXSJ, view.getId());
                } else {
                    showMsgAndVoiceAndVibrator("不允许编辑其他用户录入的数据!");
                }
            }
        });

        //
        viewHolder.CLSJ_CUSTOM.setHint("0930代表09点30分");
        viewHolder.CLSJ_CUSTOM.setInputType(InputType.TYPE_CLASS_NUMBER);
        //默认显示数字键盘
        clz_edit.setRawInputType(Configuration.KEYBOARD_QWERTY);
        final Spinner spinner = viewHolder.id_spinner_layout_CLSD.getSpinner();
        SpinnerAdapter adapter = new SpinnerAdapter();
        spinner.setAdapter(adapter);
        int index = 0;
        if (mClsdList == null) {
            showMsgAndVoiceAndVibrator("测量时点未获取成功，请查验！");
            return;
        }
        for (int i = 0; i < mClsdList.size(); i++) {
            if (mClsdList.get(i).equals(vo.CLSD)) {
                index = i;
                break;
            }
        }
        if (index != 0) {
            spinner.setSelection(index);
        } else {
            String clssss = vo.CLSD;
            if (!TextUtils.isEmpty(clssss) && clssss.contains(":")) {
                clssss = clssss.replace(":", "");
            }
            viewHolder.CLSJ_CUSTOM.setText(clssss);
            viewHolder.CLSJ_CUSTOM.setSelection(clssss != null ? clssss.length() : 0);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (isFirst) {
                    isFirst = false;
                    return;
                }
                String initData = "";
                if (i == 0) {//选择第一行 设置时点
                    String[] array = vo.SXSJ.split(" ");
                    if (array != null && array.length == 2) {
                        initData = array[1];
                        initData = TextUtils.isEmpty(initData) ? "" : initData.substring(0,
                                initData.lastIndexOf(":")).replace(":", "");
                    }
                }
             /*   String sd = TextUtils.isEmpty(vo.CLSD) ? initData : vo.CLSD;
                viewHolder.CLSJ_CUSTOM.setSelection(sd != null ? sd.length() : 0);*/
                if (i != 0) {
                    viewHolder.CLSJ_CUSTOM.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //

        AlertDialog mAlertDialog = new AlertDialog.Builder(mContext)
                .setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext, "血糖录入"))
                .setView(convertView)
                .setCancelable(false)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                        //执行赋值操作
                        if (EmptyTool.isBlank(viewHolder.CLSJ_CUSTOM.getText().toString().trim())) {
                            vo.CLSD = String.valueOf(viewHolder.id_spinner_layout_CLSD.getSpinner().getSelectedItem());
                        } else {
                            String clsj = viewHolder.CLSJ_CUSTOM.getText().toString().trim();
                            if (!TextUtils.isEmpty(clsj) && clsj.length() == 4) {
                                String clsj_S = clsj.substring(0, 2);
                                String clsj_F = clsj.substring(2, 4);
                                clsj = clsj_S + ":" + clsj_F;
                            } else {
                                Toast.makeText(mContext, "时间录入格式不对，请检查", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            vo.CLSD = clsj;
                        }
//                            mBloodSugarAdapter.refreshDataList(mBloodSugarList);
                      /*  if (viewHolder != null) {
                            viewHolder.id_tv_2_for_bar_image.setText(vo.CLSD);
                        }*/

                        //保存
                        if (vo.SXGH.equals(application.user.YHID)) {
                            if (TextUtils.isEmpty(vo.CLZ)) {
                                showMsgAndVoiceAndVibrator("测量值不能为空!");
                                return;
                            }
                            if (EmptyTool.isBlank(vo.JLXH)) {
                                addHttpTask(vo);
                            } else {
                                editHttpTask();
                            }
                        } else {
                            showMsgAndVoiceAndVibrator("不允许编辑其他用户录入的数据!");
                        }

                    }
                }).create();
        mAlertDialog.show();

    }


    public void getClsdListHttpTask() {
        if (mClsdList != null && !mClsdList.isEmpty()) {
            return;
        }
        GetClsdListHttpTask getClsdListHttpTask = new GetClsdListHttpTask();
        tasks.add(getClsdListHttpTask);
        getClsdListHttpTask.execute();
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    class GetClsdListHttpTask extends AsyncTask<Void, Void, Response<List<String>>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Response<List<String>> doInBackground(Void... arg0) {

            if (application.sickPersonVo == null) {
                return null;
            }

            return BloodSugarApi.getInstance(getActivity()).GetClsdList();
        }

        @Override
        protected void onPostExecute(Response<List<String>> result) {
            super.onPostExecute(result);
            tasks.remove(this);
          /*  if (null != mBloodSugarAdapter) {
                mBloodSugarList = new ArrayList<>();
                mBloodSugarAdapter.refreshDataList(mBloodSugarList);
            } else {
                mBloodSugarAdapter = new BloodSugarAdapter(getActivity(), new ArrayList<>());
            }*/
            if (result == null) {
                showMsgAndVoiceAndVibrator("加载失败");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), application, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        getClsdListHttpTask();
                    }
                }).showLoginDialog();
                return;
            } else if (result.ReType == 0) {
                mClsdList = result.Data;
//                performHttpTask();
            } else {
                showMsgAndVoice(result.Msg);
                return;
            }

        }
    }

    public void performHttpTask() {
        //
        getClsdListHttpTask();
        //
        String start = stime.getText().toString();
        String end = etime.getText().toString();
        GetHttpTask getHttpTask = new GetHttpTask();
        tasks.add(getHttpTask);
        getHttpTask.execute(start, end);

    }

    @SuppressWarnings("unchecked")
    class GetHttpTask extends AsyncTask<String, Void, Response<List<BloodSugar>>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<BloodSugar>> doInBackground(String... arg0) {

            if (application.sickPersonVo == null && arg0.length < 0) {
                return null;
            }

            String jgid = application.jgId;
            String stime = arg0[0];
            String etime = arg0[1];
            return BloodSugarApi.getInstance(getActivity()).GetBloodSugarList(application.sickPersonVo.ZYH,
                    stime, etime, jgid);
        }

        @Override
        protected void onPostExecute(Response<List<BloodSugar>> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (result == null) {
                showMsgAndVoiceAndVibrator("加载失败");
                return;
            }

            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), application, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        performHttpTask();
                    }
                }).showLoginDialog();
                return;
            } else if (result.ReType == 0) {
                mBloodSugarList = result.Data;
            } else {
                showMsgAndVoice(result.Msg);
            }
            if (mBloodSugarList == null) {
                mBloodSugarList = new ArrayList<>();
            }
            mBloodSugarAdapter.refreshDataList(mBloodSugarList);
        }
    }

    public void addHttpTask(BloodSugar addBloodSugarItem) {
        AddHttpTask addHttpTask = new AddHttpTask(addBloodSugarItem);
        tasks.add(addHttpTask);
        addHttpTask.execute();
    }

    @SuppressWarnings("unchecked")
    class AddHttpTask extends AsyncTask<Void, Void, Response<String>> {
        BloodSugar addBloodSugarItem;

        public AddHttpTask(BloodSugar addBloodSugarItem) {
            this.addBloodSugarItem = addBloodSugarItem;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<String> doInBackground(Void... arg0) {

            if (application.sickPersonVo == null) {
                return null;
            }

            BloodSugar item = addBloodSugarItem;
            String sxsj = "";
            String clsd = "";
            try {
                if (!EmptyTool.isBlank(item.SXSJ)) {
                    sxsj = URLEncoder.encode(item.SXSJ, "UTF-8");
                }
                if (!EmptyTool.isBlank(item.CLSD)) {
                    clsd = URLEncoder.encode(item.CLSD, "UTF-8");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return BloodSugarApi.getInstance(getActivity()).AddBloodSugar(application.sickPersonVo.ZYH,
                    application.getAreaId(), application.sickPersonVo.XSCH, sxsj,
                    application.user.YHID, application.user.YHID, clsd, item.CLZ, application.jgId);
        }

        @Override
        protected void onPostExecute(Response<String> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (result == null) {
                showMsgAndVoiceAndVibrator("新增数据失败");
                return;
            }

            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), application, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        addHttpTask(addBloodSugarItem);
                    }
                }).showLoginDialog();
                return;
            } else if (result.ReType == 0) {
               /*### String jlxh = result.Data;
                mBloodSugarList.get(itemId).JLXH = jlxh;*/
                showMsgAndVoice("新增数据成功");
                performHttpTask();
            } else {
                showMsgAndVoice(result.Msg);
                return;
            }

        }
    }

    public void editHttpTask() {
        EditHttpTask editHttpTask = new EditHttpTask();
        tasks.add(editHttpTask);
        editHttpTask.execute();
    }

    @SuppressWarnings("unchecked")
    class EditHttpTask extends AsyncTask<Void, Void, Response<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(R.string.saveing);
        }

        @Override
        protected Response<String> doInBackground(Void... arg0) {

            if (application.sickPersonVo == null) {
                return null;
            }

            BloodSugar item = mBloodSugarList.get(itemId);
            String sxsj = "";
            String clsd = "";
            try {
                if (!EmptyTool.isBlank(item.SXSJ)) {
                    sxsj = URLEncoder.encode(item.SXSJ, "UTF-8");
                }
                if (!EmptyTool.isBlank(item.CLSD)) {
                    clsd = URLEncoder.encode(item.CLSD, "UTF-8");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return BloodSugarApi.getInstance(getActivity()).EditBloodSugar(item.JLXH,
                    clsd, sxsj, item.CLZ);
        }

        @Override
        protected void onPostExecute(Response<String> result) {
            super.onPostExecute(result);
            hideLoadingDialog();
            tasks.remove(this);

            if (result == null) {
                showMsgAndVoiceAndVibrator(R.string.project_save_failed);
                return;
            }

            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), application, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        editHttpTask();
                    }
                }).showLoginDialog();
                return;
            } else if (result.ReType == 0) {
                showMsgAndVoice(R.string.project_save_success);
                performHttpTask();
            } else {
                showMsgAndVoice(result.Msg);

                return;
            }

        }
    }

    public void deleteHttpTask() {
        DeleteHttpTask deleteHttpTask = new DeleteHttpTask();
        tasks.add(deleteHttpTask);
        deleteHttpTask.execute();
    }

    @SuppressWarnings("unchecked")
    class DeleteHttpTask extends AsyncTask<Void, Void, Response<String>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<String> doInBackground(Void... arg0) {

            if (application.sickPersonVo == null) {
                return null;
            }

            return BloodSugarApi.getInstance(getActivity()).DeleteBloodSugar(mBloodSugarList.get(itemId).JLXH);
        }

        @Override
        protected void onPostExecute(Response<String> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (result == null) {
                showMsgAndVoiceAndVibrator("删除失败");
                return;
            }

            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), application, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        deleteHttpTask();
                    }
                }).showLoginDialog();
                return;
            } else if (result.ReType == 0) {
                showMsgAndVoice("删除成功");
                /*mBloodSugarList.remove(itemId);
                mBloodSugarAdapter.refreshDataList(mBloodSugarList);*/
                performHttpTask();
            } else {
                showMsgAndVoice(result.Msg);

                return;
            }

        }
    }

    /**
     * Description: 血糖治疗适配器
     * User: 田孝鸣(tianxm@bsoft.com.cn)
     * Date: 2017-05-22
     * Time: 10:39
     * Version:
     */
    private class BloodSugarAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        Context mContext;

        private List<BloodSugar> mList = new ArrayList<>();

        public BloodSugarAdapter(Context context, ArrayList<BloodSugar> bloodSugars) {
            this.mContext = context;
            inflater = LayoutInflater.from(context);
            mList = bloodSugars;
        }

        public void refreshDataList(List<BloodSugar> list) {
            mList.clear();
            mList.addAll(list);
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public BloodSugar getItem(int arg0) {
            return mList.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder vHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_list_bloodsugar_list, parent, false);
                vHolder = new ViewHolder();
                //
                vHolder.id_tv_value = (TextView) convertView.findViewById(R.id.id_tv_value);
                vHolder.id_tv_sj = (TextView) convertView.findViewById(R.id.id_tv_sj);
                vHolder.id_tv_sd = (TextView) convertView.findViewById(R.id.id_tv_sd);
                vHolder.id_tv_clr = (TextView) convertView.findViewById(R.id.id_tv_clr);
                vHolder.id_iv_delete = (ImageView) convertView.findViewById(R.id.id_iv_delete);
                convertView.setTag(vHolder);
            } else {
                vHolder = (ViewHolder) convertView.getTag();
            }

            final BloodSugar vo = mList.get(position);
            vHolder.id_tv_value.setText(vo.CLZ);
            vHolder.id_tv_clr.setText(vo.SXXM);
            String ss = vo.SXSJ.replace("/", "-");
            ss = DateTimeTool.dateTime2Custom(ss, "MM-dd HH:mm");
            vHolder.id_tv_sj.setText(ss);
            vHolder.id_tv_sd.setText(vo.CLSD);
            vHolder.id_iv_delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (vo.SXGH.equals(application.user.YHID)) {
                        new AlertDialog.Builder(mContext)
                                .setMessage("是否确定删除该数据?")
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteHttpTask();
                                    }
                                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
                    } else {
                        showMsgAndVoiceAndVibrator("不允许删除其他用户录入的数据!");
                    }
                }
            });
            return convertView;
        }

        class ViewHolder {
            public TextView id_tv_value;
            public TextView id_tv_sj;
            public TextView id_tv_sd;
            public TextView id_tv_clr;

            public ImageView id_iv_delete;
        }

    }

    /**
     * Description: 下拉框适配器
     * User: 田孝鸣(tianxm@bsoft.com.cn)
     * Date: 2017-05-23
     * Time: 16:39
     * Version:
     */
    private class SpinnerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mClsdList == null ? 0 : mClsdList.size();
        }

        @Override
        public String getItem(int position) {
            return mClsdList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView tv = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.simple_spinner_dropdown_item, parent, false);
                tv = (TextView) convertView.findViewById(R.id.text1);
                convertView.setTag(tv);
            } else {
                tv = (TextView) convertView.getTag();
            }
            tv.setText(mClsdList.get(position));
            return convertView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(getActivity()).inflate(
                        R.layout.simple_spinner_item, parent, false);
                tv = (TextView) convertView.findViewById(R.id.text1);
                convertView.setTag(tv);
            } else {
                tv = (TextView) convertView.getTag();
            }
            tv.setText(mClsdList.get(position));
            return convertView;
        }

    }
}
