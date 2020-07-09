package com.bsoft.mob.ienr.fragment.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.UserModelActivity;
import com.bsoft.mob.ienr.adapter.DailySecondAdapter;
import com.bsoft.mob.ienr.adapter.DailyTopAdapter;
import com.bsoft.mob.ienr.api.DailyCareApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.fragment.dialog.TryAltDialogFragment;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.listener.DialogContentClickListener;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.announce.AnnCompoundIdx;
import com.bsoft.mob.ienr.model.daily.DailySecondItem;
import com.bsoft.mob.ienr.model.daily.DailyTopItem;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;
import com.bsoft.mob.ienr.view.floatmenu.menu.IFloatMenuItem;
import com.bsoft.mob.ienr.view.floatmenu.menu.TextFloatMenuItem;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 护理常规  Created by hy on 14-3-24.
 */
public class DailyCareFragment extends BaseUserFragment {

    private TextView mTimeTxt;

    private ListView mSumListView;
    //右侧listview
    private ListView mDetailListView;
    //下面listview
    private ListView mGridView;
    private DailySecondAdapter mDetailAdapter;
    private DailySecondAdapter mGridAdapter;
    // private Button mSaveBtn;
    // private Button mResetBtn;
    private TextView mPaitentTxt;

    private DialogFragment tryFrgment;

    private ArrayList<DailyTopItem> topList = null;
    //private  ArrayList<DailySecondItem> secondList = null;

    /**
     * key是一级表单id
     */
    private HashMap<String, ArrayList<DailySecondItem>> secondMap = new HashMap<String, ArrayList<DailySecondItem>>();

