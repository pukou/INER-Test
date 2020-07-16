package com.bsoft.mob.ienr.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.adapter.PersonAdapter;
import com.bsoft.mob.ienr.api.BloodSugarApi;
import com.bsoft.mob.ienr.api.PatientApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.DateTimeTool;
import com.bsoft.mob.ienr.fragment.base.LeftMenuItemFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.ListViewScrollHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.helper.TestDataHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.bloodsugar.BloodSugar;
import com.bsoft.mob.ienr.model.bloodsugar.PersonBloodSugar;
import com.bsoft.mob.ienr.model.kernel.SickPersonVo;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar;
import com.bsoft.mob.ienr.view.expand.ClassicDropSelectEditView;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class BatchBloodSugarFragment extends LeftMenuItemFragment {
    private ListView mPersonListView;
    private ArrayList<SickPersonVo> mRawSickPersonVoList;

    private PersonAdapter mAdatper;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;


    private View sltStimeView;
    private View sltEtimeView;
    private TextView stime, etime;
    private ImageView searchBtn;
    private Spinner mSpinnerFiter;
    private CheckBox id_cb_sp;
    private LinearLayout speciment_content_ll;

    public BatchBloodSugarFragment() {
        // Required empty public constructor
    }

    private SpinnerAdapter mSpinnerFiterAdapter;

    public static BatchBloodSugarFragment newInstance(String param1, String param2) {
        BatchBloodSugarFragment fragment = new BatchBloodSugarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_batch_blood_sugar;
    }

    @Override
    protected void initView(View rootLayout, Bundle savedInstanceState) {
        initBarBroadCast();
        actionBar.setTitle("批量血糖录入");
        actionBar.addAction(new BsoftActionBar.Action() {
            @Override
            public int getDrawable() {
                return R.drawable.ic_mode_edit_black_24dp;
            }

            @Override
            public String getText() {
                return "待测";
            }

            @Override
            public void performAction(View view) {
                showDaiCe();
            }
        });
        actionBar.addAction(new BsoftActionBar.Action() {
            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }

            @Override
            public String getText() {
                return "保存";
            }

            @Override
            public void performAction(View view) {
                saveSuger();
            }
        });
        //
        mPersonListView = (ListView) rootLayout
                .findViewById(R.id.id_lv);
        EmptyViewHelper.setEmptyView(mPersonListView, "mPersonListView");
        // mSwipeRefreshLayoutEx = (SwipeRefreshLayoutEx) id_swipe_refresh_layout;
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mPersonListView);

        mPersonListView.setTextFilterEnabled(true);
        // checked/activated
        mPersonListView.setChoiceMode(
                AbsListView.CHOICE_MODE_SINGLE);
        mPersonListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                personSelected(position);

            }
        });
        speciment_content_ll = (LinearLayout) rootLayout.findViewById(R.id.speciment_content_ll);
