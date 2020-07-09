package com.bsoft.mob.ienr.fragment.user;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.bsoft.mob.ienr.Constant;
import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.user.AnnounceHistoryActivity;
import com.bsoft.mob.ienr.activity.user.UserModelActivity;
import com.bsoft.mob.ienr.adapter.AnnSecondAdapter;
import com.bsoft.mob.ienr.adapter.AnnThirdAdapter;
import com.bsoft.mob.ienr.adapter.AnnTopAdapter;
import com.bsoft.mob.ienr.api.AnnounceApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.datetime.DateTimeFactory;
import com.bsoft.mob.ienr.components.datetime.DateTimeHelper;
import com.bsoft.mob.ienr.fragment.base.BaseUserFragment;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.model.ParserModel;
import com.bsoft.mob.ienr.model.announce.AnnnouceSecondIdx;
import com.bsoft.mob.ienr.model.announce.AnnnouceThirdIdx;
import com.bsoft.mob.ienr.model.announce.AnnnouceTopIdx;
import com.bsoft.mob.ienr.util.DisplayUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.view.BsoftActionBar.Action;
import com.bsoft.mob.ienr.view.expand.SpinnerLayout;
import com.bsoft.mob.ienr.view.floatmenu.menu.IFloatMenuItem;
import com.bsoft.mob.ienr.view.floatmenu.menu.TextFloatMenuItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 病人宣教  Created by hy on 14-3-21.
 */
public class AnnounceFragment extends BaseUserFragment {
    private TextView mTimeTxt;
    private PopupWindow mPopupWindow;
    private Spinner mSpinner;
    private ListView mSumListView;
    private ListView mDetailListView;
    private ListView mGridView;
    private AnnThirdAdapter mGridAdapter;

    private ArrayList<AnnnouceTopIdx> topList = null;
    private ArrayList<AnnnouceSecondIdx> secondList = null;
    private ArrayList<AnnnouceThirdIdx> thirdList = null;

