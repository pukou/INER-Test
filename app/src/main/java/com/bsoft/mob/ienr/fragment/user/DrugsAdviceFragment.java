package com.bsoft.mob.ienr.fragment.user;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.UserModelActivity;
import com.bsoft.mob.ienr.activity.user.execut.KFExecutActivity;
import com.bsoft.mob.ienr.activity.user.execut.SYExecutActivity;
import com.bsoft.mob.ienr.adapter.SickPersonAdviceAdapter;
import com.bsoft.mob.ienr.api.AdviceApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.components.datetime.DateTimeTool;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.fragment.dialog.AdviceDialogFragment;
import com.bsoft.mob.ienr.fragment.dialog.ListSelectDialogFragment;
import com.bsoft.mob.ienr.helper.ContextCompatHelper;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.ListViewScrollHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.listener.ListSelected;
import com.bsoft.mob.ienr.model.ChoseVo;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.Statue;
import com.bsoft.mob.ienr.model.advice.AdvicePlanData;
import com.bsoft.mob.ienr.model.advice.AdvicePlanVo;
import com.bsoft.mob.ienr.model.advice.AdviceRefuseReasonVo;
import com.bsoft.mob.ienr.model.advice.PlanAndTransfusion;
import com.bsoft.mob.ienr.model.advice.execut.ExecutVo;
import com.bsoft.mob.ienr.model.advice.execut.ExecutVo.ExecutType;
import com.bsoft.mob.ienr.model.advice.execut.InArgument;
import com.bsoft.mob.ienr.model.advice.execut.KFModel;
import com.bsoft.mob.ienr.model.advice.execut.PlanArgInfo;
import com.bsoft.mob.ienr.model.advice.execut.REModel;
import com.bsoft.mob.ienr.model.advice.execut.RequestBodyInfo;
import com.bsoft.mob.ienr.model.advice.execut.SYZTModel;
import com.bsoft.mob.ienr.model.advice.execut.ZSModel;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.DateUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.FormSyncUtil;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.util.tools.NumberCharParser;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;
import com.bsoft.mob.ienr.view.ChoseView;
import com.bsoft.mob.ienr.view.ChoseView.ChoseListener;
import com.bsoft.mob.ienr.view.floatmenu.menu.IFloatMenuItem;
import com.bsoft.mob.ienr.view.floatmenu.menu.TextFloatMenuItem;
import com.classichu.dialogview.helper.DialogFragmentShowHelper;
import com.classichu.dialogview.listener.OnBtnClickListener;
import com.classichu.dialogview.manager.DialogManager;

import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 药品医嘱页
 *
 * @author hy
 * <p>
 * YZZH + JHSJ 一致代表一组
 */
public class DrugsAdviceFragment extends BaseUserFragment {
    private ListView mListView;

    private SickPersonAdviceAdapter mSickPersonAdviceAdapter;

    private TextView time;

    private CheckBox image;

    private LinearLayout choseViewLay;

    private ChoseView choseView_status;

    private ArrayList<AdvicePlanVo> mAdvicePlanVoArrayListRaw;

    private RadioGroup trans_type;// 多瓶还是接瓶

    private boolean isMulti = false;
    /**
     * 拒绝原因列表
     */
    private ArrayList<AdviceRefuseReasonVo> reList;

    /**
     * 执行状态 1 已执行 2 正在执行 4 暂停 0 未执行 5 拒绝
     */
    public int ZXZT = -1;
    /**
     * 归属类型 1 护理治疗 2 标本采样 3 口服用药 4 静脉输液 5 注射用药 6 处理用药 7 体征采集 8 其它医嘱
     */
    public int GSLX = -1;
    public String nowLXH = "";//初始化

    private String[] reson_more;

    // 当前执行状态
    // public ExecutType executType;
    // 保存条码信息
    private BarcodeEntity barinfo;

    private View sltDateView;
    ///
    private Map<String, String> selectMap;
    private String selectedSYDH_hand;
    private String selectedSYDH_scan;

    @Override
    public void onDateSet(int year, int month, int dayOfMonth, int viewId) {
        String dateTime = DateTimeFactory.getInstance().ymd2Date(year, month, dayOfMonth);
        initTimeTxt(dateTime, viewId);

    }

    private String getSelectTime() {
        String datet = DateTimeHelper.getServerDate();
        String sTime = time != null ? time.getText().toString() : datet;
        return sTime;
    }

    // 初始化日期选择器
    private void initTimeTxt(String timeStr, int viewId) {
        time.setText(timeStr);
        time.postDelayed(new Runnable() {
            @Override
            public void run() {
                toRefreshData();
            }
        }, 500);

    }