    private DailyTopItem mcurTopItem;

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_daily_care;
    }

    @Override
    protected void initView(View root, Bundle savedInstanceState) {
        mTimeTxt = (TextView) root.findViewById(R.id.daily_time_txt);

        mSumListView = (ListView) root.findViewById(R.id.id_lv);

        mDetailListView = (ListView) root
                .findViewById(R.id.id_lv_2);
        mGridView = (ListView) root.findViewById(R.id.id_lv_3);

        mPaitentTxt = (TextView) root.findViewById(R.id.out_control_name_txt);

        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout,mSumListView);
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout,mDetailListView);
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout,mGridView);
        initActionbar();

        //
        String ymdHM = DateTimeHelper.getServer_yyyyMMddHHmm00();
        initTimeTxt(ymdHM);

        initListView();

        initGridView();
        initName();
        toRefreshData();
    }

    @Override
    protected void toRefreshData() {
        actionHttpTask(HttpTask.RQ_TOP);
    }

    @Override
    protected List<IFloatMenuItem> configFloatMenuItems() {
        final int[] itemDrawables = {R.drawable.menu_reset, R.drawable.menu_save};
        final int[][] itemStringDrawables = {
                {R.drawable.menu_reset, R.string.comm_menu_reset},
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
            int textResId=itemDrawableRes[1];
            String text = textResId > 0 ? getString(textResId) : null;
            IFloatMenuItem floatMenuItem = new TextFloatMenuItem(itemDrawableResid,text) {
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


        if (drawableRes == R.drawable.menu_reset) {// 重置
            resetPage();
        }else if (drawableRes == R.drawable.menu_save) {// menu_save
            saveRecord(mSumListView.getCheckedItemPosition());
        }
    }

    private void initName() {
        if (mAppApplication.sickPersonVo != null) {
            String name = mAppApplication.sickPersonVo.BRXM;
            name = String.format(getString(R.string.out_control_name), name);
            mPaitentTxt.setText(name);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initBroadCast();

    }

    private void initBroadCast() {
        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {

                String action = intent.getAction();
                if (BarcodeActions.Refresh.equals(action)) {
                    sendUserName();
                    actionBar.setPatient(mAppApplication.sickPersonVo.XSCH
                            + mAppApplication.sickPersonVo.BRXM);
                    secondMap.clear();
                    toRefreshData();
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

    /**
     * 重置页面
     */
    protected void resetPage() {

        if (mGridAdapter == null) {
            return;
        }
        int count = mGridAdapter.getCount();
        for (int position = 0; position < count; position++) {
            DailySecondItem idx = mGridAdapter.getItem(position);
            resetCheckState(idx);
        }
        mGridAdapter.clear();
        mGridAdapter = null;
        mGridView.setAdapter(mGridAdapter);

        ///
//        清除 选中
    }

    public void actionHttpTask(byte type, String... params) {
        HttpTask task = new HttpTask(type);
        task.execute(params);
        tasks.add(task);
    }


    private void saveRecord(int position) {

        if (mGridAdapter == null) {
           /* VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "请选择保存项", getActivity());*/
            showMsgAndVoiceAndVibrator("请选择保存项");
            return;
        }

        ArrayList<DailySecondItem> list = mGridAdapter.getList();
        if (list.size() < 1) {
           /* VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "请选择保存项", getActivity());*/
            showMsgAndVoiceAndVibrator("请选择保存项");
            return;
        }

        JSONArray array = new JSONArray();
        for (DailySecondItem item : list) {
            array.put(item.XMBS);
        }
        actionHttpTask(HttpTask.RQ_SAVE, array.toString(),
                String.valueOf(position));

    }

    private void initGridView() {
/*
        mGridView.setClickable(true);
        mGridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);*/
        mGridView.setOnItemClickListener(onGItemClickListener);
    }

    public OnItemClickListener onGItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if (mGridAdapter != null) {
                DailySecondItem idx = (DailySecondItem) mGridView.getAdapter()
                        .getItem(position);
                mGridAdapter.removeItem(idx);
                resetGridItem(mGridAdapter.getCount());
                resetCheckState(idx);
                //
//                去掉对应的右侧选中

            }

        }

        /**
         * 存在删除第一条item,第二条的状态为未选现象，所有重置gridview
         *
         * @param count
         */
        private void resetGridItem(int count) {

            for (int i = 0; i < count; i++) {
                //  mGridView.setItemChecked(i, true);
                mGridAdapter.setItemCheck(i, true);
            }
        }

    };

    private void resetCheckState(DailySecondItem idx) {

        if (idx == null || mcurTopItem == null) {
            return;
        }

        if (!secondMap.containsKey(idx.LBBS)) {
            return;
        }

        ArrayList<DailySecondItem> list = secondMap.get(mcurTopItem.LBBS);
        int position = list.indexOf(idx);
        if (position == -1) {

            ArrayList<DailySecondItem> sList = secondMap.get(idx.LBBS);
            if (sList == null || sList.size() < 1) {
                return;
            }
            // 往上查找一级级目录
            position = sList.indexOf(idx);
            if (position != -1) {
                resetCheckSate(idx, list);
            }
            return;
        }
        idx.checked = false;
        list.set(position, idx);

        //###  mDetailListView.setItemChecked(position, false);
        mDetailListView.setItemChecked(position, false);
        mDetailAdapter.notifyDataSetChanged();
    }

    /**
     * 在列表中，查找对应item，并将其状态改为false
     *
     * @param idx
     * @param list
     */
    private void resetCheckSate(DailySecondItem idx,
                                ArrayList<DailySecondItem> list) {

        if (idx == null || list == null) {
            return;
        }
        for (DailySecondItem item : list) {
            if (item.LBBS.equals(idx.LBBS) && item.XMBS.equals(idx.XMBS)) {
                item.checked = false;
                return;
            }
        }
    }

    public void addGridItem(AnnCompoundIdx idx) {

        if (idx == null) {
            return;
        }
        if (mGridAdapter == null) {
            ArrayList<AnnCompoundIdx> list = new ArrayList<AnnCompoundIdx>();
            list.add(idx);
            // mGridAdapter = new AnnThirdAdapter(application, list);
            mGridView.setAdapter(mGridAdapter);
            //mGridView.setItemChecked(0, true);
            mGridAdapter.setItemCheck(0, true);
            return;
        }
        // int position = mGridAdapter.addItem(idx);
        // if (position == -1) {
        // boolean vib = application.getSettingConfig().vib;
        // VibratorUtil.vibratorMsg(vib, "增加失败", getActivity());
        // return;
        // }
        // mGridView.setItemChecked(position, true);

    }

    public void removeGridItem(AnnCompoundIdx idx) {

        if (idx == null || mGridAdapter == null) {
            return;
        }
        // boolean ok = mGridAdapter.removeItem(idx);
        // if (!ok) {
        // boolean vib = application.getSettingConfig().vib;
        // VibratorUtil.vibratorMsg(vib, "删除失败", getActivity());
        // }
    }

    private void initListView() {

        mSumListView.setOnItemClickListener(onSumItemClickListener);
        mSumListView.setTextFilterEnabled(true);
        // checked/activated
        mSumListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

 /*       mDetailListView.setClickable(true);
        mDetailListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);*/
        mDetailListView.setOnItemClickListener(onDItemSelectedListener);
    }

    public OnItemClickListener onSumItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            /*
            升级编号【56010046】============================================= start
			护理常规勾选后（切换后取消勾选）点击其它的护理分类，右边护理明细没有刷新
			================= Classichu 2017/10/13 14:56
			*/
            //if (mGridAdapter != null) {
            if (mGridAdapter != null && mGridAdapter.getList().size() > 0) {
                alertSave(position);
            } else {
                onSumItemClick(position);
            }
            /* =============================================================== end */
        }

    };

    private void alertSave(int position) {
        if (mGridAdapter == null) {
            return;
        }
        ArrayList<DailySecondItem> list = mGridAdapter.getList();
        if (list.size() < 1) {
            return;
        }

        JSONArray array = new JSONArray();
        for (DailySecondItem item : list) {
            array.put(item.XMBS);
        }
        showTryDialog(HttpTask.RQ_SAVE, false, array.toString(),
                String.valueOf(position));
    }

    private void updateSecondList(DailyTopItem item) {

        if (item == null) {
            return;
        }
        mcurTopItem = item;

        if (!secondMap.containsKey(item.LBBS)) {
            actionHttpTask(HttpTask.RQ_SECOND);
            return;
        }
        setDetailListView(secondMap.get(item.LBBS));
    }

    /**
     * 初始化二级列表项
     *
     * @param items
     */
    private void setDetailListView(ArrayList<DailySecondItem> items) {

        if (mcurTopItem != null) {
            mDetailAdapter = new DailySecondAdapter(
                    getActivity(), items);
            mDetailListView.setAdapter(mDetailAdapter);
            importCheckState(items);
        }
    }

    private void importCheckState(ArrayList<DailySecondItem> items) {

        if (items == null) {
            return;
        }
        int i = 0;
        for (DailySecondItem idx : items) {
            mDetailAdapter.setItemCheck(i, idx.checked);
            //## mDetailListView.setItemChecked(i, idx.checked);
            i++;
        }
    }

    public OnItemClickListener onDItemSelectedListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            DailySecondItem idx = (DailySecondItem) mDetailListView.getAdapter().getItem(position);
            idx.checked = !idx.checked;
            //##  mDetailListView.setItemChecked(position, idx.checked);
            mDetailAdapter.setItemCheck(position, idx.checked);
            if (idx.checked) {
                addGridItem(idx);
            } else {
                removeGridItem(idx);
            }
        }
    };

    public void removeGridItem(DailySecondItem idx) {

        if (idx == null || mGridAdapter == null) {
            return;
        }
        boolean ok = mGridAdapter.removeItem(idx);
        if (!ok) {
            boolean vib = mAppApplication.getSettingConfig().vib;
//            VibratorUtil.vibratorMsg(vib, "删除失败", getActivity());
            showMsgAndVoiceAndVibrator("删除失败");
        }
    }

    public void addGridItem(DailySecondItem idx) {

        if (idx == null) {
            return;
        }
        if (mGridAdapter == null) {
            ArrayList<DailySecondItem> list = new ArrayList<DailySecondItem>();
            list.add(idx);
            mGridAdapter = new DailySecondAdapter(mAppApplication, list);
            mGridView.setAdapter(mGridAdapter);
            //mGridView.setItemChecked(0, true);
            mGridAdapter.setItemCheck(0, true);
            return;
        }
        int position = mGridAdapter.addItem(idx);
        if (position == -1) {
            boolean vib = mAppApplication.getSettingConfig().vib;
//            VibratorUtil.vibratorMsg(vib, "增加失败", getActivity());
            showMsgAndVoiceAndVibrator("增加失败");
            return;
        }
        // mGridView.setItemChecked(position, true);
        mGridAdapter.setItemCheck(position, true);

    }

    private void initTimeTxt(String sTime) {
        mTimeTxt.setText(sTime);
        // mTimeTxt.setOnClickListener(onTimeClick);
    }

    // private OnClickListener onTimeClick = new OnClickListener() {
    //
    // @Override
    // public void onClick(View v) {
    //
    // String dateStr = mTimeTxt.getText().toString();
    // Date date;
    // try {
    // date = DateUtil.format6.parse(dateStr);
    // Calendar calendar = Calendar.getInstance();
    // calendar.setTime(date);
    // showPickerDateCompat(calendar.get(Calendar.YEAR),
    // calendar.get(Calendar.MONTH),
    // calendar.get(Calendar.DAY_OF_MONTH), mTimeTxt.getId());
    // } catch (ParseException e) {
    // e.printStackTrace();
    // }
    //
    // }
    // };

    /**
     * 设置title view
     */
    private void initActionbar() {

        actionBar.setTitle("护理常规");
        actionBar.setPatient(mAppApplication.sickPersonVo.XSCH
                + mAppApplication.sickPersonVo.BRXM);
        actionBar.addAction(new Action() {

            @Override
            public void performAction(View view) {
                saveRecord(mSumListView.getCheckedItemPosition());
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

    /**
     * 获取护理列表
     *
     * @author hy
     */
    class HttpTask extends AsyncTask<String, String, Response> {

        public static final byte RQ_TOP = 0;

        public static final byte RQ_SECOND = 1;

        public static final byte RQ_SAVE = 2;

        private byte mType;

        // 一级类别标识
        private String LBBS;

        private String[] param;

        public HttpTask(byte mType) {
            this.mType = mType;
        }

        @Override
        protected void onPreExecute() {
            switch (mType) {
                case RQ_TOP:
                    showSwipeRefreshLayout();
                    break;
                case RQ_SECOND:
                    showSwipeRefreshLayout();
                    break;
                case RQ_SAVE:
                    showLoadingDialog(getResources().getString(R.string.saveing));
                    break;
                default:
            }
        }

        @Override
        protected Response doInBackground(String... params) {

            DailyCareApi api = DailyCareApi.getInstance(mAppApplication);

            String jgid = mAppApplication.jgId;
            int sysType = Constant.sysType;

            switch (mType) {
                case RQ_TOP:
                    return api.GetDailyNurseType(mAppApplication.getAreaId(), jgid,
                            sysType);
                case RQ_SECOND:
                    if (mcurTopItem == null) {
                        return null;
                    }
                    LBBS = mcurTopItem.LBBS;
                    return api.GetDailyNurseList(mcurTopItem.LBBS, jgid, sysType);
                case RQ_SAVE:

                    if (params == null || params.length < 2 || params[0] == null
                            || params[1] == null) {
                        return null;
                    }
                    if (mAppApplication.sickPersonVo == null
                            || mAppApplication.user == null) {
                        return null;
                    }
                    LBBS = mcurTopItem.LBBS;
                    String zyh = mAppApplication.sickPersonVo.ZYH;
                    param = params;
                    String urid = mAppApplication.user.YHID;
                    String brbq = mAppApplication.getAreaId();
                    return api.SaveDailyNurseItems(brbq, zyh, param[0], urid, jgid,
                            sysType);
                default:
            }
            return null;

        }

        @Override
        @SuppressWarnings("unchecked")
        protected void onPostExecute(Response result) {

            switch (mType) {
                case RQ_TOP:
                    hideSwipeRefreshLayout();
                    break;
                case RQ_SECOND:
                    hideSwipeRefreshLayout();
                    break;
                case RQ_SAVE:
                    hideLoadingDialog();
                    break;
                default:
            }
            tasks.remove(this);

            if (null == result) {
                showMsgAndVoiceAndVibrator("加载失败");
                return;
            }
            if (result.ReType == 100) {
                new AgainLoginUtil(getActivity(), mAppApplication).showLoginDialog();
                return;
            } else if (result.ReType == 0) {
                if (mType == RQ_TOP) {
                    topList = (ArrayList<DailyTopItem>) result.Data;
                    setTopAdapter(topList);
                } else if (mType == RQ_SECOND) {
                    ArrayList<DailySecondItem> secondList = (ArrayList<DailySecondItem>) result
                            .Data;
                    setSecondAdapter(LBBS, secondList);
                } else if (mType == RQ_SAVE) {
                    resetPage();
                    showMsgAndVoice(R.string.project_save_success);
                    onSumItemClick(Integer.valueOf(param[1]));
                }
            } else {
                showMsgAndVoice(result.Msg);
            }

        }
    }

    private void setSecondAdapter(String lBBS2, ArrayList<DailySecondItem> items) {

        if (items != null) {
            for (DailySecondItem item : items) {
                item.LBBS = lBBS2;
            }
        }
        mDetailAdapter = new DailySecondAdapter(
                getActivity(), items);
        secondMap.put(lBBS2, items);
        mDetailListView.setAdapter(mDetailAdapter);

    }

    public void onSumItemClick(int position) {
        mSumListView.setItemChecked(position, true);
        DailyTopItem item = (DailyTopItem) mSumListView.getAdapter().getItem(
                position);
        if (item != null) {
            updateSecondList(item);
        }
    }

    public void clearSate(String lBBS) {

        if (secondMap.containsKey(lBBS)) {
            ArrayList<DailySecondItem> list = secondMap.get(lBBS);
            if (list == null) {
                return;
            }
            for (DailySecondItem item : list) {
                item.checked = false;
            }

        }
    }

    public void setTopAdapter(ArrayList<DailyTopItem> items) {

        final DailyTopAdapter adapter = new DailyTopAdapter(getActivity(),
                items);
        mSumListView.setAdapter(adapter);

    }


    protected void showTryDialog(final byte type, boolean tryAgain, final String... params) {


        tryFrgment = TryAltDialogFragment.newInstance(tryAgain, new DialogContentClickListener() {
            @Override
            public void contentClick() {
                actionHttpTask(type, params);
            }

            @Override
            public void cancelClick() {
                //继续原来选中左侧的逻辑
                int pos = 0;
                if (params != null && params.length > 1) {
                    pos = Integer.valueOf(params[1]);
                }
                onSumItemClick(pos);
            }
        });
        tryFrgment.show(getActivity().getSupportFragmentManager(),
                "TryAltDialogFragment");
    }

}