//        speciment_content_ll.setVisibility(View.INVISIBLE);

        initShowView(rootLayout);


        TextView id_tv_for_bar_spinner = (TextView) rootLayout.findViewById(R.id.id_tv_for_bar_spinner);
        id_tv_for_bar_spinner.setText("时点");
        SpinnerLayout id_spinner_layout = (SpinnerLayout) rootLayout.findViewById(R.id.id_spinner_layout);
        mSpinnerFiter = id_spinner_layout.getSpinner();
        mSpinnerFiter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                toRefreshData();
                if (mClsdList == null) {
                    showMsgAndVoiceAndVibrator("时点数据未就绪，请稍后重试");
                    return;
                }
                StringBuilder arrS = new StringBuilder();
                List<String> clsdList = new ArrayList<>();
                for (String clsd : mClsdList) {
                    if (TextUtils.isEmpty(clsd)) {
                        //去掉第一个空白
                        continue;
                    }
                    if ("其他".equals(clsd)) {
                        //去掉"其他"
                        continue;
                    }
                    clsdList.add(clsd);
                }
                for (int i = 0; i < clsdList.size(); i++) {
                    String clsd = clsdList.get(i);
                    if (i >= position) {
                        continue;
                    }
                    arrS.append(clsd);
                    arrS.append(",");
                }
                if (arrS.toString().endsWith(",")) {
                    arrS.delete(arrS.lastIndexOf(","), arrS.length());
                }
                String zhiqian_clsdArrStr = arrS.toString();
                refreshDataInner(zhiqian_clsdArrStr);//手动选择
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpinnerFiterAdapter = new SpinnerAdapter();
        mSpinnerFiter.setAdapter(mSpinnerFiterAdapter);

        id_cb_sp = (CheckBox) rootLayout.findViewById(R.id.id_cb_sp);
        id_cb_sp.setChecked(true);
        id_cb_sp.setText("筛选");
        id_cb_sp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView == null || !buttonView.isPressed()) {
                    //不响应非点击引起的改变
                    return;
                }
                toRefreshData();
            }
        });
        // TODO: 2018/6/6

        searchBtn = (ImageView) rootLayout.findViewById(R.id.search);
        //
        getClsdListHttpTask();
        //
        toRefreshData();

    }

    DcjAdapter mDcjAdapter;

    //血糖:  床号 姓名  睡前
    class DcjAdapter extends BaseAdapter {
        private List<PersonBloodSugar> personBloodSugarList = new ArrayList<>();

        void refreshData(List<PersonBloodSugar> personBloist) {
            personBloodSugarList.clear();
            personBloodSugarList.addAll(personBloist);
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return personBloodSugarList.size();
        }

        @Override
        public Object getItem(int position) {
            return personBloodSugarList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolderCC vHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_text_three_primary, parent, false);
                vHolder = new ViewHolderCC();

                vHolder.ch = (TextView) convertView
                        .findViewById(R.id.id_tv_one);
                vHolder.xm = (TextView) convertView
                        .findViewById(R.id.id_tv_two);
                vHolder.sdmc = (TextView) convertView
                        .findViewById(R.id.id_tv_three);

                convertView.setTag(vHolder);
            } else {
                vHolder = (ViewHolderCC) convertView.getTag();
            }
            vHolder.ch.setText(personBloodSugarList.get(position).BRCH);
            vHolder.xm.setText(personBloodSugarList.get(position).BRXM);
            vHolder.sdmc.setText(personBloodSugarList.get(position).SDMC);
            if ("1".equals(personBloodSugarList.get(position).TXBZ)) {
                vHolder.ch.setTextColor(Color.RED);
                vHolder.xm.setTextColor(Color.RED);
                vHolder.sdmc.setTextColor(Color.RED);
            } else {
                vHolder.ch.setTextColor(ContextCompat.getColor(mContext, R.color.textColorPrimary));
                vHolder.xm.setTextColor(ContextCompat.getColor(mContext, R.color.textColorPrimary));
                vHolder.sdmc.setTextColor(ContextCompat.getColor(mContext, R.color.textColorPrimary));
            }
            return convertView;
        }
    }

    class ViewHolderCC {
        public TextView ch;
        public TextView xm;
        public TextView sdmc;
    }

    private AlertDialog alertDialog;

    private void showDaiCeReal(List<PersonBloodSugar> personBloodSugarList) {
        mDcjAdapter = new DcjAdapter();
        mDcjAdapter.refreshData(personBloodSugarList);
        ListView listView = new ListView(mContext);
        listView.setAdapter(mDcjAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mSpinnerFiter != null) {
                    //自动选择测量时点
                    PersonBloodSugar personBloodSugar = personBloodSugarList.get(position);
                    for (int i = 0; i < mSpinnerFiterAdapter.getCount(); i++) {
                        if (personBloodSugar.SDMC.equals(mSpinnerFiterAdapter.getItem(i))) {
                            mSpinnerFiter.setSelection(i);
                        }
                    }
                    //选中筛选
                    id_cb_sp.setChecked(true);
                    //选中病人Zyh
                    nowSelectedSickerZyh = personBloodSugar.ZYH;
                    //自定刷新
                    toRefreshData();
                    //qingchu
                    alertDialog.dismiss();
                }
            }
        });
        alertDialog = new AlertDialog.Builder(mContext)
                //.setTitle("待测量：")
                .setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext, "待测量列表"))
                .setView(listView)
                .setPositiveButton(mContext.getString(R.string.project_operate_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    private void showDaiCe() {
        if (mClsdList == null) {
            showMsgAndVoiceAndVibrator("时点数据未就绪，请稍后重试");
            return;
        }
        StringBuilder arrS = new StringBuilder();
        for (String clsd : mClsdList) {
            if (TextUtils.isEmpty(clsd)) {
                continue;
            }
            arrS.append(clsd);
            arrS.append(",");
        }
        if (arrS.toString().endsWith(",")) {
            arrS.delete(arrS.lastIndexOf(","), arrS.length());
        }
        String clsdArrStr = arrS.toString();
        fiterNeedGetDataArr(clsdArrStr);


    }


    private void personSelected(int position) {
        mPersonListView.setItemChecked(position, true);
        if (mPersonListView.getAdapter().getCount() <= position) {
            return;
        }
        SickPersonVo person = (SickPersonVo) mPersonListView
                .getAdapter().getItem(position);
        mAppApplication.sickPersonVo = person;

        nowSelectedSickerZyh = person.ZYH;
        //
        getAddRightItem();
    }

    private void fiterNeedGetDataPre() {
        if (mRawSickPersonVoList == null) {
            GetPersonTask task = new GetPersonTask("toFiterNeedGetData");
            tasks.add(task);
            task.execute();
        } else {
            //zhijie
            fiterNeedGetData();
        }
    }

    private void fiterNeedGetData() {
        if (mSpinnerFiter != null && mSpinnerFiter.getSelectedItem() != null) {
            String clsd = mSpinnerFiter.getSelectedItem().toString();
            NeedGetPersonTask task = new NeedGetPersonTask();
            tasks.add(task);
            task.execute(clsd);
        }
    }

    private void fiterNeedGetDataArr(String clsds) {
        if (clsds == null) {
            return;
        }
        NeedGetPersonArrTask task = new NeedGetPersonArrTask();
        tasks.add(task);
        task.execute(clsds);
    }

    private ViewHolderCC viewHolderCC;

    private class ViewHolder {
        //        private EditText CLZ;
        private ClassicDropSelectEditView CLZ;
        private TextView CLR;
        private EditText CLSJ_CUSTOM;
        private SpinnerLayout id_spinner_layout_CLSD;
        private TextView id_tv_for_bar_image;
        private TextView id_tv_2_for_bar_image;
        private ImageView id_iv_for_bar_image;
        private ListView id_lv_2;
        private TextView XMMC;
        private LinearLayout id_ll_xmmc;
  /*      private TextView id_tv_for_bar_image_copy;
        private TextView id_tv_2_for_bar_image_copy;
        private ImageView id_iv_for_bar_image_copy;*/
    }

    private boolean isCodeSetText;
    private BloodSugar item;
    private SpinnerAdapter mSpinnerAdapter;
    private ViewHolder viewHolder;

    private void initShowView(View convertView) {
        viewHolder = new ViewHolder();
        //
        viewHolder.id_lv_2 = convertView.findViewById(R.id.id_lv_2);
        EmptyViewHelper.setEmptyView(viewHolder.id_lv_2, "viewHolder.id_lv_2");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, viewHolder.id_lv_2);
        viewHolder.id_ll_xmmc = convertView.findViewById(R.id.id_ll_xmmc);
        viewHolder.id_ll_xmmc.setVisibility(View.GONE);
        viewHolder.XMMC = convertView.findViewById(R.id.XMMC);
//        viewHolder.CLZ = (EditText) convertView.findViewById(R.id.CLZ);
        viewHolder.CLZ = (ClassicDropSelectEditView) convertView.findViewById(R.id.CLZ);
        viewHolder.CLR = (TextView) convertView.findViewById(R.id.CLR);
        viewHolder.id_spinner_layout_CLSD = (SpinnerLayout) convertView.findViewById(R.id.id_spinner_layout_CLSD);
        viewHolder.id_spinner_layout_CLSD.getSpinner().setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (viewHolder.id_ll_xmmc != null) {
                    viewHolder.id_ll_xmmc.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        viewHolder.CLSJ_CUSTOM = (EditText) convertView.findViewById(R.id.CLSJ_CUSTOM);

        viewHolder.id_tv_for_bar_image = (TextView) convertView.findViewById(R.id.id_tv_for_bar_image);
        viewHolder.id_tv_2_for_bar_image = (TextView) convertView.findViewById(R.id.id_tv_2_for_bar_image);
        viewHolder.id_iv_for_bar_image = (ImageView) convertView.findViewById(R.id.id_iv_for_bar_image);
/*
        viewHolder.id_tv_for_bar_image_copy = (TextView) convertView.findViewById(R.id.id_tv_for_bar_image_copy);
        viewHolder.id_tv_2_for_bar_image_copy = (TextView) convertView.findViewById(R.id.id_tv_2_for_bar_image_copy);
        viewHolder.id_iv_for_bar_image_copy = (ImageView) convertView.findViewById(R.id.id_iv_for_bar_image_copy);
        //
        viewHolder.id_tv_for_bar_image.setText("测量时间点");
        viewHolder.id_tv_for_bar_image_copy.setText("测量时间");*/
        viewHolder.id_tv_for_bar_image.setText("测量时间");


        // 批量切换  不亲空
        viewHolder.CLSJ_CUSTOM.setHint("0930代表09点30分");
        viewHolder.CLSJ_CUSTOM.setInputType(InputType.TYPE_CLASS_NUMBER);
        // 批量切换  不亲空
        mSpinnerAdapter = new SpinnerAdapter();
        Spinner spinner = viewHolder.id_spinner_layout_CLSD.getSpinner();
        spinner.setAdapter(mSpinnerAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String initData = "";
                if (i == 0) {//选择第一行 设置时点
                    /*String[] array = item.SXSJ.split(" ");
                    if (array != null && array.length == 2) {
                        initData = array[1];
                        initData = TextUtils.isEmpty(initData)?"":initData.substring(0,
                                initData.lastIndexOf(":")).replace(":", "");
                    }*/
                }
             /*   String sd = TextUtils.isEmpty(vo.CLSD) ? initData : vo.CLSD;
                viewHolder.CLSJ_CUSTOM.setSelection(sd != null ? sd.length() : 0);*/
                if (i == 0) {
                    viewHolder.CLSJ_CUSTOM.setText(initData);
                } else {
                    viewHolder.CLSJ_CUSTOM.setText("");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void getAddRightItem() {
        //新增
        item = new BloodSugar();
        item.ZYH = mAppApplication.sickPersonVo.ZYH;
        item.SXBQ = application.getAreaId();
        item.BRCH = mAppApplication.sickPersonVo.XSCH;
        item.SXSJ = DateTimeHelper.getServer_yyyyMMddHHmm00();
        item.SXGH = application.user.YHID;
        item.SXXM = application.user.YHXM;
        item.CJGH = application.user.YHID;
        item.CLSD = "";
        item.CLZ = "";
        item.JGID = application.jgId;
        //
        viewHolder.CLR.setText(item.SXXM);
        if (!item.SXGH.equals(application.user.YHID)) {
            viewHolder.CLZ.setEnabled(false);
            Toast.makeText(mContext, "书写工号不一致，不修改测量值", Toast.LENGTH_SHORT).show();
        }
//        viewHolder.id_tv_2_for_bar_image.setText(item.CLSD);
//        viewHolder.id_tv_2_for_bar_image_copy.setText(vo.SXSJ.replace("/", "-"));
        viewHolder.id_tv_2_for_bar_image.setText(TextUtils.isEmpty(item.SXSJ) ? "" : item.SXSJ.replace("/", ""));

        StringBuilder xmmcSb = new StringBuilder();
        String xmmc = "";
        if (mAppApplication.sickPersonVo != null && mAppApplication.sickPersonVo.personalBloodSugarList != null) {
            for (PersonBloodSugar personBloodSugar : mAppApplication.sickPersonVo.personalBloodSugarList) {
                xmmcSb.append(personBloodSugar.YZMC);
                if (!TextUtils.isEmpty(personBloodSugar.YSZT)) {
                    xmmcSb.append("(");
                    xmmcSb.append(personBloodSugar.YSZT);
                    xmmcSb.append(")");
                }
                xmmcSb.append("\n");
            }
            xmmc = xmmcSb.toString();
            xmmc = xmmc.endsWith("\n") ? xmmc.substring(0, xmmc.length() - 1) : xmmc;
        }
        if (!TextUtils.isEmpty(xmmc)) {
            viewHolder.XMMC.setText(xmmc);
            viewHolder.id_ll_xmmc.setVisibility(View.VISIBLE);
        } else {
            viewHolder.id_ll_xmmc.setVisibility(View.GONE);
        }

        isCodeSetText = true;

        Pair<String, String> stringStringPair1 = Pair.create("1", "拒测");
        Pair<String, String> stringStringPair2 = Pair.create("2", "不在");
        List<Pair<String, String>> pairList = new ArrayList<>();
        pairList.add(stringStringPair1);
        pairList.add(stringStringPair2);
        viewHolder.CLZ.setupDropDownSelectData(pairList);
        //默认显示数字键盘
        EditText clz_edit = viewHolder.CLZ.getClassicInputLayout().getInput();
        clz_edit.setRawInputType(Configuration.KEYBOARD_QWERTY);
        viewHolder.CLZ.setText(item.CLZ);
        isCodeSetText = false;

        clz_edit.setSelection(TextUtils.isEmpty(item.CLZ) ? 0 : item.CLZ.length());
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
                item.CLZ = vss;
            }
        });

        viewHolder.id_iv_for_bar_image.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (item.SXGH.equals(application.user.YHID)) {
                    showDateTimePickerCompat(item.SXSJ, view.getId());
                } else {
                    showMsgAndVoiceAndVibrator("不允许编辑其他用户录入的数据!");
                }
            }
        });

        //切换病人时候
        Spinner spinner = viewHolder.id_spinner_layout_CLSD.getSpinner();
