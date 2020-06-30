package com.bsoft.mob.ienr.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bsoft.mob.ienr.R;
import com.bsoft.mob.ienr.activity.MainActivity;
import com.bsoft.mob.ienr.activity.user.UserModelActivity;
import com.bsoft.mob.ienr.adapter.AreaAdapter;
import com.bsoft.mob.ienr.adapter.MyPagerAdapter;
import com.bsoft.mob.ienr.adapter.SickPersonAdapter;
import com.bsoft.mob.ienr.api.PatientApi;
import com.bsoft.mob.ienr.barcode.BarcodeActions;
import com.bsoft.mob.ienr.barcode.BarcodeEntity;
import com.bsoft.mob.ienr.components.mqtt.MQTTTool;
import com.bsoft.mob.ienr.fragment.base.LeftMenuItemFragment;
import com.bsoft.mob.ienr.fragment.base.LeftMenuListFragment;
import com.bsoft.mob.ienr.helper.ContextCompatHelper;
import com.bsoft.mob.ienr.helper.EmptyViewHelper;
import com.bsoft.mob.ienr.helper.LayoutParamsHelper;
import com.bsoft.mob.ienr.helper.SizeHelper;
import com.bsoft.mob.ienr.helper.SwipeRefreshEnableHelper;
import com.bsoft.mob.ienr.helper.ViewBuildHelper;
import com.bsoft.mob.ienr.model.Response;
import com.bsoft.mob.ienr.model.kernel.AreaVo;
import com.bsoft.mob.ienr.model.kernel.SickPersonVo;
import com.bsoft.mob.ienr.model.kernel.State;
import com.bsoft.mob.ienr.util.AgainLoginUtil;
import com.bsoft.mob.ienr.util.FastSwitchUtils;
import com.bsoft.mob.ienr.util.JsonUtil;
import com.bsoft.mob.ienr.util.prefs.SettingUtils;
import com.bsoft.mob.ienr.util.tools.DimensionTool;
import com.bsoft.mob.ienr.util.tools.EmptyTool;
import com.bsoft.mob.ienr.view.DataHolder;
import com.bsoft.mob.ienr.view.floatmenu.menu.IFloatMenuItem;
import com.bsoft.mob.ienr.view.floatmenu.menu.TextFloatMenuItem;
import com.bsoft.mob.ienr.view.menus.SlidingMenu;
import com.classichu.dialogview.listener.OnBtnClickListener;
import com.classichu.dialogview.ui.ClassicDialogFragment;
import com.classichu.popupwindow.ui.ClassicPopupWindow;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.List;

//import com.bsoft.mob.ienr.helper.CommonsEmailHelper;


/**
 * @author Tank E-mail:zkljxq@126.com
 * @version 创建时间：2013-11-27 下午11:18:41
 * 病人列表
 */
public class SickPersonListFragment extends LeftMenuItemFragment {
    private static final String TAG = "SickPersonListFragment";
    private SickPersonAdapter mListViewAdapter;
    private SickPersonAdapter mGridViewAdapter;
    private ListView mListView;
    private GridView mPullRefreshGridView;

    private LinearLayout leftLayout;
    //    private ImageView leftImageView;
    private TextView leftTextView;
    private TextView titleTextView;
    private SwitchCompat mSwitch;
    private ClassicPopupWindow mClassicPopupWindow;
    private TextView tv_level;
    private TextView query_tv_btn;

    private static final int[][] ITEM_DRAWABLES_LEVEL = {{R.drawable.menu_my, -1},
            {R.drawable.img_menu_level_max, 0},
            {R.drawable.img_menu_level_1, 1},
            {R.drawable.img_menu_level_2, 2},
            {R.drawable.img_menu_level_3, 3},
            {R.drawable.menu_all, 4}};

