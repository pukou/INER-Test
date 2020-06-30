package com.bsoft.mob.ienr.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.adapter.PersonAdapter;
import com.bsoft.mob.ienr.adapter.SpecimenListAdapter;
import com.bsoft.mob.ienr.api.InspectionApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.fragment.base.LeftMenuItemFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.ListViewScrollHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.helper.TestDataHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.inspection.CYInfoBean;
import com.bsoft.mob.ienr.model.inspection.SpecimenVo;
import com.bsoft.mob.ienr.model.kernel.SickPersonVo;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.SpecimenUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;
import com.classichu.dialogview.listener.OnBtnClickListener;
import com.classichu.dialogview.manager.DialogManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * 批量 标本集采 Created by hy on 14-3-21.
 */
public class BatchSpecimenFragment extends LeftMenuItemFragment {


    private ListView mPersonListView;

    private ListView mListView;


    private SpecimenListAdapter adapter;

    private PersonAdapter mAdatper;

    private ArrayList<SpecimenVo> list;

    //private  BarCodeInfo barinfo;

    private Spinner mSpinner_type;
    private Spinner mSpinner_statue;

    private View sltStimeView;
    private View sltEtimeView;

    private TextView stime;
    private TextView etime;

    private ImageView searchBtn;


    private View timeView;
    private SpecimenVo item;
    private String nowSelectedSickerZyhm = null;
    private CheckBox cb_all;
    private CheckBox cb_need;

    private ArrayList<SickPersonVo> mRawSickPersonVoList;

