package com.bsoft.mob.ienr.fragment.user;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.SignActivity;
import com.bsoft.mob.ienr.activity.user.AdviceListActivity;
import com.bsoft.mob.ienr.activity.user.BloodCheckActivity;
import com.bsoft.mob.ienr.activity.user.UserModelActivity;
import com.bsoft.mob.ienr.adapter.BTAdapter;
import com.bsoft.mob.ienr.api.BloodTransfusionApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.helper.ContextCompatHelper;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.blood.BloodTransfusionInfo;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;
import com.bsoft.mob.ienr.view.floatmenu.menu.IFloatMenuItem;
import com.bsoft.mob.ienr.view.floatmenu.menu.TextFloatMenuItem;
import com.classichu.dialogview.helper.DialogFragmentShowHelper;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 输血医嘱  Created by hy on 14-3-24.
 */
public class TransfusionBloodFragment extends BaseUserFragment {

    private RadioGroup mRadioGroup;

    private CheckBox mCheckBox;


    private ListView mListView;

    private TextView stime, etime;

    private View sltStimeView;
    private View sltEtimeView;

    private ImageView searchBtn;

    private ArrayList<BloodTransfusionInfo> mList = null;

    private BTAdapter mAdapter;

    protected static final int RQ_CHECK = 0;

    protected static final int RQ_GET_USERID = 1;

    private boolean mIsBloodChecked;