    private static final int[][] ITEM_DRAWABLES_STRING_LEVEL = {{R.drawable.menu_my, -1, R.string.main_menu_my},
            {R.drawable.img_menu_level_max, 0, R.string.main_menu_max},
            {R.drawable.img_menu_level_1, 1, R.string.main_menu_1},
            {R.drawable.img_menu_level_2, 2, R.string.main_menu_2},
            {R.drawable.img_menu_level_3, 3, R.string.main_menu_3},
            {R.drawable.menu_all, 4, R.string.main_menu_all},
            //今日出院
            {R.drawable.img_image_no, -1, R.string.main_menu_today_out}
    };

    // 默认的护理级别
    private int mCurDrawable = R.drawable.menu_all;
    private int mCurLevel = 4;
    /**
     * 我的病人
     */
    private List<SickPersonVo> mMyPatientListRaw = null;
    private List<SickPersonVo> mTodayOutPatientListRaw = null;
    /**
     * 病区病人
     */
    private List<SickPersonVo> mAreaPatientListRaw = null;

    private ViewPager viewPager;

    @Override
    protected int setupLayoutResId() {
        return R.layout.fragment_sickperson_list;
    }

    @Override
    protected int configSwipeRefreshLayoutResId() {
        return R.id.id_swipe_refresh_layout;
    }

    private void switchUpOrDownIcon(boolean isPopupWindowShow) {
       /* titleTextView.setSelected(false);//V  正常状态
        titleTextView.setSelected(true);//A  展开状态
        */
        titleTextView.setSelected(isPopupWindowShow);
    }

    @Override
    protected void initView(View mainView, Bundle savedInstanceState) {


        viewPager = (ViewPager) mainView.findViewById(R.id.id_vp);
        List<View> viewList = new ArrayList<>();
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_view_pager_list, null, false);
        View view2 = LayoutInflater.from(mContext).inflate(R.layout.layout_view_pager_grid, null, false);
        mListView = view.findViewById(R.id.id_lv);
        mPullRefreshGridView = view2.findViewById(R.id.id_gv);


        viewList.add(view);
        viewList.add(view2);
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(viewList);
        viewPager.setAdapter(myPagerAdapter);

