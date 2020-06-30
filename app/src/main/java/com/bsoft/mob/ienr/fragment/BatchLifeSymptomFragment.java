package com.bsoft.mob.ienr.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.inputmethodservice.KeyboardView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.LifeSymptomInquiryActivity;
import com.bsoft.mob.ienr.adapter.LifeSignHistoryInfoAdapter;
import com.bsoft.mob.ienr.adapter.PersonAdapter;
import com.bsoft.mob.ienr.api.LifeSignApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.DateTimeTool;
import com.bsoft.mob.ienr.dynamicui.lifesymptom.LifeSymptomViewFactoryNew;
import com.bsoft.mob.ienr.fragment.base.LeftMenuItemFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.ListViewScrollHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.kernel.SickPersonVo;
import com.bsoft.mob.ienr.model.lifesymptom.ICommonClick;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignControlItem;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignHistoryInfo;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignInputItem;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignSaveData;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignSaveDataItem;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignSync;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignTimeEntity;
import com.bsoft.mob.ienr.model.lifesymptom.LifeSignTypeItem;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.ArrayUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.FormSyncUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.ObjectUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;
import com.bsoft.mob.ienr.view.PullEditView;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;
import com.bsoft.mob.ienr.view.floatmenu.menu.IFloatMenuItem;
import com.bsoft.mob.ienr.view.floatmenu.menu.TextFloatMenuItem;

import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午11:18:41
 * 批量体征
 */
public class BatchLifeSymptomFragment extends LeftMenuItemFragment {
    private View mainView;
    private LinearLayout rootLay;
    //协和常规项目  1 体温 2 脉搏 3 呼吸 4 心率 31 大便   32 小便
   public final static String[] tzxms_XH = {"1", "2", "3","4", "31", "32"};

    // private String timeValue;

    private LifeSymptomViewFactoryNew mLifeSymptomViewFactory;

    private ListView mPersonListView;

    private PersonAdapter mAdatper;


    private SpinnerLayout mSpinnerLayout;
    private Spinner mSpinner;

    private boolean customIsSync;

    private ArrayList<SickPersonVo> mSickPersonList;
    private int mResId = R.drawable.menu_all;   //默认全部病人, 我的病人改为 menu_my

    // 体温小键盘支持 start01
    private KeyboardView keyboardView;

    // 体温小键盘支持 end01

    private CheckBox cb_default;
    private CheckBox cb_all;
    private CheckBox cb_comm;
    private TextView id_tv;