//        spinner.setSelection(0);//复位
        if (id_cb_sp.isChecked())

        {
            String fiterStr = mSpinnerFiter.getSelectedItem().toString();
            spinner.setSelection(mClsdList.indexOf(fiterStr));
//            spinner.setSelection(mClsdList.indexOf(fiterStr), false);
        }
        //
        if (speciment_content_ll != null) {
            speciment_content_ll.setVisibility(View.VISIBLE);
        }
        //
        actionGetHistoryHttpTask();

    }


    public View.OnClickListener onClickListener = new View.OnClickListener() {

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
    //不包括其他
    private List<String> mClsdList;

    class GetClsdListHttpTask extends AsyncTask<Void, Void, Response<List<String>>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Response<List<String>> doInBackground(Void... arg0) {

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
                showMsgAndVoiceAndVibrator("测量时点加载失败");
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
                mSpinnerAdapter.refreshData(mClsdList);
                List<String> CCC = new ArrayList<>(mClsdList);
                if (CCC != null && !CCC.isEmpty()) {
                    CCC.remove(0);
                }
//                CCC.add("其他"); 去除“其他”，重改血糖监测模块
                mSpinnerFiterAdapter.refreshData(CCC);
            } else {
                showMsgAndVoice(result.Msg);
                return;
            }

        }
    }


    class GetHistoryHttpTask extends AsyncTask<String, Void, Response<List<BloodSugar>>> {
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
                        actionGetHistoryHttpTask();
                    }
                }).showLoginDialog();
                return;
            } else if (result.ReType == 0) {
                mHistoryBloodSugarList = (ArrayList<BloodSugar>) result.Data;
                BloodSugarAdapter adapter = new BloodSugarAdapter(mContext, mHistoryBloodSugarList);
                viewHolder.id_lv_2.setAdapter(adapter);
                viewHolder.id_lv_2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        BloodSugar vo = (BloodSugar) parent.getAdapter().getItem(position);
                        itemId = position;
                        showEditDialog(vo);
                    }
                });
                viewHolder.id_lv_2.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        BloodSugar vo = (BloodSugar) parent.getAdapter().getItem(position);
                        itemId = position;
                        if (vo.SXGH.equals(application.user.YHID)) {
                            new AlertDialog.Builder(mContext)
                                    .setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext, "温馨提示"))
                                    .setMessage("是否删除"+vo.BRCH+"的"+(TextUtils.isEmpty(vo.CLSD)?"":vo.CLSD)+"数据:"+vo.CLZ)
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
                        //
                        return true;
                    }
                });