    private int mCheckBoxFiter = 0;//0 待采集  1 全部
    private int mTypeFilterPos;// 0 全部 1 血液 2 24小时尿液  3 其他
    private int mStatusFilterPos;// 0 待发放 1 待采集 2 已采集
//    private int mStatusFilterPos;// 0 待采集 1 已采集

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_batch_speciment;
    }

    //SwipeRefreshLayoutEx mSwipeRefreshLayoutEx;


 /*   public void setNowSelectedSickPos(int nowSelectedSickPos) {
        if (fiter == 0) {
            this.nowSelectedSickPos_All = nowSelectedSickPos;
        } else {
            this.nowSelectedSickPos_Fiter = nowSelectedSickPos;
        }
    }

    public int getNowSelectedSickPos() {
        if (fiter == 0) {
            return nowSelectedSickPos_All;
        } else {
            return nowSelectedSickPos_Fiter;
        }
    }*/

    @Override
    protected void initView(View mainView, Bundle savedInstanceState) {


        mPersonListView = (ListView) mainView
                .findViewById(R.id.id_lv);
        EmptyViewHelper.setEmptyView(mPersonListView, "mPersonListView");
        // mSwipeRefreshLayoutEx = (SwipeRefreshLayoutEx) id_swipe_refresh_layout;
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mPersonListView);


        mListView = (ListView) mainView
                .findViewById(R.id.id_lv_2);

        EmptyViewHelper.setEmptyView(mListView, "mListView");

        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mListView);
        SpinnerLayout id_spinner_layout_type = (SpinnerLayout) mainView.findViewById(R.id.id_spinner_layout_type);
        mSpinner_type = id_spinner_layout_type.getSpinner();
        //标准版 id_spinner_layout_type 不要
        id_spinner_layout_type.setVisibility(View.GONE);
        SpinnerLayout spinnerLayout = (SpinnerLayout) mainView.findViewById(R.id.id_spinner_layout);
        mSpinner_statue = spinnerLayout.getSpinner();

        sltStimeView = mainView.findViewById(R.id.slt_stime_ly);
        sltEtimeView = mainView.findViewById(R.id.slt_etime_ly);

        stime = (TextView) mainView.findViewById(R.id.stime);
        etime = (TextView) mainView.findViewById(R.id.etime);
        final TextView stimeTitle = (TextView) mainView
                .findViewById(R.id.stime_title);
        final TextView etimeTitle = (TextView) mainView
                .findViewById(R.id.etime_title);

        stimeTitle.setText(R.string.start_time);
        etimeTitle.setText(R.string.end_time);

        searchBtn = (ImageView) mainView.findViewById(R.id.search);

        timeView = mainView.findViewById(R.id.id_layout_double_time);

        TextView id_tv = (TextView) mainView.findViewById(R.id.id_tv);
        id_tv.setText("采集类型：");
        cb_all = (CheckBox) mainView.findViewById(R.id.id_cb_2);
        cb_all.setText("全部");
        cb_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCheckBoxFiter = isChecked ? 1 : 0;
                cb_need.setChecked(!isChecked);
                toRefreshData();
            }
        });
        cb_need = (CheckBox) mainView.findViewById(R.id.id_cb);
        cb_need.setText("待采集");
        cb_need.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCheckBoxFiter = isChecked ? 0 : 1;
                cb_all.setChecked(!isChecked);
                toRefreshData();
            }
        });

        initSpecimentListView();
        initPersonListView();
        initActionBar();
        initSpinner_statue();
        initSpinner_type();
        initTime();
        initSearchBtn();

        toRefreshData();
    }


    private void initTime() {

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) sltEtimeView
                .getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        sltEtimeView.setLayoutParams(params);

        String nowDate = DateTimeHelper.getServerDate();
        // 当天
        String eTimeStr = nowDate;
        etime.setText(eTimeStr);

        // 前天
        String startDate= DateTimeHelper.dateAddedDays(nowDate,-6);
        String sTimeStr = startDate;
        stime.setText(sTimeStr);

        sltStimeView.setOnClickListener(onClickListener);
        sltEtimeView.setOnClickListener(onClickListener);
        //默认不显示
        timeView.setVisibility(View.GONE);
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

    private void initTimeTxt(String dateStr, int viewId) {
        String timeStr = dateStr;
        if (viewId == R.id.slt_etime_ly) {
            etime.setText(timeStr);
        } else if (viewId == R.id.slt_stime_ly) {
            stime.setText(timeStr);
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

    private void initSearchBtn() {

        searchBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                queryWithAction();
            }
        });
    }


    private void performGetSpecimentTask(Byte... params) {
        GetSpecimentTask getHttpTask = new GetSpecimentTask();
        tasks.add(getHttpTask);
        getHttpTask.execute(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initBarBroadCast();

    }

    @Override
    protected void toRefreshData() {
        GetPersonTask task = new GetPersonTask();
        tasks.add(task);
        task.execute();
    }


    private void initPersonListView() {


        mPersonListView.setTextFilterEnabled(true);
        // checked/activated
        mPersonListView.setChoiceMode(
                AbsListView.CHOICE_MODE_SINGLE);


        mPersonListView.setOnItemClickListener(onPersonItemClickListener);
    }

    public OnItemClickListener onPersonItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            personSelected(position);
        }
    };

    private void initActionBar() {

        actionBar.setTitle("标本采集");


    }

    @Override
    public void onStart() {
        super.onStart();
        toastInfo(getResources().getString(R.string.long_save_tip));
    }

    private SickPersonVo getSelectPerson() {

        int position = mPersonListView
                .getCheckedItemPosition();
        if (position == AdapterView.INVALID_POSITION) {
            return null;
        }
        SickPersonVo person = (SickPersonVo) mPersonListView
                .getAdapter().getItem(position);
        return person;
    }


    private void initSpecimentListView() {

        mListView.setOnItemLongClickListener(
                new OnItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent,
                                                   View view, int position, long id) {

                        item = adapter.getItem(position);
                        if (item != null) {
                            actionWithType(item, false);
                            return true;
                        }
                        return false;
                    }
                });