    @Override
    protected void toRefreshData() {
        GetHttpTask getHttpTask = new GetHttpTask();
        tasks.add(getHttpTask);
        getHttpTask.execute();
    }


    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_drugs_advice;
    }

    @Override
    protected void initView(View mainView, Bundle savedInstanceState) {

        initActionBar();

        mListView = (ListView) mainView
                .findViewById(R.id.id_lv);

        EmptyViewHelper.setEmptyView(mListView, "mListView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mListView);
        mSickPersonAdviceAdapter = new SickPersonAdviceAdapter(mContext, new ArrayList<>());
        mListView.setAdapter(mSickPersonAdviceAdapter);

        time = (TextView) mainView.findViewById(R.id.time);
        image = (CheckBox) mainView.findViewById(R.id.image);
        Drawable btnDrawable = ContextCompatHelper.getDrawable(mContext, R.drawable.selector_classic_icon_up_down);
        image.setButtonDrawable(btnDrawable);
        image.setVisibility(View.INVISIBLE);
        trans_type = (RadioGroup) mainView.findViewById(R.id.trans_mode);
        choseViewLay = (LinearLayout) mainView.findViewById(R.id.id_ll_controller);
        RadioGroup id_rg = (RadioGroup) mainView.findViewById(R.id.id_rg);
        RadioButton id_rb = (RadioButton) mainView.findViewById(R.id.id_rb);
        RadioButton id_rb_2 = (RadioButton) mainView.findViewById(R.id.id_rb_2);
        RadioButton id_rb_3 = (RadioButton) mainView.findViewById(R.id.id_rb_3);
        RadioButton id_rb_4 = (RadioButton) mainView.findViewById(R.id.id_rb_4);
        id_rb.setText("全部");
        id_rb_2.setText("已执行");
        id_rb_3.setText("未执行");
        id_rb_4.setText("执行中");
        id_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int fiter = -1;
                switch (checkedId) {
                    case R.id.id_rb:
                        fiter = -1;//全部
                        break;
                    case R.id.id_rb_2:
                        fiter = 1;//已执行
                        break;
                    case R.id.id_rb_3:
                        fiter = 0;//未执行
                        break;
                    case R.id.id_rb_4:
                        fiter = 2;//执行中
                        break;
                    default:
                }
                ZXZT = fiter;

                fiterData();
            }
        });
        choseView_status = (ChoseView) mainView.findViewById(R.id.choseView_status);

        sltDateView = mainView.findViewById(R.id.slt_date_ly);
        trans_type
                .setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (checkedId == R.id.single_radio) {
                            isMulti = false;
                        } else if (checkedId == R.id.multi_radio) {
                            isMulti = true;
                        }

                    }
                });


        initDateView();
        initImageView();
        /*
        升级编号【56010040】============================================= start
增加医嘱执行情况显示，如2/5,2表示已完成，5表示总量
        ================= Classichu 2017/11/20 11:10
        */
        //####initChoseView();
        /* =============================================================== end */
        String date = DateTimeHelper.getServerDate();
        initTimeTxt(date, R.id.time);
    }

    private void fiterDataOld() {
        if (null != mAdvicePlanVoArrayListRaw && mAdvicePlanVoArrayListRaw.size() > 0) {
            ArrayList<AdvicePlanVo> datas = new ArrayList<AdvicePlanVo>();
            for (AdvicePlanVo vo : mAdvicePlanVoArrayListRaw) {
                if ((ZXZT == -1 ? true : (vo.ZXZT == ZXZT))
                        && (GSLX == -1 ? true : (vo.GSLX == GSLX))) {
                    datas.add(vo);
                }
            }
            mSickPersonAdviceAdapter.refreshData(datas);
        }
    }

    private void fiterData() {

        if (null != mAdvicePlanVoArrayListRaw && mAdvicePlanVoArrayListRaw.size() > 0) {
            ArrayList<AdvicePlanVo> datas = new ArrayList<AdvicePlanVo>();
            for (AdvicePlanVo vo : mAdvicePlanVoArrayListRaw) {
                if (GSLX == 5) {
                    //其他 nowLXH 是 25
                    if (ChoseVo.LXH_zhiLiao.equals(nowLXH)) {
                        if ((ZXZT == -1 ? true : (vo.ZXZT == ZXZT))
                                && (GSLX == -1 ? true : (vo.GSLX == GSLX))
                                && ChoseVo.LXH_zhiLiao.equals(vo.LXH)) {//是 25
                            datas.add(vo);
                        }
                    } else {
                        //nowLXH 不是 25
                        if ((ZXZT == -1 ? true : (vo.ZXZT == ZXZT))
                                && (GSLX == -1 ? true : (vo.GSLX == GSLX))
                                && !ChoseVo.LXH_zhiLiao.equals(vo.LXH)) {//也不是 25
                            datas.add(vo);
                        }
                    }
                } else {
                    //其他类型
                    if ((ZXZT == -1 ? true : (vo.ZXZT == ZXZT))
                            && (GSLX == -1 ? true : (vo.GSLX == GSLX))) {
                        datas.add(vo);
                    }
                    //
                }

            }
            mSickPersonAdviceAdapter.refreshData(datas);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initBroadCast();

    }

    @Override
    protected List<IFloatMenuItem> configFloatMenuItems() {
        final int[] itemDrawables = {R.drawable.menu_refuse,
                // R.drawable.menu_multi,
                R.drawable.menu_more,
                R.drawable.menu_fresh,
                R.drawable.menu_save};
        final int[][] itemDrawableStrings = {{R.drawable.menu_refuse, R.string.comm_menu_refuse},
                {R.drawable.menu_more, R.string.comm_menu_more},
                {R.drawable.menu_fresh, R.string.comm_menu_refresh},
//                {R.drawable.menu_save, R.string.comm_menu_save},
                {R.drawable.menu_back, R.string.comm_menu_pause}
        };
        List<IFloatMenuItem> floatMenuItemList = new ArrayList<>();
   /*     for (int itemDrawableResid : itemDrawables) {
            FloatMenuItem floatMenuItem = new FloatMenuItem(itemDrawableResid) {
                @Override
                public void actionClick(View view, int resid) {
                    onMenuItemClick(resid);
                }
            };
            floatMenuItemList.add(floatMenuItem);
        }*/
        for (int[] itemDrawableString : itemDrawableStrings) {
            int itemDrawableResid = itemDrawableString[0];
            int textResId = itemDrawableString[1];
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


    /**
     * 响应RayMenu item点击
     */
    private void onMenuItemClick(int drawableRes) {

        if (drawableRes == R.drawable.menu_refuse) {// 拒绝
            // 拒绝操作特殊处理
            if (null == reson_more) {
                if (null != reList && reList.size() > 0) {
                    reson_more = new String[reList.size()];
                    for (int i = 0; i < reList.size(); i++) {
                        reson_more[i] = reList.get(i).DYMS;
                    }
                } else {
                    showTipDialog("没有拒绝理由，无法操作");
//                    AlertBox.Show(mContext, getString(R.string.project_tips), "没有拒绝理由，无法操作", getString(R.string.project_operate_ok));
                    return;
                }
            }
            String title = "拒绝理由";
            showAdviceDialog(AdviceDialogFragment.QRT_REFUSE_DIALG, title,
                    reson_more);
        } else if (drawableRes == R.drawable.menu_multi) {// 多瓶
            showAdviceDialog(AdviceDialogFragment.QRT_ACTION_DIALG, getString(R.string.project_tips), "多路");
        } else if (drawableRes == R.drawable.menu_more) {// 更多
            String[] arr_more = getResources().getStringArray(
                    R.array.advice_drug_menu_array);
            showAdviceDialog(AdviceDialogFragment.QRT_MENU_DIALG, "更多操作",
                    arr_more);
        } else if (drawableRes == R.drawable.menu_fresh) {// refresh
            toRefreshData();
        }/* else if (drawableRes == R.drawable.menu_save) {// menu_save
            doAdvice();
        }*/ else if (drawableRes == R.drawable.menu_back) {//暂停
            performExcute(ExecutTask.Transfuse_Pasue_EXCUTE);
        }

    }

    private void doCancelEndAdvice(AdvicePlanVo advicePlanVo) {
        performExcute(ExecutTask.Transfuse_EXCUTE_CancelEnd, advicePlanVo.JHH);
    }

    private void doCancelStartAdvice(AdvicePlanVo advicePlanVo) {
        performExcute(ExecutTask.Transfuse_EXCUTE_CancelStart, advicePlanVo.JHH);
    }
    private void doCancelStartAdvice_KF(AdvicePlanVo advicePlanVo) {
        performExcute(ExecutTask.KF_EXCUTE_CancelStart, advicePlanVo.JHH);
    }
    private void doCancelStartAdvice_ZS(AdvicePlanVo advicePlanVo) {
        performExcute(ExecutTask.ZS_EXCUTE_CancelStart, advicePlanVo.JHH);
    }
    /*
    升级编号【56010040】============================================= start
增加医嘱执行情况显示，如2/5,2表示已完成，5表示总量
    ================= Classichu 2017/11/20 11:10
    */
    // 过滤器控件
    private void initChoseView(String all_tag, String hl_tag, String kf_tag,
                               String sy_tag, String zs_tag, String zl_tag) {

     /*     ArrayList<ChoseVo> datas2 = new ArrayList<ChoseVo>();
      datas2.add(new ChoseVo("全部", -1));
       ///### datas2.add(new ChoseVo("护理", 1));
        datas2.add(new ChoseVo("口服", 3));
        datas2.add(new ChoseVo("输液", 4));
        datas2.add(new ChoseVo("注射", 5));*/
        ArrayList<ChoseVo> datas2 = new ArrayList<ChoseVo>();
        datas2.add(new ChoseVo("全部\n" + all_tag, -1));
        datas2.add(new ChoseVo("护理\n" + hl_tag, 1));
        datas2.add(new ChoseVo("口服\n" + kf_tag, 3));
        datas2.add(new ChoseVo("输液\n" + sy_tag, 4));
        datas2.add(new ChoseVo("注射\n" + zs_tag, 5));
        if (!TextUtils.isEmpty(ChoseVo.LXH_zhiLiao) &&
                !Constant.DEFAULT_STRING_NEGATIVE.equals(ChoseVo.LXH_zhiLiao)) {
            //注射下面的 25 借用 存放 治疗
            datas2.add(new ChoseVo("治疗\n" + zl_tag, 5, ChoseVo.LXH_zhiLiao));
        }
        choseView_status.setData(datas2);
        choseView_status.setChoseListener(new ChoseListener() {
            @Override
            public void chose(ChoseVo choseVo) {
                GSLX = choseVo.index;
                nowLXH = choseVo.lxh;
                fiterData();
            }

        });
        //
        image.setVisibility(View.VISIBLE);
    }

    /* =============================================================== end */
    // 过滤条件隐藏或显示控制
    private void initImageView() {
        image.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                choseViewLay
                        .setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        image.performClick();
    }

    // 时间控件
    private void initDateView() {

        sltDateView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {

                String dateStr = getSelectTime();
                String dateTime = DateTimeTool.date2DateTime(dateStr);
                String max_dateTime = DateTimeTool.dateTimeAddDays(dateTime, 7);
                Date max_date = DateUtil.getDateCompat(max_dateTime);
                if (max_date != null) {
                    long maxDate = max_date.getTime();
                    showDatePickerCompat(dateStr, time.getId(), -1, maxDate);
                }

            }
        });
    }

    // 初始化工具条
    private void initActionBar() {

        actionBar.setTitle("医嘱执行");
        actionBar.setPatient(mAppApplication.sickPersonVo.XSCH
                + mAppApplication.sickPersonVo.BRXM);
        actionBar.addAction(new Action() {

            @Override
            public void performAction(View view) {

                doAdvicePreOper(true);
//                doAdvice();

            }

            @Override
            public String getText() {
                return "执行";
            }

            @Override
            public int getDrawable() {

                return R.drawable.ic_done_black_24dp;
            }
        });
    }

    private void doAdvicePreOper(boolean checkTime) {

        if (mSickPersonAdviceAdapter == null || mSickPersonAdviceAdapter.getValue() == null
                || mSickPersonAdviceAdapter.getValue().size() < 1) {

            showMsgAndVoiceAndVibrator("请求失败：请先选择医嘱项");
            return;
        }
    /*
            升级编号【56010053】============================================= start
            多瓶超过2瓶转接瓶后提示选择接哪瓶的问题
            ================= Classichu 2017/11/14 16:25
            */
        // boolean transfuseBX = Boolean.valueOf(transfuseBXStr);
//        boolean checkTime = Boolean.valueOf(checkTimeStr);
        // 选中的
        ArrayList<AdvicePlanVo> data = mSickPersonAdviceAdapter.getValue();
        if (null != data && data.size() > 0) {
            if (mSickPersonAdviceAdapter.isOneType(data)) {
                List<PlanArgInfo> planArgInfoList = new ArrayList<>();
                for (AdvicePlanVo vo : data) {
                    PlanArgInfo info = new PlanArgInfo();
                    info.JHH = vo.JHH;
                    info.GSLX = vo.GSLX;
                    info.ZXZT = vo.ZXZT;
                    info.YPYF = vo.YPYF;
                    planArgInfoList.add(info);
                }
                //!!!!!!!!============================================2017-11-13 19:10:37
                if (planArgInfoList.size() <= 0) {
                    showMsgAndVoiceAndVibrator("请先选择医嘱项");
                    return;
                }
                if (planArgInfoList.get(0).GSLX != 4) {
                    //不是输液 直接执行
                    doAdvice();
                    return;
                }
                if (planArgInfoList.get(0).ZXZT == 4) {
                    //输液 暂停的
                    DialogManager.showClassicDialog(mFragmentActivity, "温馨提示",
                            "现在输液暂停状态，继续还是停止？",
                            new OnBtnClickListener() {
                                @Override
                                public void onBtnClickOk(DialogInterface dialogInterface) {
                                    super.onBtnClickOk(dialogInterface);
                                    //继续
                                    doAdvice();
                                }

                                @Override
                                public void onBtnClickCancel(DialogInterface dialogInterface) {
                                    super.onBtnClickCancel(dialogInterface);
                                    //结束
                                    performExcute(ExecutTask.Transfuse_End_EXCUTE,
                                            Boolean.toString(checkTime));
                                }
                            }, "继续", "结束", "sho");
                } else {
                    //直接执行
                    doAdvice();
                }
            } else {
                showMsgAndVoiceAndVibrator("选择的不是同一类型");
            }
            return;
        }
        showMsgAndVoiceAndVibrator("请先选择医嘱项");
    }

    private void doAdvice() {
        if (mSickPersonAdviceAdapter == null || mSickPersonAdviceAdapter.getValue() == null
                || mSickPersonAdviceAdapter.getValue().size() < 1) {

            showMsgAndVoiceAndVibrator("请求失败：请先选择医嘱项");
            return;
        }
    /*
            升级编号【56010053】============================================= start
            多瓶超过2瓶转接瓶后提示选择接哪瓶的问题
            ================= Classichu 2017/11/14 16:25

            */
        actionDoHandleExecue(true);
        /* =============================================================== end */

    }

    /*
            升级编号【56010053】============================================= start
            多瓶超过2瓶转接瓶后提示选择接哪瓶的问题
            ================= Classichu 2017/11/14 16:25

            */
    private void actionDoHandleExecue(boolean isCheckTime) {
        performExcute(ExecutTask.HANDLE_EXCUTE, Boolean.toString(isCheckTime));
    }


    private void actionDoScanExecue(BarcodeEntity entity, boolean isCheckTime) {
        // showMsgAndVoice("" + entity.TMNR);
        performExcute(ExecutTask.SCAN_EXCUTE, entity.TMNR, entity.TMQZ, String.valueOf(entity.FLBS),
                Boolean.toString(isCheckTime));
    }

    /* =============================================================== end */

    private AdviceDialogFragment.OpearListener opearListener = new AdviceDialogFragment.OpearListener() {
        @Override
        public void performExcuteEnd(String QRDH, String s) {
            performExcute(DrugsAdviceFragment.ExecutTask.Transfuse_Continue_EXCUTE,
                    QRDH, s);
        }

        @Override
        public void performExcuteContinue(String QRDH, String s) {
            performExcute(
                    DrugsAdviceFragment.ExecutTask.Transfuse_Continue_EXCUTE,
                    QRDH, s);
        }

        @Override
        public void performExcuteRefuse(String itemStr) {
            performExcute(DrugsAdviceFragment.ExecutTask.REFUSE_EXCUTE, itemStr);
        }

        @Override
        public void actionDoHandleExecueEx(boolean flag) {
            actionDoHandleExecue(flag);
        }

        @Override
        public void actionDoScanExecueEx(boolean flag) {
            if (null != barinfo) {
                actionDoScanExecue(barinfo, flag);
            }
        }

        @Override
        public void refreshData() {
            toRefreshData();
        }
    };

    protected void showAdviceDialog(ExecutVo result) {
        AdviceDialogFragment fragment = AdviceDialogFragment.newInstance(result, reson_more, opearListener);
        DialogFragmentShowHelper.show(getChildFragmentManager(), fragment, "AdviceDialogFragment");
    }

    protected void showAdviceDialog(byte type, String title, String... items) {
        AdviceDialogFragment fragment = AdviceDialogFragment.newInstance(type,
                title, reson_more, opearListener, items);
        DialogFragmentShowHelper.show(getChildFragmentManager(), fragment, "AdviceDialogFragment");

    }


    private void initBroadCast() {

        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (BarcodeActions.Refresh.equals(intent.getAction())) {
                    sendUserName();
                    actionBar.setPatient(mAppApplication.sickPersonVo.XSCH
                            + mAppApplication.sickPersonVo.BRXM);
                    toRefreshData();
                } else if (BarcodeActions.Bar_Get.equals(intent.getAction())) {

                    BarcodeEntity entity = (BarcodeEntity) intent
                            .getParcelableExtra("barinfo");
                    if (entity.TMFL == 2) {
                        if (entity.FLBS == 3 || entity.FLBS == 5
                                || entity.FLBS == 4) {
                            barinfo = entity;
                            if (entity.FLBS != 4) {
                                trans_type.check(R.id.single_radio);
                            }

                            if (!EmptyTool.isBlank(entity.TMGZ) && entity.TMGZ.equals("1") && EmptyTool.isBlank(entity.TMQZ)) {
                                showMsgAndVoiceAndVibrator("条码前缀不允许为空");
                                return;
                            }
                            if (entity.TMQZ == null) {
                                entity.TMQZ = "";
                            }
                                /*
            升级编号【56010053】============================================= start
            多瓶超过2瓶转接瓶后提示选择接哪瓶的问题
            ================= Classichu 2017/11/14 16:25

            */
                            actionDoScanExecue(entity, true);
                            /* =============================================================== end */

                        } else if (entity.FLBS == 2) {
                            FastSwitchUtils.fastSwith(
                                    (UserModelActivity) mContext, entity);
                        }
                    }
                }
            }
        };
    }

    /*
     * 加载医嘱计划
     */
    class GetHttpTask extends AsyncTask<Void, Void, Response<AdvicePlanData>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<AdvicePlanData> doInBackground(Void... arg0) {

            String sTime = getSelectTime();
            if (mAppApplication.sickPersonVo == null) {
                return null;
            }
            String zyh = mAppApplication.sickPersonVo.ZYH;
            String jgid = mAppApplication.jgId;
            return AdviceApi.getInstance(mContext).getPlanList(zyh, sTime,
                    "", jgid);
        }

        @Override
        protected void onPostExecute(Response<AdvicePlanData> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();

            if (result != null) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(mContext, mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            toRefreshData();
                        }
                    }).showLoginDialog();
                } else if (result.ReType == 0) {
                    ArrayList<AdvicePlanVo> advicePlanVoArrayList = (ArrayList<AdvicePlanVo>) result.Data.PlanInfoList;
                    reList = (ArrayList<AdviceRefuseReasonVo>) result.Data.PhraseModelList;
                    initListItemClick();
                    if (advicePlanVoArrayList == null) {
                        advicePlanVoArrayList = new ArrayList<>();
                    }
                    mAdvicePlanVoArrayListRaw = advicePlanVoArrayList;
                    mSickPersonAdviceAdapter.refreshData(advicePlanVoArrayList);
                    /* =============================================================== end */

                    importChoseViewConfig();

                    fiterData();
                    //
                    scrollListView();
                } else {
                    boolean vib = mAppApplication.getSettingConfig().vib;
//                        VibratorUtil.vibratorMsg(vib, "暂无药品医嘱", mContext);
                    //# showMsgAndVoice("暂无药品医嘱");
                    showMsgAndVoice("暂无药品医嘱");
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
            }
        }

    }

    private void initListItemClick() {
        if (mAppApplication.userConfig.qiYong_SY_CancelToIngOperation && mAppApplication.userConfig.qiYong_SY_CancelToStartOperation) {
            //同时启用输液 取消已执行到执行中 或者 取消执行到未执行
            mSickPersonAdviceAdapter.setSYThingListener(new SickPersonAdviceAdapter.SYThingListener() {
                @Override
                public void longClick(AdvicePlanVo planVo) {

                    new AlertDialog.Builder(mContext)
                            .setCancelable(false)
                            .setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext, "温馨提示"))
                            .setMessage("是否要取消已执行到执行中或取消执行到未执行？")
                            .setPositiveButton("取消已执行到执行中", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (planVo.ZXZT == 1) {
                                        doCancelEndAdvice(planVo);
                                    } else {
                                        showMsgAndVoiceAndVibrator("输液不是已执行状态,无法操作");
                                    }
                                }
                            })
                            .setNegativeButton("取消执行到未执行", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (planVo.ZXZT == 0) {
                                        showMsgAndVoiceAndVibrator("输液是未执行状态,无法操作");
                                    } else {
                                        doCancelStartAdvice(planVo);
                                    }
                                }
                            })
                            .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
             /*   if (Constant.DEBUG) {
                    //测试
                    DialogManager.showClassicDialog(mFragmentActivity, "温馨提示",
                            "是否要取消结束至未开始？", new OnBtnClickListener() {
                                @Override
                                public void onBtnClickOk(DialogInterface dialogInterface) {
                                    doCancelStartAdvice(planVo);
                                }
                            });
                } else {
                    if (planVo.ZXZT == 1) {
                        DialogManager.showClassicDialog(mFragmentActivity, "温馨提示", "是否确定要取消结束？", new OnBtnClickListener() {
                            @Override
                            public void onBtnClickOk(DialogInterface dialogInterface) {
                                doCancelEndAdvice(planVo);
                            }
                        });
                    }
                }*/

                }
            });
        } else if (mAppApplication.userConfig.qiYong_SY_CancelToIngOperation) {
            //启用输液 取消已执行到执行中
            mSickPersonAdviceAdapter.setSYThingListener(new SickPersonAdviceAdapter.SYThingListener() {
                @Override
                public void longClick(AdvicePlanVo planVo) {

                    new AlertDialog.Builder(mContext)
                            .setCancelable(false)
                            .setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext, "温馨提示"))
                            .setMessage("是否要取消已执行到执行中？")
                            .setPositiveButton("取消已执行到执行中", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (planVo.ZXZT == 1) {
                                        doCancelEndAdvice(planVo);
                                    } else {
                                        showMsgAndVoiceAndVibrator("输液不是已执行状态,无法操作");
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
            });
        } else if (mAppApplication.userConfig.qiYong_SY_CancelToStartOperation) {
            //启用输液 取消执行到未执行
            mSickPersonAdviceAdapter.setSYThingListener(new SickPersonAdviceAdapter.SYThingListener() {
                @Override
                public void longClick(AdvicePlanVo planVo) {

                    new AlertDialog.Builder(mContext)
                            .setCancelable(false)
                            .setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext, "温馨提示"))
                            .setMessage("是否要取消执行到未执行？")
                            .setPositiveButton("取消执行到未执行", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (planVo.ZXZT == 0) {
                                        showMsgAndVoiceAndVibrator("输液是未执行状态,无法操作");
                                    } else {
                                        doCancelStartAdvice(planVo);
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
            });
        }


        ///
        if (mAppApplication.userConfig.qiYong_KF_CancelToStartOperation) {
            //启用口服 取消执行到未执行
            mSickPersonAdviceAdapter.setKFThingListener(new SickPersonAdviceAdapter.KFThingListener() {
                @Override
                public void longClick(AdvicePlanVo planVo) {

                    new AlertDialog.Builder(mContext)
                            .setCancelable(false)
                            .setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext, "温馨提示"))
                            .setMessage("是否要取消执行到未执行？")
                            .setPositiveButton("取消执行到未执行", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (planVo.ZXZT == 0) {
                                        showMsgAndVoiceAndVibrator("口服药是未执行状态,无法操作");
                                    } else {
                                        doCancelStartAdvice_KF(planVo);
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
            });
        }
        ///
        if (mAppApplication.userConfig.qiYong_ZS_CancelToStartOperation) {
            //启用注射 取消执行到未执行
            mSickPersonAdviceAdapter.setZSThingListener(new SickPersonAdviceAdapter.ZSThingListener() {
                @Override
                public void longClick(AdvicePlanVo planVo) {

                    new AlertDialog.Builder(mContext)
                            .setCancelable(false)
                            .setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(mContext, "温馨提示"))
                            .setMessage("是否要取消执行到未执行？")
                            .setPositiveButton("取消执行到未执行", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (planVo.ZXZT == 0) {
                                        showMsgAndVoiceAndVibrator("注射是未执行状态,无法操作");
                                    } else {
                                        doCancelStartAdvice_ZS(planVo);
                                    }
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .show();
                }
            });
        }
    }

    private void scrollListView() {
        if (exeList == null || mListView == null) {
            return;
        }
        int minPos = Integer.MIN_VALUE;
        for (int i = 0; i < exeList.size(); i++) {
            String yzzh = "";
            String jhsj = "";
            String jhh = "";
            Object exeObj = exeList.get(i);
            if (exeObj instanceof REModel) {
                REModel reModel = (REModel) exeObj;
                yzzh = reModel.YZZH;
                jhsj = reModel.JHSJ_NEW;
                jhh = reModel.JHH;
            } else if (exeObj instanceof SYZTModel) {
                SYZTModel syztModel = (SYZTModel) exeObj;
                yzzh = syztModel.YZZH;
                jhsj = syztModel.JHSJ;
                jhh = syztModel.JHH;
            } else if (exeObj instanceof KFModel) {
                KFModel kfModel = (KFModel) exeObj;
                yzzh = kfModel.YZZH;
                jhsj = kfModel.JHSJ;
                jhh = kfModel.JHH;
            } else if (exeObj instanceof ZSModel) {
                ZSModel zsModel = (ZSModel) exeObj;
                yzzh = zsModel.YZZH;
                jhsj = zsModel.JHSJ;
                jhh = zsModel.JHH;
            }
            int tempPos = mSickPersonAdviceAdapter.getNowDataPosInAdapter(jhsj, yzzh,jhh);
            if (tempPos >= 0) {
                //有值
                if (minPos == Integer.MIN_VALUE) {
                    minPos = tempPos;
                } else {
                    minPos = Math.min(minPos, tempPos);
                }
            }

        }
        //需要位移
        if (minPos >= 0) {
            //移动到minPos
            ListViewScrollHelper.smoothScrollToPosition(mListView, minPos);
            mSickPersonAdviceAdapter.changeBackgroundColor(mListView, minPos);
        }
        //复位
        exeList = null;
    }

    private void importChoseViewConfig() {
        /*
        升级编号【56010040】============================================= start
增加医嘱执行情况显示，如2/5,2表示已完成，5表示总量
        ================= Classichu 2017/11/20 11:10
        */
        //=======
        int all = 0;
        int all_all = 0;
        //
        int hl = 0;
        int hl_all = 0;
        int kf = 0;
        int kf_all = 0;
        int sy = 0;
        int sy_all = 0;
        int zs = 0;
        int zs_all = 0;
        int zl = 0;
        int zl_all = 0;
        for (AdvicePlanVo advicePlanVo : mAdvicePlanVoArrayListRaw) {

            switch (advicePlanVo.GSLX) {
                case 1://护理
                    if ("1".equals(advicePlanVo.YZZX)) {//主项
                        hl_all++;
                        all_all++;
                        if (1 == advicePlanVo.ZXZT) {
                            hl++;
                            all++;
                        }
                    }
                    break;
                case 3://口服
                    if ("1".equals(advicePlanVo.YZZX)) {//主项
                        kf_all++;
                        all_all++;
                        if (1 == advicePlanVo.ZXZT) {
                            kf++;
                            all++;
                        }
                    }
                    break;
                case 4://输液
                    if ("1".equals(advicePlanVo.YZZX)) {//主项
                        sy_all++;
                        all_all++;
                        if (1 == advicePlanVo.ZXZT) {
                            sy++;
                            all++;
                        }
                    }
                    break;
                case 5://注射
                    if (ChoseVo.LXH_zhiLiao.equals(advicePlanVo.LXH)) {
                        // 25 存放治疗
                        if ("1".equals(advicePlanVo.YZZX)) {//主项
                            zl_all++;
                            all_all++;
                            if (1 == advicePlanVo.ZXZT) {
                                zl++;
                                all++;
                            }
                        }
                    } else {
                        //注射 25 以外的
                        if ("1".equals(advicePlanVo.YZZX)) {//主项
                            zs_all++;
                            all_all++;
                            if (1 == advicePlanVo.ZXZT) {
                                zs++;
                                all++;
                            }
                        }
                        //
                    }
                    break;
                default:
                    if ("1".equals(advicePlanVo.YZZX)) {//主项
                        all_all++;
                        if (1 == advicePlanVo.ZXZT) {
                            all++;
                        }
                    }
                    break;
            }
        }

        //
        initChoseView(String.format("%s/%s", all, all_all),
                String.format("%s/%s", hl, hl_all),
                String.format("%s/%s", kf, kf_all),
                String.format("%s/%s", sy, sy_all),
                String.format("%s/%s", zs, zs_all),
                String.format("%s/%s", zl, zl_all));

        //========
    }

    //微注泵推注_类型
    public boolean isWeizhuBen(ExecutVo result) {
        if (result == null) {
            return false;
        }
        String ypyf = "";
        for (int i = 0; i < result.size(); i++) {
            REModel vo = (REModel) result.get(i);
            ypyf = TextUtils.isEmpty(vo.YPYF) ? "" : vo.YPYF;
        }
//        return "22".equals(ypyf);
        return mAppApplication.userConfig.weiZhuBenTuiZhu_YaoPinYongFa.equals(ypyf);
    }

    //静推_类型
    public boolean isJingTui(ExecutVo result) {
        if (result == null) {
            return false;
        }
        String ypyf = "";
        for (int i = 0; i < result.size(); i++) {
            REModel vo = (REModel) result.get(i);
            ypyf = TextUtils.isEmpty(vo.YPYF) ? "" : vo.YPYF;
        }
//        return "6".equals(ypyf);
        return mAppApplication.userConfig.jingTui_YaoPinYongFa.equals(ypyf);
    }

    // 处理执行结果
    public void toDo(final ExecutVo result) {

        // 判断是否要显示Dialog
        if (null != result && result.isOK()
                && ExecutType.RE == result.executType) {
            //ADD FJXH 处理获取到需要同步的数据
            if (result.inArgument != null) {
                //flag=1 代表 输液执行中的状态 不需要同步
                if ("1".equals(result.inArgument.flag)) {
                    showSuccessMsg("执行成功", result);
                    return;
                }
              /*  //提示
                showMsgAndVoice("可以同步到护理记录单,是否需要执行同步?");
                //需要提示同步
                new AlertDialog.Builder(mFragmentActivity).setTitle("可以同步到护理记录单").setMessage("是否需要执行同步?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //
                                if (result.get() == null) {
                                    //失败 直接刷新
                                    showSuccessMsg("同步失败", result);
                                    return;
                                }
                                String yzzh = "";
                                String jhsj = "";
                                Object exeObj = result.get().get(0);
                                if (exeObj instanceof REModel) {
                                    REModel reModel = (REModel) exeObj;
                                    yzzh = reModel.YZZH;
                                    jhsj = reModel.JHSJ_NEW;
                                } else if (exeObj instanceof SYZTModel) {
                                    SYZTModel syztModel = (SYZTModel) exeObj;
                                    yzzh = syztModel.YZZH;
                                    jhsj = syztModel.JHSJ;
                                }
                                //传给服务端 执行成功后 再传回来
                                result.inArgument.YZZH4TB = yzzh;
                                result.inArgument.JHSJ4TB = jhsj;
                                actionFJXHReadDoSync(result.inArgument);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //直接显示执行成功
                             *//*   String msg = isJingTui(result) ? "静推执行成功" : "执行成功";
                                msg = isWeizhuBen(result) ? "微注泵推注执行成功" : msg;*//*
                                String msg = isJingTui(result) ? "执行成功" : "执行成功";
                                msg = isWeizhuBen(result) ? "执行成功" : msg;
                                showSuccessMsg(msg, result);
                            }
                        }).create().show();
              *//*  DialogManager.showClassicDialog(mFragmentActivity, "可以同步到护理记录单", "是否需要执行同步?",
                        new OnBtnClickListener() {
                            @Override
                            public void onBtnClickOk(DialogInterface dialogInterface) {
                                super.onBtnClickOk(dialogInterface);
                                //
                                actionFJXHReadDoSync(result.inArgument);
                            }

                            @Override
                            public void onBtnClickCancel(DialogInterface dialogInterface) {
                                super.onBtnClickCancel(dialogInterface);
                                //直接显示执行成功
                                showSuccessMsg("执行成功");
                            }
                        });*//*
                return;*/
            }
            if (result.selectResult != null) {
                if (result.selectResultCode == 0) {
                    showSuccessMsg("医嘱已同步至护理记录单", result);
                } else if (result.selectResultCode == -777) {
                    //需要提示选择
                    showSuccessMsg("需要选择", result);
                   /* todo 2018-4-18 22:12:02
                   DialogManager.showClassicDialog(mFragmentActivity, "温馨提示", "可以同步，是否继续？",
                            new OnBtnClickListener() {
                                @Override
                                public void onBtnClickOk(DialogInterface dialogInterface) {
                                    super.onBtnClickOk(dialogInterface);
                                    actionFJXHReadDoSync(result.inArgument);
                                }
                            });*/

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
                    syncUtil.InvokeSync(mContext,result.selectResult, mAppApplication.jgId, tasks);
                } else {
                    //-888
                    showSuccessMsg("医嘱同步至护理记录失败", result);
                }
                return;
            }
            //ADD FJXH
            boolean isShowError = false;
            for (int i = 0; i < result.size(); i++) {
                REModel vo = (REModel) result.get(i);
                if (vo.YCLX != 0) {
                    isShowError = true;
                    break;
                }
            }
            if (!isShowError) {
                showSuccessMsg(TextUtils.isEmpty(result.ExceptionMessage) ? "执行成功" : result.ExceptionMessage, result);
                return;
            }
        }
        if (null != result && result.isOK()
                && ExecutType.SY == result.executType) {
            Intent syintent = new Intent(mContext, SYExecutActivity.class);
            syintent.putExtra("mAdvicePlanVoArrayList", result.list);
            startActivityForResult(syintent, 990);
            return;
        }
        if (null != result && result.isOK()
                && ExecutType.KF == result.executType) {

            Intent intent = new Intent(mContext, KFExecutActivity.class);
            intent.putExtra("mAdvicePlanVoArrayList", result.list);
            startActivityForResult(intent, 991);
            return;
        }
        if (null != result && result.isOK()
                && ExecutType.SYZT == result.executType) {
            showSuccessMsg("执行输液暂停成功", result);
            return;
        }
        if (null != result && result.isOK()
                && ExecutType.SYQX == result.executType) {
            showSuccessMsg("执行取消结束输液成功", result);
            return;
        }
        if (null != result && result.isOK()
                && ExecutType.KFQX == result.executType) {
            showSuccessMsg("执行取消执行口服药成功", result);
            return;
        }
        if (null != result && result.isOK()
                && ExecutType.ZSQX == result.executType) {
            showSuccessMsg("执行取消结束注射成功", result);
            return;
        }
        if (null != result && result.isOK()
                && ExecutType.SYJS == result.executType) {
            showSuccessMsg("执行输液结束成功", result);
            return;
        }
       /* MediaUtil.getInstance(mContext).playSound(R.raw.wrong,
                mContext);*/
        showAdviceDialog(result);

    }

    private List<Object> exeList;

    private void showSuccessMsg(String msg, ExecutVo executVo) {
        //返回结果
        try {
            exeList = executVo.get();
        } catch (Exception e) {
            exeList = null;
        }
        //直接显示执行成功
        showMsgAndVoice(msg);
        // 执行成功刷新
       /* if (isMulti) {
            trans_type.check(R.id.multi_radio);
        } else {
            trans_type.check(R.id.single_radio);
        }*/
        //默认每次 执行完后调到接瓶
        trans_type.check(R.id.single_radio);
        toRefreshData();
    }

    private void actionFJXHReadDoSync(InArgument inArgument) {
        FJXHReadDoSync fjxhReadDoSync = new FJXHReadDoSync(inArgument);
        tasks.add(fjxhReadDoSync);
        fjxhReadDoSync.execute();
    }

    class FJXHReadDoSync extends AsyncTask<Void, Void, ExecutVo> {
        private InArgument inArgument;

        public FJXHReadDoSync(InArgument inArgument) {
            this.inArgument = inArgument;
        }

        @Override
        protected void onPreExecute() {
            showLoadingDialog("执行同步中...");
        }

        @Override
        protected ExecutVo doInBackground(Void... params) {
            String realData;
            RequestBodyInfo requestBodyInfo = new RequestBodyInfo();
            try {
                requestBodyInfo.inArgument = inArgument;
                realData = JsonUtil.toJson(requestBodyInfo);
            } catch (IOException e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                return null;
            }
            return AdviceApi.getInstance(mContext).FJXHReadDoSync(realData);
        }

        @Override
        protected void onPostExecute(ExecutVo result) {
            hideLoadingDialog();
            tasks.remove(this);

            if (null == result) {
                showMsgAndVoiceAndVibrator("同步失败");
                return;
            }
            mListView.postDelayed(new Runnable() {

                @Override
                public void run() {
                    toDo(result);
                }
            }, 500);

        }
    }

    /**
     * 医嘱执行Task
     *
     * @author hy
     */
    class ExecutTask extends AsyncTask<String, Void, ExecutVo> {


        /**
         * 手动
         */
        public static final byte HANDLE_EXCUTE = 0;

        /**
         * 扫描
         */
        public static final byte SCAN_EXCUTE = HANDLE_EXCUTE + 1;

        /**
         * 口服
         */
        public static final byte Oral_Medication_EXCUTE = SCAN_EXCUTE + 1;

        /**
         * 输液
         */
        public static final byte TRANSFUSE_EXCUTE = Oral_Medication_EXCUTE + 1;

        /**
         * 拒绝
         */
        public static final byte REFUSE_EXCUTE = TRANSFUSE_EXCUTE + 1;

        /**
         * 继续输液
         */
        public static final byte Transfuse_Continue_EXCUTE = REFUSE_EXCUTE + 1;

        //
        public static final byte Transfuse_Pasue_EXCUTE = Transfuse_Continue_EXCUTE + 1;

        public static final byte Transfuse_EXCUTE_CancelEnd = Transfuse_Pasue_EXCUTE + 1;
        public static final byte Transfuse_End_EXCUTE = Transfuse_EXCUTE_CancelEnd + 1;
        public static final byte Transfuse_EndOrContinue_EXCUTE_4_SCAN = Transfuse_End_EXCUTE + 1;
        public static final byte Transfuse_EXCUTE_CancelStart = Transfuse_EndOrContinue_EXCUTE_4_SCAN + 1;
        public static final byte KF_EXCUTE_CancelStart= Transfuse_EXCUTE_CancelStart + 1;
        public static final byte ZS_EXCUTE_CancelStart= KF_EXCUTE_CancelStart + 1;

        private byte mType = HANDLE_EXCUTE;

        public ExecutTask(byte mType) {
            // this.transfuseBX = transfuseBX;
            // this.checkTime = checkTime;
            this.mType = mType;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (id_swipe_refresh_layout != null && !id_swipe_refresh_layout.isRefreshing()) {
                showLoadingDialog(R.string.doing);
            }
        }

        @Override
        protected ExecutVo doInBackground(String... params) {

            switch (mType) {
                case HANDLE_EXCUTE:
                    if (params == null || params.length < 1) {
                        return null;
                    }
                    return handleExecute(params[0]);
                case SCAN_EXCUTE:
                    if (params == null || params.length < 4) {
                        return null;
                    }
                    return scanExecute(params[0], params[1], params[2], params[3]);
                case Transfuse_EndOrContinue_EXCUTE_4_SCAN:
                    if (params == null || params.length < 5) {
                        return null;
                    }

                    //finalBarcode, finalQZ, String.valueOf(4), finalCheckTimeS,"con"
                    return transfuseEndOrContinue4Scan(params[0], params[1], params[2], params[3], params[4]);
                case Oral_Medication_EXCUTE:
                    if (params == null || params.length < 1) {
                        return null;
                    }
                    return executeOral(params[0]);

                case TRANSFUSE_EXCUTE:

                    if (params == null || params.length < 2) {
                        return null;
                    }
                    return executeTransfuse(params[0], params[1]);

                case REFUSE_EXCUTE:
                    if (params == null || params.length < 1) {
                        return null;
                    }
                    return refuseExecute(params[0]);

                case Transfuse_Continue_EXCUTE:

                    if (params == null || params.length < 2) {
                        return null;
                    }
                    return transfuseContinue(params[0], params[1]);
                case Transfuse_Pasue_EXCUTE:
                    return transfusePause();
                case Transfuse_End_EXCUTE:
                    return transfuseEnd();
                case Transfuse_EXCUTE_CancelEnd:
                    if (params == null || params.length < 1) {
                        return null;
                    }
                    return executeTransfuseCancelEnd2Ing(params[0]);
                case Transfuse_EXCUTE_CancelStart:
                    if (params == null || params.length < 1) {
                        return null;
                    }
                    return executeTransfuseCancelEnd2Start(params[0]);
                case KF_EXCUTE_CancelStart:
                    if (params == null || params.length < 1) {
                        return null;
                    }
                    return executeKFCancelEnd2Start(params[0]);
                case ZS_EXCUTE_CancelStart:
                    if (params == null || params.length < 1) {
                        return null;
                    }
                    return executeZSCancelEnd2Start(params[0]);
                default:
            }
            return null;
        }

        /**
         * 继续输液
         *
         * @param sydh
         * @param qzjsStr
         * @return
         */
        private ExecutVo transfuseContinue(String sydh, String qzjsStr) {

            if (EmptyTool.isBlank(sydh) || EmptyTool.isBlank(qzjsStr)) {
                return null;
            }
            String realData = "";
            try {
                RequestBodyInfo info = new RequestBodyInfo();
                info.ZYH = mAppApplication.sickPersonVo.ZYH;
                info.YHID = mAppApplication.user.YHID;
                info.QRDH = sydh;
                info.QZJS = Boolean.valueOf(qzjsStr);
                info.SYBX = false;
                info.JGID = mAppApplication.jgId;
                realData = JsonUtil.toJson(info);
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                return null;
            }
            return AdviceApi.getInstance(mContext).TransfuseContinue(realData);

        }

        private ExecutVo transfusePause() {

            // 选中的
            ArrayList<AdvicePlanVo> data = mSickPersonAdviceAdapter.getValue();
            if (null != data && data.size() > 0) {
                if (mSickPersonAdviceAdapter.isOneType(data)) {
                    List<PlanArgInfo> planArgInfoList = new ArrayList<>();
                    for (AdvicePlanVo vo : data) {
                        PlanArgInfo info = new PlanArgInfo();
                        info.JHH = vo.JHH;
                        info.GSLX = vo.GSLX;
                        info.ZXZT = vo.ZXZT;
                        planArgInfoList.add(info);
                    }
                    //!!!!!!!!============================================2017-11-13 19:10:37
                    if (planArgInfoList.size() <= 0) {
                        return new ExecutVo(Statue.NO_Chose);
                    }
                    if (planArgInfoList.get(0).GSLX != 4) {
                        ExecutVo executVo = new ExecutVo(Statue.SHOW_MSG);
                        executVo.LogicMsg = "选中的不是输液";
                        return executVo;
                    }
//                    if ("6".equals(planArgInfoList.get(0).YPYF)) {
                    if (mAppApplication.userConfig.jingTui_YaoPinYongFa.equals(planArgInfoList.get(0).YPYF)) {
                        ExecutVo executVo = new ExecutVo(Statue.SHOW_MSG);
                        executVo.LogicMsg = "选中的是静推类输液，无法暂停";
                        return executVo;
                    }
                    if (planArgInfoList.get(0).ZXZT == 2) {
                        //输液  且在 执行中 状态
                        if (mAppApplication.sickPersonVo == null || mAppApplication.user == null) {
                            return null;
                        }
                        String zyh = mAppApplication.sickPersonVo.ZYH;
                        String jgid = mAppApplication.jgId;
                        String sTime = getSelectTime();
                        String sydh = null;
                        //其中一个 就好
                        String jhh = planArgInfoList.get(0).JHH;
                        if (jhh == null) {
                            return null;
                        }
                        //获取当前选择天所有的[执行中、暂停中]输液单和医嘱计划
                        Response<List<PlanAndTransfusion>> transfusionInfoListResponse =
                                AdviceApi.getInstance(mContext).getTransfusionInfoListByZyh4TransfuseExecut(zyh, jgid, sTime);
                        if (transfusionInfoListResponse.ReType == 0 && transfusionInfoListResponse.Data != null
                                && transfusionInfoListResponse.Data.size() > 0) {
                            for (int i = 0; i < transfusionInfoListResponse.Data.size(); i++) {
                                PlanAndTransfusion planAndTransfusion = transfusionInfoListResponse.Data.get(i);
                                String temp = findPlanAndTransfusion_sydh(planAndTransfusion, jhh);
                                if (!TextUtils.isEmpty(temp)) {
                                    //找到了
                                    sydh = temp;
                                    break;
                                }
                            }
                        }
                        ///////
                        String realData = "";
                        try {
                            RequestBodyInfo info = new RequestBodyInfo();
                            info.ZYH = zyh;
                            info.YHID = mAppApplication.user.YHID;
                            //
                            info.QRDH = sydh;
                            info.JGID = jgid;
                            realData = JsonUtil.toJson(info);
                        } catch (Exception e) {
                            Log.e(Constant.TAG, e.getMessage(), e);
                            return null;
                        }
                        return AdviceApi.getInstance(mContext).TransfusePause(realData);
                        ///////
                    } else {
                        ExecutVo executVo = new ExecutVo(Statue.SHOW_MSG);
                        executVo.LogicMsg = "输液不是执行中状态";
                        return executVo;
                    }
                } else {
                    ExecutVo executVo = new ExecutVo(Statue.Special);
                    return executVo;
                }
            } else {
                ExecutVo executVo = new ExecutVo(Statue.SHOW_MSG);
                executVo.LogicMsg = "您没有选中";
                return executVo;
            }


        }

        private ExecutVo transfuseEnd() {

            // 选中的
            ArrayList<AdvicePlanVo> data = mSickPersonAdviceAdapter.getValue();
            if (null != data && data.size() > 0) {
                if (mSickPersonAdviceAdapter.isOneType(data)) {
                    List<PlanArgInfo> planArgInfoList = new ArrayList<>();
                    for (AdvicePlanVo vo : data) {
                        PlanArgInfo info = new PlanArgInfo();
                        info.JHH = vo.JHH;
                        info.GSLX = vo.GSLX;
                        info.ZXZT = vo.ZXZT;
                        planArgInfoList.add(info);
                    }
                    //!!!!!!!!============================================2017-11-13 19:10:37
                    if (planArgInfoList.size() <= 0) {
                        return new ExecutVo(Statue.NO_Chose);
                    }
                    if (planArgInfoList.get(0).GSLX != 4) {
                        ExecutVo executVo = new ExecutVo(Statue.SHOW_MSG);
                        executVo.LogicMsg = "选中的不是输液";
                        return executVo;
                    }
                    if (mAppApplication.userConfig.jingTui_YaoPinYongFa.equals(planArgInfoList.get(0).YPYF)) {
//                    if ("6".equals(planArgInfoList.get(0).YPYF)) {
                        ExecutVo executVo = new ExecutVo(Statue.SHOW_MSG);
                        executVo.LogicMsg = "选中的是静推类输液，无法结束";
                        return executVo;
                    }
                    if (planArgInfoList.get(0).ZXZT == 4) {
                        //输液  且在 暂停中 状态
                        if (mAppApplication.sickPersonVo == null || mAppApplication.user == null) {
                            return null;
                        }
                        String zyh = mAppApplication.sickPersonVo.ZYH;
                        String jgid = mAppApplication.jgId;
                        String sTime = getSelectTime();
                        String sydh = null;
                        //其中一个 就好
                        String jhh = planArgInfoList.get(0).JHH;
                        if (jhh == null) {
                            return null;
                        }
                        //获取当前选择天所有的[执行中、暂停中]输液单和医嘱计划
                        Response<List<PlanAndTransfusion>> transfusionInfoListResponse =
                                AdviceApi.getInstance(mContext).getTransfusionInfoListByZyh4TransfuseExecut(zyh, jgid, sTime);
                        if (transfusionInfoListResponse.ReType == 0 && transfusionInfoListResponse.Data != null
                                && transfusionInfoListResponse.Data.size() > 0) {
                            for (int i = 0; i < transfusionInfoListResponse.Data.size(); i++) {
                                PlanAndTransfusion planAndTransfusion = transfusionInfoListResponse.Data.get(i);
                                String temp = findPlanAndTransfusion_sydh(planAndTransfusion, jhh);
                                if (!TextUtils.isEmpty(temp)) {
                                    //找到了
                                    sydh = temp;
                                    break;
                                }
                            }
                        }
                        ///////
                        String realData = "";
                        try {
                            RequestBodyInfo info = new RequestBodyInfo();
                            info.ZYH = zyh;
                            info.YHID = mAppApplication.user.YHID;
                            //
                            info.QRDH = sydh;
                            info.JGID = jgid;
                            realData = JsonUtil.toJson(info);
                        } catch (Exception e) {
                            Log.e(Constant.TAG, e.getMessage(), e);
                            return null;
                        }
                        return AdviceApi.getInstance(mContext).TransfuseEnd(realData);
                        ///////
                    } else {
                        ExecutVo executVo = new ExecutVo(Statue.SHOW_MSG);
                        executVo.LogicMsg = "输液不是执行中状态";
                        return executVo;
                    }
                } else {
                    ExecutVo executVo = new ExecutVo(Statue.Special);
                    return executVo;
                }
            } else {
                ExecutVo executVo = new ExecutVo(Statue.SHOW_MSG);
                executVo.LogicMsg = "您没有选中";
                return executVo;
            }


        }

        /**
         * 暂停转结束或者继续
         *
         * @param barcode
         * @param prefix
         * @param flbs
         * @param checkTime
         * @param core
         * @return
         */
        private ExecutVo transfuseEndOrContinue4Scan(String barcode, String prefix, String flbs,
                                                     String checkTime, String core) {

            if (EmptyTool.isBlank(barcode) || EmptyTool.isBlank(flbs) || EmptyTool.isBlank(checkTime)) {
                return null;
            }
            if (mAppApplication.sickPersonVo == null || mAppApplication.user == null) {
                return null;
            }
            String zyh = mAppApplication.sickPersonVo.ZYH;
            String yhid = mAppApplication.user.YHID;
            String jgid = mAppApplication.jgId;
            String data = buildRequestData(barcode, prefix, checkTime, zyh, yhid, jgid, core);
            return AdviceApi.getInstance(mContext).ScanExecutNew(data);
        }

        /**
         * 拒绝执行
         *
         * @param key
         * @return
         */
        private ExecutVo refuseExecute(String key) {

            if (EmptyTool.isBlank(key) || mSickPersonAdviceAdapter == null
                    || !NumberUtils.isNumber(key)) {
                return null;
            }

            if (mAppApplication.sickPersonVo == null || mAppApplication.user == null) {
                return null;
            }

            int position = NumberUtils.toInt(key);
            ArrayList<AdvicePlanVo> data = mSickPersonAdviceAdapter.getValue();
            if (null != data && data.size() > 0) {
                if (mSickPersonAdviceAdapter.isOneType(data)) {
                    String realData = "";
                    try {
                        List<PlanArgInfo> planArgInfoList = new ArrayList<>();
                        RequestBodyInfo info = new RequestBodyInfo();
                        info.ZYH = mAppApplication.sickPersonVo.ZYH;
                        info.YHID = mAppApplication.user.YHID;
                        for (AdvicePlanVo vo : data) {
                            PlanArgInfo planArgInfo = new PlanArgInfo();
                            planArgInfo.JHH = vo.JHH;
                            planArgInfo.JHSJ = vo.JHSJ;
                            planArgInfo.YZZH = vo.YZZH;
                            planArgInfo.DYXH = reList.get(position).DYXH;
                            planArgInfoList.add(planArgInfo);
                        }
                        info.PlanArgInfoList = planArgInfoList;
                        info.SYBX = false;
                        info.JYSJ = false;
                        info.JGID = mAppApplication.jgId;
                        realData = JsonUtil.toJson(info);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return AdviceApi.getInstance(mContext).RefuseExecut(realData);
                } else {
                    return new ExecutVo(Statue.Special);
                }
            } else {
                return new ExecutVo(Statue.NO_Chose);
            }
        }

        /**
         * 输液非常规执行(并行接瓶) 多瓶
         *
         * @param SYDH
         * @param QRDH
         * @return
         */
        private ExecutVo executeTransfuse(String SYDH, String QRDH) {

            if (EmptyTool.isBlank(SYDH) || EmptyTool.isBlank(QRDH)) {
                return null;
            }

            if (mAppApplication.sickPersonVo == null || mAppApplication.user == null) {
                return null;
            }

            String zyh = mAppApplication.sickPersonVo.ZYH;
            String yhid = mAppApplication.user.YHID;
            String jgid = mAppApplication.jgId;

            return AdviceApi.getInstance(mContext).TransfuseExecutNew(zyh,
                    yhid, QRDH, SYDH, isMulti, jgid, Constant.sysType);

        }

        private ExecutVo executeTransfuseCancelEnd2Ing(String jhh) {

            if (EmptyTool.isBlank(jhh)) {
                return null;
            }

            if (mAppApplication.sickPersonVo == null || mAppApplication.user == null) {
                return null;
            }

            String zyh = mAppApplication.sickPersonVo.ZYH;
            String yhid = mAppApplication.user.YHID;
            String jgid = mAppApplication.jgId;

            String sydh = "";

            //输液
            String sTime = getSelectTime();
            //获取当前选择天所有de输液单和医嘱计划
            Response<List<PlanAndTransfusion>> transfusionInfoListResponse =
                    AdviceApi.getInstance(mContext).getTransfusionInfoListByZyh4TransfuseExecutAll(zyh, jgid, sTime);
            if (transfusionInfoListResponse.ReType == 0 && transfusionInfoListResponse.Data != null
                    && transfusionInfoListResponse.Data.size() > 0) {
                for (PlanAndTransfusion planAndTransfusion : transfusionInfoListResponse.Data) {
                    String temp = findPlanAndTransfusion_sydh(planAndTransfusion, jhh);
                    if (!TextUtils.isEmpty(temp)) {
                        //找到了
                        sydh = temp;
                        break;
                    }
                }
            }
            if (EmptyTool.isBlank(sydh)) {
                return null;
            }

            String data = "";
            try {
                RequestBodyInfo info = new RequestBodyInfo();
                info.ZYH = zyh;
                info.YHID = yhid;
                info.QRDH = sydh;
                info.JGID = jgid;
                data = JsonUtil.toJson(info);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AdviceApi.getInstance(mContext).TransfuseExecutCancelEnd2Ing(data);

        }

        private ExecutVo executeTransfuseCancelEnd2Start(String jhh) {

            if (EmptyTool.isBlank(jhh)) {
                return null;
            }

            if (mAppApplication.sickPersonVo == null || mAppApplication.user == null) {
                return null;
            }

            String zyh = mAppApplication.sickPersonVo.ZYH;
            String yhid = mAppApplication.user.YHID;
            String jgid = mAppApplication.jgId;

            String sydh = "";

            //输液
            String sTime = getSelectTime();
            //获取当前选择天所有de输液单和医嘱计划
            Response<List<PlanAndTransfusion>> transfusionInfoListResponse =
                    AdviceApi.getInstance(mContext).getTransfusionInfoListByZyh4TransfuseExecutAll(zyh, jgid, sTime);
            if (transfusionInfoListResponse.ReType == 0 && transfusionInfoListResponse.Data != null
                    && transfusionInfoListResponse.Data.size() > 0) {
                for (PlanAndTransfusion planAndTransfusion : transfusionInfoListResponse.Data) {
                    String temp = findPlanAndTransfusion_sydh(planAndTransfusion, jhh);
                    if (!TextUtils.isEmpty(temp)) {
                        //找到了
                        sydh = temp;
                        break;
                    }
                }
            }
            if (EmptyTool.isBlank(sydh)) {
                return null;
            }

            String data = "";
            try {
                RequestBodyInfo info = new RequestBodyInfo();
                info.ZYH = zyh;
                info.YHID = yhid;
                info.QRDH = sydh;
                info.JGID = jgid;
                data = JsonUtil.toJson(info);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AdviceApi.getInstance(mContext).TransfuseExecutCancelEnd2Start(data);

        }

        private ExecutVo executeKFCancelEnd2Start(String jhh) {

            if (EmptyTool.isBlank(jhh)) {
                return null;
            }

            if (mAppApplication.sickPersonVo == null || mAppApplication.user == null) {
                return null;
            }

            String zyh = mAppApplication.sickPersonVo.ZYH;
            String yhid = mAppApplication.user.YHID;
            String jgid = mAppApplication.jgId;
            String data = "";
            try {
                RequestBodyInfo info = new RequestBodyInfo();
                info.ZYH = zyh;
                info.YHID = yhid;
//                info.QRDH = kfdh;
                info.core = jhh;//存放jhh
                info.JGID = jgid;
                data = JsonUtil.toJson(info);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AdviceApi.getInstance(mContext).KFExecutCancelEnd2Start(data);

        }

        private ExecutVo executeZSCancelEnd2Start(String jhh) {

            if (EmptyTool.isBlank(jhh)) {
                return null;
            }

            if (mAppApplication.sickPersonVo == null || mAppApplication.user == null) {
                return null;
            }

            String zyh = mAppApplication.sickPersonVo.ZYH;
            String yhid = mAppApplication.user.YHID;
            String jgid = mAppApplication.jgId;
            String data = "";
            try {
                RequestBodyInfo info = new RequestBodyInfo();
                info.ZYH = zyh;
                info.YHID = yhid;
//                info.QRDH = zsdh;
                info.core = jhh;//存放jhh
                info.JGID = jgid;
                data = JsonUtil.toJson(info);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AdviceApi.getInstance(mContext).ZSExecutCancelEnd2Start(data);

        }
        /**
         * 执行口服药
         *
         * @param qrdh
         * @return
         */
        private ExecutVo executeOral(String qrdh) {

            if (EmptyTool.isBlank(qrdh)) {
                return null;
            }

            if (mAppApplication.sickPersonVo == null || mAppApplication.user == null) {
                return null;
            }

            String data = "";
            try {
                RequestBodyInfo info = new RequestBodyInfo();
                info.ZYH = mAppApplication.sickPersonVo.ZYH;
                info.YHID = mAppApplication.user.YHID;
                info.QRDH = qrdh;
                info.JGID = mAppApplication.jgId;
                data = JsonUtil.toJson(info);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return AdviceApi.getInstance(mContext).OralMedicationExecut(data);
        }

        /**
         * 扫描执行
         *
         * @param barcode
         * @param prefix
         * @param checkTime
         * @return
         */
        private ExecutVo scanExecute(String barcode, String prefix, String flbs,
                                     String checkTime) {

            if (EmptyTool.isBlank(barcode) || EmptyTool.isBlank(flbs) || EmptyTool.isBlank(checkTime)) {
                return null;
            }

            if (mAppApplication.sickPersonVo == null || mAppApplication.user == null) {
                return null;
            }
            /*
            升级编号【56010053】============================================= start
            多瓶超过2瓶转接瓶后提示选择接哪瓶的问题
            ================= Classichu 2017/11/14 16:25

            */
            String zyh = mAppApplication.sickPersonVo.ZYH;
            String yhid = mAppApplication.user.YHID;
            String jgid = mAppApplication.jgId;
            //!!!!!!!!============================================2017-11-13 19:10:37
            if (!"4".equals(flbs)) {
                //不是输液类型 走原有逻辑
                selectedSYDH_scan = null;
                String data = buildRequestData(barcode, prefix, checkTime, zyh, yhid, jgid);
                return AdviceApi.getInstance(mContext).ScanExecutNew(data);
            }
            if (!TextUtils.isEmpty(selectedSYDH_scan)) {
                //已选择输液单  直接执行 否则又提示选择了
                String data = buildRequestData(barcode, prefix, checkTime, zyh, yhid, jgid);
                return AdviceApi.getInstance(mContext).ScanExecutNew(data);
            }
            String sTime = getSelectTime();
            //获取需要执行的码的输液信息
            PlanAndTransfusion planAndTransfusionNeedExecue = null;
            Response<PlanAndTransfusion> tempResponse = AdviceApi.getInstance(mContext)
                    .getTransfusionInfoByBarcode4TransfuseExecut(barcode, prefix, jgid);
            if (tempResponse != null && tempResponse.ReType == 0) {
                planAndTransfusionNeedExecue = tempResponse.Data;
            }
            if (planAndTransfusionNeedExecue == null || planAndTransfusionNeedExecue.planInfoList == null) {
                //取不到需要执行的
                return new ExecutVo(Statue.NO_Chose);
            }
            if (planAndTransfusionNeedExecue.planInfoList.size() <= 0) {
                //未找到条码号xxx的标签,请确认是否已停嘱或已作废
                ExecutVo executVo = new ExecutVo(Statue.SHOW_MSG);
                executVo.LogicMsg = "未找到条码号" + prefix + barcode + "的标签,请确认是否已停嘱或已作废";
                return executVo;

            }
//            if ("6".equals(planAndTransfusionNeedExecue.planInfoList.get(0).YPYF)) {
            if (mAppApplication.userConfig.jingTui_YaoPinYongFa.equals(planAndTransfusionNeedExecue.planInfoList.get(0).YPYF)) {
                //是输液类型 但是是静推_类型  走原有逻辑
                selectedSYDH_scan = null;
                String data = buildRequestData(barcode, prefix, checkTime, zyh, yhid, jgid);
                return AdviceApi.getInstance(mContext).ScanExecutNew(data);
            }
            //获取当前选择天所有的[执行中、暂停中]输液单和医嘱计划
            Response<List<PlanAndTransfusion>> transfusionInfoListResponse =
                    AdviceApi.getInstance(mContext).getTransfusionInfoListByZyh4TransfuseExecut(zyh, jgid, sTime);
            if (transfusionInfoListResponse.ReType == 0 && transfusionInfoListResponse.Data != null
                    && transfusionInfoListResponse.Data.size() > 0) {
                //执行中的输液数据
                List<PlanAndTransfusion> transfusionInfoList_All =
                        filterPlanAndTransfusions(transfusionInfoListResponse);

                //执行【微注泵推注_类型】逻辑
                if (mAppApplication.userConfig.weiZhuBenTuiZhu_YaoPinYongFa.equals(planAndTransfusionNeedExecue.planInfoList.get(0).YPYF)) {
//                if ("22".equals(planAndTransfusionNeedExecue.planInfoList.get(0).YPYF)) {
                    //执行中的输液[微注泵推注_类型]数据
                    List<PlanAndTransfusion> transfusionInfoListNew4WeiZhu =
                            filterPlanAndTransfusions4WeiZhu(transfusionInfoListResponse);
                    //暂停中的输液[微注泵推注_类型]数据
                    List<PlanAndTransfusion> transfusionInfoListNewZT4WeiZhu =
                            filterPlanAndTransfusionsZT4WeiZhu(transfusionInfoListResponse);
                    //结束的输液[微注泵推注_类型]数据
                    List<PlanAndTransfusion> transfusionInfoListNewJS4WeiZhu =
                            filterPlanAndTransfusionsJS4WeiZhu(transfusionInfoListResponse);
                    //判断当前需要执行的输液[微注泵推注_类型]是否已经在执行中
                    if (!isPlanAndTransfusionHasInTransfusionInfoListNew(planAndTransfusionNeedExecue,
                            transfusionInfoListNew4WeiZhu)) {
                        //需要执行的输液[微注泵推注_类型]已经在暂停中
                        if (isPlanAndTransfusionHasInTransfusionInfoListNew(planAndTransfusionNeedExecue, transfusionInfoListNewZT4WeiZhu)) {
                            //判断当前需要执行的输液是否已经在暂停中
                            selectedSYDH_scan = getPlanAndTransfusionHasInTransfusionInfoListNew_SYDH(planAndTransfusionNeedExecue, transfusionInfoListNewZT4WeiZhu);
                            String data = buildRequestData(barcode, prefix, checkTime, zyh, yhid, jgid);
                            return AdviceApi.getInstance(mContext).ScanExecutNew(data);
                        }
                        //需要执行的输液[微注泵推注_类型]已经在结束中
                        if (isPlanAndTransfusionHasInTransfusionInfoListNew(planAndTransfusionNeedExecue, transfusionInfoListNewJS4WeiZhu)) {
                            //判断当前需要执行的输液是否已经在结束中
                            ExecutVo executVo = new ExecutVo(Statue.SHOW_MSG);
//                            executVo.LogicMsg = "该输液[微注泵推注]已经执行";
                            executVo.LogicMsg = "该输液已经执行";
                            return executVo;
                        }
                        //没在执行中
                        if (!isMulti) {
                            //接瓶
                            if (transfusionInfoListNew4WeiZhu.size() > 1) {
                                //当前执行中的输液是多个 显示选择对话框
                                return showSelectedDialog(transfusionInfoListNew4WeiZhu, Statue.Show_List_Selector_scanExecute);
                            } else if (transfusionInfoListNew4WeiZhu.size() == 1) {
                                //单个
                                selectedSYDH_scan = transfusionInfoListNew4WeiZhu.get(0).SYDH;
                                String data = buildRequestData(barcode, prefix, checkTime, zyh, yhid, jgid);
                                return AdviceApi.getInstance(mContext).ScanExecutNew(data);
                            } else {
                                //没有  退而求其次 取 所有的
                                if (transfusionInfoList_All.size() > 1) {
                                    //当前执行中的输液是多个 显示选择对话框
                                    return showSelectedDialog(transfusionInfoList_All, Statue.Show_List_Selector_scanExecute);
                                } else if (transfusionInfoList_All.size() == 1) {
                                    //单个
                                    selectedSYDH_scan = transfusionInfoList_All.get(0).SYDH;
                                    String data = buildRequestData(barcode, prefix, checkTime, zyh, yhid, jgid);
                                    return AdviceApi.getInstance(mContext).ScanExecutNew(data);
                                } else {
                                    //单个
                                    selectedSYDH_scan = null;
                                    String data = buildRequestData(barcode, prefix, checkTime, zyh, yhid, jgid);
                                    return AdviceApi.getInstance(mContext).ScanExecutNew(data);
                                }
                            }
                        } else {
                            selectedSYDH_scan = null;
                            //多瓶
                            String data = buildRequestData(barcode, prefix, checkTime, zyh, yhid, jgid);
                            return AdviceApi.getInstance(mContext).ScanExecutNew(data);
                        }
                    } else {
                        //需要执行的输液已经在执行中
                        selectedSYDH_scan = getPlanAndTransfusionHasInTransfusionInfoListNew_SYDH(planAndTransfusionNeedExecue, transfusionInfoListNew4WeiZhu);
                        String data = buildRequestData(barcode, prefix, checkTime, zyh, yhid, jgid);
                        return AdviceApi.getInstance(mContext).ScanExecutNew(data);
                        //
                    }
                    //
                }
                //

                ////////
                //执行中的输液数据
                List<PlanAndTransfusion> transfusionInfoListNew4Other =
                        filterPlanAndTransfusions4Other(transfusionInfoListResponse);
                //暂停中的输液数据
                List<PlanAndTransfusion> transfusionInfoListNewZT4Other =
                        filterPlanAndTransfusionsZT4Other(transfusionInfoListResponse);
                //暂停中的输液数据
                List<PlanAndTransfusion> transfusionInfoListNewJS4Other =
                        filterPlanAndTransfusionsJS4Other(transfusionInfoListResponse);
                //判断当前需要执行的输液[其他]是否已经在执行中
                if (!isPlanAndTransfusionHasInTransfusionInfoListNew(planAndTransfusionNeedExecue,
                        transfusionInfoListNew4Other)) {
                    //需要执行的输液[其他]已经在暂停中
                    if (isPlanAndTransfusionHasInTransfusionInfoListNew(planAndTransfusionNeedExecue, transfusionInfoListNewZT4Other)) {
                        //判断当前需要执行的输液[其他]是否已经在暂停中
                        selectedSYDH_scan = getPlanAndTransfusionHasInTransfusionInfoListNew_SYDH(planAndTransfusionNeedExecue, transfusionInfoListNewZT4Other);
                        String data = buildRequestData(barcode, prefix, checkTime, zyh, yhid, jgid);
                        return AdviceApi.getInstance(mContext).ScanExecutNew(data);
                    }
                    //需要执行的输液[其他]已经在结束中
                    if (isPlanAndTransfusionHasInTransfusionInfoListNew(planAndTransfusionNeedExecue, transfusionInfoListNewJS4Other)) {
                        //判断当前需要执行的输液是否已经在结束中
                        ExecutVo executVo = new ExecutVo(Statue.SHOW_MSG);
                        executVo.LogicMsg = "该输液已经执行";
                        return executVo;
                    }
                    //没在执行中
                    if (!isMulti) {
                        //接瓶
                        if (transfusionInfoListNew4Other.size() > 1) {
                            //当前执行中的输液是多个 显示选择对话框
                            return showSelectedDialog(transfusionInfoListNew4Other, Statue.Show_List_Selector_scanExecute);
                        } else if (transfusionInfoListNew4Other.size() == 1) {
                            //单个
                            selectedSYDH_scan = transfusionInfoListNew4Other.get(0).SYDH;
                            String data = buildRequestData(barcode, prefix, checkTime, zyh, yhid, jgid);
                            return AdviceApi.getInstance(mContext).ScanExecutNew(data);
                        } else {
                            //没有  退而求其次

                            if (transfusionInfoList_All.size() > 1) {
                                //当前执行中的输液是多个 显示选择对话框
                                return showSelectedDialog(transfusionInfoList_All, Statue.Show_List_Selector_scanExecute);
                            } else if (transfusionInfoList_All.size() == 1) {
                                //单个
                                selectedSYDH_scan = transfusionInfoList_All.get(0).SYDH;
                                String data = buildRequestData(barcode, prefix, checkTime, zyh, yhid, jgid);
                                return AdviceApi.getInstance(mContext).ScanExecutNew(data);
                            } else {
                                //0 个
                                selectedSYDH_scan = null;
                                String data = buildRequestData(barcode, prefix, checkTime, zyh, yhid, jgid);
                                return AdviceApi.getInstance(mContext).ScanExecutNew(data);
                            }
                        }
                    } else {
                        selectedSYDH_scan = null;
                        //多瓶
                        String data = buildRequestData(barcode, prefix, checkTime, zyh, yhid, jgid);
                        return AdviceApi.getInstance(mContext).ScanExecutNew(data);
                    }
                } else {
                    //需要执行的输液已经在执行中
                    selectedSYDH_scan = getPlanAndTransfusionHasInTransfusionInfoListNew_SYDH(planAndTransfusionNeedExecue, transfusionInfoListNew4Other);
                    String data = buildRequestData(barcode, prefix, checkTime, zyh, yhid, jgid);
                    return AdviceApi.getInstance(mContext).ScanExecutNew(data);
                    //
                }
                //!!!!!!!!============================================2017-11-13 19:10:37
                /* =============================================================== end */

            }
            selectedSYDH_scan = null;
            String data = buildRequestData(barcode, prefix, checkTime, zyh, yhid, jgid);
            return AdviceApi.getInstance(mContext).ScanExecutNew(data);
        }

        private String buildRequestDataHand(String zyh, String yhid, String jgid,
                                            boolean checkTime, List<PlanArgInfo> planArgInfoList) {
            String realData = "";
            try {
                RequestBodyInfo info = new RequestBodyInfo();
                info.ZYH = zyh;
                info.YHID = yhid;
                info.SYBX = isMulti;
                info.JYSJ = checkTime;
                info.PlanArgInfoList = planArgInfoList;
                info.JGID = jgid;
                if (!EmptyTool.isBlank(selectedSYDH_hand) && !isMulti) {
                    info.transfuse_sp_sydh = selectedSYDH_hand;
                }
                realData = JsonUtil.toJson(info);
            } catch (Exception e) {
                Log.e(Constant.TAG, e.getMessage(), e);
                return null;
            }
            return realData;
        }

        private String buildRequestData(String barcode, String prefix, String checkTime, String zyh, String yhid, String jgid) {
            return buildRequestData(barcode, prefix, checkTime, zyh, yhid, jgid, null);
        }

        private String buildRequestData(String barcode, String prefix, String checkTime, String zyh, String yhid, String jgid,
                                        String core) {
            String data = "";
            try {
                RequestBodyInfo info = new RequestBodyInfo();
                info.ZYH = zyh;
                info.YHID = yhid;
                info.TMNR = barcode;
                info.TMQZ = prefix;
                info.SYBX = isMulti;
                info.JYSJ = Boolean.valueOf(checkTime);
                info.JGID = jgid;
                info.core = core;
                if (!EmptyTool.isBlank(selectedSYDH_scan) && !isMulti) {
                    info.transfuse_sp_sydh = selectedSYDH_scan;
                }
                data = JsonUtil.toJson(info);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return data;
        }


        /**
         * 手动执行
         *
         * @param checkTimeStr
         * @return
         */
        private ExecutVo handleExecute(String checkTimeStr) {
            if (EmptyTool.isBlank(checkTimeStr)) {
                return null;
            }
            if (mAppApplication.sickPersonVo == null || mAppApplication.user == null) {
                return null;
            }
            String zyh = mAppApplication.sickPersonVo.ZYH;
            String yhid = mAppApplication.user.YHID;
            String jgid = mAppApplication.jgId;
    /*
            升级编号【56010053】============================================= start
            多瓶超过2瓶转接瓶后提示选择接哪瓶的问题
            ================= Classichu 2017/11/14 16:25
            */
            // boolean transfuseBX = Boolean.valueOf(transfuseBXStr);
            boolean checkTime = Boolean.valueOf(checkTimeStr);
            // 选中的
            ArrayList<AdvicePlanVo> data = mSickPersonAdviceAdapter.getValue();
            if (null != data && data.size() > 0) {
                if (mSickPersonAdviceAdapter.isOneType(data)) {
                    List<PlanArgInfo> planArgInfoList = new ArrayList<>();
                    for (AdvicePlanVo vo : data) {
                        PlanArgInfo info = new PlanArgInfo();
                        info.JHH = vo.JHH;
                        info.GSLX = vo.GSLX;
                        info.ZXZT = vo.ZXZT;
                        info.YPYF = vo.YPYF;
                        planArgInfoList.add(info);
                    }
                    //!!!!!!!!============================================2017-11-13 19:10:37
                    if (planArgInfoList.size() <= 0) {
                        return new ExecutVo(Statue.NO_Chose);
                    }
                    if (planArgInfoList.get(0).GSLX != 4) {
                        //不是输液 原有逻辑
                        selectedSYDH_hand = null;
                        String requestData = buildRequestDataHand(zyh, yhid, jgid, checkTime, planArgInfoList);
                        return AdviceApi.getInstance(mContext).HandExecutNew(requestData);
                    }
                    if (mAppApplication.userConfig.jingTui_YaoPinYongFa.equals(planArgInfoList.get(0).YPYF)) {
//                    if ("6".equals(planArgInfoList.get(0).YPYF)) {
                        //是输液 但是是静推_类型 开始即结束  原有逻辑
                        selectedSYDH_hand = null;
                        String requestData = buildRequestDataHand(zyh, yhid, jgid, checkTime, planArgInfoList);
                        return AdviceApi.getInstance(mContext).HandExecutNew(requestData);
                    }
                    if (!EmptyTool.isBlank(selectedSYDH_hand)) {
                        //已选 直接执行
                        String requestData = buildRequestDataHand(zyh, yhid, jgid, checkTime, planArgInfoList);
                        return AdviceApi.getInstance(mContext).HandExecutNew(requestData);
                    }
                    //输液
                    String sTime = getSelectTime();
                    //获取当前选择天所有的[执行中、暂停中]输液单和医嘱计划
                    Response<List<PlanAndTransfusion>> transfusionInfoListResponse =
                            AdviceApi.getInstance(mContext).getTransfusionInfoListByZyh4TransfuseExecut(zyh, jgid, sTime);
                    if (transfusionInfoListResponse.ReType == 0 && transfusionInfoListResponse.Data != null
                            && transfusionInfoListResponse.Data.size() > 0) {

                        //有正在执行的输液
                        List<PlanAndTransfusion> transfusionInfoList_All =
                                filterPlanAndTransfusions(transfusionInfoListResponse);

                        //执行【微注泵推注_类型】处理逻辑
                        if (mAppApplication.userConfig.weiZhuBenTuiZhu_YaoPinYongFa.equals(planArgInfoList.get(0).YPYF)) {
//                        if ("22".equals(planArgInfoList.get(0).YPYF)) {
                            //是输液 但是是微注泵推注_类型

                            //有正在执行的输液[微注泵推注_类型]
                            List<PlanAndTransfusion> transfusionInfoListNew4WeiZhu =
                                    filterPlanAndTransfusions4WeiZhu(transfusionInfoListResponse);
                            List<PlanAndTransfusion> transfusionInfoListNewZT4WeiZhu =
                                    filterPlanAndTransfusionsZT4WeiZhu(transfusionInfoListResponse);

                            //判断当前需要执行的输液[微注泵推注_类型]是否已经在执行中
                            if (!isPlanArgInfoListHasInTransfusionInfoListNew(planArgInfoList, transfusionInfoListNew4WeiZhu)) {
                                //需要执行的输液[微注泵推注_类型]已经在暂停中
                                if (isPlanArgInfoListHasInTransfusionInfoListNew(planArgInfoList, transfusionInfoListNewZT4WeiZhu)) {
                                    //判断当前需要执行的输液[微注泵推注_类型]是否已经在暂停中
                                    selectedSYDH_hand = getPlanArgInfoListHasInTransfusionInfoListNew_SYDH(planArgInfoList, transfusionInfoListNewZT4WeiZhu);
                                    String realData = buildRequestDataHand(zyh, yhid, jgid, checkTime, planArgInfoList);
                                    return AdviceApi.getInstance(mContext).HandExecutNew(realData);
                                }
                                if (!isMulti) {
                                    //接瓶状态下（之前执行过多瓶，存在2瓶以上执行中的，进行判断处理）
                                    if (transfusionInfoListNew4WeiZhu.size() > 1) {
                                        //当前执行中的输液是多个
                                        return showSelectedDialog(transfusionInfoListNew4WeiZhu, Statue.Show_List_Selector_handleExecute);
                                    } else if (transfusionInfoListNew4WeiZhu.size() == 1) {
                                        //执行中只有一个
                                        selectedSYDH_hand = transfusionInfoListNew4WeiZhu.get(0).SYDH;
                                        String realData = buildRequestDataHand(zyh, yhid, jgid, checkTime, planArgInfoList);
                                        return AdviceApi.getInstance(mContext).HandExecutNew(realData);
                                    } else {
                                        //没有[微注泵推注_类型] 退而求其次 接普通的
                                        if (transfusionInfoList_All.size() > 1) {
                                            //当前执行中的输液是多个
                                            return showSelectedDialog(transfusionInfoList_All, Statue.Show_List_Selector_handleExecute);
                                        } else if (transfusionInfoList_All.size() == 1) {
                                            //执行中只有一个
                                            selectedSYDH_hand = transfusionInfoList_All.get(0).SYDH;
                                            String realData = buildRequestDataHand(zyh, yhid, jgid, checkTime, planArgInfoList);
                                            return AdviceApi.getInstance(mContext).HandExecutNew(realData);
                                        } else {
                                            //没有
                                            selectedSYDH_hand = null;
                                            String realData = buildRequestDataHand(zyh, yhid, jgid, checkTime, planArgInfoList);
                                            return AdviceApi.getInstance(mContext).HandExecutNew(realData);
                                        }
                                    }
                                } else {
                                    selectedSYDH_hand = null;
                                    String realData = buildRequestDataHand(zyh, yhid, jgid, checkTime, planArgInfoList);
                                    return AdviceApi.getInstance(mContext).HandExecutNew(realData);
                                }
                            } else {
                                //需要执行的输液[微注泵推注_类型]已经在执行中
                                selectedSYDH_hand = getPlanArgInfoListHasInTransfusionInfoListNew_SYDH(planArgInfoList, transfusionInfoListNew4WeiZhu);
                                String realData = buildRequestDataHand(zyh, yhid, jgid, checkTime, planArgInfoList);
                                return AdviceApi.getInstance(mContext).HandExecutNew(realData);
                            }
                        }

                        //执行【其他输液】逻辑
                        //有正在执行的输液[非微注泵推注]
                        List<PlanAndTransfusion> transfusionInfoListNew4Other =
                                filterPlanAndTransfusions4Other(transfusionInfoListResponse);
                        List<PlanAndTransfusion> transfusionInfoListNewZT4Other =
                                filterPlanAndTransfusionsZT4Other(transfusionInfoListResponse);

                        //判断当前需要执行的输液[其他]是否已经在执行中
                        if (!isPlanArgInfoListHasInTransfusionInfoListNew(planArgInfoList, transfusionInfoListNew4Other)) {
                            //需要执行的输液[其他]已经在暂停中
                            if (isPlanArgInfoListHasInTransfusionInfoListNew(planArgInfoList, transfusionInfoListNewZT4Other)) {
                                //判断当前需要执行的输液[其他]是否已经在暂停中
                                selectedSYDH_hand = getPlanArgInfoListHasInTransfusionInfoListNew_SYDH(planArgInfoList, transfusionInfoListNewZT4Other);
                                String realData = buildRequestDataHand(zyh, yhid, jgid, checkTime, planArgInfoList);
                                return AdviceApi.getInstance(mContext).HandExecutNew(realData);
                            }
                            if (!isMulti) {
                                //接瓶状态下（之前执行过多瓶，存在2瓶以上执行中的，进行判断处理）
                                if (transfusionInfoListNew4Other.size() > 1) {
                                    //当前执行中的输液是多个
                                    return showSelectedDialog(transfusionInfoListNew4Other, Statue.Show_List_Selector_handleExecute);
                                } else if (transfusionInfoListNew4Other.size() == 1) {
                                    //执行中只有一个
                                    selectedSYDH_hand = transfusionInfoListNew4Other.get(0).SYDH;
                                    String realData = buildRequestDataHand(zyh, yhid, jgid, checkTime, planArgInfoList);
                                    return AdviceApi.getInstance(mContext).HandExecutNew(realData);
                                } else {
                                    //没有[其他] 退而求其次 接微注泵推注_类型的
                                    if (transfusionInfoList_All.size() > 1) {
                                        //当前执行中的输液是多个
                                        return showSelectedDialog(transfusionInfoList_All, Statue.Show_List_Selector_handleExecute);
                                    } else if (transfusionInfoList_All.size() == 1) {
                                        //执行中只有一个
                                        selectedSYDH_hand = transfusionInfoList_All.get(0).SYDH;
                                        String realData = buildRequestDataHand(zyh, yhid, jgid, checkTime, planArgInfoList);
                                        return AdviceApi.getInstance(mContext).HandExecutNew(realData);
                                    } else {
                                        //没有
                                        selectedSYDH_hand = null;
                                        String realData = buildRequestDataHand(zyh, yhid, jgid, checkTime, planArgInfoList);
                                        return AdviceApi.getInstance(mContext).HandExecutNew(realData);
                                    }
                                }
                            } else {
                                selectedSYDH_hand = null;
                                String realData = buildRequestDataHand(zyh, yhid, jgid, checkTime, planArgInfoList);
                                return AdviceApi.getInstance(mContext).HandExecutNew(realData);
                            }
                        } else {
                            //需要执行的输液已经在执行中
                            selectedSYDH_hand = getPlanArgInfoListHasInTransfusionInfoListNew_SYDH(planArgInfoList, transfusionInfoListNew4Other);
                            String realData = buildRequestDataHand(zyh, yhid, jgid, checkTime, planArgInfoList);
                            return AdviceApi.getInstance(mContext).HandExecutNew(realData);
                        }

                    }
                    //!!!!!!!!============================================2017-11-13 19:10:37
                    /* =============================================================== end */
                    //////////////////////////
                    selectedSYDH_hand = null;
                    String realData = buildRequestDataHand(zyh, yhid, jgid, checkTime, planArgInfoList);
                    return AdviceApi.getInstance(mContext).HandExecutNew(realData);
                } else {
                    return new ExecutVo(Statue.Special);
                }
            }
            return new ExecutVo(Statue.NO_Chose);
        }


        @Override
        protected void onPostExecute(final ExecutVo result) {
            super.onPostExecute(result);
            hideLoadingDialog();
            tasks.remove(this);
            //置空
            selectedSYDH_hand = null;
            selectedSYDH_scan = null;
            if (null == result) {
                showMsgAndVoiceAndVibrator("请求失败:参数错误");
                return;
            }
            if (result.statue == Statue.NO_Chose) {
//                VibratorUtil.vibratorMsg(vib, "你没有选中啊", mContext);
                showMsgAndVoiceAndVibrator("您没有选中");
                return;
            }
            if (result.statue == Statue.Special) {
//                boolean vib = mAppApplication.getSettingConfig().vib;
//                VibratorUtil.vibratorMsg(vib, "选择的不是同一类型", mContext);
                showMsgAndVoiceAndVibrator("选择的不是同一类型");
                return;
            }
            if (result.statue == Statue.SHOW_MSG) {
                if (TextUtils.isEmpty(result.LogicMsg)) {
                    showMsgAndVoiceAndVibrator("请求失败！");
                } else {
                    showMsgAndVoice(result.LogicMsg);
                }
                return;
            }
            if (result.statue == Statue.SHOW_CORE) {
                String barcode_QZ = result.ExceptionMessage;
                String barcode = "";
                String QZ = "";
                String checkTimeS = "";
                if (!TextUtils.isEmpty(barcode_QZ)) {
                    String[] barcode_QZ_ARR = barcode_QZ.split(",");
                    if (barcode_QZ_ARR.length > 2) {
                        barcode = barcode_QZ_ARR[0];
                        QZ = barcode_QZ_ARR[1];
                        checkTimeS = barcode_QZ_ARR[2];
                    }
                }
                if (!TextUtils.isEmpty(barcode) && !TextUtils.isEmpty(QZ)) {
                    //输液 暂停的
                    String finalBarcode = barcode;
                    String finalQZ = QZ;
                    String finalCheckTimeS = checkTimeS;
                    DialogManager.showClassicDialog(mFragmentActivity, "温馨提示",
                            "现在输液暂停状态，继续还是停止？",
                            new OnBtnClickListener() {
                                @Override
                                public void onBtnClickOk(DialogInterface dialogInterface) {
                                    super.onBtnClickOk(dialogInterface);
                                    //继续
                                    performExcute(ExecutTask.Transfuse_EndOrContinue_EXCUTE_4_SCAN,
                                            finalBarcode, finalQZ, String.valueOf(4), finalCheckTimeS, "con");
                                }

                                @Override
                                public void onBtnClickCancel(DialogInterface dialogInterface) {
                                    super.onBtnClickCancel(dialogInterface);
                                    //结束
                                    performExcute(ExecutTask.Transfuse_EndOrContinue_EXCUTE_4_SCAN,
                                            finalBarcode, finalQZ, String.valueOf(4), finalCheckTimeS, "end");
                                }
                            }, "继续", "结束", "sho");
                } else {
                    showMsgAndVoiceAndVibrator("执行暂停的数据准备有误，请重试");
                }
                return;
            }
                /*
            升级编号【56010053】============================================= start
            多瓶超过2瓶转接瓶后提示选择接哪瓶的问题
            ================= Classichu 2017/11/14 16:25

            */
            if (result.statue == Statue.Show_List_Selector_handleExecute) {
                ListSelectDialogFragment listSelectDialogFragment = ListSelectDialogFragment.newInstance("请选择接哪个瓶", selectMap, false, new ListSelected() {
                    @Override
                    public void onListSelected(String key, String value) {
                        selectedSYDH_hand = key;
                        actionDoHandleExecue(true);
                    }
                });
                listSelectDialogFragment.show(getChildFragmentManager(), "listSelectDialogFragment");
                return;
            }
            if (result.statue == Statue.Show_List_Selector_scanExecute) {
                ListSelectDialogFragment listSelectDialogFragment = ListSelectDialogFragment.newInstance("请选择接哪个瓶", selectMap, false, new ListSelected() {
                    @Override
                    public void onListSelected(String key, String value) {
                        selectedSYDH_scan = key;
                        actionDoScanExecue(barinfo, true);
                    }
                });
                listSelectDialogFragment.show(getChildFragmentManager(), "listSelectDialogFragment");
                return;
            }
            /* =============================================================== end */
            mListView.postDelayed(new Runnable() {

                @Override
                public void run() {
                    toDo(result);
                }
            }, 500);

        }

    }

    private String findPlanAndTransfusion_sydh(PlanAndTransfusion planAndTransfusion, String jhh) {
        if (planAndTransfusion == null) {
            return null;
        }
        if (planAndTransfusion.planInfoList == null) {
            return null;
        }
        for (int i = 0; i < planAndTransfusion.planInfoList.size(); i++) {
            if (jhh.equals(planAndTransfusion.planInfoList.get(i).JHH)) {
                return planAndTransfusion.SYDH;
            }
        }
        return null;
    }

    @NonNull
    private ExecutVo showSelectedDialog(List<PlanAndTransfusion> transfusionInfoList, int show_list_selector) {
        selectMap = new HashMap<>();
        //没有在执行在执行中，显示接哪一瓶选择对话框
        for (PlanAndTransfusion transfusionInfo : transfusionInfoList) {
            StringBuilder yzmcStringBuilder = new StringBuilder();
            if (transfusionInfo.planInfoList != null && transfusionInfo.planInfoList.size() > 0) {
                for (int i = 0; i < transfusionInfo.planInfoList.size(); i++) {
                    String yf = "  ";//如果没有用法 可以空格
//                    if ("6".equals(transfusionInfo.planInfoList.get(i).YPYF)) {
                    if (mAppApplication.userConfig.jingTui_YaoPinYongFa.equals(transfusionInfo.planInfoList.get(i).YPYF)) {
//                        yf = "【静推】";
                        yf = "【"+transfusionInfo.planInfoList.get(i).YPYFMC+"】";
//                    } else if ("22".equals(transfusionInfo.planInfoList.get(i).YPYF)) {
                    } else if (mAppApplication.userConfig.weiZhuBenTuiZhu_YaoPinYongFa.equals(transfusionInfo.planInfoList.get(i).YPYF)) {
//                        yf = "【微注泵推注】";
                        yf = "【"+transfusionInfo.planInfoList.get(i).YPYFMC+"】";
                    }
                    String lsbsChar = "";
                    String lsbs = transfusionInfo.planInfoList.get(i).LSBS;
                    if (!TextUtils.isEmpty(lsbs)) {
                        int lsbsInt = Integer.valueOf(lsbs);
                        lsbsChar = NumberCharParser.parserNumWithCircle(lsbsInt);
                    }
                    yzmcStringBuilder.append(lsbsChar).append(yf).append(transfusionInfo.planInfoList.get(i).YZMC)
                            .append("  ").append(transfusionInfo.planInfoList.get(i).SJMC);
                    if (i != transfusionInfo.planInfoList.size() - 1) {
                        yzmcStringBuilder.append("\n");
                    }
                }
            }
            selectMap.put(transfusionInfo.SYDH, yzmcStringBuilder.toString());
        }
        return new ExecutVo(show_list_selector);
    }

        /*
            升级编号【56010053】============================================= start
            多瓶超过2瓶转接瓶后提示选择接哪瓶的问题
            ================= Classichu 2017/11/14 16:25

            */

    /**
     * @param planAndTransfusionNeedExecue 当前需要执行的
     * @param transfusionInfoList          执行中的输液
     * @return
     */
    private boolean isPlanAndTransfusionHasInTransfusionInfoListNew(PlanAndTransfusion planAndTransfusionNeedExecue, List<PlanAndTransfusion> transfusionInfoList) {
        for (PlanAndTransfusion planAndTransfusion : transfusionInfoList) {
            if (planAndTransfusion.planInfoList != null && planAndTransfusion.planInfoList.size() > 0) {
                if (planAndTransfusionNeedExecue.SYDH.equals(planAndTransfusion.SYDH)) {
                    return true;
                }
            }
        }
        return false;
    }

    private String getPlanAndTransfusionHasInTransfusionInfoListNew_SYDH(PlanAndTransfusion planAndTransfusionNeedExecue, List<PlanAndTransfusion> transfusionInfoList) {
        for (PlanAndTransfusion planAndTransfusion : transfusionInfoList) {
            if (planAndTransfusion.planInfoList != null && planAndTransfusion.planInfoList.size() > 0) {
                if (planAndTransfusionNeedExecue.SYDH.equals(planAndTransfusion.SYDH)) {
                    return planAndTransfusionNeedExecue.SYDH;
                }
            }
        }
        return null;
    }

    private boolean isPlanArgInfoListHasInTransfusionInfoListNew(List<PlanArgInfo> planArgInfoList, List<PlanAndTransfusion> transfusionInfoList) {
        for (PlanAndTransfusion planAndTransfusion : transfusionInfoList) {
            if (planAndTransfusion.planInfoList != null && planAndTransfusion.planInfoList.size() > 0) {
                String jhh = planAndTransfusion.planInfoList.get(0).JHH;//其中一个
                for (PlanArgInfo planArgInfo : planArgInfoList) {
                    if (planArgInfo.JHH.equals(jhh)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String getPlanArgInfoListHasInTransfusionInfoListNew_SYDH(List<PlanArgInfo> planArgInfoList, List<PlanAndTransfusion> transfusionInfoList) {
        for (PlanAndTransfusion planAndTransfusion : transfusionInfoList) {
            if (planAndTransfusion.planInfoList != null && planAndTransfusion.planInfoList.size() > 0) {
                String jhh = planAndTransfusion.planInfoList.get(0).JHH;//其中一个
                for (PlanArgInfo planArgInfo : planArgInfoList) {
                    if (planArgInfo.JHH.equals(jhh)) {
                        return planAndTransfusion.SYDH;
                    }
                }
            }
        }
        return null;
    }

    private List<PlanAndTransfusion> filterPlanAndTransfusions4WeiZhu(Response<List<PlanAndTransfusion>> transfusionInfoListResponse) {
        List<PlanAndTransfusion> transfusionInfoListNew = new ArrayList<>();
        for (PlanAndTransfusion transfusionInfo : transfusionInfoListResponse.Data) {
//            if ("2".equals(transfusionInfo.SYZT) && "22".equals(transfusionInfo.planInfoList.get(0).YPYF)) {
            if ("2".equals(transfusionInfo.SYZT) &&
                    mAppApplication.userConfig.weiZhuBenTuiZhu_YaoPinYongFa.equals(transfusionInfo.planInfoList.get(0).YPYF)) {
                //执行中的输液[微注泵推注_类型]数据
                transfusionInfoListNew.add(transfusionInfo);
            }
        }
        return transfusionInfoListNew;
    }

    private List<PlanAndTransfusion> filterPlanAndTransfusionsJS4WeiZhu(Response<List<PlanAndTransfusion>> transfusionInfoListResponse) {
        List<PlanAndTransfusion> transfusionInfoListNew = new ArrayList<>();
        for (PlanAndTransfusion transfusionInfo : transfusionInfoListResponse.Data) {
//            if ("1".equals(transfusionInfo.SYZT) && "22".equals(transfusionInfo.planInfoList.get(0).YPYF)) {
            if ("1".equals(transfusionInfo.SYZT) &&
                    mAppApplication.userConfig.weiZhuBenTuiZhu_YaoPinYongFa.equals(transfusionInfo.planInfoList.get(0).YPYF)) {
                //结束的输液[微注泵推注_类型]数据
                transfusionInfoListNew.add(transfusionInfo);
            }
        }
        return transfusionInfoListNew;
    }

    private List<PlanAndTransfusion> filterPlanAndTransfusionsZT4WeiZhu(Response<List<PlanAndTransfusion>> transfusionInfoListResponse) {
        List<PlanAndTransfusion> transfusionInfoListNew = new ArrayList<>();
        for (PlanAndTransfusion transfusionInfo : transfusionInfoListResponse.Data) {
//            if ("4".equals(transfusionInfo.SYZT) && "22".equals(transfusionInfo.planInfoList.get(0).YPYF)) {
            if ("4".equals(transfusionInfo.SYZT) &&
                    mAppApplication.userConfig.weiZhuBenTuiZhu_YaoPinYongFa.equals(transfusionInfo.planInfoList.get(0).YPYF)) {
                //暂停中的输液[微注泵推注_类型]数据
                transfusionInfoListNew.add(transfusionInfo);
            }
        }
        return transfusionInfoListNew;
    }

    private List<PlanAndTransfusion> filterPlanAndTransfusions4Other(Response<List<PlanAndTransfusion>> transfusionInfoListResponse) {
        List<PlanAndTransfusion> transfusionInfoListNew = new ArrayList<>();
        for (PlanAndTransfusion transfusionInfo : transfusionInfoListResponse.Data) {
//            if ("2".equals(transfusionInfo.SYZT) && !"22".equals(transfusionInfo.planInfoList.get(0).YPYF)) {
            if ("2".equals(transfusionInfo.SYZT) &&
                    !mAppApplication.userConfig.weiZhuBenTuiZhu_YaoPinYongFa.equals(transfusionInfo.planInfoList.get(0).YPYF)) {
                //执行中的输液数据
                transfusionInfoListNew.add(transfusionInfo);
            }
        }
        return transfusionInfoListNew;
    }

    private List<PlanAndTransfusion> filterPlanAndTransfusions(Response<List<PlanAndTransfusion>> transfusionInfoListResponse) {
        List<PlanAndTransfusion> transfusionInfoListNew = new ArrayList<>();
        for (PlanAndTransfusion transfusionInfo : transfusionInfoListResponse.Data) {
            if ("2".equals(transfusionInfo.SYZT)) {
                //执行中的输液数据
                transfusionInfoListNew.add(transfusionInfo);
            }
        }
        return transfusionInfoListNew;
    }

    private List<PlanAndTransfusion> filterPlanAndTransfusionsJS4Other(Response<List<PlanAndTransfusion>> transfusionInfoListResponse) {
        List<PlanAndTransfusion> transfusionInfoListNew = new ArrayList<>();
        for (PlanAndTransfusion transfusionInfo : transfusionInfoListResponse.Data) {
//            if ("1".equals(transfusionInfo.SYZT) && !"22".equals(transfusionInfo.planInfoList.get(0).YPYF)) {
            if ("1".equals(transfusionInfo.SYZT) &&
                    !mAppApplication.userConfig.weiZhuBenTuiZhu_YaoPinYongFa.equals(transfusionInfo.planInfoList.get(0).YPYF)) {
                //结束中的输液数据
                transfusionInfoListNew.add(transfusionInfo);
            }
        }
        return transfusionInfoListNew;
    }

    private List<PlanAndTransfusion> filterPlanAndTransfusionsZT4Other(Response<List<PlanAndTransfusion>> transfusionInfoListResponse) {
        List<PlanAndTransfusion> transfusionInfoListNew = new ArrayList<>();
        for (PlanAndTransfusion transfusionInfo : transfusionInfoListResponse.Data) {
//            if ("4".equals(transfusionInfo.SYZT) && !"22".equals(transfusionInfo.planInfoList.get(0).YPYF)) {
            if ("4".equals(transfusionInfo.SYZT) &&
                    !mAppApplication.userConfig.weiZhuBenTuiZhu_YaoPinYongFa.equals(transfusionInfo.planInfoList.get(0).YPYF)) {
                //暂停中的输液数据
                transfusionInfoListNew.add(transfusionInfo);
            }
        }
        return transfusionInfoListNew;
    }
  /*  private List<PlanAndTransfusion> filterPlanAndTransfusionsZT(Response<List<PlanAndTransfusion>> transfusionInfoListResponse) {
        List<PlanAndTransfusion> transfusionInfoListNew = new ArrayList<>();
        for (PlanAndTransfusion transfusionInfo : transfusionInfoListResponse.Data) {
            if ("4".equals(transfusionInfo.SYZT)) {
                //暂停中的输液数据
                transfusionInfoListNew.add(transfusionInfo);
            }
        }
        return transfusionInfoListNew;
    }*/
    /* =============================================================== end */

    /*
             升级编号【56010042】============================================= start
            医嘱执行  输液时护士有时不小心扫描时连着扫了2下，此时该输液就结束了，可否增加控制，比如扫描多少时间范围内不允许执行。
             ================= Classichu 2017/11/14 16:25

             */

    private void performExcuteInner(byte mType, String... params) {
        ExecutTask task = new ExecutTask(mType);
        tasks.add(task);
        task.execute(params);
    }

    private static long LAST_SCAN_EXCUTE_FINISH_TIME;
    private static String LAST_BARCODE;
    public static final int OFFSET_TIME_IN_MILLIS = 1000 * 30;//

    public void performExcute(final byte mType, final String... params) {
        if (ExecutTask.SCAN_EXCUTE == mType) {
            /**
             * 输液条码 快速扫描确认
             */
            String barcode = params[0];
            String prefix = params[1];
            //静配中心的输液条码 长度是14 或者 我们的输液条码开头是DS
            if (prefix.startsWith("DS") || barcode.length() == 14) {
                ////////
                long nowTime = System.currentTimeMillis();
                if (barcode.equals(LAST_BARCODE)
                        && (nowTime - LAST_SCAN_EXCUTE_FINISH_TIME) <= OFFSET_TIME_IN_MILLIS) {//30秒
                    //
                    /*MediaUtil.getInstance(mContext).playSound(R.raw.wrong,
                            mContext);*/
                    new AlertDialog.Builder(mContext)
                            .setPositiveButton(getString(R.string.project_operate_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //、、、、、、、、、继续执行
                                    performExcuteInner(mType, params);
                                }
                            })
                            .setNegativeButton(getString(R.string.project_operate_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    /*no op*/
                                }
                            })
                            //  .setTitle("")
                            .setMessage("上次执行还没超过1分钟,是否确定继续？")
                            .create().show();


                } else {
                    //、、、、、、、、、继续执行
                    performExcuteInner(mType, params);
                    ///////记录时间
                    LAST_SCAN_EXCUTE_FINISH_TIME = System.currentTimeMillis();
                    LAST_BARCODE = barcode;
                }
                /////////
            } else {
                //原有逻辑
                performExcuteInner(mType, params);
            }

        } else {
            //原有逻辑
            performExcuteInner(mType, params);
        }

    }
    /* =============================================================== end */


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 990) {
                // SY
                String sydh = data.getStringExtra("sydh");
                String qrdh = data.getStringExtra("qrdh");
                if (null != sydh && null != qrdh) {

                    performExcute(ExecutTask.TRANSFUSE_EXCUTE, sydh, qrdh);
                } else {
                    boolean vib = mAppApplication.getSettingConfig().vib;
                    /*VibratorUtil
                            .vibratorMsg(vib, "输液单号或者确认单号为空", mContext);*/
                    showMsgAndVoiceAndVibrator("输液单号或者确认单号为空");
                }
            } else if (requestCode == 991) {
                // KF
                String qrdh = data.getStringExtra("qrdh");
                if (null != qrdh) {
                    performExcute(ExecutTask.Oral_Medication_EXCUTE, qrdh);
                } else {
                    boolean vib = mAppApplication.getSettingConfig().vib;
//                    VibratorUtil.vibratorMsg(vib, "确认单号为空", mContext);
                    showMsgAndVoiceAndVibrator("确认单号为空");
                }

            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null && args.containsKey("barinfo")) {
            BarcodeEntity entity = (BarcodeEntity) args
                    .getParcelable("barinfo");
            if (entity.TMFL == 2) {
                barinfo = entity;
                // 默认扫描执行

                    /*
            升级编号【56010053】============================================= start
            多瓶超过2瓶转接瓶后提示选择接哪瓶的问题
            ================= Classichu 2017/11/14 16:25

            */
                actionDoScanExecue(entity, true);
                /* =============================================================== end */

            }
        }
    }
}