//                viewHolder.id_lv_2.setVisibility(View.VISIBLE);
            } else {
                showMsgAndVoice(result.Msg);
            }

        }
    }

    private boolean isFirst;
    private int itemId;
    private ArrayList<BloodSugar> mHistoryBloodSugarList;

    private void showEditDialog(BloodSugar vo) {
        isFirst = true;
        View convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_bloodsugar, null, false);
        ViewHolder viewHolder = new ViewHolder();
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

        viewHolder.id_iv_for_bar_image.setOnClickListener(new View.OnClickListener() {
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
        adapter.refreshData(mClsdList);
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

    private class BloodSugarAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        Context mContext;

        private ArrayList<BloodSugar> mList = new ArrayList<>();

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
            final BloodSugarAdapter.ViewHolder vHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_list_bloodsugar_list, parent, false);
                vHolder = new BloodSugarAdapter.ViewHolder();
                //
                vHolder.id_tv_sd = (TextView) convertView.findViewById(R.id.id_tv_sd);
                vHolder.id_tv_value = (TextView) convertView.findViewById(R.id.id_tv_value);
                vHolder.id_tv_clr = (TextView) convertView.findViewById(R.id.id_tv_clr);
                vHolder.id_tv_sj = (TextView) convertView.findViewById(R.id.id_tv_sj);
                vHolder.id_iv_delete = (ImageView) convertView.findViewById(R.id.id_iv_delete);
                vHolder.id_iv_delete.setVisibility(View.GONE);
                convertView.setTag(vHolder);
            } else {
                vHolder = (BloodSugarAdapter.ViewHolder) convertView.getTag();
            }

            final BloodSugar vo = mList.get(position);
            vHolder.id_tv_value.setText(vo.CLZ);
            vHolder.id_tv_clr.setText(vo.SXXM);
            String ss = vo.SXSJ.replace("/", "-");
            ss = DateTimeTool.dateTime2Custom(ss, "MM-dd HH:mm");
            vHolder.id_tv_sj.setText(ss);
            vHolder.id_tv_sd.setText(vo.CLSD);
        /*    vHolder.id_iv_delete.setOnClickListener(new View.OnClickListener() {
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
                        showMsgAndVoice("不允许删除其他用户录入的数据!");
                    }
                }
            });*/
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


    public void actionGetHistoryHttpTask() {
     /*   SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String start = format.format(application.getServiceFixedTime());*/
        //

        String nowDate = DateTimeHelper.getServerDate();
        // 当天
        String end = nowDate;
        // 前天
        String start = DateTimeHelper.dateAddedDays(nowDate, -5);
        //
        GetHistoryHttpTask getHttpTask = new GetHistoryHttpTask();
        tasks.add(getHttpTask);
        getHttpTask.execute(start, end);
    }

    public void getClsdListHttpTask() {
        if (mClsdList != null && !mClsdList.isEmpty()) {
            return;
        }
        GetClsdListHttpTask getClsdListHttpTask = new GetClsdListHttpTask();
        tasks.add(getClsdListHttpTask);
        getClsdListHttpTask.execute();
    }

    private class SpinnerAdapter extends BaseAdapter {
        private List<String> clsdList = new ArrayList<>();

        public void refreshData(List<String> clsdList) {
            this.clsdList.clear();
            this.clsdList.addAll(clsdList);
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return clsdList == null ? 0 : clsdList.size();
        }

        @Override
        public String getItem(int position) {
            return clsdList.get(position);
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
            tv.setText(clsdList.get(position));
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
            tv.setText(clsdList.get(position));
            return convertView;
        }

    }


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

    }

    @Override
    public void onDateTimeSet(int year, int monthOfYear, int dayOfMonth,
                              int hourOfDay, int minute, int viewId) {
        super.onDateTimeSet(year, monthOfYear, dayOfMonth, hourOfDay, minute,
                viewId);
        String nowDateTimeSelectStr = year
                + "-"
                + ((monthOfYear + 1) < 10 ? ("0" + (monthOfYear + 1))
                : (monthOfYear + 1)) + "-"
                + (dayOfMonth < 10 ? ("0" + dayOfMonth) : dayOfMonth) + " "
                + (hourOfDay < 10 ? ("0" + hourOfDay) : hourOfDay) + ":"
                + (minute < 10 ? ("0" + minute) : minute)
                + ":00";
        String nowDateTimeSelectStr_HHmm = String.format(Locale.CHINA, "%s%s",
                (hourOfDay < 10 ? ("0" + hourOfDay) : hourOfDay),
                (minute < 10 ? ("0" + minute) : minute));
//        mBloodSugarAdapter.refreshDataList(mBloodSugarList);
        if (viewHolder != null) {
            viewHolder.id_tv_2_for_bar_image.setText(nowDateTimeSelectStr);
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

    private String nowSelectedSickerZyh = null;


    private void saveSuger() {
        //保存
        if (TextUtils.isEmpty(nowSelectedSickerZyh)) {
            showMsgAndVoiceAndVibrator("请先选择病人!");
            return;
        }

        //执行赋值操作
        if (EmptyTool.isBlank(viewHolder.CLSJ_CUSTOM.getText().toString().trim())) {
            item.CLSD = String.valueOf(viewHolder.id_spinner_layout_CLSD.getSpinner().getSelectedItem());
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
            item.CLSD = clsj;
        }

        if (item.SXGH.equals(application.user.YHID)) {
            if (TextUtils.isEmpty(item.CLZ)) {
                showMsgAndVoiceAndVibrator("测量值不能为空!");
                return;
            }
            if (EmptyTool.isBlank(item.JLXH)) {
                addHttpTask(item);
            } else {
                //###editHttpTask();
            }
        } else {
            showMsgAndVoiceAndVibrator("不允许编辑其他用户录入的数据!");
        }
    }

    public void addHttpTask(BloodSugar addBloodSugarItem) {
        AddHttpTask addHttpTask = new AddHttpTask(addBloodSugarItem);
        tasks.add(addHttpTask);
        addHttpTask.execute();
    }

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

            if (mAppApplication.sickPersonVo == null) {
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
            return BloodSugarApi.getInstance(getActivity()).AddBloodSugar(mAppApplication.sickPersonVo.ZYH,
                    application.getAreaId(), mAppApplication.sickPersonVo.BRCH, sxsj,
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
                //清空
//                getAddRightItem();
                toRefreshData();
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

            BloodSugar item = mHistoryBloodSugarList.get(itemId);
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
                //
                actionGetHistoryHttpTask();
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

            return BloodSugarApi.getInstance(getActivity())
                    .DeleteBloodSugar(mHistoryBloodSugarList.get(itemId).JLXH);
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
                actionGetHistoryHttpTask();
            } else {
                showMsgAndVoice(result.Msg);

                return;
            }

        }
    }

    private String zhiqian_clsdArrStr;

    protected void refreshDataInner(String zhiqian_clsdArrStr) {
        this.zhiqian_clsdArrStr = zhiqian_clsdArrStr;
        if (id_cb_sp.isChecked()) {
            //筛选
            fiterNeedGetDataPre();
        } else {
            actionGetPersonData();
        }
        /*if (viewHolder.id_lv_2 != null) {
            viewHolder.id_lv_2.setVisibility(View.GONE);
        }*/
        if (speciment_content_ll != null) {
//            speciment_content_ll.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void toRefreshData() {
        refreshDataInner("");
    }

    private void actionGetPersonData() {
        if (mRawSickPersonVoList == null) {
            GetPersonTask task = new GetPersonTask();
            tasks.add(task);
            task.execute();
        } else {
            hideSwipeRefreshLayout();
            importPersons(mRawSickPersonVoList);
        }
    }


    /**
     * 病人列表异步加载
     */
    private class GetPersonTask extends AsyncTask<Void, Void, Response<ArrayList<SickPersonVo>>> {
        public GetPersonTask(String toWhere) {
            this.toWhere = toWhere;
        }

        public GetPersonTask() {
        }

        private String toWhere;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<ArrayList<SickPersonVo>> doInBackground(Void... params) {

            String bqdm = mAppApplication.getAreaId();
            String jgid = mAppApplication.jgId;
            int sysType = Constant.sysType;

            return PatientApi.getInstance(getActivity()).GetPatientList(
                    bqdm, 0, -1, -1, null, jgid);
        }

        @Override
        protected void onPostExecute(Response<ArrayList<SickPersonVo>> result) {
            super.onPostExecute(result);

            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：请求参数错误");
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
            }
            if (result.ReType == 0) {
                mRawSickPersonVoList = result.Data;
                if (EmptyTool.isEmpty(mRawSickPersonVoList)) {
                    mRawSickPersonVoList = new ArrayList<>();
                    TestDataHelper.buidTestData(SickPersonVo.class, mRawSickPersonVoList);
                    //##toastInfo("病人列表为空", Style.INFO, R.id.actionbar);
                }
                if (toWhere.equals("toFiterNeedGetData")) {
                    //继续
                    fiterNeedGetData();
                } else {
                    importPersons(mRawSickPersonVoList);
                }
            } else {
                showTipDialog(result.Msg);
//                AlertBox.Show(getActivity(), getString(R.string.project_tips), result.Msg, getString(R.string.project_operate_ok));
            }

        }
    }

    private class NeedGetPersonTask extends AsyncTask<String, Void, Response<List<PersonBloodSugar>>> {
        public String clsdCN;
        public String nowZyh = "";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<PersonBloodSugar>> doInBackground(String... params) {
            clsdCN = params[0];
            String bqdm = mAppApplication.getAreaId();
            String jgid = mAppApplication.jgId;
            String clsd = "";
            if (!EmptyTool.isBlank(clsdCN)) {
                try {
                    clsd = URLEncoder.encode(clsdCN, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            String preClsdList = zhiqian_clsdArrStr;
            if (!EmptyTool.isBlank(preClsdList)) {
                try {
                    preClsdList = URLEncoder.encode(preClsdList, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            if (nowSelectedSickerZyh != null) {
                nowZyh = nowSelectedSickerZyh;
            }
            return BloodSugarApi.getInstance(getActivity()).getNeedGetBloodSugarList(
                    bqdm, clsd, jgid, preClsdList, nowZyh);
        }

        @Override
        protected void onPostExecute(Response<List<PersonBloodSugar>> result) {
            super.onPostExecute(result);

            hideSwipeRefreshLayout();
            tasks.remove(this);
            //CLEAR
            zhiqian_clsdArrStr = null;
            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：请求参数错误");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        fiterNeedGetDataPre();
                    }
                }).showLoginDialog();
                return;
            }
            if (result.ReType == 0) {
                List<PersonBloodSugar> personBloodSugarList = result.Data;
                if (EmptyTool.isEmpty(personBloodSugarList)) {
                    personBloodSugarList = new ArrayList<>();
                    TestDataHelper.buidTestData(PersonBloodSugar.class, personBloodSugarList);
                    //##toastInfo("病人列表为空", Style.INFO, R.id.actionbar);
                }

                //筛选
                ArrayList<SickPersonVo> listSickPersonVo = new ArrayList();
                for (SickPersonVo sickPersonVo : mRawSickPersonVoList) {
                    //
                    List<PersonBloodSugar> personalList = new ArrayList<>();
                    for (PersonBloodSugar personBloodSugar : personBloodSugarList) {
                        if (personBloodSugar.ZYH.equals(sickPersonVo.ZYH)) {
                            personalList.add(personBloodSugar);
                        }
                    }
                    //
                    if (!personalList.isEmpty()) {
                        sickPersonVo.personalBloodSugarList = personalList;
                        listSickPersonVo.add(sickPersonVo);
                    }
                }
                importPersons(listSickPersonVo);
                //
                if (!TextUtils.isEmpty(result.Msg) && result.Msg.contains("该病人遗漏的时点")
                        &&!listSickPersonVo.isEmpty()&&nowZyh.equals(nowSelectedSickerZyh)) {
                    //切换时点后 病人还在 定位的病人还是请求数据传过去的病人
                    String needTxCLSD = result.Msg;
                    showTipDialog(needTxCLSD);
                }
            } else {
                showTipDialog(result.Msg);
//                AlertBox.Show(getActivity(), getString(R.string.project_tips), result.Msg, getString(R.string.project_operate_ok));
            }

        }

    }

    private class NeedGetPersonArrTask extends AsyncTask<String, Void, Response<List<PersonBloodSugar>>> {
        public String clsdCNArr;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(R.string.loading);
        }

        @Override
        protected Response<List<PersonBloodSugar>> doInBackground(String... params) {
            clsdCNArr = params[0];
            String bqdm = mAppApplication.getAreaId();
            String jgid = mAppApplication.jgId;
            String clsds = "";
            if (!EmptyTool.isBlank(clsdCNArr)) {
                try {
                    clsds = URLEncoder.encode(clsdCNArr, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            return BloodSugarApi.getInstance(getActivity()).getNeedGetBloodSugarListArr(
                    bqdm, clsds, jgid);
        }

        @Override
        protected void onPostExecute(Response<List<PersonBloodSugar>> result) {
            super.onPostExecute(result);

            hideLoadingDialog();
            tasks.remove(this);
            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：请求参数错误");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        showDaiCe();
                    }
                }).showLoginDialog();
                return;
            }
            if (result.ReType == 0) {
                List<PersonBloodSugar> personBloodSugarList = result.Data;
                if (EmptyTool.isEmpty(personBloodSugarList)) {
                    personBloodSugarList = new ArrayList<>();
                    TestDataHelper.buidTestData(PersonBloodSugar.class, personBloodSugarList);
                    //##toastInfo("病人列表为空", Style.INFO, R.id.actionbar);
                }
                if (personBloodSugarList != null && !personBloodSugarList.isEmpty()) {
                    //筛选
                    showDaiCeReal(personBloodSugarList);
                    //
                } else {
                    showMsgAndVoiceAndVibrator("暂无待测量数据！");
                }
            } else {
                showTipDialog(result.Msg);
//                AlertBox.Show(getActivity(), getString(R.string.project_tips), result.Msg, getString(R.string.project_operate_ok));
            }

        }

    }

    private void importPersons(ArrayList<SickPersonVo> list) {
        mAdatper = new PersonAdapter(mContext, list);
        mPersonListView.setAdapter(mAdatper);

        if (nowSelectedSickerZyh != null) {
            int position = -1;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).ZYH.equals(nowSelectedSickerZyh)) {
                    position = i;
                }
            }
            //add 2018-6-21 17:18:13
            if (position < 0 && list.size() > 0) {
                //当前选择的病人不再需要显示时候，且还存在病人，默认选择第一个病人
                position = 0;
            }
            //add 2018-6-21 17:18:13
            if (position >= 0) {
                //  定位病人
                ListViewScrollHelper.smoothScrollToPosition(mPersonListView, position);
                personSelected(position);
            }

        }
    }


    /**
     * 扫描定位病人,并查询
     */
    private void scanSltPerson() {
        SickPersonVo person = mAppApplication.sickPersonVo;
        if (person == null || mAdatper == null) {
            /*VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "扫描的病人不在此列表中", getActivity());*/
            showMsgAndVoiceAndVibrator("扫描的病人不在此列表中");
            return;
        }
        int position = mAdatper.getPersonPostion(person.ZYH);
        if (position == -1) {
            /*VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "扫描的病人不在此列表中", getActivity());*/
            showMsgAndVoiceAndVibrator("扫描的病人不在此列表中");
            return;
        }
        personSelected(position);
        //
        ListViewScrollHelper.smoothScrollToPosition(mPersonListView, position);


    }

    private void queryWithAction(int position) {
//        performGetSpecimentTask（）；

    }

    private void initBarBroadCast() {

        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                if (BarcodeActions.Refresh.equals(intent.getAction())) {
                    scanSltPerson();
                } else if (BarcodeActions.Bar_Get.equals(intent.getAction())) {

                    BarcodeEntity entity = (BarcodeEntity) intent
                            .getParcelableExtra("barinfo");

                    if (FastSwitchUtils.needFastSwitch(entity)) {
                        if (mAppApplication.sickPersonVo == null) {
                            return;
                        }
                        FastSwitchUtils.fastSwith(getActivity(), entity);
                    }


                }

            }
        };
    }


}