/*		mListView
                .setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {

						String label = DateUtils.formatDateTime(getActivity(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);

						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						queryWithAction(mSpinner_statue.getSelectedItemPosition());
					}
				});*/
    }

    /**
     * 根据当前状态，执行相应操作
     *
     * @param item
     */
    private void actionWithType(SpecimenVo item, boolean isScan) {

        if (item == null) {
            return;
        }
        if (item.FFZT != -1) { // 执行和发放

            int positon = mSpinner_statue.getSelectedItemPosition();
            if (positon == 0) {
                performActionTask(ActionSpecimenTask.ACTION_DELIVERY_SPECIMENT,
                        item.TMBH, String.valueOf(isScan));
            } else if (positon == 1) {
                performActionTask(ActionSpecimenTask.ACTION_EXECUTE_SPECIMENT,
                        item.TMBH, String.valueOf(isScan));
            }

        } else { // 取消
            DialogManager.showClassicDialog(mFragmentActivity, "", "是否确认取消?", new OnBtnClickListener() {
                @Override
                public void onBtnClickOk(DialogInterface dialogInterface) {
                    //
                    performActionTask(ActionSpecimenTask.ACTION_CANCEL_SPECIMENT,
                            item.TMBH);
                    //
                }
            });
        }
    }

    private void performActionTask(byte type, String... params) {
        ActionSpecimenTask task = new ActionSpecimenTask(type);
        tasks.add(task);
        task.execute(params);
    }

    private void initSpinner_statue() {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                mContext, R.array.speciment_actions_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner_statue.setAdapter(adapter);
        mSpinner_statue.setOnItemSelectedListener(onOSListener);
        /*
        升级编号【56010012】============================================= start
        打开默认选中‘待采集列表’;
        ================= Classichu 2017/10/16 15:31
        */
        //选中【待采集列表】
        if (mSpinner_statue.getCount() > 1) {
            mSpinner_statue.setSelection(1, true);
        }
        /* =============================================================== end */
    }

    private void initSpinner_type() {

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                mContext, R.array.speciment_types_array,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner_type.setAdapter(adapter);
        mSpinner_type.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mTypeFilterPos = position;
                toRefreshData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mSpinner_type.setAdapter(adapter);
        //

    }


    private OnItemSelectedListener onOSListener = new OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {

            mStatusFilterPos = position;
            toRefreshData();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    };

    private void queryWithAction() {
        int position = mSpinner_statue.getSelectedItemPosition();
        if (position == 2) {// 已执行
            timeView.setVisibility(View.VISIBLE);
            performGetSpecimentTask(GetSpecimentTask.GET_ALREALY_ACTION_SPECIMENT);
        } else if (position == 0) {// 待发放
            timeView.setVisibility(View.GONE);
            performGetSpecimentTask(GetSpecimentTask.GET_WAIT_DELIEVERY_SPECIMENT);
        } else if (position == 1) {// 待采集
            timeView.setVisibility(View.GONE);
            performGetSpecimentTask(GetSpecimentTask.GET_WAIT_ACTION_SPECIMENT);
        }

    }

    /**
     * 扫描定位病人,并查询标本数据
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
        //扫描 定位病人
        ListViewScrollHelper.smoothScrollToPosition(mPersonListView, position);
        //选择病人
        personSelected(position);

    }

    void personSelected(int position) {
        mPersonListView.setItemChecked(position,
                true);
        SickPersonVo sickPersonVo = getSelectPerson();
        nowSelectedSickerZyhm = sickPersonVo.ZYHM;
        queryWithAction();
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

                    if (entity.TMFL == 2 && entity.FLBS == 2) {
                        if (adapter == null) {
                            return;
                        }
                        //转换条码
						 /*
            升级编号【56010013】============================================= start
            标本采集：是否需求转换条码:需要加入参数控制：是否需求转换条码
            ================= Classichu 2017/10/18 9:34
            */
                        String tmbh = SpecimenUtil.getTmbh(getActivity(), entity.source,
                                DateTimeHelper.getServerDate());
                        /* =============================================================== end */
                        if (tmbh == null) {
							/*VibratorUtil.vibratorMsg(
									mAppApplication.getSettingConfig().vib,
									"转换条码编号失败", getActivity());*/
                            showMsgAndVoiceAndVibrator("转换条码编号失败");
                            return;
                        }

                        SpecimenVo vo = adapter.contain(tmbh);
                        if (null != vo) {
                            actionWithType(vo, true);
                        } else {
							/*VibratorUtil.vibratorMsg(
									mAppApplication.getSettingConfig().vib,
									"条码不在此列表中", getActivity());*/
//          #####                  showMsgAndVoice("条码不在此列表中");
                            //查找該條碼信息
                            doActionQueryTask(tmbh);
                        }
                    } else if (FastSwitchUtils.needFastSwitch(entity)) {
                        if (mAppApplication.sickPersonVo == null) {
                            return;
                        }
                        FastSwitchUtils.fastSwith(getActivity(), entity);
                    }

                }
            }
        };
    }

    public void toastInfo(String msg) {
        showSnack(msg);
        //Crouton.showText(getActivity(), msg, style, viewGroupId);
    }

    /*   public ArrayList<SpecimenVo> filterType(ArrayList<SpecimenVo> tList) {
           if (TextUtils.isEmpty(mTypeFilter)) {
               return tList;
           }
           if ("血液".equals(mTypeFilter)) {
               ArrayList<SpecimenVo> tempList = new ArrayList<>();
               for (SpecimenVo specimenVo : tList) {
                   if ("1".equals(specimenVo.BBFL)) {
                       tempList.add(specimenVo);
                   }
               }
               return tempList;
           }
           if ("24小时尿液".equals(mTypeFilter)) {
               ArrayList<SpecimenVo> tempList = new ArrayList<>();
               for (SpecimenVo specimenVo : tList) {
                   if ("24".equals(specimenVo.JYLX)) {
                       tempList.add(specimenVo);
                   }
               }
               return tempList;
           }
           if ("其他".equals(mTypeFilter)) {
               ArrayList<SpecimenVo> tempList = new ArrayList<>();
               for (SpecimenVo specimenVo : tList) {
                   if (!"1".equals(specimenVo.BBFL) && !"24".equals(specimenVo.JYLX)) {
                       tempList.add(specimenVo);
                   }
               }
               return tempList;
           }
           return tList;
       }
   */
    private void importSpecimentList(ArrayList<SpecimenVo> tList) {
//        tList = filterType(tList);
        String brxxMy = "";
        SickPersonVo sickPersonVo = getSelectPerson();
        if (sickPersonVo != null) {
            brxxMy = sickPersonVo.BRXM + " " + sickPersonVo.BRCH;
        }
        adapter = new SpecimenListAdapter(getActivity(), tList, brxxMy);
        mListView.setAdapter(adapter);
    }


    /**
     * 病人列表异步加载
     */
    private class GetPersonTask extends AsyncTask<Void, Void, Response<List<SickPersonVo>>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<SickPersonVo>> doInBackground(Void... params) {

            String bqdm = mAppApplication.getAreaId();
            String jgid = mAppApplication.jgId;
            int sysType = Constant.sysType;

            InspectionApi api = InspectionApi.getInstance(getActivity());
            return api.GetPatientList(bqdm, jgid, sysType, mCheckBoxFiter, mTypeFilterPos);
        }

        @Override
        protected void onPostExecute(Response<List<SickPersonVo>> result) {
            super.onPostExecute(result);

            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：请求参数错误");
                return;
            }

            adapter = null;
            mListView.setAdapter(null);
            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        getSelectPerson();
                    }
                }).showLoginDialog();
                return;
            }
            if (result.ReType == 0) {
                mRawSickPersonVoList = (ArrayList<SickPersonVo>) result.Data;

                if (EmptyTool.isEmpty(mRawSickPersonVoList)) {
                    list = new ArrayList<>();
                    TestDataHelper.buidTestData(SickPersonVo.class, mRawSickPersonVoList);
                    //##toastInfo("病人列表为空", Style.INFO, R.id.actionbar);
                }
                importPersons(mRawSickPersonVoList);
            } else {
                showTipDialog(result.Msg);
//                AlertBox.Show(getActivity(), getString(R.string.project_tips), result.Msg, getString(R.string.project_operate_ok));
            }

        }
    }

    private void importPersons(ArrayList<SickPersonVo> list) {

        mAdatper = new PersonAdapter(getActivity(), list);
        mPersonListView.setAdapter(mAdatper);
        //
        if (nowSelectedSickerZyhm != null) {
            int position = -1;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).ZYHM.equals(nowSelectedSickerZyhm)) {
                    position = i;
                }
            }
            if (position >= 0) {
                //  定位病人
                ListViewScrollHelper.smoothScrollToPosition(mPersonListView, position);
                personSelected(position);
            }else{
                //病人不在了 右边显示也没有意义了 清空右边
                adapter = null;
                mListView.setAdapter(null);
            }

        }
    }

    class GetSpecimentTask extends AsyncTask<Byte, String, Response<List<SpecimenVo>>> {

        /**
         * 待采集
         */
        public static final byte GET_WAIT_ACTION_SPECIMENT = 0;

        /**
         * 已执行
         */
        public static final byte GET_ALREALY_ACTION_SPECIMENT = GET_WAIT_ACTION_SPECIMENT + 1;

        /**
         * 待发放
         */
        public static final byte GET_WAIT_DELIEVERY_SPECIMENT = GET_ALREALY_ACTION_SPECIMENT + 1;

        private byte mType = GET_WAIT_ACTION_SPECIMENT;

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<SpecimenVo>> doInBackground(Byte... params) {
            if (params == null || params.length < 1 || params[0] == null) {
                return null;
            }
            mType = params[0];
            SickPersonVo person = getSelectPerson();
            if (person == null) {
                return null;
            }
            String jgid = mAppApplication.jgId;
            String zyh = person.ZYH;
            InspectionApi api = InspectionApi.getInstance(getActivity());
            switch (mType) {

                case GET_WAIT_DELIEVERY_SPECIMENT:
                case GET_WAIT_ACTION_SPECIMENT:
                    Response<List<SpecimenVo>> response = api.GetSpecimenList(zyh, jgid,
                            Constant.sysType);
                    return response;
                case GET_ALREALY_ACTION_SPECIMENT:

                    String start = stime.getText().toString();
                    String end = etime.getText().toString();
                    response = api.GetHistorySpecimenList(zyh, start, end, jgid,
                            Constant.sysType);
                    return response;
                default:
            }

            return null;
        }

        @Override
        protected void onPostExecute(Response<List<SpecimenVo>> result) {
            super.onPostExecute(result);

            hideSwipeRefreshLayout();

            adapter = null;
            mListView.setAdapter(null);

            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：请求参数错误");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        queryWithAction();
                        return;
                    }
                }).showLoginDialog();
                return;
            }
            if (result.ReType == 0) {

                @SuppressWarnings("unchecked")
                ArrayList<SpecimenVo> tList = (ArrayList<SpecimenVo>) result.Data;


                if (mType == GET_WAIT_DELIEVERY_SPECIMENT) {
                    tList = filterListWithFFZT(tList);
                }

                if (EmptyTool.isEmpty(tList)) {
                    tList = new ArrayList<>();
                    //
                    TestDataHelper.buidTestData(SpecimenVo.class, tList);
                }


                importSpecimentList(tList);

            } else {
                showTipDialog(result.Msg);
//                AlertBox.Show(getActivity(), getString(R.string.project_tips), result.Msg, getString(R.string.project_operate_ok));
            }

        }
    }

    public void doActionQueryTask(String tmbh) {
        ActionQueryTask actionQueryTask = new ActionQueryTask();
        tasks.add(actionQueryTask);
        actionQueryTask.execute(tmbh);

    }

    class ActionQueryTask extends AsyncTask<String, String, Response<List<CYInfoBean>>> {
        String tmbh;

        @Override
        protected Response<List<CYInfoBean>> doInBackground(String... params) {
            String jgid = mAppApplication.jgId;
            String bqid = mAppApplication.getAreaId();
            SickPersonVo person = getSelectPerson();
            if (person == null) {
                publishProgress("请求失败：请先选择病人");
                return null;
            }
            String zyh = person.ZYH;//1119982
            String brid = person.ZYHM;//1004973
            InspectionApi api = InspectionApi.getInstance(getActivity());
            if (params == null || params.length < 1) {
                return null;
            }
            tmbh = params[0];
            try {
                // url 编码
                tmbh = URLEncoder.encode(tmbh, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return api.GetCYInfoByTMBH(brid, tmbh, jgid, Constant.sysType);

        }

        @Override
        protected void onPostExecute(Response<List<CYInfoBean>> result) {

            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：请求参数错误");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        doActionQueryTask(tmbh);
                    }
                }).showLoginDialog();
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            if (result.ReType == 0) {
                if (result.Data != null && !result.Data.isEmpty()) {
                    //最多一条 其实
                    for (CYInfoBean datum : result.Data) {
                        if (!TextUtils.isEmpty(datum.CYRQ)
                                && !TextUtils.isEmpty(datum.CYR) && !TextUtils.isEmpty(datum.CYBZ)) {
                          /*  stringBuilder.append("条码: ");
                            stringBuilder.append(tmbh);
                            stringBuilder.append(" 不在当前列表中 ");
                            stringBuilder.append(datum.CYR);
                            stringBuilder.append(" 于 ");
                            stringBuilder.append(datum.CYRQ);
                            stringBuilder.append(" ");
                            stringBuilder.append("1".equals(datum.CYBZ)?"已采样":"未采样");
                            stringBuilder.append("\n");*/
                            stringBuilder.append("该样本");
                            stringBuilder.append("1".equals(datum.CYBZ) ? "已采样" : "未采样");
                            stringBuilder.append(",采样人:");
                            stringBuilder.append(datum.CYR);
                            stringBuilder.append(",采样时间;");
                            stringBuilder.append(datum.CYRQ);
                            stringBuilder.append("\n");
                        }
                    }
                }
            } else {
                showMsgAndVoiceAndVibrator("获取信息失败");
                return;
            }
            //
            String sxxx = stringBuilder.toString();
            if (!TextUtils.isEmpty(sxxx)) {
                showTipDialog(sxxx);
            } else {
                showMsgAndVoiceAndVibrator("条码不在当前列表中");
            }

        }
    }


    class ActionSpecimenTask extends AsyncTask<String, String, Response<String>> {

        /**
         * 执行
         */
        public static final byte ACTION_EXECUTE_SPECIMENT = 1;

        /**
         * 取消
         */
        public static final byte ACTION_CANCEL_SPECIMENT = ACTION_EXECUTE_SPECIMENT + 1;

        /**
         * 发放
         */
        public static final byte ACTION_DELIVERY_SPECIMENT = ACTION_CANCEL_SPECIMENT + 1;


        private byte actionType;

        public ActionSpecimenTask(byte actionType) {

            this.actionType = actionType;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(getResources().getString(R.string.doing));
        }

        @Override
        protected Response<String> doInBackground(String... params) {

            if (mAppApplication.user == null) {
                return null;
            }
            SickPersonVo person = getSelectPerson();
            if (person == null) {
                publishProgress("请求失败：请先选择病人");
                return null;
            }

            String jgid = mAppApplication.jgId;
            String bqid = mAppApplication.getAreaId();
            String zyh = person.ZYH;
            String urid = mAppApplication.user.YHID;

            InspectionApi api = InspectionApi.getInstance(getActivity());

            switch (actionType) {
                case ACTION_EXECUTE_SPECIMENT:
                    if (params == null || params.length < 2) {
                        return null;
                    }
                    String tmbh = params[0];
                    try {
                        // url 编码
                        tmbh = URLEncoder.encode(tmbh, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String isScan = params[1];
                    return api.ExecuteSpecimen(zyh, urid, tmbh, isScan, mAppApplication.getSerialNumber(), jgid,
                            Constant.sysType);

                case ACTION_CANCEL_SPECIMENT:
                    if (params == null || params.length < 1) {
                        return null;
                    }
                    tmbh = params[0];
                    try {
                        // url 编码
                        tmbh = URLEncoder.encode(tmbh, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return api
                            .CancelSpecimen(urid, tmbh, zyh, mAppApplication.getSerialNumber(), jgid, Constant.sysType);
                case ACTION_DELIVERY_SPECIMENT:
                    if (params == null || params.length < 2) {
                        return null;
                    }
                    tmbh = params[0];
                    try {
                        // url 编码
                        tmbh = URLEncoder.encode(tmbh, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    isScan = params[1];
                    return api.Delivery(zyh, urid, tmbh, isScan, jgid,
                            Constant.sysType);

                default:

            }

            return null;

        }

        @Override
        protected void onProgressUpdate(String... values) {
            toastInfo(values[0]);
        }

        @Override
        protected void onPostExecute(Response<String> result) {
            super.onPostExecute(result);

            hideLoadingDialog();

            if (result == null) {
                showMsgAndVoiceAndVibrator("请求失败：请求参数错误");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        actionWithType(item, false);
                        return;
                    }
                }).showLoginDialog();
                return;
            }
            //MediaUtil.getInstance(getActivity()).playSound(result.ReType == 0? R.raw.success: R.raw.wrong, getActivity());

            if (result.ReType == 0) {
                // 提示完刷新
                if (!EmptyTool.isBlank(result.Msg)) {
                    showMsgAndVoice(result.Msg);
                } else {
                    String msg = "提交成功";
                    if (actionType == ACTION_EXECUTE_SPECIMENT) {
                        msg = "采集成功";
                        //本地改变待采集数量
                        SickPersonVo person = getSelectPerson();

                        for (SickPersonVo sickPersonVo : mRawSickPersonVoList) {
                            if (person != null && person.ZYH.equals(sickPersonVo.ZYH)) {
                                sickPersonVo.dcjCount--;
                            }
                        }
                    } else if (actionType == ACTION_CANCEL_SPECIMENT) {
                        msg = "取消采集成功";
                        //本地改变待采集数量
                        SickPersonVo person = getSelectPerson();
                        for (SickPersonVo sickPersonVo : mRawSickPersonVoList) {
                            if (person != null && person.ZYH.equals(sickPersonVo.ZYH)) {
                                sickPersonVo.dcjCount++;
                            }
                        }
                    } else if (actionType == ACTION_DELIVERY_SPECIMENT) {
                        msg = "发放成功";
                    }
                    showMsgAndVoice(msg);
                }
                //============本地病人列表刷新============
                ArrayList<SickPersonVo> sickPersonVoListTemp = new ArrayList<>();
                for (SickPersonVo sickPersonVo : mRawSickPersonVoList) {
                    if (mCheckBoxFiter == 0) {
                        //待采集
                        if (sickPersonVo.dcjCount > 0) {
                            sickPersonVoListTemp.add(sickPersonVo);
                        }
                    } else {
                        //全部
                        sickPersonVoListTemp.add(sickPersonVo);
                    }
                }
                mRawSickPersonVoList = sickPersonVoListTemp;
                importPersons(mRawSickPersonVoList);
                //============本地病人列表刷新============

            } else {
                showTipDialog(result.Msg);
//                AlertBox.Show(getActivity(), getString(R.string.project_tips), result.Msg, getString(R.string.project_operate_ok));
            }

        }
    }

    /**
     * 过滤列表，选择状态为待发放项目
     *
     * @param tList
     * @return
     */
    private ArrayList<SpecimenVo> filterListWithFFZT(ArrayList<SpecimenVo> tList) {

        if (tList == null) {
            return null;
        }
        ArrayList<SpecimenVo> result = new ArrayList<SpecimenVo>();

        for (SpecimenVo item : tList) {
            // 待发放
            if (item.FFZT == 0) {
                result.add(item);
            }
        }
        return result;
    }

}