    private boolean isSignStatus;

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_transfusion_blood;
    }

    @Override
    protected void initView(View root, Bundle savedInstanceState) {
        mRadioGroup = (RadioGroup) root.findViewById(R.id.id_rg);
        mRadioGroup.setVisibility(View.GONE);
        mCheckBox = (CheckBox) root.findViewById(R.id.image);
        Drawable btnDrawable = ContextCompatHelper.getDrawable(mContext, R.drawable.selector_classic_icon_up_down);
        mCheckBox.setButtonDrawable(btnDrawable);
        stime = (TextView) root.findViewById(R.id.stime);
        etime = (TextView) root.findViewById(R.id.etime);
        TextView stimeTitle = (TextView) root.findViewById(R.id.stime_title);
        TextView etimeTitle = (TextView) root.findViewById(R.id.etime_title);
        stimeTitle.setText(R.string.start_time);
        etimeTitle.setText(R.string.end_time);


        RadioButton id_rb = (RadioButton) root.findViewById(R.id.id_rb);
        RadioButton id_rb_2 = (RadioButton) root.findViewById(R.id.id_rb_2);
        RadioButton id_rb_3 = (RadioButton) root.findViewById(R.id.id_rb_3);
        RadioButton id_rb_4 = (RadioButton) root.findViewById(R.id.id_rb_4);
        id_rb.setText(R.string.transfusion_blood_state_all);
        id_rb_2.setText(R.string.transfusion_blood_state_did);
        id_rb_3.setText(R.string.transfusion_blood_state_doing);
        id_rb_4.setText(R.string.transfusion_blood_state_todo);

        sltStimeView = root.findViewById(R.id.slt_stime_ly);
        sltEtimeView = root.findViewById(R.id.slt_etime_ly);

        searchBtn = (ImageView) root.findViewById(R.id.search);

        mListView = (ListView) root
                .findViewById(R.id.id_lv);

        EmptyViewHelper.setEmptyView(mListView, "mListView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mListView);


        initActionBar();
        initDateView();
        initRadioGroup();
        initCheckBox();
        initListView();
        initSearchBtn();

        String ymdHM = DateTimeHelper.getServer_yyyyMMddHHmm00();
        initTimeTxt(ymdHM, R.id.time);

        initBroadCast();

        toRefreshData();

    }

    private void initBroadCast() {

        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (isSignStatus){
                    return;
                }

                if (mIsBloodChecked) {
                    return;
                }
                String action = intent.getAction();
                if (BarcodeActions.Refresh.equals(action)) {
                    sendUserName();
                    actionBar.setPatient(mAppApplication.sickPersonVo.BRCH
                            + mAppApplication.sickPersonVo.BRXM);
                    mAdapter = null;
                    mListView.setAdapter(mAdapter);
                    toRefreshData();
                } else if (BarcodeActions.Bar_Get.equals(action)) {

                    BarcodeEntity entity = (BarcodeEntity) intent
                            .getParcelableExtra("barinfo");
                    if (FastSwitchUtils.needFastSwitch(entity)) {
                        FastSwitchUtils.fastSwith(
                                (UserModelActivity) getActivity(), entity);
                    } else if (entity.TMFL == 9) {// 标本条码
                        setItemCheck(entity.TMQZ + entity.TMNR);
                    }

                }
            }
        };
    }

    // 扫描一项选中一项
    private void setItemCheck(String tmnr) {
        BloodTransfusionInfo entity = null;
        for (int i = 0; i < mList.size(); i++) {
            entity = mList.get(i);
            if (mList.get(i).XDH.equals(tmnr)) {
                mAdapter.checkedPostion = i;
                mAdapter.notifyDataSetChanged();
                break;
            }
        }

        if (entity != null) {
            Intent intent = new Intent(getActivity(),
                    BloodCheckActivity.class);
            intent.putExtra("entity", entity);
            mIsBloodChecked = true;
            startActivityForResult(intent, RQ_CHECK);
        }
    }


    @Override
    protected List<IFloatMenuItem> configFloatMenuItems() {

        final int[] itemDrawables = {R.drawable.menu_more,
                R.drawable.menu_fresh, R.drawable.menu_save};
        final int[][] itemStringDrawables = {
                {R.drawable.menu_more, R.string.comm_menu_more},
                {R.drawable.menu_fresh, R.string.comm_menu_refresh},
                {R.drawable.menu_save, R.string.comm_menu_save}};
        List<IFloatMenuItem> floatMenuItemList = new ArrayList<>();
      /*  for (int itemDrawableResid : itemDrawables) {
            FloatMenuItem floatMenuItem = new FloatMenuItem(itemDrawableResid) {
                @Override
                public void actionClick(View view, int resid) {
                    onMenuItemClick(resid);
                }
            };
            floatMenuItemList.add(floatMenuItem);
        }*/
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

    /**
     * 响应RayMenu item点击
     */
    private void onMenuItemClick(int drawableRes) {

        if (drawableRes == R.drawable.menu_more) {// 更多

            showAdviceDialog(AdviceDialogFragment.SHOW_MORE_MENU);
        } else if (drawableRes == R.drawable.menu_fresh) {// refresh
            toRefreshData();
        } else if (drawableRes == R.drawable.menu_save) {// menu_save
            doAdvice();
        }
    }

    private void initSearchBtn() {

        searchBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                toRefreshData();
            }
        });
    }

    protected void actionHttpTask(byte type, String... params) {

        HttpTask task = new HttpTask(type);
        tasks.add(task);
        task.execute(params); 
    }


    @Override
    protected void toRefreshData() {
        actionHttpTask(HttpTask.GET_DATE);
    }

    private void initDateView() {

        String nowDate = DateTimeHelper.getServerDate();
        // 当天
        String eTimeStr = nowDate;
        etime.setText(eTimeStr);

        // 前天
        String startDate = DateTimeHelper.dateAddedDays(nowDate, -7);
        String sTimeStr = startDate;
        stime.setText(sTimeStr);

        sltStimeView.setOnClickListener(onClickListener);
        sltEtimeView.setOnClickListener(onClickListener);
    }

    public View.OnClickListener onClickListener = new OnClickListener() {

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

    private void initActionBar() {

        actionBar.setTitle("输血医嘱执行");
        actionBar.setPatient(mAppApplication.sickPersonVo.BRCH
                + mAppApplication.sickPersonVo.BRXM);
        actionBar.addAction(new Action() {

            @Override
            public void performAction(View view) {

                doAdvice();

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

    private void doAdvice() {
        // 核对
        if (mAdapter == null || mAdapter.checkedPostion == -1) {
            showMsgAndVoiceAndVibrator("请求失败：请先选择输血项");
            return;
        }
        BloodTransfusionInfo entity = mAdapter.getItem(mAdapter.checkedPostion);

        Intent intent = new Intent(getActivity(),
                BloodCheckActivity.class);
        intent.putExtra("entity", entity);
        mIsBloodChecked = true;
        startActivityForResult(intent, RQ_CHECK);
    }

    private void initListView() {
        // checked/activated
        mListView.setChoiceMode(
                AbsListView.CHOICE_MODE_SINGLE);


    }

    private void initTimeTxt(String ymdHM, int viewId) {
        if (viewId == R.id.slt_etime_ly) {
            etime.setText(ymdHM);
        } else if (viewId == R.id.slt_stime_ly) {
            stime.setText(ymdHM);
        }

    }

    private void initCheckBox() {

        mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                mRadioGroup.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * 响应过滤
     */
    private void initRadioGroup() {

        mRadioGroup
                .setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        if (mList == null) {
                            showMsgAndVoice("暂无输血");
                            return;
                        }
                        filterAndSetList(checkedId);
                    }
                });
    }

    protected void filterAndSetList(int checkedId) {

        if (mList == null) {
            showMsgAndVoice("暂无输血");
            return;
        }

        ArrayList<BloodTransfusionInfo> result = new ArrayList<BloodTransfusionInfo>();
        if (checkedId == R.id.id_rb_4) {

            for (BloodTransfusionInfo entity : mList) {
                if (EmptyTool.isBlank(entity.SXR1)
                        && EmptyTool.isBlank(entity.SXR2)) {
                    result.add(entity);
                }
            }

        } else if (checkedId == R.id.id_rb_2) {

            for (BloodTransfusionInfo entity : mList) {

                if (!EmptyTool.isBlank(entity.JSR)) {
                    result.add(entity);
                }
            }

        } else if (checkedId == R.id.id_rb_3) {

            for (BloodTransfusionInfo entity : mList) {
                boolean sxrExisted = !EmptyTool.isBlank(entity.SXR1)
                        || !EmptyTool.isBlank(entity.SXR2);
                if (sxrExisted && EmptyTool.isBlank(entity.JSR)) {
                    result.add(entity);
                }
            }
        } else {
            result = mList;
        }

        if (result.size() == 0) {
            mAdapter = null;
        } else {
            mAdapter = new BTAdapter(getActivity(), result);
        }
        mListView.setAdapter(mAdapter);

    }

    protected void showAdviceDialog(byte type) {

        AdviceDialogFragment newFragment = AdviceDialogFragment.newInstance(type, new AdviceDialogFragment.ClickListener() {
            @Override
            public void click() {

            }
        }, new WeakReference<Context>(mContext));
     /*   try {
            getFragmentManager().beginTransaction()
                    .add(newFragment, "AdviceDialogFragment")
                    .commitAllowingStateLoss();
        } catch (Exception ex) {
            Log.e(Constant.TAG, ex.getMessage(), ex);
        }*/
        DialogFragmentShowHelper.show(getChildFragmentManager(), newFragment, "AdviceDialogFragment");
    }

    /**
     * @param
     * @author hy
     */
    public static class AdviceDialogFragment extends DialogFragment {

        /**
         * 显示更多menus
         */
        public static final byte SHOW_MORE_MENU = 0;

        private static byte mType;
        private static WeakReference<Context> sContextWeakReference;
        private static ClickListener mClickListener;

        public interface ClickListener {
            void click();
        }

        public static AdviceDialogFragment newInstance(byte type, ClickListener clickListener, WeakReference<Context> weakReference) {
            AdviceDialogFragment fragment = new AdviceDialogFragment();
            Bundle bundle = new Bundle();
            fragment.setArguments(bundle);

            mType = type;
            mClickListener = clickListener;
            sContextWeakReference = weakReference;
            return fragment;
        }


        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            switch (mType) {
                case SHOW_MORE_MENU:

                    String[] arr_more = getResources().getStringArray(
                            R.array.advice_blood_menu_array);
                    String title = "更多操作";
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            getActivity());
                    builder.setCustomTitle(ViewBuildHelper.buildDialogTitleTextView(sContextWeakReference.get(), title));

                    builder.setItems(arr_more,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int item) {
                                    if (item == 0) {
                                        Intent intent2 = new Intent(getActivity(),
                                                AdviceListActivity.class);
                                        startActivity(intent2);
                                    }
                                }
                            });
                    return builder.create();
                default:
            }
            return super.onCreateDialog(savedInstanceState);
        }
    }

    class HttpTask extends AsyncTask<String, Integer, Response<List<BloodTransfusionInfo>>> {

        /**
         * 获取列表
         */
        public static final byte GET_DATE = 0;

        /**
         * 保存数据
         */
        public static final byte SAVE_DATE = GET_DATE + 1;

        private byte mType;

        String hdgh;
        String zxgh;

        public HttpTask(byte mType) {
            this.mType = mType;
        }

        @Override
        protected void onPreExecute() {

            showSwipeRefreshLayout();
        }

        @Override
        protected Response<List<BloodTransfusionInfo>> doInBackground(String... params) {

            BloodTransfusionApi api = BloodTransfusionApi
                    .getInstance(getActivity());

            switch (mType) {
                case GET_DATE:
                    if (mAppApplication.sickPersonVo == null) {
                        return null;
                    }
                    String sTimeStr = stime.getText().toString();
                    String eTimeStr = etime.getText().toString();
                    String jgid = mAppApplication.jgId;
                    String zyh = mAppApplication.sickPersonVo.ZYH;
                    return api.GetBloodTransfusionList(sTimeStr, eTimeStr, zyh, jgid);

                case SAVE_DATE:
                    if (params == null || params.length < 2) {
                        return null;
                    }
                    if (mAppApplication.sickPersonVo == null) {
                        return null;
                    }
                    if (mAdapter == null || mAdapter.checkedPostion == -1) {
                        return null;
                    }

                    zyh = mAppApplication.sickPersonVo.ZYH;
                    hdgh = params[0];
                    zxgh = params[1];

                    BloodTransfusionInfo entity = mAdapter.getItem(mAdapter.checkedPostion);
                    String xdh = entity.XDH;
                    String xdxh = entity.XDXH;
//                    xdh = xdh + xdxh;
                    int operationType = 0;
                    if (!EmptyTool.isBlank(entity.SXR1)
                            || !EmptyTool.isBlank(entity.SXR2)) {
                        operationType = 3;
                    }

                    jgid = mAppApplication.jgId;
                    return api.ExcueteBloodTransfusion(xdh, xdxh, zyh, hdgh, zxgh,
                            operationType, jgid);
                default:
            }
            return null;
        }

        @Override
        protected void onPostExecute(Response<List<BloodTransfusionInfo>> result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication, new AgainLoginUtil.LoginSucessListener() {
                        @Override
                        public void LoginSucessEvent() {
                            if (mType == GET_DATE) {

                                toRefreshData();
                            } else if (mType == SAVE_DATE) {
                                actionHttpTask(HttpTask.SAVE_DATE, hdgh, zxgh);
                            }
                        }
                    }).showLoginDialog();
                    return;
                } else if (result.ReType == 0) {
                    switch (mType) {
                        case GET_DATE:
                            @SuppressWarnings("unchecked")
                            ArrayList<BloodTransfusionInfo> list = (ArrayList<BloodTransfusionInfo>) result.Data;
                            mList = list;
                            filterAndSetList(mRadioGroup.getCheckedRadioButtonId());
                            break;
                        case SAVE_DATE:
                            @SuppressWarnings("unchecked")
                            ArrayList<BloodTransfusionInfo> btList = (ArrayList<BloodTransfusionInfo>) result.Data;
                            if (btList != null && btList.size() > 0) {
                                BloodTransfusionInfo btresult = btList.get(0);
                                BloodTransfusionInfo entity = mAdapter.getItem(mAdapter.checkedPostion);
                                int operationType = 0;
                                if (!EmptyTool.isBlank(entity.SXR1)
                                        || !EmptyTool.isBlank(entity.SXR2)) {
                                    operationType = 3;
                                }
                                if (operationType == 0) {
                                    entity.SXR1 = btresult.SXR1;
                                    entity.SXR2 = btresult.SXR2;
                                    entity.KSSJ = btresult.KSSJ;
                                } else if (operationType == 3) {
                                    entity.JSR = btresult.JSR;
                                    entity.JSSJ = btresult.JSSJ;
                                }
                                mAdapter.notifyDataSetChanged();
                                showMsgAndVoice("请求成功");
                            }
                            break;
                        default:
                    }

                } else {
                    showMsgAndVoice(result.Msg);
                 /*   MediaUtil.getInstance(getActivity()).playSound(
                            R.raw.wrong, getActivity());*/
                }
            } else {
                showMsgAndVoiceAndVibrator("请求失败：异步请求参数错误");
                return;
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == RQ_GET_USERID) {
            isSignStatus = false;
            if (data == null) {
                return;
            }

            String yhid1 = data.getStringExtra(SignActivity.EXTRA_YHID_KEY_1);
            String yhid2 = data.getStringExtra(SignActivity.EXTRA_YHID_KEY_2);
            actionHttpTask(HttpTask.SAVE_DATE, yhid2, yhid1);
            return;
        }

        if (requestCode == RQ_CHECK) {
            mIsBloodChecked = false;
            isSignStatus = true;
            sign();
            return;
        }

    }

    public void sign() {
        Intent intent = new Intent(getActivity(), SignActivity.class);
        intent.putExtra(SignActivity.ACTION_SIGN,
                SignActivity.ACTION_EXTRA_SING_BOUSE);
        startActivityForResult(intent, RQ_GET_USERID);
    }

}