        ArrayList<SickPersonVo> list = new ArrayList<>();
        //listview
        mListViewAdapter = new SickPersonAdapter(list, R.layout.sickperson_item);
        mListView.setAdapter(mListViewAdapter);
        mListView.setOnItemClickListener(onListItemClickListener);
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mListView);
        //
        EmptyViewHelper.setEmptyView(mListView, "mListView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mListView);
        //gridview
        mGridViewAdapter = new SickPersonAdapter(list, R.layout.sickperson_grid_item);
        mPullRefreshGridView.setAdapter(mGridViewAdapter);
        mPullRefreshGridView.setOnItemClickListener(onGridItemClickListener);
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mPullRefreshGridView);
        //
        EmptyViewHelper.setEmptyView(mPullRefreshGridView, "mPullRefreshGridView");
        SwipeRefreshEnableHelper.setSwipeEnable(id_swipe_refresh_layout, mPullRefreshGridView);

        //标题栏
        leftLayout = (LinearLayout) mainView.findViewById(R.id.leftLayout);
        leftLayout.setVisibility(View.VISIBLE);
        leftLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ((MainActivity) getActivity()).toggle();
            }
        });
        /*leftImageView = (ImageView) mainView.findViewById(R.id.leftImageView);
        leftImageView.setImageResource(R.drawable.ic_menu_black_24dp);*/
        leftTextView = (TextView) mainView.findViewById(R.id.leftTextView);
        leftTextView.setText("≡菜单");
        //
        titleTextView = (TextView) mainView.findViewById(R.id.titleTextView);
        titleTextView.setVisibility(View.VISIBLE);
        titleTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                mClassicPopupWindow.showAsBottom_AnchorCenter_Center(view);
                //设置 icon状态
                switchUpOrDownIcon(true);
            }
        });
        Drawable arrowDrawable = ContextCompatHelper.getDrawable(mContext, R.drawable.selector_classic_icon_up_down, 0);
        titleTextView.setCompoundDrawablePadding(DimensionTool.getDimensionPx(mContext, R.dimen.classic_drawable_padding_primary));
        titleTextView.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowDrawable, null);
        switchUpOrDownIcon(false);
        //操作显示
        TextView id_tv = (TextView) mainView.findViewById(R.id.id_tv);
        id_tv.setText("当前病人:");
        tv_level = (TextView) mainView.findViewById(R.id.id_tv_2);
        tv_level.setText("病区病人");
         /*
            升级编号【56010015】============================================= start
            病人列表：病人列表筛选功能
            ================= Classichu 2017/10/18 9:34
            */
        query_tv_btn = (TextView) mainView.findViewById(R.id.id_tv_3);
        query_tv_btn.setText("筛选");
        query_tv_btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                queryList();
            }
        });
        /* =============================================================== end */
        mSwitch = (SwitchCompat) mainView
                .findViewById(R.id.sickperson_list_grid_switch);
        mSwitch.setTextOff("列表");
        mSwitch.setTextOn("网格");
        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                Log.d(TAG, "onCheckedChanged: isChecked:" + isChecked);
                changeAbsListViewShow();
            }
        });
        initPopupWindow();
        initBarBroadcast();
        //
        toRefreshData();
    }

    private void refreshData(List<SickPersonVo> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        mListViewAdapter.refreshData(list);
        mGridViewAdapter.refreshData(list);

    }

    /**
     * 筛选护理级别
     */
    private void fiterList() {
        List<SickPersonVo> tempList = new ArrayList<>();
        if (mCurDrawable == R.drawable.menu_my) {
            if (mMyPatientListRaw == null) {
                toRefreshData();
                return;
            }
            tempList.addAll(mMyPatientListRaw);
        } else if (mCurDrawable == R.drawable.img_image_no) {
            //今日出院
            if (mTodayOutPatientListRaw == null) {
                toRefreshData();
                return;
            }
            tempList.addAll(mTodayOutPatientListRaw);
        } else {
            //
            if (mAreaPatientListRaw == null) {
                toRefreshData();
                return;
            }
            if (mCurDrawable == R.drawable.menu_all) {
                tempList.addAll(mAreaPatientListRaw);
            } else {
                for (SickPersonVo sickPersonVo : mAreaPatientListRaw) {
                    if (mCurLevel == sickPersonVo.HLJB) {
                        tempList.add(sickPersonVo);
                    }
                }
            }
            //
        }
        refreshData(tempList);
    }

    /**
     * 搜索
     */
    private void queryList() {
        Context context = mContext;
        //
             /*   LinearLayout layout_root = new LinearLayout(context);
                LinearLayout.LayoutParams ll_lp_root = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layout_root.setOrientation(LinearLayout.VERTICAL);
                layout_root.setLayoutParams(ll_lp_root);*/
        LinearLayout layout_root = LayoutParamsHelper.buildLinearWrapWrap_V(context);
        SizeHelper.setPaddingLeftTopRight(layout_root, SizeHelper.getPaddingPrimary(), SizeHelper.getPaddingSecondary());
        //
        LinearLayout layout_one = new LinearLayout(context);
        LinearLayout.LayoutParams ll_lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_one.setOrientation(LinearLayout.HORIZONTAL);
        layout_one.setGravity(Gravity.CENTER_VERTICAL);
        layout_one.setLayoutParams(ll_lp);
        // SizeHelper.setPaddingLeftRight(layout_one, SizeHelper.getPaddingPrimary());
        TextView textView = ViewBuildHelper.buildTextView(mContext, "床号:");
        layout_one.addView(textView);
        final EditText editTextCH = ViewBuildHelper.buildEditTextAutoWrap(mContext, null);
        editTextCH.setHint("请输入病人床号");
        layout_one.addView(editTextCH);
        //
        LinearLayout layout_two = new LinearLayout(context);
        LinearLayout.LayoutParams ll_lp_2 = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layout_two.setOrientation(LinearLayout.HORIZONTAL);
        layout_two.setGravity(Gravity.CENTER_VERTICAL);
        layout_two.setLayoutParams(ll_lp_2);
        //SizeHelper.setPaddingLeftRight(layout_two, SizeHelper.getPaddingPrimary());
        TextView textView2 = ViewBuildHelper.buildTextView(mContext, "姓名:");
        layout_two.addView(textView2);
        final EditText editTextXM = ViewBuildHelper.buildEditTextAutoWrap(mContext, null);
        editTextXM.setHint("请输入病人姓名");
        layout_two.addView(editTextXM);

        //
        layout_root.addView(layout_one);
        layout_root.addView(layout_two);

        TextView titleTextView = ViewBuildHelper.buildDialogTitleTextView(context, "病人筛选");
        ClassicDialogFragment dialogFragment = new ClassicDialogFragment.Builder(context)
//                        .setTitle("病人筛选")
                .setCustomTitleView(titleTextView)
                .setContentView(layout_root)
                .setOkText(getString(R.string.project_operate_ok))
                .setCancelText(getString(R.string.project_operate_cancel))
                .setOnBtnClickListener(new OnBtnClickListener() {
                    @Override
                    public void onBtnClickOk(DialogInterface dialogInterface) {
                        //
                        String brch = editTextCH.getText().toString();
                        String brxm = editTextXM.getText().toString();
                        if (!EmptyTool.isBlank(brch) || !EmptyTool.isBlank(brxm)) {
                            //
                            ArrayList<SickPersonVo> tempArrayList = new ArrayList<>();
                            if (mCurDrawable == R.drawable.menu_my) {
                                //
                                if (mMyPatientListRaw == null) {
                                    toRefreshData();
                                    return;
                                }
                                for (SickPersonVo sickPersonVo : mMyPatientListRaw) {
                                    if (!EmptyTool.isBlank(brxm)) {
                                        if (sickPersonVo.BRXM.toLowerCase().contains(brxm.toLowerCase())) {
                                            tempArrayList.add(sickPersonVo);
                                            continue;
                                        }
                                    }
                                    if (!EmptyTool.isBlank(brch)) {
                                        //if (sickPersonVo.BRCH.toLowerCase().contains(brch.toLowerCase())) {
                                        if ((sickPersonVo.BRCH == null ? "" : sickPersonVo.BRCH).toLowerCase().contains(brch.toLowerCase())) {
                                            tempArrayList.add(sickPersonVo);
                                            continue;
                                        }
                                    }
                                }
                                //
                            } else {
                                //
                                if (mAreaPatientListRaw == null) {
                                    toRefreshData();
                                    return;
                                }
                                for (SickPersonVo sickPersonVo : mAreaPatientListRaw) {
                                    if (!EmptyTool.isBlank(brxm)) {
                                        if (sickPersonVo.BRXM.toLowerCase().contains(brxm.toLowerCase())) {
                                            tempArrayList.add(sickPersonVo);
                                            continue;
                                        }
                                    }
                                    if (!EmptyTool.isBlank(brch)) {
                                        //if (sickPersonVo.BRCH.toLowerCase().contains(brch.toLowerCase())) {
                                        if ((sickPersonVo.BRCH == null ? "" : sickPersonVo.BRCH).toLowerCase().contains(brch.toLowerCase())) {
                                            tempArrayList.add(sickPersonVo);
                                            continue;
                                        }
                                    }
                                }
                                //
                            }
                            refreshData(tempArrayList);
                        } else {
                            //////////////查出全部
                            if (mCurDrawable == R.drawable.menu_my) {
                                refreshData(mMyPatientListRaw);
                            } else {
                                refreshData(mAreaPatientListRaw);
                            }
                        }

                    }

                }).build();
        dialogFragment.show(getChildFragmentManager(), "dialogFragment");

    }

    @Override
    protected List<IFloatMenuItem> configFloatMenuItems() {
        List<IFloatMenuItem> floatMenuItemList = new ArrayList<>();

  /*      for (int[] itemDrawableResid_Level : ITEM_DRAWABLES_LEVEL) {
            int itemDrawableResid = itemDrawableResid_Level[0];
            final int Level = itemDrawableResid_Level[1];
            FloatMenuItem floatMenuItem = new FloatMenuItem(itemDrawableResid) {
                @Override
                public void actionClick(View view, int resid) {
//                    mCurLevel = (Integer) v.getTag();
                    mCurDrawable = resid;
                    mCurLevel = Level;
                    if (mCurDrawable == R.drawable.img_menu_level_max) {
                        tv_level.setText("特级病人");
                    }
                    if (mCurDrawable == R.drawable.img_menu_level_1) {
                        tv_level.setText("一级病人");
                    }
                    if (mCurDrawable == R.drawable.img_menu_level_2) {
                        tv_level.setText("二级病人");
                    }
                    if (mCurDrawable == R.drawable.img_menu_level_3) {
                        tv_level.setText("三级病人");
                    }
                    if (mCurDrawable == R.drawable.menu_my) {
                        tv_level.setText("我的病人");
                    }
                    if (mCurDrawable == R.drawable.menu_all) {
                        tv_level.setText("病区病人");
                    }
                    fiterList();
                }
            };
            floatMenuItemList.add(floatMenuItem);
        }*/

        for (int[] itemDrawableStringResid_Level : ITEM_DRAWABLES_STRING_LEVEL) {
            int itemStringResid = itemDrawableStringResid_Level[0];
            final int Level = itemDrawableStringResid_Level[1];
            final int textResId = itemDrawableStringResid_Level[2];
            String text = textResId > 0 ? getString(textResId) : null;
            IFloatMenuItem floatMenuItem = new TextFloatMenuItem(itemStringResid, text) {
                @Override
                public void actionClick(View view, int resid) {
//                    mCurLevel = (Integer) v.getTag();
                    mCurDrawable = resid;
                    mCurLevel = Level;
                    if (mCurDrawable == R.drawable.img_menu_level_max) {
                        tv_level.setText("特级病人");
                    }
                    if (mCurDrawable == R.drawable.img_menu_level_1) {
                        tv_level.setText("一级病人");
                    }
                    if (mCurDrawable == R.drawable.img_menu_level_2) {
                        tv_level.setText("二级病人");
                    }
                    if (mCurDrawable == R.drawable.img_menu_level_3) {
                        tv_level.setText("三级病人");
                    }
                    if (mCurDrawable == R.drawable.menu_my) {
                        tv_level.setText("我的病人");
                    }
                    if (mCurDrawable == R.drawable.menu_all) {
                        tv_level.setText("病区病人");
                    }
                    if (mCurDrawable == R.drawable.img_image_no) {
                        tv_level.setText("出院病人");
                    }
                    fiterList();
                }
            };
            floatMenuItemList.add(floatMenuItem);
        }
        return floatMenuItemList;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context != null && context instanceof MainActivity) {
            SlidingMenu slidingMenu = ((MainActivity) context).sm;
            if (slidingMenu == null) {
                return;
            }
            /**
             * 主要是针对 api 19~24 没有焦点的悬浮框的  病人列表主页 点击返回弹不出“是否退出登录？”的对话框
             * 其次这样也更合理  slidingMenu 展开的时候 不显示悬浮框 其他的页面也可以用此法进行优化
             */
            slidingMenu.setOnOpenListener(new SlidingMenu.OnOpenListener() {
                @Override
                public void onOpen() {
                    //正在打开
                    if (mFloatMenuManager != null) {
                        mFloatMenuManager.hide();
                    }
                }
            });
            slidingMenu.setOnClosedListener(new SlidingMenu.OnClosedListener() {
                @Override
                public void onClosed() {
                    //关闭完成
                    if (mFloatMenuManager != null) {
                        mFloatMenuManager.show();
                    }
                }
            });
        }
    }

    private String getBqJson(String jgid, String bqid) {
        String bqJson = "{APP:\"BS-iENR\",JGID:" + jgid + ",BQID:" + bqid + "}";
        return bqJson;
    }

    /**
     * 初始化病区列表popupwindow
     */
    private void initPopupWindow() {
        // 初始化病区列表
        AreaVo area = mAppApplication.getCrrentArea();
        if (area != null) {
            titleTextView.setText(area.KSMC);
        } else {
            titleTextView.setText("无病区项");
        }
        View layout_root_frame = LayoutInflater.from(mContext).inflate(R.layout.layout_root_frame, null, false);
        FrameLayout frameLayout = layout_root_frame.findViewById(R.id.id_fl_container);
        frameLayout.setPadding(5, 5, 5, 5);
        frameLayout.setBackgroundResource(R.drawable.shape_classic_bg_view_bar);
        ListView listView = new ListView(mContext);
        final AreaAdapter adapter = new AreaAdapter(getActivity(),
                mAppApplication.getAreaList(), mAppApplication.getAreaId());
        //设置病区消息主题
        MQTTTool.getInstance(getActivity().getApplicationContext())
                .setBqTopic(getBqJson(application.jgId, application.getAreaId()));
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                AreaVo item = adapter.getItem(position);
                adapter.mCurArea = item.KSDM;
                freshPaitentList(item);
                refreshMainMenu();
                //
                //设置 icon状态
                switchUpOrDownIcon(false);
                //
                mClassicPopupWindow.dismiss();
                //设置病区消息主题
                MQTTTool.getInstance(getActivity().getApplicationContext())
                        .setBqTopic(getBqJson(application.jgId, item.KSDM));
            }
        });
        listView.setAdapter(adapter);
        frameLayout.addView(listView);
        int list_width = Resources.getSystem().getDisplayMetrics().widthPixels / 2;
        mClassicPopupWindow = new ClassicPopupWindow.Builder(mContext)
                .setView(frameLayout)
                .setWidth(list_width)
                .setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        //设置 icon状态
                        switchUpOrDownIcon(false);
                    }
                })
                .build();
    }


    private void freshPaitentList(AreaVo item) {

        if (item != null) {
            mAppApplication.setAreaId(item.KSDM);
            titleTextView.setText(item.KSMC);
            // 响应刷新
            toRefreshData();
        } else {
            titleTextView.setText("无病区项");
        }
    }

    public void initBarBroadcast() {
        barBroadcast = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if (BarcodeActions.Refresh.equals(action)) {
                    if (null != mAppApplication.sickPersonVo) {
                        Intent result = new Intent(getActivity(),
                                UserModelActivity.class);
                        startActivity(result);
                    }
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


    private OnItemClickListener onListItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
                /*
            升级编号【56010049】============================================= start
            病人列表:管理员可设置是否禁用病人列表点击进入：默认不禁用
            ================= Classichu 2017/10/18 9:34
            */
            if (SettingUtils.isSickerItemCanNotClick(getActivity())) {
                showMsgAndVoiceAndVibrator("当前设置不可点击，请扫描进入！");
                return;
            }
            /* =============================================================== end */
            // prt listview add headerview
            SickPersonVo person = (SickPersonVo) mListView.getAdapter().getItem(position);
            startUserModelActivity(person);
        }
    };

    private OnItemClickListener onGridItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            /*
            升级编号【56010049】============================================= start
            病人列表:管理员可设置是否禁用病人列表点击进入：默认不禁用
            ================= Classichu 2017/10/18 9:34
            */
            if (SettingUtils.isSickerItemCanNotClick(getActivity())) {
                showMsgAndVoiceAndVibrator("当前设置不可点击，请扫描进入！");
                return;
            }
            /* =============================================================== end */
            SickPersonVo person = (SickPersonVo) mPullRefreshGridView.getAdapter().getItem(position);
            startUserModelActivity(person);
        }
    };

    private void startUserModelActivity(SickPersonVo person) {

        if (person != null) {
            mAppApplication.sickPersonVo = person;
            Intent intent = new Intent(getActivity(), UserModelActivity.class);
            getActivity().startActivity(intent);
        }
    }

    @Override
    protected void toRefreshData() {
        GetDataTask task = new GetDataTask();
        task.execute();
        tasks.add(task);
    }


    /**
     * 病人列表异步加载
     */
    private class GetDataTask extends AsyncTask<Byte, Void, Response<ArrayList<SickPersonVo>>> {
        @Override
        protected void onPreExecute() {
            showSwipeRefreshLayout();
        }

        @Override
        protected Response<ArrayList<SickPersonVo>> doInBackground(Byte... params) {

            int filter = 0;//type 默认0
            if (mAppApplication.getCrrentArea() != null && "[isSurgery]".equals(mAppApplication.getCrrentArea().YGDM)) {
                filter = 1000;//标记是 手术科室
            }
            String areaId = mAppApplication.getAreaId();//当是手术科室时候  KSDM 存放 SSKS ，会在保存交接单后 保存到BRBQ字段里
            String jgid = mAppApplication.jgId;
            String hsgh;

            if (mCurDrawable == R.drawable.menu_my) {
                //获取我的病人
                if (mAppApplication.user == null) {
                    return null;
                }
                hsgh = mAppApplication.user.YHID;
            } else {
                // 默认获取病区病人
                hsgh = "";
            }
            return PatientApi.getInstance(getActivity()).GetPatientList(
                    areaId, filter, -1, -1, hsgh, jgid);

        }

        @Override
        protected void onPostExecute(Response<ArrayList<SickPersonVo>> result) {
            super.onPostExecute(result);
            hideSwipeRefreshLayout();
            tasks.remove(this);
            if (null != result) {
                if (result.ReType == 100) {
                    new AgainLoginUtil(getActivity(), mAppApplication).showLoginDialog();
                    return;
                }
                if (result.ReType == 0) {
                    ArrayList<SickPersonVo> tList = result.Data;
                    if (tList != null) {
                        // 解析病人状态
                        for (SickPersonVo vo : tList) {
                            if (!EmptyTool.isBlank(vo.BRZT)) {
                                try {
                                    vo.state = JsonUtil.fromJson(vo.BRZT,
                                            new TypeReference<ArrayList<State>>() {
                                            });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        tList = new ArrayList<>();
                    }
                    setupData(tList);
                } else {
                    showMsgAndVoice(result.Msg);
                }
            } else {
                showMsgAndVoiceAndVibrator("加载失败");
            }

        }

    }

    /**
     * @param tList
     */
    private void setupData(ArrayList<SickPersonVo> tList) {
        if (mCurDrawable == R.drawable.menu_my) {
            mMyPatientListRaw = new ArrayList<>(tList);
            refreshData(mMyPatientListRaw);
        } else {
            //
            mAreaPatientListRaw = new ArrayList<>();
            mTodayOutPatientListRaw = new ArrayList<>();
            for (SickPersonVo sickPersonVo : tList) {
                if ("1".equals(sickPersonVo.CYPB)) {
                    mTodayOutPatientListRaw.add(sickPersonVo);
                } else {
                    mAreaPatientListRaw.add(sickPersonVo);
                }
            }
//            mAreaPatientListRaw = new ArrayList<>(tList);
            if (mCurDrawable == R.drawable.img_image_no) {
                refreshData(mTodayOutPatientListRaw);
            } else {
                refreshData(mAreaPatientListRaw);
            }
        }

    }

    private void refreshMainMenu() {
        //
        LeftMenuListFragment menuListFragment = (LeftMenuListFragment) DataHolder.getInstance().getData("hold_menuListFrag");
        if (menuListFragment != null) {
            menuListFragment.refreshMenuView();
        }
    }

    private void changeAbsListViewShow() {
        if (mSwitch.isChecked()) {
            viewPager.setCurrentItem(1);
        } else {
            viewPager.setCurrentItem(0);
        }
        Log.d(TAG, "changeAbsListViewShow: " + mSwitch.isChecked());
    }


}