    //====显示最近录入记录=================================
    private ICommonClick commonClickImpl;
    private View    mHisView;
    private AlertDialog alertDialog = null;
    private List<LifeSignHistoryInfo> hInfoList;
    //=====================================================
    /*
        升级编号【56010032】============================================= start
生命体征 默认显示所有病区病人、体征输入
        ================= Classichu 2017/10/11 16:25
        */
    //协和需求
    private int filter = 0;//0病区病人;1体温单;2常规项目
    /* =============================================================== end */
    private int nowSickPersonPosHold = -1;
    public OnItemClickListener onPersonItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            // prt listview add a head view
            onPItemClick(position);
        }
    };

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_batch_life_symptom;
    }

    @Override
    protected void initView(View rootLayout, Bundle savedInstanceState) {
        mainView = rootLayout;
        rootLay = (LinearLayout) mainView.findViewById(R.id.id_ll_container);

        mPersonListView = (ListView) mainView
                .findViewById(R.id.id_lv);
        EmptyViewHelper.setEmptyView(mPersonListView, "mPersonListView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mPersonListView);
        mSpinnerLayout = (SpinnerLayout) mainView
                .findViewById(R.id.id_spinner_layout);
        mSpinner = mSpinnerLayout.getSpinner();
        CheckBox id_cb_sp = (CheckBox) mainView
                .findViewById(R.id.id_cb_sp);
        id_cb_sp.setText("同步");
        id_cb_sp.setChecked(customIsSync);
        id_cb_sp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView == null || !buttonView.isPressed()) {
                    //不响应非点击引起的改变
                    return;
                }
                customIsSync = isChecked;
            }
        });
        id_tv = (TextView) mainView.findViewById(R.id.id_tv);
        id_tv.setText("操作类型：");
        cb_default = (CheckBox) mainView.findViewById(R.id.id_cb_2);
        cb_default.setText("需测");
        cb_all = (CheckBox) mainView.findViewById(R.id.id_cb);
        cb_all.setText("全部");
        cb_comm = (CheckBox) mainView.findViewById(R.id.id_cb_3);
        cb_comm.setText("常规");
        // 体温小键盘支持 start02
        keyboardView = (KeyboardView) mainView.findViewById(R.id.keyboard_view);
        // 体温小键盘支持 end02

        // 体温小键盘支持 start04
        String yyyyMMddHHmm = DateTimeHelper.getServer_yyyyMMddHHmm00();
        mLifeSymptomViewFactory = new LifeSymptomViewFactoryNew(getActivity(), mainView, false,
                yyyyMMddHHmm, mAppApplication);
        // 体温小键盘支持 end04
        initActionBar();
        initListView();
        initSpinner();
        initCheckBox();
        initBroadCast();

        commonClickImpl = new ICommonClick() {
            @Override
            public void OnClick(View view, Object parms) {
                mHisView = view;

                if(view == null) return;
                View v = (View) view.getTag();

                if(v == null) return;
                String xmh = (String) v.getTag();
                if(xmh == null) return;

                GetHistoryInfo(xmh);
            }
        };

        mLifeSymptomViewFactory.setCommonClick(commonClickImpl);

        //获取时间
        actionGetTimesTask();
    }

    // 体温小键盘支持 end03

    // 体温小键盘支持 start03
    public boolean isKeyboardShowing() {
        return keyboardView.getVisibility() == View.VISIBLE;
    }

    public void hideKeyboard() {
        keyboardView.setVisibility(View.GONE);
    }

    // 获取时间列表
    private void actionGetTimesTask() {
        if (mLifeSignTimeEntityList == null) {
            actionGetTimesTaskForce();
        }
    }


    @Override
    protected List<IFloatMenuItem> configFloatMenuItems() {
        final int[] itemDrawables = {R.drawable.menu_view,
                R.drawable.menu_fresh, R.drawable.menu_save};
        final int[][] itemStringDrawables = {
                {R.drawable.menu_fresh, R.string.comm_menu_refresh},
                {R.drawable.menu_view, R.string.comm_menu_view},
                {R.drawable.menu_save, R.string.comm_menu_save},
                {R.drawable.menu_my, R.string.main_menu_my},
                {R.drawable.menu_all, R.string.main_menu_all}
        };
        List<IFloatMenuItem> floatMenuItemList = new ArrayList<>();
        for (int[] itemDrawableRes : itemStringDrawables) {
            int itemDrawableResid = itemDrawableRes[0];
            int textResId = itemDrawableRes[1];
            String text = textResId > 0 ? getString(textResId) : null;
            IFloatMenuItem floatMenuItem = new TextFloatMenuItem(itemDrawableResid, text) {
                @Override
                public void actionClick(View view, int resid) {
                    onMenuItemClick(resid);
                }
            };
            floatMenuItemList.add(floatMenuItem);
        }
        return floatMenuItemList;
    }


    private void onMenuItemClick(int drawableRes) {

        if (drawableRes == R.drawable.menu_fresh) {// 刷新
            toRefreshData();
        } else if (drawableRes == R.drawable.menu_save) {//保存
            onSaveAction();
        } else if (drawableRes == R.drawable.menu_view) {// 历史
            if (nowSickPersonPosHold < 0) {
                showMsgAndVoiceAndVibrator("请先选择病人！");
                return;
            }
            Intent intent = new Intent(getActivity(),
                    LifeSymptomInquiryActivity.class);
            startActivity(intent);
        }else if(drawableRes == R.drawable.menu_my || drawableRes == R.drawable.menu_all){
            //我的病人及病区病人
            mResId = drawableRes;
            ArrayList<SickPersonVo> list = filterPerson();
            importPersons(list);
        }


        /*{R.drawable.menu_my, R.string.main_menu_my},
        {R.drawable.menu_all, R.string.main_menu_all}*/

    }

    //获取体征历史数据
    public void GetHistoryInfo(String xmh){
        GetHistoryInfoTask task = new GetHistoryInfoTask();
        tasks.add(task);
        task.execute(xmh);
    }

    class GetHistoryInfoTask extends AsyncTask<String, Void, Response<List<LifeSignHistoryInfo>>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<LifeSignHistoryInfo>> doInBackground(String... parms) {
            String zyh = mAppApplication.sickPersonVo.ZYH;
            String jgid = mAppApplication.jgId;
            String xmh = parms[0];

            return LifeSignApi.getInstance(getActivity()).getLifeSignHistoryInfo(zyh, xmh, jgid);

        }

        @Override
        protected void onPostExecute(Response<List<LifeSignHistoryInfo>> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            //actionGetTask();
                            performGetFormForce(mAppApplication.sickPersonVo);
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    hInfoList = result.Data;
                    showHistoryInfoDialog(result.Data);
                } else {
                    showMsgAndVoice(result.Msg);
                }
            }

        }
    }

    public void showHistoryInfoDialog(List<LifeSignHistoryInfo> list){
        if(list == null || list.size() < 1){
            showMsgAndVoice("无历史数据");
            return;
        }

        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_root_linear, null, false);
        ListView listView = new ListView(context);

        linearLayout.addView(listView);
        EmptyViewHelper.setEmptyView(listView, "listView");
        LifeSignHistoryInfoAdapter adapter = new LifeSignHistoryInfoAdapter(context, list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                alertDialog.hide();

                if(position >= hInfoList.size()) return;

                LifeSignHistoryInfo hInfo = hInfoList.get(position);

                View vEdit = (View) mHisView.getTag();
                if(vEdit == null) return;

                if(vEdit instanceof PullEditView){
                    ((PullEditView) vEdit).getEditText().setText(hInfo.TZNR);
                }else if(vEdit instanceof EditText){
                    ((EditText) vEdit).setText(hInfo.TZNR);
                }
            }
        });
        listView.setAdapter(adapter);
        View txt = ViewBuildHelper.buildDialogTitleTextView(mContext, "最近记录");
        builder.setView(linearLayout)
                //.setTitle("最近记录")
                .setCustomTitle(txt);
        builder.setNegativeButton(getString(R.string.project_operate_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void actionGetTimesTaskForce() {
        GetTimesTask task = new GetTimesTask();
        tasks.add(task);
        task.execute();
    }

    private void initCheckBox() {
        cb_default.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView == null || !buttonView.isPressed()) {
                    //不响应非点击引起的改变
                    return;
                }
                cb_comm.setChecked(false);
                cb_default.setChecked(true);
                cb_all.setChecked(false);
                filter = 1;
                toRefreshData();
            }
        });
        cb_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView == null || !buttonView.isPressed()) {
                    //不响应非点击引起的改变
                    return;
                }
                cb_comm.setChecked(false);
                cb_default.setChecked(false);
                cb_all.setChecked(true);
                filter = 0;
                toRefreshData();
            }
        });

        cb_comm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView == null || !buttonView.isPressed()) {
                    //不响应非点击引起的改变
                    return;
                }
                cb_comm.setChecked(true);
                cb_default.setChecked(false);
                cb_all.setChecked(false);
                filter = 2;
                toRefreshData();
            }
        });
    }

    private void initSpinner() {

        mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                toRefreshData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void actionWithTime(int position) {

        if (mSpinner.getAdapter() == null) {
            return;
        }

        LifeSignTimeEntity item = (LifeSignTimeEntity) mSpinner.getAdapter().getItem(position);
        if (item != null) {

            performGetPListTask(item.VALUE);

        }

    }

    private void performGetPListTask(int time) {

        GetPListTask getDataTask = new GetPListTask();
        tasks.add(getDataTask);
        getDataTask.execute(time);
    }

    @Override
    protected void toRefreshData() {
        int position = mSpinner.getSelectedItemPosition();
        actionWithTime(position);
    }

    private void initListView() {

        mPersonListView.setTextFilterEnabled(true);
        // checked/activated
        mPersonListView.setChoiceMode(
                AbsListView.CHOICE_MODE_SINGLE);


        mPersonListView.setOnItemClickListener(onPersonItemClickListener);

    }

    private void initActionBar() {

        actionBar.setTitle("体征录入");
        actionBar.addAction(new Action() {

            @Override
            public void performAction(View view) {
                onSaveAction();
            }

            @Override
            public String getText() {
                return "保存";
            }

            @Override
            public int getDrawable() {
                return R.drawable.ic_done_black_24dp;
            }
        });

        actionBar.addAction(new Action() {
            @Override
            public String getText() {
                return "刷新";
            }

            @Override
            public void performAction(View view) {
                //刷新时间
                actionGetTimesTaskForce();

                // int position = mPersonListView
                // .getCheckedItemPosition();
                //
                // if (position == AdapterView.INVALID_POSITION) {
                // VibratorUtil.vibratorMsg(
                // application.getSettingConfig().vib, "请选择病人",
                // getActivity());
                // return;
                // }
                //
                // SickPersonVo person = (SickPersonVo) mPersonListView
                // .getAdapter().getItem(position);
                // if (person == null) {
                // VibratorUtil.vibratorMsg(
                // application.getSettingConfig().vib, "请选择病人",
                // getActivity());
                // return;
                // }
                //
                // performGetForm(person);

            }

            @Override
            public int getDrawable() {
                //刷新时间
                return R.drawable.ic_refresh_black_24dp;
            }
        });

    }

    private void onSaveAction() {

        int position = mPersonListView.getCheckedItemPosition();

        if (position == AdapterView.INVALID_POSITION) {
           /* VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "请选择病人", getActivity());*/
            showMsgAndVoiceAndVibrator("请选择病人");
            return;
        }

        SickPersonVo person = (SickPersonVo) mPersonListView.getAdapter().getItem(position);
        if (person == null) {
           /* VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "请选择病人", getActivity());*/
            showMsgAndVoiceAndVibrator("请选择病人");
            return;
        }
        performSaveTask(person);
    }

    private void performSaveTask(SickPersonVo person) {

        Object obj = mSpinner.getSelectedItem();
        mLifeSymptomViewFactory.setTimeValue(obj);

        SaveTask task = new SaveTask();
        tasks.add(task);
        task.execute(person);
    }

    private void initBroadCast() {

        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (BarcodeActions.Refresh.equals(intent.getAction())) {
                    scanSltPerson();
                } else if (BarcodeActions.Bar_Get.equals(intent.getAction())) {

                    if (mAppApplication.sickPersonVo == null) {
                        return;
                    }
                    BarcodeEntity entity = (BarcodeEntity) intent
                            .getParcelableExtra("barinfo");
                    if (FastSwitchUtils.needFastSwitch(entity)) {
                        FastSwitchUtils.fastSwith(getActivity(), entity);
                    }
                }
            }
        };
    }

    private void scanSltPerson() {

        SickPersonVo person = mAppApplication.sickPersonVo;

        if (person == null || mAdatper == null) {
          /*  VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "扫描的病人不在此列表中", getActivity());*/
            showMsgAndVoiceAndVibrator("扫描的病人不在此列表中");
            return;
        }
        int position = mAdatper.getPersonPostion(person.ZYH);
        if (position == -1) {
           /* VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "扫描的病人不在此列表中", getActivity());*/
            showMsgAndVoiceAndVibrator("扫描的病人不在此列表中");
            return;
        }

        mPersonListView.setItemChecked(position,
                true);
    /*    View v = mPersonListView.getChildAt(position);
        int top = (v == null) ? 0 : v.getTop();
        mPersonListView.setSelectionFromTop(position, top);*/
        ListViewScrollHelper.smoothScrollToPosition(mPersonListView, position);

        // 病人列表选中后重新给person赋值，以获得其中的tzxm字段值
        person = (SickPersonVo) mPersonListView
                .getAdapter().getItem(position);
        performGetForm(person);

    }

    private void performGetFormForce(SickPersonVo person) {
        GetFormTask task = new GetFormTask();
        tasks.add(task);
        task.execute(person);
    }

    private void performGetForm(SickPersonVo person) {
        if (mLifeSignTypeItemList == null) {
            performGetFormForce(person);
        } else {
            parseView(person);
        }
    }

    /**
     * 过滤UI，把不用显示的控件过滤。
     * <p>
     * 过滤原则：ItemViewVo（三级）控件的saveid存在病人TZXM中， 则显示InputItemVo和TypeItemVo
     * </p>
     *
     * @param types
     * @return
     */
    private ArrayList<LifeSignTypeItem> filterUIs(SickPersonVo person, ArrayList<LifeSignTypeItem> types) {

        if (types == null || person == null) {
            return types;
        }

        String[] tzxms = null;
        if (!EmptyTool.isBlank(person.TZXM)) {
            tzxms = person.TZXM.split(",");
        }
        if (tzxms == null || tzxms.length < 1) {
            return types;
        }
        ArrayList<LifeSignTypeItem> result = new ArrayList<>();

        for (LifeSignTypeItem lifeSignTypeItem : types) {

            ArrayList<LifeSignInputItem> list = new ArrayList<>();

            for (LifeSignInputItem lifeSignInputItem : lifeSignTypeItem.LifeSignInputItemList) {
                boolean show = false;
                for (LifeSignControlItem lifeSignControlItem : lifeSignInputItem.LifeSignControlItemList) {
                    if (ArrayUtils.contains(tzxms, lifeSignControlItem.TZXM)) {
                        show = true;
                        break;
                    }
                }
                if (show) {
                    list.add(lifeSignInputItem);
                }
            }

            if (list.size() > 0) {
                lifeSignTypeItem.LifeSignInputItemList = list;
                result.add(lifeSignTypeItem);
            }
        }

        return result;
    }


    private ArrayList<LifeSignTypeItem> filterChangGuiUIs(SickPersonVo person, ArrayList<LifeSignTypeItem> types) {

        if (types == null || person == null) {
            return null;
        }
        if (tzxms_XH == null || tzxms_XH.length < 1) {
            return types;
        }
        ArrayList<LifeSignTypeItem> result = new ArrayList<>();

        for (LifeSignTypeItem lifeSignTypeItem : types) {

            ArrayList<LifeSignInputItem> list = new ArrayList<>();

            for (LifeSignInputItem lifeSignInputItem : lifeSignTypeItem.LifeSignInputItemList) {
                boolean show = false;
                for (LifeSignControlItem lifeSignControlItem : lifeSignInputItem.LifeSignControlItemList) {
                    if (ArrayUtils.contains(tzxms_XH, lifeSignControlItem.TZXM)) {
                        show = true;
                        break;
                    }
                }
                if (show) {
                    list.add(lifeSignInputItem);
                }
            }

            if (list.size() > 0) {
                lifeSignTypeItem.LifeSignInputItemList = list;
                result.add(lifeSignTypeItem);
            }
        }

        return result;
    }

    void hidden() {
        if (null != getActivity().getCurrentFocus()
                && null != getActivity().getCurrentFocus().getWindowToken()) {
            ((InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    getActivity().getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void importPersons(ArrayList<SickPersonVo> list) {

        mAdatper = new PersonAdapter(getActivity(), list);
        mPersonListView.setAdapter(mAdatper);
        //选中之前选中的病人
        if (nowSickPersonPosHold >= 0) {
            onPItemClick(nowSickPersonPosHold);
        }
    }

    public void importTimeList() {
        if (mLifeSignTimeEntityList == null) {
            return;
        }
        ArrayAdapter<LifeSignTimeEntity> adapter = new ArrayAdapter<LifeSignTimeEntity>(
                getActivity(), android.R.layout.simple_spinner_item);
        for (LifeSignTimeEntity entity : mLifeSignTimeEntityList) {
            adapter.add(entity);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);
        /*
        升级编号【56010031】============================================= start
        默认选中最近的时间点，通过时间点获取数据
        ================= Classichu 2017/11/20 17:23
        */
        //选择当前最近的一个时间点
        int minC_pos = getNearTimePos(mLifeSignTimeEntityList);
        mSpinner.setSelection(minC_pos);
        /* =============================================================== end */
    }

    /*
    升级编号【56010031】============================================= start
    默认选中最近的时间点，通过时间点获取数据
    ================= Classichu 2017/11/20 17:23
    */
    private int getNearTimePos(ArrayList<LifeSignTimeEntity> list) {
        //milliseconds
        long millisecondsCut = DateTimeTool.compareTo(DateTimeHelper.getServerDateTime(), DateTimeHelper.getServerDate() + " 00:00:00");
        int cutM = (int) (millisecondsCut / (1000 * 60));
        int minC_pos = 0;
        int minC = -1;
        for (int i = 0; i < list.size(); i++) {
            int dis = Math.abs(list.get(i).VALUE - cutM);
            minC = minC == -1 ? dis : minC;
            if (minC > dis) {
                minC = dis;
                minC_pos = i;
            }
        }
        return minC_pos;
    }

    private void onPItemClick(int position) {
        mPersonListView.setItemChecked(position, true);
        if (mPersonListView.getAdapter().getCount() <= position) {
            return;
        }
        SickPersonVo person = (SickPersonVo) mPersonListView
                .getAdapter().getItem(position);
        mAppApplication.sickPersonVo = person;
        nowSickPersonPosHold = position;
        performGetForm(person);
    }

    class GetFormTask extends
            AsyncTask<SickPersonVo, Void, Response<ArrayList<LifeSignTypeItem>>> {

        private SickPersonVo person;

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<ArrayList<LifeSignTypeItem>> doInBackground(SickPersonVo... params) {

            if (params == null || params.length < 1 || params[0] == null) {
                return null;
            }
            person = params[0];

            String zyh = "-1";
            if (mAppApplication.sickPersonVo != null) {
                zyh = mAppApplication.sickPersonVo.ZYH;
            }
            String areId = mAppApplication.getAreaId();
            String jgid = mAppApplication.jgId;

            Response<ArrayList<LifeSignTypeItem>> type = LifeSignApi.getInstance(
                    getActivity()).getLifeSignTypeItemList(zyh,
                    areId, jgid, Constant.sysType);
            if (type == null || type.Data == null) {
                return null;
            }

            return type;

        }


        @Override
        protected void onPostExecute(Response<ArrayList<LifeSignTypeItem>> sresult) {
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (sresult == null) {
                showMsgAndVoiceAndVibrator("加载失败");
                return;
            }

            if (sresult.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                    @Override
                    public void LoginSucessEvent() {
                        performGetFormForce(mAppApplication.sickPersonVo);
                    }
                }).showLoginDialog();
                return;
            }
            if (sresult.ReType == 0) {
                //
                mLifeSignTypeItemList = sresult.Data;
                parseView(person);

            } else {
                showMsgAndVoice(sresult.Msg);
            }

        }

    }

    private boolean mScrollFastFlag = false;

    private void parseView(SickPersonVo person) {
        ArrayList<LifeSignTypeItem> nowList = new ArrayList<>();
        boolean isChangGui = false;
        ArrayList<LifeSignTypeItem> lifeSignTypeItemList = null;
        try {
            lifeSignTypeItemList = ObjectUtil.deepCopy(mLifeSignTypeItemList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (filter == 1) {
            nowList = filterUIs(person, lifeSignTypeItemList);
        } else if (filter == 2) {
            nowList = filterChangGuiUIs(person, lifeSignTypeItemList);
            isChangGui = true;
        } else {
            nowList = lifeSignTypeItemList;
        }
        //
        mLifeSymptomViewFactory.clearData();
        rootLay.removeAllViews();
        View resultView = mLifeSymptomViewFactory.build(nowList, person.ZYH, mAppApplication.jgId, isChangGui);
        if (isChangGui) {
            mLifeSymptomViewFactory.setOnEditEnterClickListener(new LifeSymptomViewFactoryNew.OnEditEnterClickListener() {
                @Override
                public boolean onEditEnterClick() {
                    //
                    List<LifeSignSaveDataItem> itemList = mLifeSymptomViewFactory.getRealSaveData();
                    if (null != itemList && !itemList.isEmpty()) {
                        mScrollFastFlag = true;
                        //保存
                        onSaveAction();
                    } else {
                        String msg = "无数据,直接切换到下一病人";
                        trunNextPerson(msg);
                    }
                    return false;
                }
            });
        }
        rootLay.addView(resultView);
        resultView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                hidden();
                mLifeSymptomViewFactory.hidden();
                return false;
            }
        });
    }

    private void trunNextPerson(String msg) {
        //
        int posTemp = nowSickPersonPosHold + 1;
        if (posTemp < mAdatper.getCount()) {
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            //
            onPItemClick(posTemp);
            //
            int scrollPos = posTemp - 1;//方便显示
            scrollPos = scrollPos < 0 ? 0 : scrollPos;//当前病人的上一个 如果第一个就0
            ListViewScrollHelper.scrollToPosition(mPersonListView, scrollPos);
        } else {
            Toast.makeText(mContext, "已经是最后一个病人了", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<LifeSignTimeEntity> mLifeSignTimeEntityList;
    private ArrayList<LifeSignTypeItem> mLifeSignTypeItemList;

    /**
     * 加载时间过滤列
     */
    private class GetTimesTask extends AsyncTask<Void, Void, Response<List<LifeSignTimeEntity>>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoadingDialog(R.string.loading);
        }

        @Override
        protected Response<List<LifeSignTimeEntity>> doInBackground(Void... params) {

            String bqid = mAppApplication.getAreaId();
            String jgid = mAppApplication.jgId;
            int sysType = Constant.sysType;
            LifeSignApi api = LifeSignApi.getInstance(getActivity());
            return api.GetTimePointList(bqid, jgid, sysType);
        }

        @Override
        protected void onPostExecute(Response<List<LifeSignTimeEntity>> result) {

            hideLoadingDialog();
            tasks.remove(this);

            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            actionGetTimesTaskForce();
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    mLifeSignTimeEntityList = (ArrayList<LifeSignTimeEntity>) result.Data;
                    importTimeList();
                } else {
                    showMsgAndVoice(result.Msg);
                    return;
                }
            } else {
                showMsgAndVoiceAndVibrator("加载时间列表失败");
                return;
            }
        }
    }

    /**
     * 病人列表异步加载
     */
    private class GetPListTask extends AsyncTask<Integer, Void, Response<ArrayList<SickPersonVo>>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<ArrayList<SickPersonVo>> doInBackground(Integer... params) {

            if (params == null || params.length < 1 || params[0] == null) {
                return null;
            }
            String areaId = mAppApplication.getAreaId();
            int time = params[0];
            String jgid = mAppApplication.jgId;
            if (filter == 2) {
                //常规项目写死，使用 【病区病人】0 处理病人列表
                return LifeSignApi.getInstance(getActivity()).GetPatientList(areaId,
                        time, time, 0, jgid);
            }
            return LifeSignApi.getInstance(getActivity()).GetPatientList(areaId,
                    time, time, filter, jgid);
        }

        @Override
        protected void onPostExecute(Response<ArrayList<SickPersonVo>> result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);
            rootLay.removeAllViews();

            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication).showLoginDialog();
                    return;
                }
                if (result.ReType == 0) {
                    //@SuppressWarnings("unchecked")
                    /*ArrayList<SickPersonVo> list = result.Data;
                    importPersons(list);*/

                    mSickPersonList = result.Data;
                    ArrayList<SickPersonVo> list = filterPerson();
                    importPersons(list);

                } else {
                    showMsgAndVoice(result.Msg);
                }
            } else {
                showMsgAndVoiceAndVibrator("加载失败");
                return;
            }
        }
    }

    private ArrayList<SickPersonVo> filterPerson(){
        if(mResId == R.drawable.menu_all){
            return mSickPersonList;
        }

        ArrayList<SickPersonVo> list = new ArrayList<>();
        if(mSickPersonList != null && mSickPersonList.size() > 0) {
            for (SickPersonVo spv : mSickPersonList) {
                if (ArrayUtil.inList(mAppApplication.emplBedList, spv.BRCH)) {
                    list.add(spv);
                }
            }
        }
        return list;
    }

    /* =============================================================== end */
    class SaveTask extends AsyncTask<SickPersonVo, Void, Response<LifeSignSync>> {
        @Override
        protected void onPreExecute() {
            /*if (!mScrollFastFlag){
                //常规快速输入时候 不显示 不要影响输入法隐藏显示
                showLoadingDialog(R.string.saveing);
            }*/
            showLoadingDialog(R.string.saveing);
        }

        @Override
        protected Response<LifeSignSync> doInBackground(SickPersonVo... arg0) {

            if (arg0 == null || arg0.length < 1 || arg0[0] == null) {
                return null;
            }

            Response<LifeSignSync> response = new Response<>();
            response.ReType = -2;

            List<LifeSignSaveDataItem> itemList = mLifeSymptomViewFactory.getRealSaveData();
            if (null == itemList || itemList.size() == 0) {
                response.Msg = "没有要保存的数据";
                return response;

            }
            String data = "";
            LifeSignSaveData lifeSignSaveData = new LifeSignSaveData();
            try {
                lifeSignSaveData.URID = mAppApplication.user.YHID;
                lifeSignSaveData.BQID = mAppApplication.getAreaId();
                lifeSignSaveData.ZYH = arg0[0].ZYH;
                lifeSignSaveData.IsTemp = mLifeSymptomViewFactory.isTemp();
                lifeSignSaveData.TempTime = mLifeSymptomViewFactory.getSaveTime();
                lifeSignSaveData.JGID = mAppApplication.jgId;
                lifeSignSaveData.lifeSignSaveDataItemList = itemList;
                lifeSignSaveData.customIsSync = customIsSync;
                data = JsonUtil.toJson(lifeSignSaveData);
            } catch (IOException e) {
                e.printStackTrace();
            }
            LifeSignApi api = LifeSignApi.getInstance(getActivity());
            response = api.lifeSymptomSave(data);
            return response;
        }

        @Override
        protected void onPostExecute(Response<LifeSignSync> result) {

            hideLoadingDialog();
            tasks.remove(this);
            if (result != null) {
                if (result.ReType == -2) {
                    showMsgAndVoice(result.Msg);
                } else if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            performSaveTask(mAppApplication.sickPersonVo);
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    showMsgAndVoice(R.string.project_save_success);
                    LifeSignSync lifeSignSync = result.Data;
                    if (lifeSignSync.IsSync) {
                        FormSyncUtil syncUtil = new FormSyncUtil();
                        syncUtil.setOnDialogClickListener(
                                new FormSyncUtil.onCancelClickListener() {

                                    @Override
                                    public void onCancel() {

                                    }
                                }, new FormSyncUtil.onConfirmClickListener() {

                                    @Override
                                    public void onConfirm() {

                                    }
                                });
                        syncUtil.InvokeSync(getActivity(),
                                lifeSignSync.SyncData, mAppApplication.jgId, tasks);

                    }

                    // 数据情空
                    mLifeSymptomViewFactory.clearData();
                    //
                    if (mScrollFastFlag) {
                        String msg = "已切换到下一病人";
                        trunNextPerson(msg);
                        mScrollFastFlag = false;
                    }
                } else {
                    showMsgAndVoice(result.Msg);
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：参数错误");
            }
        }

    }
}