    // 缓存级联列表
    // private ArrayList<AnnnouceTopIdx> topIndx = new
    // ArrayList<AnnnouceTopIdx>();
    /**
     * key 是一级表单id
     */
    private HashMap<String, ArrayList<AnnnouceSecondIdx>> secondIndx = new HashMap<String, ArrayList<AnnnouceSecondIdx>>();
    /**
     * key是二级表单id
     */
    private HashMap<AnnnouceSecondIdx, ArrayList<AnnnouceThirdIdx>> thridIndx = new HashMap<AnnnouceSecondIdx, ArrayList<AnnnouceThirdIdx>>();
    private AnnnouceTopIdx mcurTopIndx;
    private AnnnouceSecondIdx mcurSecondIndx;
    private View timeView;
    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_announce;
    }

    @Override
    protected void initView(View root, Bundle savedInstanceState) {
        mTimeTxt = (TextView) root.findViewById(R.id.ann_time_txt);
        SpinnerLayout spinnerLayout = (SpinnerLayout) root.findViewById(R.id.id_spinner_layout);
        mSpinner = spinnerLayout.getSpinner();
        timeView = root.findViewById(R.id.slt_stime_ly);
        mSumListView = (ListView) root.findViewById(R.id.id_lv);
        mDetailListView = (ListView) root
                .findViewById(R.id.id_lv_2);
        mGridView = (ListView) root.findViewById(R.id.id_lv_3);
        initActionbar();
        initPopupWindow();
        initSpinner();
        initListView();
        initGridView();
        initBroadCast();
        //
        String ymdHM = DateTimeHelper.getServer_yyyyMMddHHmm00();
        initTimeTxt(ymdHM);

        toRefreshData();
    }

    @Override
    protected List<IFloatMenuItem> configFloatMenuItems() {
        final int[] itemDrawables = {R.drawable.menu_view,
                R.drawable.menu_reset, R.drawable.menu_fresh};
        final int[][] itemStringDrawables = {
                {R.drawable.menu_view, R.string.comm_menu_view},
                {R.drawable.menu_reset, R.string.comm_menu_reset},
                {R.drawable.menu_fresh, R.string.comm_menu_refresh}};
        List<IFloatMenuItem> floatMenuItemList = new ArrayList<>();
     /*   for (int itemDrawableResid : itemDrawables) {
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

        if (drawableRes == R.drawable.menu_fresh) {// 刷新
            toRefreshData();
        } else if (drawableRes == R.drawable.menu_reset) {// 重置
            resetPage();
        } else if (drawableRes == R.drawable.menu_view) {// 历史
            toAty_History();
        }

    }

    private void toAty_History() {
        // 启动宣教查询页
        Intent intent = new Intent(getActivity(),
                AnnounceHistoryActivity.class);
        startActivity(intent);
    }


    @Override
    protected void toRefreshData() {
        GetDateTask task = new GetDateTask();
        task.execute();
        tasks.add(task);
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
            AnnnouceThirdIdx idx = mGridAdapter.getItem(position);
            resetCheckState(idx);
        }
        mGridAdapter.clear();

    }

    private void saveRecord() {

        if (mGridAdapter == null) {
            /*VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "请选择保存项", getActivity());*/
            showMsgAndVoiceAndVibrator("请选择保存项");
            return;
        }

        ArrayList<AnnnouceThirdIdx> list = mGridAdapter.getList();
        if (list.size() < 1) {
           /* VibratorUtil.vibratorMsg(mAppApplication.getSettingConfig().vib,
                    "请选择保存项", getActivity());*/
            showMsgAndVoiceAndVibrator("请选择保存项");
            return;
        }
        performSave(list);
    }

    private void performSave(ArrayList<AnnnouceThirdIdx> list) {

        if (list == null) {
            return;
        }

        try {
            String params = JsonUtil.toJson(list);
            SaveDateTask task = new SaveDateTask();
            tasks.add(task);
            task.execute(params);
        } catch (IOException e) {
            Log.e(Constant.TAG, e.getMessage(), e);
        }

    }

    private void initGridView() {

        mGridView.setClickable(true);
        mGridView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        mGridView.setOnItemClickListener(onGItemClickListener);
    }

    public OnItemClickListener onGItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            if (mGridAdapter != null) {
                AnnnouceThirdIdx idx = (AnnnouceThirdIdx) mGridView
                        .getAdapter().getItem(position);
                mGridAdapter.removeItem(idx);
                resetGridItem(mGridAdapter.getCount());
                resetCheckState(idx);
            }

        }

        /**
         * 存在删除第一条item,第二条的状态为未选现象，所有重置gridview
         *
         * @param count
         */
        private void resetGridItem(int count) {

            for (int i = 0; i < count; i++) {
                mGridView.setItemChecked(i, true);
            }
        }

    };

    private void resetCheckState(AnnnouceThirdIdx idx) {

        if (idx == null || mcurTopIndx == null || mcurSecondIndx == null) {
            return;
        }

        ArrayList<AnnnouceThirdIdx> list = thridIndx.get(mcurSecondIndx);
        int position = list.indexOf(idx);
        // 没在当前三级显示列
        if (position == -1) {

            ArrayList<AnnnouceSecondIdx> sList = secondIndx.get(idx.YSBS);

            AnnnouceSecondIdx item = new AnnnouceSecondIdx();
            item.YSBS = idx.YSBS;
            item.LBBS = idx.LBBS;
            // 往上查找二级目录
            position = sList.indexOf(item);
            if (position != -1) {
                // 获取二级项
                item = sList.get(position);
                // 获取三级列表
                list = thridIndx.get(item);
                resetCheckSate(idx, list);
            }
            return;
        }
        idx.checked = false;
        list.set(position, idx);

        String YSBS = mcurTopIndx.YSBS;
        String LBBS = mcurSecondIndx.LBBS;
        // 当前显示，则更新状态
        if (YSBS.equals(idx.YSBS) && LBBS.equals(idx.LBBS)) {
            mDetailListView.setItemChecked(position, false);
        }

    }

    /**
     * 在列表中，查找对应item，并将其状态改为false
     *
     * @param idx
     * @param list
     */
    private void resetCheckSate(AnnnouceThirdIdx idx,
                                ArrayList<AnnnouceThirdIdx> list) {

        if (idx == null || list == null) {
            return;
        }
        for (AnnnouceThirdIdx item : list) {
            if (item.YSBS.equals(idx.YSBS) && item.LBBS.equals(idx.LBBS)
                    && item.XMBS.equals(idx.XMBS)) {
                item.checked = false;
                return;
            }
        }
    }

    public void addGridItem(AnnnouceThirdIdx idx) {

        if (idx == null) {
            return;
        }
        if (mGridAdapter == null) {
            ArrayList<AnnnouceThirdIdx> list = new ArrayList<AnnnouceThirdIdx>();
            list.add(idx);
            mGridAdapter = new AnnThirdAdapter(mAppApplication, list);
            mGridView.setAdapter(mGridAdapter);
            mGridView.setItemChecked(0, true);
            return;
        }
        int position = mGridAdapter.addItem(idx);
        if (position == -1) {
            boolean vib = mAppApplication.getSettingConfig().vib;
//            VibratorUtil.vibratorMsg(vib, "增加失败", getActivity());
            showMsgAndVoiceAndVibrator("增加失败");
            return;
        }
        mGridView.setItemChecked(position, true);

    }

    public void removeGridItem(AnnnouceThirdIdx idx) {

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

    private void initListView() {

        mSumListView.setOnItemClickListener(onSumItemClickListener);
        mSumListView.setTextFilterEnabled(true);
        // checked/activated
        mSumListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        mDetailListView.setClickable(true);
        mDetailListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        mDetailListView.setOnItemClickListener(onDItemSelectedListener);
    }

    public OnItemClickListener onDItemSelectedListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            AnnnouceThirdIdx idx = (AnnnouceThirdIdx) mDetailListView
                    .getAdapter().getItem(position);
            idx.checked = !idx.checked;
            mDetailListView.setItemChecked(position, idx.checked);
            if (idx.checked) {
                addGridItem(idx);
            } else {
                removeGridItem(idx);
            }
        }
    };

    public OnItemClickListener onSumItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            mSumListView.setItemChecked(position, true);
            AnnnouceSecondIdx index = (AnnnouceSecondIdx) mSumListView
                    .getAdapter().getItem(position);
            if (index != null) {
                updateThirdIndex(index);
            }
        }
    };

    private void initTimeTxt(String ymdHM) {
        mTimeTxt.setText(ymdHM);
        timeView.setOnClickListener(onTimeClick);
    }

    private OnClickListener onTimeClick = new OnClickListener() {

        @Override
        public void onClick(View v) {

            String dateStr = mTimeTxt.getText().toString();
            showDatePickerCompat(dateStr, mTimeTxt.getId());


        }
    };

    private void initSpinner() {
        // init spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(), R.array.ann_objs_array,
                android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSpinner.setAdapter(adapter);
        mSpinner.setOnItemSelectedListener(onOSListener);
        mSpinner.setSelection(1, true);
    }

    private OnItemSelectedListener onOSListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            // 家属
            if (position == 1) {

            } else { // 病人

            }

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

    };

    /**
     * 初始化宣教表单列表popupwindow
     */
    private void initPopupWindow() {

        Context context = mContext;
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.layout_root_linear, null, false);
        ListView listView = new ListView(context);
        linearLayout.addView(listView);
        EmptyViewHelper.setEmptyView(listView, "listView");
        linearLayout.setBackgroundResource(R.drawable.img_popup_list_background);
        int width = DisplayUtil.getWidthPixels(getActivity());
        mPopupWindow = new PopupWindow(linearLayout, width * 2 / 3,
                LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(),
                (Bitmap) null));

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                mPopupWindow.dismiss();
                AnnnouceTopIdx indx = (AnnnouceTopIdx) listView.getAdapter()
                        .getItem(position);

                if (indx != null) {

                    updateSecondIndex(indx);

                    ListAdapter mAdapter = listView.getAdapter();
                    if (mAdapter instanceof AnnTopAdapter) {
                        ((AnnTopAdapter) mAdapter).selectPostion = position;
                    }
                }
            }
        });

        mPopupWindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                onPopupStateChange(false);
            }
        });

    }

    /**
     * @param indx
     */
    public void updateSecondIndex(AnnnouceTopIdx indx) {

        if (indx == null || secondList == null) {
            return;
        }
        mcurTopIndx = indx;
        setTileView(indx.YSMC);
        if (!secondIndx.containsKey(indx.YSBS)) {
            // 过滤二级列表
            ArrayList<AnnnouceSecondIdx> list = filterSecondList(indx.YSBS);
            secondIndx.put(indx.YSBS, list);
        }
        if (secondIndx.containsKey(indx.YSBS)) {
            setSumListViewAdapter(secondIndx.get(indx.YSBS));
        }

    }

    /**
     * 过滤二级列表
     *
     * @param ySBS 一级标识
     * @return
     */
    private ArrayList<AnnnouceSecondIdx> filterSecondList(String ySBS) {

        if (secondList == null || ySBS == null) {
            return null;
        }
        ArrayList<AnnnouceSecondIdx> list = new ArrayList<AnnnouceSecondIdx>();
        for (AnnnouceSecondIdx idx : secondList) {
            if (ySBS.equals(idx.YSBS)) {
                list.add(idx);
            }
        }
        return list;
    }

    public void updateThirdIndex(AnnnouceSecondIdx indx) {

        if (indx == null) {
            return;
        }
        mcurSecondIndx = indx;

        if (!thridIndx.containsKey(indx)) {

            ArrayList<AnnnouceThirdIdx> list = filterThirdList(indx);
            thridIndx.put(indx, list);
        }
        if (thridIndx.containsKey(indx)) {
            setDetailListView(thridIndx.get(indx));
        }
    }

    private ArrayList<AnnnouceThirdIdx> filterThirdList(AnnnouceSecondIdx indx) {

        if (thirdList == null || indx == null) {
            return null;
        }
        ArrayList<AnnnouceThirdIdx> list = new ArrayList<AnnnouceThirdIdx>();
        for (AnnnouceThirdIdx idx : thirdList) {
            if (indx.YSBS.equals(idx.YSBS) && indx.LBBS.equals(idx.LBBS)) {
                list.add(idx);
            }
        }
        return list;
    }

    public void setPopupWindowAdapter(ArrayList<AnnnouceTopIdx> items) {

        if (items.get(0) != null) {
            updateSecondIndex(items.get(0));
        }
        final AnnTopAdapter adapter = new AnnTopAdapter(getActivity(), items);
        ((ListView) mPopupWindow.getContentView()).setAdapter(adapter);
    }

	/*
     * public void getSecondIndx(String glxh) { GetDateTask task = new
	 * GetDateTask(GetDateTask.GET_SECOND_INDEX); task.execute(glxh);
	 * tasks.add(task); }
	 */

	/*
	 * 
	 * /* public void getThirdIndx(String glxh, String lbxh) {
	 * 
	 * GetDateTask task = new GetDateTask(GetDateTask.GET_THIRD_INDEX);
	 * task.execute(glxh, lbxh); tasks.add(task); }
	 */

    private void showPopUp(View v) {

        int[] location = new int[2];
        v.getLocationOnScreen(location);
        // 左
        // mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
        // location[0] + v.getWidth(), location[1]);
        mPopupWindow.showAsDropDown(v);
        // 右
        // mPopupWindow.showAtLocation(v, Gravity.NO_GRAVITY,
        // location[0]+v.getWidth(), location[1]);
        onPopupStateChange(true);
    }

    /**
     * 设置title view
     */
    private void initActionbar() {

        TextView mTitleTxt = (TextView) actionBar
                .findViewById(R.id.titleTextView);
        actionBar.setPatient(mAppApplication.sickPersonVo.XSCH
                + mAppApplication.sickPersonVo.BRXM);
        onPopupStateChange(false);

        mTitleTxt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showPopUp(v);
            }
        });

        actionBar.setTitle("病人宣教");

        actionBar.addAction(new Action() {

            @Override
            public void performAction(View view) {
                saveRecord();
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

    }

    /**
     * 响应popupwindow show and close 事件
     *
     * @param show
     */
    private void onPopupStateChange(boolean show) {

        Drawable arrow = getResources().getDrawable(
                R.drawable.ic_keyboard_arrow_down_black_24dp);
        if (show) {
            arrow = getResources().getDrawable(R.drawable.ic_keyboard_arrow_up_black_24dp);
        }
        arrow.setBounds(0, 0, arrow.getMinimumWidth(), arrow.getMinimumHeight());

        TextView mTitleTxt = (TextView) actionBar
                .findViewById(R.id.titleTextView);
        mTitleTxt.setCompoundDrawables(null, null, arrow, null);
    }

    private void setTileView(String title) {

        TextView mTitleTxt = (TextView) actionBar
                .findViewById(R.id.titleTextView);
        mTitleTxt.setText(title);

    }

    @Override
    public void onDateSet(int year, int month, int dayOfMonth, int viewId) {

        String date = DateTimeFactory.getInstance().ymd2Date(year, month, dayOfMonth);
        initTimeTxt(date);

    }

    /**
     * 获取宣教列表
     *
     * @author hy
     */
    class GetDateTask extends AsyncTask<String, String, ParserModel> {

        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected ParserModel doInBackground(String... params) {

            if (mAppApplication.sickPersonVo == null) {
                return null;
            }

            AnnounceApi api = AnnounceApi.getInstance(mAppApplication);

            String jgid = mAppApplication.jgId;
            int sysType = Constant.sysType;
            String zyh = mAppApplication.sickPersonVo.ZYH;

            ParserModel model = api.GetPatientTeacherInfo(
                    mAppApplication.getAreaId(), zyh, jgid, sysType);

            return model;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void onPostExecute(ParserModel result) {

            hideSwipeRefreshLayout();
            tasks.remove(this);

            if (null == result) {
                showMsgAndVoiceAndVibrator("加载失败");
                return;
            }

            if (result.isOK()) {
                topList = result.getList("Table1");
                secondList = result.getList("Table2");
                thirdList = result.getList("Table3");
                setPopupWindowAdapter(topList);
            } else {
                result.showToast(getActivity());
            }

        }
    }

    /**
     * 保存宣教
     *
     * @author hy
     */
    class SaveDateTask extends AsyncTask<String, String, ParserModel> {

        @Override
        protected void onPreExecute() {
            showLoadingDialog(getResources().getString(R.string.saveing));
        }

        @Override
        protected ParserModel doInBackground(String... params) {

            if (params == null || params.length < 1
                    || mAppApplication.sickPersonVo == null
                    || mAppApplication.user == null) {
                return null;
            }

            AnnounceApi api = AnnounceApi.getInstance(mAppApplication);

            String jgid = mAppApplication.jgId;
            int sysType = Constant.sysType;
            String zyh = mAppApplication.sickPersonVo.ZYH;
            String brbq = mAppApplication.getAreaId();
            String xjgh = mAppApplication.user.YHID;
            String xjsj = mTimeTxt.getText().toString();
            String mxList = params[0];
            int xjdx = mSpinner.getSelectedItemPosition();

            ParserModel model = api.SavePatientTeacherInfo(zyh, brbq, xjgh,
                    xjsj, mxList, xjdx, jgid, sysType);

            return model;
        }

        @Override
        protected void onPostExecute(ParserModel result) {

            hideLoadingDialog();
            tasks.remove(this);

            if (null == result) {
                showMsgAndVoiceAndVibrator(R.string.project_save_failed);
                return;
            }

            if (result.isOK()) {
                showMsgAndVoice(R.string.project_save_success);
            } else {
                result.showToast(getActivity());
            }

        }
    }

    /**
     * 初始化三级列表项
     */
    private void setDetailListView(ArrayList<AnnnouceThirdIdx> items) {

        if (mcurSecondIndx != null && mcurTopIndx != null) {
            AnnThirdAdapter mDetailAdapter = new AnnThirdAdapter(getActivity(),
                    items);
            mDetailListView.setAdapter(mDetailAdapter);
            importCheckState(items);
        }
    }

    /**
     * 由于checkedtextview check状态受listview控制，所有由listview导入状态
     *
     * @param items
     */
    private void importCheckState(ArrayList<AnnnouceThirdIdx> items) {

        if (items == null) {
            return;
        }
        int i = 0;
        for (AnnnouceThirdIdx idx : items) {
            mDetailListView.setItemChecked(i, idx.checked);
            i++;
        }
    }

    /**
     * 初始化二级列表项
     *
     * @param list
     */
    private void setSumListViewAdapter(ArrayList<AnnnouceSecondIdx> list) {

        if (mcurTopIndx != null) {
            AnnSecondAdapter adapter = new AnnSecondAdapter(getActivity(), list);
            mSumListView.setAdapter(adapter);
            if (list != null && list.size() > 0) {
                mSumListView.setItemChecked(0, true);
                updateThirdIndex(list.get(0));
            }
        }

    }


}
